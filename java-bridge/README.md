# ASYCUDA World Android — Open-Source Java Bridge (`java-bridge/`)

This directory contains **100% of the custom Java source code** compiled into the three lightweight bridge archives (`00_bootpatch.jar`, `00_boot.jar`, and `00_jnlp.jar`) used to run the unmodified Iraqi Ministry of Finance / UNCTAD **ASYCUDA World** Java client (`so.kernel.client.DesktopMain`) on Android.

> [!IMPORTANT]
> **Zero Modification to Official Government JARs & 100% Embedded Source Transparency**
> None of the official Ministry of Finance / UNCTAD ASYCUDA World JAR files (`client.jar`, `all.jar`, `ktlc.jar`, etc., downloaded from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp`) are ever altered, patched, or decompiled-and-repackaged. All Android compatibility hooks live strictly inside the three standalone bridge JARs documented here, and each bridge JAR embeds its own `.java` source files (`AsycudaBoot.java`, `Desktop.java`, `ServiceManager.java`, `BasicService.java`, `UnavailableServiceException.java`) alongside the `.class` bytecode for immediate inspection.

---

## Overview of the 3 Bridge Archives

### 1. `00_bootpatch.jar` (`bootpatch/java/awt/Desktop.java`)
* **Purpose**: Replaces `java.awt.Desktop` via `-Xbootclasspath/p:00_bootpatch.jar`.
* **Why It Is Needed**: On standard Linux desktops, OpenJDK 8's `java.awt.Desktop.getDesktop().open(File)` and `.browse(URI)` attempt to load native GNOME/GTK libraries (`libgnomevfs-2.so` / `libgtk-x11-2.0.so`), which do not exist on Android. When a customs broker generates a customs declaration, receipt, or report inside ASYCUDA World, the client calls `Desktop.getDesktop().open(file)`.
* **How It Works**: Forwards `OPEN`, `EDIT`, `PRINT`, and `BROWSE` requests via reflection to `asycuda.AsycudaBoot.handleDesktopAction(action, target)` without touching network credentials or user sessions.

### 2. `00_jnlp.jar` (`jnlp/javax/jnlp/*.java`)
* **Purpose**: Provides a clean-room implementation of `javax.jnlp.BasicService`, `javax.jnlp.ServiceManager`, and `javax.jnlp.UnavailableServiceException`.
* **Why It Is Needed**: `AWIQWorld.jnlp` is normally launched by Oracle Java Web Start (`javaws`), which provides the proprietary `javaws.jar` (`javax.jnlp.*`) runtime classes. OpenJDK 8 builds for Linux ARM64 do not bundle Oracle's proprietary `javaws.jar`.
* **How It Works**: Implements `ServiceManager.lookup("javax.jnlp.BasicService")`, returning the official codebase URL (`https://asy.mof.gov.iq/aw/`) and forwarding `showDocument(URL)` requests to `asycuda.AsycudaBoot.handleDesktopAction(...)`.

### 3. `00_boot.jar` (`AsycudaBoot.java`)
* **Purpose**: Serves as the pre-launch wrapper around `so.kernel.client.DesktopMain.main(args)`.
* **Complete Inventory of Custom Java Functions in `AsycudaBoot.java`**:
  1. **Zero-Permission File Picker Middleman (`redirectFileChooserToDownloads`, `attachWorkspaceImportBar`, `checkWorkspaceImportedMarker`)**:
     - **Why It Is Needed**: Legacy Java 8 Swing (`javax.swing.JFileChooser` and `java.awt.FileDialog`) uses direct POSIX filesystem calls (`java.io.File`) and cannot invoke Android's `Intent.ACTION_OPEN_DOCUMENT` (`content://` URIs). Previously, this required `MANAGE_EXTERNAL_STORAGE`.
     - **How the Custom Middleman Works (`v3.9.0+`)**:
       - `redirectFileChooserToDownloads(Container root)` jails every `JFileChooser` and `FileDialog` strictly to the app's private isolated workspace directory `/data/data/iq.asycuda/files/workspace` (`WORKSPACE_DIR`), so the embedded JVM requires **zero broad Android storage permissions**.
       - `attachWorkspaceImportBar(JFileChooser chooser, File safeDir)` injects a custom Swing top bar (`name = "asycuda_import_bar"`) with a blue **"📂 Import from Phone / استيراد ملف من الهاتف"** button directly inside `JFileChooser` and attaches a `PropertyChangeListener` that prevents navigation outside `/data/data/iq.asycuda/files/workspace`.
       - When the user clicks **"Import from Phone"**, `AsycudaBoot` sends `AUTH|<token>|PICK_WORKSPACE_FILE` over `127.0.0.1:47891` to `AsycudaStandaloneManager.handleBridgeMessage()`, which launches Android's native system file picker (`Intent.ACTION_OPEN_DOCUMENT`).
       - When the user picks a file on their phone, `AsycudaStandaloneManager.onWorkspaceFilePicked(MainActivity, Uri)` copies that single file into `/data/data/iq.asycuda/files/workspace/<filename>` and writes the filename to `/data/data/iq.asycuda/files/workspace/.last_imported`.
       - `checkWorkspaceImportedMarker()` (polled every `750ms` while a `JFileChooser` is open) detects `.last_imported`, deletes the marker, calls `chooser.rescanCurrentDirectory()`, and automatically selects the newly imported file (`chooser.setSelectedFile(importedFile)`) inside the open Java dialog.
  2. **Encrypted Private App Vault for Printed PDFs & Exported Files (`DirectPdfPrintService`, `exportAndOpenFile`, `detectExtension`)**:
     - Registers a pure-Java multi-page PDF 1.4 `javax.print.PrintService` (`ASYCUDA_PDF_Printer`) and watches `/tmp/` for exported `.so` documents, saving all generated PDFs and exports **exclusively** inside the app's encrypted private storage (`/data/data/iq.asycuda/files/prints`, `PRINTS_DIR`)—never in public `/storage/emulated/0/Download`.
  3. **Authenticated Localhost IPC Bridge (`sendBridgeCommand` on `127.0.0.1:47891`)**:
     - Sends file-open (`OPEN|...`), browser-open (`BROWSE|...`), native file-picker (`PICK_WORKSPACE_FILE`), keyboard (`IME|SHOW`), and `SOClass` update (`SOCLASS_UPDATE|...`) events to the Android host activity using a per-session random authentication token (`-Dasycuda.bridge.token=<UUID>`).
  4. **`SOClass` Server Update Alert Listener (`installRejectHandlerHook`, `inspectDialogForSoClassUpdate`)**:
     - Hooks `so.rmi.transport.client.ClientTransportLayer`'s `RejectHandler` via a Java Dynamic Proxy and inspects modal error dialogs for `"SOClass Client have to be updated. It will be shutdown now."`, automatically opening the Android **Update Center** window.
  5. **Subprocess-Blocking SecurityManager (`installPrintSecurityManager`)**:
     - Installs a filesystem-watching `SecurityManager` for `/tmp/` print interception that explicitly **blocks OS subprocess execution** (`checkExec()` throws `SecurityException`).
  6. **Zero Credential or Traffic Interception**:
     - `AsycudaBoot` never reads, logs, stores, or transmits ASYCUDA usernames, passwords, customs declarations, or session tokens. All customs traffic flows directly between the unmodified UNCTAD client classes and `asy.mof.gov.iq`.

---

## Reproducible Verification & Compilation

You can compile all three bridge JARs from source and compare their contents against the packaged archives inside `lorie-app/src/main/assets/asycuda-iraq.tar.xz`:

```bash
chmod +x java-bridge/build-bridge.sh
./java-bridge/build-bridge.sh /path/to/extracted/jars/client.jar
```

Every generated `.jar` in `java-bridge/build/` contains both the compiled `.class` bytecode and the exact `.java` source files used to produce it.
