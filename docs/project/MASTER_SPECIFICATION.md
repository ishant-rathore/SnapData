# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Master Specification

**Filename:** `SnapData_MASTER_SPECIFICATION_v1.0.md`  
**Version:** 1.0  
**Status:** Draft / Master Baseline  
**Date:** 30 August 2026  
**Implementation Target:** Android application using the Google AI Studio **“Build an Android app”** workflow  
**Execution Model:** Offline-first / local processing after required AI model setup  

> **Authority and evidence rule:** This document is an integration-level master view of the approved SnapData documentation set. It does not silently resolve conflicts, invent implementation details, claim test execution, or promote roadmap ideas into MVP requirements. Where the source set leaves a decision open, this document uses **TBD**, **NOT SPECIFIED**, **PROPOSED**, or **REQUIRES TECHNICAL VALIDATION**.

---

# 1. Executive Summary

SnapData is an Android/mobile application designed to convert supported PDF documents and images into structured, editable digital data using document acquisition, image/document preprocessing, OCR, offline AI analysis, structured-data generation, human review/editing, local persistence, export, and document history.

The authoritative product concept is:

```text
Document
   ↓
OCR
   ↓
AI Understanding
   ↓
Structured Data
   ↓
Review / Edit
   ↓
Local Save
   ↓
Export / Share
   ↓
History
```

The supplied workflow diagram visually expands this into **Document Input → Document Acquisition → Image Pre-processing → OCR Processing → Offline AI Processing → Structured Data Generation → User Review & Editing → Local Storage → Export Module → Document History**. The original project specification likewise describes conversion of PDFs/images into structured editable data, Excel/CSV/JSON/PDF export, local SQLite persistence, and offline operation after initial AI model setup.

The current engineering baseline intentionally distinguishes **product capability** from **implementation proof**. Android is confirmed as the target and Google AI Studio “Build an Android app” is the stated implementation workflow. The exact generated Android language, UI toolkit, architecture pattern, OCR integration, AI model/runtime, database API, export libraries, device limits, and other concrete implementation details remain validation-dependent until the actual generated project is inspected and benchmarked.

The MVP is the smallest complete path that demonstrates the core transformation from input document to validated, user-reviewable, locally stored and exportable structured data. A backend, REST API, and cloud database are **not required for the MVP** and are explicitly outside the current core architecture.

---

# 2. Problem Statement

Documents frequently contain useful information in formats that are difficult to reuse directly: scanned PDFs, camera captures, image files, forms, statements, certificates, tables, invoices and receipts. Manual extraction requires repeated reading, copying, typing, correction and spreadsheet preparation.

SnapData addresses this problem by automating the conversion of visual/document content into structured data while retaining a human review step and keeping the core processing local/offline after the required model setup.

The product is intended to reduce manual data-entry effort, improve document digitization workflow, provide editable machine-generated results, and provide portable exports without making cloud upload a prerequisite for the core processing path.

---

# 3. Product Goals

## 3.1 Primary goals

1. Provide a complete document-to-structured-data workflow on Android.
2. Accept camera, image and PDF document input.
3. Prepare document content for OCR through the approved preprocessing boundary.
4. Extract text through OCR.
5. Analyze documents locally using the approved offline-AI capability after model setup.
6. Detect document type, fields/key-value pairs and tables within validated scope.
7. Preserve uncertainty rather than fabricating missing information.
8. Allow users to review and correct extracted information.
9. Treat the latest saved user-reviewed structured result as authoritative.
10. Persist processed records locally and make them available through document history.
11. Export the authoritative saved result to Excel, CSV, JSON and PDF.
12. Provide truthful processing, failure, cancellation, model-readiness and recovery states.

## 3.2 Goals that are not confirmed as MVP requirements

The source set contains broader roadmap ideas such as advanced organization, batch workflows, broader multilingual support, chat over documents, smart templates, handwriting recognition, voice commands, cloud synchronization, team collaboration and AI automation workflows. These are not to be promoted to MVP unless the approved scope is explicitly changed.

---

# 4. Target Users

The SRS identifies three primary user categories:

| User | Representative use |
|---|---|
| Students | Certificates, mark sheets, forms, tables and academic documents; review, correct, save and export data. |
| Professionals | PDFs, forms, receipts, statements and business documents; extract and reuse information. |
| Small Businesses | Invoices, receipts, forms and statements; verify structured information and retain local records. |

No complex role/permission system, multi-user access model or authentication system is part of the current baseline product requirement.

---

# 5. Key Features

## 5.1 MVP feature set

The PRD identifies the following P0/core MVP capability set:

- Camera scanning.
- PDF upload/import.
- Image upload/import.
- Core image/document preprocessing needed by the documented processing path.
- OCR text extraction.
- Offline-capable AI document analysis after required model setup.
- Document type detection.
- Key-value / field extraction.
- Table detection and structured table representation.
- Confidence information where the processing pipeline actually provides it.
- User review of extracted results.
- User editing of fields and supported table content.
- Local storage of processed document data.
- Excel export.
- CSV export.
- JSON export.
- PDF export.
- Document history and reopening.
- Clear loading, success, error and cancellation states.

## 5.2 Supporting baseline features

The wider approved documentation also defines or supports:

- AI model setup/readiness state.
- Processing-stage visibility.
- Settings such as model status, OCR-language selection where supported, theme/storage/about areas according to priority.
- Android sharing/open boundary for generated exports.
- Accessibility-oriented labels/state communication.
- Safe handling of unsupported/corrupt input.
- Preservation of valid intermediate/committed data during recoverable failures.

---

# 6. Functional Requirements

The SRS is the controlled behavioral requirement source and contains **FR-001 through FR-054**. Detailed requirement text is intentionally not duplicated here; this master specification preserves the inventory and the system-level interpretation.

