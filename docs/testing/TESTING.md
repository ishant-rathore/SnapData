# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## QA, Testing & Validation Strategy and Implementation

**Project:** SnapData  
**Document:** QA, Testing & Validation Strategy and Implementation  
**Version:** 1.0  
**Status:** Draft / QA Baseline  
**Date:** 30 August 2026  
**Filename:** `SnapData_TESTING_v1.0.md`

> **Authority:** This document defines how SnapData is verified from static analysis and unit tests through complete offline end-to-end and acceptance validation. It is subordinate to the PRD/SRS/TRD/system architecture and must not convert unverified implementation choices into confirmed facts.

# 1. Document Control

| Item | Value |
|---|---|
| Project | SnapData |
| Document | QA, Testing & Validation Strategy and Implementation |
| Version | 1.0 |
| Status | Draft / QA Baseline |
| Date | 30 August 2026 |
| Target | Android/mobile, offline-first |
| Core storage | SQLite + Android-local file storage boundary |
| Backend | None required for current MVP |
| REST API | None required for current MVP |
| Core processing | Local after required model setup |
| Canonical data owner | `SnapData_DATA_SCHEMA_v1.0.md` |
| Processing owner | `SnapData_DOCUMENT_PROCESSING_v1.0.md` |
| AI/OCR owner | `SnapData_AI_OCR_v1.0.md` |
| Export owner | `SnapData_EXPORT_v1.0.md` |
| DB owner | `SnapData_DATABASE_v1.0.md` |
| UI/UX owner | `SnapData_UI_UX_v1.0.md` |
| Frontend owner | `SnapData_FRONTEND_v1.0.md` |

## 1.1 Status Vocabulary

| Status | Meaning |
|---|---|
| **CONFIRMED** | Explicitly established by source material or direct implementation evidence. |
| **PROPOSED** | Recommended testing/design direction, not yet proven in implementation. |
| **TBD** | Decision has not yet been made. |
| **REQUIRES TECHNICAL VALIDATION** | Intent is known, but exact feasibility, compatibility, or measurement remains to be proven. |
| **OPTIONAL** | Useful capability not required for the current baseline. |
| **BLOCKED** | Cannot be validly executed until a named dependency is available. |

> **Testing rule:** A test may verify only behavior supported by the current source baseline. Exact device minimums, model/runtime, OCR integration, page limits, quantitative accuracy thresholds, and advanced security mechanisms remain open until technical validation establishes them.

# 2. Source of Truth and Scope

## 2.1 Primary Sources

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_UI_UX_v1.0.md`
6. `SnapData_FRONTEND_v1.0.md`
7. `SnapData_DATABASE_v1.0.md`
8. `SnapData_AI_OCR_v1.0.md`
9. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
10. `SnapData_DATA_SCHEMA_v1.0.md`
11. `SnapData_EXPORT_v1.0.md`

## 2.2 Supporting Sources

12. Original SnapData project specification  
13. Supplied SnapData workflow diagram

The original project specification defines conversion of PDFs/images into structured editable information using OCR and AI, local/offline operation after initial AI model setup, SQLite local storage, and Excel/CSV/JSON/PDF export. The supplied workflow diagram visually follows acquisition → preprocessing → OCR → offline AI → structured data → review/edit → local storage → export → history and shows Tesseract OCR, Offline AI and SQLite in its technology context.

## 2.3 Testing Scope

This strategy covers:

- Android application behavior;
- frontend/UI state and navigation;
- local application/service/domain boundaries;
- API/service boundary **only if such a component is later approved**;
- SQLite and local file persistence;
- camera/PDF/image ingestion;
- image preprocessing;
- OCR;
- offline AI model execution;
- classification;
- key-value extraction;
- table extraction;
- canonical structured data;
- confidence and warnings;
- human review/editing;
- authoritative save behavior;
- history and reopening;
- Excel/CSV/JSON/PDF export;
- Android sharing;
- offline/no-network operation;
- performance, memory and resource behavior;
- security/privacy;
- errors and recovery;
- regression;
- acceptance and release readiness.

## 2.4 Boundary Rule

The test suite SHALL NOT assume that a backend, REST API, cloud OCR service, cloud AI provider, Room, a specific UI framework, a specific export library, or a specific Android file API exists unless actual project evidence confirms it.

# 3. Quality Objectives

Testing must establish that:

1. Documents can be acquired.
2. Images/PDFs can be processed.
3. OCR produces usable text or a truthful empty/failure state.
4. AI correctly analyzes supported documents within validated scope.
5. Fields are extracted into structured data.
6. Tables are extracted into structured data.
7. Confidence is handled honestly where supplied.
8. Users can review results.
9. Users can edit fields.
10. Users can edit supported table content.
11. User corrections become authoritative after save.
12. Saved documents can be reopened with retained data.
13. Export uses the current authoritative saved result.
14. Excel/CSV/JSON/PDF outputs are valid and readable.
15. Core processing works offline after required model setup.
16. Failures do not silently convert into false success.
17. Sensitive document content stays local unless an explicitly approved exception exists.
18. Data is not silently lost, truncated or corrupted.

# 4. Critical Quality Risks

| Risk | Impact | Primary validation |
|---|---|---|
| OCR produces wrong text | High | OCR benchmark, field accuracy, regression |
| AI fabricates missing values | High | Evidence-grounding tests, negative cases |
| Table structure shifts | High | Row/column/cell benchmark |
| User edit overwritten | Critical | Edit-authority E2E test |
| Offline path silently calls network | Critical | Network dependency audit + airplane-mode tests |
| Save succeeds falsely | Critical | Persistence failure injection |
| Export uses stale AI result | Critical | Cross-layer authoritative-result tests |
| Data corruption after interruption | Critical | Crash/restart/recovery tests |
| Unicode corruption | High | Multilingual/export validation |
| Memory pressure/ANR | High | Large-document + stress tests |
| Unsafe path/file handling | High | Security/fuzz/file-name tests |
| Missing model treated as a generic crash | Medium | Model readiness/error tests |
| Confidence misrepresented | High | Contract and UI semantics tests |
| Unsupported feature accidentally tested as required | Medium | Scope gate + traceability review |

# 5. Testing Pyramid

```text
                    E2E
                   /   \
              Acceptance
              Integration
             /           \
          Component     API
             \           /
                 Unit
