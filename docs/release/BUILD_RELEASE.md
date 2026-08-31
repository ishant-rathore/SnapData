# SnapData: AI-Powered Intelligent Document Processing & Data Extraction System
## Build, Configuration, Deployment & Release Engineering Document

**Document:** `SnapData_BUILD_RELEASE_v1.0.md`  
**Version:** 1.0  
**Status:** Draft / Release Engineering Baseline  
**Date:** 30 August 2026  
**Implementation Target:** Android application generated/built through Google AI Studio — “Build an Android app” workflow

---

## Source-of-Truth Policy

This document is derived from the project PRD, SRS, TRD, system architecture, frontend, database, AI/OCR, document-processing, data-schema, export, testing, security/privacy, UI/UX and original project specification/workflow materials.

**Critical rule:** no concrete build-system, language, UI toolkit, SDK, dependency, AI runtime, OCR integration, signing, CI/CD or device value is treated as **CONFIRMED** unless it is supported by the actual generated Android project or an explicit source-backed decision.

Where evidence is absent, this document uses:

- **TBD** — decision not yet made.
- **PROPOSED** — recommended direction, not baselined as implemented.
- **REQUIRES TECHNICAL VALIDATION** — implementation intent exists, but the actual project/device/build must be inspected or benchmarked before confirmation.
- **CONFIRMED** — established by the supplied source material or directly verified in the actual implementation.
- **REJECTED** — intentionally excluded from the current MVP baseline.

---

# 1. Document Control

| Item | Value |
|---|---|
| Project | SnapData |
| Document | Build, Configuration, Deployment & Release Engineering |
| Version | 1.0 |
| Status | Draft / Release Engineering Baseline |
| Date | 30 August 2026 |
| Target platform | Android application |
| Implementation workflow | Google AI Studio — “Build an Android app” |
| Core architecture | Offline-first / local processing |
| Core persistence | SQLite + Android-local file storage boundary |
| Backend for MVP | Not required / REJECTED as a core dependency |
| REST API for MVP | Not required / REJECTED as a core dependency |
| Release artifacts | APK for direct installation/testing; AAB for Play distribution if selected |
| Exact generated-project stack | REQUIRES TECHNICAL VALIDATION |
| CI/CD provider | TBD |

---

# 2. Purpose

This document defines the controlled process required to configure, build, test, package, sign, validate, release and deploy SnapData as a production-quality Android application.

It covers:

- Android project inspection and configuration.
- Development environment setup.
- Dependency management.
- Source-control rules.
- Debug and release build handling.
- AI/OCR resource packaging and provisioning.
- Offline validation.
- APK/AAB generation.
- Release signing and keystore handling.
- CI/CD architecture.
- Release-candidate gates.
- Production smoke testing.
- Artifact traceability.
- Rollback/recovery planning.
- Build/release risk management.

The document does **not** replace the detailed behavior or implementation documents. It establishes how an approved implementation is built and shipped.

---

# 3. Build Philosophy

SnapData follows this release path:

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
Production Distribution
```

No release should bypass a mandatory gate for the core document-to-export workflow.

The testing baseline explicitly requires quality gates covering static quality, unit/component, integration, AI/OCR, offline behavior, end-to-end behavior, security/privacy and release-candidate validation.

---

# 4. Current Implementation Baseline

The source set confirms the following:

| Area | Status | Evidence / note |
|---|---|---|
| Android application | **CONFIRMED** | Current project target |
| Google AI Studio Android build workflow | **CONFIRMED** | Explicit implementation target |
| Offline-first core workflow | **CONFIRMED** | Product and architecture baseline |
| Local document processing | **CONFIRMED** | Core architectural requirement |
| Camera/PDF/image acquisition | **CONFIRMED** | Core product scope |
| Image preprocessing | **CONFIRMED requirement** | Exact algorithms TBD |
| OCR | **CONFIRMED capability** | Tesseract is source-backed; exact Android integration TBD |
| Offline AI | **CONFIRMED capability** | Exact model/runtime TBD |
| SQLite | **CONFIRMED source-backed** | Exact integration remains TBD |
| Excel/CSV/JSON/PDF export | **CONFIRMED** | Core export requirement |
| Backend | **REJECTED / not required for MVP** | No core server dependency |
| REST API | **REJECTED / not required for MVP** | No core network API |
| Programming language | **REQUIRES TECHNICAL VALIDATION** | Must inspect generated project |
| UI toolkit | **REQUIRES TECHNICAL VALIDATION** | Must inspect generated project |
| Gradle configuration | **REQUIRES TECHNICAL VALIDATION** | Must inspect project files |
| Android Gradle Plugin | **REQUIRES TECHNICAL VALIDATION** | Must inspect project files |
| Kotlin/Java versions | **REQUIRES TECHNICAL VALIDATION** | Must inspect project files |
| compileSdk/targetSdk/minSdk | **REQUIRES TECHNICAL VALIDATION** | Must inspect project files |
| AI runtime | **TBD / REQUIRES TECHNICAL VALIDATION** | Must be benchmarked |
| OCR runtime integration | **REQUIRES TECHNICAL VALIDATION** | Must inspect project |
| Native libraries/ABIs | **REQUIRES TECHNICAL VALIDATION** | Must inspect project |
| CI/CD provider | **TBD** | None selected in baseline |
| Final signing configuration | **REQUIRES TECHNICAL VALIDATION** | Depends on release setup |

The TRD and AI/OCR specification explicitly state that the actual generated Android project must be inspected before promoting these implementation details to confirmed status.

---

# 5. Actual Project Inspection

## 5.1 Inspection Objective

Before concrete release values are finalized, inspect the actual Google AI Studio generated Android project.

## 5.2 Required Inspection Targets

Inspect at minimum:

```text
Project root
├── settings.gradle / settings.gradle.kts
├── build.gradle / build.gradle.kts
├── gradle.properties
├── gradle wrapper
├── app module(s)
├── AndroidManifest.xml
├── source directories
├── resource directories
├── test directories
├── dependency declarations
├── AI model resources / model loader code
├── OCR resources / OCR integration
├── native libraries
├── packaging configuration
├── signing configuration
└── CI/CD configuration, if present
```

## 5.3 Actual Project Configuration Table

| Property | Detected Value | Source File | Status |
|---|---|---|---|
| Project name | TBD | Project root | REQUIRES TECHNICAL VALIDATION |
| Application ID | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Namespace | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Version code | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Version name | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Min SDK | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Target SDK | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Compile SDK | TBD | `app` build configuration | REQUIRES TECHNICAL VALIDATION |
| Build tools | TBD | Build configuration | REQUIRES TECHNICAL VALIDATION |
| Programming language | TBD | Source tree/build configuration | REQUIRES TECHNICAL VALIDATION |
| UI toolkit | TBD | Source tree/dependencies | REQUIRES TECHNICAL VALIDATION |
| Kotlin version | TBD | Plugin/version catalog/build file | REQUIRES TECHNICAL VALIDATION |
| Java/JDK | TBD | Build config/toolchain | REQUIRES TECHNICAL VALIDATION |
| Gradle version | TBD | `gradle-wrapper.properties` | REQUIRES TECHNICAL VALIDATION |
| Android Gradle Plugin | TBD | Build configuration | REQUIRES TECHNICAL VALIDATION |
| NDK | TBD | Build config | REQUIRES TECHNICAL VALIDATION if applicable |
| CMake | TBD | Build config | REQUIRES TECHNICAL VALIDATION if applicable |
| Build variants | TBD | Build configuration | REQUIRES TECHNICAL VALIDATION |
| Signing config | TBD | Build configuration/secure config | REQUIRES TECHNICAL VALIDATION |
| AI runtime | TBD | Dependencies/source | TBD / validation |
| AI model identifier | TBD | Model manifest/configuration | TBD / validation |
| OCR engine integration | TBD | Dependencies/source | REQUIRES TECHNICAL VALIDATION |
| Native ABIs | TBD | Packaging config/libs | REQUIRES TECHNICAL VALIDATION |
| CI/CD | TBD | Repository/config | TBD |

**Important:** this table is intentionally not populated with guesses. The project sources currently available for this baseline do not include the generated Android source tree/build artifact.

---

# 6. Project Configuration

Concrete values for the following remain open until the real project is inspected:

- Application ID.
- Namespace.
- Version code/name.
- minSdk.
- targetSdk.
- compileSdk.
- Build tools.
- Kotlin version, if Kotlin is used.
- Java/JDK version.
- Gradle version.
- Android Gradle Plugin version.
- NDK/CMake versions, if applicable.
- Exact build variants.
- Dependency versions.

The final release baseline must record the detected values and preserve them in the release record.

---

# 7. Source Control

## 7.1 Repository Expectations

The repository should contain source and reproducible project configuration, not machine-local state.

A conceptual structure is:

```text
/
├── app/                         # actual generated application module(s)
├── gradle/                      # wrapper/configuration when used
├── docs/                        # project documentation
├── tests/                       # additional test assets where applicable
├── models/                      # only approved metadata/resources; avoid unnecessary duplication
├── scripts/                     # reproducible build/release utilities when required
├── .gitignore
├── README.md
├── settings.gradle*            # actual generated form
├── build.gradle*               # actual generated form
└── gradle.properties
```

This structure is **conceptual**. The actual Google AI Studio-generated structure takes precedence.

## 7.2 Source-Control Rules

Do not commit:

- build outputs;
- IDE-local state;
- `local.properties` or equivalent local machine configuration;
- local databases;
- private test documents;
- exported user files;
- secrets;
- keystores/private signing keys;
- passwords/tokens;
- temporary model downloads unless deliberately versioned and approved;
- machine-specific caches.

---

# 8. Git Branching

A lightweight branching strategy is recommended:

```text
main
  └── release/*

development
  ├── feature/*
  └── fix/*
```

| Branch | Purpose | Release status |
|---|---|---|
| `main` | Stable/releasable baseline | Production candidate/production |
| `development` | Integrated development | Not production |
| `feature/*` | Isolated feature work | Not releasable by default |
| `fix/*` | Targeted defect corrections | Not releasable by default |
| `release/*` | Release hardening | Release candidate |

**Status:** PROPOSED. Use the actual repository strategy if one already exists.

---

# 9. Commit Policy

Commits should be:

- atomic where practical;
- descriptive;
- scoped to one logical change;
- free of secrets;
- free of generated release artifacts;
- free of private document data.

Recommended format:

```text
<type>: <summary>
```

Example categories may include `feat`, `fix`, `test`, `refactor`, `docs`, `build`, and `release`.

This naming pattern is **PROPOSED**, not a mandatory external standard.

---

# 10. `.gitignore` Baseline

The repository should exclude, as applicable to the actual project:

```gitignore
# Build output
build/
*/build/

