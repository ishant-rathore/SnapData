# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Software Implementation Plan, Engineering Roadmap & Development Execution Document

**Document:** `SnapData_IMPLEMENTATION_PLAN_v1.0.md`  
**Version:** 1.0  
**Status:** Engineering Execution Baseline  
**Date:** 30 August 2026  
**Implementation Target:** Android application using the Google AI Studio **Build an Android app** workflow  
**Primary Execution Mode:** Offline-first / local processing  
**MVP Backend:** None required  
**MVP REST API:** None required  
**MVP Cloud Database:** None required  

---

# 0. Document Control

## 0.1 Purpose

This document translates the approved SnapData product, software, technical, architecture, UX, data, AI/OCR, document-processing, export, testing, security/privacy, and build/release baselines into an executable engineering plan.

It defines **what to implement, in what order, with which dependencies, what evidence is required, and what gates determine completion**. It does not replace the source specifications and does not introduce a second product architecture.

## 0.2 Source-of-Truth Hierarchy

The following sources are authoritative for this implementation plan:

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
15. Original SnapData project specification
16. SnapData workflow diagram
17. Actual Google AI Studio-generated Android project, **when available**

## 0.3 Status Vocabulary

| Status | Meaning |
|---|---|
| **CONFIRMED** | Explicitly established by project source material or directly verified in the implementation. |
| **PROPOSED** | A recommended execution approach that is not itself an implementation fact. |
| **TBD** | A decision has not yet been made. |
| **REQUIRES TECHNICAL VALIDATION** | Product intent exists, but exact feasibility, compatibility, integration, or measured performance must be verified. |
| **REJECTED** | Intentionally excluded from the current baseline. |
| **OPTIONAL** | Permitted enhancement that is not required for the baseline. |

## 0.4 Execution Status Vocabulary

| Status | Meaning |
|---|---|
| **TODO** | Not started. |
| **IN PROGRESS** | Implementation work is actively underway. |
| **BLOCKED** | Cannot proceed because a dependency, decision, toolchain, or technical issue is unresolved. |
| **DONE** | Acceptance criteria and Definition of Done are satisfied. |
| **DEFERRED** | Explicitly postponed outside the current execution increment. |

All initial tasks in this baseline are **TODO** unless an authoritative source explicitly proves otherwise. No implementation completion is inferred from documentation completeness.

## 0.5 Actual Project Availability

The available source set contains the SnapData engineering documents, original specification, workflow diagram, and generated technical documentation, but no inspectable Gradle project, Android source tree, `AndroidManifest.xml`, dependency lock/resolution state, or build artifact is currently available as an implementation baseline.

Therefore the following remain **REQUIRES TECHNICAL VALIDATION** until the actual Google AI Studio-generated Android project is inspected:

- programming language;
- UI toolkit;
- exact Android architecture pattern;
- package/module structure;
- build plugin and SDK versions;
- dependency versions;
- camera/file APIs;
- OCR integration;
- AI runtime/model;
- model packaging and lifecycle;
- exact storage APIs;
- export libraries;
- background execution mechanism;
- permission set;
- device/resource requirements.

Historical technology labels shown in the workflow diagram are preserved as project context and must not be treated as final implementation facts unless the TRD and actual project evidence confirm them.

---

# 1. Implementation Objective

## 1.1 Primary Objective

Build SnapData from the current source baseline into a reliable, testable, production-ready Android application that can execute the complete local document-processing workflow:

```text
Launch
  ↓
Input Document
  ↓
Acquire / Normalize
  ↓
Pre-process
  ↓
OCR
  ↓
Offline AI
  ↓
Classify / Extract
  ↓
Validate / Confidence
  ↓
Structured Data
  ↓
Review / Edit
  ↓
SQLite + Local Files
  ↓
History
  ↓
Export
  ↓
Android Share
```

The original project specification and workflow describe the same end-to-end transformation from document input through OCR, AI analysis, field/table detection, review/editing, export, and local storage. The workflow diagram on page 2 additionally shows application launch, acquisition, preprocessing, OCR, offline AI, structured data, review/editing, local storage, export, and history as explicit stages.

## 1.2 What This Plan Must Answer

This plan establishes:

- the implementation sequence;
- module boundaries;
- task dependencies;
- parallelizable work;
- critical path;
- milestone gates;
- acceptance criteria;
- release gates;
- known blockers;
- implementation risks;
- MVP exclusions;
- engineering Definition of Ready and Definition of Done.

## 1.3 Execution Philosophy

Implementation will proceed from **foundation → working vertical slice → capability expansion → hardening → release**.

The preferred strategy is to prove the core document-to-data path early rather than fully building every UI screen before the processing pipeline is known to work.

---

# 2. MVP Boundary

## 2.1 MVP Required

The MVP implementation target is the smallest complete system that demonstrates the central SnapData workflow and satisfies the current P0 contract.

| Capability | MVP Priority | Implementation Status |
|---|---:|---|
| Android application shell | P0 | TODO |
| Camera document input | P0 | TODO |
| PDF import | P0 | TODO |
| Image import | P0 | TODO |
| Multi-page PDF support | P0 target | TODO / REQUIRES TECHNICAL VALIDATION for exact limits/behavior |
| Input validation | P0 | TODO |
| Image/document acquisition | P0 | TODO |
| Core image preprocessing | P0 | TODO |
| OCR text extraction | P0 | TODO |
| Offline-capable AI after model setup | P0 | TODO / model-runtime TBD |
| Document type detection | P0 | TODO |
| Key-value / field extraction | P0 | TODO |
| Table detection and structured representation | P0 | TODO |
| Confidence information where produced | P0 | TODO |
| Canonical structured data generation | P0 | TODO |
| User review | P0 | TODO |
| Field editing | P0 | TODO |
| Table editing | P0 | TODO |
| Local SQLite persistence | P0 | TODO |
| Local original/working file storage | P0 | TODO |
| Document history | P0 | TODO |
| Reopen saved document | P0 | TODO |
| Excel export | P0 | TODO |
| CSV export | P0 | TODO |
| JSON export | P0 | TODO |
| PDF export | P0 | TODO |
| Android sharing | P0 | TODO |
| Clear processing/error states | P0 | TODO |
| Offline validation | P0 | TODO |
| Security/privacy baseline controls | P0 | TODO |
| Production build/release validation | P0 | TODO |

The PRD defines the core MVP as input → preprocessing → OCR → AI extraction → structured data → review/edit → local storage → export → history, including camera/PDF/image input, offline-capable AI after setup, document type detection, field/table extraction, confidence information, review/editing, local storage, Excel/CSV/JSON/PDF export, history, reopening, and clear processing/error states.

## 2.2 MVP Optional / Conditional

These capabilities may be included only when they do not compromise the P0 pipeline or release criteria:

| Capability | Status | Decision Rule |
|---|---|---|
| AI document summary | TBD / OPTIONAL | Include only after core extraction is stable and product scope confirms it. |
| Advanced scan enhancements beyond core preprocessing | P1 / OPTIONAL | Include after baseline preprocessing is reliable. |
| Advanced document organization | P1 | Defer unless explicitly promoted. |
| Favorites/tags/folders/collections | P1/P2 | Not required for the first complete engineering slice. |
| Batch processing | P1 / FUTURE | Defer until single-document path is reliable. |
| Batch export | P1 / FUTURE | Defer until single-export correctness is proven. |
| AI chat / Ask questions | FUTURE | Not part of current core MVP. |
| Custom extraction templates | FUTURE / P1 candidate | Requires separate schema/product decision. |
| PIN/biometric lock | TBD | Do not claim implementation until confirmed. |
| Encryption at rest | TBD | Do not claim implementation until confirmed. |
| Model update/delete | TBD | Implement only after model lifecycle is finalized. |

## 2.3 Post-MVP / Future

The supplied roadmap identifies later possibilities including:

- document chat/question answering;
- batch processing and batch export;
- broader language support;
- smart extraction templates;
- stronger table recognition;
- handwriting recognition;
- voice commands;
- optional cloud synchronization;
- team collaboration;
- AI automation workflows.

These are not release blockers for the current MVP.

## 2.4 Explicitly Not in MVP

The following are deliberately excluded from the MVP execution plan:

- required backend server;
- required REST API;
- cloud document processing;
- cloud database;
- mandatory multi-user authentication;
- server-side document synchronization;
- any backend feature without an approved product/technical change.

---

# 3. Implementation Principles

## 3.1 Core Principles

1. **Build the critical data path first.**
2. **Keep module boundaries explicit.**
3. **Keep domain/structured data independent from UI details.**
4. **Keep OCR and AI behind replaceable boundaries.**
5. **Use the canonical structured-data model as the downstream contract.**
6. **Treat user corrections as authoritative.**
7. **Do not export raw OCR or stale AI candidates when the saved structured result is authoritative.**
8. **Do not silently invent missing values.**
9. **Do not expose document content in routine logs.**
10. **Validate AI output before persistence/export.**
11. **Keep the core workflow local after model setup.**
12. **Prefer incremental validation over late integration.**
13. **Do not add backend/API infrastructure to solve local MVP problems.**
14. **Measure performance before optimization.**
15. **Do not mark technical decisions as confirmed until the actual Android project or benchmark evidence confirms them.**

## 3.2 Engineering Anti-Patterns to Avoid

- giant screen components containing processing/business logic;
- direct UI access to SQLite tables;
- duplicate transformation logic in exporters;
- direct dependency on a specific OCR implementation from presentation code;
- direct dependency on a specific AI runtime from UI code;
- hard-coded secrets or credentials;
- unbounded in-memory retention of large documents;
- trusting AI output without validation;
- silently replacing saved user edits with reprocessed AI output;
- creating backend/API layers before a requirement exists;
- premature optimization based on assumptions.

---

# 4. High-Level Implementation Flow

```text
PHASE 0  Project Initialization
    ↓
PHASE 1  Application Foundation
    ↓
PHASE 2  Onboarding + Model Setup
    ↓
PHASE 3  Document Input
    ↓
PHASE 4  Document Acquisition
    ↓
PHASE 5  Image Preprocessing
    ↓
PHASE 6  OCR
    ↓
PHASE 7  Offline AI
    ↓
PHASE 8  Document Classification
    ↓
PHASE 9  Field Extraction
    ↓
PHASE 10 Table Extraction
    ↓
PHASE 11 Structured Data Engine
    ↓
PHASE 12 Review/Edit
    ↓
PHASE 13 SQLite Persistence
    ↓
PHASE 14 History/Search
    ↓
PHASE 15 Export Engine
    ↓
PHASE 16 Excel
    ↓
PHASE 17 CSV
    ↓
PHASE 18 JSON
    ↓
PHASE 19 PDF
    ↓
PHASE 20 Android Sharing
    ↓
PHASE 21 Settings
    ↓
PHASE 22 Security Hardening
    ↓
PHASE 23 Testing
    ↓
PHASE 24 Performance
    ↓
PHASE 25 Release Preparation
    ↓
PHASE 26 Production Release
```

