# SnapData

**AI-assisted document processing and data extraction for Android.**

SnapData is an Android application project for importing document images and PDFs, processing them with OCR and AI-assisted components, reviewing extracted information, saving documents locally, and exporting structured results.

> **Status:** In development. The repository contains Kotlin/Android application source, document-processing modules, UI screens, and unit-test source. A successful release build and end-to-end device workflow have not been verified as part of this README update.

## What is in the repository

- **Android UI:** Kotlin, Jetpack Compose and Material 3 screens for landing, processing, history, settings, review/editing, and authentication flows.
- **Document pipeline:** image pre-processing, OCR, multi-page document handling, and a processing-pipeline module.
- **AI integration:** on-device analysis components and a Gemini service integration. Cloud-backed AI calls depend on configuration and network access.
- **Local data:** Room/SQLite entities, DAO, database, and repository components for document persistence.
- **Export:** an export manager for structured output formats. Verify the specific format and file-sharing flow on a device before relying on it.
- **Device input:** CameraX dependencies and document/image import paths.
- **Authentication:** local/guest and provider-based authentication code, with Firebase integration available when Firebase configuration is supplied.
- **Quality checks:** unit-test source and a GitHub Actions workflow for compilation, lint, tests, and debug packaging.

## Technology stack

| Area | Technology visible in project configuration/source |
| --- | --- |
| Language and UI | Kotlin, Jetpack Compose, Material 3 |
| Android build | Gradle Kotlin DSL, JDK 17, Android SDK 36 |
| OCR and capture | ML Kit Text Recognition, CameraX |
| Local persistence | Room / SQLite |
| AI | On-device analysis components and Gemini SDK integration |
| Networking and serialization | OkHttp, Kotlin Serialization |
| Authentication and session handling | Provider-based auth, Firebase Auth integration, encrypted session-storage implementation |
| Testing and automation | JUnit/MockK test sources, GitHub Actions workflow |

## Processing flow

1. Import an image/PDF or capture an image where the relevant capture flow is available.
2. Pre-process the source and run the available OCR/AI processing components.
3. Review and edit extracted fields in the application UI.
4. Save document data through the repository and local Room database.
5. Export reviewed results using the export module.

Actual behavior can vary with runtime configuration, Android permissions, model availability, and service connectivity.

## Requirements

- Android Studio with a compatible Android SDK installation.
- JDK 17.
- Android SDK Platform 36 as declared by the app module.
- A device or emulator running Android 8.0/API 26 or later for the configured minimum SDK.
- Optional Firebase project configuration for Firebase-backed authentication.
- Optional Gemini API key for Gemini-backed operations.

## Build locally

1. Clone the repository and open it in Android Studio.
2. Configure JDK 17 and install the Android SDK required by the Gradle project.
3. Let Gradle sync, then build the debug app from Android Studio, or run the wrapper command from the repository root:

    # macOS/Linux
    ./gradlew assembleDebug

    # Windows
    gradlew.bat assembleDebug

4. To run the available unit tests:

    # macOS/Linux
    ./gradlew testDebugUnitTest

    # Windows
    gradlew.bat testDebugUnitTest

Build and test commands are documented from the repository configuration; they have not been executed as part of this README change.

## Optional configuration

- **Gemini:** the app's secrets configuration references `.env` and `.env.example`. Use a local, untracked `.env` file for development values when needed. Never commit a real API key or private credentials.
- **Firebase:** add the Firebase configuration file locally under `app/google-services.json` only when you intend to test Firebase-backed authentication. Do not commit private project credentials or signing material.
- **Offline/local behavior:** local storage and on-device OCR components exist, but Gemini-backed functionality may send submitted content to an external service when invoked. Review data flows before processing sensitive documents.

## CI status

A GitHub Actions workflow is present at [`.github/workflows/ci.yml`](.github/workflows/ci.yml). The latest workflow run inspected during this documentation review, dated 2026-09-01, failed. Check the [current Actions history](https://github.com/ishant-rathore/SnapData/actions) for newer results before treating the main branch as passing.

## Security and privacy

- Never commit `.env`, API keys, signing keys, passwords, or private credentials.
- Keep `.env.example` limited to non-secret placeholders.
- Rotate any real or reused credential that may have been exposed in repository history.
- Review whether document content is sent to Gemini or another service before using personal, financial, medical, or otherwise sensitive documents.
- This README does not represent a security audit, successful release verification, or guarantee of production readiness.

## Known verification gaps

- Verify a fresh debug build and unit-test run.
- Run the end-to-end document import, OCR, review, Room persistence, and export flow on a device/emulator.
- Validate Firebase configuration and email-verification flows if those features are used.
- Test large PDFs, supported export formats, permissions, and accessibility.
- Complete release-signing configuration and verify a signed release artifact before distributing the app.

## Contributing

Keep changes focused, add or update tests for behavior changes, and document new configuration without including real secrets.

---

SnapData is a work in progress. Please use the linked source and Actions history to assess the current implementation and verification status.