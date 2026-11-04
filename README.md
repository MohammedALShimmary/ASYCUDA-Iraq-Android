# AsyLauncher — Launcher for ASYCUDA (Android)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Independent, open-source Android compatibility launcher (**AsyLauncher**, package `io.github.mohammedalshimmary.asylauncher`) designed to run the **ASYCUDA World** Java/JNLP desktop system (`https://asy.mof.gov.iq`) natively on Android phones and tablets for customs brokers, clearing agents, and logistics operators. Not affiliated with or endorsed by the Iraqi Ministry of Finance, the General Commission of Customs, or UNCTAD.

[**Download Major Release `v4.3.0` (`AsyLauncher_v4.3.0.apk`)**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.3.0) • [**Direct APK Download**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/AsyLauncher_v4.3.0.apk) • [**Security & Privacy Policy (`SECURITY.md`)**](SECURITY.md) • [**Java Bridge Source (`java-bridge/`)**](java-bridge/) • [**CycloneDX SBOM (`sbom.cdx.json`)**](sbom.cdx.json)

---

## Latest Official Major Release: [`v4.3.0` (`versionCode 52`)](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.3.0)

> [!CAUTION]
> **MAJOR SECURITY & ARCHITECTURE RELEASE — ALL PREVIOUS RELEASES (`< v4.3.0`) ARE UNSAFE & NOT RECOMMENDED**
> Release **`v4.3.0`** is the official **Major Security, Rebrand & Architecture Release (`AsyLauncher`)** introducing the `io.github.mohammedalshimmary.asylauncher` package identity, Android 15 (`targetSdkVersion 35`) Google Play compliance, Customizable Multi-Finger Gestures, Floating Mini Customs Calculator, Interactive 5-Step First-Run Tutorial, 3-Layer Hardened Google Cloud Firestore Integration (`firestore.rules`), Dynamic Host & Port Discovery (zero hardcoded IPs or ports), Universal Network Diagnostics & On-Demand Known-Issues Catalog, Composite MOF TLS Certificate Trust (`mof_cacerts.jks`), Hardened Fail-Closed JVM Sandboxing, and One-Way Clipboard Paste. **All earlier releases (`< v4.3.0`) are superseded and not recommended for production use.** Please upgrade to **`v4.3.0`**.

