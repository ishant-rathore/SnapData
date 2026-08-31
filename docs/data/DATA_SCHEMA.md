# SnapData — Canonical Data Schema & Data Contract

**Project:** SnapData  
**Document:** Canonical Data Schema & Data Contract  
**Version:** 1.0  
**Status:** Draft / Implementation Baseline  
**Date:** 30 August 2026  
**Filename:** `SnapData_DATA_SCHEMA_v1.0.md`

> **Authority:** This document is the canonical semantic data contract for SnapData structured document data. `SnapData_DATABASE_v1.0.md` owns SQLite table/column implementation; this document owns the meaning, shape, lifecycle, serialization semantics, and cross-layer contract of the data.

---

## 0. Source-of-Truth and Status Policy

### 0.1 Source hierarchy

The canonical data contract is derived from and must remain aligned with:

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
6. `SnapData_AI_OCR_v1.0.md`
7. `SnapData_DATABASE_v1.0.md`
8. `SnapData_FRONTEND_v1.0.md`
9. `SnapData_UI_UX_v1.0.md`
10. Original SnapData project specification
11. SnapData workflow diagram

### 0.2 Decision-status vocabulary

| Status | Meaning |
|---|---|
| **CONFIRMED** | Explicitly established by project source material or direct implementation evidence. |
| **PROPOSED** | Recommended technical direction that is not yet a verified implementation fact. |
| **TBD** | Decision has not yet been made. |
| **REQUIRES TECHNICAL VALIDATION** | Product intent is established, but exact feasibility, compatibility, or performance remains to be verified. |
| **OPTIONAL** | Permitted capability that is not required for the current baseline. |
| **REJECTED** | Intentionally excluded from the current baseline. |

### 0.3 Contract authority rule

All other project documents and implementation layers SHALL reuse the semantic names, meanings, lifecycle rules, null semantics, edit rules, and serialization rules defined here. They SHALL NOT create competing interpretations of fields, tables, status values, confidence, or source references.

**Status: CONFIRMED baseline / semantic ownership.**

---

# 1. Document Control

| Item | Value |
|---|---|
| Project | SnapData |
| Document | Canonical Data Schema & Data Contract |
| Version | 1.0 |
| Status | Draft / Implementation Baseline |
| Date | 30 August 2026 |
| Platform | Android/mobile, offline-first |
| Database | SQLite, source-backed |
| Backend | None required for current MVP |
| REST API | None required for current MVP |
| Canonical schema owner | This document |
| Database schema owner | `SnapData_DATABASE_v1.0.md` |
| OCR/AI implementation owner | `SnapData_AI_OCR_v1.0.md` |
| Processing pipeline owner | `SnapData_DOCUMENT_PROCESSING_v1.0.md` |
| UI behavior owner | `SnapData_UI_UX_v1.0.md` / `SnapData_FRONTEND_v1.0.md` |

---

# 2. Purpose and Scope

This document defines the canonical semantic data model used by SnapData to move information between acquisition, OCR, AI extraction, validation, review/editing, persistence, frontend rendering, and export.

The schema covers:

- source document identity and metadata;
- document pages;
- processing jobs and processing states;
- raw and normalized OCR representations;
- AI extraction candidates;
- structured extraction results;
- document type and document-type confidence;
- key-value fields;
- tables, columns, rows, and cells;
- confidence metadata;
- source/evidence references;
- warnings and errors;
- user edits and preservation of original extracted values;
- review state;
- validation state;
- processing/model metadata;
- export metadata;
- canonical JSON serialization;
- versioning and compatibility.

The contract is intentionally provider-neutral. OCR provider objects and AI runtime/model objects SHALL NOT leak into the canonical domain model.

---

# 3. Core Design Objectives

| Objective | Status | Contract implication |
|---|---|---|
| Represent extracted document information | **CONFIRMED** | Fields/tables are first-class domain concepts. |
| Support editable fields | **CONFIRMED** | Current value remains mutable; original extraction is preserved. |
| Support editable tables | **CONFIRMED** | Columns/rows/cells are separately addressable. |
| Preserve user corrections | **CONFIRMED** | `originalValue` is never overwritten by a user edit. |
| Support confidence metadata | **CONFIRMED** | Confidence is carried where supplied; no invented scores. |
| Support source references | **CONFIRMED capability / OPTIONAL data** | Source pointers are retained when available. |
| Support SQLite persistence | **CONFIRMED source-backed** | Semantic objects map to the relational DB model. |
| Support frontend rendering | **CONFIRMED** | Frontend consumes canonical semantic objects. |
| Support Excel/CSV/JSON/PDF export | **CONFIRMED** | Exporters consume the current authoritative result. |
| Support history/reopening | **CONFIRMED** | Document/result/job relationships preserve durable records. |
| Support schema evolution | **CONFIRMED requirement** | Every serialized result carries `schemaVersion`. |
| Avoid OCR-engine coupling | **CONFIRMED architectural principle** | Use provider-neutral OCR contract. |
| Avoid AI-model coupling | **CONFIRMED architectural principle** | Use candidate/result contract. |
| Avoid Android UI coupling | **CONFIRMED architectural principle** | Domain model has no screen/widget dependency. |

---

# 4. Canonical Data Lifecycle

```text
Original Source Document
        │
        ▼
Document / DocumentPage
        │
        ▼
OCRResult
(raw OCR + optional layout evidence)
        │
        ▼
Normalized OCR
        │
        ▼
AIExtractionCandidate
(machine interpretation)
        │
        ▼
StructuredCandidate
(fields + tables)
        │
        ▼
Validated StructuredResult
(schema/domain validation)
        │
        ▼
User Review / Edit
        │
        ▼
User-Approved / Saved Result
        │
        ├──────────────► Export
        │
        └──────────────► History / Reopen
```

### 4.1 Non-destructive data rule

The following distinction is mandatory:

```text
Source document
    ≠ OCR text
    ≠ AI extraction candidate
    ≠ structured extraction result
    ≠ user-edited authoritative result
```

A user edit MUST NOT overwrite the stored original extracted value.

---

# 5. Data Ownership Model