The sequence is intentionally implementation-oriented rather than a copy of the architecture document. Several phases can execute in parallel once their contracts are stable.

---

# 5. System Module Inventory

| Module ID | Module Name | Purpose | Inputs | Outputs | Dependencies | Status | Priority | Testing |
|---|---|---|---|---|---|---|---:|---|
| MOD-001 | Application Shell | Bootstrap app lifecycle and top-level state | App launch, resources | Running app state | Project setup | TODO | P0 | Unit/UI/smoke |
| MOD-002 | Navigation | Route between application surfaces | Navigation events | Screen state | Shell, frontend | TODO | P0 | UI/navigation |
| MOD-003 | Onboarding | First-run guidance and readiness flow | First-run state | Onboarding completion | Shell, model manager | TODO | P0 | UI/E2E |
| MOD-004 | AI Model Manager | Setup, readiness, lifecycle state | Model resource/config | Model readiness state | AI runtime decision | TODO | P0 | Unit/integration |
| MOD-005 | Home Dashboard | Primary document entry point | History/model state | User actions | Navigation, history | TODO | P0 | UI |
| MOD-006 | Camera Scanner | Capture documents | Camera frames/input | Image/pages | Android camera boundary | TODO | P0 | device/UI |
| MOD-007 | File Import | Import PDF/image files | Android file selection | File reference | Platform file picker | TODO | P0 | integration |
| MOD-008 | Document Acquisition | Normalize source documents/pages | Camera/file input | Acquisition object | Camera/import | TODO | P0 | integration |
| MOD-009 | Image Preprocessing | Normalize images for OCR | Acquired page image | Processed image | Acquisition | TODO | P0 | component |
| MOD-010 | OCR Engine | Convert image to OCR text/metadata | Processed image | OCR result | Preprocessing, OCR runtime | TODO | P0 | OCR regression |
| MOD-011 | Offline AI Engine | Local document understanding | OCR evidence | AI result | Model manager | TODO | P0 | AI integration |
| MOD-012 | Document Classification | Identify document type | OCR/AI evidence | Type + evidence | AI engine | TODO | P0 | classification |
| MOD-013 | Field Extraction | Extract key-value data | OCR/AI evidence | Fields | AI/classification | TODO | P0 | extraction |
| MOD-014 | Table Extraction | Extract rows/columns/cells | OCR/AI evidence | Tables | AI/OCR | TODO | P0 | table |
| MOD-015 | Confidence Evaluation | Carry confidence/warnings | Extraction result | Confidence metadata | OCR/AI/extraction | TODO | P0 | confidence |
| MOD-016 | Structured Data Engine | Produce canonical model | Fields, tables, metadata | Structured document | schema rules | TODO | P0 | schema/unit |
| MOD-017 | Review/Edit | Human correction and validation | Structured document | Edited structured document | UI + schema | TODO | P0 | UI/integration |
| MOD-018 | SQLite Persistence | Durable local data | Canonical data | Saved entities | Database schema | TODO | P0 | DB/integration |
| MOD-019 | Document History | Reopen/delete/manage records | SQLite records | History views/actions | Persistence | TODO | P0 | UI/integration |
| MOD-020 | Search | Locate saved documents | Query/filter | Matching history | History/persistence | TODO | P1 | unit/UI |
| MOD-021 | Export Engine | Common export contract | Saved structured data | Export result | Schema/persistence | TODO | P0 | integration |
| MOD-022 | Excel Export | XLSX output | Canonical data | XLSX file | Export engine/library TBD | TODO | P0 | artifact |
| MOD-023 | CSV Export | CSV output | Canonical data | CSV file | Export engine | TODO | P0 | artifact |
| MOD-024 | JSON Export | Canonical JSON output | Canonical data | JSON file | Export engine/schema | TODO | P0 | schema/artifact |
| MOD-025 | PDF Export | Readable structured PDF | Canonical data | PDF file | Export engine/PDF library TBD | TODO | P0 | artifact |
| MOD-026 | Android Sharing | Share/open export files | Export URI/result | Android share action | Platform boundary | TODO | P0 | device |
| MOD-027 | Settings | Product configuration surfaces | User settings | Stored preferences | Shell/model/storage | TODO | P1 | UI/unit |
| MOD-028 | Security/Privacy | Enforce privacy/file/data safety | All sensitive paths | Safe execution | Platform + modules | TODO | P0 | security |
| MOD-029 | Error Handling | Normalize failure states | Module failures | User-safe errors | All modules | TODO | P0 | unit/E2E |
| MOD-030 | Logging | Minimal diagnostic observability | Internal events | Safe diagnostics | All modules | TODO | P0 | security/review |
| MOD-031 | Testing Infrastructure | Automate and organize validation | Code/build/data | Test evidence | Build/test tooling | TODO | P0 | all |

**Note:** This inventory is a logical implementation inventory. Exact source-code packages/modules must be derived from the actual generated Android project and may differ in naming or grouping.

---

# 6. Module Interaction Contract

The intended dependency direction is:

```text
Presentation
   ↓
Application / Orchestration
   ↓
Domain / Canonical Structured Data
   ↓
Processing Services
   ├── Acquisition
   ├── Preprocessing
   ├── OCR Adapter
   ├── AI Adapter
   ├── Extraction
   └── Validation / Confidence
   ↓
Infrastructure
   ├── SQLite
   ├── Local File Storage
   ├── Exporters
   └── Android Platform Boundaries
```

This reflects the architecture baseline's preference for separation of presentation, application, domain, processing, persistence, file storage, and export responsibilities.

## 6.1 Contract Rules

- Presentation does not implement OCR/AI business logic.
- Exporters do not reconstruct their own domain model.
- SQLite mapping does not become the semantic structured-data contract.
- OCR output is evidence, not the final authoritative business representation.
- AI output is candidate structured information and must be validated before use.
- Saved user edits become authoritative for reopen/export.
- Platform-specific APIs stay behind platform boundaries where practical.

---

# 7. Dependency Graph

## 7.1 Critical Sequential Path

```text
Application Shell
      ↓
Navigation
      ↓
Document Input
      ↓
Document Acquisition
      ↓
Preprocessing
      ↓
OCR
      ↓
Offline AI
      ↓
Extraction
      ↓
Structured Data
      ↓
Review/Edit
      ↓
SQLite
      ↓
History
      ↓
Export
      ↓
Testing
      ↓
Release
```

## 7.2 Parallel Work Streams

Once the shared contracts are established, the following can proceed in parallel:

```text
FRONTEND
├── Shell / Navigation
├── Home
├── Input / Camera UI
├── Processing UI
├── Results
├── Editor
├── History
├── Export UI
└── Settings

PROCESSING
├── Acquisition
├── Preprocessing
├── OCR adapter
├── AI adapter
├── Classification
├── Field extraction
├── Table extraction
└── Confidence/validation

PERSISTENCE
├── SQLite mapping
├── Local file storage
├── Migration tests
└── History queries

EXPORT
├── Common export contract
├── Excel
├── CSV
├── JSON
└── PDF

QUALITY
├── Unit tests
├── Integration tests
├── OCR/AI corpus
├── Offline testing
├── Security testing
└── Performance measurement
```

Backend/API work is intentionally absent from the parallel MVP plan.

---

# 8. Phase 0 — Project Initialization

## Goal

Turn the Google AI Studio-generated starting point into a known, buildable engineering workspace.

## Tasks

- inspect the generated project source tree;
- identify language and UI toolkit;
- identify existing architecture pattern;
- inspect Gradle/build files;
- identify package/application ID;
- identify Android SDK/toolchain assumptions;
- identify dependency graph;
- establish Git repository/branch baseline;
- establish `.gitignore`;
- configure lint/static-analysis tooling;
- establish test source sets;
- verify debug build;
- capture initial toolchain inventory.

## Exit Criteria

```text
[ ] Project opens successfully
[ ] Debug build succeeds
[ ] App installs on reference device/emulator
[ ] App launches
[ ] Current generated dependencies are inventoried
[ ] Exact language/UI/build choices are recorded
[ ] No unapproved secrets are present
```

## Blocking Issues

Any inability to produce a clean debug build is a **P0 blocker** for further implementation.

---

# 9. Phase 1 — Application Foundation

## Scope

Implement the application shell without business processing logic.

## Tasks

- establish theme and common design tokens from UI/UX source;
- implement navigation shell;
- implement reusable UI state patterns;
- implement loading state;
- implement empty state;
- implement error state;
- implement common buttons/cards/inputs/dialogs where required;
- implement accessibility semantics;
- connect placeholder screens using navigation only.

## Exit Criteria

All baseline screens can be reached and exited without crashes, while processing logic remains intentionally stubbed.

---

# 10. Phase 2 — Onboarding & AI Model Setup

## Scope

Implement first-run setup and AI readiness behavior without prematurely locking an unvalidated model/runtime.

## Tasks

- splash/launch state;
- first-run detection;
- onboarding flow;
- model readiness state;
- missing-model state;
- model setup/download path where supported by selected runtime;
- progress state;
- model failure state;
- retry behavior;
- model metadata display where source requirements support it;
- readiness gate before AI processing.

## Exit Criteria

User can complete first-time setup, reach Home, and receive a truthful model-ready/not-ready state.

## Technical Validation Gate

Before the AI processing phases are frozen, validate:

- model identity;
- runtime compatibility;
- packaging mechanism;
- memory footprint;
- storage footprint;
- initialization latency;
- offline execution.

Until validated, model/runtime fields remain **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 11. Phase 3 — Document Input

## Scope

Implement the three baseline acquisition entry points: camera, PDF, and image.

## Tasks

- request only required camera/file permissions;
- implement image picker/file picker boundary;
- implement camera entry;
- validate MIME/type;
- validate available file metadata;
- handle unsupported type;
- handle cancel;
- handle inaccessible/malformed input;
- support multi-page document entry according to validated technical limits.

## Exit Criteria

A valid supported document can enter the acquisition stage from each supported entry point.

---

# 12. Phase 4 — Document Acquisition

## Scope

Normalize input into a processing-ready internal representation.

## Tasks

- create acquisition result contract;
- capture source type metadata;
- resolve page sequence;
- extract PDF pages where required;
- normalize image/page references;
- create temporary processing workspace;
- track document metadata;
- define cleanup path;
- preserve source/original reference independently from processed page data.

## Exit Criteria

A supported input is represented as normalized processing data without unnecessary duplication.

---

# 13. Phase 5 — Image Preprocessing

## Scope

Implement the processing operations required by the documented pipeline.

## Tasks

- auto-crop where selected/available;
- perspective correction where selected/available;
- rotation correction;
- brightness enhancement;
- noise reduction;
- image normalization;
- safe fallback when an automatic step fails;
- preprocessing result validation;
- memory-safe handling of large page images.

