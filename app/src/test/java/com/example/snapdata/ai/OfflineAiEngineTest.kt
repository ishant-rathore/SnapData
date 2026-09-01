package com.example.snapdata.ai

import com.example.snapdata.ai.engine.OnDeviceNeuralDocumentAnalyzer
import com.example.snapdata.ai.model.ModelMetadata
import com.example.snapdata.ai.model.OnDeviceModelManager
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.processing.OcrEngine
import com.example.snapdata.sample.IndianDocumentCorpus
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OfflineAiEngineTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var modelFile: File
    private val analyzer = OnDeviceNeuralDocumentAnalyzer.getInstance()

    @Before
    fun setUp() = runTest {
        val modelsDir = tempFolder.newFolder("models")
        modelFile = File(modelsDir, "snapdata_doc_ai_v1.bin")

        // Create valid test weights file
        modelFile.outputStream().use { out ->
            out.write("SNAPDATA_AI_v1\n".toByteArray(Charsets.UTF_8))
            out.write("VERSION:1.0.0\n".toByteArray(Charsets.UTF_8))
            val dummyBytes = ByteArray(2048) { 42 }
            out.write(dummyBytes)
        }

        val initRes = analyzer.initialize(modelFile)
        assertTrue(initRes.isSuccess)
        assertTrue(analyzer.isReady)
    }

    @After
    fun tearDown() = runTest {
        analyzer.unload()
    }

    @Test
    fun testGstInvoiceClassificationAndExtraction() = runTest {
        val ocrInput = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.gstInvoice.rawText)
        val result = analyzer.analyze(ocrInput)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()

        assertEquals(DocumentType.INVOICE, output.detectedDocType)
        assertTrue(output.fields.isNotEmpty())

        // Validate Indian GSTIN extraction
        val gstinField = output.fields.find { it.key.contains("GSTIN", ignoreCase = true) }
        assertNotNull(gstinField)
        assertTrue(gstinField!!.value.contains("29ABCDE1234F1Z5"))
        assertEquals(ConfidenceSource.MEASURED, gstinField?.confidenceSource)

        // Validate Grand Total
        val totalField = output.fields.find { it.key.contains("Total", ignoreCase = true) }
        assertNotNull(totalField)
        assertTrue(totalField!!.value.contains("1,41,600") || output.rawModelEvidence.isNotBlank())

        // Validate Table Matrix
        assertTrue(output.tables.isNotEmpty())
        val lineItemsTable = output.tables.first()
        assertTrue(lineItemsTable.headers.size >= 3)
        assertTrue(lineItemsTable.rows.size >= 2)

        assertTrue(output.overallConfidence >= 0.80f)
        assertTrue(output.summary.isNotBlank())
    }

    @Test
    fun testRetailReceiptExtractionWithGstAndUpi() = runTest {
        val ocrInput = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.retailReceipt.rawText)
        val result = analyzer.analyze(ocrInput)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()

        assertEquals(DocumentType.RECEIPT, output.detectedDocType)
        assertTrue(output.fields.any { it.key.contains("Total", ignoreCase = true) })
        assertTrue(output.tables.isNotEmpty())
    }

    @Test
    fun testBankStatementExtractionWithLedger() = runTest {
        val ocrInput = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.bankStatement.rawText)
        val result = analyzer.analyze(ocrInput)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()

        assertEquals(DocumentType.BANK_STATEMENT, output.detectedDocType)
        val ifscField = output.fields.find { it.key.contains("IFSC", ignoreCase = true) }
        assertNotNull(ifscField)
        assertTrue(ifscField!!.value.contains("SBIN0004052"))

        assertTrue(output.tables.isNotEmpty())
        val ledger = output.tables.first()
        assertTrue(ledger.headers.any { it.contains("Debit", ignoreCase = true) || it.contains("Balance", ignoreCase = true) || it.contains("Particulars", ignoreCase = true) })
    }

    @Test
    fun testMarksheetExtractionWithSgpaAndGrades() = runTest {
        val ocrInput = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.marksheet.rawText)
        val result = analyzer.analyze(ocrInput)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()

        assertEquals(DocumentType.MARK_SHEET, output.detectedDocType)
        val sgpaField = output.fields.find { it.key.contains("SGPA", ignoreCase = true) }
        assertNotNull(sgpaField)
        assertTrue(sgpaField!!.value.contains("9.45"))

        val rollField = output.fields.find { it.key.contains("Roll", ignoreCase = true) }
        assertNotNull(rollField)
        assertTrue(rollField!!.value.contains("1VT22CS042"))
    }


    @Test
    fun testBilingualHindiEnglishExtraction() = runTest {
        val ocrInput = OcrEngine.parseTextToStructuredData(IndianDocumentCorpus.bilingualInvoice.rawText)
        val result = analyzer.analyze(ocrInput)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()

        assertEquals(DocumentType.INVOICE, output.detectedDocType)
        assertTrue(output.fields.any { it.key.contains("GSTIN", ignoreCase = true) })
        assertTrue(output.fields.any { it.key.contains("Grand Total", ignoreCase = true) })
    }

    @Test
    fun testEmptyOcrYieldsZeroConfidenceSafely() = runTest {
        val emptyOcr = OcrEngine.parseTextToStructuredData("")
        val result = analyzer.analyze(emptyOcr)

        assertTrue(result.isSuccess)
        val output = result.getOrThrow()
        assertEquals(0.0f, output.overallConfidence)
        assertTrue(output.fields.isEmpty())
    }
}
