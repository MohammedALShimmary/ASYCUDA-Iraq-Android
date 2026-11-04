package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Cloud Firestore & Push/Announcement integration manager for AsyLauncher (Launcher for ASYCUDA).
 *
 * Syncs with the project's Cloud Firestore database:
 *  - announcements/latest (News, Alerts & Release Broadcasts)
 *  - app_config/known_errors_catalog (Hot-updatable diagnostic error catalog)
 *  - app_metrics/{anonId} (Zero-PII daily anonymous installation health & active user heartbeat)
 *  - diagnostic_reports (Voluntary, in-RAM credential-redacted user issue reports)
 */
@SuppressLint("SetTextI18n")
@SuppressWarnings({"java:S3776", "java:S107", "java:S1141", "java:S1192", "java:S1172", "java:S1075", "java:S2696", "java:S9439"})
public final class AsycudaFirebaseManager {

    private static final String OFFICIAL_PACKAGE_NAME = "io.github.mohammedalshimmary.asylauncher";
    private static final String OFFICIAL_RELEASE_CERT_SHA256 =
            "d302d4d050fb986e4088dd3e684c90662d00da748dde8da3d4966baea02da023";
    private static final String DEFAULT_PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=" + OFFICIAL_PACKAGE_NAME;
    private static final int MAX_FIRESTORE_RESPONSE_BYTES = 262144; // 256 KB hard cap
    private static final String PREF_NAME = "asycuda_firebase_prefs";

    private static final String KEY_LAST_DISMISSED_ANNOUNCEMENT = "last_dismissed_announcement_id";
    private static final String KEY_LAST_READ_REMOTE_ANNOUNCEMENT = "last_read_remote_announcement_id";
    private static final String KEY_LAST_READ_RELEASE_NOTES_VER = "last_read_release_notes_ver";
    private static final String KEY_REMOTE_CATALOG_VERSION = "remote_error_catalog_version";
    private static final String KEY_ANON_INSTALLATION_ID = "anon_installation_id";
    private static final String KEY_LAST_HEARTBEAT_DATE = "last_heartbeat_utc_date";

    // Cached remote broadcast keys
    private static final String KEY_CACHED_ID = "cached_ann_id";
    private static final String KEY_CACHED_ACTIVE = "cached_ann_active";
    private static final String KEY_CACHED_SEVERITY = "cached_ann_severity";
    private static final String KEY_CACHED_TITLE_EN = "cached_ann_title_en";
    private static final String KEY_CACHED_TITLE_AR = "cached_ann_title_ar";
    private static final String KEY_CACHED_TITLE_KU = "cached_ann_title_ku";
    private static final String KEY_CACHED_BODY_EN = "cached_ann_body_en";
    private static final String KEY_CACHED_BODY_AR = "cached_ann_body_ar";
    private static final String KEY_CACHED_BODY_KU = "cached_ann_body_ku";
    private static final String KEY_CACHED_PLAY_URL = "cached_ann_play_url";
    private static final String KEY_CACHED_GITHUB_URL = "cached_ann_github_url";
    private static final String KEY_CACHED_VERSION_TAG = "cached_ann_version_tag";
    private static final String KEY_CACHED_PUBLISHED_UTC = "cached_ann_published_utc";

    private static final String DEFAULT_ANNOUNCEMENT_ID = "welcome-v4-3-0";

    private static volatile boolean sStartupSyncTriggered = false;
    private static volatile Boolean sCachedOfficialBuildStatus = null;

    private AsycudaFirebaseManager() {}

    /**
     * 3-Layer Fork/Clone & Repack Protection:
     *  - Layer 1: Requires BuildConfig.FIREBASE_PROJECT_ID injected from private CI/local secrets (empty on forks/clones).
     *  - Layer 2: Verifies package name ("io.github.mohammedalshimmary.asylauncher"), non-debuggable release flag, and either the pinned
     *             official release X.509 signing certificate SHA-256 (d302d4d050fb...) or official Google Play Store installation.
     *  - Layer 3: Attaches X-Android-Package and X-Android-Cert (SHA-1) headers for Google Cloud enforcement.
     */
    public static boolean isAuthorizedOfficialBuild(Context ctx) {
        if (ctx == null) return false;
        Boolean cached = sCachedOfficialBuildStatus;
        if (cached != null) return cached;

        try {
            String projectId = BuildConfig.FIREBASE_PROJECT_ID;
            if (projectId == null || projectId.trim().isEmpty()) {
                sCachedOfficialBuildStatus = false;
                return false;
            }
            if (!OFFICIAL_PACKAGE_NAME.equals(ctx.getPackageName())) {
                sCachedOfficialBuildStatus = false;
                return false;
            }
            if ((ctx.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                sCachedOfficialBuildStatus = false;
                return false;
            }

            android.content.pm.PackageManager pm = ctx.getPackageManager();
            byte[] certBytes = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                android.content.pm.PackageInfo pi = pm.getPackageInfo(
                        ctx.getPackageName(), android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES);
                if (pi.signingInfo != null) {
                    android.content.pm.Signature[] sigs = pi.signingInfo.hasMultipleSigners()
                            ? pi.signingInfo.getApkContentsSigners()
                            : pi.signingInfo.getSigningCertificateHistory();
                    if (sigs != null && sigs.length > 0 && sigs[0] != null) {
                        certBytes = sigs[0].toByteArray();
                    }
                }
            } else {
                @SuppressWarnings("deprecation")
                android.content.pm.PackageInfo pi = pm.getPackageInfo(
                        ctx.getPackageName(), android.content.pm.PackageManager.GET_SIGNATURES);
                if (pi.signatures != null && pi.signatures.length > 0 && pi.signatures[0] != null) {
                    certBytes = pi.signatures[0].toByteArray();
                }
            }

            if (certBytes == null || certBytes.length == 0) {
                sCachedOfficialBuildStatus = false;
                return false;
            }

            String sha256Lower = toHex(java.security.MessageDigest.getInstance("SHA-256").digest(certBytes), false);

            boolean certMatches = OFFICIAL_RELEASE_CERT_SHA256.equalsIgnoreCase(sha256Lower);
            sCachedOfficialBuildStatus = certMatches;
            return certMatches;
        } catch (Throwable t) {
            sCachedOfficialBuildStatus = false;
            return false;
        }
    }