```

### Unit
Fast, isolated verification of deterministic logic. Examples: validation, normalization, serialization, mapping, confidence handling and repository methods.

### Component
Verifies a module as a coherent unit with controlled dependencies. Examples: OCR adapter, preprocessing pipeline, editor state, exporter, model manager.

### API
Verifies service contracts only if a service/API boundary is actually implemented. For the current MVP there is no required REST API; therefore API tests are **OPTIONAL / BLOCKED** until an API exists.

### Integration
Verifies interactions between real modules: acquisition → processing, processing → schema, schema → SQLite, saved result → export, export → sharing.

### Acceptance
Validates user-visible product outcomes against SRS acceptance criteria and the approved project workflow.

### E2E
Exercises the full system through the real Android application with representative documents and device conditions.

> **Quality principle:** Lower-level tests provide speed and diagnosis; higher-level tests provide confidence that the complete user workflow actually works.

# 6. Test Levels

| Level | ID | Purpose | Primary evidence |
|---|---|---|---|
| Static analysis | L0 | Detect defects before runtime | Lint/type/static reports |
| Unit | L1 | Verify isolated logic | Automated unit report |
| Component | L2 | Verify module behavior | Component suite |
| Integration | L3 | Verify module boundaries | Integration report |
| API/service | L4 | Verify approved service contracts | API test collection **if applicable** |
| Database | L5 | Verify persistence/integrity | SQLite integrity + migration results |
| AI/OCR evaluation | L6 | Measure extraction/classification quality | Benchmark report |
| UI | L7 | Verify screens/states/interactions | UI test report/checklist |
| End-to-end | L8 | Verify complete workflow | Traceable E2E evidence |
| Performance | L9 | Verify resource/latency behavior | Measurements |
| Security/privacy | L10 | Verify local data safety and input hardening | Security checklist + results |
| Offline | L11 | Verify network-independent core workflow | Offline test report |
| Acceptance | L12 | Verify product-level outcomes | Acceptance record |
| Regression | L13 | Detect unintended change after modifications | Regression report |

# 7. Test Environment Strategy

## 7.1 Development

**Purpose:** rapid feedback while implementing features.

**Configuration:** developer build, local test data, logging enabled within privacy-safe limits.

**Dependencies:** actual project dependencies plus local models/resources required for tested flows.

**Limitations:** not release evidence; data and device combinations are not necessarily representative.

**Status:** CONFIRMED concept; exact build tooling **REQUIRES TECHNICAL VALIDATION** from the real Google AI Studio Android project.

## 7.2 Test

**Purpose:** repeatable automated and manual QA.

**Configuration:** controlled build, versioned dataset, deterministic test fixtures where possible, network toggles, storage/memory test controls.

**Dependencies:** validated OCR/AI artifacts, database schema, export modules.

**Limitations:** may use mocks/fakes that must be supplemented with real-device tests.

## 7.3 Staging

**Purpose:** release-candidate validation on representative devices and near-production configuration.

**Configuration:** release-like build, production-intent model/resources, privacy-safe logging.

**Dependencies:** only approved local resources; no cloud dependency for core processing.

**Limitations:** exact staging configuration **TBD** because the final deployment pipeline is not yet confirmed.

## 7.4 Production Validation

**Purpose:** final smoke/regression checks on the exact release artifact.

**Configuration:** signed release artifact, approved device matrix, clean install and upgrade paths.

**Data:** synthetic/controlled documents only.

**Limitation:** production validation must not use real private documents.

## 7.5 Android Local Test Environment

Because the product is offline-first, QA SHALL maintain a test mode that can run with:

- Wi-Fi disabled;
- mobile data disabled;
- airplane mode enabled;
- network physically unavailable;
- intermittent connectivity;
- model already installed;
- model absent;
- constrained storage;
- constrained memory where practical.

# 8. Test Device Matrix

Exact device requirements remain **TBD** / **REQUIRES TECHNICAL VALIDATION**.

| Dimension | Coverage strategy | Status |
|---|---|---|
| Android version | At least one lower validated version + current supported version | TBD |
| Small phone | Validate layout and touch behavior | PROPOSED |
| Standard phone | Primary reference device | PROPOSED |
| Large phone | Validate responsive layout | PROPOSED |
| Tablet | Only if later approved | OPTIONAL / TBD |
| Low RAM | Stress memory-sensitive stages | REQUIRES TECHNICAL VALIDATION |
| Mid-range CPU/RAM | Core benchmark device | REQUIRES TECHNICAL VALIDATION |
| High-end CPU/RAM | Upper-performance reference | REQUIRES TECHNICAL VALIDATION |
| Low storage | Recovery/storage-full tests | PROPOSED |
| Camera quality | Low/medium/high capture quality | REQUIRES TECHNICAL VALIDATION |
| Screen density | UI/readability | REQUIRES TECHNICAL VALIDATION |

### Device selection rule

Do not define an exact minimum Android version, RAM, CPU, storage or camera specification until the generated Android application and selected AI/OCR runtime are inspected and benchmarked.

# 9. Test Data Strategy

All QA documents SHALL use controlled, synthetic or permission-safe test documents.

## 9.1 Dataset Categories

A. Clean printed documents  
B. Low-quality scans  
C. Camera photographs  
D. Rotated documents  
E. Skewed documents  
F. Noisy documents  
G. Low-light documents  
H. Multi-page PDFs  
I. Table-heavy documents  
J. Key-value forms  
K. Mixed-language documents  
L. Unicode-heavy documents  
M. Empty/invalid documents  
N. Very large documents

## 9.2 Negative/Adversarial Documents

Include synthetic cases for:

- misleading extensions;
- corrupt PDF structure;
- malformed images;
- invalid embedded metadata;
- path traversal filenames;
- huge dimensions;
- unsupported compression;
- blank pages;
- pages containing no readable text;
- unusual Unicode;
- repeated headers;
- irregular tables;
- intentionally ambiguous dates/numbers.

## 9.3 No Real Private Documents

Do not place real bank statements, identity documents, academic records, invoices, receipts, medical records or other private documents into the shared QA corpus unless explicit authorization exists. Prefer generated data that preserves the target structure without exposing real personal data.

# 10. Ground Truth Dataset

Each AI/OCR benchmark item SHALL pair a source document with manually verified expected output.

```text
Document
+
Known-correct ground truth
+
Document type
+
Expected OCR text
+
Expected fields
+
Expected tables
+
Expected negative/missing values
```

### Example

**Input:** synthetic invoice image

**Ground truth:**

```text
Invoice Number = INV-1023
Date = 2026-08-30
Vendor = Example Ltd
Total = 10500
```

**Expected structured result:**

```json
{
  "fields": [
    {"key": "invoice_number", "value": "INV-1023"},
    {"key": "date", "value": "2026-08-30"},
    {"key": "vendor", "value": "Example Ltd"},
    {"key": "total", "value": "10500"}
  ]
}
```

The example is illustrative; it does not create an invoice-specific hard-coded implementation requirement.

## 10.1 Ground Truth Review

Each ground-truth item should be independently checked by a second reviewer where feasible. Disagreements SHALL be resolved before a document enters the release benchmark.

# 11. Dataset Versioning

Every benchmark item SHALL have:

- Dataset version;
- Document ID;
- Document type;
- source variant ID;
- ground-truth version;
- expected fields;
- expected tables;
- expected OCR text where benchmarked;
- known limitations/ambiguities;
- reviewer status.

Suggested identifier:

```text
SD-DOC-0001
SD-DOC-0002
...
```

A benchmark result SHALL record the dataset version used so that model/OCR comparisons remain traceable.

# 12. Requirement Traceability

The SRS establishes FR-001..FR-054, NFR-001..NFR-028, ERR-001..ERR-018 and AC-001..AC-012, with verification methods defined for requirements. The testing suite SHOULD maintain a requirement-to-test matrix with one or more tests linked to each applicable requirement.

| Requirement class | Test mapping rule |
|---|---|
| FR | At least one functional test; integration/E2E for major workflows |
| NFR | Measurement, inspection or compatibility test as appropriate |
| ERR | Negative/failure/recovery test |
| AC | End-to-end acceptance test |
| TBD | No release claim until resolved or explicitly deferred |

### Traceability record format

```text
Requirement ID
→ Test ID
→ Dataset/fixture
→ Build/version
→ Device
→ Result
→ Evidence
→ Defect ID (if failed)
```

# 13. Unit Testing

Test isolated logic including:

- file validation;
- MIME detection;
- filename sanitization;
- input-type mapping;
- processing state transitions;
- cancellation semantics;
- data validation;
- canonical schema validation;
- JSON serialization;
- CSV escaping;
- Unicode handling;
- date/number/currency normalization;
- identifier preservation;
- confidence handling;
- field normalization;
- table normalization;
- export selection;
- error mapping;
- repository logic;
- ViewModel/use-case/application logic where present;
- model readiness state mapping.

## 13.1 Unit-Test Properties

Unit tests should be deterministic, fast and independent of actual network access.

## 13.2 Mutation-Style Priority Cases

Where practical, deliberately change boundary conditions such as:

- null vs empty;
- 0 vs unavailable confidence;
- edited vs non-edited values;
- valid vs invalid enum;
- one table row vs zero rows;
- numeric identifier with leading zero;
- CSV value containing commas/newlines/quotes.

# 14. Component Testing

## 14.1 Input Component

Verify accepted/rejected file categories and safe handoff to processing.

## 14.2 Preprocessing Component

Verify each transformation independently and as a pipeline.

## 14.3 OCR Adapter

Verify provider output maps to the canonical OCR result without fabricated coordinates/confidence.

## 14.4 AI Adapter

Verify model output is parsed safely, malformed output is rejected or normalized safely, and no unsupported fields are silently invented.

## 14.5 Extraction/Validation Component

Verify schema checks, field/table normalization and warnings.

## 14.6 Editor Component

Verify draft/current value handling and dirty state.

## 14.7 Persistence Component

Verify transactions, relationships and reconstruction of the authoritative result.

## 14.8 Exporter Components

Verify each format independently and against canonical source data.

# 15. Document Input Tests

| Test ID | Scenario | Expected behavior | Status |
|---|---|---|---|
| IN-001 | Camera capture accepted | Capture enters validated acquisition flow | CONFIRMED requirement |
| IN-002 | Valid image upload | Image accepted | CONFIRMED requirement |
| IN-003 | Valid PDF upload | PDF accepted | CONFIRMED requirement |
| IN-004 | Multi-page PDF | Pages are preserved/processed according to validated capability | REQUIRES TECHNICAL VALIDATION |
| IN-005 | Unsupported file type | Clear rejection; no false processing | CONFIRMED requirement |
| IN-006 | Corrupt file | Clear failure; source remains safe | CONFIRMED requirement |
| IN-007 | Empty file | Clear failure | CONFIRMED requirement |
| IN-008 | Large file | Bounded handling; no crash/silent truncation | TBD threshold |
| IN-009 | Rotated image | Accepted if technically processable | CONFIRMED capability |
| IN-010 | Low-resolution image | Process or provide clear unreadability state | CONFIRMED requirement |
| IN-011 | Unsafe filename | Sanitized/non-path-authoritative handling | CONFIRMED security requirement |

# 16. Image Preprocessing Tests

Test:

- auto-crop;
- perspective correction;
- noise removal;
- brightness enhancement;
- contrast adjustment;
- auto-rotation.

### Validation rules

1. Preprocessing SHALL improve OCR where expected by measurable benchmark evidence.
2. Preprocessing SHALL NOT destroy readable text.
3. Original source SHALL remain preserved where the storage contract requires it.
4. A failed preprocessing step SHALL be recoverable or produce a truthful failure state.
5. Preprocessing output SHALL remain associated with the correct source page.
6. Excessive transformations SHALL be detectable through regression tests.

Exact preprocessing algorithms remain **TBD / REQUIRES TECHNICAL VALIDATION**.

# 17. OCR Testing

## 17.1 OCR Test Categories

- clear text;
- blurred text;
- small text;
- rotated text;
- skewed text;
- noisy text;
- low contrast;
- mixed fonts;
- numeric values;
- dates;
- identifiers;
- tables;
- multi-page ordering;
- mixed-language/Unicode where supported.

## 17.2 OCR Output Validation

Verify:

- character accuracy;
- word accuracy;
- text completeness;
- line ordering;
- page ordering;
- numeric accuracy;
- date accuracy;
- identifier accuracy;
- preservation of meaningful special characters;
- availability/unavailability semantics for coordinates and confidence.

The canonical data contract requires the application to reflect actual OCR provider capabilities and not synthesize coordinates or confidence simply to populate a schema slot.

# 18. OCR Metrics

### 18.1 Character Error Rate (CER)

```text
CER = (Substitutions + Insertions + Deletions) / Number of reference characters
```

### 18.2 Word Error Rate (WER)

```text
WER = (Substitutions + Insertions + Deletions) / Number of reference words
```

### 18.3 Additional Metrics

Where meaningful:

- field-level exact accuracy;
- numeric exact-match accuracy;
- date exact-match accuracy;
- identifier exact-match accuracy;
- table-cell accuracy.

### 18.4 Thresholds

No numeric release percentage is invented here. Exact target thresholds are:

**TBD / REQUIRES TECHNICAL VALIDATION**

Thresholds must be defined after the benchmark corpus and selected OCR/model stack are validated.

# 19. OCR Regression Strategy

Whenever any of the following changes:

- OCR engine;
- OCR engine version;
- OCR language model;
- preprocessing algorithm;
- image normalization;
- OCR adapter;
- device integration;

run the complete OCR benchmark dataset.

Compare:

```text
Previous build
      vs
