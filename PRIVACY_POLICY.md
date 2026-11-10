# Privacy Policy — Launcher for ASYCUDA (AsyLauncher)

**Effective Date:** November 9, 2026  
**Application Name:** Launcher for ASYCUDA (`AsyLauncher` / `مشغل أسيكودا` / `کارپێکەری ئەسیکۆدا`)  
**Package Name:** `io.github.mohammedalshimmary.asylauncher`  
**Developer:** Mohammed Ammar Hussein (Independent Software Developer — Baghdad, Iraq)  
**Contact & Data Protection Inquiries:** `muhammed.ammar.1997@gmail.com`  
**Public Source & Documentation Repository:** https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android  
**Canonical Privacy Policy URL:** https://mohammedalshimmary.github.io/ASYCUDA-Iraq-Android/PRIVACY_POLICY.html

---

## 1. Purpose, Independent Status Disclaimer & Official Government Information Sources

**Launcher for ASYCUDA** (`AsyLauncher`) is an independent, privately developed compatibility utility for Android phones and tablets. It provides a local X11 display server (`libXlorie.so`) and an embedded 64-bit ARM OpenJDK 8 runtime (`8u504-b01`) paired with a proprietary Java compatibility bridge (`00_boot.jar`) so licensed customs brokers, freight forwarders, and trade operators can run the standard ASYCUDA World desktop client on mobile devices.

### Mandatory Non-Affiliation Notice (Google Play Misleading Claims Policy Compliance)
**Launcher for ASYCUDA (`AsyLauncher`) is an independent third-party technical utility. It is NOT affiliated with, sponsored by, authorized by, or endorsed by the Iraqi Ministry of Finance (`وزارة المالية العراقية`), the General Commission of Customs (`الهيئة العامة للكمارك`), the United Nations Conference on Trade and Development (`UNCTAD`), or any government entity.**

### Official Government & UNCTAD Information Sources
No government `.jar` packages are pre-bundled inside this application. All official ASYCUDA World client packages and JNLP configuration manifests executed by this compatibility launcher are retrieved directly by the user's device from the following official, publicly accessible government and UNCTAD portals:
- **Iraqi Ministry of Finance — Official ASYCUDA World Portal:** https://asy.mof.gov.iq/aw/
- **Official ASYCUDA World JNLP Client Manifest:** https://asy.mof.gov.iq/aw/AWIQWorld.jnlp
- **Iraqi Ministry of Finance Official Website:** https://www.mof.gov.iq/
- **UNCTAD ASYCUDA Official Programme Website:** https://asycuda.org/en/

---

## 2. Information We Do NOT Collect, Access, or Share

**Launcher for ASYCUDA** is engineered around a strict **Zero-Personal-Data (Zero-PII)** design. We do **not** collect, store, log, transmit, or share any of the following:

1. **No Personal Identity or Contact Data:** We never collect personal names, company names, email addresses, physical addresses, national ID numbers, or phone numbers (the `user_phone` field in our diagnostic schema is strictly enforced as an empty string `""` both in client code and by server-side database rules).
2. **No Customs Credentials or Session Secrets:** We never read, log, intercept, or transmit ASYCUDA World usernames, passwords, two-factor codes, session cookies (`JSESSIONID`), Tax Identification Numbers (`TIN`), declarant codes, or authentication tokens.
3. **No Customs Declarations, Trade Documents, or Clipboard Data:** All customs declarations, assessment notices, PDFs, Excel (`.xlsx`) exports, and uploaded attachments remain strictly inside the app's private on-device storage (`/data/data/io.github.mohammedalshimmary.asylauncher/files/prints`) or pass directly between the user's device and `asy.mof.gov.iq`. Clipboard synchronization is strictly one-way (`Android -> ASYCUDA` paste only); the app never reads or logs the Android clipboard in the background.
4. **No Persistent Hardware or Advertising Identifiers:** We never read `Settings.Secure.ANDROID_ID` (SSAID), IMEI, MEID, SIM serial numbers, Wi-Fi MAC addresses, or Google Advertising ID (`AD_ID`).
5. **No Location, Sensor, Camera, Microphone, or Contacts Access:** The app does not request or access GPS/network location, camera, microphone, contacts, call logs, SMS, or external device storage.

