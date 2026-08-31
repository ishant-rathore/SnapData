# SnapData User Guide

**Project:** SnapData: AI-Powered Intelligent Document Processing & Data Extraction System  
**Document:** `SnapData_USER_GUIDE_v1.0.md`  
**Version:** 1.0  
**Audience:** Android end users  
**Status:** User Guide / Source-Aligned Baseline  
**Date:** 30 August 2026

> This guide describes the user-facing behavior supported by the approved SnapData project specifications. Where an exact device requirement, model detail, Android API behavior, or implementation choice is not finalized in those specifications, it is marked **TBD** or **Requires Technical Validation**.

---

## 1. What is SnapData?

SnapData is an Android application that converts documents captured with the camera or imported as PDF/image files into structured, editable digital data.

The application uses **Optical Character Recognition (OCR)** to read document content and **AI** to understand that content and organize it into information such as:

- Document type
- Key-value fields
- Tables
- Confidence information, where produced by the processing pipeline
- Optional summary information, where supported

You can review and correct the extracted information, save it locally, and export the current saved result to Excel, CSV, JSON, or PDF.

SnapData is designed as an **offline-first** application. After the required AI model setup is complete, the core document-processing workflow is intended to run locally on the device without requiring cloud upload.

---

## 2. Key Features

The approved core workflow includes:

- Camera-based document capture
- PDF import
- Image import
- Image/document preprocessing
- OCR text extraction
- Offline AI document analysis after required model setup
- Document type detection
- Key-value/field extraction
- Table detection
- Confidence information where available
- Structured data generation
- Review and editing of extracted fields and tables
- Local saving
- Document history
- Reopening and deleting saved documents
- Export to Excel, CSV, JSON, and PDF
- Android file sharing/opening where supported
- Settings for approved configuration areas such as AI model status/setup, OCR language, theme, storage, and About

The core sequence is: document input → acquisition → preprocessing → OCR → offline AI → structured data → review/edit → local storage → export → history.

---

## 3. System Requirements

### Confirmed

- **Platform:** Android
- **Camera:** Required only when using camera acquisition
- **Local storage:** Required for documents, processed data, and exports
- **AI setup:** Required before offline AI processing can be used

### TBD / Requires Technical Validation

The approved specifications do **not** yet finalize:

- Minimum Android version
- Exact supported Android device list
- Minimum/recommended RAM
- Minimum free storage for the AI model and documents
- CPU/GPU/NPU requirements
- Maximum document size or page count
- Exact AI model and runtime
- Exact OCR integration details

Do not assume a numeric minimum from this guide until the final Android build and compatibility testing establish it.

---

## 4. First-Time Setup

When you open SnapData for the first time, the app may show a short introduction/onboarding flow.

1. Open **SnapData**.
2. Read the brief introduction to the workflow.
3. Select **Get Started** or **Continue**.
4. Complete AI setup if the app reports that offline AI is not ready.
5. After setup succeeds, continue to the **Home** screen.

The first-run flow is intended to explain capture/import, OCR, AI structuring, review/editing, local saving, export, and offline behavior.

---

## 5. AI Model Setup

SnapData requires the local AI capability to be ready before offline AI processing can run.

### When setup is required

The app may show:

> **Offline AI needs to be set up before processing.**

### Setup steps

1. Open **AI Model Setup** from first-time setup or **Settings → AI Model**.
2. Read the readiness information shown by the app.
3. Select **Set up AI** or **Download model** when available.
4. Wait for setup to complete.
5. When the app reports **Offline AI is ready**, return to **Home**.

### Important

Initial model setup may require an internet connection. Core processing is intended to work offline **after** the required model setup is complete. If setup fails, use the recovery message and retry rather than assuming processing is available.

The exact model name, model size, download/update mechanism, and device requirements are **TBD / Requires Technical Validation** and are intentionally not specified here.

---

## 6. Home Dashboard

