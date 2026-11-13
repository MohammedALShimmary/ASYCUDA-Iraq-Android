# AsyLauncher — Independent Android Compatibility Launcher for ASYCUDA (Unofficial)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![License: Hybrid GPLv3 + Proprietary Engine](https://img.shields.io/badge/License-Hybrid%20(GPLv3%20Host%20%2B%20Proprietary%20Engine)-blue.svg)](LICENSE)

> [!IMPORTANT]
> **INDEPENDENT, UNOFFICIAL THIRD-PARTY UTILITY — NOT A GOVERNMENT APPLICATION**
> **AsyLauncher** (`Launcher for ASYCUDA`, package `io.github.mohammedalshimmary.asylauncher`) is an independent, privately developed compatibility utility built by **Mohammed Ammar Hussein** to provide Android phones and tablets with the local X11 display server and Java 8 Swing/JNLP runtime needed to run the ASYCUDA World desktop client.
> **This application is NOT affiliated with, sponsored by, authorized by, or endorsed by the Iraqi Ministry of Finance (`وزارة المالية العراقية`), the General Commission of Customs (`الهيئة العامة للكمارك`), or the United Nations (`UNCTAD`).**
>
> **تنبيه هام (تطبيق تقني مستقل وغير رسمي):** تطبيق **AsyLauncher (`مشغل أسيكودا`)** هو أداة توافقية تقنية مستقلة ومطورة بشكل فردي لتزويد نظام أندرويد ببيئة تشغيل الجافا المفقودة، **وهو تطبيق غير حكومي ولا يتبع بأي شكل من الأشكال لوزارة المالية العراقية أو الهيئة العامة للكمارك أو منظمة مؤتمر الأمم المتحدة للتجارة والتنمية (UNCTAD) ولا يمثلها رسمياً.**

[**Download Release `v4.4.1` (`AsyLauncher_v4.4.1.apk`)**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.4.1) • [**Direct APK Download**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.4.1/AsyLauncher_v4.4.1.apk) • [**Security & Architecture (`SECURITY.md`)**](SECURITY.md) • [**Privacy Policy (`PRIVACY_POLICY.md`)**](PRIVACY_POLICY.md) • [**CycloneDX SBOM (`sbom.cdx.json`)**](sbom.cdx.json)

---

## Current Release (`v4.4.1` • `versionCode 57`)

| Release Asset | Version | SHA-256 Checksum |
| :--- | :--- | :--- |
| [**`AsyLauncher_v4.4.1.apk`**](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.4.1/AsyLauncher_v4.4.1.apk) | `v4.4.1` (`versionCode 57`) | `843d447d511fe69ac2046bf8708b1fd82ac9683ccf4ce7b0b711695cfd4f36db` |
| [**`sbom.cdx.json`**](sbom.cdx.json) | CycloneDX 1.5 SBOM | Tracked in repository root |

### Official Government & UNCTAD Information Sources
No government `.jar` packages are bundled inside the APK. Official public portals and ASYCUDA programme references:
- **Iraqi Ministry of Finance Official Website:** https://www.mof.gov.iq/
- **UNCTAD ASYCUDA Official Programme Website:** https://asycuda.org/en/
- **UNCTAD ASYCUDA User Countries Directory (Iraq Profile):** https://asycuda.org/en/user-countries/
- **Iraq Customs Client Runtime Endpoint (accessible inside Iraq):** ``https://asy.mof.gov.iq/aw/AWIQWorld.jnlp`` (strictly geo-restricted by the Ministry firewall to Iraqi IP addresses)

---

## Application Features

### 1. Printing, Exporting & File Management (Zero Storage Permissions)
- **Previous Declaration (`PD`) & Search Table Printing**: Restores the **Print** button inside the End-of-Transaction confirmation dialog for Previous Declarations (`البيان المسبق`) and fixes empty-header errors when printing **Final Declarations (`تفاصيل البيان`)** search tables.
- **Vector PDF Printer (`ASYCUDA_PDF_Printer`) & Native Excel (`.xlsx`) Exporter (`ASYCUDA_Excel_Exporter`)**:
  - Prints declarations, receipts, assessment notices, and search tables to crisp vector PDFs (Portrait & Landscape) with automatic column fitting, RTL Arabic text shaping, and full-row pagination.
  - Adds `ASYCUDA_Excel_Exporter` directly inside ASYCUDA's print dialog so you can export any search result table into an OpenXML Excel (`.xlsx`) spreadsheet (protected against `CWE-1236` formula injection).
