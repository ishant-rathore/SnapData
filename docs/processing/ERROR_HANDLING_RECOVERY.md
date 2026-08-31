# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Error Handling, Failure Recovery & Resilience Specification

**Filename:** `SnapData_ERROR_HANDLING_RECOVERY_v1.0.md`  
**Version:** 1.0  
**Status:** Engineering / Reliability Baseline  
**Date:** 30 August 2026  
**Target:** Android application; offline-first/local processing  
**MVP Backend:** None required  
**MVP REST API:** None required  
**MVP Cloud Database:** None required

> **Source discipline:** This document is derived only from the approved SnapData project baseline. It defines error and recovery behavior already supported by those sources and explicitly preserves open decisions as **TBD**, **PROPOSED**, or **REQUIRES TECHNICAL VALIDATION**. It does not invent implementation details, numeric limits, model identities, Android APIs, encryption mechanisms, or recovery guarantees that are not source-backed.

---

## 0. Purpose

This document defines SnapData's complete error-handling, failure, recovery, integrity, diagnostics, and user-state strategy across the approved workflow:

```text
Camera / PDF / Image Input
          ↓
Document Acquisition
          ↓
Validation
          ↓
Image Pre-processing
          ↓
OCR
          ↓
Offline AI Analysis
          ↓
Document / Field / Table Extraction
          ↓
Structured Data Validation
          ↓
User Review & Edit
          ↓
Local Save (authoritative result)
          ↓
History / Reopen
          ↓
Export: Excel / CSV / JSON / PDF
          ↓
Android Share / Open
```

The original project specification and workflow diagram establish the same end-to-end sequence from acquisition through preprocessing, OCR, offline AI, structured data, review/editing, local storage, export, and history. The approved technical baseline additionally establishes explicit error/state management, partial-result handling, local-first processing, and preservation of user-confirmed data.

---

## 1. Source of Truth

The following approved SnapData artifacts are authoritative for this document:

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_UI_UX_v1.0.md`
6. `SnapData_FRONTEND_v1.0.md`
7. `SnapData_DATABASE_v1.0.md`
8. `SnapData_DATA_SCHEMA_v1.0.md`
9. `SnapData_AI_OCR_v1.0.md`
10. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
11. `SnapData_EXPORT_v1.0.md`
12. `SnapData_TESTING_v1.0.md`
13. `SnapData_SECURITY_PRIVACY_v1.0.md`
14. `SnapData_BUILD_RELEASE_v1.0.md`
15. `SnapData_IMPLEMENTATION_PLAN_v1.0.md`
16. `SnapData_API_SPECIFICATION_v1.0.md`
17. `SnapData_CODE_ARCHITECTURE_v1.0.md`
18. `SnapData_DEVELOPMENT_GUIDELINES_v1.0.md`
19. `SnapData_REQUIREMENTS_TRACEABILITY_v1.0.md`
20. `SnapData_TEST_CASES_v1.0.md`
21. `SnapData_AI_PROMPT_SPECIFICATION_v1.0.md`
22. Original SnapData project specification PDF
23. SnapData workflow diagram

### 1.1 Baseline principles carried into this document

- Core document processing is local/offline after the required AI model setup.
- The MVP does not require a backend, REST API, or cloud database.
- Imported documents, filenames, OCR output, AI output, model resources, and export inputs are untrusted until validated.
- AI output is candidate data, never a trusted instruction source.
- User-corrected, saved structured data is authoritative for reopen and export.
- Sensitive document content does not belong in routine diagnostics/logs.
- SQLite and Android-local file storage are separate recovery/security boundaries.
- Existing committed user data must remain intact through recoverable failures and interruptions.
- Exact interruption/resume behavior is **TBD** under SRS `TBD-012`.
- Exact document/resource limits, AI model/runtime, preprocessing algorithms, Android APIs, and advanced security mechanisms remain open where the source baseline says so.

---

## 2. Status Vocabulary

| Status | Meaning |
|---|---|
| **CONFIRMED** | Explicitly established by the approved source baseline. |
| **PROPOSED** | Recommended engineering behavior that does not claim to be an already-finalized implementation fact. |
| **TBD** | Decision is not yet made. |
| **REQUIRES TECHNICAL VALIDATION** | Product intent is established, but the exact implementation/feasibility/evidence is not yet confirmed. |
| **OPTIONAL** | Allowed capability that is not mandatory for the current baseline. |
| **REJECTED** | Explicitly outside the current MVP architecture/baseline. |

---

# 3. Error-Handling Architecture

## 3.1 Architectural flow

SnapData should normalize low-level failures into stable application-level error/state representations rather than passing provider-specific exceptions directly to the UI.

```text
Low-Level Component
        ↓
Adapter / Boundary
        ↓
Error Normalization
        ↓
Application Error Model
        ↓
Processing / UI State
        ↓
User Message + Recovery Action
        ↓
Safe Diagnostic Event
```

The System Architecture identifies an **Error Manager** as the component responsible for normalizing and routing low-level/component errors and a **Processing State Manager** for maintaining valid processing states. Both are source-backed architectural components; exact code structure remains implementation-specific.

## 3.2 Required boundaries

| Boundary | Typical failures | Required handling |
|---|---|---|
| Acquisition → Validation | unavailable camera, inaccessible file, unsupported file | normalize and present recoverable input error |
| Validation → Preprocessing | corrupt/invalid content, resource feasibility failure | reject or fail truthfully before processing |
| Preprocessing → OCR | decode/process failure | stop/branch; retry or return safely |
| OCR → AI | empty OCR, handoff failure | no false-success state; preserve recoverable OCR where safe |
| AI → Structured Data | model unavailable, runtime failure, malformed output | validate, normalize, reject/partial result where safe |
| Structured Data → Review | schema/validation failure | no invalid result becomes authoritative |
| Review → SQLite | transaction/storage failure | rollback failed transaction; preserve committed data |
| SQLite → Export | missing/corrupt authoritative result | do not export stale/non-authoritative data |
| Export → Share | share-target/access failure | keep generated local export; report truthful share failure |
| App lifecycle → Processing | interruption/crash/background termination | preserve committed data; exact resume policy TBD |

## 3.3 Non-negotiable integrity rules

1. A failed operation must not be reported as successful.
2. No invalid structured result becomes authoritative storage.
3. User-confirmed saved corrections must not be overwritten by subsequent AI output.
4. Existing committed records must survive recoverable failures.
5. Partial valid information should be preserved where the product contract permits it.
6. Transient sensitive information should have an explicit lifecycle and should not leak into logs.
7. Failure messages must be understandable without exposing technical internals.
8. Offline capability must remain truthful: a missing/unready model must never appear ready.

---

# 4. Error Classification and Severity

## 4.1 Severity model

The sources establish P0/P1/P2 priorities and release gates, but do not define a separate universal runtime severity taxonomy. The following mapping is therefore a **PROPOSED operational classification**, intended to support engineering triage without changing product requirements.

| Severity | Operational meaning | Examples | Release implication |
|---|---|---|---|
| **S0 / Critical** | Integrity/privacy or false-success condition threatens core trust | user edit overwritten; committed data corruption; unsafe path access; sensitive log leakage; false offline success | immediate investigation; release-blocking when applicable |
| **S1 / High** | Core workflow cannot complete safely for a supported scenario | repeated OCR failure; AI unavailable when required; storage failure; export failure | release blocker when it breaks P0 path |
| **S2 / Medium** | Recoverable feature/state failure with safe fallback | table extraction unavailable while valid fields remain | may continue with warning/partial result |
| **S3 / Low** | Non-core or presentation-level problem | non-critical diagnostic or UI state defect | track and fix according to release priority |

### 4.2 Classification dimensions

Every error should be classified along three dimensions where supported:

- **Origin:** input, permission, resource, processing, model, validation, persistence, export, share, lifecycle.
- **Recoverability:** retryable, recoverable by user action, partial-result capable, non-recoverable for current operation.
- **Data integrity impact:** none, transient only, uncommitted state, committed state at risk.

`recoverable` is explicitly present in the data-schema error model as a proposed capability.

---

# 5. Global Error-Handling Strategy

## 5.1 Detection

Failures should be detected at the earliest boundary that can make a reliable determination. Do not continue a pipeline stage when its required input contract is invalid.

## 5.2 Normalize

Map provider/implementation exceptions to the stable error catalogue (`ERR-001` … `ERR-018`) and include a stage when available.

## 5.3 Preserve

Before recovery or navigation, preserve any safe intermediate state that the document-processing contract permits. Existing committed records take precedence over transient recovery artifacts.

## 5.4 Recover

Use the narrowest safe recovery action:

```text
Retry current stage
        ↓ if not recoverable
