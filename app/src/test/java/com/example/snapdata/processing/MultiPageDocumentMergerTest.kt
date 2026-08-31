package com.example.snapdata.processing

import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for MultiPageDocumentMerger.
 *
 * Tests: page ordering, field deduplication, table stitching, edge cases,
 * empty pages, OCR failure pages, and large page counts.
 */
class MultiPageDocumentMergerTest {

    // ---------------------------------------------------------------------------
    // Helper builders
    // ---------------------------------------------------------------------------

    private fun makeOcrResult(
        rawText: String = "",
        docType: DocumentType = DocumentType.GENERAL_DOCUMENT,
        fields: List<ExtractedField> = emptyList(),
        tables: List<ExtractedTable> = emptyList(),
        confidence: Float = 0.90f
    ) = OcrEngine.OcrResult(
        rawText = rawText,
        detectedDocType = docType,
        summary = "Test summary",
        fields = fields,
        tables = tables,
        overallConfidence = confidence,
        lineCount = rawText.lines().filter { it.isNotBlank() }.size,
        confidenceSource = ConfidenceSource.HEURISTIC
    )

    private fun makeField(key: String, value: String, confidence: Float = 0.9f) =
        ExtractedField(key = key, value = value, confidence = confidence)

    private fun makeTable(
        tableName: String,
        headers: List<String>,
        rows: List<List<String>>,
        confidence: Float = 0.85f
    ) = ExtractedTable(
        name = tableName,
        headers = headers.toMutableList(),
        rows = rows.map { it.toMutableList() }.toMutableList(),
        confidence = confidence
    )

    private fun makePageData(
        pageIndex: Int,
        rawText: String = "Page $pageIndex text",
        fields: List<ExtractedField> = emptyList(),
        tables: List<ExtractedTable> = emptyList(),
        confidence: Float = 0.90f
    ) = MultiPageDocumentMerger.PageOcrData(
        pageIndex = pageIndex,
        rawText = rawText,
        ocrResult = makeOcrResult(rawText, fields = fields, tables = tables, confidence = confidence)
    )

    // ---------------------------------------------------------------------------
    // Empty input
    // ---------------------------------------------------------------------------

    @Test
    fun `combineMultiPageResults with empty list returns empty result`() {
        val result = MultiPageDocumentMerger.combineMultiPageResults(emptyList())
        assertEquals("", result.rawText)
        assertTrue(result.fields.isEmpty())
        assertTrue(result.tables.isEmpty())
        assertEquals(0.0f, result.overallConfidence, 0.001f)
        assertEquals(DocumentType.GENERAL_DOCUMENT, result.detectedDocType)
    }

    // ---------------------------------------------------------------------------
    // Page ordering
    // ---------------------------------------------------------------------------

