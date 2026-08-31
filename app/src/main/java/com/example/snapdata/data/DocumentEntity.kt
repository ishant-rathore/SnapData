package com.example.snapdata.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.model.ExtractedTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val docType: String = DocumentType.GENERAL_DOCUMENT.name,
    val originalImagePath: String? = null,
    val summary: String = "",
    val rawOcrText: String = "",
    val fieldsJson: String = "[]",
    val tablesJson: String = "[]",
    val overallConfidence: Float = 0.95f,
    val pageCount: Int = 1,
    val isFavorite: Boolean = false,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getTypedDocType(): DocumentType {
        return try {
            DocumentType.valueOf(docType)
        } catch (e: Exception) {
            DocumentType.GENERAL_DOCUMENT
        }
    }

    fun getFieldsList(): List<ExtractedField> {
        return try {
            json.decodeFromString(fieldsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTablesList(): List<ExtractedTable> {
        return try {
            json.decodeFromString(tablesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Verifies if the associated image file exists on disk.
     * Returns true if path is valid and file exists, or false if path is null/file deleted.
     */
    fun hasValidImageFile(): Boolean {
        if (originalImagePath.isNullOrBlank()) return false
        return try {
            File(originalImagePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true; prettyPrint = false; isLenient = true }

        fun from(
            title: String,
            docType: DocumentType,
            originalImagePath: String?,
            summary: String,
            rawOcrText: String,
            fields: List<ExtractedField>,
            tables: List<ExtractedTable>,
            overallConfidence: Float,
            id: Long = 0,
            isFavorite: Boolean = false,
            tags: String = "",
            createdAt: Long? = null,
            pageCount: Int = 1
        ): DocumentEntity {
            val now = System.currentTimeMillis()
            return DocumentEntity(
                id = id,
                title = title,
                docType = docType.name,
                originalImagePath = originalImagePath,
                summary = summary,
                rawOcrText = rawOcrText,
                fieldsJson = json.encodeToString(fields),
                tablesJson = json.encodeToString(tables),
                overallConfidence = overallConfidence,
                pageCount = pageCount,
                isFavorite = isFavorite,
                tags = tags,
                createdAt = createdAt ?: now,
                updatedAt = now
            )
        }
    }
}
