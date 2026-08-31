# SnapData Documentation

Canonical documentation index for **SnapData — AI-Powered Intelligent Document Processing & Data Extraction System**.

> The `docs/` tree follows the approved repository architecture baseline. Each specification remains authoritative for its own subject; do not create competing sources of truth.

## Documentation Structure

```text
docs/
├── README.md
├── product/
├── architecture/
├── frontend/
├── processing/
├── data/
├── export/
├── testing/
├── security/
├── engineering/
├── release/
└── project/
```

## Product

- [`PRD.md`](product/PRD.md) — Product Requirements Document
- [`SRS.md`](product/SRS.md) — Software Requirements Specification
- [`TRD.md`](product/TRD.md) — Technical Requirements Document

## Architecture

- [`SYSTEM_ARCHITECTURE.md`](architecture/SYSTEM_ARCHITECTURE.md) — System architecture baseline
- [`CODE_ARCHITECTURE.md`](architecture/CODE_ARCHITECTURE.md) — Code architecture and engineering structure
- [`ADR/`](architecture/ADR/) — Approved Architecture Decision Records

## Frontend

- [`UI_UX.md`](frontend/UI_UX.md) — UI/UX specification
- [`FRONTEND.md`](frontend/FRONTEND.md) — Frontend technical implementation specification

## Processing

- [`AI_OCR.md`](processing/AI_OCR.md) — AI/OCR technical specification
- [`AI_PROMPT_SPECIFICATION.md`](processing/AI_PROMPT_SPECIFICATION.md) — AI prompt specification
- [`DOCUMENT_PROCESSING.md`](processing/DOCUMENT_PROCESSING.md) — Document-processing specification
- [`ERROR_HANDLING_RECOVERY.md`](processing/ERROR_HANDLING_RECOVERY.md) — Error handling and recovery specification

## Data

- [`DATABASE.md`](data/DATABASE.md) — Database technical design
- [`DATA_SCHEMA.md`](data/DATA_SCHEMA.md) — Canonical data schema

## Export

- [`EXPORT.md`](export/EXPORT.md) — Export module specification

## Testing

- [`TESTING.md`](testing/TESTING.md) — QA, testing and validation strategy
- [`TEST_CASES.md`](testing/TEST_CASES.md) — Master test-case specification

## Security

- [`SECURITY_PRIVACY.md`](security/SECURITY_PRIVACY.md) — Security, privacy and threat-model baseline

## Engineering

- [`DEVELOPMENT_GUIDELINES.md`](engineering/DEVELOPMENT_GUIDELINES.md) — Development guidelines and coding standards
- [`PERFORMANCE_OPTIMIZATION.md`](engineering/PERFORMANCE_OPTIMIZATION.md) — Performance optimization and resource management
- [`REQUIREMENTS_TRACEABILITY.md`](engineering/REQUIREMENTS_TRACEABILITY.md) — Requirements traceability and verification baseline

## Release

- [`BUILD_RELEASE.md`](release/BUILD_RELEASE.md) — Build, configuration, deployment and release engineering
- [`IMPLEMENTATION_PLAN.md`](release/IMPLEMENTATION_PLAN.md) — Implementation roadmap and execution plan
- [`release-checklists/`](release/release-checklists/) — Release checklist area

## Project

- [`MASTER_SPECIFICATION.md`](project/MASTER_SPECIFICATION.md) — Consolidated master specification
- [`USER_GUIDE.md`](project/USER_GUIDE.md) — End-user guide
- [`workflow-diagram/`](project/workflow-diagram/) — Approved workflow diagram/assets

## Source-of-Truth Policy

The documentation hierarchy is an organization model:

```text
Product
  → Architecture
  → Frontend
  → Processing
  → Data
  → Export
  → Testing
  → Security
  → Engineering
  → Release
  → Project
```

This hierarchy does **not** replace the authority of an individual approved specification. When a document is marked `TBD`, `PROPOSED`, `OPTIONAL`, or `REQUIRES TECHNICAL VALIDATION`, retain that status unless an approved revision changes it.

The repository architecture also keeps implementation-specific technology decisions subject to validation against the actual generated Android project where the approved specifications require that validation. Historical technology references must not be treated as confirmed implementation facts without supporting evidence.

## Documentation Governance

- Preserve approved specification content and terminology.
- Keep canonical documents in their defined locations.
- Avoid duplicate or conflicting sources of truth.
- Report missing source documents rather than fabricating them.
- Keep cross-references aligned with the canonical paths.
- Do not use this index to introduce new product, architecture, security, or implementation decisions.
