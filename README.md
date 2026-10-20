# ASYCUDA World — Republic of Iraq (Android)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Android client for the Iraqi General Commission of Customs (**ASYCUDA World** — `https://asy.mof.gov.iq`), built for customs brokers, clearing agents, and logistics operators in Iraq.

[**Download Latest Release `v3.9.5` (`ASYCUDA_Iraq_v3.9.5.apk`)**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.9.5) • [**Direct APK Download**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.9.5/ASYCUDA_Iraq_v3.9.5.apk) • [**Security & Privacy Policy (`SECURITY.md`)**](SECURITY.md) • [**Java Bridge Source (`java-bridge/`)**](java-bridge/) • [**CycloneDX SBOM (`sbom.cdx.json`)**](sbom.cdx.json)

---

## Latest Official Release: [`v3.9.5` (`versionCode 40`)](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.9.5)

| Release Asset | SHA-256 Checksum |
| :--- | :--- |
| [**`ASYCUDA_Iraq_v3.9.5.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.9.5/ASYCUDA_Iraq_v3.9.5.apk) | `36aba646d88f0424f1aec2c83b077fb3df2689ff0f91cd49d588dcf2cb967b7e` |
| [**`asycuda-iraq.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.9.5/asycuda-iraq.tar.xz) | `31fd49b386f365a3c9874bcd8620e63363b93bbc97131d866fd42e25e51624ee` |
| [**`runtime-arm64.tar.xz`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.9.5/runtime-arm64.tar.xz) | `f3dc78325792ea764e82d108b54dfc6fe4d6d9fd752bafc83bb780403286b9c1` |

---

## Features

- **Direct Installation & Zero Storage Permissions**: Installs as a standard Android APK on 64-bit ARM (`arm64-v8a`) phones and tablets running Android 8.0 or newer with **zero broad storage or accessibility permissions** (`MANAGE_EXTERNAL_STORAGE`, `READ/WRITE_EXTERNAL_STORAGE`, and `BIND_ACCESSIBILITY_SERVICE` removed).
- **Unified Encrypted Vault (File Manager) & Automatic File Picker Bridge**:
  - Prints customs declarations, receipts, and assessment notices directly to PDF inside the app's **Encrypted Private Vault** (`/data/data/iq.asycuda/files/prints` — inaccessible to any other app on your phone) with full folder creation & navigation, **Copy**, **Move**, **Open**, **Share** (files only), and **Delete** controls via clean Apple-style icon toolbars.
  - Automatically detects when ASYCUDA opens its Java file chooser (`JFileChooser`)—with zero modification to government JARs—and prompts you on Android to either import a new file from your phone into the Vault (`Intent.ACTION_OPEN_DOCUMENT`) or select an existing file from your Vault.
- **Unified Update Center, Auto-Cleaned Update Cache & `SOClass` Alert Listener**:
  - Checks both GitHub for new app releases (with SHA-256 and APK signature verification, downloading into private `cache/updates/` and automatically deleting the `.apk` after installation) and `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on startup and on demand.
  - Dynamically syncs the full `<jar href="..."/>` list from `AWIQWorld.jnlp`, enforcing an explicit `https://asy.mof.gov.iq/aw/*.jar` allowlist and verifying that every downloaded JAR entry is cryptographically signed by UNCTAD/SOClass (`META-INF/SOCODE.RSA`, SHA-256 `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`).
  - Automatically detects ASYCUDA server `SOClass` update alerts (`"SOClass Client have to be updated"`) and opens the Update Center immediately.
- **Previewable Legal Consent Receipt & In-App Changelog ("What's New")**:
  - Includes a **"Preview Sentry Receipt Before Sending"** button inside the first-launch Terms & Privacy Policy modal so users can inspect the exact 8-field JSON receipt before accepting.
  - Displays a localized release summary after every installation or update, also accessible anytime from the About screen.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the Control Center, settings, and dialogs.
- **Display & Input Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, live-preview sliders for window scaling and shortcut bar opacity, configurable auto-keyboard popup, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **In-App Issue Reporting & Diagnostics**: Submit voluntary issue reports with redacted in-memory runtime logs (assembled strictly in RAM and never written to disk) directly from the Control Center.

---

## Security, Privacy & Open-Source Transparency

This project is open-source so that customs brokers, trade companies, and security auditors can inspect every line of code:

- **Zero Credential Collection**: Usernames, passwords, and customs declaration data are never collected, stored, or routed through third-party servers. The embedded Java 8 runtime connects directly to `https://asy.mof.gov.iq` and `//asy.mof.gov.iq:2006/`.
- **Verified TLS & Pinned Publisher Signatures**: Enforces full X.509 certificate chain verification (`Sectigo Root R46` / `ZeroSSL RSA DV SSL CA 2`) and RFC 2818 hostname verification for `asy.mof.gov.iq`, SHA-256 and APK signature checks on in-app APK updates, and pinned UNCTAD/SOClass X.509 signature verification (`SOCODE.RSA`) on every `AWIQWorld.jnlp` JAR.
- **100% Open-Source Java Bridge (`java-bridge/`)**: All custom compatibility JARs (`00_bootpatch.jar`, `jars/00_boot.jar`, `jars/00_jnlp.jar`) that bridge ASYCUDA's desktop Swing/JNLP calls to Android are published with full `.java` source code and a reproducible build script in [`java-bridge/`](java-bridge/). Official Ministry of Finance `.jar` packages are never modified.
- **Automated Adversarial Security Suite & SBOM**: Every commit runs [`scripts/SecurityVerificationTest.java`](scripts/SecurityVerificationTest.java) in GitHub Actions alongside Snyk and SonarQube Cloud.
- Read the complete 4-layer architecture, Mermaid data-flow diagram, and security audit report in **[`SECURITY.md`](SECURITY.md)**.

---

## Installation

1. Download [`ASYCUDA_Iraq_v3.9.5.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v3.9.5/ASYCUDA_Iraq_v3.9.5.apk) from the [v3.9.5 Release page](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.9.5) (or use **Check for Updates** inside the app if you are upgrading from an earlier version).
2. Open the APK on your Android phone or tablet to install it.
3. On first launch, review the Terms & Conditions and Privacy Policy (you can tap **"Preview Sentry Receipt Before Sending"** to inspect the exact 8-field receipt), and view the **What's New** summary. Zero storage permissions are required!
4. Wait ~10–15 seconds on the first run while the client unpacks its libraries and connects to the customs server.

> **Google Play Protect note**: Since this app is distributed directly on GitHub outside the Google Play Store, Android may display an *"Unrecognized app developer"* prompt during installation. Tap **More details → Install anyway** to continue.

---

<div dir="rtl">

## 🇮🇶 العربية — نظام أسيكودا العالمي (جمهورية العراق)

تطبيق أندرويد مفتوح المصدر لتشغيل نظام **أسيكودا العالمي** التابع للهيئة العامة للكمارك العراقية (`asy.mof.gov.iq`) مباشرة على الهواتف والأجهزة اللوحية، وموجه للمخلصين الكمركيين وشركات الشحن والتجارة.

### المميزات:
- **تثبيت مباشر وبدون صلاحيات تخزين واسعة**: يعمل مباشرة بعد تثبيت ملف `APK` دون طلب صلاحية الوصول الشامل للملفات (`MANAGE_EXTERNAL_STORAGE`).
- **الخزنة المشفرة للتطبيق وجسر استيراد الملفات الآمن**: طباعة التصاريح والوصلات الكمركية مباشرة بصيغة `PDF` وحفظها حصرياً داخل المساحة المشفرة المعزولة للتطبيق مع إمكانية فتحها أو مشاركتها أو حذفها، ووجود زر **«استيراد ملف من الهاتف»** داخل نافذة اختيار ملفات الجافا لفتح مستعرض ملفات الأندرويد الآمن مباشرة.
- **مركز التحديثات الذكي ومستمع تنبيهات `SOClass`**: يفحص ويحدث كلاً من إصدار التطبيق وملفات النظام (`AWIQWorld.jnlp`) من خادم وزارة المالية تلقائياً عند التشغيل مع التحقق من التوقيع الرقمي (`SOCODE.RSA`) لكل ملف، وحذف ملف تحديث التطبيق تلقائياً فور تثبيته.
- **أمان وخصوصية موثقة**: اتصال مباشر ومشفر بخادم `asy.mof.gov.iq` دون جمع أو تخزين أي بيانات دخول أو تصاريح كمركية، مع إمكانية معاينة إيصال الموافقة القانونية قبل إرساله ونشر كامل الكود المصدري لملفات الربط في مجلد [`java-bridge/`](java-bridge/).
- **إعدادات العرض والإدخال ودعم اللغات**: واجهة تحكم وإعدادات تدعم **العربية** و**الكردية** و**الإنجليزية** مع التحكم بحجم الشاشة وشفافية شريط الاختصارات.

</div>

---

<div dir="rtl">

## کوردی — سیستەمی ئەسیکۆدای جیهانی (کۆماری عێراق)

بەرنامەی ئەندرۆیدی سەرچاوە کراوە بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** سەر بە دەستەی گشتیی گومرگی عێراق (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێت، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان.

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
2. Place `runtime-arm64.tar.xz` and `asycuda-iraq.tar.xz` (available on the [v3.9.5 Release](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v3.9.5) page) into `lorie-app/src/main/assets/`.
3. Build the APK with JDK 17 and Android SDK 34:
   ```bash
   ./gradlew :lorie-app:assembleDebug
   ```

---

## Developer & License

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).

Licensed under the [GNU General Public License v3.0](LICENSE).
