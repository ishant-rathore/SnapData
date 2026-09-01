package com.example.snapdata.processing

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

object OcrEngine {

    data class OcrBlock(
        val text: String,
        val boundingBox: Rect?,
        val lines: List<OcrLine> = emptyList(),
        val recognizedLanguage: String? = null
    )

    data class OcrLine(
        val text: String,
        val boundingBox: Rect?,
        val confidence: Float? = null,
        val elements: List<String> = emptyList()
    )

    data class RawOcrOutput(
        val text: String,
        val blocks: List<OcrBlock> = emptyList(),
        val lineCount: Int = 0,
        val wordCount: Int = 0,
        val measuredConfidence: Float? = null,
        val detectedLanguages: List<String> = emptyList(),
        val isSuccessful: Boolean = true,
        val errorMessage: String? = null
    )

    data class OcrResult(
        val rawText: String,
        val detectedDocType: DocumentType,
        val summary: String,
        val fields: List<ExtractedField>,
        val tables: List<ExtractedTable>,
        val overallConfidence: Float,
        val lineCount: Int,
        val confidenceSource: ConfidenceSource = ConfidenceSource.HEURISTIC,
        val blocksCount: Int = 0,
        val wordCount: Int = 0,
        val processingTimeMs: Long = 0L,
        val qualityWarnings: List<String> = emptyList()
    )

    /**
     * Main entry point to analyze document bitmap with safety checks,
     * memory limits, ML Kit OCR recognition, heuristic confidence calculation,
     * and schema-specific structuring.
     */
    suspend fun analyzeDocumentBitmap(
        bitmap: Bitmap,
        hintText: String? = null,
        forcedType: DocumentType? = null
    ): OcrResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()

