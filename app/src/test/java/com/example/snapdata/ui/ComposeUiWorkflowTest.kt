package com.example.snapdata.ui

import com.example.snapdata.model.*
import com.example.snapdata.sample.SampleDocumentRepository
import org.junit.Assert.*
import org.junit.Test

class ComposeUiWorkflowTest {

    @Test
    fun testInitialUiStateDefaults() {
        val uiState = UiState()

        assertEquals(AppScreen.LANDING, uiState.currentScreen)
        assertEquals(ProcessingStage.IDLE, uiState.processingProgress.stage)
        assertFalse(uiState.isExporting)
        assertFalse(uiState.isDocumentSaved)
        assertEquals(0, uiState.activeFields.size)
        assertEquals(0, uiState.activeTables.size)
        assertEquals("", uiState.activeTitle)
        assertEquals(DocumentType.GENERAL_DOCUMENT, uiState.activeDocType)
        assertEquals(ExportFormat.EXCEL, uiState.selectedExportFormat)
        assertNull(uiState.exportError)
        assertNull(uiState.acquisitionError)
        assertNull(uiState.pdfError)
        assertNull(uiState.databaseError)
        assertNull(uiState.authError)
        assertFalse(uiState.isAuthLoading)
    }

    @Test
    fun testAppScreenNavigationEnumCoverage() {
        val allScreens = AppScreen.values()
        assertEquals(16, allScreens.size)
        assertTrue(allScreens.contains(AppScreen.SPLASH))
        assertTrue(allScreens.contains(AppScreen.LANDING))
        assertTrue(allScreens.contains(AppScreen.AUTH_WELCOME))
        assertTrue(allScreens.contains(AppScreen.SIGN_IN))
        assertTrue(allScreens.contains(AppScreen.SIGN_UP))
        assertTrue(allScreens.contains(AppScreen.FORGOT_PASSWORD))
        assertTrue(allScreens.contains(AppScreen.VERIFY_EMAIL))
        assertTrue(allScreens.contains(AppScreen.AUTH_SUCCESS))
        assertTrue(allScreens.contains(AppScreen.HOME))
        assertTrue(allScreens.contains(AppScreen.ACQUISITION))
        assertTrue(allScreens.contains(AppScreen.PREPROCESSING))
        assertTrue(allScreens.contains(AppScreen.PROCESSING))
        assertTrue(allScreens.contains(AppScreen.REVIEW_EDITOR))
        assertTrue(allScreens.contains(AppScreen.EXPORT))
        assertTrue(allScreens.contains(AppScreen.HISTORY))
        assertTrue(allScreens.contains(AppScreen.SETTINGS))
    }

    @Test
    fun testSampleDocumentRepositoryWorkflow() {
        val samples = SampleDocumentRepository.samples
        assertTrue("Sample documents must be populated", samples.isNotEmpty())

        val invoiceSample = samples.firstOrNull { it.type == DocumentType.INVOICE }
        assertNotNull("Invoice sample should exist", invoiceSample)
        invoiceSample?.let { sample ->
            assertTrue(sample.title.isNotBlank())
            assertTrue(sample.rawText.isNotBlank())
            assertEquals(DocumentType.INVOICE, sample.type)

            // Simulate Preprocessing State transition from Sample
            var state = UiState(
                currentScreen = AppScreen.PREPROCESSING,
                activeTitle = sample.title,
                activeDocType = sample.type,
                activeRawOcrText = sample.rawText
            )
            assertEquals(AppScreen.PREPROCESSING, state.currentScreen)
            assertEquals(sample.title, state.activeTitle)
            assertEquals(DocumentType.INVOICE, state.activeDocType)

            // Simulate updating options
            val customOptions = ProcessingOptions(
                enhanceContrast = true,
                autoCrop = true,
                removeShadows = true,
                enableCloudAi = false,
                forceOfflineAi = true
            )
            state = state.copy(processingOptions = customOptions)
            assertEquals(customOptions, state.processingOptions)
            assertTrue(state.processingOptions.forceOfflineAi)
            assertFalse(state.processingOptions.enableCloudAi)
        }
    }

    @Test
    fun testExtractedFieldManipulationsAndConfidenceValidation() {
        val field1 = ExtractedField(
            key = "Vendor",
            value = "Apex Cloud Solutions",
            confidence = 0.98f,
            category = "Financial",
            confidenceSource = ConfidenceSource.MEASURED
        )
        val field2 = ExtractedField(
            key = "Amount Due",
            value = "$2,734.20",
            confidence = 0.55f, // Below 0.70 threshold -> lowConfidenceWarning
            category = "Financial",
            confidenceSource = ConfidenceSource.HEURISTIC
        )

        assertFalse(field1.lowConfidenceWarning)
        assertTrue(field2.lowConfidenceWarning)

        // Mutate field
        field1.value = "Apex Cloud Solutions Inc."
        field1.isUserEdited = true
        assertEquals("Apex Cloud Solutions Inc.", field1.value)
        assertTrue(field1.isUserEdited)

        val fieldsList = mutableListOf(field1, field2)
        assertEquals(2, fieldsList.size)

        // Add custom attribute
        fieldsList.add(ExtractedField(key = "Tax ID", value = "US-94827103-X", category = "Tax"))
        assertEquals(3, fieldsList.size)

        // Delete attribute
        fieldsList.removeAt(0)
        assertEquals(2, fieldsList.size)
        assertEquals("Amount Due", fieldsList[0].key)
    }

