package com.example.snapdata.processing

import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import java.util.UUID

/**
 * Enterprise multi-page OCR consolidation engine.
 *
 * Responsibilities:
 * - Preserves strict sequential page ordering (Page 1, 2, ..., N)
 * - Formats combined raw OCR text with clear page demarcation headers
 * - Extracts and aggregates key-value pairs across pages without loss
 * - Intelligent field deduplication (merging identical keys across repeated page headers)
 * - Cross-page table stitching: merges consecutive page tables with matching headers/structures
 * - Generates holistic multi-page summaries and re-evaluates overall document classification
 */
object MultiPageDocumentMerger {

    data class PageOcrData(
        val pageIndex: Int, // 1-indexed (1, 2, ...)
        val rawText: String,
        val ocrResult: OcrEngine.OcrResult
    )

    /**
     * Consolidates a sequential list of individual page OCR results into a single comprehensive OcrResult.
     */
    fun combineMultiPageResults(
        pages: List<PageOcrData>,
        forcedType: DocumentType? = null
    ): OcrEngine.OcrResult {
        if (pages.isEmpty()) {
            return OcrEngine.OcrResult(
                rawText = "",
                detectedDocType = forcedType ?: DocumentType.GENERAL_DOCUMENT,
                summary = "Empty document. No pages were processed.",
                fields = emptyList(),
                tables = emptyList(),
                overallConfidence = 0.0f,
                lineCount = 0,
                confidenceSource = com.example.snapdata.model.ConfidenceSource.HEURISTIC
            )
        }

        // 1. Preserve Page Order: Ensure sorted by pageIndex
        val sortedPages = pages.sortedBy { it.pageIndex }
        val totalPages = sortedPages.size

        // 2. Format Combined Raw OCR Text with explicit Page Demarcations
        val combinedTextBuilder = StringBuilder()
        sortedPages.forEachIndexed { idx, pageData ->
            if (idx > 0) {
                combinedTextBuilder.append("\n\n")
            }
            combinedTextBuilder.append("--- PAGE ${pageData.pageIndex} OF $totalPages ---\n")
            combinedTextBuilder.append(pageData.rawText.trim())
        }
        val fullCombinedText = combinedTextBuilder.toString()

        // 3. Classify Overall Document Type across the entire combined text
        val overallParsed = OcrEngine.parseTextToStructuredData(fullCombinedText, forcedType)
        val overallDocType = forcedType ?: overallParsed.detectedDocType

        // 4. Combine and Deduplicate Extracted Fields across all pages
        val mergedFields = mergeFieldsAcrossPages(sortedPages, overallDocType)

        // 5. Detect and Stitch Tables across all pages
        val mergedTables = stitchTablesAcrossPages(sortedPages, overallDocType)

        // 6. Calculate Weighted Overall Confidence
        val allConfidenceScores = sortedPages.map { it.ocrResult.overallConfidence }
        val avgPageConf = if (allConfidenceScores.isNotEmpty()) allConfidenceScores.average().toFloat() else 0.90f
        val overallConfidence = avgPageConf.coerceIn(0.70f, 0.99f)

        // 7. Generate Multi-Page Document Summary
        val totalLines = sortedPages.sumOf { it.ocrResult.lineCount }
        val summary = generateMultiPageSummary(
            docType = overallDocType,
            totalPages = totalPages,
            totalLines = totalLines,
            fields = mergedFields,
            tables = mergedTables
        )

        return OcrEngine.OcrResult(
            rawText = fullCombinedText,
            detectedDocType = overallDocType,
            summary = summary,
            fields = mergedFields,
            tables = mergedTables,
            overallConfidence = overallConfidence,
            lineCount = totalLines
        )
    }

    /**
     * Aggregates fields from all pages, deduplicating identical key-value pairs (e.g. repeated company headers)
     * while retaining page-distinct values and boosting confidence for verified repeated fields.
     */
    private fun mergeFieldsAcrossPages(
        pages: List<PageOcrData>,
        docType: DocumentType
    ): List<ExtractedField> {
        val fieldMap = LinkedHashMap<String, ExtractedField>()

        for (page in pages) {
            for (field in page.ocrResult.fields) {
                val normalizedKey = field.key.trim()
                val existing = fieldMap[normalizedKey]

                if (existing == null) {
                    // First time seeing this key
                    fieldMap[normalizedKey] = field.copy()
                } else {
                    // Check if value is identical or very similar
                    if (existing.value.equals(field.value, ignoreCase = true)) {
                        // Boost confidence since confirmed on multiple pages
                        val boostedConf = minOf(1.0f, existing.confidence + 0.03f)
                        fieldMap[normalizedKey] = existing.copy(confidence = boostedConf)
                    } else {
                        // Different value on subsequent page (e.g. Page Subtotals or Multi-Item fields)
                        // If it's a page-specific field or accumulator, add as page-qualified or distinct entry
                        val pageQualifiedKey = "${normalizedKey} (Pg ${page.pageIndex})"
                        if (!fieldMap.containsKey(pageQualifiedKey)) {
                            fieldMap[pageQualifiedKey] = field.copy(key = pageQualifiedKey)
                        }
                    }
                }
            }
        }

        return fieldMap.values.toList()
    }

