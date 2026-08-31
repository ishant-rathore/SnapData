# SnapData: Requirements Traceability Matrix, Requirements Coverage & Verification Specification

**Document:** `SnapData_REQUIREMENTS_TRACEABILITY_v1.0.md`  
**Project:** SnapData — AI-Powered Intelligent Document Processing & Data Extraction System  
**Version:** 1.0  
**Status:** Draft / Traceability Baseline  
**Date:** 30 August 2026  
**Traceability owner:** SnapData Project Team *(Proposed)*

> **Evidence discipline:** This document distinguishes specification coverage from implementation/test execution evidence. The current workspace contains the project specifications and workflow diagram, but no inspectable Google AI Studio-generated Android source tree, Gradle project, manifest, dependency lockfile, or build artifact. Therefore no implementation, test-pass, release, or quantitative runtime result is claimed here. The AI/OCR specification records the same source-availability limitation.

## 1. Purpose
This document provides bidirectional traceability from business/product intent through software requirements, technical architecture, planned code boundaries, data contracts, AI/OCR processing, export, verification, and release. It is the control document for answering: **what requirement is this feature/code/test implementing, and what evidence proves it?**

## 2. Authoritative Baseline
The user-designated source of truth is the 18 project documents below. Requirement IDs already established by the SRS are preserved; no replacement IDs are introduced. The PRD is the product baseline, while the SRS provides the controlled FR/NFR/ERR/AC identifiers. The SRS records 54 functional requirements, 28 non-functional requirements, 18 error identifiers, 12 end-to-end acceptance criteria, and 12 open TBD decisions.

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
18. `SnapData_DEVELOPMENT_GUIDELINES_v1.0.md`

The original project specification/workflow diagram are referenced by the baseline documents as supporting source context. The workflow sequence is Start → Launch → Document Input → Acquisition → Pre-processing → OCR → Offline AI → Structured Data → Review/Edit → Local Storage → Export → History → End.

## 3. Source Authority and Conflict Policy
1. Preserve existing identifiers and source terminology.
2. Do not silently reconcile contradictions.
3. Where the current technical baseline is more specific than historical/source-era labels, record the discrepancy and keep the implementation status evidence-based.
4. The TRD rejects a required Node.js/Express backend, REST API, cloud processing, mandatory authentication, and any unverified claim that React Native/TypeScript is the final Android implementation.
5. SQLite, Tesseract OCR, Excel/CSV/JSON/PDF, local/offline intent, and the end-to-end processing flow are source-backed; exact Android implementation, AI runtime/model, storage adapter, export libraries, device matrix, quantitative thresholds, and advanced security controls remain open until validated.

## 4. ID and Status Conventions
### 4.1 Requirement IDs
- **FR-###** — SRS functional requirement (authoritative, preserved).
- **NFR-###** — SRS non-functional requirement (authoritative, preserved).
- **ERR-###** — SRS error identifier.
- **AC-###** — SRS end-to-end acceptance criterion.
- **TBD-###** — SRS open decision.
- **TEST-###** — **PROPOSED** traceability test ID used only where the source test document does not already provide a specific test ID.
- `SEC-###` — **not invented**; existing security IDs from the test/security baseline are referenced where applicable.

### 4.2 Requirement lifecycle
`PROPOSED → APPROVED → PLANNED → IN DEVELOPMENT → IMPLEMENTED → TESTED → VERIFIED → RELEASED`

Optional dispositions: `DEFERRED`, `REJECTED`, `SUPERSEDED`.

### 4.3 Traceability coverage statuses
| Status | Meaning |
|---|---|
| COVERED | Requirement has a complete specification-level mapping across source, feature, architecture and verification path. This does **not** mean code/test execution passed. |
| PARTIALLY COVERED | Some required trace links exist but one or more artifacts are missing. |
| PLANNED | Implementation/test path is defined, but direct implementation or execution evidence is absent. |
| NOT COVERED | No valid downstream mapping exists. |
| BLOCKED | Verification/implementation cannot proceed until a named dependency is resolved. |
| TBD | Requirement itself is not finalized by the source baseline. |
| NOT APPLICABLE | Requirement does not apply to the current architectural boundary. |

## 5. Current Traceability Dashboard
| Metric | Current evidence-based value |
|---|---:|
| Total controlled requirements (FR + NFR) | **82** |
| Functional requirements | 54 |
| Non-functional requirements | 28 |
| P0 / Critical requirements | 64 |
| P1 requirements | 11 |
| TBD / Proposed-priority requirements | 7 |
| Specification-level requirement coverage | **100% (82/82)** — each FR/NFR has a controlled source ID, acceptance criterion and verification method in the SRS consistency baseline |
| Verification-method coverage | **100% (82/82)** — source SRS states verification is included for every FR/NFR |
| Actual implementation evidence | **N/A** — Android source tree/build artifact unavailable for inspection |
| Actual executed test coverage | **N/A** — no executed test report is present in the accessible baseline |
| Verified/released requirements | **N/A** — no release evidence supplied |
| Quantitative OCR/AI accuracy coverage | **N/A** — validation dataset and thresholds remain open |
| Performance target coverage | **N/A** — approved numeric targets remain TBD |

The 100% figures above are **specification/traceability-definition metrics only**, not claims that the application is implemented or tested.

## 6. MVP / Post-MVP Baseline
The SRS uses P0 as the Critical/MVP tier, P1 as Important, and P2/TBD as Future/Enhancement or undecided. The implementation plan’s critical path runs from Application Shell through Input, Acquisition, Preprocessing, OCR, Offline AI, Extraction, Structured Data, Review/Edit, SQLite, History, Export, Testing and Release.

**MVP count at the controlled SRS priority level:** 64 P0 requirements. P1 requirements are not silently upgraded to MVP. Multi-page PDF is P1 and subject to validation; model update/delete is TBD/P2.

## 6.1 Requirement Hierarchy Traceability
The project sources express the requirement hierarchy at different abstraction levels. The traceability baseline preserves that hierarchy without manufacturing a one-to-one ID where the source does not define one.

| Level | Source / identifier | Traceability rule |
|---|---|---|
| Business | Product problem, goals and user value in `SnapData_PRD_v1.0.md` | Trace to one or more PRD/SRS requirements supporting the business outcome |
| Product | PRD requirements and scope | Trace to SRS FR/NFR identifiers |
| System | SRS `FR-###`, `NFR-###`, `ERR-###`, `AC-###` | Trace to architecture components and verification |
| Technical | TRD, System Architecture, Data/AI/Export/Security/Build documents | Trace to components, contracts, planned code boundaries and tests |
| Implementation | Actual Android source, when supplied | Must cite real package/file/class/module evidence; otherwise `PLANNED` / `NOT VERIFIED` |
| Verification | TEST/SEC/AC records | Must cite an executed build/dataset/device result before becoming `TESTED`/`VERIFIED` |

