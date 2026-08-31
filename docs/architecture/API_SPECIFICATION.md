# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## API, Internal Service Interface & Future Network Contract Specification

**Document:** `SnapData_API_SPECIFICATION_v1.0.md`  
**Version:** 1.0  
**Status:** Engineering Baseline  
**Date:** 30 August 2026  
**MVP API:** None / No REST API  
**MVP Architecture:** Local-first Android application  
**Implementation Target:** Google AI Studio — Build an Android app workflow

> **Authority and evidence rule:** This document defines contracts, not proof of implementation. It does not silently resolve conflicts, invent implementation details, claim test execution, or promote future network concepts into MVP requirements. Where the source leaves a decision open, the approved status is preserved.

---

# 0. Document Control

## 0.1 Purpose

This document defines the contracts between SnapData software components and establishes the boundary for any future network API without introducing a backend dependency into the current MVP.

SnapData's current baseline is a local-first Android application. The core product workflow is intended to operate locally after required AI model setup. The architecture explicitly rejects a required backend, REST API, cloud database, server-side document processing, and authentication server for the MVP. The current technical baseline instead calls for provider-neutral internal interfaces around document processing, OCR, AI, persistence, export, sharing, and model management.

## 0.2 Authority Hierarchy

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_FRONTEND_v1.0.md`
6. `SnapData_DATABASE_v1.0.md`
7. `SnapData_DATA_SCHEMA_v1.0.md`
8. `SnapData_AI_OCR_v1.0.md`
9. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
10. `SnapData_EXPORT_v1.0.md`
11. `SnapData_TESTING_v1.0.md`
12. `SnapData_SECURITY_PRIVACY_v1.0.md`
13. `SnapData_BUILD_RELEASE_v1.0.md`
14. `SnapData_IMPLEMENTATION_PLAN_v1.0.md`
15. Original SnapData project specification
16. SnapData workflow diagram
17. Actual Google AI Studio-generated Android project, when available

## 0.3 Status Vocabulary

| Status | Meaning |
|---|---|
| **CONFIRMED** | Established by project source material or direct implementation evidence. |
| **PROPOSED** | Recommended design direction that is not itself an implementation fact. |
| **TBD** | Decision has not yet been made. |
| **REQUIRES TECHNICAL VALIDATION** | Product intent exists, but exact feasibility, compatibility, integration, or measured performance must be verified. |
| **REJECTED** | Intentionally excluded from the current baseline. |
| **OPTIONAL** | Permitted enhancement not required for the baseline. |

---

# 1. Purpose and Scope

## 1.1 Purpose

The purpose of this API specification is to:

- establish stable boundaries between SnapData components;
- keep presentation independent from OCR, AI, persistence, and export implementations;
- provide explicit conceptual inputs and outputs;
- standardize state, progress, cancellation, error, and validation behavior;
- preserve the offline-first architecture;
- define authoritative-data lifecycle semantics;
- provide a future network contract without pretending it is part of the MVP.

## 1.2 In Scope

This document covers:

- internal document-processing interfaces;
- processing-state interfaces;
- progress reporting;
- OCR adapter contracts;
- AI service contracts;
- document classification;
- field extraction;
- table extraction;
- confidence handling;
- review/edit operations;
- validation and authoritative-save semantics;
- persistence/repository boundaries;
- file-storage boundaries;
- export contracts;
- Android sharing boundaries;
- AI model-manager contracts;
- common internal error structure;
- cancellation and timeout semantics;
- offline boundary;
- future REST/network contract;
- future authentication, authorization, synchronization, versioning, idempotency, validation, and observability considerations.

## 1.3 Out of Scope for MVP

The current MVP does not require:

- a mandatory backend server;
- REST API implementation;
- GraphQL implementation;
- cloud database;
- server-side OCR/document processing;
- required cloud AI processing;
- authentication server;
- authorization server;
- mandatory synchronization service;
- network-dependent core document processing.

---

# 2. API Status Matrix

| Interface Type | MVP Status |
|---|---|
| REST API | **NOT REQUIRED** |
| GraphQL | **NOT REQUIRED** |
| Cloud API | **NOT REQUIRED** |
| Authentication API | **NOT REQUIRED** |
| Sync API | **NOT REQUIRED** |
| OCR Internal Interface | **REQUIRED** |
| AI Internal Interface | **REQUIRED** |
| Processing Pipeline Interface | **REQUIRED** |
| Persistence Repository Interface | **REQUIRED** |
| Export Interface | **REQUIRED** |
| Sharing Interface | **REQUIRED** |
| Model Manager Interface | **REQUIRED** |
| Future Network API | **FUTURE / TBD** |

---

# 3. Architectural Boundary

## 3.1 MVP Logical Flow

```text
Android UI
    ↓
Application / Use Cases
    ↓
Document Processing Pipeline
    ↓
Preprocessing
    ↓
OCR Adapter
    ↓
AI Adapter
    ↓
Structured Data Builder
    ↓
Validation / Confidence
    ↓
Review / Edit
    ↓
Repository
    ├── SQLite
    └── Local Files
    ↓
Export Service
    ├── Excel
    ├── CSV
    ├── JSON
    └── PDF
    ↓
Android Sharing Boundary
```

## 3.2 Network Boundary

Any currently approved network use is limited to model setup/update behavior where required. Core document processing must remain local.

```text
Network
   ↓
Model Manager
   ↓
Local Model Resource
   ↓
AI Adapter
```

The architecture must not become:

```text
Document
   ↓
Network Upload
   ↓
Cloud AI
   ↓
