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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.provider.Settings;
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

@SuppressWarnings({"java:S3776", "java:S1141", "java:S2696", "java:S135", "java:S1075", "java:S9391"})
public class AsycudaStandaloneManager {

    private static final String MOF_CODEBASE_URL = "https://asy.mof.gov.iq/aw/";
    private static final String MOF_JNLP_URL     = "https://asy.mof.gov.iq/aw/AWIQWorld.jnlp";
    private static final String GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/latest";
    public static final int REQ_PICK_FEEDBACK_IMAGE = 47892;

    private static final String DOWNLOAD_FILES_PATH = "/storage/emulated/0/Download/Asycuda_Files";
    private static final String DOWNLOAD_PRINTS_PATH = "/storage/emulated/0/Download/Asycuda_Prints";
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
    private static final String KEY_SIGNATORY_NAME = "signatory_name";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_CONSENT_ID = "consent_id";
    private static final String KEY_POLICY_VERSION = "policy_version";
    private static final String KEY_DEVICE_HASH = "device_hash";
    private static final String KEY_SHA256_SIG = "sha256_signature";
    private static final String BOOT_JAR_REL = "jars/00_boot.jar";
    private static final String JNLP_JAR_REL = "jars/00_jnlp.jar";
    private static final String BOOTPATCH_JAR_NAME = "00_bootpatch.jar";
    private static final String DIR_PRINTS = "prints";
    private static final String DIR_CLIENT = "client";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String FILE_AWIQ_JNLP = "AWIQWorld.jnlp";
    private static final String FILE_CLASSPATH = "classpath.txt";
    private static final String FILE_ASYCUDA_LOG = "asycuda.log";
    private static final String FILE_CONSENT_RECEIPT = "legal_consent_receipt.json";
    private static final String REGEX_UNSAFE_FILENAME = "[^a-zA-Z0-9._-]";

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
    private static final AtomicBoolean sSyncInProgress = new AtomicBoolean(false);
    private static final AtomicBoolean sStartupAppCheckDone = new AtomicBoolean(false);
    private static volatile boolean sServerStarted = false;
    private static volatile CmdEntryPoint sCmdEntry = null;
    private static volatile Process sJavaProcess = null;
    private static volatile MainActivity sCurrentActivity = null;
    private static volatile AsycudaMaterialUi.FeedbackModalController sActiveFeedbackController = null;
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
                String ext = "png";
                if (origName != null && origName.contains(".")) {
                    ext = origName.substring(origName.lastIndexOf('.') + 1).toLowerCase(Locale.US);
                } else if (mime != null && mime.contains("jpeg")) {
                    ext = "jpg";
                }
                if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("webp")) {
                    ext = "png";
                }
                File printsDir = new File(activity.getFilesDir(), DIR_PRINTS);
                ensureDir(printsDir);
                File outImg = new File(printsDir, "feedback_attachment." + ext);
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

        // Listen for when user returns from Android Permission Settings screen
        activity.getWindow().getDecorView().getViewTreeObserver().addOnWindowFocusChangeListener(hasFocus -> {
            if (hasFocus) {
                checkPermissionsAndProceed(sCurrentActivity != null ? sCurrentActivity : activity);
            }
        });

        checkPermissionsAndProceed(activity);
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return true;
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
                                receipt -> {
                                    sTermsDialogShowing = false;
                                    uploadConsentReceiptToSentry(activity, receipt);
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
        } else if (!AsycudaMaterialUi.isTermsCloudSynced(activity)) {
            JSONObject savedReceipt = AsycudaMaterialUi.loadSavedConsentReceipt(activity);
            if (savedReceipt != null) {
                uploadConsentReceiptToSentry(activity, savedReceipt);
            }
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

        // Gate 1: All Files Access Permission (Required for /sdcard/Download/Asycuda_Files & Prints)
        if (!hasStoragePermission()) {
            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Storage permission is required to save & open Customs PDFs and declarations.",
                    "صلاحية الوصول للملفات مطلوبة لحفظ وفتح ملفات PDF والتصاريح الكمركية.",
                    "مۆڵەتی گەیشتن بە پەڕگەکان پێویستە بۆ پاشەکەوتکردن و کردنەوەی پەڕگەکانی PDF."));
            showSplashButton(AsycudaMaterialUi.tr(activity,
                    "Grant All Files Access",
                    "منح صلاحية الوصول للملفات",
                    "پێدانی مۆڵەتی پەڕگەکان"), v -> {
                try {
                    Intent permIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(permIntent);
                } catch (Exception ignored) {
                    // Ignore if settings intent is unavailable
                }
            });
            return;
        }

        // Gate 2: Notification Permission on Android 13+ (Wait until granted or dismissed)
        if (!hasNotificationPermission(activity) && !activity.getSharedPreferences("asy_perms", Context.MODE_PRIVATE).getBoolean("asked_notif", false)) {
            activity.getSharedPreferences("asy_perms", Context.MODE_PRIVATE).edit().putBoolean("asked_notif", true).apply();
            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Please allow or dismiss the notification prompt to continue...",
                    "يرجى السماح بالإشعارات أو إغلاق النافذة للمتابعة...",
                    "تکایە ڕێگە بە ئاگادارکردنەوەکان بدە بۆ بەردەوامبوون..."));
            return;
        }

        hideSplashButton();

        if (sExtractingOrBooting || sSyncInProgress.get()) {
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
        int pad = dp(activity, 28);
        card.setPadding(pad, pad, pad, pad);
        card.setBackground(AsycudaMaterialUi.createSheetContainerDrawable(activity, 22));
        card.setElevation(dp(activity, 20));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(activity, 68), dp(activity, 68));
        iconLp.bottomMargin = dp(activity, 12);
        card.addView(icon, iconLp);

        TextView title = new TextView(activity);
        title.setText(AsycudaMaterialUi.tr(activity,
                "ASYCUDA World — Republic of Iraq",
                "نظام أسيكودا العالمي — جمهورية العراق",
                "سیستەمی ئەسیکۆدای جیهانی — کۆماری عێراق"));
        title.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setGravity(Gravity.CENTER);
        card.addView(title);

        TextView sub = new TextView(activity);
        sub.setText(AsycudaMaterialUi.tr(activity,
                "General Commission for Customs • Client " + AsycudaMaterialUi.getAppVersionLabel(activity),
                "الهيئة العامة للكمارك • الإصدار " + AsycudaMaterialUi.getAppVersionLabel(activity),
                "دەستەی گشتیی گومرگ • وەشانی " + AsycudaMaterialUi.getAppVersionLabel(activity)));
        sub.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dp(activity, 4);
        subLp.bottomMargin = dp(activity, 18);
        card.addView(sub, subLp);

        mSplashProgress = new ProgressBar(activity);
        mSplashProgress.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        card.addView(mSplashProgress, new LinearLayout.LayoutParams(dp(activity, 32), dp(activity, 32)));

        mSplashStatus = new TextView(activity);
        mSplashStatus.setText(AsycudaMaterialUi.tr(activity,
                "Checking permissions...",
                "جاري التحقق من الصلاحيات...",
                "پشکنینی مۆڵەتەکان..."));
        mSplashStatus.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        mSplashStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        mSplashStatus.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams stLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stLp.topMargin = dp(activity, 14);
        card.addView(mSplashStatus, stLp);

        mActionBtn = AsycudaMaterialUi.createCupertinoButton(activity,
                AsycudaMaterialUi.tr(activity, "Grant All Files Access", "منح صلاحية الوصول للملفات", "پێدانی مۆڵەتی پەڕگەکان"),
                true, false, null);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dp(activity, 14);
        mActionBtn.setLayoutParams(btnLp);
        mActionBtn.setVisibility(View.GONE);
        card.addView(mActionBtn);

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(
                dp(activity, 470), ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mSplashOverlay.addView(card, cardLp);
        root.addView(mSplashOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void showSplashButton(String label, View.OnClickListener listener) {
        UI.post(() -> {
            if (mSplashProgress != null) mSplashProgress.setVisibility(View.GONE);
            if (mActionBtn != null) {
                mActionBtn.setText(label);
                mActionBtn.setOnClickListener(listener);
                mActionBtn.setVisibility(View.VISIBLE);
            }
        });
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
        try {
            startKeepAliveService(activity);

            File filesDir = activity.getFilesDir();
            File clientDir = new File(filesDir, DIR_CLIENT);
            File usrTmpDir = new File(filesDir, "usr/tmp");
            File x11UnixDir = new File(usrTmpDir, ".X11-unix");
            File homeDir = new File(filesDir, "home");
            File printsDir = new File(filesDir, DIR_PRINTS);
            File runtimeMarker = new File(filesDir, ".extracted_v3_2");
            File bootPatchMarker = new File(filesDir, ".bootpatch_v3_8_0");

            if (!runtimeMarker.exists()) {
                deleteRecursive(new File(filesDir, "usr"));
                deleteRecursive(new File(filesDir, "jre8"));
                deleteQuietly(new File(filesDir, ".extracted_v3"));
                deleteQuietly(new File(filesDir, ".extracted_v3_1"));

                updateStatus("Preparing Java 8 runtime environment (1/2)...");
                extractTarXzAsset(activity, "runtime-arm64.tar.xz", filesDir);

                if (!new File(clientDir, BOOTPATCH_JAR_NAME).exists()) {
                    updateStatus("Extracting ASYCUDA World Iraq client packages (2/2)...");
                    ensureDir(clientDir);
                    extractTarXzAsset(activity, "asycuda-iraq.tar.xz", clientDir);
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
                updateStatus("Applying v3.8.0 security & SOClass bridge components...");
                extractBootJarsOnly(activity, "asycuda-iraq.tar.xz", clientDir);
                ensureFileCreated(bootPatchMarker);
            }

            // Migrate .SOClassCache from slow external FUSE (/storage/emulated/0) to fast internal flash (files/home)
            migrateSoClassCacheToFastStorage(homeDir);

            // Smart Startup Sync: Check MOF Server (asy.mof.gov.iq) for dynamic JNLP list & JAR updates
            syncGovernmentJars(clientDir, false, null);

            // Background Startup App Update Check (checks GitHub releases without blocking startup)
            checkStartupAppUpdateQuietly(activity);

            ensureDir(x11UnixDir);
            ensureDir(homeDir);
            ensureDir(printsDir);
            try {
                Os.chmod(usrTmpDir.getAbsolutePath(), 0700);
                Os.chmod(x11UnixDir.getAbsolutePath(), 0700);
            } catch (Exception ignored) {
                // Ignore chmod if filesystem does not support POSIX permissions
            }

            deleteQuietly(new File(usrTmpDir, ".X0-lock"));
            deleteQuietly(new File(x11UnixDir, "X0"));

            Os.setenv("HOME", homeDir.getAbsolutePath(), true);
            Os.setenv("PREFIX", new File(filesDir, "usr").getAbsolutePath(), true);
            Os.setenv("TMPDIR", usrTmpDir.getAbsolutePath(), true);
            Os.setenv("XKB_CONFIG_ROOT", new File(filesDir, "usr/share/X11/xkb").getAbsolutePath(), true);
            Os.setenv("DISPLAY", ":0", true);

            updateStatus("Starting Embedded X11 Display Engine (:0)...");
            UI.post(() -> {
                try {
                    System.loadLibrary("Xlorie");
                    CmdEntryPoint.ctx = activity.getApplicationContext();
                    if (sCmdEntry == null) {
                        sCmdEntry = new CmdEntryPoint(new String[] { ":0" });
                    }
                    MainActivity act = sCurrentActivity != null ? sCurrentActivity : activity;
                    act.connectToService(sCmdEntry);
                } catch (UnsatisfiedLinkError | Exception t) {
                    Toast.makeText(activity, "X11 Init Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            File x0Socket = new File(x11UnixDir, "X0");
            for (int i = 0; i < 40; i++) {
                if (x0Socket.exists()) break;
                Thread.sleep(150);
            }

            updateStatus("Starting ASYCUDA World Iraq Client...");
            launchAsycudaJvm(activity);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception t) {
            updateStatus("Startup error: " + t.getMessage());
        } finally {
            sExtractingOrBooting = false;
        }
    }

    /**
     * Checks GitHub Releases on launch in the background; if a newer APK version is published,
     * opens the Update Center modal automatically so the user is notified right away.
     */
    private void checkStartupAppUpdateQuietly(MainActivity activity) {
        if (!sStartupAppCheckDone.compareAndSet(false, true)) return;
        new Thread(() -> {
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
     * Moves .SOClassCache (178 MB of reference tables) from external FUSE storage
     * (/storage/emulated/0/Download/Asycuda_Files/.SOClassCache) to internal ext4/f2fs flash
     * (/data/data/iq.asycuda/files/home/.SOClassCache) for 50x faster document/window loading.
     */
    private void migrateSoClassCacheToFastStorage(File internalHome) {
        try {
            File extCache = new File(DOWNLOAD_FILES_PATH + "/.SOClassCache");
            File intCache = new File(internalHome, ".SOClassCache");
            if (extCache.exists() && extCache.isDirectory()) {
                File[] extFiles = extCache.listFiles();
                File[] intFiles = intCache.listFiles();
                int extCount = extFiles != null ? extFiles.length : 0;
                int intCount = intFiles != null ? intFiles.length : 0;
                if (extCount > intCount) {
                    updateStatus("Optimizing local tariff cache on internal high-speed storage...");
                    copyDirectoryRecursive(extCache, intCache);
                }
            }
        } catch (Exception ignored) {
            // Ignore cache migration errors
        }
    }

    private void copyDirectoryRecursive(File src, File dst) {
        if (src == null || !src.exists()) return;
        if (src.isDirectory()) {
            ensureDir(dst);
            File[] children = src.listFiles();
            if (children == null) return;
            for (File c : children) {
                copyDirectoryRecursive(c, new File(dst, c.getName()));
            }
        } else {
            if (dst.exists() && dst.length() == src.length()) return;
            try (FileInputStream in = new FileInputStream(src);
                 FileOutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[65536];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            } catch (Exception ignored) {
                // Ignore individual file copy error
            }
        }
    }

    /**
     * Pre-resolves asy.mof.gov.iq via Android system DNS and writes to usr/glibc/etc/hosts
     * so glibc inside the JVM never stalls on DNS queries.
     */
    private void refreshGlibcHostsCache(File filesDir) {
        try {
            File hostsFile = new File(filesDir, "usr/glibc/etc/hosts");
            if (hostsFile.getParentFile() != null) ensureDir(hostsFile.getParentFile());
            StringBuilder sb = new StringBuilder();
            sb.append("127.0.0.1 localhost\n");
            sb.append("::1 localhost\n");
            try {
                InetAddress mofAddr = InetAddress.getByName("asy.mof.gov.iq");
                if (mofAddr != null && mofAddr.getHostAddress() != null) {
                    sb.append(mofAddr.getHostAddress()).append(" asy.mof.gov.iq\n");
                }
            } catch (Exception ignored) {
                // Offline DNS lookup fallback
            }
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
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            String destCanonPrefix = destDir.getCanonicalPath() + File.separator;
            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            int found = 0;
            while ((entry = tis.getNextTarEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith("./")) name = name.substring(2);
                if (BOOT_JAR_REL.equals(name) || JNLP_JAR_REL.equals(name)
                        || BOOTPATCH_JAR_NAME.equals(name) || "jars/client.jar".equals(name)) {
                    File outFile = new File(destDir, name);
                    if (!outFile.getCanonicalPath().startsWith(destCanonPrefix)) {
                        continue;
                    }
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int n;
                        while ((n = tis.read(buf)) != -1) fos.write(buf, 0, n);
                        fos.getFD().sync();
                    }
                    setReadableOwnerOnly(outFile);
                    found++;
                    if (found >= 4) break;
                }
            }
        } catch (Exception ignored) {
            // Ignore partial boot jar extraction error
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
        try {
            updateStatus("Checking customs packages on asy.mof.gov.iq...");
            if (modalCtrl != null) {
                modalCtrl.setMofUpdateStatus("Connecting to asy.mof.gov.iq...", "Reading " + FILE_AWIQ_JNLP, 0, 100, true);
            }
            if (!manualForceCheck) {
                Thread.sleep(250);
            }
            SSLSocketFactory sslFactory = createMofVerifiedSslFactory();

            int connTimeout = manualForceCheck ? 8000 : 3500;
            int readTimeout = manualForceCheck ? 15000 : 6000;

            HttpURLConnection jnlpConn = openMofConnection(MOF_JNLP_URL, sslFactory, connTimeout, readTimeout);
            jnlpConn.setRequestMethod("GET");
            int code = jnlpConn.getResponseCode();
            if (code != 200) {
                jnlpConn.disconnect();
                updateStatus("Server returned HTTP " + code + ", using local packages");
                if (modalCtrl != null) {
                    modalCtrl.setMofUpdateStatus("Server returned HTTP " + code + " (using local files)", "Cached", 0, 0, false);
                }
                Thread.sleep(300);
                return 0;
            }

            String jnlpXml;
            try (InputStream in = jnlpConn.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
                jnlpXml = new String(bos.toByteArray(), StandardCharsets.UTF_8);
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
                if (flatName.isEmpty() || flatName.startsWith("00_")) {
                    checkedCounter.incrementAndGet();
                    futures.add(null);
                    continue;
                }
                activeGovJarNames.add(flatName);
                cpEntries.add("jars/" + flatName);
                final File targetJar = new File(jarsDir, flatName);
                final String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));

                futures.add(pool.submit(() -> {
                    boolean needsDl = !targetJar.exists() || targetJar.length() == 0;
                    if (!needsDl) {
                        HttpURLConnection head = null;
                        try {
                            head = openMofConnection(jarUrl, sslFactory, headTimeout, headTimeout);
                            head.setRequestMethod("HEAD");
                            if (head.getResponseCode() == 200) {
                                long remoteLen = head.getContentLengthLong();
                                if (remoteLen > 0 && remoteLen != targetJar.length()) {
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
                String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));
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

            for (int d = 0; d < toDownloadUrls.size(); d++) {
                String jarUrl = toDownloadUrls.get(d);
                File targetJar = toDownloadFiles.get(d);
                String msg = "Downloading " + targetJar.getName() + " (" + (d + 1) + "/" + toDownloadUrls.size() + ")...";
                updateStatus(msg);
                if (modalCtrl != null) {
                    modalCtrl.setMofUpdateStatus(msg, targetJar.getName(), d + 1, toDownloadUrls.size(), false);
                }
                if (downloadJarAtomically(jarUrl, targetJar, sslFactory)) {
                    updatedCount++;
                }
            }

            // Rebuild classpath.txt with preserved bridge jars first
            StringBuilder cpBuilder = new StringBuilder();
            for (String entry : cpEntries) {
                if (cpBuilder.length() > 0) cpBuilder.append(":");
                cpBuilder.append(entry);
            }
            File cpFile = new File(clientDir, FILE_CLASSPATH);
            try (FileOutputStream fos = new FileOutputStream(cpFile)) {
                fos.write(cpBuilder.toString().getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Exception ignored) {
                // Ignore classpath write error
            }

            if (modalCtrl != null) {
                if (updatedCount > 0) {
                    modalCtrl.setMofUpdateStatus("Downloaded " + updatedCount + " updated package(s)",
                            total + " packages checked", total, total, false);
                } else {
                    modalCtrl.setMofUpdateStatus("All " + total + " customs packages are up to date",
                            "asy.mof.gov.iq", 0, 0, false);
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
        } catch (Exception ignored) {
            try {
                String[] list = new File(clientDir, "jars").list();
                int cached = list != null ? list.length : 0;
                updateStatus("Offline mode: using " + cached + " local packages");
                if (modalCtrl != null) {
                    modalCtrl.setMofUpdateStatus("Server unreachable, using " + cached + " local files",
                            "Check your internet or VPN connection", 0, 0, false);
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

            if (tmpJar.length() > 0) {
                if (targetJar.exists()) deleteQuietly(targetJar);
                if (tmpJar.renameTo(targetJar)) {
                    setReadableOwnerOnly(targetJar);
                    if (remoteMod > 0 && !targetJar.setLastModified(remoteMod)) {
                        // Timestamp update is best-effort
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

    private HttpURLConnection openMofConnection(String urlStr, SSLSocketFactory sslFactory, int connTimeout, int readTimeout) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection && sslFactory != null) {
            HttpsURLConnection https = (HttpsURLConnection) conn;
            https.setSSLSocketFactory(sslFactory);
            https.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
        }
        conn.setConnectTimeout(connTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setUseCaches(false);
        conn.setRequestProperty(HEADER_USER_AGENT, "JNLP/1.7.0 javaws/1.8.0_402 (b06) Java/1.8.0_402");
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

    private void launchAsycudaJvm(MainActivity activity) {
        File logFile;
        Process proc;
        synchronized (JVM_LAUNCH_LOCK) {
            try {
                if (sJavaProcess != null && isProcessAlive(sJavaProcess)) {
                    hideSplashDelayed(400);
                    return;
                }

                File filesDir = new File("/data/data/" + activity.getPackageName() + "/files");
                if (!filesDir.exists()) filesDir = activity.getFilesDir();

                // Pre-populate usr/glibc/etc/hosts with resolved IP for asy.mof.gov.iq
                refreshGlibcHostsCache(filesDir);

                File clientDir = new File(filesDir, DIR_CLIENT);
                File jreDir = new File(filesDir, "jre8");
                File javaBin = new File(jreDir, "jre/bin/java");
                if (!javaBin.exists() || javaBin.length() == 0) {
                    javaBin = new File(jreDir, "bin/java");
                }
                setExecutableOwnerOnly(javaBin);

                File ldLinux = new File(filesDir, "usr/glibc/lib/ld-linux-aarch64.so.1");
                setExecutableOwnerOnly(ldLinux);

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

                ensureDir(new File(DOWNLOAD_FILES_PATH));
                ensureDir(new File(DOWNLOAD_PRINTS_PATH));

                // Keep user.home on fast internal flash (/data/data/iq.asycuda/files/home)
                // so ASYCUDA's .SOClassCache (178 MB of small DB files) avoids Android's slow FUSE layer
                File fastInternalHome = new File(filesDir, "home");
                ensureDir(fastInternalHome);

                String cpVal = "jars/*";
                File cpFile = new File(clientDir, FILE_CLASSPATH);
                if (cpFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(cpFile))) {
                        String line = br.readLine();
                        if (line != null && !line.trim().isEmpty()) {
                            cpVal = line.trim().replace("/data/data/com.termux/files/home/asycuda_iraq/", "");
                        }
                    } catch (Exception ignored) {
                        // Use wildcard classpath fallback
                    }
                }

                List<String> cmd = new ArrayList<>();
                cmd.add(javaBin.getAbsolutePath());
                cmd.add("-Dasycuda.running=1");
                cmd.add("-Dasycuda.bridge.token=" + BRIDGE_AUTH_TOKEN);
                cmd.add("-Xbootclasspath/p:" + new File(clientDir, BOOTPATCH_JAR_NAME).getAbsolutePath());
                cmd.add("-Xms256m");
                cmd.add("-Xmx1280m");
                cmd.add("-Djava.io.tmpdir=" + tmpDir.getAbsolutePath());
                cmd.add("-Dasycuda.prints.dir=" + new File(filesDir, DIR_PRINTS).getAbsolutePath());
                cmd.add("-Djava.net.preferIPv4Stack=true");
                cmd.add("-Dsun.net.inetaddr.ttl=-1");
                cmd.add("-Dnetworkaddress.cache.ttl=-1");
                cmd.add("-Dhttp.keepAlive=true");
                cmd.add("-Dhttp.maxConnections=32");
                cmd.add("-Dsun.rmi.transport.tcp.responseTimeout=120000");
                cmd.add("-Djava.rmi.server.hostname=127.0.0.1");
                cmd.add("-Duser.home=" + fastInternalHome.getAbsolutePath());
                cmd.add("-Djava.util.logging.manager=so.util.logging.SOLogManager");
                cmd.add("-cp");
                cmd.add(cpVal);
                cmd.add("asycuda.AsycudaBoot");
                cmd.add("https://asy.mof.gov.iq/aw/help/");
                cmd.add("//asy.mof.gov.iq:2006/");
                cmd.add("-dout");

                logFile = new File(clientDir, FILE_ASYCUDA_LOG);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(clientDir);
                pb.redirectErrorStream(true);
                pb.redirectOutput(logFile);

                Map<String, String> env = pb.environment();
                env.remove("LD_PRELOAD");
                env.remove("LD_LIBRARY_PATH");
                env.put("JAVA_HOME", new File(jreDir, "jre").getAbsolutePath());
                env.put("HOME", fastInternalHome.getAbsolutePath());
                env.put("PREFIX", new File(filesDir, "usr").getAbsolutePath());
                env.put("TMPDIR", new File(filesDir, "usr/tmp").getAbsolutePath());
                env.put("DISPLAY", ":0");

                proc = pb.start();
                sJavaProcess = proc;
            } catch (Exception t) {
                updateStatus("Failed to launch JVM: " + t.getMessage());
                return;
            }
        }

        try {
            Thread.sleep(2200);
            if (!isProcessAlive(proc)) {
                int code = proc.exitValue();
                String errTail = readTail(logFile, 400);
                updateStatus("JVM exited (code " + code + "):\n" + errTail);
            } else {
                hideSplashDelayed(600);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception t) {
            updateStatus("JVM status error: " + t.getMessage());
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
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            while ((entry = tis.getNextTarEntry()) != null) {
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
                    try {
                        Os.symlink(entry.getLinkName(), outFile.getAbsolutePath());
                    } catch (Exception ignored) {
                        // Ignore symlink error if already linked
                    }
                } else if (entry.isLink()) {
                    File parent = outFile.getParentFile();
                    if (parent != null) ensureDir(parent);
                    deleteQuietly(outFile);
                    File target = new File(destDir, entry.getLinkName());
                    if (target.getCanonicalPath().startsWith(destCanonPrefix) && target.exists()) {
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
                    if ((mode & 0111) != 0 || outFile.getName().contains(".so") ||
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

    private void handleBridgeMessage(String rawMsg) {
        MainActivity act = sCurrentActivity;
        if (act == null || rawMsg == null) return;

        String authPrefix = "AUTH|" + BRIDGE_AUTH_TOKEN + "|";
        if (!rawMsg.startsWith(authPrefix)) {
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
        } else if (msg.startsWith("SOCLASS_UPDATE|")) {
            UI.post(() -> {
                Toast.makeText(act, AsycudaMaterialUi.tr(act,
                        "ASYCUDA Server requested a SOClass update. Launching Update Center...",
                        "طلب خادم أسيكودا تحديث ملفات النظام (SOClass). جاري فتح نافذة التحديث...",
                        "ڕاژەی ئەسیکۆدا داوای نوێکردنەوەی SOClass دەکات. کردنەوەی پەنجەرەی نوێکردنەوە..."),
                        Toast.LENGTH_LONG).show();
                runUnifiedUpdateCheckModal(act);
            });
        }
    }

    private File resolveSafeBridgeFile(Context ctx, String requestedPath) {
        if (requestedPath == null || requestedPath.isEmpty()) return null;
        String baseName = new File(requestedPath).getName().replaceAll(REGEX_UNSAFE_FILENAME, "_");
        if (baseName.isEmpty() || ".".equals(baseName) || "..".equals(baseName)) return null;
        File[] allowedDirs = new File[] {
            new File(ctx.getFilesDir(), DIR_PRINTS),
            new File(DOWNLOAD_PRINTS_PATH),
            new File(DOWNLOAD_FILES_PATH)
        };
        for (File dir : allowedDirs) {
            File candidate = new File(dir, baseName);
            if (candidate.exists() && candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isAllowedBridgeUrl(String url) {
        try {
            Uri u = Uri.parse(url);
            String scheme = u.getScheme();
            return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme);
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
        File internalPrints = new File(activity.getFilesDir(), DIR_PRINTS);
        File pubPrints = new File(DOWNLOAD_PRINTS_PATH);
        File pubFiles = new File(DOWNLOAD_FILES_PATH);

        for (File f : filesToDelete) {
            if (f == null) continue;
            String name = f.getName();
            boolean ok = f.delete();
            // Also remove mirrored copies with the same name so they don't reappear
            deleteQuietly(new File(internalPrints, name));
            deleteQuietly(new File(pubPrints, name));
            deleteQuietly(new File(pubFiles, name));
            if (ok) deleted++;
        }

        Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                "Deleted " + Math.max(1, deleted) + " file(s)",
                "تم حذف " + Math.max(1, deleted) + " ملف",
                Math.max(1, deleted) + " پەڕگە سڕایەوە"), Toast.LENGTH_SHORT).show();

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
        int hp = dp(activity, 13);
        int vp = dp(activity, 7);
        pill.setPadding(hp, vp, hp, vp);
        pill.setAlpha(0.90f);
        pill.setElevation(dp(activity, 12));

        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setColor(Color.parseColor("#E61C1C1E"));
        pillBg.setCornerRadius(dp(activity, 20));
        pillBg.setStroke(Math.max(1, dp(activity, 1)), Color.parseColor("#FF38383A"));
        pill.setBackground(pillBg);

        ImageView dot = new ImageView(activity);
        dot.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(activity, 18), dp(activity, 18));
        dotLp.rightMargin = dp(activity, 7);
        pill.addView(dot, dotLp);

        TextView label = new TextView(activity);
        label.setText("ASYCUDA");
        label.setTextColor(Color.WHITE);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
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
                List<File> rawFiles = new ArrayList<>();
                collectFiles(new File(activity.getFilesDir(), DIR_PRINTS), rawFiles);
                collectFiles(new File(DOWNLOAD_FILES_PATH), rawFiles);
                collectFiles(new File(DOWNLOAD_PRINTS_PATH), rawFiles);
                rawFiles.sort(Comparator.comparingLong(File::lastModified).reversed());

                // Deduplicate by filename so mirrored files appear once
                List<File> uniqueFiles = new ArrayList<>();
                Set<String> seenNames = new LinkedHashSet<>();
                for (File f : rawFiles) {
                    if (seenNames.add(f.getName())) {
                        uniqueFiles.add(f);
                    }
                }
                AsycudaMaterialUi.showFilesModal(activity, uniqueFiles, this);
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
            if (sJavaProcess != null) {
                sJavaProcess.destroyForcibly();
                sJavaProcess = null;
            }
            launchAsycudaJvm(activity);
        }, "Asycuda-SessionRestart").start();
    }

    private void runUnifiedUpdateCheckModal(MainActivity activity) {
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

                    if (apkUrl != null && isRemoteVersionNewer(tagName, currentVer)) {
                        appUpdateAvailable = true;
                        final String finalApkUrl = apkUrl;
                        final long finalApkSize = apkSize;
                        final String finalTag = tagName;
                        final String finalSha256 = apkSha256;
                        String shortNotes = body.length() > 180 ? body.substring(0, 180).trim() + "..." : body.trim();
                        ctrl.showAppUpdateAvailable(tagName, shortNotes, () ->
                            new Thread(() -> downloadAndInstallApkUpdate(activity, finalApkUrl, finalApkSize, finalTag, finalSha256, ctrl),
                                    "Asycuda-ApkDownloader").start()
                        );
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
                ctrl.onCheckFinished(updatedJars, appUpdateAvailable, () -> restartAsycudaSession(activity));
            } finally {
                sSyncInProgress.set(false);
            }
        }, "Asycuda-UnifiedUpdater").start();
    }

    private void downloadAndInstallApkUpdate(MainActivity activity, String apkUrl, long expectedSize,
                                             String versionTag, String expectedSha256,
                                             AsycudaMaterialUi.UpdateCenterController ctrl) {
        HttpURLConnection conn = null;
        try {
            File printsDir = new File(activity.getFilesDir(), DIR_PRINTS);
            ensureDir(printsDir);
            File outApk = new File(printsDir, "ASYCUDA_Iraq_" + versionTag.replaceAll(REGEX_UNSAFE_FILENAME, "") + ".apk");

            conn = (HttpURLConnection) new URL(apkUrl).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty(HEADER_USER_AGENT, "ASYCUDA-Iraq-Android-Updater");

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

            // 1. Verify SHA-256 integrity against GitHub Release asset digest when present
            if (expectedSha256 != null && !expectedSha256.isEmpty()) {
                byte[] actualBytes = sha256Digest.digest();
                StringBuilder hex = new StringBuilder();
                for (byte b : actualBytes) hex.append(String.format(Locale.US, "%02x", b & 0xFF));
                if (!hex.toString().equalsIgnoreCase(expectedSha256.trim())) {
                    deleteQuietly(outApk);
                    ctrl.setAppUpdateStatus("Security error: APK SHA-256 checksum mismatch", -1, false);
                    return;
                }
            }

            // 2. Verify APK signing certificate matches currently installed application
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
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(install);
                } catch (Exception t) {
                    Toast.makeText(activity, "Installer error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception t) {
            ctrl.setAppUpdateStatus("Download failed: " + t.getMessage(), -1, false);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    private boolean verifyDownloadedApkSignature(Context ctx, File apkFile) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo archiveInfo = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_SIGNATURES);
            if (archiveInfo == null || !ctx.getPackageName().equals(archiveInfo.packageName)) {
                return false;
            }
            PackageInfo installedInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] archiveSigs = archiveInfo.signatures;
            Signature[] installedSigs = installedInfo.signatures;
            if (archiveSigs == null || installedSigs == null || archiveSigs.length == 0 || installedSigs.length == 0) {
                return false;
            }
            return archiveSigs[0].equals(installedSigs[0]);
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
                        File capFile = new File(cacheDiagDir, "screen_capture.jpg");
                        try (FileOutputStream fos = new FileOutputStream(capFile)) {
                            bmp.compress(Bitmap.CompressFormat.JPEG, 88, fos);
                            fos.getFD().sync();
                        }
                        setReadableOwnerOnly(capFile);
                        controller.onImageAttached(capFile, "screen_capture.jpg");
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
                new File(filesDir, DIR_PRINTS), new File(DOWNLOAD_PRINTS_PATH));

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
            addFileToZipIfExists(zos, new File(clientDir, "client.log"), "client.log", true);

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
     * before any log text is written to a diagnostic report.
     */
    @SuppressWarnings({"java:S5852", "java:S5843", "java:S5998", "java:S6353"})
    private static String redactSensitiveLogContent(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        String redacted = raw.replace(BRIDGE_AUTH_TOKEN, "[REDACTED_BRIDGE_TOKEN]");
        // Scrub URL user:password@host credentials
        redacted = redacted.replaceAll("(?i)(https?+://)[^/\\s:@]++:[^/\\s@]++@", "$1[REDACTED_AUTH]@");
        // Scrub XML/SOAP tags that could hold credentials, usernames, TINs, or tokens
        redacted = redacted.replaceAll(
                "(?is)<(password|passwd|pwd|user|username|login|secret|token|jsessionid|sessionid|tin|declarant|credential|pin|otp)[^>]*+>.*?</\\1>",
                "<$1>[REDACTED]</$1>");
        // Scrub key=value / key: value / JSON "key": "value" patterns for sensitive keys
        redacted = redacted.replaceAll(
                "(?i)([\"']?+(?:password|passwd|pwd|pass|username|user_name|userid|login|declarant|tin|tax_id|secret|token|access_token|refresh_token|jsessionid|sessionid|session_id|cookie|set-cookie|authorization|bearer|api_key|apikey|pin|otp|credential|private_key)[\"']?+\\s*+[:=]\\s*+)[^\\s,;\"'&<>\\]}]++",
                "$1\"[REDACTED]\"");
        return redacted;
    }

    /**
     * Records a tamper-evident cloud consent audit event in Sentry when a user accepts the
     * Terms & Conditions and Liability Policy on initial launch.
     */
    private void uploadConsentReceiptToSentry(Context ctx, JSONObject receipt) {
        if (ctx == null || receipt == null) return;
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String dsn = BuildConfig.SENTRY_DSN;
                if (dsn == null || dsn.isEmpty()) return;
                SentryUriParts parts = parseSentryDsn(dsn);
                if (parts == null) return;

                String eventId = UUID.randomUUID().toString().replace("-", "");
                SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                String nowIso = isoFmt.format(new Date());

                String consentId = receipt.optString(KEY_CONSENT_ID, "IRQ-UNKNOWN");
                String sigName = receipt.optString(KEY_SIGNATORY_NAME, "Unknown");
                String compName = receipt.optString(KEY_COMPANY_NAME, "Unknown");
                String deviceHash = receipt.optString(KEY_DEVICE_HASH, "unknown_device");
                String policyVer = receipt.optString(KEY_POLICY_VERSION, AsycudaMaterialUi.CURRENT_POLICY_VERSION);
                String appVer = receipt.optString(KEY_APP_VERSION, AsycudaMaterialUi.getAppVersionLabel(ctx));
                String devModel = receipt.optString(KEY_DEVICE_MODEL, Build.MANUFACTURER + " " + Build.MODEL);
                String sha256 = receipt.optString(KEY_SHA256_SIG, "");

                JSONObject envHeader = new JSONObject();
                envHeader.put(KEY_EVENT_ID, eventId);
                envHeader.put("sent_at", nowIso);
                envHeader.put("dsn", dsn);

                JSONObject eventObj = new JSONObject();
                eventObj.put(KEY_EVENT_ID, eventId);
                eventObj.put("timestamp", nowIso);
                eventObj.put("platform", "java");
                eventObj.put("level", "info");
                eventObj.put("release", "iq.asycuda@" + appVer.replaceFirst("^v", "") + "+38");
                eventObj.put("environment", "production");

                JSONArray fingerprint = new JSONArray();
                fingerprint.put("terms-accepted");
                fingerprint.put(deviceHash);
                eventObj.put("fingerprint", fingerprint);

                JSONObject msgObj = new JSONObject();
                msgObj.put("formatted", "[Terms Accepted] " + sigName + " (" + compName + ") • Receipt #"
                        + consentId + " • " + devModel);
                eventObj.put("message", msgObj);

                JSONObject userObj = new JSONObject();
                userObj.put("id", deviceHash);
                userObj.put("username", sigName + " (" + compName + ")");
                userObj.put("ip_address", "{{auto}}");
                eventObj.put("user", userObj);

                JSONObject tags = new JSONObject();
                tags.put("audit_category", "terms_accepted");
                tags.put(KEY_SIGNATORY_NAME, sigName);
                tags.put(KEY_COMPANY_NAME, compName);
                tags.put(KEY_CONSENT_ID, consentId);
                tags.put(KEY_POLICY_VERSION, policyVer);
                tags.put(KEY_APP_VERSION, appVer);
                tags.put(KEY_DEVICE_MODEL, devModel);
                tags.put(KEY_DEVICE_HASH, deviceHash);
                tags.put("locale", receipt.optString("locale", "ar"));
                eventObj.put("tags", tags);

                eventObj.put("contexts", buildSentryContexts(ctx, appVer));

                JSONObject extra = new JSONObject();
                extra.put("consent_receipt", receipt.toString());
                extra.put(KEY_SIGNATORY_NAME, sigName);
                extra.put(KEY_COMPANY_NAME, compName);
                extra.put(KEY_SHA256_SIG, sha256);
                eventObj.put("extra", extra);

                byte[] eventBytes = eventObj.toString().getBytes(StandardCharsets.UTF_8);
                JSONObject itemHeader = new JSONObject();
                itemHeader.put("type", "event");
                itemHeader.put(KEY_LENGTH, eventBytes.length);
                itemHeader.put(KEY_CONTENT_TYPE, MIME_JSON);

                byte[] receiptBytes = receipt.toString(2).getBytes(StandardCharsets.UTF_8);
                JSONObject attHeader = new JSONObject();
                attHeader.put("type", TYPE_ATTACHMENT);
                attHeader.put(KEY_LENGTH, receiptBytes.length);
                attHeader.put(KEY_FILENAME, FILE_CONSENT_RECEIPT);
                attHeader.put(KEY_CONTENT_TYPE, MIME_JSON);
                attHeader.put(KEY_ATTACHMENT_TYPE, VAL_EVENT_ATTACHMENT);

                ByteArrayOutputStream envelope = new ByteArrayOutputStream();
                envelope.write((envHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                envelope.write((itemHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                envelope.write(eventBytes);
                envelope.write('\n');
                envelope.write((attHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                envelope.write(receiptBytes);
                envelope.write('\n');

                String endpoint = "https://" + parts.host + "/api/" + parts.projectId
                        + "/envelope/?sentry_key=" + parts.publicKey + "&sentry_version=7&sentry_client=asycuda.android/3.8.0";
                conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(20000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-sentry-envelope");
                byte[] payload = envelope.toByteArray();
                conn.setFixedLengthStreamingMode(payload.length);
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(payload);
                    out.flush();
                }
                int code = conn.getResponseCode();
                if (code >= 200 && code < 300) {
                    AsycudaMaterialUi.markTermsCloudSynced(ctx);
                }
            } catch (Exception ignored) {
                // Consent receipt will retry on next launch if offline
            } finally {
                if (conn != null) conn.disconnect();
            }
        }, "Asycuda-ConsentAudit").start();
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

            String eventId = UUID.randomUUID().toString().replace("-", "");
            SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            String nowIso = isoFmt.format(new Date());

            String verLabel = AsycudaMaterialUi.getAppVersionLabel(ctx);
            String firstLine = description.split("\n")[0].trim();
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
            eventObj.put("release", "iq.asycuda@" + verLabel.replaceFirst("^v", "") + "+38");
            eventObj.put("environment", "production");

            JSONObject msgObj = new JSONObject();
            msgObj.put("formatted", "[User Report] " + firstLine + "\n\n" + description);
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
            extra.put("full_description", description);
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
            fbObj.put("comments", description);
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

    private static void setExecutableOwnerOnly(File f) {
        if (f != null && f.exists() && !f.setExecutable(true, true)) {
            // Ignore if filesystem does not support POSIX permissions
        }
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