The Home screen is the main starting point for normal use.

The approved UX baseline centers the screen around two primary actions:

- **Scan Document**
- **Import Document**

It can also provide access to:

- Recent documents
- History
- Settings
- AI readiness/status

When no documents have been saved yet, the app can show a message such as:

> **No saved documents yet. Scan or import a document to get started.**

The exact navigation presentation is implementation-dependent; the approved baseline uses Home, History, and Settings as the main navigation areas.

---

## 7. Scan a Document Using the Camera

1. From **Home**, select **Scan Document**.
2. Allow camera access when Android asks for it, if required.
3. Position the document so it is clearly visible.
4. Capture the document.
5. Review the captured document in the preview step.
6. Confirm the document to continue to processing.

The camera acquisition path is followed by validation, preprocessing, OCR, AI analysis, and structured-data generation.

### Camera errors

If camera access is denied or capture fails, SnapData should show a clear recovery state. It must not create a false successful document record.

---

## 8. Upload a PDF

1. From **Home**, select **Import Document**.
2. Choose a PDF using the Android file-selection flow.
3. Select the document.
4. Review the imported document in the preview step, where provided.
5. Confirm to start processing.

PDF input is part of the confirmed core product workflow.

### Multi-page PDFs

Multi-page PDF support is included in the project material, but exact page-count limits and final processing behavior remain **Requires Technical Validation**.

---

## 9. Upload an Image

1. From **Home**, select **Import Document**.
2. Choose an image file.
3. Review the selected image in the preview step, where available.
4. Confirm to continue to processing.

If the selected file is unsupported or corrupt, SnapData should show an understandable error and allow you to choose another file.

---

## 10. Image Preprocessing

Before OCR, SnapData prepares the document image so it is more suitable for text extraction.

The approved processing baseline includes core preparation operations such as:

- Cropping
- Rotation/auto-rotation
- Perspective correction
- Noise reduction
- Brightness/contrast enhancement

The exact algorithms and final implementation details are still **Requires Technical Validation**.

As a user, you normally do not need to configure these processing steps. SnapData applies the supported processing stages as part of the document workflow.

---

## 11. OCR Processing

OCR means **Optical Character Recognition**. It converts text visible in the document into machine-readable text.

During processing, SnapData may show stage-based progress such as:

```text
Validation
   ↓
Preprocessing
   ↓
OCR Processing
   ↓
AI Processing
   ↓
Structured Data
```

If OCR cannot obtain usable text, the app should show a clear empty or failure state instead of claiming successful extraction.

The project identifies Tesseract as source-backed OCR context, but the exact Android OCR integration is not yet finalized.

---

## 12. Offline AI Processing

After OCR, SnapData sends the usable document evidence through its local AI processing stage.

AI analysis can be used to identify:

- Document type
- Key-value fields
- Tables
- Confidence information, where available
- Summary information, where supported

### Offline behavior

After the required AI model setup is complete, the core workflow is intended to operate locally without requiring cloud upload.

SnapData must **not** silently upload a document to a remote AI service as a fallback when local AI is unavailable. When the model is not ready, the app should instead direct you to the model setup/readiness flow.

---

## 13. Viewing Extracted Data

When processing finishes successfully, SnapData opens the **Results** view.

The result may include:

- Detected document type
- Extracted fields
- Extracted tables
- Confidence information, where produced
- Warnings or partial-result information, where applicable
- Source context, where supported
- Summary information, where supported

The result is intended for **review before export**. AI/OCR output is not automatically treated as the final truth.

---

## 14. Editing and Correcting Extracted Fields

Use the editor when an OCR or AI-extracted value is incorrect or incomplete.

1. Open the document's **Results**.
2. Select **Edit**.
3. Select the field you want to correct.
4. Enter the correct value.
5. Review the changed value.
6. Save the changes.

The saved, user-corrected result is authoritative for reopening and exporting. SnapData must not silently overwrite a saved correction with a new AI candidate.

