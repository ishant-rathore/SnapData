package com.example.snapdata

import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.error.AppError
import com.example.snapdata.error.ErrorDomain
import com.example.snapdata.export.ExportManager
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.*
import com.example.snapdata.processing.AiProcessingError
import com.example.snapdata.processing.GeminiAiService
import com.example.snapdata.processing.ImagePreprocessor
import com.example.snapdata.processing.OcrEngine
import com.example.snapdata.processing.PdfDocumentRenderer
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ErrorHandlingSystemTest {

    // =========================================================================
    // 1. APPLOGGER PRIVACY & SECRET SANITIZATION TESTS
    // =========================================================================

    @Test
    fun testAppLoggerSanitizesGeminiApiKeys() {
        val rawMessage = "Connecting to endpoint https://generativelanguage.googleapis.com?key=AIzaSyD-1234567890abcdefghijklmnopqrstuv"
        val sanitized = AppLogger.sanitize(rawMessage)

        assertFalse("Raw API key must not be present in sanitized log", sanitized.contains("AIzaSyD-1234567890abcdefghijklmnopqrstuv"))
        assertTrue("Sanitized message should contain redacted marker", sanitized.contains("AIzaSy[REDACTED]") || sanitized.contains("[REDACTED]"))
    }

    @Test
    fun testAppLoggerSanitizesBearerTokensAndPasswords() {
        val rawMessage = "Authorization: Bearer secret_token_xyz_987654 for user ishan"
        val sanitized = AppLogger.sanitize(rawMessage)

        assertFalse("Bearer token must not be leaked", sanitized.contains("secret_token_xyz_987654"))
        assertTrue("Sanitized message should redact authorization token", sanitized.contains("Authorization=[REDACTED]") || sanitized.contains("[REDACTED]"))
    }

    @Test
    fun testAppLoggerRedactsDocumentContentAndPII() {
        val sensitiveDocument = """
            CONFIDENTIAL TAX RETURN
            Aadhaar: 5544-2211-9988
            PAN: ABCDE1234F
            Name: Aarav Sharma
            Bank Account: 1234-5678-9012-3456
            Total Income: ₹15,00,000.00
        """.trimIndent()

        val redacted = AppLogger.redactDocumentText(sensitiveDocument)

        assertFalse("Raw Aadhaar must not be in redacted log", redacted.contains("5544-2211-9988"))
        assertFalse("Raw PAN must not be in redacted log", redacted.contains("ABCDE1234F"))
        assertFalse("Raw Name must not be in redacted log", redacted.contains("Aarav Sharma"))
        assertFalse("Raw Bank Account must not be in redacted log", redacted.contains("1234-5678-9012-3456"))
        assertTrue("Redacted text should report character count", redacted.contains("chars"))
        assertTrue("Redacted text should report line count", redacted.contains("lines"))
        assertTrue("Redacted text should report word count", redacted.contains("words"))
        assertTrue("Redacted text should report SHA256 snippet", redacted.contains("SHA256:"))
    }

    @Test
    fun testAppLoggerHandlesNullAndBlankSafely() {
        assertEquals("", AppLogger.sanitize(null))
        assertEquals("[Empty Document Text]", AppLogger.redactDocumentText(null))
        assertEquals("[Empty Document Text]", AppLogger.redactDocumentText(""))
        assertEquals("[Empty Document Text]", AppLogger.redactDocumentText("   "))
    }

    // =========================================================================
    // 2. OPERATION STATE MACHINE TESTS (All 8 States)
    // =========================================================================

    @Test
    fun testOperationStateHierarchyTransitions() {
        // 1. Idle
        val idleState: OperationState<String> = OperationState.Idle
        assertTrue(idleState is OperationState.Idle)

        // 2. Loading
        val loadingState: OperationState<String> = OperationState.Loading("Reading document buffer...")
        assertTrue(loadingState is OperationState.Loading)
        assertEquals("Reading document buffer...", (loadingState as OperationState.Loading).message)

        // 3. Processing
        val processingState: OperationState<String> = OperationState.Processing(
            stage = ProcessingStage.OCR,
            progressPercent = 50,
            currentStep = 3,
            totalSteps = 6,
            detailMessage = "Extracting optical text bounding boxes..."
        )
        assertTrue(processingState is OperationState.Processing)
        val proc = processingState as OperationState.Processing
        assertEquals(ProcessingStage.OCR, proc.stage)
        assertEquals(50, proc.progressPercent)
        assertEquals(3, proc.currentStep)

        // 4. Success
        val successState: OperationState<String> = OperationState.Success("INV-2026-001", "Extraction completed successfully.")
        assertTrue(successState is OperationState.Success)
        assertEquals("INV-2026-001", (successState as OperationState.Success).data)

        // 5. PartialSuccess
        val partialState: OperationState<String> = OperationState.PartialSuccess(
            data = "INV-2026-001",
            warnings = listOf("Low contrast detected on page 1", "Cloud AI unavailable, fell back to local OCR"),
            diagnosticMessage = "Processed with local OCR fallback."
        )
        assertTrue(partialState is OperationState.PartialSuccess)
        val partial = partialState as OperationState.PartialSuccess
        assertEquals(2, partial.warnings.size)
        assertEquals("Processed with local OCR fallback.", partial.diagnosticMessage)

        // 6. RecoverableError
        val recoverableErr = AppError.ImageImportError.InaccessibleUri("File descriptor expired")
        val recoverableState: OperationState<String> = OperationState.RecoverableError(
            error = recoverableErr,
            userMessage = recoverableErr.userMessage,
            suggestedAction = recoverableErr.suggestedAction,
            preservedData = "partial_raw_text"
        )
        assertTrue(recoverableState is OperationState.RecoverableError)
        val rec = recoverableState as OperationState.RecoverableError
        assertEquals(ErrorDomain.IMAGE_IMPORT, rec.error.domain)
        assertTrue(rec.error.isRecoverable)
        assertEquals("partial_raw_text", rec.preservedData)

        // 7. FatalError
        val fatalErr = AppError.CameraError.HardwareUnavailable("No camera sensor")
        val fatalState: OperationState<String> = OperationState.FatalError(
            error = fatalErr,
            userMessage = fatalErr.userMessage,
            technicalDetails = fatalErr.technicalDetails
        )
        assertTrue(fatalState is OperationState.FatalError)
        val fatal = fatalState as OperationState.FatalError
        assertFalse(fatal.error.isRecoverable)
        assertFalse(fatal.error.isRetryable)

        // 8. Cancelled
        val cancelledState: OperationState<String> = OperationState.Cancelled("Extraction cancelled by user.")
        assertTrue(cancelledState is OperationState.Cancelled)
        assertEquals("Extraction cancelled by user.", (cancelledState as OperationState.Cancelled).message)
    }

    // =========================================================================
    // 3. ERROR DOMAIN HIERARCHY TESTS (All 9 Domains)
    // =========================================================================

    @Test
    fun testCameraErrors() {
        val hwErr = AppError.CameraError.HardwareUnavailable()
        assertEquals(ErrorDomain.CAMERA, hwErr.domain)
        assertFalse(hwErr.isRecoverable)
        assertFalse(hwErr.isRetryable)
        assertNotNull(hwErr.suggestedAction)

        val permErr = AppError.CameraError.PermissionDenied()
        assertEquals(ErrorDomain.CAMERA, permErr.domain)
        assertTrue(permErr.isRecoverable)
        assertTrue(permErr.isRetryable)

        val permPermErr = AppError.CameraError.PermissionPermanentlyDenied()
        assertTrue(permPermErr.userMessage.contains("App Settings", ignoreCase = true) || permPermErr.userMessage.contains("disabled", ignoreCase = true))

        val storageErr = AppError.CameraError.StorageInitFailed("Cannot write to cache")
        assertTrue(storageErr.isRecoverable)

        val captureErr = AppError.CameraError.CaptureFailed("User dismissed shutter")
        assertTrue(captureErr.isRetryable)
    }

    @Test
    fun testImageImportErrors() {
        val inaccessible = AppError.ImageImportError.InaccessibleUri("Stream closed")
        assertEquals(ErrorDomain.IMAGE_IMPORT, inaccessible.domain)
        assertTrue(inaccessible.isRecoverable)

        val emptyFile = AppError.ImageImportError.EmptyFile("0 bytes")
        assertFalse(emptyFile.isRecoverable)

        val corrupted = AppError.ImageImportError.CorruptedFile("Invalid JPEG header")
        assertFalse(corrupted.isRecoverable)

        val security = AppError.ImageImportError.SecurityDenied("SecurityException")
        assertTrue(security.isRecoverable)

        val oom = AppError.ImageImportError.OutOfMemory("Heap capacity exceeded")
        assertTrue(oom.isRecoverable)

        val unsupported = AppError.ImageImportError.UnsupportedFormat("image/heic")
        assertFalse(unsupported.isRecoverable)
    }

    @Test
    fun testPdfImportErrors() {
        val encrypted = AppError.PdfImportError.EncryptedOrPasswordProtected("AES password active")
        assertEquals(ErrorDomain.PDF_IMPORT, encrypted.domain)
        assertFalse(encrypted.isRecoverable)
        assertTrue(encrypted.userMessage.contains("password", ignoreCase = true) || encrypted.userMessage.contains("encrypted", ignoreCase = true))

        val invalid = AppError.PdfImportError.InvalidPdfStructure("Missing %PDF header")
        assertFalse(invalid.isRecoverable)

        val empty = AppError.PdfImportError.EmptyPdf("0 pages")
        assertFalse(empty.isRecoverable)

        val security = AppError.PdfImportError.SecurityDenied("Permission denied")
        assertTrue(security.isRecoverable)

        val memory = AppError.PdfImportError.MemoryExhausted("OOM in rasterizer")
        assertTrue(memory.isRecoverable)

        val nativeRender = AppError.PdfImportError.NativeRendererFailed(pageIndex = 2, detail = "Native crash on page 3")
        assertEquals(2, nativeRender.pageIndex)
        assertTrue(nativeRender.userMessage.contains("page 3", ignoreCase = true))
    }

    @Test
    fun testPreprocessingErrors() {
        val enhanceErr = AppError.PreprocessingError.EnhancementFailed("ColorMatrix out of range")
        assertEquals(ErrorDomain.PREPROCESSING, enhanceErr.domain)
        assertTrue(enhanceErr.isRecoverable)

        val cropErr = AppError.PreprocessingError.CropFailed("Bounding rect outside image")
        assertTrue(cropErr.isRecoverable)

        val oomErr = AppError.PreprocessingError.OutOfMemory("GC failure during canvas draw")
        assertTrue(oomErr.isRecoverable)

        val rotErr = AppError.PreprocessingError.RotationFailed("Matrix transform error")
        assertTrue(rotErr.isRecoverable)
    }

    @Test
    fun testOcrErrors() {
        val initErr = AppError.OcrError.EngineInitFailed("ML Kit text recognizer failed")
        assertEquals(ErrorDomain.OCR, initErr.domain)
        assertTrue(initErr.isRecoverable)

        val noTextErr = AppError.OcrError.NoTextDetected("0 tokens returned")
        assertTrue(noTextErr.isRecoverable)

        val lowLegErr = AppError.OcrError.LowLegibility("Confidence below threshold 0.50")
        assertTrue(lowLegErr.isRecoverable)

        val memErr = AppError.OcrError.MemoryExhausted("ML Kit native OOM")
        assertTrue(memErr.isRecoverable)

        val recErr = AppError.OcrError.RecognitionFailed("Generic recognition failure")
        assertTrue(recErr.isRecoverable)
    }

    @Test
    fun testAiErrors() {
        val keyMissing = AppError.AiError.ApiKeyMissing("GEMINI_API_KEY is empty")
        assertEquals(ErrorDomain.AI, keyMissing.domain)
        assertTrue(keyMissing.isRecoverable)

        val invalidKey = AppError.AiError.InvalidApiKey(401, "HTTP 401 Unauthorized")
        assertEquals(401, invalidKey.statusCode)
        assertTrue(invalidKey.isRecoverable)

        val rateLimit = AppError.AiError.RateLimitExceeded(429, "HTTP 429 Resource Exhausted")
        assertEquals(429, rateLimit.statusCode)
        assertTrue(rateLimit.isRecoverable)

        val network = AppError.AiError.NetworkUnavailable("UnknownHostException")
        assertTrue(network.isRecoverable)

        val timeout = AppError.AiError.Timeout(30000, "SocketTimeoutException")
        assertEquals(30000, timeout.timeoutMs)
        assertTrue(timeout.isRecoverable)

        val malformed = AppError.AiError.MalformedResponse("Not valid JSON { [")
        assertTrue(malformed.isRecoverable)

        val oversized = AppError.AiError.OversizedPayload(3 * 1024 * 1024, "Payload 3MB exceeded 2MB limit")
        assertEquals(3 * 1024 * 1024, oversized.sizeBytes)
        assertTrue(oversized.isRecoverable)

        val serverErr = AppError.AiError.ServerError(503, "Service Unavailable")
        assertEquals(503, serverErr.statusCode)
        assertTrue(serverErr.isRecoverable)
    }

    @Test
    fun testDatabaseErrors() {
        val readErr = AppError.DatabaseError.ReadFailed("SQLite disk I/O error")
        assertEquals(ErrorDomain.DATABASE, readErr.domain)
        assertTrue(readErr.isRecoverable)

        val writeErr = AppError.DatabaseError.WriteFailed("SQLite database disk full")
        assertTrue(writeErr.isRecoverable)

        val notFound = AppError.DatabaseError.NotFound(docId = 999L)
        assertEquals(999L, notFound.docId)
        assertFalse(notFound.isRecoverable)

        val corrupt = AppError.DatabaseError.CorruptedData("fieldsJson syntax error")
        assertTrue(corrupt.isRecoverable)
    }

    @Test
    fun testExportErrors() {
        val storageErr = AppError.ExportError.StorageUnavailable("Cannot create cache/exports")
        assertEquals(ErrorDomain.EXPORT, storageErr.domain)
        assertTrue(storageErr.isRecoverable)

        val formatErr = AppError.ExportError.FormattingFailed(formatName = "Excel (.xlsx)", detail = "Zip compression failure")
        assertEquals("Excel (.xlsx)", formatErr.formatName)
        assertTrue(formatErr.isRecoverable)

        val emptyErr = AppError.ExportError.EmptyOutput("0-byte file generated")
        assertTrue(emptyErr.isRecoverable)

        val encodingErr = AppError.ExportError.EncodingError("Malformed UTF-8 sequence")
        assertTrue(encodingErr.isRecoverable)
    }

    @Test
    fun testSharingErrors() {
        val fileNotFound = AppError.SharingError.FileNotFound("Export file not on disk")
        assertEquals(ErrorDomain.SHARING, fileNotFound.domain)
        assertTrue(fileNotFound.isRecoverable)

        val securityUri = AppError.SharingError.SecurityUriGrantFailed("FileProvider authority mismatch")
        assertTrue(securityUri.isRecoverable)

        val noApp = AppError.SharingError.NoCompatibleAppFound(
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            detail = "ActivityNotFoundException"
        )
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", noApp.mimeType)
        assertTrue(noApp.isRecoverable)
        assertFalse(noApp.isRetryable)
    }

    // =========================================================================
    // 4. ERROR MAPPING & ADAPTER TESTS
    // =========================================================================

    @Test
    fun testImageErrorToAppErrorConversion() {
        val imgErr1 = ImagePreprocessor.ImageError.InaccessibleUri("Detail 1").toAppError()
        assertTrue(imgErr1 is AppError.ImageImportError.InaccessibleUri)

        val imgErr2 = ImagePreprocessor.ImageError.EmptyFile("Detail 2").toAppError()
        assertTrue(imgErr2 is AppError.ImageImportError.EmptyFile)

        val imgErr3 = ImagePreprocessor.ImageError.CorruptedFile("Detail 3").toAppError()
        assertTrue(imgErr3 is AppError.ImageImportError.CorruptedFile)

        val imgErr4 = ImagePreprocessor.ImageError.SecurityError("Detail 4").toAppError()
        assertTrue(imgErr4 is AppError.ImageImportError.SecurityDenied)

        val imgErr5 = ImagePreprocessor.ImageError.OutOfMemory("Detail 5").toAppError()
        assertTrue(imgErr5 is AppError.ImageImportError.OutOfMemory)
    }

    @Test
    fun testPdfErrorToAppErrorConversion() {
        val pdfErr1 = PdfDocumentRenderer.PdfError.EncryptedPdf("AES").toAppError()
        assertTrue(pdfErr1 is AppError.PdfImportError.EncryptedOrPasswordProtected)

        val pdfErr2 = PdfDocumentRenderer.PdfError.InvalidPdf("Corrupt").toAppError()
        assertTrue(pdfErr2 is AppError.PdfImportError.InvalidPdfStructure)

        val pdfErr3 = PdfDocumentRenderer.PdfError.EmptyPdf("0 pgs").toAppError()
        assertTrue(pdfErr3 is AppError.PdfImportError.EmptyPdf)

        val pdfErr4 = PdfDocumentRenderer.PdfError.FileAccessError("Denied").toAppError()
        assertTrue(pdfErr4 is AppError.PdfImportError.SecurityDenied)

        val pdfErr5 = PdfDocumentRenderer.PdfError.MemoryError("OOM").toAppError()
        assertTrue(pdfErr5 is AppError.PdfImportError.MemoryExhausted)

        val pdfErr6 = PdfDocumentRenderer.PdfError.RenderingFailure(pageIndex = 4, detail = "Page 5 failure").toAppError()
        assertTrue(pdfErr6 is AppError.PdfImportError.NativeRendererFailed)
        assertEquals(4, (pdfErr6 as AppError.PdfImportError.NativeRendererFailed).pageIndex)
    }

    @Test
    fun testAiProcessingErrorToAppErrorConversion() {
        val aiErr1 = AiProcessingError.ApiKeyMissing.toAppError()
        assertTrue(aiErr1 is AppError.AiError.ApiKeyMissing)

        val aiErr2 = AiProcessingError.InvalidApiKey("401").toAppError()
        assertTrue(aiErr2 is AppError.AiError.InvalidApiKey)

        val aiErr3 = AiProcessingError.RateLimitExceeded("429").toAppError()
        assertTrue(aiErr3 is AppError.AiError.RateLimitExceeded)

        val aiErr4 = AiProcessingError.NetworkUnavailable("Offline").toAppError()
        assertTrue(aiErr4 is AppError.AiError.NetworkUnavailable)

        val aiErr5 = AiProcessingError.Timeout(30000).toAppError()
        assertTrue(aiErr5 is AppError.AiError.Timeout)

        val aiErr6 = AiProcessingError.MalformedResponse("Bad JSON").toAppError()
        assertTrue(aiErr6 is AppError.AiError.MalformedResponse)

        val aiErr7 = AiProcessingError.OversizedResponse(5000000).toAppError()
        assertTrue(aiErr7 is AppError.AiError.OversizedPayload)

        val aiErr8 = AiProcessingError.ApiError(500, "Internal Server Error").toAppError()
        assertTrue(aiErr8 is AppError.AiError.ServerError)
    }

    // =========================================================================
    // 5. DATABASE RESILIENCE & CORRUPTED DATA RECOVERY TESTS
    // =========================================================================

    @Test
    fun testDocumentEntityHandlesCorruptedJsonGracefully() {
        val entityWithCorruptedJson = DocumentEntity(
            id = 10L,
            title = "Corrupted Doc",
            docType = "INVOICE",
            summary = "Summary preserved despite JSON corruption.",
            rawOcrText = "Raw text preserved.",
            fieldsJson = "This is NOT valid json at all { [",
            tablesJson = "Also completely corrupted [ { \"table\"",
            overallConfidence = 0.85f
        )

        // Fields should not throw exception; should return emptyList() gracefully
        val fields = entityWithCorruptedJson.getFieldsList()
        assertNotNull(fields)
        assertTrue(fields.isEmpty())

        // Tables should not throw exception; should return emptyList() gracefully
        val tables = entityWithCorruptedJson.getTablesList()
        assertNotNull(tables)
        assertTrue(tables.isEmpty())

        // DocType parsing should safely fallback to GENERAL_DOCUMENT if corrupted
        val entityWithUnknownDocType = entityWithCorruptedJson.copy(docType = "UNKNOWN_NEW_SCHEMA_TYPE_XYZ")
        assertEquals(DocumentType.GENERAL_DOCUMENT, entityWithUnknownDocType.getTypedDocType())
    }

    @Test
    fun testDocumentEntityHasValidImageFileCheck() {
        val entityNoPath = DocumentEntity(id = 1L, title = "No Image", originalImagePath = null)
        assertFalse(entityNoPath.hasValidImageFile())

        val entityBlankPath = DocumentEntity(id = 2L, title = "Blank Path", originalImagePath = "   ")
        assertFalse(entityBlankPath.hasValidImageFile())

        val entityNonExistent = DocumentEntity(id = 3L, title = "Missing File", originalImagePath = "/non/existent/path/doc.jpg")
        assertFalse(entityNonExistent.hasValidImageFile())

        val tempFile = File.createTempFile("snapdata_test_img_", ".jpg")
        try {
            val entityValid = DocumentEntity(id = 4L, title = "Valid File", originalImagePath = tempFile.absolutePath)
            assertTrue(entityValid.hasValidImageFile())
        } finally {
            tempFile.delete()
        }
    }
}
