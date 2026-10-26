package com.termux.x11;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.system.Os;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tukaani.xz.XZInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

@SuppressWarnings({"java:S3776", "java:S1141", "java:S2696", "java:S135", "java:S1075", "java:S1192", "java:S3063", "java:S9391", "java:S9439"})
public class AsycudaStandaloneManager {

    private static final String MOF_CODEBASE_URL = "https://asy.mof.gov.iq/aw/";
    private static final String MOF_JNLP_URL     = "https://asy.mof.gov.iq/aw/AWIQWorld.jnlp";
    private static final String GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/latest";
    public static final int REQ_PICK_FEEDBACK_IMAGE = 47892;
    public static final int REQ_PICK_WORKSPACE_FILE = 47893;

    private static final String FILE_PROVIDER_AUTH = "iq.asycuda.fileprovider";
    private static final String SANS_SERIF_MEDIUM = "sans-serif-medium";
    private static final String FLOATING_PILL_TAG = "asycuda_floating_pill";
    private static final String PREF_OPACITY_EK_BAR = "opacityEKBar";
    private static final String PREF_BACK_BUTTON_ACTION = "backButtonAction";
    private static final String ACTION_DOUBLE_BACK_EXIT = "double_back_exit";
    private static final String ACTION_OPEN_PREFS = "open preferences";
    private static final String ACTION_NO_ACTION = "no action";
    private static final String MIME_PDF = "application/pdf";
    private static final String MIME_JSON = "application/json";
    private static final String MIME_ZIP = "application/zip";
    private static final String KEY_CONTENT_TYPE = "content_type";
    private static final String KEY_ATTACHMENT_TYPE = "attachment_type";
    private static final String VAL_EVENT_ATTACHMENT = "event.attachment";
    private static final String TYPE_ATTACHMENT = "attachment";
    private static final String KEY_FILENAME = "filename";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_DEVICE_MODEL = "device_model";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String BOOT_JAR_REL = "jars/00_boot.jar";
    private static final String JNLP_JAR_REL = "jars/00_jnlp.jar";
    private static final String BOOTPATCH_JAR_NAME = "00_bootpatch.jar";
    private static final String PREFIX_JARS = "jars/";
    private static final String DIR_PRINTS = "prints";
    private static final String DIR_WORKSPACE = DIR_PRINTS;
    private static final String DIR_LEGACY_WORKSPACE = "workspace";
    private static final String DIR_UPDATES_CACHE = "updates";
    private static final String DIR_CLIENT = "client";
    private static final String DIR_USR_TMP = "usr/tmp";
    private static final String ASSET_ASYCUDA_IRAQ_TAR = "asycuda-iraq.tar.xz";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String FILE_AWIQ_JNLP = "AWIQWorld.jnlp";
    private static final String FILE_CLASSPATH = "classpath.txt";
    private static final String FILE_ASYCUDA_LOG = "asycuda.log";
    private static final String FILE_CLIENT_LOG = "client.log";
    private static final String FILE_SCREEN_CAPTURE = "screen_capture.jpg";
    private static final String REGEX_UNSAFE_FILENAME = "[^a-zA-Z0-9._-]";
    private static final int MODE_OWNER_RWX = 448;
    private static final int MASK_EXEC_BITS = 73;

    /**
     * Official Sectigo / ZeroSSL intermediate and cross-signed root CA certificates
     * chaining `asy.mof.gov.iq` to the standard Android system `USERTrust RSA Certification Authority`.
     */
    private static final String ZEROSSL_RSA_DV_CA2_PEM =
            "-----BEGIN CERTIFICATE-----\n"
            + "MIIGITCCBAmgAwIBAgIRANCpCCVfjlDl8zltERRwKjcwDQYJKoZIhvcNAQEMBQAw\n"
            + "XzELMAkGA1UEBhMCR0IxGDAWBgNVBAoTD1NlY3RpZ28gTGltaXRlZDE2MDQGA1UE\n"
            + "AxMtU2VjdGlnbyBQdWJsaWMgU2VydmVyIEF1dGhlbnRpY2F0aW9uIFJvb3QgUjQ2\n"
            + "MB4XDTI1MDkyNDAwMDAwMFoXDTM1MDkyMzIzNTk1OVowRjELMAkGA1UEBhMCQVQx\n"
            + "FTATBgNVBAoTDFplcm9TU0wgR21iSDEgMB4GA1UEAxMXWmVyb1NTTCBSU0EgRFYg\n"
            + "U1NMIENBIDIwggGiMA0GCSqGSIb3DQEBAQUAA4IBjwAwggGKAoIBgQCnXX3Qm/G+\n"
            + "ujylvYIkhJ53ZZ7XM03vXY03sCnO9VWjwMsV0qCQMlvhuRiJwrm4M5jgGehxIiCR\n"
            + "1qT0AyL2rHTWJR07vjJzuNmp7uoKu3HKwixBk9QuXD5aliO8/EDbzdZDcG/Hm5pC\n"
            + "mOeusLtds/UE/Iq24nw5WpcgZE5Ly+F/yCYDuEa7hXLJAPM5SecJIblG1OQ3ukMl\n"
            + "HHNBbDxBpGWTyDudd0DTed0NTgCPs1t8RZPhW9Gt/8nDwFX0pQ4eHfPEB8c6eNl2\n"
            + "sRr2Afp1YErakR+53yEX2SXc2Kz0fbTlUc+To0ULGcJiWNyZwj//DTZ+M4xxsT2T\n"
            + "qjQ4Xfvm2EUTymrXDrh1Pm/wkBouu860c6eeQfNlKlccUyHOSeKCtPIWreMvH3Be\n"
            + "Ydeu3DwI8lefn/VUhSB2Bbz7hX3qz3oMmtSTmWhTnobyKlx1L2b/oloaqpy1cBc/\n"
            + "QiLRSOptYGPjZtX0pRrTVKQXeP2rPUk0y5q/40WRpugSlHCX6aceWnsCAwEAAaOC\n"
            + "AW8wggFrMB8GA1UdIwQYMBaAFFZzWGSV+ZIasBIqBGJ5oUAViCFJMB0GA1UdDgQW\n"
            + "BBRLvvp2hCNEBLnOvjFv6fUyBv8MVzAOBgNVHQ8BAf8EBAMCAYYwEgYDVR0TAQH/\n"
            + "BAgwBgEB/wIBADATBgNVHSUEDDAKBggrBgEFBQcDATATBgNVHSAEDDAKMAgGBmeB\n"
            + "DAECATBUBgNVHR8ETTBLMEmgR6BFhkNodHRwOi8vY3JsLnNlY3RpZ28uY29tL1Nl\n"
            + "Y3RpZ29QdWJsaWNTZXJ2ZXJBdXRoZW50aWNhdGlvblJvb3RSNDYuY3JsMIGEBggr\n"
            + "BgEFBQcBAQR4MHYwTwYIKwYBBQUHMAKGQ2h0dHA6Ly9jcnQuc2VjdGlnby5jb20v\n"
            + "U2VjdGlnb1B1YmxpY1NlcnZlckF1dGhlbnRpY2F0aW9uUm9vdFI0Ni5wN2MwIwYI\n"
            + "KwYBBQUHMAGGF2h0dHA6Ly9vY3NwLnNlY3RpZ28uY29tMA0GCSqGSIb3DQEBDAUA\n"
            + "A4ICAQCJ/3v2/vdexHsdVyXL9aCTQE01YXl23866TVM/LgRpRW+kneZXXZxP0hy4\n"
            + "GnvlqUcxTq97B6qPdQcQxQxpGne7CRn0nWauzqieMcJzYl3fDC2Q/ANyPhyrbwCI\n"
            + "zx9EsRrgfjvuJCaUMtlfYpKqBUYiPOCPAN0HdrLD5hU6oV1tvWVsUzTA43skC3uH\n"
            + "wQM5YPIk0NDJFw3NhQPOIOwbq09T+SYSEZvsJ3t4sA4H3gh03RETNaAwTcTNS/+u\n"
            + "1tAeQUZZmKQyLWYLyoxvbISp/MFr9xqhDqrpAurYVNeiLJ5+4/WZPml20yNZjcxV\n"
            + "KKqRYdEurl8rmI2toCnCDWiEcTDvoYGtz60eYIt7VJID4DrCjTxAWTWh2T5ag2pK\n"
            + "ryNJGt8BFGLtjeD774SxAFn5MGYBVvEK4LXmCjVX78pb6/0Dceo71dlQUK68yftb\n"
            + "+yTeyoqjwgk2L8vNSrkj6UTkvvqXSONFuVU7bvC0O/9bi5MXBv7QUivMNxDsTtaT\n"
            + "IZO7PsSCRmLraQM2EPBgbNL9lRSEYi4Hj+NicT/e87pbhv88k0oec9xGcnkcvZpN\n"
            + "sJBz78mkZfeFIIjh02e2y9ke/Fqw1FbdhHtU2myaFnX0sRyGLmI/vzXSwyvT+Kxl\n"
            + "Wx8FV64z2PBdwd0vXRaAMXomGC0M1vQa5fBDn4dimzewsSiphQ==\n"
            + "-----END CERTIFICATE-----\n";

    private static final String SECTIGO_ROOT_R46_CROSS_PEM =
            "-----BEGIN CERTIFICATE-----\n"
            + "MIIGlTCCBH2gAwIBAgIRANJ/u8HeNZ5SFq1hSVhgmcQwDQYJKoZIhvcNAQEMBQAw\n"
            + "gYgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpOZXcgSmVyc2V5MRQwEgYDVQQHEwtK\n"
            + "ZXJzZXkgQ2l0eTEeMBwGA1UEChMVVGhlIFVTRVJUUlVTVCBOZXR3b3JrMS4wLAYD\n"
            + "VQQDEyVVU0VSVHJ1c3QgUlNBIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTIx\n"
            + "MDMyMjAwMDAwMFoXDTM4MDExODIzNTk1OVowXzELMAkGA1UEBhMCR0IxGDAWBgNV\n"
            + "BAoTD1NlY3RpZ28gTGltaXRlZDE2MDQGA1UEAxMtU2VjdGlnbyBQdWJsaWMgU2Vy\n"
            + "dmVyIEF1dGhlbnRpY2F0aW9uIFJvb3QgUjQ2MIICIjANBgkqhkiG9w0BAQEFAAOC\n"
            + "Ag8AMIICCgKCAgEAk77VNlJ12AEjoBxHQknuY7a3If3EldVIKyZ8FFMQ2nn9K7ct\n"
            + "pNQs+uoy3UnCub0PSD17WphUr55dMXRPB/xQId2kz2hPGxJjbSWZTCqZ80gwYfqB\n"
            + "fB6nCErcPiscHxhMcao1jK34bug7StnllALWiYQTqm3ITzPMUJY3kjPcX4jnn1TZ\n"
            + "SPCYQ9Zm/Z8XOEPFAVEL1+MjDxRdWxTnS77d9MjaAzfR1jmhIVEwg7Bt1zBOlluR\n"
            + "8HAkq79FgWRDDb0hOi886Z4NyyC1QifM2m+b7mQwkDnNk2WBITG1I1AzNyLjOO34\n"
            + "MTDMRf5i+dFdMnlCh99qzFYZQE3Oqrv5tXZJlPEn+JGlg+UGs2MOgNzgElWApjtm\n"
            + "tDmHLcjw0NEU6eQNTQ72XVdyxTscR1ad4tX7gWGMzE2AkDRbt9cUddzYBEifwMEo\n"
            + "iLTpHMqnsfFWt3tJTFnlIBWohAIp+jiUaZpJBo/NH3kUFxIMg3reH7GX7vmXeCik\n"
            + "yESS6X0mBaZYcpt5E9gRX67FOGI0aLKGMI74kGGeMmz1BzbNokxu7Io27fLmmRVE\n"
            + "cMN8vJw5wLTha/eDJSNX2RKA5UnwdQ/vjescm1QotCE8/HwK/+97a3X/ix2gGQWr\n"
            + "+vgrgULoOLq7+6r9PeDzyt9Ol5cp7fMYVumllqy9w5CYsuD5otSmR0N8bc8CAwEA\n"
            + "AaOCASAwggEcMB8GA1UdIwQYMBaAFFN5v1qqK0rPVIDh2JvAnfKyA2bLMB0GA1Ud\n"
            + "DgQWBBRWc1hklfmSGrASKgRieaFAFYghSTAOBgNVHQ8BAf8EBAMCAYYwDwYDVR0T\n"
            + "AQH/BAUwAwEB/zAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwEQYDVR0g\n"
            + "BAowCDAGBgRVHSAAMFAGA1UdHwRJMEcwRaBDoEGGP2h0dHA6Ly9jcmwudXNlcnRy\n"
            + "dXN0LmNvbS9VU0VSVHJ1c3RSU0FDZXJ0aWZpY2F0aW9uQXV0aG9yaXR5LmNybDA1\n"
            + "BggrBgEFBQcBAQQpMCcwJQYIKwYBBQUHMAGGGWh0dHA6Ly9vY3NwLnVzZXJ0cnVz\n"
            + "dC5jb20wDQYJKoZIhvcNAQEMBQADggIBADpvBIlq7bMU0cFDT/9P9+BsgCkRgQs0\n"
            + "S6Bf7vJSlWMHwby0VGvxCS0hrbi0K2BINZbEbsVsgpQq04431yyoVn3Hldorgq24\n"
            + "RldRDOOipEZDTFB9wC9HYt1thHF00XeG2C8KC1plwoEzKAIhPvefI/C3cT0CfTXJ\n"
            + "uFjUbKIgSwjNjw6YHtLgoy/hd5+JLUlLco/gzFX/qWbT7tEquOMYpsNKWZj8TLqP\n"
            + "q6zMiG4Na6feEZte6YPXGrMWlTWN341vDedc+yxQqSug79HJUQcOZs7KyDWztmae\n"
            + "QxsPE49UV/8XwrfZtZaYyrs4FpD94Z4Q8dzXGL8+qEJjxgcza7W6PROaClubavd1\n"
            + "VKPm8+aCW77u7SxpR2TFGL6kPdxsKyFijpcunR5V79sUyROfNdzjrAcFWZXK8sbb\n"
            + "9FlnwuVG677JLv+ZVTX5AxLvW5OB4zt5uS+zB62wJ/Wv+jXGAttSAcJec4iFgCWH\n"
            + "Rvdi/jJoSzRLa3nEzx6pFIzclSCnh0u1xCeLcUBypSiPga8W+6PkuoyQq8U9qs9E\n"
            + "oxG5NvrvlyshwUS9yvcZRGw7Ljlx4jJH/BhIPR8kIBCQj1vna9TziZOrw1Of8hDU\n"
            + "bHKFG9Pm8Dp2vbjz/2JH39qvxshPKVllGfq+5klPm7yZRUYTiCMAbqwNdL/nsqF2\n"
            + "Rnnyp58XRStJ\n"
            + "-----END CERTIFICATE-----\n";

    private static final Handler UI = new Handler(Looper.getMainLooper());
    private static final Object JVM_LAUNCH_LOCK = new Object();
    private static final String BRIDGE_AUTH_TOKEN = UUID.randomUUID().toString();
    private static volatile AsycudaStandaloneManager sInstance = null;
    private static volatile boolean sExtractingOrBooting = false;
    private static final AtomicBoolean sBootstrapRunning = new AtomicBoolean(false);
    private static final AtomicBoolean sSyncInProgress = new AtomicBoolean(false);
    private static final AtomicBoolean sApkDownloadActive = new AtomicBoolean(false);
    private static final AtomicBoolean sStartupAppCheckDone = new AtomicBoolean(false);
    private static volatile boolean sServerStarted = false;
    private static volatile CmdEntryPoint sCmdEntry = null;
    private static volatile Process sJavaProcess = null;
    private static volatile MainActivity sCurrentActivity = null;
    private static volatile AsycudaMaterialUi.FeedbackModalController sActiveFeedbackController = null;
    private static volatile File sVaultImportTargetDir = null;
    private static volatile Runnable sOnVaultImportCallback = null;
    private static volatile boolean sTermsDialogShowing = false;
    private static volatile boolean sWhatsNewDialogShowing = false;

    private FrameLayout mSplashOverlay;
    private TextView mSplashStatus;
    private ProgressBar mSplashProgress;
    private TextView mActionBtn;

    public static void attach(MainActivity activity) {
        sCurrentActivity = activity;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (sInstance == null) {
            sInstance = new AsycudaStandaloneManager();
        }
        sInstance.initOnActivity(activity);
    }