# Gradle caches / machine-local state
.gradle/
local.properties

# IDE-local state
.idea/
*.iml

# OS/editor noise
.DS_Store
Thumbs.db

# Signing / secrets
*.jks
*.keystore
*.p12
*.pem
*.key
.env
*.secret

# Local databases / app data
*.db
*.sqlite
*.sqlite3

# Temporary documents / private test data
/tmp/
/test-data/private/
/exports/

# Logs / reports generated locally
*.log
```

The exact ignore rules must be adapted to the generated project so required source-controlled files are not accidentally excluded.

---

# 11. Dependency Management

The project must maintain an inventory for every major dependency category.

| Category | Purpose | Version | Source | License | Security status | Required? | Status |
|---|---|---|---|---|---|---|---|
| Android platform | Core runtime/platform APIs | TBD | Generated project | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |
| UI | Application UI | TBD | Generated project | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |
| Camera | Camera acquisition if implemented | TBD | TBD | TBD | Review required | Yes for camera scope | REQUIRES TECHNICAL VALIDATION |
| PDF processing | PDF input/rendering | TBD | TBD | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |
| Image processing | Preprocessing | TBD | TBD | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |
| OCR | Text extraction | TBD | Tesseract-backed integration | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |
| AI runtime | Offline inference | TBD | TBD | TBD | Review required | Yes | TBD / validation |
| SQLite integration | Local persistence | TBD | TBD | TBD | Review required | Yes | CONFIRMED requirement / integration TBD |
| Serialization | Structured JSON/data transport | TBD | TBD | TBD | Review required | Yes | TBD |
| Excel export | `.xlsx` generation | TBD | TBD | TBD | Review required | Yes | TBD / validation |
| CSV export | `.csv` generation | TBD | TBD | TBD | Review required | Yes | TBD |
| PDF export | Result PDF generation | TBD | TBD | TBD | Review required | Yes | TBD / validation |
| Test libraries | Unit/integration/UI tests | TBD | Generated project | TBD | Review required | Yes | REQUIRES TECHNICAL VALIDATION |

Do not promote a library name or version from the historical workflow diagram to a confirmed implementation merely because it appears in the diagram.

---

# 12. Dependency Pinning

Production builds should be deterministic.

The release baseline should:

1. Pin or otherwise deterministically resolve approved dependency versions.
2. Record plugin and tool versions.
3. Use the project's lock/version-catalog mechanism where supported.
4. Avoid floating or uncontrolled dependency ranges.
5. Review dependency updates before release.
6. Preserve the resolved dependency set used for the released artifact.

Exact dependency-locking mechanics are **REQUIRES TECHNICAL VALIDATION** after project inspection.

---

# 13. Dependency Security

Before release:

- inspect direct dependencies;
- inspect important transitive dependencies;
- identify known vulnerabilities using an approved scanner/process;
- remove unused libraries;
- review native dependencies and bundled assets;
- document accepted exceptions;
- re-run checks after dependency upgrades.

No security exception should be silently carried into production.

---

# 14. Environments

Because SnapData is an offline Android application, “environment” refers primarily to **build/test/release configurations**, not server environments.

| Environment | Purpose | Network assumption | Release authority |
|---|---|---|---|
| Development | Local feature development | Network may be used for development/setup | Developer |
| Testing | Functional/integration validation | Controlled; offline tests mandatory | QA/Technical |
| Staging / Release Candidate | Final pre-release validation | Full offline + controlled network checks | Release owner |
| Production | Distributed application | Core processing offline after required setup | Approved release |

No cloud backend is required for the current MVP baseline.

---

# 15. Build Variants

At minimum, the release process should distinguish:

- **debug** — development/testing.
- **release** — production candidate/production.

Optional variants such as `staging`, `internal`, or `benchmark` should only be introduced when they provide real release or validation value.

**Status:** PROPOSED until confirmed by the generated project.

---

# 16. Debug Build

A debug build is intended for development and test use.

Expected characteristics:

- debuggable where the generated project supports it;
- development diagnostics permitted;
- non-production signing;
- local testing only;
- increased developer visibility for failures;
- no production credentials or release signing material.

Development-only logging must never expose sensitive document content.

---

# 17. Release Build

A release build must:

- disable development/debug behavior;
- use production configuration;
- use release signing;
- exclude development credentials/configuration;
- package all required application resources;
- package/prepare AI/OCR resources according to the validated design;
- pass functional, AI/OCR, offline, security and compatibility validation;
- preserve local processing requirements;
- be traceable to a source commit and validated build inputs.

---

# 18. Build-Type Security

Before release, verify:

| Check | Debug | Release |
|---|---|---|
| Debuggable | Allowed for development | Must be off |
| Test credentials | Never ship | Must be absent |
| Verbose sensitive logging | Must not log document data | Must be absent |
| Debug endpoints | If any are used, development only | Must be absent |
| Test data | Development only | Must be absent |
| Signing | Debug/test signing | Protected release signing |

Because no backend is required for the MVP, there should not be a hidden requirement for a debug or production REST endpoint.

---

# 19. Versioning

## 19.1 Version Name

User-visible application version.

Recommended policy:

```text
MAJOR.MINOR.PATCH
```

Examples:

```text
1.0.0
1.0.1
1.1.0
2.0.0
```

The exact initial release version is **TBD**.

## 19.2 Version Code

An internal monotonically increasing build number used by Android distribution tooling.

Example only:

```text
100
```

The actual starting value and increment rules are **TBD / REQUIRES TECHNICAL VALIDATION**.

## 19.3 Increment Rules

| Change | Version component |
|---|---|
| Breaking product/behavioral change | MAJOR |
| Backward-compatible feature addition | MINOR |
| Bug/security/compatibility fix | PATCH |
| Internal build rerun with no product version change | Version code only |

This policy is **PROPOSED** and must align with the final release process.

---

# 20. Build Reproducibility

A release should be reproducible from a recorded build input set:

```text
Source commit
+ Build configuration
+ Gradle/plugin/tool versions
+ Dependency versions
+ AI model/version
+ OCR version/resources
+ Build metadata
+ Signing configuration reference
        ↓