        // 1. Safety check for recycled, zero-dimension, or invalid bitmaps
        if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) {
            return@withContext createEmptyResult(
                forcedType = forcedType,
                message = "Invalid or recycled document bitmap. Unable to perform OCR extraction."
            )
        }

        // 2. OCR text extraction
        val rawOutput = if (!hintText.isNullOrBlank()) {
            RawOcrOutput(
                text = hintText.trim(),
                lineCount = hintText.trim().lines().filter { it.isNotBlank() }.size,
                wordCount = hintText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size,
                isSuccessful = true
            )
        } else {
            recognizeDetailedFromBitmap(bitmap)
        }

        val elapsed = System.currentTimeMillis() - startTime
        val parsed = parseTextToStructuredData(
            text = rawOutput.text,
            forcedType = forcedType,
            measuredConfidence = rawOutput.measuredConfidence,
            blocksCount = rawOutput.blocks.size,
            wordCount = rawOutput.wordCount
        )

        return@withContext parsed.copy(processingTimeMs = elapsed)
    }

    /**
     * Convenience method returning raw string text from bitmap with ML Kit.
     */
    suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String {
        return recognizeDetailedFromBitmap(bitmap).text
    }

    /**
     * Executes ML Kit Text Recognition with coroutine cancellation,
     * memory bounding, and structured token extraction.
     */
    suspend fun recognizeDetailedFromBitmap(bitmap: Bitmap): RawOcrOutput = withContext(Dispatchers.Default) {
        if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) {
            return@withContext RawOcrOutput(
                text = "",
                isSuccessful = false,
                errorMessage = "Bitmap is recycled or has invalid dimensions."
            )
        }

        // Downscale excessively large bitmaps (>4096px) to prevent ML Kit native memory crashes
        val preparedBitmap = if (bitmap.width > 4096 || bitmap.height > 4096) {
            val scale = 4096f / max(bitmap.width, bitmap.height)
            try {
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                )
            } catch (e: OutOfMemoryError) {
                bitmap
            }
        } else {
            bitmap
        }

        return@withContext suspendCancellableCoroutine { continuation ->
            var recognizer: com.google.mlkit.vision.text.TextRecognizer? = null
            try {
                recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val image = InputImage.fromBitmap(preparedBitmap, 0)

                recognizer.process(image)
                    .addOnSuccessListener { visionText: Text ->
                        try {
                            val blocks = mutableListOf<OcrBlock>()
                            val detectedLangs = mutableSetOf<String>()
                            var totalLines = 0
                            var totalWords = 0

                            for (block in visionText.textBlocks) {
                                val blockLines = mutableListOf<OcrLine>()
                                block.recognizedLanguage?.let { if (it.isNotBlank()) detectedLangs.add(it) }

                                for (line in block.lines) {
                                    totalLines++
                                    val elementTexts = line.elements.map { el ->
                                        totalWords++
                                        el.text
                                    }
                                    blockLines.add(
                                        OcrLine(
                                            text = line.text,
                                            boundingBox = line.boundingBox,
                                            confidence = null,
                                            elements = elementTexts
                                        )
                                    )
                                }

                                blocks.add(
                                    OcrBlock(
                                        text = block.text,
                                        boundingBox = block.boundingBox,
                                        lines = blockLines,
                                        recognizedLanguage = block.recognizedLanguage
                                    )
                                )
                            }

                            val resultText = visionText.text.trim()
                            val output = RawOcrOutput(
                                text = resultText,
                                blocks = blocks,
                                lineCount = totalLines,
                                wordCount = totalWords,
                                measuredConfidence = null,
                                detectedLanguages = detectedLangs.toList(),
                                isSuccessful = true
                            )
                            if (continuation.isActive) continuation.resume(output)
                        } catch (e: Exception) {
                            if (continuation.isActive) {
                                continuation.resume(
                                    RawOcrOutput(
                                        text = visionText.text.trim(),
                                        isSuccessful = true
                                    )
                                )
                            }
                        } finally {
                            try { recognizer?.close() } catch (_: Exception) {}
                            if (preparedBitmap !== bitmap && !preparedBitmap.isRecycled) {
                                try { preparedBitmap.recycle() } catch (_: Exception) {}
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        try { recognizer?.close() } catch (_: Exception) {}
                        if (preparedBitmap !== bitmap && !preparedBitmap.isRecycled) {
                            try { preparedBitmap.recycle() } catch (_: Exception) {}
                        }
                        if (continuation.isActive) {
                            continuation.resume(
                                RawOcrOutput(
                                    text = "",
                                    isSuccessful = false,
                                    errorMessage = exception.localizedMessage ?: "OCR recognition failed."
                                )
                            )
                        }
                    }

                continuation.invokeOnCancellation {
                    try { recognizer?.close() } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                try { recognizer?.close() } catch (_: Exception) {}
                if (continuation.isActive) {
                    continuation.resume(
                        RawOcrOutput(
                            text = "",
                            isSuccessful = false,
                            errorMessage = e.localizedMessage ?: "Exception initializing OCR engine."
                        )
                    )
                }
            }
        }
    }

    /**
     * Parses OCR text into structured Key-Value pairs, Data Tables, and Document Summary.
     * Computes honest, non-fabricated confidence metrics with explicit labeling.
     */
    fun parseTextToStructuredData(
        text: String,
        forcedType: DocumentType? = null,
        measuredConfidence: Float? = null,
        blocksCount: Int = 0,
        wordCount: Int = 0
    ): OcrResult {
        val cleanText = text.trim()
        val lines = cleanText.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return createEmptyResult(forcedType, "No legible text was detected in the document. Please verify lighting and focus.")
        }

        val fields = mutableListOf<ExtractedField>()
        val tables = mutableListOf<ExtractedTable>()
        val qualityWarnings = mutableListOf<String>()

        // 1. Classify Document Type
        val detectedType = forcedType ?: classifyDocumentType(lines)

        // 2. Extract Key-Value Pairs and Tabular Rows
        val kvRegex = Regex("^([A-Za-z0-9\\s_\\-#/().]{2,35})\\s*[:=–—-]\\s*(.+)$")
        val tableCandidateRows = mutableListOf<List<String>>()
        var currentTableHeaders: List<String>? = null

        // Domain specific scanning states
        var inTableSection = false

        for (line in lines) {
            // Check for explicit table separators, pipe delimiters, tabs, or multi-space aligned columns
            val isPipeDelimited = line.contains("|")
            val isTabDelimited = line.contains("\t")
            val isMultiSpaceColumn = line.split(Regex("\\s{2,}")).size >= 3

            // Ignore markdown / ASCII table border rows like "|---|---|---|"
            if (line.matches(Regex("^[|\\-\\s+:]+$"))) {
                continue
            }

            if (isPipeDelimited || isTabDelimited || isMultiSpaceColumn) {
                val parts = when {
                    isPipeDelimited -> line.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                    isTabDelimited -> line.split("\t").map { it.trim() }.filter { it.isNotEmpty() }
                    else -> line.split(Regex("\\s{2,}")).map { it.trim() }.filter { it.isNotEmpty() }
                }

                if (parts.size >= 2) {
                    if (currentTableHeaders == null) {
                        currentTableHeaders = parts
                        inTableSection = true
                    } else {
                        tableCandidateRows.add(parts)
                    }
                    continue
                }
            }

            // Check Key-Value match
            val kvMatch = kvRegex.find(line)
            if (kvMatch != null) {
                val rawKey = kvMatch.groupValues[1].trim()
                val rawVal = kvMatch.groupValues[2].trim()
                if (rawKey.isNotBlank() && rawVal.isNotBlank()) {
                    val fieldConf = evaluateFieldConfidence(rawKey, rawVal)
                    fields.add(
                        ExtractedField(
                            key = rawKey,
                            value = rawVal,
                            confidence = fieldConf,
                            category = determineCategory(rawKey, detectedType),
                            confidenceSource = ConfidenceSource.HEURISTIC
                        )
                    )
                    continue
                }
            }

            // Domain-specific regex token extraction for unpunctuated lines
            when {
                // Receipt / Invoice item row pattern: "Item Name 2 x 15.00 30.00" or "Item Name $24.99" or "Item 1 x ₹65.00 ₹65.00"
                line.matches(Regex("^(.*?)\\s+(\\d+\\s*[xX]\\s*[₹$€£¥]?\\d+[.,]\\d{2})?\\s*([₹$€£¥]?[\\d,]+[.]\\d{2})$")) -> {
                    val match = Regex("^(.*?)\\s+(\\d+\\s*[xX]\\s*[₹$€£¥]?\\d+[.,]\\d{2})?\\s*([₹$€£¥]?[\\d,]+[.]\\d{2})$").find(line)
                    if (match != null && detectedType in listOf(DocumentType.RECEIPT, DocumentType.INVOICE)) {
                        val itemName = match.groupValues[1].trim()
                        val qtyInfo = match.groupValues[2].trim()
                        val itemPrice = match.groupValues[3].trim()
                        if (itemName.length >= 2 && !itemName.equals("Total", ignoreCase = true) && !itemName.equals("Subtotal", ignoreCase = true) && !itemName.equals("Tax", ignoreCase = true)) {
                            if (currentTableHeaders == null) {
                                currentTableHeaders = listOf("Description", "Qty / Unit", "Amount")
                            }
                            tableCandidateRows.add(listOf(itemName, if (qtyInfo.isNotEmpty()) qtyInfo else "1", itemPrice))
                            continue
                        }
                    }
                }

                // Checkbox / Form fields: "[X] Male   [ ] Female" or "Status: [x] Approved"
                line.contains("[x]", ignoreCase = true) || line.contains("[ ]") || line.contains("(x)", ignoreCase = true) -> {
                    val cleaned = line.replace(Regex("[\\[\\]()]"), " ").trim()
                    fields.add(
                        ExtractedField(
                            key = "Selection / Status",
                            value = line,
                            confidence = 0.90f,
                            category = "Administrative",
                            confidenceSource = ConfidenceSource.HEURISTIC
                        )
                    )
                }

                // Financial Totals
                line.contains("Grand Total", ignoreCase = true) || line.contains("Total Amount", ignoreCase = true) || line.matches(Regex("(?i)^Total\\b.*")) -> {
                    val cleanedVal = extractNumericValueOrFullLine(line, "Total")
                    fields.add(ExtractedField(key = "Grand Total", value = cleanedVal, confidence = 0.95f, category = "Financial", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Subtotal", ignoreCase = true) || line.contains("Sub Total", ignoreCase = true) -> {
                    val cleanedVal = extractNumericValueOrFullLine(line, "Subtotal")
                    fields.add(ExtractedField(key = "Subtotal", value = cleanedVal, confidence = 0.94f, category = "Financial", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Tax", ignoreCase = true) || line.contains("VAT", ignoreCase = true) || line.contains("GST", ignoreCase = true) -> {
                    val cleanedVal = extractNumericValueOrFullLine(line, "Tax")
                    fields.add(ExtractedField(key = "Tax / VAT / GST", value = cleanedVal, confidence = 0.92f, category = "Financial", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Discount", ignoreCase = true) -> {
                    val cleanedVal = extractNumericValueOrFullLine(line, "Discount")
                    fields.add(ExtractedField(key = "Discount", value = cleanedVal, confidence = 0.90f, category = "Financial", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Due Date", ignoreCase = true) -> {
                    val cleanedVal = extractNumericValueOrFullLine(line, "Due Date")
                    fields.add(ExtractedField(key = "Due Date", value = cleanedVal, confidence = 0.93f, category = "Temporal", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Date", ignoreCase = true) || line.matches(Regex(".*\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\b.*")) -> {
                    if (!fields.any { it.key.equals("Date", ignoreCase = true) }) {
                        fields.add(ExtractedField(key = "Date", value = line, confidence = 0.93f, category = "Temporal", confidenceSource = ConfidenceSource.HEURISTIC))
                    }
                }
                line.contains("Invoice", ignoreCase = true) && line.matches(Regex(".*\\d+.*")) -> {
                    if (!fields.any { it.key.contains("Invoice", ignoreCase = true) }) {
                        fields.add(ExtractedField(key = "Invoice / Ref Number", value = line, confidence = 0.95f, category = "Identifier", confidenceSource = ConfidenceSource.HEURISTIC))
                    }
                }
                line.contains("Account", ignoreCase = true) && line.matches(Regex(".*\\d+.*")) -> {
                    if (!fields.any { it.key.contains("Account", ignoreCase = true) }) {
                        fields.add(ExtractedField(key = "Account Number", value = line, confidence = 0.93f, category = "Identifier", confidenceSource = ConfidenceSource.HEURISTIC))
                    }
                }
                line.contains("Balance", ignoreCase = true) && line.matches(Regex(".*[\\$€£¥]?\\d+[.,]\\d{2}.*")) -> {
                    fields.add(ExtractedField(key = "Account Balance", value = line, confidence = 0.94f, category = "Financial", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("GPA", ignoreCase = true) || line.contains("CGPA", ignoreCase = true) -> {
                    fields.add(ExtractedField(key = "Grade Point Average (GPA)", value = line, confidence = 0.94f, category = "Academic", confidenceSource = ConfidenceSource.HEURISTIC))
                }
                line.contains("Roll No", ignoreCase = true) || line.contains("Reg No", ignoreCase = true) || line.contains("Registration Number", ignoreCase = true) -> {
                    fields.add(ExtractedField(key = "Registration / Roll No", value = line, confidence = 0.95f, category = "Identifier", confidenceSource = ConfidenceSource.HEURISTIC))
                }
            }
        }

        // 3. Assemble detected table if headers and rows exist
        if (currentTableHeaders != null && tableCandidateRows.isNotEmpty()) {
            val numCols = currentTableHeaders.size
            val normalizedRows = tableCandidateRows.map { row ->
                val filledRow = row.toMutableList()
                while (filledRow.size < numCols) {
                    filledRow.add("-")
                }
                filledRow.take(numCols).toMutableList()
            }.toMutableList()

            val tableConfidence = calculateTableConfidence(currentTableHeaders, normalizedRows)
            tables.add(
                ExtractedTable(
                    name = when (detectedType) {
                        DocumentType.INVOICE, DocumentType.RECEIPT -> "Line Items & Charges"
                        DocumentType.BANK_STATEMENT -> "Transactions Ledger"
                        DocumentType.MARK_SHEET -> "Course Grades & Marks"
                        DocumentType.FORM -> "Form Data Table"
                        else -> "Extracted Matrix"
                    },
                    headers = currentTableHeaders.toMutableList(),
                    rows = normalizedRows,
                    confidence = tableConfidence,
                    confidenceSource = ConfidenceSource.HEURISTIC
                )
            )
        }

        // Deduplicate fields with identical key and value
        val uniqueFields = mutableListOf<ExtractedField>()
        val seen = mutableSetOf<String>()
        for (f in fields) {
            val sig = "${f.key.lowercase()}|${f.value.lowercase()}"
            if (seen.add(sig)) {
                uniqueFields.add(f)
            }
        }

        // 4. Calculate Heuristic Confidence Metric based on Lexical Quality and Structural Consistency
        val calculatedConfidence = calculateHeuristicConfidence(
            rawText = cleanText,
            lines = lines,
            fields = uniqueFields,
            tables = tables,
            measuredConfidence = measuredConfidence,
            qualityWarnings = qualityWarnings
        )

        val confSource = if (measuredConfidence != null) {
            ConfidenceSource.HYBRID
        } else {
            ConfidenceSource.HEURISTIC
        }

        // 5. Generate Document Summary
        val summary = generateSummary(detectedType, uniqueFields, tables, lines, qualityWarnings)

        val words = if (wordCount > 0) wordCount else cleanText.split(Regex("\\s+")).filter { it.isNotBlank() }.size

        return OcrResult(
            rawText = cleanText,
            detectedDocType = detectedType,
            summary = summary,
            fields = uniqueFields,
            tables = tables,
            overallConfidence = calculatedConfidence,
            lineCount = lines.size,
            confidenceSource = confSource,
            blocksCount = if (blocksCount > 0) blocksCount else (lines.size / 3).coerceAtLeast(1),
            wordCount = words,
            qualityWarnings = qualityWarnings
        )
    }

    private fun evaluateFieldConfidence(key: String, value: String): Float {
        var score = 0.85f
        if (key.length in 3..25) score += 0.05f
        if (value.length in 2..60) score += 0.04f
        // Check for alphanumeric structure
        val symbolRatio = value.count { !it.isLetterOrDigit() && !it.isWhitespace() && it != '-' && it != '/' && it != '.' && it != '$' && it != ':' }.toFloat() / value.length.coerceAtLeast(1)
        if (symbolRatio > 0.3f) score -= 0.15f // Garbled symbol penalty
        return score.coerceIn(0.60f, 0.98f)
    }

    private fun calculateTableConfidence(headers: List<String>, rows: List<List<String>>): Float {
        var score = 0.88f
        if (headers.size in 2..8) score += 0.04f
        if (rows.isNotEmpty()) score += 0.03f
        val emptyCellRatio = rows.sumOf { row -> row.count { it == "-" || it.isBlank() } }.toFloat() / (headers.size * rows.size).coerceAtLeast(1)
        if (emptyCellRatio > 0.4f) score -= 0.12f
        return score.coerceIn(0.65f, 0.98f)
    }

    /**
     * Non-fabricated heuristic confidence calculation based on:
     * - Alphanumeric / symbol ratio (detects garbled noise)
     * - Recognizable word length and count
     * - Delimiter consistency
     * - Structural schema match
     */
    fun calculateHeuristicConfidence(
        rawText: String,
        lines: List<String>,
        fields: List<ExtractedField>,
        tables: List<ExtractedTable>,
        measuredConfidence: Float?,
        qualityWarnings: MutableList<String>
    ): Float {
        if (rawText.isBlank() || lines.isEmpty()) return 0.0f

        val totalChars = rawText.length
        val alphaNumericChars = rawText.count { it.isLetterOrDigit() }
        val whitespaceChars = rawText.count { it.isWhitespace() }
        val alphaNumericRatio = alphaNumericChars.toFloat() / (totalChars - whitespaceChars).coerceAtLeast(1)

        // Baseline score from character clarity
        var score = (alphaNumericRatio * 0.50f) + 0.35f

        // Noise detection: high proportion of weird symbols
        val noiseChars = rawText.count { !it.isLetterOrDigit() && !it.isWhitespace() && !it.isStandardPunctuation() }
        val noiseRatio = noiseChars.toFloat() / totalChars.coerceAtLeast(1)
        if (noiseRatio > 0.15f) {
            score -= 0.20f
            qualityWarnings.add("Document contains optical noise or unrecognized character symbols.")
        }

        // Short / low-resolution text penalty
        if (rawText.length < 30 && lines.size <= 2) {
            score -= 0.15f
            qualityWarnings.add("Low text volume detected. Image resolution or illumination may be low.")
        }

        // Structural schema bonus
        if (fields.isNotEmpty()) {
            val avgFieldConf = fields.map { it.confidence }.average().toFloat()
            score = (score * 0.6f) + (avgFieldConf * 0.4f)
        }
        if (tables.isNotEmpty()) {
            val avgTableConf = tables.map { it.confidence }.average().toFloat()
            score = (score * 0.7f) + (avgTableConf * 0.3f)
        }

        // If measured confidence is available, blend with heuristic
        if (measuredConfidence != null && measuredConfidence > 0f) {
            score = (measuredConfidence * 0.5f) + (score * 0.5f)
        }

        return score.coerceIn(0.10f, 0.99f)
    }

    private fun Char.isStandardPunctuation(): Boolean {
        return this in setOf('.', ',', ':', ';', '-', '/', '\\', '(', ')', '[', ']', '$', '€', '£', '¥', '%', '@', '#', '&', '*', '+', '=', '"', '\'', '?', '!')
    }

    private fun extractNumericValueOrFullLine(line: String, prefix: String): String {
        val colonIdx = line.indexOf(':')
        if (colonIdx != -1 && colonIdx < line.length - 1) {
            return line.substring(colonIdx + 1).trim()
        }
        val regex = Regex("(?i)$prefix\\s*[:=]?\\s*(.*)")
        val match = regex.find(line)
        return match?.groupValues?.get(1)?.trim() ?: line
    }

    private fun classifyDocumentType(lines: List<String>): DocumentType {
        val fullText = lines.joinToString(" ").lowercase()
        return when {
            fullText.contains("tax invoice") || fullText.contains("invoice #") || fullText.contains("invoice no") || fullText.contains("bill to") || fullText.contains("invoice date") || fullText.contains("gstin") || fullText.contains("चालान") || (fullText.contains("invoice") && fullText.contains("total")) -> DocumentType.INVOICE
            fullText.contains("receipt") || fullText.contains("cashier") || fullText.contains("change due") || fullText.contains("pos terminal") || fullText.contains("रसीद") || (fullText.contains("subtotal") && fullText.contains("tax") && fullText.contains("cash")) -> DocumentType.RECEIPT
            fullText.contains("statement of account") || fullText.contains("bank statement") || fullText.contains("balance forward") || fullText.contains("account balance") || fullText.contains("credit balance") || fullText.contains("closing balance") || fullText.contains("खाता") || fullText.contains("ifsc") -> DocumentType.BANK_STATEMENT
            fullText.contains("application form") || fullText.contains("registration form") || fullText.contains("date of birth") || fullText.contains("applicant signature") || fullText.contains("kyc form") || fullText.contains("[x]") || fullText.contains("[ ]") -> DocumentType.FORM
            fullText.contains("certificate of") || fullText.contains("hereby certifies") || fullText.contains("awarded to") || fullText.contains("in witness whereof") || fullText.contains("conferred upon") || fullText.contains("प्रमाण पत्र") -> DocumentType.CERTIFICATE
            fullText.contains("grade point") || fullText.contains("marksheet") || fullText.contains("mark sheet") || fullText.contains("transcript of records") || fullText.contains("semester") || fullText.contains("sgpa") || fullText.contains("cgpa") || fullText.contains("अंकतालिका") -> DocumentType.MARK_SHEET
            fullText.contains("identity card") || fullText.contains("driving license") || fullText.contains("driver license") || fullText.contains("id no") || fullText.contains("passport") || fullText.contains("national identity") || fullText.contains("aadhaar") || fullText.contains("pan card") || fullText.contains("voter id") -> DocumentType.ID_CARD
            fullText.contains("business card") || (fullText.contains("tel:") && fullText.contains("@") && fullText.contains("www.")) || (fullText.contains("mobile:") && fullText.contains("@")) -> DocumentType.BUSINESS_CARD
            fullText.contains("|") || fullText.contains("columns") || fullText.contains("table") -> DocumentType.TABLE
            else -> DocumentType.GENERAL_DOCUMENT
        }
    }

    private fun determineCategory(key: String, docType: DocumentType): String {
        val lowerKey = key.lowercase()
        return when {
            lowerKey.contains("gst") || lowerKey.contains("cgst") || lowerKey.contains("sgst") || lowerKey.contains("igst") || lowerKey.contains("hsn") || lowerKey.contains("sac") || lowerKey.contains("cess") -> "Tax"
            lowerKey.contains("total") || lowerKey.contains("amount") || lowerKey.contains("price") || lowerKey.contains("tax") || lowerKey.contains("subtotal") || lowerKey.contains("balance") || lowerKey.contains("fee") || lowerKey.contains("cost") || lowerKey.contains("discount") -> "Financial"
            lowerKey.contains("date") || lowerKey.contains("due") || lowerKey.contains("time") || lowerKey.contains("year") || lowerKey.contains("period") || lowerKey.contains("month") -> "Temporal"
            lowerKey.contains("name") || lowerKey.contains("vendor") || lowerKey.contains("client") || lowerKey.contains("customer") || lowerKey.contains("recipient") || lowerKey.contains("issuer") || lowerKey.contains("holder") || lowerKey.contains("candidate") || lowerKey.contains("student") -> "Party / Entity"
            lowerKey.contains("id") || lowerKey.contains("number") || lowerKey.contains("no.") || lowerKey.contains("code") || lowerKey.contains("reg") || lowerKey.contains("pin") || lowerKey.contains("pincode") || lowerKey.contains("pan") || lowerKey.contains("aadhaar") || lowerKey.contains("gstin") || lowerKey.contains("ifsc") || lowerKey.contains("roll") || lowerKey.contains("ssn") -> "Identifier"
            lowerKey.contains("address") || lowerKey.contains("city") || lowerKey.contains("zip") || lowerKey.contains("pincode") || lowerKey.contains("pin") || lowerKey.contains("state") || lowerKey.contains("country") || lowerKey.contains("street") || lowerKey.contains("road") || lowerKey.contains("nagar") || lowerKey.contains("marg") -> "Location"
            else -> docType.category
        }
    }

    private fun generateSummary(
        docType: DocumentType,
        fields: List<ExtractedField>,
        tables: List<ExtractedTable>,
        lines: List<String>,
        qualityWarnings: List<String>
    ): String {
        val keyFields = fields.take(3).joinToString(", ") { "${it.key}: ${it.value}" }
        val tableDesc = if (tables.isNotEmpty()) " Detected ${tables.size} data table with ${tables.sumOf { it.rows.size }} rows." else ""
        val warningDesc = if (qualityWarnings.isNotEmpty()) " (Note: ${qualityWarnings.first()})" else ""

        return if (keyFields.isNotBlank()) {
            "Parsed ${docType.displayName} with ${lines.size} text lines. Extracted parameters ($keyFields).$tableDesc$warningDesc"
        } else {
            "Extracted ${lines.size} lines of text from ${docType.displayName}.$tableDesc$warningDesc"
        }
    }

    private fun createEmptyResult(forcedType: DocumentType?, message: String): OcrResult {
        return OcrResult(
            rawText = "",
            detectedDocType = forcedType ?: DocumentType.GENERAL_DOCUMENT,
            summary = message,
            fields = emptyList(),
            tables = emptyList(),
            overallConfidence = 0.0f,
            lineCount = 0,
            confidenceSource = ConfidenceSource.HEURISTIC,
            blocksCount = 0,
            wordCount = 0,
            processingTimeMs = 0L,
            qualityWarnings = listOf(message)
        )
    }
}
