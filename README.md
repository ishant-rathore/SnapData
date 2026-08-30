# SnapData

**AI-Powered Intelligent Document Processing & Data Extraction System**

SnapData is an offline-first Android application architecture for converting PDF documents and images into structured, editable data using OCR and AI.

## Repository Architecture

See `SnapData_REPO_FOLDER_STRUCTURE_v1.0.md` for the approved repository structure, architectural boundaries, processing pipeline, testing strategy, security rules, and implementation guidance.

## Architecture Principles

- Offline-first/local processing
- Presentation separated from application and domain logic
- OCR and AI isolated behind provider/adaptor boundaries
- SQLite separated from physical file storage
- Export isolated from presentation
- User-reviewed saved data is authoritative for export
- No mandatory backend, REST API, or cloud database for the MVP

## Implementation Status

This repository bootstrap intentionally leaves the concrete Android language, UI toolkit, package/namespace, Gradle configuration, OCR runtime, AI runtime/model, and dependency versions open for technical validation against the actual Google AI Studio-generated Android project.

## Next Step

Add the validated Android application foundation and implement the SnapData processing pipeline incrementally within the established architecture.
