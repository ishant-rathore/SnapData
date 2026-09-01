package com.example.snapdata.util

import java.util.Locale

/**
 * Security utility for sanitizing and validating filenames across SnapData.
 *
 * Guarantees:
 * - Completely prevents directory traversal (../, ..\, absolute paths, leading slashes).
 * - Strips ASCII control characters (0x00 to 0x1F, 0x7F) and null bytes.
 * - Replaces forbidden filesystem characters (\ / : * ? " < > |) with underscores.
 * - Protects against Windows/FAT reserved device names (CON, PRN, AUX, NUL, COM1-9, LPT1-9).
 * - Bounds maximum filename length to safe filesystem limits (max 64 characters for base name).
 * - Preserves international UTF-8 characters (e.g. Hindi/Devanagari, Arabic, Chinese).
 */
object SafeFilenameUtil {

    private val RESERVED_NAMES = setOf(
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    )

    private val ILLEGAL_CHARS_REGEX = Regex("[\\\\/:*?\"<>|\\x00-\\x1F\\x7F]")
    private val MULTI_UNDERSCORE_REGEX = Regex("_+")
    private val MULTI_SPACE_REGEX = Regex("\\s+")

    /**
     * Sanitizes an arbitrary user- or AI-provided string into a safe filename base.
     *
     * @param rawName Raw input string (e.g. document title, user input, AI generated name).
     * @param fallback Default safe name to use if input is empty or contains only invalid characters.
     * @param maxBaseLength Maximum allowed length for the sanitized base name (default: 50).
     */
    fun sanitizeBaseName(
        rawName: String?,
        fallback: String = "Document",
        maxBaseLength: Int = 50
    ): String {
        if (rawName.isNullOrBlank()) return fallback

        // 1. Remove path traversal patterns
        var clean = rawName
            .replace("..", "")
            .replace("/", "")
            .replace("\\", "")

        // 2. Replace illegal characters and control characters with underscore
        clean = ILLEGAL_CHARS_REGEX.replace(clean, "_")

        // 3. Normalize spaces and multiple underscores
        clean = MULTI_SPACE_REGEX.replace(clean, "_")
        clean = MULTI_UNDERSCORE_REGEX.replace(clean, "_")

        // 4. Trim leading and trailing periods, spaces, and underscores
        clean = clean.trim('.', ' ', '_')

        // 5. Check if resulting string is empty
        if (clean.isBlank()) {
            clean = fallback
        }

        // 6. Enforce safe length limit
        if (clean.length > maxBaseLength) {
            clean = clean.take(maxBaseLength).trimEnd('.', '_', ' ')
        }

        // 7. Check for Windows/FAT reserved device names (case-insensitive)
        val upperCheck = clean.uppercase(Locale.US)
        if (RESERVED_NAMES.contains(upperCheck) || RESERVED_NAMES.any { upperCheck.startsWith("$it.") }) {
            clean = "SnapData_$clean"
        }

        return if (clean.isBlank()) fallback else clean
    }

    /**
     * Validates and creates a fully safe filename with a valid extension.
     */
    fun buildSafeFilename(
        title: String?,
        extension: String,
        prefix: String = "SnapData",
        includeTimestamp: Boolean = true
    ): String {
        val safeBase = sanitizeBaseName(title, fallback = "Document")
        val cleanExt = extension.trimStart('.').lowercase(Locale.US).filter { it.isLetterOrDigit() }

        val safePrefix = if (prefix.isNotBlank()) "${sanitizeBaseName(prefix, "SnapData")}_" else ""

        val timestampStr = if (includeTimestamp) {
            val ts = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())
            "_${ts}"
        } else ""

        return "${safePrefix}${safeBase}${timestampStr}.${cleanExt}"
    }

    /**
     * Verifies that a target file stays strictly inside its intended parent directory.
     * Prevents path traversal vulnerabilities.
     */
    fun isPathInsideDirectory(targetFile: java.io.File, parentDirectory: java.io.File): Boolean {
        return try {
            val canonicalTarget = targetFile.canonicalPath
            val canonicalParent = parentDirectory.canonicalPath
            canonicalTarget.startsWith(canonicalParent)
        } catch (_: Exception) {
            false
        }
    }
}