## 6.1 Requirement domains

| Domain | Master interpretation |
|---|---|
| FR-001..FR-006 | Document input/acquisition and input outcome handling. |
| FR-007..FR-009 | Preprocessing behavior and safe failure. |
| FR-010..FR-013 | OCR execution, downstream availability, failure handling and language configuration where supported. |
| FR-014..FR-030 | AI analysis, structured extraction, review/editing, validation and save behavior. |
| FR-031..FR-035 | Local persistence, reopening and authoritative user-correction behavior. |
| FR-036..FR-046 | Export, sharing, settings and related application behavior. |
| FR-047..FR-054 | AI model readiness/setup, offline operation, no-cloud-upload behavior and model lifecycle decisions. |

The exact requirement-level acceptance criteria and test mappings remain in the SRS, Testing, Test Cases and Requirements Traceability documents.

## 6.2 Core functional contract

```text
Acquire → Validate → Preprocess → OCR → Offline AI
→ Classify/Extract → Validate/Confidence
→ Structured Result → Review/Edit → Save
→ Export/Share → History
```

The system SHALL NOT report a stage as successfully completed merely because a request was issued, a spinner progressed, or a response parsed. Required validation must establish successful completion.

---

# 7. Non-Functional Requirements

The SRS defines **NFR-001 through NFR-028**. The master-level non-functional contract is summarized below.

| Area | Requirement intent | Status |
|---|---|---|
| Availability of core workflow | Core path must be usable for supported scenarios. | CONFIRMED requirement |
| Offline operation | Core processing works locally after required model setup. | P0 / CONFIRMED |
| Network independence | No cloud upload is required for the core processing path. | P0 / CONFIRMED |
| Data integrity | Failed operations must not silently corrupt authoritative data. | CONFIRMED |
| Performance | Long-running processing must not make the UI unusable; exact quantitative targets are open. | CONFIRMED intent / numeric targets TBD |
| Resource use | Processing must respect validated device/resource constraints. | Exact limits TBD |
| Accessibility | Core controls/states should remain understandable; conformance level is not finalized. | P0/P1/TBD by item |
| Maintainability/extensibility | Major processing boundaries should remain modular. | CONFIRMED architectural principle |
| Server scalability | No server scalability requirement for MVP. | P0 / REJECTED as MVP dependency |
| Storage | Local persistence and lifecycle handling are required. | CONFIRMED; exact limits TBD |

No performance number, accuracy percentage, RAM minimum, page limit, storage threshold or device matrix is invented in this document.

---

# 8. Complete System Architecture

## 8.1 Logical architecture

```text
┌───────────────────────────────────────────────┐
│                  Android App                  │
│                                               │
│  Presentation / UI                            │
│       ↓                                       │
│  Application / Use Cases                      │
│       ↓                                       │
│  Domain / Stable Contracts                    │
│       ↓                                       │
│  Local Infrastructure Adapters                │
│       ├── Camera / File Acquisition           │
│       ├── Preprocessing                       │
│       ├── OCR                                 │
│       ├── Offline AI / Model Manager          │
│       ├── Structured Data Validation          │
│       ├── SQLite Repositories                 │
│       ├── Local File Storage                  │
│       └── Exporters / Android Sharing         │
└───────────────────────────────────────────────┘
```

The architecture documents define logical boundaries but deliberately avoid falsely confirming an Android-specific implementation pattern that has not been verified in the actual generated project.

## 8.2 Core architectural rules

- Processing remains local/offline after model setup.
- UI does not own OCR, AI, SQL or file-format internals.
- AI output is untrusted candidate data.
- Structured data validation is required before a candidate becomes usable application data.
- Persistence/export consume the canonical structured model.
- User edits are authoritative after save.
- Failed operations do not become false success.
- Backend/REST API/cloud database are not MVP core dependencies.

## 8.3 Component boundaries

Key logical components include acquisition, validation, preprocessing, OCR adapter, AI/model manager, classification/extraction, structured-data builder/validator, review/edit manager, persistence/repositories, history, export manager, sharing boundary, state manager and error manager.

Exact class/module names are implementation-dependent and therefore **NOT SPECIFIED / REQUIRES TECHNICAL VALIDATION**.

---

# 9. Technology Stack

## 9.1 Current confirmed/required technology context

| Technology/area | Master status |
|---|---|
| Android | **CONFIRMED target platform** |
| Google AI Studio “Build an Android app” | **CONFIRMED implementation workflow** |
| Offline-first/local processing | **CONFIRMED** |
| SQLite local database | **CONFIRMED source-backed** |
| Tesseract OCR | **Source-backed in workflow context; exact Android integration REQUIRES TECHNICAL VALIDATION** |
| Offline AI | **CONFIRMED capability; exact model/runtime TBD / validation required** |
| Excel/CSV/JSON/PDF export | **CONFIRMED** |
| React Native | **Historical/source-era label only; not confirmed as current Android implementation** |
| TypeScript | **Historical/source-era label only; not confirmed as current Android implementation** |
| Node.js | **Historical/source-era label only; not an MVP backend dependency** |
| Express.js | **Historical/source-era label only; not an MVP backend dependency** |
| Exact UI toolkit | **REQUIRES TECHNICAL VALIDATION** |
| Exact Android language | **REQUIRES TECHNICAL VALIDATION** |
| Exact SQLite integration | **REQUIRES TECHNICAL VALIDATION** |
| Exact export libraries | **TBD / REQUIRES TECHNICAL VALIDATION** |

The supplied workflow image on page 2 of the original project material visually lists React Native, TypeScript, Node.js, Express.js, SQLite, Tesseract OCR, Offline AI and Excel/CSV/JSON/PDF. The current TRD/Architecture/Frontend/Build documents explicitly prevent those historical labels from being silently promoted into the final generated Android stack.

