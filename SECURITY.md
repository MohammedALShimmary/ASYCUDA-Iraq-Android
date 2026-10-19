# Security Policy & Architecture Transparency

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)

Because **ASYCUDA World — Republic of Iraq (Android)** (`iq.asycuda`) is an independent, open-source desktop runtime wrapper used by licensed customs brokers and logistics operators, security and source-code transparency are treated as first-order engineering requirements.

---

## Supported Versions & Published Release Verification (`v3.8.0`)

All users should run **[v3.8.0](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.8.0)** (`versionCode 38`) or newer. Earlier releases (`v3.7.0` and `v3.6.0`) have been marked as superseded prereleases on GitHub Releases following the v3.8.0 security hardening audit.

| Version | Supported | Security Status | Direct Release Link |
| :--- | :--- | :--- | :--- |
| **v3.8.0** (`versionCode 38`) | **Yes (Recommended)** | Pinned UNCTAD/SOClass X.509 JAR signature enforcement (`SOCODE.RSA`), `asy.mof.gov.iq` HTTPS hostname allowlist, full X.509 TLS verification, authenticated `127.0.0.1` IPC bridge, SHA-256 + signature verified APK updater, in-memory redacted diagnostics, open-source `java-bridge/` | [**GitHub Release `v3.8.0`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.8.0) |
| **< v3.8.0** (`v3.7.0`, `v3.6.0`) | No | Superseded — upgrade to **v3.8.0** via the in-app Update Center or direct APK download | [Download `ASYCUDA_Iraq_v3.8.0.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.8.0/ASYCUDA_Iraq_v3.8.0.apk) |

### Official `v3.8.0` Release Assets & SHA-256 Checksums
Every asset published on the [official `v3.8.0` GitHub Release](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.8.0) can be verified via `sha256sum`:

| Release Asset | Purpose | SHA-256 Checksum |
| :--- | :--- | :--- |
| [**`ASYCUDA_Iraq_v3.8.0.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.8.0/ASYCUDA_Iraq_v3.8.0.apk) | Signed Android release APK (`iq.asycuda`, `versionCode 38`) | `3b141d5c17f4a52502d5037cdd87ac9bf44241db632b6c53cef84f27b9d79829` |
| [**`asycuda-iraq.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.8.0/asycuda-iraq.tar.xz) | Baseline ASYCUDA Iraq client bundle + open-source `java-bridge/` JARs | `ca3a5b787c4d0ba94fa533b00ebb0ce1bb845ceb96d6f1968fb360dfc7d0c7fe` |
| [**`runtime-arm64.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.8.0/runtime-arm64.tar.xz) | Embedded ARM64 glibc + OpenJDK 8 runtime environment | `f3dc78325792ea764e82d108b54dfc6fe4d6d9fd752bafc83bb780403286b9c1` |

### Official APK X.509 Signing Certificate Fingerprint
Every official release APK (`ASYCUDA_Iraq_v3.8.0.apk`) can be cryptographically verified prior to installation using `apksigner verify --print-certs`:

| Field | Value |
| :--- | :--- |
| **Application ID** | `iq.asycuda` |
| **Version Name / Code** | `3.8.0` (`versionCode 38`) |
| **Signer Certificate DN** | `CN=Mohammed Ammar Hussein, OU=Logistics and Customs Software, O=ASYCUDA Iraq Open Source, L=Baghdad, ST=Baghdad, C=IQ` |
| **Signer SHA-256 Digest** | `d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023` |
| **Signer SHA-1 Digest** | `e1b9c91b50e50223885fd2ee5326b165f16535e7` |

---

## 1. Data Privacy & Complete External Network Endpoints Inventory

1. **Zero Credential Collection or Storage**:
   - The Android wrapper **never** intercepts, inspects, stores, or transmits your ASYCUDA World username, password, two-factor codes, or customs declaration data.
   - All authentication and customs operations take place inside the official ASYCUDA World Java client (`so.kernel.client.DesktopMain`) communicating directly with the Iraqi Ministry of Finance server (`https://asy.mof.gov.iq` and `//asy.mof.gov.iq:2006/`).
2. **No Proxy or Relay Servers**:
   - Network traffic between the ASYCUDA Java client and `asy.mof.gov.iq` goes directly from your Android device to the Ministry of Finance servers.
3. **Unmodified & Cryptographically Verified Official Customs Packages**:
   - All 131 official `.jar` packages listed in `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` are downloaded strictly from `https://asy.mof.gov.iq/aw/`, verified against the pinned UNCTAD/SOClass code-signing certificate (`META-INF/SOCODE.RSA`), and executed without modification.