Candidate build
```

Report:

- CER/WER deltas;
- field accuracy deltas;
- numeric/date/identifier deltas;
- known regressions;
- newly fixed cases.

A candidate with a statistically or practically important regression SHALL not be promoted without explicit review and acceptance.

# 20. AI Document Classification Testing

## Supported classification targets

Where included in the validated MVP corpus:

- Invoice
- Receipt
- Form
- Bank statement
- Certificate
- Mark sheet
- ID card
- Business card
- Table document
- General document
- Unknown

## Metrics

Measure:

- accuracy;
- precision/recall per class where dataset size supports it;
- confusion matrix;
- false classification rate;
- unknown detection quality;
- confidence calibration/availability where the runtime supplies it.

Exact quantitative acceptance thresholds remain **TBD**.

### Negative requirement

A missing or ambiguous document SHALL NOT be rewarded as a correct classification by silently forcing the nearest known class.

# 21. Key-Value Extraction Testing

Test:

- standard labels;
- labels with different capitalization;
- labels in different positions;
- missing labels;
- duplicated labels;
- multi-line values;
- numeric values;
- dates;
- currency;
- identifiers;
- empty values;
- ambiguous values;
- values near other labels;
- values spanning page boundaries where supported.

## Hallucination control

If source evidence is absent:

```text
No reliable evidence
       ↓
Do not invent value
       ↓
Missing / uncertain / warning
```

The QA suite SHALL contain negative cases specifically designed to detect fabricated values.

# 22. Table Extraction Testing

Test:

- simple tables;
- multi-column tables;
- multi-row tables;
- empty cells;
- long cells;
- numeric columns;
- currency columns;
- multi-page tables;
- broken table borders;
- irregular tables;
- repeated headers;
- uneven row lengths where supported.

Verify:

- columns remain aligned;
- rows remain aligned;
- each cell remains associated with the correct column;
- values are not dropped silently;
- repeated headers are handled according to the validated structuring rule;
- empty cells are not replaced with invented content.

Advanced merged-cell persistence is not a required MVP baseline; do not treat merge/split editing as mandatory unless separately approved.

# 23. Table Accuracy Metrics

Measure where dataset structure permits:

- table detection accuracy;
- column detection accuracy;
- row detection accuracy;
- cell exact-match accuracy;
- structural accuracy;
- cell association accuracy.

## 23.1 Recommended Structural Comparison

```text
Expected table structure
        vs
Actual table structure
```

The comparison should separately score:

1. table presence;
2. number/order of columns;
3. number/order of rows;
4. cell-to-column association;
5. cell value correctness.

Exact scoring methodology is **TBD / REQUIRES TECHNICAL VALIDATION**.

# 24. Confidence Testing

Verify confidence values:

- remain within the canonical supported representation where supplied;
- are not fabricated;
- survive serialization/deserialization;
- display correctly when available;
- do not imply certainty when unavailable;
- remain associated with the correct field/table/cell/page where supported.

Test cases:

- high confidence;
- medium confidence;
- low confidence;
- missing confidence;
- mixed-confidence result;
- confidence after user edit;
- confidence after export.

### Mandatory rule

When the provider does not produce confidence:

```text
confidence = unavailable / null
```

not zero and not an invented percentage.

Exact confidence aggregation formula remains **TBD / REQUIRES TECHNICAL VALIDATION**.

# 25. Structured Data Validation

Validate results against `SnapData_DATA_SCHEMA_v1.0.md`.

Test:

- valid result;
- missing required structural data;
- invalid data types;
- invalid enum values;
- malformed table;
- invalid relationships;
- null values;
- empty result;
- partial result;
- warnings array behavior;
- review state semantics;
- original/current value semantics;
- editedFlag semantics;
- source references where available.

## 25.1 Canonical Data Invariants

1. Current user-approved value is distinct from original extracted value where edited.
2. An absent value is not silently replaced by a synthetic value.
3. Tables maintain deterministic column and row order.
4. IDs/references remain internally coherent.
5. Serialized data round-trips without semantic loss.
6. Database representation maps to the canonical semantic model.

# 26. User Review Testing

Verify users can:

- inspect extracted fields;
- inspect tables;
- see document type;
- identify questionable values;
- inspect warnings/confidence where supported;
- review source context where supported;
- save reviewed data.

### UX acceptance baseline

The review screen must allow inspection before export, and save/export states must remain truthful when operations fail.

# 27. Field Editing Testing

Test:

- edit text;
- edit number;
- edit date;
- edit currency;
- edit identifier;
- clear value;
- replace value;
- save;
- cancel.

Verify:

```text
Machine value
      ↓
User edit
      ↓
Draft/current value
      ↓
Save
      ↓
Authoritative persisted value
```

After save, user-entered data SHALL be authoritative for subsequent reopen/export behavior.

# 28. Table Editing Testing

Test only operations actually supported by the implementation.

Required supported baseline scenarios:

- edit cell;
- edit multiple cells;
- clear cell;
- save changes.

Conditional scenarios:

- add row;
- delete row;
- insert/remove column;
- merge/split cells.

These SHALL be marked **OPTIONAL / TBD** until confirmed in the product scope. Unsupported operations are not valid failures.

# 29. Mandatory User-Correction Integrity Test

**Test ID:** CRIT-001

### Scenario

1. Process a synthetic document.
2. AI extracts:
   `Total = ₹10,000`
3. User changes:
   `Total = ₹10,500`
4. Save.
5. Close the document.
6. Open History.
7. Reopen the document.
8. Verify:
   `Total = ₹10,500`
9. Export to Excel.
10. Export to CSV.
11. Export to JSON.
12. Export to PDF.
13. Verify every export contains the authoritative value `₹10,500`.

### Pass condition

No export, reopen, history view or database reconstruction may silently revert to the original AI value.

### Severity

**Critical** — failure is a release blocker for P0 export/persistence functionality.

# 30. Database Testing

Test:

- insert;
- update;
- read;
- delete;
- transactions;
- foreign keys;
- uniqueness/duplicate prevention;
- persistence;
- reopen reconstruction;
- migration;
- missing-file/orphan references;
- corrupted database recovery behavior;
- export metadata persistence where applicable.

The current database design treats SQLite as local/offline persistence and separates relational data from Android-local file storage.

## 30.1 Database Transaction Tests

Verify grouped changes commit atomically where the logical operation requires it.

Example:

```text
Save result
 ↓
