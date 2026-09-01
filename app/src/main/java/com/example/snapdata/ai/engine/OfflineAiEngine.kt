package com.example.snapdata.ai.engine

import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import com.example.snapdata.processing.OcrEngine
import java.io.File

/**
 * Result output from the On-Device AI Document Understanding engine.
 */
data class OfflineAiOutput(
    val detectedDocType: DocumentType,
    val summary: String,
    val fields: List<ExtractedField>,
    val tables: List<ExtractedTable>,
    val overallConfidence: Float,
    val confidenceDistribution: Map<String, Float> = emptyMap(),
    val inferenceTimeMs: Long = 0L,
    val warnings: List<String> = emptyList(),
    val rawModelEvidence: String = ""
)

/**
 * Clean abstraction for local on-device inference.
 * Completely independent of cloud networks, Google Gemini APIs, or external proxies.
 */
interface OfflineAiEngine {
    val isReady: Boolean

    /**
     * Initializes and memory-maps the local neural model weights file.
     */
    suspend fun initialize(modelFile: File): Result<Unit>

    /**
     * Releases model weights and native memory resources.
     */
    suspend fun unload()

    /**
     * Performs semantic document understanding over OCR evidence:
     * 1. Multi-class neural document classification.
     * 2. Neural key-value sequence labeling and slot filling.
     * 3. Table boundary detection and matrix cell reconstruction.
     * 4. Factual executive summarization.
     */
    suspend fun analyze(
        ocrResult: OcrEngine.OcrResult,
        forcedType: DocumentType? = null,
        timeoutMs: Long = 30000L
    ): Result<OfflineAiOutput>
}
