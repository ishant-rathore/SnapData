# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Software Development Guidelines, Coding Standards & Engineering Workflow

**Document:** `SnapData_DEVELOPMENT_GUIDELINES_v1.0.md`  
**Version:** 1.0  
**Status:** Baseline / Engineering Standard  
**Target Platform:** Android  
**Development Workflow:** Google AI Studio — Build an Android app  
**Audience:** Developers, reviewers, testers, technical leads, project maintainers

> **Implementation verification rule:** The actual Google AI Studio-generated Android project is the implementation authority for language, UI toolkit, build system, package/module structure, dependencies, Android APIs, OCR integration, AI runtime, storage APIs and test stack. The project specification set currently contains architecture and engineering requirements but does not include the generated Android source tree, Gradle configuration or build artifact. Therefore this document deliberately avoids promoting an unverified stack such as Kotlin, Java, Jetpack Compose, XML, Room, Retrofit, Ktor, React Native, TypeScript, Node.js or Express into a confirmed implementation requirement.

---

# 1. Purpose

This document defines the practical engineering rules developers must follow while implementing, reviewing, testing, documenting, building and maintaining SnapData.

It complements `SnapData_CODE_ARCHITECTURE_v1.0.md`. It does not replace architectural decisions, product requirements, schema definitions, security controls, testing strategy, export contracts, or release engineering.

The primary goal is to keep the codebase understandable, testable, secure, offline-first and resistant to tight coupling between UI, OCR, AI, storage and export.

---

# 2. Scope

These guidelines apply to:

- Android UI and presentation code
- application and use-case logic
- domain models and invariants
- document acquisition and file handling
- image preprocessing
- OCR adapters
- AI adapters and model management
- structured-data validation and normalization
- SQLite/local persistence
- local file storage
- export generation
- Android sharing
- asynchronous processing and cancellation
- logging and diagnostics
- tests and test data
- configuration and dependencies
- Git workflow, pull requests and code review
- build, release and documentation updates

The rules apply to MVP implementation and remain the default baseline for later versions unless explicitly superseded.

---

# 3. Source-of-Truth Hierarchy

Developers must resolve conflicts using the project source-of-truth hierarchy rather than personal preference.

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
12. `SnapData_TESTING_v1.0.md`
13. `SnapData_SECURITY_PRIVACY_v1.0.md`
14. `SnapData_BUILD_RELEASE_v1.0.md`
15. `SnapData_IMPLEMENTATION_PLAN_v1.0.md`
16. `SnapData_API_SPECIFICATION_v1.0.md`
17. `SnapData_CODE_ARCHITECTURE_v1.0.md`
18. Actual generated Google AI Studio Android project, once available, for implementation-specific verification

When a source document is revised, affected engineering decisions and this document must be reviewed for alignment.

---

# 4. Development Status Vocabulary

Use the project status terms consistently:

| Status | Meaning |
|---|---|
| **CONFIRMED** | Explicitly established by requirements or verified implementation evidence. |
| **PROPOSED** | Recommended approach awaiting final implementation validation or approval. |
| **TBD** | Decision has not yet been made. |
| **REQUIRES TECHNICAL VALIDATION** | Intent is known, but compatibility, feasibility or measured behavior remains unverified. |
| **REJECTED** | Explicitly excluded from the current baseline. |
| **BLOCKED** | Work cannot safely continue because a dependency or decision is unresolved. |
| **DONE** | Acceptance criteria and Definition of Done are satisfied. |

Do not silently convert TBD or proposed technology into a confirmed project fact.

---

# 5. Development Principles

SnapData development prioritizes:

- correctness over cleverness
- simplicity over unnecessary abstraction
- maintainability over short-term speed
- security by design
- privacy by default
- offline-first behavior
- explicit state and failure semantics
- deterministic processing
- testability
- modularity
- predictable resource use
- accessibility
- observability without document-content leakage

A feature is not complete merely because the happy path works. It is complete only when validation, failure, cancellation, persistence, security and regression impact have been considered.

---

# 6. Golden Rules

Developers MUST:

1. Follow the approved architecture.
2. Keep UI separate from business logic.
3. Keep infrastructure behind interfaces or adapter boundaries.
4. Preserve core offline operation after required AI model setup.
5. Validate every external input.
6. Treat raw AI output as untrusted data.
7. Never silently overwrite saved user corrections.
8. Never expose sensitive information through logs or error messages.
9. Never hard-code secrets.
10. Write tests for meaningful business and transformation logic.
11. Keep changes focused and reviewable.
12. Update documentation when architecture, contracts or behavior changes.
13. Never report cancelled work as successful.
14. Never fabricate missing document information.
15. Never add backend/network dependencies to MVP without an approved baseline change.

---

# 7. Actual Android Project Inspection Gate

The implementation-specific coding profile must be finalized immediately after the actual generated project is available.

Inspect at minimum:

- source files and file extensions
- root package/namespace
- Gradle settings and build files
- module structure
- Android SDK configuration
- UI framework
- Android architecture libraries
- dependency/version management
- test framework and source sets
- manifest and permissions
- camera implementation
- file-picker implementation
- SQLite implementation
- OCR library/runtime
- AI runtime/model integration
- file-storage APIs
- background/asynchronous execution strategy
- export libraries
- sharing mechanism

Only after this inspection should language-specific rules be frozen.

### Technology-specific rule

If the generated application is Kotlin + Jetpack Compose, use idiomatic Kotlin, Compose state patterns, coroutines/structured concurrency, Android lifecycle practices and the exact libraries present in the project.

If the actual project uses another stack, replace the examples with idiomatic rules for that stack. Do not force historical technologies from older diagrams into the codebase.

The supplied workflow diagram visually shows React Native, TypeScript, Node.js, Express.js, SQLite, Tesseract OCR, Offline AI and Excel/CSV/JSON/PDF as technology context. That visual baseline must not override the newer Android implementation-validation rule.

---

# 8. Code Organization

Follow `SnapData_CODE_ARCHITECTURE_v1.0.md`.

Every file/class/module must have a clear responsibility.

Before creating a new component, ask:

- Which layer owns it?
- What exact responsibility does it have?
- Who consumes it?
- Can it be tested independently?
- Does an existing component already perform the job?
- Does the new component reduce coupling or merely move code around?

Avoid duplicate implementations of the same domain behavior.

Prefer cohesion over directory volume. Create a package/module when it establishes a real responsibility, dependency or test boundary.

---

# 9. Naming Standard

Names must communicate intent and domain meaning.

## 9.1 General Rules

- Prefer nouns for state/data types.
- Prefer verbs for operations.
- Prefer domain terminology already used in the source documents.
- Avoid abbreviations unless universally understood within Android or the chosen language.
- Avoid names whose meaning depends on implementation details.

## 9.2 Classes / Types

Use idiomatic PascalCase or the equivalent convention for the actual language.

Prefer:

- `DocumentProcessor`
- `ProcessingResult`
- `ExtractionValidator`
- `ExportService`
- `DocumentRepository`