| Representation | Primary producer | Primary consumer | Persistence status |
|---|---|---|---|
| Source document | Acquisition layer | Pipeline, preview, history | Durable/local reference where supported |
| `DocumentPage` | Acquisition/normalization | Processing, preview | Optional but supported |
| `OCRResult` | OCR adapter | AI input adapter, diagnostics/review context | Implementation-dependent; raw OCR may be persisted |
| `AIExtractionCandidate` | Offline AI adapter | Parser/validator | Transient/intermediate by default |
| `StructuredCandidate` | Structuring/parser | Validator | Transient/intermediate by default |
| `ExtractionResult` | Structuring + validation | Review, persistence, export | Durable |
| `ExtractedField` | Extraction result builder | Editor, export | Durable |
| `ExtractedTable` | Extraction result builder | Editor, export | Durable |
| `TableColumn` | Table structurer | Editor, export | Durable semantic child |
| `TableRow` | Table structurer | Editor, export | Durable |
| `TableCell` | Table structurer/editor | Editor, export | Durable |
| `ProcessingWarning` | Processing/validation | UI, diagnostics | May be attached to result/job |
| `ProcessingError` | Processing/application layer | UI, recovery, diagnostics | Attached to job/result where appropriate |
| `ExportRecord` | Export layer | History/export UX | Durable metadata |
| `ModelMetadata` | Model manager | Setup/status | Optional durable metadata |

---

# 6. Canonical Object Inventory

| Object | Role | Baseline classification | Notes |
|---|---|---|---|
| `Document` | Source-document identity/lifecycle | **REQUIRED** | Core root aggregate. |
| `DocumentPage` | Page-level metadata | **OPTIONAL / REQUIRES TECHNICAL VALIDATION** | Recommended where multi-page/page-aware persistence is needed. |
| `ProcessingJob` | Processing attempt/state | **REQUIRED** | Supports retry/failure/history without overwriting prior results. |
| `OCRResult` | Provider-neutral OCR output | **REQUIRED processing contract** | Layout/confidence details are capability-dependent. |
| `OCRPage` | OCR result for a page | **OPTIONAL processing structure** | Required only when page-level OCR is available/useful. |
| `OCRBlock` | OCR block/layout unit | **OPTIONAL** | Engine-dependent. |
| `OCRLine` | OCR line unit | **OPTIONAL** | Engine-dependent. |
| `OCRWord` | OCR token/unit | **OPTIONAL** | Engine-dependent. |
| `BoundingBox` | Geometric source evidence | **OPTIONAL / ENGINE-DEPENDENT** | Not required when OCR provider cannot supply it. |
| `AIExtractionCandidate` | Raw AI structured proposal | **REQUIRED pipeline concept** | Intermediate; not authoritative. |
| `ExtractionResult` | Canonical structured extraction | **REQUIRED** | Authoritative semantic container after validation/review. |
| `ExtractedField` | Key-value field | **REQUIRED** | Addressable/editable. |
| `ExtractedTable` | Detected table | **REQUIRED** | Addressable/editable. |
| `TableColumn` | Table column definition | **REQUIRED** | Stable order/key. |
| `TableRow` | Table row | **REQUIRED** | Stable source/table order. |
| `TableCell` | Cell value | **REQUIRED** | Addressable/editable. |
| `ProcessingWarning` | Non-fatal diagnostic | **REQUIRED capability** | Exact warning codes remain implementation-governed. |
| `ProcessingError` | Failure metadata | **REQUIRED capability** | Sensitive document content must not be embedded unnecessarily. |
| `ExportRecord` | Export artifact metadata | **REQUIRED persistence concept** | Binary file remains in local file storage. |
| `ModelMetadata` | AI model package metadata | **OPTIONAL / REQUIRES TECHNICAL VALIDATION** | Model binary itself is not stored in SQLite. |

---

# 7. Root Aggregate: `Document`

`Document` represents the user's original source document and its durable lifecycle metadata. It does not itself contain detailed field/table extraction values.

## 7.1 Conceptual fields

| Field | Type | Req. | Allowed / format | Validation | Example | Status |
|---|---|---:|---|---|---|---|
| `id` | string/integer identifier | Required | Stable unique ID | Unique within local store | `101` | **CONFIRMED semantic need; exact ID type REQUIRES TECHNICAL VALIDATION** |
| `fileName` | string | Required where file-backed | Original filename | Non-empty after trim | `invoice_demo_01.pdf` | **CONFIRMED concept** |
| `fileType` | string | Optional | MIME type and/or extension representation | Must be consistent when both supplied | `application/pdf` | **PROPOSED semantic representation** |
| `filePath` / `fileReference` | string | Required for file-backed source persistence | Local storage reference | Must point to stable local artifact when persisted | `documents/101/original.pdf` | **CONFIRMED source-reference concept; exact Android path TBD** |
| `sourceType` | enum | Required | `CAMERA`, `IMAGE_UPLOAD`, `PDF_UPLOAD` | Enumerated | `PDF_UPLOAD` | **SOURCE-BACKED requested baseline; exact persisted enum naming REQUIRES TECHNICAL VALIDATION** |
| `documentType` | enum/string | Optional | Document-type vocabulary | Must not claim certainty beyond evidence | `INVOICE` | **CONFIRMED concept; enum vocabulary PROPOSED** |
| `title` | string | Optional | Human-readable document title | Preserve source/user title when known | `August Invoice` | **PROPOSED** |
| `pageCount` | integer | Optional | `>= 0` | Non-negative | `2` | **CONFIRMED concept; exact limits TBD** |
| `status` | enum | Required | Processing/document lifecycle status | See processing contract | `COMPLETED` | **PROPOSED canonical vocabulary** |
| `currentResultId` | identifier | Optional | References current `ExtractionResult` | Same document | `8001` | **OPTIONAL persistence optimization** |
| `createdAt` | date-time | Required | ISO 8601 UTC | Valid timestamp | `2026-08-30T15:15:55Z` | **CONFIRMED semantic concept** |
| `updatedAt` | date-time | Required | ISO 8601 UTC | Valid timestamp | `2026-08-30T15:18:12Z` | **CONFIRMED semantic concept** |

---

# 8. Source Type

