# ASYCUDA World — Republic of Iraq (Android)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Android client for the Iraqi General Commission of Customs (**ASYCUDA World** — `https://asy.mof.gov.iq`), built for customs brokers, clearing agents, and logistics operators in Iraq.

[**Download Major Release `v4.2.0` (`ASYCUDA_Iraq_v4.2.0.apk`)**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.2.0) • [**Direct APK Download**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.2.0/ASYCUDA_Iraq_v4.2.0.apk) • [**Security & Privacy Policy (`SECURITY.md`)**](SECURITY.md) • [**Java Bridge Source (`java-bridge/`)**](java-bridge/) • [**CycloneDX SBOM (`sbom.cdx.json`)**](sbom.cdx.json)

---

## Latest Official Major Release: [`v4.2.0` (`versionCode 50`)](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.2.0)

> [!CAUTION]
> **MAJOR SECURITY & ARCHITECTURE RELEASE — ALL PREVIOUS RELEASES (`< v4.2.0`) ARE UNSAFE & NOT RECOMMENDED**
> Release **`v4.2.0`** is the official **Major Security & Architecture Release** introducing Dynamic Host & Port Discovery (zero hardcoded IPs or ports), Universal Network Diagnostics & On-Demand Known-Issues Catalog, Composite MOF TLS Certificate Trust (`mof_cacerts.jks`), Hardened Fail-Closed JVM Sandboxing, One-Way Clipboard Paste, Google Play Protect Stripped APK Bundle (`SAB`) Hardening (zero package-installer or battery-exemption permissions), and Startup Cryptographic Stub-JAR Verification. **All earlier releases (`v4.1.0` experimental, `v4.0.5`, `v4.0.0`, `v3.9.5`, `v3.9.0`, `v3.8.0`, `v3.7.0`, and `v3.6.0`) are now unsafe, deprecated, and strictly not recommended for production use.** Please upgrade immediately to **`v4.2.0`**.

