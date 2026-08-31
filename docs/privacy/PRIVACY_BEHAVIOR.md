# SnapData Privacy & Data Governance Specification

## 1. Privacy Philosophy

SnapData is designed from the ground up on the principle of **Data Minimization and On-Device Sovereignty**. Document scanning apps often handle highly confidential personal and corporate documents (e.g. tax statements, legal contracts, pay slips, medical forms, identity cards). 

SnapData enforces strict boundaries ensuring that:
1. Processing defaults to **100% on-device** computation.
2. Network transmission is **never triggered silently**.
3. All cloud capabilities require **explicit user opt-in** and display prominent warnings.

---

## 2. On-Device vs. Cloud Data Flow Matrix

| Feature / Action | Execution Location | Network Requests | Data Stored Externally |
| :--- | :--- | :--- | :--- |
| **Camera Capture & Image Import** | Local Device | None | No |
| **Binarization & Contrast Enhancement** | Local Device CPU/GPU | None | No |
| **Optical Character Recognition (OCR)** | Local ML Kit Engine | None | No |
| **Heuristic Field & Table Extraction** | Local Kotlin Logic | None | No |
| **Document Storage & History** | Local SQLite (Room) | None | No |
| **Export to Excel / CSV / JSON / PDF** | Local Device Storage | None | No |
| **Cloud AI Reasoning (When Opted-In)** | Google Cloud / Proxy Gateway | HTTPS POST | No (Transient inference only) |

---

## 3. User Consent & Transparency Controls

### 3.1 Preprocessing Opt-In Screen
Before document processing begins, the user is presented with the **"Privacy & Processing Engine"** selector:
- **Default State**: `100% On-Device Mode (Strict offline local parsing via ML Kit OCR. Zero network data transfer.)`
- **Opt-In State**: `Cloud AI Enhancement (Transmits image to Gemini / Enterprise Backend for multimodal semantic reasoning.)`

### 3.2 Attribution Badge
Every analyzed document contains an immutable provenance indicator:
- **`On-Device Local OCR`**: Confirms the document was parsed strictly on-device.
- **`Gemini 3.5 Flash Multimodal` / `Enterprise Backend Proxy`**: Indicates cloud assistance was explicitly utilized.

### 3.3 Strict Fallback Guarantee
If the user enables Cloud AI but loses network connectivity, encounters rate limits, or has an unconfigured API key, the app seamlessly falls back to the **On-Device Local Engine** and tags the document with a diagnostic message explaining that privacy-safe local extraction was performed.