Conceptual values:

| Value | Meaning | Status |
|---|---|---|
| `CAMERA` | Document captured by device camera | **CONFIRMED concept** |
| `IMAGE_UPLOAD` | Image imported from device storage | **CONFIRMED concept** |
| `PDF_UPLOAD` | PDF imported from device storage | **CONFIRMED concept** |

No additional source types are added to the v1.0 baseline.

---

# 9. Document Type Model

Document type is an AI-detected semantic classification and is not necessarily user-confirmed.

| Value | Meaning | Status |
|---|---|---|
| `INVOICE` | Invoice document | **SOURCE-BACKED EXAMPLE / PROPOSED canonical enum** |
| `RECEIPT` | Receipt document | **SOURCE-BACKED EXAMPLE / PROPOSED canonical enum** |
| `FORM` | Form-like document | **SOURCE-BACKED EXAMPLE / PROPOSED canonical enum** |
| `BANK_STATEMENT` | Bank statement | **SOURCE-BACKED EXAMPLE / PROPOSED canonical enum** |
| `CERTIFICATE` | Certificate | **SOURCE-BACKED example** |
| `MARK_SHEET` | Academic mark sheet | **SOURCE-BACKED example** |
| `ID_CARD` | Identity card | **SOURCE-BACKED example** |
| `BUSINESS_CARD` | Business/contact card | **SOURCE-BACKED example** |
| `TABLE_DOCUMENT` | Table-oriented document | **SOURCE-BACKED example** |
| `GENERAL_DOCUMENT` | General document | **SOURCE-BACKED example** |
| `UNKNOWN` | Type could not be reliably determined | **RECOMMENDED / REQUIRES TECHNICAL VALIDATION** |

---

# 10. Document Page

`DocumentPage` represents a persisted page in a multi-page document where page-level metadata is necessary for processing, preview, source references, or history.

| Field | Type | Req. | Validation | Status |
|---|---|---:|---|---|
| `id` | identifier | Required when persisted | Unique | **PROPOSED** |
| `documentId` | identifier | Required | References parent document | **CONFIRMED** |
| `pageNumber` | integer | Required | 1-based; unique within document | **PROPOSED / DATABASE-aligned** |
| `imageReference` | string | Optional | Stable local reference | **OPTIONAL** |
| `width` | integer | Optional | `> 0` when provided | **OPTIONAL** |
| `height` | integer | Optional | `> 0` when provided | **OPTIONAL** |
| `processingStatus` | enum/string | Optional | Implementation-defined | **REQUIRES TECHNICAL VALIDATION** |

---

# 11. Processing Job

`ProcessingJob` represents one processing attempt. A new retry should be representable without destructively overwriting a previously valid result.

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required | Stable processing-attempt ID | **CONFIRMED semantic need** |
| `documentId` | identifier | Required | Parent document | **CONFIRMED** |
| `status` | enum | Required | Current job state | **CONFIRMED requirement / vocabulary PROPOSED** |
| `currentStage` | enum/string | Optional | Current pipeline stage | **CONFIRMED concept / exact values REQUIRES TECHNICAL VALIDATION** |
| `progress` | number/object | Optional | Measured progress if available | **OPTIONAL** |
| `startedAt` | date-time | Optional | Processing start | **CONFIRMED concept** |
| `completedAt` | date-time | Optional | Completion time | **CONFIRMED concept** |
| `errorCode` | string | Optional | Stable error category | **CONFIRMED capability** |
| `errorMessage` | string | Optional | Safe summary | **CONFIRMED capability** |
| `pipelineVersion` | string | Optional | Pipeline version | **PROPOSED** |

Conceptual processing states:

```text
QUEUED
VALIDATING
PREPROCESSING
OCR_PROCESSING
AI_PROCESSING
STRUCTURING
VALIDATING_RESULT
WAITING_FOR_REVIEW
SAVING
COMPLETED
FAILED
CANCELLED
```

---

# 12. OCR Result Contract

`OCRResult` is a provider-neutral representation of OCR output.

```text
OCRResult
├── rawText
├── normalizedText (optional)
├── pages[]
│   ├── pageNumber
│   ├── rawText
│   ├── normalizedText (optional)
│   ├── blocks[]
│   │   ├── lines[]
│   │   │   └── words[]
│   │   ├── boundingBox (optional)
│   │   └── confidence (optional)
│   └── confidence (optional)
├── confidence (optional)
├── metadata (optional)
└── warnings[] (optional)
```

Provider capability may range from raw text only to blocks/lines/words with coordinates and confidence. The canonical model MUST NOT synthesize unavailable coordinates or confidence.

---

# 13. Raw OCR vs Normalized OCR

Raw OCR is the closest practical representation of provider output. Normalized OCR is a conservative downstream representation suitable for AI input and parsing.

Allowed normalization families may include:

- whitespace normalization;
- line-break normalization;
- Unicode normalization;
- duplicate-whitespace removal;
- conservative broken-line joining.

Normalization MUST NOT silently change identifiers, dates, decimal separators, currency, phone numbers, or emails.

---

# 14. OCR Geometry / Bounding Box

Conceptual structure:

```text
BoundingBox
├── x
├── y
├── width
└── height
```

**Status: OPTIONAL / ENGINE-DEPENDENT.**

Exact coordinate origin, units, scaling, and transforms are **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 15. OCR Confidence

Confidence MAY appear at document/result, page, block, line, or word levels. If unavailable:

```text
confidence = unavailable / null
```

Never substitute zero or an invented percentage.

Numeric `0.0–1.0` normalization remains **PROPOSED / REQUIRES TECHNICAL VALIDATION**.

---

# 16. AI Extraction Candidate

`AIExtractionCandidate` is a machine-generated candidate and is NEVER authoritative merely because it is syntactically valid.

```text
AIExtractionCandidate
├── documentTypeCandidate
├── documentTypeConfidence (optional)
├── fields[]
├── tables[]
├── summary (optional)
├── confidence (optional)
├── sourceReferences[] (optional)
└── warnings[] (optional)
```

The AI model SHALL NOT directly create database records.

---

# 17. Extraction Result