| Release Asset | SHA-256 Checksum |
| :--- | :--- |
| [**`ASYCUDA_Iraq_v4.2.0.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.2.0/ASYCUDA_Iraq_v4.2.0.apk) | `4cb3f5308e4657280a3ab61f9d696838dbe7797a91e379ba7643e14a93ed1497` |
| [**`asycuda-iraq.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.2.0/asycuda-iraq.tar.xz) | `b6eeecab3a0c153d937db0454894216e7e90fd938ab73de8dcf29fa2a3bbb056` |
| [**`runtime-arm64.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.2.0/runtime-arm64.tar.xz) | `f3dc78325792ea764e82d108b54dfc6fe4d6d9fd752bafc83bb780403286b9c1` |

---

## Features

- **Zero Pre-Bundled Government JARs & Direct First-Launch Sync**: Zero Ministry of Finance / UNCTAD `.jar` packages are bundled inside the APK. On initial launch, the app downloads all 131 official client JARs directly from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` in parallel and cryptographically verifies every JAR entry against UNCTAD's pinned `META-INF/SOCODE.RSA` X.509 certificate.
- **Android 14 (`targetSdkVersion 34`), Verified Developer Certificate & Zero Storage Permissions**: Installs as a standard Android APK on 64-bit ARM (`arm64-v8a`) phones and tablets running Android 8.0–14+ with a verified Android Developer Console signing certificate (`SHA-256: d3:02:d4:d0:...`) and **zero broad storage or accessibility permissions** (`MANAGE_EXTERNAL_STORAGE`, `READ/WRITE_EXTERNAL_STORAGE`, and `BIND_ACCESSIBILITY_SERVICE` removed).
- **Unified Private App Vault (File Manager) & Automatic File Picker Bridge**:
  - Prints customs declarations, receipts, and assessment notices directly to PDF inside the app's **Private Vault** (`/data/data/iq.asycuda/files/prints` — protected by Android OS File-Based Encryption and kernel `0700` UID sandboxing) with full folder creation & navigation, **Rename**, **Copy**, **Move**, **Open**, **Share** (files only), and **Delete** controls via clean Apple-style icon toolbars.
  - Automatically detects when ASYCUDA opens its Java file chooser (`JFileChooser`)—with zero modification to government JARs—and prompts you on Android to either import a new file from your phone into the Vault (`Intent.ACTION_OPEN_DOCUMENT`) or select an existing file from your Vault.
- **Unified Update Center, Auto-Cleaned Update Cache & `SOClass` Alert Listener**:
  - Checks both GitHub for new app releases (with SHA-256 and APK signature verification, downloading into private `cache/updates/` and automatically deleting the `.apk` after installation) and `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on startup and on demand.
  - Dynamically syncs the full `<jar href="..."/>` list from `AWIQWorld.jnlp`, enforcing an explicit `https://asy.mof.gov.iq/aw/*.jar` allowlist and verifying that every downloaded JAR entry is cryptographically signed by UNCTAD/SOClass (`META-INF/SOCODE.RSA`, SHA-256 `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`).
  - Automatically detects ASYCUDA server `SOClass` update alerts (`"SOClass Client have to be updated"`) and opens the Update Center immediately.
- **Zero-PII Iraqi Law Click-Wrap Liability Waiver & In-App Changelog ("What's New")**:
  - Enforces a binding first-launch electronic click-wrap Terms & Conditions and Contractual Liability Waiver governed by **Article 259(2) of the Iraqi Civil Code No. 40 of 1951** (`المادة 259/2 من القانون المدني العراقي رقم 40 لسنة 1951`) and **Article 18(1) of the Iraqi Electronic Signature and Electronic Transactions Law No. 78 of 2012** (`قانون التوقيع الإلكتروني والمعاملات الإلكترونية رقم 78 لسنة 2012`), storing only a local boolean acceptance flag (`accepted = true`) on the user's device with **zero personal names, zero company names, and zero consent receipts collected or transmitted**.
  - Displays a localized release summary after every installation or update, also accessible anytime from the About screen.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the Control Center, settings, and dialogs, plus runtime guards for ASYCUDA's internal language switcher (`VisualFacetJTableSDS`) and Selectivity Administration tab (`R_Load_SadItmAdm`).
- **Display & Input Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, live-preview sliders for window scaling and shortcut bar opacity, configurable auto-keyboard popup, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **In-App Issue Reporting, Universal Error & Network Diagnostics Window & Remote Known-Issues Catalog**:
  - **Universal Error & Network Diagnostics Window (`Proposal B + Sentry Catalog`)**: Automatically diagnoses network, TLS, X11, and runtime errors (probing active transport, DNS resolution, `:443` HTTPS, and the dynamic RMI port from `AWIQWorld.jnlp`), explains the cause in plain English/Arabic/Kurdish with a collapsible 100% in-RAM redacted developer trace (with `"Full RAM Preview"` and automatic zero-fill on close), and offers user-consented one-tap **Verified Fixes** (such as alerting you to turn off Wi-Fi and switch to **4G Mobile Data** when a Wi-Fi ISP is blocked by the Ministry of Finance firewall).
  - **Data-Only Known-Issues Catalog (`java-bridge/known-errors-catalog.json`)**: Pre-bundled inside `asycuda-iraq.tar.xz` and synced on demand via the GitHub Contents API (`api.github.com`, `ETag` cached, `<= 256 KB`) when requested by the user inside the Universal Error & Network Diagnostics modal, strictly validated against a hardcoded allowlist of `verifiedFixId` constants (`ALLOWED_VERIFIED_FIX_IDS`) with zero dynamic code execution, and isolated from the JVM by `AsycudaBoot.checkBlockedHostPath()`.
  - **Voluntary Issue Reports (Optional Screenshot & Private Developer-Only Attachments)**: Submit voluntary issue reports with redacted in-memory runtime logs (assembled strictly in RAM and never written to disk) and an **optional** screenshot (with an explicit orange/yellow UI privacy warning). When mirrored to GitHub Issues via `.github/workflows/sentry-to-github-issues.yml`, **attachments and screenshots are never uploaded publicly** — GitHub Issues contain only the scrubbed text summary and a private developer-only Sentry dashboard link.

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

1. Download [`ASYCUDA_Iraq_v4.2.0.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.2.0/ASYCUDA_Iraq_v4.2.0.apk) from the [v4.2.0 Release page](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.2.0) (or use **Check for Updates** inside the app if you are upgrading from an earlier version).
2. Open the APK on your Android phone or tablet to install it. If Google Play Protect prompts to scan the newly published APK binary, tap **Scan app** — the registered Android Developer Console signing certificate and hardened Stripped APK Bundle (`SAB`) verify as **"This app looks safe" ✓**.
3. On first launch, review and accept the Click-Wrap Terms & Conditions and Liability Waiver (governed by Iraqi Civil Code Art. 259/2 & Electronic Transactions Law No. 78/2012 Art. 18/1 — zero personal data collected), and view the **What's New** summary. Zero storage permissions are required!
4. On initial run, the client automatically downloads the 131 official ASYCUDA World JARs directly from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` and launches the customs desktop.

---

<div dir="rtl">

## 🇮🇶 العربية — نظام أسيكودا العالمي (جمهورية العراق) — الإصدار الرئيسي `v4.2.0`

تطبيق أندرويد مفتوح المصدر لتشغيل نظام **أسيكودا العالمي** التابع للهيئة العامة للكمارك العراقية (`asy.mof.gov.iq`) مباشرة على الهواتف والأجهزة اللوحية، وموجه للمخلصين الكمركيين وشركات الشحن والتجارة.

> **⚠️ تنبيه أمني هام (تحديث رئيسي شامل `v4.2.0`)**: يُعد الإصدار **`v4.2.0`** إصداراً رئيسياً للأمان والبنية البرمجية. **جميع الإصدارات السابقة (`< v4.2.0` بما فيها `v4.1.0` التجريبي و`v4.0.5` وما قبله) أصبحت غير آمنة وملغاة ولا يُنصح باستخدامها نهائياً** — يُرجى التحديث فوراً إلى الإصدار **`v4.2.0`**.

### المميزات:
- **تثبيت مباشر وبدون صلاحيات تخزين واسعة**: يعمل مباشرة بعد تثبيت ملف `APK` دون طلب صلاحية الوصول الشامل للملفات (`MANAGE_EXTERNAL_STORAGE`).
- **الخزنة المشفرة للتطبيق وجسر استيراد الملفات الآمن**: طباعة التصاريح والوصلات الكمركية مباشرة بصيغة `PDF` وحفظها حصرياً داخل المساحة المشفرة المعزولة للتطبيق مع إمكانية فتحها أو مشاركتها أو حذفها، ووجود زر **«استيراد ملف من الهاتف»** داخل نافذة اختيار ملفات الجافا لفتح مستعرض ملفات الأندرويد الآمن مباشرة.
- **مركز التحديثات الذكي ومستمع تنبيهات `SOClass`**: يفحص ويحدث كلاً من إصدار التطبيق وملفات النظام (`AWIQWorld.jnlp`) من خادم وزارة المالية تلقائياً عند التشغيل مع التحقق من التوقيع الرقمي (`SOCODE.RSA`) لكل ملف، وحذف ملف تحديث التطبيق تلقائياً فور تثبيته.
- **أمان وخصوصية موثقة وحصر اتصالات الجافا**: اتصال مباشر ومشفر بخادم `asy.mof.gov.iq` مع فرض جدار حماية برمجي (`SecurityManager.checkConnect`) يحصر جميع اتصالات الجافا بالخادم الحكومي حصرياً، ونافذة تشخيص ذكية تنبه المستخدم لإيقاف الواي فاي واستخدام بيانات الهاتف (`4G`) عند حجب شبكة `Wi-Fi` من جدار حماية الكمارك، وإخلاء مسؤولية إلكتروني محلي وفق القانون المدني العراقي (م 259/2) وقانون التوقيع الإلكتروني (رقم 78 لسنة 2012) دون جمع أو إرسال أي أسماء شخصية أو أسماء شركات، ونشر كامل الكود المصدري لملفات الربط في مجلد [`java-bridge/`](java-bridge/).
- **إعدادات العرض والإدخال ودعم اللغات**: واجهة تحكم وإعدادات تدعم **العربية** و**الكردية** و**الإنجليزية** مع التحكم بحجم الشاشة وشفافية شريط الاختصارات.

</div>

---

<div dir="rtl">

## کوردی — سیستەمی ئەسیکۆدای جیهانی (کۆماری عێراق) — وەشانی سەرەکی `v4.2.0`

بەرنامەی ئەندرۆیدی سەرچاوە کراوە بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** سەر بە دەستەی گشتیی گومرگی عێراق (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێت، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان.

> **⚠️ ئاگاداری ئەمنی گرنگ (وەشانی سەرەکی `v4.2.0`)**: وەشانی **`v4.2.0`** وەشانێکی سەرەکی ئاسایش و پێکهاتەی بەرنامەکەیە. **گشت وەشانەکانی پێشتر (`< v4.2.0` بە `v4.0.5` و کۆنترەوە) چیتر سەلامەت نین و پێشنیار ناکرێن** — تکایە دەستبەجێ نوێی بکەرەوە بۆ **`v4.2.0`**.

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ و بەبێ مۆڵەتی گشتیی پەڕگەکان**: بە یەک پەڕگەی `APK` دادەمەزرێت بەبێ داواکردنی مۆڵەتی گشتیی پەڕگەکان (`MANAGE_EXTERNAL_STORAGE`).
- **قاسەی نهێنی بەرنامە و پردی هاوردەکردنی پەڕگە**: چاپکردنی بەڵگەنامە و پسوولە گومرگییەکان بە شێوەی `PDF` لەناو بیرگەی تایبەت و پارێزراوی بەرنامەکەدا لەگەڵ توانای کردنەوە، هاوبەشکردن و سڕینەوە.
- **ناوەندی نوێکردنەوەی زیرەک و گوێگری `SOClass`**: پشکنین و نوێکردنەوەی بەرنامەکە و پەڕگەکانی سیستەم (`AWIQWorld.jnlp`) ڕاستەوخۆ لە سێرڤەری وەزارەتی دارایی بە پشتڕاستکردنەوەی واژۆی ئەلیکترۆنی (`SOCODE.RSA`).
- **پاڵپشتی زمانەکان**: ڕووکاری بەرنامە و ڕێکخستنەکان پاڵپشتی زمانی **کوردی**، **عەرەبی** و **ئینگلیزی** دەکات.

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
2. Place `runtime-arm64.tar.xz` and `asycuda-iraq.tar.xz` (available on the [v4.2.0 Release](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.2.0) page) into `lorie-app/src/main/assets/`.
3. Build the APK with JDK 17 and Android SDK 34:
   ```bash
   ./gradlew :lorie-app:assembleDebug
   ```

---

## Developer & License

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).

Licensed under the [GNU General Public License v3.0](LICENSE).
