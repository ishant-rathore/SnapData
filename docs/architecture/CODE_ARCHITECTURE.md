# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Code Architecture, Project Structure & Engineering Coding Standard

**Document:** `SnapData_CODE_ARCHITECTURE_v1.0.md`  
**Version:** 1.0  
**Status:** Engineering Baseline  
**Date:** 30 August 2026  
**Implementation Target:** Android application developed from the Google AI Studio **Build an Android app** workflow  
**Primary Architecture:** Modular, layered, offline-first/local-processing architecture  
**MVP Backend:** None required  
**MVP REST API:** None required  
**MVP Cloud Database:** None required  

> **Implementation verification rule:** The exact Android programming language, UI toolkit, namespace/application ID, module structure, build plugins, dependency set, OCR integration, AI runtime, file-storage API, and test stack must be taken from the actual generated Android project before being marked **CONFIRMED**. The accessible project materials did not contain the generated Android source tree; the connected `ishant-rathore/Snapdata` GitHub repository is currently empty. Therefore, this document deliberately separates **source-backed architectural decisions** from **implementation details that still require validation**.

---

# 1. Document Control

| Item | Value |
|---|---|
| Project | SnapData |
| Document | Code Architecture, Project Structure & Engineering Coding Standard |
| Version | 1.0 |
| Status | Engineering Baseline |
| Date | 30 August 2026 |
| Platform | Android |
| Development starting point | Google AI Studio — Build an Android app |
| Core behavior | Offline-first document processing |
| Local database | SQLite — source-backed requirement |
| OCR | Tesseract OCR — source-backed context; Android integration requires validation |
| AI | On-device/offline AI capability — confirmed; exact model/runtime TBD |
| Core backend | None |
| REST API | None for MVP |
| Cloud database | None for MVP |
| Exact generated stack | Requires technical validation |

## 1.1 Status Vocabulary

- **CONFIRMED** — established by supplied source material or directly verified in implementation.
- **PROPOSED** — recommended engineering direction; not a fact about the existing implementation.
- **TBD** — decision has not been finalized.
- **REQUIRES TECHNICAL VALIDATION** — intent is known but exact feasibility/compatibility/performance must be verified.
- **OPTIONAL** — permitted enhancement, not required for the baseline.
- **REJECTED** — explicitly excluded from the current MVP architecture.

## 1.2 Implementation Status Vocabulary

- **TODO** — not started.
- **IN PROGRESS** — implementation underway.
- **BLOCKED** — cannot proceed because a dependency or decision is unresolved.
- **DONE** — acceptance criteria and Definition of Done are satisfied.
- **DEFERRED** — intentionally postponed.

---

# 2. Purpose

This document defines how SnapData source code SHALL be organized, implemented, tested, reviewed, evolved, and maintained.

It bridges:

```text
Product Requirements
        ↓
Software Requirements
        ↓
Technical Requirements
        ↓
System Architecture
        ↓
Code Architecture
        ↓
Source Code
        ↓
Tests
        ↓
Build
        ↓
Release
```

The objective is a codebase that is easy to understand, hard to misuse, safe to extend, and resistant to the common failure mode of document-processing systems: UI, OCR, AI, storage, and export logic becoming tightly coupled inside screens.

---

# 3. Source-of-Truth and Alignment Policy

The implementation SHALL follow this authority order:

```text
1. SnapData_PRD_v1.0.md
2. SnapData_SRS_v1.0.md
3. SnapData_TRD_v1.0.md
4. SnapData_SYSTEM_ARCHITECTURE_v1.0.md
5. SnapData_UI_UX_v1.0.md
6. SnapData_FRONTEND_v1.0.md
7. SnapData_DATABASE_v1.0.md
8. SnapData_DATA_SCHEMA_v1.0.md
9. SnapData_AI_OCR_v1.0.md
10. SnapData_DOCUMENT_PROCESSING_v1.0.md
11. SnapData_EXPORT_v1.0.md
12. SnapData_TESTING_v1.0.md
13. SnapData_SECURITY_PRIVACY_v1.0.md
14. SnapData_BUILD_RELEASE_v1.0.md
15. SnapData_IMPLEMENTATION_PLAN_v1.0.md
16. SnapData_API_SPECIFICATION_v1.0.md
17. Original project specification / workflow diagram
18. Actual generated Android project — implementation authority when available
```

Lower-level code MAY refine implementation details, but it MUST NOT silently contradict a higher-level requirement.

---

# 4. Architectural Objectives

The codebase SHALL provide:

1. Clear separation of concerns.
2. Strong boundaries between UI, application logic, domain concepts, infrastructure, and platform APIs.
3. Testability of major components without requiring a physical document or device for every test.
4. Offline-first operation for the core processing path after required model setup.
5. Replaceable OCR implementation.
6. Replaceable AI runtime/model implementation.
7. Replaceable export implementations.
8. Isolated persistence.
9. Secure file handling.
10. Predictable state management.
11. Minimal coupling and controlled dependencies.
12. Explicit cancellation and failure semantics.
13. Preservation of user corrections.
14. Canonical structured-data contracts shared by persistence and export.
15. Deterministic build and release behavior.

These objectives align with the system architecture's modular boundaries and the implementation plan's emphasis on evidence-backed, incremental engineering rather than unverified technology assumptions.

---

# 5. Architectural Style

## 5.1 Baseline Style

SnapData SHALL use a **layered modular architecture** with dependency direction toward stable application/domain contracts.

Conceptual structure:

```text
┌──────────────────────────────────────────────┐
│ Presentation                                 │
│ Screens • UI Components • Navigation • State │
└──────────────────────────┬───────────────────┘
                           ↓
┌──────────────────────────────────────────────┐
│ Application / Use Cases                       │
│ User-oriented workflows and orchestration    │
└──────────────────────────┬───────────────────┘
                           ↓
┌──────────────────────────────────────────────┐
│ Domain                                       │
│ Entities • Value Objects • Contracts         │
└──────────────────────────┬───────────────────┘
                           ↑
            ┌──────────────┴──────────────┐
            │                             │
┌───────────┴────────────┐  ┌─────────────┴────────────┐
│ Data / Persistence      │  │ Infrastructure / Runtime │
│ SQLite • Files • Maps   │  │ OCR • AI • Camera • OS  │
└─────────────────────────┘  └──────────────────────────┘

Export is a provider boundary consuming canonical structured data.
```

## 5.2 Feature Organization Decision

**Recommended approach: hybrid architecture.**

- Use **feature boundaries** at the presentation/application entry points.
- Use **layered boundaries** for shared domain, data, processing, export, and platform code.
- Do not allow feature folders to become independent mini-applications with duplicated domain models.

This is **PROPOSED** because the actual generated project structure is not available for inspection.

---

# 6. Non-Negotiable Architectural Rules

1. UI code SHALL NOT access SQLite directly.
2. UI code SHALL NOT call OCR engine classes directly.
3. UI code SHALL NOT call AI runtime classes directly.
4. UI code SHALL NOT generate Excel/CSV/JSON/PDF output directly.
5. Domain code SHALL NOT depend on Android UI classes.
6. Domain code SHALL NOT depend on a specific OCR engine.
7. Domain code SHALL NOT depend on a specific AI runtime.
8. Infrastructure adapters SHALL implement stable interfaces rather than leak provider objects upward.
9. AI output SHALL be validated before it becomes a structured application result.
10. Raw AI output SHALL never be treated as authoritative merely because parsing succeeded.
11. User-confirmed saved data SHALL be authoritative over earlier AI/OCR output.
12. Core document processing SHALL NOT contain a hidden cloud fallback.
13. Model binaries SHALL NOT be stored inside SQLite.
14. Sensitive document content SHALL NOT be written to normal logs.
15. Temporary files SHALL have explicit lifecycle and cleanup behavior.
16. Long-running processing SHALL NOT block the UI thread.
17. Cancellation SHALL be represented truthfully; cancelled work SHALL NOT be reported as success.
18. Exporters SHALL consume canonical structured data rather than querying SQLite independently.
19. Backend/REST dependencies SHALL NOT be introduced into MVP without a baseline change.
20. New dependencies SHALL solve a validated requirement and be reviewed for impact.

---

# 7. Actual Generated Android Project Inspection

## 7.1 Inspection Result

**Status: REQUIRES TECHNICAL VALIDATION**

The current source set contains detailed project specifications but no Gradle project, AndroidManifest, Android source tree, dependency catalog, or generated Android build artifact.

The connected GitHub repository `ishant-rathore/Snapdata` is currently empty, so it cannot be used as implementation evidence.

Therefore the following values remain unresolved until the generated project is supplied:

| Concern | Status | Required Evidence |
|---|---|---|
| Programming language | REQUIRES TECHNICAL VALIDATION | `.kt`, `.java`, or actual source files |
| UI framework | REQUIRES TECHNICAL VALIDATION | UI source + dependencies |
| Namespace | REQUIRES TECHNICAL VALIDATION | Gradle namespace/build config |
| Application ID | REQUIRES TECHNICAL VALIDATION | DefaultConfig/build config |
| Module structure | REQUIRES TECHNICAL VALIDATION | Gradle settings/project tree |
| Android SDK levels | REQUIRES TECHNICAL VALIDATION | Build configuration |
| Build system | REQUIRES TECHNICAL VALIDATION | Gradle/project files |
| Dependency versions | REQUIRES TECHNICAL VALIDATION | Version catalog/build files |
| OCR implementation | REQUIRES TECHNICAL VALIDATION | Dependencies/source/resources |
| AI runtime/model | TBD / REQUIRES TECHNICAL VALIDATION | Model manager/runtime integration |
| File storage API | REQUIRES TECHNICAL VALIDATION | Platform/data code |
| Camera implementation | REQUIRES TECHNICAL VALIDATION | Camera source/dependencies |
| Test stack | REQUIRES TECHNICAL VALIDATION | Test source/build config |

## 7.2 Inspection Gate

Before the exact source-tree section is promoted from **PROPOSED** to **CONFIRMED**, inspect at minimum:

```text
settings.gradle / settings.gradle.kts
build.gradle / build.gradle.kts
app/build.gradle / app/build.gradle.kts
AndroidManifest.xml
src/main source tree
src/test source tree
src/androidTest source tree
resources/assets
model resources/configuration
gradle/libs.versions.toml (if present)
proguard/r8 configuration (if present)
existing navigation/state architecture
```

---

# 8. Root Package and Naming Strategy

## 8.1 Root Package

**Exact root package:** `REQUIRES TECHNICAL VALIDATION`

The implementation SHALL use the namespace/application package already established by the generated project unless it is intentionally corrected through project-level change control.

The source tree SHALL NOT invent a second root namespace.

Fallback convention, only if the generated project has no valid namespace and a new one must be assigned:

```text
com.<organization-or-owner>.snapdata
```

A concrete value such as `com.snapdata.app` is **PROPOSED ONLY** and MUST NOT be treated as the current package name.

## 8.2 Naming Rules

| Artifact | Rule | Example |
|---|---|---|
| Package/namespace | lowercase, stable, semantic | `...snapdata.processing` |
| Class/type | PascalCase | `DocumentProcessingCoordinator` |
| Function/method | camelCase, verb-oriented | `startDocumentProcessing()` |
| Variable/property | camelCase | `processingStatus` |
| Constants | language/toolchain idiomatic constant style | `MAX_PAGE_SIZE` where appropriate |
| Enum values | stable semantic identifiers | `OCR_PROCESSING` |
| Test class | `<Type>Test` or idiomatic project equivalent | `DocumentRepositoryTest` |
| UI state | `<Feature>UiState` | `ProcessingUiState` |
| Event | `<Feature>UiEvent` | `EditorUiEvent` |

## 8.3 Forbidden Generic Packages

Avoid:

```text
utils/
helpers/
misc/
stuff/
common/       # unless tightly defined
manager/      # unless lifecycle/coordination semantics are explicit
```

A utility belongs in a shared package only when its responsibility is narrow, stable, and reused. Otherwise place it beside the feature or layer that owns the behavior.

---

# 9. Recommended Source Tree

**Status: PROPOSED — must be adapted to the generated Android project.**

```text
<project-root>/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── <android-source-root>/
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   ├── components/
│   │   │   │   │   ├── design/
│   │   │   │   │   └── feature/
│   │   │   │   │       ├── onboarding/
│   │   │   │   │       ├── modelsetup/
│   │   │   │   │       ├── home/
│   │   │   │   │       ├── scanner/
│   │   │   │   │       ├── importdocument/
│   │   │   │   │       ├── preview/
│   │   │   │   │       ├── processing/
│   │   │   │   │       ├── results/
│   │   │   │   │       ├── editor/
│   │   │   │   │       ├── export/
│   │   │   │   │       ├── history/
│   │   │   │   │       ├── settings/
│   │   │   │   │       └── about/
│   │   │   │   │
│   │   │   │   ├── application/
│   │   │   │   │   ├── usecase/
│   │   │   │   │   ├── orchestration/
│   │   │   │   │   └── model/
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── service/
│   │   │   │   │   └── validation/
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   ├── file/
│   │   │   │   │   └── mapper/
│   │   │   │   │
│   │   │   │   ├── processing/
│   │   │   │   │   ├── pipeline/
│   │   │   │   │   ├── acquisition/
│   │   │   │   │   ├── validation/
│   │   │   │   │   ├── preprocessing/
│   │   │   │   │   ├── ocr/
│   │   │   │   │   ├── ai/
│   │   │   │   │   ├── classification/
│   │   │   │   │   ├── extraction/
│   │   │   │   │   ├── table/
│   │   │   │   │   └── result/
│   │   │   │   │
│   │   │   │   ├── export/
│   │   │   │   │   ├── contract/
│   │   │   │   │   ├── excel/
│   │   │   │   │   ├── csv/
│   │   │   │   │   ├── json/
│   │   │   │   │   └── pdf/
│   │   │   │   │
│   │   │   │   ├── platform/
│   │   │   │   │   ├── camera/
│   │   │   │   │   ├── picker/
│   │   │   │   │   ├── permissions/
│   │   │   │   │   ├── sharing/
│   │   │   │   │   └── lifecycle/
│   │   │   │   │
│   │   │   │   ├── modelmanagement/
│   │   │   │   │   ├── metadata/
│   │   │   │   │   ├── storage/
│   │   │   │   │   └── validation/
│   │   │   │   │
│   │   │   │   └── core/
│   │   │   │       ├── error/
│   │   │   │       ├── result/
│   │   │   │       ├── logging/
│   │   │   │       ├── validation/
│   │   │   │       └── utility/
│   │   │   └── res/
│   │   ├── test/
│   │   └── androidTest/
│   └── ...
├── docs/
├── scripts/
├── gradle/
├── .gitignore
├── README.md
└── build configuration files
```

## 9.1 Tree Adaptation Rule

Do not mechanically create every folder above.

Create a package/module only when there is real responsibility, meaningful cohesion, or a test boundary.

The goal is **controlled complexity**, not maximal folder count.

---

# 10. Module and Package Responsibilities

| Area | Owns | Must not own |
|---|---|---|
| `presentation` | Screens, UI state, events, navigation, rendering | SQL, OCR engines, AI runtime, export encoding |
| `application` | User workflows/use cases, orchestration | Android UI details, SQL details, provider internals |
| `domain` | Stable entities/contracts/invariants | Android SDK classes, SQLite classes, OCR/AI provider types |
| `data` | Repository implementations, DB/file adapters, mappers | Screen state or UI rendering |
| `processing` | Acquisition validation, preprocessing, OCR/AI integration, structuring | Navigation, direct UI manipulation |
| `export` | Export strategy implementations | Reading screens, direct UI logic, independent DB queries |
| `platform` | Camera, picker, permissions, sharing, Android lifecycle adapters | Domain business rules |
| `modelmanagement` | Model lifecycle, metadata, local model resources | UI-specific controls |
| `core` | Stable cross-cutting primitives | Feature-specific logic or dumping-ground utilities |

---

# 11. Presentation Layer

## 11.1 Responsibilities

The presentation layer owns:

- screens;
- reusable UI components;
- navigation;
- screen state rendering;
- user actions/events;
- transient UI state;
- accessibility semantics;
- visual loading/error/empty/success states.

## 11.2 Prohibited Responsibilities

Presentation code MUST NOT:

- open a database connection;
- execute SQL;
- call a DAO directly;
- construct OCR engine internals;
- call an AI runtime/model directly;
- create exporter instances for file generation;
- manage private filesystem paths;
- implement document processing algorithms;
- decide whether AI output is authoritative.

## 11.3 Screen Contract

Each screen should have a small, predictable contract:

```text
Input:
    UI state / navigation arguments

Output:
    UI events / callbacks

Rendering:
    Purely reflects current state
```

Side effects should be initiated through the appropriate state-holder/ViewModel/application boundary.

---

# 12. Feature Package Pattern

For every substantial feature, prefer:

```text
feature/<name>/
├── <Name>Screen
├── <Name>UiState
├── <Name>UiEvent
├── <Name>ViewModel / StateHolder   # when appropriate to actual stack
├── components/                     # feature-private reusable pieces
└── mapper/                         # only when UI mapping is non-trivial
```

A feature package MAY contain feature-specific application models when they genuinely simplify presentation, but it MUST NOT create a second competing domain model for the same concept.

---

# 13. Application Layer

## 13.1 Responsibility

The application layer expresses user-oriented actions and coordinates domain/infrastructure contracts.

Example use cases:

```text
ImportDocument
CaptureDocument
ValidateInput
PreprocessDocument
RunOCR
AnalyzeDocument
ClassifyDocument
ExtractFields
ExtractTables
ValidateExtraction
ReviewResult
UpdateField
UpdateTableCell
SaveDocument
GetDocumentHistory
SearchDocuments
DeleteDocument
ExportDocument
ShareDocument
PrepareModel
GetModelStatus
```

## 13.2 Use-Case Rules

Each use case SHOULD have:

- one clear responsibility;
- explicit input type;
- explicit output/result type;
- explicit dependencies;
- explicit failure behavior;
- explicit cancellation semantics where applicable.

Avoid giant `AppManager`, `DocumentManager`, or `MainViewModel` classes that become system-wide god objects.

---

# 14. Domain Layer

## 14.1 Domain Concepts

Domain concepts SHALL align with `DATA_SCHEMA` and related architecture documents.

Core concepts include:

```text
Document
DocumentMetadata
DocumentPage
DocumentType
ProcessingJob
ProcessingStatus
OCRResult
AIExtractionCandidate
ExtractionResult
StructuredField
StructuredTable
TableColumn
TableRow
TableCell
Confidence
SourceReference
ReviewState
ValidationState
ProcessingWarning
ProcessingError
ExportRequest
ExportResult
```

## 14.2 Domain Invariants

Examples:

1. A table row belongs to one table.
2. A table cell belongs to one row/column position.
3. Page ordering is deterministic.
4. Current saved values remain authoritative after user correction.
5. `editedFlag` reflects whether a stored value differs from its extraction baseline.
6. A failed processing job is not a successful extraction result.
7. An invalid AI response cannot bypass schema validation into an authoritative result.
8. Confidence is a review aid, not a truth guarantee.
9. Missing/unknown/empty/not-applicable values are not silently conflated.

---

# 15. Repository Interfaces

Domain/application code SHOULD depend on repository interfaces, for example:

```text
DocumentRepository
ProcessingRepository
StructuredDataRepository
ExportRepository        # only if required for local export history/metadata
```

Conceptual methods:

```text
save(document)
get(documentId)
listHistory(query)
delete(documentId)
saveExtraction(result)
updateField(field)
updateCell(cell)
```

The exact names and language syntax are implementation-dependent.

The interface MUST express business needs, not raw storage mechanics such as SQL cursors, database handles, or provider-specific response objects.

---

# 16. Processing Pipeline Architecture

SnapData's pipeline SHALL retain explicit stage boundaries:

```text
Document Input
      ↓
Acquisition
      ↓
Input Validation
      ↓
Document / Image Normalization
      ↓
Preprocessing
      ↓
OCR
      ↓
AI Analysis
      ↓
Document Classification
      ↓
Field Extraction
      ↓
Table Detection / Reconstruction
      ↓
Structured Data Generation
      ↓
Schema / Domain Validation
      ↓
Review
      ↓
User Edit
      ↓
Save
      ↓
Export / Share
```

## 16.1 One Stage, One Responsibility

Each processing stage SHALL have one dominant reason to change.

Bad:

```text
DocumentProcessor
    - opens camera
    - runs OCR
    - parses AI JSON
    - saves SQLite
    - writes XLSX
    - shows Toast
```

Good:

```text
AcquisitionAdapter
Preprocessor
OCRService
AIService
ClassificationService
ExtractionService
ValidationService
DocumentRepository
ExportService
```

---

# 17. Processing Orchestrator

Use a dedicated orchestration boundary such as:

```text
DocumentProcessingCoordinator
```

or

```text
DocumentProcessingUseCase
```

**Status:** PROPOSED name; exact implementation TBD.

## 17.1 Responsibilities

The orchestrator SHALL:

- sequence stages;
- validate preconditions;
- report stage progress;
- propagate cancellation;
- map infrastructure failures to application errors;
- build the final processing result;
- prevent false-complete state;
- preserve valid prior saved data;
- clean temporary resources through their owners.

## 17.2 Non-Responsibilities

It MUST NOT contain engine-specific implementation details such as:

- Tesseract object setup;
- AI runtime tensor/session internals;
- SQLite SQL statements;
- PDF library rendering internals;
- Excel encoding logic.

---

# 18. OCR Abstraction

## 18.1 Interface

Define an abstraction equivalent to:

```text
OCRService
├── initialize()
├── getReadiness()
├── processPage(input)
├── processDocument(input)
└── cancel(operationId)
```

## 18.2 OCR Provider Rule

Tesseract is source-backed as the OCR context in the current baseline, but the code MUST isolate Tesseract-specific types inside the OCR adapter.

Example conceptual structure:

```text
OCRService
    ↑
TesseractOCRService
    ↑
TesseractAdapter / engine-specific glue
```

If technical validation selects another engine, only the adapter and configuration layer should need substantial changes.

## 18.3 OCR Output Boundary

Convert provider-specific output into SnapData's canonical OCR contract:

```text
Provider OCR Output
        ↓
OCR Adapter Mapper
        ↓
Canonical OCRResult
```

Raw OCR text SHOULD be preserved separately from normalized OCR text where the data contract requires both.

---

# 19. AI Abstraction

## 19.1 AI Service

Use an abstraction equivalent to:

```text
AIService
├── isReady()
├── getModelInfo()
├── analyzeDocument(input)
├── classifyDocument(input)
├── extractFields(input)
├── extractTables(input)
└── cancel(operationId)
```

The exact division of methods MAY differ if the selected AI runtime naturally exposes a single structured-analysis operation.

## 19.2 AI Runtime Isolation

The AI layer SHALL hide:

- model file locations;
- tensor/runtime handles;
- tokenizer/session internals;
- provider-specific prompt/input representations;
- provider-specific output classes.

## 19.3 Network Rule

The AI service MUST NOT silently use a remote service as a fallback for the core MVP processing path.

---

# 20. Model Management Architecture

Create a dedicated `ModelManager` or equivalent lifecycle boundary.

Responsibilities:

```text
Discover model
Validate model
Check readiness
Prepare/load model
Expose metadata
Track lifecycle
Handle setup failure
Release resources where applicable
```

The UI SHALL ask for model state through an application-facing contract, never manipulate model files directly.

## 20.1 Model Lifecycle

Recommended conceptual states:

```text
UNKNOWN
↓
NOT_AVAILABLE
↓
DOWNLOADING / INSTALLING
↓
VERIFYING
↓
READY
↓
LOADING
↓
LOADED
↓
FAILED
```

Exact state names remain implementation-dependent.

## 20.2 Model Metadata

SQLite may store metadata/reference such as:

- model identifier;
- model version;
- status;
- installation path/reference;
- checksum/validation metadata where supported;
- installed timestamp.

Model binaries themselves SHALL remain outside SQLite.

---

# 21. AI Output Validation

Never allow this shortcut:

```text
AI Output → UI
```

Correct path:

```text
Raw AI Output
      ↓
Parser
      ↓
Schema Validation
      ↓
Normalization
      ↓
Domain Mapping
      ↓
Domain Validation
      ↓
Validated ExtractionResult
      ↓
Review
```

## 21.1 Validation Rules

The validator SHALL detect, as applicable:

- malformed JSON/structured output;
- missing required structural fields;
- invalid field value types;
- invalid table shape;
- duplicate or impossible identifiers;
- unsupported document types;
- impossible row/column relationships;
- fabricated structures that cannot be grounded in OCR/source evidence where evidence is required.

## 21.2 Unknown Values

Unknown or unresolved values SHALL use the canonical schema strategy rather than invented values.

Examples of prohibited behavior:

```text
missing invoice number → "N/A" everywhere without contract
unknown amount         → 0
not detected           → fabricated text
```

---

# 22. Structured Data Model and Mapping

Separate three concerns where necessary:

```text
Domain Model
     ↕
Database Model
     ↕
Serialized Export Model
```

Do not reuse a database entity as a public domain contract merely because the fields look similar.

Do not create redundant UI/database/domain models without a mapping justification.

## 22.1 Canonical Root Aggregate

`Document` represents the original source document and durable lifecycle metadata.

It SHALL NOT become a giant object containing every page, OCR token, field, and export file unless that is explicitly justified by the chosen persistence strategy.

## 22.2 Table Model

Canonical conceptual hierarchy:

```text
StructuredTable
├── columns[]
└── rows[]
    └── cells[]
```

Ordering SHALL be deterministic.

## 22.3 User Corrections

For editable fields/cells, preserve:

```text
originalValue
currentValue
editedFlag
```

The current saved user value is authoritative.

---

# 23. SQLite Architecture

## 23.1 Access Rule

Required path:

```text
UI
 ↓
ViewModel / State Holder
 ↓
Use Case
 ↓
Repository
 ↓
DAO / Local Data Source
 ↓
SQLite
```

Prohibited:

```text
Screen → SQLite
Screen → DAO
Screen → SQL string
```

## 23.2 Logical Persistence Entities

The database document defines a logical model including:

```text
Document
document_page
processing_job
extraction_result
extracted_field
extracted_table
extracted_table_row
extracted_table_cell
export_record
app_setting
model_metadata
```

Exact table/column naming and Room/raw-SQLite implementation remain subject to actual project validation.

## 23.3 Transactions

Use transactions for logically atomic database changes, such as saving a complete structured result and its dependent relational records.

File operations are a separate consistency boundary. Do not falsely claim a filesystem write is transactionally atomic with SQLite.

---

# 24. File Storage Architecture

Define a provider-neutral boundary such as:

```text
FileStorage
├── create
├── read
├── write
├── move
├── delete
├── exists
├── size
└── cleanup
```

## 24.1 Storage Categories

Separate lifecycle policies for:

```text
Original Documents
Processed Images
Temporary Processing Files
Model Resources
Exports
```

## 24.2 Filename Safety

Do not trust imported filenames.

Normalize or generate safe internal names.

Prevent:

- path traversal;
- absolute path injection;
- control characters;
- unsupported extension spoofing;
- ambiguous overwrite behavior.

## 24.3 Temporary Files

Lifecycle:

```text
Create
 ↓
Process
 ↓
Success / Failure / Cancellation
 ↓
Cleanup
```

Cleanup SHOULD occur in a `finally`-equivalent mechanism where supported by the language/runtime.

---

# 25. Export Architecture

## 25.1 Strategy Boundary

Use:

```text
ExportService
      ↓
ExportStrategy
   ├── ExcelExporter
   ├── CSVExporter
   ├── JSONExporter
   └── PDFExporter
```

## 25.2 Export Contract

All exporters SHALL consume canonical structured data.

They MUST NOT:

- query SQLite independently for arbitrary fields;
- inspect screen state;
- call OCR/AI directly;
- reconstruct business data from raw OCR text unless explicitly defined as an export-specific representation.

## 25.3 Export Correctness

Export MUST use the latest authoritative saved values.

Example:

```text
AI extracted: 5000
User corrected: 5500
Saved value: 5500
Exported value: 5500
```

## 25.4 Sharing Boundary

Android sharing SHALL use safe content URIs or the platform-equivalent mechanism rather than exposing private filesystem paths.

---

# 26. Platform Layer

The platform layer contains Android-specific adapters.

Candidate boundaries:

```text
CameraService
DocumentPicker
PermissionService
ShareService
LifecycleAdapter
FileUriResolver
```

The platform layer MAY expose Android types at the boundary only when unavoidable, but should translate them into application-facing representations as early as practical.

---

# 27. Camera Architecture

Camera acquisition is an input adapter.

Conceptual flow:

```text
Camera UI
 ↓
Camera State Holder
 ↓
CameraService
 ↓
Captured Local Reference
 ↓
Document Acquisition Contract
```

The processing pipeline should not know whether the source came from:

- camera;
- gallery/file picker;
- imported PDF.

It should receive a normalized input contract.

---

# 28. File Picker Architecture

The file picker boundary SHALL:

1. request only required access;
2. validate MIME/type and file metadata;
3. reject unsupported/corrupt inputs early;
4. avoid exposing private provider details to higher layers;
5. return a safe document reference.

Supported input is source-backed as PDF/image acquisition; exact format lists and limits remain subject to validation.

---

# 29. Permission Management

Permission logic SHALL be centralized.

Examples:

```text
Camera permission
Storage/file access permission where actually required by platform API
Notifications/other permissions only if a future approved feature needs them
```

Do not scatter permission checks through multiple unrelated screens.

The permission service SHOULD expose semantic outcomes such as:

```text
Granted
Denied
PermanentlyDenied
NotRequired
```

rather than forcing business logic to interpret raw Android permission strings.

---

# 30. UI State Model