Avoid vague names:

- `Manager`
- `Helper`
- `Utils`
- `Common`
- `Misc`

These names are acceptable only where the responsibility is genuinely broad and explicitly justified.

## 9.3 Interfaces / Protocols

Use the language's idiomatic interface naming. The name must describe a capability or stable boundary, not the implementation.

Examples:

- `OcrProvider`
- `AiService`
- `DocumentRepository`
- `Exporter`
- `ModelManager`

A provider interface should not expose provider-specific implementation details.

## 9.4 Functions

Use descriptive action names:

- `validateDocumentInput`
- `processDocument`
- `saveReviewedResult`
- `exportStructuredData`

Avoid:

- `doIt`
- `runStuff`
- `handleData`
- `processAll`

## 9.5 Variables

Use the narrowest meaningful name. Boolean values should read naturally as predicates when the language permits:

- `isCurrent`
- `hasUnsavedChanges`
- `modelReady`

Do not use ambiguous state names such as `flag`, `temp`, `data2` or `thing`.

## 9.6 Constants

Constants must describe stable semantic values rather than magic numbers. Use the language's idiomatic constant convention.

## 9.7 Packages / Modules

Use lowercase package naming consistent with the actual Android language/toolchain.

Group by responsibility rather than arbitrary file type when the architecture calls for feature/domain/layer boundaries.

## 9.8 Files

File names should make the primary type or responsibility immediately discoverable. Avoid catch-all files.

## 9.9 Resources

Use the Android/project-native resource naming convention. Resource names should be descriptive and stable because renaming can create large review and localization costs.

## 9.10 Database Objects

Follow `SnapData_DATABASE_v1.0.md` and `SnapData_DATA_SCHEMA_v1.0.md` exactly for entity names, column semantics, enum values and migration identifiers.

## 9.11 Test Names

Test names must explain behavior and expected outcome. Use the idiomatic style of the actual test stack.

Examples:

- `rejectsUnsupportedMimeType`
- `preservesUserEditedFieldAfterReprocessing`
- `exportsCurrentSavedResult`
- `cancellationRemovesTemporaryFiles`

---

# 10. File Size & Class Complexity

Do not impose arbitrary line-count limits.

Instead, monitor responsibility, coupling, branching, state count, dependency count and testability.

Split a component when it:

- owns unrelated responsibilities
- requires many unrelated dependencies
- has multiple independent sources of state
- contains substantial repeated logic
- becomes difficult to test without the Android runtime
- mixes orchestration with low-level implementation
- becomes the only place where unrelated behaviors can be changed

Avoid giant:

- Activities
- Screens
- ViewModels/state holders
- repositories
- OCR processors
- AI processors
- export services
- utility classes

A large class is not automatically wrong; a class with unclear ownership is.

---

# 11. Function Design

Functions should:

- perform one logical operation
- have explicit inputs
- return predictable results
- minimize hidden side effects
- use the smallest necessary state surface
- make failure semantics explicit
- be straightforward to test

Prefer readable code over clever expressions.

Avoid functions that silently perform multiple external operations unless the function is an intentional application-level orchestration boundary.

Do not make a function pure-looking while it secretly mutates repositories, files or shared state.

---

# 12. Comments & Code Documentation

Comments should explain **why**, not merely repeat **what** the code already says.

Good reasons for comments:

- security decisions
- compatibility workarounds
- non-obvious Android lifecycle behavior
- AI/OCR assumptions
- migration constraints
- algorithmic trade-offs
- intentionally preserved raw data
- provider limitations

Bad comment:

```text
// Increment i by 1
```

Good comment:

```text
// Preserve the original OCR token because normalization can alter identifiers.
```

Remove stale comments immediately after the code changes.

---

# 13. Documentation Synchronization

Documentation must be treated as part of the implementation.

Update the relevant source document or implementation notes when changing:

- architecture
- packages/modules
- domain contracts
- data schema
- database migrations
- APIs/internal interfaces
- processing stages
- OCR behavior
- AI behavior
- confidence semantics
- user-edit authority
- export behavior
- security/privacy behavior
- permissions
- build configuration
- release process

Documentation changes should be part of the same PR when practical.

---

# 14. UI Development

UI code should focus on:

- rendering
- user interaction
- state presentation
- navigation
- accessibility semantics

UI must NOT directly:

- access SQLite
- manipulate low-level filesystem paths
- instantiate OCR engines
- invoke the AI runtime
- generate Excel/CSV/JSON/PDF files
- implement domain validation rules
- perform persistence transactions

Use the project-defined presentation → application/domain → infrastructure flow.

---

# 15. UI State

Use one authoritative state representation for each screen or workflow.

Required conceptual states include:

- Loading
- Ready
- Processing
- Success
- Empty
- Error
- Cancelled

Avoid contradictory Boolean combinations such as:

```text
isLoading = true
isError = true
isSuccess = true
```

Prefer a sealed/enum/result/state model or the equivalent pattern supported by the actual stack.

State must be derived from a clear source of truth rather than multiple independently mutable flags.

---

# 16. User Action Flow

Business behavior must follow a predictable path:

```text
User Action
    ↓
UI Event
    ↓
ViewModel / State Holder / Controller
    ↓
Use Case / Application Service
    ↓
Domain / Infrastructure Boundary
    ↓
Result
    ↓
UI State
```

Do not place business rules directly inside click handlers, Compose event lambdas, Activities, Fragments or equivalent UI callbacks.

---

# 17. Navigation

Navigation must follow the architecture and use one consistent ownership model.

Rules:

- screens declare navigation intent, not routing internals
- navigation arguments must be validated
- do not duplicate route construction logic
- avoid navigation side effects during rendering
- preserve Android back behavior defined by UI/UX
- do not lose unsaved edits without an explicit decision

A screen should not know how another layer stores documents or creates exports.

---

# 18. Responsive / Adaptive UI

SnapData targets Android phones first. Tablet-specific layouts are not assumed as a committed baseline unless later approved.

Nevertheless, the implementation should avoid hard-coded dimensions that make future adaptation unnecessarily expensive.

Responsive rules:

- prefer adaptive layout primitives from the actual UI toolkit
- avoid device-specific pixel assumptions
- respect system bars and safe insets
- support text scaling
- allow content-driven height where practical
- ensure horizontal actions remain usable on smaller screens
- make tables usable through scrolling or controlled horizontal layouts

UI should remain functional at the smallest validated viewport in the project's compatibility matrix.

---

# 19. Accessibility

Accessibility is a development requirement, not polish.

Developers must consider:

- meaningful semantic labels
- readable text
- accessible touch targets
- screen-reader behavior
- keyboard navigation where applicable
- text scaling
- color-independent status communication
- error/status announcements
- focus behavior after actions

Do not communicate a critical state only through color.

Tables, confidence indicators, error states and processing status must remain understandable without relying solely on visual decoration.

---

# 20. Document Input Validation