Return to last stable state
        ↓ if useful partial data survives
Expose partial/limited result
        ↓ if persistent data exists
Preserve/reload committed record
        ↓
Never fabricate success
```

## 5.5 Fail closed

When validity cannot be established, SnapData must not treat the operation as completed successfully.

Examples:

- unknown model readiness → unavailable, not ready;
- malformed AI output → invalid candidate, not saved result;
- failed transaction → rollback, not partial commit;
- unsafe path → reject, not attempt arbitrary write;
- incomplete export → failure, not success file state.

---

# 6. User-Friendly Error Messages

The approved UI/UX baseline specifies a three-part error pattern:

```text
Problem
  ↓
Plain-language explanation
  ↓
Recovery action
```

Rules:

- Do not use raw technical exceptions as the primary user message.
- Do not expose file paths, SQL errors, stack traces, prompts, OCR text, or sensitive document fields.
- Keep the primary action obvious.
- Preserve data before navigation when that data is already committed or safely recoverable.
- Do not confuse an empty state with a failure state.
- Do not show completion when the underlying operation failed.

### 6.1 Approved error catalogue

| ID | Problem | User-facing message | Primary recovery |
|---|---|---|---|
| `ERR-001` | Camera permission denied | **Camera access is required to scan.** | Retry / system permission path where appropriate / Back |
| `ERR-002` | Camera unavailable / capture failed | **A usable image could not be captured.** | Retry / Cancel |
| `ERR-003` | Unsupported file | **The selected file is not supported.** | Choose another file |
| `ERR-004` | Corrupt PDF | **The PDF could not be read.** | Choose another PDF |
| `ERR-005` | Invalid image | **The image could not be read.** | Reselect / recapture |
| `ERR-006` | Preprocessing failure | **The document could not be prepared for OCR.** | Retry / Back |
| `ERR-007` | OCR failure | **Text could not be read from the document.** | Retry with a better source / Back |
| `ERR-008` | Empty OCR | **No usable text was found.** | Recapture / reselect a better document |
| `ERR-009` | AI model unavailable | **Offline AI needs to be set up before processing.** | Open Model Manager |
| `ERR-010` | AI processing failure | **AI analysis could not complete.** | Retry / Back |
| `ERR-011` | Structured extraction failure | **Results could not be organized safely.** | Retry / Review available source data |
| `ERR-012` | Table extraction failure | **A table could not be structured reliably.** | Continue with other data / Retry |
| `ERR-013` | Local storage failure | **Data could not be saved or retrieved.** | Retry; preserve in-memory edits where safe |
| `ERR-014` | Insufficient storage | **There is not enough device space.** | Free storage / Retry |
| `ERR-015` | Export failure | **The selected output could not be generated.** | Retry / choose another format |
| `ERR-016` | Share failure | **The file could not be shared.** | Return; keep generated file |
| `ERR-017` | Processing cancelled | **Processing was cancelled.** | Return to the valid previous state |
| `ERR-018` | Application interruption | **Processing stopped unexpectedly.** | Recover safely; exact resume behavior **TBD** |

These identifiers/messages/recovery actions are source-backed by the UI/UX and SRS baselines.

---

# 7. Document Import / File Errors

## 7.1 Unsupported file — `ERR-003`

**Cause:** The selected file is outside the supported input contract.

**Detection:** Input validation checks the selected document against supported input types/structure.

**User message:** “The selected file is not supported.”

**System behavior:** Stop before downstream processing.

**Recovery:** Ask the user to choose another supported file.

**Logging:** Record sanitized operation/stage/error metadata only.

**Security:** Treat filename and file content as untrusted. Never use document content or AI output to construct paths.

**Test:** `TEST`/file-import negative case; trace to `ERR-003` and acquisition requirements.

## 7.2 Corrupt PDF — `ERR-004`

**Cause:** PDF cannot be read/parsed into usable processing input.

**Detection:** PDF acquisition/validation layer.

**User message:** “The PDF could not be read.”

**System behavior:** Reject current input or stop before preprocessing.

**Recovery:** Choose another PDF.

**Logging:** Error code, stage, sanitized file category; no document content.

**Security:** Treat malformed PDF content as untrusted input; do not allow unsafe path/file behavior.

**Test:** malformed/corrupt PDF input test.

## 7.3 Invalid image — `ERR-005`

**Cause:** Image cannot be decoded or does not provide usable input.

**Detection:** Image validation/decoder.

**User message:** “The image could not be read.”

**System behavior:** Reject current image.

**Recovery:** Reselect or recapture.

**Logging:** Error code/stage only; no raw pixels or OCR content.

**Security:** Untrusted image payload; resource-exhaustion controls remain subject to validated limits.

**Test:** malformed image case.

---

# 8. Camera and Permission Errors

## 8.1 Camera permission denied — `ERR-001`

**Cause:** Required camera access is not granted.

**Detection:** Camera acquisition boundary.

**User message:** “Camera access is required to scan.”

**System behavior:** Do not attempt capture without required access.

**Recovery:** Retry, follow the appropriate system permission path, or Back.

**Logging:** Permission outcome, operation/stage; no camera frames.

**Security:** Exact Android permission/API behavior is **REQUIRES TECHNICAL VALIDATION** from the generated Android project.

**Test:** permission denial and re-entry test.

## 8.2 Camera unavailable/capture failure — `ERR-002`

**Cause:** A usable image cannot be captured.

**Detection:** Camera component/acquisition adapter.

**User message:** “A usable image could not be captured.”

**System behavior:** No processing start until usable acquisition exists.

**Recovery:** Retry or Cancel.

**Logging:** Camera operation state, error code, stage, timing where safe.

**Security:** Avoid persisting unintended camera frames; exact camera lifecycle behavior is implementation-specific.

**Test:** camera capture failure test.

---

# 9. Image Preprocessing Errors

## 9.1 Preprocessing failure — `ERR-006`

**Cause:** Crop/rotation/perspective/noise/brightness or other approved preprocessing step cannot produce a usable input.

**Detection:** Preprocessing stage evaluates its output contract.

**User message:** “The document could not be prepared for OCR.”

**System behavior:** Stop or branch appropriately; do not send unusable data to OCR.

**Recovery:** Retry or Back. If the source itself remains usable, return to acquisition rather than corrupting the source.

**Logging:** Stage, operation, error code, sanitized resource category.

**Security:** Preprocessing receives untrusted input and must not disclose document contents in diagnostics.

**Test:** preprocessing fault-injection and unusable-output cases.

**Open item:** Exact preprocessing algorithms remain **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 10. OCR Errors

## 10.1 OCR failure — `ERR-007`

**Cause:** Approved local OCR capability fails during execution.

**Detection:** OCR adapter/stage result.

**User message:** “Text could not be read from the document.”

**System behavior:** Mark OCR failure; do not pass invalid/unknown text as a successful OCR result.

**Recovery:** Retry with improved source where practical or Back.

**Logging:** Error code, stage, timing/resource category, model/engine identifier only where safe.

**Security:** Raw OCR text must not enter routine diagnostics.

**Test:** forced OCR failure and integration failure path.

## 10.2 Empty/unusable OCR — `ERR-008`

**Cause:** OCR completes, but output is empty or unusable for downstream analysis.

**Detection:** OCR output usability validation.

**User message:** “No usable text was found.”

**System behavior:** Do not present empty OCR as successful extraction.

**Recovery:** Recapture/reselect better input.

**Logging:** Stage + output classification such as `EMPTY`, not raw text.

**Security:** Do not log the OCR payload to prove emptiness.

**Test:** blank, low-content, and unusable OCR output cases.

**Related requirements:** `FR-012`, `AC-003`.

---

# 11. AI / Model Errors

## 11.1 Model unavailable — `ERR-009`

**Cause:** Required offline AI model is not ready/available.

**Detection:** Model manager readiness state.

**User message:** “Offline AI needs to be set up before processing.”

**System behavior:** Do not attempt or pretend to complete AI processing.

**Recovery:** Open model setup/readiness flow.

**Logging:** Model state, model identifier/version only if safe, no document content.

**Security:** Incomplete/corrupt/unverified model artifacts must not be treated as ready.

**Test:** model-not-ready test, including processing attempt without a ready model.

**Open items:** Exact model, runtime, packaging, update/delete behavior, and download resume behavior remain **TBD / REQUIRES TECHNICAL VALIDATION** where specified by the AI/OCR baseline.

## 11.2 AI processing failure — `ERR-010`

**Cause:** Local AI inference fails after model readiness.

**Detection:** AI adapter/runtime boundary.

**User message:** “AI analysis could not complete.”

**System behavior:** Stop AI success path; do not fabricate fields/tables.

**Recovery:** Retry or Back.

**Logging:** stage, error code, timing/resource category, model identifier if safe; no prompt/document text.

**Security:** AI output and input are untrusted data; no remote fallback may be silently introduced.

**Test:** forced model/runtime failure.

---

# 12. Invalid AI Output

AI output is a trust boundary. The model's response must be treated as candidate data and validated against the canonical structured-data contract before use.

## 12.1 Invalid structured output — `ERR-011`

**Cause:** AI output is malformed, incomplete beyond acceptable contract, semantically invalid, or cannot be safely organized.

**Detection:** parser/schema/semantic validation after AI inference.

**User message:** “Results could not be organized safely.”

**System behavior:** Reject invalid data from authoritative persistence. Where the document-processing contract allows it, preserve valid recoverable source/OCR context instead of discarding everything.

**Recovery:** Retry; review available source data where supported.

**Logging:** error code, validation class, field-count/shape metadata where non-sensitive; never raw model response or document text.

**Security:** Never execute instruction-like model output; never use AI output as filesystem paths, SQL, commands, or application instructions.

**Test:** malformed JSON/structure, wrong types, missing required contract elements, injection-like strings treated only as data.

## 12.2 Partial AI result principle

Where supported by the processing contract:

```text
OCR succeeds + AI fails
→ preserve OCR/intermediate state where safe
→ do not claim structured success
```

```text
AI succeeds + table extraction fails
→ preserve valid fields
→ surface table warning
→ allow partial result where contract permits
```

```text
Some values unresolved
→ keep resolved values
→ unresolved values remain missing/uncertain
→ never fabricate
```

This behavior is source-backed by the Data Schema, Document Processing, AI/OCR, and Testing baselines.

---

# 13. Schema / Validation Errors

Structured data must pass validation before it can become authoritative saved data.

## 13.1 Validation strategy

```text
Raw AI Candidate
      ↓