    /**
     * Cleanly terminates the ASYCUDA JVM, stops the foreground KeepAlive service,
     * cancels all notifications, closes all activities, and exits the process.
     */
    public static void shutdownApp(Context ctx) {
        try {
            if (ctx != null) {
                try {
                    ctx.stopService(new Intent(ctx, AsycudaKeepAliveService.class));
                } catch (Exception ignored) {
                    // Ignore service stop errors during shutdown
                }
                try {
                    NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (nm != null) nm.cancelAll();
                } catch (Exception ignored) {
                    // Ignore notification manager errors during shutdown
                }
            }
            if (sJavaProcess != null) {
                try {
                    sJavaProcess.destroyForcibly();
                } catch (Exception ignored) {
                    // Ignore process termination errors
                }
                sJavaProcess = null;
            }
            for (MainActivity act : MainActivity.getInstances()) {
                try {
                    act.finishAndRemoveTask();
                } catch (Exception ignored) {
                    // Best-effort activity cleanup
                }
                try {
                    act.finishAffinity();
                } catch (Exception ignored) {
                    // Best-effort affinity cleanup
                }
            }
            if (sCurrentActivity != null) {
                try {
                    sCurrentActivity.finishAndRemoveTask();
                } catch (Exception ignored) {
                    // Best-effort current activity cleanup
                }
                try {
                    sCurrentActivity.finishAffinity();
                } catch (Exception ignored) {
                    // Best-effort current affinity cleanup
                }
            }
        } catch (Exception ignored) {
            // Ensure shutdown completes even if activity teardown throws
        } finally {
            wipeRedactedRamDiagRingBuffer();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    public static void onImagePicked(MainActivity activity, Uri uri) {
        if (activity == null || uri == null || sActiveFeedbackController == null) return;
        new Thread(() -> {
            try {
                String mime = activity.getContentResolver().getType(uri);
                if (mime != null && !mime.startsWith("image/")) {
                    UI.post(() -> Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Only image files (PNG/JPG) are supported.",
                            "يُسمح بإرفاق ملفات الصور فقط.",
                            "تەنها پەڕگەی وێنە ڕێگەپێدراوە."), Toast.LENGTH_SHORT).show());
                    return;
                }
                String origName = queryUriDisplayName(activity, uri);
                String safeFileName = "feedback_attachment.png";
                if (origName != null) {
                    String lower = origName.toLowerCase(Locale.US);
                    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                        safeFileName = "feedback_attachment.jpg";
                    } else if (lower.endsWith(".webp")) {
                        safeFileName = "feedback_attachment.webp";
                    }
                } else if (mime != null && mime.contains("jpeg")) {
                    safeFileName = "feedback_attachment.jpg";
                }
                File printsDir = new File(activity.getFilesDir(), DIR_PRINTS);
                ensureDir(printsDir);
                File outImg = new File(printsDir, safeFileName);
                try (InputStream in = activity.getContentResolver().openInputStream(uri);
                     FileOutputStream out = new FileOutputStream(outImg)) {
                    if (in == null) return;
                    byte[] buf = new byte[32768];
                    int n;
                    while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                    out.getFD().sync();
                }
                String label = (origName != null && !origName.isEmpty()) ? origName : outImg.getName();
                AsycudaMaterialUi.FeedbackModalController ctrl = sActiveFeedbackController;
                if (ctrl != null) {
                    ctrl.onImageAttached(outImg, label);
                }
            } catch (Exception t) {
                UI.post(() -> Toast.makeText(activity, "Could not attach image: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, "Asycuda-ImagePicker").start();
    }

    public static void onWorkspaceFilePicked(MainActivity activity, Uri uri) {
        if (activity == null || uri == null) return;
        new Thread(() -> {
            try {
                String origName = queryUriDisplayName(activity, uri);
                String mime = activity.getContentResolver().getType(uri);
                String safeName = (origName != null && !origName.trim().isEmpty())
                        ? origName.trim().replaceAll(REGEX_UNSAFE_FILENAME, "_")
                        : "document_" + System.currentTimeMillis();
                if (".".equals(safeName) || "..".equals(safeName) || safeName.replace("_", "").isEmpty()) {
                    safeName = "document_" + System.currentTimeMillis();
                }
                if (!safeName.contains(".") && mime != null) {
                    if (mime.contains("pdf")) safeName += ".pdf";
                    else if (mime.contains("jpeg") || mime.contains("jpg")) safeName += ".jpg";
                    else if (mime.contains("png")) safeName += ".png";
                    else if (mime.contains("xml")) safeName += ".xml";
                }
                File vaultRoot = new File(activity.getFilesDir(), DIR_PRINTS);
                ensureDir(vaultRoot);
                File customDir = sVaultImportTargetDir;
                Runnable uiRefreshCb = sOnVaultImportCallback;
                sVaultImportTargetDir = null;
                sOnVaultImportCallback = null;

                File targetDir = (customDir != null && customDir.exists() && customDir.isDirectory()) ? customDir : vaultRoot;
                ensureDir(targetDir);
                File outFile = new File(targetDir, safeName);
                try (InputStream in = activity.getContentResolver().openInputStream(uri);
                     FileOutputStream out = new FileOutputStream(outFile)) {
                    if (in == null) return;
                    byte[] buf = new byte[32768];
                    int n;
                    while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                    out.getFD().sync();
                }
                setReadableOwnerOnly(outFile);

                // Notify AsycudaBoot's JFileChooser middleman via .last_imported marker (refreshes Java list without closing it)
                File marker = new File(vaultRoot, ".last_imported");
                try (FileOutputStream mfos = new FileOutputStream(marker)) {
                    mfos.write(safeName.getBytes(StandardCharsets.UTF_8));
                    mfos.getFD().sync();
                }
                setReadableOwnerOnly(marker);

                final String importedName = safeName;
                UI.post(() -> {
                    if (uiRefreshCb != null) {
                        uiRefreshCb.run();
                        Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                                "Imported to Vault: " + importedName,
                                "تم استيراد الملف للخزنة: " + importedName,
                                "هاوردە کرا بۆ قاسە: " + importedName), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                                "Imported to Vault: " + importedName + " — select it in ASYCUDA and click Open",
                                "تم استيراد الملف للخزنة: " + importedName + " — اختره في نافذة أسيكودا واضغط Open",
                                "هاوردە کرا بۆ قاسە: " + importedName + " — لە پەنجەرەی ئەسیکۆدا دیاری بکە و Open دابگرە"), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception t) {
                UI.post(() -> Toast.makeText(activity, "Import error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, "Asycuda-WorkspaceFilePicker").start();
    }

    private static void cleanupStaleUpdateApks(Context ctx) {
        if (ctx == null) return;
        try {
            File updatesCacheDir = new File(ctx.getCacheDir(), DIR_UPDATES_CACHE);
            if (updatesCacheDir.exists()) {
                File[] cachedApks = updatesCacheDir.listFiles();
                if (cachedApks != null) {
                    for (File f : cachedApks) {
                        if (f.isFile() && f.getName().endsWith(".apk")) {
                            deleteQuietly(f);
                        }
                    }
                }
            }
            // Also clean up any legacy .apk files in files/prints from earlier versions
            File printsDir = new File(ctx.getFilesDir(), DIR_PRINTS);
            if (printsDir.exists()) {
                File[] printFiles = printsDir.listFiles();
                if (printFiles != null) {
                    for (File f : printFiles) {
                        if (f.isFile() && f.getName().endsWith(".apk")) {
                            deleteQuietly(f);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Best-effort APK cache cleanup
        }
    }

    private static String queryUriDisplayName(Context ctx, Uri uri) {
        try (Cursor c = ctx.getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return c.getString(idx);
            }
        } catch (Exception ignored) {
            // Fallback to default attachment filename
        }
        return "screenshot.png";
    }

    public static void onSingleBackPressForExit(MainActivity activity) {
        if (activity == null) return;
        UI.post(() -> Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                "Press Back again to exit ASYCUDA",
                "اضغط زر الرجوع مرة أخرى للخروج من التطبيق",
                "دووبارە دوگمەی گەڕانەوە دابگرە بۆ دەرچوون لە بەرنامە"), Toast.LENGTH_SHORT).show());
    }

    private void initOnActivity(MainActivity activity) {
        View stub = activity.findViewById(R.id.stub);
        if (stub != null) stub.setVisibility(View.GONE);

        // Apply preferred Termux:X11 settings once (allowing user changes afterwards)
        File prefsMarker = new File(activity.getFilesDir(), ".x11_prefs_v3_3");
        if (!prefsMarker.exists()) {
            try {
                android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                sp.edit()
                    .putBoolean("hideCutout", false)
                    .putBoolean("displayStretch", false)
                    .putString("notificationButton1Action", "exit")
                    .putBoolean("enforceCharBasedInput", false)
                    .putString("forceOrientation", "landscape")
                    .putBoolean("adjustResolution", false)
                    .putBoolean("tapToMove", false)
                    .putBoolean("enableAccessibilityServiceAutomatically", false)
                    .putString(PREF_BACK_BUTTON_ACTION, ACTION_DOUBLE_BACK_EXIT)
                    .putBoolean("adjustHeightForEK", false)
                    .putString("notificationTapAction", ACTION_OPEN_PREFS)
                    .putBoolean("showAdditionalKbd", true)
                    .putBoolean("stylusButtonContactModifierMode", false)
                    .putBoolean("additionalKbdVisible", true)
                    .putInt(PREF_OPACITY_EK_BAR, 80)
                    .putString("displayResolutionCustom", "2144x1103")
                    .putBoolean("stylusIsMouse", false)
                    .putBoolean("clipboardEnable", true)
                    .putBoolean("pauseKeyInterceptingWithEsc", false)
                    .putString("mediaKeysAction", ACTION_NO_ACTION)
                    .putBoolean("preferScancodes", false)
                    .putBoolean("storeSecondaryDisplayPreferencesSeparately", false)
                    .putBoolean("showIMEWhileExternalConnected", true)
                    .putBoolean("useTermuxEKBarBehaviour", false)
                    .putBoolean("showMouseHelper", false)
                    .putString("transformCapturedPointer", "no")
                    .putString("displayResolutionExact", "1280x720")
                    .putBoolean("keepScreenOn", true)
                    .putBoolean("pointerCapture", true)
                    .putString("swipeDownAction", "toggle additional key bar")
                    .putString("touchMode", "1")
                    .putBoolean("dexMetaKeyCapture", false)
                    .putBoolean("scaleTouchpad", false)
                    .putBoolean("hardwareKbdScancodesWorkaround", true)
                    .putString("notificationButton0Action", ACTION_OPEN_PREFS)
                    .putString("volumeUpAction", ACTION_NO_ACTION)
                    .putBoolean("fullscreen", false)
                    .putBoolean("Reseed", false)
                    .putBoolean("PIP", false)
                    .putInt("capturedPointerSpeedFactor", 100)
                    .putString("displayResolutionMode", "scaled")
                    .putString("swipeUpAction", ACTION_NO_ACTION)
                    .putBoolean("filterOutWinkey", false)
                    .putInt("displayScale", 120)
                    .putString("volumeDownAction", ACTION_NO_ACTION)
                    .putBoolean("showStylusClickOverride", false)
                    .putString("screenIdleTimeout", "never")
                    .apply();
                activity.toggleExtraKeys(true, true);
                ensureFileCreated(prefsMarker);
            } catch (Exception ignored) {
                // Ignore initial preference seed error
            }
        }

        // One-time migration for devices that had the old 8% default opacity
        File opacityMigrated = new File(activity.getFilesDir(), ".x11_opacity_v3_7");
        if (!opacityMigrated.exists()) {
            try {
                android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                if (sp.getInt(PREF_OPACITY_EK_BAR, 80) < 20) {
                    sp.edit().putInt(PREF_OPACITY_EK_BAR, 80).apply();
                    activity.onPreferencesChangedCallback();
                }
                ensureFileCreated(opacityMigrated);
            } catch (Exception ignored) {
                // Ignore migration marker error
            }
        }

        // One-time migration: replace deprecated "toggle soft keyboard" backButtonAction with "double_back_exit"
        File backMigrated = new File(activity.getFilesDir(), ".x11_back_v3_7_1");
        if (!backMigrated.exists()) {
            try {
                android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                String curBack = sp.getString(PREF_BACK_BUTTON_ACTION, "toggle soft keyboard");
                if ("toggle soft keyboard".equals(curBack)) {
                    sp.edit().putString(PREF_BACK_BUTTON_ACTION, ACTION_DOUBLE_BACK_EXIT).apply();
                    activity.onPreferencesChangedCallback();
                }
                ensureFileCreated(backMigrated);
            } catch (Exception ignored) {
                // Ignore migration marker error
            }
        }

        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root != null) {
            setupFloatingMenuPill(activity, root);
            if (sJavaProcess == null || !isProcessAlive(sJavaProcess)) {
                setupSplashOverlay(activity, root);
            }
        }

        startBridgeListener();
        cleanupStaleUpdateApks(activity);

        // Listen for when user returns from notification permission dialog
        activity.getWindow().getDecorView().getViewTreeObserver().addOnWindowFocusChangeListener(hasFocus -> {
            if (hasFocus) {
                checkPermissionsAndProceed(sCurrentActivity != null ? sCurrentActivity : activity);
            }
        });

        checkPermissionsAndProceed(activity);
    }

    private boolean hasNotificationPermission(MainActivity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            return activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private synchronized void checkPermissionsAndProceed(MainActivity activity) {
        if (activity == null) return;

        // Gate 0: First-Launch Terms & Conditions and Liability Policy Acceptance
        if (!AsycudaMaterialUi.hasAcceptedTerms(activity)) {
            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Please review and accept the Terms & Conditions to continue.",
                    "يرجى مراجعة الشروط والأحكام والموافقة عليها للمتابعة.",
                    "تکایە پێداچوونەوە بە مەرج و ڕێساکان بکە و پەسەندی بکە بۆ بەردەوامبوون."));
            if (!sTermsDialogShowing) {
                sTermsDialogShowing = true;
                UI.post(() -> {
                    try {
                        AsycudaMaterialUi.showTermsAndConditionsModal(
                                activity,
                                () -> {
                                    sTermsDialogShowing = false;
                                    checkPermissionsAndProceed(activity);
                                },
                                () -> {
                                    sTermsDialogShowing = false;
                                    shutdownApp(activity);
                                });
                    } catch (Exception t) {
                        sTermsDialogShowing = false;
                    }
                });
            }
            return;
        }

        // Gate 0.5: Post-Install / Post-Update In-App Changelog Modal ("What's New")
        if (AsycudaMaterialUi.shouldShowWhatsNew(activity)) {
            if (!sWhatsNewDialogShowing) {
                sWhatsNewDialogShowing = true;
                UI.post(() -> {
                    try {
                        AsycudaMaterialUi.showWhatsNewModal(activity, () -> {
                            sWhatsNewDialogShowing = false;
                            checkPermissionsAndProceed(activity);
                        });
                    } catch (Exception t) {
                        sWhatsNewDialogShowing = false;
                    }
                });
            }
            return;
        }

        // Gate 1: Notification Permission on Android 13+ (Requested non-blockingly after Terms are accepted so startup never pauses)
        if (Build.VERSION.SDK_INT >= 33
                && !hasNotificationPermission(activity)
                && !activity.getSharedPreferences("asy_perms", Context.MODE_PRIVATE).getBoolean("asked_notif", false)) {
            activity.getSharedPreferences("asy_perms", Context.MODE_PRIVATE).edit().putBoolean("asked_notif", true).apply();
            try {
                activity.requestPermissions(new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
            } catch (Exception ignored) {
                // Ignore if notification prompt is suppressed by OS
            }
        }

        hideSplashButton();

        if (sExtractingOrBooting || sBootstrapRunning.get() || sSyncInProgress.get()) {
            return;
        }

        if (sJavaProcess != null && isProcessAlive(sJavaProcess)) {
            startKeepAliveService(activity);
            if (sCmdEntry != null && !activity.getLorieView().connected()) {
                activity.connectToService(sCmdEntry);
            }
            hideSplashDelayed(300);
            return;
        }

        sExtractingOrBooting = true;
        new Thread(() -> bootstrapEnvironmentAndStart(activity), "Asycuda-Bootstrap").start();
    }

    private void setupSplashOverlay(MainActivity activity, FrameLayout root) {
        if (mSplashOverlay != null && mSplashOverlay.getParent() == root) {
            return;
        }
        mSplashOverlay = new FrameLayout(activity);
        mSplashOverlay.setBackgroundColor(Color.parseColor("#F5000000"));
        mSplashOverlay.setClickable(true);

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        int pad = dp(activity, 20);
        card.setPadding(pad, dp(activity, 18), pad, dp(activity, 18));
        card.setBackground(AsycudaMaterialUi.createSheetContainerDrawable(activity, 18));
        card.setElevation(dp(activity, 18));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(activity, 50), dp(activity, 50));
        iconLp.bottomMargin = dp(activity, 9);
        card.addView(icon, iconLp);

        TextView title = new TextView(activity);
        title.setText(AsycudaMaterialUi.tr(activity,
                "ASYCUDA World — Republic of Iraq",
                "نظام أسيكودا العالمي — جمهورية العراق",
                "سیستەمی ئەسیکۆدای جیهانی — کۆماری عێراق"));
        title.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setGravity(Gravity.CENTER);
        card.addView(title);

        TextView sub = new TextView(activity);
        sub.setText(AsycudaMaterialUi.tr(activity,
                "General Commission for Customs • Client " + AsycudaMaterialUi.getAppVersionLabel(activity),
                "الهيئة العامة للكمارك • الإصدار " + AsycudaMaterialUi.getAppVersionLabel(activity),
                "دەستەی گشتیی گومرگ • وەشانی " + AsycudaMaterialUi.getAppVersionLabel(activity)));
        sub.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dp(activity, 3);
        subLp.bottomMargin = dp(activity, 13);
        card.addView(sub, subLp);

        mSplashProgress = new ProgressBar(activity);
        mSplashProgress.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        card.addView(mSplashProgress, new LinearLayout.LayoutParams(dp(activity, 26), dp(activity, 26)));

        mSplashStatus = new TextView(activity);
        mSplashStatus.setText(AsycudaMaterialUi.tr(activity,
                "Preparing secure environment...",
                "جاري تجهيز بيئة التشغيل الآمنة...",
                "ئامادەکردنی ژینگەی پارێزراو..."));
        mSplashStatus.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        mSplashStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        mSplashStatus.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams stLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stLp.topMargin = dp(activity, 11);
        card.addView(mSplashStatus, stLp);

        mActionBtn = AsycudaMaterialUi.createCupertinoButton(activity,
                AsycudaMaterialUi.tr(activity, "Continue", "متابعة", "بەردەوامبوون"),
                true, false, null);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dp(activity, 11);
        mActionBtn.setLayoutParams(btnLp);
        mActionBtn.setVisibility(View.GONE);
        card.addView(mActionBtn);

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(
                dp(activity, 380), ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mSplashOverlay.addView(card, cardLp);
        root.addView(mSplashOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void hideSplashButton() {
        UI.post(() -> {
            if (mSplashProgress != null) mSplashProgress.setVisibility(View.VISIBLE);
            if (mActionBtn != null) mActionBtn.setVisibility(View.GONE);
        });
    }

    private void updateStatus(String text) {
        UI.post(() -> {
            if (mSplashStatus != null) mSplashStatus.setText(text);
        });
    }

    private void hideSplashDelayed(long delayMs) {
        UI.postDelayed(() -> {
            if (mSplashOverlay != null && mSplashOverlay.getParent() instanceof ViewGroup) {
                ((ViewGroup) mSplashOverlay.getParent()).removeView(mSplashOverlay);
                mSplashOverlay = null;
            }
        }, delayMs);
    }

    private void startKeepAliveService(Context ctx) {
        try {
            Intent svc = new Intent(ctx, AsycudaKeepAliveService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(svc);
            } else {
                ctx.startService(svc);
            }
        } catch (Exception ignored) {
            // Foreground service may be restricted in background state
        }
    }

    private void bootstrapEnvironmentAndStart(MainActivity activity) {
        if (!sBootstrapRunning.compareAndSet(false, true)) {
            return;
        }
        sExtractingOrBooting = true;
        hideSplashButton();
        try {
            startKeepAliveService(activity);

            File filesDir = activity.getFilesDir();
            File clientDir = new File(filesDir, DIR_CLIENT);
            File usrTmpDir = new File(filesDir, DIR_USR_TMP);
            File x11UnixDir = new File(usrTmpDir, ".X11-unix");
            File homeDir = new File(filesDir, "home");
            File printsDir = new File(filesDir, DIR_PRINTS);
            File workspaceDir = new File(filesDir, DIR_WORKSPACE);
            File runtimeMarker = new File(filesDir, ".extracted_v3_2");
            File bootPatchMarker = new File(filesDir, ".bootpatch_v4_1_0_r9");
            ensureNetworkChangeMonitor(activity.getApplicationContext(), filesDir);

            if (!runtimeMarker.exists()) {
                deleteRecursive(new File(filesDir, "usr"));
                deleteRecursive(new File(filesDir, "jre8"));
                deleteQuietly(new File(filesDir, ".extracted_v3"));
                deleteQuietly(new File(filesDir, ".extracted_v3_1"));

                updateStatus(AsycudaMaterialUi.tr(activity,
                        "Setting up the Java environment (1/2)...",
                        "جاري تجهيز بيئة التشغيل جافا (1/2)...",
                        "ئامادەکردنی ژینگەی جاڤا (1/2)..."));
                extractTarXzAsset(activity, "runtime-arm64.tar.xz", filesDir);

                if (!new File(clientDir, BOOTPATCH_JAR_NAME).exists()) {
                    updateStatus(AsycudaMaterialUi.tr(activity,
                            "Unpacking ASYCUDA World components (2/2)...",
                            "جاري استخراج مكونات نظام أسيكودا (2/2)...",
                            "دەرهێنانی پێکهاتەکانی سیستەمی ئەسیکۆدا (2/2)..."));
                    ensureDir(clientDir);
                    extractTarXzAsset(activity, ASSET_ASYCUDA_IRAQ_TAR, clientDir);
                }

                setPermissionsRecursive(new File(filesDir, "usr/glibc/lib"));
                setPermissionsRecursive(new File(filesDir, "jre8"));

                File fontFallback = new File(filesDir, "jre8/jre/lib/fonts/fallback");
                ensureDir(fontFallback);
                File sysFonts = new File("/system/fonts");
                File[] ttfList = sysFonts.listFiles();
                if (ttfList != null) {
                    for (File ttf : ttfList) {
                        if (ttf.getName().endsWith(".ttf")) {
                            File link = new File(fontFallback, ttf.getName());
                            try {
                                if (!link.exists()) Os.symlink(ttf.getAbsolutePath(), link.getAbsolutePath());
                            } catch (Exception ignored) {
                                // Ignore individual font symlink errors
                            }
                        }
                    }
                }
                ensureFileCreated(runtimeMarker);
                ensureFileCreated(bootPatchMarker);
            } else if (!bootPatchMarker.exists()) {
                updateStatus(AsycudaMaterialUi.tr(activity,
                        "Updating local components...",
                        "جاري تحديث المكونات المحلية...",
                        "نوێکردنەوەی پێکهاتە ناوخۆییەکان..."));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_8_0"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_0"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_1"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_2"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_3"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_4"));
                deleteQuietly(new File(filesDir, ".bootpatch_v3_9_5"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_0_0"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_0_5"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_0_5_r2"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r2"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r3"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r4"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r5"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r6"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r7"));
                deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r8"));
                extractBootJarsOnly(activity, ASSET_ASYCUDA_IRAQ_TAR, clientDir);
                ensureFileCreated(bootPatchMarker);
            } else {
                // Verify and enforce SHA-256 integrity of local bridge JARs on every startup
                extractBootJarsOnly(activity, ASSET_ASYCUDA_IRAQ_TAR, clientDir);
            }

            // Migrate any files from legacy workspace folder into unified prints vault
            File legacyWorkspace = new File(filesDir, DIR_LEGACY_WORKSPACE);
            if (legacyWorkspace.exists() && !legacyWorkspace.equals(printsDir)) {
                ensureDir(printsDir);
                File[] legacyFiles = legacyWorkspace.listFiles();
                if (legacyFiles != null) {
                    for (File lf : legacyFiles) {
                        if (lf.isFile() && !lf.getName().startsWith(".")) {
                            File dest = new File(printsDir, lf.getName());
                            if (!lf.renameTo(dest)) {
                                deleteQuietly(lf);
                            }
                        } else {
                            deleteRecursive(lf);
                        }
                    }
                }
                deleteRecursive(legacyWorkspace);
            }

            // Smart Startup Sync: Check MOF Server (asy.mof.gov.iq) for dynamic JNLP list & JAR updates
            boolean initialDownloadRequired = !areAllManifestJarsPresent(clientDir);
            evaluateMofRouteAndCellularBypass(activity.getApplicationContext(), filesDir);
            syncGovernmentJars(clientDir, initialDownloadRequired, null);

            if (!areAllManifestJarsPresent(clientDir)) {
                updateStatus(AsycudaMaterialUi.tr(activity,
                        "Internet connection to asy.mof.gov.iq is required for initial installation of official ASYCUDA packages.",
                        "يلزم الاتصال بخادم الكمارك الرسمي (asy.mof.gov.iq) لتحميل ملفات نظام أسيكودا لأول مرة.",
                        "پێویستە پەیوەندی بە ڕاژەی فەرمی گومرگ (asy.mof.gov.iq) هەبێت بۆ داگرتنی پەڕگەکانی ئەسیکۆدا بۆ یەکەم جار."));
                diagnoseAndShowUniversalNetworkModal(
                        activity,
                        "initial_package_download",
                        sLastSyncException,
                        "Initial installation requires downloading the 131 official JAR packages from https://asy.mof.gov.iq/aw/AWIQWorld.jnlp, but the server could not be reached.",
                        () -> {
                            hideSplashButton();
                            new Thread(() -> bootstrapEnvironmentAndStart(activity), "Asycuda-BootstrapRetry").start();
                        });
                UI.post(() -> {
                    if (mSplashProgress != null) mSplashProgress.setVisibility(View.GONE);
                    if (mActionBtn != null) {
                        mActionBtn.setText(AsycudaMaterialUi.tr(activity,
                                "Retry Official Package Download",
                                "إعادة محاولة التحميل من الخادم الرسمي",
                                "دووبارە هەوڵدانەوە بۆ داگرتن"));
                        mActionBtn.setVisibility(View.VISIBLE);
                        mActionBtn.setOnClickListener(v -> {
                            hideSplashButton();
                            new Thread(() -> bootstrapEnvironmentAndStart(activity), "Asycuda-BootstrapRetry").start();
                        });
                    }
                });
                return;
            }

            // Background Startup App Update Check (checks GitHub releases without blocking startup)
            checkStartupAppUpdateQuietly(activity);

            ensureDir(x11UnixDir);
            ensureDir(homeDir);
            ensureDir(printsDir);
            ensureDir(workspaceDir);

            Os.setenv("HOME", homeDir.getAbsolutePath(), true);
            Os.setenv("PREFIX", new File(filesDir, "usr").getAbsolutePath(), true);
            Os.setenv("TMPDIR", usrTmpDir.getAbsolutePath(), true);
            Os.setenv("XKB_CONFIG_ROOT", new File(filesDir, "usr/share/X11/xkb").getAbsolutePath(), true);
            Os.setenv("DISPLAY", ":0", true);

            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Starting the display window...",
                    "جاري تشغيل نافذة العرض...",
                    "پێکردنی پەنجەرەی پیشاندان..."));
            ensureX11ServerSocketReady(activity, filesDir);

            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Starting ASYCUDA World Iraq...",
                    "جاري تشغيل نظام أسيكودا العالمي...",
                    "پێکردنی سیستەمی ئەسیکودای جیهانی..."));
            launchAsycudaJvm(activity);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception t) {
            updateStatus("Startup error: " + t.getMessage());
            diagnoseAndShowUniversalNetworkModal(
                    activity,
                    "bootstrap_startup_exception",
                    t,
                    "Startup exception in bootstrapEnvironmentAndStart: " + t.getClass().getName() + ": " + t.getMessage(),
                    () -> {
                        hideSplashButton();
                        new Thread(() -> bootstrapEnvironmentAndStart(activity), "Asycuda-BootstrapRetry").start();
                    });
        } finally {
            sExtractingOrBooting = false;
            sBootstrapRunning.set(false);
        }
    }

    private boolean areAllManifestJarsPresent(File clientDir) {
        if (clientDir == null) return false;
        File jarsDir = new File(clientDir, "jars");
        File jnlpFile = new File(clientDir, FILE_AWIQ_JNLP);
        if (!jnlpFile.isFile() || jnlpFile.length() == 0) {
            return false;
        }
        String[] mandatoryCoreJars = { "client.jar", "core.jar", "soutil.jar", "kswing.jar", "skin.jar" };
        for (String coreName : mandatoryCoreJars) {
            File cf = new File(jarsDir, coreName);
            if (!cf.isFile() || cf.length() == 0) {
                return false;
            }
        }
        try {
            String jnlpXml = readTail(jnlpFile, 65536);
            List<String> hrefs = parseJnlpJarHrefs(jnlpXml);
            if (hrefs.isEmpty()) return false;
            int expectedCount = 0;
            int presentCount = 0;
            for (String href : hrefs) {
                String rawName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                String flatName = rawName.replaceAll(REGEX_UNSAFE_FILENAME, "");
                if (flatName.isEmpty() || flatName.startsWith("00_")) continue;
                expectedCount++;
                File f = new File(jarsDir, flatName);
                if (f.isFile() && f.length() > 0) {
                    presentCount++;
                }
            }
            return expectedCount > 0 && presentCount >= Math.max(1, expectedCount - 1);
        } catch (Exception e) {
            return false;
        }
    }

    private static final String GITHUB_KNOWN_ERRORS_CATALOG_URL =
            "https://raw.githubusercontent.com/MohammedALShimmary/ASYCUDA-Iraq-Android/main/lorie-app/src/main/assets/known-errors-catalog.json";
    private static final int MAX_CELLULAR_RELAY_PORTS = 8;
    private static final String FILE_KNOWN_ERRORS_CATALOG = "known_errors_catalog.json";
    private static final String ASSET_KNOWN_ERRORS_CATALOG = "known-errors-catalog.json";
    private static final Set<String> ALLOWED_VERIFIED_FIX_IDS = new HashSet<>(java.util.Arrays.asList(
            "NONE",
            "OPEN_NETWORK_SETTINGS",
            "REFRESH_DNS_OR_4G",
            "ROUTE_4G_CELLULAR",
            "LAUNCH_WITH_CACHED_JARS",
            "CLEAN_LOCKS_AND_RECONNECT",
            "REFRESH_DNS_AND_RETRY",
            "PURGE_SOCLASS_CACHE",
            "VERIFY_AND_RESYNC_JARS",
            "REPAIR_JAVA_RUNTIME",
            "REINIT_X11_DISPLAY",
            "CLEAR_TEMP_AND_LOCKS",
            "OPEN_APP_UPDATE_CENTER"
    ));

    /**
     * Checks GitHub Releases on launch in the background and syncs the read-only Known-Errors
     * Catalog JSON (`lorie-app/src/main/assets/known-errors-catalog.json`) using HTTP ETag caching.
     */
    private void checkStartupAppUpdateQuietly(MainActivity activity) {
        if (!sStartupAppCheckDone.compareAndSet(false, true)) return;
        new Thread(() -> {
            syncKnownErrorsCatalogQuietly(activity.getApplicationContext());
            HttpURLConnection ghConn = null;
            try {
                String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                ghConn = (HttpURLConnection) new URL(GITHUB_LATEST_RELEASE_API).openConnection();
                ghConn.setConnectTimeout(4000);
                ghConn.setReadTimeout(6000);
                ghConn.setRequestProperty("Accept", "application/vnd.github+json");
                ghConn.setRequestProperty(HEADER_USER_AGENT, "ASYCUDA-Iraq-Android/" + currentVer);
                if (ghConn.getResponseCode() == 200) {
                    String jsonStr;
                    try (InputStream in = ghConn.getInputStream();
                         ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
                        jsonStr = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                    }
                    JSONObject rel = new JSONObject(jsonStr);
                    String tagName = rel.optString("tag_name", "");
                    if (isRemoteVersionNewer(tagName, currentVer)) {
                        UI.postDelayed(() -> {
                            MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                            if (act != null && !act.isFinishing()) {
                                runUnifiedUpdateCheckModal(act);
                            }
                        }, 1500);
                    }
                }
            } catch (Exception ignored) {
                // Ignore offline errors during background startup update check
            } finally {
                if (ghConn != null) ghConn.disconnect();
            }
        }, "Asycuda-StartupAppUpdateCheck").start();
    }

    /**
     * Option A + C — Read-only background sync of {@code lorie-app/src/main/assets/known-errors-catalog.json}
     * from our official GitHub repository. Strictly validates that the JSON is data-only and
     * references only allowlisted {@code verifiedFixId} constants before saving to internal
     * storage ({@code 0600} owner-only).
     */
    private void syncKnownErrorsCatalogQuietly(Context appCtx) {
        if (appCtx == null) return;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(GITHUB_KNOWN_ERRORS_CATALOG_URL);
            String lowPath = url.getPath() != null ? url.getPath().toLowerCase(Locale.US) : "";
            if (!SCHEME_HTTPS.equalsIgnoreCase(url.getProtocol())
                    || !"raw.githubusercontent.com".equalsIgnoreCase(url.getHost())
                    || !lowPath.startsWith("/mohammedalshimmary/asycuda-iraq-android/")) {
                return;
            }
            android.content.SharedPreferences sp = appCtx.getSharedPreferences("asy_catalog_sync", Context.MODE_PRIVATE);
            String cachedEtag = sp.getString("etag", "");
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(6000);
            conn.setUseCaches(false);
            if (cachedEtag != null && !cachedEtag.isEmpty()) {
                conn.setRequestProperty("If-None-Match", cachedEtag);
            }
            int code = conn.getResponseCode();
            if (code == 304) {
                return;
            }
            if (code == 200) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try (InputStream in = conn.getInputStream()) {
                    byte[] buf = new byte[8192];
                    int n;
                    int total = 0;
                    while ((n = in.read(buf)) != -1) {
                        total += n;
                        if (total > 262144) {
                            // Reject abnormally large catalog (> 256 KB)
                            return;
                        }
                        bos.write(buf, 0, n);
                    }
                }
                String jsonStr = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                if (validateKnownErrorsCatalogJson(jsonStr)) {
                    File catalogFile = new File(appCtx.getFilesDir(), FILE_KNOWN_ERRORS_CATALOG);
                    setWritableOwnerOnly(catalogFile);
                    try (FileOutputStream fos = new FileOutputStream(catalogFile)) {
                        fos.write(jsonStr.getBytes(StandardCharsets.UTF_8));
                        fos.getFD().sync();
                    }
                    setReadableOwnerOnly(catalogFile);
                    String newEtag = conn.getHeaderField("ETag");
                    if (newEtag != null && !newEtag.isEmpty()) {
                        sp.edit().putString("etag", newEtag).apply();
                    }
                }
            }
        } catch (Exception ignored) {
            // Offline or GitHub unreachable — app uses local cached or bundled APK catalog
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static boolean validateKnownErrorsCatalogJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty() || jsonStr.length() > 262144) return false;
        try {
            JSONObject root = new JSONObject(jsonStr);
            org.json.JSONArray entries = root.optJSONArray("entries");
            if (entries == null || entries.length() == 0 || entries.length() > 200) {
                return false;
            }
            for (int i = 0; i < entries.length(); i++) {
                JSONObject item = entries.optJSONObject(i);
                if (item == null) return false;
                String id = item.optString("id", "");
                String badge = item.optString("badge", "");
                String fixId = item.optString("verifiedFixId", "NONE");
                String titleEn = item.optString("titleEn", "");
                String titleAr = item.optString("titleAr", "");
                String titleKu = item.optString("titleKu", "");
                String expEn = item.optString("explanationEn", "");
                String expAr = item.optString("explanationAr", "");
                String expKu = item.optString("explanationKu", "");
                if (id.isEmpty() || id.length() > 64
                        || badge.length() > 140
                        || titleEn.length() > 300 || titleAr.length() > 300 || titleKu.length() > 300
                        || expEn.length() > 2000 || expAr.length() > 2000 || expKu.length() > 2000
                        || !ALLOWED_VERIFIED_FIX_IDS.contains(fixId)) {
                    return false;
                }
                org.json.JSONArray kwArr = item.optJSONArray("matchKeywords");
                if (kwArr != null && kwArr.length() > 24) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static volatile boolean sNetworkMonitorRegistered = false;
    private static volatile boolean sUserConsented4gBypass = false;
    private static volatile android.net.Network sCellularBypassNetwork = null;
    private static volatile String sCellularBypassMofIp = null;
    private static final java.util.concurrent.ConcurrentHashMap<Integer, ServerSocket> sCellularRmiRelaySockets =
            new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile ServerSocket sCellularRmiRelaySocket = null;
    private static volatile int sCellularRmiRelayPort = -1;
    private static volatile ServerSocket sCellularHttpsRelaySocket = null;
    private static volatile int sCellularHttpsRelayPort = -1;
    private static volatile Exception sLastSyncException = null;

    private void ensureNetworkChangeMonitor(Context appCtx, File filesDir) {
        if (sNetworkMonitorRegistered || appCtx == null) return;
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager) appCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sNetworkMonitorRegistered = true;
                cm.registerDefaultNetworkCallback(new android.net.ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(android.net.Network network) {
                        new Thread(() -> {
                            evaluateMofRouteAndCellularBypass(appCtx, filesDir);
                            refreshGlibcHostsCache(filesDir);
                        }, "Asycuda-NetHandoverDns").start();
                    }
                });
            }
        } catch (Exception ignored) {
            // Best-effort network handover monitor
        }
    }

    /**
     * User-Consented Cellular (4G) Multipath Bypass While Connected to Wi-Fi.
     * Never activates automatically on startup — only runs when the user explicitly clicks the
     * {@code Apply Fix: Route Session via 4G Mobile Data} button in the Universal Diagnostics Window
     * ({@code sUserConsented4gBypass == true}).
     */
    private synchronized void evaluateMofRouteAndCellularBypass(Context appCtx, File filesDir) {
        if (!sUserConsented4gBypass || appCtx == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            sCellularBypassNetwork = null;
            sCellularBypassMofIp = null;
            stopCellularRmiRelay();
            return;
        }
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager) appCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return;
            android.net.Network activeNet = cm.getActiveNetwork();
            if (activeNet == null) {
                sCellularBypassNetwork = null;
                sCellularBypassMofIp = null;
                stopCellularRmiRelay();
                return;
            }
            android.net.NetworkCapabilities caps = cm.getNetworkCapabilities(activeNet);
            boolean isWifi = isWifiTransportActive(cm, caps);
            int rmiPort = extractDynamicMofRmiPort(new File(filesDir, DIR_CLIENT + "/" + FILE_AWIQ_JNLP));

            if (!isWifi) {
                // Device is already directly on 4G Mobile Data (Wi-Fi turned off)
                sCellularBypassNetwork = null;
                sCellularBypassMofIp = null;
                stopCellularRmiRelay();
                return;
            }

            // If 4G bypass network is already established in this session, keep it active and ensure relays are bound
            if (sCellularBypassNetwork != null && sCellularBypassMofIp != null) {
                android.net.NetworkCapabilities cellCaps = cm.getNetworkCapabilities(sCellularBypassNetwork);
                if (cellCaps != null && cellCaps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    ensureCellularHttpsRelay();
                    if (rmiPort > 0) {
                        ensureCellularRmiRelay(rmiPort);
                    }
                    refreshGlibcHostsCache(filesDir);
                    return;
                }
            }

            // User explicitly consented to route ASYCUDA via 4G Mobile Data while on blocked Wi-Fi
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            final android.net.Network[] cellHolder = new android.net.Network[1];
            android.net.NetworkRequest req = new android.net.NetworkRequest.Builder()
                    .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            android.net.ConnectivityManager.NetworkCallback cb = new android.net.ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(android.net.Network network) {
                    cellHolder[0] = network;
                    latch.countDown();
                }
            };
            boolean cellAcquired = false;
            try {
                cm.requestNetwork(req, cb);
                cellAcquired = latch.await(4500, java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
                // Cellular network unavailable or permission denied
            }
            android.net.Network cellNet = (cellAcquired || cellHolder[0] != null) ? cellHolder[0] : null;
            if (cellNet != null) {
                InetAddress cellAddr = null;
                try {
                    cellAddr = cellNet.getByName(ALLOWED_MOF_HOST);
                } catch (Exception ignored) {
                    // Fallback to system resolver on active network if cellular DNS is warming up
                    try {
                        cellAddr = InetAddress.getByName(ALLOWED_MOF_HOST);
                    } catch (Exception ignored2) {
                        // Unresolvable
                    }
                }
                if (cellAddr != null && cellAddr.getHostAddress() != null) {
                    sCellularBypassNetwork = cellNet;
                    sCellularBypassMofIp = cellAddr.getHostAddress().trim();
                    ensureCellularHttpsRelay();
                    if (rmiPort > 0) {
                        ensureCellularRmiRelay(rmiPort);
                    }
                    refreshGlibcHostsCache(filesDir);
                    updateStatus(AsycudaMaterialUi.tr(appCtx,
                            "Routing session via 4G Mobile Data (User-Approved Fix)...",
                            "جاري تمرير الجلسة عبر بيانات الهاتف 4G (بموافقة المستخدم)...",
                            "تێپەڕاندنی دانیشتن لە ڕێگەی داتای 4G..."));
                    return;
                }
            } else {
                try {
                    cm.unregisterNetworkCallback(cb);
                } catch (Exception ignored) {
                    // Ignore callback unregister error
                }
            }
        } catch (Exception ignored) {
            // Fallback to normal routing
        }
        sCellularBypassNetwork = null;
        sCellularBypassMofIp = null;
        stopCellularRmiRelay();
    }

    private boolean isWifiTransportActive(
            android.net.ConnectivityManager cm,
            android.net.NetworkCapabilities activeCaps) {
        if (activeCaps != null && activeCaps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)) {
            return true;
        }
        if (cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activeCaps != null && activeCaps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN)) {
            try {
                for (android.net.Network net : cm.getAllNetworks()) {
                    if (net == null) continue;
                    android.net.NetworkCapabilities nc = cm.getNetworkCapabilities(net);
                    if (nc != null && !nc.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN)
                            && nc.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // Ignore network enumeration error
            }
        }
        return false;
    }

    private synchronized void ensureCellularRmiRelay(final int rmiPort) {
        if (rmiPort <= 1024 || rmiPort > 65535 || rmiPort == 47891
                || (rmiPort >= 6000 && rmiPort <= 6010) || rmiPort == sCellularHttpsRelayPort) {
            return;
        }
        ServerSocket existing = sCellularRmiRelaySockets.get(rmiPort);
        if (existing != null && !existing.isClosed()) {
            return;
        }
        if (sCellularRmiRelaySockets.size() >= MAX_CELLULAR_RELAY_PORTS) {
            return;
        }
        try {
            final ServerSocket ss = new ServerSocket(rmiPort, 32, InetAddress.getByName("127.0.0.1"));
            sCellularRmiRelaySockets.put(rmiPort, ss);
            sCellularRmiRelaySocket = ss;
            sCellularRmiRelayPort = rmiPort;
            Thread relayThread = new Thread(() -> {
                while (!ss.isClosed() && !Thread.currentThread().isInterrupted()) {
                    try {
                        final Socket localSock = ss.accept();
                        if (!isLoopbackPeerOwnedByOurUid(localSock)) {
                            localSock.close();
                            continue;
                        }
                        new Thread(() -> pipeCellularRelayConnection(localSock, rmiPort),
                                "Asycuda-4GRelayConn-" + rmiPort).start();
                    } catch (Exception e) {
                        if (ss.isClosed()) break;
                    }
                }
            }, "Asycuda-4GRmiRelay-" + rmiPort);
            relayThread.setDaemon(true);
            relayThread.start();
        } catch (Exception ignored) {
            // Port already bound or unavailable
        }
    }

    private void pipeCellularRelayConnection(final Socket localSock, final int rmiPort) {
        Socket remoteSock = null;
        try {
            android.net.Network cellNet = sCellularBypassNetwork;
            String mofIp = sCellularBypassMofIp;
            if (cellNet == null || mofIp == null) {
                localSock.close();
                return;
            }
            remoteSock = cellNet.getSocketFactory().createSocket();
            remoteSock.connect(new java.net.InetSocketAddress(InetAddress.getByName(mofIp), rmiPort), 10000);
            remoteSock.setTcpNoDelay(true);
            localSock.setTcpNoDelay(true);
            final Socket finalRemote = remoteSock;
            Thread up = new Thread(() -> copySocketStream(localSock, finalRemote), "Asycuda-4GUp-" + rmiPort);
            up.setDaemon(true);
            up.start();
            copySocketStream(finalRemote, localSock);
        } catch (Exception ignored) {
            // Relay socket closed
        } finally {
            try { localSock.close(); } catch (Exception ignored) { /* Ignore */ }
            if (remoteSock != null) {
                try { remoteSock.close(); } catch (Exception ignored) { /* Ignore */ }
            }
        }
    }

    private void copySocketStream(Socket inSock, Socket outSock) {
        try {
            InputStream in = inSock.getInputStream();
            OutputStream out = outSock.getOutputStream();
            byte[] buf = new byte[32768];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
                out.flush();
            }
        } catch (Exception ignored) {
            // Normal stream EOF/close
        } finally {
            try { outSock.shutdownOutput(); } catch (Exception ignored) { /* Ignore */ }
        }
    }

    private synchronized void ensureCellularHttpsRelay() {
        ServerSocket existing = sCellularHttpsRelaySocket;
        if (existing != null && !existing.isClosed() && sCellularHttpsRelayPort > 0) {
            return;
        }
        try {
            final int remoteHttpsPort = new URL(MOF_CODEBASE_URL).getDefaultPort();
            final ServerSocket ss = new ServerSocket(0, 32, InetAddress.getByName("127.0.0.1"));
            sCellularHttpsRelaySocket = ss;
            sCellularHttpsRelayPort = ss.getLocalPort();
            Thread relayThread = new Thread(() -> {
                while (!ss.isClosed() && !Thread.currentThread().isInterrupted()) {
                    try {
                        final Socket localSock = ss.accept();
                        if (!isLoopbackPeerOwnedByOurUid(localSock)) {
                            localSock.close();
                            continue;
                        }
                        new Thread(() -> pipeCellularRelayConnection(localSock, remoteHttpsPort),
                                "Asycuda-4GHttpsConn").start();
                    } catch (Exception e) {
                        if (ss.isClosed()) break;
                    }
                }
            }, "Asycuda-4GHttpsRelay");
            relayThread.setDaemon(true);
            relayThread.start();
        } catch (Exception ignored) {
            // Local ephemeral port unavailable
        }
    }

    private static synchronized void stopCellularRmiRelay() {
        for (ServerSocket ss : sCellularRmiRelaySockets.values()) {
            try {
                ss.close();
            } catch (Exception ignored) {
                // Ignore close error
            }
        }
        sCellularRmiRelaySockets.clear();
        sCellularRmiRelaySocket = null;
        sCellularRmiRelayPort = -1;
        if (sCellularHttpsRelaySocket != null) {
            try {
                sCellularHttpsRelaySocket.close();
            } catch (Exception ignored) {
                // Ignore close error
            }
            sCellularHttpsRelaySocket = null;
        }
        sCellularHttpsRelayPort = -1;
    }

    /**
     * Dynamically resolves {@code host} (never using any hardcoded server IP or fallback IP):
     * 1. Queries the active 4G Cellular bypass network if Wi-Fi is blocked by the MoF firewall.
     * 2. Queries Android's currently active {@link android.net.Network#getByName(String)} (4G or Wi-Fi).
     * 3. Queries standard {@link InetAddress#getByName(String)}.
     */
    private InetAddress resolveHostDynamically(String host) {
        try {
            android.net.Network cellBypass = sCellularBypassNetwork;
            if (cellBypass != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                InetAddress addr = cellBypass.getByName(host);
                if (addr != null && addr.getAddress() != null) {
                    return InetAddress.getByAddress(host, addr.getAddress());
                }
            }
        } catch (Exception ignored) {
            // Fall through to active network
        }
        try {
            MainActivity act = sCurrentActivity;
            if (act != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.net.ConnectivityManager cm = (android.net.ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.Network activeNet = cm != null ? cm.getActiveNetwork() : null;
                if (activeNet != null) {
                    InetAddress addr = activeNet.getByName(host);
                    if (addr != null && addr.getAddress() != null) {
                        return InetAddress.getByAddress(host, addr.getAddress());
                    }
                }
            }
        } catch (Exception ignored) {
            // Try system default resolver next
        }
        try {
            InetAddress addr = InetAddress.getByName(host);
            if (addr != null && addr.getAddress() != null) {
                return InetAddress.getByAddress(host, addr.getAddress());
            }
        } catch (Exception ignored) {
            // Unreachable or DNS failed
        }
        return null;
    }

    /**
     * Dynamically resolves asy.mof.gov.iq and updates usr/glibc/etc/hosts so glibc inside the JVM
     * can resolve asy.mof.gov.iq across Wi-Fi and 4G. When the 4G Cellular Multipath Relay is active
     * because the current Wi-Fi ISP is blocked by the MoF firewall, points asy.mof.gov.iq to 127.0.0.1
     * so the JVM routes through the Android 4G socket relay.
     */
    private void refreshGlibcHostsCache(File filesDir) {
        try {
            File hostsFile = new File(filesDir, "usr/glibc/etc/hosts");
            if (hostsFile.getParentFile() != null) ensureDir(hostsFile.getParentFile());
            if (sCellularBypassNetwork != null && sCellularRmiRelaySocket != null && !sCellularRmiRelaySocket.isClosed()) {
                StringBuilder sb = new StringBuilder();
                sb.append("127.0.0.1 localhost\n");
                sb.append("::1 localhost\n");
                sb.append("127.0.0.1 asy.mof.gov.iq\n");
                try (FileOutputStream fos = new FileOutputStream(hostsFile)) {
                    fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                    fos.getFD().sync();
                }
                setReadableOwnerOnly(hostsFile);
                return;
            }
            InetAddress resolved = resolveHostDynamically(ALLOWED_MOF_HOST);
            if (resolved == null || resolved.getHostAddress() == null) {
                if (hostsFile.exists() && hostsFile.length() > 30) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("127.0.0.1 localhost\n");
                sb.append("::1 localhost\n");
                try (FileOutputStream fos = new FileOutputStream(hostsFile)) {
                    fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                    fos.getFD().sync();
                }
                setReadableOwnerOnly(hostsFile);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("127.0.0.1 localhost\n");
            sb.append("::1 localhost\n");
            sb.append(resolved.getHostAddress().trim()).append(" asy.mof.gov.iq\n");
            try (FileOutputStream fos = new FileOutputStream(hostsFile)) {
                fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            }
            setReadableOwnerOnly(hostsFile);
        } catch (Exception ignored) {
            // Ignore hosts cache update error
        }
    }

    private void extractBootJarsOnly(Context ctx, String assetName, File destDir) {
        int found = 0;
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            String destCanonPrefix = destDir.getCanonicalPath() + File.separator;
            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            while ((entry = tis.getNextTarEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith("./")) name = name.substring(2);
                if (BOOT_JAR_REL.equals(name) || JNLP_JAR_REL.equals(name)
                        || BOOTPATCH_JAR_NAME.equals(name) || (PREFIX_JARS + BOOTPATCH_JAR_NAME).equals(name)
                        || "mof.crt".equals(name)) {
                    File outFile = new File(destDir, name);
                    if (!outFile.getCanonicalPath().startsWith(destCanonPrefix)) {
                        continue;
                    }
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    setWritableOwnerOnly(outFile);
                    deleteQuietly(outFile);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int n;
                        while ((n = tis.read(buf)) != -1) fos.write(buf, 0, n);
                        fos.getFD().sync();
                    }
                    setReadableOwnerOnly(outFile);
                    setReadOnly(outFile);
                    found++;
                    if (found >= 5) break;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract mandatory boot bridge archives: " + e.getMessage(), e);
        }
        if (found < 4) {
            throw new IllegalStateException("Incomplete boot bridge archive extraction (found " + found + " files)");
        }
    }

    /**
     * Verifies all cached government JARs in {@code jarsDir} against the pinned UNCTAD/SOClass
     * certificate, immediately deleting any tampered or corrupted JAR and rebuilding {@code classpath.txt}
     * with only verified JARs.
     */
    private void verifyAndPurgeCachedJars(File clientDir) {
        if (clientDir == null) return;
        File jarsDir = new File(clientDir, "jars");
        File[] files = jarsDir.listFiles();
        if (files == null || files.length == 0) return;

        ExecutorService verifyPool = Executors.newFixedThreadPool(8);
        List<Future<?>> tasks = new ArrayList<>(files.length);
        for (final File f : files) {
            final String name = f.getName();
            if (!name.endsWith(".jar") || name.startsWith("00_")) continue;
            tasks.add(verifyPool.submit(() -> {
                if (!verifyDownloadedJarIntegrity(f)) {
                    setWritableOwnerOnly(f);
                    deleteQuietly(f);
                } else {
                    setReadOnly(f);
                }
            }));
        }
        verifyPool.shutdown();
        for (Future<?> t : tasks) {
            try {
                t.get();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException ignored) {
                // Handled per file
            }
        }
    }

    /**
     * Parses `<jar ... href="..."/>` entries from `AWIQWorld.jnlp` without backtracking regexes.
     */
    private static List<String> parseJnlpJarHrefs(String jnlpXml) {
        List<String> hrefs = new ArrayList<>();
        if (jnlpXml == null || jnlpXml.isEmpty()) return hrefs;
        String lower = jnlpXml.toLowerCase(Locale.US);
        int idx = 0;
        while (idx < lower.length()) {
            int jarStart = lower.indexOf("<jar", idx);
            if (jarStart < 0) break;
            int tagEnd = lower.indexOf('>', jarStart);
            if (tagEnd < 0) break;
            int hrefPos = lower.indexOf("href", jarStart);
            if (hrefPos > jarStart && hrefPos < tagEnd) {
                int eqPos = lower.indexOf('=', hrefPos);
                if (eqPos > hrefPos && eqPos < tagEnd) {
                    int q1 = jnlpXml.indexOf('"', eqPos + 1);
                    int q2 = (q1 >= 0 && q1 < tagEnd) ? jnlpXml.indexOf('"', q1 + 1) : -1;
                    if (q1 >= 0 && q2 > q1 && q2 < tagEnd) {
                        String val = jnlpXml.substring(q1 + 1, q2).trim();
                        if (!val.isEmpty()) {
                            hrefs.add(val);
                        }
                    }
                }
            }
            idx = tagEnd + 1;
        }
        return hrefs;
    }

    /**
     * Smart Government (MOF) JNLP & Dynamic JAR Updater.
     * Fetches the official `AWIQWorld.jnlp` manifest from `https://asy.mof.gov.iq/aw/AWIQWorld.jnlp`,
     * dynamically supports any number of JARs (whether 131, more, or fewer), checks for modified or
     * newly added JARs, removes deprecated government JARs no longer in `AWIQWorld.jnlp`, and rebuilds `classpath.txt`.
     */
    private int syncGovernmentJars(File clientDir, boolean manualForceCheck, AsycudaMaterialUi.UpdateCenterController modalCtrl) {
        int updatedCount = 0;
        sLastSyncException = null;
        try {
            if (clientDir != null && clientDir.getParentFile() != null) {
                refreshGlibcHostsCache(clientDir.getParentFile());
            }
            updateStatus("Checking customs packages on asy.mof.gov.iq...");
            if (modalCtrl != null) {
                modalCtrl.setMofUpdateStatus("Connecting to asy.mof.gov.iq...", "Reading " + FILE_AWIQ_JNLP, 0, 100, true);
            }
            if (!manualForceCheck) {
                Thread.sleep(250);
            }
            SSLSocketFactory sslFactory = createMofVerifiedSslFactory();

            int connTimeout = manualForceCheck ? 10000 : 5000;
            int readTimeout = manualForceCheck ? 15000 : 8000;

            String jnlpXml;
            HttpURLConnection jnlpConn = openMofConnection(MOF_JNLP_URL, sslFactory, connTimeout, readTimeout);
            try {
                jnlpConn.setRequestMethod("GET");
                int code = jnlpConn.getResponseCode();
                if (code != 200) {
                    verifyAndPurgeCachedJars(clientDir);
                    updateStatus("Server returned HTTP " + code + ", using verified local packages");
                    if (modalCtrl != null) {
                        modalCtrl.setMofUpdateStatus("Server returned HTTP " + code + " (using local files)", "Cached", 0, 0, false);
                    }
                    Thread.sleep(300);
                    return 0;
                }
                try (InputStream in = jnlpConn.getInputStream();
                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
                    jnlpXml = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                }
            } finally {
                jnlpConn.disconnect();
            }

            if (!jnlpXml.contains("<jnlp") || !jnlpXml.contains("<jar")) {
                return 0;
            }

            File localJnlp = new File(clientDir, FILE_AWIQ_JNLP);
            try (FileOutputStream fos = new FileOutputStream(localJnlp)) {
                fos.write(jnlpXml.getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Exception ignored) {
                // Ignore local JNLP cache write failure
            }
            if (sCellularBypassNetwork != null) {
                ensureCellularHttpsRelay();
                int newRmiPort = extractDynamicMofRmiPort(localJnlp);
                if (newRmiPort > 0) {
                    ensureCellularRmiRelay(newRmiPort);
                    if (clientDir != null && clientDir.getParentFile() != null) {
                        refreshGlibcHostsCache(clientDir.getParentFile());
                    }
                }
            }

            List<String> remoteHrefs = parseJnlpJarHrefs(jnlpXml);
            if (remoteHrefs.isEmpty()) {
                return 0;
            }

            File jarsDir = new File(clientDir, "jars");
            ensureDir(jarsDir);

            Set<String> cpEntries = new LinkedHashSet<>();
            cpEntries.add(BOOT_JAR_REL);
            cpEntries.add(JNLP_JAR_REL);

            Set<String> activeGovJarNames = new HashSet<>();
            activeGovJarNames.add("00_boot.jar");
            activeGovJarNames.add("00_jnlp.jar");

            int total = remoteHrefs.size();
            List<String> toDownloadUrls = new ArrayList<>();
            List<File> toDownloadFiles = new ArrayList<>();

            // Fast parallel HEAD verification (8 threads) so all JNLP JARs (e.g. 131+) are verified in ~2-3s
            ExecutorService pool = Executors.newFixedThreadPool(8);
            AtomicInteger checkedCounter = new AtomicInteger(0);
            List<Future<Boolean>> futures = new ArrayList<>();
            final int headTimeout = manualForceCheck ? 4000 : 2500;

            for (int i = 0; i < total; i++) {
                final String href = remoteHrefs.get(i);
                final String rawName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                final String flatName = rawName.replaceAll(REGEX_UNSAFE_FILENAME, "");
                final String jarUrl = resolveTrustedMofJarUrl(href);
                if (flatName.isEmpty() || flatName.startsWith("00_") || jarUrl == null) {
                    checkedCounter.incrementAndGet();
                    futures.add(null);
                    continue;
                }
                activeGovJarNames.add(flatName);
                final File targetJar = new File(jarsDir, flatName);

                futures.add(pool.submit(() -> {
                    boolean needsDl = !targetJar.exists() || targetJar.length() == 0;
                    if (!needsDl && !verifyDownloadedJarIntegrity(targetJar)) {
                        setWritableOwnerOnly(targetJar);
                        deleteQuietly(targetJar);
                        needsDl = true;
                    }
                    if (!needsDl) {
                        HttpURLConnection head = null;
                        try {
                            head = openMofConnection(jarUrl, sslFactory, headTimeout, headTimeout);
                            head.setRequestMethod("HEAD");
                            if (head.getResponseCode() == 200) {
                                long remoteLen = head.getContentLengthLong();
                                long remoteMod = head.getLastModified();
                                if ((remoteLen > 0 && remoteLen != targetJar.length())
                                        || (remoteMod > 0 && Math.abs(remoteMod - targetJar.lastModified()) > 2000L)) {
                                    needsDl = true;
                                }
                            }
                        } catch (Exception ignored) {
                            // Keep existing cached JAR if HEAD request times out
                        } finally {
                            if (head != null) head.disconnect();
                        }
                    }
                    int done = checkedCounter.incrementAndGet();
                    if (modalCtrl != null) {
                        modalCtrl.setMofUpdateStatus(
                                "Checking server packages (" + done + " / " + total + ")...",
                                flatName, done, total, false);
                    }
                    return needsDl;
                }));
            }
            pool.shutdown();

            for (int i = 0; i < total; i++) {
                Future<Boolean> fut = futures.get(i);
                if (fut == null) continue;
                String href = remoteHrefs.get(i);
                String rawName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                String flatName = rawName.replaceAll(REGEX_UNSAFE_FILENAME, "");
                String jarUrl = resolveTrustedMofJarUrl(href);
                if (jarUrl == null) continue;
                File targetJar = new File(jarsDir, flatName);
                try {
                    if (Boolean.TRUE.equals(fut.get())) {
                        toDownloadUrls.add(jarUrl);
                        toDownloadFiles.add(targetJar);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ExecutionException ignored) {
                    // Ignore single JAR verification exception
                }
            }

            // Clean up any deprecated JARs removed from the official AWIQWorld.jnlp manifest
            File[] existingJars = jarsDir.listFiles();
            if (existingJars != null) {
                for (File f : existingJars) {
                    String name = f.getName();
                    if (name.endsWith(".jar") && !name.startsWith("00_") && !activeGovJarNames.contains(name)) {
                        deleteQuietly(f);
                    }
                }
            }

            if (!toDownloadUrls.isEmpty()) {
                final int dlTotal = toDownloadUrls.size();
                final AtomicInteger dlDoneCounter = new AtomicInteger(0);
                final AtomicInteger dlSuccessCounter = new AtomicInteger(0);
                ExecutorService dlPool = Executors.newFixedThreadPool(8);
                List<Future<Boolean>> dlFutures = new ArrayList<>(dlTotal);

                for (int d = 0; d < dlTotal; d++) {
                    final String jarUrl = toDownloadUrls.get(d);
                    final File targetJar = toDownloadFiles.get(d);
                    dlFutures.add(dlPool.submit(() -> {
                        if (sApkDownloadActive.get()) return false;
                        boolean ok = false;
                        for (int attempt = 1; attempt <= 3 && !sApkDownloadActive.get(); attempt++) {
                            if (downloadJarAtomically(jarUrl, targetJar, sslFactory)) {
                                ok = true;
                                break;
                            }
                        }
                        int doneNow = dlDoneCounter.incrementAndGet();
                        if (ok) dlSuccessCounter.incrementAndGet();
                        String msg = "Downloading official packages from asy.mof.gov.iq (" + doneNow + "/" + dlTotal + ")...";
                        updateStatus(msg);
                        if (modalCtrl != null) {
                            modalCtrl.setMofUpdateStatus(msg, targetJar.getName(), doneNow, dlTotal, false);
                        }
                        return ok;
                    }));
                }
                dlPool.shutdown();
                for (Future<Boolean> f : dlFutures) {
                    try {
                        f.get();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (ExecutionException ignored) {
                        // Individual download failure handled by counter
                    }
                }
                updatedCount = dlSuccessCounter.get();

                // Second-pass retry for any JARs that timed out during high-concurrency pass 1
                List<String> retryUrls = new ArrayList<>();
                List<File> retryFiles = new ArrayList<>();
                for (int d = 0; d < dlTotal; d++) {
                    File f = toDownloadFiles.get(d);
                    if (!f.isFile() || f.length() == 0) {
                        retryUrls.add(toDownloadUrls.get(d));
                        retryFiles.add(f);
                    }
                }
                if (!retryUrls.isEmpty() && !sApkDownloadActive.get()) {
                    final int retryTotal = retryUrls.size();
                    final AtomicInteger retryDone = new AtomicInteger(0);
                    ExecutorService retryPool = Executors.newFixedThreadPool(4);
                    List<Future<Boolean>> retryFutures = new ArrayList<>(retryTotal);
                    for (int r = 0; r < retryTotal; r++) {
                        final String rUrl = retryUrls.get(r);
                        final File rFile = retryFiles.get(r);
                        retryFutures.add(retryPool.submit(() -> {
                            boolean ok = false;
                            for (int attempt = 1; attempt <= 3 && !sApkDownloadActive.get(); attempt++) {
                                if (downloadJarAtomically(rUrl, rFile, sslFactory)) {
                                    ok = true;
                                    break;
                                }
                            }
                            int doneR = retryDone.incrementAndGet();
                            if (ok) dlSuccessCounter.incrementAndGet();
                            String msg = "Completing remaining packages from asy.mof.gov.iq (" + doneR + "/" + retryTotal + ")...";
                            updateStatus(msg);
                            if (modalCtrl != null) {
                                modalCtrl.setMofUpdateStatus(msg, rFile.getName(), doneR, retryTotal, false);
                            }
                            return ok;
                        }));
                    }
                    retryPool.shutdown();
                    for (Future<Boolean> rf : retryFutures) {
                        try {
                            rf.get();
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (ExecutionException ignored) {
                            // Handled by counter
                        }
                    }
                    updatedCount = dlSuccessCounter.get();
                }

                int stillMissing = 0;
                for (File f : toDownloadFiles) {
                    if (!f.isFile() || f.length() == 0) stillMissing++;
                }
                if (stillMissing > 0) {
                    sLastSyncException = new IOException("Incomplete package download: " + stillMissing + " of " + dlTotal + " official JARs timed out over current connection.");
                }
            }

            // Rebuild classpath.txt with preserved bridge jars first and only verified existing government jars
            for (int i = 0; i < total; i++) {
                String href = remoteHrefs.get(i);
                String rawName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                String flatName = rawName.replaceAll(REGEX_UNSAFE_FILENAME, "");
                if (flatName.isEmpty() || flatName.startsWith("00_")) continue;
                File verifiedJar = new File(jarsDir, flatName);
                if (verifiedJar.exists() && verifiedJar.length() > 0) {
                    cpEntries.add(PREFIX_JARS + flatName);
                }
            }
            StringBuilder cpBuilder = new StringBuilder();
            for (String entry : cpEntries) {
                if (cpBuilder.length() > 0) cpBuilder.append(":");
                cpBuilder.append(entry);
            }
            File cpFile = new File(clientDir, FILE_CLASSPATH);
            setWritableOwnerOnly(cpFile);
            try (FileOutputStream fos = new FileOutputStream(cpFile)) {
                fos.write(cpBuilder.toString().getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Exception ignored) {
                // Ignore classpath write error
            }
            setReadableOwnerOnly(cpFile);
            setReadOnly(cpFile);

            if (modalCtrl != null) {
                if (updatedCount > 0) {
                    modalCtrl.setMofUpdateStatus("Downloaded " + updatedCount + " updated package(s)",
                            total + " packages checked", total, total, false);
                } else {
                    modalCtrl.setMofUpdateStatus("All " + total + " customs packages are up to date",
                            ALLOWED_MOF_HOST, 0, 0, false);
                }
            } else if (!manualForceCheck) {
                if (updatedCount > 0) {
                    updateStatus("Downloaded " + updatedCount + " updated package(s)");
                } else {
                    updateStatus("Customs packages verified (" + total + " files)");
                }
                Thread.sleep(400);
            }

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception err) {
            sLastSyncException = err;
            try {
                verifyAndPurgeCachedJars(clientDir);
                String[] list = new File(clientDir, "jars").list();
                int cached = list != null ? list.length : 0;
                String errDetail = err.getClass().getSimpleName()
                        + (err.getMessage() != null ? ": " + err.getMessage() : "");
                int rmiPort = extractDynamicMofRmiPort(new File(clientDir, FILE_AWIQ_JNLP));
                boolean rmiOnline = rmiPort > 0 && probeMofRmiPortOnline(rmiPort, 2500);
                appendNetworkDiagnosticLogEntry(clientDir, "AsycudaStandaloneManager.syncGovernmentJars", err,
                        "HTTPS JNLP/JAR sync failed (" + errDetail + "); rmiPort=" + rmiPort + ", rmiOnline=" + rmiOnline + ", cachedJars=" + cached);
                if (rmiOnline) {
                    updateStatus("Customs Server (:" + rmiPort + ") online — using " + cached + " verified local packages");
                    if (modalCtrl != null) {
                        modalCtrl.setMofUpdateStatus(
                                "Customs Server (:" + rmiPort + ") Online — " + cached + " packages ready",
                                "MoF Web JNLP temporarily offline (" + errDetail + ")",
                                0, 0, false);
                    }
                } else {
                    updateStatus("Offline mode: using " + cached + " local packages (" + errDetail + ")");
                    if (modalCtrl != null) {
                        modalCtrl.setMofUpdateStatus("Server unreachable, using " + cached + " local files",
                                errDetail, 0, 0, false);
                    }
                    // Only show the startup diagnostic modal here if all local manifest JARs already exist
                    // (otherwise bootstrapEnvironmentAndStart handles initial_package_download right after return)
                    if (sCurrentActivity != null && (areAllManifestJarsPresent(clientDir) || modalCtrl != null)) {
                        MainActivity act = sCurrentActivity;
                        diagnoseAndShowUniversalNetworkModal(
                                act,
                                manualForceCheck ? "mof_jar_sync" : "startup_mof_unreachable",
                                err,
                                "EXIT CODE = 12 | SERVER NOT FOUND | " + errDetail,
                                () -> restartAsycudaSession(act));
                    }
                }
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored2) {
                // Ignore fallback status error
            }
        }
        return updatedCount;
    }

    private static List<String> parseJnlpApplicationArgs(File jnlpFile) {
        List<String> args = new ArrayList<>();
        if (jnlpFile != null && jnlpFile.exists()) {
            try {
                String xml = readTail(jnlpFile, 16384);
                String lower = xml.toLowerCase(Locale.US);
                int idx = 0;
                while (idx < lower.length()) {
                    int s = lower.indexOf("<argument>", idx);
                    if (s < 0) break;
                    int e = lower.indexOf("</argument>", s);
                    if (e < 0) break;
                    String val = xml.substring(s + "<argument>".length(), e).trim();
                    if (!val.isEmpty()) {
                        String lowVal = val.toLowerCase(Locale.US);
                        if (lowVal.startsWith("-") || lowVal.contains(ALLOWED_MOF_HOST)) {
                            if (sCellularBypassNetwork != null && sCellularHttpsRelayPort > 0
                                    && lowVal.startsWith("https://" + ALLOWED_MOF_HOST + "/")) {
                                val = "https://" + ALLOWED_MOF_HOST + ":" + sCellularHttpsRelayPort
                                        + val.substring(("https://" + ALLOWED_MOF_HOST).length());
                            }
                            args.add(val);
                        }
                    }
                    idx = e + "</argument>".length();
                }
            } catch (Exception ignored) {
                // Ignore JNLP read failure
            }
        }
        return args;
    }

    private static int extractDynamicMofRmiPort(File jnlpFile) {
        List<String> args = parseJnlpApplicationArgs(jnlpFile);
        for (String arg : args) {
            String low = arg.toLowerCase(Locale.US);
            if (low.startsWith("https://") || low.startsWith("http://")) {
                continue;
            }
            int idx = low.indexOf(ALLOWED_MOF_HOST + ":");
            if (idx >= 0) {
                int start = idx + (ALLOWED_MOF_HOST + ":").length();
                int end = start;
                while (end < low.length() && Character.isDigit(low.charAt(end))) {
                    end++;
                }
                if (end > start) {
                    try {
                        int p = Integer.parseInt(low.substring(start, end));
                        if (p > 0 && p <= 65535) return p;
                    } catch (NumberFormatException ignored) {
                        // Ignore malformed port
                    }
                }
            }
        }
        return -1;
    }

    private boolean probeMofRmiPortOnline(int rmiPort, int timeoutMs) {
        if (rmiPort <= 0) return false;
        InetAddress addr = resolveHostDynamically(ALLOWED_MOF_HOST);
        if (addr == null) return false;
        try (Socket s = new Socket()) {
            s.connect(new java.net.InetSocketAddress(addr, rmiPort), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean downloadJarAtomically(String jarUrl, File targetJar, SSLSocketFactory sslFactory) {
        File tmpJar = new File(targetJar.getParentFile(), targetJar.getName() + ".tmp");
        HttpURLConnection conn = null;
        try {
            conn = openMofConnection(jarUrl, sslFactory, 6000, 20000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) return false;

            long remoteMod = conn.getLastModified();
            try (InputStream in = new BufferedInputStream(conn.getInputStream(), 65536);
                 FileOutputStream out = new FileOutputStream(tmpJar)) {
                byte[] buf = new byte[65536];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.getFD().sync();
            }

            if (tmpJar.length() > 0 && verifyDownloadedJarIntegrity(tmpJar)) {
                if (targetJar.exists()) {
                    setWritableOwnerOnly(targetJar);
                    deleteQuietly(targetJar);
                }
                if (tmpJar.renameTo(targetJar)) {
                    if (remoteMod > 0 && !targetJar.setLastModified(remoteMod)) {
                        // Timestamp update is best-effort
                    }
                    setReadableOwnerOnly(targetJar);
                    if (!targetJar.setWritable(false, false)) {
                        // Read-only lock is best-effort on non-POSIX filesystems
                    }
                    return true;
                }
            }
        } catch (Exception ignored) {
            // Clean up temporary file in finally block
        } finally {
            if (conn != null) conn.disconnect();
            if (tmpJar.exists()) deleteQuietly(tmpJar);
        }
        return false;
    }

    private static final String SCHEME_HTTPS = AsycudaSecurityValidator.SCHEME_HTTPS;
    private static final String ALLOWED_MOF_HOST = AsycudaSecurityValidator.ALLOWED_MOF_HOST;

    /**
     * Resolves a JNLP {@code <jar href="..."/>} reference strictly against {@code https://asy.mof.gov.iq/aw/}
     * and enforces an explicit HTTPS hostname/port allowlist ({@code asy.mof.gov.iq:443}) via
     * {@link AsycudaSecurityValidator#resolveTrustedMofJarUrl(String)}.
     */
    public static String resolveTrustedMofJarUrl(String href) {
        return AsycudaSecurityValidator.resolveTrustedMofJarUrl(href);
    }

    /**
     * Verifies that a downloaded JAR is a well-formed ZIP/JAR archive and cryptographically signed
     * by the pinned UNCTAD/SOClass X.509 certificate via
     * {@link AsycudaSecurityValidator#verifyDownloadedJarIntegrity(File)}.
     */
    public static boolean verifyDownloadedJarIntegrity(File jarFile) {
        return AsycudaSecurityValidator.verifyDownloadedJarIntegrity(jarFile);
    }

    private HttpURLConnection openMofConnection(String urlStr, SSLSocketFactory sslFactory, int connTimeout, int readTimeout) throws IOException {
        URL url = new URL(urlStr);
        if (!SCHEME_HTTPS.equalsIgnoreCase(url.getProtocol()) || !ALLOWED_MOF_HOST.equalsIgnoreCase(url.getHost())) {
            throw new SecurityException("Blocked connection to untrusted host: " + url.getHost());
        }
        HttpURLConnection conn = null;
        try {
            android.net.Network cellBypass = sCellularBypassNetwork;
            if (cellBypass != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                conn = (HttpURLConnection) cellBypass.openConnection(url);
            } else {
                MainActivity act = sCurrentActivity;
                if (act != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    android.net.ConnectivityManager cm = (android.net.ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
                    android.net.Network activeNet = cm != null ? cm.getActiveNetwork() : null;
                    if (activeNet != null) {
                        conn = (HttpURLConnection) activeNet.openConnection(url);
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall back to default url.openConnection()
        }
        if (conn == null) {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setInstanceFollowRedirects(false);
        if (conn instanceof HttpsURLConnection && sslFactory != null) {
            HttpsURLConnection https = (HttpsURLConnection) conn;
            https.setSSLSocketFactory(sslFactory);
            https.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
        }
        conn.setConnectTimeout(connTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setUseCaches(false);
        conn.setRequestProperty(HEADER_USER_AGENT, "JNLP/1.7.0 javaws/1.8.0_504 (b01) Java/1.8.0_504");
        return conn;
    }

    /**
     * Builds a verified TLS socket factory using Android's standard `TrustManagerFactory`
     * initialized with the system `AndroidCAStore` plus the official Sectigo/ZeroSSL CA
     * certificates used by `asy.mof.gov.iq`.
     */
    private SSLSocketFactory createMofVerifiedSslFactory() {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);

            // 1. Copy all standard Android system root CAs
            KeyStore systemKs = KeyStore.getInstance("AndroidCAStore");
            systemKs.load(null, null);
            Enumeration<String> aliases = systemKs.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = systemKs.getCertificate(alias);
                if (cert != null) {
                    ks.setCertificateEntry("sys_" + alias, cert);
                }
            }

            // 2. Add official Sectigo Root R46 (cross-signed by USERTrust) & ZeroSSL RSA DV SSL CA 2
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (InputStream in1 = new ByteArrayInputStream(SECTIGO_ROOT_R46_CROSS_PEM.getBytes(StandardCharsets.US_ASCII));
                 InputStream in2 = new ByteArrayInputStream(ZEROSSL_RSA_DV_CA2_PEM.getBytes(StandardCharsets.US_ASCII))) {
                ks.setCertificateEntry("sectigo_root_r46", cf.generateCertificate(in1));
                ks.setCertificateEntry("zerossl_rsa_dv_ca2", cf.generateCertificate(in2));
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, tmf.getTrustManagers(), new SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception t) {
            return HttpsURLConnection.getDefaultSSLSocketFactory();
        }
    }

    private void setPermissionsRecursive(File f) {
        if (f == null || !f.exists()) return;
        setReadableOwnerOnly(f);
        if (f.isDirectory()) {
            setExecutableOwnerOnly(f);
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) setPermissionsRecursive(c);
            }
        } else {
            String name = f.getName();
            String path = f.getAbsolutePath();
            if (name.contains(".so") || path.contains("/bin/") || path.contains("/lib/")) {
                setExecutableOwnerOnly(f);
            }
        }
    }

    private static void killOrphanedJvmProcesses() {
        try {
            int myPid = android.os.Process.myPid();
            File procDir = new File("/proc");
            File[] entries = procDir.listFiles();
            if (entries == null) return;
            for (File entry : entries) {
                String name = entry.getName();
                if (!name.matches("\\d+")) continue;
                int pid = Integer.parseInt(name);
                if (pid == myPid) continue;
                File cmdline = new File(entry, "cmdline");
                if (!cmdline.exists() || !cmdline.canRead()) continue;
                try (FileInputStream fis = new FileInputStream(cmdline)) {
                    byte[] buf = new byte[512];
                    int n = fis.read(buf);
                    if (n > 0) {
                        String cmd = new String(buf, 0, n, StandardCharsets.UTF_8);
                        if (cmd.contains("asycuda.running=1") || cmd.contains("AsycudaBoot")) {
                            Os.kill(pid, android.system.OsConstants.SIGKILL);
                        }
                    }
                } catch (Exception ignored) {
                    // Ignore unreadable proc entry
                }
            }
        } catch (Exception ignored) {
            // Best-effort orphan cleanup
        }
    }

    private void ensureX11ServerSocketReady(MainActivity activity, File filesDir) throws InterruptedException {
        File usrTmpDir = new File(filesDir, DIR_USR_TMP);
        File x11UnixDir = new File(usrTmpDir, ".X11-unix");
        ensureDir(usrTmpDir);
        ensureDir(x11UnixDir);
        try {
            Os.chmod(usrTmpDir.getAbsolutePath(), MODE_OWNER_RWX);
            Os.chmod(x11UnixDir.getAbsolutePath(), MODE_OWNER_RWX);
        } catch (Exception ignored) {
            // Ignore chmod if filesystem does not support POSIX permissions
        }
        File x0Socket = new File(x11UnixDir, "X0");
        if (sCmdEntry == null || !x0Socket.exists()) {
            deleteQuietly(new File(usrTmpDir, ".X0-lock"));
            deleteQuietly(x0Socket);
            UI.post(() -> {
                try {
                    System.loadLibrary("Xlorie");
                    CmdEntryPoint.ctx = activity.getApplicationContext();
                    sCmdEntry = new CmdEntryPoint(new String[] { ":0", "-nolisten", "tcp" });
                    MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                    act.connectToService(sCmdEntry);
                } catch (UnsatisfiedLinkError | Exception t) {
                    Toast.makeText(activity, "X11 Init Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            UI.post(() -> {
                try {
                    MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                    act.connectToService(sCmdEntry);
                } catch (Exception ignored) {
                    // Best-effort service reconnect
                }
            });
        }
        for (int i = 0; i < 40; i++) {
            if (x0Socket.exists()) break;
            Thread.sleep(150);
        }
    }

    private void launchAsycudaJvm(MainActivity activity) {
        File logFile;
        Process proc;
        synchronized (JVM_LAUNCH_LOCK) {
            try {
                if (sJavaProcess != null && isProcessAlive(sJavaProcess)) {
                    hideSplashDelayed(400);
                    return;
                }
                killOrphanedJvmProcesses();

                File filesDir = new File("/data/data/" + activity.getPackageName() + "/files");
                if (!filesDir.exists()) filesDir = activity.getFilesDir();

                ensureX11ServerSocketReady(activity, filesDir);

                // Evaluate active route and activate 4G Cellular Multipath Bypass if Wi-Fi ISP is blocked by MoF firewall
                evaluateMofRouteAndCellularBypass(activity.getApplicationContext(), filesDir);

                // Pre-populate usr/glibc/etc/hosts with resolved IP (or 127.0.0.1 4G relay) for asy.mof.gov.iq
                refreshGlibcHostsCache(filesDir);

                File clientDir = new File(filesDir, DIR_CLIENT);
                File jreDir = new File(filesDir, "jre8");
                File javaBin = new File(jreDir, "jre/bin/java");
                if (!javaBin.exists() || javaBin.length() == 0) {
                    javaBin = new File(jreDir, "bin/java");
                }
                setExecutableOwnerOnly(javaBin);
                patchLibJliForTargetSdk34(jreDir);

                File ldLinux = new File(filesDir, "usr/glibc/lib/ld-linux-aarch64.so.1");
                setExecutableOwnerOnly(ldLinux);
                File nativeLdLinux = new File(activity.getApplicationInfo().nativeLibraryDir, "libld_linux_aarch64.so");

                File tmpDir = new File(clientDir, "tmp");
                deleteRecursive(tmpDir);
                ensureDir(new File(tmpDir, "soe.tmp"));
                ensureDir(new File(tmpDir, "son.tmp"));

                File[] locks = clientDir.listFiles();
                if (locks != null) {
                    for (File lf : locks) {
                        if (lf.getName().endsWith(".lck")) deleteQuietly(lf);
                    }
                }

                File printsDir = new File(filesDir, DIR_PRINTS);
                File workspaceDir = new File(filesDir, DIR_WORKSPACE);
                ensureDir(printsDir);
                ensureDir(workspaceDir);

                // Keep user.home on fast internal flash (/data/data/iq.asycuda/files/home)
                // so ASYCUDA's .SOClassCache (178 MB of small DB files) avoids Android's slow FUSE layer
                File fastInternalHome = new File(filesDir, "home");
                ensureDir(fastInternalHome);

                String cpVal = BOOT_JAR_REL + ":" + JNLP_JAR_REL;
                File cpFile = new File(clientDir, FILE_CLASSPATH);
                if (cpFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(cpFile))) {
                        String line = br.readLine();
                        if (line != null && !line.trim().isEmpty()) {
                            String rawCp = line.trim().replace("/data/data/com.termux/files/home/asycuda_iraq/", "");
                            StringBuilder filteredCp = new StringBuilder();
                            filteredCp.append(BOOT_JAR_REL).append(":").append(JNLP_JAR_REL);
                            for (String token : rawCp.split(":")) {
                                String entry = token.trim();
                                if (entry.startsWith(PREFIX_JARS) && entry.endsWith(".jar")
                                        && !entry.contains("..") && !entry.equals(BOOT_JAR_REL) && !entry.equals(JNLP_JAR_REL)) {
                                    String jarName = entry.substring(PREFIX_JARS.length());
                                    if (!jarName.contains("/") && !jarName.contains("\\")) {
                                        File jarFile = new File(clientDir, entry);
                                        if (jarFile.isFile() && jarFile.length() > 0
                                                && verifyDownloadedJarIntegrity(jarFile)) {
                                            filteredCp.append(":").append(entry);
                                        }
                                    }
                                }
                            }
                            cpVal = filteredCp.toString();
                        }
                    } catch (Exception ignored) {
                        // Use bridge classpath fallback
                    }
                }

                List<String> cmd = new ArrayList<>();
                if (nativeLdLinux.exists()) {
                    cmd.add(nativeLdLinux.getAbsolutePath());
                    cmd.add("--argv0");
                    cmd.add(javaBin.getAbsolutePath());
                }
                cmd.add(javaBin.getAbsolutePath());
                cmd.add("-Dasycuda.running=1");
                cmd.add("-Xbootclasspath/p:" + new File(clientDir, BOOTPATCH_JAR_NAME).getAbsolutePath());
                String hardenedLibPath = new File(jreDir, "jre/lib/aarch64").getAbsolutePath() + ":"
                        + new File(jreDir, "jre/lib/aarch64/jli").getAbsolutePath() + ":"
                        + new File(jreDir, "jre/lib/aarch64/server").getAbsolutePath() + ":"
                        + new File(jreDir, "lib/aarch64").getAbsolutePath() + ":"
                        + new File(jreDir, "lib/aarch64/jli").getAbsolutePath() + ":"
                        + new File(jreDir, "lib/aarch64/server").getAbsolutePath() + ":"
                        + new File(filesDir, "usr/glibc/lib").getAbsolutePath();
                cmd.add("-Djava.library.path=" + hardenedLibPath);
                cmd.add("-Xms256m");
                cmd.add("-Xmx1280m");
                cmd.add("-Djava.io.tmpdir=" + tmpDir.getAbsolutePath());
                cmd.add("-Dasycuda.prints.dir=" + printsDir.getAbsolutePath());
                cmd.add("-Dasycuda.workspace.dir=" + workspaceDir.getAbsolutePath());
                cmd.add("-Djava.net.preferIPv4Stack=true");
                cmd.add("-Dsun.net.inetaddr.ttl=30");
                cmd.add("-Dnetworkaddress.cache.ttl=30");
                cmd.add("-Dhttp.keepAlive=true");
                cmd.add("-Dhttp.maxConnections=32");
                cmd.add("-Dsun.rmi.transport.tcp.responseTimeout=15000");
                cmd.add("-Dpreload.supportedLng=on");
                cmd.add("-Dpreload.licenseInfo=on");
                cmd.add("-Dpreload.formatDictionary=on");
                cmd.add("-Dpreload.desktop=on");
                cmd.add("-Dpreload.availableRT=on");
                cmd.add("-Dpreload.DL=on");
                cmd.add("-Djava.rmi.server.hostname=127.0.0.1");
                cmd.add("-Duser.home=" + fastInternalHome.getAbsolutePath());
                cmd.add("-Djava.util.logging.manager=so.util.logging.SOLogManager");
                cmd.add("-cp");
                cmd.add(cpVal);
                cmd.add("asycuda.AsycudaBoot");
                cmd.addAll(parseJnlpApplicationArgs(new File(clientDir, FILE_AWIQ_JNLP)));

                logFile = new File(clientDir, FILE_ASYCUDA_LOG);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(clientDir);
                pb.redirectErrorStream(true);
                pb.redirectOutput(logFile);

                Map<String, String> env = pb.environment();
                env.remove("LD_PRELOAD");
                env.remove("LD_LIBRARY_PATH");
                File javaHomeDir = new File(jreDir, "jre");
                if (!javaHomeDir.exists()) javaHomeDir = jreDir;
                env.put("JAVA_HOME", javaHomeDir.getAbsolutePath());
                env.put("HOME", fastInternalHome.getAbsolutePath());
                env.put("PREFIX", new File(filesDir, "usr").getAbsolutePath());
                env.put("TMPDIR", new File(filesDir, DIR_USR_TMP).getAbsolutePath());
                env.put("DISPLAY", ":0");
                env.put("ASYCUDA_BRIDGE_TOKEN", BRIDGE_AUTH_TOKEN);

                proc = pb.start();
                sJavaProcess = proc;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception t) {
                updateStatus("Failed to launch JVM: " + t.getMessage());
                diagnoseAndShowUniversalNetworkModal(
                        activity,
                        "jvm_process_spawn_failed",
                        t,
                        "Failed to spawn JVM process: " + t.getClass().getName() + ": " + t.getMessage(),
                        () -> restartAsycudaSession(activity));
                return;
            }
        }

        try {
            Thread.sleep(2200);
            if (!isProcessAlive(proc)) {
                int code = proc.exitValue();
                String errTail = readTail(logFile, 1200);
                updateStatus("JVM exited (code " + code + "):\n" + errTail);
                if (code != 0 || isAbnormalJvmExit(code, errTail)) {
                    diagnoseAndShowUniversalNetworkModal(
                            activity,
                            "jvm_early_exit_" + code,
                            null,
                            "JVM exited with code " + code + "\n" + errTail,
                            () -> restartAsycudaSession(activity));
                }
            } else {
                hideSplashDelayed(600);
                watchJvmProcessForNetworkExit(activity, proc, logFile);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception t) {
            updateStatus("JVM status error: " + t.getMessage());
        }
    }

    private static boolean isAbnormalJvmExit(int exitCode, String logTail) {
        if (exitCode != 0) return true;
        if (logTail == null || logTail.isEmpty()) return false;
        String upper = logTail.toUpperCase(Locale.US);
        return upper.contains("NETWORK ERROR")
                || upper.contains("SERVER NOT FOUND")
                || upper.contains("EXIT CODE = 12")
                || upper.contains("EXCEPTION")
                || upper.contains("ERROR:")
                || upper.contains("OUTOFMEMORYERROR")
                || upper.contains("UNSATISFIEDLINKERROR")
                || upper.contains("NOCLASSDEFFOUNDERROR")
                || upper.contains("CORRUPTED");
    }

    private void watchJvmProcessForNetworkExit(final MainActivity activity, final Process proc, final File logFile) {
        Thread watcher = new Thread(() -> {
            boolean shownLiveLogModal = false;
            try {
                while (isProcessAlive(proc)) {
                    if (sJavaProcess != proc) {
                        return;
                    }
                    if (!shownLiveLogModal && logFile != null && logFile.exists()) {
                        String liveTail = readTail(logFile, 1200);
                        String upper = liveTail.toUpperCase(Locale.US);
                        if (upper.contains("SERVER NOT FOUND")
                                || upper.contains("EXIT CODE = 12")
                                || upper.contains("*** FATAL ERROR:")
                                || upper.contains("OUTOFMEMORYERROR")
                                || upper.contains("UNSATISFIEDLINKERROR")) {
                            shownLiveLogModal = true;
                            MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                            if (act != null && !act.isFinishing()) {
                                diagnoseAndShowUniversalNetworkModal(
                                        act,
                                        "jvm_live_fatal_error",
                                        null,
                                        liveTail,
                                        () -> restartAsycudaSession(act));
                            }
                        }
                    }
                    Thread.sleep(800);
                }
                int exitCode = proc.waitFor();
                if (sJavaProcess != proc) {
                    // Process was intentionally restarted or shut down by the user
                    return;
                }
                String logTail = readTail(logFile, 2400);
                if (!shownLiveLogModal && isAbnormalJvmExit(exitCode, logTail)) {
                    MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                    if (act != null && !act.isFinishing()) {
                        diagnoseAndShowUniversalNetworkModal(
                                act,
                                "jvm_session_exit_" + exitCode,
                                null,
                                "JVM exited with code " + exitCode + "\n" + logTail,
                                () -> restartAsycudaSession(act));
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
                // Ignore watcher termination error
            }
        }, "Asycuda-JvmExitWatcher");
        watcher.setDaemon(true);
        watcher.start();
    }

    private static String captureCodeLineLocation(Throwable t) {
        StringBuilder sb = new StringBuilder();
        if (t != null) {
            StackTraceElement[] trace = t.getStackTrace();
            if (trace != null && trace.length > 0) {
                StackTraceElement top = trace[0];
                sb.append(top.getClassName()).append(".").append(top.getMethodName())
                        .append("(").append(top.getFileName()).append(":").append(top.getLineNumber()).append(")");
                for (StackTraceElement el : trace) {
                    if (el.getClassName() != null && (el.getClassName().startsWith("com.termux.x11")
                            || el.getClassName().startsWith("asycuda")
                            || el.getClassName().startsWith("so.kernel"))) {
                        String loc = el.getClassName() + "." + el.getMethodName()
                                + "(" + el.getFileName() + ":" + el.getLineNumber() + ")";
                        if (!sb.toString().contains(loc)) {
                            sb.append(" <- ").append(loc);
                        }
                        break;
                    }
                }
            }
        }
        StackTraceElement[] callerTrace = new Throwable().getStackTrace();
        for (StackTraceElement el : callerTrace) {
            if (el.getClassName() != null && el.getClassName().contains("AsycudaStandaloneManager")
                    && !"captureCodeLineLocation".equals(el.getMethodName())
                    && !"appendNetworkDiagnosticLogEntry".equals(el.getMethodName())
                    && !"diagnoseAndShowUniversalNetworkModal".equals(el.getMethodName())) {
                if (sb.length() > 0) sb.append(" | Triggered at ");
                sb.append(el.getFileName()).append(":").append(el.getLineNumber())
                        .append(" (").append(el.getMethodName()).append(")");
                break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "AsycudaStandaloneManager.java (DiagnosticMonitor)";
    }

    private static final StringBuilder sRedactedRamDiagRingBuffer = new StringBuilder(4096);

    private static void wipeRedactedRamDiagRingBuffer() {
        synchronized (sRedactedRamDiagRingBuffer) {
            for (int i = 0; i < sRedactedRamDiagRingBuffer.length(); i++) {
                sRedactedRamDiagRingBuffer.setCharAt(i, '\0');
            }
            sRedactedRamDiagRingBuffer.setLength(0);
            sRedactedRamDiagRingBuffer.trimToSize();
        }
    }

    private void appendNetworkDiagnosticLogEntry(File clientDir, String callerMethod, Throwable err, String summary) {
        if (clientDir == null) return;
        try {
            String codeLine = captureCodeLineLocation(err);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date());
            StringBuilder entry = new StringBuilder();
            entry.append("[UNIVERSAL-DIAGNOSTIC] ").append(timestamp)
                    .append(" | Method: ").append(callerMethod)
                    .append(" | CodeLine: ").append(codeLine)
                    .append(" | Summary: ").append(redactSensitiveLogContent(summary)).append("\n");
            if (err != null) {
                entry.append("[UNIVERSAL-DIAGNOSTIC-EXCEPTION] ").append(err.getClass().getName())
                        .append(": ").append(redactSensitiveLogContent(err.getMessage())).append("\n");
            }
            String redactedEntry = redactSensitiveLogContent(entry.toString());
            for (int i = 0; i < entry.length(); i++) {
                entry.setCharAt(i, '\0');
            }
            entry.setLength(0);
            synchronized (sRedactedRamDiagRingBuffer) {
                if (sRedactedRamDiagRingBuffer.length() + redactedEntry.length() > 6144) {
                    int drop = Math.min(sRedactedRamDiagRingBuffer.length(), redactedEntry.length() + 1024);
                    for (int i = 0; i < drop; i++) {
                        sRedactedRamDiagRingBuffer.setCharAt(i, '\0');
                    }
                    sRedactedRamDiagRingBuffer.delete(0, drop);
                }
                sRedactedRamDiagRingBuffer.append(redactedEntry);
            }
        } catch (Exception ignored) {
            // Best-effort in-RAM redacted diagnostic append (zero disk writes)
        }
    }

    private org.json.JSONArray loadKnownErrorsCatalogEntries(Context ctx) {
        if (ctx == null) return null;
        // 1. Try synced catalog in private internal storage (/files/known_errors_catalog.json)
        File syncedFile = new File(ctx.getFilesDir(), FILE_KNOWN_ERRORS_CATALOG);
        if (syncedFile.isFile() && syncedFile.length() > 0 && syncedFile.length() <= 262144) {
            try {
                byte[] bytes = readFileBytes(syncedFile);
                String json = new String(bytes, StandardCharsets.UTF_8);
                if (validateKnownErrorsCatalogJson(json)) {
                    return new JSONObject(json).optJSONArray("entries");
                }
            } catch (Exception ignored) {
                // Fall back to bundled APK asset catalog
            }
        }
        // 2. Fallback to bundled offline APK asset catalog (assets/known-errors-catalog.json)
        try (InputStream in = ctx.getAssets().open(ASSET_KNOWN_ERRORS_CATALOG);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            String json = new String(bos.toByteArray(), StandardCharsets.UTF_8);
            if (validateKnownErrorsCatalogJson(json)) {
                return new JSONObject(json).optJSONArray("entries");
            }
        } catch (Exception ignored) {
            // Ignore asset read error
        }
        return null;
    }

    private static JSONObject findCatalogEntryById(org.json.JSONArray entries, String targetId) {
        if (entries == null || targetId == null) return null;
        for (int i = 0; i < entries.length(); i++) {
            JSONObject obj = entries.optJSONObject(i);
            if (obj != null && targetId.equalsIgnoreCase(obj.optString("id", ""))) {
                return obj;
            }
        }
        return null;
    }

    private static JSONObject findCatalogEntryByKeywords(org.json.JSONArray entries, String combinedUpperText) {
        if (entries == null || combinedUpperText == null || combinedUpperText.isEmpty()) return null;
        for (int i = 0; i < entries.length(); i++) {
            JSONObject obj = entries.optJSONObject(i);
            if (obj == null) continue;
            org.json.JSONArray kwArr = obj.optJSONArray("matchKeywords");
            if (kwArr == null) continue;
            for (int k = 0; k < kwArr.length(); k++) {
                String kw = kwArr.optString(k, "").trim().toUpperCase(Locale.US);
                if (!kw.isEmpty() && combinedUpperText.contains(kw)) {
                    return obj;
                }
            }
        }
        return null;
    }

    private String resolveVerifiedFixButtonLabel(MainActivity activity, String verifiedFixId, String fixedInVersion) {
        if (verifiedFixId == null || "NONE".equals(verifiedFixId)) return null;
        switch (verifiedFixId) {
            case "ROUTE_4G_CELLULAR":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Route Session via 4G Mobile Data",
                        "تطبيق الحل: تمرير الجلسة عبر بيانات الهاتف 4G",
                        "جێبەجێکردنی چارەسەر: پەیوەندیکردن لە ڕێگەی داتای 4G");
            case "REFRESH_DNS_OR_4G":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Flush DNS Cache & Route via 4G",
                        "تطبيق الحل: تحديث ذاكرة DNS والتمرير عبر 4G",
                        "جێبەجێکردنی چارەسەر: نوێکردنەوەی DNS و پەیوەندی 4G");
            case "OPEN_NETWORK_SETTINGS":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Open Device Internet Settings",
                        "تطبيق الحل: فتح إعدادات الإنترنت في الجهاز",
                        "جێبەجێکردنی چارەسەر: کردنەوەی ڕێکخستنەکانی ئینتەرنێت");
            case "LAUNCH_WITH_CACHED_JARS":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Launch Using Verified Local Packages",
                        "تطبيق الحل: التشغيل باستخدام الحزم المحلية الموثقة",
                        "جێبەجێکردنی چارەسەر: کارپێکردن بە گورزە ناوخۆییەکان");
            case "CLEAN_LOCKS_AND_RECONNECT":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Clean Session Locks & Reconnect",
                        "تطبيق الحل: تنظيف أقفال الجلسة وإعادة الاتصال",
                        "جێبەجێکردنی چارەسەر: پاککردنەوەی قفڵەکان و پەیوەندیکردنەوە");
            case "REFRESH_DNS_AND_RETRY":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Refresh Server Route & Reconnect",
                        "تطبيق الحل: تحديث مسار الخادم وإعادة الاتصال",
                        "جێبەجێکردنی چارەسەر: نوێکردنەوەی ڕێڕەوی ڕاژە و هەوڵدانەوە");
            case "PURGE_SOCLASS_CACHE":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Rebuild Local Customs Cache (.SOClassCache)",
                        "تطبيق الحل: إعادة بناء ذاكرة الجداول المحلية (.SOClassCache)",
                        "جێبەجێکردنی چارەسەر: دروستکردنەوەی بیرگەی (.SOClassCache)");
            case "VERIFY_AND_RESYNC_JARS":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Verify & Re-Download Official Packages",
                        "تطبيق الحل: فحص وإعادة تحميل الحزم الرسمية",
                        "جێبەجێکردنی چارەسەر: پشکنین و داگرتنەوەی گورزە فەرمییەکان");
            case "REPAIR_JAVA_RUNTIME":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Repair & Re-Extract Java Runtime",
                        "تطبيق الحل: إصلاح وإعادة استخراج بيئة جافا",
                        "جێبەجێکردنی چارەسەر: چاککردنەوە و دەرهێنانەوەی ژینگەی جاڤا");
            case "REINIT_X11_DISPLAY":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Reset Display Server (:0) & Restart",
                        "تطبيق الحل: إعادة تهيئة نافذة العرض (:0) وإعادة التشغيل",
                        "جێبەجێکردنی چارەسەر: ڕێکخستنەوەی پەنجەرەی پیشاندان و دەستپێکردنەوە");
            case "CLEAR_TEMP_AND_LOCKS":
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Clear Temporary Cache & Restart Session",
                        "تطبيق الحل: مسح الملفات المؤقتة وإعادة تشغيل الجلسة",
                        "جێبەجێکردنی چارەسەر: سڕینەوەی پەڕگە کاتییەکان و دەستپێکردنەوە");
            case "OPEN_APP_UPDATE_CENTER":
                String verLabel = (fixedInVersion != null && !fixedInVersion.isEmpty()) ? (" (" + fixedInVersion + ")") : "";
                return AsycudaMaterialUi.tr(activity,
                        "Apply Fix: Open Update Center" + verLabel,
                        "تطبيق الحل: فتح مركز تحديث التطبيق" + verLabel,
                        "جێبەجێکردنی چارەسەر: کردنەوەی ناوەندی نوێکردنەوە" + verLabel);
            default:
                return null;
        }
    }

    private Runnable createVerifiedFixAction(
            final MainActivity activity,
            final String verifiedFixId,
            final Runnable onRetry) {
        if (activity == null || verifiedFixId == null || "NONE".equals(verifiedFixId)) return null;
        return () -> {
            switch (verifiedFixId) {
                case "OPEN_NETWORK_SETTINGS":
                    try {
                        Intent netIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        netIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(netIntent);
                    } catch (Exception e) {
                        try {
                            activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        } catch (Exception ignored) {
                            // Ignore settings launch error
                        }
                    }
                    break;

                case "ROUTE_4G_CELLULAR":
                case "REFRESH_DNS_OR_4G":
                    hideSplashButton();
                    updateStatus(AsycudaMaterialUi.tr(activity,
                            "Activating user-approved 4G Mobile Data route for ASYCUDA...",
                            "جاري تفعيل مسار بيانات الهاتف (4G) المعتمد لنظام أسيكودا...",
                            "چالاککردنی ڕێڕەوی داتای مۆبایل (4G) بۆ ئەسیکۆدا..."));
                    Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Activating user-approved 4G Mobile Data route for ASYCUDA...",
                            "جاري تفعيل مسار بيانات الهاتف (4G) المعتمد لنظام أسيكودا...",
                            "چالاککردنی ڕێڕەوی داتای مۆبایل (4G) بۆ ئەسیکۆدا..."), Toast.LENGTH_LONG).show();
                    new Thread(() -> {
                        sUserConsented4gBypass = true;
                        evaluateMofRouteAndCellularBypass(activity.getApplicationContext(), activity.getFilesDir());
                        refreshGlibcHostsCache(activity.getFilesDir());
                        if (onRetry != null) {
                            UI.post(onRetry);
                        } else {
                            UI.post(() -> restartAsycudaSession(activity));
                        }
                    }, "Asycuda-VerifiedFix-4G").start();
                    break;

                case "LAUNCH_WITH_CACHED_JARS":
                    new Thread(() -> {
                        File filesDir = activity.getFilesDir();
                        File clientDir = new File(filesDir, DIR_CLIENT);
                        verifyAndPurgeCachedJars(clientDir);
                        File clientJar = new File(clientDir, PREFIX_JARS + "client.jar");
                        if (clientJar.isFile() && clientJar.length() > 0) {
                            UI.post(() -> restartAsycudaSession(activity));
                        } else if (onRetry != null) {
                            UI.post(onRetry);
                        }
                    }, "Asycuda-VerifiedFix-CachedLaunch").start();
                    break;

                case "PURGE_SOCLASS_CACHE":
                    Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Rebuilding local .SOClassCache and restarting session...",
                            "جاري مسح وإعادة بناء ذاكرة .SOClassCache وإعادة التشغيل...",
                            "دروستکردنەوەی بیرگەی .SOClassCache و دەستپێکردنەوە..."), Toast.LENGTH_LONG).show();
                    new Thread(() -> {
                        File filesDir = activity.getFilesDir();
                        deleteRecursive(new File(filesDir, "home/.SOClassCache"));
                        deleteRecursive(new File(filesDir, DIR_CLIENT + "/tmp"));
                        UI.post(() -> restartAsycudaSession(activity));
                    }, "Asycuda-VerifiedFix-SOClassCache").start();
                    break;

                case "VERIFY_AND_RESYNC_JARS":
                    Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Verifying package signatures and syncing from asy.mof.gov.iq...",
                            "جاري فحص التواقيع الرقمية للحزم والمزامنة مع الخادم الرسمي...",
                            "پشکنینی گورزەکان و هاوکاتکردن لەگەڵ ڕاژەی فەرمی..."), Toast.LENGTH_LONG).show();
                    new Thread(() -> {
                        File clientDir = new File(activity.getFilesDir(), DIR_CLIENT);
                        verifyAndPurgeCachedJars(clientDir);
                        syncGovernmentJars(clientDir, true, null);
                        UI.post(() -> restartAsycudaSession(activity));
                    }, "Asycuda-VerifiedFix-ResyncJars").start();
                    break;

                case "REPAIR_JAVA_RUNTIME":
                    Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Repairing and re-extracting Java 8 runtime environment...",
                            "جاري إصلاح وإعادة استخراج بيئة تشغيل جافا 8...",
                            "چاککردنەوە و دەرهێنانەوەی ژینگەی کارپێکردنی جاڤا 8..."), Toast.LENGTH_LONG).show();
                    new Thread(() -> {
                        File filesDir = activity.getFilesDir();
                        deleteQuietly(new File(filesDir, ".extracted_v3_2"));
                        deleteQuietly(new File(filesDir, ".bootpatch_v4_1_0_r9"));
                        UI.post(() -> restartAsycudaSession(activity));
                    }, "Asycuda-VerifiedFix-RepairRuntime").start();
                    break;

                case "REINIT_X11_DISPLAY":
                    Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                            "Resetting graphical display server (:0) and restarting...",
                            "جاري إعادة تهيئة نافذة العرض (:0) وإعادة التشغيل...",
                            "ڕێکخستنەوەی پەنجەرەی پیشاندان (:0) و دەستپێکردنەوە..."), Toast.LENGTH_SHORT).show();
                    new Thread(() -> {
                        File filesDir = activity.getFilesDir();
                        File usrTmp = new File(filesDir, DIR_USR_TMP);
                        deleteQuietly(new File(usrTmp, ".X0-lock"));
                        deleteQuietly(new File(usrTmp, ".X11-unix/X0"));
                        sCmdEntry = null;
                        try {
                            ensureX11ServerSocketReady(activity, filesDir);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        UI.post(() -> restartAsycudaSession(activity));
                    }, "Asycuda-VerifiedFix-ResetX11").start();
                    break;

                case "OPEN_APP_UPDATE_CENTER":
                    runUnifiedUpdateCheckModal(activity, true);
                    break;

                case "CLEAN_LOCKS_AND_RECONNECT":
                case "REFRESH_DNS_AND_RETRY":
                case "CLEAR_TEMP_AND_LOCKS":
                default:
                    new Thread(() -> {
                        File filesDir = activity.getFilesDir();
                        File clientDir = new File(filesDir, DIR_CLIENT);
                        deleteRecursive(new File(clientDir, "tmp"));
                        File[] locks = clientDir.listFiles();
                        if (locks != null) {
                            for (File lf : locks) {
                                if (lf.getName().endsWith(".lck")) deleteQuietly(lf);
                            }
                        }
                        refreshGlibcHostsCache(filesDir);
                        if (onRetry != null) {
                            UI.post(onRetry);
                        } else {
                            UI.post(() -> restartAsycudaSession(activity));
                        }
                    }, "Asycuda-VerifiedFix-CleanRestart").start();
                    break;
            }
        };
    }

    /**
     * Proposal B + Sentry Known-Issues Catalog Engine.
     * Detects, logs (with exact Java file/method/line number), and explains in clear human language
     * all known network, system, and security issues from the local/synced catalog, provides a
     * user-consented Verified Fix button, and highlights brand-new uncatalogued errors with a
     * prompt to share to Sentry.
     */
    private void diagnoseAndShowUniversalNetworkModal(
            final MainActivity activity,
            final String stage,
            final Throwable caughtException,
            final String rawContextLog,
            final Runnable onRetry) {
        if (activity == null || activity.isFinishing()) return;
        final String callerCodeLine = captureCodeLineLocation(caughtException);
        new Thread(() -> {
            try {
                File filesDir = activity.getFilesDir();
                File clientDir = new File(filesDir, DIR_CLIENT);
                File jnlpFile = new File(clientDir, FILE_AWIQ_JNLP);
                int rmiPort = extractDynamicMofRmiPort(jnlpFile);
                int probeRmiPort = rmiPort > 0 ? rmiPort : 443;

                android.net.ConnectivityManager cm = (android.net.ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.Network activeNet = (cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? cm.getActiveNetwork() : null;
                android.net.NetworkCapabilities caps = (cm != null && activeNet != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        ? cm.getNetworkCapabilities(activeNet) : null;

                boolean hasInternetCap = caps != null && caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET);
                boolean isWifi = isWifiTransportActive(cm, caps);
                boolean isCellular = !isWifi && caps != null && caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR);
                boolean isVpn = caps != null && caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN);

                String transportName = "NONE (Offline)";
                if (isWifi) transportName = isVpn ? "Wi-Fi + VPN" : "Wi-Fi (TRANSPORT_WIFI)";
                else if (isCellular) transportName = isVpn ? "4G/5G Mobile Data + VPN" : "4G/5G Mobile Data (TRANSPORT_CELLULAR)";
                else if (isVpn) transportName = "VPN Tunnel (TRANSPORT_VPN)";
                else if (activeNet != null) transportName = "Active Network";

                // 1. DNS Resolution Probe
                String resolvedIp = null;
                String dnsError = null;
                long dnsStart = System.currentTimeMillis();
                try {
                    InetAddress addr = activeNet != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            ? activeNet.getByName(ALLOWED_MOF_HOST)
                            : InetAddress.getByName(ALLOWED_MOF_HOST);
                    if (addr != null) resolvedIp = addr.getHostAddress();
                } catch (Exception dnsEx) {
                    dnsError = dnsEx.getClass().getSimpleName() + ": " + dnsEx.getMessage();
                }
                long dnsMs = System.currentTimeMillis() - dnsStart;

                // 2. TCP Port :443 (HTTPS JNLP/JAR Server) Probe
                boolean port443Ok = false;
                String port443Err = null;
                long port443Start = System.currentTimeMillis();
                if (resolvedIp != null) {
                    try (Socket s443 = (activeNet != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            ? activeNet.getSocketFactory().createSocket() : new Socket()) {
                        s443.connect(new java.net.InetSocketAddress(InetAddress.getByName(resolvedIp), 443), 2600);
                        port443Ok = true;
                    } catch (Exception e443) {
                        port443Err = e443.getClass().getSimpleName() + ": " + e443.getMessage();
                    }
                }
                long port443Ms = System.currentTimeMillis() - port443Start;

                // 3. TCP Port (ASYCUDA RMI Application Server) Probe
                boolean portRmiOk = false;
                String portRmiErr = null;
                long portRmiStart = System.currentTimeMillis();
                if (resolvedIp != null) {
                    try (Socket sRmi = (activeNet != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            ? activeNet.getSocketFactory().createSocket() : new Socket()) {
                        sRmi.connect(new java.net.InetSocketAddress(InetAddress.getByName(resolvedIp), probeRmiPort), 2600);
                        portRmiOk = true;
                    } catch (Exception eRmi) {
                        portRmiErr = eRmi.getClass().getSimpleName() + ": " + eRmi.getMessage();
                    }
                }
                long portRmiMs = System.currentTimeMillis() - portRmiStart;

                // 4. Check 4G Cellular Multipath Bypass status
                boolean bypassActive = sUserConsented4gBypass && sCellularBypassNetwork != null
                        && sCellularRmiRelaySocket != null && !sCellularRmiRelaySocket.isClosed();

                String excText = caughtException != null
                        ? (caughtException.getClass().getName() + ": " + caughtException.getMessage()) : "";
                String combinedUpper = ((rawContextLog != null ? rawContextLog : "") + "\n" + excText).toUpperCase(Locale.US);
                boolean isDesktopMainExit12 = combinedUpper.contains("EXIT CODE = 12") || combinedUpper.contains("SERVER NOT FOUND");

                String primaryCodeLocation = callerCodeLine;
                if (isDesktopMainExit12) {
                    primaryCodeLocation = "so.kernel.client.DesktopMain.connection(DesktopMain.java:235 -> 353) [ClientTransportLayer.establishSignallingChannel(\""
                            + ALLOWED_MOF_HOST + "\", " + probeRmiPort + ")] | Caught by " + callerCodeLine;
                }

                // Load Known-Issues Catalog (Synced GitHub DB + Bundled APK Fallback)
                org.json.JSONArray catalogEntries = loadKnownErrorsCatalogEntries(activity);
                JSONObject matchedEntry = null;

                // Priority 1: Match specific System / Security / Runtime exceptions in the log or Throwable first
                if (!isDesktopMainExit12 && !"initial_package_download".equals(stage) && !stage.startsWith("manual_")) {
                    matchedEntry = findCatalogEntryByKeywords(catalogEntries, combinedUpper);
                }

                // Priority 2: Match live network telemetry conditions if not already matched to a system/runtime issue
                if (matchedEntry == null) {
                    if (activeNet == null || !hasInternetCap) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-001");
                    } else if (resolvedIp == null) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-002");
                    } else if ((caughtException instanceof javax.net.ssl.SSLException)
                            || combinedUpper.contains("SSLHANDSHAKE") || combinedUpper.contains("CERTPATHVALIDATOR")) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-005");
                    } else if ((caughtException instanceof SecurityException)
                            || combinedUpper.contains("BLOCKED OUTBOUND") || combinedUpper.contains("SECURITYEXCEPTION")) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-SEC-001");
                    } else if (!port443Ok && portRmiOk && "initial_package_download".equals(stage)) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-004");
                    } else if (isWifi && (!portRmiOk || isDesktopMainExit12)) {
                        matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-003");
                    } else if (!portRmiOk || isDesktopMainExit12 || "initial_package_download".equals(stage) || stage.startsWith("manual_")) {
                        matchedEntry = findCatalogEntryByKeywords(catalogEntries, combinedUpper);
                        if (matchedEntry == null) {
                            matchedEntry = findCatalogEntryById(catalogEntries, "SENTRY-NET-007");
                        }
                    }
                }

                boolean isNewUncataloguedError = (matchedEntry == null);
                String errorCodeBadge;
                String humanTitle;
                String humanExplanation;
                String verifiedFixId;
                String fixedInVersion = "";

                if (matchedEntry != null) {
                    errorCodeBadge = matchedEntry.optString("badge", matchedEntry.optString("id", "SENTRY-KNOWN"));
                    humanTitle = AsycudaMaterialUi.tr(activity,
                            matchedEntry.optString("titleEn", "Known Issue Detected"),
                            matchedEntry.optString("titleAr", "تم رصد مشكلة معروفة"),
                            matchedEntry.optString("titleKu", "کێشەیەکی ناسراو دۆزرایەوە"));
                    humanExplanation = AsycudaMaterialUi.tr(activity,
                            matchedEntry.optString("explanationEn", ""),
                            matchedEntry.optString("explanationAr", ""),
                            matchedEntry.optString("explanationKu", ""));
                    verifiedFixId = matchedEntry.optString("verifiedFixId", "NONE");
                    fixedInVersion = matchedEntry.optString("fixedInVersion", "");
                    String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                    if (!fixedInVersion.isEmpty() && isRemoteVersionNewer(fixedInVersion, currentVer)) {
                        verifiedFixId = "OPEN_APP_UPDATE_CENTER";
                    }
                } else {
                    // Brand-New / Uncatalogued Error -> Extract generic human explanation & highlight Sentry share
                    errorCodeBadge = "SENTRY-NEW-000 • UNCATALOGUED_RUNTIME_ERROR";
                    String simpleErrName = caughtException != null
                            ? caughtException.getClass().getSimpleName()
                            : extractFirstExceptionNameFromLog(rawContextLog, stage);
                    String rawErrMsg = caughtException != null && caughtException.getMessage() != null
                            ? caughtException.getMessage()
                            : extractFirstErrorLineFromLog(rawContextLog);
                    String simpleErrMsg = redactSensitiveLogContent(rawErrMsg);
                    humanTitle = AsycudaMaterialUi.tr(activity,
                            "New Uncatalogued Error (" + simpleErrName + ")",
                            "خطأ جديد غير مسجل (" + simpleErrName + ")",
                            "هەڵەیەکی نوێی تۆمارنەکراو (" + simpleErrName + ")");
                    humanExplanation = AsycudaMaterialUi.tr(activity,
                            "An unexpected error occurred that is not yet in the Known Issues Catalog:\n\n"
                            + "• Detected Type: " + simpleErrName + "\n"
                            + "• Details: " + simpleErrMsg + "\n"
                            + "• Code Location: " + primaryCodeLocation + "\n"
                            + "• Network Status: " + transportName + " (Server Port :" + probeRmiPort + " " + (portRmiOk ? "Online" : "Unreachable") + ")\n\n"
                            + "What you can do right now:\n"
                            + "1. Tap 'Share New Error to Sentry' below so our engineering team can identify this error and publish a direct fix to the catalog.\n"
                            + "2. Or tap 'Apply Fix: Clear Temporary Cache & Restart Session' to recover immediately.",
                            "حدث خطأ غير متوقع وغير مدرج بعد في قاعدة بيانات الأعطال المعروفة:\n\n"
                            + "• نوع الخطأ المرصود: " + simpleErrName + "\n"
                            + "• التفاصيل المتاحة: " + simpleErrMsg + "\n"
                            + "• موقع الكود البرمجي: " + primaryCodeLocation + "\n"
                            + "• حالة الشبكة: " + transportName + " (منفذ الخادم :" + probeRmiPort + " " + (portRmiOk ? "متصل" : "غير مستجيب") + ")\n\n"
                            + "الخطوات المقترحة الآن:\n"
                            + "1. اضغط على زر (مشاركة الخطأ الجديد إلى Sentry) بالأسفل ليتم تسجيل هذا الخطأ في قاعدة البيانات وإضافة حل مباشر له.\n"
                            + "2. أو اضغط على زر (مسح الملفات المؤقتة وإعادة تشغيل الجلسة) للاستئناف فوراً.",
                            "هەڵەیەکی چاوەڕواننەکراو ڕوویدا کە هێشتا لە بنکەی زانیاری تۆمار نەکراوە:\n\n"
                            + "• جۆری هەڵە: " + simpleErrName + "\n"
                            + "• وردەکاری: " + simpleErrMsg + "\n"
                            + "• شوێنی کۆد: " + primaryCodeLocation + "\n\n"
                            + "تکایە دوگمەی (هاوبەشکردنی هەڵەی نوێ بۆ Sentry) لە خوارەوە دابگرە تا چارەسەری بۆ زیاد بکرێت.");
                    verifiedFixId = "CLEAR_TEMP_AND_LOCKS";
                }

                StringBuilder tech = new StringBuilder();
                tech.append("=== ASYCUDA UNIVERSAL DIAGNOSTIC & SENTRY CATALOG LOG (IN-RAM REDACTED) ===\n");
                tech.append("PrivacyPolicy    : STRICT_RAM_ONLY (Credentials, usernames, passwords, TINs, tokens, and server IPs scrubbed; zero disk writes)\n");
                tech.append("Timestamp        : ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(new Date())).append("\n");
                tech.append("Diagnostic Stage : ").append(stage).append("\n");
                tech.append("Catalog Status   : ").append(isNewUncataloguedError ? "NEW_UNCATALOGUED_ERROR (Suggest Sentry Share)" : "KNOWN_CATALOG_MATCH").append("\n");
                tech.append("Error Code       : ").append(errorCodeBadge).append("\n");
                tech.append("Verified Fix ID  : ").append(verifiedFixId).append("\n");
                tech.append("Code Line Source : ").append(primaryCodeLocation).append("\n");
                tech.append("Active Transport : ").append(transportName).append("\n");
                tech.append("DNS Lookup       : ").append(ALLOWED_MOF_HOST).append(" -> ")
                        .append(resolvedIp != null ? "RESOLVED_OK [IP_REDACTED]" : "FAILED (" + redactSensitiveLogContent(dnsError) + ")")
                        .append(" (").append(dnsMs).append(" ms)\n");
                tech.append("HTTPS Web (:443) : ").append(port443Ok ? "ONLINE" : "FAILED (" + redactSensitiveLogContent(port443Err) + ")")
                        .append(" (").append(port443Ms).append(" ms)\n");
                tech.append("RMI Port (:").append(probeRmiPort).append(") : ").append(portRmiOk ? "ONLINE" : "FAILED (" + redactSensitiveLogContent(portRmiErr) + ")")
                        .append(" (").append(portRmiMs).append(" ms)\n");
                tech.append("4G User Relay    : ").append(bypassActive ? "USER_ENABLED on 127.0.0.1:" + sCellularRmiRelayPort : "OPT_IN_INACTIVE (Default)")
                        .append("\n");

                File hostsFile = new File(filesDir, "usr/glibc/etc/hosts");
                if (hostsFile.exists()) {
                    String hostsTail = readTail(hostsFile, 256);
                    boolean usesLoopbackRelay = hostsTail.contains("127.0.0.1 asy.mof.gov.iq");
                    boolean hasMofEntry = hostsTail.contains("asy.mof.gov.iq");
                    String safeHostsState;
                    if (usesLoopbackRelay) {
                        safeHostsState = "127.0.0.1 asy.mof.gov.iq (Local 4G Loopback Relay)";
                    } else if (hasMofEntry) {
                        safeHostsState = "[IP_REDACTED] asy.mof.gov.iq (Direct Host Mapping)";
                    } else {
                        safeHostsState = "localhost only";
                    }
                    tech.append("glibc /etc/hosts : ").append(safeHostsState).append("\n");
                }
                if (caughtException != null) {
                    tech.append("\n--- JAVA EXCEPTION STACK TRACE (REDACTED) ---\n");
                    tech.append(caughtException.getClass().getName()).append(": ")
                            .append(redactSensitiveLogContent(caughtException.getMessage())).append("\n");
                    StackTraceElement[] st = caughtException.getStackTrace();
                    int limit = Math.min(st != null ? st.length : 0, 8);
                    for (int i = 0; i < limit; i++) {
                        tech.append("  at ").append(redactSensitiveLogContent(st[i].toString())).append("\n");
                    }
                }
                if (rawContextLog != null && !rawContextLog.trim().isEmpty()) {
                    tech.append("\n--- SESSION / ASYCUDA LOG TAIL (REDACTED) ---\n");
                    tech.append(redactSensitiveLogContent(rawContextLog.trim())).append("\n");
                }

                appendNetworkDiagnosticLogEntry(clientDir, "diagnoseAndShowUniversalNetworkModal[" + stage + "]",
                        caughtException, errorCodeBadge + " | " + primaryCodeLocation);

                final String fTitle = humanTitle;
                final String fBadge = errorCodeBadge;
                final String fExplain = humanExplanation;
                final StringBuilder fTechRamBuffer = new StringBuilder(redactSensitiveLogContent(tech.toString()));
                for (int i = 0; i < tech.length(); i++) {
                    tech.setCharAt(i, '\0');
                }
                tech.setLength(0);
                final boolean fIsNew = isNewUncataloguedError;
                final String fFixBtnLabel = resolveVerifiedFixButtonLabel(activity, verifiedFixId, fixedInVersion);
                final Runnable fFixAction = createVerifiedFixAction(activity, verifiedFixId, onRetry);
                final String fSummaryContext = redactSensitiveLogContent(errorCodeBadge + " | " + primaryCodeLocation);

                UI.post(() -> {
                    if (!activity.isFinishing()) {
                        AsycudaMaterialUi.showUniversalNetworkDiagnosticModal(
                                activity,
                                fTitle,
                                fBadge,
                                fExplain,
                                fTechRamBuffer,
                                fIsNew,
                                fFixBtnLabel,
                                fFixAction,
                                () -> AsycudaMaterialUi.showDiagnosticDataPreviewModal(
                                        activity,
                                        buildDiagnosticPreviewBufferInRam(activity, fSummaryContext)),
                                () -> {
                                    new Thread(() -> {
                                        refreshGlibcHostsCache(activity.getFilesDir());
                                        if (onRetry != null) {
                                            UI.post(onRetry);
                                        } else {
                                            UI.post(() -> restartAsycudaSession(activity));
                                        }
                                    }, "Asycuda-DiagRetry").start();
                                },
                                () -> openFeedbackAndBugReportModal(activity));
                    }
                });
            } catch (Exception ignored) {
                // Best-effort diagnostic modal
            }
        }, "Asycuda-UniversalNetDiag").start();
    }

    private static String extractFirstExceptionNameFromLog(String rawLog, String fallbackStage) {
        if (rawLog != null && !rawLog.isEmpty()) {
            for (String line : rawLog.split("\n")) {
                String t = line.trim();
                if (t.contains("Exception") || t.contains("Error")) {
                    int colon = t.indexOf(':');
                    String head = colon > 0 ? t.substring(0, colon).trim() : t;
                    int lastDot = head.lastIndexOf('.');
                    String candidate = lastDot >= 0 ? head.substring(lastDot + 1) : head;
                    if (candidate.length() > 2 && candidate.length() <= 64) {
                        return candidate;
                    }
                }
            }
        }
        return fallbackStage != null ? fallbackStage : "RuntimeError";
    }

    private static String extractFirstErrorLineFromLog(String rawLog) {
        if (rawLog != null && !rawLog.isEmpty()) {
            String[] lines = rawLog.split("\n");
            for (int i = lines.length - 1; i >= 0; i--) {
                String t = lines[i].trim();
                if (!t.isEmpty() && !t.startsWith("at ")) {
                    return t.length() > 180 ? t.substring(0, 180) + "..." : t;
                }
            }
        }
        return "See developer technical log below for full trace.";
    }

    private static void patchLibJliForTargetSdk34(File jreDir) {
        if (jreDir == null || !jreDir.exists()) return;
        String[] candidates = {
            "jre/lib/aarch64/jli/libjli.so",
            "lib/aarch64/jli/libjli.so",
            "lib/jli/libjli.so",
            "lib/libjli.so"
        };
        byte[] needle = "/proc/self/exe".getBytes(StandardCharsets.US_ASCII);
        byte[] replacement = "/proc/self/exX".getBytes(StandardCharsets.US_ASCII);
        for (String rel : candidates) {
            File f = new File(jreDir, rel);
            if (!f.exists() || !f.isFile()) continue;
            try {
                byte[] data = readFileBytes(f);
                boolean modified = false;
                for (int i = 0; i <= data.length - needle.length; i++) {
                    boolean match = true;
                    for (int j = 0; j < needle.length; j++) {
                        if (data[i + j] != needle[j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        System.arraycopy(replacement, 0, data, i, replacement.length);
                        modified = true;
                    }
                }
                if (modified) {
                    setWritableOwnerOnly(f);
                    try (FileOutputStream fos = new FileOutputStream(f)) {
                        fos.write(data);
                        fos.getFD().sync();
                    }
                    setExecutableOwnerOnly(f);
                }
            } catch (Exception ignored) {
                // Best-effort binary patch for targetSdk 34 loader
            }
        }
    }

    private static void setWritableOwnerOnly(File f) {
        if (f != null && f.exists() && !f.setWritable(true, true)) {
            // Ignore if filesystem does not support POSIX permissions
        }
    }

    private static String readTail(File f, int maxChars) {
        if (f == null || !f.exists()) return "No log output";
        try (FileInputStream fis = new FileInputStream(f)) {
            long fileLen = f.length();
            int len = (int) Math.min(fileLen, maxChars);
            if (len <= 0) return "Empty log";
            long toSkip = Math.max(0L, fileLen - len);
            while (toSkip > 0) {
                long skipped = fis.skip(toSkip);
                if (skipped <= 0) break;
                toSkip -= skipped;
            }
            byte[] b = new byte[len];
            int totalRead = 0;
            while (totalRead < len) {
                int r = fis.read(b, totalRead, len - totalRead);
                if (r < 0) break;
                totalRead += r;
            }
            return new String(b, 0, totalRead, StandardCharsets.UTF_8).trim();
        } catch (Exception t) {
            return t.getMessage();
        }
    }

    private void extractTarXzAsset(Context ctx, String assetName, File destDir) throws IOException {
        String destCanonPrefix = destDir.getCanonicalPath() + File.separator;
        boolean isRuntimeArchive = "runtime-arm64.tar.xz".equals(assetName);
        int extractedCount = 0;
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            while ((entry = tis.getNextTarEntry()) != null) {
                extractedCount++;
                if (isRuntimeArchive && extractedCount % 250 == 0) {
                    final int countSnap = extractedCount;
                    updateStatus(AsycudaMaterialUi.tr(ctx,
                            "Setting up the Java environment (1/2) — " + countSnap + " files...",
                            "جاري تجهيز بيئة التشغيل جافا (1/2) — " + countSnap + " ملف...",
                            "ئامادەکردنی ژینگەی جاڤا (1/2) — " + countSnap + " پەڕگە..."));
                }
                File outFile = new File(destDir, entry.getName());
                String outCanon = outFile.getCanonicalPath();
                if (!outCanon.equals(destDir.getCanonicalPath()) && !outCanon.startsWith(destCanonPrefix)) {
                    throw new SecurityException("Blocked archive entry outside target directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    ensureDir(outFile);
                } else if (entry.isSymbolicLink()) {
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    deleteQuietly(outFile);
                    String linkTarget = entry.getLinkName();
                    if (linkTarget != null && linkTarget.startsWith("/system/fonts/")) {
                        // Device-specific /system/fonts/*.ttf symlinks are linked dynamically after extraction
                        continue;
                    }
                    if (!AsycudaSecurityValidator.isSafeTarSymlinkTarget(destDir, outFile, linkTarget)) {
                        throw new SecurityException("Blocked archive symlink pointing outside target directory: " + entry.getName());
                    }
                    try {
                        Os.symlink(linkTarget, outFile.getAbsolutePath());
                    } catch (Exception ignored) {
                        // Ignore symlink error if already linked
                    }
                } else if (entry.isLink()) {
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    deleteQuietly(outFile);
                    File target = new File(destDir, entry.getLinkName());
                    if (!AsycudaSecurityValidator.isSafeTarEntryPath(destDir, target)) {
                        throw new SecurityException("Blocked archive hardlink pointing outside target directory: " + entry.getName());
                    }
                    if (target.exists()) {
                        try (FileInputStream in = new FileInputStream(target);
                             FileOutputStream out = new FileOutputStream(outFile)) {
                            int n;
                            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                            out.getFD().sync();
                        }
                        setExecutableOwnerOnly(outFile);
                    }
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    if (outFile.exists()) deleteQuietly(outFile);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int n;
                        while ((n = tis.read(buf)) != -1) {
                            fos.write(buf, 0, n);
                        }
                        fos.getFD().sync();
                    }
                    setReadableOwnerOnly(outFile);
                    int mode = entry.getMode();
                    if ((mode & MASK_EXEC_BITS) != 0 || outFile.getName().contains(".so") ||
                            outFile.getAbsolutePath().contains("/bin/")) {
                        setExecutableOwnerOnly(outFile);
                    }
                }
            }
        }
    }

    private void startBridgeListener() {
        if (sServerStarted) return;
        sServerStarted = true;
        Thread t = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(47891, 16, InetAddress.getByName("127.0.0.1"))) {
                while (!Thread.currentThread().isInterrupted()) {
                    try (Socket s = ss.accept();
                         BufferedReader br = new BufferedReader(
                                 new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))) {
                        if (!isLoopbackPeerOwnedByOurUid(s)) {
                            continue;
                        }
                        String line = br.readLine();
                        if (line != null) handleBridgeMessage(line.trim());
                    } catch (Exception ignored) {
                        // Ignore malformed socket message
                    }
                }
            } catch (Exception ignored) {
                sServerStarted = false;
            }
        }, "Asycuda-BridgeListener");
        t.setDaemon(true);
        t.start();
    }

    private static boolean isLoopbackPeerOwnedByOurUid(Socket s) {
        if (s == null || s.getInetAddress() == null || !s.getInetAddress().isLoopbackAddress()) {
            return false;
        }
        int peerPort = s.getPort();
        int myUid = android.os.Process.myUid();
        String hexIpv4 = String.format(Locale.US, "0100007F:%04X", peerPort);
        String hexIpv6 = String.format(Locale.US, "0000000000000000FFFF00000100007F:%04X", peerPort);
        String hexIpv6Loop = String.format(Locale.US, "00000000000000000000000001000000:%04X", peerPort);
        boolean foundAnyEntry = false;
        String[] procFiles = { "/proc/net/tcp", "/proc/net/tcp6" };
        for (String path : procFiles) {
            File procTcp = new File(path);
            if (!procTcp.exists() || !procTcp.canRead()) {
                continue;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(procTcp))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("sl")) continue;
                    foundAnyEntry = true;
                    String[] cols = trimmed.split("\\s+");
                    if (cols.length >= 8 && (hexIpv4.equalsIgnoreCase(cols[1])
                            || hexIpv6.equalsIgnoreCase(cols[1])
                            || hexIpv6Loop.equalsIgnoreCase(cols[1]))) {
                        return String.valueOf(myUid).equals(cols[7]);
                    }
                }
            } catch (Exception ignored) {
                // Continue checking remaining proc files
            }
        }
        // Only fall back to the 128-bit session token when Android 10+ SELinux hides all /proc/net/tcp rows
        return !foundAnyEntry;
    }

    private void handleBridgeMessage(String rawMsg) {
        MainActivity act = sCurrentActivity;
        if (act == null || rawMsg == null) return;

        String authPrefix = "AUTH|" + BRIDGE_AUTH_TOKEN + "|";
        if (rawMsg.length() < authPrefix.length()) {
            return;
        }
        byte[] expectedBytes = authPrefix.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = rawMsg.substring(0, authPrefix.length()).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedBytes, actualBytes)) {
            return;
        }
        String msg = rawMsg.substring(authPrefix.length());

        if (msg.startsWith("IME|SHOW")) {
            if (!AsycudaMaterialUi.isAutoKeyboardEnabled(act)) {
                return;
            }
            UI.post(() -> {
                try {
                    LorieView v = act.getLorieView();
                    if (v != null) {
                        v.requestFocus();
                        v.setKeyboardVisible(true);
                    }
                } catch (Exception ignored) {
                    // Ignore keyboard show error if view is detached
                }
            });
        } else if (msg.startsWith("OPEN|")) {
            String[] parts = msg.split("\\|");
            if (parts.length >= 2) {
                File f = resolveSafeBridgeFile(act, parts[1]);
                String mime = parts.length >= 3 ? parts[2] : MIME_PDF;
                if (f != null) {
                    UI.post(() -> openFileWithProvider(act, f, mime));
                }
            }
        } else if (msg.startsWith("BROWSE|")) {
            String url = msg.substring("BROWSE|".length()).trim();
            if (isAllowedBridgeUrl(url)) {
                UI.post(() -> openUrl(act, url));
            }
        } else if (msg.startsWith("PICK_WORKSPACE_FILE")) {
            UI.post(() -> {
                List<File> vaultFiles = new ArrayList<>();
                collectFiles(new File(act.getFilesDir(), DIR_PRINTS), vaultFiles);
                AsycudaMaterialUi.showFilePickerBridgePromptModal(
                        act,
                        vaultFiles.size(),
                        () -> launchSystemDocumentPicker(act),
                        null);
            });
        } else if (msg.startsWith("SOCLASS_UPDATE|")) {
            UI.post(() -> {
                Toast.makeText(act, AsycudaMaterialUi.tr(act,
                        "ASYCUDA Server requested a SOClass update. Launching Update Center...",
                        "طلب خادم أسيكودا تحديث ملفات النظام (SOClass). جاري فتح نافذة التحديث...",
                        "ڕاژەی ئەسیکۆدا داوای نوێکردنەوەی SOClass دەکات. کردنەوەی پەنجەرەی نوێکردنەوە..."),
                        Toast.LENGTH_LONG).show();
                runUnifiedUpdateCheckModal(act, true);
            });
        } else if (msg.startsWith("BIND_4G_RELAY_PORT|")) {
            if (sUserConsented4gBypass && sCellularBypassNetwork != null) {
                try {
                    int dynamicPort = Integer.parseInt(msg.substring("BIND_4G_RELAY_PORT|".length()).trim());
                    if (dynamicPort > 1024 && dynamicPort <= 65535 && dynamicPort != 47891
                            && (dynamicPort < 6000 || dynamicPort > 6010)
                            && dynamicPort != sCellularHttpsRelayPort) {
                        ensureCellularRmiRelay(dynamicPort);
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore malformed port
                }
            }
        } else if (msg.startsWith("SECURITY_ERROR|")) {
            final String reason = msg.substring("SECURITY_ERROR|".length()).trim();
            UI.post(() -> AsycudaMaterialUi.showSecuritySandboxErrorModal(
                    act,
                    reason,
                    () -> restartAsycudaSession(act),
                    () -> shutdownApp(act)));
        }
    }

    private void launchSystemDocumentPicker(MainActivity act) {
        if (act == null || act.isFinishing()) return;
        try {
            Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pick.setType("*/*");
            pick.addCategory(Intent.CATEGORY_OPENABLE);
            act.startActivityForResult(pick, REQ_PICK_WORKSPACE_FILE);
        } catch (Exception t) {
            try {
                Intent fallback = new Intent(Intent.ACTION_GET_CONTENT);
                fallback.setType("*/*");
                fallback.addCategory(Intent.CATEGORY_OPENABLE);
                act.startActivityForResult(Intent.createChooser(fallback,
                        AsycudaMaterialUi.tr(act,
                                "Select File for ASYCUDA Vault",
                                "اختر ملفاً لإضافته إلى الخزنة المشفرة",
                                "پەڕگەیەک هەڵبژێرە بۆ قاسەی نهێنی")),
                        REQ_PICK_WORKSPACE_FILE);
            } catch (Exception e2) {
                Toast.makeText(act, "Cannot open file picker: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File resolveSafeBridgeFile(Context ctx, String requestedPath) {
        if (requestedPath == null || requestedPath.isEmpty()) return null;
        File[] allowedDirs = {
            new File(ctx.getFilesDir(), DIR_PRINTS),
            new File(ctx.getFilesDir(), DIR_WORKSPACE)
        };
        try {
            File req = new File(requestedPath);
            if (req.isAbsolute()) {
                String reqCanon = req.getCanonicalPath();
                for (File dir : allowedDirs) {
                    String dirCanonPrefix = dir.getCanonicalPath() + File.separator;
                    if (reqCanon.startsWith(dirCanonPrefix) && req.exists() && req.isFile()) {
                        return req;
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall through to sanitized basename check
        }
        String baseName = AsycudaSecurityValidator.sanitizeBridgeFileName(requestedPath);
        if (baseName.isEmpty() || ".".equals(baseName) || "..".equals(baseName)) return null;
        for (File dir : allowedDirs) {
            File candidate = new File(dir, baseName);
            if (candidate.exists() && candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isAllowedBridgeUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        try {
            Uri u = Uri.parse(url.trim());
            String scheme = u.getScheme();
            String host = u.getHost() != null ? u.getHost().toLowerCase(Locale.US) : "";
            String path = u.getPath() != null ? u.getPath().toLowerCase(Locale.US) : "";
            if (SCHEME_HTTPS.equalsIgnoreCase(scheme)) {
                if (ALLOWED_MOF_HOST.equals(host)
                        || "www.asycuda.org".equals(host)
                        || "asycuda.org".equals(host)) {
                    return true;
                }
                if ("github.com".equals(host)) {
                    return path.startsWith("/mohammedalshimmary/asycuda-iraq-android");
                }
            }
            if ("http".equalsIgnoreCase(scheme) || SCHEME_HTTPS.equalsIgnoreCase(scheme)) {
                int port = u.getPort();
                return ("127.0.0.1".equals(host) || "localhost".equals(host))
                        && port > 1024 && port == sCellularHttpsRelayPort;
            }
            return false;
        } catch (Exception t) {
            return false;
        }
    }

    private void openFileWithProvider(Context ctx, File f, String mime) {
        try {
            if (!f.exists()) {
                Toast.makeText(ctx, "File not found: " + f.getName(), Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = FileProvider.getUriForFile(ctx, FILE_PROVIDER_AUTH, f);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(Intent.createChooser(intent, "Open " + f.getName()));
        } catch (Exception t) {
            Toast.makeText(ctx, "Could not open file: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareFileWithProvider(Context ctx, File f, String mime) {
        try {
            Uri uri = FileProvider.getUriForFile(ctx, FILE_PROVIDER_AUTH, f);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(mime);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(Intent.createChooser(share, "Share " + f.getName()));
        } catch (Exception t) {
            Toast.makeText(ctx, "Share error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareMultipleFilesWithProvider(Context ctx, List<File> files) {
        if (files == null || files.isEmpty()) return;
        try {
            ArrayList<Uri> uris = new ArrayList<>();
            for (File f : files) {
                if (f != null && f.exists()) {
                    uris.add(FileProvider.getUriForFile(ctx, FILE_PROVIDER_AUTH, f));
                }
            }
            if (uris.isEmpty()) return;
            Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
            share.setType("*/*");
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(Intent.createChooser(share, AsycudaMaterialUi.tr(ctx,
                    "Share " + uris.size() + " files",
                    "مشاركة " + uris.size() + " ملفات",
                    "هاوبەشکردنی " + uris.size() + " پەڕگە")));
        } catch (Exception t) {
            Toast.makeText(ctx, "Share error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteSelectedFiles(MainActivity activity, List<File> filesToDelete, AsycudaMaterialUi.MenuActionCallback cb) {
        if (filesToDelete == null || filesToDelete.isEmpty()) return;
        int deleted = 0;

        for (File f : filesToDelete) {
            if (f == null || !f.exists()) continue;
            deleteRecursive(f);
            deleted++;
        }

        Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                "Deleted " + Math.max(1, deleted) + " item(s)",
                "تم حذف " + Math.max(1, deleted) + " عنصر",
                Math.max(1, deleted) + " دانە سڕایەوە"), Toast.LENGTH_SHORT).show();

        if (cb != null) {
            cb.onOpenFiles();
        }
    }

    public static void openDisplaySettings(MainActivity activity) {
        if (activity == null) return;
        UI.post(() -> {
            if (sInstance == null) sInstance = new AsycudaStandaloneManager();
            AsycudaMaterialUi.showDisplaySettingsModal(activity, sInstance.createMenuCallback(activity));
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFloatingMenuPill(MainActivity activity, FrameLayout root) {
        if (root.findViewWithTag(FLOATING_PILL_TAG) != null) {
            return;
        }
        LinearLayout pill = new LinearLayout(activity);
        pill.setTag(FLOATING_PILL_TAG);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER_VERTICAL);
        int hp = dp(activity, 11);
        int vp = dp(activity, 5);
        pill.setPadding(hp, vp, hp, vp);
        pill.setAlpha(0.90f);
        pill.setElevation(dp(activity, 10));

        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setColor(Color.parseColor("#E61C1C1E"));
        pillBg.setCornerRadius(dp(activity, 16));
        pillBg.setStroke(Math.max(1, dp(activity, 1)), Color.parseColor("#FF38383A"));
        pill.setBackground(pillBg);

        ImageView dot = new ImageView(activity);
        dot.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(activity, 16), dp(activity, 16));
        dotLp.rightMargin = dp(activity, 6);
        pill.addView(dot, dotLp);

        TextView label = new TextView(activity);
        label.setText("ASYCUDA");
        label.setTextColor(Color.WHITE);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        label.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        label.setLetterSpacing(0.04f);
        pill.addView(label);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.topMargin = dp(activity, 14);
        lp.rightMargin = dp(activity, 16);

        pill.setOnTouchListener(new View.OnTouchListener() {
            float dX;
            float dY;
            float downX;
            float downY;
            boolean dragged;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setAlpha(1.0f);
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        downX = event.getRawX();
                        downY = event.getRawY();
                        dragged = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.hypot(event.getRawX() - downX, event.getRawY() - downY) > dp(activity, 8)) {
                            dragged = true;
                        }
                        if (dragged) {
                            v.setX(event.getRawX() + dX);
                            v.setY(event.getRawY() + dY);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setAlpha(0.90f);
                        if (!dragged) {
                            openMaterialControlCenter(activity);
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        root.addView(pill, lp);
    }

    private AsycudaMaterialUi.MenuActionCallback createMenuCallback(MainActivity activity) {
        return new AsycudaMaterialUi.MenuActionCallback() {
            @Override
            public void onToggleAutoKeyboard(boolean newState) {
                if (!newState && activity.getLorieView() != null) {
                    activity.getLorieView().setKeyboardVisible(false);
                }
            }

            @Override
            public void onToggleAssistKeys(boolean newState) {
                AsycudaMaterialUi.setAssistKeysEnabled(activity, newState);
            }

            @Override
            public void onOpenFiles() {
                AsycudaMaterialUi.showFilesModal(activity, this);
            }

            @Override
            public void onOpenDisplaySettings() {
                AsycudaMaterialUi.showDisplaySettingsModal(activity, this);
            }

            @Override
            public void onCheckUpdates() {
                runUnifiedUpdateCheckModal(activity);
            }

            @Override
            public void onRestartSession() {
                restartAsycudaSession(activity);
            }

            @Override
            public void onOpenFeedbackModal() {
                openFeedbackAndBugReportModal(activity);
            }

            @Override
            public void onOpenAbout() {
                AsycudaMaterialUi.showAboutAndCreditsModal(activity, this);
            }

            @Override
            public void onExitApplication() {
                shutdownApp(activity);
            }

            @Override
            public void onLanguageChanged() {
                FrameLayout root = activity.findViewById(android.R.id.content);
                if (root != null) {
                    View oldPill = root.findViewWithTag(FLOATING_PILL_TAG);
                    if (oldPill != null) root.removeView(oldPill);
                    setupFloatingMenuPill(activity, root);
                }
            }

            @Override
            public void onOpenUrl(String url) {
                openUrl(activity, url);
            }

            @Override
            public void onOpenFile(File f) {
                openFileWithProvider(activity, f, guessMime(f.getName()));
            }

            @Override
            public void onShareFile(File f) {
                shareFileWithProvider(activity, f, guessMime(f.getName()));
            }

            @Override
            public void onShareMultipleFiles(List<File> files) {
                shareMultipleFilesWithProvider(activity, files);
            }

            @Override
            public void onDeleteFiles(List<File> files) {
                deleteSelectedFiles(activity, files, this);
            }

            @Override
            public void onImportFileToVault(File targetDir, Runnable onImported) {
                sVaultImportTargetDir = targetDir;
                sOnVaultImportCallback = onImported;
                launchSystemDocumentPicker(activity);
            }
        };
    }

    private void openMaterialControlCenter(MainActivity activity) {
        AsycudaMaterialUi.showControlCenter(activity, createMenuCallback(activity));
    }

    private void restartAsycudaSession(MainActivity activity) {
        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root != null) {
            setupSplashOverlay(activity, root);
            hideSplashButton();
            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Restarting session...",
                    "جاري إعادة تشغيل الجلسة...",
                    "دووبارە پێکردنەوەی دانیشتن..."));
        }
        new Thread(() -> {
            Process oldProc = sJavaProcess;
            sJavaProcess = null;
            if (oldProc != null) {
                try {
                    oldProc.destroyForcibly();
                    oldProc.waitFor();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                    // Best-effort wait for previous JVM exit
                }
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            File filesDir = activity.getFilesDir();
            File runtimeMarker = new File(filesDir, ".extracted_v3_2");
            File clientDir = new File(filesDir, DIR_CLIENT);
            if (!runtimeMarker.exists() || !areAllManifestJarsPresent(clientDir)) {
                bootstrapEnvironmentAndStart(activity);
            } else {
                launchAsycudaJvm(activity);
            }
        }, "Asycuda-SessionRestart").start();
    }

    private void runUnifiedUpdateCheckModal(MainActivity activity) {
        runUnifiedUpdateCheckModal(activity, false);
    }

    private void runUnifiedUpdateCheckModal(MainActivity activity, boolean forceSessionRestart) {
        if (!sSyncInProgress.compareAndSet(false, true)) {
            Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                    "Update check is already running...",
                    "فحص التحديثات جارٍ بالفعل...",
                    "پشکنینی نوێکردنەوە لە کاردایە..."), Toast.LENGTH_SHORT).show();
            return;
        }

        AsycudaMaterialUi.UpdateCenterController ctrl = AsycudaMaterialUi.showUpdateCenterModal(
                activity, () -> openMaterialControlCenter(activity));

        new Thread(() -> {
            boolean appUpdateAvailable = false;
            try {
                ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                        "Checking GitHub for new app releases...",
                        "جاري البحث عن تحديث للتطبيق...",
                        "گەڕان بۆ وەشانی نوێی بەرنامە..."), 0, true);

                String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                HttpURLConnection ghConn = (HttpURLConnection) new URL(GITHUB_LATEST_RELEASE_API).openConnection();
                ghConn.setConnectTimeout(4500);
                ghConn.setReadTimeout(7000);
                ghConn.setRequestProperty("Accept", "application/vnd.github+json");
                ghConn.setRequestProperty(HEADER_USER_AGENT, "ASYCUDA-Iraq-Android/" + currentVer);

                if (ghConn.getResponseCode() == 200) {
                    String jsonStr;
                    try (InputStream in = ghConn.getInputStream();
                         ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
                        jsonStr = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                    } finally {
                        ghConn.disconnect();
                    }

                    JSONObject rel = new JSONObject(jsonStr);
                    String tagName = rel.optString("tag_name", "");
                    String body = rel.optString("body", "");
                    String apkUrl = null;
                    String apkSha256 = null;
                    long apkSize = 0;

                    JSONArray assets = rel.optJSONArray("assets");
                    if (assets != null) {
                        for (int i = 0; i < assets.length(); i++) {
                            JSONObject a = assets.getJSONObject(i);
                            String name = a.optString("name", "");
                            if (name.endsWith(".apk")) {
                                apkUrl = a.optString("browser_download_url", null);
                                apkSize = a.optLong("size", 0);
                                String digest = a.optString("digest", "");
                                if (digest.startsWith("sha256:")) {
                                    apkSha256 = digest.substring("sha256:".length()).trim();
                                }
                                break;
                            }
                        }
                    }
                    if (!AsycudaSecurityValidator.isValidSha256Hex(apkSha256)) {
                        apkSha256 = AsycudaSecurityValidator.extractSha256FromReleaseBody(body);
                    }

                    if (apkUrl != null && isRemoteVersionNewer(tagName, currentVer)) {
                        appUpdateAvailable = true;
                        final String finalApkUrl = apkUrl;
                        final long finalApkSize = apkSize;
                        final String finalTag = tagName;
                        final String finalSha256 = apkSha256;
                        String shortNotes = cleanReleaseNotesPreview(body);
                        ctrl.showAppUpdateAvailable(tagName, shortNotes, () -> {
                            sApkDownloadActive.set(true);
                            new Thread(() -> downloadAndInstallApkUpdate(activity, finalApkUrl, finalApkSize, finalTag, finalSha256, ctrl),
                                    "Asycuda-ApkDownloader").start();
                        });
                    } else {
                        ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                                "App is up to date (" + currentVer + ")",
                                "التطبيق محدّث (" + currentVer + ")",
                                "بەرنامەکە نوێکراوەتەوە (" + currentVer + ")"), -1, false);
                    }
                } else {
                    ghConn.disconnect();
                    ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                            "Current version: " + currentVer,
                            "الإصدار الحالي: " + currentVer,
                            "وەشانی ئێستا: " + currentVer), -1, false);
                }
            } catch (Exception ignored) {
                String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                        "Current version: " + currentVer,
                        "الإصدار الحالي: " + currentVer,
                        "وەشانی ئێستا: " + currentVer), -1, false);
            }

            try {
                File clientDir = new File(activity.getFilesDir(), DIR_CLIENT);
                int updatedJars = syncGovernmentJars(clientDir, true, ctrl);
                int effectiveRestartTrigger = forceSessionRestart ? Math.max(1, updatedJars) : updatedJars;
                ctrl.onCheckFinished(effectiveRestartTrigger, appUpdateAvailable, () -> restartAsycudaSession(activity));
            } finally {
                sSyncInProgress.set(false);
            }
        }, "Asycuda-UnifiedUpdater").start();
    }

    private static String cleanReleaseNotesPreview(String rawBody) {
        if (rawBody == null || rawBody.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        String[] lines = rawBody.split("\n");
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("[!") || t.startsWith("[![")
                    || t.startsWith("|") || t.startsWith("---") || t.startsWith(">")) {
                continue;
            }
            while (t.startsWith("#")) {
                t = t.substring(1).trim();
            }
            t = t.replace("**", "").replace("`", "").trim();
            if (t.isEmpty()) continue;
            if (sb.length() > 0) sb.append("\n");
            sb.append(t);
            if (sb.length() >= 180) break;
        }
        String res = sb.toString().trim();
        return res.length() > 190 ? res.substring(0, 187).trim() + "..." : res;
    }

    @SuppressWarnings("deprecation")
    private void downloadAndInstallApkUpdate(MainActivity activity, String apkUrl, long expectedSize,
                                             String versionTag, String expectedSha256,
                                             AsycudaMaterialUi.UpdateCenterController ctrl) {
        HttpURLConnection conn = null;
        sApkDownloadActive.set(true);
        try {
            if (!AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl(apkUrl)) {
                ctrl.setAppUpdateStatus("Security error: Untrusted APK download URL", -1, false);
                return;
            }
            if (!AsycudaSecurityValidator.isValidSha256Hex(expectedSha256)) {
                ctrl.setAppUpdateStatus("Security error: Missing or invalid release SHA-256 digest", -1, false);
                return;
            }
            cleanupStaleUpdateApks(activity);
            File updatesCacheDir = new File(activity.getCacheDir(), DIR_UPDATES_CACHE);
            ensureDir(updatesCacheDir);
            File outApk = new File(updatesCacheDir, "ASYCUDA_Iraq_" + versionTag.replaceAll(REGEX_UNSAFE_FILENAME, "") + ".apk");

            conn = (HttpURLConnection) new URL(apkUrl).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty(HEADER_USER_AGENT, "ASYCUDA-Iraq-Android-Updater");

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                ctrl.setAppUpdateStatus("HTTP error downloading APK: " + code, -1, false);
                return;
            }
            if (!AsycudaSecurityValidator.isTrustedGitHubReleaseAssetUrl(conn.getURL().toString())) {
                ctrl.setAppUpdateStatus("Security error: Untrusted APK redirect destination", -1, false);
                return;
            }

            long totalBytes = conn.getContentLengthLong();
            if (totalBytes <= 0) totalBytes = expectedSize;

            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            try (InputStream in = new BufferedInputStream(conn.getInputStream(), 65536);
                 FileOutputStream out = new FileOutputStream(outApk)) {
                byte[] buf = new byte[65536];
                long downloaded = 0;
                int n;
                long lastUiUpdate = 0;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                    sha256Digest.update(buf, 0, n);
                    downloaded += n;
                    long now = System.currentTimeMillis();
                    if (now - lastUiUpdate > 120) {
                        lastUiUpdate = now;
                        int pct = totalBytes > 0 ? (int) ((downloaded * 100L) / totalBytes) : 0;
                        String mbDone = String.format(Locale.US, "%.1f", downloaded / (1024.0 * 1024.0));
                        String mbTotal = totalBytes > 0 ? String.format(Locale.US, "%.1f", totalBytes / (1024.0 * 1024.0)) : "?";
                        ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                                "Downloading " + versionTag + "... " + pct + "% (" + mbDone + " / " + mbTotal + " MB)",
                                "جاري تحميل " + versionTag + "... " + pct + "% (" + mbDone + " / " + mbTotal + " MB)",
                                "دابەزاندنی " + versionTag + "... " + pct + "% (" + mbDone + " / " + mbTotal + " MB)"),
                                pct, totalBytes <= 0);
                    }
                }
                out.getFD().sync();
            }

            // 1. Mandatory SHA-256 integrity verification against GitHub Release digest
            byte[] actualBytes = sha256Digest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte b : actualBytes) hex.append(String.format(Locale.US, "%02x", b & 0xFF));
            if (!hex.toString().equalsIgnoreCase(expectedSha256.trim())) {
                deleteQuietly(outApk);
                ctrl.setAppUpdateStatus("Security error: APK SHA-256 checksum mismatch", -1, false);
                return;
            }

            // 2. Verify APK signing certificate matches currently installed application & pinned release key
            if (!verifyDownloadedApkSignature(activity, outApk)) {
                deleteQuietly(outApk);
                ctrl.setAppUpdateStatus("Security error: APK signature verification failed", -1, false);
                return;
            }

            ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                    "Verified SHA-256 & signature. Opening installer...",
                    "تم التحقق من التوقيع الرقمي. جاري فتح المثبت...",
                    "پشتڕاستکردنەوە تەواو بوو. کردنەوەی دامەزرێنەر..."), 100, false);

            UI.post(() -> {
                try {
                    Uri apkUri = FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTH, outApk);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, activity.getPackageName());
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(install);
                } catch (Exception t) {
                    Toast.makeText(activity, "Installer error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception t) {
            ctrl.setAppUpdateStatus("Download failed: " + t.getMessage(), -1, false);
        } finally {
            sApkDownloadActive.set(false);
            if (conn != null) conn.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    private boolean verifyDownloadedApkSignature(Context ctx, File apkFile) {
        try {
            PackageManager pm = ctx.getPackageManager();
            Signature[] archiveSigs = null;
            Signature[] installedSigs = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo archiveInfo = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_SIGNING_CERTIFICATES);
                if (archiveInfo == null || !ctx.getPackageName().equals(archiveInfo.packageName) || archiveInfo.signingInfo == null) {
                    return false;
                }
                if (archiveInfo.signingInfo.hasMultipleSigners()) {
                    return false;
                }
                archiveSigs = archiveInfo.signingInfo.getSigningCertificateHistory();
                PackageInfo installedInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                if (installedInfo == null || installedInfo.signingInfo == null) {
                    return false;
                }
                installedSigs = installedInfo.signingInfo.getSigningCertificateHistory();
            } else {
                PackageInfo archiveInfo = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_SIGNATURES);
                if (archiveInfo == null || !ctx.getPackageName().equals(archiveInfo.packageName)) {
                    return false;
                }
                PackageInfo installedInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
                archiveSigs = archiveInfo.signatures;
                installedSigs = installedInfo.signatures;
            }
            if (archiveSigs == null || installedSigs == null || archiveSigs.length == 0 || installedSigs.length == 0) {
                return false;
            }
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            String archiveHex = AsycudaSecurityValidator.sha256Hex(sha256, archiveSigs[0].toByteArray());
            String installedHex = AsycudaSecurityValidator.sha256Hex(sha256, installedSigs[0].toByteArray());
            return archiveHex.equalsIgnoreCase(installedHex)
                    && AsycudaSecurityValidator.PINNED_APK_RELEASE_CERT_SHA256.equalsIgnoreCase(archiveHex);
        } catch (Exception t) {
            return false;
        }
    }

    private static boolean isRemoteVersionNewer(String remoteTag, String localTag) {
        if (remoteTag == null || localTag == null) return false;
        String r = remoteTag.trim().replaceFirst("^[vV]", "");
        String l = localTag.trim().replaceFirst("^[vV]", "");
        String[] rp = r.split("\\.");
        String[] lp = l.split("\\.");
        int max = Math.max(rp.length, lp.length);
        for (int i = 0; i < max; i++) {
            int rv = 0;
            int lv = 0;
            try {
                if (i < rp.length) rv = Integer.parseInt(rp[i].replaceAll("\\D", ""));
            } catch (Exception ignored) {
                // Default non-numeric component to 0
            }
            try {
                if (i < lp.length) lv = Integer.parseInt(lp[i].replaceAll("\\D", ""));
            } catch (Exception ignored) {
                // Default non-numeric component to 0
            }
            if (rv > lv) return true;
            if (rv < lv) return false;
        }
        return false;
    }

    /**
     * Captures the live X11 SurfaceView (LorieView) using Android's hardware PixelCopy API
     * so screenshots are never blank/white.
     */
    @SuppressLint("NewApi")
    private void captureWindow(MainActivity activity, java.util.function.Consumer<Bitmap> callback) {
        try {
            LorieView lorieView = activity.getLorieView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && lorieView != null && lorieView.getWidth() > 0 && lorieView.getHeight() > 0) {
                Bitmap bmp = Bitmap.createBitmap(lorieView.getWidth(), lorieView.getHeight(), Bitmap.Config.ARGB_8888);
                android.view.PixelCopy.request(lorieView, bmp, copyResult -> {
                    if (copyResult == android.view.PixelCopy.SUCCESS) {
                        callback.accept(bmp);
                    } else if (activity.getWindow() != null) {
                        android.view.PixelCopy.request(activity.getWindow(), bmp, winRes ->
                            callback.accept(winRes == android.view.PixelCopy.SUCCESS ? bmp : null), UI);
                    } else {
                        callback.accept(null);
                    }
                }, UI);
                return;
            }
            View decor = activity.getWindow().getDecorView();
            Bitmap bmp = Bitmap.createBitmap(Math.max(1, decor.getWidth()), Math.max(1, decor.getHeight()), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            decor.draw(canvas);
            callback.accept(bmp);
        } catch (Exception t) {
            callback.accept(null);
        }
    }

    private void openFeedbackAndBugReportModal(MainActivity activity) {
        sActiveFeedbackController = AsycudaMaterialUi.showFeedbackModal(activity, new AsycudaMaterialUi.FeedbackSubmitHandler() {
            @Override
            public void onCaptureScreenRequested(AsycudaMaterialUi.FeedbackModalController controller, Dialog feedbackDialog) {
                try {
                    feedbackDialog.hide();
                } catch (Exception ignored) {
                    // Ignore dialog hide error
                }
                UI.postDelayed(() -> captureWindow(activity, bmp -> {
                    try {
                        if (bmp == null) throw new IllegalStateException("empty frame");
                        File cacheDiagDir = new File(activity.getCacheDir(), "diagnostics");
                        ensureDir(cacheDiagDir);
                        File capFile = new File(cacheDiagDir, FILE_SCREEN_CAPTURE);
                        try (FileOutputStream fos = new FileOutputStream(capFile)) {
                            bmp.compress(Bitmap.CompressFormat.JPEG, 88, fos);
                            fos.getFD().sync();
                        }
                        setReadableOwnerOnly(capFile);
                        controller.onImageAttached(capFile, FILE_SCREEN_CAPTURE);
                    } catch (Exception t) {
                        Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                                "Couldn't capture the screen",
                                "تعذر التقاط الشاشة",
                                "نەتوانرا وێنەی شاشە بگیرێت"), Toast.LENGTH_SHORT).show();
                    } finally {
                        try {
                            feedbackDialog.show();
                        } catch (Exception ignored) {
                            // Ignore dialog show error
                        }
                    }
                }), 250);
            }

            @Override
            public void onPickImageRequested(AsycudaMaterialUi.FeedbackModalController controller) {
                sActiveFeedbackController = controller;
                try {
                    Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
                    pick.setType("image/*");
                    pick.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "image/png", "image/jpeg", "image/webp" });
                    pick.addCategory(Intent.CATEGORY_OPENABLE);
                    activity.startActivityForResult(Intent.createChooser(pick,
                            AsycudaMaterialUi.tr(activity, "Select Image", "اختر صورة", "وێنەیەک هەڵبژێرە")),
                            REQ_PICK_FEEDBACK_IMAGE);
                } catch (Exception t) {
                    Toast.makeText(activity, "Cannot open image picker: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public StringBuilder buildRedactedDiagnosticPreviewInRam(String description) {
                return buildDiagnosticPreviewBufferInRam(activity, description);
            }

            @Override
            public void onSubmitToSentry(String description, File attachedImage,
                                         AsycudaMaterialUi.FeedbackModalController controller, Dialog feedbackDialog) {
                controller.setUploadState(true, AsycudaMaterialUi.tr(activity,
                        "Sending report and logs...",
                        "جاري إرسال البلاغ والسجلات...",
                        "ناردنی ڕاپۆرت و تۆمارەکان..."), false);

                new Thread(() -> {
                    try {
                        byte[] zipBytes = buildDiagnosticsZipBytes(activity, description, attachedImage);
                        boolean sent = uploadReportEnvelopeToSentry(activity, description, zipBytes, attachedImage);
                        if (sent) {
                            if (attachedImage != null && attachedImage.getName().equals(FILE_SCREEN_CAPTURE)) {
                                deleteQuietly(attachedImage);
                            }
                            controller.setUploadState(false, AsycudaMaterialUi.tr(activity,
                                    "Report sent. Thank you!",
                                    "تم إرسال البلاغ بنجاح. شكراً لك!",
                                    "ڕاپۆرتەکە بە سەرکەوتوویی نێردرا. سوپاس!"), true);
                            UI.postDelayed(() -> {
                                try {
                                    feedbackDialog.dismiss();
                                } catch (Exception ignored) {
                                    // Dialog already dismissed
                                }
                            }, 1300);
                        } else {
                            controller.setUploadState(false, AsycudaMaterialUi.tr(activity,
                                    "Couldn't connect to Sentry. Please check your internet connection and try again.",
                                    "تعذر الاتصال بالخادم. يرجى التحقق من اتصال الإنترنت والمحاولة مرة أخرى.",
                                    "پەیوەندی نەکرا. تکایە هێڵی ئینتەرنێت بپشکنە و هەوڵ بدەرەوە."), false);
                        }
                    } catch (Exception t) {
                        controller.setUploadState(false, "Upload error: " + t.getMessage(), false);
                    }
                }, "Asycuda-SentryUploader").start();
            }
        }, () -> openMaterialControlCenter(activity));
    }

    /**
     * Constructs a 100% in-RAM mutable {@link StringBuilder} preview of the redacted diagnostic
     * data that would be transmitted in a voluntary report. Zero bytes are written to disk, and
     * the caller wipes/zeroes the buffer on dialog dismissal.
     */
    private StringBuilder buildDiagnosticPreviewBufferInRam(MainActivity activity, String userDescription) {
        File filesDir = activity.getFilesDir();
        File clientDir = new File(filesDir, DIR_CLIENT);
        String safeDescription = redactSensitiveLogContent(userDescription);
        StringBuilder sb = new StringBuilder(8192);
        sb.append("=== 1. Device & App Details ===\n");
        sb.append("App Version: ").append(AsycudaMaterialUi.getAppVersionLabel(activity)).append("\n");
        sb.append("Package: ").append(activity.getPackageName()).append("\n");
        sb.append("Language: ").append(AsycudaMaterialUi.getAppLanguage(activity)).append("\n");
        sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
                .append(" (Android ").append(Build.VERSION.RELEASE).append(", SDK ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("JVM Process Alive: ").append(sJavaProcess != null && isProcessAlive(sJavaProcess)).append("\n");
        sb.append("Free Internal Storage (MB): ").append(filesDir.getUsableSpace() / (1024 * 1024)).append("\n");
        sb.append("TermsAccepted: ").append(AsycudaMaterialUi.hasAcceptedTerms(activity)).append("\n");

        if (!safeDescription.isEmpty()) {
            sb.append("\n=== 2. Issue Description ===\n");
            sb.append(safeDescription).append("\n");
        }

        File jarsDir = new File(clientDir, "jars");
        File[] jars = jarsDir.listFiles();
        int jarCount = jars != null ? jars.length : 0;
        sb.append("\n=== 3. Installed Packages ===\n");
        sb.append("Total JARs in client/jars: ").append(jarCount).append(" (filenames and byte sizes only)\n");

        sb.append("\n=== 4. Recent Session Log (asycuda.log) ===\n");
        sb.append(redactSensitiveLogContent(readTail(new File(clientDir, FILE_ASYCUDA_LOG), 3000))).append("\n");

        File clientLog = new File(clientDir, FILE_CLIENT_LOG);
        if (clientLog.exists()) {
            sb.append("\n=== 5. Recent Client Log (client.log) ===\n");
            sb.append(redactSensitiveLogContent(readTail(clientLog, 2000))).append("\n");
        }
        synchronized (sRedactedRamDiagRingBuffer) {
            if (sRedactedRamDiagRingBuffer.length() > 0) {
                sb.append("\n=== 6. In-RAM Universal Diagnostic Trace ===\n");
                sb.append(sRedactedRamDiagRingBuffer).append("\n");
            }
        }
        return sb;
    }

    /**
     * Builds a strictly sanitized, credential-free diagnostic archive purely in memory
     * ({@link ByteArrayOutputStream}) for direct HTTPS transmission to Sentry.
     * Zero diagnostic {@code .zip} files are ever written to disk (neither internal nor external storage),
     * raw JNLP/SharedPreferences/PII files are excluded, logcat is scoped strictly to {@code --pid},
     * and all usernames, passwords, tokens, TINs, and session IDs are scrubbed before packaging.
     */
    private byte[] buildDiagnosticsZipBytes(MainActivity activity, String userDescription, File attachedImage) throws IOException {
        File filesDir = activity.getFilesDir();
        File clientDir = new File(filesDir, DIR_CLIENT);
        purgeStaleDiagnosticFiles(new File(activity.getCacheDir(), "diagnostics"),
                new File(filesDir, DIR_PRINTS));

        String safeDescription = redactSensitiveLogContent(userDescription);
        ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream(65536);

        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(zipBuffer)) {

            // 1. Sanitized Device Summary (zero credentials, zero raw user PII, zero on-disk file)
            StringBuilder info = new StringBuilder();
            info.append("=== ASYCUDA Iraq Sanitized Diagnostics Report (In-Memory Sentry Stream) ===\n");
            info.append("PrivacySanitization: STRICT (Credentials, usernames, passwords, TINs, cookies, and session tokens scrubbed; streamed in RAM only; zero local ZIP files written to disk)\n");
            info.append("Timestamp: ").append(new Date().toString()).append("\n");
            info.append("App Version: ").append(AsycudaMaterialUi.getAppVersionLabel(activity)).append("\n");
            info.append("Package: ").append(activity.getPackageName()).append("\n");
            info.append("Language: ").append(AsycudaMaterialUi.getAppLanguage(activity)).append("\n");
            info.append("Auto-Show Keyboard: ").append(AsycudaMaterialUi.isAutoKeyboardEnabled(activity)).append("\n");
            info.append("Function Keys Bar: ").append(AsycudaMaterialUi.isAssistKeysEnabled(activity)).append("\n");
            info.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            info.append("Device Model: ").append(Build.MODEL).append(" (").append(Build.DEVICE).append(")\n");
            info.append("Android Version: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
            info.append("Supported ABIs: ").append(java.util.Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
            info.append("JVM Process Alive: ").append(sJavaProcess != null && isProcessAlive(sJavaProcess)).append("\n");
            info.append("Free Internal Storage (MB): ").append(filesDir.getUsableSpace() / (1024 * 1024)).append("\n");
            info.append("TermsAccepted: ").append(AsycudaMaterialUi.hasAcceptedTerms(activity)).append("\n");
            if (!safeDescription.isEmpty()) {
                info.append("\n=== USER REPORT (REDACTED) ===\n");
                info.append(safeDescription).append("\n");
                addBytesToZip(zos, "user_description.txt", safeDescription.getBytes(StandardCharsets.UTF_8));
            }
            addBytesToZip(zos, "device_info.txt", info.toString().getBytes(StandardCharsets.UTF_8));

            // 2. Attached Screenshot / Image if explicitly chosen by the user
            if (attachedImage != null && attachedImage.exists()) {
                addFileToZipIfExists(zos, attachedImage, attachedImage.getName(), false);
            }

            // 3. Installed JARs inventory (filenames and byte sizes only)
            File jarsDir = new File(clientDir, "jars");
            File[] jars = jarsDir.listFiles();
            if (jars != null) {
                java.util.Arrays.sort(jars, Comparator.comparing(File::getName));
                StringBuilder jarList = new StringBuilder();
                jarList.append("Total JARs in client/jars: ").append(jars.length).append("\n\n");
                for (File j : jars) {
                    jarList.append(String.format(Locale.US, "%-32s  %10d bytes  %s%n",
                            j.getName(), j.length(), new Date(j.lastModified()).toString()));
                }
                addBytesToZip(zos, "jars_inventory.txt", jarList.toString().getBytes(StandardCharsets.UTF_8));
            }

            // 4. Core ASYCUDA & Client Logs (tail only + strict credential/token redaction)
            addFileToZipIfExists(zos, new File(clientDir, FILE_ASYCUDA_LOG), FILE_ASYCUDA_LOG, true);
            addFileToZipIfExists(zos, new File(clientDir, FILE_CLIENT_LOG), FILE_CLIENT_LOG, true);
            synchronized (sRedactedRamDiagRingBuffer) {
                if (sRedactedRamDiagRingBuffer.length() > 0) {
                    addBytesToZip(zos, "universal_diagnostics_ram.txt",
                            sRedactedRamDiagRingBuffer.toString().getBytes(StandardCharsets.UTF_8));
                }
            }

            // 5. Recent Android Logcat scoped strictly to our own process PID
            String myPid = String.valueOf(android.os.Process.myPid());
            try {
                Process logProc = new ProcessBuilder("/system/bin/logcat", "-d", "-t", "500", "--pid=" + myPid, "-v", "threadtime")
                        .redirectErrorStream(true)
                        .start();
                ByteArrayOutputStream lbo = new ByteArrayOutputStream();
                try (InputStream in = logProc.getInputStream()) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        lbo.write(buf, 0, n);
                        if (lbo.size() > 256 * 1024) break;
                    }
                }
                logProc.waitFor();
                byte[] logBytes = lbo.toByteArray();
                if (logBytes.length > 0) {
                    String redactedLogcat = redactSensitiveLogContent(new String(logBytes, StandardCharsets.UTF_8));
                    addBytesToZip(zos, "android_logcat.txt", redactedLogcat.getBytes(StandardCharsets.UTF_8));
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
                // Ignore logcat capture failure
            }

            zos.finish();
        }

        return zipBuffer.toByteArray();
    }

    private static void purgeStaleDiagnosticFiles(File... dirs) {
        if (dirs == null) return;
        for (File dir : dirs) {
            if (dir == null || !dir.exists()) continue;
            File[] children = dir.listFiles();
            if (children == null) continue;
            for (File f : children) {
                String name = f.getName();
                if (name.startsWith("ASYCUDA_Diagnostics_") && name.endsWith(".zip")) {
                    deleteQuietly(f);
                }
            }
        }
    }

    /**
     * Strictly scrubs any ASYCUDA login credentials, usernames, passwords, session IDs,
     * cookies, authorization headers, TIN/declarant identifiers, XML auth tags, and tokens
     * (including both quoted JSON strings and unquoted key-value pairs) before any log text
     * is written to a diagnostic report.
     */
    public static String redactSensitiveLogContent(String raw) {
        return AsycudaSecurityValidator.redactSensitiveLogContent(raw, BRIDGE_AUTH_TOKEN);
    }

    private static long getAppVersionCode(Context ctx) {
        try {
            android.content.pm.PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return pInfo.getLongVersionCode();
            }
            return pInfo.versionCode;
        } catch (Exception ignored) {
            return 42L;
        }
    }

    private static JSONObject buildSentryContexts(Context ctx, String appVer) throws org.json.JSONException {
        JSONObject contexts = new JSONObject();
        JSONObject devCtx = new JSONObject();
        devCtx.put("name", Build.MODEL);
        devCtx.put("model", Build.MODEL);
        devCtx.put("manufacturer", Build.MANUFACTURER);
        devCtx.put("brand", Build.BRAND);
        devCtx.put("arch", "arm64-v8a");
        contexts.put("device", devCtx);

        JSONObject osCtx = new JSONObject();
        osCtx.put("name", "Android");
        osCtx.put("version", Build.VERSION.RELEASE);
        contexts.put("os", osCtx);

        JSONObject appCtx = new JSONObject();
        appCtx.put("app_identifier", ctx.getPackageName());
        appCtx.put("app_name", "ASYCUDA Iraq");
        appCtx.put(KEY_APP_VERSION, appVer);
        contexts.put("app", appCtx);
        return contexts;
    }

    /**
     * Uploads the user report, device metadata, optional screenshot/image, and in-memory sanitized
     * diagnostic bundle directly to Sentry's Envelope API (write-only public DSN) over HTTPS.
     */
    private boolean uploadReportEnvelopeToSentry(Context ctx, String description, byte[] zipBytes, File attachedImage) {
        HttpURLConnection conn = null;
        try {
            String dsn = BuildConfig.SENTRY_DSN;
            if (dsn == null || dsn.isEmpty()) return false;

            SentryUriParts parts = parseSentryDsn(dsn);
            if (parts == null) return false;

            String safeDescription = redactSensitiveLogContent(description != null ? description : "");
            String eventId = UUID.randomUUID().toString().replace("-", "");
            SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            String nowIso = isoFmt.format(new Date());

            String verLabel = AsycudaMaterialUi.getAppVersionLabel(ctx);
            String firstLine = safeDescription.split("\n")[0].trim();
            if (firstLine.length() > 90) firstLine = firstLine.substring(0, 90) + "...";

            // 1. Envelope Header
            JSONObject envHeader = new JSONObject();
            envHeader.put(KEY_EVENT_ID, eventId);
            envHeader.put("sent_at", nowIso);
            envHeader.put("dsn", dsn);

            // 2. Event Payload
            JSONObject eventObj = new JSONObject();
            eventObj.put(KEY_EVENT_ID, eventId);
            eventObj.put("timestamp", nowIso);
            eventObj.put("platform", "java");
            eventObj.put("level", "info");
            eventObj.put("release", "iq.asycuda@" + verLabel.replaceFirst("^v", "") + "+" + getAppVersionCode(ctx));
            eventObj.put("environment", "production");

            JSONObject msgObj = new JSONObject();
            msgObj.put("formatted", "[User Report] " + firstLine + "\n\n" + safeDescription);
            eventObj.put("message", msgObj);

            JSONObject userObj = new JSONObject();
            userObj.put("id", Build.MANUFACTURER + "_" + Build.MODEL);
            userObj.put("username", Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
            eventObj.put("user", userObj);

            JSONObject tags = new JSONObject();
            tags.put(KEY_APP_VERSION, verLabel);
            tags.put(KEY_DEVICE_MODEL, Build.MANUFACTURER + " " + Build.MODEL);
            tags.put("android_version", Build.VERSION.RELEASE);
            tags.put("app_language", AsycudaMaterialUi.getAppLanguage(ctx));
            tags.put("has_screenshot", String.valueOf(attachedImage != null && attachedImage.exists()));
            eventObj.put("tags", tags);

            eventObj.put("contexts", buildSentryContexts(ctx, verLabel));

            JSONObject extra = new JSONObject();
            extra.put("full_description", safeDescription);
            extra.put("jvm_running", sJavaProcess != null && isProcessAlive(sJavaProcess));
            File logFile = new File(new File(ctx.getFilesDir(), DIR_CLIENT), FILE_ASYCUDA_LOG);
            extra.put("asycuda_log_tail", redactSensitiveLogContent(readTail(logFile, 2500)));
            eventObj.put("extra", extra);

            byte[] eventBytes = eventObj.toString().getBytes(StandardCharsets.UTF_8);
            JSONObject eventItemHeader = new JSONObject();
            eventItemHeader.put("type", "event");
            eventItemHeader.put(KEY_LENGTH, eventBytes.length);
            eventItemHeader.put(KEY_CONTENT_TYPE, MIME_JSON);

            // 3. User Report Item (also populates Sentry's User Feedback tab)
            JSONObject fbObj = new JSONObject();
            fbObj.put(KEY_EVENT_ID, eventId);
            fbObj.put("name", Build.MANUFACTURER + " " + Build.MODEL);
            fbObj.put("email", "user@asycuda-iraq.app");
            fbObj.put("comments", safeDescription);
            byte[] fbBytes = fbObj.toString().getBytes(StandardCharsets.UTF_8);
            JSONObject fbItemHeader = new JSONObject();
            fbItemHeader.put("type", "user_report");
            fbItemHeader.put(KEY_LENGTH, fbBytes.length);
            fbItemHeader.put(KEY_CONTENT_TYPE, MIME_JSON);

            String endpoint = "https://" + parts.host + "/api/" + parts.projectId
                    + "/envelope/?sentry_key=" + parts.publicKey + "&sentry_version=7&sentry_client=asycuda.android/3.8.0";

            conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(25000);
            conn.setRequestProperty("Content-Type", "application/x-sentry-envelope");

            try (OutputStream out = conn.getOutputStream()) {
                out.write((envHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));

                out.write((eventItemHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                out.write(eventBytes);
                out.write('\n');

                out.write((fbItemHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                out.write(fbBytes);
                out.write('\n');

                if (zipBytes != null && zipBytes.length > 0) {
                    JSONObject zipHeader = new JSONObject();
                    zipHeader.put("type", TYPE_ATTACHMENT);
                    zipHeader.put(KEY_LENGTH, zipBytes.length);
                    zipHeader.put(KEY_FILENAME, "ASYCUDA_Diagnostics_Sanitized.zip");
                    zipHeader.put(KEY_CONTENT_TYPE, MIME_ZIP);
                    zipHeader.put(KEY_ATTACHMENT_TYPE, VAL_EVENT_ATTACHMENT);
                    out.write((zipHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                    out.write(zipBytes);
                    out.write('\n');
                }

                if (attachedImage != null && attachedImage.exists()) {
                    byte[] imgBytes = readFileBytes(attachedImage);
                    if (imgBytes.length > 0) {
                        JSONObject imgHeader = new JSONObject();
                        imgHeader.put("type", TYPE_ATTACHMENT);
                        imgHeader.put(KEY_LENGTH, imgBytes.length);
                        imgHeader.put(KEY_FILENAME, attachedImage.getName());
                        imgHeader.put(KEY_CONTENT_TYPE, guessMime(attachedImage.getName()));
                        imgHeader.put(KEY_ATTACHMENT_TYPE, VAL_EVENT_ATTACHMENT);
                        out.write((imgHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                        out.write(imgBytes);
                        out.write('\n');
                    }
                }
                out.flush();
            }

            int respCode = conn.getResponseCode();
            return respCode >= 200 && respCode < 300;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static class SentryUriParts {
        String publicKey;
        String host;
        String projectId;
    }

    private static SentryUriParts parseSentryDsn(String dsn) {
        try {
            URL u = new URL(dsn);
            SentryUriParts p = new SentryUriParts();
            p.publicKey = u.getUserInfo();
            if (p.publicKey != null && p.publicKey.contains(":")) {
                p.publicKey = p.publicKey.split(":")[0];
            }
            p.host = u.getHost();
            String path = u.getPath();
            if (path != null && path.startsWith("/")) path = path.substring(1);
            p.projectId = path;
            if (p.publicKey == null || p.host == null || p.projectId == null) return null;
            return p;
        } catch (Exception t) {
            return null;
        }
    }

    private static byte[] readFileBytes(File f) {
        try (FileInputStream fis = new FileInputStream(f);
             ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            byte[] buf = new byte[32768];
            int n;
            while ((n = fis.read(buf)) != -1) bos.write(buf, 0, n);
            return bos.toByteArray();
        } catch (Exception t) {
            return new byte[0];
        }
    }

    private static void addBytesToZip(java.util.zip.ZipOutputStream zos, String entryName, byte[] data) {
        try {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
            zos.putNextEntry(entry);
            zos.write(data);
            zos.closeEntry();
        } catch (Exception ignored) {
            // Ignore single zip entry write failure
        }
    }

    private static void addFileToZipIfExists(java.util.zip.ZipOutputStream zos, File file, String entryName, boolean redactLogs) {
        if (file == null || !file.exists() || !file.isFile()) return;
        if (redactLogs) {
            String tail = readTail(file, 65536);
            if (!tail.isEmpty()) {
                String clean = redactSensitiveLogContent(tail);
                addBytesToZip(zos, entryName, clean.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
            zos.putNextEntry(entry);
            byte[] buf = new byte[16384];
            int n;
            while ((n = fis.read(buf)) != -1) {
                zos.write(buf, 0, n);
            }
            zos.closeEntry();
        } catch (Exception ignored) {
            // Ignore individual file zip failure
        }
    }

    private void openUrl(Context ctx, String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception t) {
            Toast.makeText(ctx, "Cannot open link: " + url, Toast.LENGTH_SHORT).show();
        }
    }

    private static void collectFiles(File dir, List<File> out) {
        if (dir == null || !dir.exists()) return;
        File[] list = dir.listFiles();
        if (list == null) return;
        for (File f : list) {
            if (f.isFile() && f.length() > 0 && !f.getName().startsWith(".")) {
                out.add(f);
            }
        }
    }

    private static String guessMime(String name) {
        String low = name.toLowerCase(Locale.US);
        if (low.endsWith(".pdf")) return MIME_PDF;
        if (low.endsWith(".zip")) return MIME_ZIP;
        if (low.endsWith(".jpg") || low.endsWith(".jpeg")) return "image/jpeg";
        if (low.endsWith(".png")) return "image/png";
        if (low.endsWith(".webp")) return "image/webp";
        if (low.endsWith(".xls")) return "application/vnd.ms-excel";
        if (low.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (low.endsWith(".doc")) return "application/msword";
        if (low.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (low.endsWith(".xml")) return "text/xml";
        return "application/octet-stream";
    }

    private static boolean isProcessAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) deleteRecursive(c);
            }
        }
        deleteQuietly(f);
    }

    private static void deleteQuietly(File f) {
        if (f != null && f.exists() && !f.delete()) {
            f.deleteOnExit();
        }
    }

    private static void ensureDir(File dir) {
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            // Directory already created concurrently
        }
    }

    private static void ensureFileCreated(File f) {
        try {
            if (f != null && !f.exists() && !f.createNewFile()) {
                // File already exists
            }
        } catch (IOException ignored) {
            // Marker creation is best-effort
        }
    }

    private static void setReadableOwnerOnly(File f) {
        if (f != null && f.exists() && !f.setReadable(true, true)) {
            // Ignore if filesystem does not support POSIX permissions
        }
    }

    private static void setReadOnly(File f) {
        if (f != null && f.exists() && !f.setWritable(false, false)) {
            // Ignore if filesystem does not support POSIX permissions
        }
    }

    private static void setExecutableOwnerOnly(File f) {
        if (f != null && f.exists() && !f.setExecutable(true, true)) {
            // Ignore if filesystem does not support POSIX permissions
        }
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