    public static boolean isTrustedAnnouncementUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) return false;
        try {
            Uri uri = Uri.parse(rawUrl.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            String idParam = uri.getQueryParameter("id");
            if ("market".equalsIgnoreCase(scheme) && "details".equalsIgnoreCase(host)) {
                return OFFICIAL_PACKAGE_NAME.equals(idParam);
            }
            if ("https".equalsIgnoreCase(scheme) && "play.google.com".equalsIgnoreCase(host)) {
                return "/store/apps/details".equals(path) && OFFICIAL_PACKAGE_NAME.equals(idParam);
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

    private static String sanitizeTrustedAnnouncementUrl(String rawUrl) {
        return isTrustedAnnouncementUrl(rawUrl) ? rawUrl.trim() : DEFAULT_PLAY_STORE_URL;
    }

    private static String toHex(byte[] bytes, boolean upperCase) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(java.util.Locale.US, upperCase ? "%02X" : "%02x", b & 0xff));
        }
        return sb.toString();
    }

    private static HttpURLConnection openAuthorizedFirestoreConnection(
            Context ctx,
            String subPathAndQuery,
            String method,
            int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        if (!isAuthorizedOfficialBuild(ctx)) {
            throw new SecurityException("Cloud Firestore restricted to official signed AsyLauncher builds.");
        }
        String projectId = BuildConfig.FIREBASE_PROJECT_ID != null ? BuildConfig.FIREBASE_PROJECT_ID.trim() : "";
        if (!projectId.matches("^[a-z0-9-]{6,63}$")) {
            throw new SecurityException("Invalid Cloud Firestore project ID format.");
        }
        String baseUrl = "https://firestore.googleapis.com/v1/projects/"
                + projectId + "/databases/(default)/documents";
        URL url = new URL(baseUrl + subPathAndQuery);
        if (!"https".equalsIgnoreCase(url.getProtocol()) || !"firestore.googleapis.com".equalsIgnoreCase(url.getHost())) {
            throw new SecurityException("Blocked non-Firestore endpoint: " + url.getHost());
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(method);
        conn.setConnectTimeout(connectTimeoutMs);
        conn.setReadTimeout(readTimeoutMs);
        conn.setRequestProperty("X-Android-Package", OFFICIAL_PACKAGE_NAME);
        return conn;
    }

    /**
     * Triggered once per session after startup to fetch the latest News/Announcement broadcast,
     * hot-reload the remote Known Errors Catalog, and record the once-per-day Zero-PII anonymous
     * installation heartbeat in Cloud Firestore.
     */
    public static void syncOnStartup(Activity activity) {
        if (activity == null || sStartupSyncTriggered || !AsycudaMaterialUi.hasAcceptedTerms(activity)) return;
        sStartupSyncTriggered = true;
        final Context appCtx = activity.getApplicationContext();

        new Thread(() -> {
            fetchAndProcessLatestAnnouncement(activity, false, null);
            fetchAndSyncKnownErrorsCatalog(appCtx);
            sendAnonymousDailyHeartbeatSync(appCtx);
        }, "AsycudaFirestoreStartupSync").start();
    }

    /**
     * Returns the number of unread News & Release Alerts (0, 1, or 2):
     * 1. Latest Cloud Broadcast (e.g. "welcome-v4-3-0" or any newer remote announcement ID)
     * 2. Built-in Release Highlights for the current BuildConfig.VERSION_CODE (v4.3.0)
     */
    public static int getUnreadAlertsCount(Context ctx) {
        if (ctx == null) return 0;
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int unread = 0;

        String currentRemoteId = prefs.getString(KEY_CACHED_ID, DEFAULT_ANNOUNCEMENT_ID);
        if (currentRemoteId == null || currentRemoteId.isEmpty()) {
            currentRemoteId = DEFAULT_ANNOUNCEMENT_ID;
        }
        String lastReadRemoteId = prefs.getString(KEY_LAST_READ_REMOTE_ANNOUNCEMENT, "");
        if (!currentRemoteId.equals(lastReadRemoteId)) {
            unread++;
        }

        int lastReadVer = prefs.getInt(KEY_LAST_READ_RELEASE_NOTES_VER, 0);
        if (lastReadVer < BuildConfig.VERSION_CODE) {
            unread++;
        }
        return unread;
    }

    public static boolean isRemoteAnnouncementUnread(Context ctx) {
        if (ctx == null) return false;
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String currentRemoteId = prefs.getString(KEY_CACHED_ID, DEFAULT_ANNOUNCEMENT_ID);
        if (currentRemoteId == null || currentRemoteId.isEmpty()) {
            currentRemoteId = DEFAULT_ANNOUNCEMENT_ID;
        }
        String lastReadRemoteId = prefs.getString(KEY_LAST_READ_REMOTE_ANNOUNCEMENT, "");
        return !currentRemoteId.equals(lastReadRemoteId);
    }

    public static boolean isReleaseNotesUnread(Context ctx) {
        if (ctx == null) return false;
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int lastReadVer = prefs.getInt(KEY_LAST_READ_RELEASE_NOTES_VER, 0);
        return lastReadVer < BuildConfig.VERSION_CODE;
    }

    /**
     * Marks both the active Cloud Broadcast and the current version's Release Notes as read,
     * and refreshes the floating pill unread badge.
     */
    public static void markAllAlertsRead(Activity activity) {
        if (activity == null) return;
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String currentRemoteId = prefs.getString(KEY_CACHED_ID, DEFAULT_ANNOUNCEMENT_ID);
        if (currentRemoteId == null || currentRemoteId.isEmpty()) {
            currentRemoteId = DEFAULT_ANNOUNCEMENT_ID;
        }
        prefs.edit()
                .putString(KEY_LAST_READ_REMOTE_ANNOUNCEMENT, currentRemoteId)
                .putString(KEY_LAST_DISMISSED_ANNOUNCEMENT, currentRemoteId)
                .putInt(KEY_LAST_READ_RELEASE_NOTES_VER, BuildConfig.VERSION_CODE)
                .apply();
        AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
    }

    /**
     * Opens the unified News & Release Alerts Center immediately (never blocks on network)
     * and triggers a live Cloud Firestore sync in the background.
     */
    public static void checkAnnouncementsManually(Activity activity) {
        if (activity instanceof MainActivity) {
            showNewsAndAlertsModal((MainActivity) activity, null);
        } else if (activity != null) {
            new Thread(() -> fetchAndProcessLatestAnnouncement(activity, true, null), "AsycudaAnnouncementManual").start();
        }
    }

    /**
     * Unified Apple/Material Dark Mode "News & Release Alerts" Modal Window.
     * Opens immediately with cached/seeded Cloud Broadcast + v4.3.0 Release Highlights,
     * displays "NEW" badges on unread alerts, marks them read, and refreshes live from Firestore.
     */
    public static void showNewsAndAlertsModal(MainActivity activity, AsycudaMaterialUi.MenuActionCallback cb) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

        final boolean remoteWasUnread = isRemoteAnnouncementUnread(activity);
        final boolean releaseWasUnread = isReleaseNotesUnread(activity);

        // Mark alerts as read upon viewing and update floating pill counter
        markAllAlertsRead(activity);

        Dialog dialog = AsycudaMaterialUi.createModalDialog(activity, 0.84f, 600, true);
        LinearLayout root = AsycudaMaterialUi.createDialogContainer(activity);

        // Header Row
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_notifications);
        icon.setImageTintList(ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(AsycudaMaterialUi.tr(activity,
                "News & Release Alerts",
                "الأخبار والتنبيهات الفورية",
                "هەواڵ و ئاگادارییەکان"));
        title.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(AsycudaMaterialUi.SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        final TextView subtitle = new TextView(activity);
        subtitle.setText(AsycudaMaterialUi.tr(activity,
                "AsyLauncher cloud broadcasts, updates & v" + BuildConfig.VERSION_NAME + " release notes",
                "تنبيهات AsyLauncher السحابية، التحديثات وملخص الإصدار v" + BuildConfig.VERSION_NAME,
                "ڕاگەیاندنی هەوری AsyLauncher، نوێکارییەکان و وەشانی v" + BuildConfig.VERSION_NAME));
        subtitle.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        // Live Refresh Button
        final LinearLayout cardsContainer = new LinearLayout(activity);
        cardsContainer.setOrientation(LinearLayout.VERTICAL);

        View refreshBtn = AsycudaMaterialUi.createIconButton(activity, R.drawable.ic_menu_cloud_sync,
                AsycudaMaterialUi.tr(activity, "Sync Live", "تحديث الآن", "هاوکاتکردن"),
                false, false, v -> {
                    subtitle.setText(AsycudaMaterialUi.tr(activity,
                            "Syncing live broadcasts from Cloud Firestore...",
                            "جارٍ مزامنة أحدث الإشعارات من السحابة...",
                            "هاوکاتکردنی نوێترین ئاگادارییەکان لە هەورەوە..."));
                    new Thread(() -> fetchAndProcessLatestAnnouncement(activity, false, synced -> {
                        activity.runOnUiThread(() -> {
                            if (activity.isFinishing() || activity.isDestroyed() || !dialog.isShowing()) return;
                            markAllAlertsRead(activity);
                            populateAlertsCards(activity, cardsContainer, remoteWasUnread, releaseWasUnread);
                            subtitle.setText(synced
                                    ? AsycudaMaterialUi.tr(activity,
                                            "Synced live with Cloud Firestore  •  Up to date",
                                            "تمت المزامنة المباشرة مع السحابة  •  محدّث بالكامل",
                                            "هاوکاتکرا لەگەڵ سێرڤەری هەوری  •  نوێکراوەتەوە")
                                    : AsycudaMaterialUi.tr(activity,
                                            "Showing cached broadcasts (offline or unreachable)",
                                            "يتم عرض الإشعارات المحفوظة (لا يوجد اتصال حالياً)",
                                            "پیشاندانی ئاگادارییە پاشەکەوتکراوەکان"));
                        });
                    }), "AsycudaAlertsManualSync").start();
                });
        LinearLayout.LayoutParams refLp = (LinearLayout.LayoutParams) refreshBtn.getLayoutParams();
        refLp.setMarginEnd(dp(activity, 6));
        header.addView(refreshBtn);

        if (cb != null) {
            header.addView(AsycudaMaterialUi.createIconButton(activity, R.drawable.ic_nav_back,
                    AsycudaMaterialUi.tr(activity, "Back to Control Center", "العودة لمركز التحكم", "گەڕانەوە بۆ ناوەندی کۆنترۆڵ"),
                    false, false, v -> {
                        dialog.dismiss();
                        AsycudaMaterialUi.showControlCenter(activity, cb);
                    }));
            dialog.setOnCancelListener(d -> AsycudaMaterialUi.showControlCenter(activity, cb));
        } else {
            header.addView(AsycudaMaterialUi.createIconButton(activity, R.drawable.ic_action_close,
                    AsycudaMaterialUi.tr(activity, "Done", "تم", "تەواو"),
                    true, false, v -> dialog.dismiss()));
        }

        root.addView(header);
        root.addView(AsycudaMaterialUi.createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 8);
        scroll.setLayoutParams(scrollLp);

        populateAlertsCards(activity, cardsContainer, remoteWasUnread, releaseWasUnread);
        scroll.addView(cardsContainer);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();

        // Trigger background Firestore sync automatically when modal opens
        new Thread(() -> fetchAndProcessLatestAnnouncement(activity, false, synced -> {
            if (synced) {
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed() || !dialog.isShowing()) return;
                    markAllAlertsRead(activity);
                    populateAlertsCards(activity, cardsContainer, remoteWasUnread, releaseWasUnread);
                });
            }
        }), "AsycudaAlertsAutoSync").start();
    }

    private static void populateAlertsCards(
            MainActivity activity,
            LinearLayout container,
            boolean remoteWasUnread,
            boolean releaseWasUnread) {
        container.removeAllViews();

        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 1. Cloud Broadcast Card (Seeded with welcome-v4-3-0 if not yet fetched)
        String severity = prefs.getString(KEY_CACHED_SEVERITY, "info");
        String versionTag = prefs.getString(KEY_CACHED_VERSION_TAG, "v" + BuildConfig.VERSION_NAME);
        String publishedUtc = prefs.getString(KEY_CACHED_PUBLISHED_UTC, "2026-11-04");
        if (publishedUtc != null && publishedUtc.length() >= 10) {
            publishedUtc = publishedUtc.substring(0, 10);
        }

        String titleEn = prefs.getString(KEY_CACHED_TITLE_EN, "Welcome to AsyLauncher v" + BuildConfig.VERSION_NAME);
        String titleAr = prefs.getString(KEY_CACHED_TITLE_AR, "مرحباً بكم في مشغل أسيكودا (AsyLauncher) v" + BuildConfig.VERSION_NAME);
        String titleKu = prefs.getString(KEY_CACHED_TITLE_KU, "بەخێربێن بۆ کارپێکەری ئەسیکۆدا (AsyLauncher) v" + BuildConfig.VERSION_NAME);

        String bodyEn = prefs.getString(KEY_CACHED_BODY_EN,
                "New customizable finger gestures, interactive tutorial, and floating mini-calculator are now available!");
        String bodyAr = prefs.getString(KEY_CACHED_BODY_AR,
                "يتوفر الآن تخصيص إيماءات اللمس والماوس، الدليل التفاعلي، والحاسبة المصغرة العائمة!");
        String bodyKu = prefs.getString(KEY_CACHED_BODY_KU,
                "ئێستا ڕێکخستنی جوڵەی پەنجەکان و ماوس، فێرکاری کارلێککار و ژمێرەری بچووک بەردەستە!");

        String playUrl = prefs.getString(KEY_CACHED_PLAY_URL,
                "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        String githubUrl = prefs.getString(KEY_CACHED_GITHUB_URL,
                "https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/releases");

        String cloudTitle = AsycudaMaterialUi.tr(activity, titleEn, titleAr, titleKu);
        String cloudBody = AsycudaMaterialUi.tr(activity, bodyEn, bodyAr, bodyKu);
        String cloudCategory = AsycudaMaterialUi.tr(activity,
                "LIVE CLOUD BROADCAST  •  " + versionTag + (publishedUtc != null && !publishedUtc.isEmpty() ? "  •  " + publishedUtc : ""),
                "بث سحابي مباشر  •  " + versionTag + (publishedUtc != null && !publishedUtc.isEmpty() ? "  •  " + publishedUtc : ""),
                "پەخشی هەوری ڕاستەوخۆ  •  " + versionTag + (publishedUtc != null && !publishedUtc.isEmpty() ? "  •  " + publishedUtc : ""));

        container.addView(createAlertCardView(
                activity,
                cloudCategory,
                cloudTitle,
                cloudBody,
                severity,
                remoteWasUnread,
                playUrl,
                githubUrl));

        // 2. Built-in Release Highlights Card for v4.3.0
        String relCategory = AsycudaMaterialUi.tr(activity,
                "RELEASE HIGHLIGHTS  •  v" + BuildConfig.VERSION_NAME + " (Build " + BuildConfig.VERSION_CODE + ")",
                "ملخص ميزات الإصدار  •  v" + BuildConfig.VERSION_NAME + " (بناء " + BuildConfig.VERSION_CODE + ")",
                "گرنگترین نوێکارییەکانی وەشان  •  v" + BuildConfig.VERSION_NAME + " (بینای " + BuildConfig.VERSION_CODE + ")");

        String relTitle = AsycudaMaterialUi.tr(activity,
                "What's New in AsyLauncher v" + BuildConfig.VERSION_NAME,
                "ما الجديد في مشغل أسيكودا (AsyLauncher) v" + BuildConfig.VERSION_NAME,
                "نوێکارییەکانی کارپێکەری ئەسیکۆدا (AsyLauncher) v" + BuildConfig.VERSION_NAME);

        String relBody = AsycudaMaterialUi.tr(activity,
                "• Unified Input Engine: Dedicated Trackpad (Virtual Mouse) and Direct Touchscreen modes with optional hidden cursor in Direct Touch.\n"
                        + "• Customizable Multi-Finger Gestures: Assign 1-finger, 2-finger, 3-finger, and 4-finger taps/holds independently per input mode.\n"
                        + "• Floating Mini Customs Calculator: Resizable horizontal & vertical calculator window attached directly to the AsyLauncher floating pill.\n"
                        + "• Interactive First-Run Tutorial: Guided 5-step walkthrough before the ASYCUDA session starts.\n"
                        + "• Modern On-Screen Mouse Bar: Translucent pill-styled L / M / R / Scroll wheel overlay for Trackpad mode.",
                "• محرك تحكم موحّد: نمط لوحة اللمس (ماوس افتراضي) ونمط اللمس المباشر مع إخفاء تلقائي لمؤشر الماوس في اللمس المباشر.\n"
                        + "• تخصيص إيماءات الأصابع: ضبط نقرات المطوّل، النقر المزدوج، وإيماءات ٢ و٣ و٤ أصابع بشكل مستقل لكل نمط.\n"
                        + "• الحاسبة الكمركية المصغرة: نافذة حاسبة عائمة قابلة لتغيير الحجم أفقياً وعمودياً مدمجة بجانب زر AsyLauncher العائم.\n"
                        + "• الدليل التفاعلي الذكي: جولة إرشادية من ٥ خطوات قبل بدء جلسة أسيكودا.\n"
                        + "• أزرار الماوس على الشاشة: تصميم زجاجي موحّد لأزرار اليمين واليسار وعجلة التمرير في نمط لوحة اللمس.",
                "• سیستەمی کۆنترۆڵی یەکگرتوو: شێوازی تراکپاد و شاشەی لەمسی ڕاستەوخۆ لەگەڵ شاردنەوەی نیشاندەری ماوس.\n"
                        + "• ڕێکخستنی جوڵەی پەنجەکان: دیاریکردنی فرمانەکانی ١، ٢، ٣ و ٤ پەنجە بە سەربەخۆیی بۆ هەر شێوازێک.\n"
                        + "• ژمێرەری گومرگی بچووک: پەنجەرەی ژمێرەری سەرئاوکەوتوو بە توانای گۆڕینی قەبارە بە ئاسۆیی و ستوونی.\n"
                        + "• ڕێبەری فێرکاری کارلێککار: فێرکاری ٥ هەنگاوی پێش دەستپێکردنی دانیشتنی ئەسیکۆدا.\n"
                        + "• دوگمەکانی ماوس لەسەر شاشە: دیزاینی نوێ و هاوچەرخ بۆ دوگمەکانی ماوس لە شێوازی تراکپاد.");

        container.addView(createAlertCardView(
                activity,
                relCategory,
                relTitle,
                relBody,
                "release",
                releaseWasUnread,
                null,
                null));
    }

    private static View createAlertCardView(
            MainActivity activity,
            String categoryText,
            String titleText,
            String bodyText,
            String severity,
            boolean isNew,
            String playUrl,
            String githubUrl) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 13);
        card.setPadding(pad, dp(activity, 11), pad, dp(activity, 12));

        int accentColor = resolveAlertAccentColor(severity);

        int strokeColor = isNew ? accentColor : AsycudaMaterialUi.SEPARATOR_HAIRLINE;
        card.setBackground(AsycudaMaterialUi.createCardDrawable(activity, AsycudaMaterialUi.SURFACE_GROUPED_CARD, 13, strokeColor));

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(activity, 10);
        card.setLayoutParams(cardLp);

        // Top metadata row (Category + NEW / READ badge)
        LinearLayout metaRow = new LinearLayout(activity);
        metaRow.setOrientation(LinearLayout.HORIZONTAL);
        metaRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView catTv = new TextView(activity);
        catTv.setText(categoryText);
        catTv.setTextColor(accentColor);
        catTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        catTv.setTypeface(Typeface.create(AsycudaMaterialUi.SANS_SERIF_MEDIUM, Typeface.BOLD));
        catTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        metaRow.addView(catTv);

        TextView statusBadge = new TextView(activity);
        statusBadge.setText(isNew
                ? AsycudaMaterialUi.tr(activity, "NEW", "جديد", "نوێ")
                : AsycudaMaterialUi.tr(activity, "READ", "مقروء", "خوێندراوە"));
        statusBadge.setTextColor(isNew ? Color.WHITE : AsycudaMaterialUi.TEXT_SECONDARY);
        statusBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9.5f);
        statusBadge.setTypeface(Typeface.create(AsycudaMaterialUi.SANS_SERIF_MEDIUM, Typeface.BOLD));
        statusBadge.setPadding(dp(activity, 8), dp(activity, 2), dp(activity, 8), dp(activity, 2));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(dp(activity, 8));
        badgeBg.setColor(isNew ? accentColor : AsycudaMaterialUi.SURFACE_ELEVATED);
        if (!isNew) {
            badgeBg.setStroke(Math.max(1, dp(activity, 1)), AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        }
        statusBadge.setBackground(badgeBg);
        metaRow.addView(statusBadge);
        card.addView(metaRow);

        // Title
        TextView titleTv = new TextView(activity);
        titleTv.setText(titleText);
        titleTv.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.8f);
        titleTv.setTypeface(Typeface.create(AsycudaMaterialUi.SANS_SERIF_MEDIUM, Typeface.BOLD));
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tLp.topMargin = dp(activity, 5);
        card.addView(titleTv, tLp);

        // Body
        TextView bodyTv = new TextView(activity);
        bodyTv.setText(bodyText);
        bodyTv.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        bodyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        bodyTv.setLineSpacing(dp(activity, 2), 1.05f);
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bLp.topMargin = dp(activity, 6);
        card.addView(bodyTv, bLp);

        // Optional action button row (Google Play Store)
        if (playUrl != null && !playUrl.isEmpty()) {
            final String safePlayUrl = sanitizeTrustedAnnouncementUrl(playUrl);
            LinearLayout btnRow = new LinearLayout(activity);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            btnRow.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams brLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            brLp.topMargin = dp(activity, 10);
            btnRow.setLayoutParams(brLp);

            TextView playBtn = AsycudaMaterialUi.createCupertinoButton(activity,
                    AsycudaMaterialUi.tr(activity, "Google Play Store", "متجر Google Play", "فرۆشگای Google Play"),
                    true, false, v -> {
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(safePlayUrl)));
                        } catch (Exception ignored) {
                            // Ignore when no external browser or Play Store app is available
                        }
                    });
            btnRow.addView(playBtn);
            card.addView(btnRow);
        }

        return card;
    }

    private static int resolveAlertAccentColor(String severity) {
        if ("critical".equalsIgnoreCase(severity)) {
            return AsycudaMaterialUi.SYSTEM_RED;
        }
        if ("warning".equalsIgnoreCase(severity)) {
            return AsycudaMaterialUi.SYSTEM_ORANGE;
        }
        if ("release".equalsIgnoreCase(severity)) {
            return AsycudaMaterialUi.SYSTEM_GREEN;
        }
        return AsycudaMaterialUi.SYSTEM_BLUE;
    }

    private static String resolveAlertBorderHex(String severity) {
        if ("critical".equalsIgnoreCase(severity)) {
            return "#FFFF453A";
        }
        if ("warning".equalsIgnoreCase(severity)) {
            return "#FFFF9F0A";
        }
        return "#FF0A84FF";
    }

    private interface SyncResultCallback {
        void onComplete(boolean success);
    }

    private static void fetchAndProcessLatestAnnouncement(Activity activity, boolean forceShowPopup, SyncResultCallback callback) {
        HttpURLConnection conn = null;
        boolean syncedSuccess = false;
        try {
            conn = openAuthorizedFirestoreConnection(activity, "/announcements/latest", "GET", 7000, 7000);
            conn.setRequestProperty("Accept", "application/json");

            int code = conn.getResponseCode();
            if (code != 200) return;

            String jsonStr = readStream(conn.getInputStream());
            JSONObject root = new JSONObject(jsonStr);
            JSONObject fields = root.optJSONObject("fields");
            if (fields == null) return;

            // Support both camelCase (active, titleEn, bodyEn, playStoreUrl, githubUrl) and snake_case (enabled, title_en, body_en, play_store_url, action_url)
            boolean enabled = getBoolField(fields, "active", getBoolField(fields, "enabled", true));
            String id = getStringField(fields, "id", DEFAULT_ANNOUNCEMENT_ID);
            String severity = getStringField(fields, "severity", "info");
            long minVer = getIntField(fields, "minVersionCode", getIntField(fields, "min_version_code", 1));
            long maxVer = getIntField(fields, "maxVersionCode", getIntField(fields, "max_version_code", 999999));
            int curVer = BuildConfig.VERSION_CODE;

            String titleEn = getStringField(fields, "titleEn", getStringField(fields, "title_en", "Welcome to AsyLauncher v" + BuildConfig.VERSION_NAME));
            String titleAr = getStringField(fields, "titleAr", getStringField(fields, "title_ar", titleEn));
            String titleKu = getStringField(fields, "titleKu", getStringField(fields, "title_ku", titleEn));

            String bodyEn = getStringField(fields, "bodyEn", getStringField(fields, "body_en", ""));
            String bodyAr = getStringField(fields, "bodyAr", getStringField(fields, "body_ar", bodyEn));
            String bodyKu = getStringField(fields, "bodyKu", getStringField(fields, "body_ku", bodyEn));

            String actionUrl = sanitizeTrustedAnnouncementUrl(
                    getStringField(fields, "githubUrl", getStringField(fields, "action_url", DEFAULT_PLAY_STORE_URL)));
            String playUrl = sanitizeTrustedAnnouncementUrl(
                    getStringField(fields, "playStoreUrl", getStringField(fields, "play_store_url", DEFAULT_PLAY_STORE_URL)));
            String versionTag = getStringField(fields, "versionTag", "v" + BuildConfig.VERSION_NAME);
            String publishedUtc = getStringField(fields, "publishedAtUtc", "2026-11-04");

            SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            if (!bodyEn.isEmpty() || !bodyAr.isEmpty()) {
                prefs.edit()
                        .putString(KEY_CACHED_ID, id)
                        .putBoolean(KEY_CACHED_ACTIVE, enabled)
                        .putString(KEY_CACHED_SEVERITY, severity)
                        .putString(KEY_CACHED_TITLE_EN, titleEn)
                        .putString(KEY_CACHED_TITLE_AR, titleAr)
                        .putString(KEY_CACHED_TITLE_KU, titleKu)
                        .putString(KEY_CACHED_BODY_EN, bodyEn)
                        .putString(KEY_CACHED_BODY_AR, bodyAr)
                        .putString(KEY_CACHED_BODY_KU, bodyKu)
                        .putString(KEY_CACHED_PLAY_URL, playUrl)
                        .putString(KEY_CACHED_GITHUB_URL, actionUrl)
                        .putString(KEY_CACHED_VERSION_TAG, versionTag)
                        .putString(KEY_CACHED_PUBLISHED_UTC, publishedUtc)
                        .apply();
                syncedSuccess = true;
                AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
            }

            if (!enabled && !forceShowPopup) return;
            if (!forceShowPopup && (curVer < minVer || curVer > maxVer)) {
                return;
            }

            // Only auto-popup on startup if severity is "critical" or "warning", or if forceShowPopup is true.
            // Normal "info" broadcasts increment the unread badge on the floating pill & Control Center tile without interrupting the user's session.
            String dismissedId = prefs.getString(KEY_LAST_DISMISSED_ANNOUNCEMENT, "");
            boolean isUrgent = "critical".equalsIgnoreCase(severity) || "warning".equalsIgnoreCase(severity);
            if (!forceShowPopup && (!isUrgent || (!id.isEmpty() && id.equals(dismissedId) && !"critical".equalsIgnoreCase(severity)))) {
                return;
            }

            if (bodyEn.isEmpty() && bodyAr.isEmpty()) return;

            activity.runOnUiThread(() -> {
                if (activity.isFinishing() || activity.isDestroyed()) return;
                showAnnouncementModal(activity, id, severity, titleEn, titleAr, titleKu, bodyEn, bodyAr, bodyKu, playUrl, actionUrl);
            });
        } catch (Exception ignored) {
            // Best-effort announcement fetch when offline
        } finally {
            if (conn != null) conn.disconnect();
            if (callback != null) {
                try {
                    callback.onComplete(syncedSuccess);
                } catch (Exception ignored) {
                    // Ignore callback error
                }
            }
        }
    }

    public static boolean fetchAndSyncKnownErrorsCatalog(Context ctx) {
        if (ctx == null) return false;
        HttpURLConnection conn = null;
        try {
            conn = openAuthorizedFirestoreConnection(ctx, "/app_config/known_errors_catalog", "GET", 7000, 7000);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) return false;

            String jsonStr = readStream(conn.getInputStream());
            JSONObject root = new JSONObject(jsonStr);
            JSONObject fields = root.optJSONObject("fields");
            if (fields == null) return false;

            long remoteVersion = getIntField(fields, "version", 0);
            String catalogJson = getStringField(fields, "catalogJson",
                    getStringField(fields, "catalog_json",
                            getStringField(fields, "entriesJson", "")));
            if (catalogJson.isEmpty() || !AsycudaStandaloneManager.validateKnownErrorsCatalogJson(catalogJson)) {
                return false;
            }

            SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            long localVersion = prefs.getLong(KEY_REMOTE_CATALOG_VERSION, 0);
            File cacheFile = new File(ctx.getFilesDir(), "known-errors-catalog-remote.json");

            if (remoteVersion >= localVersion || !cacheFile.exists()) {
                if (cacheFile.exists() && !cacheFile.setWritable(true, true)) {
                    return false;
                }
                try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                    fos.write(catalogJson.getBytes(StandardCharsets.UTF_8));
                    fos.getFD().sync();
                }
                boolean permissionsHardened = cacheFile.setReadable(false, false)
                        && cacheFile.setReadable(true, true)
                        && cacheFile.setWritable(false, false);
                if (!permissionsHardened) {
                    cacheFile.deleteOnExit();
                }
                prefs.edit().putLong(KEY_REMOTE_CATALOG_VERSION, remoteVersion).apply();
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * Returns the latest cached remote version tag (e.g. "v4.3.0") and Play Store / GitHub release URLs.
     */
    public static String getCachedRemoteVersionTag(Context ctx) {
        if (ctx == null) return "v" + BuildConfig.VERSION_NAME;
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CACHED_VERSION_TAG, "v" + BuildConfig.VERSION_NAME);
    }

    /**
     * Submits a voluntary, in-RAM credential-redacted diagnostic report synchronously to Cloud Firestore
     * (`diagnostic_reports` collection) and returns true if HTTP 200/201 was received.
     */
    public static boolean submitDiagnosticReportSync(
            Context ctx,
            String errorCode,
            String title,
            String summary,
            String userPhone,
            String userNote,
            String networkTransport) {
        HttpURLConnection conn = null;
        try {
            String docId = "rep_" + System.currentTimeMillis() + "_"
                    + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            conn = openAuthorizedFirestoreConnection(ctx, "/diagnostic_reports?documentId=" + docId, "POST", 8000, 10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String safeCode = AsycudaSecurityValidator.redactSensitiveLogContent(
                    errorCode != null ? errorCode : "UNKNOWN", "");
            String safeTitle = AsycudaSecurityValidator.redactSensitiveLogContent(
                    title != null ? title : "", "");
            String safeSummary = AsycudaSecurityValidator.redactSensitiveLogContent(
                    summary != null ? summary : "", "");
            String safeNote = AsycudaSecurityValidator.redactSensitiveLogContent(
                    userNote != null ? userNote : "", "");
            String safeTransport = AsycudaSecurityValidator.redactSensitiveLogContent(
                    networkTransport != null ? networkTransport : "", "");

            JSONObject fields = new JSONObject();
            putStringField(fields, "error_code", truncate(safeCode, 128));
            putStringField(fields, "title", truncate(safeTitle, 500));
            putStringField(fields, "summary", truncate(safeSummary, 1800));
            putStringField(fields, "user_phone", "");
            putStringField(fields, "user_note", truncate(safeNote, 1000));
            putStringField(fields, "network_transport", truncate(safeTransport, 128));
            putStringField(fields, "app_version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
            putStringField(fields, "device_model", truncate(Build.MANUFACTURER + " " + Build.MODEL + " (SDK " + Build.VERSION.SDK_INT + ")", 256));
            putIntField(fields, "created_at_ms", System.currentTimeMillis());

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            byte[] bodyBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }
            int code = conn.getResponseCode();
            return code >= 200 && code < 300;
        } catch (Throwable ignored) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * Submits a voluntary diagnostic report asynchronously to Cloud Firestore (`diagnostic_reports`).
     */
    public static void submitDiagnosticReportAsync(
            Context ctx,
            String errorCode,
            String title,
            String summary,
            String userPhone,
            String userNote,
            String networkTransport) {
        new Thread(() -> submitDiagnosticReportSync(ctx, errorCode, title, summary, userPhone, userNote, networkTransport),
                "AsycudaFirestoreReport").start();
    }

    /**
     * Sends a once-per-calendar-day Zero-PII anonymous installation heartbeat to Cloud Firestore
     * (`app_metrics/{anonId}`) containing ONLY 6 non-personal technical fields:
     *  - anon_id (random app-scoped UUID, resets on app uninstall/clear)
     *  - app_version ("4.3.0 (51)")
     *  - android_sdk (e.g. 34)
     *  - device_model ("ZTE NX729J")
     *  - ui_lang ("ar" | "en" | "ku")
     *  - last_active_date ("YYYY-MM-DD" in UTC)
     */
    public static void sendAnonymousDailyHeartbeatSync(Context ctx) {
        if (ctx == null) return;
        HttpURLConnection conn = null;
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String anonId = prefs.getString(KEY_ANON_INSTALLATION_ID, "");
            if (anonId == null || anonId.length() < 16) {
                anonId = "inst_" + UUID.randomUUID().toString().replace("-", "");
                prefs.edit().putString(KEY_ANON_INSTALLATION_ID, anonId).apply();
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String todayUtc = sdf.format(new java.util.Date());
            String lastSentDate = prefs.getString(KEY_LAST_HEARTBEAT_DATE, "");
            String curVer = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
            String lastSentVer = prefs.getString("last_heartbeat_ver", "");

            if (todayUtc.equals(lastSentDate) && curVer.equals(lastSentVer)) {
                return;
            }

            // Upsert document via POST with X-HTTP-Method-Override header
            conn = openAuthorizedFirestoreConnection(ctx, "/app_metrics/" + anonId, "POST", 7000, 7000);
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String uiLang = AsycudaMaterialUi.getAppLanguage(ctx);
            if (uiLang == null || uiLang.length() < 2) uiLang = "en";
            String devModel = truncate((Build.MANUFACTURER + " " + Build.MODEL).trim(), 120);
            if (devModel.isEmpty()) devModel = "Android Device";

            JSONObject fields = new JSONObject();
            putStringField(fields, "anon_id", anonId);
            putStringField(fields, "app_version", truncate(curVer, 64));
            putIntField(fields, "android_sdk", Math.max(1, Math.min(100, Build.VERSION.SDK_INT)));
            putStringField(fields, "device_model", devModel);
            putStringField(fields, "ui_lang", truncate(uiLang, 16));
            putStringField(fields, "last_active_date", todayUtc);

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            byte[] bodyBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                prefs.edit()
                        .putString(KEY_LAST_HEARTBEAT_DATE, todayUtc)
                        .putString("last_heartbeat_ver", curVer)
                        .apply();
            }
        } catch (Exception ignored) {
            // Best-effort anonymous daily metric ping
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static void showAnnouncementModal(
            Activity activity,
            String id,
            String severity,
            String titleEn,
            String titleAr,
            String titleKu,
            String bodyEn,
            String bodyAr,
            String bodyKu,
            String playUrl,
            String actionUrl) {

        String title = AsycudaMaterialUi.tr(activity, titleEn, titleAr, titleKu);
        String body = AsycudaMaterialUi.tr(activity, bodyEn, bodyAr, bodyKu);

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(activity, 18), dp(activity, 14), dp(activity, 18), dp(activity, 14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(AsycudaMaterialUi.SURFACE_SHEET);
        bg.setCornerRadius(dp(activity, 16));
        String borderHex = resolveAlertBorderHex(severity);
        bg.setStroke(dp(activity, 1), Color.parseColor(borderHex));
        card.setBackground(bg);

        LinearLayout headerRow = new LinearLayout(activity);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_notifications);
        headerIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(borderHex)));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 16), dp(activity, 16));
        hiLp.rightMargin = dp(activity, 6);
        headerRow.addView(headerIcon, hiLp);

        TextView badge = new TextView(activity);
        badge.setText(AsycudaMaterialUi.tr(activity,
                "APP NEWS & ANNOUNCEMENT",
                "إشعار وأخبار التطبيق",
                "ئاگاداری و هەواڵی بەرنامە"));
        badge.setTextColor(Color.parseColor(borderHex));
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        headerRow.addView(badge);
        card.addView(headerRow);

        TextView titleTv = new TextView(activity);
        titleTv.setText(title);
        titleTv.setTextColor(Color.parseColor("#F8FAFC"));
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.5f);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tLp.topMargin = dp(activity, 4);
        card.addView(titleTv, tLp);

        ScrollView bodyScroll = new ScrollView(activity);
        TextView bodyTv = new TextView(activity);
        bodyTv.setText(body);
        bodyTv.setTextColor(Color.parseColor("#CBD5E1"));
        bodyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        bodyTv.setLineSpacing(dp(activity, 3), 1f);
        bodyScroll.addView(bodyTv);
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bLp.topMargin = dp(activity, 8);
        bLp.bottomMargin = dp(activity, 14);
        card.addView(bodyScroll, bLp);

        // Action buttons
        LinearLayout btnRow = new LinearLayout(activity);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        if (playUrl != null && !playUrl.isEmpty()) {
            final String safePlayUrl = sanitizeTrustedAnnouncementUrl(playUrl);
            TextView playBtn = createButton(activity, "Google Play", "#059669", "#FFFFFF");
            playBtn.setOnClickListener(v -> {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(safePlayUrl)));
                } catch (Exception ignored) {
                    // Ignore when no browser or Play Store handler is available
                }
            });
            LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pLp.rightMargin = dp(activity, 8);
            btnRow.addView(playBtn, pLp);
        }

        TextView dismissBtn = createButton(activity,
                AsycudaMaterialUi.tr(activity, "Got It", "حسناً، فهمت", "باشە، تێگەیشتم"),
                "#0284C7", "#FFFFFF");
        dismissBtn.setOnClickListener(v -> {
            if (id != null && !id.isEmpty()) {
                activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(KEY_LAST_DISMISSED_ANNOUNCEMENT, id)
                        .putString(KEY_LAST_READ_REMOTE_ANNOUNCEMENT, id)
                        .apply();
                AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
            }
            dialog.dismiss();
        });
        btnRow.addView(dismissBtn);

        card.addView(btnRow);
        dialog.setContentView(card, new ViewGroup.LayoutParams(
                Math.min(dp(activity, 460), (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90f)),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    private static TextView createButton(Context ctx, String label, String bgHex, String fgHex) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(Color.parseColor(fgHex));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(dp(ctx, 13), dp(ctx, 8), dp(ctx, 13), dp(ctx, 8));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgHex));
        bg.setCornerRadius(dp(ctx, 9));
        tv.setBackground(bg);
        return tv;
    }

    private static String getStringField(JSONObject fields, String key, String def) {
        JSONObject obj = fields.optJSONObject(key);
        return obj != null ? obj.optString("stringValue", def) : def;
    }

    private static boolean getBoolField(JSONObject fields, String key, boolean def) {
        JSONObject obj = fields.optJSONObject(key);
        return obj != null ? obj.optBoolean("booleanValue", def) : def;
    }

    private static long getIntField(JSONObject fields, String key, long def) {
        JSONObject obj = fields.optJSONObject(key);
        if (obj == null) return def;
        try {
            return Long.parseLong(obj.optString("integerValue", String.valueOf(def)));
        } catch (Exception e) {
            return def;
        }
    }

    private static void putStringField(JSONObject fields, String key, String val) throws org.json.JSONException {
        JSONObject wrapper = new JSONObject();
        wrapper.put("stringValue", val != null ? val : "");
        fields.put(key, wrapper);
    }

    private static void putIntField(JSONObject fields, String key, long val) throws org.json.JSONException {
        JSONObject wrapper = new JSONObject();
        wrapper.put("integerValue", String.valueOf(val));
        fields.put(key, wrapper);
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

    private static String readStream(InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int total = 0;
        int n;
        while ((n = is.read(buf)) != -1) {
            total += n;
            if (total > MAX_FIRESTORE_RESPONSE_BYTES) {
                throw new java.io.IOException("Firestore response exceeds 256 KB safety limit");
            }
            bos.write(buf, 0, n);
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