Every document entering SnapData must be validated before processing.

Validate at the platform-appropriate boundary:

- MIME type
- extension where applicable
- accessibility/readability
- file size
- supported format
- page count/structure where applicable
- basic corruption or malformed-input conditions

Treat file picker output, camera output and imported paths/URIs as untrusted external input.

Unsupported documents must fail safely and produce a useful user-facing message.

---

# 21. File Security

Never trust filenames, URIs, paths or file extensions supplied by users or external applications.

Protect against:

- path traversal
- malformed filenames
- invalid extensions
- oversized input
- malformed PDFs/images
- unexpected URI schemes
- temporary-file collisions
- unsafe destination construction

Never construct filesystem paths by concatenating raw user input.

Use the Android/project-native file abstraction and content-URI model supported by the validated implementation.

---

# 22. Document Acquisition

Document acquisition must be isolated behind an acquisition boundary.

Supported conceptual sources:

- camera capture
- image import
- PDF import

The acquisition layer is responsible for producing a validated document reference suitable for processing.

It is not responsible for OCR or AI.

Acquisition failure must not leave orphaned database metadata or inaccessible temporary files.

---

# 23. Image Processing

Image preprocessing must remain isolated from UI and should not mutate the original source document unless explicitly required by the approved design.

Potential stages include:

- crop
- rotation
- perspective correction
- enhancement
- brightness/contrast adjustment
- noise reduction
- shadow removal
- other validated image-quality operations

Preserve the original document where practical so that the user can compare the processed result with the source.

Do not apply destructive transformations that alter document meaning merely to improve OCR appearance.

---

# 24. OCR Development

OCR must remain behind the project's OCR abstraction.

The UI must never directly depend on an OCR library or engine.

Conceptual lifecycle:

```text
READY
  ↓
PROCESSING
  ↓
COMPLETED / FAILED / CANCELLED
```

Rules:

- preserve raw OCR output where practical
- preserve provider confidence when available
- do not invent confidence values
- do not assume coordinates are available unless the provider actually supplies them
- do not silently discard OCR warnings
- do not equate OCR completion with correct extraction

Tesseract OCR is source-backed project context, but exact Android integration remains subject to implementation validation.

---

# 25. AI Development

AI functionality must remain behind the AI abstraction.

The UI must never directly depend on the AI runtime.

The AI layer is responsible for AI-specific operations; it is not the source of truth for persisted user data.

AI output must always be treated as untrusted candidate data.

---

# 26. AI Output Validation

Required pipeline:

```text
AI Output
   ↓
Parse
   ↓
Schema Validate
   ↓
Normalize
   ↓
Map to Domain Model
   ↓
Apply Warnings / Confidence Semantics
   ↓
Application Review
   ↓
Persist
```

Invalid output must never silently enter the database.

Validation must catch at least:

- malformed structures
- missing required fields
- invalid enum values
- wrong field types
- impossible or suspicious values where domain rules exist
- duplicate or contradictory structure where prohibited

Do not "repair" invalid AI output by silently inventing content.

---

# 27. AI Hallucination Prevention

SnapData must not fabricate information that is absent or unresolved in the source document.

When a value cannot be determined, represent it using the project's unknown/unresolved semantics.

Never invent:

- names
- dates
- amounts
- addresses
- account numbers
- identifiers
- table values
- document facts

AI-derived information is always subordinate to evidence and later human correction.

---

# 28. Confidence Handling

Confidence semantics must follow `SnapData_AI_OCR_v1.0.md`, `SnapData_DATA_SCHEMA_v1.0.md` and `SnapData_UI_UX_v1.0.md`.

Rules:

- preserve provider confidence where available
- never assign arbitrary percentages merely to fill UI space
- do not compare confidence values across providers without an approved normalization model
- represent unavailable confidence as unavailable, not zero
- visually surface low-confidence information for review according to UX rules

Confidence describes model/system confidence. It does not mean correctness guaranteed.

---

# 29. User Edits and Authority

User corrections are authoritative after saving.

The system must distinguish, where supported by the data model:

- AI-generated value
- user-modified value
- user-confirmed value

Reprocessing must never silently overwrite saved user edits.

When reprocessing is intentionally offered, the UI must make the consequences explicit and the implementation must preserve historical/edit provenance according to the data model.

---

# 30. Structured Data Rules

All downstream features must consume canonical structured data rather than independently reinterpreting raw OCR text.

The canonical model should remain aligned with the data-schema document, including:

- document identity
- page metadata
- OCR result
- extraction result
- fields
- tables
- confidence/source references
- review/validation state
- current authoritative value

Do not create parallel, conflicting representations without a defined mapping.

---

# 31. Database Development

Database access must go through the repository boundary.

Correct:

```text
UI
 ↓
Use Case
 ↓
Repository
 ↓
DAO / Database Adapter
 ↓
SQLite
```

Incorrect:

```text
UI
 ↓
SQLite
```

The domain/application layer must not depend on SQLite classes.

Do not bypass repositories merely because a query appears simple.

---

# 32. Database Changes & Migrations

Every schema change must:

1. update `SnapData_DATABASE_v1.0.md` where required
2. update schema/versioning metadata
3. update affected models
4. update repositories/DAOs
5. update migration logic
6. update tests
7. verify compatibility with existing data

Never delete or reset the local database merely to avoid writing a proper migration.

Destructive migration requires explicit approval and a defined user/data-impact strategy.

---

# 33. Database Transactions

Use a transaction whenever multiple persistence operations must succeed or fail together.

Examples:

- document creation + page metadata
- processing result + child extracted fields/tables
- related structured-data updates
- delete of dependent records
- current-result state changes

The implementation must preserve the database's integrity if execution stops halfway through a logical operation.

---

# 34. File / Database Consistency

Database and filesystem are separate failure domains.

Design for:

| Failure | Expected engineering response |
|---|---|
| DB saved, file failed | Detect inconsistency; clean/recover safely. |
| File saved, DB failed | Remove or quarantine orphaned file when safe. |
| App terminated during save | Recover or retry without corrupting saved data. |
| Storage unavailable/full | Fail predictably and preserve prior data. |
| Temporary file remains | Cleanup using explicit lifecycle rules. |

Never assume a two-system write is atomic unless the implementation explicitly makes it so.

---

# 35. File Storage Rules

Keep relational metadata in SQLite and physical document/model/export files in the appropriate Android-local storage boundary.

Do not store large model binaries inside SQLite.

Use safe, stable generated identifiers for stored files rather than trusting user-visible filenames.

Temporary files must have:

- known owner
- creation point
- cleanup point
- failure cleanup path
- collision-resistant naming

---

# 36. Export Development

Exporters consume authoritative structured data.

Required conceptual flow:

```text
Saved Structured Data
   ↓
Export Service
   ↓
Format-specific Exporter
   ↓
Generated File
   ↓
Android Share / Save
```

Export code must not independently re-run OCR or reconstruct business data from raw OCR text.