---

# 10. Application Workflow

## 10.1 Authoritative end-to-end workflow

```text
Document Input
      ↓
Document Acquisition
      ↓
Image/PDF Preprocessing
      ↓
OCR
      ↓
AI Analysis
      ↓
Document Classification
      ↓
Field & Table Extraction
      ↓
Validation / Confidence
      ↓
Structured Data
      ↓
User Review / Edit
      ↓
Local Database
      ↓
Export
      ↓
Document History
```

The supplied workflow image additionally identifies launch/setup and an explicit end state. Its visual stages are consistent with the architecture baseline.

## 10.2 State integrity

A successful end-to-end path requires truthful state transitions. At minimum, cancellation and failure must remain distinct from completion. An incomplete or unready AI model must never be represented as ready.

---

# 11. Frontend Architecture

The frontend is a **local-first Android presentation layer** connecting UX behavior to application/use-case contracts.

## 11.1 Responsibilities

- Application navigation.
- Screen presentation.
- Camera/file acquisition interaction.
- Document preview.
- Processing status/progress presentation.
- Results presentation.
- Field/table editing.
- Validation feedback.
- Save controls.
- Export selection/initiation.
- Sharing UI.
- History UI.
- Settings UI.
- Empty/loading/success/error/cancelled states.
- Accessibility semantics.
- Unsaved-edit confirmation.

## 11.2 Logical screen set

The UI/UX baseline defines logical screens including:

```text
Splash
Onboarding
Model Setup
Home
Scanner
Import
Preview
Processing
Results
Editor
Export
History
Details (optional)
Settings
OCR Language
Storage
About
Reusable Error / Empty states
```

The exact Android navigation implementation is **REQUIRES TECHNICAL VALIDATION**.

## 11.3 Frontend rules

The frontend must not:

- run OCR algorithms;
- execute AI runtime internals;
- query SQLite directly from visual components;
- generate file formats inside UI components;
- bypass application/domain contracts;
- silently lose unsaved edits;
- expose raw implementation exceptions as the primary user message.

---

# 12. Backend/API Architecture

## 12.1 MVP decision

**No backend is required for the MVP.**  
**No REST API is required for the MVP.**  
**No cloud database is required for the MVP.**

This is an architectural decision, not a missing implementation that should automatically be filled in.

## 12.2 Internal application-service architecture

The API Specification documents an internal service-contract model for the local MVP:

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
   ├── Local Files
   ├── Exporters
   └── Android Sharing
```

A future network boundary may exist later, but it is explicitly not part of the current MVP path.

## 12.3 API status

The API specification therefore describes local application contracts rather than a required external HTTP backend. Exact method/class names remain subject to implementation evidence.

---

# 13. Database Architecture

## 13.1 Persistence baseline

The database baseline is:

```text
SQLite relational database
        +
Android-local file storage
```

SQLite is the source-backed intended local database. The precise Android integration mechanism remains validation-dependent.

## 13.2 Logical stored information

The database/data-schema documents establish concepts for:

- document metadata;
- processing/history state;
- structured result;
- structured fields;
- tables;
- columns/rows/cells;
- user-edit state/authoritative current values;
- export metadata where implemented.

The canonical DATA_SCHEMA remains the semantic authority. Exact physical table definitions, indexes and migrations belong to the Database/Data Schema specifications and are not duplicated here.

## 13.3 Persistence invariants

1. Failed database transactions must not produce a false-success save.
2. Previously committed records must survive recoverable failures.
3. User-corrected saved data remains authoritative.
4. Delete behavior must reconcile database records and applicable local files according to the implemented policy.
5. No secret or unnecessary sensitive value should be persisted.
6. Database schema migrations must be versioned and tested.

Encryption of SQLite/local storage is **TBD**; this document makes no unsupported encryption guarantee.

---

# 14. Document Processing Pipeline

## 14.1 Stage sequence

```text
Acquisition
   ↓
Validation
   ↓
Preprocessing
   ↓
OCR
   ↓
AI Analysis
   ↓
Classification / Field / Table Extraction
   ↓
Structured Result Validation
   ↓
Review / Edit
```

## 14.2 Preprocessing

The supplied product/technical baseline references preprocessing capabilities including auto crop, perspective correction, noise reduction/image cleanup, brightness/image enhancement and auto rotation where available in the validated implementation.

The exact algorithms, thresholds, ordering, and implementation library are **REQUIRES TECHNICAL VALIDATION**.

## 14.3 Pipeline discipline

The processing pipeline should preserve valid intermediate evidence where safe. For example, if OCR succeeds but AI fails, OCR should remain recoverable rather than being discarded solely because a downstream stage failed.

The pipeline must use bounded recovery and must not endlessly retry.

---

# 15. OCR Pipeline

## 15.1 OCR contract

OCR converts supported document imagery into text evidence for downstream analysis.

Conceptually:

```text
Source image/PDF
      ↓
Preprocessed representation
      ↓
OCR engine / adapter
      ↓
Raw OCR evidence
      ↓
Conservative normalization
      ↓
AI input context
```

The workflow diagram identifies **Tesseract OCR** as the OCR technology context. The exact Android/Tesseract integration is not confirmed until the real implementation is inspected.

## 15.2 OCR confidence

Where confidence is supplied by the OCR implementation, it should be preserved. If confidence is unavailable, the value must remain unavailable/null rather than becoming a fabricated percentage.

## 15.3 OCR normalization

Permitted normalization is conservative. It must not silently alter high-sensitivity values such as dates, identifiers, account/reference numbers, decimal/currency semantics, phone numbers or email addresses unless the canonical processing rules support the transformation.

## 15.4 OCR failure states

Supported states include successful usable OCR, empty/unusable OCR and explicit OCR failure. These are not to be conflated.

---

# 16. AI Processing Pipeline

## 16.1 AI boundary

```text
OCR / layout evidence
       ↓
