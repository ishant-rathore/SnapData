# SnapData Repository Folder Structure

The approved repository architecture specification is maintained as the source of truth for the SnapData codebase organization.

Refer to the project-approved `SnapData_REPO_FOLDER_STRUCTURE_v1.0.md` specification supplied with the project for the complete repository tree, layer responsibilities, dependency rules, processing pipeline, testing organization, security requirements, and validation guidance.

## Architecture Status

- Android target: confirmed
- Offline-first/local processing: confirmed
- SQLite local persistence: confirmed logical boundary
- OCR boundary: confirmed logical requirement
- Offline AI boundary: confirmed logical requirement
- Mandatory backend: not required for MVP
- Mandatory REST API: not required for MVP
- Cloud database: not required for MVP
- Concrete language/UI toolkit/package/Gradle/OCR runtime/AI runtime: requires technical validation against the actual generated Android project

This repository baseline intentionally avoids inventing technology-specific implementation details before the generated Android project is available for inspection.