- **Private App Vault (`Vault — File Manager`) & Automatic `JFileChooser` Bridge**:
  - All printed PDFs, Excel spreadsheets, and imported attachments are stored inside the app's private **Vault** (`/data/data/io.github.mohammedalshimmary.asylauncher/files/prints`, protected by OS File-Based Encryption and `0700` UID isolation) with full subfolder creation, **Rename**, **Copy**, **Move**, **Open**, **Share** (files only), and **Delete** controls.
  - Automatically detects when ASYCUDA opens its Java file chooser (`JFileChooser`)—without modifying government JARs—and prompts you on Android to either import a file from your device (`Intent.ACTION_OPEN_DOCUMENT`) or pick an existing file from your Vault.

### 2. Display Scaling, Touch Controls & Desktop Ergonomics
- **Live Font & UI Scaling Presets + Stability Benchmark**:
  - Choose **Default (100%)**, **Comfort (Medium)**, or **Touch (Large)** in Display Settings to enlarge ASYCUDA World's internal Swing menus, document trees, tables, buttons, and cursors across any screen density without restarting your session.
  - Includes a live **Display Stability & Clarity Benchmark** in Display Settings that reports active canvas resolution, DPI, UI preset, and frame pacing.
- **Direct Touchscreen & Virtual Trackpad Modes**:
  - Switch anytime between **Virtual Trackpad** (laptop-style cursor glide) and **Direct Touchscreen** mode, with customizable 1-, 2-, and 3-finger tap/long-press gestures and a 5-step interactive tutorial on first launch.
  - **Touch-Friendly Window Resizing & Titlebar Restore**: Expanded outer grab zones make internal Swing windows (`JInternalFrame`) easy to resize by finger drag without blocking inner scrollbars or buttons, plus double-tap titlebar restore for shaded windows.
  - **Search Criteria Dropdown Stability & Single-Tap File Selection**: Prevents premature dropdown closing and unwanted auto-keyboard popups in **Declaration Search (`بحث البيان`)** and **Previous Declaration Search (`بحث البيان المسبق`)**, and enables single-tap file selection in `JFileChooser`.
- **High-FPS (`120Hz`) & GPU Rendering + Floating Customs Mini-Calculator**:
  - Supports `60Hz–120Hz` frame pacing and hardware GPU acceleration in Display Settings, an on-screen shortcut keybar (`TAB`, `CTRL`, `ESC`, arrows), and a draggable, resizable floating **Customs Mini-Calculator** right next to the floating `AsyLauncher` button.

### 3. Direct Package Sync, Network Diagnostics & Cloud Updates
- **Zero Pre-Bundled Government JARs & Pinned `SOCODE.RSA` Verification**:
  - On first launch (and through the **Update Center**), the app downloads all 131 official client JARs in parallel from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` and verifies every entry in every JAR against UNCTAD/StrategyObject's pinned X.509 certificate (`META-INF/SOCODE.RSA`, SHA-256: `5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d`).
  - Automatically detects ASYCUDA server `SOClass` update prompts (`"SOClass Client have to be updated"`) and opens the Update Center immediately.
- **Universal Error & Network Diagnostics + Remote Known-Issues Catalog**:
  - Automatically probes active network transport, DNS resolution, `:443` HTTPS, and the dynamic JNLP RMI port when a connection error occurs, explaining the cause in plain **English**, **Arabic (العربية)**, and **Kurdish (کوردی)**.
  - Detects when a local Wi-Fi ISP is blocked by the Ministry of Finance firewall and guides you to switch to **4G Mobile Data**.
  - Syncs read-only diagnostic definitions from Google Cloud Firestore (`app_config/known_errors_catalog`) and lets you preview and voluntarily submit redacted in-RAM diagnostic reports (`"Preview Diagnostic Data"` with automatic `\0` memory zeroing on close).
- **Multi-Card Cloud Broadcasts & Verified Google Play Update Handoff**:
  - Displays up to 10 cached News & Release announcements with unread badge counters (`announcements/latest`) and hands off app updates directly to the **Google Play Store** (`market://details?id=io.github.mohammedalshimmary.asylauncher`) with zero `REQUEST_INSTALL_PACKAGES` permission.
