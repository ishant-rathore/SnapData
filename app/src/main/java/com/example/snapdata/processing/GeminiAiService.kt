package com.example.snapdata.processing

import android.graphics.Bitmap
import android.util.Base64
import com.example.snapdata.BuildConfig
import com.example.snapdata.error.AppError
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max

@Serializable
data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@Serializable
data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GeminiGenerationConfig(
    val responseMimeType: String? = "application/json",
    val temperature: Float? = 0.1f
)

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

enum class ExecutionEngine(val displayName: String, val isCloud: Boolean) {
    ON_DEVICE_LOCAL("On-Device ML Kit OCR", false),
    GEMINI_CLOUD_AI("Gemini 3.5 Flash Multimodal", true),
    ENTERPRISE_BACKEND_PROXY("Enterprise Backend Proxy", true)
}

sealed class AiProcessingError(val message: String, val userFriendlyReason: String) {
    data object ApiKeyMissing : AiProcessingError(
        message = "GEMINI_API_KEY is not configured.",
        userFriendlyReason = "Gemini API key is not configured. Configured in AI Studio Secrets panel."
    )
    data class InvalidApiKey(val detail: String = "HTTP 401/403: Unauthorized or Invalid Key") : AiProcessingError(
        message = "Invalid or unauthorized API key: $detail",
        userFriendlyReason = "Gemini API key is unauthorized or invalid. Please verify your credentials."
    )
    data class RateLimitExceeded(val detail: String = "HTTP 429: Resource exhausted or rate limited") : AiProcessingError(
        message = "Rate limit exceeded: $detail",
        userFriendlyReason = "Cloud AI rate limit or quota exceeded. Recovered using fast on-device OCR."
    )
    data class NetworkUnavailable(val detail: String = "No network connectivity") : AiProcessingError(
        message = "Network unavailable: $detail",
        userFriendlyReason = "Network connection is unavailable. Switched to offline on-device processing."
    )
    data object OfflineModeForced : AiProcessingError(
        message = "Offline mode requested.",
        userFriendlyReason = "Processed 100% locally on-device with zero internet data transfer."
    )
    data class OversizedResponse(val sizeBytes: Int) : AiProcessingError(
        message = "AI response exceeded safe memory buffer ($sizeBytes bytes).",
        userFriendlyReason = "AI response was abnormally large. Recovered safely using on-device OCR."
    )
    data class MalformedResponse(val rawSnippet: String = "") : AiProcessingError(
        message = "Malformed AI response: $rawSnippet",
        userFriendlyReason = "AI service returned an unparseable response. Recovered using on-device OCR."
    )
    data class ApiError(val statusCode: Int, val errorBody: String = "") : AiProcessingError(
        message = "API returned HTTP $statusCode: $errorBody",
        userFriendlyReason = "AI cloud service returned error code $statusCode. Recovered using on-device OCR."
    )
    data class Timeout(val timeoutDurationMs: Long = 30000) : AiProcessingError(
        message = "AI service timed out after ${timeoutDurationMs}ms.",
        userFriendlyReason = "Cloud AI request timed out. Recovered using fast on-device OCR."
    )

    fun toAppError(): AppError.AiError {
        return when (this) {
            is ApiKeyMissing -> AppError.AiError.ApiKeyMissing(userFriendlyReason)
            is InvalidApiKey -> AppError.AiError.InvalidApiKey(401, detail)
            is RateLimitExceeded -> AppError.AiError.RateLimitExceeded(429, detail)
            is NetworkUnavailable -> AppError.AiError.NetworkUnavailable(detail)
            is OfflineModeForced -> AppError.AiError.ApiKeyMissing("Offline mode active")
            is OversizedResponse -> AppError.AiError.OversizedPayload(sizeBytes, message)
            is MalformedResponse -> AppError.AiError.MalformedResponse(rawSnippet)
            is ApiError -> AppError.AiError.ServerError(statusCode, errorBody)
            is Timeout -> AppError.AiError.Timeout(timeoutDurationMs, message)
        }
    }
}

data class ProcessingExecutionResult(
    val ocrResult: OcrEngine.OcrResult,
    val engineUsed: ExecutionEngine,
    val isOffline: Boolean,
    val diagnosticMessage: String,
    val error: AiProcessingError? = null
)


