/*
 * Copyright (C) 2026 Mohammed Ammar Hussein (@MohammedALShimmary)
 * Licensed under GNU GPL-3.0; see the repository LICENSE file.
 */
package com.termux.x11;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Pure-Java production security validator for ASYCUDA World Iraq Android.
 * Contains zero {@code android.*} framework imports so that both the Android runtime
 * ({@link AsycudaStandaloneManager}) and the automated CI/CD adversarial test suite
 * ({@code scripts/SecurityVerificationTest.java}) compile and execute the exact same
 * production validation methods without helper duplication.
 */
@SuppressWarnings({"java:S5852", "java:S5843", "java:S5998", "java:S6353"})
public final class AsycudaSecurityValidator {

    public static final String SCHEME_HTTPS = "https";
    public static final String ALLOWED_MOF_HOST = "asy.mof.gov.iq";
    public static final String MOF_CODEBASE_URL = "https://asy.mof.gov.iq/aw/";
    public static final String REGEX_UNSAFE_FILENAME = "[^a-zA-Z0-9._-]";
    public static final String PINNED_MOF_SOCODE_CERT_SHA256 =
            "5614dc62a20653686934a216439c8c779cd84d58ccbc37e85dda33433f9e972d";
    public static final String PINNED_APK_RELEASE_CERT_SHA256 =
            "d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023";

    private AsycudaSecurityValidator() {
        // Pure-Java static security validator
    }