Exact algorithms are **REQUIRES TECHNICAL VALIDATION** and must follow the selected implementation, not this plan.

## Exit Criteria

OCR receives a valid normalized image for all supported reference cases, or the system returns a truthful and recoverable preprocessing failure.

---

# 14. Phase 6 — OCR

## Scope

Implement OCR behind an adapter boundary.

The AI/OCR baseline identifies Tesseract as the source-backed OCR context while leaving the exact Android integration open for validation.

## OCR Pipeline

```text
Normalized Image
      ↓
OCR Adapter
      ↓
Text + Available Layout/Metadata
      ↓
OCR Validation
      ↓
AI Input Preparation
```

## Tasks

- initialize OCR engine/resources;
- establish language/resource handling;
- process one page;
- process multi-page inputs;
- collect page-level text;
- collect confidence/metadata where exposed;
- normalize OCR output;
- handle empty output;
- handle OCR failure;
- measure latency and memory;
- create representative OCR corpus;
- establish regression baseline.

## Exit Criteria

OCR produces usable text for the approved sample corpus and fails truthfully for unsupported/poor-quality inputs.

---

# 15. Phase 7 — Offline AI

## Scope

Implement on-device AI document analysis after model/runtime validation.

## AI Pipeline

```text
OCR Evidence
    ↓
Document Analysis
    ↓
Document Type
    ↓
Fields / Key-Value Pairs
    ↓
Tables
    ↓
Confidence / Warnings
    ↓
Schema Validation
```

## Tasks

- initialize validated runtime;
- confirm model readiness before inference;
- define input preparation contract;
- run document analysis;
- parse structured output;
- validate returned structure;
- reject malformed output;
- handle unavailable model;
- handle resource exhaustion;
- release inference resources;
- support cancellation where runtime allows;
- measure inference latency/memory/storage;
- build AI regression corpus.

## Exit Criteria

The application can execute AI extraction locally with validated structured output on the approved reference device/corpus.

## Hard Rule

Missing information is not to be fabricated. Unknown or unresolved values remain explicitly unresolved.

---

# 16. Phase 8 — Document Classification

## Scope

Implement document-type determination as a downstream capability of AI/extraction.

## Target Types

- Invoice
- Receipt
- Form
- Bank Statement
- Certificate
- Mark Sheet
- ID Card
- Business Card
- Table
- General Document / Unknown fallback

These are intended source-backed categories; exact classification coverage and acceptance thresholds remain subject to the validation corpus.

## Exit Criteria

Supported reference documents receive stable type values, while unknown documents fall back safely according to the finalized model.

---

# 17. Phase 9 — Field Extraction

## Scope

Implement generic key-value/field extraction without document-specific hard-coding beyond approved schema rules.

## Tasks

- field identification;
- label/key normalization;
- value normalization;
- value type assignment;
- confidence propagation where available;
- missing-value handling;
- source/page association where the data model supports it;
- validation rules;
- evidence handling;
- correction-safe representation.

## Acceptance Rules

- no invented values;
- malformed AI values are rejected or flagged;
- unresolved values remain unresolved;
- user edits override candidates;
- saved fields can be reloaded unchanged.

---

# 18. Phase 10 — Table Extraction

## Scope

Implement table detection and editable structured representation.

## Tasks

- detect table regions/structures;
- infer columns;
- infer rows;
- extract cells;
- normalize column ordering;
- maintain row/cell associations;
- preserve empty cells where semantically required;
- produce canonical table objects;
- expose tables to editor and exporters;
- validate table consistency.

## Exit Criteria

Approved reference tables can be rendered, edited, saved, reopened, and exported without row/column association corruption.

---

# 19. Phase 11 — Structured Data Engine

## Purpose

Produce the canonical SnapData structured result consumed by review, persistence, history, and export.

## Canonical Concepts

```text
Document
├── Metadata
├── Processing Metadata
├── Result
│   ├── Document Type
│   ├── Fields
│   ├── Tables
│   │   ├── Columns
│   │   └── Rows
│   │       └── Cells
│   ├── Confidence Metadata
│   └── Warnings
└── Review State
```

## Tasks

- implement canonical domain model;
- map extraction output into canonical data;
- apply validation/invariants;
- separate semantic schema from SQLite internals;
- serialize/deserialize canonical data where required;
- preserve edit flags/authoritative state;
- provide a stable contract to export/persistence.

## Exit Criteria

All downstream consumers use the same canonical structure.

---

# 20. Phase 12 — Review & Edit

## Scope

Implement the human validation boundary.

## Tasks

- results view;
- field editing;
- table editing;
- add row;
- delete row;
- edit cell;
- validation feedback;
- save changes;
- cancel changes;
- unsaved-change behavior;
- user correction tracking as supported by the canonical data model;
- prevent silent AI overwrite.

## Critical Integrity Rule

The saved user-corrected structured result is the authoritative result for reopen and export.

## Exit Criteria

A user can correct a field/table, save, close/reopen, and verify that the correction remains authoritative.

---

# 21. Phase 13 — SQLite & Local Persistence

## Scope

Implement local durable persistence according to `SnapData_DATABASE_v1.0.md` and `SnapData_DATA_SCHEMA_v1.0.md`.

## Tasks

- database initialization;
- schema creation;
- versioning;
- migrations;
- document metadata persistence;
- processing metadata;
- structured result persistence;
- field persistence;
- table/row/cell persistence;
- history persistence;
- deletion semantics;
- transaction boundaries;
- mapping tests;
- orphan detection/recovery rules.

The database baseline confirms SQLite as the intended local database, while the exact Android persistence API/library remains **REQUIRES TECHNICAL VALIDATION** until the generated project is inspected.

## Exit Criteria

Saved documents survive application restart, can be reopened, and can be deleted without corrupting unrelated records.

---

# 22. Phase 14 — Document History & Search

## Scope

Implement the local document-management surface required by the MVP.

## Tasks

- history list;
- recent documents ordering according to source rules;
- search where included in baseline UI scope;
- metadata display;
- reopen;
- delete;
- empty state;
- error state;
- storage failure handling.

## Exit Criteria

User can locate, reopen, and delete saved documents while authoritative values remain intact.

---

# 23. Phase 15 — Export Engine

## Scope

Create a common export abstraction consumed by all formats.

```text
Authoritative Saved Structured Data
            ↓
      Pre-Export Validation
            ↓
       Export Engine
       /    |    |    \
     XLSX  CSV  JSON   PDF
```

## Tasks

- export request contract;
- authoritative-data resolution;
- stale-data/revision protection;
- pre-export validation;
- exporter registry/selection;
- file naming/sanitization;
- temporary-output handling;
- file integrity checking;
- export failure handling;
- export cancellation where supported;
- artifact persistence metadata where specified.

## Exit Criteria

All exporters consume the same canonical saved result and never bypass the authoritative-data rule.

---

# 24. Phase 16 — Excel Export

## Tasks

- workbook generation;
- field sheet;
- table sheet(s);
- headers;
- values;
- data typing where supported;
- stable file naming;
- basic formatting;
- artifact integrity validation.

## Acceptance

Exported workbook reflects the latest saved values, contains required fields/tables, and opens successfully in a compatible spreadsheet viewer.

Exact Excel library is **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 25. Phase 17 — CSV Export

## Tasks

- field export strategy;
- table export strategy;
- delimiter handling;
- escaping;
- encoding;
- line endings as required by target behavior;
- filename sanitization;
- output validation.

## Acceptance

Generated CSV is parseable and represents current authoritative values without malformed escaping or truncated rows.

---

# 26. Phase 18 — JSON Export

## Tasks

- serialize canonical structured data;
- preserve schema version;
- validate JSON syntax;
- preserve correct value types according to canonical schema;
- exclude SQLite-only implementation metadata;
- validate round-trip behavior where specified.

## Acceptance

Generated JSON is valid, schema-consistent, and reflects the current authoritative saved result.

---

# 27. Phase 19 — PDF Export

## Tasks

- document metadata representation;
- field rendering;
- table rendering;
- pagination;
- readable layout;
- output file naming;
- artifact integrity validation.

## Acceptance

Generated PDF is readable, contains current fields/tables, and paginates without silently dropping required content.

Exact PDF engine/library is **TBD / REQUIRES TECHNICAL VALIDATION**.

---

# 28. Phase 20 — Android Sharing

## Scope

Implement secure sharing/opening of exported files through Android content/URI boundaries.

## Tasks

- generate content URI rather than exposing internal paths;
- assign correct MIME type;
- grant temporary access where required;
- invoke Android share/open action;
- handle no target application;
- handle target failure;
- ensure internal filesystem paths are not exposed.

## Exit Criteria

An exported file can be shared/opened without leaking arbitrary internal storage paths.

Exact Android API implementation is **REQUIRES TECHNICAL VALIDATION**.

---

# 29. Phase 21 — Settings

## Scope

Implement only finalized settings from the product and UX baseline.

## Candidate MVP Settings

- OCR language, when language scope is finalized;
- AI model status/information;
- storage information;
- theme;
- export preferences;
- about.

The exact OCR language list, model management actions, theme implementation, and advanced settings are subject to their respective source decisions.

## Exit Criteria

Settings modify only approved behavior and do not introduce new unsupported configuration paths.

---

# 30. Phase 22 — Security Hardening

## Scope

Apply the security/privacy implementation baseline without claiming unimplemented controls.

## Tasks

- minimize Android permissions;
- validate file type and source;
- prevent unsafe paths/path traversal;
- clean temporary files;
- minimize sensitive diagnostics;
- validate AI output;
- maintain database integrity;
- protect export paths;
- use safe Android file-sharing boundaries;
- handle malformed/malicious files safely;
- review model resources before activation;
- audit network behavior;
- test offline operation.

## Explicit Non-Claims

Do **not** claim the following until implementation evidence exists:

- encrypted SQLite;
- encryption at rest;
- PIN/biometric lock;
- secure physical deletion;
- cryptographic key management;
- universal secure backup/restore.

These are explicitly open/qualified in the security baseline.

## Exit Criteria

All P0 security/privacy tests pass and no release-blocking privacy defect remains.

---

# 31. Phase 23 — Testing

Testing begins during development, not after feature completion.

## Test Layers

```text
Unit
  ↓
Component
  ↓
Integration
  ↓
System/UI
  ↓
E2E
  ↓
Offline
  ↓
Security/Privacy
  ↓
Performance
  ↓
Release Validation
```

The detailed test authority is `SnapData_TESTING_v1.0.md`.

## Mandatory Areas

- document input;
- preprocessing;
- OCR;
- AI classification;
- field extraction;
- table extraction;
- confidence handling;
- structured data invariants;
- user review/editing;
- correction integrity;
- SQLite and migrations;
- history;
- all export formats;
- Android sharing;
- offline mode;
- error handling;
- processing state transitions;
- lifecycle/rotation where applicable;
- accessibility;
- Unicode/date/number/currency handling;
- performance/memory/large documents;
- security/privacy.

