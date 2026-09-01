package com.example.snapdata

import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.export.ExportManager
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExportFormat
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.zip.ZipInputStream

class ExportManagerTest {

    private val sampleEntity = DocumentEntity.from(
        id = 1L,
        title = "Test Invoice",
        docType = DocumentType.INVOICE,
        originalImagePath = null,
        summary = "Test summary for export",
        rawOcrText = "Raw OCR Text Line 1\nRaw OCR Text Line 2",
        fields = listOf(
            ExtractedField(key = "Invoice #", value = "INV-001", category = "Identifier"),
            ExtractedField(key = "Total", value = "₹5,000.00", category = "Financial")
        ),
        tables = listOf(
            ExtractedTable(
                name = "Items",
                headers = mutableListOf("Description", "Price"),
                rows = mutableListOf(
                    mutableListOf("Service A", "₹2,000"),
                    mutableListOf("Service B", "₹3,000")
                )
            )
        ),
        overallConfidence = 0.96f
    )

    // =========================================================================
    // 1. BASELINE GENERATION TESTS
    // =========================================================================

    @Test
    fun testGenerateCsv() {
        val csv = ExportManager.generateCsvString(sampleEntity)
        assertTrue(csv.contains("Invoice #"))
        assertTrue(csv.contains("INV-001"))
        assertTrue(csv.contains("Service A"))
        assertTrue(csv.contains("Service B"))
        assertTrue(csv.contains("Total"))
        assertTrue(csv.contains("Test summary for export"))
    }

    @Test
    fun testGenerateJson() {
        val jsonStr = ExportManager.generateJsonString(sampleEntity)
        assertTrue(jsonStr.contains("\"title\": \"Test Invoice\""))
        assertTrue(jsonStr.contains("\"documentType\": \"INVOICE\""))
        assertTrue(jsonStr.contains("\"key\": \"Invoice #\""))
        assertTrue(jsonStr.contains("\"snapDataVersion\": \"2.0\""))

        // Verify valid parseable JSON
        val parsed = Json.parseToJsonElement(jsonStr).jsonObject
        assertEquals("Test Invoice", parsed["title"]?.jsonPrimitive?.content)
        assertEquals("INVOICE", parsed["documentType"]?.jsonPrimitive?.content)
        assertEquals(2, parsed["fields"]?.jsonArray?.size)
        assertEquals(1, parsed["tables"]?.jsonArray?.size)
    }

    @Test
    fun testGenerateMarkdown() {
        val md = ExportManager.generateMarkdownString(sampleEntity)
        assertTrue(md.contains("# Test Invoice"))
        assertTrue(md.contains("| Description | Price |"))
        assertTrue(md.contains("**Invoice #**: INV-001"))
        assertTrue(md.contains("## Summary"))
    }

    // =========================================================================
    // 2. EMPTY DOCUMENT HANDLING
    // =========================================================================

    @Test
    fun testEmptyDocumentExport() {
        val emptyDoc = DocumentEntity.from(
            id = 2L,
            title = "",
            docType = DocumentType.GENERAL_DOCUMENT,
            originalImagePath = null,
            summary = "",
            rawOcrText = "",
            fields = emptyList(),
            tables = emptyList(),
            overallConfidence = 0.0f
        )

        // CSV
        val csv = ExportManager.generateCsvString(emptyDoc)
        assertNotNull(csv)
        assertTrue(csv.contains("Document Title"))

        // JSON
        val json = ExportManager.generateJsonString(emptyDoc)
        val parsed = Json.parseToJsonElement(json).jsonObject
        assertEquals(0, parsed["fields"]?.jsonArray?.size)
        assertEquals(0, parsed["tables"]?.jsonArray?.size)

        // Markdown
        val md = ExportManager.generateMarkdownString(emptyDoc)
        assertTrue(md.contains("# Untitled Document"))

        // Excel XLSX file generation
        val tempExcel = File.createTempFile("snapdata_empty_", ".xlsx")
        try {
            ExportManager.generateExcelXlsx(emptyDoc, tempExcel)
            assertTrue(tempExcel.exists())
            assertTrue(tempExcel.length() > 0)
            verifyValidZipArchive(tempExcel)
        } finally {
            tempExcel.delete()
        }
    }

    // =========================================================================
    // 3. UNICODE & HINDI TEXT SUPPORT
    // =========================================================================

    @Test
    fun testUnicodeAndHindiText() {
        val hindiDoc = DocumentEntity.from(
            id = 3L,
            title = "चालान रसीद (Invoice Receipt)",
            docType = DocumentType.RECEIPT,
            originalImagePath = null,
            summary = "यह एक आधिकारिक परीक्षण सारांश है। Total: ₹1,50,000.50",
            rawOcrText = "ग्राहक का नाम: राहुल शर्मा\nदिनांक: 15 अगस्त 2026",
            fields = listOf(
                ExtractedField(key = "ग्राहक का नाम", value = "राहुल शर्मा", category = "Identity"),
                ExtractedField(key = "कुल राशि", value = "₹1,50,000", category = "Financial"),
                ExtractedField(key = "Special Note", value = "Électricité & Français • 日本語 • 🚀", category = "Notes")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "वस्तु सूची (Items)",
                    headers = mutableListOf("विवरण (Description)", "मूल्य (Price)"),
                    rows = mutableListOf(
                        mutableListOf("सामग्री क (Item A)", "₹50,000"),
                        mutableListOf("सामग्री ख (Item B)", "₹1,00,000")
                    )
                )
            ),
            overallConfidence = 0.98f
        )

