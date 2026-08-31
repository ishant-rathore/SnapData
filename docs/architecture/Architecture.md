SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
System Architecture Document
Project: SnapData
Document: System Architecture Document
Version: 1.0
Status: Draft / Architecture Baseline
Date: 30 August 2026
Implementation Target: Android application using Google AI Studio's "Build an Android app" workflow
Document Control
Item
Value
Project
SnapData
Document
System Architecture Document
Version
1.0
Status
Draft / Architecture Baseline
Date
30 August 2026
Primary Sources
SnapData_PRD_v1.0.md, SnapData_SRS_v1.0.md, SnapData_TRD_v1.0.md
Supporting Sources
Original SnapData project specification; SnapData workflow diagram
Architecture Rule
No unverified implementation choice is represented as confirmed
Technology Statuses
CONFIRMED / PROPOSED / TBD / REQUIRES TECHNICAL VALIDATION / REJECTED
Detailed Database Schema
DATABASE.md / equivalent data-schema artifact
Detailed UI/UX
UI_UX.md, FRONTEND.md
AI/OCR Implementation
AI_OCR.md
Test Cases
TESTING.md
Release/Build Details
BUILD_RELEASE.md
Source hierarchy
PRD
 ↓
SRS
 ↓
TRD
 ↓
SYSTEM ARCHITECTURE
This architecture document refines the approved product/software/technical baseline without silently replacing unresolved decisions.
Decision-status vocabulary
CONFIRMED — Established by source material or directly verified in the actual implementation.
PROPOSED — Recommended architecture direction, not yet baselined as implemented.
TBD — Decision not yet made.
REQUIRES TECHNICAL VALIDATION — Intent is established, but feasibility, compatibility, performance or integration must be verified.
REJECTED — Explicitly not selected for the current baseline.
Implementation verification rule: The exact Android language, UI framework, Android architecture pattern, module structure, dependency versions, AI model, AI runtime, OCR integration and file-storage implementation must be verified from the actual Google AI Studio-generated Android project before they are promoted to CONFIRMED.
1. Purpose
This document defines the high-level and detailed logical architecture of SnapData.
The architecture provides a single blueprint for:
system components and boundaries;
architectural layers;
module responsibilities;
document and processing data flow;
pipeline stage interactions;
storage and persistence boundaries;
OCR and AI integration boundaries;
export architecture;
state management;
error propagation;
cancellation and recovery;
privacy/security boundaries;
offline operation;
dependency direction;
concurrency/resource management;
testability and extensibility.
The document is intentionally implementation-aware but does not invent a concrete Android stack where the source documents leave that decision open.
2. Architectural Scope
SnapData is an Android/mobile, offline-first document-processing application that transforms camera captures, images and PDF documents into structured, editable information through OCR and AI.
The logical workflow is:
Document Input
    ↓
Document Acquisition
    ↓
Validation
    ↓
Image / Document Pre-processing
    ↓
OCR Processing
    ↓
Offline AI Processing
    ↓
Document Type / Field / Table Detection
    ↓
Confidence / Validation
    ↓
Structured Data Generation
    ↓
User Review & Editing
    ↓
Local Persistence
    ↓
Export
    ↓