Parse
      ↓
Schema Validation
      ↓
Semantic / consistency checks where defined
      ↓
Canonical Structured Result
      ↓
Review / Edit
      ↓
Save
```

### Failure policy

- Invalid candidate → `ERR-011` or the closest stable stage error.
- Unresolved field → missing/uncertain, not fabricated.
- Invalid table structure → `ERR-012` where table extraction specifically fails.
- Invalid data must never silently pass into authoritative storage.

### Logging

Record validation failure class/count where safe; never log sensitive field values or raw AI responses.

### Test

Malformed structured output, wrong-type value, missing field, invalid table shape, Unicode, and unresolved-value tests.

---

# 14. Table Extraction Errors

## `ERR-012` — Table extraction failure

**Cause:** A detected table cannot be structured reliably.

**Detection:** Table parsing/validation stage.

**User message:** “A table could not be structured reliably.”

**System behavior:** Preserve other valid structured information if the partial-result contract permits it; mark the table portion incomplete rather than silently dropping it.

**Recovery:** Continue with available fields or retry.

**Logging:** table failure category, stage, error code; no table contents.

**Security:** Table cell values may be sensitive and must not be logged.

**Test:** table extraction failure and mixed success scenarios.

**Open item:** Exact complex-table export fidelity remains `TBD-011` / **REQUIRES TECHNICAL VALIDATION**.

---

# 15. Database / Local Storage Errors

SQLite is the intended local database. Android-local file storage is a separate persistence boundary.

## 15.1 Local storage failure — `ERR-013`

**Cause:** Open/read/write/transaction/constraint/persistence failure.

**Detection:** Persistence/repository boundary.

**User message:** “Data could not be saved or retrieved.”

**System behavior:** No false save. A failed transaction must not leave a misleading committed state. Existing committed records must remain intact.

**Recovery:** Retry; preserve in-memory edits where safe.

**Logging:** sanitized database operation, stage, error code, transaction outcome; never SQL data containing document content.

**Security:** Do not expose SQL internals, paths, schema details, or sensitive values.

**Test:** injected save failure, read failure, transaction failure, constraint failure, restart/reopen.

## 15.2 Database/file boundary

The database contains application/domain persistence; physical file storage is separately managed. Deleting or saving one does not automatically prove the other succeeded.

Recovery must reconcile the two boundaries according to the implemented operation. Exact low-level file API behavior remains **REQUIRES TECHNICAL VALIDATION**.

---

# 16. Export Errors

The export layer consumes the **saved authoritative structured result**. It must not export an unsaved/stale AI candidate when a user has already confirmed corrections.

## 16.1 Export failure — `ERR-015`

**Cause:** XLSX/CSV/JSON/PDF generation fails.

**Detection:** Export adapter/serializer/file-generation layer.

**User message:** “The selected output could not be generated.”

**System behavior:** No false export-success state. Existing authoritative data remains unchanged.

**Recovery:** Retry or choose another supported format.

**Logging:** format, stage, error code, operation identifier; no exported document content.

**Security:** protect against unsafe filenames/path traversal; do not leak metadata or sensitive content in logs.

**Test:** exporter failure injection for each approved output type where implementation supports it.

## 16.2 Source-of-truth protection

```text
User edits
    ↓
