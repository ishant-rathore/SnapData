package com.example.snapdata.ui

import android.app.Application
import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.data.DocumentRepository
import com.example.snapdata.error.AppError
import com.example.snapdata.export.ExportManager
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.*
import com.example.snapdata.processing.ImagePreprocessor
import com.example.snapdata.processing.MultiPageDocumentMerger
import com.example.snapdata.processing.PdfDocumentRenderer
import com.example.snapdata.processing.ProcessingPipeline
import com.example.snapdata.sample.SampleDocument
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.snapdata.ai.engine.OfflineAiEngine
import com.example.snapdata.ai.engine.OnDeviceNeuralDocumentAnalyzer
import com.example.snapdata.ai.model.ModelDownloadProgress
import com.example.snapdata.ai.model.ModelMetadata
import com.example.snapdata.ai.model.ModelStatus
import com.example.snapdata.ai.model.OnDeviceModelManager
import com.example.snapdata.auth.data.AuthRepository
import com.example.snapdata.auth.data.SecureSessionStorage
import com.example.snapdata.auth.domain.*
import com.example.snapdata.ui.screens.guide.UserGuidePreferences
import java.io.File
import java.io.FileOutputStream


enum class AppScreen {
    SPLASH,
    LANDING,
    AUTH_WELCOME,
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    VERIFY_EMAIL,
    AUTH_SUCCESS,
    USER_GUIDE,
    HOME,
    ACQUISITION,
    PREPROCESSING,
    PROCESSING,
    REVIEW_EDITOR,
    EXPORT,
    HISTORY,
    SETTINGS
}

data class UiState(
    val currentScreen: AppScreen = AppScreen.LANDING,
    val selectedDocTypeFilter: DocumentType? = null,
    val searchQuery: String = "",
    val activeBitmap: Bitmap? = null,
    val activeImagePath: String? = null,
    val activeHintText: String? = null,
    val processingOptions: ProcessingOptions = ProcessingOptions(),
    val processingProgress: ProcessingProgress = ProcessingProgress(),
    val preprocessedBitmap: Bitmap? = null,

    // Core Application State Machine
    val appOperationState: OperationState<Any> = OperationState.Idle,

    // Acquisition & Camera State
    val pendingCameraUri: Uri? = null,
    val acquisitionError: String? = null,
    val cameraError: AppError.CameraError? = null,
    val imageError: AppError.ImageImportError? = null,

    // PDF specific state
    val activePdfFile: File? = null,
    val activePdfPageCount: Int = 1,
    val activePdfFileName: String? = null,
    val pdfError: String? = null,
    val pdfImportError: AppError.PdfImportError? = null,
    val activePageResults: List<MultiPageDocumentMerger.PageOcrData> = emptyList(),

    // Active Document Being Reviewed / Edited
    val activeDocId: Long = 0,
    val activeTitle: String = "",
    val activeDocType: DocumentType = DocumentType.GENERAL_DOCUMENT,
    val activeSummary: String = "",
    val activeRawOcrText: String = "",
    val activeFields: List<ExtractedField> = emptyList(),
    val activeTables: List<ExtractedTable> = emptyList(),
    val activeConfidence: Float = 0.95f,
    val activeEngineUsed: String = "On-Device Local OCR",
    val activeDiagnosticMessage: String = "",
    val activeCreatedAt: Long? = null,
    val isDocumentSaved: Boolean = false,
    val saveMessage: String? = null,
    val databaseError: AppError.DatabaseError? = null,

    // Export & Sharing state
    val selectedExportFormat: ExportFormat = ExportFormat.EXCEL,
    val lastExportResult: ExportManager.ExportResult? = null,
    val isExporting: Boolean = false,
    val exportError: String? = null,
    val exportAppError: AppError.ExportError? = null,
    val sharingAppError: AppError.SharingError? = null,

    // Authentication State
    val authError: AppAuthError? = null,
    val isAuthLoading: Boolean = false,
    val isResetSent: Boolean = false,
    val isResendSuccess: Boolean = false,

    // Settings & Diagnostics
    val selectedOcrLanguage: String = "English (en)",
    val isDarkMode: Boolean = false
)

class SnapDataViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        savedStateHandle = SavedStateHandle(),
        authRepository = AuthRepository.create(
            SecureSessionStorage(application),
            application
        )
    )

    constructor(application: Application, savedStateHandle: SavedStateHandle) : this(
        application = application,
        savedStateHandle = savedStateHandle,
        authRepository = AuthRepository.create(
            SecureSessionStorage(application),
            application
        )
    )


    companion object {
        private const val KEY_PENDING_CAMERA_URI = "snapdata_pending_camera_uri"
        private const val KEY_PENDING_CAMERA_PATH = "snapdata_pending_camera_path"
        private const val KEY_ACTIVE_IMAGE_PATH = "snapdata_active_image_path"
        private const val KEY_ACTIVE_PDF_PATH = "snapdata_active_pdf_path"
        private const val KEY_CURRENT_SCREEN = "snapdata_current_screen"
    }

    private val repository = DocumentRepository(application)
    val modelManager = OnDeviceModelManager.getInstance(application)

    val modelStatus: StateFlow<ModelStatus> = modelManager.status
    val modelProgress: StateFlow<ModelDownloadProgress> = modelManager.progress
    val modelLastVerified: StateFlow<Long> = modelManager.lastVerifiedTimestamp
    val modelMetadata: ModelMetadata get() = modelManager.metadata
    val isOfflineAiReady: Boolean get() = modelManager.status.value.isReady

    val authState: StateFlow<AuthState> = authRepository.authState
    val currentUser: AuthUser? get() = authRepository.currentUser
    val isLoggedIn: Boolean get() = currentUser != null
    val isFirebaseConfigured: Boolean get() = authRepository.isFirebaseConfigured


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var activeProcessingJob: Job? = null
    private var activeExportJob: Job? = null

    init {
        // Prototype requirement: Always start from Landing Page and reset/ignore saved navigation state
        savedStateHandle.remove<String>(KEY_CURRENT_SCREEN)

        // Safe session restoration on startup
        viewModelScope.launch {
            try {
                authRepository.restoreSession()
            } catch (t: Throwable) {
                AppLogger.w(AppLogger.LogDomain.AUTH, "Session restoration fallback: ${t.localizedMessage}")
            }
        }

        // Restore pending camera URI or active document state across process recreation safely
        try {
            val restoredCameraUriStr = savedStateHandle.get<String>(KEY_PENDING_CAMERA_URI)
            if (!restoredCameraUriStr.isNullOrBlank()) {
                val restoredUri = Uri.parse(restoredCameraUriStr)
                _uiState.update { it.copy(pendingCameraUri = restoredUri) }
            }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.CAMERA, "Failed to restore pending camera URI: ${e.localizedMessage}")
        }

        try {
            val restoredImagePath = savedStateHandle.get<String>(KEY_ACTIVE_IMAGE_PATH)
            if (!restoredImagePath.isNullOrBlank()) {
                val file = File(restoredImagePath)
                if (file.exists() && file.length() > 0) {
                    val restoredBitmap = ImagePreprocessor.loadBitmapFromUri(application, Uri.fromFile(file))
                    if (restoredBitmap != null) {
                        _uiState.update {
                            it.copy(
                                activeBitmap = restoredBitmap,
                                activeImagePath = file.absolutePath
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.IMAGE, "Failed to restore active image: ${e.localizedMessage}")
        }

        try {
            val restoredPdfPath = savedStateHandle.get<String>(KEY_ACTIVE_PDF_PATH)
            if (!restoredPdfPath.isNullOrBlank()) {
                val file = File(restoredPdfPath)
                if (file.exists() && file.length() > 0) {
                    setPdfUri(Uri.fromFile(file))
                }
            }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.PDF, "Failed to restore active PDF: ${e.localizedMessage}")
        }
    }

    // History Flow from Room Database (Reactively observed)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val savedDocuments: StateFlow<List<DocumentEntity>> = _uiState
        .flatMapLatest { state ->
            if (state.searchQuery.isNotBlank()) {
                repository.searchDocuments(state.searchQuery)
            } else if (state.selectedDocTypeFilter != null) {
                repository.getDocumentsByType(state.selectedDocTypeFilter.name)
            } else {
                repository.allDocuments
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDocumentCount: StateFlow<Int> = repository.documentCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val protectedScreens = setOf(
        AppScreen.USER_GUIDE,
        AppScreen.HOME,
        AppScreen.ACQUISITION,
        AppScreen.PREPROCESSING,
        AppScreen.PROCESSING,
        AppScreen.REVIEW_EDITOR,
        AppScreen.EXPORT,
        AppScreen.HISTORY,
        AppScreen.SETTINGS
    )

    private val guidePrefs = UserGuidePreferences(application)

    val hasCompletedUserGuide: Boolean
        get() = guidePrefs.hasCompletedUserGuide

    fun startUserGuide() {
        if (!isLoggedIn) {
            continueAsGuest()
        }
        navigateTo(AppScreen.USER_GUIDE)
    }

    fun completeUserGuide() {
        guidePrefs.hasCompletedUserGuide = true
        navigateTo(AppScreen.HOME)
    }

    fun skipUserGuide() {
        guidePrefs.hasCompletedUserGuide = true
        navigateTo(AppScreen.HOME)
    }

    fun resetUserGuide() {
        guidePrefs.resetGuide()
    }

    fun navigateTo(screen: AppScreen) {
        val currentAuthState = authState.value
        val target = if (screen in protectedScreens && currentAuthState !is AuthState.Authenticated) {
            AppLogger.w(AppLogger.LogDomain.AUTH, "Auth Guard: Blocked unauthenticated attempt to navigate to $screen. Redirecting to AUTH_WELCOME.")
            AppScreen.AUTH_WELCOME
        } else {
            screen
        }

        _uiState.update {
            it.copy(
                currentScreen = target,
                activeTitle = if (target == AppScreen.ACQUISITION && it.currentScreen != AppScreen.ACQUISITION) "" else it.activeTitle,
                activeBitmap = if (target == AppScreen.ACQUISITION && it.currentScreen != AppScreen.ACQUISITION) null else it.activeBitmap,
                saveMessage = null,
                acquisitionError = null,
                cameraError = null,
                imageError = null,
                pdfError = null,
                pdfImportError = null,
                databaseError = null,
                exportError = null,
                exportAppError = null,
                sharingAppError = null,
                authError = null
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setDocTypeFilter(type: DocumentType?) {
        _uiState.update { it.copy(selectedDocTypeFilter = type) }
    }

    /**
     * Prepares a persistent temp file and FileProvider Uri for Camera capture.
     * Persisted in SavedStateHandle so camera activity recreation / process death retains it.
     */
    fun prepareCameraTempUri(): Uri? {
        return try {
            val cacheDir = File(getApplication<Application>().cacheDir, "camera")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val tempFile = File(cacheDir, "snap_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                getApplication(),
                "${getApplication<Application>().packageName}.fileprovider",
                tempFile
            )
            savedStateHandle[KEY_PENDING_CAMERA_URI] = uri.toString()
            savedStateHandle[KEY_PENDING_CAMERA_PATH] = tempFile.absolutePath
            _uiState.update {
                it.copy(
                    pendingCameraUri = uri,
                    acquisitionError = null,
                    cameraError = null
                )
            }
            AppLogger.d(AppLogger.LogDomain.CAMERA, "Prepared camera capture URI: $uri")
            uri
        } catch (e: Exception) {
            val err = AppError.CameraError.StorageInitFailed(e.localizedMessage ?: "FileProvider init failed")
            AppLogger.e(AppLogger.LogDomain.CAMERA, "Failed to initialize camera storage: ${e.localizedMessage}", e)
            _uiState.update {
                it.copy(
                    acquisitionError = err.userMessage,
                    cameraError = err,
                    appOperationState = OperationState.RecoverableError(err)
                )
            }
            null
        }
    }

    /**
     * Handles Camera Activity result.
     * If user captured photo, loads the bitmap safely.
     * If user cancelled, cleans up temporary capture file.
     */
    fun onCameraCaptureResult(success: Boolean) {
        val uriString = savedStateHandle.get<String>(KEY_PENDING_CAMERA_URI)
        val pathString = savedStateHandle.get<String>(KEY_PENDING_CAMERA_PATH)
        val uri = _uiState.value.pendingCameraUri ?: (uriString?.let { Uri.parse(it) })

        savedStateHandle.remove<String>(KEY_PENDING_CAMERA_URI)
        savedStateHandle.remove<String>(KEY_PENDING_CAMERA_PATH)
        _uiState.update { it.copy(pendingCameraUri = null) }

        if (success && uri != null) {
            AppLogger.i(AppLogger.LogDomain.CAMERA, "Camera capture returned success. Loading bitmap from URI.")
            setImageUri(uri)
        } else {
            AppLogger.d(AppLogger.LogDomain.CAMERA, "Camera capture cancelled or failed. Cleaning up temp file.")
            if (pathString != null) {
                try {
                    val file = File(pathString)
                    if (file.exists() && file.length() == 0L) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    AppLogger.w(AppLogger.LogDomain.CAMERA, "Failed to clean up empty camera capture file: ${e.localizedMessage}")
                }
            }
        }
    }

    /**
     * Sets camera permission error states.
     */
    fun onCameraPermissionDenied(permanentlyDenied: Boolean) {
        val error = if (permanentlyDenied) {
            AppError.CameraError.PermissionPermanentlyDenied()
        } else {
            AppError.CameraError.PermissionDenied()
        }
        AppLogger.w(AppLogger.LogDomain.CAMERA, "Camera permission denied (permanently=$permanentlyDenied)")
        _uiState.update {
            it.copy(
                cameraError = error,
                acquisitionError = error.userMessage,
                appOperationState = OperationState.RecoverableError(error)
            )
        }
    }

    fun onCameraHardwareUnavailable() {
        val error = AppError.CameraError.HardwareUnavailable()
        AppLogger.w(AppLogger.LogDomain.CAMERA, "No camera hardware detected")
        _uiState.update {
            it.copy(
                cameraError = error,
                acquisitionError = error.userMessage,
                appOperationState = OperationState.FatalError(error)
            )
        }
    }

    /**
     * Resets acquisition state to live camera mode with zero active sample documents.
     */
    fun resetAcquisitionMode() {
        _uiState.update {
            it.copy(
                activeTitle = "",
                activeBitmap = null,
                activeImagePath = null,
                activeHintText = null,
                activeSummary = "",
                activeRawOcrText = "",
                activeFields = emptyList(),
                activeTables = emptyList(),
                activeDocType = DocumentType.GENERAL_DOCUMENT,
                activeConfidence = 0.0f,
                activeDiagnosticMessage = "",
                activePdfFile = null,
                activePdfPageCount = 1,
                activePdfFileName = null,
                activePageResults = emptyList(),
                activeDocId = 0,
                activeCreatedAt = null,
                isDocumentSaved = false,
                acquisitionError = null,
                cameraError = null,
                imageError = null,
                pdfError = null,
                pdfImportError = null,
                lastExportResult = null,
                exportError = null
            )
        }
        AppLogger.i(AppLogger.LogDomain.CAMERA, "Acquisition reset to pure live camera mode")
    }

    /**
     * Explicit Interactive Sample Demo (Runs on explicit user click, without preloading database).
     */
    fun selectSampleDocument(sample: SampleDocument) {
        val bitmap = sample.createRenderedBitmap()
        _uiState.update {
            it.copy(
                activeBitmap = bitmap,
                activeImagePath = null,
                activeHintText = sample.rawText,
                activeTitle = sample.title,
                activeDocType = sample.type,
                activeSummary = sample.summary,
                activeRawOcrText = sample.rawText,
                activeFields = sample.fields.map { f -> f.copy() },
                activeTables = sample.tables.map { t ->
                    ExtractedTable(
                        id = t.id,
                        name = t.name,
                        headers = t.headers.toMutableList(),
                        rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                        confidence = t.confidence
                    )
                },
                activeConfidence = sample.confidence,
                activeDocId = 0,
                activeCreatedAt = null,
                activePdfFile = null,
                activePdfPageCount = 1,
                activePdfFileName = null,
                activePageResults = emptyList(),
                isDocumentSaved = false,
                acquisitionError = null,
                cameraError = null,
                imageError = null,
                pdfError = null,
                pdfImportError = null,
                lastExportResult = null,
                exportError = null,
                appOperationState = OperationState.Success(sample.title),
                currentScreen = AppScreen.PREPROCESSING
            )
        }
    }

    fun setImageUri(uri: Uri) {
        _uiState.update { it.copy(appOperationState = OperationState.Loading("Decoding and loading document image...")) }
        val result = ImagePreprocessor.loadBitmapResultFromUri(getApplication(), uri)
        when (result) {
            is ImagePreprocessor.ImageLoadResult.Success -> {
                val cacheDir = File(getApplication<Application>().cacheDir, "documents")
                if (!cacheDir.exists()) cacheDir.mkdirs()
                val localFile = File(cacheDir, "active_doc_${System.currentTimeMillis()}.jpg")
                try {
                    FileOutputStream(localFile).use { out ->
                        result.bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    savedStateHandle[KEY_ACTIVE_IMAGE_PATH] = localFile.absolutePath
                } catch (e: Exception) {
                    AppLogger.w(AppLogger.LogDomain.IMAGE, "Failed to cache active working image: ${e.localizedMessage}")
                }

                _uiState.update {
                    it.copy(
                        activeBitmap = result.bitmap,
                        activeImagePath = localFile.absolutePath,
                        activeTitle = if (it.activeTitle.isBlank() || it.activeTitle == "Scanned Document") "Imported Image" else it.activeTitle,
                        activeHintText = null,
                        activeDocType = DocumentType.GENERAL_DOCUMENT,
                        activeSummary = "",
                        activeRawOcrText = "",
                        activeFields = emptyList(),
                        activeTables = emptyList(),
                        activeConfidence = 0.0f,
                        activeDiagnosticMessage = "",
                        activeDocId = 0,
                        activeCreatedAt = null,
                        activePdfFile = null,
                        activePdfPageCount = 1,
                        activePdfFileName = null,
                        activePageResults = emptyList(),
                        isDocumentSaved = false,
                        acquisitionError = null,
                        cameraError = null,
                        imageError = null,
                        pdfError = null,
                        pdfImportError = null,
                        lastExportResult = null,
                        exportError = null,
                        appOperationState = OperationState.Idle,
                        currentScreen = AppScreen.PREPROCESSING
                    )
                }
            }
            is ImagePreprocessor.ImageLoadResult.Failure -> {
                val appErr = result.error.toAppError()
                AppLogger.e(AppLogger.LogDomain.IMAGE, "Image import failed: ${appErr.userMessage}")
                _uiState.update {
                    it.copy(
                        acquisitionError = appErr.userMessage,
                        imageError = appErr,
                        appOperationState = OperationState.RecoverableError(appErr)
                    )
                }
            }
        }
    }

    fun setPdfUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pdfError = null,
                    pdfImportError = null,
                    acquisitionError = null,
                    appOperationState = OperationState.Loading("Inspecting and preparing PDF document...")
                )
            }
            val result = PdfDocumentRenderer.inspectAndPreparePdf(getApplication(), uri)
            when (result) {
                is PdfDocumentRenderer.PdfInspectionResult.Success -> {
                    savedStateHandle[KEY_ACTIVE_PDF_PATH] = result.cachedFile.absolutePath
                    _uiState.update {
                        it.copy(
                            activeBitmap = result.firstPageThumbnail,
                            activePdfFile = result.cachedFile,
                            activePdfPageCount = result.pageCount,
                            activePdfFileName = result.fileName,
                            activeTitle = if (result.pageCount > 1) {
                                "Imported PDF (${result.pageCount} Pages)"
                            } else {
                                "Imported PDF (1 Page)"
                            },
                            activeHintText = null,
                            activeDocType = DocumentType.GENERAL_DOCUMENT,
                            activeSummary = "",
                            activeRawOcrText = "",
                            activeFields = emptyList(),
                            activeTables = emptyList(),
                            activeConfidence = 0.0f,
                            activeDiagnosticMessage = "",
                            activeDocId = 0,
                            activeCreatedAt = null,
                            activePageResults = emptyList(),
                            isDocumentSaved = false,
                            pdfError = null,
                            pdfImportError = null,
                            acquisitionError = null,
                            lastExportResult = null,
                            exportError = null,
                            appOperationState = OperationState.Idle,
                            currentScreen = AppScreen.PREPROCESSING
                        )
                    }
                }
                is PdfDocumentRenderer.PdfInspectionResult.Error -> {
                    val appErr = result.error.toAppError()
                    AppLogger.e(AppLogger.LogDomain.PDF, "PDF import failed: ${appErr.userMessage}")
                    _uiState.update {
                        it.copy(
                            pdfError = appErr.userMessage,
                            pdfImportError = appErr,
                            acquisitionError = appErr.userMessage,
                            appOperationState = OperationState.RecoverableError(appErr)
                        )
                    }
                }
            }
        }
    }

    fun dismissAcquisitionError() {
        _uiState.update {
            it.copy(
                acquisitionError = null,
                cameraError = null,
                imageError = null,
                pdfError = null,
                pdfImportError = null,
                appOperationState = OperationState.Idle
            )
        }
    }

    fun dismissPdfError() {
        _uiState.update {
            it.copy(
                pdfError = null,
                pdfImportError = null,
                acquisitionError = null,
                appOperationState = OperationState.Idle
            )
        }
    }

    fun rotateActiveBitmap(degrees: Float) {
        val current = _uiState.value.activeBitmap ?: return
        val rotated = ImagePreprocessor.rotateBitmap(current, degrees)
        _uiState.update { it.copy(activeBitmap = rotated) }
    }

    fun updateProcessingOptions(options: ProcessingOptions) {
        _uiState.update { it.copy(processingOptions = options) }
    }

    fun cancelProcessing() {
        activeProcessingJob?.cancel()
        activeProcessingJob = null
        AppLogger.i(AppLogger.LogDomain.PIPELINE, "User cancelled document processing pipeline")
        _uiState.update {
            it.copy(
                processingProgress = ProcessingProgress(
                    stage = ProcessingStage.CANCELLED,
                    detailMessage = "Document extraction cancelled."
                ),
                appOperationState = OperationState.Cancelled("Document extraction was cancelled."),
                currentScreen = if (it.activeBitmap != null) AppScreen.PREPROCESSING else AppScreen.HOME
            )
        }
    }

    fun startProcessingPipeline() {
        val state = _uiState.value
        val bitmap = state.activeBitmap ?: return
        val pdfFile = state.activePdfFile

        activeProcessingJob?.cancel()

        // Explicit user filter selection can override, otherwise null so OCR/AI accurately classifies
        val forcedType = state.selectedDocTypeFilter

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PROCESSING,
                processingProgress = ProcessingProgress(stage = ProcessingStage.ACQUISITION),
                appOperationState = OperationState.Processing(stage = ProcessingStage.ACQUISITION, progressPercent = 15, currentStep = 1, totalSteps = 6, detailMessage = "Starting pipeline..."),
                // Clean slate for new processing run
                activeSummary = "",
                activeRawOcrText = "",
                activeFields = emptyList(),
                activeTables = emptyList(),
                activeConfidence = 0.0f,
                activeDiagnosticMessage = "",
                lastExportResult = null,
                exportError = null
            )
        }

        activeProcessingJob = viewModelScope.launch {
            val pipelineFlow = if (pdfFile != null && pdfFile.exists()) {
                ProcessingPipeline.runMultiPagePdfPipeline(
                    context = getApplication(),
                    pdfFile = pdfFile,
                    options = state.processingOptions,
                    forcedType = forcedType
                )
            } else {
                ProcessingPipeline.runPipeline(
                    context = getApplication(),
                    inputBitmap = bitmap,
                    options = state.processingOptions,
                    hintOcrText = state.activeHintText,
                    forcedType = forcedType
                )
            }

            pipelineFlow.collect { (progress, output) ->
                _uiState.update {
                    it.copy(
                        processingProgress = progress,
                        appOperationState = OperationState.Processing(
                            stage = progress.stage,
                            progressPercent = progress.stage.progressPercent,
                            currentStep = progress.currentStep,
                            totalSteps = progress.totalSteps,
                            detailMessage = progress.detailMessage
                        )
                    )
                }

                if (output != null) {
                    val ocrRes = output.ocrResult
                    val execRes = output.executionResult
                    val hasWarnings = ocrRes.qualityWarnings.isNotEmpty() || execRes.isOffline && state.processingOptions.enableCloudAi

                    val opState = if (hasWarnings) {
                        OperationState.PartialSuccess(
                            data = ocrRes,
                            warnings = ocrRes.qualityWarnings,
                            diagnosticMessage = execRes.diagnosticMessage
                        )
                    } else {
                        OperationState.Success(data = ocrRes, message = execRes.diagnosticMessage)
                    }

                    _uiState.update {
                        it.copy(
                            currentScreen = AppScreen.REVIEW_EDITOR,
                            activeImagePath = output.processedImagePath,
                            preprocessedBitmap = output.enhancedBitmap,
                            activeDocType = ocrRes.detectedDocType,
                            activeSummary = ocrRes.summary,
                            activeRawOcrText = ocrRes.rawText,
                            activeFields = ocrRes.fields,
                            activeTables = ocrRes.tables,
                            activeConfidence = ocrRes.overallConfidence,
                            activeEngineUsed = execRes.engineUsed.displayName,
                            activeDiagnosticMessage = execRes.diagnosticMessage,
                            activePdfPageCount = output.pageCount,
                            activePageResults = output.pageResults,
                            activeCreatedAt = null,
                            appOperationState = opState,
                            activeTitle = if (it.activeTitle.isNotBlank() &&
                                it.activeTitle != "Scanned Document" &&
                                it.activeTitle != "Imported Image" &&
                                !it.activeTitle.startsWith("Imported PDF")
                            ) {
                                it.activeTitle
                            } else {
                                val pageSuffix = if (output.pageCount > 1) " (${output.pageCount} pgs)" else ""
                                "${ocrRes.detectedDocType.displayName}$pageSuffix - ${java.text.SimpleDateFormat("MMM dd", java.util.Locale("en", "IN")).format(java.util.Date())}"
                            },
                            isDocumentSaved = false
                        )
                    }
                }
            }
        }
    }

    // --- Review & Editor State Mutations ---
    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(activeTitle = newTitle, isDocumentSaved = false) }
    }

    fun updateDocType(newType: DocumentType) {
        _uiState.update { it.copy(activeDocType = newType, isDocumentSaved = false) }
    }

    fun updateSummary(newSummary: String) {
        _uiState.update { it.copy(activeSummary = newSummary, isDocumentSaved = false) }
    }

    fun updateRawOcrText(newText: String) {
        _uiState.update { it.copy(activeRawOcrText = newText, isDocumentSaved = false) }
    }

    fun addField(key: String = "", value: String = "", category: String = "General") {
        val newField = ExtractedField(
            key = key,
            value = value,
            category = category,
            confidence = 1.0f,
            isUserEdited = true,
            lowConfidenceWarning = false,
            confidenceSource = ConfidenceSource.MEASURED
        )
        _uiState.update {
            it.copy(
                activeFields = it.activeFields + newField,
                isDocumentSaved = false
            )
        }
    }

    fun updateField(index: Int, key: String, value: String) {
        val currentFields = _uiState.value.activeFields.toMutableList()
        if (index in currentFields.indices) {
            val field = currentFields[index]
            currentFields[index] = field.copy(key = key, value = value, isUserEdited = true)
            _uiState.update { it.copy(activeFields = currentFields, isDocumentSaved = false) }
        }
    }

    fun updateField(fieldId: String, newKey: String, newValue: String, newCategory: String = "General") {
        _uiState.update { state ->
            val updated = state.activeFields.map { f ->
                if (f.id == fieldId) {
                    f.copy(
                        key = newKey,
                        value = newValue,
                        category = newCategory,
                        isUserEdited = true,
                        lowConfidenceWarning = false
                    )
                } else f
            }
            state.copy(activeFields = updated, isDocumentSaved = false)
        }
    }

    fun deleteField(index: Int) {
        val currentFields = _uiState.value.activeFields.toMutableList()
        if (index in currentFields.indices) {
            currentFields.removeAt(index)
            _uiState.update { it.copy(activeFields = currentFields, isDocumentSaved = false) }
        }
    }

    fun deleteField(fieldId: String) {
        _uiState.update { state ->
            state.copy(
                activeFields = state.activeFields.filterNot { it.id == fieldId },
                isDocumentSaved = false
            )
        }
    }

    fun addTable(name: String = "Table") {
        val newTable = ExtractedTable(
            name = name,
            headers = mutableListOf("Column 1", "Column 2"),
            rows = mutableListOf(mutableListOf("", ""))
        )
        _uiState.update {
            it.copy(
                activeTables = it.activeTables + newTable,
                isDocumentSaved = false
            )
        }
    }

    fun updateTableCell(tableIndex: Int, rowIndex: Int, colIndex: Int, value: String) {
        val currentTables = _uiState.value.activeTables.map { t ->
            ExtractedTable(
                id = t.id,
                name = t.name,
                headers = t.headers.toMutableList(),
                rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                confidence = t.confidence
            )
        }.toMutableList()

        if (tableIndex in currentTables.indices) {
            val table = currentTables[tableIndex]
            if (rowIndex in table.rows.indices && colIndex in table.rows[rowIndex].indices) {
                table.rows[rowIndex][colIndex] = value
                _uiState.update { it.copy(activeTables = currentTables, isDocumentSaved = false) }
            }
        }
    }

    fun updateTableCell(tableId: String, rowIndex: Int, colIndex: Int, value: String) {
        _uiState.update { state ->
            val updatedTables = state.activeTables.map { table ->
                if (table.id == tableId) {
                    val newRows = table.rows.mapIndexed { rIdx, row ->
                        if (rIdx == rowIndex) {
                            val newRow = row.toMutableList()
                            if (colIndex in newRow.indices) {
                                newRow[colIndex] = value
                            }
                            newRow
                        } else row
                    }.toMutableList()
                    ExtractedTable(
                        id = table.id,
                        name = table.name,
                        headers = table.headers,
                        rows = newRows,
                        confidence = table.confidence
                    )
                } else table
            }
            state.copy(activeTables = updatedTables, isDocumentSaved = false)
        }
    }

    fun addTableRow(tableIndex: Int) {
        val currentTables = _uiState.value.activeTables.map { t ->
            ExtractedTable(
                id = t.id,
                name = t.name,
                headers = t.headers.toMutableList(),
                rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                confidence = t.confidence
            )
        }.toMutableList()

        if (tableIndex in currentTables.indices) {
            val table = currentTables[tableIndex]
            val newRow = MutableList(table.headers.size) { "" }
            table.rows.add(newRow)
            _uiState.update { it.copy(activeTables = currentTables, isDocumentSaved = false) }
        }
    }

    fun deleteTableRow(tableIndex: Int, rowIndex: Int) {
        val currentTables = _uiState.value.activeTables.map { t ->
            ExtractedTable(
                id = t.id,
                name = t.name,
                headers = t.headers.toMutableList(),
                rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                confidence = t.confidence
            )
        }.toMutableList()

        if (tableIndex in currentTables.indices) {
            val table = currentTables[tableIndex]
            if (rowIndex in table.rows.indices) {
                table.rows.removeAt(rowIndex)
                _uiState.update { it.copy(activeTables = currentTables, isDocumentSaved = false) }
            }
        }
    }

    fun addTableColumn(tableIndex: Int, columnName: String) {
        val currentTables = _uiState.value.activeTables.map { t ->
            ExtractedTable(
                id = t.id,
                name = t.name,
                headers = t.headers.toMutableList(),
                rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                confidence = t.confidence
            )
        }.toMutableList()

        if (tableIndex in currentTables.indices) {
            val table = currentTables[tableIndex]
            table.headers.add(columnName.ifBlank { "Col ${table.headers.size + 1}" })
            table.rows.forEach { row -> row.add("") }
            _uiState.update { it.copy(activeTables = currentTables, isDocumentSaved = false) }
        }
    }

    fun updateTableName(tableIndex: Int, newName: String) {
        val currentTables = _uiState.value.activeTables.map { t ->
            ExtractedTable(
                id = t.id,
                name = t.name,
                headers = t.headers.toMutableList(),
                rows = t.rows.map { r -> r.toMutableList() }.toMutableList(),
                confidence = t.confidence
            )
        }.toMutableList()

        if (tableIndex in currentTables.indices) {
            currentTables[tableIndex].name = newName
            _uiState.update { it.copy(activeTables = currentTables, isDocumentSaved = false) }
        }
    }

    fun deleteTable(tableId: String) {
        _uiState.update { state ->
            state.copy(
                activeTables = state.activeTables.filterNot { it.id == tableId },
                isDocumentSaved = false
            )
        }
    }

    // --- Persistence & History ---
    fun saveActiveDocument() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                val entity = DocumentEntity.from(
                    id = state.activeDocId,
                    title = state.activeTitle.ifBlank { "${state.activeDocType.displayName} Extract" },
                    docType = state.activeDocType,
                    originalImagePath = state.activeImagePath,
                    summary = state.activeSummary,
                    rawOcrText = state.activeRawOcrText,
                    fields = state.activeFields,
                    tables = state.activeTables,
                    overallConfidence = state.activeConfidence,
                    createdAt = state.activeCreatedAt,
                    pageCount = state.activePdfPageCount
                )
                val savedId = repository.saveDocument(entity)
                _uiState.update {
                    it.copy(
                        activeDocId = if (state.activeDocId == 0L) savedId else state.activeDocId,
                        activeCreatedAt = state.activeCreatedAt ?: entity.createdAt,
                        isDocumentSaved = true,
                        saveMessage = "Document saved to local SQLite database!",
                        databaseError = null
                    )
                }
            } catch (e: Exception) {
                val dbErr = AppError.DatabaseError.WriteFailed(e.localizedMessage ?: "Database insert failure")
                AppLogger.e(AppLogger.LogDomain.DATABASE, "Error saving document: ${e.localizedMessage}", e)
                _uiState.update {
                    it.copy(
                        databaseError = dbErr,
                        saveMessage = dbErr.userMessage,
                        appOperationState = OperationState.RecoverableError(dbErr)
                    )
                }
            }
        }
    }

    fun reopenDocument(doc: DocumentEntity) {
        _uiState.update {
            it.copy(
                activeDocId = doc.id,
                activeTitle = doc.title,
                activeDocType = doc.getTypedDocType(),
                activeImagePath = if (doc.hasValidImageFile()) doc.originalImagePath else null,
                activeSummary = doc.summary,
                activeRawOcrText = doc.rawOcrText,
                activeFields = doc.getFieldsList(),
                activeTables = doc.getTablesList(),
                activeConfidence = doc.overallConfidence,
                activeCreatedAt = doc.createdAt,
                activePdfFile = null,
                activePdfPageCount = doc.pageCount,
                activePageResults = emptyList(),
                isDocumentSaved = true,
                databaseError = null,
                currentScreen = AppScreen.REVIEW_EDITOR
            )
        }
    }

    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch {
            try {
                repository.deleteDocument(doc)
                if (_uiState.value.activeDocId == doc.id) {
                    _uiState.update { it.copy(activeDocId = 0, isDocumentSaved = false) }
                }
            } catch (e: Exception) {
                val dbErr = AppError.DatabaseError.WriteFailed("Failed to delete document: ${e.localizedMessage}")
                AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to delete document: ${e.localizedMessage}", e)
                _uiState.update { it.copy(databaseError = dbErr) }
            }
        }
    }

    fun clearSavedDocuments() {
        viewModelScope.launch {
            try {
                repository.deleteAllDocuments()
                _uiState.update { it.copy(activeDocId = 0, isDocumentSaved = false) }
            } catch (e: Exception) {
                val dbErr = AppError.DatabaseError.WriteFailed("Failed to clear documents: ${e.localizedMessage}")
                AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to clear documents: ${e.localizedMessage}", e)
                _uiState.update { it.copy(databaseError = dbErr) }
            }
        }
    }

    // --- Export Operations ---
    fun setSelectedExportFormat(format: ExportFormat) {
        _uiState.update {
            it.copy(
                selectedExportFormat = format,
                exportError = null,
                exportAppError = null,
                sharingAppError = null
            )
        }
    }

    fun clearExportError() {
        _uiState.update {
            it.copy(
                exportError = null,
                exportAppError = null,
                sharingAppError = null
            )
        }
    }

    fun cancelExport() {
        activeExportJob?.cancel()
        activeExportJob = null
        AppLogger.i(AppLogger.LogDomain.EXPORT, "User cancelled document export")
        _uiState.update {
            it.copy(
                isExporting = false,
                exportError = "Export cancelled by user",
                appOperationState = OperationState.Cancelled("Export was cancelled.")
            )
        }
    }

    fun performExport(format: ExportFormat = _uiState.value.selectedExportFormat) {
        val state = _uiState.value
        val entity = DocumentEntity.from(
            id = state.activeDocId,
            title = state.activeTitle.ifBlank { "SnapData_Document" },
            docType = state.activeDocType,
            originalImagePath = state.activeImagePath,
            summary = state.activeSummary,
            rawOcrText = state.activeRawOcrText,
            fields = state.activeFields,
            tables = state.activeTables,
            overallConfidence = state.activeConfidence,
            createdAt = state.activeCreatedAt,
            pageCount = state.activePdfPageCount
        )

        activeExportJob?.cancel()
        _uiState.update {
            it.copy(
                isExporting = true,
                exportError = null,
                exportAppError = null,
                sharingAppError = null,
                appOperationState = OperationState.Loading("Formatting and generating ${format.displayName}...")
            )
        }

        activeExportJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val result = ExportManager.exportDocument(getApplication(), entity, format)
                _uiState.update {
                    it.copy(
                        lastExportResult = result,
                        selectedExportFormat = format,
                        isExporting = false,
                        exportError = null,
                        exportAppError = null,
                        appOperationState = OperationState.Success(result, "Export generated successfully (${result.file.name}).")
                    )
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                _uiState.update { it.copy(isExporting = false) }
            } catch (e: Exception) {
                val exportErr = AppError.ExportError.FormattingFailed(format.displayName, e.localizedMessage ?: "Export formatting failed")
                AppLogger.e(AppLogger.LogDomain.EXPORT, "Failed to export document: ${e.localizedMessage}", e)
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportError = exportErr.userMessage,
                        exportAppError = exportErr,
                        appOperationState = OperationState.RecoverableError(exportErr)
                    )
                }
            }
        }
    }

    fun shareExportFile() {
        val result = _uiState.value.lastExportResult
        if (result == null) {
            val err = AppError.SharingError.FileNotFound()
            _uiState.update {
                it.copy(
                    exportError = err.userMessage,
                    sharingAppError = err,
                    appOperationState = OperationState.RecoverableError(err)
                )
            }
            return
        }
        try {
            ExportManager.shareExportedFile(getApplication(), result)
            _uiState.update {
                it.copy(
                    sharingAppError = null,
                    exportError = null
                )
            }
        } catch (e: ActivityNotFoundException) {
            val shareErr = AppError.SharingError.NoCompatibleAppFound(result.mimeType, e.localizedMessage ?: "No app found")
            AppLogger.w(AppLogger.LogDomain.SHARING, "No compatible app for sharing: ${result.mimeType}", e)
            _uiState.update {
                it.copy(
                    exportError = shareErr.userMessage,
                    sharingAppError = shareErr,
                    appOperationState = OperationState.RecoverableError(shareErr)
                )
            }
        } catch (e: Exception) {
            val shareErr = AppError.SharingError.SecurityUriGrantFailed(e.localizedMessage ?: "Sharing permission failure")
            AppLogger.e(AppLogger.LogDomain.SHARING, "Failed to share exported file: ${e.localizedMessage}", e)
            _uiState.update {
                it.copy(
                    exportError = shareErr.userMessage,
                    sharingAppError = shareErr,
                    appOperationState = OperationState.RecoverableError(shareErr)
                )
            }
        }
    }

    fun setOcrLanguage(language: String) {
        _uiState.update { it.copy(selectedOcrLanguage = language) }
    }

    data class CacheStats(val fileCount: Int, val totalBytes: Long)
    data class CacheClearResult(val filesDeleted: Int, val bytesFreed: Long)

    fun getCacheStats(): CacheStats {
        var count = 0
        var bytes = 0L
        fun calc(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach { calc(it) }
            } else if (file.isFile) {
                count++
                bytes += file.length()
            }
        }
        try {
            val app = getApplication<Application>()
            app.cacheDir?.listFiles()?.forEach { calc(it) }
            app.externalCacheDir?.listFiles()?.forEach { calc(it) }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.STORAGE, "Cache calculation warning: ${e.localizedMessage}")
        }
        return CacheStats(count, bytes)
    }

    fun clearTemporaryCache(): CacheClearResult {
        var filesDeleted = 0
        var bytesFreed = 0L

        fun deleteRecursive(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach { child -> deleteRecursive(child) }
                file.delete()
            } else if (file.isFile) {
                val size = file.length()
                if (file.delete()) {
                    filesDeleted++
                    bytesFreed += size
                }
            }
        }

        try {
            val app = getApplication<Application>()
            app.cacheDir?.listFiles()?.forEach { child -> deleteRecursive(child) }
            app.externalCacheDir?.listFiles()?.forEach { child -> deleteRecursive(child) }
            AppLogger.i(AppLogger.LogDomain.STORAGE, "Cache cleared: $filesDeleted files, $bytesFreed bytes freed.")
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.STORAGE, "Error during cache deletion: ${e.localizedMessage}", e)
        }

        return CacheClearResult(filesDeleted, bytesFreed)
    }

    // -------------------------------------------------------------
    // Authentication API & State Management
    // -------------------------------------------------------------

    fun signIn(email: String, password: CharArray) {
        if (_uiState.value.isAuthLoading) return // Prevent duplicate submit
        _uiState.update { it.copy(isAuthLoading = true, authError = null) }
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            currentScreen = AppScreen.AUTH_SUCCESS
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error
                        )
                    }
                }
            }
        }
    }

    fun signUp(fullName: String, email: String, password: CharArray) {
        if (_uiState.value.isAuthLoading) return // Prevent duplicate submit
        _uiState.update { it.copy(isAuthLoading = true, authError = null) }
        viewModelScope.launch {
            val result = authRepository.signUp(fullName, email, password)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            currentScreen = AppScreen.VERIFY_EMAIL
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error
                        )
                    }
                }
            }
        }
    }

    fun continueAsGuest() {
        if (_uiState.value.isAuthLoading) return
        _uiState.update { it.copy(isAuthLoading = true, authError = null) }
        viewModelScope.launch {
            val result = authRepository.continueAsGuest()
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            currentScreen = AppScreen.HOME
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error
                        )
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update {
                it.copy(
                    authError = null,
                    isResetSent = false,
                    isResendSuccess = false,
                    currentScreen = AppScreen.AUTH_WELCOME
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (_uiState.value.isAuthLoading) return
        _uiState.update { it.copy(isAuthLoading = true, authError = null, isResetSent = false) }
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            isResetSent = true
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error,
                            isResetSent = false
                        )
                    }
                }
            }
        }
    }

    fun sendEmailVerification() {
        _uiState.update { it.copy(isAuthLoading = true, authError = null, isResendSuccess = false) }
        viewModelScope.launch {
            val result = authRepository.sendEmailVerification()
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            isResendSuccess = true
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error,
                            isResendSuccess = false
                        )
                    }
                }
            }
        }
    }

    fun checkEmailVerified() {
        _uiState.update { it.copy(isAuthLoading = true, authError = null) }
        viewModelScope.launch {
            val result = authRepository.checkEmailVerified()
            when (result) {
                is AuthResult.Success -> {
                    if (result.data) {
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = null,
                                currentScreen = AppScreen.AUTH_SUCCESS
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = AppAuthError.EmailNotVerified
                            )
                        }
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = result.error
                        )
                    }
                }
            }
        }
    }

    fun clearAuthError() {
        _uiState.update {
            it.copy(
                authError = null,
                isResetSent = false,
                isResendSuccess = false
            )
        }
    }

    // --- On-Device Offline AI Model Management ---
    fun downloadOfflineAiModel() {
        viewModelScope.launch {
            modelManager.downloadModel()
        }
    }

    fun cancelOfflineAiModelDownload() {
        modelManager.cancelDownload()
    }

    fun verifyOfflineAiModel() {
        viewModelScope.launch {
            modelManager.verifyInstalledModel()
        }
    }

    fun deleteOfflineAiModel() {
        viewModelScope.launch {
            OnDeviceNeuralDocumentAnalyzer.getInstance().unload()
            modelManager.deleteModel()
        }
    }
}