Document + result + fields + tables + rows + cells
```

If any required database write fails, the application must not report a successful save for an incomplete transaction.

# 31. Database Data Integrity Invariants

Verify:

1. Every document has valid identity.
2. Every processing job references a valid document.
3. Every extraction result references a valid document.
4. Every field references a valid result.
5. Every table references a valid result.
6. Every row references a valid table.
7. Every cell references valid row/column structures.
8. `row_index` ordering remains deterministic where defined.
9. User corrections survive persistence.
10. Deleted records do not remain unintentionally visible in history.
11. Foreign-key relationships remain valid after update/delete operations.
12. Database migrations preserve supported records.

# 32. Document History Testing

Test:

```text
Save document
   ↓
Close
   ↓
Open History
   ↓
Find document
   ↓
Open
   ↓
Review saved result
```

Scenarios:

- multiple documents;
- duplicate names;
- no-history empty state;
- sorting where implemented;
- search where implemented;
- delete;
- reopen;
- missing source file;
- corrupted record;
- previously edited document.

Search, rename, folders, favorites and tags are not assumed P0 unless confirmed by the current product baseline.

# 33. Export Testing

Formats:

- Excel `.xlsx`;
- CSV `.csv`;
- JSON `.json`;
- PDF `.pdf`.

Verify:

- file generated;
- correct extension;
- correct MIME type where the sharing layer uses one;
- valid format;
- current authoritative values;
- tables preserved;
- Unicode preserved;
- leading zeros preserved;
- special characters preserved;
- file opens in representative consumer software;
- export failure leaves saved result unchanged.

Exact libraries and Android APIs remain **TBD / REQUIRES TECHNICAL VALIDATION**.

# 34. Export Cross-Format Consistency

Given one canonical saved result:

```text
Canonical saved result
       ↓
 ┌─────┼─────┬─────┐
 ↓     ↓     ↓     ↓
XLSX  CSV  JSON   PDF
```

Compare important semantic values across all formats.

Minimum consistency checks:

- document title/name where included;
- document type where included;
- field values;
- numeric values;
- dates;
- identifiers;
- table row/column order;
- edited/current value;
- Unicode text.

The visual layout of PDF/Excel may differ; semantic content must remain consistent.

# 35. Android Sharing Testing

Flow:

```text
Export
  ↓
Share
  ↓
Android Share Sheet
  ↓
Compatible target application
```

Verify:

- correct MIME type;
- file is accessible through the approved URI/storage mechanism;
- target application can open/import it where supported;
- no corrupted URI/reference;
- sharing cancellation does not delete the generated file unexpectedly;
- sharing failure does not mutate saved structured data.

Exact Android sharing API/provider remains **REQUIRES TECHNICAL VALIDATION**.

# 36. Offline Testing — Mandatory Gate

Offline verification is a release-critical area.

## 36.1 Required Conditions

Run after required model setup with:

- airplane mode;
- Wi-Fi disabled;
- mobile data disabled;
- physically unavailable network;
- intermittent network;
- network toggled off during active processing.

## 36.2 Required Capabilities

Verify:

- document input;
- camera capture where permission/camera are available;
- preprocessing;
- OCR;
- offline AI;
- document classification;
- key-value extraction;
- table extraction;
- structured data generation;
- review;
- editing;
- SQLite save;
- history;
- export;
- Android sharing of locally generated files where OS behavior permits.

The core document-processing workflow MUST NOT require remote OCR/AI/cloud upload after required local model setup.

# 37. Offline Failure Test

**Test ID:** OFF-CRIT-001

### Scenario A — Network disabled at start

1. Model is ready.
2. Enable airplane mode.
3. Acquire a supported document.
4. Process.
5. Review.
6. Edit.
7. Save.
8. Reopen from History.
9. Export.

**Expected:** core workflow completes locally.

### Scenario B — Network disabled during processing

1. Start processing with network available.
2. Disable network during preprocessing/OCR/AI.
3. Observe behavior.

**Expected:**

- continue locally, OR
- fail gracefully with clear messaging and preserved safe state.

**Never acceptable:** silent corruption, false success, or an undocumented remote dependency.

# 38. Network Dependency Audit

Every component capable of network access SHALL be listed.

| Component | Purpose | Network required? | Expected offline behavior | Risk | Status |
|---|---|---:|---|---|---|
| AI model setup | Initial resource acquisition | Potentially yes | Setup blocked/available based on cached resources | Medium | CONFIRMED concept |
| Core OCR | Text extraction | No expected remote dependency | Works locally | Critical | REQUIRES VALIDATION |
| AI runtime | Local inference | No expected remote dependency | Works locally when model ready | Critical | REQUIRES VALIDATION |
| Database | Persistence | No | Works | Critical | CONFIRMED |
| Export | File generation | No | Works | High | CONFIRMED |
| Sharing | OS share handoff | No internet inherently required | Works with local file/target | Medium | REQUIRES VALIDATION |
| Analytics/telemetry | Not required by current baseline | No | Must not block processing | Medium | OPTIONAL / TBD |

Any unexpected network call during core processing SHALL be treated as a release-blocking defect until explicitly approved.

# 39. Performance Testing

Measure:

- application launch;
- document loading;
- image preprocessing;
- OCR;
- AI model load time;
- AI inference;
- structured data generation;
- schema validation;
- database save;
- history loading;
- export generation;
- sharing handoff;
- memory usage;
- CPU usage;
- storage usage;
- battery impact where practical.

### Threshold policy

Do not invent hard limits.

| Metric | Target |
|---|---|
| App launch | TBD |
| First document open | TBD |
| Preprocessing | TBD |
| OCR per page | TBD |
| AI model load | TBD |
| AI inference | TBD |
| Save | TBD |
| Export | TBD |
| Peak memory | TBD |
| Battery impact | TBD |

Exact thresholds require benchmark data from the validated device matrix and selected implementation.

# 40. Large Document Testing

Test:

- large image;
- high-resolution image;
- multi-page PDF;
- large tables;
- many extracted fields;
- long OCR text;
- repeated pages;
- large export content.

Verify:

- no crash;
- no ANR;
- no silent truncation;
- no invalid partial save;
- no corrupt export;
- user receives clear state if a limit is exceeded.

Exact maximum page/file size remains **TBD / REQUIRES TECHNICAL VALIDATION**.

# 41. Memory Testing

Monitor for:

- bitmap retention;
- repeated page buffers;
- OCR result duplication;
- AI model duplication;
- large structured-result allocations;
- export memory spikes;
- temporary-file accumulation.

### Repetition scenario

```text
Acquire
→ Process
→ Review
→ Save
→ Export
→ Close
→ Repeat
```

Track memory after each iteration. Memory growth that does not return toward a stable baseline is a defect candidate.

# 42. Stress Testing

Execute repeated workflow cycles:

```text
Upload / Capture
→ Process
→ Review
→ Edit
→ Save
→ Export
→ Delete
→ Repeat
```

Look for:

- memory growth;
- database growth;
- orphan files;
- stale temporary files;
- state corruption;
- UI degradation;
- crashes;
- ANRs;
- export naming collisions.

The exact iteration count is **TBD** and should be set from practical device/resource limits.

# 43. Security Testing

Test:

- local file access boundaries;
- unsafe filenames;
- path traversal;
- malformed files;
- crafted document input;
- database file access assumptions;
- temporary files;
- exported files;
- debug logs;
- error messages;
- crash artifacts where controllable.

Verify:

- user-controlled filenames cannot become arbitrary paths;
- untrusted input does not execute as code;
- sensitive content is not copied into logs unnecessarily;
- secrets are not stored in document/extraction data;
- export paths cannot silently overwrite unrelated files without an approved user action.

Encryption, secure delete and app lock mechanisms are **TBD** in the parent specifications; they must not be falsely marked as tested features before implementation.

# 44. File Security Tests

Test filenames such as:

```text
../document.pdf
../../secret
..\\..\\secret.pdf
document<script>.pdf
document\".pdf
CON.pdf
NUL.pdf
very-long-filename....pdf
```

Expected:

- safe normalization;
- no directory traversal;
- no arbitrary overwrite;
- no application crash;
- no unsafe temporary path.

Cross-platform edge cases should be included where the storage layer can receive such input.

# 45. Privacy Testing

Verify:

- documents remain local for core processing;
- extracted data remains local;
- AI/OCR processing remains local where required;
- no accidental document upload occurs;
- sensitive text is absent from ordinary logs;
- crash/diagnostic data avoids sensitive document payloads where technically controllable;
- exported files remain under the intended local/file-sharing boundary.

### Privacy smoke check

With a synthetic secret string embedded in a test document, inspect:

- app logs;
- debug output;
- database diagnostic snapshots;
- temporary files;
- network traffic in the offline audit environment.

The secret string should appear only in intended local document/result storage, not in network payloads or diagnostic output.

# 46. Error Handling Testing

Test:

- invalid input;
- OCR failure;
- empty OCR result;
- AI model unavailable;
- AI inference failure;
- malformed AI output;
- schema validation failure;
- database failure;
- export failure;
- storage full;
- permission failure;
- cancellation;
- unexpected exception.

Every failure should result in:

1. safe internal error state;
2. user-friendly message;
3. preservation of source or valid prior data where possible;
4. recovery action where possible;
5. no false completion state.

# 47. Processing State Tests

The processing documents define stage-oriented events including:

```text
PROCESSING_STARTED
STAGE_STARTED
STAGE_COMPLETED
STAGE_FAILED
CANCELLATION_REQUESTED
PROCESSING_CANCELLED
MODEL_READY
MODEL_LOAD_FAILED
PARTIAL_RESULT_CREATED
VALIDATION_WARNING
PERSISTENCE_STARTED
PERSISTENCE_COMPLETED
EXPORT_STARTED
EXPORT_COMPLETED
```

Tests SHALL ensure:

- impossible transitions are rejected;
- failed stages do not become successful without valid completion evidence;
- cancelled work is not reported as completed;
- the UI reflects meaningful stage status;
- persisted state remains coherent after interruption.

Exact final enum names remain implementation-dependent where the source marks vocabulary as subject to validation.

# 48. Recovery Testing

Test recovery from:

- app restart;
- process cancellation;
- crash during preprocessing;
- crash during OCR;
- crash during AI inference;
- crash during save;
- crash during export;
- low storage;
- permission loss;
- missing source file;
- corrupted database;
- incomplete temporary output.

### Recovery hierarchy

```text
Retry current stage
      ↓