---

# 32. Phase 24 — Performance Optimization

## Principle

**Measure first. Optimize second.**

## Measure

- cold startup;
- warm startup;
- document acquisition time;
- preprocessing latency;
- OCR latency;
- AI initialization latency;
- AI inference latency;
- total processing time;
- peak memory;
- storage consumption;
- large-document behavior;
- export latency;
- share handoff latency where relevant.

## Baseline Policy

Actual numeric targets are **TBD** unless already approved in the source baseline. The implementation must record measured values before a release decision.

## Exit Criteria

Benchmark values exist for the validated device matrix and no critical resource regression remains.

---

# 33. Phase 25 — Release Preparation

## Tasks

- production configuration;
- version name/version code strategy;
- release build configuration;
- signing configuration;
- release dependency review;
- AI/OCR resource validation;
- shrinker behavior validation where applicable;
- release smoke test;
- offline test;
- security test;
- regression suite;
- artifact checksum;
- release metadata;
- release notes;
- final sign-off.

Use `SnapData_BUILD_RELEASE_v1.0.md` as the release authority.

## Exit Criteria

A release candidate satisfies all mandatory quality gates and no blocking defect remains.

---

# 34. Phase 26 — Production Release

## Controlled Flow

```text
Release Candidate
      ↓
QA Approval
      ↓
Security/Privacy Approval
      ↓
AI/OCR Validation
      ↓
Offline Validation
      ↓
Production Build
      ↓
Signing
      ↓
Artifact Verification
      ↓
Distribution
      ↓
Post-Release Smoke Test
```

Exact distribution channel is **TBD** until selected.

---

# 35. Detailed Implementation Task Register

Complexity scale: **S = Small**, **M = Medium**, **L = Large**, **XL = High-risk/major integration**. Complexity is relative and should be recalibrated after actual project inspection.

| Task ID | Phase | Module | Task | Priority | Dependencies | Complexity | Acceptance Criteria | Status | Notes |
|---|---:|---|---|---:|---|---:|---|---|---|
| IMP-001 | P0 | MOD-031 | Inspect generated Android project | P0 | Source project | M | Toolchain and source structure inventoried | TODO | Blocker if unavailable |
| IMP-002 | P0 | MOD-001 | Establish source-control baseline | P0 | IMP-001 | S | Clean initial branch/commit state | TODO | |
| IMP-003 | P0 | MOD-001 | Verify debug build | P0 | IMP-001 | M | App builds/launches | TODO | P0 blocker |
| IMP-004 | P0 | MOD-001 | Configure lint/static analysis | P0 | IMP-001 | S | Checks execute | TODO | Exact tools TBD |
| IMP-005 | P0 | MOD-031 | Establish test structure | P0 | IMP-001 | M | Test sources execute | TODO | |
| IMP-006 | P0 | MOD-001 | Record package/app identity | P0 | IMP-001 | S | Identity documented | TODO | |
| IMP-007 | P1 | MOD-001 | Remove unused generated code/dependencies | P1 | IMP-003 | M | No unnecessary baseline dependency remains | TODO | Only after inspection |
| IMP-008 | P0 | MOD-002 | Implement navigation shell | P0 | IMP-003 | M | Core routes accessible | TODO | |
| IMP-009 | P0 | MOD-001 | Implement theme/common UI states | P0 | IMP-003 | M | Loading/error/empty states reusable | TODO | Follow UI/UX |
| IMP-010 | P0 | MOD-002 | Validate Android Back behavior | P0 | IMP-008 | S | Back follows defined UX | TODO | |
| IMP-011 | P0 | MOD-003 | Implement splash/first-run state | P0 | IMP-008 | S | First launch state correct | TODO | |
| IMP-012 | P0 | MOD-004 | Create model readiness contract | P0 | IMP-001 | M | UI can observe ready/not-ready/failure | TODO | Runtime TBD |
| IMP-013 | P0 | MOD-004 | Validate model/runtime choice | P0 | IMP-012 | XL | Runtime/model benchmarked | TODO | Critical blocker |
| IMP-014 | P0 | MOD-003 | Implement onboarding | P0 | IMP-012 | M | User can complete setup | TODO | |
| IMP-015 | P0 | MOD-004 | Implement model setup/download path | P0 | IMP-013 | L | Model reaches verified ready state | TODO | Mechanism TBD |
| IMP-016 | P0 | MOD-004 | Implement model failure/retry state | P0 | IMP-015 | M | Failure is truthful/recoverable | TODO | |
| IMP-017 | P0 | MOD-005 | Implement Home dashboard | P0 | IMP-008 | M | Scan/import actions available | TODO | |
| IMP-018 | P0 | MOD-006 | Implement camera boundary | P0 | IMP-001 | L | Camera capture works on reference device | TODO | API TBD |
| IMP-019 | P0 | MOD-007 | Implement image/PDF file import | P0 | IMP-001 | M | Valid files imported | TODO | |
| IMP-020 | P0 | MOD-007 | Validate MIME/file inputs | P0 | IMP-019 | M | Unsupported files rejected safely | TODO | |
| IMP-021 | P0 | MOD-008 | Define acquisition result | P0 | IMP-018/019 | M | Uniform document representation | TODO | |
| IMP-022 | P0 | MOD-008 | Implement page sequencing | P0 | IMP-021 | L | Multi-page order preserved | TODO | Limits TBD |
| IMP-023 | P0 | MOD-008 | Implement temporary workspace | P0 | IMP-021 | M | Temporary artifacts isolated/cleaned | TODO | |
| IMP-024 | P0 | MOD-009 | Implement image normalization | P0 | IMP-021 | M | OCR input valid | TODO | |
| IMP-025 | P0 | MOD-009 | Implement crop/perspective/rotation paths | P0 | IMP-024 | L | Approved preprocessing cases pass | TODO | Algorithms TBD |
| IMP-026 | P0 | MOD-009 | Add preprocessing fallback handling | P0 | IMP-025 | M | Failure never yields false success | TODO | |
| IMP-027 | P0 | MOD-010 | Create OCR adapter | P0 | IMP-013/024 | M | OCR provider isolated | TODO | |
| IMP-028 | P0 | MOD-010 | Integrate validated OCR runtime | P0 | IMP-027 | L | OCR runs on reference corpus | TODO | Tesseract context |
| IMP-029 | P0 | MOD-010 | Implement page-level OCR | P0 | IMP-028 | M | Page text returned | TODO | |
| IMP-030 | P0 | MOD-010 | Implement OCR metadata/confidence handling | P0 | IMP-029 | M | Available metadata preserved | TODO | |
| IMP-031 | P0 | MOD-010 | Implement OCR failure/empty behavior | P0 | IMP-029 | M | Truthful failure states | TODO | |
| IMP-032 | P0 | MOD-031 | Create OCR regression corpus | P0 | IMP-028 | M | Versioned test corpus exists | TODO | Synthetic/test data preferred |
| IMP-033 | P0 | MOD-011 | Create AI adapter | P0 | IMP-013 | M | AI provider/runtime isolated | TODO | |
| IMP-034 | P0 | MOD-011 | Implement AI initialization | P0 | IMP-013 | L | Model loads safely | TODO | |
| IMP-035 | P0 | MOD-011 | Implement AI input preparation | P0 | IMP-029 | M | Valid AI context generated | TODO | |
| IMP-036 | P0 | MOD-011 | Implement structured AI output parsing | P0 | IMP-033/035 | L | Output parsed/validated | TODO | |
| IMP-037 | P0 | MOD-011 | Implement AI failure/resource handling | P0 | IMP-034 | M | Failure state/retry works | TODO | |
| IMP-038 | P0 | MOD-031 | Create AI regression corpus | P0 | IMP-036 | M | Versioned corpus and expected results | TODO | Ground truth TBD |
| IMP-039 | P0 | MOD-012 | Implement document classification | P0 | IMP-036 | M | Reference types classify or fallback | TODO | |
| IMP-040 | P0 | MOD-013 | Implement field extraction mapping | P0 | IMP-036 | L | Fields represented canonically | TODO | |
| IMP-041 | P0 | MOD-013 | Implement field validation | P0 | IMP-040 | M | Invalid values flagged/rejected | TODO | |
| IMP-042 | P0 | MOD-014 | Implement table extraction mapping | P0 | IMP-036 | XL | Tables preserve structure | TODO | High-risk |
| IMP-043 | P0 | MOD-014 | Implement table normalization | P0 | IMP-042 | M | Rows/columns stable | TODO | |
| IMP-044 | P0 | MOD-015 | Implement confidence/warning propagation | P0 | IMP-039/040/042 | M | Available confidence preserved | TODO | Aggregation TBD |
| IMP-045 | P0 | MOD-016 | Implement canonical structured model | P0 | IMP-039/040/042/044 | L | One canonical downstream contract | TODO | |
| IMP-046 | P0 | MOD-016 | Implement structured data invariants | P0 | IMP-045 | M | Invalid structures rejected | TODO | |
| IMP-047 | P0 | MOD-017 | Implement results view | P0 | IMP-045 | M | Fields/tables visible | TODO | |
| IMP-048 | P0 | MOD-017 | Implement field editor | P0 | IMP-047 | M | Edits retained | TODO | |
| IMP-049 | P0 | MOD-017 | Implement table editor | P0 | IMP-047 | XL | Cells/rows editable | TODO | |
| IMP-050 | P0 | MOD-017 | Implement add/delete row | P0 | IMP-049 | M | Table changes persist in state | TODO | |
| IMP-051 | P0 | MOD-017 | Implement save/cancel edit state | P0 | IMP-048/049 | M | No accidental loss/overwrite | TODO | |
| IMP-052 | P0 | MOD-016 | Enforce user-authoritative result rule | P0 | IMP-051 | L | Reprocessing cannot silently overwrite saved edits | TODO | Critical |
| IMP-053 | P0 | MOD-018 | Initialize SQLite | P0 | IMP-045 | M | DB opens and schema creates | TODO | API TBD |
| IMP-054 | P0 | MOD-018 | Implement schema migrations | P0 | IMP-053 | L | Migrations deterministic | TODO | |
| IMP-055 | P0 | MOD-018 | Persist document metadata | P0 | IMP-053 | M | Reopen metadata | TODO | |
| IMP-056 | P0 | MOD-018 | Persist fields/tables | P0 | IMP-045/053 | L | Structured result round-trips | TODO | |
| IMP-057 | P0 | MOD-018 | Persist user edits | P0 | IMP-052/056 | M | Corrections survive restart | TODO | |
| IMP-058 | P0 | MOD-018 | Implement delete semantics | P0 | IMP-055/056 | M | No orphaned DB records under tested paths | TODO | |
| IMP-059 | P0 | MOD-008 | Implement local file persistence boundary | P0 | IMP-023 | M | Original/working files stored safely | TODO | Storage API TBD |
| IMP-060 | P0 | MOD-019 | Implement history list | P0 | IMP-055 | M | Saved records visible | TODO | |
| IMP-061 | P0 | MOD-019 | Implement reopen | P0 | IMP-057/060 | M | Same authoritative data restored | TODO | |
| IMP-062 | P0 | MOD-019 | Implement delete UI | P0 | IMP-058/060 | S | Record deleted safely | TODO | |
| IMP-063 | P1 | MOD-020 | Implement search | P1 | IMP-060 | M | Matching records returned | TODO | Scope may be promoted |
| IMP-064 | P0 | MOD-021 | Create export contract | P0 | IMP-045/057 | M | All exporters share canonical input | TODO | |
| IMP-065 | P0 | MOD-021 | Implement pre-export validation | P0 | IMP-064 | M | Invalid exports blocked | TODO | |
| IMP-066 | P0 | MOD-022 | Implement Excel exporter | P0 | IMP-064 | L | XLSX opens and values current | TODO | Library TBD |
| IMP-067 | P0 | MOD-023 | Implement CSV exporter | P0 | IMP-064 | M | CSV parses correctly | TODO | |
| IMP-068 | P0 | MOD-024 | Implement JSON exporter | P0 | IMP-064/045 | M | JSON valid/schema-consistent | TODO | |
| IMP-069 | P0 | MOD-025 | Implement PDF exporter | P0 | IMP-064 | XL | PDF readable/current | TODO | Engine TBD |
| IMP-070 | P0 | MOD-026 | Implement secure share URI flow | P0 | IMP-066/067/068/069 | M | No internal path exposure | TODO | API TBD |
| IMP-071 | P0 | MOD-027 | Implement model/settings status surfaces | P1 | IMP-012 | S | Approved settings work | TODO | |
| IMP-072 | P0 | MOD-028 | Implement file/path security checks | P0 | IMP-019/023 | M | Unsafe inputs rejected | TODO | |
| IMP-073 | P0 | MOD-028 | Audit permissions | P0 | IMP-001/018 | S | Minimum required permissions | TODO | |
| IMP-074 | P0 | MOD-030 | Implement privacy-safe logging | P0 | IMP-003 | M | No routine document content logs | TODO | |
| IMP-075 | P0 | MOD-029 | Implement normalized processing errors | P0 | IMP-026/031/037 | M | Errors map to truthful UI states | TODO | |
| IMP-076 | P0 | MOD-029 | Implement cancellation handling | P0 | IMP-008/037 | M | Cancellation does not fake completion | TODO | Resume TBD |
| IMP-077 | P0 | MOD-031 | Implement unit test suite | P0 | Core components | L | Required unit suite passes | TODO | |
| IMP-078 | P0 | MOD-031 | Implement integration test suite | P0 | Processing/Persistence | L | Core boundaries pass | TODO | |
| IMP-079 | P0 | MOD-031 | Implement E2E happy path | P0 | End-to-end feature set | L | AC happy path passes | TODO | |
| IMP-080 | P0 | MOD-031 | Implement mandatory correction-integrity test | P0 | IMP-052 | M | User edits remain authoritative | TODO | Release blocker |
| IMP-081 | P0 | MOD-031 | Implement offline test gate | P0 | IMP-034/066-070 | M | Core workflow works without network after setup | TODO | Release blocker |
| IMP-082 | P0 | MOD-031 | Implement security test suite | P0 | Security controls | L | P0 security tests pass | TODO | |
| IMP-083 | P0 | MOD-031 | Implement export cross-format tests | P0 | IMP-066-069 | M | Same authoritative data across formats | TODO | |
| IMP-084 | P0 | MOD-031 | Implement performance benchmarks | P0 | Working pipeline | M | Benchmark report produced | TODO | Actual values recorded |
| IMP-085 | P0 | MOD-031 | Validate large-document behavior | P0 | IMP-022/034/066-069 | L | Approved limits tested | TODO | Limits TBD |
| IMP-086 | P0 | MOD-031 | Validate memory/resource behavior | P0 | Working pipeline | L | No release-blocking resource failure | TODO | Device matrix TBD |
| IMP-087 | P0 | MOD-031 | Run release regression suite | P0 | All P0 | L | No blocking regression | TODO | |
| IMP-088 | P0 | MOD-031 | Run production smoke test | P0 | Release candidate | M | All core smoke steps pass | TODO | |
| IMP-089 | P0 | MOD-031 | Generate release evidence bundle | P0 | Test/release | M | Build/test/security evidence linked | TODO | |
| IMP-090 | P0 | MOD-031 | Production release approval | P0 | All release gates | M | Sign-off recorded | TODO | |

