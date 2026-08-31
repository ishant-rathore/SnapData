package com.example.snapdata.data

import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DocumentEntity covering:
 * - JSON serialization/deserialization of fields and tables
 * - Document type resolution with invalid enum values
 * - File existence check
 * - Factory `from()` builder
 */
class DocumentEntityTest {

    // ---------------------------------------------------------------------------
    // Factory builder
    // ---------------------------------------------------------------------------

    @Test
    fun `from() creates entity with correct document type name`() {
        val entity = DocumentEntity.from(
            title = "Test Invoice",
            docType = DocumentType.INVOICE,
            originalImagePath = null,
            summary = "An invoice",
            rawOcrText = "Invoice text",
            fields = emptyList(),
            tables = emptyList(),
            overallConfidence = 0.95f
        )
        assertEquals("INVOICE", entity.docType)
        assertEquals("Test Invoice", entity.title)
        assertEquals("An invoice", entity.summary)
        assertEquals(0.95f, entity.overallConfidence, 0.001f)
    }

    @Test
    fun `from() sets pageCount correctly`() {
        val entity = DocumentEntity.from(
            title = "Multi-page",
            docType = DocumentType.BANK_STATEMENT,
            originalImagePath = null,
            summary = "",
            rawOcrText = "",
            fields = emptyList(),
            tables = emptyList(),
            overallConfidence = 0.88f,
            pageCount = 5
        )
        assertEquals(5, entity.pageCount)
    }

    @Test
    fun `from() sets createdAt from parameter when provided`() {
        val customTime = 1234567890L
        val entity = DocumentEntity.from(
            title = "Test",
            docType = DocumentType.GENERAL_DOCUMENT,
            originalImagePath = null,
            summary = "",
            rawOcrText = "",
            fields = emptyList(),
            tables = emptyList(),
            overallConfidence = 0.90f,
            createdAt = customTime
        )
        assertEquals(customTime, entity.createdAt)
    }

    // ---------------------------------------------------------------------------
    // Document type resolution
    // ---------------------------------------------------------------------------

    @Test
    fun `getTypedDocType returns correct type for valid enum name`() {
        val entity = DocumentEntity(
            title = "Invoice",
            docType = "INVOICE"
        )
        assertEquals(DocumentType.INVOICE, entity.getTypedDocType())
    }

    @Test
    fun `getTypedDocType returns GENERAL_DOCUMENT for invalid enum name`() {
        val entity = DocumentEntity(
            title = "Unknown",
            docType = "COMPLETELY_INVALID_TYPE_XYZ"
        )
        assertEquals(DocumentType.GENERAL_DOCUMENT, entity.getTypedDocType())
    }

    @Test
    fun `getTypedDocType returns GENERAL_DOCUMENT for empty docType string`() {
        val entity = DocumentEntity(title = "Test", docType = "")
        assertEquals(DocumentType.GENERAL_DOCUMENT, entity.getTypedDocType())
    }

    // ---------------------------------------------------------------------------
    // Fields JSON serialization
    // ---------------------------------------------------------------------------

    @Test
    fun `getFieldsList returns empty list when fieldsJson is empty array`() {
        val entity = DocumentEntity(title = "Test", fieldsJson = "[]")
        assertEquals(emptyList<ExtractedField>(), entity.getFieldsList())
    }

    @Test
    fun `getFieldsList returns empty list when fieldsJson is invalid JSON`() {
        val entity = DocumentEntity(title = "Test", fieldsJson = "INVALID_JSON_{{{")
        // Should not throw — must return empty list gracefully
        assertEquals(emptyList<ExtractedField>(), entity.getFieldsList())
    }

    @Test
    fun `getFieldsList returns empty list when fieldsJson is blank`() {
        val entity = DocumentEntity(title = "Test", fieldsJson = "")
        assertEquals(emptyList<ExtractedField>(), entity.getFieldsList())
    }