The central product transformation is documented as document → OCR → AI understanding → structured data → review/edit → local save/export, with the core workflow intended to operate offline after model setup.

## 7. Requirement → Feature Matrix
| Requirement | Feature | Priority | Traceability Status |
|---|---|---|---|
| FR-001 | Camera document capture | P0 | PLANNED |
| FR-002 | Image file import | P0 | PLANNED |
| FR-003 | PDF file import | P0 | PLANNED |
| FR-004 | Input validation | P0 | PLANNED |
| FR-005 | Multi-page PDF support | P1 | PLANNED |
| FR-006 | Acquisition cancellation | P0 | PLANNED |
| FR-007 | Preprocessing | P0 | PLANNED |
| FR-008 | Preprocessing | P0 | PLANNED |
| FR-009 | Preprocessing | P0 | PLANNED |
| FR-010 | OCR | P0 | PLANNED |
| FR-011 | OCR | P0 | PLANNED |
| FR-012 | OCR | P0 | PLANNED |
| FR-013 | OCR | P1 | PLANNED |
| FR-014 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-015 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-016 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-017 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-018 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-019 | Offline AI analysis / extraction | P1 | PLANNED |
| FR-020 | Offline AI analysis / extraction | P0 | PLANNED |
| FR-021 | Structured results / review | P0 | PLANNED |
| FR-022 | Structured results / review | P0 | PLANNED |
| FR-023 | Structured results / review | P0 | PLANNED |
| FR-024 | Structured results / review | P0 | PLANNED |
| FR-025 | Review & editing | P0 | PLANNED |
| FR-026 | Review & editing | P0 | PLANNED |
| FR-027 | Review & editing | P0 | PLANNED |
| FR-028 | Review & editing | P1 | PLANNED |
| FR-029 | Review & editing | P1 | PLANNED |
| FR-030 | Review & editing | P1 | PLANNED |
| FR-031 | Local storage & lifecycle | P0 | PLANNED |
| FR-032 | Local storage & lifecycle | P0 | PLANNED |
| FR-033 | Local storage & lifecycle | P0 | PLANNED |
| FR-034 | Local storage & lifecycle | P0 | PLANNED |
| FR-035 | Local storage & lifecycle | P0 | PLANNED |
| FR-036 | Export & sharing | P0 | PLANNED |
| FR-037 | Export & sharing | P0 | PLANNED |
| FR-038 | Export & sharing | P0 | PLANNED |
| FR-039 | Export & sharing | P0 | PLANNED |
| FR-040 | Export & sharing | P0 | PLANNED |
| FR-041 | Export & sharing | P1 | PLANNED |
| FR-042 | History | P0 | PLANNED |
| FR-043 | History | P0 | PLANNED |
| FR-044 | History | P0 | PLANNED |
| FR-045 | History | P0 | PLANNED |
| FR-046 | History | P1 | PLANNED |
| FR-047 | AI model setup/readiness | P0 | PLANNED |
| FR-048 | AI model setup/readiness | P0 | PLANNED |
| FR-049 | AI model setup/readiness | P0 | PLANNED |
| FR-050 | AI model setup/readiness | P0 | PLANNED |
| FR-051 | AI model setup/readiness | P0 | PLANNED |
| FR-052 | AI model setup/readiness | P0 | PLANNED |
| FR-053 | AI model setup/readiness | P0 | PLANNED |
| FR-054 | AI model setup/readiness | TBD/P2 | TBD |
| NFR-001 | Camera document capture | P0 | PLANNED |
| NFR-002 | Image file import | P0 | PLANNED |
| NFR-003 | PDF file import | TBD/Proposed | TBD |
| NFR-004 | Input validation | TBD | TBD |
| NFR-005 | Multi-page PDF support | P0 | PLANNED |
| NFR-006 | Acquisition cancellation | P0 | PLANNED |
| NFR-007 | Preprocessing | P1 | PLANNED |
| NFR-008 | Preprocessing | P0 | PLANNED |
| NFR-009 | Preprocessing | P0 | PLANNED |
| NFR-010 | OCR | P0 | PLANNED |
| NFR-011 | OCR | P0 | PLANNED |
| NFR-012 | OCR | P0 | PLANNED |
| NFR-013 | OCR | P0 | PLANNED |
| NFR-014 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-015 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-016 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-017 | Offline AI analysis / extraction | TBD | TBD |
| NFR-018 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-019 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-020 | Offline AI analysis / extraction | P0 | PLANNED |
| NFR-021 | Structured results / review | TBD | TBD |
| NFR-022 | Structured results / review | P0 | PLANNED |
| NFR-023 | Structured results / review | TBD | TBD |
| NFR-024 | Structured results / review | P0 | PLANNED |
| NFR-025 | Review & editing | P1 | PLANNED |
| NFR-026 | Review & editing | TBD | TBD |
| NFR-027 | Review & editing | P1 | PLANNED |
| NFR-028 | Review & editing | P0 | PLANNED |

## 8. Requirement → Architecture Matrix
Use the architecture component IDs from the approved System Architecture as planned mappings, not concrete source-code classes.