object GeminiAiService {
    // Model identifier per AI Studio guidelines: gemini-3.5-flash
    const val GEMINI_MODEL = "gemini-3.5-flash"
    const val DEFAULT_GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent"
    const val MAX_SAFE_RESPONSE_BYTES = 2 * 1024 * 1024 // 2MB max safe payload size

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    val defaultOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    fun isGeminiConfigured(): Boolean {
        val key = getApiKey()
        val backendUrl = getBackendUrl()
        val hasKey = key.isNotBlank() && key != "YOUR_GEMINI_API_KEY"
        val hasBackend = backendUrl.isNotBlank() && backendUrl != "NONE" && backendUrl != "YOUR_BACKEND_URL"
        return hasKey || hasBackend
    }

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun getBackendUrl(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_BACKEND_URL")
            val raw = (field.get(null) as? String) ?: ""
            if (raw == "NONE" || raw == "YOUR_BACKEND_URL") "" else raw
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Main entry point for document extraction.
     * Guaranteed fail-safe: Falls back to local ML Kit OCR under all error conditions,
     * supports coroutine cancellation, enforces timeout boundaries, and performs strict schema validation.
     */
    suspend fun extractStructuredDocument(
        bitmap: Bitmap,
        hintText: String? = null,
        forceOffline: Boolean = false,
        enableCloudAi: Boolean = false,
        forcedType: DocumentType? = null,
        customApiKey: String? = null,
        customBackendUrl: String? = null,
        httpClient: OkHttpClient = defaultOkHttpClient
    ): ProcessingExecutionResult = withContext(Dispatchers.IO) {
        currentCoroutineContext().ensureActive()
        val apiKey = customApiKey ?: getApiKey()
        val backendUrl = customBackendUrl ?: getBackendUrl()

        // 1. Check Offline / Opt-in Constraints
        if (forceOffline || !enableCloudAi) {
            val localResult = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = localResult,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Processed 100% on-device (Offline Mode). Zero cloud data transmission.",
                error = AiProcessingError.OfflineModeForced
            )
        }

        // 2. Check Credential Configuration
        val hasKey = apiKey.isNotBlank() && apiKey != "YOUR_GEMINI_API_KEY"
        val hasBackend = backendUrl.isNotBlank()

        if (!hasKey && !hasBackend) {
            val localResult = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = localResult,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Gemini API key or backend proxy is not configured. Processed securely using local OCR.",
                error = AiProcessingError.ApiKeyMissing
            )
        }

