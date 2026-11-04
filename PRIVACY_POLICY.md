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
- Personal names, email addresses, phone numbers, or national ID numbers.
- Customs login credentials, usernames, passwords, or cryptographic tokens.
- Customs declarations, trade documents, financial records, or clipboard contents.
- Persistent hardware identifiers (no IMEI, no MAC address, no SIM serial, no Android SSAID, and no Advertising ID).
- Precise or coarse GPS location data.
- Camera, microphone, contacts, or external device storage files.

All sessions between the user's device and the ASYCUDA World portal take place **directly** between the user's device and the target server over encrypted HTTPS/TLS. Nothing from the user's session passes through any developer-operated proxy.

---

## 3. Limited Anonymous Telemetry & Voluntary Diagnostic Reports

To maintain application stability and provide trilingual (Arabic, English, and Kurdish) self-healing error guidance, the app communicates with a secure Google Cloud Firestore backend for two strictly limited functions:

### A. Anonymous Daily Installation Heartbeat (`app_metrics`)
- **What is sent:** A randomly generated, installation-scoped UUID (`anon_id`), application version (`4.3.0`), Android API level (`android_sdk`), device manufacturer/model (`device_model`), UI language code (`ui_lang`), and UTC calendar date (`last_active_date`).
- **Purpose:** Aggregate active user and Android version compatibility statistics.
- **Zero-PII Guarantee:** The `anon_id` cannot be linked to any person or hardware serial and is reset whenever the app data is cleared or the app is reinstalled.

### B. Voluntary Diagnostic Error Reports (`diagnostic_reports`)
- **When it is sent:** **Strictly opt-in.** Diagnostic reports are only transmitted when the user explicitly taps **"Send Diagnostic Report"** inside the app.
- **What is sent:** Diagnostic error code (`error_code`), error title (`title`), automatically scrubbed diagnostic summary (`summary`), optional user-typed note (`user_note`), network transport type (`Wi-Fi` / `Cellular`), app version, and device model.
- **Automatic Client-Side Redaction:** Before any diagnostic summary is shown to the user or transmitted, our in-RAM scrubber automatically strips IPv4/IPv6 addresses, URLs, file paths, usernames, passwords, tokens, and phone numbers.

---

## 4. Android Permissions Used

**Launcher for ASYCUDA** requests only the minimum Android permissions required to run the local X11 and Java runtime session:
- `INTERNET` & `ACCESS_NETWORK_STATE`: Required to connect directly to the ASYCUDA server and perform network transport diagnostics.
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, & `WAKE_LOCK`: Required to keep the local X11/JVM session alive when the user briefly switches apps.
- `POST_NOTIFICATIONS`: Used for the active session status notification and optional release/announcement alerts.

The application requests **zero** camera, microphone, location, contacts, storage, or package-installer permissions.

---

## 5. Data Security, Retention & Deletion

- **Encryption in Transit:** All communication uses HTTPS/TLS 1.2+.
- **Write-Only Isolation:** Backend security rules enforce strict write-only isolation on `diagnostic_reports` and `app_metrics` so no client can read another client's reports.
- **Data Deletion:** Because no account or personal identifier is collected, clearing the application storage or uninstalling **Launcher for ASYCUDA** immediately destroys the local random installation ID. Users who wish to request deletion of a specific voluntary diagnostic report may contact `muhammed.ammar.1997@gmail.com` with the approximate timestamp of their report.

---

## 6. Children's Privacy

**Launcher for ASYCUDA** is a professional enterprise/logistics utility intended exclusively for adults (**18 years and older**) and is not directed at children under the age of 13.

---

## 7. Changes to This Privacy Policy

Any updates to this Privacy Policy will be published in this file in our public GitHub repository with an updated Effective Date.