AI input adapter
       ↓
Local AI runtime
       ↓
Prompt package
       ↓
Model output
       ↓
Parser
       ↓
Schema validation
       ↓
Semantic validation
       ↓
Confidence / warnings
       ↓
Structured extraction candidate
```

The AI subsystem is responsible for document understanding tasks such as classification, field extraction, table detection and other explicitly approved extraction behavior.

## 16.2 AI trust model

AI output is **candidate data**, not application authority.

The model MUST NOT:

- write to SQLite;
- create application files/paths;
- execute commands;
- change permissions/settings;
- bypass validation;
- fabricate missing facts;
- fabricate confidence;
- overwrite saved user corrections;
- silently fall back to remote inference.

## 16.3 Missing information

```text
No source evidence
      ↓
Do not guess
      ↓
Preserve null / unresolved / unknown semantics
```

## 16.4 Confidence handling

The canonical confidence concept is optional where supplied by the processing pipeline. Exact aggregation and threshold policy are **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 17. Structured Data Model

The Data Schema document is the semantic authority for the structured result.

## 17.1 Core concepts

```text
Document
 ├── metadata
 ├── processing state/history
 └── result
      ├── document type
      ├── fields
      ├── tables
      │    ├── columns
      │    └── rows
      │         └── cells
      ├── summary where approved
      ├── confidence where available
      ├── warnings
      └── review/validation state
```

## 17.2 Authority model

```text
OCR output
   ↓
AI extraction candidate
   ↓
Validated structured result
   ↓
Human review/edit
   ↓
Saved result = authoritative
```

Raw OCR or an unapproved AI candidate is not the authoritative export source.

## 17.3 Canonical semantics

The schema supports field/table concepts, original/current values where defined, value types and review/edit semantics. The exact field vocabulary and physical representation must remain synchronized with `SnapData_DATA_SCHEMA_v1.0.md`.

---

# 18. User Review & Editing

User review is a trust and quality boundary, not merely a cosmetic screen.

## 18.1 Review capabilities

- Inspect extracted document type.
- Inspect extracted fields.
- Inspect tables.
- See available confidence/warnings.
- Edit field values.
- Edit supported table data.
- Validate edited data.
- Save corrections.

## 18.2 Edit authority

Once user corrections are saved:

```text
Saved user-reviewed value
        ↓
Authoritative persistence
        ↓
Reopen/history
        ↓
Export
```

Subsequent AI processing must not silently overwrite those saved corrections.

## 18.3 Unsaved changes

Leaving an edited document with unsaved changes requires explicit user choice. The UI baseline provides Save / Discard / Cancel semantics.

---

# 19. Local Storage

## 19.1 Storage model

```text
SQLite
  ├── Document / Result Metadata
  ├── Fields
  ├── Tables / Rows / Cells
  ├── Processing / History State
  └── Export Metadata where applicable

Android-local files
  ├── Original/source document
  ├── Required local resources
  └── Generated export/temporary artifacts according to lifecycle
```

## 19.2 File/database boundary

SQLite and local file storage are separate recovery/security boundaries. Database commit does not automatically prove physical file destruction and physical file cleanup must be reconciled by the implementation.

Exact paths, Android storage APIs, backup behavior and physical deletion policy are **REQUIRES TECHNICAL VALIDATION / TBD**.

---

# 20. Export System

## 20.1 Supported formats

- Excel (`.xlsx`)
- CSV (`.csv`)
- JSON (`.json`)
- PDF (`.pdf`)

These are **CONFIRMED product requirements**.

## 20.2 Export authority

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

Exports must reflect the latest saved/edited authoritative structured result, not stale raw OCR or an earlier AI candidate.

## 20.3 Export safety

- Validate the export request.
- Reject unsafe paths/filenames.
- Prevent path traversal/arbitrary file access.
- Preserve saved data if export fails.
- Do not report success when the file is incomplete or missing.
- Treat Android sharing as an explicit privacy boundary.

Exact export libraries and platform APIs are **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 21. Security & Privacy

## 21.1 Security principles

The security baseline explicitly requires:

1. Core processing stays local after required model setup.
2. Cloud upload is not a prerequisite for the core workflow.
3. Imported files, filenames, OCR text, AI output and export parameters are untrusted inputs.
4. Sensitive document content must not appear in routine diagnostics/logs.
5. User-corrected saved data is authoritative.
6. SQLite and local files remain separate security/recovery boundaries.
7. Export/sharing is an explicit privacy boundary.
8. Incomplete/unverified model resources are not ready.
9. Security claims must be supported by implementation and test evidence.

## 21.2 Security controls required for the MVP

At minimum, release validation must cover:

- no unintended core document upload/network dependency;
- safe file/path validation;
- no sensitive document content in routine logs;
- AI output treated as untrusted data;
- preservation of user edits;
- export path/file safety;
- deletion/reconciliation behavior;
- model-readiness/integrity handling to the extent implemented;
- least-privilege Android permissions once the manifest is known;
- offline/network audit;
- interruption/failure integrity.

## 21.3 Security features not yet confirmed

The following must not be presented as already implemented:

- encrypted SQLite/local storage;
- cryptographic key management;
- PIN lock;
- biometric lock;
- guaranteed secure/physical deletion;
- specific backup exclusion policy.

Their status is **TBD / REQUIRES TECHNICAL VALIDATION** according to the security baseline.

---

# 22. Offline-First Architecture

## 22.1 Core promise

After required AI model setup, the core processing path is intended to operate without an active internet connection.

```text
Initial setup / model provisioning
          │
          └── network may be required if the approved setup process requires it

Normal core workflow
          ↓
      Local device
          ↓
