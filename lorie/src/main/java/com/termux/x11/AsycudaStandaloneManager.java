package com.termux.x11;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AsycudaStandaloneManager {

    private static final String MOF_CODEBASE_URL = "https://asy.mof.gov.iq/aw/";
    private static final String MOF_JNLP_URL     = "https://asy.mof.gov.iq/aw/AWIQWorld.jnlp";
    private static final String GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/MohammedALShimmary/ASYCUDA-Iraq-Android/releases/latest";
    public static final int REQ_PICK_FEEDBACK_IMAGE = 47892;

    private static final Handler UI = new Handler(Looper.getMainLooper());
    private static volatile AsycudaStandaloneManager sInstance = null;
    private static volatile boolean sExtractingOrBooting = false;
    private static final AtomicBoolean sSyncInProgress = new AtomicBoolean(false);
    private static volatile boolean sServerStarted = false;
    private static volatile CmdEntryPoint sCmdEntry = null;
    private static volatile Process sJavaProcess = null;
    private static volatile MainActivity sCurrentActivity = null;
    private static volatile AsycudaMaterialUi.FeedbackModalController sActiveFeedbackController = null;

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
                } catch (Throwable ignored) {}
                try {
                    NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (nm != null) nm.cancelAll();
                } catch (Throwable ignored) {}
            }
            if (sJavaProcess != null) {
                try {
                    sJavaProcess.destroyForcibly();
                } catch (Throwable ignored) {}
                sJavaProcess = null;
            }
            for (MainActivity act : MainActivity.getInstances()) {
                try {
                    act.finishAndRemoveTask();
                } catch (Throwable ignored) {}
                try {
                    act.finishAffinity();
                } catch (Throwable ignored) {}
            }
            if (sCurrentActivity != null) {
                try {
                    sCurrentActivity.finishAndRemoveTask();
                } catch (Throwable ignored) {}
                try {
                    sCurrentActivity.finishAffinity();
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {
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
                File printsDir = new File(activity.getFilesDir(), "prints");
                printsDir.mkdirs();
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
            } catch (Throwable t) {
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
        } catch (Throwable ignored) {}
        return "screenshot.png";
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
                    .putString("backButtonAction", "toggle soft keyboard")
                    .putBoolean("adjustHeightForEK", false)
                    .putString("notificationTapAction", "open preferences")
                    .putBoolean("showAdditionalKbd", true)
                    .putBoolean("stylusButtonContactModifierMode", false)
                    .putBoolean("additionalKbdVisible", true)
                    .putInt("opacityEKBar", 8)
                    .putString("displayResolutionCustom", "2144x1103")
                    .putBoolean("stylusIsMouse", false)
                    .putBoolean("clipboardEnable", true)
                    .putBoolean("pauseKeyInterceptingWithEsc", false)
                    .putString("mediaKeysAction", "no action")
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
                    .putString("notificationButton0Action", "open preferences")
                    .putString("volumeUpAction", "no action")
                    .putBoolean("fullscreen", false)
                    .putBoolean("Reseed", false)
                    .putBoolean("PIP", false)
                    .putInt("capturedPointerSpeedFactor", 100)
                    .putString("displayResolutionMode", "scaled")
                    .putString("swipeUpAction", "no action")
                    .putBoolean("filterOutWinkey", false)
                    .putInt("displayScale", 120)
                    .putString("volumeDownAction", "no action")
                    .putBoolean("showStylusClickOverride", false)
                    .putString("screenIdleTimeout", "never")
                    .apply();
                activity.toggleExtraKeys(true, true);
                prefsMarker.createNewFile();
            } catch (Throwable ignored) {}
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
                } catch (Throwable ignored) {}
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
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
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
        } catch (Throwable ignored) {}
    }

    private void bootstrapEnvironmentAndStart(MainActivity activity) {
        try {
            startKeepAliveService(activity);

            File filesDir = activity.getFilesDir();
            File clientDir = new File(filesDir, "client");
            File usrTmpDir = new File(filesDir, "usr/tmp");
            File x11UnixDir = new File(usrTmpDir, ".X11-unix");
            File homeDir = new File(filesDir, "home");
            File printsDir = new File(filesDir, "prints");
            File runtimeMarker = new File(filesDir, ".extracted_v3_2");
            File bootPatchMarker = new File(filesDir, ".bootpatch_v3_7_0");

            if (!runtimeMarker.exists()) {
                deleteRecursive(new File(filesDir, "usr"));
                deleteRecursive(new File(filesDir, "jre8"));
                new File(filesDir, ".extracted_v3").delete();
                new File(filesDir, ".extracted_v3_1").delete();

                updateStatus("Preparing Java 8 runtime environment (1/2)...");
                extractTarXzAsset(activity, "runtime-arm64.tar.xz", filesDir);

                if (!new File(clientDir, "00_bootpatch.jar").exists()) {
                    updateStatus("Extracting ASYCUDA World Iraq client packages (2/2)...");
                    clientDir.mkdirs();
                    extractTarXzAsset(activity, "asycuda-iraq.tar.xz", clientDir);
                }

                setPermissionsRecursive(new File(filesDir, "usr/glibc/lib"));
                setPermissionsRecursive(new File(filesDir, "jre8"));

                File fontFallback = new File(filesDir, "jre8/jre/lib/fonts/fallback");
                fontFallback.mkdirs();
                File sysFonts = new File("/system/fonts");
                File[] ttfList = sysFonts.listFiles();
                if (ttfList != null) {
                    for (File ttf : ttfList) {
                        if (ttf.getName().endsWith(".ttf")) {
                            File link = new File(fontFallback, ttf.getName());
                            try {
                                if (!link.exists()) Os.symlink(ttf.getAbsolutePath(), link.getAbsolutePath());
                            } catch (Throwable ignored) {}
                        }
                    }
                }
                runtimeMarker.createNewFile();
                bootPatchMarker.createNewFile();
            } else if (!bootPatchMarker.exists()) {
                updateStatus("Applying v3.7.0 high-speed network & print components...");
                extractBootJarsOnly(activity, "asycuda-iraq.tar.xz", clientDir);
                bootPatchMarker.createNewFile();
            }

            // Migrate .SOClassCache from slow external FUSE (/storage/emulated/0) to fast internal flash (files/home)
            migrateSoClassCacheToFastStorage(homeDir);

            // Smart Startup Sync: Check MOF Server (asy.mof.gov.iq)
            syncGovernmentJars(clientDir, false, null);

            x11UnixDir.mkdirs();
            homeDir.mkdirs();
            printsDir.mkdirs();
            try {
                Os.chmod(usrTmpDir.getAbsolutePath(), 0777);
                Os.chmod(x11UnixDir.getAbsolutePath(), 0777);
            } catch (Throwable ignored) {}

            new File(usrTmpDir, ".X0-lock").delete();
            new File(x11UnixDir, "X0").delete();

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
                } catch (Throwable t) {
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

        } catch (Throwable t) {
            updateStatus("Startup error: " + t.getMessage());
        } finally {
            sExtractingOrBooting = false;
        }
    }

    /**
     * Moves .SOClassCache (178 MB of reference tables) from external FUSE storage
     * (/storage/emulated/0/Download/Asycuda_Files/.SOClassCache) to internal ext4/f2fs flash
     * (/data/data/iq.asycuda/files/home/.SOClassCache) for 50x faster document/window loading.
     */
    private void migrateSoClassCacheToFastStorage(File internalHome) {
        try {
            File extCache = new File("/storage/emulated/0/Download/Asycuda_Files/.SOClassCache");
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
        } catch (Throwable ignored) {}
    }

    private void copyDirectoryRecursive(File src, File dst) {
        if (src == null || !src.exists()) return;
        if (src.isDirectory()) {
            dst.mkdirs();
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
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Pre-resolves asy.mof.gov.iq via Android system DNS and writes to usr/glibc/etc/hosts
     * so glibc inside the JVM never stalls on DNS queries.
     */
    private void refreshGlibcHostsCache(File filesDir) {
        try {
            File hostsFile = new File(filesDir, "usr/glibc/etc/hosts");
            if (hostsFile.getParentFile() != null) hostsFile.getParentFile().mkdirs();
            StringBuilder sb = new StringBuilder();
            sb.append("127.0.0.1 localhost\n");
            sb.append("::1 localhost\n");
            try {
                InetAddress mofAddr = InetAddress.getByName("asy.mof.gov.iq");
                if (mofAddr != null && mofAddr.getHostAddress() != null) {
                    sb.append(mofAddr.getHostAddress()).append(" asy.mof.gov.iq\n");
                }
            } catch (Throwable ignored) {}
            try (FileOutputStream fos = new FileOutputStream(hostsFile)) {
                fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            }
            hostsFile.setReadable(true, false);
        } catch (Throwable ignored) {}
    }

    private void extractBootJarsOnly(Context ctx, String assetName, File destDir) {
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            int found = 0;
            while ((entry = tis.getNextTarEntry()) != null) {
                String name = entry.getName();
                if ("jars/00_boot.jar".equals(name) || "00_bootpatch.jar".equals(name) || "jars/client.jar".equals(name)) {
                    File outFile = new File(destDir, name);
                    File parent = outFile.getParentFile();
                    if (parent != null) parent.mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int n;
                        while ((n = tis.read(buf)) != -1) fos.write(buf, 0, n);
                        fos.getFD().sync();
                    }
                    outFile.setReadable(true, false);
                    found++;
                    if (found >= 3) break;
                }
            }
        } catch (Throwable ignored) {}
    }

    /**
     * Smart Government (MOF) JNLP & JAR Updater.
     * Supports both startup verification and fast parallel verification for the Update Center modal.
     */
    private int syncGovernmentJars(File clientDir, boolean manualForceCheck, AsycudaMaterialUi.UpdateCenterController modalCtrl) {
        int updatedCount = 0;
        try {
            updateStatus("Connecting to MOF Server (asy.mof.gov.iq) to verify JARs...");
            if (modalCtrl != null) {
                modalCtrl.setMofUpdateStatus("Connecting to asy.mof.gov.iq...", "Fetching AWIQWorld.jnlp", 0, 100, true);
            }
            if (!manualForceCheck) {
                Thread.sleep(300);
            }
            SSLSocketFactory sslFactory = createMofTrustAllSslFactory();

            int connTimeout = manualForceCheck ? 8000 : 3500;
            int readTimeout = manualForceCheck ? 15000 : 6000;

            HttpURLConnection jnlpConn = openMofConnection(MOF_JNLP_URL, sslFactory, connTimeout, readTimeout);
            jnlpConn.setRequestMethod("GET");
            int code = jnlpConn.getResponseCode();
            if (code != 200) {
                jnlpConn.disconnect();
                updateStatus("MOF Server returned HTTP " + code + " • Using cached JARs");
                if (modalCtrl != null) {
                    modalCtrl.setMofUpdateStatus("MOF server returned HTTP " + code + " • Using cached packages", "Offline / Cached", 0, 0, false);
                }
                Thread.sleep(400);
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

            File localJnlp = new File(clientDir, "AWIQWorld.jnlp");
            try (FileOutputStream fos = new FileOutputStream(localJnlp)) {
                fos.write(jnlpXml.getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Throwable ignored) {}

            Pattern jarPattern = Pattern.compile("<jar\\s+[^>]*href\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
            Matcher m = jarPattern.matcher(jnlpXml);
            List<String> remoteHrefs = new ArrayList<>();
            while (m.find()) {
                String href = m.group(1).trim();
                if (!href.isEmpty()) {
                    remoteHrefs.add(href);
                }
            }

            if (remoteHrefs.isEmpty()) {
                return 0;
            }

            File jarsDir = new File(clientDir, "jars");
            jarsDir.mkdirs();

            Set<String> cpEntries = new LinkedHashSet<>();
            cpEntries.add("jars/00_boot.jar");
            cpEntries.add("jars/00_jnlp.jar");

            int total = remoteHrefs.size();
            List<String> toDownloadUrls = new ArrayList<>();
            List<File> toDownloadFiles = new ArrayList<>();

            if (manualForceCheck) {
                // Fast parallel HEAD verification (8 threads) so 131 JARs verify in ~3 seconds
                ExecutorService pool = Executors.newFixedThreadPool(8);
                AtomicInteger checkedCounter = new AtomicInteger(0);
                List<Future<Boolean>> futures = new ArrayList<>();

                for (int i = 0; i < total; i++) {
                    final String href = remoteHrefs.get(i);
                    final String flatName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                    if (flatName.equals("00_boot.jar") || flatName.equals("00_jnlp.jar")) {
                        checkedCounter.incrementAndGet();
                        futures.add(null);
                        continue;
                    }
                    cpEntries.add("jars/" + flatName);
                    final File targetJar = new File(jarsDir, flatName);
                    final String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));

                    futures.add(pool.submit(() -> {
                        boolean needsDl = !targetJar.exists() || targetJar.length() == 0;
                        if (!needsDl) {
                            HttpURLConnection head = null;
                            try {
                                head = openMofConnection(jarUrl, sslFactory, 4000, 4000);
                                head.setRequestMethod("HEAD");
                                if (head.getResponseCode() == 200) {
                                    long remoteLen = head.getContentLengthLong();
                                    if (remoteLen > 0 && remoteLen != targetJar.length()) {
                                        needsDl = true;
                                    }
                                }
                            } catch (Throwable ignored) {
                            } finally {
                                if (head != null) head.disconnect();
                            }
                        }
                        int done = checkedCounter.incrementAndGet();
                        if (modalCtrl != null) {
                            modalCtrl.setMofUpdateStatus(
                                    "Verifying MOF packages (" + done + " / " + total + ")...",
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
                    String flatName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                    String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));
                    File targetJar = new File(jarsDir, flatName);
                    try {
                        if (Boolean.TRUE.equals(fut.get())) {
                            toDownloadUrls.add(jarUrl);
                            toDownloadFiles.add(targetJar);
                        }
                    } catch (Throwable ignored) {}
                }
            } else {
                for (int i = 0; i < total; i++) {
                    String href = remoteHrefs.get(i);
                    String flatName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                    if (flatName.equals("00_boot.jar") || flatName.equals("00_jnlp.jar")) continue;
                    cpEntries.add("jars/" + flatName);
                    File targetJar = new File(jarsDir, flatName);
                    String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));
                    if (!targetJar.exists() || targetJar.length() == 0) {
                        toDownloadUrls.add(jarUrl);
                        toDownloadFiles.add(targetJar);
                    }
                }
            }

            for (int d = 0; d < toDownloadUrls.size(); d++) {
                String jarUrl = toDownloadUrls.get(d);
                File targetJar = toDownloadFiles.get(d);
                String msg = "Downloading update: " + targetJar.getName() + " (" + (d + 1) + "/" + toDownloadUrls.size() + ")...";
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
            File cpFile = new File(clientDir, "classpath.txt");
            try (FileOutputStream fos = new FileOutputStream(cpFile)) {
                fos.write(cpBuilder.toString().getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Throwable ignored) {}

            if (modalCtrl != null) {
                if (updatedCount > 0) {
                    modalCtrl.setMofUpdateStatus("✓ Downloaded " + updatedCount + " updated MOF package(s)",
                            total + " total packages verified", total, total, false);
                } else {
                    modalCtrl.setMofUpdateStatus("✓ All " + total + " MOF customs packages are up to date",
                            "Verified against asy.mof.gov.iq", 0, 0, false);
                }
            } else if (!manualForceCheck) {
                if (updatedCount > 0) {
                    updateStatus("✓ Downloaded " + updatedCount + " new Government JAR(s) from MOF!");
                } else {
                    updateStatus("✓ MOF Government JARs Verified (" + total + " packages up to date)");
                }
                Thread.sleep(500);
            }

        } catch (Throwable ignored) {
            try {
                int cached = new File(clientDir, "jars").list().length;
                updateStatus("✓ Offline Mode: Using " + cached + " cached MOF JARs");
                if (modalCtrl != null) {
                    modalCtrl.setMofUpdateStatus("MOF server unreachable • Using " + cached + " cached packages",
                            "Check internet or VPN connection", 0, 0, false);
                }
                Thread.sleep(400);
            } catch (Throwable ignored2) {}
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
                if (targetJar.exists()) targetJar.delete();
                if (tmpJar.renameTo(targetJar)) {
                    targetJar.setReadable(true, false);
                    if (remoteMod > 0) targetJar.setLastModified(remoteMod);
                    return true;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (conn != null) conn.disconnect();
            if (tmpJar.exists()) tmpJar.delete();
        }
        return false;
    }

    private HttpURLConnection openMofConnection(String urlStr, SSLSocketFactory sslFactory, int connTimeout, int readTimeout) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection && sslFactory != null) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier((hostname, session) -> true);
        }
        conn.setConnectTimeout(connTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", "JNLP/1.7.0 javaws/1.8.0_402 (b06) Java/1.8.0_402");
        return conn;
    }

    @SuppressLint({"CustomX509TrustManager", "TrustAllX509TrustManager"})
    private SSLSocketFactory createMofTrustAllSslFactory() {
        try {
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new SecureRandom());
            return sc.getSocketFactory();
        } catch (Throwable t) {
            return null;
        }
    }

    private void setPermissionsRecursive(File f) {
        if (f == null || !f.exists()) return;
        f.setReadable(true, false);
        if (f.isDirectory()) {
            f.setExecutable(true, false);
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) setPermissionsRecursive(c);
            }
        } else {
            String name = f.getName();
            String path = f.getAbsolutePath();
            if (name.contains(".so") || path.contains("/bin/") || path.contains("/lib/")) {
                f.setExecutable(true, false);
            }
        }
    }

    private synchronized void launchAsycudaJvm(MainActivity activity) {
        try {
            if (sJavaProcess != null && isProcessAlive(sJavaProcess)) {
                hideSplashDelayed(400);
                return;
            }

            File filesDir = new File("/data/data/" + activity.getPackageName() + "/files");
            if (!filesDir.exists()) filesDir = activity.getFilesDir();

            // Pre-populate usr/glibc/etc/hosts with resolved IP for asy.mof.gov.iq
            refreshGlibcHostsCache(filesDir);

            File clientDir = new File(filesDir, "client");
            File jreDir = new File(filesDir, "jre8");
            File javaBin = new File(jreDir, "jre/bin/java");
            if (!javaBin.exists() || javaBin.length() == 0) {
                javaBin = new File(jreDir, "bin/java");
            }
            javaBin.setExecutable(true, false);

            File ldLinux = new File(filesDir, "usr/glibc/lib/ld-linux-aarch64.so.1");
            ldLinux.setExecutable(true, false);

            File tmpDir = new File(clientDir, "tmp");
            deleteRecursive(tmpDir);
            new File(tmpDir, "soe.tmp").mkdirs();
            new File(tmpDir, "son.tmp").mkdirs();

            File[] locks = clientDir.listFiles();
            if (locks != null) {
                for (File lf : locks) {
                    if (lf.getName().endsWith(".lck")) lf.delete();
                }
            }

            File pubFiles = new File("/storage/emulated/0/Download/Asycuda_Files");
            File pubPrints = new File("/storage/emulated/0/Download/Asycuda_Prints");
            pubFiles.mkdirs();
            pubPrints.mkdirs();

            // CRITICAL SPEED BOOST: Keep user.home on fast internal flash (/data/data/iq.asycuda/files/home)
            // so ASYCUDA's .SOClassCache (178 MB of small DB files) avoids Android's slow FUSE /storage/emulated/0 layer!
            File fastInternalHome = new File(filesDir, "home");
            fastInternalHome.mkdirs();

            String cpVal = "jars/*";
            File cpFile = new File(clientDir, "classpath.txt");
            if (cpFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(cpFile))) {
                    String line = br.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        cpVal = line.trim().replace("/data/data/com.termux/files/home/asycuda_iraq/", "");
                    }
                } catch (Throwable ignored) {}
            }

            List<String> cmd = new ArrayList<>();
            cmd.add(javaBin.getAbsolutePath());
            cmd.add("-Dasycuda.running=1");
            cmd.add("-Xbootclasspath/p:" + new File(clientDir, "00_bootpatch.jar").getAbsolutePath());
            cmd.add("-Xms256m");
            cmd.add("-Xmx1280m");
            cmd.add("-Djava.io.tmpdir=" + tmpDir.getAbsolutePath());
            cmd.add("-Dasycuda.prints.dir=" + new File(filesDir, "prints").getAbsolutePath());
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

            File logFile = new File(clientDir, "asycuda.log");
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

            sJavaProcess = pb.start();

            Thread.sleep(2200);
            if (!isProcessAlive(sJavaProcess)) {
                int code = sJavaProcess.exitValue();
                String errTail = readTail(logFile, 400);
                updateStatus("JVM exited (code " + code + "):\n" + errTail);
            } else {
                hideSplashDelayed(600);
            }
        } catch (Throwable t) {
            updateStatus("Failed to launch JVM: " + t.getMessage());
        }
    }

    private String readTail(File f, int maxChars) {
        if (f == null || !f.exists()) return "No log output";
        try (FileInputStream fis = new FileInputStream(f)) {
            int len = (int) Math.min(f.length(), maxChars);
            if (len <= 0) return "Empty log";
            byte[] b = new byte[len];
            fis.skip(Math.max(0, f.length() - len));
            fis.read(b);
            return new String(b, StandardCharsets.UTF_8).trim();
        } catch (Throwable t) {
            return t.getMessage();
        }
    }

    private void extractTarXzAsset(Context ctx, String assetName, File destDir) throws Exception {
        try (InputStream raw = ctx.getAssets().open(assetName);
             BufferedInputStream bis = new BufferedInputStream(raw, 65536);
             XZInputStream xzis = new XZInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis)) {

            TarArchiveEntry entry;
            byte[] buf = new byte[32768];
            while ((entry = tis.getNextTarEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else if (entry.isSymbolicLink()) {
                    File parent = outFile.getParentFile();
                    if (parent != null) parent.mkdirs();
                    outFile.delete();
                    try {
                        Os.symlink(entry.getLinkName(), outFile.getAbsolutePath());
                    } catch (Throwable ignored) {}
                } else if (entry.isLink()) {
                    File parent = outFile.getParentFile();
                    if (parent != null) parent.mkdirs();
                    outFile.delete();
                    File target = new File(destDir, entry.getLinkName());
                    if (target.exists()) {
                        try (FileInputStream in = new FileInputStream(target);
                             FileOutputStream out = new FileOutputStream(outFile)) {
                            int n;
                            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                            out.getFD().sync();
                        }
                        outFile.setExecutable(true, false);
                    }
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null) parent.mkdirs();
                    if (outFile.exists()) outFile.delete();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int n;
                        while ((n = tis.read(buf)) != -1) {
                            fos.write(buf, 0, n);
                        }
                        fos.getFD().sync();
                    }
                    outFile.setReadable(true, false);
                    int mode = entry.getMode();
                    if ((mode & 0111) != 0 || outFile.getName().contains(".so") ||
                            outFile.getAbsolutePath().contains("/bin/")) {
                        outFile.setExecutable(true, false);
                    }
                }
            }
        }
    }

    private void startBridgeListener() {
        if (sServerStarted) return;
        sServerStarted = true;
        Thread t = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(47891)) {
                while (true) {
                    try (Socket s = ss.accept();
                         BufferedReader br = new BufferedReader(
                                 new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))) {
                        String line = br.readLine();
                        if (line != null) handleBridgeMessage(line.trim());
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
        }, "Asycuda-BridgeListener");
        t.setDaemon(true);
        t.start();
    }

    private void handleBridgeMessage(String msg) {
        MainActivity act = sCurrentActivity;
        if (act == null) return;

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
                } catch (Throwable ignored) {}
            });
        } else if (msg.startsWith("OPEN|")) {
            String[] parts = msg.split("\\|");
            if (parts.length >= 2) {
                File f = new File(parts[1]);
                String mime = parts.length >= 3 ? parts[2] : "application/pdf";
                UI.post(() -> openFileWithProvider(act, f, mime));
            }
        } else if (msg.startsWith("BROWSE|")) {
            String url = msg.substring("BROWSE|".length());
            UI.post(() -> openUrl(act, url));
        }
    }

    private void openFileWithProvider(Context ctx, File f, String mime) {
        try {
            if (!f.exists()) {
                Toast.makeText(ctx, "File not found: " + f.getName(), Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = FileProvider.getUriForFile(ctx, "iq.asycuda.fileprovider", f);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(Intent.createChooser(intent, "Open " + f.getName()));
        } catch (Throwable t) {
            Toast.makeText(ctx, "Could not open file: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareFileWithProvider(Context ctx, File f, String mime) {
        try {
            Uri uri = FileProvider.getUriForFile(ctx, "iq.asycuda.fileprovider", f);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(mime);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(Intent.createChooser(share, "Share " + f.getName()));
        } catch (Throwable t) {
            Toast.makeText(ctx, "Share error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFloatingMenuPill(MainActivity activity, FrameLayout root) {
        if (root.findViewWithTag("asycuda_floating_pill") != null) {
            return;
        }
        LinearLayout pill = new LinearLayout(activity);
        pill.setTag("asycuda_floating_pill");
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER_VERTICAL);
        int hp = dp(activity, 13);
        int vp = dp(activity, 7);
        pill.setPadding(hp, vp, hp, vp);
        pill.setAlpha(0.90f);
        pill.setElevation(dp(activity, 12));

        // Apple iOS/macOS Translucent Dark Capsule
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
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        label.setLetterSpacing(0.04f);
        pill.addView(label);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.topMargin = dp(activity, 14);
        lp.rightMargin = dp(activity, 16);

        pill.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY, downX, downY;
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
                }
                return false;
            }
        });

        root.addView(pill, lp);
    }

    private void openMaterialControlCenter(MainActivity activity) {
        AsycudaMaterialUi.MenuActionCallback cb = new AsycudaMaterialUi.MenuActionCallback() {
            @Override
            public void onToggleAutoKeyboard(boolean newState) {
                if (!newState && activity.getLorieView() != null) {
                    activity.getLorieView().setKeyboardVisible(false);
                }
            }

            @Override
            public void onToggleAssistKeys() {
                activity.prefs.showAdditionalKbd.put(true);
                activity.toggleExtraKeys();
            }

            @Override
            public void onOpenFiles() {
                List<File> allFiles = new ArrayList<>();
                collectFiles(new File(activity.getFilesDir(), "prints"), allFiles);
                collectFiles(new File("/storage/emulated/0/Download/Asycuda_Files"), allFiles);
                if (allFiles.isEmpty()) {
                    collectFiles(new File("/storage/emulated/0/Download/Asycuda_Prints"), allFiles);
                }
                allFiles.sort(Comparator.comparingLong(File::lastModified).reversed());
                AsycudaMaterialUi.showFilesModal(activity, allFiles, this);
            }

            @Override
            public void onOpenDisplaySettings() {
                Intent prefIntent = new Intent(activity, LoriePreferences.class);
                prefIntent.setAction(Intent.ACTION_MAIN);
                activity.startActivity(prefIntent);
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
                    View oldPill = root.findViewWithTag("asycuda_floating_pill");
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
        };

        AsycudaMaterialUi.showControlCenter(activity, cb);
    }

    private void restartAsycudaSession(MainActivity activity) {
        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root != null) {
            setupSplashOverlay(activity, root);
            hideSplashButton();
            updateStatus(AsycudaMaterialUi.tr(activity,
                    "Restarting ASYCUDA World Iraq...",
                    "جاري إعادة تشغيل نظام أسيكودا العراق...",
                    "دووبارە پێکردنەوەی ئەسیکۆدای عێراق..."));
        }
        new Thread(() -> {
            if (sJavaProcess != null) {
                sJavaProcess.destroyForcibly();
                sJavaProcess = null;
            }
            launchAsycudaJvm(activity);
        }, "Asycuda-SessionRestart").start();
    }

    /**
     * Requirement #5: Unified Blocking Update Center Modal
     * Checks both GitHub Releases for new APK versions AND Ministry of Finance (MOF) JARs.
     */
    private void runUnifiedUpdateCheckModal(MainActivity activity) {
        if (!sSyncInProgress.compareAndSet(false, true)) {
            Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                    "Update check is already in progress...",
                    "عملية فحص التحديثات جارية بالفعل...",
                    "پشکنینی نوێکردنەوە لە کاردایە..."), Toast.LENGTH_SHORT).show();
            return;
        }

        AsycudaMaterialUi.UpdateCenterController ctrl = AsycudaMaterialUi.showUpdateCenterModal(activity, null);

        new Thread(() -> {
            boolean appUpdateAvailable = false;
            try {
                // Step 1: Check GitHub Releases API for a newer APK
                ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                        "Checking GitHub for new app releases...",
                        "جاري التحقق من وجود تحديث للتطبيق على GitHub...",
                        "پشکنینی GitHub بۆ وەشانی نوێی بەرنامە..."), 0, true);

                String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                HttpURLConnection ghConn = (HttpURLConnection) new URL(GITHUB_LATEST_RELEASE_API).openConnection();
                ghConn.setConnectTimeout(4500);
                ghConn.setReadTimeout(7000);
                ghConn.setRequestProperty("Accept", "application/vnd.github+json");
                ghConn.setRequestProperty("User-Agent", "ASYCUDA-Iraq-Android/" + currentVer);

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
                    long apkSize = 0;

                    JSONArray assets = rel.optJSONArray("assets");
                    if (assets != null) {
                        for (int i = 0; i < assets.length(); i++) {
                            JSONObject a = assets.getJSONObject(i);
                            String name = a.optString("name", "");
                            if (name.endsWith(".apk")) {
                                apkUrl = a.optString("browser_download_url", null);
                                apkSize = a.optLong("size", 0);
                                break;
                            }
                        }
                    }

                    if (apkUrl != null && isRemoteVersionNewer(tagName, currentVer)) {
                        appUpdateAvailable = true;
                        final String finalApkUrl = apkUrl;
                        final long finalApkSize = apkSize;
                        final String finalTag = tagName;
                        String shortNotes = body.length() > 180 ? body.substring(0, 180).trim() + "..." : body.trim();
                        ctrl.showAppUpdateAvailable(tagName, shortNotes, () -> {
                            new Thread(() -> downloadAndInstallApkUpdate(activity, finalApkUrl, finalApkSize, finalTag, ctrl),
                                    "Asycuda-ApkDownloader").start();
                        });
                    } else {
                        ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                                "✓ Application is up to date (" + currentVer + ")",
                                "✓ التطبيق محدّث لآخر إصدار (" + currentVer + ")",
                                "✓ بەرنامەکە نوێکراوەتەوە (" + currentVer + ")"), -1, false);
                    }
                } else {
                    ghConn.disconnect();
                    ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                            "✓ Running version " + currentVer,
                            "✓ الإصدار الحالي " + currentVer,
                            "✓ وەشانی ئێستا " + currentVer), -1, false);
                }
            } catch (Throwable ignored) {
                String currentVer = AsycudaMaterialUi.getAppVersionLabel(activity);
                ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                        "✓ Running version " + currentVer,
                        "✓ الإصدار الحالي " + currentVer,
                        "✓ وەشانی ئێستا " + currentVer), -1, false);
            }

            try {
                // Step 2: Check Ministry of Finance (MOF) Government JARs
                File clientDir = new File(activity.getFilesDir(), "client");
                int updatedJars = syncGovernmentJars(clientDir, true, ctrl);
                ctrl.onCheckFinished(updatedJars, appUpdateAvailable, () -> restartAsycudaSession(activity));
            } finally {
                sSyncInProgress.set(false);
            }
        }, "Asycuda-UnifiedUpdater").start();
    }

    private void downloadAndInstallApkUpdate(MainActivity activity, String apkUrl, long expectedSize,
                                             String versionTag, AsycudaMaterialUi.UpdateCenterController ctrl) {
        HttpURLConnection conn = null;
        try {
            File printsDir = new File(activity.getFilesDir(), "prints");
            printsDir.mkdirs();
            File outApk = new File(printsDir, "ASYCUDA_Iraq_" + versionTag.replaceAll("[^a-zA-Z0-9._-]", "") + ".apk");

            conn = (HttpURLConnection) new URL(apkUrl).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("User-Agent", "ASYCUDA-Iraq-Android-Updater");

            long totalBytes = conn.getContentLengthLong();
            if (totalBytes <= 0) totalBytes = expectedSize;

            try (InputStream in = new BufferedInputStream(conn.getInputStream(), 65536);
                 FileOutputStream out = new FileOutputStream(outApk)) {
                byte[] buf = new byte[65536];
                long downloaded = 0;
                int n;
                long lastUiUpdate = 0;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
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

            ctrl.setAppUpdateStatus(AsycudaMaterialUi.tr(activity,
                    "✓ Download complete • Launching installer...",
                    "✓ اكتمل التحميل • جاري فتح المثبت...",
                    "✓ دابەزاندن تەواو بوو • کردنەوەی دامەزرێنەر..."), 100, false);

            UI.post(() -> {
                try {
                    Uri apkUri = FileProvider.getUriForFile(activity, "iq.asycuda.fileprovider", outApk);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(install);
                } catch (Throwable t) {
                    Toast.makeText(activity, "Installer error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Throwable t) {
            ctrl.setAppUpdateStatus("Download failed: " + t.getMessage(), -1, false);
        } finally {
            if (conn != null) conn.disconnect();
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
            int rv = 0, lv = 0;
            try { if (i < rp.length) rv = Integer.parseInt(rp[i].replaceAll("[^0-9]", "")); } catch (Throwable ignored) {}
            try { if (i < lp.length) lv = Integer.parseInt(lp[i].replaceAll("[^0-9]", "")); } catch (Throwable ignored) {}
            if (rv > lv) return true;
            if (rv < lv) return false;
        }
        return false;
    }

    /**
     * Requirement #6: Full-Fledged Feedback & Bug Report Modal + Background Sentry Envelope Upload
     */
    private void openFeedbackAndBugReportModal(MainActivity activity) {
        sActiveFeedbackController = AsycudaMaterialUi.showFeedbackModal(activity, new AsycudaMaterialUi.FeedbackSubmitHandler() {
            @Override
            public void onCaptureScreenRequested(AsycudaMaterialUi.FeedbackModalController controller, Dialog feedbackDialog) {
                try {
                    feedbackDialog.hide();
                } catch (Throwable ignored) {}
                UI.postDelayed(() -> {
                    try {
                        View decor = activity.getWindow().getDecorView();
                        int w = Math.max(1, decor.getWidth());
                        int h = Math.max(1, decor.getHeight());
                        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmp);
                        decor.draw(canvas);

                        File printsDir = new File(activity.getFilesDir(), "prints");
                        printsDir.mkdirs();
                        File capFile = new File(printsDir, "screen_capture.png");
                        try (FileOutputStream fos = new FileOutputStream(capFile)) {
                            bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                            fos.getFD().sync();
                        }
                        controller.onImageAttached(capFile, "screen_capture.png");
                    } catch (Throwable t) {
                        Toast.makeText(activity, "Capture error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        try {
                            feedbackDialog.show();
                        } catch (Throwable ignored) {}
                    }
                }, 180);
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
                            AsycudaMaterialUi.tr(activity, "Select Screenshot or Image", "اختر صورة الشاشة", "وێنەیەک هەڵبژێرە")),
                            REQ_PICK_FEEDBACK_IMAGE);
                } catch (Throwable t) {
                    Toast.makeText(activity, "Cannot open image picker: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSubmitToSentry(String description, File attachedImage,
                                         AsycudaMaterialUi.FeedbackModalController controller, Dialog feedbackDialog) {
                controller.setUploadState(true, AsycudaMaterialUi.tr(activity,
                        "Packaging diagnostic logs & uploading report in background...",
                        "جاري تجميع السجلات ورفع البلاغ تلقائياً في الخلفية...",
                        "ئامادەکردنی تۆمارەکان و ناردنی ڕاپۆرت لە پاشبنەمادا..."), false);

                new Thread(() -> {
                    try {
                        File zipFile = buildDiagnosticsZipFile(activity, description, attachedImage);
                        boolean sent = uploadReportEnvelopeToSentry(activity, description, zipFile, attachedImage);
                        if (sent) {
                            controller.setUploadState(false, AsycudaMaterialUi.tr(activity,
                                    "✓ Report & diagnostic logs uploaded successfully! Thank you.",
                                    "✓ تم إرسال البلاغ وسجلات التشخيص للمطور بنجاح! شكراً لك.",
                                    "✓ ڕاپۆرت و تۆمارەکانی پشکنین بە سەرکەوتوویی نێردران! سوپاس."), true);
                            UI.postDelayed(() -> {
                                try { feedbackDialog.dismiss(); } catch (Throwable ignored) {}
                            }, 1400);
                        } else {
                            controller.setUploadState(false, AsycudaMaterialUi.tr(activity,
                                    "Could not reach server. Tap 'Share ZIP...' to send manually.",
                                    "تعذر الاتصال بالخادم. يمكنك الضغط على 'مشاركة ملف ZIP...' لإرساله.",
                                    "پەیوەندی بە سێرڤەرەوە نەکرا. دەتوانیت گرتە لە هاوبەشکردنی ZIP بکەیت."), false);
                        }
                    } catch (Throwable t) {
                        controller.setUploadState(false, "Upload error: " + t.getMessage(), false);
                    }
                }, "Asycuda-SentryUploader").start();
            }

            @Override
            public void onExportZipLocally(String description, File attachedImage, Dialog feedbackDialog) {
                Toast.makeText(activity, AsycudaMaterialUi.tr(activity,
                        "Preparing Diagnostics ZIP...",
                        "جاري تجهيز ملف التشخيص (ZIP)...",
                        "ئامادەکردنی پەڕگەی پشکنین (ZIP)..."), Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    try {
                        File zipFile = buildDiagnosticsZipFile(activity, description, attachedImage);
                        UI.post(() -> {
                            try { feedbackDialog.dismiss(); } catch (Throwable ignored) {}
                            shareFileWithProvider(activity, zipFile, "application/zip");
                        });
                    } catch (Throwable t) {
                        UI.post(() -> Toast.makeText(activity, "Failed to export ZIP: " + t.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }, "Asycuda-ExportZip").start();
            }
        });
    }

    private File buildDiagnosticsZipFile(MainActivity activity, String userDescription, File attachedImage) throws Exception {
        File filesDir = activity.getFilesDir();
        File clientDir = new File(filesDir, "client");
        File printsDir = new File(filesDir, "prints");
        printsDir.mkdirs();

        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String zipName = "ASYCUDA_Diagnostics_" + stamp + ".zip";
        File zipFile = new File(printsDir, zipName);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(fos)) {

            // 1. User Description & Device Summary
            StringBuilder info = new StringBuilder();
            info.append("=== ASYCUDA World Iraq Diagnostics & Feedback Report ===\n");
            info.append("Timestamp: ").append(new Date().toString()).append("\n");
            info.append("App Version: ").append(AsycudaMaterialUi.getAppVersionLabel(activity)).append("\n");
            info.append("Package: ").append(activity.getPackageName()).append("\n");
            info.append("Language: ").append(AsycudaMaterialUi.getAppLanguage(activity)).append("\n");
            info.append("Auto-Show Keyboard: ").append(AsycudaMaterialUi.isAutoKeyboardEnabled(activity)).append("\n");
            info.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            info.append("Device Model: ").append(Build.MODEL).append(" (").append(Build.DEVICE).append(")\n");
            info.append("Android Version: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
            info.append("Supported ABIs: ").append(java.util.Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
            info.append("JVM Process Alive: ").append(sJavaProcess != null && isProcessAlive(sJavaProcess)).append("\n");
            info.append("Free Internal Storage (MB): ").append(filesDir.getUsableSpace() / (1024 * 1024)).append("\n");
            if (userDescription != null && !userDescription.isEmpty()) {
                info.append("\n=== USER FEEDBACK / ISSUE DESCRIPTION ===\n");
                info.append(userDescription).append("\n");
                addBytesToZip(zos, "user_description.txt", userDescription.getBytes(StandardCharsets.UTF_8));
            }
            addBytesToZip(zos, "device_info.txt", info.toString().getBytes(StandardCharsets.UTF_8));

            // 2. Attached Screenshot / Image if present
            if (attachedImage != null && attachedImage.exists()) {
                addFileToZipIfExists(zos, attachedImage, attachedImage.getName());
            }

            // 3. Installed JARs inventory
            File jarsDir = new File(clientDir, "jars");
            File[] jars = jarsDir.listFiles();
            if (jars != null) {
                java.util.Arrays.sort(jars, Comparator.comparing(File::getName));
                StringBuilder jarList = new StringBuilder();
                jarList.append("Total JARs in client/jars: ").append(jars.length).append("\n\n");
                for (File j : jars) {
                    jarList.append(String.format(Locale.US, "%-32s  %10d bytes  %s\n",
                            j.getName(), j.length(), new Date(j.lastModified()).toString()));
                }
                addBytesToZip(zos, "jars_inventory.txt", jarList.toString().getBytes(StandardCharsets.UTF_8));
            }

            // 4. Core ASYCUDA & Client Logs
            addFileToZipIfExists(zos, new File(clientDir, "asycuda.log"), "asycuda.log");
            addFileToZipIfExists(zos, new File(clientDir, "client.log"), "client.log");
            addFileToZipIfExists(zos, new File(clientDir, "classpath.txt"), "classpath.txt");
            addFileToZipIfExists(zos, new File(clientDir, "AWIQWorld.jnlp"), "AWIQWorld.jnlp");

            // 5. Termux:X11 SharedPreferences XML
            File prefsXml = new File(filesDir.getParentFile(), "shared_prefs/" + activity.getPackageName() + "_preferences.xml");
            addFileToZipIfExists(zos, prefsXml, "x11_preferences.xml");

            // 6. Recent Android Logcat
            try {
                Process logProc = new ProcessBuilder("logcat", "-d", "-t", "1500").redirectErrorStream(true).start();
                ByteArrayOutputStream lbo = new ByteArrayOutputStream();
                try (InputStream in = logProc.getInputStream()) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) != -1) lbo.write(buf, 0, n);
                }
                addBytesToZip(zos, "android_logcat.txt", lbo.toByteArray());
            } catch (Throwable ignored) {}

            zos.finish();
            fos.getFD().sync();
        }

        try {
            File pubPrints = new File("/storage/emulated/0/Download/Asycuda_Prints");
            pubPrints.mkdirs();
            File pubZip = new File(pubPrints, zipName);
            try (FileInputStream in = new FileInputStream(zipFile);
                 FileOutputStream out = new FileOutputStream(pubZip)) {
                byte[] buf = new byte[16384];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                out.getFD().sync();
            }
        } catch (Throwable ignored) {}

        return zipFile;
    }

    /**
     * Uploads the user report, device metadata, optional screenshot/image, and full Diagnostics ZIP
     * directly to Sentry's Envelope API (write-only public DSN) in the background with zero login.
     */
    private boolean uploadReportEnvelopeToSentry(Context ctx, String description, File zipFile, File attachedImage) {
        HttpURLConnection conn = null;
        try {
            String dsn = BuildConfig.SENTRY_DSN;
            if (dsn == null || dsn.isEmpty()) return false;

            URI_Parts parts = parseSentryDsn(dsn);
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
            envHeader.put("event_id", eventId);
            envHeader.put("sent_at", nowIso);
            envHeader.put("dsn", dsn);

            // 2. Event Payload
            JSONObject eventObj = new JSONObject();
            eventObj.put("event_id", eventId);
            eventObj.put("timestamp", nowIso);
            eventObj.put("platform", "java");
            eventObj.put("level", "info");
            eventObj.put("release", "iq.asycuda@" + verLabel.replaceFirst("^v", "") + "+37");
            eventObj.put("environment", "production");

            JSONObject msgObj = new JSONObject();
            msgObj.put("formatted", "[User Report] " + firstLine + "\n\n" + description);
            eventObj.put("message", msgObj);

            JSONObject userObj = new JSONObject();
            userObj.put("id", Build.MANUFACTURER + "_" + Build.MODEL);
            userObj.put("username", Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
            eventObj.put("user", userObj);

            JSONObject tags = new JSONObject();
            tags.put("app_version", verLabel);
            tags.put("device_model", Build.MANUFACTURER + " " + Build.MODEL);
            tags.put("android_version", Build.VERSION.RELEASE);
            tags.put("app_language", AsycudaMaterialUi.getAppLanguage(ctx));
            tags.put("has_screenshot", String.valueOf(attachedImage != null && attachedImage.exists()));
            eventObj.put("tags", tags);

            JSONObject contexts = new JSONObject();
            JSONObject devCtx = new JSONObject();
            devCtx.put("name", Build.MODEL);
            devCtx.put("model", Build.MODEL);
            devCtx.put("manufacturer", Build.MANUFACTURER);
            devCtx.put(" brand", Build.BRAND);
            devCtx.put("arch", "arm64-v8a");
            contexts.put("device", devCtx);

            JSONObject osCtx = new JSONObject();
            osCtx.put("name", "Android");
            osCtx.put("version", Build.VERSION.RELEASE);
            contexts.put("os", osCtx);

            JSONObject appCtx = new JSONObject();
            appCtx.put("app_identifier", ctx.getPackageName());
            appCtx.put("app_name", "ASYCUDA Iraq");
            appCtx.put("app_version", verLabel);
            contexts.put("app", appCtx);
            eventObj.put("contexts", contexts);

            JSONObject extra = new JSONObject();
            extra.put("full_description", description);
            extra.put("jvm_running", sJavaProcess != null && isProcessAlive(sJavaProcess));
            File logFile = new File(ctx.getFilesDir(), "client/asycuda.log");
            extra.put("asycuda_log_tail", readTail(logFile, 2500));
            eventObj.put("extra", extra);

            byte[] eventBytes = eventObj.toString().getBytes(StandardCharsets.UTF_8);
            JSONObject eventItemHeader = new JSONObject();
            eventItemHeader.put("type", "event");
            eventItemHeader.put("length", eventBytes.length);
            eventItemHeader.put("content_type", "application/json");

            // 3. User Report Item (also populates Sentry's User Feedback tab)
            JSONObject fbObj = new JSONObject();
            fbObj.put("event_id", eventId);
            fbObj.put("name", Build.MANUFACTURER + " " + Build.MODEL);
            fbObj.put("email", "user@asycuda-iraq.app");
            fbObj.put("comments", description);
            byte[] fbBytes = fbObj.toString().getBytes(StandardCharsets.UTF_8);
            JSONObject fbItemHeader = new JSONObject();
            fbItemHeader.put("type", "user_report");
            fbItemHeader.put("length", fbBytes.length);
            fbItemHeader.put("content_type", "application/json");

            String endpoint = "https://" + parts.host + "/api/" + parts.projectId
                    + "/envelope/?sentry_key=" + parts.publicKey + "&sentry_version=7&sentry_client=asycuda.android/3.7.0";

            conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(25000);
            conn.setRequestProperty("Content-Type", "application/x-sentry-envelope");

            try (OutputStream out = conn.getOutputStream()) {
                // Line 1: Envelope Header
                out.write((envHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));

                // Item 1: Event
                out.write((eventItemHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                out.write(eventBytes);
                out.write('\n');

                // Item 2: User Feedback
                out.write((fbItemHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                out.write(fbBytes);
                out.write('\n');

                // Item 3: Diagnostics ZIP Attachment
                if (zipFile != null && zipFile.exists()) {
                    byte[] zipBytes = readFileBytes(zipFile);
                    if (zipBytes != null) {
                        JSONObject zipHeader = new JSONObject();
                        zipHeader.put("type", "attachment");
                        zipHeader.put("length", zipBytes.length);
                        zipHeader.put("filename", zipFile.getName());
                        zipHeader.put("content_type", "application/zip");
                        zipHeader.put("attachment_type", "event.attachment");
                        out.write((zipHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                        out.write(zipBytes);
                        out.write('\n');
                    }
                }

                // Item 4: Screenshot / Image Attachment
                if (attachedImage != null && attachedImage.exists()) {
                    byte[] imgBytes = readFileBytes(attachedImage);
                    if (imgBytes != null) {
                        JSONObject imgHeader = new JSONObject();
                        imgHeader.put("type", "attachment");
                        imgHeader.put("length", imgBytes.length);
                        imgHeader.put("filename", attachedImage.getName());
                        imgHeader.put("content_type", guessMime(attachedImage.getName()));
                        imgHeader.put("attachment_type", "event.attachment");
                        out.write((imgHeader.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                        out.write(imgBytes);
                        out.write('\n');
                    }
                }
                out.flush();
            }

            int respCode = conn.getResponseCode();
            return respCode >= 200 && respCode < 300;
        } catch (Throwable ignored) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static class URI_Parts {
        String publicKey;
        String host;
        String projectId;
    }

    private static URI_Parts parseSentryDsn(String dsn) {
        try {
            URL u = new URL(dsn);
            URI_Parts p = new URI_Parts();
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
            return null;
        }
    }

    private static void addBytesToZip(java.util.zip.ZipOutputStream zos, String entryName, byte[] data) {
        try {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
            zos.putNextEntry(entry);
            zos.write(data);
            zos.closeEntry();
        } catch (Throwable ignored) {}
    }

    private static void addFileToZipIfExists(java.util.zip.ZipOutputStream zos, File file, String entryName) {
        if (file == null || !file.exists() || !file.isFile()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
            zos.putNextEntry(entry);
            byte[] buf = new byte[16384];
            int n;
            while ((n = fis.read(buf)) != -1) {
                zos.write(buf, 0, n);
            }
            zos.closeEntry();
        } catch (Throwable ignored) {}
    }

    private void openUrl(Context ctx, String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Throwable t) {
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
        String low = name.toLowerCase();
        if (low.endsWith(".pdf")) return "application/pdf";
        if (low.endsWith(".zip")) return "application/zip";
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
        f.delete();
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