---

# 36. Workstream Ownership Model

Exact named personnel are **TBD**. Recommended responsibility ownership is:

| Workstream | Primary Responsibility | Key Dependencies |
|---|---|---|
| Project/Build | Build & Release owner | Actual project inspection |
| Frontend | UI/Frontend owner | UI/UX + processing contracts |
| Processing | Processing owner | Acquisition + OCR + AI |
| AI/OCR | AI/OCR owner | Runtime/model validation |
| Persistence | Database owner | Canonical data schema |
| Export | Export owner | Canonical saved data |
| Security | Security owner | Storage/file/network boundaries |
| QA | Testing owner | All completed features |
| Release | Release owner | QA + security + AI/OCR validation |

For a single-developer mini-project, these ownership areas may be performed by one person, but the responsibilities should remain separated conceptually.

---

# 37. Critical Path Analysis

## 37.1 Critical Path

```text
Project Setup
→ Application Foundation
→ Input
→ Acquisition
→ Preprocessing
→ OCR
→ Offline AI
→ Structured Extraction
→ Review/Edit
→ SQLite
→ History
→ Export
→ Integration Testing
→ Security/Offline Validation
→ Release
```

## 37.2 Bottlenecks

### Bottleneck A — Actual Android Project Validation

Without the generated project, exact framework/toolchain decisions cannot be frozen.

**Risk:** implementation starts against assumptions that later conflict with the generated project.

### Bottleneck B — AI Runtime/Model

The model and runtime determine memory requirements, packaging, latency, offline feasibility, and supported devices.

**Risk:** the AI runtime becomes the dominant architecture constraint.

### Bottleneck C — OCR Integration

OCR quality strongly influences downstream extraction quality.

**Risk:** poor OCR causes false downstream confidence and weak table/field extraction.

### Bottleneck D — Table Extraction

Tables contain structural relationships that are harder to preserve than plain text.

**Risk:** row/column/cell corruption can invalidate exports.

### Bottleneck E — Export Fidelity

Each output format can introduce its own constraints.

**Risk:** export becomes a late-stage defect multiplier if not tested against canonical data earlier.

### Bottleneck F — Device Memory

Local OCR + AI + image processing can create high resource pressure.

**Risk:** crashes or thermal/memory issues on lower-resource Android devices.

---

# 38. Integration Strategy

## 38.1 Vertical-Slice Strategy

The recommended first complete vertical slice is:

```text
Image/PDF
 ↓
Acquire
 ↓
Preprocess
 ↓
OCR
 ↓
AI
 ↓
One structured result
 ↓
Review one field
 ↓
Save
 ↓
Reopen
 ↓
Export JSON
```

After this slice is stable, expand to:

- tables;
- camera;
- full history;
- Excel/CSV/PDF;
- sharing;
- hardening.

## 38.2 Why JSON Is an Early Export Target

JSON directly reflects the canonical structured-data contract and is therefore useful as an early validation path before more presentation-heavy export formats. This is an implementation tactic, not a change to MVP export priorities.

---

# 39. Feature-by-Feature Acceptance Gates

| Feature | Minimum Gate |
|---|---|
| Camera | Capture → acquisition → processing without crash |
| PDF import | Supported PDF enters normalized pipeline |
| Image import | Supported image enters normalized pipeline |
| Preprocessing | Valid normalized image or truthful failure |
| OCR | Usable text or truthful failure |
| AI | Local structured result or truthful model/resource failure |
| Classification | Stable type/fallback for reference corpus |
| Fields | Canonical fields, no invented missing data |
| Tables | Rows/columns/cells preserved |
| Confidence | Available information exposed without false precision |
| Review | User can inspect result before export |
| Edit | Saved user correction remains authoritative |
| SQLite | Data survives restart |
| History | Reopen/delete work |
| Excel | Workbook opens and values match saved result |
| CSV | Parseable and current |
| JSON | Valid and schema-consistent |
| PDF | Readable and current |
| Sharing | Content URI/MIME share flow works |
| Offline | Core workflow works without network after model setup |
| Security | P0 controls/tests pass |
| Release | All mandatory gates pass |

---

# 40. Processing State Implementation Gates

The source architecture defines a state-driven processing lifecycle. The implementation should validate at least the following conceptual states:

```text
IDLE
 ↓
ACQUIRING
 ↓
VALIDATING
 ↓
PREPROCESSING
 ↓
OCR
 ↓
AI_ANALYSIS
 ↓
EXTRACTING
 ↓
VALIDATING_RESULT
 ↓
READY_FOR_REVIEW
 ↓
SAVING
 ↓
COMPLETED
```

Failure/cancellation exits must be explicit and must not be treated as `COMPLETED`.

Exact enum/state names are implementation details and may differ in the final Android source.

---

# 41. Error Handling Execution Matrix

| Area | Example failure | Required behavior |
|---|---|---|
| Input | Unsupported file | Explain unsupported input; no processing success |
| Acquisition | Page extraction failure | Stop/recover truthfully; preserve prior state |
| Preprocessing | Transform failure | Safe fallback or explicit failure |
| OCR | Empty/unavailable engine | Truthful OCR state; no fabricated text |
| AI | Model unavailable | Model setup/readiness action |
| AI | Resource exhaustion | Safe failure/retry guidance |
| AI | Malformed output | Reject/flag output; never save as trusted result |
| Save | DB failure | Do not report successful save |
| Export | Format failure | Persisted structured result remains intact |
| Share | No compatible target | Explain sharing unavailable; keep file intact |
| Storage | Low/insufficient storage | Stop safely and tell user what failed |
| Cancellation | User cancels | No false completion; clean temporary work |
| Lifecycle | App loses focus | Preserve/restore state according to validated behavior |

---

# 42. Security & Privacy Implementation Gates

Security validation must be integrated at the point of feature implementation rather than postponed until the final week.

## Gate S1 — File Input

```text
[ ] MIME/type validated
[ ] Unsafe files rejected
[ ] Path traversal impossible through user input
[ ] Temporary paths controlled
```

## Gate S2 — Processing

```text
[ ] Core processing remains local after setup
[ ] No unexpected network request for document processing
[ ] Sensitive content absent from ordinary logs
```

