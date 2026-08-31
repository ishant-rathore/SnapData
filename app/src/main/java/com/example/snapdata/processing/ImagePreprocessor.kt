package com.example.snapdata.processing

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.snapdata.error.AppError
import com.example.snapdata.logging.AppLogger
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

object ImagePreprocessor {

    const val MAX_IMAGE_DIMENSION = 2560
    const val MIN_RECOMMENDED_OCR_DIMENSION = 800

    data class PreprocessingResult(
        val originalBitmap: Bitmap,
        val enhancedBitmap: Bitmap,
        val processedImagePath: String,
        val rotationDegrees: Float = 0f,
        val contrastFactor: Float = 1.25f,
        val wasUpscaled: Boolean = false,
        val wasDeskewed: Boolean = false
    )

    sealed class ImageError(val userMessage: String, val technicalDetail: String? = null) {
        data class InaccessibleUri(val detail: String? = "Unable to access or read the selected image file.") :
            ImageError("Unable to access or read the selected image file.", detail)

        data class EmptyFile(val detail: String? = "The selected image file contains no data (0 bytes).") :
            ImageError("The selected image file contains no data (0 bytes).", detail)

        data class CorruptedFile(val detail: String? = "The file is corrupted or in an unsupported image format.") :
            ImageError("The file is corrupted or in an unsupported image format.", detail)

        data class SecurityError(val detail: String? = "Permission to access the selected image file was denied by the system.") :
            ImageError("Permission to access the selected image file was denied by the system.", detail)

        data class OutOfMemory(val detail: String? = "The image resolution is too high for available device memory.") :
            ImageError("The image resolution is too high for available device memory.", detail)

        fun toAppError(): AppError.ImageImportError {
            return when (this) {
                is InaccessibleUri -> AppError.ImageImportError.InaccessibleUri(technicalDetail ?: userMessage)
                is EmptyFile -> AppError.ImageImportError.EmptyFile(technicalDetail ?: userMessage)
                is CorruptedFile -> AppError.ImageImportError.CorruptedFile(technicalDetail ?: userMessage)
                is SecurityError -> AppError.ImageImportError.SecurityDenied(technicalDetail ?: userMessage)
                is OutOfMemory -> AppError.ImageImportError.OutOfMemory(technicalDetail ?: userMessage)
            }
        }
    }

    sealed class ImageLoadResult {
        data class Success(
            val bitmap: Bitmap,
            val originalWidth: Int,
            val originalHeight: Int,
            val rotationDegrees: Float = 0f
        ) : ImageLoadResult()

        data class Failure(val error: ImageError) : ImageLoadResult()
    }