| Release Asset | SHA-256 Checksum |
| :--- | :--- |
| [**`AsyLauncher_v4.3.0.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/AsyLauncher_v4.3.0.apk) | `d96171884f32596da9ba39ca195e5449f079ebd09a39eac510c4b66e2e753217` |
| [**`AsyLauncher_v4.3.0.aab`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/AsyLauncher_v4.3.0.aab) | `2589d493665ec418a7deb3eda6ef31b1f7b29f4c6a2f2263f0a0937c857b899d` |
| [**`asycuda-iraq.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/asycuda-iraq.tar.xz) | `03211e0fd9c1584947b63e898cefbe8ab16740154ad52c9203cd2ba0824ae54d` |
| [**`runtime-arm64.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/runtime-arm64.tar.xz) | `04cdd11f4fb4ceef01ca482d78ad8e8248b3a284edb1c9133518b68fe5af1e37` |

---

## Features

- **Zero Pre-Bundled Government JARs & Direct First-Launch Sync**: Zero Ministry of Finance / UNCTAD `.jar` packages are bundled inside the APK. On initial launch, the app downloads all 131 official client JARs directly from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` in parallel and cryptographically verifies every JAR entry against UNCTAD's pinned `META-INF/SOCODE.RSA` X.509 certificate.
- **Android 15 (`targetSdkVersion 35`), Verified Developer Certificate & Zero Storage Permissions**: Installs as a standard Android APK/AAB on 64-bit ARM (`arm64-v8a`, 16 KB page-size aligned) phones and tablets running Android 8.0–15+ with a verified Android Developer Console signing certificate (`SHA-256: d3:02:d4:d0:...`) and **zero broad storage or accessibility permissions** (`MANAGE_EXTERNAL_STORAGE`, `READ/WRITE_EXTERNAL_STORAGE`, and `BIND_ACCESSIBILITY_SERVICE` removed).
- **Unified Private App Vault (File Manager) & Automatic File Picker Bridge**:
  - Prints customs declarations, receipts, and assessment notices directly to PDF inside the app's **Private Vault** (`/data/data/io.github.mohammedalshimmary.asylauncher/files/prints` — protected by Android OS File-Based Encryption and kernel `0700` UID sandboxing) with full folder creation & navigation, **Rename**, **Copy**, **Move**, **Open**, **Share** (files only), and **Delete** controls via clean Apple-style icon toolbars.
  - Automatically detects when ASYCUDA opens its Java file chooser (`JFileChooser`)—with zero modification to government JARs—and prompts you on Android to either import a new file from your phone into the Vault (`Intent.ACTION_OPEN_DOCUMENT`) or select an existing file from your Vault.
- **Unified Update Center, Google Play Store Handoff & `SOClass` Alert Listener**:
  - Checks Cloud Firestore for new app releases (opening Google Play Store for verified updates with zero `REQUEST_INSTALL_PACKAGES` permission) and `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on startup and on demand.
  - Dynamically syncs the full `<jar href="..."/>` list from `AWIQWorld.jnlp`, enforcing an explicit `https://asy.mof.gov.iq/aw/*.jar` allowlist and verifying that every downloaded JAR entry is cryptographically signed by UNCTAD/SOClass (`META-INF/SOCODE.RSA`, SHA-256 `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`).
  - Automatically detects ASYCUDA server `SOClass` update alerts (`"SOClass Client have to be updated"`) and opens the Update Center immediately.
- **Zero-PII Iraqi Law Click-Wrap Liability Waiver & In-App Changelog ("What's New")**:
  - Enforces a binding first-launch electronic click-wrap Terms & Conditions and Contractual Liability Waiver governed by **Article 259(2) of the Iraqi Civil Code No. 40 of 1951** (`المادة 259/2 من القانون المدني العراقي رقم 40 لسنة 1951`) and **Article 18(1) of the Iraqi Electronic Signature and Electronic Transactions Law No. 78 of 2012** (`قانون التوقيع الإلكتروني والمعاملات الإلكترونية رقم 78 لسنة 2012`), storing only a local boolean acceptance flag (`accepted = true`) on the user's device with **zero personal names, zero company names, and zero consent receipts collected or transmitted**.
  - Displays a localized release summary after every installation or update, also accessible anytime from the About screen.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the Control Center, settings, and dialogs, plus runtime guards for ASYCUDA's internal language switcher (`VisualFacetJTableSDS`) and Selectivity Administration tab (`R_Load_SadItmAdm`).
- **Display & Input Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, live-preview sliders for window scaling and shortcut bar opacity, configurable auto-keyboard popup, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **In-App Issue Reporting, Universal Error & Network Diagnostics Window & Remote Known-Issues Catalog**:
  - **Universal Error & Network Diagnostics Window (`Proposal B + Known-Issues Catalog`)**: Automatically diagnoses network, TLS, X11, and runtime errors (probing active transport, DNS resolution, `:443` HTTPS, and the dynamic RMI port from `AWIQWorld.jnlp`), explains the cause in plain English/Arabic/Kurdish with a collapsible 100% in-RAM redacted developer trace (with `"Full RAM Preview"` and automatic zero-fill on close), and offers user-consented one-tap **Verified Fixes** (such as alerting you to turn off Wi-Fi and switch to **4G Mobile Data** when a Wi-Fi ISP is blocked by the Ministry of Finance firewall).
  - **Data-Only Known-Issues Catalog (`java-bridge/known-errors-catalog.json`)**: Pre-bundled inside `asycuda-iraq.tar.xz` and synced read-only from Google Cloud Firestore (`app_config/known_errors_catalog`), strictly validated against a hardcoded allowlist of `verifiedFixId` constants (`ALLOWED_VERIFIED_FIX_IDS`) with zero dynamic code execution, and isolated from the JVM by `AsycudaBoot.checkBlockedHostPath()`.
  - **Voluntary Issue Reports & News/Release Alerts (`Cloud Firestore`)**: Receive instant Cloud Broadcasts & Release Alerts with unread badge counters (`announcements/latest`), and submit voluntary issue reports with redacted in-memory runtime logs (assembled strictly in RAM and never written to disk) to the write-only `diagnostic_reports` collection in Google Cloud Firestore.

---

## Security, Privacy & Open-Source Transparency

This project is open-source so that customs brokers, trade companies, and security auditors can inspect every line of code:

- **Zero Credential & Zero PII Collection + Enforced JVM Network Allowlist (`checkConnect`)**: Usernames, passwords, personal/company names, and customs declaration data are never collected, stored, or routed through third-party servers. The embedded Eclipse Temurin OpenJDK `8u504-b01` LTS runtime connects directly to `https://asy.mof.gov.iq` and the dynamic RMI port parsed from `AWIQWorld.jnlp` (with zero hardcoded server IPs or fallback ports), and `AsycudaBoot`'s `SecurityManager` enforces a strict outbound network allowlist (`checkConnect`) blocking all JVM connections to any host other than `localhost` and `asy.mof.gov.iq`.
- **Verified TLS & Pinned Publisher Signatures**: Enforces full X.509 certificate chain verification (`Sectigo Root R46` / `ZeroSSL RSA DV SSL CA 2`, plus the composite MOF certificate trust store `mof.crt` via `installMofHttpsTrustStore()`) and RFC 2818 hostname verification for `asy.mof.gov.iq`, SHA-256 and APK signature checks on in-app APK updates, and pinned UNCTAD/SOClass X.509 signature verification (`SOCODE.RSA`) on every `AWIQWorld.jnlp` JAR.
- **100% Open-Source Java Bridge (`java-bridge/`)**: All custom compatibility JARs (`00_bootpatch.jar`, `jars/00_boot.jar`, `jars/00_jnlp.jar`) that bridge ASYCUDA's desktop Swing/JNLP calls to Android are published with full `.java` source code and a reproducible build script in [`java-bridge/`](java-bridge/). Official Ministry of Finance `.jar` packages are never modified.
- **Automated Adversarial Security Suite & SBOM**: Every commit runs 7 adversarial security suites in [`scripts/SecurityVerificationTest.java`](scripts/SecurityVerificationTest.java) in GitHub Actions alongside Snyk and SonarQube Cloud.
- Read the complete 4-layer architecture, Mermaid data-flow diagram, and security audit report in **[`SECURITY.md`](SECURITY.md)**.

---

## Installation

1. Download [`AsyLauncher_v4.3.0.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.3.0/AsyLauncher_v4.3.0.apk) from the [v4.3.0 Release page](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.3.0) or install from **Google Play Store**.
2. Open the APK on your Android phone or tablet to install it. If Google Play Protect prompts to scan the newly published APK binary, tap **Scan app** — the registered Android Developer Console signing certificate and hardened Stripped APK Bundle (`SAB`) verify as **"This app looks safe" ✓**.
3. On first launch, review and accept the Click-Wrap Terms & Conditions and Liability Waiver (governed by Iraqi Civil Code Art. 259/2 & Electronic Transactions Law No. 78/2012 Art. 18/1 — zero personal data collected), and complete the interactive 5-step gesture walkthrough. Zero storage permissions are required!
4. On initial run, the launcher automatically downloads the 131 official ASYCUDA World client JARs directly from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` and launches the session.

---

<div dir="rtl">

## 🇮🇶 العربية — AsyLauncher (مشغل أسيكودا) — الإصدار الرئيسي `v4.3.0`

أداة توافقية مستقلة ومفتوحة المصدر (**AsyLauncher** — الحزمة `io.github.mohammedalshimmary.asylauncher`) مصممة لتشغيل عميل سطح المكتب الجافا لنظام **أسيكودا العالمي** (`asy.mof.gov.iq`) مباشرة على هواتف وأجهزة الأندرويد اللوحية للمخلصين الكمركيين وشركات الشحن والتجارة. هذا المشروع مستقل تماماً ولا يتبع لوزارة المالية العراقية أو الهيئة العامة للكمارك أو منظمة UNCTAD.

> **⚠️ تنبيه أمني هام (تحديث رئيسي شامل `v4.3.0`)**: يُعد الإصدار **`v4.3.0`** إصداراً رئيسياً للأمان والهوية البرمجية. يُرجى التحديث إلى الإصدار **`v4.3.0`**.

### المميزات:
- **تثبيت مباشر وبدون صلاحيات تخزين واسعة**: يعمل مباشرة بعد التثبيت دون طلب صلاحية الوصول الشامل للملفات (`MANAGE_EXTERNAL_STORAGE`).
- **إيماءات اللمس المخصصة والحاسبة الكمركية المصغرة العائمة**: تخصيص نقرات الأصابع لكل من وضع لوحة اللمس ووضع اللمس المباشر، مع نافذة حاسبة كمركية عائمة قابلة لتغيير الحجم مدمجة بجانب زر `AsyLauncher` العائم، ودليل تفاعلي من ٥ خطوات عند التشغيل لأول مرة.
- **الخزنة الخاصة للمستندات وجسر استيراد الملفات الآمن**: طباعة التصاريح والوصلات مباشرة بصيغة `PDF` وحفظها حصرياً داخل المساحة المعزولة للتطبيق مع إمكانية فتحها أو مشاركتها أو حذفها.
- **مركز التحديثات الذكي وتنبيهات Cloud Firestore**: يفحص حالة إصدار التطبيق والتنبيهات الفورية عبر `Google Cloud Firestore` (في النسخ الرسمية الموقعة فقط)، ويحدث ملفات عميل الجافا (`AWIQWorld.jnlp`) من الخادم (`asy.mof.gov.iq`) تلقائياً عند التشغيل مع التحقق من التوقيع الرقمي (`SOCODE.RSA`) لكل ملف.
- **أمان وخصوصية موثقة وحصر اتصالات الجافا**: اتصال مباشر ومشفر بخادم `asy.mof.gov.iq` مع فرض جدار حماية برمجي (`SecurityManager.checkConnect`) يحصر جميع اتصالات جلسة الجافا بالخادم حصرياً، ونشر كامل الكود المصدري لملفات الربط في مجلد [`java-bridge/`](java-bridge/).
- **إعدادات العرض والإدخال ودعم اللغات**: واجهة تحكم واسم تطبيق ديناميكي يدعم **العربية (`مشغل أسيكودا`)** و**الكردية (`کارپێکەری ئەسیکۆدا`)** و**الإنجليزية (`Launcher for ASYCUDA`)** تلقائياً حسب لغة الجهاز.

</div>

---

<div dir="rtl">

## کوردی — AsyLauncher (کارپێکەری ئەسیکۆدا) — وەشانی سەرەکی `v4.3.0`

ئامرازێکی تەکنیکی سەربەخۆ و سەرچاوە کراوە (**AsyLauncher** — پاکێجی `io.github.mohammedalshimmary.asylauncher`) بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێتی ئەندرۆید، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان. ئەم پڕۆژەیە سەربەخۆیە و سەر بە وەزارەتی دارایی عێراق، دەستەی گشتیی گومرگ، یان ڕێکخراوی UNCTAD نییە.

> **⚠️ ئاگاداری ئەمنی گرنگ (وەشانی سەرەکی `v4.3.0`)**: وەشانی **`v4.3.0`** وەشانێکی سەرەکی ئاسایش و پێکهاتەی بەرنامەکەیە — تکایە نوێی بکەرەوە بۆ **`v4.3.0`**.

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ و بەبێ مۆڵەتی گشتیی پەڕگەکان**: بەبێ داواکردنی مۆڵەتی گشتیی پەڕگەکان (`MANAGE_EXTERNAL_STORAGE`).
- **ڕێکخستنی جوڵەی پەنجەکان و ژمێرەری گومرگی بچووک**: دیاریکردنی فرمانەکانی پەنجەکان بۆ تراکپاد و شاشەی لەمس، لەگەڵ ژمێرەری سەرئاوکەوتوو و فێرکاری کارلێککاری ٥ هەنگاوی.
- **قاسەی نهێنی بەرنامە و پردی هاوردەکردنی پەڕگە**: چاپکردنی بەڵگەنامەکان بە شێوەی `PDF` لەناو بیرگەی تایبەت و پارێزراوی بەرنامەکەدا.
- **ناوەندی نوێکردنەوەی زیرەک و Cloud Firestore**: پشکنینی نوێکردنەوەی بەرنامەکە لە ڕێگەی `Google Cloud Firestore` و پەڕگەکانی سیستەم (`AWIQWorld.jnlp`) لە ڕاژەی (`asy.mof.gov.iq`) بە پشتڕاستکردنەوەی واژۆی ئەلیکترۆنی (`SOCODE.RSA`).
- **پاڵپشتی زمانەکان**: ناوی بەرنامە و ڕووکاری ڕێکخستنەکان خۆکارانە دەگۆڕێت بەپێی زمانی ئامێرەکە بۆ **کوردی (`کارپێکەری ئەسیکۆدا`)**، **عەرەبی (`مشغل أسيكودا`)** و **ئینگلیزی (`Launcher for ASYCUDA`)**.

</div>

---

## Disclaimer

This application is an independent technical utility built to help customs brokers and trade companies run the ASYCUDA World client on Android devices. It is not affiliated with or officially endorsed by the Iraqi Ministry of Finance, the General Commission for Customs, or UNCTAD. The software connects directly to `asy.mof.gov.iq` using the unmodified official client packages and is provided *"as is"* without warranties of any kind.

---

## Building from Source

1. **(Optional)** Rebuild the open-source Java-to-Android bridge JARs (`00_bootpatch.jar`, `00_boot.jar`, `00_jnlp.jar`) from source:
   ```bash
   ./java-bridge/build-bridge.sh
   ```
2. Place `runtime-arm64.tar.xz` and `asycuda-iraq.tar.xz` (available on the [v4.3.0 Release](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.3.0) page) into `lorie-app/src/main/assets/`.
3. Build the APK with JDK 17 and Android SDK 35:
   ```bash
   ./gradlew :lorie-app:assembleDebug
   ```

---

## Developer & License

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).

Licensed under the [GNU General Public License v3.0](LICENSE).