If an edit is invalid, the app should show validation feedback rather than silently accepting a known-invalid value.

---

## 15. Editing Tables

When SnapData detects a table, the result can be presented as structured rows and columns.

To correct a table:

1. Open **Results**.
2. Select **Edit**.
3. Select the table.
4. Edit the required cells or supported row content.
5. Check that row/column values still match the source document.
6. Save the changes.

The approved implementation plan includes table editing, cell editing, and row editing operations; the exact advanced editing controls in the final UI may depend on the validated build.

---

## 16. Validating and Saving Data

Before saving:

1. Review important fields.
2. Check extracted tables.
3. Correct obvious OCR/AI mistakes.
4. Review warnings or confidence information where shown.
5. Select **Save**.

When Save succeeds, the corrected structured result becomes the authoritative local result.

If saving fails, the app should preserve your current edits where supported and show a clear recovery action. It must not report success when the save did not complete.

---

## 17. Exporting to Excel, CSV, JSON, and PDF

SnapData supports these export formats:

| Format | Typical file extension |
|---|---|
| Excel | `.xlsx` |
| CSV | `.csv` |
| JSON | `.json` |
| PDF | `.pdf` |

### Export steps

1. Open the saved document result.
2. Select **Export**.
3. Choose **Excel**, **CSV**, **JSON**, or **PDF**.
4. Start the export.
5. Wait for the success/failure result.
6. Use the resulting file for local use or sharing.

Exports must use the **current saved/edited structured result**, not an outdated AI candidate or raw OCR output.

### Important

Export performance limits, exact formatting behavior for complex tables, and exact export libraries are **TBD / Requires Technical Validation**.

---

## 18. Sharing Exported Files

After an export is created, SnapData can use the Android-supported sharing/opening mechanism where available.

1. Complete the export.
2. Choose **Share** or the corresponding Android sharing action, when shown.
3. Select an available destination app.
4. Complete the share action in Android.

If sharing is cancelled or no suitable sharing target is available, the exported file should remain preserved and the app should report the outcome clearly.

Sharing is a deliberate privacy boundary because the exported file leaves SnapData's local processing flow. Only share a file with destinations you trust.

---

## 19. Document History

**History** is the local list of documents that have been saved by SnapData.

A saved history item can retain information such as:

- Document metadata
- Processing status/history information
- Saved extracted fields
- Saved tables
- Other approved local result information

The Home screen can also show recent documents as a shortcut to saved items.

---

## 20. Reopening and Deleting Documents

### Reopen

1. Open **History**.
2. Select the saved document.
3. Open the retained result.
4. Review or edit it as required.

Saved user corrections should remain authoritative when the document is reopened.

### Delete

1. Open **History**.
2. Select the document's delete action.
3. Confirm deletion when the app asks for confirmation, where applicable.

Deleting a saved item removes it from local history and applicable stored content. Exact operating-system file-cleanup behavior is an implementation detail.

---

## 21. Settings

The approved settings areas include:

### AI Model

View model readiness/setup information and access the setup flow.

### OCR Language

Choose from the OCR language options supported by the validated build. The final language list is **TBD**.

### Theme

Use the supported light/dark theme behavior provided by the app. Theme implementation details are part of the UI baseline.

### Storage Management

View/manage local storage information where supported.

### About

View approved app/project information.

### Export Preferences

Export preferences are not part of the required MVP behavior and remain **P2 / TBD** where not finalized.

> Do not assume PIN/biometric lock, encryption controls, secure-delete controls, cloud sync, or other security settings are available unless they are explicitly present in the validated build. The security specification keeps these mechanisms open until implemented and tested.

---

## 22. Storage Management

SnapData uses local persistence for saved documents and structured data. The approved database baseline identifies **SQLite** as the intended local database, with local file storage used for document/file content.

Storage management is intended to help you understand available local space and avoid storage-related failures.

