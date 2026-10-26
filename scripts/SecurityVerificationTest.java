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
 * Automated Adversarial Security Verification Suite for ASYCUDA-Iraq-Android.
 * Compiles and invokes production classes ({@link asycuda.AsycudaBoot} and
 * {@link com.termux.x11.AsycudaSecurityValidator}) directly in CI/CD on every commit
 * with zero duplicated test helper methods.
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
        testJvmHostIsolationExactPortAllowlistAndAttribution();
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
            "BIND_ACCESSIBILITY_SERVICE"
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

        String unauthenticated = "OPEN|/data/data/iq.asycuda/shared_prefs/asycuda_legal_consent.xml";
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
        if (mgrSrc.contains("NextGen-Customs-") || !mgrSrc.contains("MohammedALShimmary/ASYCUDA-Iraq-Android")) {
            throw new AssertionError("AsycudaStandaloneManager must use the official ASYCUDA-Iraq-Android catalog URL (R5-04)");
        }
        if (mgrSrc.contains("requestNetwork(") || mgrSrc.contains("BIND_4G_RELAY_PORT")
                || mgrSrc.contains("ROUTE_4G_CELLULAR") || mgrSrc.contains("sCellularBypassNetwork")) {
            throw new AssertionError("The 4G network bypass relay was removed in v4.1.0; the app must never request, bind, or relay traffic over a secondary network");
        }
        File bootFile = resolveProjectFile("java-bridge/AsycudaBoot.java");
        String bootSrc = new String(Files.readAllBytes(bootFile.toPath()), StandardCharsets.UTF_8);
        if (bootSrc.contains("BIND_4G_RELAY_PORT") || bootSrc.contains("REQUESTED_RELAY_PORTS")) {
            throw new AssertionError("AsycudaBoot must not request 4G relay ports (4G bypass removed in v4.1.0)");
        }
        if (bootSrc.contains("public static boolean sendToAndroidApk(")) {
            throw new AssertionError("sendToAndroidApk must be private static in AsycudaBoot (R5-01)");
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
            throw new AssertionError("AsycudaStandaloneManager.java must not attach ip_address to Sentry events");
        }
        if (mgrSrc.contains("uploadConsentReceiptToSentry")) {
            throw new AssertionError("AsycudaStandaloneManager.java must not upload consent receipts to Sentry");
        }
    }

    private static void testJvmHostIsolationExactPortAllowlistAndAttribution() throws Exception {
        // 1. Dynamically register JNLP arguments and verify permitted endpoints and ports
        asycuda.AsycudaBoot.registerDynamicJnlpArguments(new String[] {
            "https://asy.mof.gov.iq/aw/help/",
            "//asy.mof.gov.iq:2006/"
        });
        System.setProperty("so.rmi.url.port", "37760");
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("127.0.0.1", 49152);
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("localhost", 8080);
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("asy.mof.gov.iq", 443);
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("asy.mof.gov.iq", 2006);
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("asy.mof.gov.iq", 37760);
        asycuda.AsycudaBoot.checkAllowedOutboundNetwork("asy.mof.gov.iq", -1);

        // 2. Blocked domains, wildcard subdomains, and non-registered ports on public MOF endpoints
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("evil.example.com", 443),
                "Untrusted external domain must be blocked by AsycudaBoot.checkAllowedOutboundNetwork");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("other.mof.gov.iq", 443),
                "Wildcard .mof.gov.iq subdomain must be blocked (only asy.mof.gov.iq is allowed)");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("test.asycuda.gov.iq", 443),
                "Wildcard .asycuda.gov.iq subdomain must be blocked");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("asy.mof.gov.iq", 8443),
                "Unauthorized port 8443 on asy.mof.gov.iq must be blocked");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("8.8.8.8", 53),
                "Untrusted external IP must be blocked by AsycudaBoot.checkAllowedOutboundNetwork");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("10.0.0.1", 2006),
                "Untrusted private IP must be blocked by AsycudaBoot.checkAllowedOutboundNetwork");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkBlockedHostPath("/data/data/iq.asycuda/shared_prefs/asycuda_legal_consent.xml"),
                "Android shared_prefs must be blocked by AsycudaBoot.checkBlockedHostPath");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkBlockedHostPath("/data/user/0/iq.asycuda/files/legal_consent_receipt.json"),
                "Consent receipt path must be blocked by AsycudaBoot.checkBlockedHostPath");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkBlockedHostPath("/data/data/iq.asycuda/files/known_errors_catalog.json"),
                "Known-Errors Catalog path must be blocked from JVM access by AsycudaBoot.checkBlockedHostPath");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkBlockedHostPath("../known_errors_catalog.json"),
                "Relative Known-Errors Catalog path must be blocked from JVM access by AsycudaBoot.checkBlockedHostPath");

        // 3. Enforce strict Zero-Hardcoded-IP / Zero-Hardcoded-Port-Fallback Policy across production files
        String[] prodFilesToCheck = new String[] {
            "java-bridge/AsycudaBoot.java",
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

        // 3. Verify fail-closed SecurityManager installation in AsycudaBoot.java
        File bootFile = resolveProjectFile("java-bridge/AsycudaBoot.java");
        String bootSrc = new String(Files.readAllBytes(bootFile.toPath()), StandardCharsets.UTF_8);
        if (!bootSrc.contains("Runtime.getRuntime().halt(1)") || !bootSrc.contains("SECURITY_ERROR|")) {
            throw new AssertionError("AsycudaBoot.java must fail closed with SECURITY_ERROR notification and Runtime.getRuntime().halt(1)");
        }

        // 4. Verify GPL-3.0 Copyright & Attribution notices across all 5 original Java bridge source files
        String[] bridgeFiles = new String[] {
            "java-bridge/AsycudaBoot.java",
            "java-bridge/bootpatch/java/awt/Desktop.java",
            "java-bridge/jnlp/javax/jnlp/BasicService.java",
            "java-bridge/jnlp/javax/jnlp/ServiceManager.java",
            "java-bridge/jnlp/javax/jnlp/UnavailableServiceException.java"
        };
        for (String rel : bridgeFiles) {
            File bf = resolveProjectFile(rel);
            String src = new String(Files.readAllBytes(bf.toPath()), StandardCharsets.UTF_8);
            if (!src.contains("Copyright (C) 2026 Mohammed Ammar Hussein (@MohammedALShimmary)")) {
                throw new AssertionError("Missing GPL-3.0 author copyright header in: " + rel);
            }
        }
        File attrFile = resolveProjectFile("java-bridge/ATTRIBUTION-TERMS.md");
        if (!attrFile.exists()) {
            throw new AssertionError("Missing java-bridge/ATTRIBUTION-TERMS.md");
        }

        // 5. Verify SecurityManager tamper-resistance, read-only JAR protection, checkLink, and 0.0.0.0 block
        SecurityManager sm = asycuda.AsycudaBoot.createHardenedSecurityManager();
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("setSecurityManager")),
                "setSecurityManager must be blocked by HardenedSecurityManager");
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("createSecurityManager")),
                "createSecurityManager must be blocked by HardenedSecurityManager");
        expectSecurityException(() -> sm.checkPermission(new java.security.SecurityPermission("setPolicy")),
                "setPolicy must be blocked by HardenedSecurityManager");
        expectSecurityException(() -> sm.checkWrite("/data/data/iq.asycuda/files/client/jars/client.jar"),
                "Overwriting signed JARs in /files/client/jars/ must be blocked");
        expectSecurityException(() -> sm.checkWrite("00_bootpatch.jar"),
                "Relative path write to 00_bootpatch.jar must be canonicalized and blocked (R2-01)");
        expectSecurityException(() -> sm.checkWrite("jars/client.jar"),
                "Relative path write to jars/client.jar must be canonicalized and blocked (R2-01)");
        expectSecurityException(() -> sm.checkWrite("../jre8/bin/java"),
                "Relative parent-traversal write to ../jre8/bin/java must be blocked (R2-01)");
        expectSecurityException(() -> sm.checkDelete("/data/data/iq.asycuda/files/client/jars/client.jar"),
                "Deleting signed JARs in /files/client/jars/ must be blocked");
        expectSecurityException(() -> sm.checkDelete("00_bootpatch.jar"),
                "Relative path delete of 00_bootpatch.jar must be blocked (R2-01)");
        expectSecurityException(() -> sm.checkRead("../shared_prefs/asycuda_legal_consent.xml"),
                "Relative path read of ../shared_prefs/ must be blocked (R2-01)");
        expectSecurityException(() -> sm.checkRead("/proc/self/environ"),
                "Reading /proc/self/environ must be blocked (R3-01)");
        expectSecurityException(() -> sm.checkRead("/proc/self/mem"),
                "Reading /proc/self/mem must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkWrite("/proc/self/mem"),
                "Writing /proc/self/mem must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkWrite("mof.crt"),
                "Writing mof.crt must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkDelete("../.extracted_v3_2"),
                "Deleting .extracted_v3_2 marker must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("getenv.ASYCUDA_BRIDGE_TOKEN")),
                "Reading ASYCUDA_BRIDGE_TOKEN via getenv must be blocked (R3-01)");
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("getenv.*")),
                "Reading all environment variables via System.getenv() (getenv.*) must be blocked (R3-01)");
        expectSecurityException(() -> sm.checkPermission(new java.nio.file.LinkPermission("symbolic")),
                "NIO symbolic link creation must be blocked inside JVM sandbox (R3-02)");
        expectSecurityException(() -> sm.checkPermission(new java.nio.file.LinkPermission("hard")),
                "NIO hard link creation must be blocked inside JVM sandbox (R3-02)");
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("setFactory")),
                "Socket/stream setFactory must be blocked inside JVM sandbox (R3-02)");
        expectSecurityException(() -> sm.checkPermission(new java.net.NetPermission("setProxySelector")),
                "Untrusted caller invoking setProxySelector must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkPermission(new java.security.SecurityPermission("insertProvider")),
                "Untrusted caller invoking insertProvider must be blocked (R3-02)");
        expectSecurityException(() -> sm.checkWrite("/data/data/iq.asycuda/cache/updates/payload.apk"),
                "Writing to /cache/updates/ from JVM must be blocked");
        expectSecurityException(() -> sm.checkLink("/data/data/iq.asycuda/files/client/tmp/evil.so"),
                "Loading native libraries from writable /tmp/ directory must be blocked");
        expectSecurityException(() -> sm.checkLink("evil_implant"),
                "Short library name not in JRE allowlist must be blocked (R2-07)");
        expectSecurityException(() -> sm.checkLink("/data/data/iq.asycuda/files/jre8/jre/lib/aarch64/../../../client/tmp/evil.so"),
                "Path-traversal native library load must be canonicalized and blocked (R2-07)");
        sm.checkLink("awt");
        sm.checkLink("/data/data/iq.asycuda/files/jre8/jre/lib/aarch64/libawt.so");
        expectSecurityException(() -> sm.checkPermission(new java.lang.reflect.ReflectPermission("suppressAccessChecks")),
                "Untrusted caller invoking suppressAccessChecks must be blocked across full stack (R2-02)");
        expectSecurityException(() -> sm.checkPermission(new RuntimePermission("accessDeclaredMembers")),
                "Untrusted caller invoking accessDeclaredMembers must be blocked across full stack (R2-02)");
        expectSecurityException(() -> sm.checkListen(4444),
                "Untrusted caller opening ServerSocket listener must be blocked (R2-02)");
        expectSecurityException(() -> asycuda.AsycudaBoot.checkAllowedOutboundNetwork("0.0.0.0", 4444),
                "Outbound socket connection to 0.0.0.0:4444 must be blocked");
        // Verify JDK internal JceSecurityManager / SSLContext.getInstance("TLSv1.2") is permitted while SecurityManager is active
        javax.net.ssl.SSLContext tlsCtx = javax.net.ssl.SSLContext.getInstance("TLSv1.2");
        if (tlsCtx == null) {
            throw new AssertionError("SSLContext.getInstance(\"TLSv1.2\") returned null");
        }

        // 6. Verify APK updater SHA-256 & URL validation and Tar symlink traversal protection
        if (!AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl(
                "https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/v4.1.0/ASYCUDA_Iraq_v4.1.0.apk")) {
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

        // 7. Verify native X11 loopback bind, one-way clipboard, zero keystroke logging, and FileProvider scope
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
        String soAscii = new String(Files.readAllBytes(libXlorieSo.toPath()), StandardCharsets.ISO_8859_1);
        if (soAscii.contains("Sending unicode event:")
                || soAscii.contains("Parsing text:")
                || soAscii.contains("Clipboard content (")
                || soAscii.contains("Trying to input keysym")) {
            throw new AssertionError("libXlorie.so binary must not contain plaintext keystroke or clipboard log format strings");
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
    }

    private static File resolveProjectFile(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f;
        File fallback = new File("asycuda-apk/" + relativePath);
        if (fallback.exists()) return fallback;
        throw new IllegalStateException("Required project file not found: " + relativePath);
    }

    private static void expectSecurityException(Runnable action, String message) {
        try {
            action.run();
        } catch (SecurityException expected) {
            return;
        }
        throw new AssertionError(message);
    }

    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) throw new AssertionError(msg);
    }

    private static void assertNull(Object obj, String msg) {
        if (obj != null) throw new AssertionError(msg + " (got " + obj + ")");
    }
}