    /**
     * Resolves a JNLP {@code <jar href="..."/>} reference strictly against
     * {@code https://asy.mof.gov.iq/aw/} and enforces an explicit HTTPS scheme,
     * exact hostname ({@code asy.mof.gov.iq}), port ({@code -1} or {@code 443}),
     * {@code /aw/} path prefix, {@code .jar} extension, and zero path traversal.
     */
    public static String resolveTrustedMofJarUrl(String href) {
        if (href == null || href.trim().isEmpty()) return null;
        String trimmed = href.trim();
        if (trimmed.contains("..") || trimmed.contains("\\")) return null;
        try {
            URL base = new URL(MOF_CODEBASE_URL);
            URL resolved = trimmed.contains("://")
                    ? new URL(trimmed)
                    : new URL(base, trimmed.replaceFirst("^/+", ""));
            if (!SCHEME_HTTPS.equalsIgnoreCase(resolved.getProtocol())) {
                return null;
            }
            if (!ALLOWED_MOF_HOST.equalsIgnoreCase(resolved.getHost())) {
                return null;
            }
            int port = resolved.getPort();
            int expectedDefaultPort = base.getPort() == -1 ? base.getDefaultPort() : base.getPort();
            if (port != -1 && port != expectedDefaultPort) {
                return null;
            }
            String path = resolved.getPath();
            if (path == null || !path.startsWith("/aw/") || !path.endsWith(".jar") || path.contains("..")) {
                return null;
            }
            return resolved.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifies that a downloaded JAR is a well-formed ZIP/JAR archive, enforces Java cryptographic
     * signature verification ({@code JarFile(..., true)}) on all entries, and requires that both
     * the {@code META-INF/SOCODE.RSA} PKCS#7 block and every non-{@code META-INF/} entry in the archive
     * are cryptographically signed by the pinned UNCTAD/SOClass X.509 code-signing certificate.
     */
    public static boolean verifyDownloadedJarIntegrity(File jarFile) {
        return verifyJarSignedByPinnedCertificate(jarFile, PINNED_MOF_SOCODE_CERT_SHA256);
    }

    public static boolean verifyJarSignedByPinnedCertificate(File jarFile, String expectedCertSha256) {
        if (jarFile == null || !jarFile.exists() || jarFile.length() < 22) return false;
        if (expectedCertSha256 == null || expectedCertSha256.isEmpty()) return false;
        try (JarFile jf = new JarFile(jarFile, true)) {
            JarEntry rsaEntry = jf.getJarEntry("META-INF/SOCODE.RSA");
            JarEntry sfEntry = jf.getJarEntry("META-INF/SOCODE.SF");
            if (rsaEntry == null || sfEntry == null) {
                return false;
            }

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            if (!doesPkcs7ContainPinnedSigner(jf, rsaEntry, sha256, expectedCertSha256)) {
                return false;
            }

            Enumeration<JarEntry> entries = jf.entries();
            int nonMetaEntryCount = 0;
            byte[] buf = new byte[16384];
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                drainJarEntryStream(jf, je, buf);
                if (isNonMetaInfFileEntry(je)) {
                    nonMetaEntryCount++;
                    if (!isEntrySignedByPinnedCert(je, sha256, expectedCertSha256)) {
                        return false;
                    }
                }
            }
            return nonMetaEntryCount > 0;
        } catch (Exception invalidJar) {
            return false;
        }
    }

    private static boolean doesPkcs7ContainPinnedSigner(
            JarFile jf, JarEntry rsaEntry, MessageDigest sha256, String expectedCertSha256)
            throws java.security.GeneralSecurityException, java.io.IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream rsaIn = jf.getInputStream(rsaEntry)) {
            Collection<? extends Certificate> pkcs7Certs = cf.generateCertificates(rsaIn);
            if (pkcs7Certs == null) return false;
            for (Certificate c : pkcs7Certs) {
                if (expectedCertSha256.equalsIgnoreCase(sha256Hex(sha256, c.getEncoded()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void drainJarEntryStream(JarFile jf, JarEntry je, byte[] buf) throws java.io.IOException {
        try (InputStream is = jf.getInputStream(je)) {
            while (is.read(buf) != -1) {
                // Draining the entry stream triggers JarVerifier digest & signature validation
            }
        }
    }

    private static boolean isNonMetaInfFileEntry(JarEntry je) {
        return !je.isDirectory() && !je.getName().toUpperCase(Locale.US).startsWith("META-INF/");
    }

    private static boolean isEntrySignedByPinnedCert(
            JarEntry je, MessageDigest sha256, String expectedCertSha256)
            throws java.security.cert.CertificateEncodingException {
        Certificate[] entryCerts = je.getCertificates();
        if (entryCerts == null || entryCerts.length == 0) {
            return false;
        }
        return expectedCertSha256.equalsIgnoreCase(sha256Hex(sha256, entryCerts[0].getEncoded()));
    }

    public static String sha256Hex(MessageDigest sha256, byte[] data) {
        sha256.reset();
        byte[] digest = sha256.digest(data);
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            hex.append(String.format(Locale.US, "%02x", b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * Strictly scrubs any ASYCUDA login credentials, usernames, passwords, session IDs,
     * cookies, authorization headers, TIN/declarant identifiers, XML auth tags, and tokens
     * (including both quoted JSON strings and unquoted key-value pairs) before any log text
     * is displayed in RAM or included in a voluntary diagnostic report.
     */
    public static String redactSensitiveLogContent(String raw, String bridgeAuthToken) {
        if (raw == null || raw.isEmpty()) return "";
        String redacted = (bridgeAuthToken != null && !bridgeAuthToken.isEmpty())
                ? raw.replace(bridgeAuthToken, "[REDACTED_BRIDGE_TOKEN]")
                : raw;
        // Scrub URL user:password@host credentials
        redacted = redacted.replaceAll("(?i)(https?+://)[^/\\s:@]++:[^/\\s@]++@", "$1[REDACTED_AUTH]@");
        // Scrub XML/SOAP tags that could hold credentials, usernames, TINs, or tokens
        redacted = redacted.replaceAll(
                "(?is)<(password|passwd|pwd|user|username|login|secret|token|jsessionid|sessionid|tin|declarant|credential|pin|otp)[^>]*+>.*?</\\1>",
                "<$1>[REDACTED]</$1>");
        // Scrub Bearer / Basic authorization tokens
        redacted = redacted.replaceAll("(?i)\\b(bearer|basic)\\s++[^\\s,;\"'&<>\\]}]++", "$1 [REDACTED]");
        // Scrub SOClassCache username folder paths (e.g. .SOClassCache/202604030102004730/User7015W/)
        redacted = redacted.replaceAll("(\\.SOClassCache/[^/\\s]++/)[^/\\s]++", "$1[REDACTED_USER]");
        // Scrub "Tin code received : <value>" lines in asycuda.log
        redacted = redacted.replaceAll("(?i)(tin\\s++code(?:\\s++received)?+\\s*+[:=]\\s*+)\\S++", "$1\"[REDACTED]\"");
        // Scrub key=value / key: value / JSON "key": "value" patterns for sensitive keys (both quoted and unquoted values)
        redacted = redacted.replaceAll(
                "(?i)([\"']?+(?:password|passwd|pwd|pass|username|user_name|userid|login|login_id|declarant|declarant_id|tin|tax_id|secret|client_secret|token|auth_token|access_token|refresh_token|jsessionid|sessionid|session_id|cookie|set-cookie|authorization|bearer|api_key|apikey|pin|otp|cvv|credential|private_key|account_no|iban|customs_decl|declaration_no|sad_no|awb_no|bl_no)[\"']?+\\s*+[:=]\\s*+)(?:\"(?:\\\\.|[^\"\\\\])*+\"|'(?:\\\\.|[^'\\\\])*+'|[^\\s,;\"'&<>\\]}]++)",
                "$1\"[REDACTED]\"");
        return redacted;
    }

    public static String sanitizeBridgeFileName(String requestedPath) {
        if (requestedPath == null || requestedPath.isEmpty()) return "";
        String normalized = requestedPath.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String rawName = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        return rawName.replaceAll(REGEX_UNSAFE_FILENAME, "_");
    }

    /**
     * Validates that an APK update download URL uses HTTPS and points strictly to an official
     * GitHub Releases download path ({@code github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/download/...})
     * or the official GitHub Release asset CDN ({@code objects.githubusercontent.com} / {@code release-assets.githubusercontent.com}).
     */
    public static boolean isTrustedGitHubReleaseAssetUrl(String urlStr) {
        if (urlStr == null || urlStr.trim().isEmpty()) return false;
        try {
            URL u = new URL(urlStr.trim());
            if (!SCHEME_HTTPS.equalsIgnoreCase(u.getProtocol())) return false;
            String host = u.getHost();
            if (host == null) return false;
            String lowHost = host.toLowerCase(Locale.US);
            String path = u.getPath() != null ? u.getPath().toLowerCase(Locale.US) : "";
            if (path.contains("..")) return false;
            if ("github.com".equals(lowHost)) {
                return path.startsWith("/mohammedalshimmary/asycuda-iraq-android/releases/download/");
            }
            return "objects.githubusercontent.com".equals(lowHost)
                    || "release-assets.githubusercontent.com".equals(lowHost);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that an expected SHA-256 hex digest is non-empty and consists of exactly 64 hex characters.
     */
    public static boolean isValidSha256Hex(String sha256) {
        if (sha256 == null) return false;
        String trimmed = sha256.trim();
        return trimmed.length() == 64 && isHexRange(trimmed, 0, 64);
    }

    /**
     * Extracts a 64-character hex SHA-256 digest from GitHub release notes body if the asset's
     * {@code digest} field was not populated by the API.
     */
    public static String extractSha256FromReleaseBody(String body) {
        if (body == null || body.length() < 64) return null;
        int n = body.length();
        for (int i = 0; i <= n - 64; i++) {
            if (isHexRange(body, i, 64) && isBoundedHexToken(body, i, 64)) {
                return body.substring(i, i + 64).toLowerCase(Locale.US);
            }
        }
        return null;
    }

    private static boolean isHexRange(String s, int offset, int len) {
        for (int j = 0; j < len; j++) {
            if (!isHexChar(s.charAt(offset + j))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static boolean isBoundedHexToken(String s, int offset, int len) {
        boolean leftOk = (offset == 0) || !Character.isLetterOrDigit(s.charAt(offset - 1));
        boolean rightOk = (offset + len == s.length()) || !Character.isLetterOrDigit(s.charAt(offset + len));
        return leftOk && rightOk;
    }

    /**
     * Validates that a tar archive entry or hardlink target resolves strictly within {@code destDir}.
     */
    public static boolean isSafeTarEntryPath(File destDir, File entryFile) {
        if (destDir == null || entryFile == null) return false;
        try {
            String destCanon = destDir.getCanonicalPath();
            String destCanonPrefix = destCanon.endsWith(File.separator) ? destCanon : destCanon + File.separator;
            String entryCanon = entryFile.getCanonicalPath();
            return entryCanon.equals(destCanon) || entryCanon.startsWith(destCanonPrefix);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that a tar archive symlink target is strictly relative (never starting with {@code /})
     * and stays strictly within {@code destDir} after canonicalization.
     */
    public static boolean isSafeTarSymlinkTarget(File destDir, File symlinkFile, String linkTarget) {
        if (destDir == null || symlinkFile == null || linkTarget == null || linkTarget.isEmpty()) {
            return false;
        }
        if (linkTarget.startsWith("/") || linkTarget.startsWith("\\")) {
            return false;
        }
        try {
            if (!isSafeTarEntryPath(destDir, symlinkFile)) {
                return false;
            }
            File rawParent = symlinkFile.getParentFile();
            if (rawParent == null) return false;
            File linkParent = rawParent.getCanonicalFile();
            if (!isSafeTarEntryPath(destDir, linkParent)) {
                return false;
            }
            String destCanon = destDir.getCanonicalPath();
            String destCanonPrefix = destCanon.endsWith(File.separator) ? destCanon : destCanon + File.separator;
            File resolvedTarget = new File(linkParent, linkTarget);
            String targetCanon = resolvedTarget.getCanonicalPath();
            String targetNorm = resolvedTarget.getAbsoluteFile().toPath().normalize().toString();
            return (targetCanon.equals(destCanon) || targetCanon.startsWith(destCanonPrefix))
                    && (targetNorm.equals(destCanon) || targetNorm.startsWith(destCanonPrefix));
        } catch (Exception e) {
            return false;
        }
    }
}