Retry earlier recoverable stage
      ↓
Preserve partial result
      ↓
Request user action
      ↓
Fail safely
```

Avoid silent data loss.

# 49. Save Failure / Crash Consistency

Critical test:

1. Start save of a valid edited result.
2. Inject/fake failure during persistence.
3. Reopen application.
4. Inspect History and document data.

Expected:

- no false success;
- no half-written semantic result presented as fully saved;
- prior valid saved version remains intact where applicable;
- user edits remain available in an approved recoverable path or the user receives a truthful failure state.

Exact crash-recovery/resume semantics are **TBD** in the parent SRS.

# 50. UI Testing

Major screens/areas:

- Home;
- Document input;
- Camera scanner;
- Preview;
- Processing;
- AI results;
- Field editor;
- Table editor;
- Validation/review state;
- Save state;
- History;
- Export;
- Settings;
- About.

Verify:

- navigation;
- buttons;
- loading;
- empty states;
- success states;
- error states;
- offline state;
- model readiness state;
- unsaved edit warnings;
- accessibility semantics.

# 51. UI State Testing

Test each major flow through:

```text
Loading
Empty
Success
Partial
Error
Offline
Saving
Saved
Exporting
Exported
Cancelled
```

The UI must not reach impossible states such as:

- `Exported` with no generated file;
- `Saved` after a failed transaction;
- `Processing complete` when a required stage failed;
- `Offline unavailable` when the local model is ready and the core workflow is expected to operate locally;
- stale editor content after a successful save/reload.

# 52. Navigation / Frontend Test Cases

At minimum verify:

```text
Home → Camera → Preview → Processing → Results → Editor → Save → Export
Home → Import → Preview → Processing → Results
Home → History → Document → Results/Editor
Home → Settings → Model / OCR Language / Storage / About
```

Back behavior SHALL be tested in every state that can hold user work.

Representative baseline cases:

| Test ID | Scenario | Expected |
|---|---|---|
| FE-001 | Fresh launch | Correct onboarding/setup path |
| FE-002 | Returning user | Home available |
| FE-003 | Missing model | Safe setup state; processing blocked |
| FE-004 | Model ready | Core processing path available |
| FE-005 | Camera permission granted | Scanner opens |
| FE-006 | Camera permission denied | Clear recovery |
| FE-007 | Valid import | Preview/processing path |
| FE-008 | Unsupported file | Clear error + reselect |
| FE-009 | Processing stage changes | Correct stage shown |
| FE-010 | Processing cancel | Cancelled state; no false completion |
| FE-011 | Fields present | Render correctly |
| FE-012 | Tables present | Render correctly |
| FE-013 | Edit field | Dirty state shown |
| FE-014 | Invalid edit | Validation shown |
| FE-015 | Save success | Saved state shown |
| FE-016 | Save failure | Edits preserved/retry offered |
| FE-017 | Back with unsaved edits | Confirmation shown |
| FE-018 | Export success | Success state only after validation |
| FE-019 | Export failure | Failure state; saved data preserved |
| FE-020 | History item reopened | Correct data restored |

# 53. Rotation / Lifecycle Testing

Test:

- Activity recreation;
- background/foreground transitions;
- screen rotation if supported;
- app minimization;
- app termination;
- process restart;
- returning after long background period.

Verify:

- no duplicate processing jobs;
- no lost unsaved edits where support is promised;
- no false completion;
- no corrupted result;
- no duplicated export;
- no invalid navigation stack.

Exact resume semantics are **TBD**.

# 54. Accessibility Testing

Test:

- screen-reader labels;
- content descriptions;
- semantic reading order;
- keyboard navigation where applicable;
- contrast;
- touch target sizes;
- readable text;
- text scaling;
- error announcements;
- status announcements;
- table row/column semantics.

The UI/UX baseline requires core actions and states not to depend on color alone. Accessibility conformance beyond the baseline remains **TBD / REQUIRES TECHNICAL VALIDATION**.

# 55. Localization / Unicode Testing

Test at minimum for supported/validated language scope:

- English;
- Hindi;
- Marathi;
- other supported languages once finalized.

Verify Unicode integrity through:

```text
OCR
→ AI extraction
→ Structured data
→ SQLite
→ History
→ JSON
→ CSV
→ Excel
→ PDF
```

Test characters including:

- Devanagari;
- accented Latin characters;
- currency symbols;
- mathematical symbols;
- combining marks;
- emoji only where permitted by the schema/export format.

Exact MVP OCR language list is **TBD**.

# 56. Date / Number / Currency Testing

Test ambiguous and formatting-sensitive values.

## 56.1 Dates

Examples:

```text
03/04/2026
04/03/2026
2026-08-30
30-08-2026
```

The system must preserve source meaning and SHALL NOT silently change an ambiguous date into a different factual value.

## 56.2 Numbers

Test:

```text
1000
1,000
1.000
1 000
00123
1,234.56
1.234,56
```

Locale interpretation rules are **TBD / REQUIRES TECHNICAL VALIDATION**.

## 56.3 Currency

Test:

```text
₹10,500
INR 10500
$1,050.00
€1.050,00
```

Verify symbol/code/value semantics remain understandable and do not lose meaningful separators or leading zeros.

# 57. Cancellation Testing

Test cancellation at:

- acquisition;
- preprocessing;
- OCR;
- AI model loading;
- AI inference;
- validation;
- save;
- export.

Expected:

- cancellation request produces an explicit cancellation state where supported;
- no false completed result;
- already-saved valid data remains unchanged;
- temporary output is cleaned safely;
- user receives a clear state.

Long-running stage cancellation granularity is **REQUIRES TECHNICAL VALIDATION**.

# 58. Partial Result Testing

Scenarios:

### OCR success / AI failure

Preserve OCR context and show retry/recovery.

### AI success / table failure

Preserve valid fields and warn that the table portion is incomplete where the product contract permits partial results.

### Some pages succeed / one page fails

Preserve successful page evidence according to the validated multi-page policy.

### Some fields unresolved

Keep detected values and represent unresolved values as missing/uncertain, never fabricated.

# 59. Model Management Testing

Required concepts:

- model readiness;
- model unavailable;
- model load failure;
- model setup flow.

Test:

- first-run model-not-ready state;
- successful initial setup;
- model present and usable offline;
- model load failure;
- insufficient storage during setup where practical;
- model resource missing/corrupt;
- processing attempted when model unavailable;
- restart after model setup.

Model update/delete/resume-download behavior remains **TBD**.

# 60. Backend / API Testing Boundary

The current MVP has **no required REST API or backend dependency** for the core workflow.

Therefore:

- API contract tests are **OPTIONAL / BLOCKED** unless an API is introduced.
- No test should invent endpoint names, authentication rules, HTTP schemas or cloud workflows.
- Any future backend requires a separate approved technical change and a new test layer.

If an API is later introduced, minimum API test categories should include:

- authentication/authorization if applicable;
- schema validation;
- error codes;
- timeout/retry;
- idempotency;
- offline behavior when server is unavailable;
- privacy/data transmission policy.

# 61. Repository / Service Boundary Testing

The frontend should not directly own OCR, AI runtime, or SQLite implementation.

Verify through interface-level tests that:

- acquisition returns a normalized document reference;
- processing returns explicit state/result objects;
- persistence saves/retrieves canonical data;
- export consumes authoritative saved data;
- UI receives state without depending on provider-specific objects.

This boundary enables replacing OCR/AI/export implementations without rewriting the entire UI.

# 62. Data Round-Trip Tests

Test semantic round trips:

```text
Canonical Result
 → JSON serialize
 → parse
 → Canonical Result