Acquisition → Preprocess → OCR → AI → Structure
→ Review/Edit → SQLite/File Storage → Export
```

## 22.2 Offline requirements

The implementation must validate that:

- the model is actually ready before offline inference;
- processing does not depend on hidden remote fallback;
- offline failure states are explicit and truthful;
- previously saved local documents remain accessible when no network exists.

Exact model size, download mechanism, runtime, supported devices and resource requirements remain **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 23. Error Handling & Recovery

## 23.1 Core failure rules

1. A failed operation must not be reported as successful.
2. Invalid structured data must not become authoritative storage.
3. Saved user corrections must not be silently overwritten.
4. Existing committed records must survive recoverable failures.
5. Safe partial results should be preserved where supported.
6. Sensitive transient content must have a controlled lifecycle.
7. Error messages must be understandable without leaking internal technical details.
8. A missing/unready model must never appear ready.

## 23.2 Error domains

The error baseline covers input, permission, preprocessing, OCR, model, AI, validation, persistence, resource, export, sharing, lifecycle and cancellation failures. The SRS defines **ERR-001 through ERR-018**.

## 23.3 Recovery hierarchy

```text
Fail honestly
   ↓
Preserve valid intermediate/committed evidence
   ↓
Deterministic safe repair where justified
   ↓
Bounded retry
   ↓
Partial/degraded result where contract permits
   ↓
User action / setup / alternate input
```

## 23.4 Transaction integrity

Database-bound saves must behave transactionally. A failure results in rollback/preservation rather than false success.

## 23.5 Cancellation

Cancellation is distinct from failure and completion. The current exact in-flight resume semantics are **TBD**.

---

# 24. Performance Strategy

## 24.1 Performance principles

- Keep the UI responsive during long-running work.
- Avoid unnecessary whole-document decoding/copying.
- Avoid redundant OCR/AI recomputation.
- Process large documents incrementally where the implementation supports it.
- Manage memory aggressively around OCR, AI and export stages.
- Avoid rendering unnecessarily large table datasets in one blocking operation.
- Clean up temporary resources under explicit lifecycle rules.

## 24.2 Required measurements

The performance baseline requires evidence for relevant:

- AI model load time;
- inference time;
- OCR processing time;
- preprocessing time;
- export time;
- memory/resource use;
- storage footprint;
- device compatibility;
- offline behavior under realistic resource conditions.

No numeric threshold is invented here. Exact targets are **TBD / REQUIRES TECHNICAL VALIDATION**.

## 24.3 Performance evidence

Performance evidence must be tied to a specific build, device and dataset/version. Documentation alone is not a performance result.

---

# 25. Testing Strategy

The testing baseline covers the full system and explicitly distinguishes test design from actual execution.

## 25.1 Required test layers

- Unit/component tests.
- Application/use-case integration tests.
- Persistence/database tests.
- AI/OCR integration tests.
- Golden-corpus evaluation.
- Malformed-output tests.
- Hallucination/unsupported-value tests.
- UI/E2E tests.
- Offline tests.
- Accessibility/usability validation at the approved scope.
- Security/privacy tests.
- Performance/resource benchmarks.
- Regression tests.
- Database migration tests.
- Export format and fidelity validation.

## 25.2 End-to-end acceptance criteria

The SRS and implementation plan define **AC-001 through AC-012**, covering:

1. Input/acquisition.
2. Preprocessing.
3. OCR.
4. AI classification/extraction.
5. Review.
6. Edit/save/reopen authority.
7. Export formats.
8. Sharing.
9. History.
10. Offline processing.
11. Model unavailable/readiness behavior.
12. Failure/recovery integrity.

The exact acceptance text belongs to the SRS and test catalog.

## 25.3 Test execution status

The approved Test Cases document states **NOT EXECUTED** at baseline creation because no build-linked execution evidence was available. Therefore this master document claims **no passed tests** unless later evidence is attached.

---

# 26. Requirements Traceability

The Requirements Traceability document is the authoritative traceability control. This master specification summarizes its model rather than duplicating the full matrix.

## 26.1 Traceability chain

```text
Requirement
   ↓
Feature / Behavior
   ↓
Architecture Boundary
   ↓
Implementation Boundary
   ↓
Test Case
   ↓
Execution Evidence
   ↓
Verification / Release Decision
```

## 26.2 Current traceability status

The traceability baseline records coverage for all **82 controlled FR/NFR requirements** and preserves the SRS identifiers. It distinguishes specification coverage from implementation/test evidence.

## 26.3 Evidence rule

A mapped test is not proof of passing behavior. A complete requirement matrix is not proof of implementation. A screenshot is not proof of AI accuracy. A generated artifact is not proof that release gates passed.

---

# 27. Implementation Plan

The Implementation Plan is the execution authority for implementation sequencing and evidence requirements.

## 27.1 Recommended execution order from the approved baseline

```text
1. Inspect generated Android project / freeze concrete stack
2. Confirm build/toolchain
3. Establish application/navigation shell
4. Implement document acquisition
5. Implement validation and preprocessing
6. Integrate/validate OCR
7. Integrate/validate AI model/runtime
8. Implement structured result validation
9. Implement review/edit workflow
10. Implement SQLite/local persistence
11. Implement history
12. Implement exports/sharing
13. Implement error/recovery hardening
14. Run unit/integration/E2E/security/performance validation
15. Produce release candidate
16. Run release gates and production smoke test
```

The plan explicitly states that documentation completeness must not be mistaken for implementation completion. Initial engineering tasks are treated as **TODO** unless actual evidence establishes another state.

## 27.2 Implementation evidence

Completion evidence may include source commit, review/check record, build output, static-analysis result, screenshots/screen recordings, representative processed documents, database integrity results, JSON validation, export samples, AI/OCR benchmark evidence, security evidence and release artifacts.

---

# 28. Build & Release

## 28.1 Release model

The Build & Release baseline defines a controlled flow:

```text
Source
  ↓