| Requirement | Architecture component | Layer/boundary | Status |
|---|---|---|---|
| FR-001 | CMP-004 — Document Acquisition | Architecture boundary | PLANNED |
| FR-002 | CMP-004 — Document Acquisition | Architecture boundary | PLANNED |
| FR-003 | CMP-004 — Document Acquisition | Architecture boundary | PLANNED |
| FR-004 | CMP-005 — Input Validator | Architecture boundary | PLANNED |
| FR-005 | CMP-004/CMP-005 — Document Acquisition + Input Validation | Architecture boundary | PLANNED |
| FR-006 | CMP-004 — Document Acquisition | Architecture boundary | PLANNED |
| FR-007 | CMP-006 — Image Preprocessor | Architecture boundary | PLANNED |
| FR-008 | CMP-006 — Image Preprocessor | Architecture boundary | PLANNED |
| FR-009 | CMP-023/CMP-024 — Error Manager + Processing State Manager | Architecture boundary | PLANNED |
| FR-010 | CMP-007 — OCR Adapter | Architecture boundary | PLANNED |
| FR-011 | CMP-007/CMP-003 — OCR Adapter + Application Coordinator | Architecture boundary | PLANNED |
| FR-012 | CMP-007/CMP-023 — OCR Adapter + Error Manager | Architecture boundary | PLANNED |
| FR-013 | CMP-007 — OCR Adapter | Architecture boundary | PLANNED |
| FR-014 | CMP-008 — AI Adapter | Architecture boundary | PLANNED |
| FR-015 | CMP-009 — Extraction Processor | Architecture boundary | PLANNED |
| FR-016 | CMP-009 — Extraction Processor | Architecture boundary | PLANNED |
| FR-017 | CMP-009 — Extraction Processor | Architecture boundary | PLANNED |
| FR-018 | CMP-010 — Confidence Processor | Architecture boundary | PLANNED |
| FR-019 | CMP-009/CMP-011 — Extraction Processor + Structured Data Builder | Architecture boundary | PLANNED |
| FR-020 | CMP-008/CMP-023 — AI Adapter + Error Manager | Architecture boundary | PLANNED |
| FR-021 | CMP-011 — Structured Data Builder | Architecture boundary | PLANNED |
| FR-022 | CMP-011 — Structured Data Builder | Architecture boundary | PLANNED |
| FR-023 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-024 | CMP-012/CMP-001 — Review/Edit Manager + UI | Architecture boundary | PLANNED |
| FR-025 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-026 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-027 | CMP-013/CMP-014 — Persistence Manager + SQLite Repository | Architecture boundary | PLANNED |
| FR-028 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-029 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-030 | CMP-012 — Review/Edit Manager | Architecture boundary | PLANNED |
| FR-031 | CMP-013/CMP-014 — Persistence Manager + SQLite Repository | Architecture boundary | PLANNED |
| FR-032 | CMP-013/CMP-014 — Persistence Manager + SQLite Repository | Architecture boundary | PLANNED |
| FR-033 | CMP-013/CMP-021 — Persistence Manager + History Manager | Architecture boundary | PLANNED |
| FR-034 | CMP-013/CMP-015 — Persistence Manager + File Storage Manager | Architecture boundary | PLANNED |
| FR-035 | CMP-013/CMP-014 — Persistence Manager + SQLite Repository | Architecture boundary | PLANNED |
| FR-036 | CMP-016/CMP-017 — Export Manager + Excel Exporter | Architecture boundary | PLANNED |
| FR-037 | CMP-016/CMP-018 — Export Manager + CSV Exporter | Architecture boundary | PLANNED |
| FR-038 | CMP-016/CMP-019 — Export Manager + JSON Exporter | Architecture boundary | PLANNED |
| FR-039 | CMP-016/CMP-020 — Export Manager + PDF Exporter | Architecture boundary | PLANNED |
| FR-040 | CMP-016 — Export Manager | Architecture boundary | PLANNED |
| FR-041 | CMP-016/platform sharing boundary — Export + Android Sharing | Architecture boundary | PLANNED |
| FR-042 | CMP-021 — History Manager | Architecture boundary | PLANNED |
| FR-043 | CMP-021/CMP-013 — History Manager + Persistence Manager | Architecture boundary | PLANNED |
| FR-044 | CMP-021 — History Manager | Architecture boundary | PLANNED |
| FR-045 | CMP-021/CMP-013/CMP-015 — History + Persistence + File Storage | Architecture boundary | PLANNED |
| FR-046 | CMP-021 — History Manager | Architecture boundary | PLANNED |
| FR-047 | CMP-022 — AI Model Manager | Architecture boundary | PLANNED |
| FR-048 | CMP-022/CMP-024/CMP-008 — Model Manager + State Manager + AI Adapter | Architecture boundary | PLANNED |
| FR-049 | CMP-008/CMP-022/CMP-024 — Local AI/Model/State boundaries | Architecture boundary | PLANNED |
| FR-050 | CMP-022/CMP-023 — AI Model Manager + Error Manager | Architecture boundary | PLANNED |
| FR-051 | CMP-022 — AI Model Manager | Architecture boundary | PLANNED |
| FR-052 | CMP-022 — AI Model Manager | Architecture boundary | PLANNED |
| FR-053 | CMP-022/CMP-024 — Model Manager + Processing State Manager | Architecture boundary | PLANNED |
| FR-054 | CMP-022 — AI Model Manager | Architecture boundary | PLANNED |
| NFR-001 | CMP-024 — Processing State Manager | Architecture boundary | PLANNED |
| NFR-002 | CMP-024 + presentation/application boundaries | Architecture boundary | PLANNED |
| NFR-003 | System-wide processing pipeline | Architecture boundary | PLANNED |
| NFR-004 | CMP-004/CMP-024 + device compatibility boundary | Architecture boundary | PLANNED |
| NFR-005 | CMP-023/CMP-024 — Error + State Management | Architecture boundary | PLANNED |
| NFR-006 | CMP-013/CMP-014 — Persistence | Architecture boundary | PLANNED |
| NFR-007 | CMP-013/CMP-014/CMP-024 — Persistence + State | Architecture boundary | PLANNED |
| NFR-008 | CMP-001/CMP-002 — UI + Navigation | Architecture boundary | PLANNED |
| NFR-009 | CMP-024/CMP-001 | Architecture boundary | PLANNED |
| NFR-010 | CMP-001/CMP-003/CMP-016/CMP-021 | Architecture boundary | PLANNED |
| NFR-011 | All layers; interface boundaries | Architecture boundary | PLANNED |
| NFR-012 | CMP-009/CMP-011/CMP-012/CMP-023 | Architecture boundary | PLANNED |
| NFR-013 | System-wide local-first architecture | Architecture boundary | PLANNED |
| NFR-014 | System-wide network boundary | Architecture boundary | PLANNED |
| NFR-015 | CMP-022/CMP-024 | Architecture boundary | PLANNED |
| NFR-016 | Platform + storage + file + AI boundaries | Architecture boundary | PLANNED |
| NFR-017 | Security architecture boundary; exact controls TBD | Architecture boundary | PLANNED |
| NFR-018 | CMP-007/CMP-008/CMP-022/CMP-024 | Architecture boundary | PLANNED |
| NFR-019 | CMP-022 + network boundary | Architecture boundary | PLANNED |
| NFR-020 | CMP-014/CMP-015/CMP-023 | Architecture boundary | PLANNED |
| NFR-021 | CMP-014/CMP-015 | Architecture boundary | PLANNED |
| NFR-022 | Platform/Build compatibility boundary | Architecture boundary | PLANNED |
| NFR-023 | Platform/Build compatibility boundary | Architecture boundary | PLANNED |
| NFR-024 | CMP-001 presentation/accessibility | Architecture boundary | PLANNED |
| NFR-025 | CMP-001/platform accessibility boundary | Architecture boundary | PLANNED |
| NFR-026 | CMP-001/platform accessibility boundary | Architecture boundary | PLANNED |
| NFR-027 | Domain/application/processing extension boundaries | Architecture boundary | PLANNED |
| NFR-028 | No server component required for MVP | Architecture boundary | PLANNED |