Use explicit state machines or sealed/discriminated result types where they improve correctness.

Preferred conceptual pattern:

```text
ProcessingUiState
├── Idle
├── Validating
├── Preprocessing
├── OcrProcessing
├── AiProcessing
├── Structuring
├── ReviewReady
├── Failed
└── Cancelled
```

Avoid uncontrolled flag combinations such as:

```text
isLoading
isProcessing
isSuccess
isError
isCancelled
hasResult
```

when those states can contradict each other.

## 30.1 State Source-of-Truth

| Concern | Owner |
|---|---|
| Navigation route | Navigation layer |
| Active processing status | Processing/application layer |
| Saved document | Repository/persistence |
| Unsaved editor changes | Editor state |
| Model readiness | Model manager |
| Export execution | Export use case/service |
| Dialog visibility | Transient UI state |

---

# 31. UI Event Architecture

Preferred flow:

```text
User Action
    ↓
UiEvent
    ↓
ViewModel / State Holder
    ↓
Use Case
    ↓
Domain/Application Result
    ↓
UiState
    ↓
UI
```

Examples:

```text
SelectDocument
CaptureDocument
StartProcessing
CancelProcessing
RetryProcessing
EditField
EditTableCell
AddTableRow
DeleteTableRow
SaveChanges
ExportDocument
ShareExport
DeleteDocument
```

UI events should express intent rather than implementation details.

Good:

```text
ExportDocument(EXCEL)
```

Bad:

```text
CreateApachePoiWorkbookAndWriteToPath(...)
```

---

# 32. Navigation Architecture

Screens SHALL be navigation destinations or task states according to actual implementation, but navigation business rules SHALL stay out of low-level UI components.

Conceptual route graph:

```text
Splash
  ↓
Onboarding / Home / Model Setup
  ↓
Home
  ├── Camera
  ├── Import
  ├── History
  └── Settings
        
Camera / Import
      ↓
Preview
      ↓
Processing
      ↓
Results
      ↓
Editor
      ↓
Export
      ↓
Share / Success
```

## 32.1 Navigation Arguments

Prefer stable identifiers over passing entire mutable domain aggregates through navigation.

Good:

```text
history/{documentId}
```

Avoid:

```text
results/{serializedHugeDocumentJson}
```

unless the actual navigation mechanism requires a small serialized primitive contract.

## 32.2 Back Behavior

Back SHALL respect unsaved edits and active processing states.

The editor MUST ask before discarding unsaved changes.

---

# 33. Error Architecture

Create a stable application error taxonomy:

```text
AppError
├── InputError
├── PermissionError
├── PreprocessingError
├── OCRFailure
├── AIError
├── ValidationError
├── DatabaseError
├── StorageError
├── ExportError
├── SharingError
├── ModelError
├── Cancellation
└── UnknownError
```

Exact class hierarchy is implementation-dependent.

## 33.1 Error Shape

Each error SHOULD carry:

```text
stableCode
safeMessage
recoverability
retryability
causeCategory
```

Raw exceptions SHOULD be retained only at diagnostic boundaries where safe and useful.

## 33.2 Error Mapping

Required flow:

```text
Infrastructure Exception
      ↓
Application Error
      ↓
UI State
      ↓
User-safe Message
```

Never surface raw stack traces or internal file paths to the user.

---

# 34. Result Types

Where appropriate, use explicit success/failure structures.

Conceptually:

```text
Result<T, E>
├── Success(value)
└── Failure(error)
```

or the idiomatic equivalent supported by the selected implementation language.

Do not use exceptions as the only business-control mechanism for expected failures such as unsupported input, model unavailable, validation failure, cancellation, or exporter rejection.

Exceptions remain appropriate for unexpected runtime faults when mapped at a controlled boundary.

---

# 35. Coroutine / Asynchronous Execution Policy

## 35.1 Conditional Kotlin Profile

If the actual generated project is Kotlin-based, long-running work SHOULD use Kotlin coroutines or the project's established async mechanism.

Operations that MUST NOT block the UI thread include:

- OCR;
- AI inference;
- PDF parsing/rendering;
- image preprocessing;
- model loading;
- model installation;
- large exports;
- database operations that may be expensive.

The exact dispatcher/executor policy SHALL follow the validated project stack.

## 35.2 Dispatcher Ownership

A lower-level service SHOULD own the execution details for its work rather than forcing screens to choose thread pools.

For example:

```text
UI
 ↓
Use Case
 ↓
OCRService
      ↳ chooses appropriate background execution
```

Avoid hardcoding `Thread.sleep`, ad-hoc thread creation, or nested executors without lifecycle ownership.

---

# 36. Cancellation Architecture

Cancellation must be cooperative where the underlying engine supports it.

Requirements:

1. A cancelled operation must stop work as early as possible.
2. Temporary resources must be cleaned.
3. Previously saved data must remain intact.
4. A cancelled job must not become `COMPLETED`.
5. The UI must receive a truthful cancellation state.

Conceptual flow:

```text
User taps Cancel
       ↓
Application cancellation signal
       ↓
Orchestrator
       ├── Preprocessor cancel
       ├── OCR cancel
       ├── AI cancel
       └── temp cleanup
       ↓
CANCELLED
```

---

# 37. Concurrency Rules

SnapData SHALL explicitly control concurrent operations.

## 37.1 Duplicate Processing

Do not allow accidental duplicate processing of the same document unless the business flow explicitly supports separate processing jobs.

## 37.2 Duplicate Export

Multiple taps on Export SHALL NOT create uncontrolled duplicate writes.

Use a busy/locked state or idempotent operation strategy.

## 37.3 Concurrent Saves

Editor saves SHALL serialize or otherwise prevent lost updates.

## 37.4 Model Initialization

Prevent simultaneous model loads that compete for memory/resources.

## 37.5 Repository Concurrency

Repository writes SHALL define ordering and transaction semantics where multiple asynchronous operations can touch the same record.

---

# 38. User Correction Authority

This is a mandatory rule:

> Once the user modifies and saves extracted data, the saved user value becomes authoritative.

The codebase SHALL distinguish:

```text
Machine-generated candidate
      vs
User-confirmed saved value
```

## 38.1 Reprocessing Behavior

If a document is reprocessed:

- prior saved user values MUST NOT be overwritten silently;
- the implementation MUST decide explicitly whether to create a new processing result or merge candidates according to an approved policy;
- any merge rule MUST be tested.

The simplest MVP-safe default is to create a new extraction attempt and preserve the prior saved authoritative result until the user explicitly accepts replacement.

---

# 39. Validation Boundaries

Validation SHALL exist at multiple boundaries:

```text
Input
 ↓
Preprocessing
 ↓
OCR
 ↓
AI raw output
 ↓
Canonical structured result
 ↓
Domain invariants
 ↓
Persistence
 ↓
Export
```

## 39.1 Input Validation

Validate:

- supported type;
- accessible source;
- non-zero size;
- reasonable size/page count within validated limits;
- readable/correctly parseable content;
- non-malicious path/reference behavior.

## 39.2 Persistence Validation

Repository/database writes SHALL enforce relevant constraints even if the UI already validated them.

## 39.3 Export Validation

Exporter SHALL reject impossible/malformed canonical data rather than emitting silent corruption.

---

# 40. Logging Standard

## 40.1 Allowed Logging

Log safe operational metadata such as:

- stage;
- operation identifier;
- duration;
- model version/identifier where non-sensitive;
- application version;
- error code/category;
- item/page counts when not sensitive in context;
- retry/cancellation state.

## 40.2 Prohibited Logging

Never log routinely:

- full OCR text;
- document contents;
- extracted personal/financial values;
- complete AI prompts containing private content;
- AI raw output containing private document data;
- access tokens;
- passwords;
- API keys;
- private file contents;
- internal storage secrets.

## 40.3 Debug Logging

Debug verbosity MAY be higher in local development but MUST still respect the privacy baseline.

Never create a hidden "dump everything" mode for document content.

---

# 41. Observability Without Data Leakage

For a failed document job, prefer:

```text
jobId=123
stage=AI_PROCESSING
errorCode=AI_OUTPUT_INVALID
durationMs=4810
modelVersion=...
```

Avoid:

```text
invoice text: <full sensitive document>
full raw AI payload: <entire extraction>
local path: /data/user/0/.../private_document.pdf
```

For detailed debugging, use sanitized test documents and controlled local reproduction.

---

# 42. Offline-First Architecture

Core workflow SHALL remain network-independent after required model setup.

```text
Input
 ↓
Preprocessing
 ↓
OCR
 ↓
Offline AI
 ↓
Structured Data
 ↓
Review
 ↓
SQLite / local files
 ↓
Export
```

Network use, when approved, belongs around model setup/update rather than the core document-processing path.

## 42.1 No Hidden Fallback

This is prohibited:

```text
Local AI fails
   ↓
secret cloud API call
   ↓
result
```

The application must fail truthfully or request explicit user-approved action in a future, separately baselined cloud feature.

---

# 43. Network Isolation Boundary

If model downloading or future optional cloud features exist, isolate them in explicit infrastructure modules.

Conceptually:

```text
network/
  ModelDownloadDataSource
  FutureRemoteRepository   # future only
```

The processing core SHALL depend on a model abstraction, not on HTTP client code.

---

# 44. Dependency Injection

Use the smallest dependency-management mechanism that cleanly supports:

- constructor dependency passing;
- test doubles;
- lifecycle management;
- platform adapters;
- repository implementations;
- OCR/AI provider replacement.

Do not introduce a large dependency-injection framework solely to satisfy an architectural label.

## 44.1 Preferred Injection Direction

```text
Screen
  ↓
ViewModel / StateHolder
  ↓
Use Case
  ↓
Interface
  ↑
Implementation
```

## 44.2 Constructor Injection

Constructor injection is preferred where supported because it makes dependencies explicit and improves testability.

Avoid service-locator patterns in domain/application code.

---

# 45. Dependency Direction Rules

The desired direction is:

```text
Presentation → Application → Domain
                           ↑
                           │
                    Data / Infrastructure
```

Allowed examples:

```text
RepositoryImpl → Repository
OCRAdapter      → OCRService
ExcelExporter   → ExportStrategy
```

Disallowed examples:

```text
Domain → Android Context
Domain → SQLite Cursor
Domain → Tesseract types
Domain → AI runtime session
Screen → DAO
Screen → exporter implementation
```

---

# 46. Shared Core Standard

`core` is a high-leverage package and must not become a dumping ground.

Allowed shared concepts include:

```text
AppError
Result
common validation primitives
logging abstraction
small stable utility primitives
lifecycle-safe abstractions
```

A feature-specific helper belongs in its feature or layer, not in `core` merely because several developers may find it convenient.

---

# 47. API/Internal Service Contract Standard

Although the MVP does not require REST APIs, the codebase SHALL define internal service contracts for major boundaries where replacement/testing benefits from them.

Required conceptual contracts include:

```text
DocumentProcessingService
OCRService
AIService
ModelManager
DocumentRepository
FileStorage
ExportService
ShareService
CameraService
DocumentPicker
```

Future network APIs MUST be adapters behind repository/service boundaries rather than direct calls from UI code.

---

# 48. Future Backend Boundary

Current MVP:

```text
Backend = NONE
REST API = NONE
```

Future approved backend:

```text
UI
 ↓
Use Case
 ↓
Repository Interface
 ├── LocalRepository
 └── RemoteRepository
        ↓
      API
```

No UI rewrite should be required solely because remote persistence is introduced.

---

# 49. Database/File Consistency Architecture

SQLite and physical file storage are distinct consistency boundaries.

## 49.1 Case Matrix

| Case | Required behavior |
|---|---|
| DB row exists, source file missing | Detect missing file; show truthful recovery/error state |
| File exists, DB row missing | Treat as orphan; cleanup/recovery policy must be explicit |
| DB commit succeeds, file write fails | Record/storage transaction strategy must preserve consistency and truthful status |
| File write succeeds, DB commit fails | Remove/reconcile orphan or mark for deterministic cleanup |
| Document delete | Remove DB records transactionally, then coordinate file cleanup |

The implementation SHALL NOT claim cross-resource atomicity it cannot actually provide.

---

# 50. Temporary Resource Lifecycle

All temporary resources require an owner.

Example:

```text
ProcessingCoordinator
    owns → TempWorkspace

TempWorkspace
    creates → intermediate files
    cleans → all owned intermediates on completion/failure/cancel
```

Avoid global temp-file directories that no component owns.

---

# 51. Memory Management

Document processing is memory-sensitive.

Code SHOULD:

- process multi-page documents incrementally where possible;
- avoid retaining all high-resolution images simultaneously;
- release large intermediate objects promptly;
- avoid unnecessary copies of image buffers;
- stream export output when the selected library supports it safely;
- cap/validate input according to measured device constraints.

The exact maximum page count/size remains subject to validation.

---

# 52. Large Table Handling

Table rendering and editing SHOULD be bounded and incremental.

Avoid creating thousands of duplicated UI objects when a virtualization strategy exists in the selected UI toolkit.

Canonical table order SHALL remain stable even if the UI uses paging/virtualization.

---

# 53. Multi-Page Document Architecture

Where multi-page PDF support is enabled, page-oriented processing SHOULD preserve:

```text
pageIndex
sourceReference
pageStatus
OCR evidence
page-specific errors/warnings
```

The orchestrator SHOULD avoid loading all pages at maximum resolution at once unless benchmarks demonstrate that it is safe on the supported device matrix.

---

# 54. Preprocessing Architecture

Preprocessing SHALL remain separate from OCR implementation.

Conceptual chain:

```text
Original
  ↓
Quality Assessment
  ↓
Auto Crop
  ↓
Perspective Correction
  ↓
Rotation
  ↓
Noise Reduction
  ↓
Brightness / Contrast Enhancement
  ↓
Normalized Image
```

The original source should be preserved when possible so users can compare or recover from processing decisions.

Exact algorithms and thresholds are **REQUIRES TECHNICAL VALIDATION**.

---

# 55. Document Type Classification Architecture

Classification is a semantic result, not merely a file-type check.

Represent at least conceptually:

```text
DetectedDocumentType
DetectedConfidence (optional)
UserConfirmedType (optional)
```

Do not treat an AI classification as user-confirmed truth unless the user explicitly confirms it.

---

# 56. Field Extraction Architecture

A field SHOULD contain:

```text
fieldKey
fieldLabel
currentValue
valueType
confidence (optional)
sourcePage (optional)
sourceReference (optional)
originalValue (optional)
editedFlag
order
```

The canonical data schema remains the authority for exact persisted fields.

---

# 57. Table Extraction Architecture

Table extraction SHALL produce structured tables rather than UI-only grid objects.

```text
AI/Processing Output
        ↓
Table Reconstructor
        ↓
Canonical StructuredTable
        ↓
Validation
        ↓
Review/Edit
        ↓
Persistence
```

Uncertain reconstruction MUST NOT silently invent values or columns.

---

# 58. Confidence Architecture

Confidence MAY exist at multiple levels:

```text
Document classification
Field
Table
Cell
OCR token/region
```

Important rules:

1. Do not fabricate numerical confidence.
2. Do not invent aggregation formulas without approval.
3. Confidence is a review aid, not a guarantee.
4. UI must not rely on color alone to communicate confidence.
5. Low-confidence thresholds remain configurable/validated rather than guessed.

---

# 59. Review Architecture

Review is a distinct application stage.

```text
Machine Result
    ↓
Validation
    ↓
Review Ready
    ↓
User Review
    ↓
Correction
    ↓
Save
```

The review layer SHOULD expose source evidence where the UX/data contract supports it.

Do not hide material uncertainty behind a generic "success" banner.

---

# 60. Editor Architecture

The editor owns unsaved user changes.

Recommended conceptual model:

```text
PersistedSnapshot
      ↓
EditableDraft
      ↓
User changes
      ↓
Validation
      ↓
Save
      ↓
Persisted authoritative state
```

## 60.1 Undo/Redo

Undo/redo is a UX feature and may be implemented within the editor state layer. It should not require a database audit table for MVP unless separately approved.

## 60.2 Dirty State

The editor MUST be able to answer:

```text
hasUnsavedChanges()
```

This state is required for safe Back/navigation behavior.

---

# 61. Settings Architecture

Settings SHOULD be accessed through a typed settings contract rather than raw preference strings spread across screens.

Example conceptual settings:

```text
Theme
OCR language
Export preference
Model readiness/configuration references
Storage management options
```

Exact setting set is controlled by the UX/product baseline.

---

# 62. State Persistence and Restoration

The implementation SHALL distinguish:

```text
Persisted business state
vs
transient UI state
```

Persist only what is needed to restore or resume user work safely.

Do not persist secrets or unnecessary private document content in UI-state caches.

Navigation state restoration SHALL not accidentally create duplicate processing jobs or duplicate exports.

---

# 63. Lifecycle Safety

Android lifecycle events can interrupt UI and processing.

A screen disappearing MUST NOT automatically imply the processing operation is cancelled unless that behavior is explicitly designed.

Long-running work should have an application/lifecycle owner appropriate to the selected execution model.

The actual use of foreground services/work managers/background execution APIs remains **REQUIRES TECHNICAL VALIDATION** against project scope and device behavior.

---

# 64. Thread-Safety Standard

Shared mutable state SHOULD be minimized.

Prefer:

```text
immutable state snapshot
      ↓
new state emission
```

over:

```text
many components mutate one global object
```

Where shared mutable state is unavoidable, define its synchronization/serialization mechanism explicitly.

---

# 65. Immutability Standard

Prefer immutable data structures for:

- UI state;
- domain results;
- processing status snapshots;
- export requests;
- validated structured data.

Mutation SHOULD occur at controlled application/editor boundaries rather than through arbitrary object mutation across layers.

---

# 66. Nullability and Optional Data

Nullability SHALL have semantic meaning.

Do not use `null` interchangeably for:

```text
missing
unknown
empty
not applicable
not yet loaded
failed
```

Use the canonical data contract where it defines explicit distinctions.

---

# 67. Time and Date Handling

The application SHALL use a consistent time representation in domain/persistence layers.

Store/transfer machine timestamps in a stable, deterministic representation and convert to user-local presentation only at the UI boundary.

Avoid using formatted display strings as persisted business timestamps.

---

# 68. Identifier Strategy

Use stable identifiers for persisted records.

The database baseline recommends integer primary keys for local SQLite where appropriate, while external identifiers may be used when integration requirements later justify them.

The code architecture SHALL treat identifiers as typed domain concepts where practical rather than passing arbitrary strings throughout the system.

---

# 69. Serialization Standard

Use the canonical structured-data contract as the serialization boundary.

Serialization rules:

1. deterministic field naming;
2. stable ordering where order has semantic meaning;
3. explicit null/missing rules;
4. no provider-specific runtime objects;
5. versioned schemas where migration compatibility matters;
6. validation before deserialization into domain objects.

JSON is both an export format and a useful contract format, but the internal domain model must not be defined solely by a JSON parser's output shape.

---

# 70. Export Naming and Path Policy

Export file names SHALL:

- use safe normalized names;
- avoid path separators;
- avoid collisions through deterministic suffix/versioning strategy;
- preserve correct extension and MIME type;
- never permit arbitrary user-supplied paths to escape the app-controlled export area.

Example conceptual naming:

```text
<safe-document-name>-export.xlsx
<safe-document-name>-export.csv
<safe-document-name>-export.json
<safe-document-name>-export.pdf
```

Exact naming is **TBD**.

---

# 71. Security Coding Standard

Code SHALL defend against:

- path traversal;
- malformed PDFs;
- malformed images;
- oversized inputs;
- decompression/resource exhaustion attacks where applicable;
- unsafe filenames;
- unauthorized file references;
- invalid AI output;
- insecure sharing;
- accidental sensitive logging;
- secrets committed to source control.

## 71.1 Secure Defaults

Prefer deny-by-default behavior for:

- unsupported file types;
- untrusted paths;
- unvalidated model resources;
- unrecognized AI fields;
- invalid exporter requests.