Released artifact
```

The release record should retain:

- Git commit hash;
- branch/tag;
- version name;
- version code;
- detected Android build configuration;
- dependency snapshot/lock state where supported;
- AI model identifier/version/checksum if applicable;
- OCR version/resource set;
- build timestamp;
- artifact SHA-256 or equivalent checksum;
- signer/release channel reference.

---

# 21. AI Model Packaging

The architecture establishes offline AI capability, but the exact model packaging strategy is not yet confirmed.

Possible implementation patterns are:

1. Model packaged inside the application.
2. Model provisioned/downloaded during first-run setup.
3. Model installed through another approved mechanism.
4. Hybrid approach.

**Current status: TBD / REQUIRES TECHNICAL VALIDATION.**

The actual project must be inspected before selecting one as implemented.

## 21.1 Required Model Metadata

| Property | Value | Status |
|---|---|---|
| Model ID | TBD | REQUIRES TECHNICAL VALIDATION |
| Model version | TBD | REQUIRES TECHNICAL VALIDATION |
| Runtime | TBD | REQUIRES TECHNICAL VALIDATION |
| Package format | TBD | REQUIRES TECHNICAL VALIDATION |
| Size | TBD | REQUIRES TECHNICAL VALIDATION |
| Checksum | TBD | REQUIRES TECHNICAL VALIDATION |
| Minimum storage | TBD | REQUIRES TECHNICAL VALIDATION |
| Runtime compatibility | TBD | REQUIRES TECHNICAL VALIDATION |
| Supported devices | TBD | REQUIRES TECHNICAL VALIDATION |

The database stores model metadata/reference information; it must not be treated as the storage location for the model binary itself.

---

# 22. AI Model Setup Lifecycle

The product-level setup lifecycle is:

```text
Launch
  ↓
Check model readiness
  ↓
If not ready → Show setup
  ↓
Download / provision
  ↓
Validate resource
  ↓
Load/test readiness
  ↓
Mark Ready
  ↓
Enable offline processing
```

The core workflow must not silently fall back to a cloud AI service when the local model is unavailable.

### Failure behavior

```text
Incomplete / corrupt / invalid model
        ↓
Reject
        ↓
Remove or quarantine incomplete resource
        ↓
Show recovery state
        ↓
Retry / repair / re-provision
```

Exact provisioning mechanics remain TBD.

---

# 23. Model Integrity

Where model resources are downloaded or otherwise provisioned, the implementation should validate, as supported:

- expected resource identity;
- expected version;
- checksum/signature where supported;
- successful completion;
- file/resource availability;
- runtime compatibility.

A resource must not enter the “Ready” state merely because a file exists.

**Status:** PROPOSED control; exact cryptographic mechanism requires validation.

---

# 24. OCR Resource Packaging

The source baseline identifies Tesseract OCR as the OCR context, but the exact Android integration is not finalized.

Release engineering must capture:

- OCR engine/version;
- language resources;
- trained data/resources;
- packaging location;
- initialization method;
- resource validation;
- failure/recovery behavior;
- native library/ABI implications, if applicable.

For a Tesseract-backed integration, the exact Android packaging model is **REQUIRES TECHNICAL VALIDATION**.

---

# 25. Large Model / Resource Handling

The application must define and test behavior for:

- insufficient device storage;
- incomplete model download/provisioning;
- interrupted setup;
- corrupted model resources;
- unsupported device/runtime;
- memory pressure;
- app interruption during setup;
- uninstall/reinstall behavior.

Exact thresholds are TBD until benchmarked.

---

# 26. Offline Build Validation

A release candidate must be tested under:

- Wi-Fi disabled;
- mobile data disabled;
- airplane mode.

After required AI setup, verify:

```text
Launch
 ↓
Document input
 ↓
Preprocessing
 ↓
OCR
 ↓
Offline AI
 ↓
Structured data
 ↓
Review/edit
 ↓
Save
 ↓
History/reopen
 ↓