Save succeeds
    ↓
Saved authoritative result
    ↓
Export
```

Export must not silently return to the previous AI result.

## 16.3 Partial export

No separate product requirement authorizes claiming a partially generated file is a successful export. Therefore, unless a future approved contract defines resumable/partial export behavior, failure should be reported as `ERR-015` and the current authoritative data preserved.

---

# 17. File / Share Errors

## 17.1 Share failure — `ERR-016`

**Cause:** Generated file exists, but the Android sharing/opening action does not complete.

**Detection:** Share boundary.

**User message:** “The file could not be shared.”

**System behavior:** Keep the locally generated export; do not delete it just because sharing failed.

**Recovery:** Return to the export/result context.

**Logging:** format/file-generation state, share outcome, error code; no file contents.

**Security:** Sharing is an explicit privacy boundary. Only the intended generated file should be handed to the sharing mechanism.

**Open item:** Exact share URI/access-grant mechanism is **REQUIRES TECHNICAL VALIDATION**.

**Test:** no-share-target, share rejection, inaccessible share target, repeated share attempt.

---

# 18. Offline / Network-Related Errors

## 18.1 Core rule

The MVP core document workflow must not depend on network connectivity after required AI model setup.

```text
Model Ready
   ↓
Network OFF
   ↓
Acquire → Preprocess → OCR → AI → Review → Save → Export
```

The SRS establishes local processing/storage priority and no mandatory cloud upload. The implementation plan and acceptance suite explicitly require offline core behavior.

## 18.2 Network unavailable during model setup

This is a setup-stage condition rather than a normal document-processing failure. The exact model download mechanism and download recovery behavior remain **TBD / REQUIRES TECHNICAL VALIDATION**.

Required truthfulness:

- Do not mark the model ready before successful completion/validation.
- Do not treat a partial/corrupt artifact as usable.
- Do not upload document content as a hidden workaround.

## 18.3 Unexpected network dependency

If the core workflow attempts network access after setup, treat this as an implementation defect/security finding, not as an expected product fallback.

**Test:** Wi-Fi off, mobile data off, airplane mode, network transition during processing, and network audit.

---

# 19. Low-Storage / Resource Errors

## 19.1 Insufficient storage — `ERR-014`

**Cause:** Device lacks sufficient space for the current operation/resource.

**Detection:** Resource checks or failed storage operation.

**User message:** “There is not enough device space.”

**System behavior:** Stop the operation safely; do not report completion.

**Recovery:** Free device storage and retry.

**Logging:** resource category, stage, error code; do not log document content.

**Security:** Do not expose unrelated filesystem information.

**Test:** controlled low-storage/failed-write test where practical.

## 19.2 Memory / processing-resource failures

The sources require resource management and testing but do not establish a universal runtime error code for all memory pressure cases. Therefore:

- map to the most accurate existing stage-level error where supported;
- preserve existing committed records;
- do not invent numeric memory thresholds;
- exact resource ceilings are **TBD / REQUIRES TECHNICAL VALIDATION**.

## 19.3 Large-document behavior

Exact maximum document size/page count is `TBD-003` / **REQUIRES TECHNICAL VALIDATION**. Until validated, error handling must not advertise unsupported limits as hard product guarantees.

---

# 20. Processing Cancellation

## `ERR-017` — Processing cancelled

**Cause:** User or system requests cancellation while processing is active.

**Detection:** Processing State Manager / cancellation token or equivalent implementation boundary.

**User message:** “Processing was cancelled.”

**System behavior:** Stop launching further work, release resources, and do not report successful completion.

**Recovery:** Return to the valid previous state or another safe state defined by the implemented navigation model.

**Logging:** cancellation event, stage, operation id, duration where safe; no document content.

**Security:** Clean transient sensitive data according to the implemented cleanup policy.

**Test:** cancellation during preprocessing, OCR, AI, save/export where supported.

The AI/OCR baseline explicitly requires cancellation to stop further work, release resources, and avoid successful completion state.

---

# 21. Application Crash / Recovery / Interruption

## `ERR-018` — Application interruption

**Cause:** Application lifecycle interruption, unexpected termination, or other interruption that stops an active operation.

**Detection:** On restart/resume, persisted operation state and committed records are evaluated where such state exists.

**User message:** “Processing stopped unexpectedly.”

**System behavior:** Recover safely and ensure existing committed records remain valid and accessible.

**Recovery:** Exact resume/replay behavior is **TBD** under `TBD-012`.

**Logging:** interruption event, last known stage, operation identifier, sanitized state; no sensitive content.

**Security:** Do not store raw documents/prompts merely to support crash debugging.

**Test:** terminate app during each major stage, then relaunch and verify committed data integrity.

### 21.1 Confirmed recovery invariant

Regardless of the eventual resume mechanism:

```text
Interrupted operation
       ↓
Existing committed data
       ↓