```

```text
Canonical Result
 → SQLite persistence
 → reload
 → Canonical Result
```

```text
Canonical Result
 → Export
 → Consumer parser/reader
 → Important values
```

The round trip SHALL preserve authoritative values, table structure and Unicode to the extent required by the target format.

# 63. Export Consumer Compatibility

Where practical:

- open XLSX in representative spreadsheet software;
- import CSV into representative spreadsheet/data tools;
- parse JSON against the canonical schema;
- open PDF in representative PDF readers.

Exact consumer/application matrix: **TBD / REQUIRES TECHNICAL VALIDATION**.

# 64. Regression Testing Strategy

## Trigger conditions

Run regression after:

- OCR/model changes;
- preprocessing changes;
- extraction/parser changes;
- schema changes;
- database migrations;
- editor changes;
- export changes;
- navigation/state changes;
- Android upgrade;
- library/runtime upgrade;
- performance optimization;
- security fixes.

## Regression suites

### Smoke
Launch, acquire one document, process, review, save, history, export.

### Core
Full P0 feature set.

### AI/OCR benchmark
Complete ground-truth corpus.

### Offline
Complete no-network suite.

### Persistence
Save/reopen/edit/export suite.

### Export
All format + consistency tests.

### Security/privacy
Input-hardening + log/network inspection.

### Compatibility
Approved Android device matrix.

# 65. Defect Management

Each defect SHALL contain:

- unique defect ID;
- summary;
- severity;
- priority;
- environment;
- build version;
- device;
- dataset/document ID;
- reproduction steps;
- expected result;
- actual result;
- evidence;
- related requirement/test ID;
- root cause when known;
- fix version;
- retest result.

## 65.1 Severity guidance

| Severity | Meaning |
|---|---|
| S0 Critical | Data corruption, privacy breach, offline core path failure, false authoritative export/save, crash blocking P0 workflow |
| S1 High | Major P0 feature incorrect/unusable, repeated crash, severe extraction/structure failure |
| S2 Medium | Important defect with workaround or limited scope |
| S3 Low | Minor UI/content defect |

# 66. Test Execution and Evidence

Recommended evidence types:

- automated test logs;
- screenshots;
- screen recordings for E2E/UX where useful;
- benchmark CSV/JSON reports;
- device metadata;
- database integrity output;
- exported artifact samples;
- checksum/hash for generated artifacts where helpful;
- network capture/audit evidence;
- memory/performance measurements.

Evidence SHALL be linked to a specific build and dataset version.

# 67. Test Reporting

A QA test report SHOULD include:

```text
Build
Date
Device
OS
Dataset version
Tests executed
Passed
Failed
Blocked
Skipped
Critical defects
Performance results
Offline result
Security/privacy result
Regression result
Release recommendation
```

Automated and manual results should be clearly distinguished.

# 68. Quality Gates

## Gate 0 — Static Quality

- no unresolved critical static-analysis errors;
- type/compile/lint checks pass according to project tooling;
- no known security-critical warnings accepted without review.

## Gate 1 — Unit/Component

- required unit/component suites pass;
- deterministic core logic covered.

## Gate 2 — Integration

- processing boundaries pass;
- persistence mapping passes;
- export handoff passes.

## Gate 3 — AI/OCR

- benchmark executed on approved dataset version;
- no unexplained critical regression;
- thresholds met once approved.

## Gate 4 — Offline

- core offline workflow passes;
- no unexpected network dependency.

## Gate 5 — E2E

- major P0 workflows pass;
- critical acceptance tests pass.

## Gate 6 — Security/Privacy

- file/path tests pass;
- no unintended document upload;
- sensitive data not exposed by normal diagnostics.

## Gate 7 — Release Candidate

- compatibility suite passes;
- performance measurements recorded;
- no unresolved release-blocking defects;
- acceptance sign-off complete.

# 69. Release Blocking Criteria

A release SHALL be blocked by any of the following:

1. Core offline workflow depends unexpectedly on network.
2. User edits are overwritten after save/reopen/export.
3. Saved data is corrupted or silently lost.
4. A generated export contains stale authoritative data.
5. Critical table association corruption is detected.
6. Critical privacy/security defect is confirmed.
7. Application crashes consistently on a supported/reference device during the P0 path.
8. Acceptance criteria AC-001..AC-012 are not satisfied without an approved exception.
9. Critical regression is introduced in OCR/AI processing without approved disposition.

# 70. Acceptance Test Suite

The SRS defines twelve high-level acceptance criteria. The following mapping operationalizes them.

| Acceptance ID | Validation |
|---|---|
| AC-001 | Acquire supported image/PDF via camera or file import and reach processing |
| AC-002 | Run preprocessing and verify available baseline operations + safe failure |
| AC-003 | Run OCR and verify usable output or truthful empty/failure state |
| AC-004 | With AI capability ready, verify supported type/fields/tables and available confidence |
| AC-005 | Review extracted fields/tables before export |
| AC-006 | Edit field/table, save, close, reopen, verify persistence |
| AC-007 | Export saved result to XLSX/CSV/JSON/PDF and verify current values |
| AC-008 | Share exported file or receive truthful no-target/failure behavior |
| AC-009 | Open History, reopen saved record, delete selected record and verify local state |
| AC-010 | With model ready and no network, complete core processing locally |
| AC-011 | Without model, attempt offline processing and receive setup/readiness guidance |
| AC-012 | Inject P0 processing/export/storage failure and verify truthful failure state |

# 71. End-to-End Scenarios

## E2E-001 — Happy Path: Image

```text
Launch
→ Home
→ Upload Image
→ Preview
→ Preprocess
→ OCR
→ Offline AI
→ Structured Result
→ Review
→ Edit
→ Save
→ History
→ Reopen
→ Export XLSX
→ Share
```

## E2E-002 — Happy Path: PDF

Same flow using a supported PDF.

## E2E-003 — Camera

```text
Home
→ Camera
→ Capture
→ Preview
→ Processing
→ Review
→ Save
→ Export
```

## E2E-004 — Low-Quality Scan

Verify preprocessing/OCR degradation is handled without invented data.

## E2E-005 — Table Document

Verify table structure and export consistency.

## E2E-006 — User Correction

Use CRIT-001 exactly.

## E2E-007 — Offline

Use OFF-CRIT-001 with airplane mode.

## E2E-008 — Failure Recovery

Inject an OCR/AI/save/export failure and confirm safe recovery.

# 72. Golden Demo / Viva Acceptance Flow

A production-quality mini-project demonstration SHOULD be backed by one fully deterministic synthetic document.

Recommended sequence:

1. Launch SnapData.
2. Confirm model readiness.
3. Disable network.
4. Scan/upload a synthetic invoice/form/table document.
5. Show preprocessing.
6. Show OCR stage.
7. Show offline AI analysis.
8. Show document type + fields + table.
9. Deliberately correct one wrong value.
10. Save.
11. Reopen from History.
12. Verify correction remains.
13. Export to Excel/CSV/JSON/PDF.
14. Open at least one generated file.
15. Show that no network was required.

The demo dataset SHALL be synthetic and versioned.

# 73. Test Automation Strategy

Where the generated Android project permits automation, automate:

- unit tests;
- schema validation;
- serialization round trips;
- repository/database tests;
- deterministic preprocessing tests;
- parser/extraction tests;
- exporter tests;
- UI smoke/navigation tests;
- offline mode smoke tests;
- regression benchmark execution.

Manual validation remains necessary for:

- camera capture quality;
- visual layout/accessibility review;
- representative PDF/table rendering;
- Android Share Sheet behavior;
- device-specific performance;
- exploratory testing;
- physical lifecycle/interruption scenarios.

Exact test frameworks are **REQUIRES TECHNICAL VALIDATION** from the actual Google AI Studio-generated Android project.

# 74. AI/OCR Benchmark Execution Protocol

For every benchmark run record:

- build ID;
- OCR engine/version if available;
- AI model/version if available;
- preprocessing configuration;
- device;
- Android version;
- dataset version;
- total documents;
- successful documents;
- failed documents;
- OCR metrics;
- classification metrics;
- field metrics;
- table metrics;
- regressions;
- known exceptions.

Do not compare results across dataset revisions without recording the change.

# 75. Reproducibility

A reported QA result SHOULD be reproducible from:

```text
Build + Device + Dataset Version + Model/OCR Version + Test Configuration
```

For nondeterministic AI behavior, the test report MUST identify the model/runtime and note variability rather than presenting a single run as a universal truth.

Exact deterministic controls are **TBD**.

# 76. Test Environment Fault Injection

Where safe and practical, simulate:

- network loss;
- camera permission denial;
- missing model;
- model-load error;
- malformed OCR output;
- malformed AI output;
- schema validation error;
- database write failure;
- storage-full condition;
- export failure;
- sharing target unavailable;
- application restart.

The purpose is to validate the recovery contracts, not to manufacture unrealistic pass rates.

# 77. Storage and File Lifecycle Testing

Test:

- source file creation;
- temporary preprocessing files;
- OCR/intermediate artifacts where they exist;
- database-linked file references;
- export files;
- deletion;
- cleanup after cancellation/failure;
- orphan detection.

Verify that deleting a document according to product behavior does not leave unintended persistent copies in ordinary application-managed storage.

Exact secure-delete semantics are **TBD**.

# 78. Privacy-Safe Logging Rules

Normal logs SHOULD contain:

- stage name;
- state transition;
- error class/code;
- timing/resource metrics;
- document/job IDs that are non-sensitive identifiers.

Normal logs SHOULD NOT contain:

- raw document text;
- complete OCR output;
- full extracted fields;
- document images;
- authentication secrets;
- private file contents.

Where diagnostic payload logging is temporarily required for development, it must be clearly isolated from release builds and synthetic test data should be preferred.

# 79. Exploratory Testing

In addition to scripted cases, QA SHALL perform exploratory sessions around:

- unusual document layouts;
- very long values;
- rapid repeated edits;
- fast navigation during processing;
- repeated cancellation/retry;
- switching offline/online repeatedly;
- backgrounding during long processing;
- malformed files;
- mixed-language text;
- export filename edge cases.

Exploratory defects must still receive traceable IDs and severity.

# 80. Known Open Testing Decisions

| ID | Decision | Status |
|---|---|---|
| TEST-001 | Exact Android version/device matrix | TBD |
| TEST-002 | Exact AI/OCR benchmark corpus | TBD |
| TEST-003 | OCR CER/WER release thresholds | REQUIRES TECHNICAL VALIDATION |
| TEST-004 | Classification accuracy thresholds | TBD |
| TEST-005 | Field extraction acceptance threshold | TBD |
| TEST-006 | Table structural scoring formula | REQUIRES TECHNICAL VALIDATION |
| TEST-007 | Confidence aggregation semantics | TBD |
| TEST-008 | Max document size/page count | REQUIRES TECHNICAL VALIDATION |
| TEST-009 | Exact performance thresholds | TBD |
| TEST-010 | Exact test automation framework | REQUIRES TECHNICAL VALIDATION |
| TEST-011 | Exact export consumer matrix | TBD |
| TEST-012 | Exact interruption/resume behavior | TBD |
| TEST-013 | Final security/encryption coverage | TBD |
| TEST-014 | Exact supported OCR languages | TBD |

# 81. Implementation Status Matrix

| Area | Status |
|---|---|
| Android/mobile target | **CONFIRMED** |
| Offline-first core processing | **CONFIRMED** |
| SQLite local persistence | **CONFIRMED source-backed** |
| Camera input | **CONFIRMED** |
| PDF input | **CONFIRMED** |
| Image input | **CONFIRMED** |
| OCR stage | **CONFIRMED** |
| Tesseract as source-backed OCR context | **CONFIRMED source-backed** |
| Exact OCR integration | **REQUIRES TECHNICAL VALIDATION** |
| Offline AI capability | **CONFIRMED** |
| Exact AI model/runtime | **TBD / REQUIRES TECHNICAL VALIDATION** |
| Document classification | **CONFIRMED capability** |
| Key-value extraction | **CONFIRMED capability** |
| Table extraction | **CONFIRMED capability** |
| Confidence handling | **CONFIRMED capability; exact aggregation TBD** |
| User review/edit | **CONFIRMED** |
| User edits authoritative after save | **CONFIRMED** |
| History/reopen | **CONFIRMED** |
| Excel/CSV/JSON/PDF export | **CONFIRMED** |
| Android sharing | **CONFIRMED product intent; exact API TBD** |
| REST API for core MVP | **REJECTED / NOT REQUIRED** |
| Backend for core MVP | **REJECTED / NOT REQUIRED** |
| Advanced app lock | **TBD** |
| Encryption | **TBD** |
| Secure delete | **TBD** |
| Multi-page exact limits | **TBD** |
| Performance thresholds | **TBD** |

# 82. Test Case ID Convention

Recommended naming:

```text
L0-###   Static
UT-###   Unit
CT-###   Component
IT-###   Integration
API-###  API/service (if applicable)
DB-###   Database
AI-###   AI/OCR
UI-###   UI
E2E-###  End-to-end
PERF-### Performance
SEC-###  Security
PRI-###  Privacy
OFF-###  Offline
AC-###   Acceptance
REG-###  Regression
REC-###  Recovery
```

Critical tests SHOULD use explicit `CRIT-###` IDs.