---

# 72. Secrets Management

Never hard-code:

- API keys;
- passwords;
- private tokens;
- release signing secrets;
- credentials.

The current MVP should have no runtime cloud credential requirement for core document processing.

If future optional network features are introduced, credentials SHALL remain outside source-controlled code and be injected through the approved build/runtime mechanism.

---

# 73. Android Sharing Security

Sharing shall use platform-safe content URI mechanisms or the selected equivalent.

Do not share:

```text
/storage/emulated/0/.../private/path
```

Do not grant broader access than required by the share operation.

---

# 74. Testing Architecture

The codebase SHALL support at least these test boundaries, subject to the generated project's available framework:

```text
Unit Tests
Component Tests
Repository/Data Tests
OCR Adapter Tests
AI Adapter Tests
Processing Pipeline Tests
Export Tests
Security/Path Tests
UI Tests
End-to-End Workflow Tests
Offline Tests
```

## 74.1 High-Value Unit Test Targets

Prioritize deterministic, logic-heavy code:

- state reducers/state holders;
- AI output parser/validator;
- domain invariants;
- table reconstruction;
- field normalization;
- error mapping;
- filename/path sanitization;
- repository mappers;
- export data transformation;
- processing state transitions.

---

# 75. Test Doubles and Provider Isolation

OCR and AI dependencies MUST be replaceable in tests.

Example:

```text
FakeOCRService
FakeAIService
FakeModelManager
FakeFileStorage
FakeDocumentRepository
FakeExportService
```

A unit test should not need to boot the real AI model simply to test `DocumentProcessingCoordinator` control flow.

---

# 76. Processing Pipeline Tests

Required scenarios include:

1. successful image flow;
2. successful PDF flow;
3. unsupported file;
4. corrupt file;
5. preprocessing failure;
6. OCR failure;
7. AI model unavailable;
8. malformed AI output;
9. schema-validation failure;
10. partial result;
11. cancellation during preprocessing;
12. cancellation during OCR;
13. cancellation during AI inference;
14. save failure;
15. export failure;
16. interruption/recovery;
17. user correction persistence.

---

# 77. Mandatory User-Correction Integrity Test

Scenario:

```text
AI extracts value A
↓
User changes value to B
↓
Save
↓
Close/reopen document
↓
Export
```

Expected:

```text
Reopened value = B
Exported value = B
AI baseline may still be A
No silent overwrite
```

This test is a release gate.

---

# 78. Offline Test Standard

At least one end-to-end validation MUST disable network connectivity after model setup and verify:

```text
Acquire
→ Preprocess
→ OCR
→ AI
→ Validate
→ Review
→ Save
→ Export
```

No hidden network request should be required for the core workflow.

---

# 79. Logging Privacy Tests

The test suite SHALL include checks or review procedures that confirm logs do not contain:

- full OCR text;
- sensitive extracted fields;
- raw document content;
- credentials;
- private file contents.

Where feasible, automated tests should inspect generated logs for known test-sensitive markers.

---

# 80. Code Formatting and Style

The project SHALL use the formatter/linter/tooling actually established by the generated project.

General rules:

- consistent indentation;
- no dead imports;
- no unexplained magic numbers;
- no shadowed variables;
- no duplicate logic where a shared abstraction is genuinely warranted;
- clear names over clever names;
- short functions when practical;
- explicit dependencies;
- predictable error handling.

Do not optimize for minimum line count. Optimize for maintainability and correctness.

---

# 81. Function Design Standard

A function should generally:

1. do one coherent thing;
2. have a name that describes intent;
3. avoid hidden side effects;
4. avoid mutating shared global state;
5. return an explicit result when failure is possible;
6. keep provider details out of high-level business code.

Bad:

```text
processEverything()
```

Better:

```text
validateInput()
preprocessDocument()
runOCR()
analyzeDocument()
validateExtraction()
saveDocument()
```

---

# 82. Class Design Standard

Classes SHOULD have one primary responsibility.

Avoid:

```text
SnapDataManager
MainController
UtilityService
CommonHelper
```

when the class actually coordinates unrelated concerns.

Prefer explicit roles:

```text
DocumentProcessingCoordinator
DocumentRepository
TesseractOCRService
AIOutputValidator
ExcelExporter
ModelManager
```

---

# 83. Comments and Documentation Standard

Comments should explain **why**, not restate obvious code.

Good:

```text
// Preserve the original extraction so user corrections remain auditable
// without requiring a full edit-history table in MVP.
```

Bad:

```text
// Increment i by one
```

Document non-obvious:

- architecture decisions;
- safety constraints;
- format assumptions;
- model/runtime quirks;
- migration hazards;
- cancellation behavior.

---

# 84. TODO / FIXME Standard

Use structured markers with enough context to be actionable.

Example:

```text
TODO(ARCH-042): Replace temporary file adapter after generated-project
storage API is confirmed.
```

Avoid:

```text
TODO: fix later
```

Every material TODO should have an owner or issue reference when the project tracking workflow supports it.

---

# 85. Magic Values

Do not scatter protocol/status values throughout the code.

Bad:

```text
if status == "AI_PROCESSING"
```

repeated across dozens of files.

Prefer a typed enum/sealed representation where supported.

Similarly centralize:

- MIME types;
- export formats;
- processing status values;
- error codes;
- schema versions.

---

# 86. Constants Standard

Constants belong near the boundary that owns them.

Examples:

```text
AIModelConfig
ExportFormat
ProcessingLimits
StoragePolicy
```

Do not place unrelated constants into a giant `Constants` object.

---

# 87. Feature Boundary Rules

Feature code may depend on shared application/domain contracts, but should avoid direct coupling to other feature internals.

For example:

```text
results → domain/application
editor  → domain/application
history → domain/application
```

Avoid:

```text
HistoryScreen → ResultsScreenViewModel internals
```

Use a shared application/domain contract instead.

---

# 88. Model-to-UI Mapping

UI should receive presentation-friendly models rather than raw infrastructure structures.

Example:

```text
DatabaseEntity
   ↓ mapper
Domain ExtractionResult
   ↓ mapper
ResultsUiModel
   ↓
ResultsScreen
```

A UI model MAY flatten or format fields for rendering, but must not change business authority.

---

# 89. Domain-to-Database Mapping

Use explicit mapping functions/types:

```text
Domain → DatabaseEntity
DatabaseEntity → Domain
```

Mapping logic should be deterministic and unit-tested.

Do not embed database conversion logic inside domain entities unless the selected persistence framework explicitly requires it and the dependency cost is justified.

---

# 90. Domain-to-Export Mapping

Prefer:

```text
Canonical Domain Result
        ↓
ExportProjection
        ↓
Exporter
```

This allows each output format to satisfy format-specific requirements without corrupting the canonical domain model.

---

# 91. Exporter Isolation

Each exporter should be independently testable.

Example:

```text
CsvExporterTest
JsonExporterTest
ExcelExporterTest
PdfExporterTest
```

Exporters should not depend on Android screens or user navigation.

---

# 92. Schema Versioning

Persisted structured data should carry a schema version where migration/compatibility needs exist.

When schema changes:

```text
Version N
   ↓ migration
Version N+1
```

Do not silently reinterpret historical rows using a newer schema without explicit migration logic.

---

# 93. Database Migration Standard

Every schema migration SHALL:

1. have a deterministic version;
2. preserve valid prior user data;
3. be tested from supported previous versions;
4. define behavior for orphaned/missing file references;
5. avoid destructive changes without explicit migration policy.

Migration tests are release gates.

---

# 94. Build Configuration Standard

The exact build configuration SHALL follow the generated project.

Record and review:

```text
namespace
applicationId
compileSdk
targetSdk
minSdk
JDK version
Gradle version
Android Gradle Plugin version
Kotlin version if applicable
build variants
R8/shrinker settings
native ABI configuration
resource packaging rules
```

Do not hard-code these values into architecture documentation until they are evidence-backed.

---

# 95. Resource Organization

Separate resources by responsibility where supported by the actual Android stack:

```text
UI resources
Localization resources
Icons/images
OCR language/model resources
Static configuration
Test fixtures
```

Large binary model assets SHOULD NOT be duplicated unnecessarily inside source-controlled resources.

Model packaging strategy remains dependent on the validated AI runtime.

---

# 96. Localization Standard

User-visible strings SHOULD be externalized using the project's supported resource/localization mechanism.

Do not hard-code business-facing UI copy throughout source files.

Source-backed language requirements include multilingual OCR as a roadmap/context item; exact MVP language list is TBD.

---

# 97. Accessibility Coding Standard

Core UI actions SHALL expose:

- meaningful labels;
- readable state announcements where appropriate;
- logical navigation order;
- sufficient non-color cues;
- touch targets appropriate to the selected platform guidance.

Accessibility is not solely a visual styling concern; state semantics are part of code architecture.

---

# 98. UI Performance Standard

The UI layer SHALL avoid:

- synchronous file I/O;
- synchronous AI/OCR work;
- unnecessary recomposition/re-rendering where the framework exposes such behavior;
- large image decoding on the UI thread;
- repeatedly converting the same large data structure during rendering.

Use memoization/cache mechanisms only when they are justified by measurement and framework semantics.

---

# 99. Processing Progress Standard

Progress MUST be truthful.

Use semantic stage updates such as:

```text
Preparing document…
Running OCR…
Understanding document…
Building structured data…
Validating result…
```

A numerical percentage SHOULD only be displayed when it is based on a meaningful measurable unit such as page completion or validated work units.

Do not fabricate 73%, 92%, or an ETA simply to make the UI look busy.

---

# 100. Anti-Pattern Catalogue

## 100.1 God ViewModel

A single ViewModel controls camera, OCR, AI, database, export, settings, and navigation.

**Action:** split by feature/use case/application responsibility.

## 100.2 UI-as-Repository

A screen directly queries SQLite.

**Action:** repository/use-case boundary.

## 100.3 AI-as-Truth

Raw model output becomes final data with no validation/review.

**Action:** parser → schema validation → domain validation → review.

## 100.4 Hidden Cloud Fallback