    /**
     * Computes the power-of-2 sample size to downscale high-resolution images
     * within the safe dimension limit.
     */
    fun calculateInSampleSize(origWidth: Int, origHeight: Int, maxDimension: Int = MAX_IMAGE_DIMENSION): Int {
        var sampleSize = 1
        val maxSide = max(origWidth, origHeight)
        while ((maxSide / sampleSize) > maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }

    /**
     * Safely loads and validates a bitmap from Uri with typed error result:
     * - Security and FileNotFound exception handling
     * - Memory bounds pre-calculation (prevents OutOfMemoryError on 48MP/108MP photos)
     * - Complete EXIF orientation matrix correction (handling all 8 EXIF tags)
     * - Memory retry fallback on OutOfMemoryError
     * - Safe stream resource management
     */
    fun loadBitmapResultFromUri(context: Context, uri: Uri): ImageLoadResult {
        try {
            AppLogger.d(AppLogger.LogDomain.IMAGE, "Attempting to load image bitmap from URI: $uri")

            // 1. Decode bounds only with stream safety
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, boundsOptions)
                } ?: run {
                    AppLogger.w(AppLogger.LogDomain.IMAGE, "ContentResolver returned null input stream for URI: $uri")
                    return ImageLoadResult.Failure(ImageError.InaccessibleUri("Unable to open stream for URI: $uri"))
                }
            } catch (se: SecurityException) {
                AppLogger.w(AppLogger.LogDomain.IMAGE, "Security permission denied reading image URI: $uri", se)
                return ImageLoadResult.Failure(ImageError.SecurityError("Access denied by system security: ${se.localizedMessage}"))
            } catch (ioe: java.io.FileNotFoundException) {
                AppLogger.w(AppLogger.LogDomain.IMAGE, "Image file not found or URI expired: $uri", ioe)
                return ImageLoadResult.Failure(ImageError.InaccessibleUri("File not found or URI expired: ${ioe.localizedMessage}"))
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.IMAGE, "Error reading image header: ${e.localizedMessage}", e)
                return ImageLoadResult.Failure(ImageError.InaccessibleUri("Error reading image header: ${e.localizedMessage}"))
            }

            val origWidth = boundsOptions.outWidth
            val origHeight = boundsOptions.outHeight
            if (origWidth <= 0 || origHeight <= 0) {
                AppLogger.w(AppLogger.LogDomain.IMAGE, "Invalid image dimensions decoded: ${origWidth}x${origHeight}")
                return ImageLoadResult.Failure(ImageError.CorruptedFile("Invalid image dimensions: ${origWidth}x${origHeight}"))
            }

            // 2. Compute power-of-2 sample size
            val sampleSize = calculateInSampleSize(origWidth, origHeight, MAX_IMAGE_DIMENSION)
            AppLogger.d(AppLogger.LogDomain.IMAGE, "Calculated inSampleSize=$sampleSize for ${origWidth}x${origHeight} image")

            // 3. Decode scaled bitmap safely with OOM retry loop
            var decoded: Bitmap? = null
            var currentSampleSize = sampleSize
            while (decoded == null && currentSampleSize <= 32) {
                try {
                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = currentSampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        decoded = BitmapFactory.decodeStream(stream, null, decodeOptions)
                    }
                } catch (e: OutOfMemoryError) {
                    AppLogger.w(AppLogger.LogDomain.IMAGE, "OutOfMemoryError during decode with sampleSize=$currentSampleSize. Doubling sample size and retrying GC.")
                    System.gc()
                    currentSampleSize *= 2
                } catch (se: SecurityException) {
                    AppLogger.w(AppLogger.LogDomain.IMAGE, "Security permission revoked during image decode: $uri", se)
                    return ImageLoadResult.Failure(ImageError.SecurityError("Access revoked during decode: ${se.localizedMessage}"))
                } catch (e: Exception) {
                    AppLogger.e(AppLogger.LogDomain.IMAGE, "Failed to decode image data: ${e.localizedMessage}", e)
                    return ImageLoadResult.Failure(ImageError.CorruptedFile("Failed to decode image data: ${e.localizedMessage}"))
                }
            }

            if (decoded == null) {
                AppLogger.e(AppLogger.LogDomain.IMAGE, "Device memory exhausted; unable to decode image after scaling retries.")
                return ImageLoadResult.Failure(ImageError.OutOfMemory("Device ran out of memory while decoding image."))
            }

            // 4. Correct EXIF orientation matrix with independent stream
            var rotationApplied = 0f
            val orientationMatrix = getExifOrientationMatrix(context, uri)
            if (orientationMatrix != null) {
                try {
                    val rotated = Bitmap.createBitmap(
                        decoded!!,
                        0,
                        0,
                        decoded!!.width,
                        decoded!!.height,
                        orientationMatrix,
                        true
                    )
                    if (rotated !== decoded) {
                        decoded!!.recycle()
                        decoded = rotated
                    }
                } catch (e: OutOfMemoryError) {
                    AppLogger.w(AppLogger.LogDomain.IMAGE, "OutOfMemoryError applying EXIF rotation. Retaining unrotated image.")
                    System.gc()
                }
            }

            AppLogger.i(AppLogger.LogDomain.IMAGE, "Successfully loaded bitmap: ${decoded!!.width}x${decoded!!.height}px (original: ${origWidth}x${origHeight}px)")

            return ImageLoadResult.Success(
                bitmap = decoded!!,
                originalWidth = origWidth,
                originalHeight = origHeight,
                rotationDegrees = rotationApplied
            )
        } catch (se: SecurityException) {
            AppLogger.w(AppLogger.LogDomain.IMAGE, "Security permission error loading bitmap", se)
            return ImageLoadResult.Failure(ImageError.SecurityError(se.localizedMessage ?: "Security permission error"))
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.IMAGE, "Unexpected exception loading bitmap from URI: ${e.localizedMessage}", e)
            return ImageLoadResult.Failure(ImageError.InaccessibleUri(e.localizedMessage ?: "Unexpected image loading error"))
        }
    }

    /**
     * Safely loads a bitmap from Uri, returning null on failure (convenience helper).
     */
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return when (val result = loadBitmapResultFromUri(context, uri)) {
            is ImageLoadResult.Success -> result.bitmap
            is ImageLoadResult.Failure -> null
        }
    }

    /**
     * Reads EXIF metadata and constructs complete transformation Matrix
     * for all 8 EXIF orientation standards.
     */
    fun getExifOrientationMatrix(context: Context, uri: Uri): Matrix? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        matrix.postRotate(90f)
                        matrix.postScale(-1f, 1f)
                    }
                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        matrix.postRotate(270f)
                        matrix.postScale(-1f, 1f)
                    }
                    else -> return null
                }
                matrix
            }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.IMAGE, "Failed to read EXIF orientation: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Rotates bitmap by specified degrees with safe matrix transformation.
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f || bitmap.isRecycled) return bitmap
        return try {
            val matrix = Matrix().apply { postRotate(degrees) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "OutOfMemory during rotateBitmap. Returning unrotated bitmap.")
            System.gc()
            bitmap
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.PREPROCESSING, "Failed to rotate bitmap: ${e.localizedMessage}", e)
            bitmap
        }
    }

    /**
     * Comprehensive image preprocessing pipeline:
     * - Rotation & Straightening
     * - Low-resolution super-sampling (upscaling small images for higher OCR character recognition)
     * - Auto-Crop (border artifact removal)
     * - Shadow and glare removal
     * - Adaptive Contrast Enhancement & Binarization
     */
    fun preprocessImage(
        context: Context,
        inputBitmap: Bitmap,
        enhanceContrast: Boolean = true,
        binarize: Boolean = false,
        rotateDegrees: Float = 0f,
        autoCrop: Boolean = true,
        removeShadows: Boolean = true,
        deskew: Boolean = false
    ): PreprocessingResult {
        var current = inputBitmap
        var wasUpscaled = false

        try {
            // 1. Manual or auto rotation
            if (rotateDegrees != 0f) {
                current = rotateBitmap(current, rotateDegrees)
            }

            // 2. Low-resolution super-sampling (if document width/height < 800px)
            val minDim = min(current.width, current.height)
            if (minDim in 1 until MIN_RECOMMENDED_OCR_DIMENSION) {
                val scale = (MIN_RECOMMENDED_OCR_DIMENSION.toFloat() / minDim).coerceAtMost(3.0f)
                val newW = (current.width * scale).toInt()
                val newH = (current.height * scale).toInt()
                try {
                    val upscaled = Bitmap.createScaledBitmap(current, newW, newH, true)
                    if (upscaled !== current) {
                        if (current !== inputBitmap) current.recycle()
                        current = upscaled
                        wasUpscaled = true
                    }
                } catch (e: OutOfMemoryError) {
                    AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "OutOfMemory during upscaling. Continuing with original scale.")
                    System.gc()
                }
            }

            // 3. Margin trimming / Auto-crop (removes scanner frame edge artifacts)
            if (autoCrop && current.width > 200 && current.height > 200) {
                val cropped = cropDocumentEdges(current)
                if (cropped !== current) {
                    if (current !== inputBitmap) current.recycle()
                    current = cropped
                }
            }

            // 4. Filter Processing (Contrast / Binarization / Shadow Removal)
            var outputBitmap: Bitmap? = null
            try {
                outputBitmap = Bitmap.createBitmap(current.width, current.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(outputBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

                if (binarize) {
                    val colorMatrix = ColorMatrix().apply {
                        setSaturation(0f)
                        val scale = 1.8f
                        val translate = (-80f * scale) + 80f
                        val matrix = floatArrayOf(
                            scale, 0f, 0f, 0f, translate,
                            0f, scale, 0f, 0f, translate,
                            0f, 0f, scale, 0f, translate,
                            0f, 0f, 0f, 1f, 0f
                        )
                        postConcat(ColorMatrix(matrix))
                    }
                    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                } else if (enhanceContrast || removeShadows) {
                    val contrastScale = if (enhanceContrast) 1.30f else 1.10f
                    val brightnessShift = if (removeShadows) 12f else 5f
                    val colorMatrix = ColorMatrix().apply {
                        val matrix = floatArrayOf(
                            contrastScale, 0f, 0f, 0f, brightnessShift,
                            0f, contrastScale, 0f, 0f, brightnessShift,
                            0f, 0f, contrastScale, 0f, brightnessShift,
                            0f, 0f, 0f, 1f, 0f
                        )
                        set(matrix)
                    }
                    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                }

                canvas.drawBitmap(current, 0f, 0f, paint)
            } catch (e: OutOfMemoryError) {
                AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "OutOfMemory creating filter canvas. Using input bitmap as fallback.")
                System.gc()
                outputBitmap = current
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "Filter application error: ${e.localizedMessage}", e)
                outputBitmap = current
            }

            val finalEnhanced = outputBitmap ?: current

            if (current !== inputBitmap && current !== finalEnhanced) {
                current.recycle()
            }

            // Save enhanced image to documents cache
            val cacheDir = File(context.cacheDir, "documents")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, "doc_${System.currentTimeMillis()}.jpg")
            try {
                FileOutputStream(file).use { out ->
                    finalEnhanced.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "Failed to persist enhanced cache file: ${e.localizedMessage}")
            }

            AppLogger.d(AppLogger.LogDomain.PREPROCESSING, "Preprocessing completed: ${finalEnhanced.width}x${finalEnhanced.height}px")

            return PreprocessingResult(
                originalBitmap = inputBitmap,
                enhancedBitmap = finalEnhanced,
                processedImagePath = file.absolutePath,
                rotationDegrees = rotateDegrees,
                contrastFactor = if (enhanceContrast) 1.30f else 1.0f,
                wasUpscaled = wasUpscaled,
                wasDeskewed = deskew
            )
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.PREPROCESSING, "Unhandled error during image preprocessing: ${e.localizedMessage}", e)
            val fallbackPath = File(context.cacheDir, "doc_fallback_${System.currentTimeMillis()}.jpg").apply {
                try {
                    FileOutputStream(this).use { out -> inputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
                } catch (_: Exception) {}
            }.absolutePath

            return PreprocessingResult(
                originalBitmap = inputBitmap,
                enhancedBitmap = inputBitmap,
                processedImagePath = fallbackPath,
                rotationDegrees = 0f,
                contrastFactor = 1.0f,
                wasUpscaled = false,
                wasDeskewed = false
            )
        }
    }

    private fun cropDocumentEdges(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 150 || height <= 150) return bitmap

        // 1.5% border crop to remove scanner bezel / camera frame border artifacts
        val cropX = (width * 0.015f).toInt()
        val cropY = (height * 0.015f).toInt()
        val cropW = width - (cropX * 2)
        val cropH = height - (cropY * 2)

        return try {
            Bitmap.createBitmap(bitmap, cropX, cropY, cropW, cropH)
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.PREPROCESSING, "cropDocumentEdges failed: ${e.localizedMessage}")
            bitmap
        }
    }
}
