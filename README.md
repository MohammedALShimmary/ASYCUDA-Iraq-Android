<div align="center">

# ASYCUDA World — Republic of Iraq (Android Client)
### نظام أسيكودا العالمي — جمهورية العراق (تطبيق أندرويد المستقل)

[![Release](https://img.shields.io/badge/version-v3.5.0-2E9BFF?style=for-the-badge)](https://github.com/MohammedALShimmary)
[![Platform](https://img.shields.io/badge/platform-Android%20ARM64%20(API%2026%2B)-00D68F?style=for-the-badge)](https://developer.android.com)
[![Runtime](https://img.shields.io/badge/runtime-OpenJDK%2021%20%2B%20X11%20Lorie-FFB547?style=for-the-badge)](https://openjdk.org)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg?style=for-the-badge)](LICENSE)

**A self-contained, native ARM64 Android application for running the Iraqi General Commission of Customs (Ministry of Finance) ASYCUDA World desktop client on Android phones and tablets — without Termux, proot, or external rootfs installations.**

[Download Latest APK (Releases)](../../releases) · [Report an Issue](../../issues) · [العربية](#-نبذة-عن-المشروع)

</div>

---

## Overview

The official **ASYCUDA World** customs management portal (`https://asy.mof.gov.iq`) relies on a legacy Java WebStart (`.jnlp`) desktop client (`so.kernel.client.ClientApp`). Historically, running this system on Android required complex multi-gigabyte Linux container setups (`Termux` + `proot-distro` + `Termux:X11`).

**ASYCUDA World — Republic of Iraq (`iq.asycuda`)** packages the entire stack into a single standalone Android APK (~90 MB):

- **Embedded Native X11 Server (`libXlorie.so`)**: Renders Java AWT/Swing windows directly onto a hardware-accelerated Android `SurfaceView`.
- **Self-Contained ARM64 OpenJDK 21 + X11 Libraries**: Extracted automatically on first launch into the app's private data directory (`/data/data/iq.asycuda/files`).
- **Live Ministry of Finance JNLP Synchronizer**: Queries `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on startup and on demand, comparing remote JAR sizes against local libraries and downloading only updated packages with atomic swap.
- **Native Android Document & Print Bridge (`00_boot.jar`)**: Intercepts desktop file chooser dialogs (`JFileChooser`, `FileDialog`), clipboard copy/paste, and print streams (`PrintServiceLookup`, `DirectPdfPrintService`) and routes them natively to Android's Document Picker, PDF generator, and system share sheet.
- **Background Network Persistence**: Keeps a foreground `PARTIAL_WAKE_LOCK` and low-latency `WifiLock` active so switching between apps (such as WhatsApp, Authenticator, or PDF viewers) never drops the active customs session.
- **One-Tap Diagnostics Exporter**: Packages runtime logs, JNLP manifests, JAR inventory, and filtered `logcat` output into a single shareable `.zip` archive for remote troubleshooting.

---

## Key Features

| Feature | Details |
| :--- | :--- |
| **Zero External Dependencies** | Single APK install. No Termux, no Linux chroot, no external X server required. |
| **Automatic MOF JAR Updates** | Verifies client JARs against `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` on boot and via the in-app Control Center. |
| **Direct PDF Print & Export** | Registers a virtual ISO-A4 PostScript/PDF printer inside Java (`ASYCUDA_Android_PDF_Printer`) that converts declarations and receipts directly to PDF using Ghostscript (`gs`) or native PostScript output. |
| **Native File Upload & Download** | Bridges desktop file dialogs to Android's Storage Access Framework (SAF) and an in-app Document Hub (`/sdcard/Download/ASYCUDA_Iraq`). |
| **Touch & Hardware Input** | Pre-configured with relative touchpad cursor mode, two-finger scroll, pinch-to-zoom, and an on-screen modifier bar (`ESC`, `TAB`, `CTRL`, `ALT`, arrow keys). |
| **Session Keep-Alive** | Foreground service prevents Android battery optimization from suspending the JVM or closing TCP sockets when multitasking. |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                   Android App (iq.asycuda)                           │
│                                                                      │
│  ┌─────────────────────────┐    ┌─────────────────────────────────┐  │
│  │  MainActivity / Lorie   │◄──►│  AsycudaMaterialUi              │  │
│  │  X11 Surface Renderer   │    │  Control Center & Document Hub  │  │
│  └───────────▲─────────────┘    └────────────────▲────────────────┘  │
│              │ X11 Socket (:0)                   │ IPC Bridge Files  │
│  ┌───────────┴───────────────────────────────────┴────────────────┐  │
│  │  AsycudaStandaloneManager + AsycudaKeepAliveService            │  │
│  │  • Extracts runtime-arm64.tar.xz & asycuda-iraq.tar.xz         │  │
│  │  • Syncs JARs from https://asy.mof.gov.iq/aw/AWIQWorld.jnlp    │  │
│  │  • Spawns OpenJDK 21 with LD_LIBRARY_PATH & X11 env            │  │
│  └───────────▲────────────────────────────────────────────────────┘  │
│              │                                                       │
│  ┌───────────┴────────────────────────────────────────────────────┐  │
│  │  Java Bridge (java-bridge/AsycudaBoot.java -> 00_boot.jar)     │  │
│  │  • Custom SSL/TLS TrustManager for MOF certificates            │  │
│  │  • DirectPdfPrintService (ISO A4 MediaPrintableArea + PDF)     │  │
│  │  • JFileChooser / FileDialog interceptor -> Android SAF        │  │
│  │  • Swing window auto-fit & touch-friendly scrollbars           │  │
│  └───────────▲────────────────────────────────────────────────────┘  │
│              │                                                       │
│  ┌───────────┴────────────────────────────────────────────────────┐  │
│  │  UNCTAD / MOF Client (so.kernel.client.ClientApp)              │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Installation

1. Go to the [**Releases**](../../releases) page and download the latest `ASYCUDA_Iraq_v3.5.0.apk`.
2. Open the APK on your Android device (Android 8.0 / API 26 or newer, 64-bit ARM `arm64-v8a`).
3. On first launch, the app will unpack the embedded OpenJDK 21 runtime and Iraqi Customs libraries (~10–15 seconds) and check `asy.mof.gov.iq` for any updated JAR packages.
4. Tap the floating **Menu** button at any time to open the Control Center, toggle the keyboard, upload/share documents, sync JARs, or export a diagnostics ZIP.

> **Note on Google Play Protect**: Because this application is distributed directly on GitHub outside the Google Play Store and embeds a native Linux JVM runtime (`libXlorie.so` + OpenJDK 21), Google Play Protect may display an *"Unrecognized app developer"* prompt on first install. Tap **More details → Install anyway** to proceed. Every release APK is signed with a dedicated 4096-bit RSA release certificate (`SHA-256: D3:02:D4:D0:50:FB:98:6E:40:88:DD:3E:68:4C:90:66:2D:00:DA:74:8D:DE:8D:A3:D4:96:6B:AE:A0:2D:A0:23`).

---

## Building from Source

### Prerequisites
- **JDK 17** (for Android Gradle Plugin 8.7.0)
- **Android SDK** (`platforms;android-34`, `build-tools;34.0.0`)
- Runtime archives placed in `lorie-app/src/main/assets/`:
  - `runtime-arm64.tar.xz` (ARM64 OpenJDK 21 + X11 client libraries)
  - `asycuda-iraq.tar.xz` (Base Iraqi Customs JAR cache + `00_boot.jar`)
  *(Both archives are available as downloadable assets in the [GitHub Release](../../releases).)*

### Build Steps

```bash
# 1. Clone the repository
git clone https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android.git
cd ASYCUDA-Iraq-Android

# 2. Download runtime-arm64.tar.xz and asycuda-iraq.tar.xz from the latest GitHub Release
#    and place them in lorie-app/src/main/assets/
mkdir -p lorie-app/src/main/assets

# 3. Build the APK
./gradlew :lorie-app:assembleRelease -x extractReleaseAnnotations -x lint -x lintVitalRelease
```

To recompile the Java Swing/Print bridge (`java-bridge/AsycudaBoot.java` -> `00_boot.jar`):

```bash
javac -source 8 -target 8 -cp "path/to/client/jars/*" java-bridge/AsycudaBoot.java -d build/boot_classes
jar cf 00_boot.jar -C build/boot_classes .
```

---

<div dir="rtl">

## 🇮🇶 نبذة عن المشروع

تطبيق **نظام أسيكودا العالمي — جمهورية العراق (`iq.asycuda`)** هو تطبيق أندرويد مستقل بالكامل لتشغيل نظام الكمارك العراقي (الهيئة العامة للكمارك - وزارة المالية) مباشرة على الهواتف والأجهزة اللوحية العاملة بنظام أندرويد (معالجات ARM64) دون الحاجة إلى تطبيق `Termux` أو أي إضافات خارجية.

### أبرز المميزات:
- **تثبيت مباشر بملف واحد (APK)**: يحتوي التطبيق داخلياً على بيئة `OpenJDK 21` ومحرك العرض الرسومي `X11`.
- **تحديث تلقائي لمكتبات النظام**: يفحص التطبيق تلقائياً خادم وزارة المالية (`https://asy.mof.gov.iq/aw/AWIQWorld.jnlp`) عند التشغيل أو يدوياً من القائمة الرئيسية لتحديث ملفات `JAR` الحكومية عند صدور أي تحديث.
- **طباعة وتصدير ملفات PDF مباشرة**: يدعم الطباعة المباشرة من داخل نوافذ أسيكودا وتحويل التصاريح والوصلات الكمركية إلى ملفات `PDF` جاهزة للمشاركة أو الحفظ في مجلد `Download/ASYCUDA_Iraq`.
- **استقرار الاتصال في الخلفية**: يحافظ التطبيق على اتصال الشبكة والـ Session حتى عند التنقل بين التطبيقات الأخرى على الهاتف.
- **مشاركة سجل التشخيص (Share Diagnostics ZIP)**: إمكانية ضغط ومشاركة جميع سجلات النظام بضغطة زر لتسهيل الدعم الفني وحل المشاكل.

</div>

---

## Author & Credits

- **Author & Lead Developer**: **Mohammed Ammar Hussein** ([@MohammedALShimmary](https://github.com/MohammedALShimmary))
- **X11 Rendering Layer**: Based on [Termux:X11 (`libXlorie`)](https://github.com/termux/termux-x11) (GPLv3)
- **Customs Platform**: UNCTAD ASYCUDA World / Iraqi Ministry of Finance — General Commission of Customs

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)** — see the [LICENSE](LICENSE) file for details.