# 83. Test Case Template

```text
Test ID:
Requirement ID:
Title:
Priority:
Environment:
Device:
Build:
Dataset:
Preconditions:
Steps:
Expected Result:
Actual Result:
Status: PASS / FAIL / BLOCKED / SKIPPED
Evidence:
Defect ID:
Notes:
```

# 84. Entry Criteria

Testing for a feature begins when:

- implementation is available in a testable build;
- relevant requirement/source baseline is identified;
- dependencies are present;
- required test data exists;
- known blockers are documented;
- expected behavior is not ambiguous or is explicitly marked TBD.

For AI/OCR evaluation, the model/runtime and dataset must be identified before meaningful quantitative comparison.

# 85. Exit Criteria

A release candidate exits QA when:

- required test suites pass;
- critical E2E and acceptance tests pass;
- mandatory offline tests pass;
- no open release-blocking defects remain;
- AI/OCR benchmark is accepted against approved thresholds;
- performance measurements are recorded;
- database integrity and migration tests pass;
- export validation passes for all required formats;
- privacy/security checks pass;
- compatibility validation completes for the approved matrix;
- all exceptions have documented approval.

# 86. Regression Selection Matrix

| Change | Minimum regression |
|---|---|
| UI-only change | UI + navigation + smoke |
| State management | UI + integration + lifecycle |
| OCR engine/model | Full OCR/AI benchmark + E2E + offline |
| Preprocessing | OCR benchmark + input + E2E |
| AI parser/extractor | AI benchmark + schema + E2E |
| DB schema/migration | DB + persistence + history + export |
| Editor | Review/edit + persistence + export |
| Exporter | Export + cross-format + share |
| Storage/file handling | Input + security + persistence + export |
| Android SDK/runtime upgrade | Full compatibility + smoke + offline + performance |

# 87. Quality Dashboard KPIs

The QA dashboard SHOULD track:

- test pass rate;
- critical defect count;
- defect reopen rate;
- regression failure count;
- OCR CER/WER;
- classification accuracy;
- field accuracy;
- table-cell accuracy;
- offline pass rate;
- P0 E2E pass rate;
- export format pass rate;
- crash/ANR count in test runs;
- peak memory;
- processing duration.

