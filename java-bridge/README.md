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
  1. **Zero-Permission Automatic File Picker Bridge & Unified Vault Sandbox (`redirectFileChooserToDownloads`, `enforceChooserJail`, `checkWorkspaceImportedMarker`, `VaultFileSystemView`)**:
     - **Why It Is Needed**: Legacy Java 8 Swing (`javax.swing.JFileChooser` and `java.awt.FileDialog`) uses direct POSIX filesystem calls (`java.io.File`) and cannot invoke Android's `Intent.ACTION_OPEN_DOCUMENT` (`content://` URIs). Previously, this required `MANAGE_EXTERNAL_STORAGE`.
     - **How the Custom Bridge Works (`v4.2.0` — 100% Zero Modification to Government JARs & Zero Custom Swing Buttons)**:
       - `redirectFileChooserToDownloads(Container root)` and `enforceChooserJail(JFileChooser chooser, File safeDir)` jail every `JFileChooser` and `FileDialog` strictly to the app's unified Encrypted Private Vault directory `/data/data/iq.asycuda/files/prints` (`DEFAULT_WORKSPACE_DIR` / `DEFAULT_PRINTS_DIR`) using a custom `VaultFileSystemView` (`javax.swing.filechooser.FileSystemView`), so the embedded JVM requires **zero broad Android storage permissions** and cannot navigate up to parent Android system folders while still supporting subfolder creation and navigation inside `/prints`.
       - **Zero UI Injection into Government Dialogs**: No buttons or custom components are ever injected into ASYCUDA's `JFileChooser`. Instead, the first time a `JFileChooser` opens (`OPEN_DIALOG` or `CUSTOM_DIALOG`), `AsycudaBoot` automatically sends `AUTH|<token>|PICK_WORKSPACE_FILE` over `127.0.0.1:47891` to `AsycudaStandaloneManager.handleBridgeMessage()`.
       - On the Android side, `AsycudaMaterialUi.showFilePickerBridgePromptModal()` displays a non-intrusive Android dialog informing the user that ASYCUDA opened its file chooser and asking whether they want to **Import a new file from their phone into the Vault** (`Intent.ACTION_OPEN_DOCUMENT`) or **Use an existing file already in the Vault**.
       - When the user imports a file from their phone, `AsycudaStandaloneManager.onWorkspaceFilePicked(MainActivity, Uri)` copies that single file into `/data/data/iq.asycuda/files/prints/<filename>` and writes the filename to `/data/data/iq.asycuda/files/prints/.last_imported` (without closing the Java `JFileChooser`).
       - `checkWorkspaceImportedMarker()` (polled every `750ms` while a `JFileChooser` is open) detects `.last_imported`, deletes the marker, refreshes `chooser.rescanCurrentDirectory()`, and highlights the newly imported file (`chooser.setSelectedFile(importedFile)`) inside the open Java file chooser so the user can select it into their ASYCUDA declaration.
  2. **Encrypted Private App Vault for Printed PDFs & Exported Files + Upload Staging Protection (`DirectPdfPrintService`, `processAndOpenFile`, `detectExtensionAndMime`, `isFileAlreadyInVault`, `scanTmpDir`)**:
     - Registers a pure-Java multi-page PDF 1.4 `javax.print.PrintService` (`ASYCUDA_PDF_Printer`) and watches `/tmp/` for exported `.so` documents, saving all generated PDFs and exports **exclusively** inside the app's unified encrypted private storage (`/data/data/iq.asycuda/files/prints`, `DEFAULT_PRINTS_DIR`)—never in public `/storage/emulated/0/Download`.
     - **Upload Staging Exclusion (`soems*ems` & `isFileAlreadyInVault`)**: When a user attaches a document from the Vault to an ASYCUDA declaration and clicks the **Upload** button (`so.ems`), ASYCUDA creates a temporary upload staging file (`soems*ems`) in `/client/tmp/`. Both `processAndOpenFile()` and `scanTmpDir()` explicitly ignore `soems*ems` and `prn*xc` staging files and call `isFileAlreadyInVault(getWorkspaceDir(), f, candHash, 0)` (recursively checking the Vault directory tree by filename+size and SHA-256 digest) so uploading a file to the customs server never re-saves or re-opens the file on Android.
  3. **Authenticated Localhost IPC Bridge (`sendToAndroidApk` on `127.0.0.1:47891`)**:
     - `sendToAndroidApk(String)` is `private static` and authenticates to the Android host over `127.0.0.1:47891` using the per-session random authentication token passed via the `ASYCUDA_BRIDGE_TOKEN` environment variable (blocked from JVM read access by `SecurityManager.checkPermission(RuntimePermission("getenv.*"))`) plus constant-time `MessageDigest.isEqual` token validation and loopback interface verification.
     - `handleDesktopAction(String, String)` verifies that its caller originates from `java.awt.Desktop`, `javax.jnlp.ServiceManager`, or `AsycudaBoot` (`isAuthorizedDesktopActionCaller()`) and validates outbound URLs against `isAllowedOutboundBrowseUrl()`.
  4. **Composite Ministry of Finance TLS TrustStore (`installMofHttpsTrustStore`) & `SOClass` Alert Listener (`installRejectHandlerHook`, `inspectDialogForSoClassUpdate`)**:
     - `installMofHttpsTrustStore()` builds a composite JKS truststore (`mof_cacerts.jks`) combining the default OpenJDK 8 `cacerts` with `/data/data/iq.asycuda/files/client/mof.crt` via `javax.net.ssl.trustStore` so embedded `https://asy.mof.gov.iq/aw/help/index.html` requests verify cleanly using the standard PKIX validator.
     - Hooks `so.rmi.transport.client.ClientTransportLayer`'s `RejectHandler` via a Java Dynamic Proxy and inspects modal error dialogs for `"SOClass Client have to be updated. It will be shutdown now."`, automatically opening the Android **Update Center** window.
  5. **Fail-Closed Hardened SecurityManager (`createHardenedSecurityManager`, `checkBlockedHostPath`, `checkAllowedOutboundNetwork`)**:
     - Installs a fail-closed `SecurityManager` (if `System.setSecurityManager(...)` fails or is inactive, `AsycudaBoot` sends `SECURITY_ERROR|...` to the Android host to display an alert modal and immediately halts via `Runtime.getRuntime().halt(1)`).
     - Explicitly **blocks OS subprocess execution** (`checkExec()` throws `SecurityException`), **blocks JVM access to Android host storage paths** (`checkBlockedHostPath()` blocks `/shared_prefs`, `legal_consent_receipt.json`, `known_errors_catalog.json`, `/databases/`, `/datastore/`, and `/proc/self/environ`), and **enforces a 100% dynamic host & port outbound network allowlist with zero hardcoded IPs or ports** (`checkConnect()` / `checkAllowedOutboundNetwork()` restricts direct JVM sockets strictly to `localhost`/`127.0.0.1` and `asy.mof.gov.iq`, dynamically resolving IPs via `refreshAllowedCustomsIps()` and dynamically discovering authorized ports and virtualized RMI endpoints from `AWIQWorld.jnlp`, `so.rmi.url.port`, and `ClientTransportLayer` via `syncMofTransportState()`).
  6. **Zero Credential or Traffic Interception**:
     - `AsycudaBoot` never reads, logs, stores, or transmits ASYCUDA usernames, passwords, customs declarations, or session tokens. All customs traffic flows directly between the unmodified UNCTAD client classes and `asy.mof.gov.iq`.

---

## Original Java Bridge Attribution

The original Java bridge modules (`00_bootpatch.jar`, `00_boot.jar`, and `00_jnlp.jar`) were authored by **Mohammed Ammar Hussein (@MohammedALShimmary)** for this project.

* **Original project**: https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android
* **Attribution terms**: See [`java-bridge/ATTRIBUTION-TERMS.md`](ATTRIBUTION-TERMS.md) (GNU GPL-3.0 Section 7).

Please preserve applicable copyright and author-attribution notices, and identify modified versions, in accordance with the applicable license terms.

---

## Reproducible Verification & Compilation

You can compile all three bridge JARs from source and compare their contents against the packaged archives inside `lorie-app/src/main/assets/asycuda-iraq.tar.xz`:

```bash
chmod +x java-bridge/build-bridge.sh
./java-bridge/build-bridge.sh /path/to/extracted/jars/client.jar
```

Every generated `.jar` in `java-bridge/build/` contains both the compiled `.class` bytecode and the exact `.java` source files used to produce it.