Result
```

---

# 4. Internal API Design Principles

Internal interfaces SHALL:

1. use explicit inputs and outputs;
2. define success, failure, cancellation, and partial-result semantics;
3. keep provider-specific dependencies behind adapters;
4. remain independent of UI-specific types;
5. avoid direct database access from UI;
6. avoid direct AI-runtime access from UI;
7. avoid direct OCR-runtime access from UI;
8. preserve the offline core path;
9. expose stable domain concepts rather than low-level runtime objects;
10. preserve user corrections and authoritative saved state;
11. make partial/degraded results explicit;
12. avoid leaking sensitive document content into diagnostics;
13. keep exact implementation choices adaptable to the generated Android project.

---

# 5. Contract Notation

Interface definitions use conceptual contract notation rather than language-specific implementation syntax.

Example:

```text
processDocument(request, cancellationSignal, progressSink)
    → ProcessingResult
```

This is intentional while the concrete generated Android stack remains subject to technical validation.

---

# 6. Canonical Domain Contract

The API/service layer SHALL use the canonical SnapData data contract rather than inventing a competing structure.

```text
Document
  ↓
ProcessingJob
  ↓
OCRResult
  ↓
AIExtractionCandidate
  ↓
ExtractionResult
  ├── Fields
  ├── Tables
  ├── Confidence
  ├── Warnings
  ├── Source references
  └── Review/validation state
        ↓
User Review / Edit
        ↓
Saved Authoritative Result
        ↓
Export
```

## 6.1 Authoritative Data Rule

The current saved user-reviewed value is authoritative for persistence, reopening, display of the saved result, and export.

```text
AI / OCR Candidate
        ↓
Validation
        ↓
User Review / Edit
        ↓
Save
        ↓
Authoritative Result
        ↓
Reopen / Export
```

---

# 7. Document Processing Interface

## 7.1 Conceptual Interface

```text
DocumentProcessingService

processDocument(
    input,
    cancellationSignal,
    progressSink
) → ProcessingResult

cancel(jobId) → CancellationResult
getStatus(jobId) → ProcessingStatus
```

## 7.2 DocumentInput

Conceptual fields include:

| Field | Meaning | Status |
|---|---|---|
| `sourceReference` | Local source/file reference | **PROPOSED concept** |
| `inputType` | Camera image, imported image, PDF, etc. | **CONFIRMED concept** |
| `mimeType` | Source MIME type where available | **CONFIRMED concept** |
| `pageInformation` | Page count/index metadata where applicable | **CONFIRMED concept** |
| `processingOptions` | Approved processing settings | **PROPOSED** |

## 7.3 ProcessingResult

Conceptual fields include:

- document metadata;
- processing metadata;
- OCR result/evidence;
- detected document type;
- extracted fields;
- extracted tables;
- confidence information;
- warnings;
- validation result;
- terminal processing status.

A failed or cancelled operation SHALL NOT be represented as successful completion.

---

# 8. Processing State Interface

## 8.1 Conceptual State Set

```text
IDLE
ACQUIRING
VALIDATING
PREPROCESSING
OCR_PROCESSING
AI_PROCESSING
STRUCTURING
VALIDATING_RESULT
REVIEW_READY
REVIEW
EDITING
SAVING
SAVED
EXPORTING
COMPLETED
FAILED
CANCELLED
```

## 8.2 Main Transition Path

```text
IDLE
  ↓
ACQUIRING
  ↓
VALIDATING
  ↓
PREPROCESSING
  ↓
OCR_PROCESSING
  ↓
AI_PROCESSING
  ↓
STRUCTURING
  ↓
VALIDATING_RESULT
  ↓
REVIEW_READY / REVIEW
  ↓
EDITING (optional)
  ↓
SAVING
  ↓
SAVED
  ↓
EXPORTING (optional)
  ↓
COMPLETED
```

## 8.3 Cancellation

Cancellable work may include preprocessing, OCR, AI inference, export and model operations where technically supported.

Cancellation SHALL stop work where safe/possible, release temporary resources, preserve committed data, and return a truthful cancellation result.

## 8.4 Invalid Transitions

Examples of invalid transitions include:

```text
IDLE → SAVING
IDLE → EXPORTING
OCR_PROCESSING → COMPLETED
AI_PROCESSING → EXPORTING
FAILED → COMPLETED without a new attempt
CANCELLED → COMPLETED without a new attempt
```

---

# 9. Progress Interface

## 9.1 ProcessingProgress

| Field | Meaning | Status |
|---|---|---|
| `stage` | Current processing stage | **CONFIRMED concept** |
| `percentage` | Measured progress when meaningful | **CONFIRMED requirement, conditional** |
| `currentPage` | Active page where applicable | **CONFIRMED concept** |
| `totalPages` | Total page count where applicable | **CONFIRMED concept** |
| `statusMessage` | User-safe stage message | **CONFIRMED concept** |
| `estimatedRemainingTime` | ETA where actually measurable | **OPTIONAL / conditional** |

Progress must never imply success when success has not occurred. Do not fabricate percentages or ETAs.

## 9.2 Progress Sink

```text
progressSink.onProgress(progress: ProcessingProgress)
```

The pipeline produces progress; the UI renders it.

---

# 10. OCR Interface

## 10.1 Conceptual Interface

```text
OCRService

