# Security Policy & Architecture Transparency

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)

Because **ASYCUDA World — Republic of Iraq (Android)** (`iq.asycuda`) is an independent, open-source desktop runtime wrapper used by licensed customs brokers and logistics operators, security and source-code transparency are treated as first-order engineering requirements.

---

## Supported Versions & Published Release Verification (`v4.0.0`)

All users should run **[v4.0.0](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.0.0)** (`versionCode 41`). Earlier releases (`v3.9.5`, `v3.9.0`, `v3.8.0`, `v3.7.0`, and `v3.6.0`) have been marked as superseded on GitHub Releases following the `v4.0.0` Zero Pre-Bundled Government JARs, Android 14 (`targetSdkVersion 34`) & Verified Developer Certificate, Hardware Keystore `AES-256-GCM` Consent Receipt, and JVM Host Storage Isolation upgrade.

| Version | Supported | Security Status | Direct Release Link |
| :--- | :--- | :--- | :--- |
| **v4.0.0** (`versionCode 41`) | **Yes (Recommended)** | **Zero pre-bundled government JARs** (all 131 official JARs downloaded directly from `asy.mof.gov.iq` on first launch & verified against `SOCODE.RSA`), **Android 14 (`targetSdkVersion 34`)** with verified Android Developer Console signing certificate, **Zero storage permissions** (`MANAGE_EXTERNAL_STORAGE` & `BIND_ACCESSIBILITY_SERVICE` removed), Hardware Keystore `AES-256-GCM` local consent receipt with 1:1 8-field parity (zero IP collection), JVM Host Storage Isolation (`checkBlockedHostPath`), X11 `-nolisten tcp`, Quoted-JSON log redaction, Eclipse Temurin OpenJDK `8u504-b01` LTS, 7-Suite Adversarial CI Security Verification & CycloneDX SBOM | [**GitHub Release `v4.0.0`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.0.0) |
| **< v4.0.0** (`v3.9.5`, `v3.9.0`, `v3.8.0`, `v3.7.0`, `v3.6.0`) | No | Superseded — upgrade to **v4.0.0** via the in-app Update Center or direct APK download | [Download `ASYCUDA_Iraq_v4.0.0.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.0.0/ASYCUDA_Iraq_v4.0.0.apk) |

### Official `v4.0.0` Release Assets & SHA-256 Checksums
Every asset published on the [official `v4.0.0` GitHub Release](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.0.0) can be verified via `sha256sum`:

| Release Asset | Purpose | SHA-256 Checksum |
| :--- | :--- | :--- |
| [**`ASYCUDA_Iraq_v4.0.0.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.0.0/ASYCUDA_Iraq_v4.0.0.apk) | Signed Android release APK (`iq.asycuda`, `versionCode 41`, `targetSdkVersion 34`) | `d36932653f446916d168bada469111f99c94da19f844184b1de9b377e2734861` |
| [**`asycuda-iraq.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.0.0/asycuda-iraq.tar.xz) | Open-source `java-bridge/` JARs (`00_bootpatch.jar`, `00_boot.jar`, `00_jnlp.jar`) & config (`zero` government JARs bundled; all 131 official JARs are downloaded directly from `asy.mof.gov.iq` on first launch) | `a0ea2336d3917e1583e923bbb4dd64ae2a37504ed873027e968abdd5d58defd1` |
| [**`runtime-arm64.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.0.0/runtime-arm64.tar.xz) | Embedded ARM64 glibc 2.38 + Eclipse Temurin OpenJDK `8u504-b01` LTS (`1.8.0_504-b01`) runtime environment | `f3dc78325792ea764e82d108b54dfc6fe4d6d9fd752bafc83bb780403286b9c1` |
| [**`sbom.cdx.json`**](sbom.cdx.json) | CycloneDX 1.5 Software Bill of Materials (Android Maven + ARM64 Runtime + Government JARs) | Tracked in repository root |

### Official APK X.509 Signing Certificate Fingerprint
Every official release APK (`ASYCUDA_Iraq_v4.0.0.apk`) can be cryptographically verified prior to installation using `apksigner verify --print-certs`:

| Field | Value |
| :--- | :--- |
| **Application ID** | `iq.asycuda` |
| **Version Name / Code** | `4.0.0` (`versionCode 41`) |
| **Signer Certificate DN** | `CN=Mohammed Ammar Hussein, OU=Logistics and Customs Software, O=ASYCUDA Iraq Open Source, L=Baghdad, ST=Baghdad, C=IQ` |
| **Signer SHA-256 Digest** | `d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023` |
| **Signer SHA-1 Digest** | `e1b9c91b50e50223885fd2ee5326b165f16535e7` |

---

## 1. 4-Layer Architecture & Data-Flow Matrix

To prevent any confusion about which component performs which action, the application is strictly separated into four isolated layers:

| Layer | Component | Source Location | Trust & Storage Boundary | Network Access |
| :--- | :--- | :--- | :--- | :--- |
| **Layer 1: Android UI & Lifecycle Manager** | `MainActivity`, `AsycudaStandaloneManager`, `AsycudaMaterialUi` | `lorie/src/main/java/com/termux/x11/` | Runs inside Android app UID (`0700` `/data/data/iq.asycuda/`, `targetSdkVersion 34`). Zero broad storage permissions (`MANAGE_EXTERNAL_STORAGE` removed). Encrypts `legal_consent_receipt.json` at rest via Android Hardware Keystore `AES-256-GCM`. | • `asy.mof.gov.iq:443` (JNLP/JAR sync)<br>• `api.github.com:443` (APK release check)<br>• `o4512278937206784.ingest.de.sentry.io:443` (Previewed 8-field legal consent receipt & voluntary bug reports)<br>• `sonarcloud.io` / `snyk.io` (About dialog badges) |
| **Layer 2: Native X11 Display Surface** | `libXlorie.so` (Termux:X11) | `lorie/src/main/cpp/` | Renders the local UNIX domain X11 socket (`/data/data/iq.asycuda/files/usr/tmp/.X11-unix/X0`, `0700`) onto an Android `SurfaceView` with TCP listening disabled (`-nolisten tcp`). | **None** (100% local UNIX socket, `-nolisten tcp`) |
| **Layer 3: Open-Source Java-to-Android Bridge** | `00_bootpatch.jar`, `00_boot.jar`, `00_jnlp.jar` | [`java-bridge/`](java-bridge/) | Intercepts desktop printing (`ASYCUDA_PDF_Printer`), `.so` document exports, and `JFileChooser` file imports inside the JVM, jailing all files to `/data/data/iq.asycuda/files/prints` (OS FBE + `0700` UID Sandbox) and blocking JVM access to `/shared_prefs` and `legal_consent_receipt.json`. Communicates with Layer 1 over `127.0.0.1:47891` using per-session `AUTH|<UUID>|` tokens. | **None** (`127.0.0.1:47891` loopback only) |
| **Layer 4: Official Iraqi Customs Client** | `so.kernel.client.DesktopMain` (131 signed `.jar` files) | Downloaded on first launch from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` | Zero government JARs are bundled in the APK. Downloaded directly from `asy.mof.gov.iq` and verified against UNCTAD/StrategyObject's pinned X.509 certificate (`META-INF/SOCODE.RSA`). Handles all customs logins, declarations, and tariff tables. | Direct connection to **`asy.mof.gov.iq:443`** and **`asy.mof.gov.iq:2006`** |

```mermaid
flowchart TB
    subgraph AndroidSandbox["Android App Sandbox (/data/data/iq.asycuda — Mode 0700, targetSdkVersion 34, Zero Storage Permissions)"]
        UI["Layer 1: Android Material UI & Manager\n(AsycudaStandaloneManager / AsycudaMaterialUi)"]
        X11["Layer 2: Embedded X11 SurfaceEngine\n(libXlorie.so — UNIX Socket :0, -nolisten tcp)"]
        VaultPrints[("Private App Vault (OS FBE + 0700)\nfiles/prints (PDFs, Exports & JFileChooser Jail)")]
        ConsentReceipt[("Keystore AES-256-GCM Encrypted\nfiles/legal_consent_receipt.json")]
        CacheUpdates[("Private Update Cache\ncache/updates/*.apk (Auto-Deleted)")]

        subgraph LinuxJVM["Embedded ARM64 Temurin OpenJDK 8u504-b01 Process (glibc 2.38)"]
            Bridge["Layer 3: Open-Source Java Bridge\n(00_boot.jar / 00_bootpatch.jar / 00_jnlp.jar)"]
            MOFClient["Layer 4: Official Unmodified ASYCUDA World Client\n(so.kernel.client.DesktopMain — 131 Signed JARs)"]
        end
    end

    SAF["Android System File Picker\n(Intent.ACTION_OPEN_DOCUMENT)"]
    Share["Android FileProvider\n(Open / Share Sheet)"]
    MOFServer["Official Iraqi Customs Server\n(asy.mof.gov.iq :443 & :2006)"]
    GitHub["GitHub Releases\n(api.github.com :443)"]
    Sentry["Sentry Compliance & Bug Report Endpoint\n(o4512278937206784.ingest.de.sentry.io :443)"]

    Bridge <-->|"AUTH|<token>| on 127.0.0.1:47891"| UI
    MOFClient <-->|"UNIX Socket :0"| X11
    Bridge -->|"Writes Printed PDFs"| VaultPrints
    Bridge <-->|"Jailed File Chooser"| VaultPrints
    SAF -->|"Copies 1 User-Picked File"| VaultPrints
    VaultPrints -->|"Grant Temporary Read URI"| Share
    UI -->|"AES-256-GCM Encrypt/Decrypt"| ConsentReceipt
    UI -->|"Downloads & Auto-Deletes APK"| CacheUpdates
    MOFClient <-->|"Direct HTTPS & SOClass TLS"| MOFServer
    UI -->|"Pinned X.509 JNLP/JAR Sync"| MOFServer
    UI -->|"SHA-256 + Cert Verified Update"| GitHub
    UI -->|"1. Previewed 8-Field Legal Receipt\n2. Voluntary Redacted Bug Report"| Sentry