Exporters must reflect the current saved user-reviewed result.

---

# 37. Export Formats

Current supported formats:

- Excel (`.xlsx`)
- CSV
- JSON
- PDF

Exact content, field ordering, table representation, metadata inclusion and formatting follow `SnapData_EXPORT_v1.0.md`.

Export behavior must remain deterministic for identical canonical input and configuration.

---

# 38. Android Sharing

Use the content-sharing mechanism supported by the actual Android project.

Rules:

- do not expose private filesystem paths
- prefer safe content URI handling
- apply least-privilege access
- revoke temporary grants where the Android implementation requires it
- handle receiving-app failure explicitly
- do not leak document data through debug output

Sharing is a distribution boundary, not part of the core processing pipeline.

---

# 39. Asynchronous Processing

Never block the UI thread with:

- OCR
- AI inference
- PDF processing
- image processing
- large database operations
- export generation
- large model loading
- heavy serialization/deserialization

Use the asynchronous and background execution mechanism actually provided by the generated Android project.

Avoid unstructured background work that can outlive screen ownership without an explicit lifecycle strategy.

---

# 40. Cancellation

Long-running operations should support cancellation wherever technically possible.

Cancellation must:

- stop or interrupt work where the underlying operation allows it
- release resources
- clean temporary files
- preserve prior saved data
- leave the domain in a consistent state
- report `CANCELLED` truthfully

Cancellation is not success.

A cancelled operation must never be rendered as completed merely because some intermediate work finished.

---

# 41. Concurrency

Prevent conflicting operations such as:

- duplicate processing of the same document
- duplicate exports for one action
- simultaneous saves of the same result
- multiple model initializations
- conflicting history updates

Each mutable state owner must be clearly defined.

Prefer serialized access at the narrowest boundary necessary rather than global locks.

Idempotency should be used where practical for operations that may be retried.

---

# 42. Lifecycle & Interruption Handling

Android interruption scenarios must be considered during implementation:

- configuration changes
- app backgrounding
- process termination
- low-memory conditions
- permission revocation
- storage unavailability
- user navigation away from a screen

The UI must not assume a screen instance owns the lifetime of long-running work.

Persist durable progress/state only when the architecture defines it; do not use the database as an accidental job queue.

---

# 43. Memory Management

Be especially careful with:

- high-resolution camera images
- multi-page PDFs
- decoded page bitmaps
- OCR buffers
- AI input payloads
- AI output payloads
- large tables
- generated export files

Rules:

- avoid unnecessary copies
- process pages incrementally when supported
- release heavy resources promptly
- avoid retaining entire multi-page documents in UI state
- do not duplicate large strings/arrays solely for convenience

Memory optimization should preserve correctness and traceability.

---

# 44. Performance Engineering

Optimize only after identifying a real bottleneck.

Prioritize measurement around:

1. app startup
2. document acquisition
3. image preprocessing
4. OCR
5. AI inference
6. database operations
7. export generation
8. memory pressure

Record timings at stage boundaries rather than instrumenting every line.

Do not sacrifice document fidelity or user-edit integrity for speculative performance gains.

Performance targets remain those validated through the actual implementation/device matrix; do not invent unsupported numeric thresholds.

---

# 45. Large Document & Table Handling

The system must remain stable when document size, page count or table size increases within the validated product limits.

Use:

- incremental processing where practical
- bounded in-memory buffers
- lazy loading for large history sets
- pagination/virtualization for large tables when supported
- streaming export for formats/libraries that support it

Avoid building giant UI trees or loading every historical document at startup.

---

# 46. Error Handling

Every failure must have an intentional handling strategy.

Never use silent catch blocks such as:

```text
catch (Exception) { }
```

or equivalent patterns.

For every caught failure, decide explicitly whether to:

- recover
- retry
- fallback
- show an error
- return a structured failure
- log safe diagnostics
- terminate the operation safely

Do not catch broad exceptions merely to keep the application moving.

---

# 47. Error Taxonomy

Prefer typed/domain-aware errors rather than free-form strings.

Conceptual categories include:

- invalid input
- unsupported format
- acquisition failure
- preprocessing failure
- OCR failure
- AI unavailable
- AI output validation failure
- persistence failure
- storage unavailable
- export failure
- sharing failure
- cancellation
- unexpected/internal failure

Map technical failures to safe user-facing messages at the presentation boundary.

---

# 48. User-Facing Error Messages

Messages must be:

- understandable
- actionable where possible
- non-technical
- privacy-safe
- truthful

Do not expose:

- stack traces
- SQL statements
- private paths
- internal class names
- AI prompt internals
- model internals
- credentials

A technical diagnostic should be available to safe developer logs, not dumped into the user interface.

---

# 49. Logging

Routine logs may include:

- operation ID
- processing stage
- duration
- status
- safe error code
- app version
- model version
- schema version

Routine logs must NOT include:

- full OCR text
- raw document contents
- personal information
- extracted financial information
- account numbers
- API keys
- credentials
- authentication tokens
- unnecessary private file paths

Use correlation IDs/operation IDs to connect processing-stage events without logging document contents.

---

# 50. Debug Logging

Debug logging may be more verbose during local development but must still obey privacy rules.

Never create a workflow where developers must print full document contents to diagnose normal behavior.

Temporary debug instrumentation must be removed or disabled before release.

Use structured, targeted diagnostics instead:

```text
operation=1234 stage=OCR status=FAILED durationMs=842 error=OCR_ENGINE_INIT
```

Do not include the extracted text payload in the same diagnostic record.

---

# 51. Security Development Rules

Security is part of normal feature implementation.

Developers must consider:

- input validation
- permission minimization
- safe URI/file handling
- storage isolation
- temporary file cleanup
- export handling
- share boundaries
- logging
- dependency security
- model integrity
- network boundaries

Follow `SnapData_SECURITY_PRIVACY_v1.0.md` for detailed threat and control requirements.

---

# 52. Privacy Rules

SnapData is offline-first.

Core document processing must not secretly send document content over a network.

Do not introduce hidden analytics, telemetry, remote logging or cloud processing involving document contents.

If future network functionality is introduced for model setup/update, it must remain isolated from the core processing path and be explicitly documented.

---

# 53. Network Rule

The MVP does not require a backend or REST API.

Do not add network calls simply because they are technically convenient.

Where connectivity is used for approved model setup/update behavior:

- isolate it behind the model-management boundary
- validate downloads
- verify model metadata/integrity according to the security baseline
- handle partial downloads safely
- support offline operation after required model setup
- do not upload user document content as a hidden fallback

---

# 54. Dependency Management

Before adding a dependency, evaluate:

- necessity
- maintenance health
- compatibility with the actual Android stack
- security history
- license
- application size impact
- offline behavior
- transitive dependencies
- platform support
- testability

A dependency should solve a validated requirement.

Avoid adding a library when a small local implementation is clearer and safer—without re-inventing a mature security-sensitive mechanism.

---

# 55. Third-Party Code Boundaries