## 9. Requirement → Code Matrix
**Actual code inspection result:** no generated Android source tree/Gradle project/build artifact was available. The code mapping is therefore limited to planned package/module boundaries; no fabricated classes, functions, or filenames are asserted.

| Requirement | Planned code boundary | Evidence status |
|---|---|---|
| FR-001..FR-006 | PLANNED: platform + processing + application | NOT VERIFIED — actual source unavailable |
| FR-007..FR-020 | PLANNED: processing + application | NOT VERIFIED — actual source unavailable |
| FR-021..FR-030 | PLANNED: domain + application + presentation | NOT VERIFIED — actual source unavailable |
| FR-031..FR-035 | PLANNED: data + application + domain | NOT VERIFIED — actual source unavailable |
| FR-036..FR-041 | PLANNED: export + platform | NOT VERIFIED — actual source unavailable |
| FR-042..FR-046 | PLANNED: data + application + presentation | NOT VERIFIED — actual source unavailable |
| FR-047..FR-054 | PLANNED: modelmanagement + processing + application | NOT VERIFIED — actual source unavailable |
| NFR-001..NFR-028 | PLANNED: relevant architectural boundaries | NOT VERIFIED — actual source unavailable |

## 10. Requirement → Data Matrix
The data contract owns semantic meaning; the database document owns SQLite table/column implementation. The current schema includes `document`, `document_page`, `processing_job`, `extraction_result`, fields/tables/rows/cells, `export_record`, `app_setting`, and `model_metadata`.

| Requirement cluster | Domain/data mapping | Persistence | Test focus |
|---|---|---|---|
| FR-001..FR-006 | Input/document metadata | As required by data contract | Input validation/integration |
| FR-007..FR-013 | OCRResult / page-processing state | As required by data contract | OCR/schema/integrity |
| FR-014..FR-020 | OCRResult + ExtractionResult | As required by data contract | AI/schema/integrity |
| FR-021..FR-027 | Fields/tables/edit authority | SQLite/local-file boundary | Edit/save/reopen round trip |
| FR-031..FR-035 | Document + processing metadata | SQLite/local-file boundary | Persistence/history/restart |
| FR-036..FR-041 | Export record + canonical result | SQLite/local-file boundary | Cross-format/export/share |
| FR-042..FR-046 | Document/history model | SQLite/local-file boundary | List/search/reopen/delete |
| FR-051..FR-054 | Model readiness metadata | SQLite/local-file boundary | Ready/not-ready/setup |

## 11. Requirement → AI/OCR Matrix
The approved AI/OCR baseline defines the pipeline `Document → Preprocessed Document → OCR Text/Layout Evidence → AI Understanding → Document Type → Fields/Tables → Confidence/Warnings → Validated Structured Data → Review/Edit`. Tesseract is source-backed context; exact runtime/model/integration remains validation-open.

| Requirement cluster | Processing stage | AI/OCR mapping | Validation/test focus |
|---|---|---|---|
| FR-007..FR-009 | Preprocessing | Preprocessing → OCR preparation | Benchmark + schema + E2E |
| FR-010..FR-013 | OCR | OCR → text/layout evidence → validation | OCR corpus + regression |
| FR-014..FR-020 | AI / Extraction | AI → classification/extraction → validation → confidence/warnings | AI benchmark + schema + E2E |
| FR-021..FR-027 | Structuring / Review | Validated structured output / review authority | Functional/integration |
| FR-047..FR-054 | Model Management | Model readiness/setup boundary; exact model/runtime TBD | Setup/offline validation |
| NFR-003..NFR-004 | Cross-cutting | AI/OCR performance/offline validation | Benchmark + device validation |
| NFR-018 | Cross-cutting | AI/OCR offline validation | Offline test |

## 12. Requirement → Export Matrix
The approved export architecture requires XLSX, CSV, JSON and PDF exporters to consume the **current saved canonical structured result**, not raw OCR or an unapproved AI candidate.

| Requirement | Export path | Format(s) | Verification |
|---|---|---|---|
| FR-036 | Canonical saved result → Export Manager → formatter → local file | XLSX | Export validity + edited-value round trip |
| FR-037 | Canonical saved result → Export Manager → formatter → local file | CSV | Export validity + edited-value round trip |
| FR-038 | Canonical saved result → Export Manager → formatter → local file | JSON | Export validity + edited-value round trip |
| FR-039 | Canonical saved result → Export Manager → formatter → local file | PDF | Export validity + edited-value round trip |
| FR-040 | Canonical saved result → Export Manager → formatter → local file | All | Success/failure state |
| FR-041 | Canonical saved result → Export Manager → local file → Android sharing | Android sharing | Share success/failure |

## 13. Security / Privacy Traceability
Preserve the security baseline: core processing remains local, imported files and AI outputs are untrusted, sensitive content is excluded from routine diagnostics, user-corrected saved data is authoritative, and external sharing is an explicit privacy boundary. Advanced security mechanisms remain unresolved unless explicitly confirmed.

| Requirement / concern | Security control | Implementation boundary | Existing evidence |
|---|---|---|---|
| FR-049 / NFR-014 | No mandatory cloud upload | Local processing/network boundary | Planned; execution N/A |
| FR-004 / ERR-003..005 | Validate supported/unsafe input | Input validation boundary | Planned; execution N/A |
| FR-023 / FR-027 / FR-035 | User correction authority/persistence | Review + persistence | Planned; execution N/A |
| FR-036..FR-041 | Export path safety + share boundary | Export + platform sharing | Planned; execution N/A |
| NFR-016 / NFR-017 | Platform security; advanced controls TBD | Platform/storage/file boundary | Defined; execution N/A |
| NFR-006 / NFR-007 | Restart/interruption integrity | Persistence + state | Defined; execution N/A |

## 14. Offline-First Traceability
The offline requirement must cover the complete core pipeline.

| Stage | Offline dependency | Requirement(s) | Verification focus |
|---|---|---|---|
| Document input | Camera/file + local acquisition | FR-001..FR-006 | Airplane-mode acquisition/permission |
| Preprocessing | Local image processing | FR-007..FR-009 | No network calls + recovery |
| OCR | Local OCR capability | FR-010..FR-013 | OCR corpus + network audit |
| AI | Installed local model/runtime | FR-014..FR-020, FR-047..FR-053 | Offline inference + readiness |
| Structured data | Local memory/domain contracts | FR-021..FR-024 | Schema/evidence/uncertainty |
| Review/Edit | Local UI/application state | FR-025..FR-030 | Edit/save/reopen |
| Database/files | SQLite + local files | FR-031..FR-035 | Restart/file reconciliation |
| Export | Local exporters | FR-036..FR-040 | All required formats offline |
| History/share | Local history; share is OS boundary | FR-041..FR-046 | History offline; share failure safe |

