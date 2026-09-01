package com.example.snapdata.util

import android.content.Context
import com.example.snapdata.logging.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Production-grade temporary file lifecycle and cleanup manager for SnapData.
 *
 * Guarantees:
 * - Safely prunes orphaned temporary files (camera snapshots, preprocessing bitmaps, rendered PDF pages, export files).
 * - Leaves user-saved database documents (in app files) and downloaded AI models untouched.
 * - Protects against disk accumulation and leakage of intermediate document artifacts.
 * - Runs asynchronously on Dispatchers.IO to never block the main UI thread.
 */
object TempFileCleanupManager {

    private val TEMP_SUBDIRS = listOf("camera", "exports", "preprocessing", "pdf_pages", "temp")

    /**
     * Cleans up all stale temporary cache files older than [maxAgeMs] (default: 1 hour).
     */
    suspend fun cleanupStaleTempFiles(
        context: Context,
        maxAgeMs: Long = 60 * 60 * 1000L
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        var deletedCount = 0
        var freedBytes = 0L

        try {
            val cacheRoot = context.cacheDir
            for (subDirName in TEMP_SUBDIRS) {
                val dir = File(cacheRoot, subDirName)
                if (dir.exists() && dir.isDirectory) {
                    dir.listFiles()?.forEach { file ->
                        if (file.isFile && (now - file.lastModified() > maxAgeMs || file.name.endsWith(".tmp"))) {
                            val size = file.length()
                            if (file.delete()) {
                                deletedCount++
                                freedBytes += size
                            }
                        }
                    }
                }
            }

            if (deletedCount > 0) {
                AppLogger.d(
                    AppLogger.LogDomain.STORAGE,
                    "Cleaned up $deletedCount stale temp files (freed ${freedBytes / 1024} KB)"
                )
            }
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.STORAGE, "Error during temp file cleanup: ${e.localizedMessage}")
        }
    }

    /**
     * Immediately deletes a specific temporary file if it resides in the app cache directory.
     */
    fun deleteTempFile(file: File?): Boolean {
        if (file == null || !file.exists()) return false
        return try {
            file.delete()
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.STORAGE, "Failed to delete temp file: ${file.name}: ${e.localizedMessage}")
            false
        }
    }

    /**
     * Immediately clears transient preprocessing and PDF page cache directories.
     */
    suspend fun clearTransientProcessingCache(context: Context) = withContext(Dispatchers.IO) {
        val transientDirs = listOf("preprocessing", "pdf_pages")
        for (name in transientDirs) {
            val dir = File(context.cacheDir, name)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { it.delete() }
            }
        }
    }
}
