# SnapData Security & Privacy Architecture

## 1. Executive Summary

SnapData is an enterprise-grade document extraction and digitization application engineered with an **offline-first, privacy-by-default architecture**. 

Sensitive documents (invoices, receipts, tax forms, bank statements, identification records) processed by the application are handled exclusively on-device by default. Cloud AI capabilities (powered by Google Gemini 3.5 Flash) operate strictly as an **opt-in enhancement** with rigorous secret isolation and fallback mechanisms.

---

## 2. Threat Model & Key Attack Surfaces

### 2.1 Direct Client-Side API Key Exposure (APK Decompilation)
- **Vulnerability**: Embedding API keys directly in client applications or source repositories allows attackers to decompile the APK, extract credentials, and exhaust API quotas or impersonate the application.
- **SnapData Safeguards**:
  1. **Secrets Panel & `.env` Isolation**: API keys are managed exclusively via the AI Studio Secrets panel and local `.env` files. The `.env` file is strictly excluded from version control (`.gitignore`).
  2. **Enterprise Backend Proxy Support**: SnapData natively supports `GEMINI_BACKEND_URL`, allowing enterprises to proxy all AI requests through a secure server-side gateway (e.g., Firebase Cloud Functions, API Gateway) where tokens and credentials are added server-side with zero client-side secret exposure.
  3. **No Hardcoded Fallbacks**: Default builds fail-safe to 100% on-device OCR if credentials are missing or unconfigured.

### 2.2 Unintentional Document Data Exfiltration
- **Vulnerability**: Document scanner applications sending user scans to cloud endpoints without explicit consent or user awareness.
- **SnapData Safeguards**:
  1. **Offline Mode by Default**: `ProcessingOptions.forceOfflineAi = true` and `enableCloudAi = false` are the default state.
  2. **Transparent UI Disclosure**: The UI clearly displays an "On-Device Mode" badge (Green lock) when offline and requires a conscious user toggle for "Cloud AI Enhancement" with an explicit transmission disclosure.
  3. **Visual Processing Attribution**: The review editor and history views explicitly display whether the document was processed via **On-Device Local OCR** or **Gemini Cloud AI**.

---

## 3. Defense-in-Depth Architecture

```
+-------------------------------------------------------------------------+
|                        SnapData Processing Pipeline                     |
+-------------------------------------------------------------------------+
                                    |
                                    v
                  +-----------------------------------+
                  |  Image Preprocessing & Crop / Rot |
                  +-----------------------------------+
                                    |
                                    v
                     +-----------------------------+
                     | Cloud AI Explicitly Opted-In?
                     +-----------------------------+
                            /               \
                  YES (Opt-in)             NO (Default / Offline)
                         /                     \
                        v                       v
         +-----------------------------+   +-----------------------------+
         | GeminiAiService Dispatcher   |   | ML Kit On-Device Text Engine|
         +-----------------------------+   +-----------------------------+
           |                         |                  |
   Backend Proxy Set?          Direct API Key?          |
           |                         |                  |
           v                         v                  |
   [Enterprise Gateway]     [Gemini 3.5 Flash]          |
           \                         /                  |
            +-----------+-----------+                   |
                        |                               |
                 Success or Error?                      |
                  /           \                         |
            [Success]     [Any Failure]                 |
                |               \                       |
                |          Automatic Fallback --------> |
                |                                       |
                v                                       v
         [Parsed Output]                    [OcrEngine Rule Parser]
                \                                       /
                 +------------------+------------------+
                                    |
                                    v
                     +-----------------------------+
                     | Room SQLite App-Private DB  |
                     +-----------------------------+
```

---

## 4. Error Handling & Recovery Matrix

| Scenario | Engine Reaction | Error Classification | Data Privacy Guarantee |
| :--- | :--- | :--- | :--- |
| **No API Key Configured** | Falls back to on-device OCR | `ApiKeyMissing` | 100% On-Device, zero network egress |
| **Invalid / Revoked API Key** | Falls back to on-device OCR | `InvalidApiKey` (HTTP 401/403) | 100% On-Device, zero network egress |
| **Airplane Mode / No Internet** | Falls back to on-device OCR | `NetworkUnavailable` | 100% On-Device, zero network egress |
| **User Forces Offline Mode** | Uses on-device OCR directly | `OfflineModeForced` | 100% On-Device, zero network egress |
| **Cloud Service Timeout (60s)** | Falls back to on-device OCR | `Timeout` | 100% On-Device recovery |
| **Cloud Rate Limit / 5xx Error** | Falls back to on-device OCR | `ApiError` (HTTP 429/500) | 100% On-Device recovery |
| **Malformed Response Payload** | Falls back to on-device OCR | `MalformedResponse` | 100% On-Device recovery |

---

## 5. Storage & Backup Security

1. **App-Private SQLite Storage**: All documents, metadata, extracted fields, and matrices are stored in internal app-private database storage (`/data/data/com.aistudio.snapdata.../databases/`).
2. **Android 12+ Data Extraction Rules**: `app/src/main/res/xml/data_extraction_rules.xml` explicitly disallows cloud backups and device-to-device transfers of raw temporary scan images and database files.
3. **Cache Hygiene**: Preprocessed bitmap caches are stored in `context.cacheDir` with automatic cleanup upon application termination or manual cache clear in Settings.
