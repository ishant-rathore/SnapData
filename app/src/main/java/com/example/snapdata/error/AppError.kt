package com.example.snapdata.error

/**
 * Unified error classification and domain architecture for SnapData.
 *
 * Covers all 9 application operations:
 * 1. CAMERA
 * 2. IMAGE_IMPORT
 * 3. PDF_IMPORT
 * 4. PREPROCESSING
 * 5. OCR
 * 6. AI
 * 7. DATABASE
 * 8. EXPORT
 * 9. SHARING
 */
enum class ErrorDomain(val displayName: String) {
    CAMERA("Camera Acquisition"),
    IMAGE_IMPORT("Image Import"),
    PDF_IMPORT("PDF Document Import"),
    PREPROCESSING("Image Preprocessing"),
    OCR("Optical Character Recognition"),
    AI("AI Cloud Analysis"),
    DATABASE("Local Database Storage"),
    EXPORT("Document Export"),
    SHARING("File Sharing")
}

sealed class AppError(
    val domain: ErrorDomain,
    val userMessage: String,
    val technicalDetails: String,
    val isRecoverable: Boolean = true,
    val isRetryable: Boolean = true,
    val suggestedAction: String? = null
) {
    // =========================================================================
    // 1. CAMERA ERRORS
    // =========================================================================
    sealed class CameraError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.CAMERA, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class HardwareUnavailable(detail: String = "No camera sensor or camera provider application found") : CameraError(
            userMessage = "No camera hardware or compatible camera app was detected on this device.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Import documents from photo gallery or PDF files instead."
        )

        class PermissionDenied(detail: String = "Camera runtime permission was denied") : CameraError(
            userMessage = "Camera permission is required to scan physical documents.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Tap to grant camera access permission."
        )

        class PermissionPermanentlyDenied(detail: String = "Camera permission permanently denied or Don't ask again selected") : CameraError(
            userMessage = "Camera access is disabled. Please enable Camera permissions in System App Settings to scan documents.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Open App Settings to grant Camera permission."
        )

        class StorageInitFailed(detail: String = "Failed to create temporary camera file provider URI") : CameraError(
            userMessage = "Unable to initialize temporary camera storage.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Check device storage space and retry."
        )

        class CaptureFailed(detail: String = "Camera capture cancelled or image write failed") : CameraError(
            userMessage = "Document photo capture was cancelled or failed to save.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Tap shutter button to try taking the photo again."
        )
    }

    // =========================================================================
    // 2. IMAGE IMPORT ERRORS
    // =========================================================================
    sealed class ImageImportError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.IMAGE_IMPORT, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class InaccessibleUri(detail: String = "ContentResolver could not open input stream for URI") : ImageImportError(
            userMessage = "Unable to access or read the selected image file.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Select the image again or choose a different photo."
        )

        class EmptyFile(detail: String = "Image stream contains 0 bytes") : ImageImportError(
            userMessage = "The selected image file contains no data (0 bytes).",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Please choose a valid non-empty image file."
        )

        class CorruptedImage(detail: String = "BitmapFactory failed to decode image header or pixels") : ImageImportError(
            userMessage = "The selected file is corrupted or in an unsupported image format.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Use standard JPG, PNG, or WEBP image formats."
        )

        class CorruptedFile(detail: String = "BitmapFactory failed to decode image header or pixels") : ImageImportError(
            userMessage = "The selected file is corrupted or in an unsupported image format.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Use standard JPG, PNG, or WEBP image formats."
        )


        class SecurityDenied(detail: String = "SecurityException accessing URI provider") : ImageImportError(
            userMessage = "Permission to read the image file was denied by system security.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Select the image again to refresh system permissions."
        )

        class OutOfMemory(detail: String = "OutOfMemoryError during bitmap allocation") : ImageImportError(
            userMessage = "The image resolution is too high for available device memory.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "The image was automatically downscaled for processing."
        )

        class UnsupportedFormat(detail: String = "MIME type is not a recognized bitmap format") : ImageImportError(
            userMessage = "The chosen file format is not supported for image OCR.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Import JPEG, PNG, WEBP images or PDF documents."
        )
    }

    // =========================================================================
    // 3. PDF IMPORT ERRORS
    // =========================================================================
    sealed class PdfImportError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.PDF_IMPORT, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class EncryptedOrPasswordProtected(detail: String = "PdfRenderer failed due to encryption/password protection") : PdfImportError(
            userMessage = "The PDF is password-protected or encrypted. Please unlock or decrypt the file before scanning.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Remove password protection from the PDF and re-import."
        )

        class InvalidPdfStructure(detail: String = "File does not contain valid %PDF header or cross-reference table") : PdfImportError(
            userMessage = "The file is corrupted or not a valid PDF document.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Select a valid PDF document."
        )

        class EmptyPdf(detail: String = "PDF contains 0 pages") : PdfImportError(
            userMessage = "The PDF contains 0 pages or no renderable content.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "Provide a PDF with at least one document page."
        )

        class SecurityDenied(detail: String = "SecurityException reading PDF content stream") : PdfImportError(
            userMessage = "Permission to read the PDF was denied by the system.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Select the PDF file again."
        )

        class MemoryExhausted(detail: String = "OutOfMemoryError during PDF rasterization") : PdfImportError(
            userMessage = "Insufficient device memory available to render high-resolution PDF pages.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Close other background apps and retry."
        )

        class NativeRendererFailed(val pageIndex: Int, detail: String = "PdfRenderer native rendering failure on page") : PdfImportError(
            userMessage = "Failed to render PDF page ${pageIndex + 1}.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Retry processing or convert PDF pages to images."
        )
    }

    // =========================================================================
    // 4. PREPROCESSING ERRORS
    // =========================================================================
    sealed class PreprocessingError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.PREPROCESSING, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class EnhancementFailed(detail: String = "ColorMatrix or Canvas filter failed") : PreprocessingError(
            userMessage = "Image enhancement filter failed. Continuing with original scan.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Original scan preserved."
        )

        class CropFailed(detail: String = "Auto-crop calculated invalid rectangle bounds") : PreprocessingError(
            userMessage = "Auto-crop could not determine document margins. Scan preserved without cropping.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Original image preserved."
        )

        class OutOfMemory(detail: String = "OutOfMemory during preprocessing bitmap allocation") : PreprocessingError(
            userMessage = "Memory exhausted during image filters. Retrying with basic processing.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Reduced filter resolution applied."
        )

        class RotationFailed(detail: String = "Matrix rotation failed") : PreprocessingError(
            userMessage = "Failed to rotate document image.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Try rotating the image again."
        )
    }

    // =========================================================================
    // 5. OCR ERRORS
    // =========================================================================
    sealed class OcrError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.OCR, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class EngineInitFailed(detail: String = "ML Kit TextRecognizer initialization exception") : OcrError(
            userMessage = "On-device OCR engine failed to initialize.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Restart the scanner and retry."
        )

        class NoTextDetected(detail: String = "ML Kit returned empty text tokens from bitmap") : OcrError(
            userMessage = "No legible text was detected in the document.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Ensure good lighting, sharp focus, and document is clearly visible."
        )

        class LowLegibility(detail: String = "High noise ratio or low confidence detected in OCR tokens") : OcrError(
            userMessage = "Document text has low legibility or contains significant optical noise.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Extracted data with partial confidence. You can edit fields manually."
        )

        class MemoryExhausted(detail: String = "ML Kit process crashed with native OutOfMemory") : OcrError(
            userMessage = "OCR memory capacity exceeded on high-resolution image.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Downscaling image and retrying OCR."
        )

        class RecognitionFailed(detail: String = "Generic ML Kit recognition failure") : OcrError(
            userMessage = "Optical Character Recognition failed on document text.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Try taking a clearer photo."
        )
    }

    // =========================================================================
    // 6. AI CLOUD ERRORS (Fail-safe: Always fall back to local OCR)
    // =========================================================================
    sealed class AiError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.AI, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class ApiKeyMissing(detail: String = "GEMINI_API_KEY is not configured") : AiError(
            userMessage = "Cloud AI key is not configured. Switched securely to 100% on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = false,
            suggestedAction = "Configure API key in settings or proceed with on-device extraction."
        )

        class InvalidApiKey(val statusCode: Int = 401, detail: String = "HTTP 401/403: Unauthorized API Key") : AiError(
            userMessage = "Cloud AI key is unauthorized or invalid. Recovered using on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = false,
            suggestedAction = "Verify your Gemini API key in settings."
        )

        class RateLimitExceeded(val statusCode: Int = 429, detail: String = "HTTP 429: Resource exhausted") : AiError(
            userMessage = "Cloud AI rate limit or quota exceeded. Recovered using fast on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "On-device OCR completed extraction."
        )

        class NetworkUnavailable(detail: String = "No network connectivity or host unreachable") : AiError(
            userMessage = "No internet connection available. Switched seamlessly to offline on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Offline OCR completed extraction."
        )

        class Timeout(val timeoutMs: Long = 30000, detail: String = "SocketTimeoutException") : AiError(
            userMessage = "Cloud AI request timed out. Recovered using on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "On-device OCR completed extraction."
        )

        class MalformedResponse(detail: String = "JSON syntax error or unexpected candidate payload") : AiError(
            userMessage = "AI cloud service returned an unexpected response. Recovered using on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "On-device OCR completed extraction."
        )

        class OversizedPayload(val sizeBytes: Int, detail: String = "Payload exceeded safe buffer limit") : AiError(
            userMessage = "AI response exceeded safe memory buffer. Recovered using on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = false,
            suggestedAction = "On-device OCR completed extraction."
        )

        class ServerError(val statusCode: Int, detail: String = "HTTP 500/502/503/504 error") : AiError(
            userMessage = "Cloud AI service encountered a temporary error ($statusCode). Recovered using on-device OCR.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "On-device OCR completed extraction."
        )
    }

    // =========================================================================
    // 7. DATABASE ERRORS
    // =========================================================================
    sealed class DatabaseError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.DATABASE, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class ReadFailed(detail: String = "SQLite query execution failure") : DatabaseError(
            userMessage = "Failed to load saved documents from local storage.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Pull down to refresh document archive."
        )

        class WriteFailed(detail: String = "SQLite insert/update failure") : DatabaseError(
            userMessage = "Failed to save document to local database.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Check device storage space and retry saving."
        )

        class NotFound(val docId: Long, detail: String = "Document entity not found") : DatabaseError(
            userMessage = "Document #$docId was not found in local database.",
            technicalDetails = detail,
            isRecoverable = false,
            isRetryable = false,
            suggestedAction = "The document may have already been deleted."
        )

        class CorruptedData(detail: String = "JSON schema parsing failed on stored fields/tables") : DatabaseError(
            userMessage = "Document metadata was partially corrupted. Recovered basic text and summary.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = false,
            suggestedAction = "Review and re-save document fields."
        )
    }

    // =========================================================================
    // 8. EXPORT ERRORS
    // =========================================================================
    sealed class ExportError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.EXPORT, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class StorageUnavailable(detail: String = "Failed to create export cache directory") : ExportError(
            userMessage = "Unable to create export file in device storage.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Check available disk storage and retry."
        )

        class FormattingFailed(val formatName: String, detail: String = "Formatting exception during file serialization") : ExportError(
            userMessage = "Failed to format document as $formatName.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Try exporting in CSV or JSON format."
        )

        class EmptyOutput(detail: String = "Export generated a 0-byte file") : ExportError(
            userMessage = "Export produced an empty file. Please verify document contents.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Add at least one key-value field or table and retry export."
        )

        class EncodingError(detail: String = "Character encoding failure during export") : ExportError(
            userMessage = "Character encoding failed during document export.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Try exporting as JSON or CSV with UTF-8."
        )
    }

    // =========================================================================
    // 9. SHARING ERRORS
    // =========================================================================
    sealed class SharingError(
        userMessage: String,
        technicalDetails: String,
        isRecoverable: Boolean = true,
        isRetryable: Boolean = true,
        suggestedAction: String? = null
    ) : AppError(ErrorDomain.SHARING, userMessage, technicalDetails, isRecoverable, isRetryable, suggestedAction) {

        class FileNotFound(detail: String = "Export file does not exist on disk or was cleared") : SharingError(
            userMessage = "No exported file found to share. Please generate an export first.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Tap 'Export' to generate a fresh file before sharing."
        )

        class SecurityUriGrantFailed(detail: String = "FileProvider failed to generate content URI") : SharingError(
            userMessage = "System security failed to grant file sharing access.",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = true,
            suggestedAction = "Try re-exporting the document."
        )

        class NoCompatibleAppFound(val mimeType: String, detail: String = "ActivityNotFoundException for Intent.ACTION_SEND") : SharingError(
            userMessage = "No installed application was found on this device to share or open this file type ($mimeType).",
            technicalDetails = detail,
            isRecoverable = true,
            isRetryable = false,
            suggestedAction = "Install a compatible viewer app or choose CSV/JSON export."
        )
    }
}