Dependency Resolution
  ↓
Static / Quality Checks
  ↓
Unit Tests
  ↓
Integration Tests
  ↓
AI/OCR Validation
  ↓
Offline Validation
  ↓
Release Build
  ↓
Security / Privacy Validation
  ↓
Artifact Verification
  ↓
Release Candidate Approval
  ↓
Distribution
```

## 28.2 Release artifacts

The source baseline references APK for direct installation/testing and AAB for Play distribution if selected. The exact build configuration, signing configuration and CI/CD provider are implementation-specific/TBD until verified.

## 28.3 Release requirements

A release candidate requires:

- successful build;
- stable launch/navigation;
- validated core input/processing/storage/export path;
- required tests passed;
- offline validation passed;
- security/privacy gate passed;
- performance measurements recorded;
- release blockers cleared;
- artifact integrity/signing verified;
- production smoke test passed.

---

# 29. User Workflow

## 29.1 Primary user journey

```text
Launch SnapData
      ↓
First-run model setup if required
      ↓
Home
      ↓
Scan / Upload PDF / Upload Image
      ↓
Preview / Confirm
      ↓
Processing
      ├── Preprocessing
      ├── OCR
      ├── Offline AI
      ├── Classification
      ├── Field/Table extraction
      └── Validation
      ↓
Results
      ↓
Review / Edit
      ↓
Save
      ↓
Export / Share
      ↓