initialize() → OCRReadiness
isReady() → boolean
processImage(input) → OCRResult
processPage(input) → OCRResult
processDocument(input) → OCRDocumentResult
cancel(operationId) → CancellationResult
```

The exact Android OCR engine integration is subject to technical validation.

## 10.2 OCR Input

Input is a normalized image/page representation produced by acquisition and preprocessing.

The OCR service must not require UI code to understand engine-specific objects.

## 10.3 OCRResult

Conceptual fields include:

- extracted text;
- page reference;
- confidence where provided;
- OCR metadata;
- warnings;
- error information where applicable;
- source references/coordinates where supported.

---

# 11. OCR Failure Contract

Only source-defined/validated error identifiers may be implemented. Conceptual failures include:

```text
OCR_NOT_READY
OCR_INITIALIZATION_FAILED
OCR_INPUT_INVALID
OCR_UNSUPPORTED_LANGUAGE
OCR_PROCESSING_FAILED
OCR_CANCELLED
OCR_RESOURCE_ERROR
```

These are contract concepts, not permission to invent additional production codes.

---

# 12. AI Service Interface

## 12.1 Conceptual Interface

```text
AIService

initialize() → AIReadiness
isReady() → boolean
analyzeDocument(input) → AIResult
extractFields(input) → StructuredField[]
extractTables(input) → StructuredTable[]
classifyDocument(input) → DocumentTypeResult
generateSummary(input) → SummaryResult (if approved)
cancel(operationId) → CancellationResult
```

Exact model/runtime and operation availability remain TBD / require technical validation.

## 12.2 AI Input Contract

Conceptual inputs may include:

- OCR text;
- page structure;
- document metadata;
- optional image references where supported;
- processing options;
- model/context constraints.

## 12.3 AI Output Contract

AI output is an untrusted candidate and must pass validation:

```text
AI Output
   ↓
Schema Validation
   ↓
Normalization
   ↓
Semantic Validation
   ↓
Canonical Structured Data
```

The model must not write directly to persistence, access unrestricted filesystem paths, execute commands, alter permissions/settings, bypass validation, fabricate facts/confidence, overwrite saved corrections, or silently fall back to remote inference.

---

# 13. AI Output Validation Contract

Validate, as applicable:

1. identifiers;
2. enum values;
3. parent/child relationships;
4. field-key validity;
5. table/row/cell structure;
6. page/source-reference consistency;
7. value-type compatibility;
8. confidence semantics;
9. warning/error structures;
10. schema version;
11. unresolved-value semantics;
12. nested data ownership.

For malformed output:

```text
Malformed AI Output
   ↓
Bounded Repair if Safe
   ↓
Re-validation
   ├── PASS → continue
   └── FAIL → structured extraction failure / partial result
```

Never fabricate missing factual values during repair.

---

# 14. Unknown and Unresolved Values

```text
No reliable source evidence
        ↓
Do not synthesize factual value
        ↓
Preserve unknown / unresolved semantics
```

Do not silently substitute guesses, arbitrary defaults, or semantically incorrect empty strings.

---

# 15. Document Classification Interface

```text
classifyDocument(documentRepresentation)
    → DocumentTypeResult
```

Conceptual result:

- `documentType`;
- `confidence` where supported;
- alternatives where supported;
- warnings.

Representative source-backed categories include invoice, receipt, form, bank statement, certificate, mark sheet, ID card, business card, table, and general/unknown document. Exact model coverage and thresholds require validation.

---

# 16. Field Extraction Interface

```text
extractFields(input)
    → StructuredField[]
```

Conceptual field structure:

| Field | Meaning |
|---|---|
| `id` | Stable field identifier |
| `key` | Stable logical key |
| `label` | Human-readable label where available |
| `value` | Current value; nullable where required |
| `valueType` | Canonical semantic value type |
| `originalValue` | Pre-edit extraction baseline |
| `editedFlag` | Indicates user modification |
| `confidence` | Upstream confidence where available |
| `sourcePage` | Source page where supported |
| `sourceReference` | Source region/block/line reference where supported |
| `order` | Display/export ordering where defined |
| `validationState` | Validation state where approved |
| `userEditState` | User-edit semantics where approved |

## 16.1 Field Authority

Before save:

```text
AI value → working value
```

After successful user edit/save:

```text
User-corrected value → authoritative value
```

---

# 17. Table Extraction Interface

```text
extractTables(input)
    → StructuredTable[]
```

A structured table conceptually contains:

- table identifier;
- columns;
- rows;
- cells;
- confidence where available;
- validation metadata;
- source references where supported.

Supported editing includes editable cells, row insertion/deletion where approved, stable row order, stable column association, and deterministic export mapping.

---

# 18. Confidence Interface

Where the upstream capability supports categorical semantics, the application may use:

```text
HIGH
MEDIUM
LOW
UNKNOWN
```

Do not invent numerical confidence or accuracy semantics.

---

# 19. Review and Edit Interface

Conceptual operations:

```text
getResult(documentId / resultId)
updateField(fieldId, value)
updateCell(cellId, value)
addRow(tableId, row)
deleteRow(rowId)
validateData(resultId)
saveChanges(resultId)
discardChanges(resultId)
```

The review layer converts validated machine-generated candidates into an authoritative user-reviewed result.

Unsaved edits must not be silently discarded, and a failed save must not appear successful.

---

# 20. Persistence Repository Interface

## DocumentRepository

```text
create(document)
get(documentId)
list(query / filters)
search(criteria)
update(document)
delete(documentId)
exists(documentId)
```

## ProcessingRepository

```text
createJob(job)
saveProcessingState(jobId, state)
getProcessingState(jobId)
updateProcessingState(jobId, state)
listJobs(documentId)
```

## StructuredDataRepository

```text
saveResult(result)
getResult(resultId)
getCurrentResult(documentId)
updateResult(result)
deleteResult(resultId)
```

## ExportRepository

```text
createExportRecord(record)
getExport(exportId)
listExports(documentId)
updateExport(exportId, state)
```

### Repository Boundary

```text
UI
 ↓