All communication with the ASYCUDA World portal (`asy.mof.gov.iq`) occurs **directly** between the user's Android device and the official Ministry of Finance server over encrypted HTTPS/TLS. Zero traffic from the customs session passes through any developer-operated server or proxy.

---

## 3. Limited Technical Data Processed & Google Play Data Safety Alignment

To deliver release announcements (`announcements/latest`), update the remote Known-Errors diagnostic catalog (`app_config/known_errors_catalog`), record aggregate version compatibility counts, and allow users to voluntarily submit error reports when troubleshooting, official signed builds communicate over HTTPS (`TLS 1.2+`) with **Google Cloud Firestore** (`firestore.googleapis.com`).

Below is the complete, exhaustive disclosure of every data field processed by those functions:

### A. Anonymous Daily Installation Heartbeat (`app_metrics` Collection)
Once per UTC calendar day—and only after the user accepts the first-launch Terms & Conditions—the application sends a single non-personal heartbeat document (`app_metrics/{anon_id}`) containing **exclusively these 6 technical fields**:

| Field Name | Example Value | Purpose | Linked to User Identity? |
| :--- | :--- | :--- | :--- |
| `anon_id` | `inst_9f8e7d6c-5b4a-...` | Randomly generated installation UUID (`UUID.randomUUID()`). Resets automatically whenever app data is cleared or the app is uninstalled. | **No** |
| `app_version` | `4.4.0 (56)` | Tracks which app version is active so obsolete versions can be identified. | **No** |
| `android_sdk` | `35` or `36` | Verifies compatibility across Android OS API levels (Android 8.0–16+). | **No** |
| `device_model` | `Samsung SM-S928B` | Identifies device screen/GPU class for X11 rendering compatibility. | **No** |
| `ui_lang` | `ar`, `en`, or `ku` | Indicates active interface language preference for translation prioritization. | **No** |
| `last_active_date` | `2026-11-09` | Current UTC calendar date (`YYYY-MM-DD`). | **No** |

### B. Voluntary User-Initiated Diagnostic & Bug Reports (`diagnostic_reports` Collection)
- **Strictly Opt-In (Never Automatic):** Diagnostic reports are **never** transmitted automatically on crash or error. A report is sent **only** if the user explicitly taps **"Send Report"** inside the Universal Network & Error Diagnostics window or the Control Center *"Report an Issue"* dialog.
- **Full User Transparency ("Preview Diagnostic Data"):** Before tapping **"Send Report"**, the user can tap **"Preview Diagnostic Data"** (`showDiagnosticDataPreviewModal`) to inspect the exact, complete redacted payload in plain text.
- **100% In-RAM Assembly & Immediate Memory Zeroing (`'\0'`):** Diagnostic logs are assembled exclusively in volatile RAM (`sRedactedRamDiagRingBuffer` and `StringBuilder` preview buffers). No diagnostic `.zip` or report file is ever written to device storage, logs are never copied to the Android clipboard, and every character of the in-RAM `StringBuilder` buffer is overwritten with null bytes (`'\0'`) immediately when the dialog closes or the upload finishes.
- **Exact Technical Data Included in a Voluntary Report:**
  1. **Device & Runtime State:** App version (`app_version`), package name, UI language (`ui_lang`), device manufacturer/model (`device`), Android OS version & SDK level (`android_sdk`), whether the local JVM process is currently running (`JVM Process Alive`), free internal app sandbox storage in MB, and boolean terms-acceptance state.
  2. **Optional User Note (`user_note`):** The issue description voluntarily typed by the user (passed through the credential redaction filter and capped at 1,000 characters).
  3. **Installed JAR Count:** The numeric count of `.jar` files in `files/client/jars/` (count only — never user documents or file names from the Vault).
  4. **Redacted Session Bootstrap Log Tail (`asycuda.log`):** Up to the last 3,000 characters of the local launcher bootstrap log after client-side redaction.
  5. **Redacted Java Client Log Tail (`client.log`):** Up to the last 2,000 characters of the local Java Swing/JNLP log after client-side redaction.
  6. **Redacted Network & Exception Trace (`summary` / `error_code`):** Active network transport category (`Wi-Fi`, `Cellular`, `VPN`), DNS / `/etc/hosts` resolution status, diagnostic error classification code (e.g., `ASY-NET-TIMEOUT`), and up to 8 stack trace frames of the caught exception (redacted and capped at 1,800 characters).