History / Reopen / Delete
```

## 29.2 User trust points

The user should always be able to distinguish:

- machine-generated information from user-corrected information;
- unavailable information from failed processing;
- processing from completed work;
- local processing readiness from unavailable model state;
- saved data from unsaved draft state.

---

# 30. MVP Scope

## 30.1 MVP requirements

The MVP contains only the smallest complete core workflow:

| Capability | MVP |
|---|---:|
| Camera scan | REQUIRED |
| PDF import | REQUIRED |
| Image import | REQUIRED |
| Core preprocessing | REQUIRED |
| OCR | REQUIRED |
| Offline AI after model setup | REQUIRED |
| Document type detection | REQUIRED |
| Field/key-value extraction | REQUIRED |
| Table detection/structure | REQUIRED |
| Confidence where available | REQUIRED |
| Review | REQUIRED |
| Field/table editing | REQUIRED |
| Local persistence | REQUIRED |
| History/reopen | REQUIRED |
| Excel export | REQUIRED |
| CSV export | REQUIRED |
| JSON export | REQUIRED |
| PDF export | REQUIRED |
| Clear error/cancellation states | REQUIRED |
| Required model setup/readiness | REQUIRED |
| Core offline operation | REQUIRED |
| Backend | NOT REQUIRED / REJECTED |
| REST API | NOT REQUIRED / REJECTED |
| Cloud database | NOT REQUIRED / REJECTED |

## 30.2 MVP non-goals

Do not add future capabilities simply because they are documented elsewhere. Items such as AI chat, handwriting recognition, voice commands, team collaboration and cloud sync remain future concepts unless the approved baseline changes.

---

# 31. Future Scope

The supplied roadmap supports future concepts including:

- batch processing and batch export;
- expanded multilingual/OCR support;
- smart extraction templates;
- richer document-specific understanding;
- AI chat / questions about documents;
- handwriting recognition;
- voice commands;
- optional cloud synchronization;
- team collaboration;
- AI automation workflows.

These are **future scope**, not MVP requirements.

---

# 32. Known Limitations

The current source set has several deliberate limits:

1. Exact Android generated-project stack is not verified.
2. Exact AI model is not finalized.
3. Exact AI runtime is not finalized.
4. Exact OCR/Tesseract Android integration is not finalized.
5. Exact preprocessing algorithms/parameters are not finalized.
6. Exact OCR language list is not finalized.
7. Exact supported document/page/size limits are not finalized.
8. Exact device/RAM/storage baseline is not finalized.
9. Exact quantitative OCR/AI accuracy thresholds are not finalized.
10. Exact performance thresholds are not finalized.
11. Exact export implementation libraries are not finalized.
12. Exact encryption/key-management mechanism is not finalized.
13. Exact interrupted-processing resume semantics are not finalized.
14. Actual Android build/test evidence was not available in the approved documentation set at baseline creation.

---

# 33. Risks & Mitigations

| Risk | Impact | Source-aligned mitigation |
|---|---|---|
| AI/OCR feasibility on device | High | Benchmark model/runtime/OCR on the target device matrix before freeze. |
| Unsupported/poor document quality | High | Validate preprocessing, OCR, confidence/warnings and review workflow. |
| Hallucinated AI values | Critical | Evidence-grounded extraction, schema validation, null/unknown semantics, review boundary. |
| User corrections overwritten | Critical | Authoritative saved-result rule and end-to-end regression tests. |
| Local resource exhaustion | High | Incremental processing, resource validation, graceful failure, performance testing. |
| Sensitive data leakage in logs | High | Explicit logging restrictions and security audit. |
| Export of stale data | High | Export only current authoritative saved result; export tests. |
| Model marked ready when corrupt/incomplete | High | Explicit readiness and integrity checks. |
| File/path abuse | High | Input and export path validation. |
| Temporary sensitive artifacts remain | High | Lifecycle ownership, cleanup and reconciliation testing. |
| Android implementation differs from historical diagram | Medium/High | Inspect actual generated project before stack freeze. |
| Documentation says more than implementation proves | High | Evidence discipline and traceability status separation. |

---

# 34. Project Quality Gates

## Gate 0 — Requirements / Scope

- [ ] MVP scope is explicitly frozen.
- [ ] No future feature is unintentionally treated as MVP.
- [ ] Open product decisions with release impact are resolved or formally accepted.

## Gate 1 — Technical Baseline

- [ ] Actual generated Android project inspected.
- [ ] Language/UI/build/toolchain recorded.
- [ ] OCR integration recorded.
- [ ] AI model/runtime recorded.
- [ ] SQLite integration recorded.
- [ ] Export implementation recorded.
- [ ] Supported Android/device baseline recorded.

## Gate 2 — Application Integrity

- [ ] App builds.
- [ ] App launches.
- [ ] Navigation is stable.
- [ ] Loading/error/success/cancelled states are truthful.

## Gate 3 — Input & Processing

- [ ] Camera input validated.
- [ ] PDF input validated.
- [ ] Image input validated.
- [ ] Preprocessing validated.
- [ ] OCR validated.
- [ ] Offline AI validated.
- [ ] Classification validated.
- [ ] Fields validated.
- [ ] Tables validated.
- [ ] Confidence/warning semantics validated.

## Gate 4 — Data Integrity

- [ ] Canonical structured model validated.
- [ ] User edits become authoritative.
- [ ] SQLite persistence validated.
- [ ] Migration tests pass.
- [ ] History/reopen/delete validated.

## Gate 5 — Export

- [ ] Excel validated.
- [ ] CSV validated.
- [ ] JSON validated.
- [ ] PDF validated.
- [ ] Sharing validated.
- [ ] Stale-data prevention validated.

## Gate 6 — Security / Privacy

- [ ] Offline/no-hidden-upload audit passes.
- [ ] Sensitive logging audit passes.
- [ ] Safe path/file tests pass.
- [ ] AI output trust-boundary tests pass.
- [ ] Model integrity/readiness tests pass to implemented scope.
- [ ] Deletion/reconciliation behavior validated.
- [ ] Open encryption/security decisions explicitly recorded.

## Gate 7 — Performance / Compatibility

- [ ] Performance measurements recorded.
- [ ] Resource behavior validated.
- [ ] Supported-device matrix validated.
- [ ] No critical unexplained regression.

## Gate 8 — Release Candidate

- [ ] Release build succeeds.
- [ ] Signing/artifact integrity verified.
- [ ] Production smoke test passes.
- [ ] Release blockers cleared.
- [ ] Acceptance criteria AC-001..AC-012 are satisfied or formally dispositioned.

---

# 35. Final MVP Readiness Checklist

## 35.1 Product

- [ ] P0/MVP scope implemented.
- [ ] No unresolved release-blocking product ambiguity.

## 35.2 Core workflow

- [ ] Document acquisition works.
- [ ] Validation works.
- [ ] Preprocessing works.
- [ ] OCR works.
- [ ] Offline AI works after model setup.
- [ ] Classification works within validated scope.
- [ ] Field extraction works within validated scope.
- [ ] Table extraction works within validated scope.
- [ ] Structured validation works.
- [ ] Review/edit works.
- [ ] Save works.
- [ ] History/reopen/delete works.
- [ ] Excel/CSV/JSON/PDF export works.
- [ ] Sharing works or fails truthfully.

## 35.3 Data integrity

- [ ] User edits survive save/reopen.
- [ ] User edits survive export.
- [ ] Failed saves roll back safely.
- [ ] Failed exports do not damage saved results.
- [ ] AI does not overwrite authoritative user corrections.

## 35.4 AI/OCR

- [ ] Selected model/runtime documented.
- [ ] OCR integration documented.
- [ ] Golden corpus established.
- [ ] Malformed-output tests pass.
- [ ] Hallucination/unsupported-value tests pass.
- [ ] Missing-value semantics are correct.
- [ ] Confidence is only shown when supported.
- [ ] Performance/resource benchmark exists.

## 35.5 Security/privacy

- [ ] No unexpected core network dependency.
- [ ] No raw document/OCR/sensitive values in routine logs.
- [ ] Path/file safety validated.
- [ ] Export/share boundary validated.
- [ ] Model readiness/integrity validated.
- [ ] Deletion/reconciliation validated.
- [ ] Any unimplemented security control is explicitly disclosed.

## 35.6 Release

- [ ] Build passes.
- [ ] Required test suites pass.
- [ ] Offline gate passes.
- [ ] Security/privacy gate passes.
- [ ] Performance evidence recorded.
- [ ] Compatibility evidence recorded.
- [ ] Release artifact verified.
- [ ] Production smoke test passes.
- [ ] No release-blocking defects remain.

---

# Master Status Register

| Area | Status |
|---|---|
| Product definition | CONFIRMED from PRD/SRS |
| Android target | CONFIRMED |
| Google AI Studio Android build workflow | CONFIRMED |
| Offline-first core | CONFIRMED |
| SQLite local persistence | CONFIRMED source-backed; exact integration TBD |
| Camera/PDF/image input | CONFIRMED |
| OCR capability | CONFIRMED; exact integration requires validation |
| Tesseract | Source-backed technology context; exact implementation requires validation |
| Offline AI | CONFIRMED capability; model/runtime TBD/validation |
| Structured fields/tables | CONFIRMED |
| Review/edit | CONFIRMED |
| Excel/CSV/JSON/PDF | CONFIRMED |
| Document history | CONFIRMED |
| Backend for MVP | REJECTED / NOT REQUIRED |
| REST API for MVP | REJECTED / NOT REQUIRED |
| Cloud database for MVP | REJECTED / NOT REQUIRED |
| Exact Android language | REQUIRES TECHNICAL VALIDATION |
| Exact UI toolkit | REQUIRES TECHNICAL VALIDATION |
| Exact architecture pattern | PROPOSED / validation dependent |
| Exact AI model | TBD |
| Exact AI runtime | TBD / REQUIRES TECHNICAL VALIDATION |
| Exact OCR language list | TBD |
| Exact preprocessing implementation | TBD / REQUIRES TECHNICAL VALIDATION |
| Exact page/size limits | REQUIRES TECHNICAL VALIDATION |
| Exact device/RAM/storage baseline | TBD / REQUIRES TECHNICAL VALIDATION |
| Numeric AI/OCR accuracy targets | NOT SPECIFIED / TBD |
| Numeric performance targets | TBD |
| Export libraries | TBD / REQUIRES TECHNICAL VALIDATION |
| Encryption/key management | TBD |
| PIN/biometric lock | TBD / not an established MVP guarantee |
| Secure physical deletion | TBD / not an established guarantee |
| Interrupted-processing resume | TBD |
| Actual implementation status | NOT VERIFIED from available source evidence |
| Test execution status | NOT EXECUTED at baseline unless later build-linked evidence is supplied |

---

# Open Decisions / Contradictions / Gaps

## A. Open technical decisions

The approved TRD/Frontend/Architecture/Build documents identify open decisions including exact Android minimum/target versions, generated language/UI stack, architecture pattern, SQLite integration, Tesseract integration, OCR languages, AI model/runtime, model packaging/lifecycle, device requirements, maximum document/page limits, multi-page PDF priority, preprocessing algorithms, export implementations, final test framework, CI/CD pipeline and related implementation specifics.

## B. Source-era technology contradiction

The original workflow diagram visually lists **React Native, TypeScript, Node.js and Express.js**. The current technical baseline targets a **Google AI Studio Android application** and explicitly states that the historical labels must not be treated as confirmed current implementation choices. Accordingly:

- React Native/TypeScript are retained as historical/source-era context only.
- Node.js/Express.js are not required as MVP backend infrastructure.
- The current implementation stack must be confirmed from the actual Android project.

This is a documented discrepancy, not silently resolved content.

## C. Specification versus implementation gap

The Requirements Traceability and Test Case baselines state that the source workspace did not contain an inspectable Google AI Studio-generated Android source tree/build artifact at baseline creation. Consequently, the following are **not claimed** by this master specification:

- implementation completion;
- passed tests;
- measured performance;
- achieved AI/OCR accuracy;
- final device compatibility;
- final release readiness.

## D. Security gaps

Encryption, cryptographic key management, PIN/biometric lock, exact backup handling and guaranteed physical secure deletion are not confirmed. Security must not be overstated in presentations or release notes until evidence exists.

---

# Documentation Relationship Map

The complete approved documentation set works together as follows:

```text
PRD
  → WHY / product intent and scope