    @Test
    fun `fields round-trip through DocumentEntity factory`() {
        val fields = listOf(
            ExtractedField(key = "Invoice Number", value = "INV-001", confidence = 0.98f),
            ExtractedField(key = "Total", value = "1500.00", confidence = 0.95f)
        )
        val entity = DocumentEntity.from(
            title = "Test",
            docType = DocumentType.INVOICE,
            originalImagePath = null,
            summary = "",
            rawOcrText = "",
            fields = fields,
            tables = emptyList(),
            overallConfidence = 0.95f
        )
        val restored = entity.getFieldsList()
        assertEquals("Should restore 2 fields", 2, restored.size)
        assertEquals("Invoice Number", restored[0].key)
        assertEquals("INV-001", restored[0].value)
        assertEquals("Total", restored[1].key)
    }

    // ---------------------------------------------------------------------------
    // Tables JSON serialization
    // ---------------------------------------------------------------------------

    @Test
    fun `getTablesList returns empty list when tablesJson is empty array`() {
        val entity = DocumentEntity(title = "Test", tablesJson = "[]")
        assertEquals(emptyList<ExtractedTable>(), entity.getTablesList())
    }

    @Test
    fun `getTablesList returns empty list when tablesJson is invalid JSON`() {
        val entity = DocumentEntity(title = "Test", tablesJson = "{{BROKEN")
        assertEquals(emptyList<ExtractedTable>(), entity.getTablesList())
    }

    @Test
    fun `tables round-trip through DocumentEntity factory`() {
        val tables = listOf(
            ExtractedTable(
                name = "Line Items",
                headers = mutableListOf("Item", "Qty", "Amount"),
                rows = mutableListOf(
                    mutableListOf("Widget A", "5", "50.00"),
                    mutableListOf("Widget B", "3", "30.00")
                ),
                confidence = 0.90f
            )
        )
        val entity = DocumentEntity.from(
            title = "Test",
            docType = DocumentType.INVOICE,
            originalImagePath = null,
            summary = "",
            rawOcrText = "",
            fields = emptyList(),
            tables = tables,
            overallConfidence = 0.90f
        )
        val restored = entity.getTablesList()
        assertEquals("Should restore 1 table", 1, restored.size)
        assertEquals("Line Items", restored[0].name)
        assertEquals(3, restored[0].headers.size)
        assertEquals(2, restored[0].rows.size)
    }

    // ---------------------------------------------------------------------------
    // File existence check
    // ---------------------------------------------------------------------------

    @Test
    fun `hasValidImageFile returns false when imagePath is null`() {
        val entity = DocumentEntity(title = "Test", originalImagePath = null)
        assertFalse(entity.hasValidImageFile())
    }

    @Test
    fun `hasValidImageFile returns false when imagePath is blank`() {
        val entity = DocumentEntity(title = "Test", originalImagePath = "  ")
        assertFalse(entity.hasValidImageFile())
    }

    @Test
    fun `hasValidImageFile returns false when file does not exist`() {
        val entity = DocumentEntity(
            title = "Test",
            originalImagePath = "/nonexistent/path/to/image_xyz_12345.jpg"
        )
        assertFalse(entity.hasValidImageFile())
    }

    @Test
    fun `hasValidImageFile returns true for existing temp file`() {
        val tempFile = createTempFile("snapdata_test_", ".jpg")
        try {
            val entity = DocumentEntity(title = "Test", originalImagePath = tempFile.absolutePath)
            assertTrue(entity.hasValidImageFile())
        } finally {
            tempFile.delete()
        }
    }

    // ---------------------------------------------------------------------------
    // Default values
    // ---------------------------------------------------------------------------

    @Test
    fun `default isFavorite is false`() {
        val entity = DocumentEntity(title = "Test")
        assertFalse(entity.isFavorite)
    }

    @Test
    fun `default pageCount is 1`() {
        val entity = DocumentEntity(title = "Test")
        assertEquals(1, entity.pageCount)
    }

    @Test
    fun `default overallConfidence is 0_95`() {
        val entity = DocumentEntity(title = "Test")
        assertEquals(0.95f, entity.overallConfidence, 0.001f)
    }
}