`ExtractionResult` is the canonical structured result container.

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required for persistence | Result snapshot ID | **CONFIRMED semantic need** |
| `documentId` | identifier | Required | Parent document | **CONFIRMED** |
| `processingJobId` | identifier | Required | Job that produced it | **CONFIRMED** |
| `documentType` | enum/string | Optional | Detected document type | **CONFIRMED concept** |
| `schemaVersion` | string | Required | Canonical schema version | **CONFIRMED** |
| `extractionTimestamp` | date-time | Required | Production time | **CONFIRMED** |
| `fields` | array | Required | Extracted fields | **CONFIRMED** |
| `tables` | array | Required | Extracted tables | **CONFIRMED** |
| `summary` | string/null | Optional | AI summary | **OPTIONAL / P1** |
| `confidence` | number/object/null | Optional | Overall confidence | **OPTIONAL** |
| `warnings` | array | Optional | Non-fatal warnings | **CONFIRMED capability** |
| `reviewState` | enum | Required for review flow | Human-review lifecycle | **PROPOSED / TBD exact final model** |
| `validationState` | enum | Optional | Validation outcome | **PROPOSED / persistence TBD** |
| `sourceMetadata` | object | Optional | Provenance | **OPTIONAL** |
| `rawOcr` | object/string | Optional | Raw OCR | **OPTIONAL** |
| `createdAt` | date-time | Required when persisted | Creation time | **CONFIRMED** |
| `updatedAt` | date-time | Required when persisted | Last mutation | **CONFIRMED** |
| `isCurrent` | boolean | Required in persistence model | Current-result marker | **CONFIRMED semantic / persistence concern** |

At most one persisted result per document should be current/authoritative.

---

# 18. Extracted Field

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required for persistence | Stable field ID | **CONFIRMED semantic need** |
| `resultId` | identifier | Required for persistence | Parent result | **CONFIRMED** |
| `key` | string | Required | Stable logical field identifier | **CONFIRMED** |
| `label` | string | Optional | Human-readable label | **CONFIRMED concept** |
| `value` | string/number/boolean/null | Required conceptually; may be null | Current authoritative value | **CONFIRMED** |
| `valueType` | enum | Required | Semantic value type | **PROPOSED vocabulary** |
| `originalValue` | string/null | Optional | Pre-edit extraction baseline | **CONFIRMED** |
| `editedFlag` | boolean | Required in persistence model | User changed value | **CONFIRMED** |
| `confidence` | number/null | Optional | Confidence | **CONFIRMED capability** |
| `sourcePage` | integer/null | Optional | Source page | **CONFIRMED capability** |
| `sourceReference` | object/string/null | Optional | Source evidence | **CONFIRMED capability** |
| `order` | integer | Optional | Display/export order | **PROPOSED** |
| `createdAt` | date-time | Optional | Creation time | **CONFIRMED** |
| `updatedAt` | date-time | Optional | Last mutation | **CONFIRMED** |

---

# 19. Value Types

Canonical conceptual types:

```text
TEXT
NUMBER
DATE
DATETIME
CURRENCY
BOOLEAN
EMAIL
PHONE
IDENTIFIER
UNKNOWN
```

Automatic conversion MUST NOT be forced when semantics are uncertain. Preserve textual representation or use `UNKNOWN` according to validated implementation policy.

---

# 20. Null, Missing, Empty, Unknown and Not Applicable

The canonical v1.0 strategy uses:

- `null` = no reliable/available value was extracted;
- `""` = source explicitly contains an empty textual value when that distinction is meaningful.

No separate required enum for `MISSING`, `UNKNOWN`, or `NOT_APPLICABLE` is introduced in v1.0.

---

# 21. User Edit Model

Canonical rule:

```text
Machine extraction
      ↓
originalValue
      ↓
User review/edit
      ↓
value
      ↓
Save
      ↓
Authoritative result
```

Example:

```json
{
  "key": "total_amount",
  "value": "10500",
  "originalValue": "10000",
  "editedFlag": true
}
```

A per-edit audit table is **REJECTED for MVP**; future extension is permitted.

---

# 22. Extracted Table

`ExtractedTable` represents a detected tabular region with deterministic ordering and editable cells.

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required for persistence | Table ID | **CONFIRMED semantic need** |
| `resultId` | identifier | Required | Parent result | **CONFIRMED** |
| `name` | string/null | Optional | Human-readable name | **CONFIRMED concept** |
| `confidence` | number/null | Optional | Table confidence | **OPTIONAL** |
| `sourcePage` | integer/null | Optional | Source page | **OPTIONAL** |
| `sourceReference` | object/string/null | Optional | Source table region | **OPTIONAL** |
| `columns` | array | Required | Column definitions | **CONFIRMED** |
| `rows` | array | Required | Table rows | **CONFIRMED** |
| `createdAt` | date-time | Optional | Creation time | **CONFIRMED** |
| `updatedAt` | date-time | Optional | Last mutation | **CONFIRMED** |

---

# 23. Table Column

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier/string key | Required within persisted table | Stable column identity | **PROPOSED** |
| `tableId` | identifier | Required for persistence | Parent table | **CONFIRMED** |
| `index` | integer | Required | Deterministic order | **CONFIRMED** |
| `key` | string/null | Optional | Stable logical column key | **CONFIRMED capability** |
| `label` | string | Optional | Human-readable header | **CONFIRMED concept** |
| `valueType` | enum/string | Optional | Preferred semantics | **PROPOSED** |

---

# 24. Table Row

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required for persistence | Stable row ID | **CONFIRMED semantic need** |
| `tableId` | identifier | Required | Parent table | **CONFIRMED** |
| `index` | integer | Required | Deterministic row order | **CONFIRMED** |
| `rowType` | enum/string/null | Optional | Header/data/total or similar | **OPTIONAL / exact values TBD** |
| `cells` | array | Required | Row cell collection | **CONFIRMED** |

---

# 25. Table Cell

