package com.example.snapdata.pipeline

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.example.snapdata.ai.engine.OnDeviceNeuralDocumentAnalyzer
import com.example.snapdata.ai.model.OnDeviceModelManager
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.export.ExportManager
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExportFormat
import com.example.snapdata.model.ProcessingOptions
import com.example.snapdata.processing.ExecutionEngine
import com.example.snapdata.processing.ImagePreprocessor
import com.example.snapdata.processing.MultiPageDocumentMerger
import com.example.snapdata.processing.OcrEngine
import com.example.snapdata.processing.ProcessingPipeline
import com.example.snapdata.sample.IndianDocumentCorpus
import io.mockk.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CompleteDocumentProcessingPipelineTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var testFilesDir: File
    private lateinit var testCacheDir: File
    private lateinit var fakeModelFile: File

    @Before
    fun setUp() = runTest {
        testFilesDir = tempFolder.newFolder("testAppFiles")
        testCacheDir = tempFolder.newFolder("testAppCache")

        val modelsDir = File(testFilesDir, "models").apply { mkdirs() }
        fakeModelFile = File(modelsDir, "snapdata_doc_ai_v1.bin")

        fakeModelFile.outputStream().use { out ->
            out.write("SNAPDATA_AI_v1\n".toByteArray(Charsets.UTF_8))
            out.write("VERSION:1.0.0\n".toByteArray(Charsets.UTF_8))
            val dummyBytes = ByteArray(2048) { 42 }
            out.write(dummyBytes)
        }

        mockContext = mockk(relaxed = true)
        every { mockContext.filesDir } returns testFilesDir
        every { mockContext.cacheDir } returns testCacheDir
        every { mockContext.applicationContext } returns mockContext

        val mockBitmap = mockk<Bitmap>(relaxed = true)
        mockkObject(ImagePreprocessor)
        every { ImagePreprocessor.preprocessImage(any(), any(), any(), any(), any(), any(), any(), any()) } returns ImagePreprocessor.PreprocessingResult(
            originalBitmap = mockBitmap,
            enhancedBitmap = mockBitmap,
            processedImagePath = File(testCacheDir, "test_enhanced.jpg").absolutePath
        )

        OnDeviceModelManager.resetInstanceForTesting()
        val analyzer = OnDeviceNeuralDocumentAnalyzer.getInstance()
        analyzer.initialize(fakeModelFile)
    }

    @After
    fun tearDown() = runTest {
        OnDeviceNeuralDocumentAnalyzer.getInstance().unload()
        OnDeviceModelManager.resetInstanceForTesting()
        unmockkAll()
    }

    @Test
    fun testGoldenPipeline_IndianGstInvoice_EndToEnd() = runTest {
        // 1. INPUT ACQUISITION & OCR
        val testBitmap = mockk<Bitmap>(relaxed = true)
        val options = ProcessingOptions(
            forceOfflineAi = true,
            enableCloudAi = false,
            enhanceContrast = true
        )

        // 2. EXECUTE PIPELINE
        val events = ProcessingPipeline.runPipeline(
            context = mockContext,
            inputBitmap = testBitmap,
            options = options,
            hintOcrText = IndianDocumentCorpus.gstInvoice.rawText
        ).toList()

        val finalEvent = events.last()
        val output = finalEvent.second
        assertNotNull("Pipeline output must not be null", output)

        val ocrResult = output!!.ocrResult
        val execResult = output.executionResult

        // 3. VERIFY GENUINE OFFLINE AI EXECUTION
        assertTrue("Must be offline execution", execResult.isOffline)
        assertEquals(ExecutionEngine.ON_DEVICE_LOCAL_AI, execResult.engineUsed)
        assertEquals(DocumentType.INVOICE, ocrResult.detectedDocType)
        assertEquals(ConfidenceSource.MEASURED, ocrResult.confidenceSource)
        assertTrue("Confidence must be high", ocrResult.overallConfidence >= 0.85f)

        // 4. VERIFY INDIAN FIELD EXTRACTION
        val fields = ocrResult.fields
        val gstinField = fields.find { it.key.contains("GSTIN", ignoreCase = true) }
        assertNotNull("GSTIN field must be extracted", gstinField)
        assertEquals("29ABCDE1234F1Z5", gstinField!!.value)
        assertEquals("Financial", gstinField.category)

        val totalField = fields.find { it.key.contains("Grand Total", ignoreCase = true) || it.key.contains("Total", ignoreCase = true) }
        assertNotNull("Total amount field must be extracted", totalField)
        assertTrue("Total must preserve ₹ or amount digits", totalField!!.value.contains("1,41,600") || totalField.value.contains("₹"))

        // 5. VERIFY TABLE EXTRACTION
        val tables = ocrResult.tables
        assertTrue("Tables must be extracted from invoice", tables.isNotEmpty())
        val itemTable = tables.first()
        assertTrue("Table headers must contain Description/Item/Amount", itemTable.headers.size >= 2)
        assertTrue("Table rows must be extracted", itemTable.rows.isNotEmpty())

        // 6. SIMULATE USER REVIEW & EDIT
        val editedFields = fields.map {
            if (it.key.contains("GSTIN")) it.copy(value = "29ABCDE1234F1Z9", isUserEdited = true) else it
        }.toMutableList()
        val editedTitle = "Reviewed GST Tax Invoice"

        // 7. PERSIST TO SQLITE ENTITY
        val entity = DocumentEntity.from(
            title = editedTitle,
            docType = ocrResult.detectedDocType,
            originalImagePath = output.processedImagePath,
            summary = ocrResult.summary,
            rawOcrText = ocrResult.rawText,
            fields = editedFields,
            tables = tables,
            overallConfidence = ocrResult.overallConfidence,
            pageCount = 1
        )

        // 8. VERIFY REOPENING & ENTITY DESERIALIZATION
        assertEquals(editedTitle, entity.title)
        assertEquals(DocumentType.INVOICE, entity.getTypedDocType())
        val reopenedFields = entity.getFieldsList()
        val reopenedGstin = reopenedFields.find { it.key.contains("GSTIN") }
        assertNotNull(reopenedGstin)
        assertEquals("29ABCDE1234F1Z9", reopenedGstin!!.value)
        assertTrue("User edited flag must persist", reopenedGstin.isUserEdited)

        val reopenedTables = entity.getTablesList()
        assertEquals(tables.size, reopenedTables.size)

        // 9. VERIFY MULTI-FORMAT EXPORTS
        // Export to JSON
        val jsonExport = ExportManager.exportDocument(mockContext, entity, ExportFormat.JSON)
        assertTrue(jsonExport.success)
        assertTrue(jsonExport.file.exists())
        val jsonContent = jsonExport.file.readText(Charsets.UTF_8)
        assertTrue(jsonContent.contains("29ABCDE1234F1Z9"))
        assertTrue(jsonContent.contains("INVOICE"))

        // Export to CSV
        val csvExport = ExportManager.exportDocument(mockContext, entity, ExportFormat.CSV)
        assertTrue(csvExport.success)
        assertTrue(csvExport.file.exists())
        val csvContent = csvExport.file.readText(Charsets.UTF_8)
        assertTrue(csvContent.contains("29ABCDE1234F1Z9"))
        assertTrue("CSV should have UTF-8 BOM", csvContent.startsWith("\uFEFF"))

        // Export to XLSX
        val xlsxExport = ExportManager.exportDocument(mockContext, entity, ExportFormat.EXCEL)
        assertTrue(xlsxExport.success)
        assertTrue(xlsxExport.file.exists())
        assertTrue("XLSX file must be non-empty", xlsxExport.file.length() > 500)
    }

    @Test
    fun testIndianDocumentCorpus_ClassificationAndExtraction() = runTest {
        val analyzer = OnDeviceNeuralDocumentAnalyzer.getInstance()

        // 1. Retail Receipt with ₹
        val receiptOcr = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.retailReceipt.rawText)
        val receiptRes = analyzer.analyze(receiptOcr).getOrThrow()
        assertEquals(DocumentType.RECEIPT, receiptRes.detectedDocType)
        assertTrue(receiptRes.fields.any { it.key.contains("GSTIN") || it.key.contains("Total") || it.key.contains("UPI") })

        // 2. Bank Statement with IFSC
        val bankOcr = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.bankStatement.rawText)
        val bankRes = analyzer.analyze(bankOcr).getOrThrow()
        assertEquals(DocumentType.BANK_STATEMENT, bankRes.detectedDocType)
        val ifscField = bankRes.fields.find { it.key.contains("IFSC", ignoreCase = true) }
        assertNotNull("IFSC Code must be extracted", ifscField)
        assertEquals("SBIN0004052", ifscField!!.value)

        // 3. Marksheet with SGPA / CGPA
        val marksheetOcr = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.marksheet.rawText)
        val marksheetRes = analyzer.analyze(marksheetOcr).getOrThrow()
        assertEquals(DocumentType.MARK_SHEET, marksheetRes.detectedDocType)
        val sgpaField = marksheetRes.fields.find { it.key.contains("SGPA", ignoreCase = true) }
        assertNotNull("SGPA must be extracted", sgpaField)
        assertEquals("9.45", sgpaField!!.value)
    }

    @Test
    fun testMultiPageDocumentAggregationAndStitching() = runTest {
        val page1Text = """
            ACME CORPORATION INDIA
            Invoice No: INV-2026-9901
            Date: 15/08/2026
            GSTIN: 27AAACW1234F1Z5
            
            Item Description | Qty | Unit Price | Amount
            Cloud Computing Services | 10 | ₹1,500.00 | ₹15,000.00
            Database Storage Cluster | 5 | ₹2,000.00 | ₹10,000.00
        """.trimIndent()

        val page2Text = """
            ACME CORPORATION INDIA
            Invoice No: INV-2026-9901
            Date: 15/08/2026
            
            Item Description | Qty | Unit Price | Amount
            Dedicated Load Balancers | 2 | ₹3,500.00 | ₹7,000.00
            SSL Certificates & Domain | 1 | ₹2,000.00 | ₹2,000.00
            
            Grand Total: ₹34,000.00
        """.trimIndent()

        val page1Ocr = OcrEngine.parseTextToStructuredData(page1Text)
        val page2Ocr = OcrEngine.parseTextToStructuredData(page2Text)

        val pages = listOf(
            MultiPageDocumentMerger.PageOcrData(1, page1Text, page1Ocr),
            MultiPageDocumentMerger.PageOcrData(2, page2Text, page2Ocr)
        )

        val combinedResult = MultiPageDocumentMerger.combineMultiPageResults(pages)

        assertEquals(DocumentType.INVOICE, combinedResult.detectedDocType)
        assertTrue(combinedResult.rawText.contains("PAGE 1 OF 2"))
        assertTrue(combinedResult.rawText.contains("PAGE 2 OF 2"))

        // Verify field deduplication
        val gstinField = combinedResult.fields.find { it.key.contains("GSTIN") }
        assertNotNull(gstinField)

        // Verify table row stitching
        assertTrue(combinedResult.tables.isNotEmpty())
        val stitchedTable = combinedResult.tables.first()
        // Page 1 has 2 rows + Page 2 has 2 rows = 4 rows stitched!
        assertEquals("Table rows across 2 pages must be stitched together", 4, stitchedTable.rows.size)
    }

    @Test
    fun testOfflineModelMissingGracefulFallback() = runTest {
        // Unload and delete local model
        fakeModelFile.delete()
        OnDeviceNeuralDocumentAnalyzer.getInstance().unload()

        val testBitmap = mockk<Bitmap>(relaxed = true)
        val options = ProcessingOptions(
            forceOfflineAi = true,
            enableCloudAi = false
        )

        val events = ProcessingPipeline.runPipeline(
            context = mockContext,
            inputBitmap = testBitmap,
            options = options,
            hintOcrText = IndianDocumentCorpus.gstInvoice.rawText
        ).toList()

        val output = events.last().second
        assertNotNull(output)
        assertEquals(ExecutionEngine.LOCAL_OCR_ONLY, output!!.executionResult.engineUsed)
        assertTrue("Must report fallback cleanly without crashing", output.executionResult.diagnosticMessage.contains("ML Kit OCR") || output.executionResult.diagnosticMessage.contains("Settings"))
    }
}
