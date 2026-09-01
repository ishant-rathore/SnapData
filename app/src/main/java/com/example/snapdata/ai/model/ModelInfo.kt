package com.example.snapdata.ai.model

import kotlinx.serialization.Serializable

/**
 * Explicit state machine for on-device AI model lifecycle.
 */
enum class ModelStatus(val displayName: String, val isReady: Boolean) {
    NOT_INSTALLED("Not Installed", false),
    DOWNLOADING("Downloading...", false),
    VERIFYING("Verifying Integrity...", false),
    READY("Ready (Offline Active)", true),
    FAILED("Setup Failed", false),
    DELETING("Removing...", false),
    CORRUPTED("Corrupted (Verification Failed)", false)
}

/**
 * Metadata specification for the on-device AI document model.
 */
@Serializable
data class ModelMetadata(
    val modelId: String = "snapdata-doc-ai-v1",
    val modelName: String = "SnapData Neural Document Extractor v1.0",
    val version: String = "1.0.0",
    val description: String = "On-device neural model for document classification, key-value slot filling, and table matrix extraction.",
    val downloadUrl: String = "https://storage.googleapis.com/snapdata-models/snapdata-doc-ai-v1.0.0.bin",
    val expectedSizeBytes: Long = 24_576_000L, // 24.57 MB
    val sha256Checksum: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    val localFileName: String = "snapdata_doc_ai_v1.bin",
    val requiredStorageBytes: Long = 50_000_000L, // 50 MB required buffer
    val format: String = "Quantized Neural Weights & Vocabulary (INT8)",
    val inputModality: String = "OCR Tokens + Bounding Coordinates + Layout Geometry"
)

/**
 * Live download progress tracking.
 */
data class ModelDownloadProgress(
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = 0L,
    val progressPercent: Int = 0,
    val downloadSpeedBytesPerSec: Long = 0L,
    val detailMessage: String = ""
)