        // CSV
        val csv = ExportManager.generateCsvString(hindiDoc)
        assertTrue(csv.contains("चालान रसीद"))
        assertTrue(csv.contains("राहुल शर्मा"))
        assertTrue(csv.contains("₹1,50,000"))
        assertTrue(csv.contains("Électricité & Français • 日本語 • 🚀"))

        // JSON
        val json = ExportManager.generateJsonString(hindiDoc)
        assertTrue(json.contains("राहुल शर्मा"))
        val parsed = Json.parseToJsonElement(json).jsonObject
        assertEquals("चालान रसीद (Invoice Receipt)", parsed["title"]?.jsonPrimitive?.content)

        // Excel XLSX
        val tempExcel = File.createTempFile("snapdata_hindi_", ".xlsx")
        try {
            ExportManager.generateExcelXlsx(hindiDoc, tempExcel)
            assertTrue(tempExcel.exists())
            assertTrue(tempExcel.length() > 0)
            verifyValidZipArchive(tempExcel)
        } finally {
            tempExcel.delete()
        }
    }

    // =========================================================================
    // 4. SPECIAL CHARACTERS & FORMULA INJECTION PREVENTION
    // =========================================================================

    @Test
    fun testSpecialCharactersAndCsvInjection() {
        val dangerousDoc = DocumentEntity.from(
            id = 4L,
            title = "Special <Doc> & \"Quotes\" \n Newline",
            docType = DocumentType.FORM,
            originalImagePath = null,
            summary = "Summary with <XML tags>, \"quotes\", \t tabs, and \n newlines.",
            rawOcrText = "",
            fields = listOf(
                // Formula injection candidates
                ExtractedField(key = "=cmd|' /C calc'!A0", value = "+123456", category = "-Financial"),
                ExtractedField(key = "@SUM(A1:A10)", value = "\tTabbed Value", category = "Tax"),
                ExtractedField(key = "HTML & Special", value = "<script>alert('xss')</script>", category = "Sec")
            ),
            tables = listOf(
                ExtractedTable(
                    name = "Table & <Tags>",
                    headers = mutableListOf("Col 1, with comma", "Col \"2\""),
                    rows = mutableListOf(
                        mutableListOf("=1+1", "Cell\nwith\nmultiline")
                    )
                )
            ),
            overallConfidence = 0.90f
        )

        val csv = ExportManager.generateCsvString(dangerousDoc)
        // Verify formula injection prefixes (')
        assertTrue(csv.contains("'''=cmd|' /C calc'!A0\"") || csv.contains("''=cmd") || csv.contains("'=cmd"))
        assertTrue(csv.contains("'+123456"))
        assertTrue(csv.contains("'-Financial"))
        assertTrue(csv.contains("'@SUM(A1:A10)"))
        assertTrue(csv.contains("''=1+1") || csv.contains("'=1+1"))

        // Verify XML escaping in Excel
        val tempExcel = File.createTempFile("snapdata_specials_", ".xlsx")
        try {
            ExportManager.generateExcelXlsx(dangerousDoc, tempExcel)
            assertTrue(tempExcel.exists())
            val entries = getZipEntryNames(tempExcel)
            assertTrue(entries.contains("[Content_Types].xml"))
            assertTrue(entries.contains("xl/workbook.xml"))
            assertTrue(entries.contains("xl/worksheets/sheet1.xml"))
        } finally {
            tempExcel.delete()
        }
    }

    // =========================================================================
    // 5. LARGE TABLE & MULTIPLE TABLES (Base-26 Column Coordinate Verification)
    // =========================================================================

    @Test
    fun testBase26ColumnCalculation() {
        assertEquals("A", ExportManager.getColumnLetter(0))
        assertEquals("B", ExportManager.getColumnLetter(1))
        assertEquals("Z", ExportManager.getColumnLetter(25))
        assertEquals("AA", ExportManager.getColumnLetter(26))
        assertEquals("AB", ExportManager.getColumnLetter(27))
        assertEquals("AZ", ExportManager.getColumnLetter(51))
        assertEquals("BA", ExportManager.getColumnLetter(52))
        assertEquals("ZZ", ExportManager.getColumnLetter(701))
        assertEquals("AAA", ExportManager.getColumnLetter(702))
    }

    @Test
    fun testLargeDatasetAndMultipleTables() {
        // Create a table with 35 columns (> 26 columns) and 150 rows
        val numCols = 35
        val numRows = 150
        val headers = (0 until numCols).map { "Col_${ExportManager.getColumnLetter(it)}" }.toMutableList()
        val rows = (0 until numRows).map { r ->
            (0 until numCols).map { c -> "Cell_${r}_${c}" }.toMutableList()
        }.toMutableList()

        val largeTable = ExtractedTable(
            name = "Large Wide Matrix",
            headers = headers,
            rows = rows
        )

        val secondTable = ExtractedTable(
            name = "Summary Taxes",
            headers = mutableListOf("Tax Type", "Rate", "Amount"),
            rows = mutableListOf(
                mutableListOf("GST", "18%", "₹900.00"),
                mutableListOf("Cess", "1%", "₹50.00")
            )
        )

        val largeDoc = DocumentEntity.from(
            id = 5L,
            title = "Large Multi-Table Dataset",
            docType = DocumentType.TABLE,
            originalImagePath = null,
            summary = "Large dataset test with 2 multi-dimensional tables.",
            rawOcrText = "",
            fields = (1..30).map { ExtractedField(key = "MetaKey_$it", value = "MetaVal_$it", category = "Meta") },
            tables = listOf(largeTable, secondTable),
            overallConfidence = 0.99f
        )

        // CSV test
        val csv = ExportManager.generateCsvString(largeDoc)
        assertTrue(csv.contains("--- TABLE 1: Large Wide Matrix ---"))
        assertTrue(csv.contains("--- TABLE 2: Summary Taxes ---"))
        assertTrue(csv.contains("Col_AA"))
        assertTrue(csv.contains("Cell_149_34"))

        // Excel multi-sheet test
        val tempExcel = File.createTempFile("snapdata_large_", ".xlsx")
        try {
            ExportManager.generateExcelXlsx(largeDoc, tempExcel)
            assertTrue(tempExcel.exists())
            assertTrue(tempExcel.length() > 5000)

            val zipEntries = getZipEntryNames(tempExcel)
            assertTrue(zipEntries.contains("[Content_Types].xml"))
            assertTrue(zipEntries.contains("xl/workbook.xml"))
            assertTrue(zipEntries.contains("xl/worksheets/sheet1.xml"))
            assertTrue(zipEntries.contains("xl/worksheets/sheet2.xml"))
            assertTrue(zipEntries.contains("xl/worksheets/sheet3.xml"))
        } finally {
            tempExcel.delete()
        }
    }

    // =========================================================================
    // 6. SAFE FILENAME SANITIZATION
    // =========================================================================

    @Test
    fun testFilenameSanitization() {
        assertEquals("Invoice_123", ExportManager.sanitizeFilename("Invoice/123"))
        assertEquals("Invoice_Test", ExportManager.sanitizeFilename("Invoice:Test?*\"<>|"))
        assertEquals("My_Document_2026", ExportManager.sanitizeFilename("  My   Document   2026  "))
        assertEquals("चालान_रसीद", ExportManager.sanitizeFilename("चालान रसीद"))
        assertEquals("Document", ExportManager.sanitizeFilename("///???:::"))
        assertEquals("Document", ExportManager.sanitizeFilename(""))
        assertEquals("Document", ExportManager.sanitizeFilename("   "))

        val excelFilename = ExportManager.generateExportFilename("Test / Title : 123", ExportFormat.EXCEL)
        assertTrue(excelFilename.startsWith("SnapData_Test_Title_123_"))
        assertTrue(excelFilename.endsWith(".xlsx"))

        val csvFilename = ExportManager.generateExportFilename("", ExportFormat.CSV)
        assertTrue(csvFilename.startsWith("SnapData_Document_"))
        assertTrue(csvFilename.endsWith(".csv"))
    }

    // =========================================================================
    // 7. XML ESCAPING & CONTROL CHARACTERS
    // =========================================================================

    @Test
    fun testXmlSanitization() {
        // Control characters < 0x20 except tab (0x09), LF (0x0A), CR (0x0D) should be filtered
        val rawWithControlChars = "Hello\u0000World\u0007Test\tTab\nNewline\rReturn"
        val sanitized = ExportManager.escapeXml(rawWithControlChars)
        assertFalse(sanitized.contains("\u0000"))
        assertFalse(sanitized.contains("\u0007"))
        assertTrue(sanitized.contains("HelloWorldTest\tTab\nNewline\rReturn"))

        val xmlEntities = "<tag attr='value' & \"quoted\">"
        val escapedEntities = ExportManager.escapeXml(xmlEntities)
        assertEquals("&lt;tag attr=&apos;value&apos; &amp; &quot;quoted&quot;&gt;", escapedEntities)
    }

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    private fun verifyValidZipArchive(file: File) {
        ZipInputStream(file.inputStream()).use { zis ->
            var entry = zis.nextEntry
            var entryCount = 0
            while (entry != null) {
                entryCount++
                assertNotNull(entry.name)
                assertTrue(entry.name.isNotEmpty())
                zis.closeEntry()
                entry = zis.nextEntry
            }
            assertTrue("Zip archive should have at least 5 standard OpenXML entries", entryCount >= 5)
        }
    }

    private fun getZipEntryNames(file: File): List<String> {
        val names = mutableListOf<String>()
        ZipInputStream(file.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                names.add(entry.name)
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        return names
    }
}