Use Case / Application Service
 ↓
Repository Interface
 ↓
SQLite Adapter
```

UI must not access SQLite directly.

---

# 21. File Storage Interface

```text
FileStorage

save(data, location)
read(reference)
exists(reference)
delete(reference)
move(reference, destination)
cleanup(scope)
```

The storage layer may own original documents, processed artifacts, export files, model resources, and temporary files according to approved lifecycle rules.

Database and physical filesystem operations are separate consistency boundaries.

### Missing DB File

```text
DB record found
  ↓
File reference check fails
  ↓
Unavailable / recovery state
```

### Orphan File

```text
Physical file exists
  ↓
No valid DB record
  ↓
Orphan candidate
```

Do not present an orphan as a valid history record without reconciliation.

---

# 22. Export Interface

```text
ExportService

export(request: ExportRequest) → ExportResult
cancel(operationId) → CancellationResult
```

Format-specific operations conceptually include:

```text
exportExcel(request) → ExportResult
exportCSV(request) → ExportResult
exportJSON(request) → ExportResult
exportPDF(request) → ExportResult
```

Supported formats are Excel, CSV, JSON, and PDF. Exact libraries remain TBD / require technical validation.

## 22.1 Authoritative Export Rule

```text
Current saved structured result
          ↓
Export manager
          ↓
Format-specific exporter
          ↓
Output validation
          ↓
Local file / Android URI
          ↓
Share / Open
```

Exports must use the latest saved user-reviewed authoritative result.

---

# 23. Export Validation

Before export, validate:

1. target result exists;
2. result is saved;
3. result is current/authoritative;
4. data is structurally valid;
5. format is supported;
6. destination is safe;
7. sufficient local storage exists;
8. filename is safe;
9. path traversal is prevented.

On failure, saved structured data must remain intact and success must not be reported.

---

# 24. Android Sharing Interface

```text
ShareService

share(exportFile) → ShareResult
open(exportFile) → OpenResult (where supported)
```

The service receives an approved local export artifact/reference rather than exposing an arbitrary filesystem path to the UI.

The exact Android URI/share mechanism is **PROPOSED / REQUIRES TECHNICAL VALIDATION**.

Sharing failures must not delete the export or alter authoritative saved data.

---

# 25. Model Manager Interface

```text
ModelManager

getStatus() → ModelStatus
download(request) → ModelOperationResult
validate(modelRef) → ModelValidationResult
load(modelRef) → ModelOperationResult
unload(modelRef) → ModelOperationResult
delete(modelRef) → ModelOperationResult
update(request) → ModelOperationResult
```

Model update/delete behavior is TBD where not explicitly approved.

Conceptual states include:

```text
NOT_INSTALLED
DOWNLOADING
VERIFYING
READY
LOADING
LOADED
FAILED
UPDATING
UNAVAILABLE
```

A model must not become `READY` until required integrity/readiness validation succeeds.

Model binaries are not stored inside SQLite.

---

# 26. Offline Contract

After required model setup, the core path must remain local:

```text
Document Acquisition
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
SQLite / Local Files
        ↓
Export
        ↓
Android Sharing
```

No hidden cloud fallback is allowed.

If the local model is unavailable, the application must expose a clear setup/unavailable state.

---

# 27. Common Internal Error Contract

Conceptual `AppError` fields:

| Field | Meaning |
|---|---|
| `code` | Stable error identifier |
| `category` | Error family |
| `userMessage` | Safe user-facing message |
| `recoverable` | Whether recovery is possible |
| `retryable` | Whether retry may be useful |
| `stage` | Pipeline/application stage |
| `technicalDetails` | Internal diagnostic metadata subject to privacy rules |

Conceptual categories:

```text
INPUT_ERROR
FILE_ERROR
PERMISSION_ERROR
PREPROCESSING_ERROR
OCR_ERROR
AI_ERROR
MODEL_ERROR
VALIDATION_ERROR
DATABASE_ERROR
STORAGE_ERROR
EXPORT_ERROR
SHARING_ERROR
CANCELLED
UNKNOWN_ERROR
```

## 27.1 Privacy Rule

Technical details must not contain raw document content, OCR text, sensitive extracted values, secrets, or inappropriate internal paths.

---

# 28. Error Mapping Strategy

```text
Low-Level Provider Failure
        ↓
Provider Adapter Error
        ↓
Domain/Application Error
        ↓
Stable AppError
        ↓
UI-Safe State / Message
```

Example:

```text
OCR Provider Error
   ↓
OCR Adapter
   ↓
OCR_PROCESSING_FAILED
   ↓
Processing Orchestrator
   ↓
FAILED
   ↓