Third-party libraries must not bypass architecture boundaries.

Examples:

- PDF library → document/infrastructure adapter
- OCR library → OCR provider implementation
- AI runtime → AI provider/runtime adapter
- spreadsheet library → Excel exporter implementation
- PDF export library → PDF exporter implementation

Third-party objects should not leak throughout the domain model unless explicitly approved.

This makes libraries replaceable and keeps business logic provider-neutral.

---

# 56. Configuration Management

Never hard-code:

- passwords
- API keys
- private keys
- production secrets
- authentication tokens
- signing secrets

Keep configuration separate from business code according to the actual Android build system.

Configuration names should be descriptive and environment-safe.

Never commit local secret files unless they contain only non-sensitive templates.

---

# 57. AI Model Management Development Rules

The AI model lifecycle should follow the architecture and build/release documents.

Conceptual lifecycle:

```text
NOT_READY
   ↓
DOWNLOAD / INSTALL
   ↓
VERIFY
   ↓
READY
   ↓
USE
   ↓
UPDATE / REMOVE
```

Rules:

- model metadata and model binaries are separate concerns
- model binaries do not belong in SQLite
- model integrity must be checked according to the security baseline
- model initialization must not occur repeatedly for every UI recomposition/event
- model availability is an explicit application state
- a missing model must produce a specific, truthful user experience

Exact model/runtime values remain subject to actual project validation.

---

# 58. Model Initialization & Concurrency

Model initialization must be coordinated.

Avoid:

- multiple concurrent initializations
- repeated initialization on screen recomposition
- initialization on the main/UI thread
- hidden initialization triggered by unrelated actions

Expose model readiness through an explicit application/service state.

Long initialization operations must support lifecycle-aware cancellation or controlled lifetime according to the actual runtime.

---

# 59. Document Processing Pipeline

The implementation must preserve the documented pipeline:

```text
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
Structured Data Generation
        ↓
Validation / Normalization
        ↓
User Review & Edit
        ↓
Authoritative Save
        ↓
Export / History
```

The processing orchestrator coordinates stages. Each stage should have one primary responsibility.

A stage must not quietly duplicate the responsibilities of another stage.

---

# 60. Processing Stage Rules

Each stage should define:

- input contract
- output contract
- validation requirements
- progress semantics
- cancellation behavior
- failure behavior
- resource ownership
- temporary artifacts, if any

The implementation should make it possible to test stages independently where practical.

Progress must be truthful. Never fabricate percentage completion or estimated remaining time when the underlying operation cannot support a meaningful estimate.

---

# 61. Processing Orchestrator Rules

The orchestrator may coordinate:

- acquisition validation
- preprocessing
- OCR
- AI
- structured-data mapping
- result validation

The orchestrator must not become a giant implementation class containing every algorithm.

Its responsibility is sequencing and lifecycle coordination.

Provider-specific operations belong behind their respective abstractions.

---

# 62. Persistence Workflow

A typical successful save flow is:

```text
Reviewed Result
   ↓
Validate Domain Invariants
   ↓
Begin Transaction
   ↓
Persist Canonical Result + Related Records
   ↓
Commit
   ↓
Update UI State
```

Do not update the UI to show a successful save before persistence has actually succeeded.

On failure, keep the user's in-memory edits available where possible and report the persistence failure truthfully.

---

# 63. Export Correctness Rules

Before export:

1. identify the authoritative current saved result
2. validate that required export data exists
3. use the export contract for the selected format
4. generate the file outside the UI layer
5. verify successful file creation
6. expose the generated file through the safe sharing/save mechanism

Do not export stale AI output when newer user-reviewed data exists.

---

# 64. Search & History

History is a persistence/query concern, not a document-processing concern.

Rules:

- use repository/query boundaries
- avoid loading all documents just to display recent history
- search against indexed fields where the database design defines them
- preserve ordering semantics defined by the product
- keep deletion behavior aligned with file/database consistency rules

The UI should receive domain-level results rather than raw SQLite rows.

---

# 65. Forms & Validation in UI

Field validation should distinguish:

- empty/optional
- syntactically invalid
- semantically invalid
- unresolved/unknown
- low-confidence but usable
- user-confirmed

Validation rules should live in the appropriate application/domain boundary rather than being duplicated across multiple UI components.

The UI may present validation feedback but should not become the only place where integrity rules exist.

---

# 66. Unsaved Changes

Never discard user edits silently.

When leaving an edited screen:

- save explicitly
- discard explicitly
- or request user confirmation

Android Back must follow the UI/UX rules and should trigger the same unsaved-change policy as other navigation paths.

---

# 67. Input and Data Normalization

Normalization must never destroy meaningful source information.

Be particularly careful with:

- identifiers
- dates
- decimal separators
- currency values
- phone numbers
- email addresses
- leading zeros
- table alignment

Preserve original/raw values where the data model permits and maintain the relationship between original extraction and normalized/current values.

---

# 68. Unicode & Localization Safety

SnapData supports multilingual document scenarios.

Developers must test Unicode-safe behavior for:

- OCR text
- field values
- table cells
- document names where supported
- exports
- search/indexing
- UI display

Do not assume ASCII-only input.

CSV exports must follow the encoding and interoperability requirements in `SnapData_EXPORT_v1.0.md`.

---

# 69. Testing Requirements

Follow `SnapData_TESTING_v1.0.md` as the authoritative test strategy.

New meaningful logic should include appropriate tests.

At minimum, consider:

- normal behavior
- invalid input
- edge cases
- failure paths
- cancellation
- persistence
- user edits
- export correctness
- offline behavior
- resource cleanup

Do not use tests that merely increase line coverage without validating meaningful behavior.

---

# 70. Test Layers

Use the highest-value test level that verifies the behavior.

Conceptual layers:

1. unit tests
2. component/module tests
3. integration tests
4. Android/UI tests
5. end-to-end tests
6. device/compatibility checks
7. release smoke tests

Keep fast business-rule tests independent of Android infrastructure where possible.

Use real Android/device tests where platform behavior matters.

---

# 71. Test Data

Use synthetic or intentionally public/sample documents.

Never commit real:

- identity documents
- bank statements
- invoices containing personal information
- financial records
- private educational records
- private business documents

Test data must itself be treated as a project asset with controlled provenance.

---

# 72. OCR & AI Test Corpus

Maintain a representative test corpus covering:

- clean documents
- skewed documents
- low-light images
- noisy images
- multi-page documents
- tables
- key-value layouts
- mixed languages where supported
- missing fields
- ambiguous values
- unsupported formats
- malformed content

The corpus should include negative examples designed to detect hallucination and over-normalization.

---

# 73. AI Safety Tests

Required validation areas include:

- missing information remains unresolved
- fabricated values are rejected
- invalid JSON/structure is rejected
- confidence semantics are preserved
- user edits survive reprocessing
- current saved results are exported
- model-unavailable states are explicit

Treat AI regression testing as contract testing, not just screenshot testing.

---