## 15. AI Trust and User-Correction Traceability
```text
OCR evidence
   ↓
AI analysis
   ↓
Parser / structured output
   ↓
Schema validation
   ↓
Domain mapping
   ↓
Review UI
   ↓
User edit
   ↓
Authoritative save
   ↓
History / reopen
   ↓
Export current saved value
```

| Trace link | Rule | Verification |
|---|---|---|
| Evidence → AI | Do not synthesize unsupported factual values | Negative AI tests / corpus review |
| AI → Schema | Reject/contain malformed output | Schema/contract tests |
| Schema → UI | Preserve confidence/warnings/uncertainty | UI + contract tests |
| UI → Persistence | Saved edits are authoritative | Edit-persistence E2E |
| Persistence → Export | Export reads current saved result | Cross-format round trip |

## 16. Database Traceability
```text
Requirement
   ↓
Domain Model / Canonical Data Contract
   ↓
Repository boundary
   ↓
DAO / Database Adapter
   ↓
SQLite
   ↓
Reload / History / Export
```

| Requirement cluster | Domain model | DB mapping | Verification |
|---|---|---|---|
| FR-031..FR-035 | Document + ProcessingJob + current structured result | document, processing_job, extraction_result, fields/tables | CRUD, transaction, migration, restart |
| FR-021..FR-027 | Fields/tables/edit authority | extracted_field, extracted_table, rows/cells | Edit/save/reopen |
| FR-036..FR-041 | Export record + canonical result | export_record | Cross-format output/share |
| FR-042..FR-046 | History model | document + history metadata/query | List/search/reopen/delete |
| FR-051..FR-054 | Model readiness metadata | model_metadata | Ready/not-ready/setup |

## 17. Build / Release Traceability
Build/release records must remain traceable to application version/build number, source commit, model version, OCR resource/version, artifact, signing reference and checksum. Release candidates pass QA, security/privacy, performance, offline and smoke validation before approval.

| Release requirement | Artifact/control | Verification evidence | Status |
|---|---|---|---|
| Controlled versioning | Version + source commit | Build metadata | PLANNED |
| Reproducible candidate | Release branch/tag + clean build | CI/build log | PLANNED |
| AI/OCR readiness | Model/OCR resources + versions | Validation report | PLANNED |
| Offline release gate | Airplane-mode core flow | Offline report | PLANNED |
| Export release gate | XLSX/CSV/JSON/PDF | Artifact validation | PLANNED |
| Security release gate | Input/path/log/network tests | Security report | PLANNED |
| Final artifact | APK/AAB + checksum/signing reference | Signed artifact record | PLANNED |

## 18. Implementation Plan Traceability
```text
M1 Application foundation
  ↓
M2 Document input
  ↓
M3 OCR
  ↓
M4 Offline AI
  ↓
M5 Structured extraction
  ↓
M6 Review/Edit
  ↓
M7 SQLite/History
  ↓
M8 Export
  ↓
M9 Security
  ↓
M10 Full testing
  ↓
M11 Release candidate
  ↓
M12 Production release
```

| Milestone | Primary requirement range | Exit evidence | Current status |
|---|---|---|---|
| M1 | NFR-008..NFR-012 | Build/navigation/state evidence | TODO / evidence pending |
| M2 | FR-001..FR-006 | Valid inputs reach processing | TODO / evidence pending |
| M3 | FR-007..FR-013 | OCR gate/regression corpus | TODO / evidence pending |
| M4 | FR-014..FR-020 + FR-047..FR-053 | Local inference/model readiness | TODO / evidence pending |
| M5 | FR-021..FR-024 | Schema-valid representative results | TODO / evidence pending |
| M6 | FR-025..FR-030 | Correction-integrity test | TODO / evidence pending |
| M7 | FR-031..FR-035 + FR-042..FR-045 | Restart/history integrity | TODO / evidence pending |
| M8 | FR-036..FR-041 | Export acceptance tests | TODO / evidence pending |
| M9 | NFR-013..NFR-020 | Security/privacy gate | TODO / evidence pending |
| M10 | All | Test evidence/defect closure | TODO / evidence pending |
| M11 | All release-blocking requirements | Signed candidate/regression | TODO / evidence pending |
| M12 | All release requirements | Distributed artifact/smoke | TODO / evidence pending |

All initial implementation tasks remain TODO unless authoritative implementation evidence exists. Documentation completeness is not implementation completion.

## 19. Acceptance Criteria Traceability
| AC ID | Acceptance outcome | Primary requirement group | Release role |
|---|---|---|---|
| AC-001 | Supported document acquisition reaches processing | FR-001..FR-006 | P0 |
| AC-002 | Preprocessing succeeds or fails truthfully | FR-007..FR-009 | P0 |
| AC-003 | OCR yields usable text or truthful empty/failure state | FR-010..FR-012 | P0 |
| AC-004 | AI produces supported type/fields/tables + available confidence | FR-014..FR-018 | P0 |
| AC-005 | User reviews results before export | FR-024 | P0 |
| AC-006 | User edits persist through save/reopen | FR-023, FR-025..FR-027, FR-035 | P0 |
| AC-007 | Export reflects current saved values | FR-036..FR-040 | P0 |
| AC-008 | Generated export can be safely shared or fails truthfully | FR-041 | P1 |
| AC-009 | History/reopen/delete work as defined | FR-042..FR-045 | P0 |
| AC-010 | Core processing works without network after model setup | FR-048..FR-049 | P0 |
| AC-011 | Missing local model produces clear readiness/unavailable state | FR-050..FR-053 | P0 |
| AC-012 | Critical processing/export/storage failures are truthful and recoverable | FR-009, FR-020, FR-040 + ERR-001..018 | P0 |

## 20. Requirement → Test Matrix
For each FR/NFR, the SRS defines a verification method. Where a concrete executed test case ID is not present in the source, `TEST-###` is **PROPOSED** and remains unexecuted until a real test record exists.