Document History
The core processing path is local/offline after the required AI model setup. Network access is therefore an optional setup dependency, not a core-processing dependency.
3. Architectural Goals
Goal
Architectural implication
Status
Android deployment
Architecture must run inside an Android application boundary
CONFIRMED
Offline-first operation
Core OCR/AI/document flow must not require remote document upload after setup
CONFIRMED
Local processing
Document content remains on-device for the core workflow
CONFIRMED
OCR
OCR is a dedicated stage behind an adapter boundary
PROPOSED
Offline AI
AI inference is isolated behind an adapter/runtime boundary
PROPOSED
Structured extraction
Stable domain/data contract separates extraction from UI/storage
PROPOSED
User review/editing
Human-corrected result becomes authoritative for save/export
CONFIRMED
Local persistence
SQLite-backed persistence is used for application data
CONFIRMED source-backed; integration TBD
History
Saved processed records can be reopened locally
CONFIRMED
Export
Excel/CSV/JSON/PDF outputs are supported
CONFIRMED
Failure safety
Failed/cancelled operations must not be reported as completed
CONFIRMED requirement / PROPOSED implementation
Cancellation
Long-running stages expose cancellable boundaries where supported
CONFIRMED requirement / implementation TBD
Testability
Major modules have explicit boundaries
PROPOSED
Extensibility
OCR, AI, exporters and preprocessing can be replaced/extended without UI rewrite
PROPOSED
Minimal dependencies
Avoid technology choices that do not serve the local MVP
CONFIRMED principle
4. Architectural Principles
Principle
Meaning in SnapData
Status
Offline-first
Design the core document workflow around local capabilities first
CONFIRMED
Local-first data processing
Documents and extracted data should not require cloud processing
CONFIRMED
Privacy by design
Avoid unnecessary transmission/logging of sensitive content
CONFIRMED principle
Separation of concerns
UI, orchestration, domain, processing, infrastructure and storage remain distinct
PROPOSED
Dependency inversion
Stable interfaces separate domain/application logic from platform/provider implementations
PROPOSED
Testability
Components should be replaceable/mocked at boundaries
PROPOSED
Replaceable OCR provider
OCR engine is not exposed directly to presentation code
PROPOSED
Replaceable AI runtime/model
AI implementation is an infrastructure concern
PROPOSED
Replaceable export providers
Each output format is isolated
PROPOSED
Stable structured-data contract
Extraction, review, storage and export communicate through shared concepts
PROPOSED
Failure-safe processing
A failed stage cannot silently create a success state
CONFIRMED
Explicit processing states
User-visible and internal state transitions are controlled
CONFIRMED requirement
Minimal unnecessary dependencies
Keep MVP architecture simple enough for the project scope
CONFIRMED principle
Android platform alignment
Concrete implementation must follow the actual generated Android project
CONFIRMED
5. System Context
5.1 Context diagram
flowchart TB
    U[User]

    subgraph APP[SnapData Android Application]
        CORE[Local Document Processing Core]
        UI[Presentation Layer]
        STORE[Local Persistence]
        MODEL[AI Model Manager]
        EXP[Export / Sharing]
    end

    CAM[Device Camera]
    PICK[Android File Picker]
    FS[Local File Storage]
    SQL[(SQLite)]
    OCR[Local OCR Engine]
    AI[Offline AI Runtime + Model]
    SHARE[Android Sharing]
    FMT[Excel / CSV / JSON / PDF]
    NET[Network\nOptional Setup Dependency]

    U --> UI
    CAM --> CORE
    PICK --> CORE
    UI --> CORE
    CORE --> OCR
    OCR --> AI
    AI --> CORE
    CORE --> STORE
    STORE --> SQL
    STORE --> FS
    CORE --> EXP
    EXP --> FMT
    EXP --> FS
    EXP --> SHARE
    MODEL --> AI
    MODEL --> FS
    NET --> MODEL
Context interpretation
User is the primary actor.
SnapData Android Application owns the end-to-end workflow.
Device Camera supplies captured document images.
Android File Picker supplies PDF/image documents.
Local File Storage contains source documents, temporary artifacts, exports and potentially model files.
SQLite stores application metadata, structured data, processing metadata and history.
OCR Engine performs local OCR.
Offline AI Runtime + Model performs document understanding and extraction.
Android Sharing provides optional distribution of generated files.
Network appears only on the model setup/download path; it is not part of the core processing path.
6. High-Level Architecture
6.1 Logical component view
flowchart TD
    P[Presentation Layer]
    A[Application Layer]
    D[Domain Layer]
    O[Document Processing Orchestrator]

    V[Input Validation]
    PR[Image / Document Preprocessor]
    OCR[OCR Adapter]
    AI[AI Adapter]
    X[Extraction Processor]
    C[Confidence / Validation Processor]
    S[Structured Data Builder]

    R[Review / Edit Manager]
    PM[Persistence Manager]
    DB[SQLite Repository]
    FS[File Storage Manager]
    H[History Manager]

    M[AI Model Manager]
    E[Export Manager]
    EX1[Excel Exporter]
    EX2[CSV Exporter]
    EX3[JSON Exporter]
    EX4[PDF Exporter]
    SH[Sharing Boundary]

    P --> A
    A --> D
    A --> O
    O --> V --> PR --> OCR --> AI --> X --> C --> S
    S --> R
    R --> PM
    PM --> DB
    PM --> FS
    PM --> H
    A --> M
    A --> E
    E --> EX1
    E --> EX2
    E --> EX3
    E --> EX4
    E --> SH