Export
```

No unexpected network dependency is acceptable on the core processing path.

The testing and security baselines make offline validation a mandatory release gate.

---

# 27. Android Manifest Review

Before release, inspect the actual `AndroidManifest.xml` and runtime behavior for:

- application components;
- activities;
- services;
- providers;
- exported components;
- intent filters;
- application flags;
- permissions;
- backup-related behavior where configured;
- cleartext/network behavior if applicable;
- debug/release differences.

Least privilege applies. Unnecessary permissions must not be requested.

---

# 28. Android Permissions

The exact permission set is not yet confirmed.

Potential permissions/capabilities, only where actually required by the implementation, include:

| Permission/capability | Purpose | Runtime? | Risk | Status |
|---|---|---|---|---|
| Camera | Document scanning | TBD | Camera privacy | REQUIRES TECHNICAL VALIDATION |
| File/document access | User-selected PDF/image input | TBD | File/privacy | REQUIRES TECHNICAL VALIDATION |
| Storage access | Only if technically required by chosen storage APIs | TBD | Data exposure | REQUIRES TECHNICAL VALIDATION |
| Network | Initial model setup, if required | TBD | Network exposure | REQUIRES TECHNICAL VALIDATION |

Do not add a permission merely because it appears in a legacy technology plan.

---

# 29. Application Identity

Record the verified values for:

- Application ID.
- Namespace.
- App label.
- Version name.
- Version code.
- Launcher icon.
- Splash/startup behavior.
- Supported launcher behavior.

All concrete values remain **TBD / REQUIRES TECHNICAL VALIDATION** until the actual project is inspected.

---

# 30. Resource Configuration

Release packaging must account for:

- app icons;
- strings/resources;
- themes;
- fonts, if packaged;
- layout/Compose resources according to the actual UI framework;
- OCR language files;
- AI model resources;
- native libraries;
- export resources/templates, if any.

Large binary resources must not be duplicated unnecessarily.

---

# 31. Build Optimization

Evaluate, without prematurely enabling unverified optimization:

- code shrinking;
- resource shrinking;
- R8/ProGuard where applicable;
- APK size;
- AAB size;
- installed size;
- AI model footprint;
- OCR resource footprint;
- native library footprint.

Aggressive shrinking should only be enabled after AI/OCR/native integrations are validated in the release build.

---

# 32. R8 / ProGuard

If release shrinking is enabled:

1. Define required keep rules.
2. Protect reflection-based libraries where applicable.
3. Protect serialization/deserialization paths.
4. Protect OCR/native integrations where required.
5. Build a release artifact.
6. Execute the complete release smoke test.
7. Execute AI/OCR and export validation.
8. Compare against the non-shrunk reference build where useful.

A release build that passes compilation but breaks runtime AI/OCR behavior is a release failure.

Exact rules are **REQUIRES TECHNICAL VALIDATION**.

---

# 33. Native Libraries

If OCR/AI/PDF/image libraries use native binaries, record:

- supported ABIs;
- library names/versions;
- packaging mode;
- debug/release differences;
- emulator support;
- physical-device support.

Potential ABI examples include:

```text
arm64-v8a
armeabi-v7a
x86
x86_64
```

These are **examples only**. The actual supported ABI set is **REQUIRES TECHNICAL VALIDATION**.

---

# 34. APK vs AAB

## APK

Use APK artifacts for:

- direct device installation;
- local QA;
- emulator/reference testing;
- controlled internal distribution.

## AAB

Use an AAB when Google Play distribution is selected and the generated Android project is configured accordingly.

**Important:** generating an AAB does not mean that SnapData has been published to Google Play. Publication remains a separate release/distribution activity.

---

# 35. Signing

## 35.1 Debug Signing

Debug builds may use the generated/default debug signing path appropriate to the project.

## 35.2 Release Signing

Release builds must use protected production signing material.

Never commit:

- keystores;
- private keys;
- keystore passwords;
- signing credentials;
- recovery secrets.

The actual signing implementation is **REQUIRES TECHNICAL VALIDATION**.

---

# 36. Keystore Management

The release process must define:

1. Creation.
2. Secure storage.
3. Restricted access.
4. Backup.
5. Recovery.
6. Ownership.
7. Rotation/replacement strategy where supported.
8. Emergency release procedure.

For a student/miniproject context, the operational requirement is still strict: release signing material must not live in source control or unprotected project files.

The exact organizational custody model is **TBD**.

---

# 37. Release Signing Checklist

```text
[ ] Correct release keystore
[ ] Correct signing configuration
[ ] Release artifact is not debug-signed
[ ] Correct application ID
[ ] Correct version name
[ ] Correct version code
[ ] Signing credentials are external to source control
[ ] Keystore backup exists in an approved secure location
[ ] Artifact checksum recorded
[ ] Installed release build verified
```

---

# 38. Build Commands

The actual build commands must be taken from the generated project's build system after inspection.

Typical Gradle commands are shown only as **examples** and must not be treated as confirmed until the project is validated:

```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew bundleRelease
```

If the generated project uses different module/task names, the release document must be updated with the actual commands.

---

# 39. Clean Build

Recommended clean-build sequence:

```text
Clean generated outputs
        ↓
Resolve approved dependencies
        ↓
Compile
        ↓
Run tests
        ↓
Package
        ↓
Validate artifact
```

A clean build is particularly appropriate when:

- changing build plugins/toolchain versions;
- changing native libraries;
- changing packaging/shrinker configuration;
- preparing a release candidate;
- investigating cache-related failures;
- validating reproducibility.

A clean build does not replace automated testing.

---

# 40. Local Development Workflow

Developer workflow:

1. Obtain the repository.
2. Install the required JDK/Android tooling identified by the project.
3. Open the generated project in the required IDE/tooling.
4. Synchronize or resolve dependencies.
5. Build a debug variant.
6. Run unit/component tests.
7. Install on emulator/reference device.
8. Exercise the core SnapData workflow.
9. Validate offline behavior where the model is ready.
10. Review logs for sensitive-data leakage.

Exact tooling versions remain TBD until project inspection.

---

# 41. Development Device Setup

Document the validated development setup for:

- Android Studio or equivalent toolchain, if required;
- Android SDK;
- required JDK;
- emulator configuration;
- physical Android reference devices;
- USB debugging, if used;
- Google AI Studio-generated project import/build setup;
- model/OCR local resources.

No concrete version is asserted here without project evidence.

---

# 42. Test Build Pipeline

The release engineering pipeline should follow:

```text
Checkout
   ↓
Validate dependencies/tooling
   ↓
Static analysis / compile
   ↓
Unit tests
   ↓
Integration tests
   ↓
AI/OCR validation
   ↓
Build APK
   ↓
Install on test target
   ↓
UI/E2E tests
   ↓
Offline validation
   ↓
Security/privacy validation
   ↓
Release candidate artifact
```

This pipeline reflects the existing testing, architecture and security documents.

---

# 43. CI/CD

**Current status: TBD.**

No specific provider is assumed.

Possible provider classes include repository-integrated CI, dedicated mobile CI services, or another approved pipeline. Provider selection must follow the actual repository/workflow and must not be invented in this document.

## 43.1 Provider-Neutral Pipeline

```text
Checkout source
      ↓
Validate project configuration
      ↓
Resolve/pin dependencies
      ↓
Static analysis
      ↓
Unit/component tests
      ↓
Integration tests
      ↓
Build debug/test artifact
      ↓
AI/OCR validation
      ↓
Offline validation
      ↓
Release build
      ↓
Sign
      ↓
Artifact verification
      ↓
Publish/store release artifact
```

---

# 44. CI Secrets

If CI is introduced, secure CI secret storage must be used.

Secrets include, as applicable:

- release keystore credentials;
- encrypted signing material references;
- repository credentials;
- distribution credentials;
- tokens;
- private release metadata.

Never store these in:

- repository source files;
- `gradle.properties` committed to Git;
- shell scripts committed to the repository;
- application resources;
- test data files.

---

# 45. Artifact Management

Every release artifact should be traceable to:

- application version;
- build number/version code;
- source commit;
- build date/time;
- model version;
- OCR version/resource set;
- artifact format (APK/AAB);
- signing identity/reference;
- SHA-256 or equivalent checksum.

A conceptual artifact record:

```text
SnapData
Version: TBD
Build: TBD
Commit: <git-sha>
Model: <model-id/version>
OCR: <engine/resource version>
Artifact: APK/AAB
Checksum: <sha-256>
Built: <timestamp>
```

---

# 46. Release Candidate

A release candidate is created only after development changes are frozen for release hardening.

Flow:

```text
Development
   ↓