MUST remain valid and accessible
```

This is explicitly required by `NFR-006` and `NFR-007`.

### 21.2 Not assumed

The following are intentionally **TBD** until implementation validation:

- automatic pipeline resume;
- page-level resume;
- stage checkpoint persistence;
- user choice between resume/restart;
- recovery of unsaved edits after force-stop;
- recovery of an interrupted export.

---

# 22. Data Corruption / Recovery

## 22.1 Database corruption

The SRS requires a clear unavailable/corrupt state when stored data is externally corrupted.

**Behavior:**

1. Detect integrity/read failure.
2. Do not silently manufacture a replacement record.
3. Present a truthful unavailable/corrupt state.
4. Preserve unaffected committed records where possible.
5. Use any approved recovery path that actually exists in the implementation.

**Backup/restore:** Outside the current MVP database baseline unless separately approved; therefore no automatic backup/restore mechanism is claimed here.

## 22.2 File corruption

If a physical source file is unreadable:

- surface the relevant input/file error;
- do not silently substitute another file;
- retain unaffected database records.

## 22.3 Model corruption

An incomplete/corrupt/unverified AI model is not ready for offline processing.

**Recovery:** Return to model setup/readiness behavior; exact update/delete/re-download semantics remain TBD.

## 22.4 Result corruption

If a stored structured result fails schema/parsing/integrity validation:

- do not export it as valid;
- do not overwrite it with a fresh AI result without explicit user-controlled workflow;
- classify the record as unavailable/corrupt if necessary;
- preserve the committed record bytes/state where technically possible.

---

# 23. Transaction Rollback

## 23.1 Save transaction principle

Persistence must behave transactionally at the repository/database boundary.

```text
Validate authoritative result
        ↓
Begin transaction
        ↓
Write required state
        ↓
Commit
     ↙     ↘
Success   Failure
  ↓           ↓