```

---

## 2. Zero Storage Permissions, Unified Private Vault & Automatic File Picker Bridge (`v4.0.0`)

1. **Zero Broad Storage or Accessibility Permissions (`AndroidManifest.xml`)**:
   - `MANAGE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`, and `BIND_ACCESSIBILITY_SERVICE` have been **completely removed** from [`lorie/src/main/AndroidManifest.xml`](lorie/src/main/AndroidManifest.xml) (enforced on every commit by [`scripts/SecurityVerificationTest.java`](scripts/SecurityVerificationTest.java)).
   - The app requests only normal runtime permissions required for an X11 foreground session (`INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, `POST_NOTIFICATIONS`, `WAKE_LOCK`, `VIBRATE`).
2. **Automatic File Picker Bridge (`Zero Modification to Government Dialogs`)**:
   - Because the legacy ASYCUDA Java 8 Swing client uses `javax.swing.JFileChooser` / `java.awt.FileDialog` (which cannot invoke Android `content://` intents directly), [`AsycudaBoot.java`](java-bridge/AsycudaBoot.java) jails `JFileChooser` and `FileDialog` strictly to the unified private Vault folder `/data/data/iq.asycuda/files/prints` using `VaultFileSystemView` (preventing traversal to parent system folders while allowing subfolder management inside the Vault).
   - No buttons or custom UI components are ever injected into ASYCUDA's `JFileChooser`. Instead, when ASYCUDA opens its file chooser, `AsycudaBoot` sends `AUTH|<token>|PICK_WORKSPACE_FILE` over `127.0.0.1:47891` to `AsycudaStandaloneManager`, which displays a compact Android prompt (`showFilePickerBridgePromptModal`) asking whether the user wants to **Import a new file from their phone into the Vault** (`Intent.ACTION_OPEN_DOCUMENT`) or **Use a file already in the Vault**.
   - When the user imports a file from their phone, only that single chosen file is copied into `/data/data/iq.asycuda/files/prints/` and automatically highlighted inside the open Java `JFileChooser` dialog.
