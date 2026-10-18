# ASYCUDA World — Republic of Iraq (Android)

Android client for the Iraqi General Commission of Customs (**ASYCUDA World** — `https://asy.mof.gov.iq`), built for customs brokers, clearing agents, and logistics operators in Iraq.

[**Download Latest APK (v3.7.0)**](../../releases/latest)

---

## Features

- **Direct Installation**: Installs as a standard Android APK on 64-bit ARM (`arm64-v8a`) phones and tablets running Android 8.0 or newer.
- **Unified Update Center (App & MOF Packages)**: Checks both GitHub for new app releases (with in-app download and installation) and `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` to keep client libraries synchronized with the Ministry of Finance server.
- **PDF Printing & File Manager**: Prints customs declarations, receipts, and assessment notices directly to PDF (`Download/Asycuda_Prints`) with single and multi-file sharing and deletion, and opens Android's native file picker when attaching documents.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the Control Center, settings, and dialogs.
- **Display & Input Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, live-preview sliders for window scaling and shortcut bar opacity, configurable auto-keyboard popup, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **In-App Issue Reporting & Diagnostics**: Submit issue reports with screenshots and runtime logs directly from the Control Center, or export diagnostic logs as a `.zip` file.

---

## Installation

1. Download `ASYCUDA_Iraq_v3.7.0.apk` from the [Releases](../../releases/latest) page.
2. Open the APK on your Android phone or tablet to install it.
3. On first launch, review and accept the Terms & Conditions, then grant file access when prompted so the app can save printed PDFs and attach declaration files.
4. Wait ~10–15 seconds on the first run while the client unpacks its libraries and connects to the customs server.

> **Google Play Protect note**: Since this app is distributed directly on GitHub outside the Google Play Store, Android may display an *"Unrecognized app developer"* prompt during installation. Tap **More details → Install anyway** to continue.

---

<div dir="rtl">

## 🇮🇶 العربية — نظام أسيكودا العالمي (جمهورية العراق)

تطبيق أندرويد لتشغيل نظام **أسيكودا العالمي** التابع للهيئة العامة للكمارك العراقية (`asy.mof.gov.iq`) مباشرة على الهواتف والأجهزة اللوحية، وموجه للمخلصين الكمركيين وشركات الشحن والتجارة.

### المميزات:
- **تثبيت مباشر**: يعمل مباشرة بعد تثبيت ملف `APK` دون الحاجة لأي تطبيقات أو إضافات خارجية.
- **مركز التحديثات الموحد**: يفحص ويحدث كلاً من إصدار التطبيق وملفات النظام من خادم وزارة المالية تلقائياً أو يدوياً من داخل التطبيق.
- **طباعة وإدارة ملفات PDF**: طباعة التصاريح والوصلات الكمركية مباشرة بصيغة `PDF` وحفظها في مجلد `Download/Asycuda_Prints` مع إمكانية مشاركتها أو حذفها (مفرداً أو بتحديد متعدد).
- **إعدادات العرض والإدخال**: التحكم بحجم الشاشة وشفافية شريط الاختصارات بمعاينة فورية، مع خيار تفعيل أو إيقاف الظهور التلقائي للوحة المفاتيح.
- **دعم اللغات**: واجهة تحكم وإعدادات تدعم **العربية** و**الكردية** و**الإنجليزية**.
- **ثبات الاتصال في الخلفية**: يحافظ على جلسة العمل والاتصال بالإنترنت عند التنقل بين التطبيقات الأخرى على الهاتف.
- **الإبلاغ عن المشاكل**: إرسال الملاحظات والبلاغات الفنية مع لقطة شاشة وسجلات الفحص مباشرة من داخل التطبيق.

</div>

---

<div dir="rtl">

## کوردی — سیستەمی ئەسیکۆدای جیهانی (کۆماری عێراق)

بەرنامەی ئەندرۆید بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** سەر بە دەستەی گشتیی گومرگی عێراق (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێت، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان.

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ**: بە یەک پەڕگەی `APK` دادەمەزرێت بەبێ پێویستی بە هیچ بەرنامەیەکی دەرەکی.
- **ناوەندی نوێکردنەوەی یەکگرتوو**: پشکنین و نوێکردنەوەی بەرنامەکە و پەڕگەکانی سیستەم ڕاستەوخۆ لە سێرڤەری وەزارەتی دارایی.
- **چاپکردن و بەڕێوەبردنی PDF**: چاپکردنی بەڵگەنامە و پسوولە گومرگییەکان بە شێوەی `PDF` لەناو فۆڵدەری `Download/Asycuda_Prints` لەگەڵ توانای هاوبەشکردن و سڕینەوە.
- **ڕێکخستنی پیشاندان و تەختەکلیل**: کۆنترۆڵکردنی قەبارەی شاشە و ڕووناکی شریتی دوگمەکان بە شێوەی زیندوو، لەگەڵ کاراکردن یان ناچالاککردنی دەرکەوتنی خۆکارانەی تەختەکلیل.
- **پاڵپشتی زمانەکان**: ڕووکاری بەرنامە و ڕێکخستنەکان پاڵپشتی زمانی **کوردی**، **عەرەبی** و **ئینگلیزی** دەکات.
- **پاراستنی پەیوەندی لە پاشبنەمادا**: پەیوەندی ئینتەرنێت و دانیشتنی کارکردن ناپچڕێت کاتێک دەچیتە سەر بەرنامەیەکی تر لە مۆبایلەکەتدا.
- **ناردنی ڕاپۆرتی کێشەکان**: ناردنی تێبینی و ڕاپۆرتی کێشە تەکنیکییەکان ڕاستەوخۆ لەناو بەرنامەکەوە.

</div>

---

## Disclaimer

This application is an independent technical utility built to help customs brokers and trade companies run the ASYCUDA World client on Android devices. It is not affiliated with or officially endorsed by the Iraqi Ministry of Finance, the General Commission for Customs, or UNCTAD. The software connects directly to `asy.mof.gov.iq` using the unmodified official client packages and is provided *"as is"* without warranties of any kind.

---

## Building from Source

1. Place `runtime-arm64.tar.xz` and `asycuda-iraq.tar.xz` (available on the [Releases](../../releases) page) into `lorie-app/src/main/assets/`.
2. Build the release APK with JDK 17 and Android SDK 34:

```bash
./gradlew :lorie-app:assembleRelease -x extractReleaseAnnotations -x lint -x lintVitalRelease
```

---

## Developer & License

Developed and maintained by **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary)).

Licensed under the [GNU General Public License v3.0](LICENSE).