| Field | Type | Req. | Description | Status |
|---|---|---:|---|---|
| `id` | identifier | Required for persistence | Stable cell ID | **CONFIRMED semantic need** |
| `rowId` | identifier | Required | Parent row | **CONFIRMED** |
| `columnId` / `columnKey` | identifier/string | Required conceptually | Identifies column | **CONFIRMED concept** |
| `columnIndex` | integer | Required | Deterministic reference | **CONFIRMED** |
| `value` | string/number/boolean/null | Required conceptually; may be null | Current authoritative value | **CONFIRMED** |
| `originalValue` | string/null | Optional | Pre-edit value | **CONFIRMED** |
| `editedFlag` | boolean | Required in persistence model | User changed cell | **CONFIRMED** |
| `valueType` | enum/string | Required/optional | Cell semantics | **PROPOSED** |
| `confidence` | number/null | Optional | Confidence | **CONFIRMED capability** |
| `sourcePage` | integer/null | Optional | Source page | **OPTIONAL** |
| `sourceReference` | object/string/null | Optional | Source reference | **OPTIONAL** |
| `createdAt` | date-time | Optional | Creation | **CONFIRMED** |
| `updatedAt` | date-time | Optional | Last mutation | **CONFIRMED** |

---

# 26. Table Structure Invariants

1. Column order is deterministic.
2. Row order is deterministic.
3. Cell column association is deterministic.
4. Missing cells remain missing/null; values are never fabricated.
5. Multi-line cell text remains one logical cell unless an approved transformation explicitly changes the structure.
6. OCR-fragmented content may be combined only when supported by structuring logic.
7. Repeated headers may remain represented as rows when detected.
8. Uneven row lengths are legal.
9. Empty tables are legal.
10. Complex merged-cell geometry is not represented in the MVP persistence model.

**Merged-cell persistence: REJECTED for MVP.**

---

# 27. Confidence Model

Confidence may appear at OCR/result, page, block, line, word, classification, overall, field, table, and cell levels.

The schema stores/communicates confidence; it does not invent confidence or define an unapproved aggregation formula.

**Aggregation formula: TBD / REQUIRES TECHNICAL VALIDATION.**

---

# 28. Source References / Evidence Model

Conceptual structure:

```text
SourceReference
├── sourcePage (optional)
├── sourceBlock (optional)
├── sourceLine (optional)
├── sourceWord (optional)
└── boundingBox (optional)
```

Exact serialized shape is **PROPOSED / REQUIRES TECHNICAL VALIDATION**.

Source references are optional and non-blocking.

---

# 29. Review State

Candidate detailed vocabulary:

```text
NOT_REVIEWED
IN_REVIEW
REVIEWED
MODIFIED
READY_TO_SAVE
SAVED
```

**Final exact review-state vocabulary: TBD / REQUIRES TECHNICAL VALIDATION.**

Regardless of enum spelling, the invariant is:

```text
Machine result
   ↓
Review/edit
   ↓
User-approved result
   ↓
Saved result
```

---

# 30. Validation State

Candidate values:

```text
VALID
VALID_WITH_WARNINGS
INVALID
PARTIAL
```

`VALID` means schema/domain validation succeeded. It does **not** guarantee factual correctness.

Persistence of a dedicated validation state is **PROPOSED / REQUIRES TECHNICAL VALIDATION**.

---

# 31. Processing Warning

Conceptual fields:

```text
code
message
stage (optional)
severity (optional)
sourceReference (optional)
```

Candidate warning codes are illustrative only and must reflect actual detected conditions.

---

# 32. Processing Error

Conceptual fields:

```text
code
message
stage (optional)
recoverable (optional)
details (optional)
```

Sensitive document content, secrets, and unnecessary extracted values MUST NOT be embedded in routine errors/logs.

---

# 33. Partial Result Model

SnapData SHOULD prefer safe recoverable partial results to total data loss where surviving data is valid.

Examples:

```text
OCR succeeds, AI fails
→ preserve OCR where supported
→ no false structured-success state
```

```text
AI succeeds, table extraction fails
→ preserve valid fields
→ table warning
→ result may be PARTIAL or VALID_WITH_WARNINGS
```

```text
Some fields unresolved
→ preserve resolved fields
→ unresolved values remain null/empty
→ review required
```

---

# 34. Summary Field

The AI-generated summary is a **P1 / OPTIONAL** feature.

```text
summary: optional string | null
```

A result remains valid for the P0 extraction workflow when no summary exists.

---

# 35. Processing Metadata

Candidate metadata includes:

```text
pipelineVersion
ocrEngineIdentifier (optional)
ocrEngineVersion (optional)
aiModelIdentifier (optional)
aiModelVersion (optional)
processingStartedAt (optional)
processingCompletedAt (optional)
capabilities (optional)
```

Exact provider metadata persistence is **OPTIONAL / REQUIRES TECHNICAL VALIDATION**.

---

# 36. Model Metadata

Conceptual structure:

```text
ModelMetadata
├── id
├── modelIdentifier
├── modelVersion
├── fileReference
├── status
├── installedAt
├── fileSizeBytes
├── checksum (optional)
├── createdAt
└── updatedAt
```

The model binary itself SHALL NOT be stored in SQLite. Only metadata/reference belongs there where model metadata persistence is implemented.

---

# 37. Export Record

`ExportRecord` stores metadata about an export artifact and is not itself the canonical structured data.

Supported formats:

```text
EXCEL
CSV
JSON
PDF
```

The binary export file remains in local file storage.

---

# 38. Currency Values

Currency extraction is sensitive to locale and formatting ambiguity.

Conceptual representation:

```text
CurrencyValue
├── amount
├── currency
└── originalValue
```

Exact nested representation is **TBD / REQUIRES TECHNICAL VALIDATION**.

When semantic conversion is unsafe, preserve the source text rather than forcing a numeric conversion.

---

# 39. Date and Date-Time Values

Normalized semantic date/time values SHOULD use ISO-style representations:

```text
YYYY-MM-DD
YYYY-MM-DDTHH:mm:ssZ
```

**Status: PROPOSED baseline.**

Ambiguous date strings must not be silently locale-converted.

---

# 40. Numeric Values

Numeric normalization must preserve source fidelity where formatting carries meaning.

```text
Source: 1,25,000
Normalized semantic value: 125000
Original source: 1,25,000
```

Exact locale-aware parsing is **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 41. Identifier Values