| Requirement ID | Test ID | Test Type | Test objective | Expected result | Execution status |
|---|---|---|---|---|---|
| FR-001 | SEC-021 | Security/Privacy | Verify camera document capture | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-002 | TEST-002 (PROPOSED) | Functional | Verify supported image import | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-003 | TEST-003 (PROPOSED) | Functional | Verify supported PDF import | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-004 | TEST-004 (PROPOSED) | Functional | Verify input eligibility validation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-005 | TEST-005 (PROPOSED) | Compatibility | Verify multi-page PDF support | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-006 | TEST-006 (PROPOSED) | Functional | Verify acquisition cancellation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-007 | TEST-007 (PROPOSED) | Integration | Verify preprocessing pipeline | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-008 | TEST-008 (PROPOSED) | Integration | Verify baseline image preprocessing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-009 | TEST-009 (PROPOSED) | Functional | Verify preprocessing failure handling | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-010 | TEST-010 (PROPOSED) | Integration | Verify OCR execution | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-011 | TEST-011 (PROPOSED) | Integration | Verify OCR output availability | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-012 | TEST-012 (PROPOSED) | Functional | Verify OCR failure/unusable-output state | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-013 | TEST-013 (PROPOSED) | Compatibility | Verify supported OCR language selection | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-014 | TEST-014 (PROPOSED) | Integration | Verify local/offline AI analysis | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-015 | TEST-015 (PROPOSED) | Functional | Verify document type detection | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-016 | TEST-016 (PROPOSED) | Functional | Verify key-value extraction | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-017 | TEST-017 (PROPOSED) | Functional | Verify table detection | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-018 | TEST-018 (PROPOSED) | Functional | Verify confidence information | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-019 | TEST-019 (PROPOSED) | Functional | Verify document summary where enabled | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-020 | TEST-020 (PROPOSED) | Functional | Verify AI failure state | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-021 | TEST-021 (PROPOSED) | Integration | Verify structured field representation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-022 | TEST-022 (PROPOSED) | Integration | Verify structured table representation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-023 | SEC-008 | Security/Privacy | Verify preservation of user-corrected values | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-024 | TEST-024 (PROPOSED) | Usability | Verify review of extracted results | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-025 | TEST-025 (PROPOSED) | Functional | Verify field editing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-026 | TEST-026 (PROPOSED) | Functional | Verify table editing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-027 | SEC-008 | Security/Privacy | Verify saving corrections | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-028 | TEST-028 (PROPOSED) | Functional | Verify OCR text editing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-029 | TEST-029 (PROPOSED) | Functional | Verify row add/delete | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-030 | TEST-030 (PROPOSED) | Functional | Verify undo/redo | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-031 | TEST-031 (PROPOSED) | Integration | Verify local processed-document storage | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-032 | TEST-032 (PROPOSED) | Integration | Verify local structured-data storage | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-033 | TEST-033 (PROPOSED) | Integration | Verify processing/history metadata | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-034 | TEST-034 (PROPOSED) | Functional | Verify deletion of locally stored documents | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-035 | TEST-035 (PROPOSED) | Integration | Verify restoration of corrections | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-036 | SEC-009 | Security/Privacy | Verify Excel export | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-037 | SEC-009 | Security/Privacy | Verify CSV export | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-038 | SEC-009 | Security/Privacy | Verify JSON export | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-039 | SEC-009 | Security/Privacy | Verify PDF export | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-040 | TEST-040 (PROPOSED) | Functional | Verify export success/failure state | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-041 | SEC-010 | Security/Privacy | Verify Android sharing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-042 | TEST-042 (PROPOSED) | Functional | Verify history listing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-043 | TEST-043 (PROPOSED) | Integration | Verify history reopen | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-044 | TEST-044 (PROPOSED) | Usability | Verify distinguishing history metadata | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-045 | SEC-018 | Security/Privacy | Verify history deletion | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-046 | TEST-046 (PROPOSED) | Functional | Verify history search | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-047 | TEST-047 (PROPOSED) | Integration | Verify initial AI model setup | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-048 | TEST-048 (PROPOSED) | Offline | Verify offline core processing | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-049 | SEC-004 | Security/Privacy | Verify no cloud upload for core workflow | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-050 | SEC-005 | Security/Privacy | Verify offline-unavailable state | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-051 | TEST-051 (PROPOSED) | Functional | Verify AI readiness state | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-052 | TEST-052 (PROPOSED) | Integration | Verify model setup/download initiation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-053 | TEST-053 (PROPOSED) | Usability | Verify model setup progress/completion | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| FR-054 | TEST-054 (PROPOSED) | Functional | Verify model update/delete only after approval | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-001 | TEST-055 (PROPOSED) | Functional | Verify processing progress visibility | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-002 | TEST-056 (PROPOSED) | Performance | Verify UI responsiveness | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-003 | TEST-057 (PROPOSED) | Performance | Verify processing-time targets are established | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-004 | TEST-058 (PROPOSED) | Performance | Verify large-document/multi-page limits | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-005 | TEST-059 (PROPOSED) | Integration | Verify recoverable processing failures | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-006 | TEST-060 (PROPOSED) | Integration | Verify persistence after restart | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-007 | TEST-061 (PROPOSED) | Integration | Verify interrupted-operation integrity | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-008 | TEST-062 (PROPOSED) | Usability | Verify first-time workflow usability | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-009 | TEST-063 (PROPOSED) | Usability | Verify processing-state clarity | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-010 | TEST-064 (PROPOSED) | Usability | Verify simple review/edit/save/export flow | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-011 | TEST-065 (PROPOSED) | Inspection | Verify separation of product/implementation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-012 | TEST-066 (PROPOSED) | Functional | Verify consistent failure/editable-output representation | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-013 | TEST-067 (PROPOSED) | Security/Privacy | Verify local processing/storage priority | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-014 | SEC-004 | Security/Privacy | Verify no mandatory cloud upload | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-015 | TEST-069 (PROPOSED) | Usability | Verify offline availability communication | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-016 | SEC-001/002/003/004/005/006/007/009 | Security/Privacy | Verify platform security controls | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-017 | SEC-023/024 (scope TBD) | Security/Privacy | Verify security implementation remains TBD where specified | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-018 | TEST-072 (PROPOSED) | Offline | Verify offline operation after setup | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-019 | TEST-073 (PROPOSED) | Usability | Verify network-dependent vs offline features | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-020 | TEST-074 (PROPOSED) | Functional | Verify insufficient-storage feedback | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-021 | TEST-075 (PROPOSED) | Performance | Verify storage limits/cleanup strategy | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-022 | TEST-076 (PROPOSED) | Compatibility | Verify supported Android target | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-023 | TEST-077 (PROPOSED) | Compatibility | Verify minimum Android/device resources | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-024 | TEST-078 (PROPOSED) | Usability | Verify understandable control labels | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-025 | TEST-079 (PROPOSED) | Usability | Verify accessibility support | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-026 | TEST-080 (PROPOSED) | Inspection | Verify accessibility conformance remains validation-open | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-027 | TEST-081 (PROPOSED) | Integration | Verify extensible workflow | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |
| NFR-028 | TEST-082 (PROPOSED) | Inspection | Verify no server scalability requirement | Behaves per SRS acceptance criterion | NOT EXECUTED / N/A |