## Gate S3 — Persistence

```text
[ ] SQLite mapping integrity verified
[ ] User corrections authoritative
[ ] Orphan handling tested
[ ] No unnecessary secrets/sensitive diagnostics persisted
```

## Gate S4 — Export/Share

```text
[ ] Export reflects current saved data
[ ] Filenames sanitized
[ ] Internal paths are not exposed
[ ] MIME types correct
[ ] Share access limited to intended file
```

## Gate S5 — Release

```text
[ ] Permissions reviewed
[ ] Release artifact scanned/reviewed
[ ] Offline test passed
[ ] Security test suite passed
```

---

# 43. Testing Gates

The testing document defines seven principal quality gates that should control implementation progression.

## Gate 0 — Static Quality

- compile/type checks pass;
- lint/static-analysis baseline is acceptable;
- no unresolved critical tooling failures.

## Gate 1 — Unit/Component

- deterministic core logic covered;
- required component tests pass.

## Gate 2 — Integration

- pipeline boundaries pass;
- persistence mapping passes;
- export handoff passes.

## Gate 3 — AI/OCR

- approved dataset executed;
- no unexplained critical regression;
- thresholds met once finalized.

## Gate 4 — Offline

- core offline workflow passes;
- no unexpected network dependency.

## Gate 5 — E2E

- major P0 workflows pass;
- acceptance suite passes.

## Gate 6 — Security/Privacy

- path/file tests pass;
- no unintended document upload;
- sensitive data not exposed through routine diagnostics.

## Gate 7 — Release Candidate

- compatibility suite passes;
- performance measurements recorded;
- release blockers cleared;
- acceptance sign-off complete.

---

# 44. Release Blocking Criteria

A release must be blocked when any of these conditions is true:

1. The core offline workflow unexpectedly depends on network connectivity.
2. User edits are overwritten after save/reopen/export.
3. Saved data is corrupted or silently lost.
4. An export contains stale/non-authoritative values.
5. Table associations are corrupted in a release-blocking case.
6. A critical privacy/security defect remains.
7. The P0 path consistently crashes on a supported/reference device.
8. SRS acceptance criteria AC-001 through AC-012 are not satisfied without a documented approved exception.
9. A critical OCR/AI regression remains unresolved.
10. Release artifact verification fails.

---

# 45. Definition of Ready

A task is **READY** only when:

```text
[ ] Requirement is understood
[ ] Source-of-truth reference identified
[ ] Dependencies identified
[ ] Acceptance criteria are testable
[ ] Technical approach is sufficiently known
[ ] Required files/tools/resources are available
[ ] No unresolved critical blocker prevents execution
```

A task may still contain a **TBD** implementation detail only when the task explicitly exists to resolve that TBD.

---

# 46. Definition of Done

```text
[ ] Implementation complete
[ ] Code reviewed/self-reviewed against source baseline
[ ] Unit tests pass
[ ] Integration tests pass where applicable
[ ] UI validated where applicable
[ ] Error handling implemented
[ ] Security/privacy considerations addressed
[ ] Documentation updated
[ ] No known critical regression
[ ] Acceptance criteria pass
```

## 46.1 Feature Definition of Done

A feature is complete only when:

```text
Requirements
    +
Implementation
    +
Testing
    +
Error Handling
    +
Security
    +
UX Validation
    +
Documentation
    =
DONE
```

A screen that merely renders, or a pipeline stage that works only on one happy-path sample, is not considered done.

---

# 47. Milestone Roadmap

## M0 — Project Baseline

**Goal:** Establish a verified Android build baseline.

**Deliverables:** inspected generated project, source-control baseline, build/toolchain inventory, debug build, testing structure.

**Dependencies:** actual project availability.

**Exit Criteria:** app builds and launches successfully; exact implementation stack is recorded.

## M1 — Android Shell

**Goal:** Stable application shell and navigation.

**Deliverables:** theme, navigation, common states, placeholder screens.

**Dependencies:** M0.

**Exit Criteria:** core screens navigate without crashes.

## M2 — Document Input

**Goal:** Accept camera, image, and PDF inputs.

**Deliverables:** camera/file paths, validation, acquisition contract.

**Dependencies:** M1.

**Exit Criteria:** valid inputs enter processing.

## M3 — OCR

**Goal:** Produce usable text locally.

**Deliverables:** preprocessing, OCR adapter/integration, regression corpus.

**Dependencies:** M2.

**Exit Criteria:** OCR gate passes.

## M4 — Offline AI

**Goal:** Perform local document analysis.

**Deliverables:** validated model/runtime, model readiness, AI adapter, local inference.

**Dependencies:** M3 + model validation.

**Exit Criteria:** local inference and structured output gate passes.

## M5 — Structured Extraction

**Goal:** Produce validated fields and tables.

**Deliverables:** classification, fields, tables, confidence, canonical data.

**Dependencies:** M4.

**Exit Criteria:** representative corpus produces schema-valid results.

## M6 — Review/Edit

**Goal:** Make extracted data user-authoritative.

**Deliverables:** results UI, field editor, table editor, validation, save/cancel behavior.

**Dependencies:** M5.

**Exit Criteria:** correction-integrity test passes.

## M7 — SQLite/History

**Goal:** Durable local data lifecycle.

**Deliverables:** schema/migrations, persistence, file storage, history, reopen/delete.

**Dependencies:** M6.

**Exit Criteria:** data survives restart and history operations.

## M8 — Export

**Goal:** Export authoritative structured data.

**Deliverables:** export engine, Excel, CSV, JSON, PDF, sharing.

**Dependencies:** M7.

**Exit Criteria:** all export acceptance tests pass.

## M9 — Security

**Goal:** Harden the local application and data boundaries.

**Deliverables:** permissions, file safety, logging controls, AI output validation, offline/network audit.

**Dependencies:** working product.

**Exit Criteria:** security/privacy gate passes.

## M10 — Full Testing

**Goal:** Complete system validation.

**Deliverables:** unit/integration/UI/E2E/OCR/AI/database/export/security/performance/offline test evidence.

**Dependencies:** M8 + M9.

**Exit Criteria:** no release-blocking defects.

## M11 — Release Candidate

**Goal:** Freeze and validate a production candidate.

**Deliverables:** release build, signed candidate, artifact metadata, regression report, security/offline/performance evidence.

**Dependencies:** M10.

**Exit Criteria:** release gates approved.

## M12 — Production Release

**Goal:** Produce and distribute the approved release artifact.

**Deliverables:** production artifact, release notes, checksums/metadata, post-release smoke evidence.

**Dependencies:** M11.

**Exit Criteria:** artifact is successfully distributed through the selected channel and passes post-release verification.

---

# 48. MVP Demo Milestone

The minimum complete demonstration must prove the entire central transformation, not just isolated UI screens.

## Demo Flow

```text
Launch SnapData
      ↓
Import supported image/PDF
      ↓
Acquire / preview
      ↓
Preprocess
      ↓
OCR
      ↓
Offline AI
      ↓
Document type + fields + table
      ↓
Confidence / warnings where available
      ↓
Review
      ↓
Edit one or more values
      ↓
Save
      ↓
Reopen from History
      ↓
Verify edits remain authoritative
      ↓
Export JSON
      ↓
Export CSV
      ↓
Export Excel
      ↓
Export PDF
      ↓
Share one export
      ↓
Repeat core path with network disabled
```

## Demo Exit Criteria

```text
[ ] End-to-end path completes
[ ] User correction survives reopen
[ ] Exports match saved data
[ ] No unexpected network dependency after model setup
[ ] No critical crash
```

The testing baseline also recommends using a deterministic synthetic document for a repeatable demonstration/viva flow.

---

# 49. Parallel Development Plan

## Stream A — Frontend

Can begin immediately after M0/M1:

- navigation;
- shell;
- Home;
- Camera UI;
- import UI;
- preview;
- processing;
- results;
- editor;
- history;
- export;
- settings.

## Stream B — Processing

Begins once input contracts are known:

- acquisition;
- preprocessing;
- OCR;
- AI;
- classification;
- field extraction;
- table extraction;
- confidence;
- structured model.

## Stream C — Persistence

Begins after canonical schema mapping is stable:

- SQLite initialization;
- migrations;
- persistence mapping;
- file storage;
- history;
- deletion.

## Stream D — Export

Can begin against the canonical structured-data model before full UI completion:

- export contract;
- JSON;
- CSV;
- Excel;
- PDF;
- file naming;
- artifact validation.

## Stream E — Quality

Runs continuously:

- unit tests;
- corpus creation;
- integration tests;
- offline tests;
- security tests;
- performance measurements;
- regression tests.

---

# 50. Blocker Register

| Blocker ID | Potential Blocker | Why It Matters | Trigger | Resolution Path | Status |
|---|---|---|---|---|---|
| BLOCK-001 | Generated Android project unavailable | Exact implementation stack cannot be verified | No source/build artifact | Obtain project source and inspect | OPEN |
| BLOCK-002 | AI model/runtime undefined | Core offline pipeline cannot be finalized | Runtime/model not validated | Benchmark candidate implementation(s) | OPEN |
| BLOCK-003 | OCR integration mismatch | OCR is upstream of AI | Runtime incompatibility | Validate OCR adapter on target project | OPEN |
| BLOCK-004 | Android toolchain mismatch | Build cannot proceed | SDK/plugin/dependency conflict | Align project using actual generated config | OPEN |
| BLOCK-005 | Native dependency incompatibility | Could block build/runtime | ABI/native build issue | Validate device/ABI/dependency support | OPEN |
| BLOCK-006 | Model memory pressure | Could crash on target device | Benchmark exceeds available resources | Re-evaluate model/runtime/resource strategy | OPEN |
| BLOCK-007 | Model storage size | Affects setup and local storage | Model too large | Validate packaging/storage strategy | OPEN |
| BLOCK-008 | PDF page extraction issue | Blocks multi-page processing | Unsupported/malformed PDF | Validate chosen implementation/library | OPEN |
| BLOCK-009 | Table extraction failure | Core P0 capability | Structural accuracy below acceptable level | Refine extraction contract/validation | OPEN |
| BLOCK-010 | Excel compatibility issue | Export requirement | Workbook generation fails on reference viewers | Validate library/format | OPEN |
| BLOCK-011 | SQLite migration issue | Can corrupt saved data | Migration failure | Test all supported schema versions | OPEN |
| BLOCK-012 | Release signing failure | Blocks distribution | Production signing/config issue | Validate signing setup | OPEN |

---

# 51. Implementation Risk Register

