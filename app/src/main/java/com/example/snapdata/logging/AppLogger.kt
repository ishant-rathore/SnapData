package com.example.snapdata.logging

import android.util.Log

/**
 * Production-grade, privacy-safe structured logging system for SnapData.
 *
 * Guarantees:
 * - Redacts Gemini API keys, Authorization tokens, and password parameters.
 * - Redacts sensitive document text contents and PII, logging only structural metadata (lengths, line counts).
 * - Categorizes logs by subsystem domain tags (SnapData.Camera, SnapData.OCR, SnapData.AI, etc.).
 * - Replaces unformatted printStackTrace with controlled exception diagnostics.
 */
object AppLogger {

    enum class LogDomain(val tag: String) {
        CAMERA("SnapData.Camera"),
        IMAGE("SnapData.Image"),
        PDF("SnapData.PDF"),
        PREPROCESSING("SnapData.Preprocessing"),
        OCR("SnapData.OCR"),
        AI("SnapData.AI"),
        DATABASE("SnapData.Database"),
        EXPORT("SnapData.Export"),
        SHARING("SnapData.Sharing"),
        PIPELINE("SnapData.Pipeline"),
        APP("SnapData.App"),
        AUTH("SnapData.Auth"),
        STORAGE("SnapData.Storage")
    }

    private val BEARER_REGEX = Regex("(?i)bearer\\s+([a-zA-Z0-9_\\-\\.]+)")
    private val API_KEY_REGEX = Regex("(?i)(key|api_key|apikey|authorization|bearer|token|password|secret|pass|pin|pwd|session_token)\\s*[:=]\\s*[\"']?([a-zA-Z0-9_\\-\\.]+)[\"']?")
    private val GEMINI_KEY_PATTERN = Regex("AIzaSy[a-zA-Z0-9_\\-]{33}")
    private val GENERIC_SECRET_PATTERN = Regex("(?i)(password|secret|apikey|access_token|auth_token)\\s*=\\s*[^&\\s]+")
    
    // Indian PII patterns for safe log redaction
    private val AADHAAR_REGEX = Regex("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b")
    private val PAN_REGEX = Regex("\\b[A-Z]{5}[0-9]{4}[A-Z]{1}\\b")
    private val CREDIT_CARD_REGEX = Regex("\\b(?:\\d{4}[-\\s]?){3}\\d{4}\\b")
    private val EMAIL_REGEX = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")

    var isDebugEnabled: Boolean = true

    /**
     * Sanitizes sensitive secrets, API keys, and credentials from log strings.
     */
    fun sanitize(message: String?): String {
        if (message == null) return ""
        var sanitized = message
        sanitized = GEMINI_KEY_PATTERN.replace(sanitized, "AIzaSy[REDACTED]")
        sanitized = BEARER_REGEX.replace(sanitized, "Bearer [REDACTED]")
        sanitized = API_KEY_REGEX.replace(sanitized) { matchResult ->
            val param = matchResult.groupValues[1]
            "$param=[REDACTED]"
        }
        sanitized = GENERIC_SECRET_PATTERN.replace(sanitized) { matchResult ->
            val param = matchResult.groupValues[1]
            "$param=[REDACTED]"
        }
        return sanitized
    }

    /**
     * Deep-redacts Personally Identifiable Information (PII) like PAN, Aadhaar, Email, and Cards.
     */
    fun redactPii(message: String?): String {
        if (message == null) return ""
        var sanitized = sanitize(message)
        sanitized = AADHAAR_REGEX.replace(sanitized, "XXXX-XXXX-[REDACTED]")
        sanitized = PAN_REGEX.replace(sanitized, "XXXXX[REDACTED]")
        sanitized = CREDIT_CARD_REGEX.replace(sanitized, "XXXX-XXXX-XXXX-[REDACTED]")
        sanitized = EMAIL_REGEX.replace(sanitized) { match ->
            val parts = match.value.split("@")
            "${parts.firstOrNull()?.take(2) ?: ""}***@${parts.getOrNull(1) ?: "***"}"
        }
        return sanitized
    }

    /**
     * Redacts user document text or PII, preserving safe diagnostic metadata.
     */
    fun redactDocumentText(rawText: String?): String {
        if (rawText.isNullOrBlank()) return "[Empty Document Text]"
        val lines = rawText.lines()
        val wordCount = rawText.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        return "[Document Content: ${rawText.length} chars, ${lines.size} lines, $wordCount words (SHA256: ${hashSnippet(rawText)})]"
    }

    private fun hashSnippet(text: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(text.toByteArray(Charsets.UTF_8))
            hashBytes.take(4).joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "****"
        }
    }

    fun d(domain: LogDomain, message: String) {
        if (isDebugEnabled) {
            try {
                Log.d(domain.tag, sanitize(message))
            } catch (_: Exception) {
                println("[DEBUG] [${domain.tag}] ${sanitize(message)}")
            }
        }
    }

    fun i(domain: LogDomain, message: String) {
        try {
            Log.i(domain.tag, sanitize(message))
        } catch (_: Exception) {
            println("[INFO] [${domain.tag}] ${sanitize(message)}")
        }
    }

    fun w(domain: LogDomain, message: String, throwable: Throwable? = null) {
        val cleanMsg = sanitize(message)
        val exMsg = throwable?.let { " [Exception: ${it.javaClass.simpleName}: ${sanitize(it.localizedMessage)}]" } ?: ""
        try {
            Log.w(domain.tag, cleanMsg + exMsg, throwable)
        } catch (_: Exception) {
            println("[WARN] [${domain.tag}] $cleanMsg$exMsg")
        }
    }

    fun e(domain: LogDomain, message: String, throwable: Throwable? = null) {
        val cleanMsg = sanitize(message)
        val exMsg = throwable?.let { " [Exception: ${it.javaClass.simpleName}: ${sanitize(it.localizedMessage)}]" } ?: ""
        try {
            Log.e(domain.tag, cleanMsg + exMsg, throwable)
        } catch (_: Exception) {
            System.err.println("[ERROR] [${domain.tag}] $cleanMsg$exMsg")
        }
    }
}