    @Test
    fun testExtractedTableMatrixManipulations() {
        val headers = mutableListOf("Description", "Qty", "Unit Price", "Total")
        val rows = mutableListOf(
            mutableListOf("Cloud Server Tier 3", "1", "$1,250.00", "$1,250.00"),
            mutableListOf("Dedicated Storage 5TB", "5", "$90.00", "$450.00")
        )
        val table = ExtractedTable(
            name = "Line Items",
            headers = headers,
            rows = rows,
            confidence = 0.94f,
            confidenceSource = ConfidenceSource.HYBRID
        )

        assertEquals("Line Items", table.name)
        assertEquals(4, table.headers.size)
        assertEquals(2, table.rows.size)

        // Add column
        table.headers.add("Discount")
        table.rows.forEach { it.add("0%") }
        assertEquals(5, table.headers.size)
        assertEquals(5, table.rows[0].size)
        assertEquals("0%", table.rows[0][4])

        // Add row
        table.rows.add(mutableListOf("Support SLA", "1", "$500.00", "$500.00", "0%"))
        assertEquals(3, table.rows.size)

        // Update cell
        table.rows[2][4] = "10%"
        assertEquals("10%", table.rows[2][4])

        // Delete row
        table.rows.removeAt(0)
        assertEquals(2, table.rows.size)
    }

    @Test
    fun testProcessingStageTransitions() {
        val stages = listOf(
            ProcessingStage.IDLE,
            ProcessingStage.ACQUISITION,
            ProcessingStage.PREPROCESSING,
            ProcessingStage.OCR,
            ProcessingStage.AI_ANALYSIS,
            ProcessingStage.STRUCTURED_EXTRACTION,
            ProcessingStage.TABLE_DETECTION,
            ProcessingStage.VALIDATION,
            ProcessingStage.COMPLETED,
            ProcessingStage.ERROR
        )

        stages.forEach { stage ->
            assertTrue(stage.title.isNotBlank())
            assertTrue(stage.description.isNotBlank())
            assertTrue(stage.progressPercent in 0..100)
        }

        val stepProgress = ProcessingProgress(
            stage = ProcessingStage.OCR,
            currentStep = 2,
            totalSteps = 4,
            detailMessage = "Processing page 2 of 4"
        )
        assertEquals(2, stepProgress.currentStep)
        assertEquals(4, stepProgress.totalSteps)
        assertEquals("Processing page 2 of 4", stepProgress.detailMessage)
    }

    @Test
    fun testExportFormatAttributes() {
        val formats = ExportFormat.values()
        assertEquals(4, formats.size)

        val excel = ExportFormat.EXCEL
        assertEquals("xlsx", excel.extension)
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excel.mimeType)

        val csv = ExportFormat.CSV
        assertEquals("csv", csv.extension)
        assertEquals("text/csv", csv.mimeType)

        val json = ExportFormat.JSON
        assertEquals("json", json.extension)
        assertEquals("application/json", json.mimeType)

        val pdf = ExportFormat.PDF
        assertEquals("pdf", pdf.extension)
        assertEquals("application/pdf", pdf.mimeType)
    }

    @Test
    fun testErrorStatesDismissalAndRecovery() {
        var state = UiState(
            exportError = "Disk write permission denied",
            acquisitionError = "Camera device hardware busy",
            pdfError = "Corrupt PDF stream"
        )

        assertNotNull(state.exportError)
        assertNotNull(state.acquisitionError)
        assertNotNull(state.pdfError)

        // Clear export error
        state = state.copy(exportError = null)
        assertNull(state.exportError)

        // Clear acquisition and pdf errors
        state = state.copy(acquisitionError = null, pdfError = null)
        assertNull(state.acquisitionError)
        assertNull(state.pdfError)
    }

    @Test
    fun testFreshInstallStartupAndNavigationFlow() {
        // Requirement: LandingScreen loads on startup as the default root screen
        val freshState = UiState()
        assertEquals(AppScreen.LANDING, freshState.currentScreen)

        // Step 1: Tap Get Started / Download -> Navigate to AUTH_WELCOME (or HOME)
        var state = freshState.copy(currentScreen = AppScreen.AUTH_WELCOME)
        assertEquals(AppScreen.AUTH_WELCOME, state.currentScreen)

        // Step 2: Choose Sign In / Sign Up -> Navigate to SIGN_IN
        state = state.copy(currentScreen = AppScreen.SIGN_IN)
        assertEquals(AppScreen.SIGN_IN, state.currentScreen)

        // Step 3: Successful Authentication -> Navigate to HOME
        state = state.copy(currentScreen = AppScreen.HOME)
        assertEquals(AppScreen.HOME, state.currentScreen)

        // Step 4: Verify flow sequence: LANDING -> AUTH_WELCOME -> SIGN_IN -> HOME
        assertTrue(state.currentScreen == AppScreen.HOME)
    }

    @Test
    fun testAiAndOcrStartupSafety() {
        // Verify GeminiAiService does not crash on missing / default credentials
        val key = com.example.snapdata.processing.GeminiAiService.getApiKey()
        assertNotNull(key)

        val backend = com.example.snapdata.processing.GeminiAiService.getBackendUrl()
        assertNotNull(backend)

        // Verify DocumentType enum sanitizer never throws
        val sanitized = com.example.snapdata.processing.GeminiAiService.sanitizeDocumentType("UNKNOWN_CUSTOM_TYPE")
        assertEquals(DocumentType.GENERAL_DOCUMENT, sanitized)
    }
}