Local AI failure triggers a remote API silently.

**Action:** fail truthfully; cloud is a future explicit feature.

## 100.5 Database-Leaking Domain

Domain entities extend database rows or expose cursors.

**Action:** mapper boundary.

## 100.6 Export-by-Screen

Screen assembles CSV/Excel/PDF directly.

**Action:** ExportService + strategy adapter.

## 100.7 Global Utility Dump

Every new helper goes into `utils/`.

**Action:** place logic next to its responsibility; keep core narrow.

## 100.8 Mutable Global State

Many components directly mutate a shared global document.

**Action:** controlled state holder / repository authority.

## 100.9 Silent Overwrite

Reprocessing replaces user corrections.

**Action:** explicit authority model and regression test.

## 100.10 Log Everything

Debugging by dumping OCR text and AI output.

**Action:** structured safe diagnostics + sanitized test fixtures.

---

# 101. Code Review Standard

Every meaningful pull request/change SHOULD be reviewed against:

```text
Architecture
Correctness
Security
Privacy
State management
Error handling
Cancellation
Persistence
Testing
Performance
Build impact
Dependency impact
```

## 101.1 Reviewer Questions

1. Does this change add a new dependency? Why?
2. Does it cross a layer boundary? Is the direction valid?
3. Does it expose Android/provider details upward?
4. Does it preserve user edits?
5. Can it run offline where required?
6. Does it log private data?
7. Does it add new failure modes without tests?
8. Does it change persisted data or schema?
9. Does it change export correctness?
10. Does it require a document baseline update?

---

# 102. Definition of Done for Code

A code change is **DONE** when applicable:

```text
[ ] Requirement trace is understood
[ ] Correct layer/package chosen
[ ] Dependency direction is valid
[ ] Naming/style checks pass
[ ] Unit/component tests added or updated
[ ] Error handling defined
[ ] Cancellation considered for long-running operations
[ ] Sensitive logging reviewed
[ ] User-correction authority preserved
[ ] Persistence behavior verified
[ ] Export behavior verified if affected
[ ] Offline behavior verified if affected
[ ] Documentation updated where architecture changed
[ ] Build succeeds
[ ] Relevant regression suite passes
```

---

# 103. Feature Implementation Template

Every major feature should be implemented in this order:

```text
1. Requirement / use case
2. Domain contract
3. Application use case
4. Infrastructure interface if needed
5. Infrastructure implementation
6. UI state/event contract
7. Screen/UI
8. Persistence mapping if needed
9. Tests
10. Security/privacy review
11. Documentation update
```

Avoid beginning with UI and retrofitting all business logic later.

---

# 104. Pull Request / Change Size

Prefer changes that are:

- logically coherent;
- reviewable;
- testable;
- reversible where practical.

A PR that simultaneously rewrites navigation, persistence, OCR, AI, and export is high risk and should be split unless the architecture itself requires a coordinated migration.

---

# 105. Refactoring Standard

Refactoring is allowed when it:

- removes duplication;
- strengthens boundaries;
- improves testability;
- simplifies state handling;
- reduces resource risk;
- clarifies ownership.

Do not refactor simply to create more abstractions.

Each abstraction should have a reason to exist: replacement, testing, lifecycle, ownership, or policy.

---

# 106. Performance Engineering Standard

Performance work must be measurement-driven.

Priority measurement areas:

```text
Input parsing time
Preprocessing time
OCR latency
AI model load time
AI inference time
Peak memory
Database save latency
Export latency
Cold-start time
```

Do not optimize by guesswork and do not make architecture more complex without benchmark evidence.

---

# 107. Resource Exhaustion Safeguards

The application SHALL have explicit policies for:

- maximum supported input size once validated;
- maximum practical page count once validated;
- large image dimensions;
- repeated model loads;
- large tables;
- repeated exports;
- temporary file accumulation.

Limits are **REQUIRES TECHNICAL VALIDATION** and MUST be based on the supported device matrix rather than arbitrary numbers.

---

# 108. Model Resource Safety

Before loading a model resource:

```text
Locate
 ↓
Validate metadata
 ↓
Validate integrity where supported
 ↓
Check compatibility
 ↓
Load
 ↓
Expose READY/LOADED state
```

Do not assume that a file existing on disk means a model is safe or compatible.

---

# 109. OCR Resource Safety

OCR language/resource files SHALL be treated as application resources with explicit versioning and compatibility checks where needed.

Do not blindly scan arbitrary model/data directories for executable or unsafe resources.

---

# 110. Release Architecture Gate

Before a release candidate is declared ready, verify:

```text
[ ] Actual project stack recorded
[ ] Build configuration verified
[ ] Dependencies reviewed
[ ] Processing pipeline passes
[ ] User corrections survive reopen
[ ] Export values match authoritative saved data
[ ] Offline core path passes
[ ] No hidden network dependency
[ ] Logging privacy review passes
[ ] File/path security tests pass
[ ] Migration tests pass
[ ] Model/OCR readiness verified
[ ] Cancellation tests pass
[ ] Release build installs and runs
```

---

# 111. Architecture Change Control

A code change requires architectural review when it:

- introduces a new layer/module;
- changes dependency direction;
- introduces a backend/API dependency;
- changes canonical data structures;
- changes persistence semantics;
- replaces OCR/AI providers;
- changes model packaging;
- changes security/privacy guarantees;
- changes authoritative-data semantics.

Architecture changes SHALL update the relevant source-of-truth documents instead of leaving code and documentation inconsistent.

---

# 112. Decision Record Standard

For meaningful architectural decisions, record:

```text
Decision ID
Context
Options considered
Decision
Rationale
Consequences
Status
Date
```

Example:

```text
ADR-SNAP-001
Decision: isolate OCR behind OCRService
Reason: provider replacement + testability
Status: PROPOSED until generated project integration is confirmed
```

---

# 113. Recommended Package-Level Dependency Graph

```text
presentation
    ↓
application
    ↓
domain
    ↑
    │ interfaces
    │
┌───┴────────────────────────────────────────────┐
│ data        processing       export   platform │
└────────────────────────────────────────────────┘
```

Important clarification:

- `processing` may depend on domain contracts.
- `data` may implement repository interfaces.
- `export` may depend on canonical domain/export contracts.
- `platform` provides Android adapters.
- domain remains as platform-independent as reasonably possible.

---

# 114. Example End-to-End Dependency Trace

When the user presses **Process**:

```text
ProcessingScreen
   ↓
ProcessingUiEvent.Start
   ↓
ProcessingViewModel / StateHolder
   ↓
ProcessDocumentUseCase
   ↓
DocumentProcessingCoordinator
   ├── InputValidator
   ├── Preprocessor
   ├── OCRService
   ├── AIService
   ├── ExtractionValidator
   └── ResultMapper
   ↓
Validated ExtractionResult
   ↓
Repository / Review State
   ↓
ResultsScreen
```

The screen never needs to know which OCR library or AI runtime produced the result.

---

# 115. Example End-to-End Save Trace

```text
Editor UI
   ↓ SaveChanges
EditorViewModel / StateHolder
   ↓
SaveDocumentUseCase
   ↓
Domain validation
   ↓
DocumentRepository
   ↓
Database mapper
   ↓
SQLite transaction
   ↓
Saved authoritative result
```

No AI call occurs merely because the user saved.

---

# 116. Example End-to-End Export Trace

```text
Export UI
   ↓
ExportDocumentUseCase
   ↓
Repository: load authoritative saved result
   ↓
Export projection
   ↓
ExportService
   ↓
Selected exporter
   ↓
Local export file
   ↓
ShareService (optional)
```

Export should operate from saved authoritative data, not an obsolete in-memory AI candidate.

---

# 117. Example Error Trace

```text
Tesseract exception
   ↓
OCR adapter maps failure
   ↓
OCRFailure(errorCode)
   ↓
DocumentProcessingCoordinator
   ↓
ProcessingResult.Failure
   ↓
ProcessingUiState.Error
   ↓
User-safe recovery message
```

The raw engine exception remains below the application boundary.

---

# 118. Conditional Kotlin + Jetpack Compose Coding Profile

**Activation condition:** only if the actual generated project confirms Kotlin + Jetpack Compose.

## 118.1 Recommended Shape

```text
@Composable screen
      ↓
ViewModel / state holder
      ↓
Use case
      ↓
Repository / Service interfaces
      ↓
Adapters
```

## 118.2 Kotlin Guidance

Prefer:

- immutable `data class` UI state;
- sealed interfaces/classes for finite UI states/events when useful;
- constructor injection;
- structured concurrency;
- `suspend` functions for suspendable operations;
- explicit domain types;
- null-safety instead of defensive runtime crashes.

Avoid:

- mutable global singletons for business state;
- `!!` unless the invariant is proven and documented;
- launching arbitrary coroutines from composables;
- passing `Context` into domain objects;
- repository calls directly from composable functions.

## 118.3 Compose Guidance

Prefer:

```text
Composable
    receives state
    emits events
```

rather than composables owning business logic or repository calls.

Do not perform expensive document work directly during composition.

---

# 119. Conditional Java/XML Profile

If the generated project is Java/XML rather than Kotlin/Compose, preserve the same architectural principles while using idiomatic Android mechanisms for:

- lifecycle-aware state;
- background execution;
- navigation;
- view binding/UI state;
- repository injection;
- test doubles.

Do not rewrite the codebase merely to move from Java/XML to Kotlin/Compose unless a separate migration decision is approved.

---

# 120. Generated-Project Preservation Rule

The architecture SHALL adapt to the generated application rather than forcing a wholesale rewrite.

When the actual project is inspected:

1. preserve working code where boundaries are acceptable;
2. refactor incrementally;
3. introduce missing abstractions at actual seam points;
4. avoid renaming every package without benefit;
5. avoid replacing build tooling without evidence;
6. avoid changing UI technology solely for architectural preference.

The target is **production-quality architecture with controlled change**, not architecture cosplay.

---

# 121. Implementation Sequence

Recommended engineering order:

```text
Phase 0 — Inspect generated project
      ↓
Phase 1 — Stabilize project/build/package structure
      ↓
Phase 2 — Establish core/domain contracts
      ↓
Phase 3 — Establish application/use-case boundaries
      ↓
Phase 4 — Establish persistence/file boundaries
      ↓
Phase 5 — Integrate document acquisition
      ↓
Phase 6 — Integrate preprocessing
      ↓
Phase 7 — Integrate OCR adapter
      ↓
Phase 8 — Integrate AI/model manager
      ↓
Phase 9 — Implement structured validation
      ↓
Phase 10 — Review/editor
      ↓
Phase 11 — Export/share
      ↓
Phase 12 — History/settings
      ↓
Phase 13 — Security/performance hardening
      ↓
Phase 14 — Release validation
```

This sequence aligns with the implementation plan while preventing UI-first coupling to an unvalidated processing stack.

---

# 122. Technical Decision Register

| Decision | Status | Current position |
|---|---|---|
| Android target | CONFIRMED | Primary application platform |
| Google AI Studio Build an Android app | CONFIRMED | Current starting workflow |
| Layered/modular architecture | PROPOSED | Recommended implementation shape |
| Feature/hybrid package organization | PROPOSED | Preferred maintainability structure |
| SQLite | CONFIRMED source-backed | Local persistence requirement |
| Tesseract OCR | CONFIRMED source-backed context | Integration still requires validation |
| Offline AI capability | CONFIRMED | Exact model/runtime TBD |
| Backend for MVP | REJECTED / not required | Core processing remains local |
| REST API for MVP | REJECTED / not required | No server dependency |
| Cloud DB for MVP | REJECTED / not required | Local persistence only |
| Exact root package | REQUIRES TECHNICAL VALIDATION | Obtain from generated project |
| Exact programming language | REQUIRES TECHNICAL VALIDATION | Obtain from generated project |
| Exact UI framework | REQUIRES TECHNICAL VALIDATION | Obtain from generated project |
| Exact Android architecture pattern | REQUIRES TECHNICAL VALIDATION | Adapt to generated code |
| Exact DI framework | TBD | Use smallest justified mechanism |
| Exact OCR Android integration | REQUIRES TECHNICAL VALIDATION | Benchmark/compatibility proof |
| Exact AI runtime | TBD / validation | Benchmark candidate(s) |
| Exact model | TBD | Benchmark candidate(s) |
| Exact export libraries | TBD / validation | Select by format/security/license/performance |
| Exact max page/size limits | REQUIRES TECHNICAL VALIDATION | Device benchmark |
| Encryption | TBD | Do not claim until implemented/validated |
| PIN/biometric lock | TBD | Optional/product decision |
| Secure delete | TBD | Optional/product decision |

---

# 123. Required Evidence Before Architecture Freeze

The architecture may move unresolved items to **CONFIRMED** only after evidence exists for:

```text
[ ] Generated project source tree inspected
[ ] Root namespace/applicationId verified
[ ] Language verified
[ ] UI framework verified
[ ] Build system verified
[ ] Existing modules verified
[ ] Existing dependency versions verified
[ ] Existing navigation/state pattern verified
[ ] SQLite implementation path verified
[ ] File storage API verified
[ ] Camera/picker path verified
[ ] OCR integration verified
[ ] AI runtime/model integration verified
[ ] Test framework verified
[ ] Native ABI/resources verified if applicable
[ ] Actual build succeeds
[ ] Offline core flow demonstrated
```

---

# 124. Architecture Review Checklist

```text
[ ] Presentation has no direct infrastructure access
[ ] Application layer owns use cases
[ ] Domain is provider-neutral
[ ] Repositories are behind interfaces
[ ] OCR is behind an abstraction
[ ] AI is behind an abstraction
[ ] Model lifecycle is isolated
[ ] Export is strategy-based
[ ] File storage is isolated
[ ] Platform APIs are isolated
[ ] AI output is validated
[ ] User edits are authoritative after save
[ ] Processing state machine is explicit
[ ] Cancellation is supported where practical
[ ] Temporary files have owners/cleanup
[ ] Sensitive data is absent from routine logs
[ ] Offline path has no hidden network call
[ ] Database/file consistency is explicitly handled
[ ] Tests cover high-risk boundaries
[ ] Build/release configuration is evidence-backed
```

---

# 125. Documentation Synchronization Rule

When code changes an architectural contract, update the appropriate source document.

Examples:

| Code change | Required documentation review |
|---|---|
| New repository boundary | SYSTEM_ARCHITECTURE / DATABASE / CODE_ARCHITECTURE |
| New AI runtime | AI_OCR / TRD / BUILD_RELEASE / CODE_ARCHITECTURE |
| New export format | EXPORT / SRS / CODE_ARCHITECTURE |
| New persisted entity | DATABASE / DATA_SCHEMA |
| New security control | SECURITY_PRIVACY |
| New build dependency | BUILD_RELEASE / TRD |
| New navigation flow | UI_UX / FRONTEND |
| New API/network dependency | API_SPECIFICATION / TRD / SYSTEM_ARCHITECTURE |

Source and code must evolve together.

---

# 126. Final Architectural Baseline

SnapData is defined as a **local-first Android application whose codebase is organized around explicit application/domain contracts and replaceable infrastructure adapters**.

The durable baseline is:

```text
┌──────────────────────────────┐
│ Presentation                 │
│ Screens / UI State / Events  │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│ Application                  │
│ Use Cases / Orchestration    │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│ Domain                       │
│ Canonical Models / Contracts │
└───────┬─────────┬────────────┘
        │         │
        ↓         ↓
   ┌─────────┐  ┌─────────────────┐
   │  Data   │  │ Infrastructure  │
   │ SQLite  │  │ OCR / AI / OS   │
   │ Files   │  │ Camera / Picker │
   └────┬────┘  └────────┬────────┘
        │                │
        └───────┬────────┘
                ↓
       Canonical Result
                ↓
        Review / Edit
                ↓
             Save
                ↓
         Export Strategy
                ↓
       Excel / CSV / JSON / PDF
                ↓
         Android Sharing
```

The architecture intentionally does **not** force React Native, TypeScript, Node.js, Express.js, Kotlin, Jetpack Compose, a specific DI framework, a specific AI runtime, or a specific SQLite API without project evidence. Historical workflow materials mention React Native, TypeScript, Node.js, Express.js, SQLite, Tesseract OCR and Offline AI, while the current technical baseline explicitly says the actual generated Android project must take precedence for implementation-specific choices.

---

# 127. Appendix A — Source Alignment Summary

## PRD / SRS

Establish the product as an Android/mobile application converting PDFs/images into structured, editable data through OCR and AI, with local/offline operation after initial AI setup, local storage/history, review/editing, and Excel/CSV/JSON/PDF export.

## TRD / System Architecture

Establish Android + Google AI Studio as the current implementation target, SQLite as source-backed local persistence, Tesseract OCR as source-backed OCR context, offline AI as a capability requirement, no mandatory MVP backend/API, and mandatory actual-project inspection before implementation-specific confirmation.

## Frontend / UI_UX

Establish screen-oriented state, explicit processing stages, review/edit flows, history, settings, cancellation, unsaved-edit handling, and the rule that UI does not directly access SQLite/OCR/AI/export internals.

## Database / Data Schema

Establish the logical persistence model for documents, jobs, extraction results, fields, tables/rows/cells, export records, settings and model metadata; define original-vs-current values and user correction authority; and keep model binaries outside SQLite.

## AI/OCR / Document Processing

Establish stage isolation, OCR and AI abstractions, model lifecycle, raw-vs-normalized OCR, validated structured output, confidence semantics, partial-result behavior, interruption/cancellation handling, and safe resource management.

## Export / Security / Testing / Build

Establish canonical-data export, path/file security, Android sharing as a privacy boundary, offline verification, logging privacy, build/release gates, dependency review, migration testing, and evidence-backed release readiness.

## Workflow Diagram

The supplied workflow image shows the sequence from document input/acquisition and image preprocessing through OCR, offline AI, structured data generation, review/editing, local storage, export, and document history. The footer provides historical/source-backed technology context, but those labels do not override the current implementation rule requiring inspection of the generated Android project.

---

# 128. Appendix B — Immediate Engineering Actions

The next engineering action is not to guess the missing stack. It is to inspect the actual generated project and record:

```text
1. Namespace / applicationId
2. Language
3. UI toolkit
4. Module tree
5. Gradle/AGP/JDK configuration
6. Current navigation/state pattern
7. Existing dependencies
8. Existing camera/file picker implementation
9. SQLite implementation
10. OCR integration
11. AI runtime/model integration
12. Tests/build configuration
```

Then update this document's **REQUIRES TECHNICAL VALIDATION / TBD** entries with evidence-backed values.

---

# 129. Appendix C — Architecture Sign-Off

| Area | Status |
|---|---|
| Product alignment | READY |
| System architecture alignment | READY |
| Layering strategy | PROPOSED / READY FOR IMPLEMENTATION |
| Feature boundaries | PROPOSED / READY FOR ADAPTATION |
| Domain/data separation | READY |
| OCR abstraction | READY |
| AI abstraction | READY |
| Export abstraction | READY |
| Persistence boundary | READY |
| Offline architecture | READY |
| Security/logging rules | READY |
| Testing boundaries | READY |
| Actual generated project mapping | BLOCKED / REQUIRES TECHNICAL VALIDATION |
| Exact package/module tree | BLOCKED / REQUIRES TECHNICAL VALIDATION |
| Exact language/UI/build stack | BLOCKED / REQUIRES TECHNICAL VALIDATION |

**Engineering decision:** do not freeze implementation-specific details until the generated Google AI Studio Android project is available for direct inspection.

---

# 130. Appendix D — Reference Documents

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
17. Original project specification PDF: `SnapData _ Ai-Powered Intelligent Document Processing & Data Extraction System.pdf`
18. Workflow diagram: `SnapData WorkFLow.png`

---

**Document:** `SnapData_CODE_ARCHITECTURE_v1.0.md`  
**Status:** Engineering Baseline  
**Version:** 1.0  
**Date:** 30 August 2026