    @Test
    fun `combineMultiPageResults preserves strict sequential page ordering`() {
        val pages = listOf(
            makePageData(pageIndex = 3, rawText = "Third page"),
            makePageData(pageIndex = 1, rawText = "First page"),
            makePageData(pageIndex = 2, rawText = "Second page")
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        val text = result.rawText
        val idx1 = text.indexOf("First page")
        val idx2 = text.indexOf("Second page")
        val idx3 = text.indexOf("Third page")
        assertTrue("Page 1 must appear before Page 2", idx1 < idx2)
        assertTrue("Page 2 must appear before Page 3", idx2 < idx3)
    }

    @Test
    fun `combineMultiPageResults includes page demarcation headers`() {
        val pages = listOf(
            makePageData(pageIndex = 1, rawText = "Content A"),
            makePageData(pageIndex = 2, rawText = "Content B")
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Should contain page 1 marker", result.rawText.contains("PAGE 1"))
        assertTrue("Should contain page 2 marker", result.rawText.contains("PAGE 2"))
    }

    @Test
    fun `single page result is preserved correctly`() {
        val fields = listOf(makeField("Name", "John Doe"))
        val pages = listOf(makePageData(pageIndex = 1, rawText = "Invoice #001", fields = fields))
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Raw text should contain page content", result.rawText.contains("Invoice #001"))
        assertEquals("Should have 1 field", 1, result.fields.size)
        assertEquals("Name", result.fields[0].key)
        assertEquals("John Doe", result.fields[0].value)
    }

    // ---------------------------------------------------------------------------
    // Field deduplication
    // ---------------------------------------------------------------------------

    @Test
    fun `duplicate fields across pages are deduplicated`() {
        val pages = listOf(
            makePageData(
                pageIndex = 1,
                fields = listOf(
                    makeField("Invoice Number", "INV-001"),
                    makeField("Date", "2026-01-15")
                )
            ),
            makePageData(
                pageIndex = 2,
                fields = listOf(
                    makeField("Invoice Number", "INV-001"), // exact duplicate
                    makeField("Total Amount", "5000.00")
                )
            )
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        val invoiceFields = result.fields.filter { it.key == "Invoice Number" }
        assertEquals("Duplicate invoice number field should be merged to 1", 1, invoiceFields.size)
    }

    @Test
    fun `fields from all pages are included in result`() {
        val pages = listOf(
            makePageData(pageIndex = 1, fields = listOf(makeField("FieldA", "ValueA"))),
            makePageData(pageIndex = 2, fields = listOf(makeField("FieldB", "ValueB")))
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("FieldA should be present", result.fields.any { it.key == "FieldA" })
        assertTrue("FieldB should be present", result.fields.any { it.key == "FieldB" })
    }

    // ---------------------------------------------------------------------------
    // Table stitching
    // ---------------------------------------------------------------------------

    @Test
    fun `tables with identical headers across consecutive pages are stitched`() {
        val headers = listOf("Item", "Qty", "Price")
        val pages = listOf(
            makePageData(
                pageIndex = 1,
                tables = listOf(makeTable("Items", headers, listOf(listOf("Widget A", "10", "5.00"))))
            ),
            makePageData(
                pageIndex = 2,
                tables = listOf(makeTable("Items", headers, listOf(listOf("Widget B", "5", "10.00"))))
            )
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Merged table should have rows from both pages", result.tables.isNotEmpty())
        val stitched = result.tables.find { t -> t.headers == headers }
        if (stitched != null) {
            assertEquals("Stitched table should have 2 rows", 2, stitched.rows.size)
        }
    }

    @Test
    fun `unrelated tables from different pages are not merged`() {
        val pages = listOf(
            makePageData(
                pageIndex = 1,
                tables = listOf(makeTable("Employees", listOf("ID", "Name"), listOf(listOf("1", "Alice"))))
            ),
            makePageData(
                pageIndex = 2,
                tables = listOf(makeTable("Products", listOf("SKU", "Description", "Price"), listOf(listOf("P001", "Widget", "9.99"))))
            )
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Both tables should be present separately", result.tables.size >= 2)
    }

    @Test
    fun `page-specific standalone table with no continuation is preserved`() {
        val pages = listOf(
            makePageData(
                pageIndex = 1,
                tables = listOf(makeTable("Summary", listOf("Key", "Value"), listOf(listOf("Total", "100"))))
            ),
            makePageData(pageIndex = 2, tables = emptyList())
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Summary table should still be present", result.tables.any { it.name == "Summary" })
    }

    @Test
    fun `tables with different column counts are not incorrectly merged`() {
        val pages = listOf(
            makePageData(
                pageIndex = 1,
                tables = listOf(makeTable("Data", listOf("Col1", "Col2"), listOf(listOf("a", "b"))))
            ),
            makePageData(
                pageIndex = 2,
                tables = listOf(makeTable("Data", listOf("Col1", "Col2", "Col3"), listOf(listOf("a", "b", "c"))))
            )
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        // Verify no row has a cell count mismatch vs its table headers
        for (table in result.tables) {
            for (row in table.rows) {
                assertTrue(
                    "Row cells (${row.size}) should not exceed header count (${table.headers.size})",
                    row.size <= table.headers.size
                )
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Confidence calculation
    // ---------------------------------------------------------------------------

    @Test
    fun `overall confidence is within valid range`() {
        val pages = listOf(
            makePageData(pageIndex = 1, confidence = 0.80f),
            makePageData(pageIndex = 2, confidence = 0.90f)
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Confidence should be between 0.0 and 1.0", result.overallConfidence in 0.0f..1.0f)
    }

    @Test
    fun `forced document type overrides auto-detection`() {
        val pages = listOf(makePageData(pageIndex = 1, rawText = "some random text"))
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages, forcedType = DocumentType.INVOICE)
        assertEquals("Forced type should be INVOICE", DocumentType.INVOICE, result.detectedDocType)
    }

    // ---------------------------------------------------------------------------
    // OCR failure / empty pages
    // ---------------------------------------------------------------------------

    @Test
    fun `empty page text does not crash merger`() {
        val pages = listOf(
            makePageData(pageIndex = 1, rawText = "Normal content"),
            makePageData(pageIndex = 2, rawText = ""), // empty page (OCR failure)
            makePageData(pageIndex = 3, rawText = "More content")
        )
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        assertTrue("Should contain page 1 content", result.rawText.contains("Normal content"))
        assertTrue("Should contain page 3 content", result.rawText.contains("More content"))
    }

    @Test
    fun `large page count preserves all page content`() {
        val pages = (1..20).map { i ->
            makePageData(pageIndex = i, rawText = "Page $i content line")
        }
        val result = MultiPageDocumentMerger.combineMultiPageResults(pages)
        for (i in 1..20) {
            assertTrue("Page $i content should be in result", result.rawText.contains("Page $i content line"))
        }
    }
}
