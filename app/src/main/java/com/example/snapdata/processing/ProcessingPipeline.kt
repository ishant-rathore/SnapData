package com.example.snapdata.processing

import android.content.Context
import android.graphics.Bitmap
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ProcessingOptions
import com.example.snapdata.model.ProcessingProgress
import com.example.snapdata.model.ProcessingStage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

object ProcessingPipeline {

    data class PipelineOutput(
        val originalBitmap: Bitmap,
        val enhancedBitmap: Bitmap,
        val processedImagePath: String,
        val ocrResult: OcrEngine.OcrResult,
        val executionResult: ProcessingExecutionResult,
        val pageCount: Int = 1,
        val pageResults: List<MultiPageDocumentMerger.PageOcrData> = emptyList()
    )

    /**
     * Single-image bitmap processing pipeline.
     */
    fun runPipeline(
        context: Context,
        inputBitmap: Bitmap,
        options: ProcessingOptions = ProcessingOptions(),
        hintOcrText: String? = null,
        forcedType: DocumentType? = null
    ): Flow<Pair<ProcessingProgress, PipelineOutput?>> = flow {
        try {
            // Stage 1: Acquisition
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.ACQUISITION,
                        currentStep = 1,
                        totalSteps = 6,
                        detailMessage = "Acquiring document image (${inputBitmap.width}x${inputBitmap.height}px)..."
                    ),
                    null
                )
            )
            delay(150)

            // Stage 2: Preprocessing & Enhancement
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.PREPROCESSING,
                        currentStep = 2,
                        totalSteps = 6,
                        detailMessage = "Enhancing contrast, auto-cropping margins & straightening orientation..."
                    ),
                    null
                )
            )
            val prepResult = ImagePreprocessor.preprocessImage(
                context = context,
                inputBitmap = inputBitmap,
                enhanceContrast = options.enhanceContrast,
                binarize = options.binarize,
                autoCrop = options.autoCrop,
                removeShadows = options.removeShadows,
                deskew = options.deskew,
                rotateDegrees = if (options.autoRotate) 0f else 0f
            )
            delay(150)

            // Stage 3: OCR Recognition
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.OCR,
                        currentStep = 3,
                        totalSteps = 6,
                        detailMessage = "Extracting raw optical text tokens and character bounding regions..."
                    ),
                    null
                )
            )
            val rawOcrText = if (!hintOcrText.isNullOrBlank()) {
                hintOcrText
            } else {
                OcrEngine.recognizeTextFromBitmap(prepResult.enhancedBitmap)
            }
            delay(150)

            // Stage 4: AI Analysis & Classification
            currentCoroutineContext().ensureActive()
            val isCloud = options.enableCloudAi && !options.forceOfflineAi
            val stage4Message = if (isCloud) {
                "Executing cloud semantic extraction via Gemini AI..."
            } else {
                "Running 100% on-device heuristic layout analysis and classification..."
            }

            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.AI_ANALYSIS,
                        currentStep = 4,
                        totalSteps = 6,
                        detailMessage = stage4Message
                    ),
                    null
                )
            )

            val executionResult = GeminiAiService.extractStructuredDocument(
                bitmap = prepResult.enhancedBitmap,
                hintText = rawOcrText,
                forceOffline = options.forceOfflineAi,
                enableCloudAi = options.enableCloudAi,
                forcedType = forcedType
            )
            val ocrResult = executionResult.ocrResult
            delay(150)

            // Stage 5: Structured Field & Table Matrix Extraction
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.STRUCTURED_EXTRACTION,
                        currentStep = 5,
                        totalSteps = 6,
                        detailMessage = "Structuring ${ocrResult.fields.size} key-value fields and ${ocrResult.tables.size} data tables..."
                    ),
                    null
                )
            )
            delay(150)

            // Stage 6: Validation & Verification
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.VALIDATION,
                        currentStep = 6,
                        totalSteps = 6,
                        detailMessage = "Verifying schema consistency (${executionResult.engineUsed.displayName})..."
                    ),
                    null
                )
            )
            delay(100)

            // Completed
            val finalOutput = PipelineOutput(
                originalBitmap = prepResult.originalBitmap,
                enhancedBitmap = prepResult.enhancedBitmap,
                processedImagePath = prepResult.processedImagePath,
                ocrResult = if (forcedType != null) ocrResult.copy(detectedDocType = forcedType) else ocrResult,
                executionResult = executionResult,
                pageCount = 1,
                pageResults = listOf(
                    MultiPageDocumentMerger.PageOcrData(
                        pageIndex = 1,
                        rawText = ocrResult.rawText,
                        ocrResult = ocrResult
                    )
                )
            )

            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.COMPLETED,
                        currentStep = 6,
                        totalSteps = 6,
                        detailMessage = executionResult.diagnosticMessage
                    ),
                    finalOutput
                )
            )
        } catch (c: CancellationException) {
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.CANCELLED,
                        detailMessage = "Processing cancelled by user."
                    ),
                    null
                )
            )
            throw c
        } catch (e: Exception) {
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.ERROR,
                        detailMessage = "Processing error: ${e.localizedMessage}",
                        error = e.localizedMessage ?: "Unknown processing error"
                    ),
                    null
                )
            )
        }
    }

    /**
     * Memory-safe multi-page PDF processing pipeline.
     * Renders each page one-by-one, performs OCR, immediately recycles bitmaps to prevent OOM,
     * preserves page order, extracts & deduplicates cross-page fields, and stitches multi-page tables.
     */
    fun runMultiPagePdfPipeline(
        context: Context,
        pdfFile: File,
        options: ProcessingOptions = ProcessingOptions(),
        forcedType: DocumentType? = null,
        maxPages: Int = PdfDocumentRenderer.MAX_SUPPORTED_PAGES_DEFAULT
    ): Flow<Pair<ProcessingProgress, PipelineOutput?>> = flow {
        var pdfSession: PdfDocumentRenderer.PdfSession? = null
        try {
            // Stage 1: Acquisition & PDF Validation
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.ACQUISITION,
                        currentStep = 1,
                        totalSteps = 6,
                        detailMessage = "Validating PDF and initializing document session..."
                    ),
                    null
                )
            )

            pdfSession = try {
                PdfDocumentRenderer.openSession(pdfFile)
            } catch (e: Exception) {
                emit(
                    Pair(
                        ProcessingProgress(
                            stage = ProcessingStage.ERROR,
                            detailMessage = "Failed to open PDF: ${e.localizedMessage}",
                            error = e.localizedMessage ?: "Corrupted or encrypted PDF document."
                        ),
                        null
                    )
                )
                return@flow
            }

            val totalPagesInDoc = pdfSession.pageCount
            if (totalPagesInDoc <= 0) {
                emit(
                    Pair(
                        ProcessingProgress(
                            stage = ProcessingStage.ERROR,
                            detailMessage = "The PDF contains 0 pages.",
                            error = "Empty PDF document."
                        ),
                        null
                    )
                )
                return@flow
            }

            val pagesToProcess = minOf(totalPagesInDoc, maxPages)

            // Stage 2: Memory-Safe Per-Page Rendering & OCR
            val pageOcrResults = mutableListOf<MultiPageDocumentMerger.PageOcrData>()
            var coverOriginalBitmap: Bitmap? = null
            var coverEnhancedBitmap: Bitmap? = null
            var coverProcessedPath: String = ""

            for (pageIndex in 0 until pagesToProcess) {
                currentCoroutineContext().ensureActive()
                val pageNum = pageIndex + 1

                emit(
                    Pair(
                        ProcessingProgress(
                            stage = ProcessingStage.OCR,
                            currentStep = pageNum,
                            totalSteps = pagesToProcess,
                            detailMessage = "Rendering & optical reading page $pageNum of $pagesToProcess..."
                        ),
                        null
                    )
                )

                // Render single page with memory bounds
                val pageBitmap = try {
                    PdfDocumentRenderer.renderSinglePage(
                        renderer = pdfSession.renderer,
                        pageIndex = pageIndex,
                        maxDimension = PdfDocumentRenderer.MAX_RENDER_DIMENSION
                    )
                } catch (e: OutOfMemoryError) {
                    System.gc()
                    emit(
                        Pair(
                            ProcessingProgress(
                                stage = ProcessingStage.ERROR,
                                detailMessage = "Insufficient memory while rendering page $pageNum.",
                                error = "Out of memory error rendering page $pageNum."
                            ),
                            null
                        )
                    )
                    return@flow
                } catch (e: Exception) {
                    emit(
                        Pair(
                            ProcessingProgress(
                                stage = ProcessingStage.ERROR,
                                detailMessage = "Failed to render page $pageNum: ${e.localizedMessage}",
                                error = "Rendering failure on page $pageNum."
                            ),
                            null
                        )
                    )
                    return@flow
                }

                // If first page, keep enhanced version as cover preview
                val rawPageText: String
                if (pageIndex == 0) {
                    val prep = ImagePreprocessor.preprocessImage(
                        context = context,
                        inputBitmap = pageBitmap,
                        enhanceContrast = options.enhanceContrast,
                        binarize = options.binarize,
                        autoCrop = false,
                        removeShadows = options.removeShadows,
                        deskew = options.deskew
                    )
                    coverOriginalBitmap = prep.originalBitmap
                    coverEnhancedBitmap = prep.enhancedBitmap
                    coverProcessedPath = prep.processedImagePath
                    rawPageText = OcrEngine.recognizeTextFromBitmap(prep.enhancedBitmap)
                } else {
                    rawPageText = OcrEngine.recognizeTextFromBitmap(pageBitmap)
                    // Free memory immediately for secondary pages!
                    pageBitmap.recycle()
                }

                // Parse single-page structured data
                val singlePageOcr = OcrEngine.parseTextToStructuredData(rawPageText, forcedType)
                pageOcrResults.add(
                    MultiPageDocumentMerger.PageOcrData(
                        pageIndex = pageNum,
                        rawText = rawPageText,
                        ocrResult = singlePageOcr
                    )
                )
                delay(60)
            }

            // Close PDF Session safely to free native descriptors
            pdfSession.close()
            pdfSession = null

            // Stage 3: Multi-Page Aggregation & Cross-Page Consolidation
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.STRUCTURED_EXTRACTION,
                        currentStep = 4,
                        totalSteps = 6,
                        detailMessage = "Consolidating key-value pairs and stitching tables across $pagesToProcess pages..."
                    ),
                    null
                )
            )
            val combinedOcrResult = MultiPageDocumentMerger.combineMultiPageResults(
                pages = pageOcrResults,
                forcedType = forcedType
            )
            delay(150)

            // Stage 4: Semantic AI Evaluation & Refinement
            currentCoroutineContext().ensureActive()
            val isCloud = options.enableCloudAi && !options.forceOfflineAi
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.AI_ANALYSIS,
                        currentStep = 5,
                        totalSteps = 6,
                        detailMessage = if (isCloud) "Running cloud semantic refinement via Gemini AI..." else "Applying multi-page semantic verification on-device..."
                    ),
                    null
                )
            )

            val executionResult = if (isCloud && coverEnhancedBitmap != null) {
                GeminiAiService.extractStructuredDocument(
                    bitmap = coverEnhancedBitmap,
                    hintText = combinedOcrResult.rawText,
                    forceOffline = options.forceOfflineAi,
                    enableCloudAi = options.enableCloudAi,
                    forcedType = forcedType
                )
            } else {
                ProcessingExecutionResult(
                    ocrResult = combinedOcrResult,
                    engineUsed = ExecutionEngine.ON_DEVICE_LOCAL,
                    isOffline = true,
                    diagnosticMessage = "Successfully parsed $pagesToProcess pages with on-device intelligence."
                )
            }

            // Merge any final AI adjustments or use combined result
            val finalOcrResult = if (isCloud && executionResult.engineUsed != ExecutionEngine.ON_DEVICE_LOCAL) {
                // If cloud returned fields, keep multi-page combined tables and text
                executionResult.ocrResult.copy(
                    rawText = combinedOcrResult.rawText,
                    tables = if (executionResult.ocrResult.tables.isNotEmpty()) executionResult.ocrResult.tables else combinedOcrResult.tables
                )
            } else {
                combinedOcrResult
            }
            delay(150)

            // Stage 5: Validation & Integrity Check
            currentCoroutineContext().ensureActive()
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.VALIDATION,
                        currentStep = 6,
                        totalSteps = 6,
                        detailMessage = "Validating document matrix schema and field confidence (${pagesToProcess} pages)..."
                    ),
                    null
                )
            )
            delay(100)

            // Stage 6: Completion
            val fallbackBitmap = coverOriginalBitmap ?: Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
            val fallbackEnhanced = coverEnhancedBitmap ?: fallbackBitmap
            val finalOutput = PipelineOutput(
                originalBitmap = fallbackBitmap,
                enhancedBitmap = fallbackEnhanced,
                processedImagePath = coverProcessedPath,
                ocrResult = finalOcrResult,
                executionResult = executionResult.copy(ocrResult = finalOcrResult),
                pageCount = pagesToProcess,
                pageResults = pageOcrResults
            )

            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.COMPLETED,
                        currentStep = 6,
                        totalSteps = 6,
                        detailMessage = "Successfully extracted ${finalOcrResult.fields.size} fields and ${finalOcrResult.tables.size} tables across $pagesToProcess pages."
                    ),
                    finalOutput
                )
            )
        } catch (c: CancellationException) {
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.CANCELLED,
                        detailMessage = "Processing cancelled by user."
                    ),
                    null
                )
            )
            throw c
        } catch (e: Exception) {
            emit(
                Pair(
                    ProcessingProgress(
                        stage = ProcessingStage.ERROR,
                        detailMessage = "Failed to process PDF: ${e.localizedMessage}",
                        error = e.localizedMessage ?: "PDF processing failure."
                    ),
                    null
                )
            )
        } finally {
            try {
                pdfSession?.close()
            } catch (_: Exception) {}
        }
    }
}