Document identifiers SHALL generally remain textual values, including invoice numbers, certificate numbers, roll numbers, and account/reference identifiers.

Do not automatically convert identifiers into numeric semantics merely because they contain digits.

---

# 42. Serialization Rules

- Use ISO 8601 UTC strings for timestamps where normalized semantic timestamps are represented.
- Use JSON numeric values only for safely numeric values.
- Use JSON booleans for genuine boolean semantics.
- Preserve Unicode content without lossy conversion.
- Use JSON `null` for missing/unavailable values according to the canonical rules.
- Keep database-only relational details out of the semantic JSON contract where nesting already expresses the relationship.

---

# 43. Canonical JSON Representation

Illustrative canonical shape:

```json
{
  "schemaVersion": "1.0",
  "document": {
    "id": "101",
    "fileName": "invoice_demo_01.pdf",
    "fileType": "application/pdf",
    "sourceType": "PDF_UPLOAD",
    "documentType": "INVOICE",
    "title": "Acme Invoice 01",
    "pageCount": 2,
    "status": "COMPLETED",
    "createdAt": "2026-08-30T15:15:55Z",
    "updatedAt": "2026-08-30T15:18:12Z"
  },
  "result": {
    "documentType": "INVOICE",
    "fields": [
      {
        "key": "invoice_number",
        "label": "Invoice Number",
        "value": "INV-2026-0142",
        "valueType": "IDENTIFIER",
        "originalValue": "INV-2026-0142",
        "editedFlag": false,
        "confidence": 0.99,
        "sourcePage": 1
      }
    ],
    "tables": [],
    "summary": null,
    "confidence": null,
    "warnings": [],
    "reviewState": "NOT_REVIEWED"
  }
}
```

This example is illustrative and does not create invoice-specific hard-coding or a finalized confidence aggregation model.

---

# 44. Formal JSON Schema

The approved source provides a Draft 2020-12 JSON Schema for the canonical semantic result. Preserve that formal schema exactly from the source document in the repository copy, including:

- `$schema` and `$id`;
- `schemaVersion` constraint;
- `document` definition;
- `extractionResult` definition;
- field/table/row/cell definitions;
- warning/OCR definitions;
- value-type enums;
- review/validation states;
- bounding-box definitions;
- `additionalProperties` policy.

Do not replace it with a newly designed schema.

---

# 45. Frontend Mapping

The frontend SHALL consume the semantic contract rather than invent a second extraction model.

| Canonical object | Frontend concept | Status |
|---|---|---|
| `ExtractionResult` | `DocumentResult` | **CONFIRMED mapping concept** |
| `ExtractedField` | `EditableField` | **CONFIRMED mapping concept** |
| `ExtractedTable` | `EditableTable` | **CONFIRMED mapping concept** |
| `TableRow` | `EditableRow` | **CONFIRMED mapping concept** |
| `TableCell` | `EditableCell` | **CONFIRMED mapping concept** |
| `ProcessingJob` | `ProcessingState` | **CONFIRMED mapping concept** |
| `Document` | Document/history view model | **CONFIRMED concept** |
| `ProcessingWarning` | Review/status message | **CONFIRMED concept** |

UI may introduce display-only properties, but these must not become authoritative domain values without an explicit contract update.

---

# 46. AI/OCR Mapping

```text
OCR Adapter
    ↓
OCRResult
    ↓
Normalized OCR Context
    ↓
AI Adapter
    ↓
AIExtractionCandidate
    ↓
StructuredDataBuilder
    ↓
ExtractionResult
    ↓
Validation
    ↓
Reviewable Result
```

Provider changes should primarily affect adapters/capability mapping rather than redefining field/table semantics.

---

# 47. Database Mapping

`SnapData_DATABASE_v1.0.md` remains the owner of physical SQLite schema, constraints, indexes, migrations, and storage implementation.

| Canonical object | SQLite entity | Status |
|---|---|---|
| `Document` | `document` | **CONFIRMED source-backed** |
| `DocumentPage` | `document_page` | **OPTIONAL / REQUIRES TECHNICAL VALIDATION** |
| `ProcessingJob` | `processing_job` | **CONFIRMED source-backed** |
| `ExtractionResult` | `extraction_result` | **CONFIRMED source-backed** |
| `ExtractedField` | `extracted_field` | **CONFIRMED source-backed** |
| `ExtractedTable` | `extracted_table` | **CONFIRMED source-backed** |
| `TableRow` | `extracted_table_row` | **CONFIRMED source-backed** |
| `TableCell` | `extracted_table_cell` | **CONFIRMED source-backed** |
| `ExportRecord` | `export_record` | **CONFIRMED source-backed** |
| `ModelMetadata` | `model_metadata` | **OPTIONAL / REQUIRES TECHNICAL VALIDATION** |

This document must not redefine SQLite columns, indexes, migrations, or foreign-key implementation beyond semantic mapping.

---

# 48. Export Representation

Export SHALL use the current saved/user-approved structured result.

### Excel
Fields and tables are exported using current values. Exact worksheet layout is implementation-dependent.

### CSV
Single-table export is supported; multi-table CSV packaging remains TBD unless explicitly defined by implementation.

### JSON
JSON export uses the canonical semantic representation defined here.

### PDF
PDF export is a presentation representation of the current saved structured result. Exact pagination/layout is implementation-dependent.

---

# 49. Schema Versioning

Every canonical serialized result SHALL contain:

```json
"schemaVersion": "1.0"
```

Version semantics are **PROPOSED / REQUIRES TECHNICAL VALIDATION**:

- Major = breaking semantic/structural change;
- Minor = backward-compatible extension;
- Patch = clarification/non-structural change.

---

# 50. Backward Compatibility

Older saved records SHOULD remain readable through migration or adapter logic.

```text
Older Schema
     ↓
Migration / Adapter
     ↓
Current Schema
```

Migration SHALL NOT silently discard unknown fields unless explicitly documented.

---

# 51. Forward Compatibility

Where possible, older application versions receiving newer canonical data SHOULD fail gracefully rather than corrupting or silently misinterpreting it.

Recommended:

- tolerate unknown optional fields;
- preserve unknown metadata during migration where possible;
- reject unsupported breaking versions safely;
- never reinterpret a newer field with a different meaning without explicit migration rules.

---

# 52. Data Validation Rules

The canonical validator SHALL check, as applicable:

1. required identifiers are present;
2. enum values are supported;
3. timestamps are valid;
4. page numbers are positive and deterministic;
5. table column indexes are non-negative and ordered;
6. row indexes are non-negative and deterministic;
7. cell column association is coherent;
8. field keys are non-empty;
9. `editedFlag` and `originalValue` semantics remain consistent;
10. confidence values are range-validated only when the contract guarantees the range;
11. warnings/errors use defined shapes;
12. missing values are not fabricated;
13. canonical JSON is structurally valid;
14. nested data belongs to the correct parent;
15. current-result invariants are maintained at persistence boundary.

---

# 53. Hallucination / Unsupported Value Controls

The canonical schema SHALL support:

```text
No source evidence
       ↓
Do not synthesize a factual value
       ↓
Preserve missing/uncertain state
```

Controls include source grounding, optional source references, confidence, warnings, validation, review before export, user-correction authority, and no fabricated defaults.

---

# 54. Save and Authority Boundary

Only a structurally valid, user-reviewable result SHALL cross into durable persistence.

```text
Validated Structured Result
        ↓
User Review / Edit
        ↓
User-Approved Result
        ↓
Save Transaction
        ↓
Authoritative Saved Result
```

Original source identity/content is never overwritten merely because the processed result is edited.

A future reprocessing operation SHOULD create a new processing job/result rather than overwrite a saved authoritative result.

---

# 55. Persistence vs Transient Data

### Durable data

- source-document metadata/reference;
- extraction result;
- fields;
- tables/rows/cells;
- relevant processing metadata;
- processing history;
- export metadata.

### Transient data

- temporary UI/navigation state;
- temporary OCR/preprocessing buffers;
- in-flight model runtime objects;
- uncommitted candidates unless specifically retained for recovery.

---

# 56. Edge Cases

### Empty OCR
Represent no usable OCR without fabricating structured values.

### OCR error
Represent failure through job/error state; preserve prior valid saved results.

### Missing field
Use `null` and/or warning/source context.

### Empty field
Use empty string when explicit source blankness matters.

### Table missing cell
Do not fabricate content.

### Multi-line cell
Preserve as one logical text value when appropriate.

### Repeated headers
May remain represented as rows when detected.

### App interruption
Do not convert interruption into `COMPLETED`.

### Missing source file
Do not claim the source is available if its physical local reference no longer resolves.

---

# 57. Security / Privacy Data Rules

Rules:

- Do not log raw sensitive document text by default.
- Do not place secrets in document fields or schema examples.
- Do not transmit canonical document data to a server in the MVP core workflow.
- Model binaries are stored outside SQLite.
- Encryption, secure-delete, and biometric/PIN behavior remain TBD unless separately approved.

---

# 58. API Boundary

There is no required REST API for the current MVP.

The schema functions primarily as an internal/domain/serialization contract between:

```text
Acquisition
   ↓
Processing
   ↓
Review/Edit
   ↓
Persistence
   ↓
Export
```

---

# 59. End-to-End Example

```text
Document
 ├── sourceType = PDF_UPLOAD
 ├── documentType = INVOICE
 └── pageCount = 2
        │
        ▼
OCRResult
 ├── rawText
 ├── pages[1..2]
 └── optional confidence/geometry
        │
        ▼
AIExtractionCandidate
 ├── invoice_number
 ├── invoice_date
 ├── vendor_name
 └── line_items table
        │
        ▼
Validated ExtractionResult
 ├── fields[]
 ├── tables[]
 ├── warnings[]
 └── review state
        │
        ▼
User edits vendor_name
        │
        ├── originalValue = "Acme Office Supplles"
        └── value = "Acme Office Supplies"
        │
        ▼
Save
        │
        ▼
Current authoritative result
        │
        ├── Excel
        ├── CSV
        ├── JSON
        └── PDF
```

---

# 60. Traceability Matrix

| Canonical concern | Source artifact | Alignment |
|---|---|---|
| Document identity/source | PRD, SRS, DATABASE | **CONFIRMED** |
| Camera/PDF/image source types | PRD, SRS, original specification | **CONFIRMED** |
| Multi-page pages | PRD/roadmap, DATABASE | **OPTIONAL / validation-dependent** |
| Processing states | SRS, architecture, processing docs | **CONFIRMED requirement; exact vocabulary validation pending** |
| OCR result model | AI_OCR, Document Processing | **CONFIRMED concept** |
| Raw vs normalized OCR | AI_OCR | **CONFIRMED** |
| AI candidate | Document Processing, Architecture | **CONFIRMED architectural concept** |
| Fields/key-value pairs | PRD, SRS, DATABASE | **CONFIRMED** |
| Tables/rows/cells | PRD, SRS, DATABASE | **CONFIRMED** |
| Confidence | PRD, AI_OCR, UI_UX | **CONFIRMED where available; aggregation TBD** |
| Source references | AI_OCR, Document Processing, UI_UX | **OPTIONAL capability** |
| User edit authority | PRD, SRS, DATABASE, architecture | **CONFIRMED** |
| Review status | SRS, UI_UX, DATABASE | **CONFIRMED concept; exact final enum TBD** |
| Validation state | Processing docs / architecture | **PROPOSED; persistence TBD** |
| Warnings/errors | SRS, processing docs | **CONFIRMED capability** |
| Summary | PRD/UI_UX | **OPTIONAL/P1** |
| SQLite mapping | DATABASE | **CONFIRMED source-backed** |
| Frontend mapping | FRONTEND | **CONFIRMED contract dependency** |
| Export formats | PRD/SRS/TRD | **CONFIRMED** |
| JSON canonical serialization | Architecture/DATA_SCHEMA responsibility | **CONFIRMED semantic ownership** |
| Schema version | DATABASE + canonical contract | **CONFIRMED requirement** |

---

# 61. Contract Rules for Other Documents