Release branch/candidate
   ↓
QA validation
   ↓
Security/privacy validation
   ↓
Performance validation
   ↓
Offline validation
   ↓
Release smoke test
   ↓
Approval
   ↓
Production artifact
```

---

# 47. Release Checklist

## Source

```text
[ ] Correct release branch/tag
[ ] Correct source commit
[ ] No unintended uncommitted changes
[ ] Release notes prepared
```

## Dependencies

```text
[ ] Approved versions verified
[ ] Vulnerability review completed
[ ] Unused dependencies removed
[ ] Dependency lock/resolution state recorded where applicable
```

## Build

```text
[ ] Clean build completed
[ ] Debug build tested
[ ] Release build tested
[ ] Release configuration reviewed
[ ] Shrinker behavior validated
```

## AI/OCR

```text
[ ] Model resource available
[ ] Model readiness verified
[ ] Model integrity verified where supported
[ ] OCR resources verified
[ ] OCR validated
[ ] Offline AI validated
```

## Functional

```text
[ ] Camera input
[ ] Image input
[ ] PDF input
[ ] Preprocessing
[ ] OCR
[ ] AI extraction
[ ] Fields
[ ] Tables
[ ] Review
[ ] Edit
[ ] Save
[ ] History
[ ] Reopen
[ ] Delete
```

## Offline

```text
[ ] Airplane mode launch verified
[ ] Core document processing verified offline
[ ] No unexpected network dependency
[ ] Export works offline after model setup
```

## Export

```text
[ ] Excel
[ ] CSV
[ ] JSON
[ ] PDF
[ ] Export validation completed
[ ] Android share handoff tested where supported
```

## Security / Privacy

```text
[ ] No secrets in repository/artifacts
[ ] Permissions reviewed
[ ] Sensitive logs reviewed
[ ] File/path security tests passed
[ ] No unexpected network document upload
[ ] Release signing verified
```

## Release

```text
[ ] Correct version name
[ ] Correct version code
[ ] Correct application ID
[ ] Correct release signature
[ ] Artifact checksum recorded
[ ] Production smoke test passed
[ ] Approval recorded
```

---

# 48. Production Smoke Test

After installing the release build:

1. Launch SnapData.
2. Verify the Home/entry experience.
3. Import a supported image or PDF.
4. Verify preprocessing.
5. Verify OCR output.
6. Verify offline AI readiness and processing.
7. Verify extracted fields.
8. Verify detected tables.
9. Edit at least one field.
10. Save.
11. Reopen from history.
12. Confirm the correction persisted.
13. Export JSON.
14. Export CSV.
15. Export Excel.
16. Export PDF.
17. Verify generated files.
18. Verify history behavior.
19. Disable all network connectivity.
20. Repeat the core processing flow.

This smoke test should use approved/fictitious validation documents, not private production documents.

---

# 49. Offline Release Acceptance

Offline acceptance is mandatory after required AI model setup.

Expected sequence:

```text
Document Acquisition
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
Edit
       ↓
Save
       ↓
History
       ↓
Export
```

A release is blocked if the core path unexpectedly requires network access.

The source security/testing documents explicitly treat offline behavior as a release gate.

---

# 50. Performance Release Check

Measure, on the approved device matrix:

- launch time;
- model load time;
- OCR time;
- AI inference time;
- total processing time;
- memory use;
- storage use;
- export time;
- large-document behavior;
- repeated-processing stability.

Exact thresholds are intentionally **TBD / REQUIRES TECHNICAL VALIDATION**.

The AI/OCR specification specifically requires measuring load time, inference time and memory/storage footprint before establishing a technical release baseline.

---

# 51. Compatibility Release Check

The final compatibility matrix must be established by technical validation.

Suggested dimensions:

| Dimension | Status |
|---|---|
| Android version range | TBD |
| RAM classes | TBD |
| CPU architecture | TBD |
| GPU/NPU capability | TBD |
| Storage capacity | TBD |
| Camera capabilities | TBD |
| Screen sizes | TBD |
| ABI support | TBD |
| Emulator support | TBD |

Do not publish a hard supported-device claim until this matrix is measured and approved.

---

# 52. App Size

Track:

- base APK/AAB size;
- AI model size;
- OCR language/resource size;
- native library size;
- total installed footprint;
- temporary working-storage requirements.

No fixed size target is imposed here without validation.

The release record should capture measured values so future releases can be compared.

---

# 53. Large Document and Storage Release Check

The release candidate must be evaluated for:

- large PDF input;
- multi-page documents where supported;
- image batches where supported;
- temporary storage pressure;
- database growth;
- export size;
- interrupted processing;
- insufficient storage.

Exact maximum document/page limits are **REQUIRES TECHNICAL VALIDATION**.

---

# 54. Database Release Validation

The database baseline is SQLite plus Android-local file storage, with separate failure boundaries.

Before release, validate:

- schema creation;
- migrations from each supported prior schema;
- CRUD behavior;
- save/reopen;
- user-edit persistence;
- history retrieval;
- deletion;
- orphan handling;
- transaction behavior;
- database integrity after interruption;
- no cloud/server dependency in the core persistence path;
- no secrets or unnecessary sensitive values stored in prohibited places.

The database document explicitly requires deterministic handling when database and physical-file state diverge.

---

# 55. Export Release Validation

The release candidate must validate all required formats:

| Format | Validation |
|---|---|
| Excel | Valid `.xlsx`; expected fields/tables present |
| CSV | Valid syntax; correct escaping/encoding |
| JSON | Valid JSON; reflects saved structured result |
| PDF | Readable document/result representation |

Critical export rule:

> User-saved/edited structured data is authoritative for export.

Export must not silently revert to stale OCR/AI output.

Export generation must be read-only with respect to the authoritative saved result.

---

# 56. Security and Privacy Release Validation

Before release:

- inspect manifest and permissions;
- review file and path handling;
- test malformed/untrusted documents;
- test resource-exhaustion cases;
- review logging;
- confirm no sensitive document content is written to routine logs;
- confirm model setup cannot activate invalid resources;
- confirm exports do not leak internal metadata unnecessarily;
- test sharing as a privacy boundary;
- verify release signing;
- verify no secrets in source/artifacts;
- review backup-related configuration if present.

The security baseline emphasizes local processing, minimal logging, validated inputs, authoritative saved edits, safe export/share behavior and evidence-based security claims.

---

# 57. Release Blocking Criteria

A release **SHALL be blocked** by any of the following:

1. Core processing unexpectedly depends on a network connection after required model setup.
2. Release artifact is debug-signed or uses an unauthorized signing identity.
3. User edits are lost, overwritten or not used for export.
4. Data or history corruption is detected on an approved device.
5. Critical OCR/AI regressions are present without approved disposition.
6. Required export formats fail validation.
7. Critical privacy/security defects remain unresolved.
8. Consistent crash on the supported/reference device occurs during the P0 workflow.
9. Model/resource corruption can result in a false “ready” state.
10. Release acceptance criteria remain unmet without explicit approved exception.

---

# 58. Artifact Verification

After building/signing:

1. Verify the expected artifact exists.
2. Record artifact filename.
3. Record artifact size.
4. Record SHA-256 checksum.
5. Verify application ID.
6. Verify version name/code.
7. Verify signing information using approved tooling.
8. Install artifact on a clean/reference device.
9. Run smoke test.
10. Archive the final release record.

Artifact size alone must not be treated as proof of correctness.

---

# 59. Release Notes

Each release should include:

- version;
- build number;
- release date;
- major feature changes;
- important fixes;
- known limitations;
- compatibility changes;
- AI/OCR model/resource changes;
- export changes;
- security changes;
- migration requirements, if any;
- rollback notes.

The release note must distinguish confirmed capabilities from known TBD/limited areas.

---

# 60. Rollback and Recovery

For a locally installed Android application, rollback is primarily a **distribution/install strategy** rather than a server rollback.

Recovery scenarios include:

### A. Faulty release artifact

```text
Stop distribution
   ↓