Because the exact Android storage APIs, minimum free-space requirements, and AI model storage footprint are not finalized, the app's validated build is the authority for exact values and controls.

If SnapData reports insufficient storage:

1. Read the message shown by the app.
2. Free device storage if needed.
3. Retry the failed operation.
4. Avoid assuming a failure means the document itself is invalid.

---

## 23. Error Handling and Recovery

SnapData is designed to show truthful, understandable failure states.

Common situations include:

| Situation | Expected user action |
|---|---|
| AI model not ready | Open AI setup/readiness flow |
| Initial model setup fails | Follow the displayed requirement and retry |
| Not enough storage | Free storage and retry |
| Unsupported file | Select a supported file |
| Corrupt file | Select another file or restore the source file |
| OCR returns no usable text | Check document readability and retry |
| Processing fails | Read the displayed error and retry when available |
| Save fails | Retry; edits should be preserved where supported |
| Export fails | Retry export; previously saved data should remain safe |
| Sharing unavailable | Keep the exported file and use another supported destination |
| Missing/corrupt history record | Use the app's recovery state; do not assume successful recovery |

SnapData should never show **Completed** when an operation actually failed or was cancelled. Previously saved authoritative data should remain protected during processing/export failures.

---

## 24. Privacy and Offline Processing

SnapData's privacy model is based on local-first processing.

### What happens locally

After required AI setup, the core workflow is intended to process documents on the device through:

```text
Document Input
   ↓
Preprocessing
   ↓
OCR
   ↓
Offline AI
   ↓
Structured Data
   ↓
Review/Edit
   ↓
Local Save
   ↓
Export
```

The MVP does not require a backend, REST API, or cloud database for the core workflow.

### Important privacy boundaries

- Initial AI model setup may require internet connectivity.
- Core processing is intended to work offline after setup.
- SnapData should not silently upload documents as an AI fallback.
- Normal diagnostics should not contain raw document contents, complete OCR text, or sensitive extracted values.
- Export and Android sharing are explicit points where a file can leave the SnapData local workflow.

The project security baseline does **not** authorize this guide to claim that local data is already encrypted, that the app has PIN/biometric lock, or that secure delete is implemented. Those features remain **TBD / Requires Technical Validation** until confirmed by the actual build and testing.

---

## 25. Troubleshooting

### SnapData says offline AI is not ready

Open **Settings → AI Model** and complete the required setup. Initial setup may require connectivity.

### AI setup stops or fails

Check the requirement reported by the app, such as connectivity or storage, then retry. If the exact requirement is not shown, the final validated build determines the recovery behavior.

### The scan is hard to read

Capture the document again so the text is clearly visible. SnapData's preprocessing pipeline is designed to prepare images for OCR, but no OCR system is guaranteed to read every source perfectly.

### Extracted data is incorrect

Review the result and correct the affected field or table before saving. User corrections are authoritative for later reopen and export.

### A PDF/image cannot be imported

The file may be unsupported or corrupt. Select another file or verify the source file before retrying.

### Export failed

Retry the export. The saved structured result should remain intact even when export generation fails.

### Sharing failed

The export should remain available. Try another Android sharing destination if one is available.

### Storage is full

Free local device storage and retry. The exact space required by the model, documents, temporary processing files, and exports is **Requires Technical Validation**.

### A saved document is missing or cannot be reopened

Use the app's displayed recovery state. The approved specifications require clear handling of missing/corrupt records rather than a false successful reopen.

---

## 26. FAQ

### Does SnapData require internet every time I process a document?

No. After the required AI model setup is complete, the core document-processing workflow is intended to work offline. Initial AI setup may require internet access.

### Does SnapData upload my documents to the cloud for normal processing?

The MVP is designed to process the core workflow locally and does not require cloud upload or a backend API for normal document processing.

### Can I correct OCR or AI mistakes?

Yes. The review/edit workflow exists specifically so you can inspect and correct extracted fields and tables before saving or exporting.

