package com.example.snapdata.processing

import android.content.Context
import android.graphics.Bitmap
import com.example.snapdata.ai.engine.OnDeviceNeuralDocumentAnalyzer
import com.example.snapdata.ai.model.OnDeviceModelManager
import com.example.snapdata.model.ConfidenceSource
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ProcessingOptions
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

class OfflinePipelineEnforcementTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var testFilesDir: File
    private lateinit var fakeModelFile: File

    @Before
    fun setUp() = runTest {
        testFilesDir = tempFolder.newFolder("appFiles")
        val modelsDir = File(testFilesDir, "models").apply { mkdirs() }
        fakeModelFile = File(modelsDir, "snapdata_doc_ai_v1.bin")

        fakeModelFile.outputStream().use { out ->
            out.write("SNAPDATA_AI_v1\n".toByteArray(Charsets.UTF_8))
            out.write("VERSION:1.0.0\n".toByteArray(Charsets.UTF_8))
            val dummyBytes = ByteArray(2048) { 7 }
            out.write(dummyBytes)
        }

        mockContext = mockk(relaxed = true)
        every { mockContext.filesDir } returns testFilesDir
        every { mockContext.applicationContext } returns mockContext

        val mockBitmap = mockk<Bitmap>(relaxed = true)
        mockkObject(ImagePreprocessor)
        every { ImagePreprocessor.preprocessImage(any(), any(), any(), any(), any(), any(), any(), any()) } returns ImagePreprocessor.PreprocessingResult(
            originalBitmap = mockBitmap,
            enhancedBitmap = mockBitmap,
            processedImagePath = "mock_path"
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
    fun testOfflinePipelineWithReadyModelExecutesOnDeviceLocalAi() = runTest {
        val testBitmap = mockk<Bitmap>(relaxed = true)
        val options = ProcessingOptions(
            forceOfflineAi = true,
            enableCloudAi = false
        )

        val flowEvents = ProcessingPipeline.runPipeline(
            context = mockContext,
            inputBitmap = testBitmap,
            options = options,
            hintOcrText = IndianDocumentCorpus.gstInvoice.rawText
        ).toList()

        val lastEvent = flowEvents.last()
        val finalOutput = lastEvent.second

        assertNotNull(finalOutput)
        val execResult = finalOutput!!.executionResult

        assertTrue(execResult.isOffline)
        assertEquals(ExecutionEngine.ON_DEVICE_LOCAL_AI, execResult.engineUsed)
        assertEquals(ConfidenceSource.MEASURED, finalOutput.ocrResult.confidenceSource)
        assertEquals(DocumentType.INVOICE, finalOutput.ocrResult.detectedDocType)
        assertTrue(finalOutput.ocrResult.fields.isNotEmpty())
    }

    @Test
    fun testOfflinePipelineWithMissingModelReportsLocalOcrOnlyWithoutCloud() = runTest {
        // Remove fake model file
        fakeModelFile.delete()
        OnDeviceNeuralDocumentAnalyzer.getInstance().unload()

        val testBitmap = mockk<Bitmap>(relaxed = true)
        val options = ProcessingOptions(
            forceOfflineAi = true,
            enableCloudAi = false
        )

        val flowEvents = ProcessingPipeline.runPipeline(
            context = mockContext,
            inputBitmap = testBitmap,
            options = options,
            hintOcrText = IndianDocumentCorpus.retailReceipt.rawText
        ).toList()


        val lastEvent = flowEvents.last()
        val finalOutput = lastEvent.second

        assertNotNull(finalOutput)
        val execResult = finalOutput!!.executionResult

        assertTrue(execResult.isOffline)
        assertEquals(ExecutionEngine.LOCAL_OCR_ONLY, execResult.engineUsed)
        assertTrue(execResult.diagnosticMessage.contains("Offline AI Model not installed") || execResult.diagnosticMessage.contains("ML Kit OCR"))
    }
}