UI: user-safe failure message
```

The application must preserve error semantics across boundaries.

---

# 29. Cancellation Contract

Cancellable long-running operations may include:

- preprocessing;
- OCR;
- AI inference;
- export;
- model download/update.

Cancellation SHALL:

1. request cancellation;
2. stop work where safe/possible;
3. release temporary resources;
4. avoid false completion;
5. preserve previous committed data;
6. return a truthful cancellation result.

Exact save/resume semantics remain subject to technical validation where the source marks them open.

---

# 30. Timeout Contract

Timeouts apply only where technically meaningful. The implementation must define, where required:

1. timeout condition;
2. stable error semantics;
3. cleanup behavior;
4. retryability;
5. partial-result behavior.

Do not invent numeric timeout values.

---

# 31. Partial and Degraded Result Contract

Where safe, the pipeline may preserve partial/degraded work, such as:

- continuing after a nonessential preprocessing failure;
- retaining OCR when downstream AI fails;
- processing remaining valid pages;
- returning partial structured data;
- continuing without optional source coordinates.

Degraded/partial output must be surfaced explicitly and must not be silently presented as fully equivalent output.

---

# 32. Idempotency and Duplicate Processing

A logical processing job must have a stable identity sufficient to distinguish retry of the same job from a new user request.

Repeated callbacks or retries must not create conflicting final results.

Future network side-effecting operations should support an idempotency mechanism where approved.

---

# 33. Concurrency Contract

The local MVP must prevent conflicting mutations to authoritative results.

Examples:

```text
User Save
vs.
Automatic AI Retry
```

AI retries must not overwrite a newer user-corrected result.

```text
Export
vs.
Edit/Save
```

Export should resolve the current saved authoritative revision at initiation.

Exact locking/versioning remains **REQUIRES TECHNICAL VALIDATION** where not otherwise specified.

---

# 34. Versioning of Internal Contracts

Internal contracts should evolve compatibly where practical.

Breaking changes include incompatible removal of required fields, semantic changes, enum meaning changes, null-semantics changes, or incompatible mandatory-input changes.

Serialized structured results should carry the canonical schema version according to the Data Schema specification, for example:

```json
{
  "schemaVersion": "1.0"
}
```

The exact current schema-version value must remain aligned with the approved Data Schema.

---

# 35. Testing Contract Requirements

Every required internal contract should be tested for:

- valid input;
- invalid input;
- provider failure;
- partial result;
- cancellation;
- resource exhaustion;
- timeout where applicable;
- repeated calls/retry;
- state validity;
- privacy-safe error mapping.

Mandatory cross-layer integrity scenario:

```text
AI extracts A
→ User changes to B
→ Save
→ Reopen
→ Export
→ Expected authoritative value = B
```

---

# 36. Security Contract for Internal Interfaces

All internal interfaces SHALL treat incoming data as untrusted until validated.

Validate:

- camera content;
- imported PDFs/images;
- filenames;
- file references;
- OCR output;
- AI output;
- export filenames/options;
- model artifacts;
- optional network model-download responses.

Rules:

1. Never use AI output directly as a filesystem path.
2. Never treat document text as executable instructions.
3. Validate file type and structure.
4. Prevent path traversal.
5. Prevent unsafe overwrite.
6. Do not leak sensitive content through errors/logs.
7. Do not mark corrupt/incomplete models ready.
8. Keep core processing local.
9. Treat sharing as an explicit privacy boundary.
10. Preserve authoritative user-edited data.

---

# 37. Logging and Diagnostics Contract

Operational diagnostics may record safe metadata such as:

- operation type;
- duration;
- stage;
- error code;
- resource failure category;
- retry count;
- model identifier/version where safe;
- export format.

Routine diagnostics must NOT record:

- raw document content;
- OCR text;
- sensitive extracted fields;
- private prompts;
- secrets;
- raw images;
- credentials/tokens.

---

# 38. Future Network API — NOT MVP

> **Status: FUTURE / NOT MVP**

Any future network layer must be separately approved before implementation.

## 38.1 Conceptual Future Endpoints

```http
POST   /api/v1/documents
GET    /api/v1/documents/{id}
GET    /api/v1/documents
DELETE /api/v1/documents/{id}
POST   /api/v1/documents/{id}/process
GET    /api/v1/documents/{id}/results
PUT    /api/v1/documents/{id}/results
POST   /api/v1/documents/{id}/exports
```

These routes are conceptual future contracts only and are NOT MVP endpoints.

## 38.2 Future Backend Architecture

```text
Android App
     ↓ HTTPS
API Gateway
     ↓
Backend Services
     ├── Authentication
     ├── Authorization
     ├── Document Service
     ├── Processing Service
     ├── Storage
     └── Sync Service
```

---

# 39. Future Authentication Contract — NOT MVP

If a backend is approved later, authentication may define registration, login, password reset, token/session lifecycle, logout, and account deletion.

Exact identity provider and token type are TBD.

---

# 40. Future Authorization Contract — NOT MVP

Future network services may enforce authenticated ownership, document/export ownership, administrative permissions, and service-to-service authorization.

The current MVP has no server authorization layer.

---

# 41. Future Synchronization Contract — NOT MVP

If cloud sync is approved later:

```text
Local Data
    ↓
Sync Engine
    ↓
Remote API
```

The future contract must define revision tracking, conflict detection/resolution, offline edits, retries, duplicate handling, deletes/tombstones, partial synchronization, and reconciliation.

Current MVP excludes required synchronization.

---

# 42. Future API Security Contract — NOT MVP

Any future network implementation must be reviewed for:

- HTTPS/TLS;
- authentication;
- authorization;
- schema validation;
- payload/file limits;
- safe file parsing;
- rate limiting;
- secure token handling;
- input sanitization;
- audit logging;
- privacy review;
- threat modeling;
- abuse protection;
- retention/deletion policy.

These are future considerations, not current MVP claims.

---

# 43. Future API Versioning

Future REST APIs should use an explicit versioning strategy such as:

```text
/api/v1/
```

Breaking changes should use a new approved version or migration strategy.

The current MVP exposes no network routes.

---

# 44. Future Request Validation

Future network requests must validate:

- identity/authentication;
- authorization;
- content type;
- schema;
- payload/file size;
- file type;
- identifiers;
- query parameters;
- dates/enums;
- idempotency where applicable.

Malformed requests must fail safely without unintended partial writes.

---

# 45. Future Response Envelope

A future envelope is proposed:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "requestId": "..."
}
```

