# ASYCUDA World — Republic of Iraq (Android)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MohammedALShimmary_ASYCUDA-Iraq-Android&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Android client for the Iraqi General Commission of Customs (**ASYCUDA World** — `https://asy.mof.gov.iq`), built for customs brokers, clearing agents, and logistics operators in Iraq.

[**Download Latest APK (v3.8.0)**](../../releases/latest) • [**Security & Privacy Policy (`SECURITY.md`)**](SECURITY.md) • [**Java Bridge Source (`java-bridge/`)**](java-bridge/)

---

## Features

- **Direct Installation**: Installs as a standard Android APK on 64-bit ARM (`arm64-v8a`) phones and tablets running Android 8.0 or newer.
- **Unified Update Center & `SOClass` Alert Listener**:
  - Checks both GitHub for new app releases (with SHA-256 and APK signature verification) and `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on startup and on demand.
  - Dynamically syncs the full `<jar href="..."/>` list from `AWIQWorld.jnlp` (supporting any number of server packages and removing deprecated files).
  - Automatically detects ASYCUDA server `SOClass` update alerts (`"SOClass Client have to be updated"`) and opens the Update Center immediately.
- **PDF Printing & File Manager**: Prints customs declarations, receipts, and assessment notices directly to PDF (`Download/Asycuda_Prints`) with single and multi-file sharing and deletion, and opens Android's native file picker when attaching documents.
- **In-App Changelog ("What's New")**: Displays a localized release summary after every installation or update, also accessible anytime from the About screen.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the Control Center, settings, and dialogs.
- **Display & Input Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, live-preview sliders for window scaling and shortcut bar opacity, configurable auto-keyboard popup, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **In-App Issue Reporting & Diagnostics**: Submit issue reports with screenshots and redacted runtime logs directly from the Control Center, or export diagnostic logs as a `.zip` file.

---

## Security, Privacy & Open-Source Transparency

This project is open-source so that customs brokers, trade companies, and security auditors can inspect every line of code:

- **Zero Credential Collection**: Usernames, passwords, and customs declaration data are never collected, stored, or routed through third-party servers. The embedded Java 8 runtime connects directly to `https://asy.mof.gov.iq` and `//asy.mof.gov.iq:2006/`.
- **Verified TLS & Update Integrity**: Enforces full X.509 certificate chain verification (`Sectigo Root R46` / `ZeroSSL RSA DV SSL CA 2`) and RFC 2818 hostname verification for `asy.mof.gov.iq`, plus SHA-256 and APK signature checks on in-app updates.
- **100% Open-Source Java Bridge (`java-bridge/`)**: All custom compatibility JARs (`00_bootpatch.jar`, `jars/00_boot.jar`, `jars/00_jnlp.jar`) that bridge ASYCUDA's desktop Swing/JNLP calls to Android are published with full `.java` source code and a reproducible build script in [`java-bridge/`](java-bridge/). Official Ministry of Finance `.jar` packages are never modified.
- Read the complete security architecture and audit report in **[`SECURITY.md`](SECURITY.md)**.

---

## Installation

1. Download `ASYCUDA_Iraq_v3.8.0.apk` from the [Releases](../../releases/latest) page (or use **Check for Updates** inside the app if you are upgrading from an earlier version).
2. Open the APK on your Android phone or tablet to install it.
3. On first launch, review and accept the Terms & Conditions, view the **What's New** summary, and grant file access when prompted so the app can save printed PDFs and attach declaration files.
4. Wait ~10–15 seconds on the first run while the client unpacks its libraries and connects to the customs server.

> **Google Play Protect note**: Since this app is distributed directly on GitHub outside the Google Play Store, Android may display an *"Unrecognized app developer"* prompt during installation. Tap **More details → Install anyway** to continue.

---

<div dir="rtl">

## 🇮🇶 العربية — نظام أسيكودا العالمي (جمهورية العراق)

تطبيق أندرويد مفتوح المصدر لتشغيل نظام **أسيكودا العالمي** التابع للهيئة العامة للكمارك العراقية (`asy.mof.gov.iq`) مباشرة على الهواتف والأجهزة اللوحية، وموجه للمخلصين الكمركيين وشركات الشحن والتجارة.

