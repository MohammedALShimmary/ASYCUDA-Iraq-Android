package com.termux.x11;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private static final Handler UI = new Handler(Looper.getMainLooper());
    private static volatile AsycudaStandaloneManager sInstance = null;
    private static volatile boolean sExtractingOrBooting = false;
    private static final AtomicBoolean sSyncInProgress = new AtomicBoolean(false);
    private static volatile boolean sServerStarted = false;
    private static volatile CmdEntryPoint sCmdEntry = null;
    private static volatile Process sJavaProcess = null;
    private static volatile MainActivity sCurrentActivity = null;

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

    private void initOnActivity(MainActivity activity) {
        View stub = activity.findViewById(R.id.stub);
        if (stub != null) stub.setVisibility(View.GONE);

        // Apply your exact preferred Termux:X11 settings once (allowing user changes afterwards)
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
            updateStatus("Storage permission is required to save & open Customs PDFs and declarations.");
            showSplashButton("Grant All Files Access", v -> {
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
            updateStatus("Please allow or dismiss the notification prompt to continue...");
            return;
        }

        hideSplashButton();

        // If a manual JAR sync or bootstrap is currently running, do not interfere
        if (sExtractingOrBooting || sSyncInProgress.get()) {
            return;
        }

        // If already running, just reconnect X11 view and hide splash
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
        GradientDrawable backdrop = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { Color.parseColor("#E60B1120"), Color.parseColor("#E6090D16") });
        mSplashOverlay.setBackground(backdrop);
        mSplashOverlay.setClickable(true);

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        int pad = dp(activity, 28);
        card.setPadding(pad, pad, pad, pad);
        card.setBackground(AsycudaMaterialUi.createGlassContainerDrawable(activity, 28));
        card.setElevation(dp(activity, 18));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(activity, 72), dp(activity, 72));
        iconLp.bottomMargin = dp(activity, 12);
        card.addView(icon, iconLp);

        TextView title = new TextView(activity);
        title.setText("ASYCUDA World — Republic of Iraq");
        title.setTextColor(Color.parseColor("#F8FAFC"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        title.setGravity(Gravity.CENTER);
        card.addView(title);

        TextView sub = new TextView(activity);
        sub.setText("General Commission for Customs • Client " + AsycudaMaterialUi.getAppVersionLabel(activity));
        sub.setTextColor(Color.parseColor("#7DD3FC"));
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dp(activity, 4);
        subLp.bottomMargin = dp(activity, 18);
        card.addView(sub, subLp);

        mSplashProgress = new ProgressBar(activity);
        card.addView(mSplashProgress, new LinearLayout.LayoutParams(dp(activity, 36), dp(activity, 36)));

        mSplashStatus = new TextView(activity);
        mSplashStatus.setText("Checking permissions...");
        mSplashStatus.setTextColor(Color.parseColor("#E2E8F0"));
        mSplashStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        mSplashStatus.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams stLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stLp.topMargin = dp(activity, 14);
        card.addView(mSplashStatus, stLp);

        mActionBtn = AsycudaMaterialUi.createGlassPillButton(activity, "Grant All Files Access", true, null);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dp(activity, 14);
        mActionBtn.setLayoutParams(btnLp);
        mActionBtn.setVisibility(View.GONE);
        card.addView(mActionBtn);

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(
                dp(activity, 490), ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
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
            File bootPatchMarker = new File(filesDir, ".bootpatch_v3_4");

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

                // Ensure all ELF binaries & shared libraries have clean read/execute permissions
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
                updateStatus("Updating client print & session components...");
                extractBootJarsOnly(activity, "asycuda-iraq.tar.xz", clientDir);
                bootPatchMarker.createNewFile();
            }

            // Smart Startup Sync: Check MOF Server (asy.mof.gov.iq) with clear status feedback
            syncGovernmentJars(clientDir, false);

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
     * Extracts only 00_boot.jar and 00_bootpatch.jar from asycuda-iraq.tar.xz so updates to our
     * bridge and NPE fixes apply in <1 second without wiping user/MOF JARs.
     */
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
                if ("jars/00_boot.jar".equals(name) || "00_bootpatch.jar".equals(name)) {
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
                    if (found >= 2) break;
                }
            }
        } catch (Throwable ignored) {}
    }

    /**
     * Smart Government (MOF) JNLP & JAR Updater.
     * Connects to https://asy.mof.gov.iq/aw/AWIQWorld.jnlp (trusting MOF custom SSL certificate),
     * parses all <jar href="..."/> entries, checks missing or updated JARs via HTTP Content-Length,
     * downloads only changed/new JARs atomically, and updates classpath.txt while preserving 00_boot.jar & 00_jnlp.jar.
     */
    private int syncGovernmentJars(File clientDir, boolean manualForceCheck) {
        int updatedCount = 0;
        try {
            updateStatus("Connecting to MOF Server (asy.mof.gov.iq) to verify JARs...");
            if (!manualForceCheck) {
                Thread.sleep(500);
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
                Thread.sleep(500);
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

            // Save latest AWIQWorld.jnlp locally
            File localJnlp = new File(clientDir, "AWIQWorld.jnlp");
            try (FileOutputStream fos = new FileOutputStream(localJnlp)) {
                fos.write(jnlpXml.getBytes(StandardCharsets.UTF_8));
                fos.getFD().sync();
            } catch (Throwable ignored) {}

            // Parse <jar href="..."/> entries
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
            for (int i = 0; i < total; i++) {
                String href = remoteHrefs.get(i);
                String flatName = href.contains("/") ? href.substring(href.lastIndexOf('/') + 1) : href;
                if (flatName.equals("00_boot.jar") || flatName.equals("00_jnlp.jar")) {
                    continue;
                }
                cpEntries.add("jars/" + flatName);

                File targetJar = new File(jarsDir, flatName);
                String jarUrl = href.startsWith("http") ? href : (MOF_CODEBASE_URL + href.replaceFirst("^/+", ""));

                boolean shouldDownload = !targetJar.exists() || targetJar.length() == 0;

                // In manual check mode, compare exact byte size (Content-Length) to avoid false timestamp differences
                if (!shouldDownload && manualForceCheck) {
                    updateStatus("Verifying MOF JARs (" + (i + 1) + "/" + total + "): " + flatName);
                    HttpURLConnection head = null;
                    try {
                        head = openMofConnection(jarUrl, sslFactory, 4000, 4000);
                        head.setRequestMethod("HEAD");
                        if (head.getResponseCode() == 200) {
                            long remoteLen = head.getContentLengthLong();
                            if (remoteLen > 0 && remoteLen != targetJar.length()) {
                                shouldDownload = true;
                            }
                        }
                    } catch (Throwable ignored) {
                    } finally {
                        if (head != null) head.disconnect();
                    }
                }

                if (shouldDownload) {
                    updateStatus("Downloading MOF update: " + flatName + " (" + (i + 1) + "/" + total + ")...");
                    if (downloadJarAtomically(jarUrl, targetJar, sslFactory)) {
                        updatedCount++;
                    }
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

            if (!manualForceCheck) {
                if (updatedCount > 0) {
                    updateStatus("✓ Downloaded " + updatedCount + " new Government JAR(s) from MOF!");
                } else {
                    updateStatus("✓ MOF Government JARs Verified (" + total + " packages up to date)");
                }
                Thread.sleep(700);
            }

        } catch (Throwable ignored) {
            // Offline or unreachable MOF server: proceed with cached local JARs
            try {
                updateStatus("✓ Offline Mode: Using " + new File(clientDir, "jars").list().length + " cached MOF JARs");
                Thread.sleep(500);
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
            try (InputStream in = new BufferedInputStream(conn.getInputStream(), 32768);
                 FileOutputStream out = new FileOutputStream(tmpJar)) {
                byte[] buf = new byte[32768];
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

            // Canonical /data/data/iq.asycuda/files path so OpenJDK resolves libjava.so cleanly
            File filesDir = new File("/data/data/" + activity.getPackageName() + "/files");
            if (!filesDir.exists()) filesDir = activity.getFilesDir();

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
            File userHome = pubFiles.canWrite() ? pubFiles : new File(filesDir, "home");

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

            // Execute javaBin directly (NOT ld-linux) so /proc/self/exe points to jre8/jre/bin/java
            List<String> cmd = new ArrayList<>();
            cmd.add(javaBin.getAbsolutePath());
            cmd.add("-Dasycuda.running=1");
            cmd.add("-Xbootclasspath/p:" + new File(clientDir, "00_bootpatch.jar").getAbsolutePath());
            cmd.add("-Xms128m");
            cmd.add("-Xmx1224m");
            cmd.add("-Djava.io.tmpdir=" + tmpDir.getAbsolutePath());
            cmd.add("-Dasycuda.prints.dir=" + new File(filesDir, "prints").getAbsolutePath());
            cmd.add("-Djava.net.preferIPv4Stack=true");
            cmd.add("-Djava.rmi.server.hostname=127.0.0.1");
            cmd.add("-Duser.home=" + userHome.getAbsolutePath());
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
            env.put("HOME", new File(filesDir, "home").getAbsolutePath());
            env.put("PREFIX", new File(filesDir, "usr").getAbsolutePath());
            env.put("TMPDIR", new File(filesDir, "usr/tmp").getAbsolutePath());
            env.put("DISPLAY", ":0");

            sJavaProcess = pb.start();

            Thread.sleep(2500);
            if (!isProcessAlive(sJavaProcess)) {
                int code = sJavaProcess.exitValue();
                String errTail = readTail(logFile, 400);
                updateStatus("JVM exited (code " + code + "):\n" + errTail);
            } else {
                hideSplashDelayed(800);
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
        int hp = dp(activity, 14);
        int vp = dp(activity, 8);
        pill.setPadding(hp, vp, hp, vp);
        pill.setAlpha(0.86f);
        pill.setElevation(dp(activity, 10));

        // Frosted Glass Floating Pill
        pill.setBackground(AsycudaMaterialUi.createGlassGradientDrawable(
                activity,
                Color.parseColor("#B31E293B"),
                Color.parseColor("#990F172A"),
                24,
                Color.parseColor("#807DD3FC")));

        ImageView dot = new ImageView(activity);
        dot.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(activity, 18), dp(activity, 18));
        dotLp.rightMargin = dp(activity, 7);
        pill.addView(dot, dotLp);

        TextView label = new TextView(activity);
        label.setText("ASYCUDA");
        label.setTextColor(Color.parseColor("#F8FAFC"));
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        label.setLetterSpacing(0.05f);
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
                        v.setAlpha(0.86f);
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
            public void onToggleKeyboard() {
                activity.toggleKeyboardVisibility();
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
            public void onSyncGovJars() {
                if (!sSyncInProgress.compareAndSet(false, true)) {
                    Toast.makeText(activity, "Government JAR sync is already in progress...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Checking MOF server (asy.mof.gov.iq) in the background...", Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    try {
                        File clientDir = new File(activity.getFilesDir(), "client");
                        int count = syncGovernmentJars(clientDir, true);
                        if (count > 0) {
                            UI.post(() -> {
                                FrameLayout root = activity.findViewById(android.R.id.content);
                                if (root != null) {
                                    setupSplashOverlay(activity, root);
                                    hideSplashButton();
                                    updateStatus("Downloaded " + count + " updated JAR(s) from MOF! Restarting session...");
                                }
                                Toast.makeText(activity,
                                        "Updated " + count + " Government JAR(s)! Restarting ASYCUDA session...",
                                        Toast.LENGTH_LONG).show();
                            });
                            if (sJavaProcess != null) {
                                sJavaProcess.destroyForcibly();
                                sJavaProcess = null;
                            }
                            launchAsycudaJvm(activity);
                        } else {
                            UI.post(() -> Toast.makeText(activity,
                                    "✓ All ASYCUDA Government JARs are up to date!",
                                    Toast.LENGTH_LONG).show());
                        }
                    } finally {
                        sSyncInProgress.set(false);
                    }
                }, "Asycuda-ManualGovSync").start();
            }

            @Override
            public void onRestartSession() {
                Toast.makeText(activity, "Restarting ASYCUDA World session...", Toast.LENGTH_SHORT).show();
                FrameLayout root = activity.findViewById(android.R.id.content);
                if (root != null) {
                    setupSplashOverlay(activity, root);
                    hideSplashButton();
                    updateStatus("Restarting ASYCUDA World Iraq...");
                }
                new Thread(() -> {
                    if (sJavaProcess != null) {
                        sJavaProcess.destroyForcibly();
                        sJavaProcess = null;
                    }
                    launchAsycudaJvm(activity);
                }).start();
            }

            @Override
            public void onShareDiagnosticsLog() {
                Toast.makeText(activity, "Packaging diagnostics & logs into ZIP...", Toast.LENGTH_SHORT).show();
                new Thread(() -> exportAndShareDiagnosticsZip(activity), "Asycuda-DiagnosticsZip").start();
            }

            @Override
            public void onOpenAbout() {
                AsycudaMaterialUi.showAboutAndCreditsModal(activity, this);
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

    private void exportAndShareDiagnosticsZip(MainActivity activity) {
        try {
            File filesDir = activity.getFilesDir();
            File clientDir = new File(filesDir, "client");
            File printsDir = new File(filesDir, "prints");
            printsDir.mkdirs();

            String stamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(new java.util.Date());
            String zipName = "ASYCUDA_Diagnostics_" + stamp + ".zip";
            File zipFile = new File(printsDir, zipName);

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(fos)) {

                // 1. Device & Environment Summary
                StringBuilder info = new StringBuilder();
                info.append("=== ASYCUDA World Iraq Diagnostics Report ===\n");
                info.append("Timestamp: ").append(new java.util.Date().toString()).append("\n");
                info.append("App Version: ").append(AsycudaMaterialUi.getAppVersionLabel(activity)).append("\n");
                info.append("Package: ").append(activity.getPackageName()).append("\n");
                info.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
                info.append("Device Model: ").append(Build.MODEL).append(" (").append(Build.DEVICE).append(")\n");
                info.append("Android Version: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
                info.append("Supported ABIs: ").append( java.util.Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
                info.append("JVM Process Alive: ").append(sJavaProcess != null && isProcessAlive(sJavaProcess)).append("\n");
                info.append("Free Internal Storage (MB): ").append(filesDir.getUsableSpace() / (1024 * 1024)).append("\n");
                addBytesToZip(zos, "device_info.txt", info.toString().getBytes(StandardCharsets.UTF_8));

                // 2. Installed JARs inventory (names, sizes, dates)
                File jarsDir = new File(clientDir, "jars");
                File[] jars = jarsDir.listFiles();
                if (jars != null) {
                    java.util.Arrays.sort(jars, Comparator.comparing(File::getName));
                    StringBuilder jarList = new StringBuilder();
                    jarList.append("Total JARs in client/jars: ").append(jars.length).append("\n\n");
                    for (File j : jars) {
                        jarList.append(String.format(java.util.Locale.US, "%-32s  %10d bytes  %s\n",
                                j.getName(), j.length(), new java.util.Date(j.lastModified()).toString()));
                    }
                    addBytesToZip(zos, "jars_inventory.txt", jarList.toString().getBytes(StandardCharsets.UTF_8));
                }

                // 3. Core ASYCUDA & Client Logs
                addFileToZipIfExists(zos, new File(clientDir, "asycuda.log"), "asycuda.log");
                addFileToZipIfExists(zos, new File(clientDir, "client.log"), "client.log");
                addFileToZipIfExists(zos, new File(clientDir, "classpath.txt"), "classpath.txt");
                addFileToZipIfExists(zos, new File(clientDir, "AWIQWorld.jnlp"), "AWIQWorld.jnlp");

                // 4. Termux:X11 SharedPreferences XML
                File prefsXml = new File(filesDir.getParentFile(), "shared_prefs/" + activity.getPackageName() + "_preferences.xml");
                addFileToZipIfExists(zos, prefsXml, "x11_preferences.xml");

                // 5. Recent Android Logcat (filtered to this app process)
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

            // Also copy to /sdcard/Download/Asycuda_Prints for easy local access
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

            UI.post(() -> shareFileWithProvider(activity, zipFile, "application/zip"));
        } catch (Throwable t) {
            UI.post(() -> Toast.makeText(activity, "Failed to export logs: " + t.getMessage(), Toast.LENGTH_LONG).show());
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