# 74. Regression Testing

Before merging significant processing changes, verify the complete path:

```text
Input
 → OCR
 → AI
 → Structured Data
 → Review
 → Save
 → Export
 → History
```

A change in one stage must not unintentionally alter downstream semantics.

---

# 75. UI Testing

Core UI flows include:

- onboarding
- model setup
- document selection
- camera capture
- processing
- results review
- field editing
- table editing
- save
- export
- share
- history
- settings
- error recovery

Include lifecycle/Back behavior and unsaved-change scenarios where relevant.

---

# 76. Security Testing

Security validation must include:

- file-name/path traversal cases
- malformed files
- oversized files
- permission denial
- offline network audit
- temporary-file cleanup
- sensitive-log inspection
- export/share boundary checks
- model integrity checks
- storage/data consistency

Security tests are required before release for the risks identified as critical/high in `SnapData_SECURITY_PRIVACY_v1.0.md`.

---

# 77. Static Quality Gates

Before merge, the project should pass the static quality tools configured by the actual Android project, such as:

- compiler/build checks
- formatter
- linter
- static analysis
- dependency vulnerability checks where available

The exact toolchain is determined from the generated project.

Do not claim a tool is required merely because it is common in another Android stack.

---

# 78. Git Workflow

Recommended workflow:

```text
main
  ↓
feature / fix / refactor / test branch
  ↓
development
  ↓
validation
  ↓
code review
  ↓
merge
```

Adapt to the actual repository workflow once a non-empty source repository is available.

Do not create long-lived branches unless a release or organizational constraint requires them.

Keep changes small enough to review meaningfully.

---

# 79. Branch Naming

Use consistent intent-based names.

Examples:

```text
feature/document-scanner
feature/ocr-pipeline
feature/excel-export
feature/table-editor
fix/duplicate-processing
fix/file-validation
refactor/repository-boundary
test/ocr-validation
security/share-uri-hardening
build/release-baseline
```

Names should describe the change, not the ticket author's name or a temporary thought.

---

# 80. Commit Convention

Recommended prefixes:

```text
feat:
fix:
refactor:
test:
docs:
build:
chore:
perf:
security:
```

Examples:

```text
feat: add PDF document acquisition
fix: prevent duplicate document processing
test: add OCR failure cases
docs: update export architecture
security: sanitize imported filenames
```

Use one meaningful change per commit where practical.

---

# 81. Commit Rules

Commits should:

- be focused
- explain meaningful behavior changes
- avoid unrelated formatting churn
- compile/build when practical
- keep generated artifacts out of source control unless explicitly required

Avoid commits such as:

```text
stuff
changes
update
final
final2
fixing
```

A good commit should help a future maintainer understand why the repository changed.

---

# 82. Pull Request Rules

Every meaningful PR should include:

- purpose
- scope
- implementation summary
- test evidence
- screenshots for UI changes
- schema/migration notes where applicable
- security/privacy considerations where relevant
- known limitations/TBDs
- documentation updates where required

The PR should explicitly identify behavior changes that may affect downstream modules.

---

# 83. Code Review Checklist

Reviewers should examine:

### Architecture

- correct layer
- correct dependency direction
- no boundary violations
- no duplicate domain logic

### Correctness

- happy path
- edge cases
- invalid input
- authoritative save semantics
- cancellation and error behavior

### Security / Privacy

- untrusted input handling
- safe file/URI use
- no sensitive logs
- no hidden network calls
- secrets protected

### Performance

- no UI-thread blocking
- bounded memory use
- no unnecessary copies
- appropriate lifecycle behavior

### Testability

- meaningful tests added/updated
- test data safe
- regression risks addressed

### Maintainability

- clear naming
- cohesive responsibilities
- documentation aligned
- no unnecessary dependency introduced

---

# 84. Definition of Ready

A development task is ready when:

- product behavior is sufficiently understood
- owning layer/module is identified
- data/API contract is known or explicitly marked TBD
- dependencies are known
- acceptance criteria exist
- security/privacy impact is considered
- test approach is identifiable
- unresolved implementation assumptions are recorded

Do not begin large implementation work based only on an ambiguous UI description.

---

# 85. Definition of Done

A task is DONE when applicable:

- implementation follows architecture
- code is readable and named correctly
- error/cancellation behavior is defined
- meaningful tests pass
- persistence/export effects are verified
- offline behavior remains intact
- security/privacy checks pass
- documentation is synchronized
- code review is complete
- build/static checks pass
- no known release-blocking regression remains

"It works on my device" is not a Definition of Done.

---

# 86. Engineering Workflow

Use this default workflow for feature implementation:

```text
1. Read the governing requirements
        ↓
2. Identify owning layer and boundary
        ↓
3. Confirm existing implementation patterns
        ↓
4. Inspect dependencies / contracts
        ↓
5. Design smallest coherent change
        ↓
6. Implement
        ↓
7. Add or update tests
        ↓
8. Run static/build checks
        ↓
9. Run focused integration/UI tests
        ↓
10. Review security/privacy impact
        ↓
11. Update documentation
        ↓
12. Open PR
        ↓
13. Review / revise
        ↓
14. Merge
        ↓
15. Regression / release validation as required
```

Avoid coding first and discovering the architecture afterward.

---

# 87. Feature Development Workflow by SnapData Module

For document-processing features, use the relevant vertical slice:

```text
Requirement
  ↓
Input / UI contract
  ↓
Application use case
  ↓
Domain model / validation
  ↓
Provider/infrastructure boundary
  ↓
Persistence or export
  ↓
Tests
  ↓
UI integration
```

A feature is incomplete if it works only in the provider layer or only in the UI mock.

---

# 88. Change Impact Analysis

Before modifying a shared component, identify:

- upstream callers
- downstream consumers
- persistence impact
- export impact
- test impact
- UI/state impact
- security/privacy impact
- documentation impact

Examples of high-impact changes:

- changing an extraction field name
- changing confidence semantics
- altering current-result selection
- changing database relationships
- changing export serialization
- changing AI output schema
- changing file storage behavior

Prefer additive, migration-safe changes over abrupt contract breaks.

---

# 89. API / Internal Contract Development

The current MVP uses internal service boundaries rather than a required REST backend.

Internal interfaces should:

- have explicit inputs/outputs
- define success/failure/cancellation
- support progress where appropriate
- avoid UI-specific types
- keep provider details private
- preserve offline operation
- make partial/degraded results explicit

Follow `SnapData_API_SPECIFICATION_v1.0.md` for conceptual contracts.

Do not introduce HTTP just to imitate a conventional backend architecture.

---

# 90. Offline-First Development

A developer must be able to reason about every core path without assuming internet access.

Test conditions include:

- airplane mode
- no network available
- model already installed
- model missing
- model setup interrupted
- network unavailable during non-network core operations

The application must not silently fall back to cloud OCR or AI.

An offline failure must be explicit rather than disguised as a generic processing error.

---

# 91. Permission Development