SRS
  → WHAT / externally observable behavior and controlled requirements

TRD
  → TECHNICAL REQUIREMENTS / constraints and open technical decisions

SYSTEM ARCHITECTURE
  → HOW THE SYSTEM IS STRUCTURED / boundaries and dependencies

UI/UX
  → HOW USERS EXPERIENCE THE PRODUCT / interaction and visual behavior

FRONTEND
  → UI IMPLEMENTATION / presentation and application boundary

BACKEND/API
  → APPLICATION SERVICES / local contracts; no required MVP network backend

DATABASE
  → DATA STORAGE / persistence and migration boundary

AI/OCR
  → INTELLIGENCE PIPELINE / OCR and local AI processing

DOCUMENT PROCESSING
  → PROCESSING PIPELINE / stages, validation, partial results and provenance

DATA SCHEMA
  → STRUCTURED DATA / canonical semantic model

EXPORT
  → OUTPUT FORMATS / Excel, CSV, JSON, PDF and sharing boundary

SECURITY & PRIVACY
  → PROTECTION / privacy, trust boundaries and security acceptance

TESTING
  → VERIFICATION STRATEGY / test layers and quality gates

BUILD & RELEASE
  → SHIPPING / build, signing, artifacts and release controls

IMPLEMENTATION PLAN
  → EXECUTION / implementation order and evidence requirements

REQUIREMENTS TRACEABILITY
  → COVERAGE / requirement → implementation → test → evidence

TEST CASES
  → VERIFICATION CATALOG / executable test definitions

AI PROMPT SPECIFICATION
  → AI OUTPUT CONTROL / prompting, validation, grounding and regression

ERROR HANDLING & RECOVERY
  → FAILURE / recovery, integrity and user-safe error behavior

PERFORMANCE OPTIMIZATION
  → OPTIMIZATION / resource/performance strategy and measurement discipline

USER GUIDE
  → END-USER OPERATION / how the completed product is used

MASTER SPECIFICATION
  → COMPLETE SYSTEM VIEW / integration and release-readiness reference
```

---

# Final Master Conclusion

SnapData's authoritative MVP is a **local-first Android document-processing system** whose core engineering value is the complete and verifiable pipeline from document acquisition to structured, reviewable, locally stored and exportable data.

The master architecture is:

```text
Android App
   ↓
Document Input
   ↓
Acquisition
   ↓
Preprocessing
   ↓
OCR
   ↓
Offline AI
   ↓
Classification + Field/Table Extraction
   ↓
Validation + Confidence/Warnings
   ↓
Canonical Structured Data
   ↓
User Review/Edit
   ↓
SQLite + Local File Storage
   ↓
History
   ↓
Excel / CSV / JSON / PDF
   ↓
Android Sharing
```

The product should not be considered MVP-ready merely because the documentation set is complete. Production-quality readiness requires implementation evidence, build-linked test evidence, offline validation, security/privacy validation, performance measurements, artifact verification and release-gate completion.

Until the actual generated Android project and build/test evidence are inspected, all implementation-specific items that remain open must stay explicitly labeled **TBD**, **NOT SPECIFIED**, **PROPOSED**, or **REQUIRES TECHNICAL VALIDATION**.

**End of `SnapData_MASTER_SPECIFICATION_v1.0.md`**