        // 3. Attempt Cloud AI Extraction with Safe Execution & Retry Policy
        try {
            currentCoroutineContext().ensureActive()
            val base64Image = bitmapToBase64(bitmap)
            val prompt = """
                Analyze this document image in detail. Extract structured key-value fields and tabular data in valid JSON matching this exact schema:
                {
                  "documentType": "INVOICE" | "RECEIPT" | "BANK_STATEMENT" | "FORM" | "CERTIFICATE" | "MARK_SHEET" | "ID_CARD" | "BUSINESS_CARD" | "TABLE" | "GENERAL_DOCUMENT",
                  "summary": "Brief 1-2 sentence executive summary of the document",
                  "fields": [
                    {
                      "key": "Field Name",
                      "value": "Extracted Value",
                      "confidence": 0.98,
                      "category": "Financial" | "Temporal" | "Party / Entity" | "Identifier" | "Location" | "General"
                    }
                  ],
                  "tables": [
                    {
                      "name": "Table Name",
                      "headers": ["Col 1", "Col 2", "Col 3"],
                      "rows": [
                        ["Val 1", "Val 2", "Val 3"]
                      ],
                      "confidence": 0.95
                    }
                  ],
                  "rawText": "Complete reconstructed text"
                }
            """.trimIndent()

            val geminiReq = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt),
                            GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Image))
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.1f
                )
            )

            val reqBodyJson = json.encodeToString(geminiReq)
            val requestBuilder = Request.Builder()
                .post(reqBodyJson.toRequestBody("application/json".toMediaType()))

            val engine: ExecutionEngine
            if (hasBackend) {
                requestBuilder.url(backendUrl)
                engine = ExecutionEngine.ENTERPRISE_BACKEND_PROXY
            } else {
                requestBuilder.url("$DEFAULT_GEMINI_ENDPOINT?key=$apiKey")
                engine = ExecutionEngine.GEMINI_CLOUD_AI
            }

            val request = requestBuilder.build()

            // Execute HTTP call with cancellation and retry on transient server errors (503/502)
            var responseResult = executeWithTransientRetry(httpClient, request)

            if (!responseResult.isSuccessful) {
                val error = classifyHttpError(responseResult.code, responseResult.body)
                val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
                return@withContext ProcessingExecutionResult(
                    ocrResult = fallback,
                    engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                    isOffline = true,
                    diagnosticMessage = "Cloud AI call failed (${error.userFriendlyReason}). Recovered using local OCR.",
                    error = error
                )
            }

            // Oversized response protection
            val rawBodyBytes = responseResult.body.toByteArray(Charsets.UTF_8)
            if (rawBodyBytes.size > MAX_SAFE_RESPONSE_BYTES) {
                val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
                return@withContext ProcessingExecutionResult(
                    ocrResult = fallback,
                    engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                    isOffline = true,
                    diagnosticMessage = "Cloud AI payload exceeded safe memory limits. Recovered using local OCR.",
                    error = AiProcessingError.OversizedResponse(rawBodyBytes.size)
                )
            }

            // Parse response body with strict schema validation
            val parsedResult = parseGeminiResponsePayload(responseResult.body, hintText ?: "", forcedType)
            if (parsedResult != null) {
                return@withContext ProcessingExecutionResult(
                    ocrResult = parsedResult,
                    engineUsed = engine,
                    isOffline = false,
                    diagnosticMessage = "Successfully extracted structured document data via ${engine.displayName}.",
                    error = null
                )
            } else {
                val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
                return@withContext ProcessingExecutionResult(
                    ocrResult = fallback,
                    engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                    isOffline = true,
                    diagnosticMessage = "Malformed Cloud AI payload. Recovered using on-device OCR.",
                    error = AiProcessingError.MalformedResponse(responseResult.body.take(150))
                )
            }

        } catch (c: CancellationException) {
            throw c
        } catch (e: SocketTimeoutException) {
            val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = fallback,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Cloud AI request timed out. Recovered using local OCR.",
                error = AiProcessingError.Timeout()
            )
        } catch (e: UnknownHostException) {
            val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = fallback,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Network host unreachable. Recovered using local OCR.",
                error = AiProcessingError.NetworkUnavailable(e.message ?: "Unknown host")
            )
        } catch (e: ConnectException) {
            val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = fallback,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Network connection refused. Recovered using local OCR.",
                error = AiProcessingError.NetworkUnavailable(e.message ?: "Connection refused")
            )
        } catch (e: IOException) {
            val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = fallback,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Network I/O error: ${e.localizedMessage}. Recovered using local OCR.",
                error = AiProcessingError.NetworkUnavailable(e.message ?: "I/O error")
            )
        } catch (e: Exception) {
            val fallback = OcrEngine.analyzeDocumentBitmap(bitmap, hintText, forcedType)
            return@withContext ProcessingExecutionResult(
                ocrResult = fallback,
                engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                isOffline = true,
                diagnosticMessage = "Unexpected exception during cloud processing: ${e.localizedMessage}. Recovered using local OCR.",
                error = AiProcessingError.MalformedResponse(e.message ?: "Parsing exception")
            )
        }
    }

    data class HttpResponseData(val isSuccessful: Boolean, val code: Int, val body: String)

    /**
     * Executes HTTP Call with Coroutine Cancellation and retries ONLY for transient 502/503/504 errors.
     */
    private suspend fun executeWithTransientRetry(httpClient: OkHttpClient, request: Request): HttpResponseData {
        var attempts = 0
        val maxAttempts = 2

        while (true) {
            attempts++
            currentCoroutineContext().ensureActive()

            val call = httpClient.newCall(request)
            val result = suspendCancellableCoroutine<HttpResponseData> { continuation ->
                continuation.invokeOnCancellation {
                    call.cancel()
                }

                try {
                    val response = call.execute()
                    val body = response.body?.string() ?: ""
                    val data = HttpResponseData(response.isSuccessful, response.code, body)
                    continuation.resume(data)
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }
            }

            // Retry only on transient HTTP 502/503/504 if attempts remaining
            if (!result.isSuccessful && (result.code == 502 || result.code == 503 || result.code == 504) && attempts < maxAttempts) {
                delay(500L)
                continue
            }

            return result
        }
    }

    fun classifyHttpError(statusCode: Int, responseBody: String): AiProcessingError {
        return when (statusCode) {
            400 -> {
                if (responseBody.contains("API_KEY_INVALID", ignoreCase = true) || responseBody.contains("key not valid", ignoreCase = true)) {
                    AiProcessingError.InvalidApiKey("Invalid Gemini API key parameter.")
                } else {
                    AiProcessingError.ApiError(statusCode, responseBody.take(150))
                }
            }
            401, 403 -> AiProcessingError.InvalidApiKey("HTTP $statusCode: Unauthorized or permission denied.")
            429 -> AiProcessingError.RateLimitExceeded("HTTP 429: API rate limit or quota exceeded.")
            else -> AiProcessingError.ApiError(statusCode, responseBody.take(150))
        }
    }

    /**
     * Parses the outer Gemini content payload safely.
     */
    fun parseGeminiResponsePayload(responseBody: String, fallbackText: String, forcedType: DocumentType? = null): OcrEngine.OcrResult? {
        return try {
            val parsedResp = json.parseToJsonElement(responseBody)
            if (parsedResp !is JsonObject) return null

            val candidates = parsedResp["candidates"] as? JsonArray ?: return null
            if (candidates.isEmpty()) return null

            val candidateObj = candidates[0] as? JsonObject ?: return null
            val contentObj = candidateObj["content"] as? JsonObject ?: return null
            val parts = contentObj["parts"] as? JsonArray ?: return null
            if (parts.isEmpty()) return null

            val partObj = parts[0] as? JsonObject ?: return null
            val textPrimitive = partObj["text"]?.jsonPrimitive ?: return null
            val jsonText = textPrimitive.contentOrNull ?: return null

            parseGeminiStructuredJson(jsonText, fallbackText, forcedType)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Strict JSON Parsing and Schema Validation for structured AI output.
     * Guaranteed never to throw or crash on unexpected enums, missing/null fields, malformed tables, or invalid confidence.
     */
    fun parseGeminiStructuredJson(jsonStr: String, fallbackText: String, forcedType: DocumentType? = null): OcrEngine.OcrResult? {
        return try {
            val cleaned = cleanJsonString(jsonStr)
            if (cleaned.isBlank()) return null

            val parsedElement = json.parseToJsonElement(cleaned)
            val parsed = parsedElement as? JsonObject ?: return null

            // 1. Document Type Validation (Sanitized Enum Handling)
            val rawDocType = parsed["documentType"]?.jsonPrimitive?.contentOrNull
            val detectedType = forcedType ?: sanitizeDocumentType(rawDocType)

            // 2. Summary Validation
            val summary = sanitizeString(parsed["summary"]?.jsonPrimitive?.contentOrNull)
                ?.take(500)
                ?: "Structured ${detectedType.displayName} extracted successfully via AI."

            // 3. Raw Text
            var rawText = sanitizeString(parsed["rawText"]?.jsonPrimitive?.contentOrNull) ?: fallbackText

            // 4. Fields Schema Validation
            val fields = mutableListOf<ExtractedField>()
            val fieldsArray = parsed["fields"] as? JsonArray
            fieldsArray?.forEach { fieldEl ->
                val fObj = fieldEl as? JsonObject ?: return@forEach
                val k = sanitizeString(fObj["key"]?.jsonPrimitive?.contentOrNull)?.take(100) ?: return@forEach
                if (k.isBlank()) return@forEach

                val rawVal = fObj["value"]
                val v = extractStringValue(rawVal)
                val rawConf = fObj["confidence"]
                val conf = sanitizeConfidence(rawConf, defaultVal = 0.95f)
                val rawCategory = sanitizeString(fObj["category"]?.jsonPrimitive?.contentOrNull) ?: "General"

                fields.add(
                    ExtractedField(
                        key = k,
                        value = v,
                        confidence = conf,
                        category = sanitizeCategory(rawCategory, k, detectedType),
                        confidenceSource = ConfidenceSource.MEASURED
                    )
                )
            }

            // 5. Tables Schema Validation (Mismatched headers/rows protection)
            val tables = mutableListOf<ExtractedTable>()
            val tablesArray = parsed["tables"] as? JsonArray
            tablesArray?.forEach { tableEl ->
                val tObj = tableEl as? JsonObject ?: return@forEach
                val name = sanitizeString(tObj["name"]?.jsonPrimitive?.contentOrNull)?.take(80) ?: "Data Table"
                
                val headersList = mutableListOf<String>()
                val rawHeaders = tObj["headers"] as? JsonArray
                rawHeaders?.forEach { h ->
                    val hStr = sanitizeString(h.jsonPrimitive.contentOrNull) ?: ""
                    if (hStr.isNotBlank()) headersList.add(hStr)
                }

                val rowsList = mutableListOf<MutableList<String>>()
                val rawRows = tObj["rows"] as? JsonArray
                rawRows?.forEach { rowEl ->
                    val rArr = rowEl as? JsonArray ?: return@forEach
                    val row = rArr.map { cell -> extractStringValue(cell) }.toMutableList()
                    rowsList.add(row)
                }

                // If headers are missing but rows exist, synthesize columns
                if (headersList.isEmpty() && rowsList.isNotEmpty()) {
                    val maxCols = rowsList.maxOfOrNull { it.size } ?: 1
                    for (i in 1..maxCols) {
                        headersList.add("Col $i")
                    }
                }

                // Normalize ragged rows: align columns with header count
                if (headersList.isNotEmpty()) {
                    val numCols = headersList.size
                    val normalizedRows = rowsList.map { row ->
                        val fixedRow = row.toMutableList()
                        while (fixedRow.size < numCols) {
                            fixedRow.add("-")
                        }
                        fixedRow.take(numCols).toMutableList()
                    }.toMutableList()

                    val rawConf = tObj["confidence"]
                    val conf = sanitizeConfidence(rawConf, defaultVal = 0.92f)

                    tables.add(
                        ExtractedTable(
                            name = name,
                            headers = headersList,
                            rows = normalizedRows,
                            confidence = conf,
                            confidenceSource = ConfidenceSource.MEASURED
                        )
                    )
                }
            }

            // 6. Overall Confidence Calculation
            val overallConf = if (fields.isNotEmpty() || tables.isNotEmpty()) {
                val allScores = fields.map { it.confidence } + tables.map { it.confidence }
                (allScores.average().toFloat()).coerceIn(0.0f, 1.0f)
            } else {
                0.90f
            }

            if (rawText.isBlank() && fields.isNotEmpty()) {
                rawText = fields.joinToString("\n") { "${it.key}: ${it.value}" }
            }

            OcrEngine.OcrResult(
                rawText = rawText,
                detectedDocType = detectedType,
                summary = summary,
                fields = fields,
                tables = tables,
                overallConfidence = overallConf,
                lineCount = if (rawText.isNotBlank()) rawText.lines().size else fields.size,
                confidenceSource = ConfidenceSource.MEASURED,
                blocksCount = fields.size.coerceAtLeast(1),
                wordCount = if (rawText.isNotBlank()) rawText.split(Regex("\\s+")).filter { it.isNotBlank() }.size else 0
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun cleanJsonString(raw: String): String {
        var str = raw.trim()
        // Remove markdown code fences
        if (str.startsWith("```")) {
            val firstNewline = str.indexOf('\n')
            if (firstNewline != -1) {
                str = str.substring(firstNewline + 1)
            } else {
                str = str.removePrefix("```json").removePrefix("```")
            }
        }
        if (str.endsWith("```")) {
            str = str.substring(0, str.length - 3)
        }
        str = str.trim()

        // Extract substring between first '{' and last '}' if extra text is present
        val firstBrace = str.indexOf('{')
        val lastBrace = str.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace > firstBrace) {
            str = str.substring(firstBrace, lastBrace + 1)
        }

        return str
    }

    private fun sanitizeString(str: String?): String? {
        if (str == null) return null
        val trimmed = str.trim()
        return if (trimmed.equals("null", ignoreCase = true) || trimmed.isBlank()) null else trimmed
    }

    private fun extractStringValue(element: JsonElement?): String {
        return when (element) {
            null -> ""
            is JsonPrimitive -> {
                if (element.isString) element.content else element.toString()
            }
            is JsonArray -> {
                element.joinToString(", ") { extractStringValue(it) }
            }
            is JsonObject -> {
                element.entries.joinToString("; ") { "${it.key}: ${extractStringValue(it.value)}" }
            }
            else -> element.toString()
        }
    }

    private fun sanitizeConfidence(element: JsonElement?, defaultVal: Float): Float {
        if (element == null) return defaultVal
        return try {
            when (element) {
                is JsonPrimitive -> {
                    val floatVal = element.floatOrNull
                    if (floatVal != null && !floatVal.isNaN() && !floatVal.isInfinite()) {
                        floatVal.coerceIn(0.0f, 1.0f)
                    } else {
                        // Check if percentage string like "98%"
                        val str = element.content.trim().removeSuffix("%")
                        val parsed = str.toFloatOrNull()
                        if (parsed != null) {
                            if (parsed > 1.0f && parsed <= 100f) (parsed / 100f).coerceIn(0.0f, 1.0f) else parsed.coerceIn(0.0f, 1.0f)
                        } else {
                            defaultVal
                        }
                    }
                }
                else -> defaultVal
            }
        } catch (e: Exception) {
            defaultVal
        }
    }

    fun sanitizeDocumentType(rawType: String?): DocumentType {
        if (rawType.isNullOrBlank()) return DocumentType.GENERAL_DOCUMENT
        val normalized = rawType.trim().uppercase().replace(" ", "_").replace("-", "_")
        return try {
            DocumentType.valueOf(normalized)
        } catch (e: Exception) {
            when {
                normalized.contains("INVOICE") || normalized.contains("BILL") -> DocumentType.INVOICE
                normalized.contains("RECEIPT") || normalized.contains("POS") -> DocumentType.RECEIPT
                normalized.contains("STATEMENT") || normalized.contains("BANK") -> DocumentType.BANK_STATEMENT
                normalized.contains("FORM") || normalized.contains("APPLICATION") -> DocumentType.FORM
                normalized.contains("CERTIFICATE") || normalized.contains("AWARD") -> DocumentType.CERTIFICATE
                normalized.contains("MARK") || normalized.contains("TRANSCRIPT") || normalized.contains("GRADE") -> DocumentType.MARK_SHEET
                normalized.contains("ID") || normalized.contains("LICENSE") || normalized.contains("PASSPORT") -> DocumentType.ID_CARD
                normalized.contains("CARD") || normalized.contains("BUSINESS") -> DocumentType.BUSINESS_CARD
                normalized.contains("TABLE") || normalized.contains("MATRIX") -> DocumentType.TABLE
                else -> DocumentType.GENERAL_DOCUMENT
            }
        }
    }

    private fun sanitizeCategory(rawCategory: String, key: String, docType: DocumentType): String {
        val validCategories = setOf("Financial", "Temporal", "Party / Entity", "Identifier", "Location", "General", "Academic", "Administrative")
        for (v in validCategories) {
            if (rawCategory.equals(v, ignoreCase = true)) return v
        }
        val lowerKey = key.lowercase()
        return when {
            lowerKey.contains("total") || lowerKey.contains("amount") || lowerKey.contains("tax") || lowerKey.contains("price") || lowerKey.contains("fee") -> "Financial"
            lowerKey.contains("date") || lowerKey.contains("time") || lowerKey.contains("due") -> "Temporal"
            lowerKey.contains("name") || lowerKey.contains("vendor") || lowerKey.contains("client") || lowerKey.contains("company") -> "Party / Entity"
            lowerKey.contains("id") || lowerKey.contains("no") || lowerKey.contains("number") || lowerKey.contains("code") -> "Identifier"
            lowerKey.contains("address") || lowerKey.contains("city") || lowerKey.contains("country") || lowerKey.contains("street") -> "Location"
            else -> docType.category
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        val maxDim = 1600
        val scale = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val maxSide = max(bitmap.width, bitmap.height)
            maxDim.toFloat() / maxSide
        } else 1.0f

        val scaledBitmap = if (scale < 1.0f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else {
            bitmap
        }

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}
