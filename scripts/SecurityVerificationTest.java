import com.termux.x11.AsycudaSecurityValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

/**
 * Automated Adversarial Security Verification Suite for AsyLauncher (Android Host Layer).
 * Compiles and invokes production classes ({@link com.termux.x11.AsycudaSecurityValidator})
 * and verifies Android host layer security invariants directly in CI/CD on every commit.
 */
@SuppressWarnings({"java:S106", "java:S1192", "java:S3776"})
public final class SecurityVerificationTest {

    private static final Logger LOG = Logger.getLogger(SecurityVerificationTest.class.getName());

    private SecurityVerificationTest() {
    }

    public static void main(String[] args) throws Exception {
        testManifestZeroBroadPermissions();
        testJnlpUrlAndPortAllowlist();
        testUnsignedAndTamperedJarRejection();
        testBridgeAuthAndPathTraversal();
        testCredentialAndPiiRedaction();
        testZeroPiiIraqiLawWaiverAndInRamPreview();
        testHostIsolationNativeLoopbackAndUpdaterHardening();
        LOG.info("ALL 7 DIRECT PRODUCTION SECURITY VERIFICATION SUITES PASSED.");
    }

    private static void testManifestZeroBroadPermissions() throws IOException {
        File manifest = resolveProjectFile("lorie/src/main/AndroidManifest.xml");
        String xml = new String(Files.readAllBytes(manifest.toPath()), StandardCharsets.UTF_8);
        String[] forbidden = new String[] {
            "MANAGE_EXTERNAL_STORAGE",
            "READ_EXTERNAL_STORAGE",
            "WRITE_EXTERNAL_STORAGE",
            "ACCESS_WIFI_STATE",
            "CHANGE_WIFI_STATE",
            "CHANGE_NETWORK_STATE",
            "BIND_ACCESSIBILITY_SERVICE",
            "REQUEST_INSTALL_PACKAGES",
            "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
        };
        for (String perm : forbidden) {
            if (xml.contains(perm)) {
                throw new AssertionError("Forbidden permission present in AndroidManifest.xml: " + perm);
            }
        }
    }