Identify prior approved artifact
   ↓
Revalidate prior artifact integrity
   ↓
Distribute approved prior build
   ↓
Create corrective release
```

### B. Database migration defect

The implementation must follow the validated migration/recovery design. Downgrade behavior must not be assumed unless explicitly supported and tested.

### C. Model provisioning defect

The application should preserve a safe not-ready state and allow retry/recovery rather than activating an invalid model.

Exact rollback mechanics are **REQUIRES TECHNICAL VALIDATION**.

---

# 61. Incident Response for Release Defects

For a serious post-release defect:

1. Classify severity.
2. Stop further rollout where possible.
3. Identify affected version/build.
4. Determine whether the defect is data-integrity, privacy/security, functional or compatibility related.
5. Preserve diagnostic information without collecting document contents.
6. Identify the last known-good release.
7. Reproduce with safe test data.
8. Prepare and validate the fix.
9. Re-run the applicable release gates.
10. Publish corrective release notes.

The application must not rely on sensitive document content in normal diagnostics.

---

# 62. Build and Release Governance

## 62.1 Change Approval

Changes requiring technical/release review include:

- build-system/plugin changes;
- major dependency upgrades;
- AI runtime/model changes;
- OCR engine/resource changes;
- packaging changes;
- signing changes;
- minimum Android version changes;
- ABI changes;
- data-schema/migration changes;
- export-library changes;
- introduction of network/cloud dependencies.

## 62.2 Documentation Synchronization

When an implementation decision becomes confirmed, update the affected source document(s):

- TRD;
- SYSTEM_ARCHITECTURE;
- FRONTEND;
- DATABASE;
- AI_OCR;
- EXPORT;
- TESTING;
- SECURITY_PRIVACY;
- this BUILD_RELEASE document.

Do not leave release documentation claiming `TBD` after an implementation has been validated and baselined.

---

# 63. Traceability to Existing Project Documents

| Project document | Build/release dependency |
|---|---|
| `SnapData_PRD_v1.0.md` | Product scope and release intent |
| `SnapData_SRS_v1.0.md` | Software behavior and acceptance requirements |
| `SnapData_TRD_v1.0.md` | Technical baseline and open technical decisions |
| `SnapData_SYSTEM_ARCHITECTURE_v1.0.md` | Component boundaries and local-first architecture |
| `SnapData_FRONTEND_v1.0.md` | Frontend implementation constraints |
| `SnapData_DATABASE_v1.0.md` | SQLite schema, migrations and persistence behavior |
| `SnapData_AI_OCR_v1.0.md` | OCR/model/runtime validation and resource handling |
| `SnapData_DOCUMENT_PROCESSING_v1.0.md` | Pipeline, states, interruption and recovery |
| `SnapData_DATA_SCHEMA_v1.0.md` | Canonical structured-data contract |
| `SnapData_EXPORT_v1.0.md` | Export correctness and file/share requirements |
| `SnapData_TESTING_v1.0.md` | Test levels, gates, offline and release acceptance |
| `SnapData_SECURITY_PRIVACY_v1.0.md` | Security/privacy controls and release gates |
| `SnapData_UI_UX_v1.0.md` | UI behavior and user-facing release checks |
| Original project specification | Core product intent |
| Workflow diagram | End-to-end process visualization and historical technology context |

---

# 64. Technical Decision Register

| ID | Decision | Status | Resolution evidence |
|---|---|---|---|
| BR-001 | Actual generated Android language | **REQUIRES TECHNICAL VALIDATION** | Generated source inspection |
| BR-002 | Actual UI toolkit | **REQUIRES TECHNICAL VALIDATION** | Generated source/dependencies |
| BR-003 | Gradle wrapper version | **REQUIRES TECHNICAL VALIDATION** | `gradle-wrapper.properties` |
| BR-004 | Android Gradle Plugin | **REQUIRES TECHNICAL VALIDATION** | Build configuration |
| BR-005 | compileSdk/targetSdk/minSdk | **REQUIRES TECHNICAL VALIDATION** | Build configuration |
| BR-006 | JDK/Java version | **REQUIRES TECHNICAL VALIDATION** | Toolchain/build config |
| BR-007 | Exact dependency versions | **REQUIRES TECHNICAL VALIDATION** | Dependency files/lock state |
| BR-008 | Build variants | **REQUIRES TECHNICAL VALIDATION** | Build configuration |
| BR-009 | AI model | **TBD / REQUIRES TECHNICAL VALIDATION** | AI benchmark |
| BR-010 | AI runtime | **TBD / REQUIRES TECHNICAL VALIDATION** | Runtime inspection + benchmark |
| BR-011 | OCR integration | **REQUIRES TECHNICAL VALIDATION** | Source/dependencies |
| BR-012 | AI model packaging | **TBD / REQUIRES TECHNICAL VALIDATION** | Setup implementation |
| BR-013 | Native ABIs | **REQUIRES TECHNICAL VALIDATION** | Packaging/source inspection |
| BR-014 | R8/shrinker policy | **TBD / REQUIRES TECHNICAL VALIDATION** | Release build validation |
| BR-015 | File storage implementation | **REQUIRES TECHNICAL VALIDATION** | Source inspection |
| BR-016 | Release signing setup | **REQUIRES TECHNICAL VALIDATION** | Secure build configuration |
| BR-017 | CI/CD provider | **TBD** | Project decision |
| BR-018 | Device support matrix | **TBD / REQUIRES TECHNICAL VALIDATION** | Compatibility testing |
| BR-019 | Performance thresholds | **TBD / REQUIRES TECHNICAL VALIDATION** | Benchmark results |
| BR-020 | Initial release version/code | **TBD** | Release decision |

---

# 65. Release Engineering Validation Plan

## Phase 1 — Inspect Actual Android Project

- Inspect source tree.
- Inspect build files.
- Inspect manifest.
- Inspect test configuration.
- Inspect dependencies.
- Inspect AI/OCR integrations.
- Inspect model assets/provisioning.
- Inspect native libraries.
- Inspect signing configuration.

**Output:** completed Actual Project Configuration table.

## Phase 2 — Establish Build Baseline

- Confirm JDK/toolchain.
- Confirm Gradle/AGP.
- Confirm SDK levels.
- Confirm variants.
- Confirm dependency resolution.
- Confirm local debug build.

## Phase 3 — Validate OCR

- Build and run OCR integration.
- Validate required language resources.
- Validate preprocessing path.
- Validate error handling.
- Validate release build behavior.

## Phase 4 — Validate Offline AI

- Benchmark candidate model/runtime.
- Measure load time.
- Measure inference time.
- Measure memory/storage footprint.
- Validate model readiness state.
- Validate airplane-mode processing after setup.

## Phase 5 — Validate Document Processing

- Camera capture.
- Image input.
- PDF input.
- Multi-page PDF where supported.
- Preprocessing stages.
- Cancellation.
- Interruption/recovery.
- Partial-result behavior.

## Phase 6 — Validate Persistence

- SQLite schema/migrations.
- Save/reopen.
- Edit persistence.
- History.
- Delete.
- Orphan/reconciliation scenarios.

## Phase 7 — Validate Exports

- Excel.
- CSV.
- JSON.
- PDF.
- Complex tables.
- Unicode/encoding scenarios where approved.
- Android file/share behavior.

## Phase 8 — Validate Security and Privacy

- Permission audit.
- Manifest review.
- Sensitive logging audit.
- Path/file handling tests.
- Export/share boundary tests.
- Model integrity tests.
- Release signing verification.

## Phase 9 — Establish Release Baseline

- Supported Android range.
- Device matrix.
- Resource requirements.
- Performance targets.
- AI model/runtime baseline.
- OCR baseline.
- Dependency baseline.
- Release versioning.
- CI/CD decision.

---

# 66. Quality Gates

## Gate 0 — Static Quality

- [ ] Compile/type/static checks pass according to actual project tooling.
- [ ] No unresolved critical build errors.
- [ ] No security-critical warnings accepted without review.

## Gate 1 — Unit / Component

- [ ] Required unit/component suites pass.
- [ ] Core deterministic logic is covered.

## Gate 2 — Integration

- [ ] Module handoffs pass.
- [ ] Persistence mapping passes.
- [ ] Export handoff passes.

## Gate 3 — AI/OCR

- [ ] Approved validation dataset used.
- [ ] OCR validated.
- [ ] AI benchmark executed.
- [ ] No unexplained critical regression.
- [ ] Approved thresholds met once established.

## Gate 4 — Offline

- [ ] Core offline workflow passes.
- [ ] No unexpected network dependency.

## Gate 5 — End-to-End

- [ ] Major P0 workflows pass.
- [ ] Critical acceptance tests pass.

## Gate 6 — Security / Privacy

- [ ] File/path tests pass.
- [ ] No unintended document upload.
- [ ] Sensitive document content absent from routine diagnostics.
- [ ] Export/share boundary passes.

## Gate 7 — Release Candidate

- [ ] Compatibility suite passes.
- [ ] Performance data recorded.
- [ ] Artifact integrity verified.
- [ ] Release signing verified.
- [ ] No release-blocking defects.
- [ ] Approval/sign-off complete.

---

# 67. Release Acceptance Criteria

A SnapData release is acceptable when all of the following are true:

1. The artifact was built from the approved source commit.
2. All required dependencies/tools are identified and reproducibly resolved.
3. The actual build configuration is recorded.
4. AI/OCR resources are available and validated.
5. Core camera/image/PDF input works.
6. OCR works on the approved validation set.
7. Offline AI works after setup.
8. Structured fields/tables are produced in the expected schema.
9. Review/edit/save works.
10. History/reopen/delete works.
11. Excel/CSV/JSON/PDF exports validate.
12. Offline processing passes with network disabled.
13. Security/privacy checks pass.
14. Release signing is correct.
15. Artifact checksum and metadata are recorded.
16. Production smoke test passes.

---

# 68. Release Artifact Naming

The project should use deterministic release artifact naming without exposing secrets.

Conceptual pattern:

```text
SnapData-<version>-<build>-<artifact>.apk
SnapData-<version>-<build>-<artifact>.aab
```

Example only:

```text
SnapData-1.0.0-100-release.apk
```

The exact production naming convention is **TBD**.

---

# 69. Release Directory / Archive

A release archive should contain, as appropriate:

```text
release/
├── artifact.apk or artifact.aab
├── checksums.txt
├── release-notes.md
├── test-summary.md
├── compatibility-summary.md
├── ai-ocr-validation-summary.md
├── security-release-check.md
└── release-metadata.txt
```

Do not place private user documents in release archives.

---

# 70. Build Metadata Manifest

Where practical, create a machine-readable release manifest containing:

```json
{
  "application": "SnapData",
  "versionName": "TBD",
  "versionCode": "TBD",
  "gitCommit": "TBD",
  "buildTimestamp": "TBD",
  "artifact": "TBD",
  "sha256": "TBD",
  "aiModel": "TBD",
  "aiModelVersion": "TBD",
  "ocrEngine": "TBD",
  "ocrVersion": "TBD"
}
```

All values are placeholders until the build system populates them.

---

# 71. Known Current Limitations / Open Decisions

The following are intentionally unresolved in this baseline:

- actual generated Android language;
- actual UI toolkit;
- exact Gradle/AGP/toolchain versions;
- exact Android SDK range;
- exact dependencies and versions;
- exact AI runtime/model;
- exact model packaging strategy;
- exact OCR Android integration;
- exact OCR language list;
- exact preprocessing algorithms;
- exact file storage APIs;
- exact native ABIs;
- exact export libraries;
- exact device/RAM/storage requirements;
- exact performance thresholds;
- exact document/page limits;
- exact interruption/resume semantics;
- exact CI/CD provider;
- exact release signing organization/custody;
- exact initial version/version-code policy.

These are not gaps to hide. They are controlled technical decisions that must be promoted to **CONFIRMED** only after evidence exists.

---

# 72. What Must Be Inspected Before Coding/Release Automation Is Frozen

The following inspection is the immediate technical prerequisite:

```text
1. Generated project root
2. Build files and Gradle wrapper
3. AndroidManifest.xml
4. App module(s)
5. Source language and UI toolkit
6. Dependencies
7. AI runtime/model integration
8. OCR integration/resources
9. Native binaries/ABIs
10. Test framework/configuration
11. Existing signing/build types
12. Existing CI/CD files
```

Once that inspection is completed, this document should be updated with the actual values in Section 5 and the relevant decision register entries.

---

# 73. Source Alignment Notes

## PRD

The PRD establishes SnapData as an Android/mobile document-processing application using OCR and AI, with camera/PDF/image input, structured fields/tables, user review/editing, local storage/history and Excel/CSV/JSON/PDF export. It preserves offline-first behavior after initial AI setup and leaves exact model/device/performance decisions open.

## SRS

The SRS establishes the software workflow and acceptance requirements while deliberately leaving implementation-specific build, API, database, AI runtime, preprocessing and deployment choices to separate technical documents.

## TRD

The TRD confirms Android + Google AI Studio as the implementation target, SQLite as a source-backed local persistence choice, Tesseract as a source-backed OCR context, offline AI capability, and no required backend/REST API for the MVP. It explicitly requires actual generated-project inspection before concrete implementation details are confirmed.

## SYSTEM ARCHITECTURE

The architecture defines the local processing pipeline and component boundaries, including OCR, AI, structured data, persistence, export and history. Build/release architecture is a separate concern implemented here.

## AI/OCR

The AI/OCR specification explicitly states that the available project package does not include the generated Android project/build artifact and therefore the concrete language, UI toolkit, build configuration, model/runtime and OCR integration remain open.

## DATABASE

The database baseline establishes SQLite plus Android-local file storage, with integrity and migration testing required before release.

## EXPORT

The export baseline requires Excel/CSV/JSON/PDF validation, safe file handling and use of the current saved/edited structured result.

## TESTING

The testing baseline establishes release gates across unit/component, integration, AI/OCR, offline, end-to-end, security/privacy, compatibility and release-candidate validation.

## SECURITY / PRIVACY

The security baseline requires local-first processing, minimal sensitive logging, safe file handling, protection of model setup, release signing controls and evidence-backed security claims.

## Original Specification / Workflow Diagram

The original specification defines the core product behavior and offline/local-storage direction. The workflow diagram on page 2 visually shows the sequence from Start and Launch through Document Input, Acquisition, Image Pre-processing, OCR Processing, Offline AI Processing, Structured Data Generation, User Review & Editing, Local Storage, Export Module and Document History. Its technology footer includes React Native, TypeScript, Node.js, Express.js, SQLite, Tesseract OCR, Offline AI and Excel/CSV/JSON/PDF export; these technology labels are preserved as historical source context only where the current technical baseline has not independently confirmed them.

---

# 74. Final Release Engineering Baseline

**SnapData is released as a local-first Android application whose core document workflow must remain functional offline after required AI model setup.**

The release process is therefore centered on:

```text
Verified Source
    ↓