- **Background Session Keep-Alive & Iraqi Law Click-Wrap Waiver**:
  - Keeps your customs session alive when switching briefly to WhatsApp, Authenticator, or PDF viewers.
  - Enforces a first-launch click-wrap Terms & Conditions and Liability Waiver governed by **Iraqi Civil Code No. 40 of 1951 (Art. 259/2)** and **Iraqi Electronic Transactions Law No. 78 of 2012 (Art. 18/1)**, storing only a local `accepted = true` flag on the device.

---

## Security, Privacy & Audited Hybrid Architecture

This repository publishes the complete Android X11 Host & Security Layer (`lorie/`, `lorie-app/`) so customs brokers, trade companies, and security auditors can inspect the permissions, network validators, and storage isolation:

- **Zero Credential & Zero PII Collection + Enforced JVM Network Allowlist (`checkConnect`)**: Usernames, passwords, personal/company names, and declaration data are never collected or routed through third-party servers. The embedded Eclipse Temurin OpenJDK `8u504-b01` LTS runtime connects directly to `https://asy.mof.gov.iq` and the dynamic RMI port parsed from `AWIQWorld.jnlp` (with zero hardcoded server IPs or fallback ports), and `00_boot.jar`'s `SecurityManager` blocks all JVM connections to any host other than `localhost` and `asy.mof.gov.iq`.
- **Verified TLS & Pinned Publisher Signatures**: Enforces full X.509 certificate chain verification (`Sectigo Root R46` / `ZeroSSL RSA DV SSL CA 2`, plus the composite MOF trust store `mof_cacerts.jks`) and RFC 2818 hostname verification for `asy.mof.gov.iq`, alongside pinned `SOCODE.RSA` X.509 verification on every `AWIQWorld.jnlp` JAR.
- **Proprietary R8-Hardened & String-Encrypted Java Compatibility Engine (`00_boot.jar`)**: The custom Java-to-Android compatibility engine (`00_boot.jar`, `00_bootpatch.jar`, `00_jnlp.jar`) is a proprietary, R8-obfuscated and string-encrypted component (`Copyright © 2026 Mohammed Ammar Hussein — All Rights Reserved`) bound cryptographically to official signed `AsyLauncher` builds (`verifyHostApkIntegrity()`). Official Ministry of Finance `.jar` packages are never modified.
- **Pre-Disk JVM Log Redaction & Telemetry Opt-Out**: `AsycudaStandaloneManager` scrubs JVM `stdout`/`stderr` in RAM before writing to disk (and deletes `.log` files on graceful exit), while **About, Security & Software Licences** provides a persistent opt-out toggle for the once-per-day 6-field anonymous installation heartbeat.
- **Automated 7-Suite Security Verification & SBOM**: Every commit runs [`scripts/SecurityVerificationTest.java`](scripts/SecurityVerificationTest.java) in GitHub Actions alongside Snyk and SonarQube Cloud, with [`firestore.rules`](firestore.rules) published openly for audit.
- Read the complete 4-layer architecture, Mermaid data-flow diagram, and security audit details in **[`SECURITY.md`](SECURITY.md)**.

---

## Installation

1. Download [`AsyLauncher_v4.4.1.apk`](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.4.1/AsyLauncher_v4.4.1.apk) from the [v4.4.1 Release page](https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/tag/v4.4.1) or install from **Google Play Store**.
2. Open the APK on your Android phone or tablet (requires 64-bit ARM `arm64-v8a`, Android 8.0–16+).
3. On first launch, review and accept the Click-Wrap Terms & Conditions and Liability Waiver (Iraqi Civil Code Art. 259/2 & Electronic Transactions Law No. 78/2012 Art. 18/1) and complete the 5-step gesture walkthrough.
4. On initial run (on an Iraqi internet connection), the launcher downloads the 131 official ASYCUDA World client JARs directly from the Ministry server (`asy.mof.gov.iq`) and starts the session.

---

<div dir="rtl">

## 🇮🇶 العربية — AsyLauncher (مشغل أسيكودا — تطبيق توافقي مستقل وغير رسمي) — الإصدار `v4.4.1`

