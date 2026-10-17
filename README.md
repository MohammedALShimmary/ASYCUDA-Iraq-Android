# ASYCUDA World — Republic of Iraq (Android)

Android client for the Iraqi General Commission of Customs (**ASYCUDA World** — `https://asy.mof.gov.iq`), built for customs brokers, clearing agents, and logistics operators in Iraq.

[**Download Latest APK (v3.6.0)**](../../releases/latest)

---

## Features

- **Direct Installation**: Installs as a standard Android APK on 64-bit ARM (`arm64-v8a`) phones and tablets running Android 8.0 or newer.
- **Automatic MOF Updates**: Checks `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on launch and from the in-app menu to keep client libraries synchronized with the Ministry of Finance server.
- **PDF Printing & File Export**: Prints customs declarations, receipts, and assessment notices directly to PDF (`Download/Asycuda_Prints`) and opens Android's native file picker when attaching documents.
- **Multi-Language Interface**: Supports **English**, **Arabic (العربية)**, and **Kurdish (کوردی)** across the app menu and settings.
- **Stable Background Connection**: Keeps the network session active when switching to other apps (such as WhatsApp, Authenticator, or PDF viewers).
- **Touch & Keyboard Controls**: Includes touchpad cursor control, two-finger scrolling, pinch-to-zoom, and an on-screen shortcut bar (`TAB`, `CTRL`, `ESC`, arrow keys).
- **Diagnostics Export**: Packages runtime logs into a `.zip` file from the menu for support and troubleshooting.

---

## Installation

1. Download `ASYCUDA_Iraq_v3.6.0.apk` from the [Releases](../../releases/latest) page.
2. Open the APK on your Android phone or tablet to install it.
3. On first launch, grant file access when prompted so the app can save printed PDFs and attach declaration files.
4. Wait ~10–15 seconds on the first run while the client unpacks its libraries and connects to the customs server.

> **Google Play Protect note**: Since this app is distributed directly on GitHub outside the Google Play Store, Android may display an *"Unrecognized app developer"* prompt during installation. Tap **More details → Install anyway** to continue.

---

<div dir="rtl">

## 🇮🇶 العربية — نظام أسيكودا العالمي (جمهورية العراق)

تطبيق أندرويد لتشغيل نظام **أسيكودا العالمي** التابع للهيئة العامة للكمارك العراقية (`asy.mof.gov.iq`) مباشرة على الهواتف والأجهزة اللوحية، وموجه للمخلصين الكمركيين وشركات الشحن والتجارة.

### المميزات:
- **تثبيت مباشر**: يعمل مباشرة بعد تثبيت ملف `APK` دون الحاجة لأي تطبيقات أو إضافات خارجية.
- **تحديث تلقائي من خادم الوزارة**: يفحص ويحدث ملفات النظام تلقائياً من خادم وزارة المالية عند وجود أي تحديث جديد.
- **طباعة وحفظ ملفات PDF**: طباعة التصاريح والوصلات الكمركية مباشرة بصيغة `PDF` وحفظها في مجلد `Download/Asycuda_Prints` مع إمكانية مشاركتها مباشرة.
- **دعم اللغات**: واجهة تحكم وإعدادات تدعم **العربية** و**الكردية** و**الإنجليزية**.
- **ثبات الاتصال في الخلفية**: يحافظ على جلسة العمل والاتصال بالإنترنت عند التنقل بين التطبيقات الأخرى على الهاتف.

</div>

---

<div dir="rtl">

## کوردی — سیستەمی ئەسیکۆدای جیهانی (کۆماری عێراق)

بەرنامەی ئەندرۆید بۆ کارپێکردنی سیستەمی **ئەسیکۆدای جیهانی** سەر بە دەستەی گشتیی گومرگی عێراق (`asy.mof.gov.iq`) لەسەر مۆبایل و تابلێت، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان.

### تایبەتمەندییەکان:
- **دامەزراندنی ڕاستەوخۆ**: بە یەک پەڕگەی `APK` دادەمەزرێت بەبێ پێویستی بە هیچ بەرنامەیەکی دەرەکی.
- **نوێکردنەوەی خۆکارانە**: پشکنین و نوێکردنەوەی پەڕگەکانی سیستەم ڕاستەوخۆ لە سێرڤەری وەزارەتی دارایی.
- **چاپکردن و پاشەکەوتکردنی PDF**: چاپکردنی بەڵگەنامە و پسوولە گومرگییەکان بە شێوەی `PDF` لەناو فۆڵدەری `Download/Asycuda_Prints`.
- **پاڵپشتی زمانەکان**: ڕووکاری بەرنامە و ڕێکخستنەکان پاڵپشتی زمانی **کوردی**، **عەرەبی** و **ئینگلیزی** دەکات.
- **پاراستنی پەیوەندی لە پاشبنەمادا**: پەیوەندی ئینتەرنێت و دانیشتنی کارکردن ناپچڕێت کاتێک دەچیتە سەر بەرنامەیەکی تر لە مۆبایلەکەتدا.

</div>

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