Status: **PROPOSED / NOT IMPLEMENTED**.

---

# 46. Future API Error Envelope

Conceptual:

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "The requested resource was not found.",
  "details": null,
  "requestId": "..."
}
```

Future error responses must not expose stack traces, secrets, SQL, internal filesystem paths, raw documents, or credentials.

---

# 47. Future Idempotency Contract

Future side-effecting network operations should support idempotency where duplicate client retries are plausible.

Potential operations include document upload, processing initiation, export requests, and synchronization mutations.

A conceptual header may be:

```http
Idempotency-Key: <client-generated-unique-key>
```

Exact semantics remain TBD.

---

# 48. Future Concurrency Contract

Future result mutation should use explicit revision/version checks.

```text
Client A revision 7
Client B revision 7

A updates → revision 8
B updates revision 7
      ↓
Conflict
```

Stale writes must be rejected or explicitly reconciled rather than silently overwriting newer authoritative data.

---

# 49. Future Network Retry Policy

Future clients/services should distinguish:

- safe retryable transport errors;
- non-retryable validation errors;
- authorization failures;
- resource conflicts;
- transient server errors;
- idempotent operations.

Exact retry counts/backoff remain TBD.

---

# 50. Future Rate Limiting Contract

If a backend is introduced, request limits may be defined by identity/client/endpoint/processing class. Exact limits are TBD.

---

# 51. Future Asynchronous Processing Contract

Future cloud processing, if approved, should remain asynchronous for long-running jobs.

```text
POST /documents/{id}/process
        ↓
jobId
        ↓
GET /documents/{id}
        ↓
QUEUED / PROCESSING / COMPLETED / FAILED
```

The client must not assume completion merely because the start request was accepted.

---

# 52. API-to-UI Boundary

Correct:

```text
OCR Provider
   ↓
OCR Adapter
   ↓
Normalized OCR Result
   ↓
Processing Service
   ↓
Application State
   ↓
UI
```

Incorrect:

```text
UI
 ↓
OCR Runtime API
```

Likewise, UI code should access service functionality through application/use-case boundaries, not construct raw runtime/model requests.

---

# 53. API-to-Database Boundary

Correct:

```text
Application Service
      ↓
Repository Interface
      ↓
SQLite Adapter
```

Incorrect:

```text
AIService
   ↓
SQLite tables
```

The AI layer must return normalized domain data; persistence remains outside the model-runtime adapter.

---

# 54. API-to-Export Boundary

Correct:

```text
Application / Export Use Case
          ↓
ExportService
          ↓
Current Authoritative Result
          ↓
Format Exporter
          ↓
File Writer
```

Exporters must not bypass the canonical saved result.

---

# 55. API-to-Model Boundary

Correct:

```text
Application
   ↓
ModelManager / AIService
   ↓
AI Adapter
   ↓
Model Runtime
```

Higher layers must remain independent of model-runtime internals.

---

# 56. Data Contract Compatibility

Older saved records should remain readable through migration/adapters where required.

```text
Old Schema
   ↓
Migration / Adapter
   ↓
Current Schema
```

Breaking incompatible versions must fail safely rather than silently changing data semantics.

---

# 57. State and Error Correlation

A processing state and its error must remain coherent.

Examples:

```text
OCR failure
→ state = FAILED
→ error.category = OCR_ERROR
```

```text
User cancellation
→ state = CANCELLED
→ error.category = CANCELLED or cancellation result
```

```text
Export success
→ successful export result
→ no failure code
```

Avoid invalid combinations such as `COMPLETED + EXPORT_FAILED` unless a separately approved partial-success contract exists.

---

# 58. Lifecycle and Interruption Contract

If processing is interrupted:

- do not mark the job complete without evidence;
- preserve already committed records;
- reconcile/clean temporary resources according to approved policy;
- expose the true state on next launch;
- keep exact automatic resume semantics TBD where not finalized.

---

# 59. Persistence Transaction Contract

Multi-object authoritative saves should use an atomic database transaction where possible:

```text
User-edited Result
      ↓
Validate
      ↓
DB Transaction
   ├── result
   ├── fields
   ├── tables
   ├── rows
   └── cells
      ↓
Commit
```

Physical file operations remain a separate consistency boundary and require reconciliation where both resources participate.

---

# 60. History Service Contract

Conceptual local service operations:

```text
listHistory(query)
searchHistory(criteria)
getHistoryItem(documentId)
reopen(documentId)
delete(documentId)
```

Prefer stable document identifiers over transferring complete document/result blobs through navigation.

---

# 61. Document Acquisition Contract

Conceptual local operations:

```text
captureCamera() → DocumentInput
pickImage() → DocumentInput
pickPdf() → DocumentInput
validateInput(input) → ValidationResult
```

Exact Android camera/file-picker APIs remain implementation-dependent.

---

# 62. Preprocessing Contract

Conceptual operations:

```text
prepareImage(input) → ProcessedImage
preparePage(input) → ProcessedPage
prepareDocument(input) → PreparedDocument
cancel(operationId) → CancellationResult
```

Exact preprocessing algorithms and parameters remain technical-validation items unless explicitly confirmed elsewhere.

---

# 63. Validation Contract

Conceptual reusable boundary:

```text
ValidationService