Saved      Rollback
```

A failed save must not produce a success indication.

## 23.2 Rollback rules

- Roll back failed database transactions.
- Preserve prior committed records.
- Do not partially overwrite user-corrected values.
- Keep transient recovery artifacts separate from authoritative saved state.
- Reconciliation with physical files must follow the actual implementation boundary.

The database specification explicitly requires transaction-safe deletion/persistence concepts and states that failed migrations should have a safe recovery path without destructive data loss.

## 23.3 Migrations

Database migrations must:

- have unique versions;
- preserve existing user data wherever practical;
- be tested on representative pre-migration datasets;
- avoid destructive transformations unless explicitly approved;
- record resulting schema version.

Automatic reversible migration rollback is **not required** by the source baseline. Forward migration safety and a recovery path are required instead.

---

# 24. Retry and Recovery Rules

## 24.1 General retry policy

Retry is appropriate only when the failed operation is safe to repeat and the source contract provides a meaningful retry path.

| Error | Retry posture | Reason |
|---|---|---|
| `ERR-001` | Retry after permission state changes | Access state may change |
| `ERR-002` | Retry | Capture can succeed on next attempt |
| `ERR-003` | No repeated retry of same bad input | Choose supported input |
| `ERR-004` | Prefer replacement input | Corrupt PDF is unlikely to improve by repetition |
| `ERR-005` | Reselect/recapture | Invalid source remains invalid |
| `ERR-006` | Retry / new source | Depends on processing condition |
| `ERR-007` | Retry / better source | OCR may be transient or input-sensitive |
| `ERR-008` | Better source | Empty OCR is an input-quality condition |
| `ERR-009` | Setup/readiness action | Model must become ready first |
| `ERR-010` | Retry | Runtime/inference failure may be transient |
| `ERR-011` | Retry; inspect available source | Candidate output invalid |
| `ERR-012` | Retry / continue without table where allowed | Partial-result path may be valid |
| `ERR-013` | Retry after storage/state recovery | Persistence must succeed before success |
| `ERR-014` | Retry after storage is freed | Resource prerequisite changed |
| `ERR-015` | Retry / alternate format | Format-specific generation may fail |
| `ERR-016` | Retry share, preserve local file | Share is downstream of successful export |
| `ERR-017` | No automatic continuation assumed | Cancellation is intentional |
| `ERR-018` | Recovery/restart; resume semantics TBD | Exact resume is not finalized |

## 24.2 Idempotence and duplicate operations

The sources require safe behavior but do not establish a complete idempotency-key protocol for the local MVP. Therefore:

- avoid duplicate commits where the repository can detect them;
- do not invent an API-style idempotency contract for the MVP;
- exact duplicate-operation handling is **REQUIRES TECHNICAL VALIDATION** where implementation-specific.

---

# 25. Temporary-File Cleanup

## 25.1 Policy

Transient processing artifacts may include intermediate images, temporary exports, model setup fragments, or other local working resources depending on implementation. The exact Android filesystem locations and cleanup schedule are **REQUIRES TECHNICAL VALIDATION**.

The confirmed privacy principle is:

> Transient sensitive data should have an explicit end-of-life.

## 25.2 Cleanup triggers

Where technically supported, cleanup should occur after:

- successful stage completion when the artifact is no longer needed;
- cancellation;
- unrecoverable processing failure;
- failed export after any partial artifact is safely discarded;
- application restart recovery of known abandoned transient state.

## 25.3 Cleanup safety

- Never delete a file that has become the authoritative user-facing export.
- Never delete authoritative saved data as part of transient cleanup.
- Do not claim guaranteed physical destruction on flash storage.
- Temporary cleanup must not delete another document's data.

Exact cleanup behavior is an open security/technical decision in the approved baseline.

---

# 26. Logging and Diagnostics

## 26.1 Diagnostic goals

Diagnostics should support:

- error localization;
- stage identification;
- retry/recovery analysis;
- performance/resource diagnosis;
- release regression analysis;
- security/privacy auditing.

## 26.2 Safe diagnostic event structure

A conceptual safe event may contain:

```text
event_code
stage
operation_id
recoverable
severity
processing_state
retry_count
resource_category
model_identifier/version (where safe)
export_format (where applicable)
timestamp/duration (where safe)
```

The exact implementation schema is not fixed by the product requirements.

## 26.3 Never log routinely

- raw document content;
- OCR text;
- sensitive extracted fields;
- raw images;
- private AI prompts/responses;
- passwords/secrets/tokens;
- database credentials;
- unnecessary full filesystem paths;
- model binary contents.

## 26.4 Example

### Acceptable

```text
event=AI_OUTPUT_INVALID
stage=AI
recoverable=false
operation_id=<opaque>
```

### Not acceptable

```text
event=AI_OUTPUT_INVALID
full_ocr="..."
account_number="..."
prompt="...private document..."
```

## 26.5 Error logging principle

Logging an error is not permission to copy the data that caused it.

---

# 27. Security / Privacy-Safe Error Handling

## 27.1 Threat boundaries

Every one of the following is untrusted until validated:

- imported PDFs;
- imported images;
- camera input;
- filenames;
- file references;
- OCR output;
- AI output;
- export filenames/options;
- model artifacts;
- any model-download response used during setup.

## 27.2 Required security properties

1. Never use AI output as a filesystem path.
2. Never execute document text as instructions.
3. Validate file types and structures.
4. Protect against path traversal.
5. Prevent unsafe overwrite.
6. Do not expose sensitive document content in errors/logs.
7. Do not mark incomplete/corrupt/unverified models as ready.
8. Keep core processing local.
9. Treat external sharing as an explicit privacy boundary.
10. Preserve user-confirmed saved data as authoritative.

## 27.3 Error disclosure boundary

User-facing errors should disclose enough to recover, but not enough to expose:

- internal stack traces;
- SQL implementation details;
- sensitive document values;
- security-sensitive paths;
- secrets/tokens;
- internal AI prompts.

## 27.4 Security features intentionally not assumed

The current security baseline explicitly does not claim finalized:

- encryption at rest;
- key management;
- PIN lock;
- biometric lock;
- secure-delete implementation;
- external crash-reporting provider;
- Android backup policy;
- exact share URI/access-grant mechanism.

Those remain **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 28. UI Loading / Error / Empty States

## 28.1 Semantic state separation

The UI must distinguish:

```text
LOADING / PROCESSING
ERROR / FAILURE
EMPTY / NO CONTENT
SUCCESS / VALID RESULT
PARTIAL / VALID WITH LIMITATIONS (where supported)
CANCELLED
INTERRUPTED
```

An empty state is not an error. Examples from the approved UX baseline:

- “No saved documents yet.”
- “No saved documents will appear here.”
- “No structured fields were detected.”
- “No tables were detected in this document.”
- “Offline AI needs to be set up before processing.”

## 28.2 Processing-state clarity

`NFR-009` requires the current processing stage to be understandable. A failure must not leave a stale progress indicator that looks like continued processing or successful completion.

## 28.3 Error-state requirements

Every core error state should expose:

- what happened;
- what the user can do next;
- whether any valid data remains;
- whether retry is available.

## 28.4 Unsaved edits

The UI baseline explicitly represents unsaved edit warnings. Error/recovery handling must not silently discard user edits.

---

# 29. Error Object / Error Code Contract

The Data Schema defines the following safe error structure:

```text
Error
├── code        : required string
├── message     : required safe human/application-facing string
├── stage       : optional stage/enum
├── recoverable : optional boolean (PROPOSED)
└── details     : optional non-sensitive diagnostics (OPTIONAL)
```

## 29.1 Naming convention

The approved SRS error identifiers already use `ERR-001` through `ERR-018`. These remain the stable error-code namespace for v1.0.

### Rules

- Format: `ERR-NNN`.
- Numeric identifiers are stable across UI, processing, tests, and traceability.
- Do not repurpose an existing code for a different semantic failure without change control.
- Provider-specific exception codes may exist internally but must be mapped to the stable application error code.
- Do not encode sensitive values in error codes.

## 29.2 Error-to-stage mapping

| Stage | Primary codes |
|---|---|
| Camera / acquisition | `ERR-001`, `ERR-002` |
| File validation | `ERR-003`, `ERR-004`, `ERR-005` |
| Preprocessing | `ERR-006` |
| OCR | `ERR-007`, `ERR-008` |
| AI/model | `ERR-009`, `ERR-010` |
| Structured extraction | `ERR-011`, `ERR-012` |
| Persistence | `ERR-013`, `ERR-014` |
| Export | `ERR-015` |
| Share | `ERR-016` |
| Cancellation | `ERR-017` |
| Interruption | `ERR-018` |

---

# 30. Developer Debugging Guidance

## 30.1 Triage sequence

When a defect is reported:

```text
1. Identify ERR code
2. Identify processing stage
3. Identify processing state
4. Determine committed vs transient data
5. Reproduce with safe/synthetic input
6. Inspect sanitized diagnostics
7. Verify whether the failure is retryable
8. Verify user edit authority
9. Verify offline behavior if relevant
10. Add/repair the mapped test case
```

## 30.2 Debugging by layer

### Acquisition
Check permission/access state, file classification, capture result, and lifecycle timing.

### Preprocessing
Check whether the stage produced the expected normalized input contract; do not infer exact algorithm behavior until validated.

### OCR
Check engine invocation, output usability, and downstream handoff. Do not use private OCR text in routine logs.

### AI
Check model readiness, runtime status, resource availability, parser/schema validation, and whether the response was treated as data rather than instructions.

### Persistence
Check transaction boundaries, migration version, database/file reconciliation, and whether the authoritative result was used.

### Export
Check canonical saved input, format mapping, serializer behavior, safe path generation, and file finalization.

### Share
Check local export existence and share target/access behavior. A share failure must not erase a valid local export.

## 30.3 Failure injection

Testing should deliberately inject:

- preprocessing failure;
- OCR failure;
- model unavailable;
- AI failure;
- invalid AI output;
- table extraction failure;
- database save failure;
- insufficient storage;
- export failure;
- share failure;
- cancellation;
- lifecycle interruption.

The implementation plan specifically requires P0 processing/export/storage failure injection with preservation of previously saved authoritative data.

---

# 31. Error → Requirement → Test Traceability

The table below maps the controlled error catalogue to the approved requirement/test baseline. Where a specific test ID is implementation/test-document-specific but not explicitly recoverable from the current source excerpt, the relationship is expressed by test category rather than inventing a new identifier.

| Error | Primary requirement / contract | Verification / test coverage |
|---|---|---|
| `ERR-001` | Camera acquisition; error UX | Camera permission denied/recovery case; `AC-001` boundary |
| `ERR-002` | Camera acquisition | Camera capture failure/cancel; `AC-001` |
| `ERR-003` | File validation / supported input | Unsupported-file negative case |
| `ERR-004` | PDF input validation | Corrupt-PDF negative case |
| `ERR-005` | Image input validation | Invalid-image negative case |
| `ERR-006` | Preprocessing; `AC-002`, `FR` preprocessing requirements | Preprocessing failure/recovery case |
| `ERR-007` | `FR-010` OCR execution; `AC-003` | OCR failure integration/functional case |
| `ERR-008` | `FR-012` OCR unusable output; `AC-003` | Empty/unusable OCR case |
| `ERR-009` | Model readiness; `AC-011`, offline readiness requirements | Model-not-ready processing case |
| `ERR-010` | AI processing; `AC-004` | AI/model/runtime failure case |
| `ERR-011` | Structured extraction / schema validation | Invalid AI output/schema-validation cases |
| `ERR-012` | Table extraction; partial-result contract | AI-success/table-failure and table-structure cases |
| `ERR-013` | `NFR-005`, `NFR-006`, persistence requirements | Save/read/transaction failure injection |
| `ERR-014` | Resource/storage requirements | Low-storage/failed-write case |
| `ERR-015` | `AC-007` export | Export failure cases for supported formats |
| `ERR-016` | `AC-008` sharing | Share failure/no-target case |
| `ERR-017` | Cancellation requirements / `AC-012` | Cancellation at processing stages |
| `ERR-018` | `NFR-006`, `NFR-007`, `TBD-012`, `AC-012` | Crash/restart/interruption integrity case |

### 31.1 Cross-cutting requirements

| Requirement | Error-handling coverage |
|---|---|
| `NFR-005` Recoverable processing failures | Safe failure, no false success, intermediate preservation |
| `NFR-006` Persistence after restart | Committed data survives relaunch; clear corrupt/unavailable state |
| `NFR-007` Interrupted operation integrity | Existing committed records remain valid; exact resume TBD |
| `NFR-009` Processing-state clarity | State/UI must communicate current stage and avoid misleading completion |
| `NFR-012` Consistent failure/edit representations | Stable error/state model and `ERR-001..018` catalogue |
| `NFR-013` Local processing/storage priority | Recovery must not introduce cloud dependency |
| `NFR-014` No mandatory cloud upload | Offline error handling keeps cloud out of core workflow |
| `NFR-015` Offline availability communication | Model-ready/not-ready state must be truthful |
| Security/privacy acceptance criteria | Safe errors/logs, path handling, AI trust boundary, user edit authority |

---

# 32. Critical Error Test Scenarios

The following scenarios are release-critical or high-risk because the approved testing baseline identifies false success, data corruption, privacy leakage, and offline violations as critical risks.

## 32.1 Acquisition

1. Deny camera permission → `ERR-001`; no processing starts.
2. Camera becomes unavailable → `ERR-002`; retry/cancel works.
3. Select unsupported file → `ERR-003`; no downstream processing.
4. Open corrupt PDF → `ERR-004`; no false success.
5. Select invalid image → `ERR-005`; no false success.

## 32.2 Processing

6. Preprocessing failure → `ERR-006`; retry/back path works.
7. OCR throws/fails → `ERR-007`; downstream AI does not falsely run as successful.
8. OCR returns empty → `ERR-008`; user sees empty/error state.
9. Model not ready → `ERR-009`; setup/readiness path is shown.
10. AI runtime failure → `ERR-010`; no fake fields/tables.
11. Invalid AI payload → `ERR-011`; payload is rejected as data, not executed.
12. Table extraction fails while fields succeed → `ERR-012`; valid fields preserved if allowed.
13. Missing values → unresolved, not fabricated.

## 32.3 Persistence

14. Database write fails → `ERR-013`; prior saved data unchanged.
15. Transaction fails after partial work → rollback; no false save.
16. Low storage → `ERR-014`; no partial authoritative commit.
17. Relaunch after successful save → saved result remains available.
18. Relaunch after interrupted save → committed records remain valid.
19. External database corruption → clear unavailable/corrupt state.

## 32.4 Export/share

20. Export failure → `ERR-015`; authoritative saved result unchanged.
21. Export with unsafe filename/path → rejected/safely contained.
22. Export after user correction → output reflects saved correction, never stale AI result.
23. Share failure → `ERR-016`; local export remains preserved.

## 32.5 Lifecycle/cancellation

24. Cancel OCR/AI processing → `ERR-017`; no completion state.
25. Interrupt application during processing → `ERR-018`; committed data intact.
26. Interrupt during export → existing authoritative data intact; exact export resume remains TBD.

## 32.6 Privacy

27. Run processing and inspect routine diagnostics → no raw OCR/document/sensitive fields.
28. Run offline core workflow → no unexpected network dependency.
29. Feed instruction-like AI output → treated only as data and rejected/validated.
30. Attempt path traversal through filename/export input → blocked.

---

# 33. MVP Error-Handling Checklist

## Architecture

- [ ] Stable `ERR-001..ERR-018` catalogue implemented.
- [ ] Low-level exceptions normalized before reaching UI.
- [ ] Processing State Manager and Error Manager boundaries respected.
- [ ] No provider-specific raw exception shown to users.

## Input

- [ ] Camera permission failure handled.
- [ ] Camera capture failure handled.
- [ ] Unsupported file handled.
- [ ] Corrupt PDF handled.
- [ ] Invalid image handled.

## Processing

- [ ] Preprocessing failure handled.
- [ ] OCR failure handled.
- [ ] Empty OCR handled.
- [ ] Model-not-ready state handled.
- [ ] AI runtime failure handled.
- [ ] Invalid AI output rejected.
- [ ] Table failure handled with partial-result semantics where permitted.
- [ ] Unresolved values never fabricated.

## Persistence

- [ ] Save failure does not report success.
- [ ] Transactions roll back on failure.
- [ ] Existing committed user data remains intact.
- [ ] Saved edits are authoritative.
- [ ] Restart persistence passes.
- [ ] Corrupt/unavailable storage state is truthful.
- [ ] Low-storage failure handled.

## Export / Share

- [ ] Export failures are visible.
- [ ] Export uses authoritative saved data.
- [ ] Excel/CSV/JSON/PDF output paths are validated.
- [ ] Unsafe filenames/path traversal are rejected.
- [ ] Share failure preserves the local export.

## Offline

- [ ] Core processing works after model setup with network disabled.
- [ ] No silent cloud fallback.
- [ ] Missing model does not appear ready.
- [ ] Unexpected network use is auditable.

## Lifecycle

- [ ] Cancellation stops further work.
- [ ] Cancellation releases resources.
- [ ] Interruption leaves committed data intact.
- [ ] Exact resume behavior is explicitly tracked as `TBD-012` until decided.

## Logging / privacy

- [ ] No raw document content in normal logs.
- [ ] No OCR text in normal logs.
- [ ] No sensitive extracted fields in normal logs.
- [ ] No private prompts/model responses in normal logs.
- [ ] No secrets/tokens in logs.
- [ ] Error details are non-sensitive.

## Testing / release

- [ ] Failure injection covers P0 processing/export/storage failures.
- [ ] Critical error scenarios are executed on the real Android project.
- [ ] Offline test passes.
- [ ] Persistence/restart test passes.
- [ ] Security/privacy log audit passes.
- [ ] No known P0 false-success/data-loss defect remains.

---

# 34. Open Decisions and Explicit Non-Assumptions

| ID | Decision | Status |
|---|---|---|
| `TBD-002` | Exact AI model/runtime | **Requires Technical Validation** |
| `TBD-003` | Exact maximum document size/page count | **Requires Technical Validation** |
| `TBD-004` | Exact OCR language list for MVP | **TBD** |
| `TBD-005` | Exact extraction validation dataset | **TBD** |
| `TBD-006` | OCR/AI usefulness threshold | **TBD** |
| `TBD-008` | Multi-page PDF priority | **TBD** |
| `TBD-009` | PIN/biometric advanced security scope | **TBD** |
| `TBD-010` | Model update/delete MVP behavior | **TBD** |
| `TBD-011` | Complex-table export fidelity | **Requires Technical Validation** |
| `TBD-012` | Exact application interruption recovery/resume behavior | **TBD** |
| `SEC-TBD-001` etc. | Encryption/key management and other advanced security controls | **TBD / Requires Technical Validation** |
| Technical | Exact Android permission/API set | **Requires Technical Validation** |
| Technical | Exact temporary-file cleanup policy | **Requires Technical Validation** |
| Technical | Exact share URI/access-grant mechanism | **Requires Technical Validation** |

This document intentionally does not promote any of these open decisions to implementation facts.

---

# 35. Acceptance and Release Gates

The error-handling strategy is considered implementation-complete only when the applicable source-backed acceptance conditions are demonstrated on the actual Android project.

## Minimum release gates

1. Camera/PDF/image acquisition works or fails truthfully.
2. Preprocessing returns usable input or a recoverable failure.
3. OCR returns usable text or a truthful failure/empty state.
4. Offline AI never pretends to succeed when the model is unavailable.
5. AI output is validated before application use.
6. User review/edit is preserved.
7. Saved corrections remain authoritative after reopen.
8. Storage failures do not destroy committed data.
9. Export reflects current saved values.
10. Share failures preserve generated local files.
11. Offline mode works after model setup.
12. P0 failure injection shows truthful failure and preserves authoritative data.
13. Routine diagnostics contain no sensitive document content.
14. No unsupported security/recovery claim is published as confirmed.

---

# 36. Traceability to Approved Project Documents

| Document | Relevance to this strategy |
|---|---|
| PRD | Product scope, offline-first direction, user review/edit, local storage, export/history |
| SRS | `ERR-001..ERR-018`, FR/NFR failure behavior, AC-001..AC-012, `TBD-012` |
| TRD | Technical boundaries, no required backend/API, validation-open implementation details |
| SYSTEM_ARCHITECTURE | Error Manager, Processing State Manager, module boundaries, local-first architecture |
| FRONTEND | State handling and UI integration boundaries |
| UI_UX | Error messages, empty states, loading states, cancellation/interruption UX |
| DATABASE | SQLite persistence, transactions, migration integrity, database/file boundary |
| DATA_SCHEMA | Error object, canonical structured result, user edit authority |
| AI_OCR | AI/OCR failure, model readiness, invalid output, cancellation, privacy boundary |
| DOCUMENT_PROCESSING | Stage contracts, partial results, interruption/error behavior |
| EXPORT | Saved authoritative data, safe filenames/paths, export/share boundary |
| TESTING | Failure injection, offline tests, security/privacy checks, release gates |
| SECURITY_PRIVACY | Safe logging, threat boundaries, local processing, model/file/share integrity |
| BUILD_RELEASE | Release validation and failure gates |
| IMPLEMENTATION_PLAN | Acceptance scenarios, failure injection, integration order, release gates |
| API_SPECIFICATION | Internal contracts and explicit absence of required MVP network API |
| CODE_ARCHITECTURE | Boundary discipline and separation of concerns |
| DEVELOPMENT_GUIDELINES | Implementation/quality discipline where applicable |
| REQUIREMENTS_TRACEABILITY | Coverage mapping to requirements and tests |
| TEST_CASES | Master failure, offline, database, export, lifecycle and security verification catalog |
| AI_PROMPT_SPECIFICATION | AI prompt/output boundary and validation context |

---

# 37. Final Engineering Principles

SnapData's error-handling strategy is governed by the following rules:

```text
FAIL TRUTHFULLY
    ↓