أداة توافقية تقنية مستقلة وغير رسمية (**AsyLauncher** — الحزمة `io.github.mohammedalshimmary.asylauncher`) مصممة لتزويد هواتف وأجهزة الأندرويد اللوحية ببيئة الجافا اللازمة لتشغيل عميل سطح المكتب لنظام **أسيكودا العالمي** (`asy.mof.gov.iq`) للمخلصين الكمركيين وشركات الشحن والتجارة. **هذا المشروع مستقل تماماً ولا يتبع لوزارة المالية العراقية أو الهيئة العامة للكمارك أو منظمة UNCTAD.**

### المميزات الكاملة:
- **تثبيت مباشر وبدون ملفات حكومية مدمجة أو صلاحيات تخزين واسعة**: يعمل مباشرة دون طلب صلاحية الوصول الشامل للملفات (`MANAGE_EXTERNAL_STORAGE`)، ويقوم بتحميل ملفات العميل الرسمية (131 ملف `JAR`) مباشرة من `asy.mof.gov.iq` عند التشغيل الأول مع التحقق من التوقيع الرقمي (`SOCODE.RSA`) لكل ملف.
- **إصلاح طباعة البيان المسبق (PD) وتصدير الجداول إلى Excel (`.xlsx`) و PDF**: تفعيل زر الطباعة داخل نافذة إتمام العملية للبيان المسبق (`البيان المسبق`)، معالجة طباعة جدول **تفاصيل البيان**، وتصدير نتائج البحث مباشرة إلى ملف `Excel (.xlsx)` أو `PDF` عالي الدقة داخل الخزنة الخاصة.
- **تكبير الخطوط والقوائم الذكي وتحسينات اللمس المباشر (`120Hz`)**: اختيار حجم واجهة وخطوط أسيكودا (**افتراضي** / **مريح** / **كبير للمس**) مع تطبيق فوري ومؤشر حي لاستقرار الشاشة، تسهيل تغيير حجم النوافذ باللمس من الحواف والزوايا، تثبيت القوائم المنسدلة في **بحث البيان** و**بحث البيان المسبق**، واختيار الملفات بلمسة واحدة.
- **إيماءات اللمس المخصصة والحاسبة الكمركية المصغرة العائمة**: تخصيص نقرات الأصابع لكل من وضع لوحة اللمس (Trackpad) ووضع اللمس المباشر، مع حاسبة كمركية عائمة قابلة للتحريك وتغيير الحجم بجانب زر `AsyLauncher` العائم، ودليل تفاعلي من ٥ خطوات عند التشغيل لأول مرة.
- **الخزنة الخاصة للمستندات (`Vault`) وجسر استيراد الملفات التلقائي**: حفظ جميع التصاريح والوصلات المطبوعة وملفات الإكسل داخل المساحة المعزولة للتطبيق (`0700`) مع إدارة كاملة للمجلدات (**إنشاء مجلد، إعادة تسمية، نسخ، نقل، فتح، مشاركة، وحذف**)، واكتشاف نافذة اختيار الملفات (`JFileChooser`) تلقائياً لاستيراد الملفات من الهاتف بأمان.
- **تشخيص الشبكة الشامل ومركز التحديثات الذكي (`Cloud Firestore`)**: فحص تلقائي لمشاكل الاتصال وملف الشبكة وحجب مزودي خدمة الواي فاي (مع التوجيه لاستخدام بيانات الهاتف `4G` عند الحاجة)، واستلام التنبيهات الفورية وتحديثات الأخطاء المعروفة عبر `Google Cloud Firestore` مع إمكانية معاينة وإرسال تقارير الأعطال الاختيارية من الذاكرة العشوائية (`RAM`) بعد تنقيتها من أي بيانات حساسة.
- **دعم اللغات الثلاث**: واجهة تحكم واسم تطبيق ديناميكي يدعم **العربية (`مشغل أسيكودا`)** و**الكردية (`کارپێکەری ئەسیکۆدا`)** و**الإنجليزية (`Launcher for ASYCUDA`)** تلقائياً حسب لغة الجهاز.

</div>

---

<div dir="rtl">

## کوردی — AsyLauncher (کارپێکەری ئەسیکۆدا — ئامرازی سەربەخۆ و نافەرمی) — وەشانی `v4.4.1`