1. `PRD` defines product need; it does not redefine schema semantics.
2. `SRS` defines observable behavior; it references this schema for structured-data meaning.
3. `TRD` defines technical choices; it must not create a competing serialized data model.
4. `SYSTEM_ARCHITECTURE` defines boundaries; its structured-result concepts map to this contract.
5. `DOCUMENT_PROCESSING` defines stage behavior; stage outputs conform to these objects.
6. `AI_OCR` defines provider/model implementation; provider outputs adapt into `OCRResult`/candidate contracts.
7. `DATABASE` owns physical tables/constraints, while semantic field meaning comes from this document.
8. `FRONTEND` consumes canonical semantic objects and may create presentation view models, but not a competing domain model.
9. `UI_UX` defines user-facing presentation without changing canonical meaning.
10. Exporters consume the current authoritative structured result.

---

# 62. Implementation Acceptance Criteria

- [ ] Every persisted structured result carries `schemaVersion`.
- [ ] Original source identity is distinct from extraction results.
- [ ] OCR output is provider-neutral.
- [ ] Raw OCR and normalized OCR are distinguishable.
- [ ] AI candidate data is not authoritative without validation/review.
- [ ] Fields support current value + original extracted value.
- [ ] Tables support deterministic columns, rows, and cells.
- [ ] Missing values can be represented without fabrication.
- [ ] Confidence is nullable/unavailable when not supplied.
- [ ] Source references are optional and non-blocking.
- [ ] User-edited values become authoritative for save/export.
- [ ] Original extracted values remain preserved.
- [ ] Review and processing state are not conflated.
- [ ] Validation state does not imply factual correctness.
- [ ] Partial results are representable where safe.
- [ ] Exporters read the current authoritative result.
- [ ] SQLite mappings agree with `DATABASE.md`.
- [ ] Frontend mappings agree with `FRONTEND.md`.
- [ ] AI/OCR adapters agree with `AI_OCR.md`.
- [ ] Schema migrations are tested.
- [ ] Compatibility behavior is documented.

---

# 63. Known Open Decisions

| Decision | Status |
|---|---|
| Exact Android ID type and serialization strategy | **REQUIRES TECHNICAL VALIDATION** |
| Exact persisted source-type enum spelling | **REQUIRES TECHNICAL VALIDATION** |
| Final document-type enum set | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Exact review-state enum | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Validation-state persistence | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Exact confidence normalization/range | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Confidence aggregation formula | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Bounding-box coordinate units/origin | **TBD / REQUIRES TECHNICAL VALIDATION** |
| OCR language list | **TBD** |
| Exact OCR engine integration | **REQUIRES TECHNICAL VALIDATION** |
| Exact AI model/runtime | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Currency nested representation | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Multi-table CSV packaging convention | **TBD** |
| Exact export layouts | **REQUIRES TECHNICAL VALIDATION** |
| Exact Android file-reference strategy | **REQUIRES TECHNICAL VALIDATION** |
| Database migration tooling | **REQUIRES TECHNICAL VALIDATION** |
| Encryption/secure-delete implementation | **TBD** |
| Full edit audit trail | **REJECTED for MVP** |
| Merged-cell persistence | **REJECTED for MVP** |
| Required backend/API | **REJECTED / NOT REQUIRED for MVP** |

---

# 64. Final Canonical Baseline

```text
Document
  ↓
DocumentPage (optional)
  ↓
ProcessingJob
  ↓
OCRResult
  ↓
AIExtractionCandidate
  ↓
ExtractionResult
  ├── ExtractedField[]
  ├── ExtractedTable[]
  │     ├── TableColumn[]
  │     └── TableRow[]
  │            └── TableCell[]
  ├── confidence (optional)
  ├── warnings (optional)
  ├── source references (optional)
  ├── review state
  ├── validation state (optional/proposed)
  └── summary (optional/P1)
        ↓
User Review / Edit
        ↓
Saved Authoritative Result
        ↓
Excel / CSV / JSON / PDF
```

### Canonical authority rule

> **The current saved value is authoritative for persistence, reopening, frontend display of the saved result, and export. The original extracted value remains preserved and must never be overwritten merely because the user edits the result.**

### Architectural rule

> **The canonical schema is provider-neutral, UI-neutral, and storage-semantic. OCR engines, AI models/runtimes, Android UI frameworks, and SQLite implementation details must adapt to the contract rather than redefine it.**

---

# Appendix A — Source References

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
6. `SnapData_AI_OCR_v1.0.md`
7. `SnapData_DATABASE_v1.0.md`
8. `SnapData_FRONTEND_v1.0.md`
9. `SnapData_UI_UX_v1.0.md`
10. Original SnapData project specification
11. SnapData workflow diagram

---

# Appendix B — Version 1.0 Contract Checklist

### Required

- [x] Canonical `Document` concept.
- [x] Canonical `ProcessingJob` concept.
- [x] Canonical `OCRResult` concept.
- [x] Canonical `ExtractionResult` concept.
- [x] Canonical fields and tables.
- [x] Original/current edit model.
- [x] Confidence metadata.
- [x] Source references.
- [x] Review semantics.
- [x] Validation semantics.
- [x] Warning/error semantics.
- [x] Export mapping.
- [x] Database mapping.
- [x] Frontend mapping.
- [x] AI/OCR mapping.
- [x] JSON example.
- [x] Formal JSON Schema Draft 2020-12.
- [x] Versioning rules.
- [x] Backward/forward compatibility guidance.
- [x] Missing/null/empty handling.
- [x] Date/currency/numeric/identifier preservation rules.
- [x] MVP exclusions for merged-cell persistence and full edit audit.

### Still open

- [ ] Final exact implementation enum spelling.
- [ ] Final confidence semantics/aggregation.
- [ ] Final source-reference coordinate convention.
- [ ] Final review-state vocabulary.
- [ ] Final validation-state persistence.
- [ ] Final multi-table CSV convention.
- [ ] Final schema migration implementation.

---

**Document status:** Draft / Implementation Baseline  
**Canonical semantic owner:** `SnapData_DATA_SCHEMA_v1.0.md`  
**Physical persistence owner:** `SnapData_DATABASE_v1.0.md`  
**Date:** 30 August 2026
