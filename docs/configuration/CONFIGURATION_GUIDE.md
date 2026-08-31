# SnapData Configuration Guide

This guide details how to configure SnapData for local development, production releases, and enterprise cloud deployments.

---

## 1. Secret & Key Configuration

SnapData uses the **Secrets Gradle Plugin** to securely inject credentials at build time without hardcoding them in source files.

### 1.1 Managing Secrets via AI Studio Secrets Panel
When developing within Google AI Studio:
1. Open the **Secrets** panel in the AI Studio environment.
2. Add your secret key named `GEMINI_API_KEY`.
3. The platform automatically exposes this secret to the build environment via `.env`.

### 1.2 Managing Secrets via `.env` (Local Development)
For local terminal builds:
1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Populate `.env` with your development credentials:
   ```properties
   # Optional direct API key for Gemini multimodal extraction
   GEMINI_API_KEY=AIzaSy...YourKeyHere

   # Optional enterprise proxy endpoint (recommended for production)
   GEMINI_BACKEND_URL=https://api.yourcompany.com/v1/extract-document
   ```
3. **CRITICAL**: Never commit `.env` to Git. It is listed in `.gitignore`.

---

## 2. Architecture Modes

SnapData supports two distinct operational modes:

### Mode A: 100% On-Device Offline Engine (Default)
- **Configuration**: No keys or backend URL required.
- **Dependencies**: Uses Google ML Kit Text Recognition on-device models + built-in heuristic rule-based schema parser.
- **Behavior**: Operates entirely offline (e.g. in Airplane Mode). Zero data leaves the physical device.

### Mode B: Enterprise Backend Proxy (Recommended for Production)
- **Configuration**: Set `GEMINI_BACKEND_URL` in `.env` or CI/CD environment.
- **Behavior**: Rather than embedding a Google Cloud API key inside the distributed APK (which is vulnerable to decompilation), the mobile app makes an authenticated HTTPS request to your enterprise backend gateway.
- **Backend Responsibility**: The enterprise backend validates the user session, injects the server-side Gemini API key, and relays the structured JSON response back to the client.

### Mode C: Direct Cloud API (Development Only)
- **Configuration**: Set `GEMINI_API_KEY` in `.env`.
- **Behavior**: Calls `generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent`.

---

## 3. Release Signing Configuration

SnapData supports safe release signing via standard Gradle properties or CI/CD environment variables.

### Environment Variables for Release Builds
```bash
export RELEASE_STORE_FILE="/path/to/keystore.jks"
export RELEASE_STORE_PASSWORD="keystore_password"
export RELEASE_KEY_ALIAS="snapdata_key"
export RELEASE_KEY_PASSWORD="key_password"
```

If these environment variables are not provided, local release builds safely fallback to the debug signing configuration without failing compilation.
