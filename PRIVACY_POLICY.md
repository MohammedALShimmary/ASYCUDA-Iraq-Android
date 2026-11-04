# Privacy Policy — Launcher for ASYCUDA (AsyLauncher)

**Effective Date:** November 4, 2026  
**Application Name:** Launcher for ASYCUDA (`AsyLauncher`)  
**Package Name:** `io.github.mohammedalshimmary.asylauncher`  
**Developer:** Mohammed Ammar Hussein (Independent Open-Source Developer)  
**Contact Email:** `muhammed.ammar.1997@gmail.com`  
**Repository:** https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android

---

## 1. Overview & Independent Status Disclaimer

**Launcher for ASYCUDA** (`AsyLauncher`) is an independent, open-source X11 and Java Swing compatibility runtime for Android devices. It is **not** affiliated with, endorsed by, or an official application of the United Nations Conference on Trade and Development (UNCTAD), the Iraqi Ministry of Finance, the General Commission of Customs, or any government entity.

We are committed to protecting user privacy through a strict **Zero-PII (Zero Personally Identifiable Information)** architecture.

---

## 2. Information We Do NOT Collect

**Launcher for ASYCUDA** does **not** collect, store, transmit, or share any of the following:
- Personal names, email addresses, phone numbers (`user_phone` is strictly enforced as empty `""`), or national ID numbers.
- Customs login credentials, usernames, passwords, session cookies (`JSESSIONID`), Tax Identification Numbers (TIN), declarant codes, or cryptographic tokens.
- Customs declarations, trade documents, financial records, or clipboard contents.
- Persistent hardware identifiers (no IMEI, no MAC address, no SIM serial, no Android SSAID, and no Advertising ID).
- Precise or coarse GPS location data.
- Camera, microphone, contacts, or external device storage files.

All sessions between the user's device and the ASYCUDA World portal take place **directly** between the user's device and the target server over encrypted HTTPS/TLS. Nothing from the user's customs session passes through any developer-operated proxy.

---

## 3. Limited Anonymous Telemetry & Voluntary In-RAM Diagnostic Reports

To maintain application stability, provide live announcements (`announcements/latest`), hot-reload the Known-Errors Catalog (`app_config/known_errors_catalog`), and diagnose runtime/network errors in Arabic, English, and Kurdish, the app communicates with a secure Google Cloud Firestore backend for two strictly limited functions:

### A. Anonymous Daily Installation Heartbeat (`app_metrics`)
- **What is sent (once per UTC calendar day):**
  1. `anon_id`: A randomly generated, installation-scoped UUID (`inst_<uuid>`) that resets whenever app data is cleared or the app is reinstalled.
  2. `app_version`: Application version name and build code (e.g., `4.3.0 (52)`).
  3. `android_sdk`: Android OS API level (e.g., `34` or `35`).
  4. `device_model`: Device manufacturer and model name.
  5. `ui_lang`: Active interface language code (`ar`, `en`, or `ku`).
  6. `last_active_date`: Current UTC date (`YYYY-MM-DD`).
- **Purpose:** Aggregate active user counts and Android device/version compatibility statistics.
- **Zero-PII Guarantee:** The `anon_id` cannot be linked to any person, account, or hardware serial.

### B. Voluntary Diagnostic & Bug Reports (`diagnostic_reports` — 100% In-RAM Assembly, Redaction & Zeroing)
- **Strictly Opt-In with Full User Preview:** Diagnostic reports are **never** sent automatically. They are only transmitted when the user explicitly taps **"Send Report"** inside the Diagnostic or Feedback modal. Before sending, the user can tap **"Preview Diagnostic Data"** to inspect the exact redacted text that will be transmitted.
- **100% In-RAM Processing & Memory Zeroing (`\0`):** Diagnostic data is assembled exclusively in a volatile in-RAM buffer (`StringBuilder`). Zero diagnostic archive files are written to disk, and all in-RAM diagnostic buffers (`sRedactedRamDiagRingBuffer` and preview buffers) are explicitly overwritten character-by-character with null bytes (`'\0'`) immediately upon dialog dismissal or upload completion.
- **Exact Technical Data Included in a Voluntary Report:**
  1. **Device & Runtime Metadata:** App version, package name, active UI language (`ar`/`en`/`ku`), device manufacturer and model, Android version and SDK level, whether the local JVM child process is currently running (`JVM Process Alive`), available free internal app storage (`MB`), and terms-acceptance boolean flag.
  2. **User Issue Description (`user_note`):** The optional description typed by the user (redacted and truncated to 1,000 characters).
  3. **Installed Package Count:** The total numeric count of JAR files in the local `client/jars` directory (count only — no user files or customs documents).
  4. **Redacted Session Launch Log Tail (`asycuda.log`):** Up to the last 3,000 characters of the local bootstrap/session log after passing through our security redaction filter.
  5. **Redacted Java Client Log Tail (`client.log`):** Up to the last 2,000 characters of the local Java Swing/JNLP client log (if present) after passing through our security redaction filter.
  6. **Redacted In-RAM Universal Network & Exception Trace:** Active network transport type (`Wi-Fi`, `Cellular`, `VPN`), DNS / `/etc/hosts` cache status, error code classification (`error_code`), and up to 8 stack trace frames of any caught Java/TLS/network exception (redacted and truncated to 1,800 characters in the transmitted `summary` field).
- **Mandatory Client-Side Security Redaction (`AsycudaSecurityValidator.redactSensitiveLogContent`):** Before any log tail or exception trace is displayed in the preview modal or transmitted over the network, our multi-pattern scrubber automatically strips and replaces with `[REDACTED]` any usernames, passwords, login credentials, session IDs, cookies, HTTP `Authorization`/`Bearer` headers, TIN/declarant identifiers, XML authentication tags, internal IPC tokens, and phone numbers.

---

## 4. Android Permissions Used

**Launcher for ASYCUDA** requests only the minimum Android permissions required to run the local X11 and Java runtime session:
- `INTERNET` & `ACCESS_NETWORK_STATE`: Required to connect directly to the ASYCUDA server, run network connectivity diagnostics, and communicate with Cloud Firestore over HTTPS.
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, & `WAKE_LOCK`: Required to keep the local X11/JVM desktop session alive when the user briefly switches apps.
- `POST_NOTIFICATIONS`: Used for the active session status notification and optional release/announcement alerts.
- `io.github.mohammedalshimmary.asylauncher.PERMISSION_INTERNAL_BROADCAST` (`signature` protection level): Internal app-only broadcast protection.

The application requests **zero** camera, microphone, location, contacts, external storage, or package-installer permissions.

---

## 5. Data Security, Retention & Deletion

- **Encryption in Transit:** All communication uses HTTPS/TLS 1.2+.
- **Write-Only Isolation & Schema Enforcement:** Backend Cloud Firestore Security Rules enforce strict write-only isolation on `diagnostic_reports` and `app_metrics` (no client can ever read, list, or query another client's reports) and reject any payload if `user_phone` is non-empty.
- **Data Deletion:** Because no account or personal identifier is collected, clearing the application storage or uninstalling **Launcher for ASYCUDA** immediately destroys the local random installation ID (`anon_id`). Users who wish to request deletion of a specific voluntary diagnostic report may contact `muhammed.ammar.1997@gmail.com` with the approximate timestamp or device model of their report.

---

## 6. Children's Privacy

**Launcher for ASYCUDA** is a professional enterprise/logistics utility intended exclusively for adults (**18 years and older**) and is not directed at children under the age of 13.

---

## 7. Changes to This Privacy Policy

Any updates to this Privacy Policy will be published in this file in our public GitHub repository with an updated Effective Date.