    private static void testJnlpUrlAndPortAllowlist() {
        assertNotNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("lib/so.jar"),
                "Relative MOF JAR path should be accepted");
        assertNotNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq/aw/lib/unior.jar"),
                "Official HTTPS MOF URL on default port 443 should be accepted");
        assertNotNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq:443/aw/lib/unior.jar"),
                "Official HTTPS MOF URL on explicit port 443 should be accepted");

        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("http://asy.mof.gov.iq/aw/lib/unior.jar"),
                "Plaintext HTTP downgrade must be rejected");
        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://evil.example.com/aw/lib/unior.jar"),
                "Untrusted host must be rejected");
        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq.evil.com/aw/lib/unior.jar"),
                "Spoofed host suffix must be rejected");
        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq:8443/aw/lib/unior.jar"),
                "Non-443 port must be rejected by production AsycudaSecurityValidator");
        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq/aw/../../etc/passwd"),
                "Path traversal must be rejected");
        assertNull(AsycudaSecurityValidator.resolveTrustedMofJarUrl("https://asy.mof.gov.iq/aw/not_a_jar.txt"),
                "Non-JAR extension must be rejected");
    }

    private static void testUnsignedAndTamperedJarRejection() throws IOException {
        // 1. Unsigned JAR must be rejected by production AsycudaSecurityValidator
        File unsignedJar = File.createTempFile("unsigned_test_", ".jar");
        try {
            try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(unsignedJar))) {
                JarEntry entry = new JarEntry("so/kernel/FakeClass.class");
                jos.putNextEntry(entry);
                jos.write(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
                jos.closeEntry();
            }
            if (AsycudaSecurityValidator.verifyDownloadedJarIntegrity(unsignedJar)) {
                throw new AssertionError("Unsigned JAR was falsely accepted by production validator!");
            }
        } finally {
            Files.deleteIfExists(unsignedJar.toPath());
        }

        // 2. Empty JAR (no class/resource entries) must be rejected by production AsycudaSecurityValidator
        File emptyJar = File.createTempFile("empty_test_", ".jar");
        try {
            try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(emptyJar))) {
                JarEntry meta = new JarEntry("META-INF/MANIFEST.MF");
                jos.putNextEntry(meta);
                jos.write("Manifest-Version: 1.0\n\n".getBytes(StandardCharsets.UTF_8));
                jos.closeEntry();
            }
            if (AsycudaSecurityValidator.verifyDownloadedJarIntegrity(emptyJar)) {
                throw new AssertionError("META-INF-only JAR was falsely accepted by production validator!");
            }
        } finally {
            Files.deleteIfExists(emptyJar.toPath());
        }

        // 3. Unsigned directory-only JAR must be rejected (R5-03)
        File unsignedDirJar = File.createTempFile("unsigned_dir_test_", ".jar");
        try {
            try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(unsignedDirJar))) {
                JarEntry meta = new JarEntry("META-INF/MANIFEST.MF");
                jos.putNextEntry(meta);
                jos.write("Manifest-Version: 1.0\n\n".getBytes(StandardCharsets.UTF_8));
                jos.closeEntry();
                JarEntry dirEntry = new JarEntry("un/asytax/");
                jos.putNextEntry(dirEntry);
                jos.closeEntry();
            }
            if (AsycudaSecurityValidator.verifyDownloadedJarIntegrity(unsignedDirJar)) {
                throw new AssertionError("Unsigned directory-only JAR was falsely accepted by production validator (R5-03)!");
            }
        } finally {
            Files.deleteIfExists(unsignedDirJar.toPath());
        }
    }

    private static void testBridgeAuthAndPathTraversal() throws IOException {
        String sessionToken = "9f8e7d6c-5b4a-3210-fedc-ba9876543210";
        String authPrefix = "AUTH|" + sessionToken + "|";

        String unauthenticated = "OPEN|/data/data/io.github.mohammedalshimmary.asylauncher/shared_prefs/asycuda_legal_consent.xml";
        if (unauthenticated.startsWith(authPrefix)) {
            throw new AssertionError("Unauthenticated bridge command must be rejected");
        }

        String traversalPath = "../../shared_prefs/asycuda_legal_consent.xml";
        String safeBaseName = AsycudaSecurityValidator.sanitizeBridgeFileName(traversalPath);
        if (safeBaseName.contains("..") || safeBaseName.contains("/") || safeBaseName.contains("\\")) {
            throw new AssertionError("Path traversal was not stripped: " + safeBaseName);
        }

        File mgrFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaStandaloneManager.java");
        String mgrSrc = new String(Files.readAllBytes(mgrFile.toPath()), StandardCharsets.UTF_8);
        if (!mgrSrc.contains("MessageDigest.isEqual") || !mgrSrc.contains("isLoopbackPeerOwnedByOurUid")) {
            throw new AssertionError("AsycudaStandaloneManager must enforce constant-time MessageDigest.isEqual and loopback UID peer verification");
        }
        if (mgrSrc.contains("NextGen-Customs-") || !mgrSrc.contains("AsycudaFirebaseManager.fetchAndSyncKnownErrorsCatalog")) {
            throw new AssertionError("AsycudaStandaloneManager must sync Known-Issues Catalog exclusively via AsycudaFirebaseManager (R5-04)");
        }
        if (mgrSrc.contains("requestNetwork(") || mgrSrc.contains("BIND_4G_RELAY_PORT")
                || mgrSrc.contains("ROUTE_4G_CELLULAR") || mgrSrc.contains("sCellularBypassNetwork")
                || mgrSrc.contains("/proc/net/tcp") || mgrSrc.contains("raw.githubusercontent.com")
                || mgrSrc.contains("ConnectivityManager") || mgrSrc.contains("/proc/self/exe")
                || mgrSrc.contains("/system/bin/logcat") || mgrSrc.contains("application/vnd.android.package-archive")) {
            throw new AssertionError("The 4G network bypass relay and Play Protect SAB heuristic triggers must not be present in AsycudaStandaloneManager");
        }
        File cmdEntryFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/CmdEntryPoint.java");
        String cmdEntrySrc = new String(Files.readAllBytes(cmdEntryFile.toPath()), StandardCharsets.UTF_8);
        if (cmdEntrySrc.contains("sun.misc.Unsafe") || cmdEntrySrc.contains("ActivityManagerNative")) {
            throw new AssertionError("CmdEntryPoint.java must not contain hidden API reflection or sun.misc.Unsafe");
        }
    }

    private static void testCredentialAndPiiRedaction() {
        String token = "bridge-secret-token-12345";
        String rawLog = "Connecting to https://customs_user:MySuperSecretPass!@asy.mof.gov.iq/aw/\n"
                + "<login>iraq_broker_01</login><password>Passw0rd#2026</password>\n"
                + "TIN=7788990011, jsessionid=ABCDEF0123456789, Authorization: Bearer eyJhbGciOiJIUzI1NiJ9\n"
                + "{\"password\":\"MySecret123\",\"username\":\"broker01\",\"TIN\":\"1234567890\"}\n"
                + "BridgeToken=" + token;
        String scrubbed = AsycudaSecurityValidator.redactSensitiveLogContent(rawLog, token);
        String[] mustNotContain = new String[] {
            "MySuperSecretPass!",
            "iraq_broker_01",
            "Passw0rd#2026",
            "7788990011",
            "ABCDEF0123456789",
            "eyJhbGciOiJIUzI1NiJ9",
            "MySecret123",
            "broker01",
            "1234567890",
            token
        };
        for (String secret : mustNotContain) {
            if (scrubbed.contains(secret)) {
                throw new AssertionError("Sensitive value leaked through production redaction filter: " + secret);
            }
        }
    }

    private static void testZeroPiiIraqiLawWaiverAndInRamPreview() throws IOException {
        File uiFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaMaterialUi.java");
        String uiSrc = new String(Files.readAllBytes(uiFile.toPath()), StandardCharsets.UTF_8);
        if (!uiSrc.contains("259/2") || !uiSrc.contains("78") || !uiSrc.contains("purgeLegacyConsentPii")) {
            throw new AssertionError("AsycudaMaterialUi.java must enforce Iraqi Civil Code Art. 259/2 & Law 78/2012 Art. 18 click-wrap waiver and purge legacy PII");
        }
        if (uiSrc.contains("Settings.Secure.ANDROID_ID")) {
            throw new AssertionError("AsycudaMaterialUi.java must not read Settings.Secure.ANDROID_ID");
        }
        if (!uiSrc.contains("showDiagnosticDataPreviewModal") || !uiSrc.contains("ramBuffer.setCharAt(i, '\\0')")) {
            throw new AssertionError("AsycudaMaterialUi.java must provide 100% in-RAM diagnostic preview with immediate buffer zeroing on close");
        }

        File mgrFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaStandaloneManager.java");
        String mgrSrc = new String(Files.readAllBytes(mgrFile.toPath()), StandardCharsets.UTF_8);
        if (mgrSrc.contains("ip_address")) {
            throw new AssertionError("AsycudaStandaloneManager.java must not attach ip_address to diagnostic events");
        }
        if (mgrSrc.toLowerCase(java.util.Locale.US).contains("sentry") || mgrSrc.contains("api.github.com")) {
            throw new AssertionError("AsycudaStandaloneManager.java must not reference legacy third-party crash trackers or background GitHub API polling");
        }

        File fbFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaFirebaseManager.java");
        String fbSrc = new String(Files.readAllBytes(fbFile.toPath()), StandardCharsets.UTF_8);
        if (fbSrc.contains("consent_receipts") || fbSrc.contains("submitConsentReceipt")) {
            throw new AssertionError("AsycudaFirebaseManager.java must not upload consent receipts");
        }
        if (fbSrc.contains("gen-lang-client-") || !fbSrc.contains("BuildConfig.FIREBASE_PROJECT_ID")
                || !fbSrc.contains("isAuthorizedOfficialBuild")
                || !fbSrc.contains("d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023")
                || !fbSrc.contains("X-Android-Cert")) {
            throw new AssertionError("AsycudaFirebaseManager.java must enforce 3-layer fork/clone protection (BuildConfig.FIREBASE_PROJECT_ID, release signing cert lock, and X-Android-Cert header)");
        }

        File catFile = resolveProjectFile("config/known-errors-catalog.json");
        String catSrc = new String(Files.readAllBytes(catFile.toPath()), StandardCharsets.UTF_8);
        if (catSrc.toLowerCase(java.util.Locale.US).contains("sentry")) {
            throw new AssertionError("known-errors-catalog.json must use ASY-* error codes and contain zero references to legacy trackers");
        }
    }

    private static void testHostIsolationNativeLoopbackAndUpdaterHardening() throws Exception {
        // 1. Enforce strict Zero-Hardcoded-IP / Zero-Hardcoded-Port-Fallback Policy across public production files
        String[] prodFilesToCheck = new String[] {
            "lorie/src/main/java/com/termux/x11/AsycudaStandaloneManager.java",
            "lorie/src/main/java/com/termux/x11/AsycudaSecurityValidator.java"
        };
        String[] forbiddenHardcodedLiterals = new String[] {
            "185.112.191.94",
            "118.97.60.75",
            "10.25.25.10",
            "return 2006;",
            "//asy.mof.gov.iq:2006/"
        };
        for (String relPath : prodFilesToCheck) {
            String content = new String(Files.readAllBytes(resolveProjectFile(relPath).toPath()), StandardCharsets.UTF_8);
            for (String forbidden : forbiddenHardcodedLiterals) {
                if (content.contains(forbidden)) {
                    throw new AssertionError("Forbidden hardcoded IP/port literal '" + forbidden + "' found in " + relPath);
                }
            }
        }

        // 2. Verify APK updater SHA-256 & URL validation and Tar symlink traversal protection
        if (!AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl(
                "https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.4.0/AsyLauncher_v4.4.0.apk")) {
            throw new AssertionError("Official GitHub release URL should be trusted");
        }
        if (AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl(
                "https://github.com/attacker/malware/releases/download/v1.0/evil.apk")) {
            throw new AssertionError("Other GitHub repository release URL must be rejected (R2-06)");
        }
        if (AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl("http://github.com/evil.apk")
                || AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl("https://evil.example.com/ASYCUDA_Iraq.apk")) {
            throw new AssertionError("Non-HTTPS or untrusted APK host must be rejected");
        }
        if (AsycudaSecurityValidator.isValidSha256Hex("") || AsycudaSecurityValidator.isValidSha256Hex("1234")) {
            throw new AssertionError("Empty or short SHA-256 must be rejected");
        }
        File fakeDest = new File("/tmp/asycuda_tar_test");
        File fakeLink = new File(fakeDest, "sub/link");
        if (AsycudaSecurityValidator.isSafeTarSymlinkTarget(fakeDest, fakeLink, "../../../etc/passwd")) {
            throw new AssertionError("Tar symlink traversal outside destDir must be rejected");
        }
        if (AsycudaSecurityValidator.isSafeTarSymlinkTarget(fakeDest, fakeLink, "/etc/passwd")) {
            throw new AssertionError("Absolute Tar symlink target /etc/passwd must be rejected (R2-03)");
        }
        if (!AsycudaSecurityValidator.isSafeTarSymlinkTarget(fakeDest, fakeLink, "../lib/libfoo.so")) {
            throw new AssertionError("Valid relative symlink inside destDir should be allowed");
        }
        File repoKeystore = new File(resolveProjectFile("lorie-app/build.gradle").getParentFile(), "asycuda_release.jks");
        if (repoKeystore.exists()) {
            throw new AssertionError("Release keystore must not reside inside repository workspace (R2-04)");
        }

        // 3. Verify native X11 loopback bind, one-way clipboard, zero keystroke logging, and FileProvider scope
        File cmdCpp = resolveProjectFile("lorie/src/main/cpp/lorie/cmdentrypoint.cpp");
        String cmdCppSrc = new String(Files.readAllBytes(cmdCpp.toPath()), StandardCharsets.UTF_8);
        if (cmdCppSrc.contains("INADDR_ANY") || cmdCppSrc.contains("SO_REUSEPORT") || !cmdCppSrc.contains("CPU_ZERO(&mask)")) {
            throw new AssertionError("cmdentrypoint.cpp must bind to INADDR_LOOPBACK without SO_REUSEPORT and initialize CPU_ZERO(&mask)");
        }
        File actCpp = resolveProjectFile("lorie/src/main/cpp/lorie/activity.cpp");
        String actCppSrc = new String(Files.readAllBytes(actCpp.toPath()), StandardCharsets.UTF_8);
        if (actCppSrc.contains("Sending unicode event:") || actCppSrc.contains("Parsing text:") || actCppSrc.contains("Clipboard content (")) {
            throw new AssertionError("activity.cpp must not log keystrokes or clipboard contents to logcat");
        }
        File lorieViewFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/LorieView.java");
        String lorieViewSrc = new String(Files.readAllBytes(lorieViewFile.toPath()), StandardCharsets.UTF_8);
        if (lorieViewSrc.contains("sending clipboard contents:")
                || lorieViewSrc.contains("clipboard.setPrimaryClip(")
                || !lorieViewSrc.contains("setClipboardSyncEnabled(mNativeContext, false, false)")) {
            throw new AssertionError("LorieView.java must enforce one-way clipboard (Android -> ASYCUDA only) and zero clipboard logging");
        }
        File libXlorieSo = resolveProjectFile("lorie/src/main/jniLibs/arm64-v8a/libXlorie.so");
        if (!libXlorieSo.exists() || libXlorieSo.length() == 0) {
            throw new AssertionError("libXlorie.so binary must exist");
        }
        File filePathsXml = resolveProjectFile("lorie/src/main/res/xml/asycuda_file_paths.xml");
        String filePathsSrc = new String(Files.readAllBytes(filePathsXml.toPath()), StandardCharsets.UTF_8);
        if (filePathsSrc.contains("path=\".\"")) {
            throw new AssertionError("asycuda_file_paths.xml must not expose root cache-path '.'");
        }
        File mgrFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaStandaloneManager.java");
        String mgrSrc = new String(Files.readAllBytes(mgrFile.toPath()), StandardCharsets.UTF_8);
        if (mgrSrc.contains("|| true") || mgrSrc.contains("-Dasycuda.bridge.token=")) {
            throw new AssertionError("AsycudaStandaloneManager.java must not contain '|| true' in UID check or pass bridge token via command-line argv");
        }
        if (mgrSrc.contains("contents/lorie-app/src/main/assets/known-errors-catalog.json")
                || !mgrSrc.contains("known-errors-catalog-remote.json")
                || !mgrSrc.contains(".bootpatch_v4_4_0")) {
            throw new AssertionError("AsycudaStandaloneManager.java must reference known-errors-catalog-remote.json and .bootpatch_v4_4_0 (R7-01/R7-03/R11-03)");
        }

        File fbFile = resolveProjectFile("lorie/src/main/java/com/termux/x11/AsycudaFirebaseManager.java");
        String fbSrc = new String(Files.readAllBytes(fbFile.toPath()), StandardCharsets.UTF_8);
        if (fbSrc.contains("installedFromPlayStore")
                || !fbSrc.contains("validateKnownErrorsCatalogJson(catalogJson)")
                || !fbSrc.contains("MAX_FIRESTORE_RESPONSE_BYTES")
                || !fbSrc.contains("sanitizeTrustedAnnouncementUrl")
                || !fbSrc.contains("setInstanceFollowRedirects(false)")
                || !fbSrc.contains("AsycudaSecurityValidator.redactSensitiveLogContent")) {
            throw new AssertionError("AsycudaFirebaseManager.java must enforce R9-01..R9-04 hardening controls");
        }
    }

    private static File resolveProjectFile(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f;
        File fallback = new File("asycuda-apk/" + relativePath);
        if (fallback.exists()) return fallback;
        throw new IllegalStateException("Required project file not found: " + relativePath);
    }

    private static void assertNotNull(Object val, String message) {
        if (val == null) {
            throw new AssertionError(message);
        }
    }

    private static void assertNull(Object val, String message) {
        if (val != null) {
            throw new AssertionError(message + " (got: " + val + ")");
        }
    }
}