4. **Complete Inventory of External Network Endpoints**:
   Every external hostname contacted by the application and its exact purpose are listed below:

   | Hostname / Endpoint | Protocol | Direction | Purpose & Data Transmitted |
   | :--- | :--- | :--- | :--- |
   | **`asy.mof.gov.iq`** (`:443` & `:2006`) | HTTPS / SOClass TLS | Read/Write | Official Iraqi General Commission of Customs ASYCUDA World server (`AWIQWorld.jnlp`, signed government `.jar` packages, and live customs session). |
   | **`api.github.com`** & **`github.com`** | HTTPS (`443`) | Read-Only (`GET`) | Fetches the latest open-source release metadata (`releases/latest`) and downloads `ASYCUDA_Iraq_vX.Y.Z.apk` when the user installs an app update. |
   | **`o4511245705412608.ingest.de.sentry.io`** | HTTPS (`443`) | Write-Only (`POST`) | Used strictly for: (a) recording the first-launch legal consent receipt (`SHA-256 integrity digest`), and (b) uploading voluntary, user-submitted technical bug reports. |
   | **`sonarcloud.io`** & **`snyk.io`** | HTTPS (`443`) | Read-Only (`GET`) | Queries public code-quality and security badge status (`sonarcloud.io/api/measures/component` and `snyk.io/test/github/.../badge.svg`) solely when the user opens the **About & Security Verification** dialog. Sends zero user or device data. |

5. **Diagnostic & Compliance Reporting Privacy Controls**:
   - **First-Launch Legal Consent Receipt (`SHA-256 Integrity Digest`)**: When the user accepts the Terms & Conditions on initial launch, the app computes a deterministic `SHA-256 integrity digest` over the consent fields (`consentId`, signatory name, company name, timestamp, policy version, and anonymized `ANDROID_ID` hash) and sends that receipt to Sentry. Note: this is a cryptographic SHA-256 hash digest for audit integrity, not an asymmetric private-key digital signature.
   - **Voluntary User Bug Reports (In-Memory Only — Zero Local ZIP Files)**:
     - **Zero Local Diagnostic ZIP Files on Disk**: Diagnostic bundles (`buildDiagnosticsZipBytes()`) are assembled strictly in RAM (`ByteArrayOutputStream`) and streamed directly over HTTPS to Sentry **only** when the user explicitly opens *"Report a Technical Issue"* and taps **"Submit Report"**. **No diagnostic `.zip` file is ever written to internal or external storage**, and there is no local ZIP export/share feature.
     - **Strict Credential & PII Scrubbing Across All Envelope Fields**: Before any text is placed in the Sentry event message (`formatted`), `full_description`, `user_report` (`comments`), or in-memory diagnostic stream, `redactSensitiveLogContent()` strips local IPC tokens, URL credentials, XML/SOAP auth tags (`<password>`, `<user>`, `<username>`, `<tin>`, `<declarant>`, `<token>`, `<jsessionid>`), and key-value/JSON patterns for usernames, passwords, TINs, cookies, and session tokens. Raw `AWIQWorld.jnlp`, `SharedPreferences` XMLs, and raw consent files are excluded, and `logcat` is restricted strictly to the application's own process (`--pid=<myPid>`).
     - **Screenshot Privacy Notice**: Because optical character redaction cannot be performed on bitmap images, the *"Report a Technical Issue"* dialog displays a prominent warning in English, Arabic, and Kurdish advising users **not** to attach screenshots showing visible passwords, TINs, or confidential live customs declarations.
   - Build-time developer tokens (`GITHUB_TOKEN`, `SENTRY_AUTH_TOKEN`, `SNYK_TOKEN`) and signing keystores are never stored in the repository or embedded in the APK.

---

## 2. Full Transparency of Custom Java-to-Android Bridge JARs (`java-bridge/`)

Because Android does not include desktop `javaws` (`.jnlp`), a desktop CUPS print spooler, or `java.awt.Desktop` native browser/PDF handlers, the app places three small, open-source compatibility JARs on the JVM classpath alongside the unmodified Ministry of Finance JARs.

Every line of `.java` source code for these three JARs—and the script to compile them from scratch—is published in [`java-bridge/`](java-bridge/):