ئامرازێکی تەکنیکی سەربەخۆ و نافەرمی (**AsyLauncher** — پاکێجی `io.github.mohammedalshimmary.asylauncher`) بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێتی ئەندرۆید، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان. **ئەم پڕۆژەیە سەربەخۆیە و سەر بە وەزارەتی دارایی عێراق، دەستەی گشتیی گومرگ، یان ڕێکخراوی UNCTAD نییە.**

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ و بەبێ مۆڵەتی گشتیی پەڕگەکان**: کاردەکات بەبێ داواکردنی مۆڵەتی گشتیی پەڕگەکان (`MANAGE_EXTERNAL_STORAGE`) و هەموو پەڕگە فەرمییەکان (131 پەڕگەی `JAR`) ڕاستەوخۆ لە `asy.mof.gov.iq` دادەگرێت بە پشتڕاستکردنەوەی واژۆی ئەلیکترۆنی (`SOCODE.RSA`).
- **چاککردنی چاپکردنی بەیانی پێشوەختە (PD) و هەناردەکردنی خشتەکان بۆ Excel (`.xlsx`) و PDF**: چالاککردنی دوگمەی چاپکردنی بەیانی پێشوەختە و هەناردەکردنی ئەنجامەکانی گەڕان بۆ پەڕگەی `Excel` و `PDF` لەناو قاسەی تایبەتی بەرنامەکەدا.
- **گەورەکردنی فۆنت و لیستەکانی ئەسیکۆدا و باشترکردنی شاشەی لەمس (`120Hz`)**: هەڵبژاردنی قەبارەی فۆنت (**بنەڕەتی** / **مامناوەند** / **گەورە بۆ لەمس**)، ئاسانکردنی گۆڕینی قەبارەی پەنجەرەکان، جێگیرکردنی لیستەکانی گەڕان، و هەڵبژاردنی پەڕگەکان بە یەک دەستلێدان.
- **ڕێکخستنی جوڵەی پەنجەکان و ژمێرەری گومرگی بچووک**: دیاریکردنی فرمانەکانی پەنجەکان بۆ تراکپاد و شاشەی لەمس، لەگەڵ ژمێرەری سەرئاوکەوتوو و فێرکاری کارلێککاری ٥ هەنگاوی.
- **قاسەی نهێنی بەرنامە (`Vault`) و پردی هاوردەکردنی پەڕگە**: چاپکردن و بەڕێوەبردنی بەڵگەنامەکان (فۆڵدەر، گۆڕینی ناو، کۆپی، گواستنەوە، کردنەوە، هاوبەشکردن، سڕینەوە) لەناو بیرگەی تایبەت و پارێزراوی بەرنامەکەدا.
- **پشکنینی تۆڕ و ناوەندی نوێکردنەوەی زیرەک (`Cloud Firestore`)**: دەستنیشانکردنی کێشەکانی هێڵی ئینتەرنێت و وەرگرتنی ئاگادارییەکان لە ڕێگەی `Google Cloud Firestore`.
- **پاڵپشتی زمانەکان**: ناوی بەرنامە و ڕووکاری ڕێکخستنەکان خۆکارانە دەگۆڕێت بەپێی زمانی ئامێرەکە بۆ **کوردی (`کارپێکەری ئەسیکۆدا`)**، **عەرەبی (`مشغل أسيكودا`)** و **ئینگلیزی (`Launcher for ASYCUDA`)**.

</div>

---

## Disclaimer & Non-Affiliation Notice

**AsyLauncher (`Launcher for ASYCUDA`)** is an independent, privately developed technical utility built to help customs brokers and trade companies run the ASYCUDA World desktop client on Android devices. **It is NOT affiliated with, sponsored by, or officially endorsed by the Iraqi Ministry of Finance, the General Commission for Customs, or UNCTAD.** The software connects directly to `asy.mof.gov.iq` using the unmodified official client packages and is provided *"as is"* without warranties of any kind.

---

## Architecture & Licensing

- **Android X11 Host & UI Layer (`lorie/`, `lorie-app/`)**: Licensed under the [GNU General Public License v3.0](LICENSE) (incorporating Termux:X11 `libXlorie.so`).
- **AsyLauncher Java Compatibility Engine (`00_boot.jar`, `00_bootpatch.jar`, `00_jnlp.jar`)**: **Proprietary Closed-Source Freeware** (`Copyright © 2026 Mohammed Ammar Hussein — All Rights Reserved`). Provided free of charge for end-users inside official signed releases of `AsyLauncher`. Reverse engineering, decompilation, extraction, modification, or unauthorized redistribution of the proprietary Java Bridge binaries is strictly prohibited.

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).