PRESERVE COMMITTED DATA
    ↓
PRESERVE VALID PARTIAL DATA WHERE ALLOWED
    ↓
PROTECT USER CORRECTIONS
    ↓
RECOVER AT THE NARROWEST SAFE BOUNDARY
    ↓
NEVER LEAK DOCUMENT CONTENT THROUGH DIAGNOSTICS
    ↓
NEVER INTRODUCE A HIDDEN CLOUD DEPENDENCY
    ↓
NEVER FABRICATE SUCCESS
```

The defining reliability property of SnapData is therefore not that every operation always succeeds. It is that when an operation fails, the application remains **truthful, recoverable where practical, privacy-safe, and protective of the user's last authoritative saved state**.

---

## Appendix A — Core Error Matrix (Condensed)

| ID | Cause | Detection | System behavior | Recovery | Test |
|---|---|---|---|---|---|
| ERR-001 | Camera permission denied | Camera boundary | Block capture | Retry/permission/Back | Permission denial |
| ERR-002 | Camera capture unusable | Camera boundary | No processing | Retry/Cancel | Capture failure |
| ERR-003 | Unsupported file | Input validation | Reject | Choose another | Unsupported input |
| ERR-004 | Corrupt PDF | PDF validation | Reject | Choose another | Corrupt PDF |
| ERR-005 | Invalid image | Image validation | Reject | Reselect/recapture | Invalid image |
| ERR-006 | Preprocess failure | Preprocess stage | Stop/branch | Retry/Back | Preprocess fault |
| ERR-007 | OCR failure | OCR stage | Stop/clear failure | Retry/source | OCR fault |
| ERR-008 | Empty OCR | Output validation | No success state | Better source | Empty OCR |
| ERR-009 | Model unavailable | Model manager | AI unavailable | Setup model | Model absent |
| ERR-010 | AI failure | AI runtime | No fake result | Retry/Back | AI fault |
| ERR-011 | Invalid extraction | Schema validation | Reject candidate | Retry/review source | Malformed AI output |
| ERR-012 | Table failure | Table stage | Partial result if allowed | Continue/Retry | Table fault |
| ERR-013 | Local storage failure | Persistence layer | No false save | Retry/preserve edits | Save fault |
| ERR-014 | Low storage | Resource/storage check | Stop safely | Free storage/Retry | Low-space |
| ERR-015 | Export failure | Export layer | No success | Retry/other format | Export fault |
| ERR-016 | Share failure | Share layer | Keep local export | Retry/Return | Share failure |
| ERR-017 | Cancellation | State manager | Stop further work | Return | Cancel mid-stage |
| ERR-018 | Interruption | Lifecycle/restart | Recover safely | Resume policy TBD | Crash/restart |

---

## Appendix B — Source-Backed Integrity Invariants

```text
I1: No false success after failure.
I2: Existing committed data remains valid after interruption.
I3: User-saved corrections are authoritative.
I4: AI output is validated before persistence.
I5: Sensitive document content stays out of routine diagnostics.
I6: Core document processing remains local/offline after model setup.
I7: Incomplete/corrupt/unverified models are not ready.
I8: Export consumes the current saved authoritative result.
I9: Share failure does not destroy a valid local export.
I10: Empty state and error state remain semantically distinct.
```

---

**Document status:** Production-oriented error/recovery baseline, with implementation-specific decisions explicitly retained as `TBD`, `PROPOSED`, or `REQUIRES TECHNICAL VALIDATION` until verified against the actual Google AI Studio-generated Android project and associated test evidence.
