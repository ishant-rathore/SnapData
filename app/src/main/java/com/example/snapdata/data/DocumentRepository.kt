package com.example.snapdata.data

import android.content.Context
import com.example.snapdata.error.AppError
import com.example.snapdata.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import java.io.File

class DocumentRepository(context: Context) {
    private val documentDao = AppDatabase.getDatabase(context).documentDao()

    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()
    val documentCount: Flow<Int> = documentDao.getDocumentCount()

    fun searchDocuments(query: String): Flow<List<DocumentEntity>> {
        return if (query.isBlank()) {
            documentDao.getAllDocuments()
        } else {
            documentDao.searchDocuments(query.trim())
        }
    }

    fun getDocumentsByType(type: String): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByType(type)
    }

    suspend fun getDocumentById(id: Long): DocumentEntity? {
        return try {
            documentDao.getDocumentById(id)
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Error fetching document #$id: ${e.localizedMessage}", e)
            null
        }
    }

    /**
     * Atomically saves or updates a document entity with exception safety.
     * Returns the persistent entity ID.
     */
    suspend fun saveDocument(document: DocumentEntity): Long {
        return try {
            val id = documentDao.insertDocument(document)
            AppLogger.i(AppLogger.LogDomain.DATABASE, "Successfully saved document #$id ('${document.title}')")
            id
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to save document '${document.title}': ${e.localizedMessage}", e)
            throw e
        }
    }

    suspend fun updateDocument(document: DocumentEntity) {
        try {
            documentDao.updateDocument(document)
            AppLogger.i(AppLogger.LogDomain.DATABASE, "Successfully updated document #${document.id}")
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to update document #${document.id}: ${e.localizedMessage}", e)
            throw e
        }
    }

    /**
     * Deletes document from database and cleans up cached image file if present.
     */
    suspend fun deleteDocument(document: DocumentEntity) {
        document.originalImagePath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    AppLogger.d(AppLogger.LogDomain.DATABASE, "Cleaned up image cache file: $path")
                }
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.DATABASE, "Failed to delete cached image file: $path: ${e.localizedMessage}")
            }
        }
        try {
            documentDao.deleteDocument(document)
            AppLogger.i(AppLogger.LogDomain.DATABASE, "Deleted document #${document.id}")
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to delete document #${document.id}: ${e.localizedMessage}", e)
            throw e
        }
    }

    suspend fun deleteDocumentById(id: Long) {
        try {
            val existing = documentDao.getDocumentById(id)
            existing?.let { deleteDocument(it) } ?: documentDao.deleteDocumentById(id)
            AppLogger.i(AppLogger.LogDomain.DATABASE, "Deleted document #$id by ID")
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to delete document by ID #$id: ${e.localizedMessage}", e)
            throw e
        }
    }

    suspend fun deleteAllDocuments() {
        try {
            documentDao.deleteAllDocuments()
            AppLogger.i(AppLogger.LogDomain.DATABASE, "Cleared all saved documents from database")
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.DATABASE, "Failed to clear all documents: ${e.localizedMessage}", e)
            throw e
        }
    }
}