Verified Build Configuration
    ↓
Deterministic Dependencies
    ↓
Tested OCR + Offline AI
    ↓
Tested SQLite + Local Files
    ↓
Tested Excel / CSV / JSON / PDF Export
    ↓
Offline Validation
    ↓
Security / Privacy Validation
    ↓
Signed Release Artifact
    ↓
Production Smoke Test
```

The release engineering baseline does **not** require a backend or REST API for the current MVP.

The most important unresolved prerequisite is the actual Google AI Studio-generated Android project. Its build files, source tree, manifest, dependencies, AI/OCR integrations, native libraries, variants and signing configuration must be inspected before this document can safely promote any concrete toolchain values from **TBD / REQUIRES TECHNICAL VALIDATION** to **CONFIRMED**.

---

# 75. Deployment Channels

Deployment means distributing a validated release artifact to its intended audience. The exact production channel is **TBD**.

Possible channels, subject to project choice, are:

| Channel | Purpose | Status |
|---|---|---|
| Direct APK installation | Development/demo/controlled manual installation | Available as an artifact path; operational process TBD |
| Internal testing | Controlled tester distribution | TBD |
| Closed testing | Limited distribution before wider release | TBD |
| Production Play distribution | Public Google Play distribution | TBD / only after account and release setup |

Generating an APK/AAB is not equivalent to publishing the application.

---

# 76. Production Deployment Procedure

A production deployment should follow:

```text
Approved Release Candidate
        ↓