These are measurement dimensions, not invented targets.

# 88. Acceptance Decision Record

The final release decision should record:

```text
Build:
Date:
QA owner:
Dataset version:
Device matrix:
Critical tests:
Offline gate:
AI/OCR gate:
Export gate:
Security/privacy gate:
Open defects:
Approved exceptions:
Final decision: PASS / CONDITIONAL / FAIL
```

# 89. Source Alignment

The testing strategy follows the current project baseline that:

- SnapData is an Android/mobile application;
- the core workflow is document acquisition → preprocessing → OCR → offline AI → structured extraction → review/edit → local save → export/history;
- SQLite is the source-backed local database;
- Excel, CSV, JSON and PDF are required export formats;
- user-edited data becomes authoritative for save/export;
- no REST API or backend is required for the core MVP;
- exact Android implementation choices, AI model/runtime, OCR integration, device matrix, quantitative accuracy thresholds and some advanced security capabilities remain open until technically validated.

The supplied workflow diagram's page 2 visually confirms the major workflow stages and the historical technology context. The parent technical documents explicitly caution against promoting unverified implementation details to confirmed status.

# 90. Traceability to Parent Documents

| Parent document | QA use |
|---|---|
| PRD | Product scope, priorities, acceptance expectations |
| SRS | Testable software requirements, errors, NFRs, AC-001..AC-012 |
| TRD | Technical boundaries and validation-open decisions |
| SYSTEM_ARCHITECTURE | Component boundaries, offline architecture, recovery |
| UI_UX | Screen states, accessibility, review/export UX |
| FRONTEND | Navigation/state/component behavior |
| DATABASE | SQLite entities, integrity, persistence, migrations |
| AI_OCR | OCR/AI contracts, benchmark concerns, model/runtime TBDs |
| DOCUMENT_PROCESSING | Stage behavior, partial results, recovery, confidence |
| DATA_SCHEMA | Canonical structured result and edit authority |
| EXPORT | Export mapping, validity, sharing, file semantics |
| Original specification | Product intent and overall workflow |
| Workflow diagram | Visual processing sequence and source-era stack context |

# 91. Final QA Baseline

SnapData QA is built around one non-negotiable principle:

> **The application must be proven as a complete offline-first document-to-structured-data system, not merely as a collection of individually working features.**

The validation chain is:

```text
Acquire
  ↓
Validate Input
  ↓
Preprocess
  ↓
OCR
  ↓
Offline AI
  ↓
Classify / Extract
  ↓
Validate Structured Data
  ↓
Review
  ↓
User Edit
  ↓
Save Authoritative Result
  ↓
Reopen / History
  ↓
Export XLSX / CSV / JSON / PDF
  ↓
Share / Open
```

Every critical boundary must be tested for:

- correctness;
- traceability;
- offline behavior;
- persistence;
- recovery;
- security/privacy;
- performance;
- regression safety.

No quantitative claim should be made until it is supported by a versioned dataset, measured benchmark and identified test environment.

# 92. Final Release Checklist

### Functional

- [ ] Camera input passes.
- [ ] PDF import passes.
- [ ] Image import passes.
- [ ] Preprocessing passes.
- [ ] OCR passes approved benchmark.
- [ ] Offline AI passes approved benchmark.
- [ ] Classification passes.
- [ ] Field extraction passes.
- [ ] Table extraction passes.
- [ ] Structured schema validation passes.
- [ ] Review passes.
- [ ] Field editing passes.
- [ ] Table editing passes for supported operations.
- [ ] Save/reopen passes.
- [ ] History passes.
- [ ] Excel export passes.
- [ ] CSV export passes.
- [ ] JSON export passes.
- [ ] PDF export passes.
- [ ] Android sharing passes.

### Offline

- [ ] Airplane-mode processing passes.
- [ ] Wi-Fi-off processing passes.
- [ ] Mobile-data-off processing passes.
- [ ] Mid-process network loss behavior passes.
- [ ] No unexpected network dependency found.

### Reliability

- [ ] Cancellation passes.
- [ ] App restart recovery passes.
- [ ] Save failure behavior passes.
- [ ] Export failure behavior passes.
- [ ] Storage-full behavior passes.
- [ ] Repeated processing stress passes.
- [ ] No silent truncation/corruption found.

### Security/Privacy

- [ ] Unsafe filename tests pass.
- [ ] Path traversal tests pass.
- [ ] Malformed-input tests pass.
- [ ] Sensitive-data logging audit passes.
- [ ] Network dependency audit passes.

### Quality

- [ ] Static analysis clean.
- [ ] Unit suite passes.
- [ ] Component suite passes.
- [ ] Integration suite passes.
- [ ] UI suite passes.
- [ ] E2E suite passes.
- [ ] Regression suite passes.
- [ ] Performance results recorded.
- [ ] Device matrix completed.
- [ ] Acceptance sign-off completed.

# 93. Document Status Summary

| Area | Status |
|---|---|
| QA strategy | **CONFIRMED baseline** |
| Testing pyramid | **CONFIRMED strategy** |
| Test levels | **CONFIRMED strategy** |
| Offline test gate | **CONFIRMED requirement** |
| Ground-truth dataset approach | **PROPOSED / REQUIRED TO VALIDATE AI/OCR** |
| Quantitative accuracy thresholds | **TBD** |
| Exact device minimums | **TBD** |
| Exact performance thresholds | **TBD** |
| Exact test automation stack | **REQUIRES TECHNICAL VALIDATION** |
| API testing | **OPTIONAL / BLOCKED unless API exists** |
| Security encryption validation | **TBD** |
| Advanced secure-delete validation | **TBD** |

---

# Appendix A — Critical Test Inventory

| ID | Test |
|---|---|
| CRIT-001 | User correction survives save → reopen → XLSX/CSV/JSON/PDF |
| CRIT-002 | Core document workflow completes with network disabled after model setup |
| CRIT-003 | Network loss during processing does not cause silent corruption |
| CRIT-004 | Failed save does not report success |
| CRIT-005 | Failed export does not mutate saved data |
| CRIT-006 | AI does not fabricate missing source values in controlled negative cases |
| CRIT-007 | Table row/column/cell associations remain correct |
| CRIT-008 | Malicious filenames cannot escape approved storage paths |
| CRIT-009 | Corrupt document cannot produce a false completed record |
| CRIT-010 | Application restart does not lose valid saved data |

---

# Appendix B — Minimal Automated Smoke Suite

```text
1. App launch
2. Verify model-ready state
3. Import synthetic image
4. Process offline
5. Verify OCR non-empty
6. Verify structured result schema
7. Edit a field
8. Save
9. Reopen from History
10. Verify edited value
11. Export JSON
12. Parse JSON and verify edited value
13. Export CSV/XLSX/PDF
14. Verify output artifacts exist and are valid
```

---

# Appendix C — Implementation Readiness Checklist

Before test automation is finalized, inspect the real Google AI Studio-generated Android project and record:

- [ ] actual programming language;
- [ ] UI toolkit/framework;
- [ ] build system;
- [ ] Android SDK/API versions;
- [ ] camera implementation;
- [ ] file-picker implementation;
- [ ] SQLite integration;
- [ ] OCR integration;
- [ ] AI model/runtime;
- [ ] model packaging;
- [ ] export libraries;
- [ ] Android sharing API;
- [ ] test framework(s);
- [ ] CI/build pipeline;
- [ ] actual logging/diagnostics configuration.

These decisions move from **TBD / REQUIRES TECHNICAL VALIDATION** to **CONFIRMED** only when implementation evidence or approved project decisions exist.

---

# Appendix D — Reference Acceptance Baseline

The source SRS records these high-level acceptance conditions:

- AC-001: supported document acquisition;
- AC-002: preprocessing and visible failure handling;
- AC-003: usable OCR or clear OCR failure/empty state;
- AC-004: AI type/field/table extraction with available confidence;
- AC-005: review before export;
- AC-006: persisted user edits;
- AC-007: export reflects current saved values;
- AC-008: Android sharing behavior;
- AC-009: history/reopen/delete;
- AC-010: core offline processing after setup;
- AC-011: missing-model readiness behavior;
- AC-012: truthful handling of P0 processing/export/storage failures.

These acceptance conditions are the primary E2E release backbone for this QA document.

---

# End of `SnapData_TESTING_v1.0.md`

**Document Status:** Draft / QA Baseline  
**Version:** 1.0  
**Date:** 30 August 2026
