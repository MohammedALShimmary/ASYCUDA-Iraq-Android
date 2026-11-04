# Privacy Policy — Launcher for ASYCUDA (AsyLauncher)

**Effective Date:** November 4, 2026  
**Application Name:** Launcher for ASYCUDA (`AsyLauncher`)  
**Package Name:** `io.github.mohammedalshimmary.asylauncher`  
**Developer:** Mohammed Ammar Hussein (Independent Open-Source Developer)  
**Contact Email:** `mohammedalshimmary@users.noreply.github.com`  
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

All sessions between the user's device and their configured ASYCUDA server take place **directly** between the user's device and that server over encrypted HTTPS/TLS. Nothing from the user's customs session passes through any developer-operated proxy.

---

## 3. Limited Anonymous Telemetry & Voluntary Diagnostic Reports

To maintain application stability and provide bilingual (Arabic/English/Kurdish) self-healing error guidance, the app uses Google Cloud Firestore (`gen-lang-client-0583678881`) for two strictly limited functions:

### A. Anonymous Daily Installation Heartbeat (`app_metrics`)
- **What is sent:** A randomly generated, installation-scoped UUID (`anon_id`), application version (`4.3.0`), Android API level (`android_sdk`), device manufacturer/model (`device_model`), UI language code (`ui_lang`), and UTC calendar date (`last_active_date`).
- **Purpose:** Aggregate active user and Android version compatibility statistics.
- **Zero-PII Guarantee:** The `anon_id` cannot be linked to any person or hardware serial and is reset whenever the app data is cleared or the app is reinstalled.

### B. Voluntary Diagnostic Error Reports (`diagnostic_reports`)
- **When it is sent:** **Strictly opt-in.** Diagnostic reports are only transmitted when the user explicitly taps **"Send Diagnostic Report"** inside the app.
- **What is sent:** Diagnostic error code (`error_code`), error title (`title`), automatically scrubbed diagnostic summary (`summary`), optional user-typed note (`user_note`), network transport type (`Wi-Fi` / `Cellular`), app version, and device model.
- **Automatic Client-Side Redaction:** Before any diagnostic summary is shown to the user or transmitted, our in-RAM scrubber automatically strips IPv4/IPv6 addresses, URLs, file paths, usernames, passwords, tokens, and phone numbers.

---

## 4. Camera & Local Document Processing

If you use the optional built-in **Document Scanner / Image Optimizer** tool to prepare customs attachments:
- The `android.permission.CAMERA` permission is requested at runtime only when you open the scanner.
- All image cropping, perspective correction, grayscale filtering, and PDF/JPEG compression occur **100% locally on your device**.
- Scanned documents are never uploaded to the developer or any third-party cloud service.

---

## 5. Data Security, Retention & Deletion

- **Encryption in Transit:** All communication with Google Cloud Firestore uses HTTPS/TLS 1.2+.
- **Write-Only Isolation:** Cloud Firestore Security Rules enforce strict write-only isolation on `diagnostic_reports` and `app_metrics` so no client can read another client's reports.
- **Data Deletion:** Because no account or personal identifier is collected, clearing the application storage or uninstalling **Launcher for ASYCUDA** immediately destroys the local random installation ID. Users who wish to request deletion of a specific voluntary diagnostic report may contact `mohammedalshimmary@users.noreply.github.com` with the timestamp of their report.

---

## 6. Children's Privacy

**Launcher for ASYCUDA** is a professional enterprise/logistics utility intended exclusively for adults (**18 years and older**) and is not directed at children under the age of 13.

---

## 7. Changes to This Privacy Policy

Any updates to this Privacy Policy will be published in this file in our public GitHub repository with an updated Effective Date.