### Which export formats are supported?

Excel, CSV, JSON, and PDF.

### Will my corrections remain after I close and reopen a document?

A saved correction is intended to remain authoritative when the document is reopened and when it is exported.

### Can I process a PDF and an image?

Yes. PDF and image input are core supported acquisition paths, along with camera capture.

### Does SnapData guarantee perfect OCR/AI accuracy?

No. The project explicitly keeps the user in the review loop because OCR and AI extraction may contain errors. Confidence information is shown where the processing pipeline produces it.

### What exact AI model does SnapData use?

The final AI model is **TBD / Requires Technical Validation** in the approved technical baseline. This guide intentionally does not invent a model name or specifications.

### What Android version do I need?

The project confirms Android as the target platform, but the minimum Android version has not yet been finalized. **TBD.**

### Does SnapData have PIN or fingerprint protection?

Do not assume this feature is available. PIN/biometric controls remain **TBD / Requires Technical Validation** until confirmed by the implemented and tested build.

### Can I share exported files?

Yes, where Android sharing is available in the validated build. Sharing uses the Android-supported sharing boundary.

### Can I delete saved documents?

Yes. Document deletion is part of the local history workflow.

---

## 27. Basic Usage Workflow

The normal workflow is:

```text
Document Input
      ↓
OCR
      ↓
AI Analysis
      ↓
Structured Data
      ↓
Review / Edit
      ↓
Save
      ↓
Export
      ↓
History
```

### Step-by-step

**1. Document Input**  
Choose **Scan Document** or **Import Document**.

**2. Acquisition and Validation**  
Capture or select the document and confirm it is suitable for processing.

**3. Image Preprocessing**  
SnapData prepares the document for OCR using the supported preprocessing pipeline.

**4. OCR**  
SnapData extracts machine-readable text from the document.

**5. AI Analysis**  
Offline AI analyzes the available document evidence when the model is ready.

**6. Structured Data**  
SnapData presents detected document type, fields, tables, and other supported result information.

**7. Review / Edit**  
Check the extracted values and correct anything that is wrong.

**8. Save**  
Save the reviewed result. The saved user-corrected data becomes authoritative.

**9. Export**  
Export the saved result as Excel, CSV, JSON, or PDF.

**10. History**  
Return later through History to reopen or delete the saved document.

This matches the approved end-to-end acceptance flow: acquisition → preprocessing → OCR → AI → structured result → review/edit → save → export → history.

---

## Quick Reference

| Task | Where to start |
|---|---|
| Scan a paper document | Home → **Scan Document** |
| Import a PDF | Home → **Import Document** |
| Import an image | Home → **Import Document** |
| Set up offline AI | Settings → **AI Model** |
| Review extracted data | Open the processing result |
| Correct a field | Results → **Edit** |
| Correct a table | Results → **Edit** → Table |
| Save reviewed data | Editor → **Save** |
| Export data | Results/Editor → **Export** |
| Share an exported file | Export result → Android **Share** |
| Reopen a saved document | **History** → Select document |
| Delete a saved document | **History** → Delete |
| Change theme/language/storage options | **Settings** |

---

## Source Alignment

This user guide is based only on the approved SnapData project baseline, including the PRD, SRS, TRD, Architecture, UI/UX, Frontend, AI/OCR, Document Processing, Data Schema, Database, Export, Testing, Security & Privacy, API, Requirements Traceability, original project specification, and supplied workflow diagram.

The approved sources confirm the Android/local-first product direction, camera/PDF/image input, preprocessing, OCR, offline AI after required setup, structured extraction, user review/editing, local persistence, history, export, sharing, and error/recovery behavior. They also explicitly leave several implementation-specific details open, including the exact AI model/runtime, Android version/device matrix, some limits, and certain security mechanisms. This guide does not promote those unresolved items to confirmed features.

**End of `SnapData_USER_GUIDE_v1.0.md`**