Request only permissions needed by the current operation.

Camera permission rules:

- request at the user-relevant moment
- explain why it is needed when the product flow requires explanation
- handle denial gracefully
- do not repeatedly prompt without a meaningful user action

File/document access must follow the actual Android file-picker/content-access mechanism and least-privilege principles.

---

# 92. Camera Development

Camera code must remain behind a platform boundary.

The scanner UI owns presentation and capture interactions.

The camera implementation owns device/platform operations.

The processing layer receives a validated document/image reference; it should not know about preview surfaces, UI controls or device-specific camera widgets.

Handle:

- permission denial
- unavailable camera
- capture failure
- user cancellation
- multi-page workflow interruption

---

# 93. Temporary Resource Lifecycle

Every temporary resource must have explicit ownership.

For each temporary file/buffer/resource, identify:

- who creates it
- who owns it
- who can replace it
- when it is no longer needed
- who cleans it on success
- who cleans it on failure
- what happens after app interruption

Do not rely exclusively on application exit for cleanup.

---

# 94. Resource Ownership

For any object that owns native or heavy resources, the code should make ownership visible.

Examples:

- bitmap/page buffers
- file handles
- database cursors/streams
- model sessions
- camera resources
- export streams

Use the lifecycle/resource-management pattern supported by the actual stack.

Avoid static/global ownership unless required by the architecture and proven safe.

---

# 95. Observability Without Data Leakage

Engineering diagnostics should answer:

- what operation ran?
- which stage failed?
- how long did it take?
- what safe error category occurred?
- what app/model/schema version was involved?

They should not answer by exposing the entire document.

Instrumentation must never become a second data-exfiltration channel.

---

# 96. Build Integration

Every developer should use the canonical build commands and configuration defined by `SnapData_BUILD_RELEASE_v1.0.md` after the generated project has been inspected.

Do not invent alternate local build flows that bypass required validation.

Changes to:

- Gradle/build configuration
- dependencies
- manifest
- signing
- ProGuard/R8 rules
- native libraries
- model resources

must be reviewed for release impact.

---

# 97. Dependency & Build Reproducibility

Prefer pinned, reviewable dependency versions or the version-management approach already used by the generated project.

Avoid unbounded version ranges where they undermine reproducibility.

A successful local build must be reproducible by another developer using the project's documented environment.

---

# 98. Release Engineering Handoff

Before a feature is included in a release candidate:

- tests pass
- build is reproducible
- offline validation passes where relevant
- document processing regression path passes
- data migration path passes when applicable
- export validation passes
- security/privacy checks pass
- artifact is generated through the approved build process

Follow `SnapData_BUILD_RELEASE_v1.0.md` for final release gates.

---

# 99. Release-Blocking Conditions

A change must not be released when any of the following is true without explicit approved exception:

- user data can be lost
- saved user edits can be silently overwritten
- sensitive document content is emitted to logs
- unsupported files can crash the application
- core offline processing silently requires network access
- an invalid AI result can bypass validation into persistence
- a critical migration is missing
- exports can use stale/non-authoritative data
- cancellation is reported as success
- release build integrity is unverified

---

# 100. Refactoring Rules

Refactor when it reduces meaningful complexity or coupling.

Do not refactor unrelated code merely because you are already editing a nearby file.

A safe refactor should preserve observable behavior unless behavior change is the explicit purpose.

For risky refactors:

- add characterization tests first
- make smaller commits
- separate mechanical changes from behavior changes
- review generated diffs carefully

---

# 101. Technical Debt Rules

Technical debt should be visible, bounded and intentional.

When accepting a shortcut:

- document why it exists
- record the risk
- identify affected boundaries
- specify a follow-up condition when appropriate

Do not normalize TODO comments as permanent architecture decisions.

Use explicit project issues/task records for substantial debt.

---

# 102. Anti-Patterns

The following patterns are prohibited unless explicitly justified:

### UI-owned business logic

Business rules hidden in screens or event handlers.

### Direct database access from UI

Screens querying SQLite directly.

### Provider leakage

Domain models depending on OCR/AI/PDF library classes.

### AI trust shortcut

Persisting AI output without validation.

### Silent recovery

Catching failures and pretending success.

### Hidden network fallback

Uploading a document when offline processing fails.

### Magic values

Unexplained strings/numbers controlling business behavior.

### Global mutable state

Shared mutable singleton state used as an easy substitute for proper ownership.

### Copy-paste domain logic

Multiple implementations of the same validation/transformation rule.

### Unbounded in-memory processing

Loading a complete multi-page/high-resolution document plus all intermediate copies simultaneously without need.

---

# 103. Engineering Heuristics

When deciding between two valid implementations, prefer the one that:

- has fewer moving parts
- has clearer ownership
- has fewer dependencies
- is easier to test
- is easier to explain in a code review
- fails more predictably
- preserves more source evidence
- reduces irreversible data transformation
- supports offline operation naturally

Prefer boring code that is easy to audit over clever code that saves a few lines.

---

# 104. Review Questions for New Components

Before approving a new class/module, the reviewer should be able to answer:

1. Why does this component exist?
2. Which layer owns it?
3. What contract does it implement?
4. What state does it own?
5. What external systems does it depend on?
6. Can it be tested without the entire app?
7. What happens when it fails?
8. What happens when it is cancelled?
9. What resources does it own?
10. Can it leak sensitive information?
11. Could an existing component do the same job?
12. Does it need a new dependency?

If these answers are unclear, the design is probably premature.

---

# 105. Architecture Change Workflow

Architecture changes require a higher review threshold.

Use:

```text
Problem / limitation identified
        ↓
Impact analysis
        ↓
Architecture proposal
        ↓
Decision / approval
        ↓
Update source-of-truth documentation
        ↓
Implementation plan
        ↓
Migration / compatibility strategy
        ↓
Implementation
        ↓
Tests
        ↓
Release validation
```

Do not allow the codebase to drift into a new architecture without documenting the decision.

---

# 106. Documentation Change Workflow

For any contract-changing implementation, update all affected artifacts as one change set where possible.

Potentially affected documents include:

- CODE_ARCHITECTURE
- API_SPECIFICATION
- DATABASE
- DATA_SCHEMA
- AI_OCR
- DOCUMENT_PROCESSING
- EXPORT
- FRONTEND
- UI_UX
- TESTING
- SECURITY_PRIVACY
- BUILD_RELEASE
- IMPLEMENTATION_PLAN

The exact set depends on impact.

---

# 107. Production Readiness Workflow

Before calling SnapData production-ready, the team must demonstrate:

```text
Requirements Traceability
        ↓
Architecture Conformance
        ↓
Implementation Validation
        ↓
Static Quality
        ↓
Unit / Component Tests
        ↓
Integration Tests
        ↓
AI / OCR Validation
        ↓
Offline Validation
        ↓
End-to-End Validation
        ↓
Security / Privacy Validation
        ↓
Performance / Resource Validation
        ↓
Release Build Validation
        ↓
Production Smoke Test
```