- **Mandatory Client-Side Credential & PII Scrubber (`AsycudaSecurityValidator.redactSensitiveLogContent`):** Before any text is shown in the preview modal or transmitted over HTTPS, a deterministic multi-pattern regex scrubber strips and replaces with `[REDACTED]` any URL credentials (`user:pass@host`), HTTP `Authorization` / `Bearer` / `Basic` headers, XML/SOAP auth tags (`<login>`, `<user>`, `<username>`, `<password>`, `<tin>`, `<declarant>`, `<token>`, `<jsessionid>`), `.SOClassCache` username paths, `Tin code received` lines, quoted JSON and unquoted key-value pairs for usernames/passwords/TINs/cookies/tokens, internal loopback IPC tokens, and phone numbers.

---

## 4. Third-Party Service Providers & External Endpoints

The application does **not** include any advertising SDKs, analytics trackers, or third-party crash reporting SDKs (all legacy crash SDKs such as Sentry have been completely removed). The app communicates only with:

1. **Official Iraqi Customs Portal (`asy.mof.gov.iq`):** Direct HTTPS (`:443`) and SOClass/RMI TLS connections initiated by the user to download official `AWIQWorld.jnlp` packages and conduct their ASYCUDA World session.
2. **Google Cloud Firestore (`firestore.googleapis.com` — Google LLC):** Used by official signed builds to read public announcements (`announcements/latest`), read the Known-Errors diagnostic catalog (`app_config/known_errors_catalog`), record the 6-field daily anonymous heartbeat (`app_metrics`), and receive voluntary user-submitted bug reports (`diagnostic_reports`). Governed by the [Google Privacy Policy](https://policies.google.com/privacy).
3. **SonarCloud (`sonarcloud.io`) & Snyk (`snyk.io`):** Read-only `GET` requests made only when the user opens the in-app **About, Security & Software Licences** dialog to display live code-quality badge statuses. Zero device or user data is sent.

---

## 5. Android Permissions Used

**Launcher for ASYCUDA** requests only 7 minimal operational permissions in `AndroidManifest.xml` and requests **zero** broad storage (`MANAGE_EXTERNAL_STORAGE`, `READ/WRITE_EXTERNAL_STORAGE`), accessibility (`BIND_ACCESSIBILITY_SERVICE`), or package-installer (`REQUEST_INSTALL_PACKAGES`) permissions:

| Android Permission | Protection Level | Why It Is Required |
| :--- | :--- | :--- |
| `android.permission.INTERNET` | Normal | Connects directly to `asy.mof.gov.iq`, runs connection diagnostics, and communicates with Google Cloud Firestore over HTTPS. |
| `android.permission.ACCESS_NETWORK_STATE` | Normal | Detects active network transport changes (Wi-Fi / Cellular / VPN) during package synchronization and network diagnostics. |
| `android.permission.FOREGROUND_SERVICE` | Normal | Keeps the embedded X11 display server and Java 8 client alive when the user briefly switches to another app (e.g., viewing a printed PDF or checking a 2FA code). |
| `android.permission.FOREGROUND_SERVICE_SPECIAL_USE` | Normal (Android 14–16) | Required by Android 14–16 (`targetSdkVersion 36`) for persistent local desktop/X11 sessions. |
| `android.permission.WAKE_LOCK` | Normal | Prevents the CPU from sleeping mid-download while syncing the 131 official JAR packages or submitting a customs declaration. |
| `android.permission.POST_NOTIFICATIONS` | Runtime (Optional) | Shows the foreground X11 session status notification (which provides a quick **Exit** button). |
| `io.github.mohammedalshimmary.asylauncher.PERMISSION_INTERNAL_BROADCAST` | `signature` (App-Private) | Protects internal X11 surface state broadcasts between `CmdEntryPoint` and `MainActivity` so no external app can intercept or spoof display intents. |

---

## 6. Local Data Storage, Security Controls, Retention & Deletion Rights

1. **On-Device Storage Isolation (`0700` Sandbox & OS File-Based Encryption):**
   - All printed PDFs, exported `.xlsx` spreadsheets, and files imported via the Android system file picker (`Intent.ACTION_OPEN_DOCUMENT`) are stored exclusively inside the app's private internal directory (`/data/data/io.github.mohammedalshimmary.asylauncher/files/prints`), protected by Android's kernel-enforced per-app UID sandbox (`0700`) and operating-system File-Based Encryption (FBE).
   - Acceptance of the first-launch Click-Wrap Terms & Conditions (governed by Iraqi Civil Code No. 40 of 1951 Art. 259/2 and Iraqi Electronic Transactions Law No. 78 of 2012 Art. 18/1) is stored purely as a local boolean flag (`accepted = true`) on the user's device; no consent receipt is ever uploaded.
2. **Encryption in Transit & Backend Write-Only Isolation (`firestore.rules`):**
   - All network communication uses encrypted HTTPS (`TLS 1.2+`).
   - Our Google Cloud Firestore Security Rules (`firestore.rules`) enforce strict **write-only isolation** on `diagnostic_reports` and `app_metrics` (`allow read, update, delete: if false` for all clients), validate schema field counts and string length bounds, and reject any document if `user_phone` is non-empty.
3. **How Users Can Delete Their Data at Any Time:**
   - **On-Device Data & Installation ID (`anon_id`):** Users can delete individual printed PDFs or exported spreadsheets at any time using the in-app **Vault — File Manager** (`Delete` action), or wipe all local files, cached JARs, and the random `anon_id` by clearing the app's storage in Android Settings or uninstalling **Launcher for ASYCUDA**.
   - **Voluntary Diagnostic Reports:** Because voluntary diagnostic reports contain no personal identifiers or account emails, users who wish to request deletion of a specific voluntary diagnostic report from Cloud Firestore may email `muhammed.ammar.1997@gmail.com` with the approximate UTC timestamp and device model of their report, and it will be permanently deleted within 7 business days.

---

## 7. Children's Privacy (`18+` Enterprise & Logistics Audience)

**Launcher for ASYCUDA** is a specialized B2B customs clearance and trade compatibility utility intended exclusively for licensed adult professionals (**18 years of age and older**). It is not directed at children under the age of 13 (or under 18), and we do not knowingly collect any information from children.

---

## 8. Changes to This Privacy Policy & Contact Information

Any updates to this Privacy Policy will be published in [`PRIVACY_POLICY.md`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/blob/main/PRIVACY_POLICY.md) and at https://mohammedalshimmary.github.io/ASYCUDA-Iraq-Android/PRIVACY_POLICY.html with an updated Effective Date.

If you have any questions about this Privacy Policy, our data handling practices, or your deletion rights, please contact:
- **Developer:** Mohammed Ammar Hussein
- **Email:** `muhammed.ammar.1997@gmail.com`
- **GitHub Issues:** https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/issues