6.2 Architectural interpretation
The system is organized around a modular local processing core rather than a client/server request-response architecture.
The principal runtime path is:
Presentation
    ↓
Application / Domain
    ↓
Processing Orchestrator
    ↓
Processing Pipeline
    ↓
Review / Edit
    ↓
Persistence
    ↓
Export / History
Provider-specific implementations sit behind stable boundaries.
7. Architectural Layers
7.1 Presentation Layer
Responsibilities
screens and UI components;
navigation;
user actions;
loading/progress display;
processing status;
errors and recovery affordances;
review/edit interface;
model-readiness presentation.
Must not directly
execute SQL;
run OCR;
execute AI inference;
implement export mapping;
manage low-level files;
contain domain rules that should be reusable/testable outside UI.
Status: PROPOSED boundary.
7.2 Application Layer
Responsibilities
use cases;
workflow orchestration;
commands;
state transitions;
progress/event propagation;
save/export commands;
translation of infrastructure failures into application-level errors;
coordination of model readiness and processing prerequisites.
Example conceptual operations:
startDocumentProcessing
cancelProcessing
getProcessingStatus
getProcessingResult
saveEditedResult
exportResult
openHistoryItem
deleteHistoryItem
checkModelReadiness
These are conceptual internal application interfaces, not REST APIs.
Status: PROPOSED.
7.3 Domain Layer
Responsibilities
Core concepts independent of Android UI or persistence implementation:
Document;
Document Type;
Extracted Field;
Key-Value Pair;
Table;
Column;
Row;
Confidence;
Processing Status;
Processing Result;
User Correction;
Export Request.
The domain layer must not depend directly on Android UI, SQLite classes, OCR engine classes or AI runtime classes.
Status: PROPOSED.
7.4 Processing Layer
Responsibilities
pipeline execution;
stage ordering;
progress reporting;
cancellation propagation;
failure handling;
intermediate result coordination;
resource-aware sequencing.
Status: PROPOSED.
7.5 OCR Layer
Responsibilities
prepare OCR input;
invoke OCR engine;
normalize OCR output;
preserve available OCR confidence/location information;
expose OCR results through a stable contract.
Tesseract OCR is source-backed; exact Android integration remains REQUIRES TECHNICAL VALIDATION.
Status: CONFIRMED requirement / adapter boundary PROPOSED.
7.6 AI Layer
Responsibilities
model readiness;
input preparation;
local inference;
document understanding;
document type detection;
field extraction;
table extraction;
confidence information where provided;
structured output validation.
The exact AI model and runtime are TBD / REQUIRES TECHNICAL VALIDATION.
Status: CONFIRMED capability / concrete implementation TBD.
7.7 Persistence Layer
Responsibilities
persist document metadata;
persist structured data;
persist processing metadata;
persist history records;
persist user corrections;
reopen saved records;
coordinate database/file consistency.
SQLite is the source-backed local database choice; exact integration remains validation work.
Status: CONFIRMED source-backed / integration TBD.
7.8 File Storage Layer
Responsibilities
original documents;
captured images;
PDF files;
temporary processing artifacts;
exports;
AI model files, where applicable.
Exact Android storage APIs, paths and retention rules remain TBD / REQUIRES TECHNICAL VALIDATION.
7.9 Export Layer
Responsibilities
convert current structured data to selected output format;
validate export input;
create output file;
store output where required;
return an export result;
integrate with Android sharing where supported.
Each exporter remains independent.
8. Component Architecture
ID
Component
Responsibility
Inputs
Outputs
Dependencies
Failure Modes
Status
CMP-001
UI
Render user-facing state and capture actions
View state, user events
UI events
Presentation platform
Rendering/state error
PROPOSED
CMP-002
Navigation
Route between screens/workflows
Navigation commands
Screen state
UI framework
Invalid route
PROPOSED
CMP-003
Application Coordinator
Coordinate use cases and workflow
Commands, domain inputs
Results/events
Domain + interfaces
Use-case failure
PROPOSED
CMP-004
Document Acquisition
Obtain camera/file source
Camera/file input
ProcessingInput
Android acquisition boundary
Permission, file access, cancel
CONFIRMED requirement / implementation TBD
CMP-005
Input Validator
Validate supported/correct input
ProcessingInput
ValidatedInput
Platform/file metadata
Unsupported/corrupt input
PROPOSED
CMP-006
Image Preprocessor
Prepare input for OCR
Images/pages
ProcessedDocument
Preprocessing implementation
Decode/process failure
CONFIRMED requirement / algorithm TBD
CMP-007
OCR Adapter
Convert image/document content to OCR result
ProcessedDocument
OCRResult
OCR provider
OCR failure/empty result
PROPOSED
CMP-008
AI Adapter
Perform local AI inference
AIInput
AIExtractionResult
AI runtime/model
Model unavailable/inference failure
PROPOSED
CMP-009
Extraction Processor
Normalize document/field/table extraction
AIExtractionResult
ExtractionResult
Domain rules
Partial/invalid extraction
PROPOSED
CMP-010
Confidence Processor
Attach/interpret confidence where available
OCR/AI output
Confidence-enriched result
Provider metadata
Missing confidence
PROPOSED
CMP-011
Structured Data Builder
Build canonical structured document
ExtractionResult
StructuredDocument
Domain contract
Structuring failure
PROPOSED
CMP-012
Review/Edit Manager
Apply user corrections
StructuredDocument, edit commands
EditedStructuredDocument
Presentation/application
Invalid edit/save failure
PROPOSED
CMP-013
Persistence Manager
Coordinate durable save/reopen
StructuredDocument, metadata
SavedDocument
Repository + file store
Partial/failed save
PROPOSED
CMP-014
SQLite Repository
Persist metadata/data/history
Domain persistence model
Stored records
SQLite integration
DB failure
CONFIRMED source-backed / integration TBD
CMP-015
File Storage Manager
Persist document/file artifacts
File operations
Local file references
Android storage
Read/write/space failure
REQUIRES TECHNICAL VALIDATION
CMP-016
Export Manager
Route export requests
StructuredDocument, ExportRequest
ExportFile
Exporters
Invalid request/format failure
PROPOSED
CMP-017
Excel Exporter
Generate .xlsx output
StructuredDocument
Excel file
TBD library/provider
Mapping/file failure
PROPOSED
CMP-018
CSV Exporter
Generate .csv output
StructuredDocument
CSV file
Serializer logic
Encoding/mapping failure
PROPOSED
CMP-019
JSON Exporter
Generate .json output
StructuredDocument
JSON file
Canonical schema
Serialization/schema failure
PROPOSED
CMP-020
PDF Exporter
Generate readable .pdf output
StructuredDocument / render model
PDF file
TBD PDF implementation
Layout/generation failure
PROPOSED
CMP-021
History Manager
List/reopen/delete saved records
History commands
History results
Persistence
Orphan/corrupt record
PROPOSED
CMP-022
AI Model Manager
Setup/readiness/load model
Model commands
Readiness/model state
Network/setup + file store + runtime
Download/corrupt/load/resource failure
PROPOSED
CMP-023
Error Manager
Normalize and route errors
Low-level/component errors
Application error
All adapters/services
Unknown/unmapped error
PROPOSED
CMP-024
Processing State Manager
Maintain valid processing states
Stage events, commands
Current state
Application/pipeline
Invalid transition
PROPOSED
9. Document Processing Architecture
9.1 Detailed pipeline
flowchart TD
    A[Document Input]
    B[Acquisition]
    C[Validation]
    D[Preprocessing]
    E[OCR]
    F[AI Analysis]
    G[Document Classification]
    H[Field Extraction]
    I[Table Detection]
    J[Confidence Processing]
    K[Structured Data]
    L[Review / Edit]
    M[Save]
    N[Export]
    O[History]

    A --> B --> C --> D --> E --> F
    F --> G
    G --> H
    G --> I
    H --> J
    I --> J
    J --> K --> L --> M
    M --> N
    M --> O