The project must not declare capabilities as production-ready solely because they exist in the UI.

---

# 108. Final Engineering Checklist

Before merge/release, confirm:

## Architecture

- [ ] correct layer
- [ ] correct dependency direction
- [ ] no direct UI → database/OCR/AI/export dependency
- [ ] provider boundaries preserved

## Input & Processing

- [ ] input validated
- [ ] unsafe filenames/URIs handled
- [ ] original source preserved where required
- [ ] OCR failure explicit
- [ ] AI output validated
- [ ] unknown values not fabricated

## Data

- [ ] canonical structured model used
- [ ] user edits authoritative
- [ ] transaction boundaries correct
- [ ] migration updated when schema changes
- [ ] file/database consistency considered

## Async / Resources

- [ ] heavy work off UI thread
- [ ] cancellation handled
- [ ] duplicate operations prevented
- [ ] temporary resources cleaned
- [ ] memory use considered

## Security / Privacy

- [ ] no secrets in source
- [ ] no sensitive data in logs
- [ ] no hidden network processing
- [ ] sharing uses safe Android mechanism
- [ ] malformed/oversized inputs handled

## Testing

- [ ] unit/component tests
- [ ] failure cases
- [ ] cancellation where relevant
- [ ] persistence tests
- [ ] export tests
- [ ] regression path
- [ ] UI tests for important flows

## Documentation / Delivery

- [ ] documentation synchronized
- [ ] PR description complete
- [ ] screenshots added for UI changes
- [ ] build/static checks pass
- [ ] release impact reviewed

---

# 109. Relationship to Other SnapData Documents

This document governs **how engineers implement** the system.

It is subordinate to product and architecture decisions while providing the day-to-day coding and workflow discipline needed to execute them.

| Document | Primary responsibility |
|---|---|
| PRD | What product behavior is required |
| SRS | Functional/non-functional software requirements |
| TRD | Technical direction and validated/TBD technology decisions |
| SYSTEM_ARCHITECTURE | System boundaries and architectural structure |
| UI_UX | Interaction and visual behavior |
| FRONTEND | Presentation implementation guidance |
| DATABASE | Persistence design |
| AI_OCR | OCR/AI capability and trust boundaries |
| DOCUMENT_PROCESSING | End-to-end processing behavior |
| DATA_SCHEMA | Canonical data contracts |
| EXPORT | Output formats and export behavior |
| TESTING | Verification strategy |
| SECURITY_PRIVACY | Threat model and security/privacy controls |
| BUILD_RELEASE | Build and release engineering |
| IMPLEMENTATION_PLAN | Sequencing and delivery roadmap |
| API_SPECIFICATION | Internal/future service contracts |
| CODE_ARCHITECTURE | Code structure and architecture rules |
| DEVELOPMENT_GUIDELINES | Daily engineering/coding/review workflow |

---

# 110. Current Implementation Validation Note

At the time of this guideline's creation, the available project package did not contain the actual Google AI Studio-generated Android source tree, Gradle project, dependency lock/version catalog, Android manifest, generated build artifact or non-empty application repository source suitable for direct implementation inspection.

Therefore the following remain implementation-validation items rather than invented facts:

- exact programming language
- exact UI toolkit
- exact Android architecture libraries
- exact build configuration
- exact package/module structure
- exact SQLite integration
- exact OCR library integration
- exact AI runtime/model
- exact camera/file-picker APIs
- exact export libraries
- exact background execution mechanism
- exact test framework and device matrix

When source becomes available, update this document and the companion technical documents with the verified values.

---

# 111. Baseline Engineering Policy

The SnapData codebase should be:

> **easy to understand, hard to misuse, safe to extend, easy to test, privacy-preserving, offline-first and explicit about uncertainty.**

The engineering standard is simple:

```text
Build the smallest correct change.
Keep boundaries clear.
Validate everything untrusted.
Preserve user authority.
Fail safely.
Test meaningful behavior.
Document architectural change.
```

---

# Appendix A — Suggested PR Template

```markdown
## Purpose

<!-- What problem does this PR solve? -->

## Scope

<!-- What changed? What did not change? -->

## Architecture / Boundary Impact

<!-- List affected layers, modules, interfaces. -->

## Testing

- [ ] Unit/component tests
- [ ] Integration tests
- [ ] UI tests
- [ ] Offline validation
- [ ] Security/privacy validation
- [ ] Regression validation

## Data / Migration Impact

<!-- None / describe migration -->

## Export Impact

<!-- None / describe -->

## Screenshots

<!-- Required for UI changes -->

## Security / Privacy Considerations

<!-- Relevant risks and mitigations -->

## Documentation Updated

- [ ] Yes
- [ ] Not required

## Known Limitations / Follow-ups

<!-- Explicit TBDs or debt -->
```

---

# Appendix B — Suggested Commit Examples

```text
feat: add camera document acquisition
feat: add structured field validation
fix: preserve edited values during reprocessing
fix: clean temporary files after cancellation
refactor: isolate OCR provider from processing pipeline
test: cover malformed AI extraction payloads
test: verify current saved result is exported
docs: update database migration guidance
security: reject unsafe imported filenames
perf: reduce duplicate image buffers during OCR
build: pin validated Android dependency versions
chore: update project test fixtures
```

---

# Appendix C — First Implementation Baseline After Android Source Inspection

When the generated project is available, complete this table and commit it with the implementation baseline:

| Area | Verified value | Evidence | Status |
|---|---|---|---|
| Language | TBD | Source files | REQUIRES TECHNICAL VALIDATION |
| UI toolkit | TBD | UI source/dependencies | REQUIRES TECHNICAL VALIDATION |
| Build system | TBD | Gradle/build files | REQUIRES TECHNICAL VALIDATION |
| Root namespace | TBD | Build configuration | REQUIRES TECHNICAL VALIDATION |
| Module structure | TBD | Project tree | REQUIRES TECHNICAL VALIDATION |
| SQLite integration | TBD | Database code/dependencies | REQUIRES TECHNICAL VALIDATION |
| OCR integration | TBD | OCR adapter/dependencies | REQUIRES TECHNICAL VALIDATION |
| AI runtime/model | TBD | Model/runtime integration | REQUIRES TECHNICAL VALIDATION |
| Camera implementation | TBD | Camera source/dependencies | REQUIRES TECHNICAL VALIDATION |
| File access mechanism | TBD | Platform source | REQUIRES TECHNICAL VALIDATION |
| Export libraries | TBD | Export source/dependencies | REQUIRES TECHNICAL VALIDATION |
| Async execution | TBD | Processing source | REQUIRES TECHNICAL VALIDATION |
| Test stack | TBD | Test source/build config | REQUIRES TECHNICAL VALIDATION |
| Minimum supported Android | TBD | Build/device matrix | REQUIRES TECHNICAL VALIDATION |
```

---

**End of `SnapData_DEVELOPMENT_GUIDELINES_v1.0.md`**
