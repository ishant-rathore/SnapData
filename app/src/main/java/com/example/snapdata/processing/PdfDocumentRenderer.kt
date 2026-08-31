package com.example.snapdata.processing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.snapdata.error.AppError
import com.example.snapdata.logging.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Robust, production-grade PDF inspection and memory-safe page renderer.
 *
 * Implements:
 * - Precise error classification (Encrypted, Invalid, Empty, Unsupported, Rendering/OCR failures)
 * - Single-page memory allocation per render cycle with immediate recycling to prevent OOM
 * - Scaled rendering with bounded maximum dimensions for high-resolution documents
 * - Safe resource cleanup ensuring ParcelFileDescriptor and PdfRenderer are never leaked
 */
object PdfDocumentRenderer {

    const val MAX_SUPPORTED_PAGES_DEFAULT = 50
    const val MAX_RENDER_DIMENSION = 1800 // px max width/height to bound bitmap allocation

    /**
     * Clear, strongly-typed error hierarchy for all PDF failure modes.
     */
    sealed class PdfError(val userMessage: String, val technicalDetail: String? = null) {
        data class InvalidPdf(val detail: String? = "The file is corrupted or not a valid PDF document.") :
            PdfError("The file is corrupted or not a valid PDF document.", detail)

        data class EmptyPdf(val detail: String? = "The PDF contains 0 pages or no renderable content.") :
            PdfError("The PDF contains 0 pages or no renderable content.", detail)

        data class EncryptedPdf(val detail: String? = "The PDF is password-protected or encrypted. Please unlock or decrypt the file before processing.") :
            PdfError("The PDF is password-protected or encrypted. Please unlock or decrypt the file before processing.", detail)

        data class UnsupportedPdf(val detail: String? = "The PDF format or feature is not supported by the system renderer.") :
            PdfError("The PDF format or feature is not supported by the system renderer.", detail)

        data class RenderingFailure(val pageIndex: Int, val reason: String = "Failed to render PDF page ${pageIndex + 1}.", val detail: String? = reason) :
            PdfError("Failed to render PDF page ${pageIndex + 1}.", detail)

        data class OcrFailure(val pageIndex: Int? = null, val reason: String = "Optical Character Recognition failed on document text.", val detail: String? = reason) :
            PdfError("Optical Character Recognition failed on document text.", detail)

        data class FileAccessError(val detail: String? = "Unable to access or read the selected PDF file.") :
            PdfError("Unable to access or read the selected PDF file.", detail)

        data class MemoryError(val detail: String? = "Insufficient memory available to render high-resolution PDF pages.") :
            PdfError("Insufficient memory available to render high-resolution PDF pages.", detail)

        data class Cancellation(val detail: String? = "PDF document processing was cancelled.") :
            PdfError("PDF document processing was cancelled.", detail)

        fun toAppError(): AppError.PdfImportError {
            return when (this) {
                is EncryptedPdf -> AppError.PdfImportError.EncryptedOrPasswordProtected(technicalDetail ?: userMessage)
                is InvalidPdf -> AppError.PdfImportError.InvalidPdfStructure(technicalDetail ?: userMessage)
                is EmptyPdf -> AppError.PdfImportError.EmptyPdf(technicalDetail ?: userMessage)
                is FileAccessError -> AppError.PdfImportError.SecurityDenied(technicalDetail ?: userMessage)
                is MemoryError -> AppError.PdfImportError.MemoryExhausted(technicalDetail ?: userMessage)
                is RenderingFailure -> AppError.PdfImportError.NativeRendererFailed(pageIndex, technicalDetail ?: userMessage)
                else -> AppError.PdfImportError.InvalidPdfStructure(technicalDetail ?: userMessage)
            }
        }
    }

    /**
     * Outcome of inspecting a PDF before pipeline execution.
     */
    sealed class PdfInspectionResult {
        data class Success(
            val cachedFile: File,
            val pageCount: Int,
            val firstPageThumbnail: Bitmap,
            val fileSize: Long,
            val fileName: String
        ) : PdfInspectionResult()

        data class Error(val error: PdfError) : PdfInspectionResult()
    }

