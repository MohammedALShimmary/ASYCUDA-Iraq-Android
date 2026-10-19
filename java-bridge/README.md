# ASYCUDA World Android — Open-Source Java Bridge (`java-bridge/`)

This directory contains **100% of the custom Java source code** compiled into the three lightweight bridge archives (`00_bootpatch.jar`, `00_boot.jar`, and `00_jnlp.jar`) used to run the unmodified Iraqi Ministry of Finance / UNCTAD **ASYCUDA World** Java client (`so.kernel.client.DesktopMain`) on Android.

> [!IMPORTANT]
> **Zero Modification to Official Government JARs**
> None of the official Ministry of Finance / UNCTAD ASYCUDA World JAR files (`client.jar`, `all.jar`, `ktlc.jar`, etc., downloaded from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp`) are ever altered, patched, or decompiled-and-repackaged. All Android compatibility hooks live strictly inside the three standalone bridge JARs documented here, and each bridge JAR embeds its own `.java` source files alongside the `.class` bytecode for immediate inspection.

---

## Overview of the 3 Bridge Archives

### 1. `00_bootpatch.jar` (`bootpatch/java/awt/Desktop.java`)
* **Purpose**: Replaces `java.awt.Desktop` via `-Xbootclasspath/p:00_bootpatch.jar`.
* **Why It Is Needed**: On standard Linux desktops, OpenJDK 8's `java.awt.Desktop.getDesktop().open(File)` and `.browse(URI)` attempt to load native GNOME/GTK libraries (`libgnomevfs-2.so` / `libgtk-x11-2.0.so`), which do not exist on Android. When a customs broker generates a customs declaration, receipt, or report inside ASYCUDA World, the client calls `Desktop.getDesktop().open(file)`.
* **How It Works**: Forwards `OPEN`, `EDIT`, `PRINT`, and `BROWSE` requests via reflection to `asycuda.AsycudaBoot.handleDesktopAction(action, target)` without touching network credentials or user sessions.

### 2. `00_jnlp.jar` (`jnlp/javax/jnlp/*.java`)
* **Purpose**: Provides a clean room implementation of `javax.jnlp.BasicService`, `javax.jnlp.ServiceManager`, and `javax.jnlp.UnavailableServiceException`.
* **Why It Is Needed**: `AWIQWorld.jnlp` is normally launched by Oracle Java Web Start (`javaws`), which provides the proprietary `javaws.jar` (`javax.jnlp.*`) runtime classes. OpenJDK 8 builds for Linux ARM64 do not bundle Oracle's proprietary `javaws.jar`.
* **How It Works**: Implements `ServiceManager.lookup("javax.jnlp.BasicService")`, returning the official codebase URL (`https://asy.mof.gov.iq/aw/`) and forwarding `showDocument(URL)` requests to `asycuda.AsycudaBoot.handleDesktopAction(...)`.

### 3. `00_boot.jar` (`AsycudaBoot.java`)
* **Purpose**: Serves as the pre-launch wrapper around `so.kernel.client.DesktopMain.main(args)`.
* **What It Does (and Does Not Do)**:
  1. **Authenticated Localhost IPC Bridge (`127.0.0.1:47891`)**: Sends file-open (`OPEN|...`), browser-open (`BROWSE|...`), keyboard (`IME|SHOW`), and `SOClass` update (`SOCLASS_UPDATE|...`) events to the Android host activity using a per-session random authentication token (`-Dasycuda.bridge.token=<UUID>`).
  2. **Pure-Java Multi-Page PDF 1.4 Printer (`DirectPdfPrintService`)**: Registers a virtual `javax.print.PrintService` (`ASYCUDA_PDF_Printer`) that renders Java `Pageable`/`Printable` print jobs directly into multi-page PDF 1.4 documents inside the app's private print directory and opens them in Android's native PDF viewer / print spooler.
  3. **Pure-Java Window Manager & Touch Keyboard Helper**: Automatically maximizes the main ASYCUDA desktop frame to match the Android screen resolution, centers modal dialogs, redirects `JFileChooser` / `FileDialog` away from internal `/data/data/` paths to `/storage/emulated/0/Download/Asycuda_Files`, and triggers the Android soft keyboard when an editable text component is tapped.
  4. **`SOClass` Server Update Alert Listener**: Hooks `so.rmi.transport.client.ClientTransportLayer`'s `RejectHandler` via a Java Dynamic Proxy and inspects modal error dialogs for `"SOClass Client have to be updated. It will be shutdown now."`. Because the app runs directly on OpenJDK 8 rather than desktop `javaws`, detecting this server rejection automatically opens the Android **Update Center** window so the user can synchronize the latest JAR classes from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp` with one tap.
  5. **Scoped SecurityManager for JNLP Compatibility**: Desktop Java Web Start applications install a JNLP `SecurityManager`. `AsycudaBoot` installs a compatibility `SecurityManager` solely inside the isolated ASYCUDA child JVM process so that `DesktopMain` can initialize and write temporary report files under the app's private `tmp/` directory.
  6. **Zero Credential or Traffic Interception**: `AsycudaBoot` never reads, logs, stores, or transmits ASYCUDA usernames, passwords, customs declarations, or session tokens. All customs traffic flows directly between the unmodified UNCTAD client classes and `asy.mof.gov.iq`.

---

## Reproducible Verification & Compilation

You can compile all three bridge JARs from source and compare their contents against the packaged archives inside `lorie-app/src/main/assets/asycuda-iraq.tar.xz`:

```bash
chmod +x java-bridge/build-bridge.sh
./java-bridge/build-bridge.sh /path/to/extracted/jars/client.jar
```

Every generated `.jar` in `java-bridge/build/` contains both the compiled `.class` bytecode and the exact `.java` source files used to produce it.