| Risk ID | Risk | Likelihood | Impact | Mitigation | Owner | Status |
|---|---|---|---|---|---|---|
| IMP-RISK-001 | AI model integration complexity | High | Critical | Validate runtime/model before broad implementation; isolate adapter | AI/OCR | OPEN |
| IMP-RISK-002 | OCR accuracy insufficient | High | High | Build representative corpus; measure regression early | AI/OCR + QA | OPEN |
| IMP-RISK-003 | Android compatibility/toolchain mismatch | Medium | High | Inspect generated project first; validate reference devices | Build | OPEN |
| IMP-RISK-004 | Memory pressure during OCR+AI | High | Critical | Measure peak memory; release intermediates; validate device matrix | Processing | OPEN |
| IMP-RISK-005 | AI model size too large | Medium | High | Measure package/storage footprint; validate model strategy | AI/OCR | OPEN |
| IMP-RISK-006 | PDF parsing/page extraction gaps | Medium | High | Test varied PDFs; define supported corpus/limits | Processing | OPEN |
| IMP-RISK-007 | Table extraction structural errors | High | Critical | Use ground-truth tables; compare rows/columns/cells | AI/OCR + QA | OPEN |
| IMP-RISK-008 | Export formatting/fidelity defects | Medium | High | Cross-format consistency tests; artifact validation | Export | OPEN |
| IMP-RISK-009 | SQLite migration defects | Medium | Critical | Versioned migrations + upgrade tests | Database | OPEN |
| IMP-RISK-010 | Release build failure | Medium | Critical | Run release builds before final freeze; validate signing | Build/Release | OPEN |
| IMP-RISK-011 | User edits overwritten | Medium | Critical | Mandatory correction-integrity test and authoritative-data rule | Core | OPEN |
| IMP-RISK-012 | Unexpected network dependency | Medium | Critical | Airplane-mode test and network audit | QA/Security | OPEN |
| IMP-RISK-013 | Temporary file leakage | Medium | High | Lifecycle cleanup tests and storage audits | Security | OPEN |
| IMP-RISK-014 | Sensitive content in logs | Medium | High | Logging policy, review, test assertions | Security | OPEN |
| IMP-RISK-015 | Export path/share boundary leaks | Medium | High | Content URI/MIME testing | Android | OPEN |

---

# 52. Technical Decision Register

| Decision ID | Topic | Current Status | Implementation Rule |
|---|---|---|---|
| DEC-001 | Platform | CONFIRMED | Android application |
| DEC-002 | Build starting point | CONFIRMED | Google AI Studio “Build an Android app” workflow |
| DEC-003 | Core architecture | CONFIRMED | Offline-first/local processing |
| DEC-004 | Backend | REJECTED for MVP | Do not create backend tasks |
| DEC-005 | REST API | REJECTED for MVP | Use local component interfaces |
| DEC-006 | Cloud database | REJECTED for MVP | Use local SQLite |
| DEC-007 | SQLite | CONFIRMED source-backed | Exact Android implementation TBD/validation |
| DEC-008 | OCR context | Tesseract source-backed | Exact integration requires validation |
| DEC-009 | AI model | TBD | Must benchmark/validate before freeze |
| DEC-010 | AI runtime | TBD / validation | Must be replaceable behind boundary |
| DEC-011 | UI toolkit | Validation required | Derive from actual project |
| DEC-012 | Programming language | Validation required | Derive from actual project |
| DEC-013 | Exact Android architecture pattern | PROPOSED | Use logical layer boundaries; adapt to actual project |
| DEC-014 | Export abstraction | PROPOSED/architecture-aligned | Common contract + format adapters |
| DEC-015 | Canonical data model | CONFIRMED | All downstream consumers use it |
| DEC-016 | User-corrected result | CONFIRMED | Authoritative for persistence/export |
| DEC-017 | Encryption at rest | TBD | No claim until implemented/tested |
| DEC-018 | PIN/biometric lock | TBD | No claim until product decision/implementation |
| DEC-019 | Model update/delete | TBD | Not mandatory until finalized |
| DEC-020 | CI/CD provider | TBD | Provider-neutral build process |
| DEC-021 | Distribution channel | TBD | Decide before production release |

---

# 53. Traceability from Requirements to Implementation

| Requirement Area | Source Authority | Implementation Phases | Validation Evidence |
|---|---|---|---|
| Android app | PRD/TRD/Architecture | P0-P1 | Build/install evidence |
| Camera/PDF/Image input | PRD/SRS/UI/Architecture | P3-P4 | Input tests |
| Preprocessing | SRS/TRD/AI-OCR/Document Processing | P5 | Preprocessing component tests |
| OCR | SRS/TRD/AI-OCR | P6 | OCR corpus/metrics |
| Offline AI | PRD/SRS/TRD/AI-OCR | P7 | Offline AI benchmark |
| Classification | PRD/SRS/AI-OCR | P8 | Classification tests |
| Fields | PRD/SRS/Data Schema/AI-OCR | P9/P11 | Extraction tests |
| Tables | PRD/SRS/Data Schema/AI-OCR | P10/P11 | Table structural metrics |
| Confidence | PRD/SRS/UI/AI-OCR | P9/P10/P11 | Confidence tests |
| Review/Edit | SRS/UI/Frontend | P12 | Correction integrity |
| SQLite | PRD/SRS/TRD/Database | P13 | Migration/integrity tests |
| History | PRD/SRS/Frontend/Database | P14 | History E2E |
| Excel | PRD/SRS/Export | P15-P16 | XLSX artifact tests |
| CSV | PRD/SRS/Export | P15/P17 | CSV parser tests |
| JSON | PRD/SRS/Data Schema/Export | P15/P18 | JSON schema tests |
| PDF | PRD/SRS/Export | P15/P19 | PDF artifact validation |
| Sharing | SRS/Architecture/Export | P20 | Android share tests |
| Security/privacy | SRS/Security/Architecture | P22-P25 | Security/privacy evidence |
| Offline operation | PRD/SRS/TRD/Testing/Build Release | P7/P23/P25 | Airplane-mode evidence |
| Release | Build Release/Testing/Security | P25/P26 | RC/release evidence |

---

# 54. MVP Scope Matrix

| Feature | P0 | P1 | Future | Notes |
|---|:---:|:---:|:---:|---|
| Camera scan | ✓ | | | Core input |
| PDF import | ✓ | | | Core input |
| Image import | ✓ | | | Core input |
| Multi-page PDF | ✓ target | | | Exact limits/behavior require validation |
| Preprocessing | ✓ | | | Core pipeline |
| OCR | ✓ | | | Tesseract source-backed context |
| Offline AI | ✓ | | | Runtime/model TBD |
| Document type detection | ✓ | | | |
| Key-value extraction | ✓ | | | |
| Table extraction | ✓ | | | |
| Confidence information | ✓ | | | Where pipeline produces it |
| User review/edit | ✓ | | | |
| SQLite persistence | ✓ | | | |
| History/reopen | ✓ | | | |
| Search | | ✓ | | Include if schedule permits; PRD/SRS preserve it as a product interaction area |
| Excel | ✓ | | | |
| CSV | ✓ | | | |
| JSON | ✓ | | | |
| PDF | ✓ | | | |
| Android sharing | ✓ | | | |
| Dark/light theme | | ✓ | | Settings scope |
| OCR language selection | | ✓/TBD | | Language list not finalized |
| AI summary | | ✓/TBD | | Not required for core pipeline |
| Batch processing | | ✓ | | Defer |
| AI chat | | | ✓ | Future |
| Smart templates | | ✓ | ✓ | Requires product decision |
| Handwriting | | | ✓ | |
| Voice commands | | | ✓ | |
| Cloud sync | | | ✓ | Not allowed as MVP dependency |
| Collaboration | | | ✓ | |

---

# 55. Engineering Execution Order

The recommended implementation order is:

### Stage A — Establish the Build

M0 → verified Android project, source control, build/test baseline.

### Stage B — Build the UI Shell

M1 → navigation, common states, shell, placeholder screens.

### Stage C — Prove Inputs

M2 → camera/image/PDF → normalized acquisition data.

### Stage D — Prove OCR

M3 → preprocessing → OCR → regression evidence.

### Stage E — Prove AI

M4 → validated local runtime → structured AI output.

### Stage F — Prove Structured Data

M5 → classification + fields + tables + confidence → canonical model.

### Stage G — Prove Human Control

M6 → review/edit → authoritative saved result.

### Stage H — Prove Durability

M7 → SQLite + local files → history/reopen/delete.

### Stage I — Prove Portability

M8 → JSON/CSV/XLSX/PDF → Android sharing.

### Stage J — Harden

M9 → security/privacy + resource controls + failure behavior.

### Stage K — Certify

M10 → full tests + offline + performance + compatibility.

### Stage L — Ship

M11/M12 → release candidate → production artifact → post-release smoke.

---

# 56. What Should NOT Be Implemented Yet

The following should not consume MVP implementation capacity unless a documented scope change is approved:

1. Backend server architecture.
2. REST APIs for core processing.
3. Cloud database.
4. Cloud OCR/AI document processing.
5. Team collaboration.
6. Cloud synchronization.
7. AI chat about documents.
8. Voice controls.
9. Handwriting recognition.
10. Large-scale document automation workflows.
11. Advanced organizational features such as folders/tags/favorites unless explicitly promoted.
12. Encryption or biometric claims without verified implementation.
13. Exact device-performance guarantees before benchmarks exist.

This prevents scope creep and preserves engineering focus on the P0 pipeline.

---

# 57. Build & Release Integration Plan

The implementation plan and release document are coupled at the following checkpoints:

| Development Point | Release/Build Evidence |
|---|---|
| After M0 | Debug build works |
| After M3 | OCR resources/integration validated |
| After M4 | AI model/resource readiness validated |
| After M7 | Database migrations verified |
| After M8 | Export artifacts verified |
| After M9 | Security/privacy review started/complete |
| M10 | Full regression and performance report |
| M11 | Release candidate artifact |
| M12 | Production artifact + checksum + smoke test |

Release engineering already requires traceability to application version, build, commit, model/OCR versions where applicable, artifact, and checksum. The implementation plan therefore treats those values as release evidence rather than hard-coding them prematurely.

---

# 58. Documentation Synchronization Rules

When implementation changes a confirmed requirement or technical decision:

```text
Code change
   ↓
Identify affected source document
   ↓
Update technical/product document if required
   ↓
Update implementation plan status/dependency
   ↓
Update tests
   ↓
Update release evidence
```

The implementation plan must never become a shadow architecture. Architecture remains in the architecture/TRD documents; detailed test cases remain in the testing document; exact database schema remains in the database/data-schema documents; exact AI/OCR implementation remains in AI/OCR; and build/signing specifics remain in Build & Release.

---

# 59. Evidence Requirements

Every completed milestone should produce evidence appropriate to its risk.

## Code Evidence

- source commit;
- review/check record;
- build output;
- static-analysis output.

## Functional Evidence

- screenshots or screen recordings where useful;
- E2E results;
- representative processed documents.

## Data Evidence

- SQLite integrity/migration results;
- JSON validation;
- exported artifact samples.

## AI/OCR Evidence