### المميزات:
- **تثبيت مباشر**: يعمل مباشرة بعد تثبيت ملف `APK` دون الحاجة لأي تطبيقات أو إضافات خارجية.
- **مركز التحديثات الذكي ومستمع تنبيهات `SOClass`**: يفحص ويحدث كلاً من إصدار التطبيق وملفات النظام (`AWIQWorld.jnlp`) من خادم وزارة المالية تلقائياً عند التشغيل، ويستجيب فوراً لتنبيهات تحديث `SOClass` الصادرة من الخادم.
- **أمان وخصوصية موثقة**: اتصال مباشر ومشفر بخادم `asy.mof.gov.iq` دون جمع أو تخزين أي بيانات دخول أو تصاريح كمركية، مع نشر كامل الكود المصدري لملفات الربط في مجلد [`java-bridge/`](java-bridge/).
- **سجل التغييرات داخل التطبيق**: عرض ملخص التحديثات والمزايا الجديدة تلقائياً بعد كل تحديث أو تثبيت.
- **طباعة وإدارة ملفات PDF**: طباعة التصاريح والوصلات الكمركية مباشرة بصيغة `PDF` وحفظها في مجلد `Download/Asycuda_Prints` مع إمكانية مشاركتها أو حذفها (مفرداً أو بتحديد متعدد).
- **إعدادات العرض والإدخال**: التحكم بحجم الشاشة وشفافية شريط الاختصارات بمعاينة فورية، مع خيار تفعيل أو إيقاف الظهور التلقائي للوحة المفاتيح.
- **دعم اللغات**: واجهة تحكم وإعدادات تدعم **العربية** و**الكردية** و**الإنجليزية**.
- **ثبات الاتصال في الخلفية**: يحافظ على جلسة العمل والاتصال بالإنترنت عند التنقل بين التطبيقات الأخرى على الهاتف.

</div>

---

<div dir="rtl">

## کوردی — سیستەمی ئەسیکۆدای جیهانی (کۆماری عێراق)

بەرنامەی ئەندرۆیدی سەرچاوە کراوە بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** سەر بە دەستەی گشتیی گومرگی عێراق (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێت، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان.

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ**: بە یەک پەڕگەی `APK` دادەمەزرێت بەبێ پێویستی بە هیچ بەرنامەیەکی دەرەکی.
- **ناوەندی نوێکردنەوەی زیرەک و گوێگری `SOClass`**: پشکنین و نوێکردنەوەی بەرنامەکە و پەڕگەکانی سیستەم (`AWIQWorld.jnlp`) ڕاستەوخۆ لە سێرڤەری وەزارەتی دارایی، لەگەڵ کردنەوەی خۆکارانەی پەنجەرەی نوێکردنەوە لە کاتی ئاگادارکردنەوەی `SOClass`.
- **ئاسایش و تایبەتمەندی پشتڕاستکراوە**: پەیوەندی ڕاستەوخۆ و پارێزراو لەگەڵ `asy.mof.gov.iq` بەبێ کۆکردنەوە یان هەڵگرتنی زانیارییەکانی چوونەژوورەوە، لەگەڵ بڵاوکردنەوەی تەواوی کۆدی سەرچاوەی پردی جاڤا لە [`java-bridge/`](java-bridge/).
- **تۆماری گۆڕانکارییەکان لەناو بەرنامەدا**: پیشاندانی پوختەی نوێکارییەکان دوای هەر دامەزراندن یان نوێکردنەوەیەک.
- **چاپکردن و بەڕێوەبردنی PDF**: چاپکردنی بەڵگەنامە و پسوولە گومرگییەکان بە شێوەی `PDF` لەناو فۆڵدەری `Download/Asycuda_Prints` لەگەڵ توانای هاوبەشکردن و سڕینەوە.
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
2. Place `runtime-arm64.tar.xz` and `asycuda-iraq.tar.xz` (available on the [Releases](../../releases) page) into `lorie-app/src/main/assets/`.
3. Build the APK with JDK 17 and Android SDK 34:
   ```bash
   ./gradlew :lorie-app:assembleDebug
   ```

---

## Developer & License

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).

Licensed under the [GNU General Public License v3.0](LICENSE).