## 21. Error Verification Matrix
| Error ID | Condition | Related requirement(s) | Verification | Status |
|---|---|---|---|---|
| ERR-001 | Camera permission denied | FR-001/NFR-016 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-002 | Camera unavailable/capture failed | FR-001 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-003 | Unsupported file | FR-004 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-004 | Corrupt PDF | FR-003/FR-004 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-005 | Invalid image | FR-002/FR-004 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-006 | Preprocessing failure | FR-009 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-007 | OCR failure | FR-012 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-008 | Empty OCR | FR-012 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-009 | AI model unavailable | FR-050/FR-051 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-010 | AI processing failure | FR-020 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-011 | Structured extraction failure | FR-021..FR-024 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-012 | Table extraction failure | FR-017/FR-022 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-013 | Local storage failure | FR-031/FR-032 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-014 | Insufficient storage | FR-031/NFR-020 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-015 | Export failure | FR-040 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-016 | Share failure | FR-041 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-017 | Processing cancelled | FR-006/NFR-007 | Negative functional/integration/security/recovery test | PLANNED |
| ERR-018 | Application interruption | NFR-007/FR-033 | Negative functional/integration/security/recovery test | PLANNED |

## 22. Critical (P0) Requirement List
All P0 requirements are release-critical in the SRS baseline. `PLANNED` means traceability/implementation planning exists; it does not mean the requirement is verified.

Representative P0 groups preserved exactly by requirement ID and source classification:
- FR-001..FR-004 — Document input/acquisition/validation
- FR-006..FR-012 — Acquisition/preprocessing/OCR
- FR-014..FR-018 — AI/classification/extraction/confidence
- FR-020..FR-027 — AI failure/structured data/review/edit/save
- FR-031..FR-040 — Local persistence/export
- FR-042..FR-045 — History
- FR-047..FR-053 — AI model setup/readiness
- NFR-001..NFR-002 — Progress/responsiveness
- NFR-005..NFR-016 — Reliability/usability/local-first/security
- NFR-018..NFR-020 — Offline/storage
- NFR-022, NFR-024, NFR-028 — Compatibility/usability/no-server requirement

Release status for these requirements: **NOT RELEASE READY** until implementation and verification evidence exists.

## 23. MVP Readiness Matrix
| MVP requirement cluster | Implementation | Test | Verification | Release ready |
|---|---|---|---|---|
| Document input/acquisition | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Preprocessing | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| OCR | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Offline AI + extraction | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Structured data + review | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Local persistence + history | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Export | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Offline/privacy | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Critical UX/reliability | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |
| Build/release quality | NOT VERIFIED | PLANNED / unexecuted | Pending evidence | **NO — evidence gap** |

**Current evidence-based conclusion:** SnapData MVP is **not yet demonstrated as release-ready** because the required implementation/build/test evidence is unavailable in the baseline.

## 24. Unimplemented Requirements
Requirements with no direct implementation evidence in the accessible workspace remain **NOT VERIFIED**, not automatically defects, until the actual Android project is inspected.

The complete requirement inventory is FR-001..FR-054 and NFR-001..NFR-028. P0/P1/TBD classifications are preserved from the approved source.

## 25. Untested Requirements
| Priority class | Count | Untested status | Recommended action |
|---|---:|---|---|
| P0 | 64 | All 64 require execution evidence | Execute P0 smoke/E2E/integration + critical security/offline/persistence/export tests first |
| P1 | 11 | 11 unverified | Validate after P0 baseline or as approved scope |
| TBD/Proposed | 7 | Not release-verifiable until decision/validation | Resolve source TBDs before converting to release requirements |

## 26. Orphan Requirements
**Current result:** No specification-level orphan FR/NFR was found in the controlled SRS inventory. Each FR/NFR has a source ID, acceptance criterion and verification method.

**Potential orphan risk:** Future PRD/backlog items lacking an SRS ID, implementation boundary, or test mapping must enter change control rather than being implemented ad hoc.

## 27. Orphan Code
**Status: NOT VERIFIABLE.** No actual Android source tree is available in the current workspace, so orphan-code analysis cannot be completed honestly. When available, classify unmapped modules as `Required`, `Supporting infrastructure`, `Technical necessity`, `Future`, `Unapproved`, or `Needs review`.

## 28. Orphan Tests
**Status: TRACEABILITY REVIEW REQUIRED.** Every future test must reference at least one requirement, acceptance criterion, security property, architectural constraint, or approved behavior.

## 29. Requirement Gaps
| Gap | Missing artifact/evidence | Impact | Priority | Recommended action |
|---|---|---|---|---|
| Actual implementation mapping | Generated Android project/source tree | High — cannot prove code coverage | P0 | Inspect source tree, packages, dependencies, manifest, build files |
| Executed verification results | Build-linked test report | Critical — cannot mark requirements VERIFIED | P0 | Execute P0 unit/integration/E2E/offline/security/export tests |
| AI model/runtime | Selected/validated model + runtime | Critical | P0 | Resolve TBD-002 and complete device/model validation |
| OCR integration | Concrete OCR adapter/resource evidence | High | P0 | Validate local OCR integration and benchmark |
| Device/performance thresholds | Approved device matrix and targets | High | P0/TBD | Resolve TBD-001/TBD-003/TBD-006 and NFR-003/NFR-004/NFR-023 |
| Exact MVP OCR languages | Approved language list | Medium | P1 | Resolve TBD-004 / FR-013 |
| Advanced security mechanisms | Encryption/PIN/biometric/secure-delete decisions | High if claimed | TBD | Resolve TBD-009 and NFR-017 |
| Multi-page behavior | Exact page/size support | High | P1/TBD | Resolve TBD-008 and NFR-004 |
| Export fidelity | Complex-table fidelity criteria | High | TBD | Resolve TBD-011 and verify all formats |
| Recovery/resume behavior | Exact interruption policy | High | TBD | Resolve TBD-012 and test ERR-018 |

## 30. Consistency / Conflict Report
| Issue | Source A | Source B | Impact | Resolution required |
|---|---|---|---|---|
| Historical stack labels vs current Android evidence | Original workflow/source-era labels | TRD and implementation-validation rules | Incorrect implementation claims | Keep source-era labels as context; validate actual Android project |
| AI model/runtime | SRS/AI_OCR | TBD-002/validation status | AI cannot be release-certified | Resolve with benchmark/device evidence |
| Multi-page PDF priority | FR-005 = P1 | TBD-008 | Scope ambiguity | Keep P1 until formal decision |
| AI summary | FR-019 = P1 | TBD-007 | MVP ambiguity | Keep P1 unless approved otherwise |
| Model update/delete | FR-054 = TBD/P2 | UI/model-manager scope | Scope creep risk | Keep Future/TBD |
| Advanced security | Security/PRD open decisions | Roadmap may suggest stronger controls | Overclaim risk | Keep TBD/validation-open |