9.2 Stage contract
Stage
Input
Processing
Output
Failure
Recovery
Cancellation
Next stage
Acquisition
Camera/file request
Obtain source
ProcessingInput
Camera/file/permission failure
Retry or choose another source
Safe cancel
Validation
Validation
ProcessingInput
Check format/access/resource feasibility
ValidatedInput
Unsupported/corrupt input
Select another file or retry
Safe cancel
Preprocessing
Preprocessing
ValidatedInput
Crop/rotate/perspective/noise/brightness enhancement as supported
ProcessedDocument
Decode/process failure
Retry or return to source
Stop stage if supported
OCR
OCR
ProcessedDocument
Run local OCR
OCRResult
OCR failure/empty usable output
Retry/improve input where practical
Cancel processing
AI
AI
OCR/document context
Local inference
AIExtractionResult
Model missing, load/inference failure
Setup/retry/exit
Cancel inference if supported
Classification/extraction
Classification
AI result
Determine document type
DocumentType
Unknown/unsupported type
Continue with generic handling when supported
Stop
Field/table extraction
Field extraction
AI result
Identify key/value fields
Field candidates
Extraction failure
Retry/preserve raw context
Stop
Confidence
Table detection
AI result
Identify rows/columns
Table candidates
Partial/failed table extraction
Preserve valid non-table data; retry
Stop
Confidence
Confidence
Extraction result
Attach/interpet available confidence
Validated extraction
Missing confidence
Continue without fabrication
Stop
Structuring
Structuring
Extraction result
Normalize to domain contract
StructuredDocument
Contract/normalization failure
Preserve prior valid data where safe
Stop
Review
Review/Edit
StructuredDocument
User review/corrections
EditedStructuredDocument
Edit/save problem
Retry/preserve current data where safe
Cancel editing
Save
Persistence
EditedStructuredDocument
Durable local save
SavedDocument
Storage/DB failure
Retry/repair path
Cancel where safe
Export/history
Export
Saved data + request
Generate requested format
ExportFile
Mapping/generation/storage failure
Retry/another format
User cancel
Share/history
History
Saved records
List/reopen/delete
History result
Orphan/corrupt record
Report/repair/delete safely
N/A
End
Stage invariants
A stage must not claim success when its required output is unavailable.
A later stage must not consume an invalid output object.
Cancellation must propagate to active cancellable work.
Valid intermediate data should be retained in memory or temporary storage where safe.
User-edited data supersedes machine-generated values for save/export.
Missing confidence must not be replaced with fabricated confidence.
10. Data Flow Architecture
10.1 End-to-end data flow
flowchart LR
    A[Raw Document]
    B[Processed Document]
    C[OCR Result]
    D[AI Input]
    E[AI Result]
    F[Structured Data]
    G[User Corrections]
    H[Persisted Data]
    I[Export Data]

    A --> B --> C --> D --> E --> F --> G --> H --> I
10.2 Authoritative-data rule
The authoritative result for persistence and export is the latest user-reviewed/edited structured result.
Machine Extraction
      ↓
Structured Result
      ↓
User Review
      ↓
User Corrections
      ↓
Authoritative Working Result
      ↓
Save
      ↓
Export
This prevents re-exporting stale AI/OCR values after user corrections.
11. OCR Data Flow
flowchart LR
    A[Image / 