validateDocument(input) → ValidationResult
validateExtraction(result) → ValidationResult
validateBeforeSave(result) → ValidationResult
validateBeforeExport(result) → ValidationResult
```

Validation semantics should remain consistent across UI, persistence, and export.

---

# 64. Service Ownership Matrix

| Service / Interface | Owns | Must Not Own |
|---|---|---|
| Document Acquisition | Camera/file acquisition boundary | OCR/AI/SQL |
| Preprocessing | Image/document preparation | UI rendering/AI inference |
| Processing Pipeline | Orchestration/state/progress/cancel | SQL/UI/export encoding |
| OCR Adapter | Provider-specific OCR access | Business data authority |
| AI Adapter | Provider-specific inference | UI/SQL/business persistence |
| Structured Data Validator | Schema/semantic validation | File I/O/UI |
| Document Repository | Document persistence | OCR/AI |
| Processing Repository | Processing/history persistence | Model runtime |
| Structured Data Repository | Authoritative results | UI rendering |
| File Storage | Local file lifecycle | SQL business semantics |
| Export Service | Export orchestration/validation | OCR/AI |
| Format Exporter | Format-specific encoding | Database mutation |
| Share Service | Android share/open boundary | Data transformation authority |
| Model Manager | Model lifecycle/readiness | Presentation state |
| Future Network Client | HTTP transport to approved backend | Local OCR/AI implementation |

---

# 65. Acceptance Criteria

## AC-API-001 — No MVP REST Dependency

Given the current MVP architecture, core processing requires no REST backend.

## AC-API-002 — Offline Core Processing

Given required local AI model setup and network disabled, supported core processing executes locally.

## AC-API-003 — OCR Boundary

Given valid OCR-ready input, the OCR service returns a normalized result or truthful OCR failure.

## AC-API-004 — AI Validation

Given AI output, it is parsed and validated before becoming canonical structured data.

## AC-API-005 — No Fabricated Values

Given no reliable source evidence, unresolved/unknown semantics are preserved and no factual value is fabricated.

## AC-API-006 — User Edit Authority

Given a user edits and saves a field/table value, reopening/export uses the saved correction.

## AC-API-007 — Persistence Boundary

The UI does not directly execute SQL; repositories own persistence.

## AC-API-008 — Export Authority

Given a saved authoritative result, export uses that current result.

## AC-API-009 — Share Boundary

Sharing exposes only the intended export artifact/reference and a share failure preserves the export.

## AC-API-010 — Cancellation

A cancelled long-running operation does not report successful completion and cleans up safely.

## AC-API-011 — Error Safety

User-visible errors do not expose secrets, stack traces, raw sensitive document content, or unsafe internal details.

## AC-API-012 — Future API Separation

Any future backend is treated as a separately approved architecture layer and is not retroactively part of the MVP.

---

# 66. Implementation Guidance

Before concrete interface implementation, inspect the actual Google AI Studio-generated Android project for:

- project tree;
- source language;
- UI toolkit;
- navigation/state approach;
- build configuration;
- camera integration;
- file picker;
- SQLite/local persistence;
- OCR integration;
- AI integration;
- model loading;
- test framework;
- export dependencies.

Recommended adaptation order:

```text
1. Inspect generated project
2. Create/adapt domain contracts
3. Implement application use cases
4. Add provider-specific adapters behind contracts
5. Implement persistence repositories
6. Implement export/share adapters
7. Add lifecycle/state/progress/cancellation
8. Validate offline behavior
9. Execute contract/integration tests
```

Do not add a backend merely because historical project material mentions one.

---

# 67. Traceability Matrix

| Contract Area | Primary Source Ownership | Status |
|---|---|---|
| MVP API absence | SYSTEM_ARCHITECTURE / TRD / IMPLEMENTATION_PLAN | **CONFIRMED** |
| Processing orchestration | DOCUMENT_PROCESSING | **CONFIRMED concept** |
| Processing states | DOCUMENT_PROCESSING / FRONTEND | **CONFIRMED concept** |
| Progress | SRS / FRONTEND / DOCUMENT_PROCESSING | **CONFIRMED** |
| OCR boundary | SYSTEM_ARCHITECTURE / AI_OCR | **PROPOSED adapter; integration validation required** |
| AI boundary | SYSTEM_ARCHITECTURE / AI_OCR | **PROPOSED adapter; model/runtime TBD** |
| Structured data | DATA_SCHEMA | **CONFIRMED semantic contract** |
| User edits authoritative | DATA_SCHEMA / DATABASE | **CONFIRMED** |
| Repository boundary | DATABASE / FRONTEND | **CONFIRMED architectural boundary** |
| File storage boundary | DATABASE / SECURITY | **CONFIRMED boundary; exact API TBD** |
| Export contract | EXPORT | **CONFIRMED requirement; implementation TBD** |
| Android sharing | EXPORT / SECURITY | **CONFIRMED capability; exact mechanism TBD** |
| Model manager | AI_OCR / SYSTEM_ARCHITECTURE | **REQUIRED boundary; lifecycle details TBD** |
| Offline behavior | SRS / SECURITY / TRD | **CONFIRMED** |
| Error contract | SRS / DOCUMENT_PROCESSING / SECURITY | **CONFIRMED semantic direction** |
| Future REST | This document | **FUTURE / TBD** |
| Future authentication | This document | **FUTURE / NOT MVP** |
| Future sync | This document | **FUTURE / NOT MVP** |

---

# 68. Open Decisions Register

| ID | Decision | Status |
|---|---|---|
| API-001 | Exact Android implementation language | **REQUIRES TECHNICAL VALIDATION** |
| API-002 | Exact UI framework | **REQUIRES TECHNICAL VALIDATION** |
| API-003 | Exact navigation/state mechanism | **REQUIRES TECHNICAL VALIDATION** |
| API-004 | Exact OCR integration | **REQUIRES TECHNICAL VALIDATION** |
| API-005 | Exact AI model | **TBD** |
| API-006 | Exact AI runtime | **TBD / REQUIRES TECHNICAL VALIDATION** |
| API-007 | Model packaging/update mechanism | **TBD** |
| API-008 | Exact Android storage API | **REQUIRES TECHNICAL VALIDATION** |
| API-009 | Room vs direct SQLite | **REQUIRES TECHNICAL VALIDATION** |
| API-010 | Exact export libraries | **TBD / REQUIRES TECHNICAL VALIDATION** |
| API-011 | Exact share/URI mechanism | **REQUIRES TECHNICAL VALIDATION** |
| API-012 | Exact source-reference representation | **REQUIRES TECHNICAL VALIDATION** |
| API-013 | Exact confidence aggregation | **TBD** |
| API-014 | Exact timeout values | **TBD** |
| API-015 | Resume-after-interruption behavior | **TBD** |
| API-016 | Future network product scope | **TBD** |

---

# 69. Release Readiness Checklist

## MVP Internal Contracts

- [ ] Internal service interfaces exist for required boundaries.
- [ ] Processing orchestration is isolated from UI.
- [ ] OCR is isolated behind an adapter.
- [ ] AI is isolated behind an adapter.
- [ ] Structured-data validation is implemented.
- [ ] Unknown/unresolved values are preserved honestly.
- [ ] User-edited data becomes authoritative after save.
- [ ] Repository boundary prevents direct UI-to-SQLite access.
- [ ] File-storage references are handled safely.
- [ ] Export consumes the current authoritative saved result.
- [ ] Sharing does not expose arbitrary internal filesystem paths.
- [ ] Model readiness is validated before offline processing.
- [ ] Cancellation works where technically supported.
- [ ] Failure states are truthful.
- [ ] Offline processing is validated with network disabled after setup.
- [ ] Routine diagnostics contain no raw document content.

## Future Network Separation

- [ ] Any backend introduction has explicit approval.
- [ ] Future endpoints are versioned.
- [ ] Authentication/authorization requirements are defined.
- [ ] Request validation is defined.
- [ ] Idempotency is defined.
- [ ] Error envelope is defined.
- [ ] Concurrency/versioning is defined.
- [ ] Rate limiting is defined.
- [ ] Privacy/security review is completed.

---

# 70. Non-Goals and Anti-Patterns

### Fake MVP REST backend

```text
Android → invented Express server → database
```

**Status:** REJECTED.

### UI-owned OCR

```text
Screen → OCR engine directly
```

**Status:** REJECTED architectural pattern.

### UI-owned SQL

```text
Screen → SQLite directly
```

**Status:** REJECTED.

### AI output as authority

```text
AI → Save/Export directly
```

**Status:** REJECTED.

### Hidden cloud fallback

```text
Local AI unavailable → silently upload document
```

**Status:** REJECTED.

### Fake completion

```text
Cancellation/failure → COMPLETED
```

**Status:** REJECTED.

### Historical technology treated as current fact

Historical React Native/TypeScript/Node.js/Express.js references must not be treated as the confirmed current Android implementation stack without project evidence.

---

# 71. Final API Baseline

SnapData v1.0 does **not** require a backend API.

The production-quality API architecture for the MVP is primarily an internal service-contract architecture:

```text
Android UI
      ↓