- dataset version;
- benchmark results;
- measured latency/memory;
- regression comparison.

## Security Evidence

- permission review;
- network/offline audit;
- file/path tests;
- logging review.

## Release Evidence

- signed artifact;
- checksum;
- version/build metadata;
- release smoke test;
- approval record.

Never use private production documents as routine release evidence when synthetic or approved test data can demonstrate the same behavior.

---

# 60. Exit Criteria for the Entire MVP

The SnapData MVP is ready for production release only when **all** of the following are true:

```text
PRODUCT
[ ] MVP scope implemented
[ ] No unresolved product ambiguity on release-blocking behavior

APPLICATION
[ ] App builds cleanly
[ ] App launches
[ ] Navigation stable
[ ] Core states handled

INPUT
[ ] Camera works
[ ] PDF import works
[ ] Image import works
[ ] Multi-page behavior validated

PROCESSING
[ ] Preprocessing validated
[ ] OCR validated
[ ] Offline AI validated
[ ] Classification validated
[ ] Fields validated
[ ] Tables validated
[ ] Confidence/warnings handled

DATA
[ ] Canonical structured model validated
[ ] User edits authoritative
[ ] SQLite persistence validated
[ ] Migrations validated
[ ] History/reopen/delete validated

EXPORT
[ ] Excel validated
[ ] CSV validated
[ ] JSON validated
[ ] PDF validated
[ ] Sharing validated

QUALITY
[ ] Unit tests pass
[ ] Integration tests pass
[ ] UI/E2E tests pass
[ ] Offline tests pass
[ ] Security/privacy tests pass
[ ] Performance measured
[ ] Compatibility validated

RELEASE
[ ] Release candidate passes all quality gates
[ ] Signing verified
[ ] Artifact integrity verified
[ ] Release blocking defects cleared
[ ] Production smoke test passes
```

---

# 61. Final Implementation Baseline

SnapData should be developed as a **local-first Android document-processing product whose core engineering value is the complete, validated pipeline rather than any individual screen or AI demonstration**.

The implementation baseline is:

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
Classification + Extraction
   ↓
Confidence / Validation
   ↓
Canonical Structured Data
   ↓
User Review/Edit
   ↓
SQLite + Local Files
   ↓
History
   ↓
Excel / CSV / JSON / PDF
   ↓
Android Sharing
```

The most important engineering constraint is that **the pipeline must be made real, testable, and offline-valid before the project spends significant effort on optional features**.

The plan deliberately does not invent an Android framework, AI model, AI runtime, preprocessing algorithm, storage API, export library, performance threshold, encryption mechanism, or device specification where the authoritative sources have not finalized those choices. Those items remain clearly marked **TBD**, **PROPOSED**, or **REQUIRES TECHNICAL VALIDATION** until implementation evidence exists.

---

# 62. Source Alignment Notes

## PRD

The PRD establishes the product as an Android/mobile application that converts PDFs/images into structured, editable data using OCR and AI; it defines the core MVP sequence, P0 capabilities, document categories, offline-first direction, local storage, history, exports, and explicit TBDs such as exact AI runtime/model, supported devices, maximum document size, OCR language list, validation dataset, and advanced security options.

## SRS

The SRS defines the behavior boundary from document input and acquisition through preprocessing, OCR, offline AI, structured data, review/edit, local storage, history, export, sharing, model management, settings, error handling, security/privacy, accessibility, performance, data validation, and state management. It explicitly leaves concrete implementation details to technical artifacts.

## TRD

The TRD confirms Android via Google AI Studio's **Build an Android app** workflow, offline-first/local processing, SQLite as the source-backed local persistence direction, Tesseract as source-backed OCR context, and no backend/REST API requirement for the current MVP. Exact Android language/toolkit, model/runtime, dependencies, device requirements, and implementation APIs remain validation items.

## System Architecture

The architecture establishes presentation/application/domain/processing/OCR/AI/persistence/file-storage/export boundaries, local processing, canonical structured data, processing-state management, cancellation/error handling, user-authoritative saved data, and no required backend/API for MVP.

## UI/UX and Frontend

The UI/UX and frontend baselines define the screen inventory, navigation, processing states, results, editor, history, export, settings, empty/error/loading states, accessibility, and Android lifecycle behavior. This plan converts those into implementation phases without reproducing the design specification.

## Database and Data Schema

The database/data-schema baselines define SQLite/local file responsibilities, relational persistence, canonical structured data, migration/integrity requirements, and the rule that user-corrected saved data is authoritative. This plan places persistence after the canonical model and review contract are stable.

## AI/OCR and Document Processing

The AI/OCR and document-processing baselines define acquisition, preprocessing, OCR, AI inference, classification, field/table extraction, confidence handling, resource management, cancellation, offline behavior, and validation as separate technical concerns. Exact runtime/model/algorithm choices remain validation items.

## Export

The export baseline defines a common export abstraction and requires Excel/CSV/JSON/PDF exporters to consume the canonical saved structured result. This plan intentionally implements the common contract before format-specific adapters.

## Testing

The testing baseline requires unit/component/integration/UI/E2E, OCR/AI, database, export, security/privacy, offline, performance, lifecycle, and compatibility validation, with explicit release gates and release-blocking conditions.

## Security/Privacy

The security baseline requires local-first processing, safe file handling, path security, sensitive-log minimization, AI-output validation, data integrity, and safe export/share boundaries, while intentionally avoiding unsupported claims about encryption, app lock, secure delete, or cryptographic key management.

## Build/Release

The build/release baseline requires controlled debug/release builds, versioning, signing, AI/OCR resource validation, offline validation, artifact verification, release candidate gates, production smoke testing, and traceable release artifacts.

## Original Project Specification and Workflow Diagram

The original project specification describes SnapData as an AI-powered mobile application that converts PDF documents and images into structured, editable data using OCR and AI, with camera/file acquisition, local/offline operation after initial AI model setup, SQLite local storage, and Excel/CSV/JSON/PDF export. The workflow diagram on page 2 visually sequences Start → Launch → Document Input → Document Acquisition → Image Pre-processing → OCR Processing → Offline AI Processing → Structured Data Generation → User Review & Editing → Local Storage → Export Module → Document History → End, and includes source-era technology labels in its footer.

---

# 63. Appendix A — Quick Engineering Checklist

## Before Coding

```text
[ ] Actual generated Android project inspected
[ ] Toolchain recorded
[ ] AI model/runtime decision or validation task created
[ ] OCR integration validation task created
[ ] Canonical schema reviewed
[ ] Test corpus identified
[ ] Build succeeds
```

## Before Processing Integration

```text
[ ] Acquisition contract stable
[ ] Preprocessing contract stable
[ ] OCR adapter stable
[ ] AI adapter stable
[ ] Error/state contract stable
```

## Before Persistence

```text
[ ] Canonical structured model stable
[ ] User-edit authority rule implemented
[ ] Database mapping reviewed
[ ] Migration strategy validated
```

## Before Export

```text
[ ] Saved structured result is authoritative
[ ] Export contract stable
[ ] JSON serialization validated
[ ] Excel/CSV/PDF strategies validated
```

## Before Release

```text
[ ] P0 acceptance tests pass
[ ] Offline test passes
[ ] Security test passes
[ ] Performance measured
[ ] Release build passes
[ ] Artifact signed/verified
[ ] Smoke test passes
```

---

# 64. Appendix B — Core Acceptance Scenario Set

### AC-001 — Acquire

Supported image/PDF can be acquired from camera/file input and reaches processing.

### AC-002 — Preprocess

Preprocessing runs and either provides a valid normalized image or a truthful recoverable failure.

### AC-003 — OCR

OCR provides usable text or a truthful empty/failure state.

### AC-004 — AI Extraction

With AI ready, supported documents produce document type/fields/tables and available confidence information.

### AC-005 — Review

User can inspect extracted results before export.

### AC-006 — Edit Persistence

User edits a field/table, saves, closes/reopens, and the correction remains.

### AC-007 — Export

Saved results export to XLSX/CSV/JSON/PDF and reflect current values.

### AC-008 — Sharing

A generated export can be shared/opened safely, or a truthful failure is displayed.

### AC-009 — History

History lists saved records; reopen and delete work as defined.

### AC-010 — Offline

After model setup, the core processing path succeeds without network access.

### AC-011 — Model Unavailable

Without a ready model, processing does not pretend to succeed and directs the user to setup/readiness behavior.

### AC-012 — Failure Handling

Injected P0 processing/export/storage failures produce truthful failures and preserve previously saved authoritative data.

---

# 65. Appendix C — Change Control

Any requested feature or technical change during implementation must answer:

1. Does it change MVP scope?
2. Does it change a confirmed requirement?
3. Does it change an architecture boundary?
4. Does it introduce a backend/API dependency?
5. Does it affect the canonical data model?
6. Does it affect security/privacy guarantees?
7. Does it affect export compatibility?
8. Does it require new tests?
9. Does it change release criteria?

A change that introduces a new mandatory backend, API, cloud-processing path, or cloud database is not an ordinary implementation task; it requires an explicit product/technical baseline change.

---

# 66. Final Status Summary

| Area | Baseline Status |
|---|---|
| Product scope | CONFIRMED from PRD/SRS |
| Android target | CONFIRMED |
| Google AI Studio starting workflow | CONFIRMED |
| Offline-first direction | CONFIRMED |
| Core local document processing | CONFIRMED |
| SQLite local persistence | CONFIRMED source-backed |
| Camera/PDF/Image input | CONFIRMED |
| OCR capability | CONFIRMED; exact integration validation required |
| Offline AI | CONFIRMED capability; model/runtime TBD/validation |
| Canonical structured data | CONFIRMED concept/contract |
| User corrections authoritative | CONFIRMED |
| Excel/CSV/JSON/PDF | CONFIRMED |
| Backend for MVP | REJECTED / NOT REQUIRED |
| REST API for MVP | REJECTED / NOT REQUIRED |
| Cloud database for MVP | REJECTED / NOT REQUIRED |
| Exact Android language | REQUIRES TECHNICAL VALIDATION |
| Exact UI toolkit | REQUIRES TECHNICAL VALIDATION |
| Exact AI model | TBD |
| Exact AI runtime | TBD / REQUIRES TECHNICAL VALIDATION |
| Exact preprocessing algorithms | REQUIRES TECHNICAL VALIDATION |
| Exact device matrix | TBD |
| Exact performance thresholds | TBD |
| Exact export libraries | TBD / REQUIRES TECHNICAL VALIDATION |
| Encryption/app lock/secure delete | TBD; no unverified claims |
| Actual implementation status | TODO unless proven otherwise |

---

# 67. Document References

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
15. Original SnapData project specification PDF
16. SnapData workflow diagram
17. Actual Google AI Studio-generated Android project, when supplied/accessible

---

**End of `SnapData_IMPLEMENTATION_PLAN_v1.0.md`**
