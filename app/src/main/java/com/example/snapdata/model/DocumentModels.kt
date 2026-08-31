package com.example.snapdata.model

import kotlinx.serialization.Serializable

@Serializable
enum class DocumentType(val displayName: String, val category: String) {
    INVOICE("Invoice", "Financial"),
    RECEIPT("Receipt", "Financial"),
    BANK_STATEMENT("Bank Statement", "Financial"),
    FORM("Application Form", "Administrative"),
    CERTIFICATE("Certificate", "Academic/Legal"),
    MARK_SHEET("Mark Sheet / Transcript", "Academic"),
    ID_CARD("Identity Card", "Identity"),
    BUSINESS_CARD("Business Card", "Contact"),
    TABLE("Data Table / Matrix", "Tabular"),
    GENERAL_DOCUMENT("General Document", "General")
}

@Serializable
enum class ConfidenceSource(val displayName: String) {
    MEASURED("Measured Engine Score"),
    HEURISTIC("Calculated Heuristic Score"),
    HYBRID("Hybrid (Measured + Layout Analysis)")
}

@Serializable
data class ExtractedField(
    val id: String = java.util.UUID.randomUUID().toString(),
    var key: String,
    var value: String,
    val confidence: Float = 0.95f,
    val category: String = "General",
    var isUserEdited: Boolean = false,
    val lowConfidenceWarning: Boolean = confidence < 0.70f,
    val confidenceSource: ConfidenceSource = ConfidenceSource.HEURISTIC
)

@Serializable
data class ExtractedTable(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String = "Table",
    val headers: MutableList<String> = mutableListOf(),
    val rows: MutableList<MutableList<String>> = mutableListOf(),
    val confidence: Float = 0.92f,
    val confidenceSource: ConfidenceSource = ConfidenceSource.HEURISTIC
)

@Serializable
enum class ProcessingStage(val title: String, val description: String, val progressPercent: Int) {
    IDLE("Ready", "Waiting for document input", 0),
    ACQUISITION("Acquiring Document", "Loading image and verifying resolution", 15),
    PREPROCESSING("Preprocessing & Enhancing", "Auto-crop, contrast enhancement & binarization", 30),
    OCR("OCR Text Recognition", "Extracting character bounding boxes and word sequences", 50),
    AI_ANALYSIS("AI Document Analysis", "Analyzing layout semantics and document type", 70),
    STRUCTURED_EXTRACTION("Extracting Structured Fields", "Parsing key-value pairs and metadata", 85),
    TABLE_DETECTION("Table Matrix Reconstruction", "Aligning rows, columns, and numeric cells", 95),
    VALIDATION("Schema & Confidence Verification", "Finalizing structured JSON payload", 100),
    COMPLETED("Extraction Complete", "Document is ready for review and export", 100),
    ERROR("Processing Error", "Failed to complete extraction", 0),
    CANCELLED("Processing Cancelled", "Operation cancelled by user", 0)
}

@Serializable
data class ProcessingOptions(
    val ocrLanguage: String = "English (en)",
    val enhanceContrast: Boolean = true,
    val autoCrop: Boolean = true,
    val autoRotate: Boolean = true,
    val removeShadows: Boolean = true,
    val binarize: Boolean = false,
    val deskew: Boolean = true,
    val forceOfflineAi: Boolean = true,
    val enableCloudAi: Boolean = false
)

enum class ExportFormat(val extension: String, val mimeType: String, val displayName: String) {
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel Workbook (.xlsx)"),
    CSV("csv", "text/csv", "Comma-Separated Values (.csv)"),
    JSON("json", "application/json", "Structured JSON (.json)"),
    PDF("pdf", "application/pdf", "Digitized PDF Report (.pdf)")
}

data class ProcessingProgress(
    val stage: ProcessingStage = ProcessingStage.IDLE,
    val currentStep: Int = 0,
    val totalSteps: Int = 6,
    val detailMessage: String = "",
    val error: String? = null
)

/**
 * Production-grade application operation state machine for SnapData.
 *
 * Explicitly represents all 8 required application states:
 * 1. Idle
 * 2. Loading
 * 3. Processing
 * 4. Success
 * 5. PartialSuccess
 * 6. RecoverableError
 * 7. FatalError
 * 8. Cancelled
 */
sealed class OperationState<out T> {
    data object Idle : OperationState<Nothing>()

    data class Loading(
        val message: String = "Loading..."
    ) : OperationState<Nothing>()

    data class Processing(
        val stage: ProcessingStage = ProcessingStage.ACQUISITION,
        val progressPercent: Int = 0,
        val currentStep: Int = 0,
        val totalSteps: Int = 6,
        val detailMessage: String = ""
    ) : OperationState<Nothing>()

    data class Success<out T>(
        val data: T,
        val message: String = "Operation completed successfully."
    ) : OperationState<T>()

    data class PartialSuccess<out T>(
        val data: T,
        val warnings: List<String> = emptyList(),
        val diagnosticMessage: String = "Operation completed with fallback or quality warnings."
    ) : OperationState<T>()

    data class RecoverableError(
        val error: com.example.snapdata.error.AppError,
        val userMessage: String = error.userMessage,
        val suggestedAction: String? = error.suggestedAction,
        val preservedData: Any? = null
    ) : OperationState<Nothing>()

    data class FatalError(
        val error: com.example.snapdata.error.AppError,
        val userMessage: String = error.userMessage,
        val technicalDetails: String = error.technicalDetails
    ) : OperationState<Nothing>()

    data class Cancelled(
        val message: String = "Operation was cancelled."
    ) : OperationState<Nothing>()
}