Application / Use Cases
      ↓
Domain Interfaces
      ↓
Infrastructure Adapters
      ├── Acquisition
      ├── Preprocessing
      ├── OCR
      ├── AI
      ├── Model Manager
      ├── SQLite Repositories
      ├── Local File Storage
      ├── Exporters
      └── Android Sharing
```

The future network boundary remains separate:

```text
                FUTURE / NOT MVP

Android App
     ↓ HTTPS
API Gateway
     ↓
Backend Services
     ├── Auth
     ├── Documents
     ├── Processing
     ├── Storage
     └── Sync
```

---

# 72. Source References

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_FRONTEND_v1.0.md`
6. `SnapData_DATABASE_v1.0.md`
7. `SnapData_DATA_SCHEMA_v1.0.md`
8. `SnapData_AI_OCR_v1.0.md`
9. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
10. `SnapData_EXPORT_v1.0.md`
11. `SnapData_TESTING_v1.0.md`
12. `SnapData_SECURITY_PRIVACY_v1.0.md`
13. `SnapData_BUILD_RELEASE_v1.0.md`
14. `SnapData_IMPLEMENTATION_PLAN_v1.0.md`
15. Original SnapData project specification and workflow diagram.
16. Actual Google AI Studio-generated Android project — required for implementation-specific confirmation.

---

# 73. Document Status

**Document:** `SnapData_API_SPECIFICATION_v1.0.md`  
**Version:** 1.0  
**Status:** Engineering Baseline  
**MVP REST API:** None / Not Required  
**MVP Architecture:** Local-first Android application  
**Future Network API:** Conceptual / TBD / Requires Product Approval  
**Exact Android implementation details:** Requires Technical Validation

---

**End of `SnapData_API_SPECIFICATION_v1.0.md`**