Final Artifact Verification
        ↓
Release Signing Verification
        ↓
Production Metadata Review
        ↓
Distribution Upload / Installation
        ↓
Post-Install Smoke Test
        ↓
Release Monitoring / Feedback
```

Before production distribution, verify:

- application identity;
- version name/code;
- release signature;
- artifact checksum;
- supported Android/device assumptions;
- AI/OCR resource readiness path;
- offline workflow;
- export formats;
- privacy/security gates;
- release notes.

Exact Google Play Console configuration, store metadata, rollout controls and signing-service configuration are **TBD** until that deployment channel is selected.

---

# 77. Post-Release Verification

Within the approved release process, verify the deployed/distributed artifact using safe test data:

1. Install the released artifact.
2. Confirm launch and basic navigation.
3. Confirm AI model readiness/setup behavior.
4. Process an approved image/PDF.
5. Confirm OCR and offline AI.
6. Confirm review/edit/save.
7. Confirm history/reopen.
8. Confirm Excel/CSV/JSON/PDF export.
9. Confirm behavior with network connectivity disabled after setup.
10. Record any release-blocking or high-severity defect immediately.

Do not use private production documents merely to prove deployment success.

---

# 78. Release Ownership and Sign-Off

The exact project roles are **TBD**, but the release record should identify at least:

| Responsibility | Owner | Status |
|---|---|---|
| Source/release candidate owner | TBD | TBD |
| Build/release owner | TBD | TBD |
| QA/test approval | TBD | TBD |
| Security/privacy approval | TBD | TBD |
| AI/OCR validation approval | TBD | TBD |
| Final production approval | TBD | TBD |

A production release should have explicit approval rather than relying on an implicit “build succeeded” condition.

---

# Appendix A — Quick Release Command Reference

**Not yet confirmed for the generated project.** Use only after Section 5 inspection confirms Gradle and task names.

```bash
# Example debug build
./gradlew assembleDebug

# Example release APK build
./gradlew assembleRelease

# Example Play distribution bundle
./gradlew bundleRelease
```

---

# Appendix B — Release Readiness Snapshot

| Area | Current status |
|---|---|
| Product scope | CONFIRMED |
| Android target | CONFIRMED |
| Offline-first direction | CONFIRMED |
| SQLite requirement | CONFIRMED source-backed |
| Tesseract OCR context | CONFIRMED source-backed; integration TBD |
| Offline AI capability | CONFIRMED; model/runtime TBD |
| Exact generated project stack | REQUIRES TECHNICAL VALIDATION |
| Build toolchain | REQUIRES TECHNICAL VALIDATION |
| Dependency baseline | REQUIRES TECHNICAL VALIDATION |
| Signing setup | REQUIRES TECHNICAL VALIDATION |
| Device matrix | TBD / validation |
| Performance thresholds | TBD / validation |
| CI/CD provider | TBD |
| Production deployment channel | TBD |

---

# Appendix C — Source References

1. `SnapData_PRD_v1.0.md`
2. `SnapData_SRS_v1.0.md`
3. `SnapData_TRD_v1.0.md`
4. `SnapData_SYSTEM_ARCHITECTURE_v1.0.md`
5. `SnapData_FRONTEND_v1.0.md`
6. `SnapData_DATABASE_v1.0.md`
7. `SnapData_AI_OCR_v1.0.md`
8. `SnapData_DOCUMENT_PROCESSING_v1.0.md`
9. `SnapData_DATA_SCHEMA_v1.0.md`
10. `SnapData_EXPORT_v1.0.md`
11. `SnapData_TESTING_v1.0.md`
12. `SnapData_SECURITY_PRIVACY_v1.0.md`
13. `SnapData_UI_UX_v1.0.md`
14. Original SnapData project specification PDF
15. SnapData workflow diagram
16. Supplementary SnapData feature/roadmap material

---

**Document Status:** Draft / Release Engineering Baseline  
**Next mandatory action:** Inspect the actual Google AI Studio-generated Android project and replace all applicable TBD / REQUIRES TECHNICAL VALIDATION entries with evidence-backed values.