| Bridge Archive | Source Files | Purpose |
| :--- | :--- | :--- |
| **`00_bootpatch.jar`** | [`java-bridge/bootpatch/java/awt/Desktop.java`](java-bridge/bootpatch/java/awt/Desktop.java) | Loaded via `-Xbootclasspath/p:00_bootpatch.jar`. Intercepts `Desktop.getDesktop().open(File)` and `.browse(URI)` inside the headless Linux JVM and forwards them to `AsycudaBoot.handleDesktopAction()`. |
| **`jars/00_jnlp.jar`** | [`java-bridge/jnlp/javax/jnlp/BasicService.java`](java-bridge/jnlp/javax/jnlp/BasicService.java)<br>[`java-bridge/jnlp/javax/jnlp/ServiceManager.java`](java-bridge/jnlp/javax/jnlp/ServiceManager.java)<br>[`java-bridge/jnlp/javax/jnlp/UnavailableServiceException.java`](java-bridge/jnlp/javax/jnlp/UnavailableServiceException.java) | Provides the `javax.jnlp.ServiceManager` and `BasicService` API expected by `client.jar` when running outside Oracle's desktop `javaws` launcher, routing `showDocument(URL)` to the Android bridge. |
| **`jars/00_boot.jar`** | [`java-bridge/AsycudaBoot.java`](java-bridge/AsycudaBoot.java) | Pre-main wrapper that:<br>• Registers a pure-Java PDF 1.4 print service (`ASYCUDA_PDF_Printer`) so printing any declaration or receipt produces a crisp PDF in `Download/Asycuda_Prints` without Ghostscript.<br>• Watches for temporary `.so` files exported by ASYCUDA, detects their real file type (`PDF`, `XLSX`, `DOCX`, `XML`, `JPG`, `PNG`) via magic-byte headers and SHA-256 deduplication, and opens them on Android.<br>• Auto-maximizes the main ASYCUDA desktop frame and signals Android when an editable text field is tapped.<br>• Hooks `so.rmi.transport.client.ClientTransportLayer`'s `RejectHandler` and watches AWT modal dialogs for `"SOClass Client have to be updated"` alerts so the Android **Update Center** opens automatically.<br>• Installs a filesystem-watching `SecurityManager` for `/tmp/` print interception that explicitly **blocks OS subprocess execution** (`checkExec()` throws `SecurityException`).<br>• Invokes the official `so.kernel.client.DesktopMain.main(args)`. |

To rebuild and verify all three JARs from source:

```bash
./java-bridge/build-bridge.sh
```

See [`java-bridge/README.md`](java-bridge/README.md) for full compilation and verification instructions.

---

## 3. Security Controls & Hardening (v3.8.0)

### 3.1 Verified X.509 TLS (`443`) & UNCTAD SOClass Cryptography (`2006`)
- **HTTPS (`asy.mof.gov.iq:443`)**: All HTTPS connections to `https://asy.mof.gov.iq` in [`AsycudaStandaloneManager.java`](lorie/src/main/java/com/termux/x11/AsycudaStandaloneManager.java) use `createMofVerifiedSslFactory()`, which initializes a standard `TrustManagerFactory` (`PKIX`/`X509`) with Android's system `AndroidCAStore` plus the official **Sectigo Public Server Authentication Root R46** (cross-signed by `USERTrust RSA Certification Authority`) and **ZeroSSL RSA DV SSL CA 2** intermediate certificate, combined with `HttpsURLConnection.getDefaultHostnameVerifier()`. No trust-all `X509TrustManager` or permissive `HostnameVerifier` exists anywhere in the codebase.
- **SOClass Application Server Transport (`asy.mof.gov.iq:2006`)**: In UNCTAD ASYCUDA World's architecture (`AWIQWorld.jnlp`), authentication and customs document payloads over port `2006` (`//asy.mof.gov.iq:2006/`) are encrypted at the **SOClass application protocol layer** by UNCTAD's official cryptographic modules (`lib/ext/socrypto.jar`, `lib/sslca.jar`, and `lib/ext/socpe.jar`). `AsycudaBoot.java`'s `RMISocketFactory` wrapper does not alter SOClass encryption; it solely applies mobile TCP socket tuning (`setTcpNoDelay(true)`, `setKeepAlive(true)`, and 256 KB socket buffers) to prevent cellular NAT idle drops.

### 3.2 Cryptographically Verified In-App Updates & Pinned Publisher Certificate for Government JARs
- **APK Updates (`downloadAndInstallApkUpdate`)**:
  - Computes the APK's **SHA-256 digest** during download and verifies it against the official GitHub Release asset digest.
  - Verifies that the downloaded APK's **X.509 signing certificate** (`PackageManager.GET_SIGNATURES`) matches the currently installed application (`d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023`) before invoking the Android package installer.
