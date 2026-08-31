package com.example.snapdata.ui.screens.guide

import android.content.Context
import android.content.SharedPreferences

/**
 * Enumeration of all 10 interactive guide steps + completion
 */
enum class GuideStep(
    val stepNumber: Int,
    val totalSteps: Int = 10,
    val title: String,
    val subtitle: String,
    val tooltipText: String,
    val highlightTargetTag: String
) {
    HOME_DASHBOARD(
        stepNumber = 1,
        title = "01  HOME DASHBOARD",
        subtitle = "Scan Document",
        tooltipText = "Scan documents instantly with your camera.",
        highlightTargetTag = "guide_target_scan_document"
    ),
    DOCUMENT_INPUT(
        stepNumber = 2,
        title = "02  DOCUMENT INPUT",
        subtitle = "Upload PDF",
        tooltipText = "Import an existing PDF for intelligent document processing.",
        highlightTargetTag = "guide_target_upload_pdf"
    ),
    CAMERA_SCANNER(
        stepNumber = 3,
        title = "03  CAMERA SCANNER",
        subtitle = "Capture Button",
        tooltipText = "Place the document inside the frame and capture it.",
        highlightTargetTag = "guide_target_capture_button"
    ),
    IMAGE_ENHANCEMENT(
        stepNumber = 4,
        title = "04  IMAGE ENHANCEMENT",
        subtitle = "Auto Crop / Enhance",
        tooltipText = "Improve your document before OCR for cleaner extraction.",
        highlightTargetTag = "guide_target_enhancement_toolbar"
    ),
    OCR_PROCESSING(
        stepNumber = 5,
        title = "05  OCR PROCESSING",
        subtitle = "OCR Engine",
        tooltipText = "SnapData converts document images into editable digital text.",
        highlightTargetTag = "guide_target_ocr_area"
    ),
    AI_DOCUMENT_INTELLIGENCE(
        stepNumber = 6,
        title = "06  AI DOCUMENT INTELLIGENCE",
        subtitle = "AI Analysis",
        tooltipText = "AI identifies document structure and important information automatically.",
        highlightTargetTag = "guide_target_ai_analysis"
    ),
    STRUCTURED_DATA(
        stepNumber = 7,
        title = "07  STRUCTURED DATA",
        subtitle = "Extracted Data",
        tooltipText = "Review structured information extracted from your document.",
        highlightTargetTag = "guide_target_structured_data"
    ),
    REVIEW_AND_EDIT(
        stepNumber = 8,
        title = "08  REVIEW & EDIT",
        subtitle = "Edit Text",
        tooltipText = "Correct, validate, and refine extracted information before saving.",
        highlightTargetTag = "guide_target_edit_text"
    ),
    EXPORT(
        stepNumber = 9,
        title = "09  EXPORT",
        subtitle = "Excel (.xlsx)",
        tooltipText = "Export your structured data in the format you need.",
        highlightTargetTag = "guide_target_export_excel"
    ),
    DOCUMENT_HISTORY(
        stepNumber = 10,
        title = "10  DOCUMENT HISTORY",
        subtitle = "Search / History",
        tooltipText = "Find, reopen, and manage your previously processed documents.",
        highlightTargetTag = "guide_target_history_search"
    ),
    COMPLETION(
        stepNumber = 11,
        title = "YOU'RE READY",
        subtitle = "Scan. Extract. Edit. Export.",
        tooltipText = "Start processing your documents with SnapData.",
        highlightTargetTag = "guide_target_completion"
    );

    companion object {
        fun fromIndex(index: Int): GuideStep {
            val steps = entries
            return if (index in 0 until steps.size) steps[index] else HOME_DASHBOARD
        }
    }
}

/**
 * Preferences manager to track if the interactive guide has been completed.
 */
class UserGuidePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var hasCompletedUserGuide: Boolean
        get() = prefs.getBoolean(KEY_HAS_COMPLETED_GUIDE, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_COMPLETED_GUIDE, value).apply()

    fun resetGuide() {
        prefs.edit().remove(KEY_HAS_COMPLETED_GUIDE).apply()
    }

    companion object {
        private const val PREFS_NAME = "snapdata_user_guide_prefs"
        private const val KEY_HAS_COMPLETED_GUIDE = "has_completed_user_guide"
    }
}