## 31. Change Impact Analysis
```text
Requirement
   ↓
Feature
   ↓
Architecture
   ↓
Code boundary
   ↓
Data model / migration
   ↓
AI/OCR / processing
   ↓
Export
   ↓
Tests
   ↓
Documentation
   ↓
Release
```

### 31.1 Change Request Matrix Template
| Change ID | Requirement ID | Requested Change | Reason | Affected Components | Affected Tests | Risk | Approval | Status |
|---|---|---|---|---|---|---|---|---|
| CR-001 | <REQ-ID> | <change> | <reason> | <feature/architecture/code/data/AI/export> | <test IDs> | <P0/P1/etc.> | <owner> | PROPOSED |

## 32. Verification Methods
| Method | Use when | Evidence required |
|---|---|---|
| Inspection | UI labels, docs, configuration, code structure | Review record / screenshot / source reference |
| Analysis | Architecture, data mapping, dependency reasoning | Analysis note / model review |
| Unit Test | Deterministic isolated logic | Build-linked automated result |
| Component Test | One coherent module | Test result + fixture |
| Integration Test | Module boundaries and data flow | Build-linked integration result |
| UI Test | Screens, interactions, lifecycle | Device/emulator evidence |
| E2E Test | Full user workflow | Recording/report + build ID |
| Security Test | Threat/control behavior | Security test result |
| Performance Test | Latency, memory, limits | Dataset/device/benchmark report |
| Manual Verification | UX/acceptance where automation is insufficient | Signed checklist/evidence |

## 33. Quality Gates
### 33.1 Requirement verification gate
A requirement cannot be marked `VERIFIED` unless:
- [ ] Approved requirement baseline exists.
- [ ] Expected behavior/acceptance criterion is defined.
- [ ] Implementation evidence exists.
- [ ] Appropriate test/inspection exists.
- [ ] Verification passed.
- [ ] No unresolved release-blocking defect remains.

### 33.2 MVP release gate
- [ ] All P0 requirements implemented and tested.
- [ ] Critical offline path verified with network disabled.
- [ ] Security/privacy checks passed.
- [ ] Data persistence/restart/edit authority verified.
- [ ] Excel/CSV/JSON/PDF exports verified.
- [ ] No unresolved S0/S1 release blocker.
- [ ] Build/signing/artifact evidence recorded.
- [ ] Known exceptions approved.

## 34. Coverage Calculation Rules
These formulas are to be used only when underlying evidence exists:

- **Requirement Coverage %** = requirements with valid trace links / total controlled requirements × 100.
- **Implementation Coverage %** = requirements with implementation evidence / total applicable requirements × 100.
- **Test Coverage %** = requirements with at least one executed passing verification / total testable requirements × 100.
- **Critical Coverage %** = covered P0 requirements / total P0 requirements × 100.
- **MVP Coverage %** = covered MVP/P0 requirements / total MVP/P0 requirements × 100.
- **Security Coverage %** = security requirements with executed passing security verification / total applicable security requirements × 100.

**Rule:** absence of evidence must produce `N/A`, not an invented zero or fabricated pass rate.

## 35. Requirement Baseline and Version Control
Requirement IDs are stable identifiers. Revisions preserve the original ID and record change history rather than renumbering. Record:
- original version;
- changed version;
- reason;
- source/approval;
- affected features/components/tests;
- verification status;
- release impact.

Baseline status for v1.0: **Draft / Baseline**, with P0/P1/TBD boundaries preserved from the source.

## 36. Traceability Maintenance Triggers
Update this document whenever:
- a requirement changes;
- a feature is added/removed;
- architecture boundaries change;
- code modules change materially;
- database/schema/migrations change;
- AI model/runtime/OCR resources change;
- export formats/behavior change;
- security/privacy controls change;
- tests are added/removed;
- a build/release baseline changes;
- a defect changes requirement disposition.

## 37. Final Master Traceability Matrix
**Legend:** Code is planned unless marked otherwise; Data/AI/OCR/Export are semantic mappings; `TEST-###` IDs are proposed unless an existing source-defined test is referenced.

The master matrix preserves the complete FR-001..FR-054 and NFR-001..NFR-028 requirement inventory, with each requirement retaining its source, category, priority, MVP/Future classification, feature, architecture, planned code boundary, data mapping, AI/OCR mapping, export mapping, test reference, verification method, status, and evidence notes from the approved source document.

## 38. Final Audit Checklist
- [x] Every controlled FR/NFR requirement has an ID.
- [x] Existing authoritative SRS IDs are preserved.
- [x] Every FR/NFR has a source document.
- [x] Every FR/NFR has an SRS acceptance criterion and verification method.
- [x] P0/P1/TBD boundaries are preserved.
- [x] No unsupported backend/API requirement is introduced.
- [x] No fabricated Android class names or filenames are claimed.
- [x] No fabricated test execution results are claimed.
- [x] No fabricated runtime/accuracy/performance percentages are claimed.
- [x] Existing security test identifiers are preserved where referenced.
- [x] Conflicts/ambiguities are explicitly reported.
- [ ] Actual Google AI Studio source tree inspected.
- [ ] Actual implementation-to-requirement mapping verified.
- [ ] Executed test evidence linked to build(s).
- [ ] AI/OCR quantitative benchmark completed.
- [ ] Approved device/performance matrix completed.
- [ ] Release candidate passes all release gates.

## 39. Management Summary
**Traceability is strong at the specification level, but execution evidence is the current bottleneck.** The project baseline is well-defined: the SRS inventory is controlled, the major UI/architecture/data/AI/OCR/export/security/testing documents cross-reference the same core workflow, and the implementation plan provides a sequential critical path.

The highest-risk open items are the actual Android project inspection, exact AI model/runtime, concrete OCR integration, approved device/performance thresholds, multi-page limits, unresolved security decisions, and build-linked execution evidence. Until those are supplied, the correct management status is **PLANNED / NOT VERIFIED**, not “complete.”

## Appendix A — Core Source Alignment
The project baseline defines document → OCR → AI understanding → structured editable data, with local/offline processing after required setup, SQLite/local persistence, and Excel/CSV/JSON/PDF export.

The implementation plan reinforces the no-required-backend/no-required-cloud MVP boundary and evidence-driven release gates.

## Appendix B — Next Traceability Update Inputs
When the actual Android project is provided, record at minimum:
`package/application ID`, language, UI toolkit, build system/plugin versions, manifest permissions, camera implementation, file-picker implementation, SQLite integration, OCR engine/resource integration, AI runtime/model, model packaging, export libraries, sharing API, test framework, CI/build pipeline, and actual logging/diagnostics configuration.

---
**End of `SnapData_REQUIREMENTS_TRACEABILITY_v1.0.md`**