- **Strict Hostname Allowlist & Pinned X.509 Code-Signing Certificate for `AWIQWorld.jnlp` JARs (`syncGovernmentJars` & `verifyDownloadedJarIntegrity`)**:
  - **HTTPS & Hostname Allowlist (`resolveTrustedMofJarUrl` / `openMofConnection`)**: Every `<jar href="..."/>` entry parsed from `AWIQWorld.jnlp` is validated by `resolveTrustedMofJarUrl()`. Any URL that does not use `https`, does not match the exact hostname `asy.mof.gov.iq`, or points outside `/aw/*.jar` is immediately rejected, and HTTP redirects are disabled (`setInstanceFollowRedirects(false)`).
  - **Pinned UNCTAD/SOClass Publisher Certificate (`PINNED_MOF_SOCODE_CERT_SHA256`)**: Every official ASYCUDA World JAR published on `asy.mof.gov.iq/aw/` is signed by UNCTAD/StrategyObject (`META-INF/SOCODE.RSA` / `META-INF/SOCODE.SF`). Before any downloaded `.jar.tmp` file is renamed into `asycuda/jars/`, `verifyDownloadedJarIntegrity(File jarFile)` enforces three mandatory cryptographic checks:
    1. Verifies that the archive contains both `META-INF/SOCODE.RSA` and `META-INF/SOCODE.SF` and parses the PKCS#7 certificate chain (`CertificateFactory.getInstance("X.509")`) to confirm that the signer certificate's SHA-256 fingerprint matches `PINNED_MOF_SOCODE_CERT_SHA256`:
       - **Signer Subject / Issuer**: `CN=SOClass, OU=SOClass, O=Strategy Object, L=Paris, ST=France, C=FR`
       - **Pinned X.509 Certificate SHA-256**: `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`
    2. Opens `new java.util.jar.JarFile(jarFile, true)` and drains every entry's input stream so `JarVerifier` validates every manifest digest against `META-INF/SOCODE.SF`.
    3. Inspects `je.getCertificates()` on **every single non-`META-INF/` file entry** in the archive and rejects the JAR if any class or resource is unsigned or signed by a certificate whose SHA-256 fingerprint does not match `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`.

### 3.3 Authenticated Localhost IPC Bridge (`127.0.0.1:47891`)
- The communication socket between the embedded JVM and the Android UI is bound strictly to `InetAddress.getByName("127.0.0.1")` (loopback only, inaccessible from Wi-Fi or cellular networks).
- At launch, `AsycudaStandaloneManager` generates a random per-session token (`UUID.randomUUID()`) and passes it to the JVM via `-Dasycuda.bridge.token=<token>`.
- `handleBridgeMessage()` rejects any socket message that does not carry the valid `AUTH|<token>|` prefix, validates `OPEN|` file paths against canonical allowed directories (`getCanonicalPath()`), and restricts `BROWSE|` URLs to `http`/`https` schemes.

### 3.4 Android Application, Storage & Filesystem Hardening
- **Why `MANAGE_EXTERNAL_STORAGE` Is Required**: The official ASYCUDA World client runs inside an embedded Linux glibc OpenJDK 8 process (`jre8/bin/java`) and uses desktop Swing `JFileChooser` / POSIX `java.io.File` system calls—which cannot invoke Android's `Intent`-based Storage Access Framework (`content://` URIs)—to read customs declaration XMLs and commercial invoices from `/sdcard/Download/Asycuda_Files` and write printed PDF declarations to `/sdcard/Download/Asycuda_Prints`. The app accesses only those dedicated user workflow folders (`Download/Asycuda_Files` and `Download/Asycuda_Prints`).
- **Backup Disabled (`android:allowBackup="false"`)**: Prevents unauthorized extraction of internal application directories via ADB backup.
- **Cleartext Traffic Disabled (`android:usesCleartextTraffic="false"`)**: Enforces HTTPS for all Android network connections.
- **Tar-Slip Path Traversal Protection**: Both `extractTarXzAsset()` and `extractBootJarsOnly()` validate every archive entry's canonical path (`outFile.getCanonicalPath().startsWith(destCanonPrefix)`) before writing to disk.
- **Principle of Least Privilege Permissions**: Internal runtime directories and extracted files use owner-only permissions (`0700` for directories/executables, `setReadable(true, true)` for files) instead of world-writable `0777` modes.

---

## 4. Reporting a Vulnerability

If you discover a security issue in this repository, please report it via [GitHub Issues](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/issues) or contact **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)). Reports are reviewed promptly and patches are released with full credit.