3. **Full-Featured Private Vault — File Manager (`Folders`, `Copy`, `Move`, `Open`, `Share`, `Delete` & `Upload Staging Exclusion`)**:
   - All printed PDFs, exported customs documents, and imported attachments are stored **exclusively** inside `/data/data/iq.asycuda/files/prints` (protected by Android's kernel-enforced `0700` UID sandbox and OS File-Based Encryption). Zero files are ever written to public `/storage/emulated/0/Download`.
   - Inside the in-app **Vault — File Manager** (`showVaultFileManagerModal`), users can create subfolders, navigate into and out of folders, **Copy** or **Move** files and folders between locations, **Open**, **Share** (files only—folders are strictly prevented from being shared), or **Delete** items via Apple-style icon buttons and multi-select batch actions.
   - **Upload Staging Exclusion**: Temporary `soems*ems` and `prn*xc` upload staging files in `/client/tmp/` (and any file already present in the Vault tree by name+size or SHA-256 digest via `isFileAlreadyInVault`) are ignored by the print/export watcher so uploading an attachment to the customs server never triggers duplicate Vault saves or file-open dialogs.
4. **Private Cache & Automatic Cleanup for In-App APK Updates**:
   - When installing an app update via the **Update Center**, `downloadAndInstallApkUpdate()` downloads the `.apk` into `/data/data/iq.asycuda/cache/updates/` (never into `files/prints` or public storage), verifies its SHA-256 digest and X.509 signing certificate, launches the Android package installer, and `cleanupStaleUpdateApks()` automatically deletes the `.apk` file on startup once the update completes.

---

## 3. Data Privacy, Mandatory Previewable Legal Receipt & External Network Inventory

1. **Zero Credential Collection or Storage**:
   - The Android wrapper **never** intercepts, inspects, stores, or transmits your ASYCUDA World username, password, two-factor codes, or customs declaration data.
   - All authentication and customs operations take place inside the official ASYCUDA World Java client (`so.kernel.client.DesktopMain`) communicating directly with the Iraqi Ministry of Finance server (`https://asy.mof.gov.iq` and `//asy.mof.gov.iq:2006/`).
2. **Mandatory First-Launch Legal Consent Receipt with Explicit Pre-Transmission Preview (`Preview Sentry Receipt Before Sending`)**:
   - To protect both the user and the open-source developer under a clear legal agreement, the application requires acceptance of the **Terms & Conditions (Liability Waiver)** and **Privacy Policy (Mandatory Legal Consent Receipt Registration)** before launching.
   - **Dual Mandatory Checkboxes**: Users must explicitly tick both Checkboxes `1` and `2` in `showTermsAndConditionsModal()`. If declined, the application exits immediately without transmitting any data.
   - **Interactive Receipt Preview Button (`🔍 Preview Sentry Receipt Before Sending`)**: Before tapping **"Accept & Continue"**, the user can tap **"Preview Sentry Receipt Before Sending (See Exact 8 Fields)"** right inside the Terms modal (`showConsentReceiptPreviewModal()`) to inspect the exact 8-field JSON payload (`consent_id`, `signatory_name`, `company_name`, `policy_version`, `app_version`, `accepted_at_utc`, `device_hash`, `sha256_signature`) that will be sent once over HTTPS to Sentry. Note: `sha256_signature` is a deterministic `SHA-256 integrity digest` over those fields, not an asymmetric private-key signature.
   - **Hardware Keystore `AES-256-GCM` Local Encryption**: The local copy (`legal_consent_receipt.json`) is encrypted at rest using an Android Hardware Keystore `AES-256-GCM` key (`AsycudaConsentReceiptKey_v1`) and blocked from JVM access by `AsycudaBoot.checkBlockedHostPath()`.
3. **Voluntary User Bug Reports (In-Memory Only — Zero Local ZIP Files)**:
   - Diagnostic bundles (`buildDiagnosticsZipBytes()`) are assembled strictly in RAM (`ByteArrayOutputStream`) and streamed directly over HTTPS to Sentry **only** when the user explicitly opens *"Report a Technical Issue"* and taps **"Submit Report"**. **No diagnostic `.zip` file is ever written to internal or external storage**.
   - Before any text is placed in the Sentry envelope, `redactSensitiveLogContent()` scrubs local IPC tokens, URL credentials, `Bearer`/`Basic` tokens, XML/SOAP auth tags (`<password>`, `<user>`, `<username>`, `<tin>`, `<declarant>`, `<token>`, `<jsessionid>`), and both quoted JSON strings and unquoted key-value patterns for usernames, passwords, TINs, cookies, and session tokens.
4. **Complete Inventory of External Network Endpoints**:

   | Hostname / Endpoint | Protocol | Direction | Purpose & Data Transmitted |
   | :--- | :--- | :--- | :--- |
   | **`asy.mof.gov.iq`** (`:443` & `:2006`) | HTTPS / SOClass TLS | Read/Write | Official Iraqi General Commission of Customs ASYCUDA World server (`AWIQWorld.jnlp`, signed government `.jar` packages, and live customs session). |
   | **`api.github.com`** & **`github.com`** | HTTPS (`443`) | Read-Only (`GET`) | Fetches the latest open-source release metadata (`releases/latest`) and downloads `ASYCUDA_Iraq_vX.Y.Z.apk` when the user installs an app update. |
   | **`o4512278937206784.ingest.de.sentry.io`** | HTTPS (`443`) | Write-Only (`POST`) | Used strictly for: (a) recording the previewed 8-field first-launch legal consent receipt (`SHA-256 integrity digest`, with zero IP collection), and (b) uploading voluntary, user-submitted technical bug reports. |
   | **`sonarcloud.io`** & **`snyk.io`** | HTTPS (`443`) | Read-Only (`GET`) | Queries public code-quality and security badge status solely when the user opens the **About & Security Verification** dialog. Sends zero user or device data. |

---

## 4. Full Transparency of Custom Java-to-Android Bridge JARs (`java-bridge/`)

Every line of `.java` source code for the three compatibility JARs—and the script to compile them from scratch—is published in [`java-bridge/`](java-bridge/):

| Bridge Archive | Source Files | Purpose |
| :--- | :--- | :--- |
| **`00_bootpatch.jar`** | [`java-bridge/bootpatch/java/awt/Desktop.java`](java-bridge/bootpatch/java/awt/Desktop.java) | Loaded via `-Xbootclasspath/p:00_bootpatch.jar`. Intercepts `Desktop.getDesktop().open(File)` and `.browse(URI)` inside the headless Linux JVM and forwards them to `AsycudaBoot.handleDesktopAction()`. |
| **`jars/00_jnlp.jar`** | [`java-bridge/jnlp/javax/jnlp/BasicService.java`](java-bridge/jnlp/javax/jnlp/BasicService.java)<br>[`java-bridge/jnlp/javax/jnlp/ServiceManager.java`](java-bridge/jnlp/javax/jnlp/ServiceManager.java)<br>[`java-bridge/jnlp/javax/jnlp/UnavailableServiceException.java`](java-bridge/jnlp/javax/jnlp/UnavailableServiceException.java) | Provides the `javax.jnlp.ServiceManager` and `BasicService` API expected by `client.jar` when running outside Oracle's desktop `javaws` launcher, routing `showDocument(URL)` to the Android bridge. |
| **`jars/00_boot.jar`** | [`java-bridge/AsycudaBoot.java`](java-bridge/AsycudaBoot.java) | Pre-main wrapper that:<br>• Registers a pure-Java PDF 1.4 print service (`ASYCUDA_PDF_Printer`) saving printed declarations exclusively in `/data/data/iq.asycuda/files/prints`.<br>• Jails `JFileChooser` and `FileDialog` to `/data/data/iq.asycuda/files/prints` via `VaultFileSystemView` and sends `PICK_WORKSPACE_FILE` so users can import files via Android's native file picker without storage permissions.<br>• Watches for temporary `.so` files exported by ASYCUDA (excluding `soems*ems` / `prn*xc` upload staging files), detects their real file type (`PDF`, `XLSX`, `DOCX`, `XML`, `JPG`, `PNG`) via magic-byte headers and SHA-256 deduplication, and opens them on Android.<br>• Hooks `so.rmi.transport.client.ClientTransportLayer`'s `RejectHandler` for `"SOClass Client have to be updated"` alerts, guards `so.kernel.client.Resources.multiLinguisticListeners` (`VisualFacetJTableSDS.updateComponent`) during UI language switches, and filters benign `R_Load_SadItmAdm` / `R_Load_HisWriteOff` NPE popups.<br>• Installs a filesystem-watching `SecurityManager` that explicitly **blocks OS subprocess execution** (`checkExec()` throws `SecurityException`) and **blocks JVM access to Android host storage paths** (`/shared_prefs`, `legal_consent_receipt.json`, `/databases/`, `/datastore/`).<br>• Invokes the official `so.kernel.client.DesktopMain.main(args)`. |

To rebuild and verify all three JARs from source:

```bash
./java-bridge/build-bridge.sh
```

---

## 5. Cryptographic Verification & Automated Adversarial Security Suite

### 5.1 Pinned UNCTAD/SOClass Publisher Certificate (`PINNED_MOF_SOCODE_CERT_SHA256`)
Every official ASYCUDA World JAR published on `asy.mof.gov.iq/aw/` is signed by UNCTAD/StrategyObject (`META-INF/SOCODE.RSA` / `META-INF/SOCODE.SF`). Before any downloaded `.jar.tmp` file is renamed into `asycuda/jars/`, `verifyDownloadedJarIntegrity(File jarFile)` enforces three mandatory cryptographic checks:
1. Verifies that the archive contains both `META-INF/SOCODE.RSA` and `META-INF/SOCODE.SF` and parses the PKCS#7 certificate chain (`CertificateFactory.getInstance("X.509")`) to confirm that the signer certificate's SHA-256 fingerprint matches `PINNED_MOF_SOCODE_CERT_SHA256`:
   - **Signer Subject / Issuer**: `CN=SOClass, OU=SOClass, O=Strategy Object, L=Paris, ST=France, C=FR`
   - **Pinned X.509 Certificate SHA-256**: `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`
2. Opens `new java.util.jar.JarFile(jarFile, true)` and drains every entry's input stream so `JarVerifier` validates every manifest digest against `META-INF/SOCODE.SF`.
3. Inspects `je.getCertificates()` on **every single non-`META-INF/` file entry** in the archive and rejects the JAR if any class or resource is unsigned or signed by a certificate whose SHA-256 fingerprint does not match `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`.

### 5.2 Automated Adversarial Security Test Suite (`scripts/SecurityVerificationTest.java`)
Executed automatically on every commit and pull request in [`.github/workflows/security-scan.yml`](.github/workflows/security-scan.yml):
- **Suite 1 (`testManifestZeroBroadPermissions`)**: Fails the build if `MANAGE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`, or `BIND_ACCESSIBILITY_SERVICE` ever appear in `AndroidManifest.xml`.
- **Suite 2 (`testJnlpUrlAllowlist`)**: Verifies that `resolveTrustedMofJarUrl()` rejects plaintext `http://`, spoofed domains, non-443 ports, and `..` path traversal.
- **Suite 3 (`testUnsignedAndTamperedJarRejection`)**: Verifies that unsigned and `META-INF`-only JARs are rejected by X.509 entry certificate verification.
- **Suite 4 (`testBridgeAuthAndPathTraversal`)**: Verifies that unauthenticated localhost IPC commands and `../../` file paths are rejected/stripped.
- **Suite 5 (`testCredentialAndPiiRedaction`)**: Verifies that URL credentials, XML `<login>`/`<password>` tags, `TIN`, `jsessionid`, `Authorization: Bearer ...`, bridge tokens, and both quoted JSON strings (`"password": "..."`) and unquoted key-value pairs are 100% redacted.
- **Suite 6 (`testJvmHostStorageIsolation`)**: Verifies that `AsycudaBoot.checkBlockedHostPath()` throws `SecurityException` on any attempt to read, write, or delete `/shared_prefs`, `legal_consent_receipt.json`, `/databases/`, or `/datastore/`.
- **Suite 7 (`testConsentReceiptParityAndZeroBundledJars`)**: Verifies that `AsycudaStandaloneManager.uploadConsentReceiptToSentry()` contains zero `ip_address` or `contexts` fields (enforcing 1:1 8-field parity with the preview modal) and that `asycuda-iraq.tar.xz` bundles strictly our 3 open-source bridge JARs (`00_boot.jar`, `00_bootpatch.jar`, `00_jnlp.jar`) and zero government JARs.

---

## 6. Reporting a Vulnerability

If you discover a security issue in this repository, please report it via [GitHub Issues](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/issues) or contact **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)). Reports are reviewed promptly and patches are released with full credit.