    /**
     * Single rendered page container.
     */
    data class PageRender(
        val pageIndex: Int,
        val bitmap: Bitmap,
        val width: Int,
        val height: Int
    )

    /**
     * Inspects and validates a PDF from a content Uri or File.
     * Copies content to a safe temporary cache file, verifies PDF header,
     * checks for encryption, counts pages, and extracts the first-page thumbnail.
     */
    suspend fun inspectAndPreparePdf(context: Context, uri: Uri): PdfInspectionResult = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, "pdf_staging")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val tempFile = File(cacheDir, "pdf_${System.currentTimeMillis()}_${(1000..9999).random()}.pdf")
        AppLogger.d(AppLogger.LogDomain.PDF, "Inspecting PDF from URI: $uri staged to ${tempFile.name}")

        try {
            // 1. Copy stream to cache file
            val copied = context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: 0L

            if (copied <= 0L || !tempFile.exists() || tempFile.length() == 0L) {
                tempFile.delete()
                AppLogger.w(AppLogger.LogDomain.PDF, "PDF file is empty (0 bytes).")
                return@withContext PdfInspectionResult.Error(PdfError.EmptyPdf("The selected PDF file is empty (0 bytes)."))
            }

            // 2. Validate PDF magic bytes (%PDF-)
            val isPdfHeader = validatePdfHeader(tempFile)
            if (!isPdfHeader) {
                tempFile.delete()
                AppLogger.w(AppLogger.LogDomain.PDF, "File does not contain valid %PDF- magic header.")
                return@withContext PdfInspectionResult.Error(
                    PdfError.InvalidPdf("The selected file does not appear to be a valid PDF document (missing %PDF- header).")
                )
            }

            // 3. Attempt to open with PdfRenderer to inspect page count and test encryption
            var pfd: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null
            try {
                pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
            } catch (sec: SecurityException) {
                pfd?.close()
                tempFile.delete()
                AppLogger.w(AppLogger.LogDomain.PDF, "Encrypted / password-protected PDF detected", sec)
                return@withContext PdfInspectionResult.Error(
                    PdfError.EncryptedPdf("The PDF is password-protected or encrypted. Please remove password encryption before scanning.")
                )
            } catch (ioe: IOException) {
                pfd?.close()
                tempFile.delete()
                val msg = ioe.message.orEmpty()
                AppLogger.w(AppLogger.LogDomain.PDF, "IOException opening PDF renderer: $msg", ioe)
                return@withContext if (msg.contains("password", ignoreCase = true) || msg.contains("encrypt", ignoreCase = true)) {
                    PdfInspectionResult.Error(PdfError.EncryptedPdf("The PDF is password-protected or encrypted."))
                } else {
                    PdfInspectionResult.Error(PdfError.InvalidPdf("Failed to parse PDF document structure: ${ioe.localizedMessage}"))
                }
            } catch (iae: IllegalArgumentException) {
                pfd?.close()
                tempFile.delete()
                AppLogger.w(AppLogger.LogDomain.PDF, "Corrupted or invalid PDF: ${iae.localizedMessage}", iae)
                return@withContext PdfInspectionResult.Error(
                    PdfError.InvalidPdf("Corrupted or invalid PDF format: ${iae.localizedMessage}")
                )
            }

            val pageCount = renderer.pageCount
            if (pageCount <= 0) {
                renderer.close()
                pfd.close()
                tempFile.delete()
                AppLogger.w(AppLogger.LogDomain.PDF, "PDF contains 0 pages.")
                return@withContext PdfInspectionResult.Error(PdfError.EmptyPdf("The PDF document contains 0 pages."))
            }

            // 4. Render first page thumbnail (lightweight render)
            val thumbnailBitmap = try {
                renderSinglePage(renderer, pageIndex = 0, maxDimension = 1000, scale = 1.5f)
            } catch (e: Exception) {
                renderer.close()
                pfd.close()
                tempFile.delete()
                AppLogger.e(AppLogger.LogDomain.PDF, "Failed to render cover preview: ${e.localizedMessage}", e)
                return@withContext PdfInspectionResult.Error(
                    PdfError.RenderingFailure(0, "Failed to render cover preview of PDF: ${e.localizedMessage}")
                )
            }

            renderer.close()
            pfd.close()

            AppLogger.i(AppLogger.LogDomain.PDF, "PDF inspection successful: $pageCount pages, ${tempFile.length()} bytes")

            PdfInspectionResult.Success(
                cachedFile = tempFile,
                pageCount = pageCount,
                firstPageThumbnail = thumbnailBitmap,
                fileSize = tempFile.length(),
                fileName = getFileName(context, uri) ?: "document.pdf"
            )
        } catch (e: Exception) {
            tempFile.delete()
            AppLogger.e(AppLogger.LogDomain.PDF, "Unexpected error inspecting PDF: ${e.localizedMessage}", e)
            PdfInspectionResult.Error(
                PdfError.FileAccessError("An unexpected error occurred while reading PDF: ${e.localizedMessage}")
            )
        }
    }

    /**
     * Memory-safely renders a single page of a PDF with dimension bounds.
     * Fills the background with white to guarantee high OCR contrast.
     */
    fun renderSinglePage(
        renderer: PdfRenderer,
        pageIndex: Int,
        maxDimension: Int = MAX_RENDER_DIMENSION,
        scale: Float = 2.0f
    ): Bitmap {
        val page = renderer.openPage(pageIndex)
        try {
            var targetWidth = (page.width * scale).toInt()
            var targetHeight = (page.height * scale).toInt()

            // Clamp max dimension to prevent out-of-memory errors on oversized engineering/CAD drawings
            if (targetWidth > maxDimension || targetHeight > maxDimension) {
                val ratio = minOf(
                    maxDimension.toFloat() / targetWidth.coerceAtLeast(1),
                    maxDimension.toFloat() / targetHeight.coerceAtLeast(1)
                )
                targetWidth = (targetWidth * ratio).toInt().coerceAtLeast(120)
                targetHeight = (targetHeight * ratio).toInt().coerceAtLeast(120)
            }

            val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
            return bitmap
        } finally {
            page.close()
        }
    }

    /**
     * Opens a PdfRenderer session for streaming page-by-page rendering.
     * Caller MUST close the returned session when finished.
     */
    class PdfSession(
        val pfd: ParcelFileDescriptor,
        val renderer: PdfRenderer,
        val pageCount: Int
    ) : AutoCloseable {
        override fun close() {
            try {
                renderer.close()
            } catch (_: Exception) {}
            try {
                pfd.close()
            } catch (_: Exception) {}
        }
    }

    fun openSession(file: File): PdfSession {
        if (!file.exists() || file.length() == 0L) {
            throw IllegalArgumentException("PDF file is empty or missing")
        }
        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = try {
            PdfRenderer(pfd)
        } catch (e: Exception) {
            pfd.close()
            throw e
        }
        return PdfSession(pfd, renderer, renderer.pageCount)
    }

    /**
     * Checks if the first bytes match the PDF specification magic header.
     */
    private fun validatePdfHeader(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(1024)
                val read = fis.read(buffer)
                if (read < 4) return false
                val headerStr = String(buffer, 0, read)
                headerStr.contains("%PDF-")
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) name = it.getString(idx)
                }
            }
        }
        if (name == null) {
            name = uri.path?.let { File(it).name }
        }
        return name
    }

    /**
     * Legacy single-page helper kept for backwards compatibility.
     */
    data class PdfRenderResult(
        val bitmap: Bitmap,
        val pageCount: Int,
        val currentPage: Int = 1
    )

    fun renderPdfFirstPage(context: Context, uri: Uri): PdfRenderResult? {
        val cacheFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            val pfd = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val pageCount = renderer.pageCount

            if (pageCount == 0) {
                renderer.close()
                pfd.close()
                cacheFile.delete()
                return null
            }

            val bitmap = renderSinglePage(renderer, 0, MAX_RENDER_DIMENSION)
            renderer.close()
            pfd.close()
            cacheFile.delete()

            PdfRenderResult(
                bitmap = bitmap,
                pageCount = pageCount,
                currentPage = 1
            )
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.PDF, "renderPdfFirstPage failed: ${e.localizedMessage}", e)
            cacheFile.delete()
            null
        }
    }
}