    /**
     * Stitches tables across multiple pages.
     * If Page N and Page N+1 both have tables with matching headers (or identical column counts and column names),
     * their rows are combined into a single unified table.
     */
    private fun stitchTablesAcrossPages(
        pages: List<PageOcrData>,
        docType: DocumentType
    ): List<ExtractedTable> {
        val stitchedTables = mutableListOf<ExtractedTable>()
        var currentStitchedTable: ExtractedTable? = null

        for (page in pages) {
            val pageTables = page.ocrResult.tables
            for (table in pageTables) {
                if (currentStitchedTable == null) {
                    currentStitchedTable = ExtractedTable(
                        id = UUID.randomUUID().toString(),
                        name = table.name,
                        headers = table.headers.toMutableList(),
                        rows = table.rows.map { it.toMutableList() }.toMutableList(),
                        confidence = table.confidence
                    )
                } else {
                    // Check if this table is a continuation of the previous table
                    val headersMatch = areHeadersCompatible(currentStitchedTable.headers, table.headers)
                    val columnCountMatches = currentStitchedTable.headers.size == table.headers.size

                    if (headersMatch || columnCountMatches) {
                        // Append rows from this page's table
                        val targetColCount = currentStitchedTable.headers.size
                        for (row in table.rows) {
                            val normalizedRow = row.toMutableList()
                            while (normalizedRow.size < targetColCount) {
                                normalizedRow.add("-")
                            }
                            currentStitchedTable.rows.add(normalizedRow.take(targetColCount).toMutableList())
                        }
                        // Update confidence
                        val updatedConf = ((currentStitchedTable.confidence + table.confidence) / 2.0f).coerceIn(0.70f, 0.99f)
                        currentStitchedTable = currentStitchedTable.copy(confidence = updatedConf)
                    } else {
                        // Different table structure: finalize previous table and start new table
                        stitchedTables.add(currentStitchedTable)
                        currentStitchedTable = ExtractedTable(
                            id = UUID.randomUUID().toString(),
                            name = table.name,
                            headers = table.headers.toMutableList(),
                            rows = table.rows.map { it.toMutableList() }.toMutableList(),
                            confidence = table.confidence
                        )
                    }
                }
            }
        }

        if (currentStitchedTable != null) {
            stitchedTables.add(currentStitchedTable)
        }

        return stitchedTables
    }

    /**
     * Checks whether two header lists represent the same table schema.
     */
    private fun areHeadersCompatible(headersA: List<String>, headersB: List<String>): Boolean {
        if (headersA.size != headersB.size) return false
        var matches = 0
        for (i in headersA.indices) {
            if (headersA[i].equals(headersB[i], ignoreCase = true) ||
                headersA[i].contains(headersB[i], ignoreCase = true) ||
                headersB[i].contains(headersA[i], ignoreCase = true)
            ) {
                matches++
            }
        }
        return matches.toFloat() / headersA.size >= 0.6f
    }

    private fun generateMultiPageSummary(
        docType: DocumentType,
        totalPages: Int,
        totalLines: Int,
        fields: List<ExtractedField>,
        tables: List<ExtractedTable>
    ): String {
        val keyFields = fields.take(3).joinToString(", ") { "${it.key}: ${it.value}" }
        val tableDesc = if (tables.isNotEmpty()) {
            val totalRows = tables.sumOf { it.rows.size }
            " Extracted ${tables.size} table structure(s) with $totalRows total rows stitched across pages."
        } else {
            ""
        }

        val pageWord = if (totalPages == 1) "1 page" else "$totalPages pages"
        return if (keyFields.isNotBlank()) {
            "Parsed ${docType.displayName} across $pageWord ($totalLines text lines). Extracted key parameters ($keyFields).$tableDesc"
        } else {
            "Extracted $totalLines lines of text across $pageWord of ${docType.displayName}.$tableDesc"
        }
    }
}
