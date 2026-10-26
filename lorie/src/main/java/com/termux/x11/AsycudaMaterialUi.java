package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

@SuppressWarnings({"java:S3776", "java:S107", "java:S1141", "java:S1192", "java:S9439"})
public class AsycudaMaterialUi {

    private static final String PREFS_NAME = "asycuda_ui_prefs";
    private static final String CONSENT_PREFS_NAME = "asycuda_legal_consent";
    public static final String CURRENT_POLICY_VERSION = "2.0-2026.10";
    public static final int CURRENT_CHANGELOG_VERSION = 43;
    private static final String KEY_LAST_CHANGELOG_VER = "last_seen_changelog_version";
    private static final String KEY_APP_LANG = "app_lang";
    private static final String KEY_AUTO_KEYBOARD = "auto_show_keyboard";
    private static final String KEY_ASSIST_KEYS = "assist_keys_enabled";

    private static final String SANS_SERIF_MEDIUM = "sans-serif-medium";
    private static final String KU_DONE = "تەواو";
    private static final String EN_BACK_CONTROL_CENTER = "← Back";
    private static final String AR_BACK_CONTROL_CENTER = "← رجوع";
    private static final String KU_BACK_CONTROL_CENTER = "← گەڕانەوە";
    private static final String PERMISSION_INTERNAL_BROADCAST = "iq.asycuda.PERMISSION_INTERNAL_BROADCAST";
    private static final String SONAR_DASHBOARD_URL = "https://sonarcloud.io/summary/new_code?id=MohammedALShimmary_ASYCUDA-Iraq-Android";
    private static final String SONAR_MEASURES_API_URL = "https://sonarcloud.io/api/measures/component?component=MohammedALShimmary_ASYCUDA-Iraq-Android&metricKeys=alert_status,bugs,vulnerabilities,code_smells,security_rating,reliability_rating,sqale_rating";
    private static final String AR_ON = "مفعّل";
    private static final String KU_ON = "چالاک";
    private static final String AR_OFF = "متوقف";
    private static final String KU_OFF = "ناچالاک";
    private static final String KEY_FROM_BROADCAST = "fromBroadcast";
    private static final String MODE_SCALED = "scaled";
    private static final String MODE_EXACT = "exact";
    private static final String MODE_CUSTOM = "custom";
    private static final String TIMEOUT_NEVER = "never";
    private static final String ACTION_DOUBLE_BACK_EXIT = "double_back_exit";
    private static final String KEY_ACCEPTED = "accepted";
    private static final String KEY_POLICY_VERSION = "policy_version";
    private static final String KEY_CLOUD_SYNCED = "cloud_synced";
    private static final String KEY_CONSENT_ID = "consent_id";
    private static final String KEY_SIGNATORY_NAME = "signatory_name";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String KEY_ACCEPTED_UTC = "accepted_at_utc";
    private static final String KEY_ACCEPTED_LOCAL = "accepted_at_local";
    private static final String KEY_DEVICE_HASH = "device_hash";
    private static final String KEY_SHA256_SIG = "sha256_signature";

    // Dark Mode Palette
    public static final int SURFACE_SHEET         = Color.parseColor("#F21C1C1E");
    public static final int SURFACE_GROUPED_CARD  = Color.parseColor("#FF2C2C2E");
    public static final int SURFACE_INSET_CARD    = Color.parseColor("#FF242426");
    public static final int SURFACE_INPUT_FIELD   = Color.parseColor("#FF141416");
    public static final int SEPARATOR_HAIRLINE    = Color.parseColor("#FF38383A");
    public static final int GRABBER_COLOR         = Color.parseColor("#FF5A5A5E");

    public static final int SYSTEM_BLUE           = Color.parseColor("#FF0A84FF");
    public static final int SYSTEM_BLUE_TINT      = Color.parseColor("#240A84FF");
    public static final int SYSTEM_GREEN          = Color.parseColor("#FF30D158");
    public static final int SYSTEM_GREEN_TINT     = Color.parseColor("#2430D158");
    public static final int SYSTEM_RED            = Color.parseColor("#FFFF453A");
    public static final int SYSTEM_RED_TINT       = Color.parseColor("#24FF453A");
    public static final int SYSTEM_GRAY_FILL      = Color.parseColor("#FF3A3A3C");

    public static final int TEXT_PRIMARY          = Color.parseColor("#FFFFFFFF");
    public static final int TEXT_SECONDARY        = Color.parseColor("#FF8E8E93");
    public static final int TEXT_TERTIARY         = Color.parseColor("#FF636366");
    public static final int RIPPLE_HIGHLIGHT      = Color.parseColor("#28FFFFFF");

    public interface MenuActionCallback {
        void onToggleAutoKeyboard(boolean newState);
        void onToggleAssistKeys(boolean newState);
        void onOpenFiles();
        void onOpenDisplaySettings();
        void onCheckUpdates();
        void onRestartSession();
        void onOpenFeedbackModal();
        void onOpenAbout();
        void onExitApplication();
        void onLanguageChanged();
        void onOpenUrl(String url);
        void onOpenFile(File f);
        void onShareFile(File f);
        void onShareMultipleFiles(List<File> files);
        void onDeleteFiles(List<File> files);
        default void onImportFileToVault(File targetDir, Runnable onImported) {}
    }

    public interface UpdateCenterController {
        void setAppUpdateStatus(String statusText, int progressPercent, boolean indeterminate);
        void showAppUpdateAvailable(String versionTag, String notesSummary, Runnable onInstallClicked);
        void setMofUpdateStatus(String statusText, String detailText, int current, int total, boolean indeterminate);
        void onCheckFinished(int updatedJarsCount, boolean appUpdatePending, Runnable onRestartNow);
    }

    public interface FeedbackModalController {
        void onImageAttached(File imageFile, String displayName);
        void setUploadState(boolean uploading, String statusMessage, boolean success);
    }

    public interface FeedbackSubmitHandler {
        void onCaptureScreenRequested(FeedbackModalController controller, Dialog feedbackDialog);
        void onPickImageRequested(FeedbackModalController controller);
        void onSubmitToSentry(String description, File attachedImage, FeedbackModalController controller, Dialog feedbackDialog);
        default StringBuilder buildRedactedDiagnosticPreviewInRam(String description) {
            return new StringBuilder();
        }
    }

    public static String getAppLanguage(Context ctx) {
        if (ctx == null) return "en";
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String saved = sp.getString(KEY_APP_LANG, null);
        if (saved != null && !saved.isEmpty()) {
            return saved;
        }
        String sys = Locale.getDefault().getLanguage();
        if ("ar".equalsIgnoreCase(sys)) return "ar";
        if ("ku".equalsIgnoreCase(sys) || "ckb".equalsIgnoreCase(sys)) return "ku";
        return "en";
    }

    public static void setAppLanguage(Context ctx, String langCode) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_APP_LANG, langCode)
                .apply();
    }

    public static boolean isAutoKeyboardEnabled(Context ctx) {
        if (ctx == null) return true;
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_AUTO_KEYBOARD, true);
    }

    public static void setAutoKeyboardEnabled(Context ctx, boolean enabled) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_AUTO_KEYBOARD, enabled)
                .apply();
    }

    public static boolean isAssistKeysEnabled(MainActivity activity) {
        if (activity == null) return true;
        try {
            if (activity.prefs != null) {
                return activity.prefs.showAdditionalKbd.get() && activity.prefs.additionalKbdVisible.get();
            }
        } catch (Throwable ignored) {
            // Fallback to SharedPreferences if activity prefs are uninitialized
        }
        return activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ASSIST_KEYS, true);
    }

    public static void setAssistKeysEnabled(MainActivity activity, boolean enabled) {
        if (activity == null) return;
        activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_ASSIST_KEYS, enabled)
                .apply();
        try {
            if (activity.prefs != null) {
                activity.prefs.showAdditionalKbd.put(true);
                activity.prefs.additionalKbdVisible.put(enabled);
            }
            activity.toggleExtraKeys(enabled, true);
        } catch (Throwable ignored) {
            // Ignore toolbar toggle error if view hierarchy is not ready
        }
    }

    public static boolean isRtl(Context ctx) {
        String lang = getAppLanguage(ctx);
        return "ar".equals(lang) || "ku".equals(lang);
    }

    public static String tr(Context ctx, String en, String ar, String ku) {
        String lang = getAppLanguage(ctx);
        if ("ar".equals(lang)) return ar;
        if ("ku".equals(lang)) return ku;
        return en;
    }

    public static String getCurrentLanguageDisplayName(Context ctx) {
        String lang = getAppLanguage(ctx);
        if ("ar".equals(lang)) return "العربية";
        if ("ku".equals(lang)) return "کوردی";
        return "English";
    }

    public static Context wrapLocaleContext(Context base) {
        if (base == null) return null;
        try {
            String lang = getAppLanguage(base);
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration(base.getResources().getConfiguration());
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            return base.createConfigurationContext(config);
        } catch (Throwable ignored) {
            return base;
        }
    }

    public static String getAppVersionLabel(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            if (pi != null && pi.versionName != null && !pi.versionName.isEmpty()) {
                return "v" + pi.versionName;
            }
        } catch (Throwable ignored) {
            // Fallback to current release version label
        }
        return "v4.1.0";
    }

    public static boolean shouldShowWhatsNew(Context ctx) {
        if (ctx == null) return false;
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_LAST_CHANGELOG_VER, 0) < CURRENT_CHANGELOG_VERSION;
    }

    public static void markWhatsNewSeen(Context ctx) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_LAST_CHANGELOG_VER, CURRENT_CHANGELOG_VERSION)
                .apply();
    }

    public static void showControlCenter(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.82f, 590, true);
        LinearLayout root = createDialogContainer(activity);

        // Header Row
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(activity, 32), dp(activity, 32));
        logoLp.setMarginEnd(dp(activity, 10));
        header.addView(logo, logoLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "ASYCUDA World — Republic of Iraq",
                "نظام أسيكودا العالمي — جمهورية العراق",
                "سیستەمی ئەسیکۆدای جیهانی — کۆماری عێراق"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Control Center  |  Release " + getAppVersionLabel(activity),
                "مركز التحكم  |  الإصدار " + getAppVersionLabel(activity),
                "ناوەندی کۆنترۆڵ  |  وەشانی " + getAppVersionLabel(activity)));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        View langBtn = createIconButton(activity, R.drawable.ic_menu_language,
                getCurrentLanguageDisplayName(activity), false, false, v -> {
                    dialog.dismiss();
                    showLanguageModal(activity, cb);
                });
        LinearLayout.LayoutParams langLp = (LinearLayout.LayoutParams) langBtn.getLayoutParams();
        langLp.setMarginEnd(dp(activity, 6));
        header.addView(langBtn);

        View closeBtn = createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Done", "تم", KU_DONE),
                true, false, v -> dialog.dismiss());
        header.addView(closeBtn);
        root.addView(header);

        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 6);
        scroll.setLayoutParams(scrollLp);

        GridLayout grid = new GridLayout(activity);
        int cols = isCompactWidth(activity) ? 1 : 2;
        grid.setColumnCount(cols);
        grid.setUseDefaultMargins(false);
        grid.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        boolean autoKbd = isAutoKeyboardEnabled(activity);
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_keyboard,
                tr(activity, "Auto-Show Keyboard", "الإظهار التلقائي للكيبورد", "پیشاندانی خۆکاری تەختەکلیل"),
                autoKbd
                        ? tr(activity, "Automatically opens when tapping input fields", "يظهر الكيبورد تلقائياً عند النقر على حقول الإدخال", "خۆکارانە دەکرێتەوە لە کاتی داگرتنی خانەکانی نووسین")
                        : tr(activity, "Disabled (press Back button to open manually)", "متوقف حالياً (اضغط زر الرجوع لإظهاره يدوياً)", "ناچالاکە (دوگمەی گەڕانەوە دابگرە بۆ کردنەوەی)"),
                autoKbd ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                autoKbd, false, () -> {
                    boolean next = !isAutoKeyboardEnabled(activity);
                    setAutoKeyboardEnabled(activity, next);
                    cb.onToggleAutoKeyboard(next);
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                });

        boolean assistKeys = isAssistKeysEnabled(activity);
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_assist_keys,
                tr(activity, "Function & Arrow Keys", "شريط أزرار الوظائف والأسهم", "شریتی دوگمەکانی فرمانی و تیرەکان"),
                assistKeys
                        ? tr(activity, "Shows top bar with TAB, CTRL, ESC, F-keys & arrows", "يعرض شريط أزرار TAB و CTRL و ESC والأسهم في الأعلى", "شریتی سەرەوە بۆ TAB و CTRL و ESC و تیرەکان پیشان دەدات")
                        : tr(activity, "Shortcut bar is hidden for maximum screen space", "شريط الاختصارات مخفي حالياً لتوفير مساحة أكبر للشاشة", "شریتی قەدبڕەکان شاردراوەتەوە بۆ پانتایی زیاتری شاشە"),
                assistKeys ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                assistKeys, false, () -> {
                    boolean next = !isAssistKeysEnabled(activity);
                    setAssistKeysEnabled(activity, next);
                    cb.onToggleAssistKeys(next);
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_cloud_sync,
                tr(activity, "Check for Updates", "فحص التحديثات", "پشکنینی نوێکردنەوەکان"),
                tr(activity, "Verify app releases and Iraqi Customs server packages", "التحقق من تحديثات التطبيق وملفات خادم الكمارك العراقية", "پشکنینی نوێکردنەوەی بەرنامە و پەڕگەکانی سێرڤەری گومرگ"),
                null, true, false, () -> {
                    dialog.dismiss();
                    cb.onCheckUpdates();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_folder,
                tr(activity, "Vault (File Manager)", "الخزنة (مدير الملفات)", "قاسە (بەڕێوەبەری پەڕگەکان)"),
                tr(activity, "Manage folders, import files, and view or share saved PDFs", "إدارة المجلدات واستيراد الملفات وفتح أو مشاركة المستندات", "بەڕێوەبردنی بوخچەکان، هاوردەکردنی پەڕگە و کردنەوەی PDF"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onOpenFiles();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_display,
                tr(activity, "Display & Input Settings", "إعدادات العرض والتحكم", "ڕێکخستنی شاشە و کۆنترۆڵ"),
                tr(activity, "Touch mode, resolution scaling, bar opacity & screen options", "نمط اللمس، دقة العرض، شفافية الشريط وخيارات الشاشة", "شێوازی لمس، وردی شاشە، ڕووناکی شریت و بژاردەکان"),
                null, false, false, () -> {
                    dialog.dismiss();
                    showDisplaySettingsModal(activity, cb);
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_language,
                tr(activity, "Interface Language", "لغة واجهة التطبيق", "زمانی ڕووکاری بەرنامە"),
                tr(activity, "Switch between English, Arabic, and Kurdish", "التبديل بين العربية والإنكليزية والكردية", "گۆڕینی زمان لە نێوان کوردی، عەرەبی و ئینگلیزی"),
                getCurrentLanguageDisplayName(activity), false, false, () -> {
                    dialog.dismiss();
                    showLanguageModal(activity, cb);
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_diagnostics,
                tr(activity, "Report an Issue", "الإبلاغ عن مشكلة فنية", "ناردنی ڕاپۆرتی کێشەی تەکنیکی"),
                tr(activity, "Send a description, screenshot, and diagnostic logs", "إرسال وصف المشكلة مع صورة الشاشة وسجلات الفحص للمطور", "ناردنی وەسفی کێشە لەگەڵ وێنەی شاشە و تۆمارەکان بۆ گەشەپێدەر"),
                null, true, false, () -> {
                    dialog.dismiss();
                    cb.onOpenFeedbackModal();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_restart,
                tr(activity, "Restart ASYCUDA Session", "إعادة تشغيل جلسة أسيكودا", "دووبارە پێکردنەوەی دانیشتنی ئەسیکۆدا"),
                tr(activity, "Reload the ASYCUDA World client without exiting the app", "إعادة تشغيل واجهة النظام دون إغلاق التطبيق بالكامل", "دووبارە بارکردنەوەی سیستەمی ئەسیکۆدا بەبێ داخستنی بەرنامە"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onRestartSession();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_info,
                tr(activity, "About & Open-Source Licences", "حول التطبيق والتراخيص", "دەربارەی بەرنامە و مۆڵەتەکان"),
                tr(activity, "Developer profile, open-source credits & legal disclaimer", "معلومات المطور والمكتبات مفتوحة المصدر وإخلاء المسؤولية", "زانیاری گەشەپێدەر، سەرچاوە کراوەکان و بەرپرسیارێتی یاسایی"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onOpenAbout();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_exit,
                tr(activity, "Exit Application", "إغلاق التطبيق بالكامل", "داخستنی تەواوەتی بەرنامە"),
                tr(activity, "Close active session and stop background services", "إنهاء الجلسة الحالية وإيقاف خدمات الخلفية تماماً", "کۆتاییهێنان بە دانیشتن و ڕاگرتنی خزمەتگوزارییەکانی پاشبنەما"),
                null, false, true, () -> {
                    dialog.dismiss();
                    cb.onExitApplication();
                });

        scroll.addView(grid);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();
    }

    /**
     * Unified Apple-style Display & Input Settings Modal:
     * Covers all essential X11 & input preferences (including Shortcut Bar Opacity,
     * Reserve Space for Shortcut Bar, Display Cutout, Keep Screen Awake, and Back Button Action).
     */
    public static void showDisplaySettingsModal(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.82f, 580, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_display);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Display & Input Settings",
                "إعدادات العرض والتحكم",
                "ڕێکخستنی شاشە و کۆنترۆڵ"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Customize display resolution, shortcut bar opacity, and touch behavior",
                "تخصيص دقة الشاشة، شفافية شريط الاختصارات، ونمط التحكم باللمس",
                "ڕێکخستنی وردی شاشە، ڕووناکی شریتی قەدبڕەکان و شێوازی کۆنترۆڵکردن"));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        header.addView(createIconButton(activity, R.drawable.ic_nav_back,
                tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER),
                false, false, v -> {
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                }));
        dialog.setOnCancelListener(d -> showControlCenter(activity, cb));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 7);
        scroll.setLayoutParams(scrollLp);

        LinearLayout body = new LinearLayout(activity);
        body.setOrientation(LinearLayout.VERTICAL);

        Prefs prefs = activity.prefs;
        Runnable applyChanges = () -> {
            try {
                Intent broadcast = new Intent(LoriePreferences.ACTION_PREFERENCES_CHANGED);
                broadcast.putExtra(KEY_FROM_BROADCAST, true);
                broadcast.setPackage(activity.getPackageName());
                activity.sendBroadcast(broadcast, PERMISSION_INTERNAL_BROADCAST);
                activity.onPreferencesChangedCallback();
            } catch (Exception ignored) { /* Best-effort UI operation */ }
            dialog.dismiss();
            showDisplaySettingsModal(activity, cb);
        };

        // Section 1: Touch Mode
        body.addView(createSectionHeader(activity,
                tr(activity, "TOUCH INPUT MODE", "نمط التحكم باللمس", "شێوازی کۆنترۆڵکردن بە لمس")));

        String curTouch = prefs != null ? prefs.touchMode.get() : "1";
        LinearLayout touchRow = new LinearLayout(activity);
        touchRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        trLp.bottomMargin = dp(activity, 12);
        touchRow.setLayoutParams(trLp);

        addSegmentedChoice(activity, touchRow,
                tr(activity, "Trackpad (Mouse)", "لوحة لمس (ماوس)", "تراکپاد (ماوس)"),
                "1".equals(curTouch), () -> {
                    if (prefs != null) prefs.touchMode.put("1");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, touchRow,
                tr(activity, "Simulated Touch", "لمس محاكي", "لمسی هاوشێوە"),
                "2".equals(curTouch), () -> {
                    if (prefs != null) prefs.touchMode.put("2");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, touchRow,
                tr(activity, "Direct Touch", "لمس مباشر", "لمسی ڕاستەوخۆ"),
                "3".equals(curTouch), () -> {
                    if (prefs != null) prefs.touchMode.put("3");
                    applyChanges.run();
                });
        body.addView(touchRow);

        // Section 2: Display Resolution Mode
        body.addView(createSectionHeader(activity,
                tr(activity, "DISPLAY RESOLUTION & SCALING", "دقة العرض وتكبير النوافذ", "وردی شاشە و قەبارەی پەنجەرەکان")));

        String curResMode = prefs != null ? prefs.displayResolutionMode.get() : MODE_SCALED;
        LinearLayout resRow = new LinearLayout(activity);
        resRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rrLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rrLp.bottomMargin = dp(activity, 8);
        resRow.setLayoutParams(rrLp);

        addSegmentedChoice(activity, resRow,
                tr(activity, "Scaled", "مقياسي", "پێوانەکراو"),
                MODE_SCALED.equals(curResMode), () -> {
                    if (prefs != null) prefs.displayResolutionMode.put(MODE_SCALED);
                    applyChanges.run();
                });
        addSegmentedChoice(activity, resRow,
                tr(activity, "Native", "دقة الجهاز", "وردی ئامێر"),
                "native".equals(curResMode), () -> {
                    if (prefs != null) prefs.displayResolutionMode.put("native");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, resRow,
                tr(activity, "Exact", "ثابتة", "دیاریکراو"),
                MODE_EXACT.equals(curResMode), () -> {
                    if (prefs != null) prefs.displayResolutionMode.put(MODE_EXACT);
                    applyChanges.run();
                });
        addSegmentedChoice(activity, resRow,
                tr(activity, "Custom", "مخصصة", "تایبەت"),
                MODE_CUSTOM.equals(curResMode), () -> {
                    if (prefs != null) prefs.displayResolutionMode.put(MODE_CUSTOM);
                    applyChanges.run();
                });
        body.addView(resRow);

        if (MODE_SCALED.equals(curResMode) && prefs != null) {
            int curScale = Math.max(60, Math.min(200, prefs.displayScale.get()));
            final long[] lastScaleTick = { 0 };
            LinearLayout scaleSlider = createLivePreviewSliderCard(
                    activity, dialog, root, scroll, body,
                    tr(activity,
                            "Window Scale — Grab and drag to preview live on screen",
                            "حجم النوافذ — اسحب المؤشر لمعاينة التغيير مباشرة على الشاشة",
                            "قەبارەی پەنجەرە — مؤشەرەکە ڕابکێشە بۆ بینینی ڕاستەوخۆ"),
                    60, 200, curScale,
                    new LiveSliderCallback() {
                        @Override
                        public void onStartPreview() { /* No-op before drag */ }

                        @Override
                        public void onLiveProgress(int value) {
                            prefs.get().edit().putInt("displayScale", value).apply();
                            long now = android.os.SystemClock.uptimeMillis();
                            if (now - lastScaleTick[0] >= 45) {
                                lastScaleTick[0] = now;
                                try {
                                    activity.getLorieView().reloadPreferences(prefs);
                                    activity.getLorieView().triggerCallback();
                                } catch (Exception ignored) { /* Best-effort UI operation */ }
                            }
                        }

                        @Override
                        public void onStopPreview(int finalValue) {
                            prefs.get().edit().putInt("displayScale", finalValue).apply();
                            try {
                                Intent broadcast = new Intent(LoriePreferences.ACTION_PREFERENCES_CHANGED);
                                broadcast.putExtra(KEY_FROM_BROADCAST, true);
                                broadcast.setPackage(activity.getPackageName());
                                activity.sendBroadcast(broadcast, PERMISSION_INTERNAL_BROADCAST);
                                activity.onPreferencesChangedCallback();
                            } catch (Exception ignored) { /* Best-effort UI operation */ }
                        }
                    });
            body.addView(scaleSlider);
        } else if (MODE_EXACT.equals(curResMode) && prefs != null) {
            String curExact = prefs.displayResolutionExact.get();
            LinearLayout exactRow = new LinearLayout(activity);
            exactRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams exLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            exLp.bottomMargin = dp(activity, 12);
            exactRow.setLayoutParams(exLp);

            String[] presets = { "1280x720", "1366x768", "1600x900", "1920x1080" };
            for (String p : presets) {
                addSegmentedChoice(activity, exactRow, p, p.equals(curExact), () -> {
                    prefs.displayResolutionExact.put(p);
                    applyChanges.run();
                });
            }
            body.addView(exactRow);
        } else if (MODE_CUSTOM.equals(curResMode) && prefs != null) {
            LinearLayout customRow = new LinearLayout(activity);
            customRow.setOrientation(LinearLayout.HORIZONTAL);
            customRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams crLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            crLp.bottomMargin = dp(activity, 12);
            customRow.setLayoutParams(crLp);

            EditText customInput = new EditText(activity);
            customInput.setText(prefs.displayResolutionCustom.get());
            customInput.setHint("2144x1103");
            customInput.setHintTextColor(TEXT_TERTIARY);
            customInput.setTextColor(TEXT_PRIMARY);
            customInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            customInput.setSingleLine(true);
            customInput.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 12), dp(activity, 8));
            customInput.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 10, SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams ciLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            ciLp.setMarginEnd(dp(activity, 8));
            customRow.addView(customInput, ciLp);

            TextView applyResBtn = createCupertinoButton(activity,
                    tr(activity, "Apply", "تطبيق", "جێبەجێکردن"),
                    true, false, v -> {
                        String val = customInput.getText() != null ? customInput.getText().toString().trim() : "";
                        if (val.matches("\\d+x\\d+")) {
                            prefs.displayResolutionCustom.put(val);
                            applyChanges.run();
                        }
                    });
            customRow.addView(applyResBtn);
            body.addView(customRow);
        }

        // Section 3: Function & Arrow Keys Bar Position
        body.addView(createSectionHeader(activity,
                tr(activity, "FUNCTION & ARROW KEYS BAR POSITION", "موقع شريط أزرار الوظائف والأسهم", "شوێنی شریتی دوگمەکانی فرمانی و تیرەکان")));

        String curPos = prefs != null ? prefs.ekbarPosition.get() : "top";
        LinearLayout posRow = new LinearLayout(activity);
        posRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams prLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prLp.bottomMargin = dp(activity, 12);
        posRow.setLayoutParams(prLp);

        addSegmentedChoice(activity, posRow,
                tr(activity, "Top", "الأعلى", "سەرەوە"),
                "top".equals(curPos), () -> {
                    if (prefs != null) prefs.ekbarPosition.put("top");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, posRow,
                tr(activity, "Bottom", "الأسفل", "خوارەوە"),
                "bottom".equals(curPos), () -> {
                    if (prefs != null) prefs.ekbarPosition.put("bottom");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, posRow,
                tr(activity, "Left", "اليسار", "چەپ"),
                "left".equals(curPos), () -> {
                    if (prefs != null) prefs.ekbarPosition.put("left");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, posRow,
                tr(activity, "Right", "اليمين", "ڕاست"),
                "right".equals(curPos), () -> {
                    if (prefs != null) prefs.ekbarPosition.put("right");
                    applyChanges.run();
                });
        body.addView(posRow);

        // Section 4: Function & Arrow Keys Bar Opacity (opacityEKBar) — Live-Preview Slider
        body.addView(createSectionHeader(activity,
                tr(activity, "FUNCTION & ARROW KEYS BAR OPACITY", "شفافية شريط أزرار الوظائف والأسهم", "ڕووناکی شریتی دوگمەکانی فرمانی و تیرەکان")));

        int curOpacity = prefs != null ? Math.max(15, Math.min(100, prefs.opacityEKBar.get())) : 80;
        final boolean[] wasBarHiddenBeforePreview = { false };
        LinearLayout opacitySlider = createLivePreviewSliderCard(
                activity, dialog, root, scroll, body,
                tr(activity,
                        "Shortcut Bar Opacity — Grab and drag to preview live",
                        "شفافية شريط الاختصارات — اسحب المؤشر للمعاينة المباشرة",
                        "ڕووناکی شریتی قەدبڕەکان — مؤشەرەکە ڕابکێشە بۆ بینینی ڕاستەوخۆ"),
                15, 100, curOpacity,
                new LiveSliderCallback() {
                    @Override
                    public void onStartPreview() {
                        try {
                            View pager = activity.getTerminalToolbarViewPager();
                            if (pager != null) {
                                wasBarHiddenBeforePreview[0] = (pager.getVisibility() != View.VISIBLE);
                                if (wasBarHiddenBeforePreview[0]) {
                                    pager.setVisibility(View.VISIBLE);
                                    pager.bringToFront();
                                }
                            }
                        } catch (Exception ignored) { /* Best-effort UI operation */ }
                    }

                    @Override
                    public void onLiveProgress(int value) {
                        if (prefs != null) {
                            prefs.get().edit().putInt("opacityEKBar", value).apply();
                        }
                        try {
                            activity.getTerminalToolbarViewPager().setAlpha(value / 100.0f);
                        } catch (Exception ignored) { /* Best-effort UI operation */ }
                    }

                    @Override
                    public void onStopPreview(int finalValue) {
                        if (prefs != null) {
                            prefs.get().edit().putInt("opacityEKBar", finalValue).apply();
                        }
                        try {
                            View pager = activity.getTerminalToolbarViewPager();
                            if (pager != null) {
                                pager.setAlpha(finalValue / 100.0f);
                                if (wasBarHiddenBeforePreview[0]) {
                                    pager.setVisibility(View.INVISIBLE);
                                }
                            }
                            Intent broadcast = new Intent(LoriePreferences.ACTION_PREFERENCES_CHANGED);
                            broadcast.putExtra(KEY_FROM_BROADCAST, true);
                            broadcast.setPackage(activity.getPackageName());
                            activity.sendBroadcast(broadcast, PERMISSION_INTERNAL_BROADCAST);
                            activity.onPreferencesChangedCallback();
                        } catch (Exception ignored) { /* Best-effort UI operation */ }
                    }
                });
        body.addView(opacitySlider);

        // Section 5: Keep Screen Awake / Idle Timeout & Back Button Behavior
        body.addView(createSectionHeader(activity,
                tr(activity, "KEEP SCREEN AWAKE (IDLE TIMEOUT)", "إبقاء الشاشة قيد التشغيل أثناء العمل", "هێشتنەوەی شاشە بە کراوەیی لە کاتی کارکردن")));

        String curTimeout = prefs != null ? prefs.screenIdleTimeout.get() : TIMEOUT_NEVER;
        LinearLayout timeoutRow = new LinearLayout(activity);
        timeoutRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams tmLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tmLp.bottomMargin = dp(activity, 12);
        timeoutRow.setLayoutParams(tmLp);

        addSegmentedChoice(activity, timeoutRow,
                tr(activity, "Always On", "دائماً قيد التشغيل", "هەمیشە کراوە"),
                TIMEOUT_NEVER.equals(curTimeout), () -> {
                    if (prefs != null) prefs.screenIdleTimeout.put(TIMEOUT_NEVER);
                    applyChanges.run();
                });
        addSegmentedChoice(activity, timeoutRow,
                tr(activity, "10 Min", "10 دقائق", "١٠ خولەک"),
                "10".equals(curTimeout), () -> {
                    if (prefs != null) prefs.screenIdleTimeout.put("10");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, timeoutRow,
                tr(activity, "20 Min", "20 دقيقة", "٢٠ خولەک"),
                "20".equals(curTimeout), () -> {
                    if (prefs != null) prefs.screenIdleTimeout.put("20");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, timeoutRow,
                tr(activity, "System Default", "إعداد الهاتف", "ڕێکخستنی مۆبایل"),
                "system".equals(curTimeout), () -> {
                    if (prefs != null) prefs.screenIdleTimeout.put("system");
                    applyChanges.run();
                });
        body.addView(timeoutRow);

        body.addView(createSectionHeader(activity,
                tr(activity, "ANDROID BACK BUTTON ACTION", "وظيفة زر الرجوع في الهاتف", "کرداری دوگمەی گەڕانەوەی مۆبایل")));

        String curBack = prefs != null ? prefs.backButtonAction.get() : ACTION_DOUBLE_BACK_EXIT;
        LinearLayout backRow = new LinearLayout(activity);
        backRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bkLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bkLp.bottomMargin = dp(activity, 12);
        backRow.setLayoutParams(bkLp);

        addSegmentedChoice(activity, backRow,
                tr(activity, "Double-Press to Exit", "ضغطتين للخروج", "دوو جار داگرتن بۆ دەرچوون"),
                ACTION_DOUBLE_BACK_EXIT.equals(curBack), () -> {
                    if (prefs != null) prefs.backButtonAction.put(ACTION_DOUBLE_BACK_EXIT);
                    applyChanges.run();
                });
        addSegmentedChoice(activity, backRow,
                tr(activity, "Shortcut Bar", "شريط الاختصارات", "شریتی قەدبڕەکان"),
                "toggle additional key bar".equals(curBack), () -> {
                    if (prefs != null) prefs.backButtonAction.put("toggle additional key bar");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, backRow,
                tr(activity, "Display Settings", "إعدادات العرض", "ڕێکخستنی شاشە"),
                "open preferences".equals(curBack), () -> {
                    if (prefs != null) prefs.backButtonAction.put("open preferences");
                    applyChanges.run();
                });
        addSegmentedChoice(activity, backRow,
                tr(activity, "Disabled", "بدون إجراء", KU_OFF),
                "no action".equals(curBack), () -> {
                    if (prefs != null) prefs.backButtonAction.put("no action");
                    applyChanges.run();
                });
        body.addView(backRow);

        // Section 6: Toggles Grid
        body.addView(createSectionHeader(activity,
                tr(activity, "DISPLAY & INPUT OPTIONS", "خيارات الشاشة والإدخال المتقدمة", "بژاردەکانی پێشکەوتووی شاشە و تێکردن")));

        GridLayout grid = new GridLayout(activity);
        grid.setColumnCount(isCompactWidth(activity) ? 1 : 2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        boolean adjustHeightEK = prefs != null && prefs.adjustHeightForEK.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_assist_keys,
                tr(activity, "Reserve Space for Shortcut Bar", "تخصيص مساحة مستقلة لشريط الاختصارات", "تەرخانکردنی شوێن بۆ شریتی قەدبڕەکان"),
                tr(activity, "Shift ASYCUDA window below the shortcut bar so menus never overlap", "إزاحة نافذة النظام أسفل شريط الاختصارات لمنع تغطية القوائم العلوية", "داگرتنی پەنجەرەی سیستەم لە ژێر شریتەکە بۆ ئەوەی لیستەکان دانەپۆشێت"),
                adjustHeightEK ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                adjustHeightEK, false, () -> {
                    if (prefs != null) prefs.adjustHeightForEK.put(!adjustHeightEK);
                    applyChanges.run();
                });

        boolean isFullscreen = prefs != null && prefs.fullscreen.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_display,
                tr(activity, "Fullscreen Mode", "وضع ملء الشاشة", "دۆخی شاشەی پڕ"),
                tr(activity, "Hide Android status and navigation bars while working", "إخفاء شريط الحالة وأزرار التنقل الخاصة بالهاتف أثناء العمل", "شاردنەوەی شریتی سەرەوە و خوارەوەی سیستەم لە کاتی کارکردن"),
                isFullscreen ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                isFullscreen, false, () -> {
                    if (prefs != null) prefs.fullscreen.put(!isFullscreen);
                    applyChanges.run();
                });

        boolean hideCutout = prefs != null && prefs.hideCutout.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_display,
                tr(activity, "Avoid Camera Cutout (Notch)", "تجنب ثقب الكاميرا الأمامية", "دوورکەوتنەوە لە شوێنی کامێرای پێشەوە"),
                tr(activity, "Keep the workspace clear of the front camera cutout area", "منع تداخل نافذة النظام مع منطقة الكاميرا الأمامية للهاتف", "ڕێگری لە تێکەڵبوونی پەنجەرەی سیستەم لەگەڵ کامێرای پێشەوە"),
                hideCutout ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                hideCutout, false, () -> {
                    if (prefs != null) prefs.hideCutout.put(!hideCutout);
                    applyChanges.run();
                });

        boolean isStretch = prefs != null && prefs.displayStretch.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_display,
                tr(activity, "Stretch to Fill Screen", "تمديد العرض لملء الشاشة", "کشاندنی دیمەن بۆ پڕکردنەوەی شاشە"),
                tr(activity, "Expand desktop display across the full screen aspect ratio", "تمديد عرض سطح المكتب ليغطي كامل أبعاد الشاشة", "فراوانکردنی پەنجەرەی سیستەم بۆ پڕکردنەوەی تەواوی شاشەکە"),
                isStretch ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                isStretch, false, () -> {
                    if (prefs != null) prefs.displayStretch.put(!isStretch);
                    applyChanges.run();
                });

        boolean mouseHelper = prefs != null && prefs.showMouseHelper.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_assist_keys,
                tr(activity, "On-Screen Mouse Buttons", "أزرار الماوس العائمة", "دوگمە سەرئاوکەوتووەکانی ماوس"),
                tr(activity, "Show floating Left, Middle, and Right click buttons", "إظهار أزرار النقر الأيسر والأيمن العائمة على الشاشة", "پیشاندانی دوگمەی کلیکی چەپ و ناوەڕاست و ڕاست لەسەر شاشە"),
                mouseHelper ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                mouseHelper, false, () -> {
                    if (prefs != null) prefs.showMouseHelper.put(!mouseHelper);
                    applyChanges.run();
                });

        boolean tapToMove = prefs != null && prefs.tapToMove.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_assist_keys,
                tr(activity, "Tap to Move Cursor", "النقر لتحريك مؤشر الماوس", "داگرتن بۆ گواستنەوەی نیشاندەری ماوس"),
                tr(activity, "Jump cursor directly to tapped position in Trackpad mode", "نقل مؤشر الماوس مباشرة إلى موضع اللمس في وضع لوحة اللمس", "گواستنەوەی ڕاستەوخۆی نیشاندەری ماوس بۆ شوێنی داگرتن"),
                tapToMove ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                tapToMove, false, () -> {
                    if (prefs != null) prefs.tapToMove.put(!tapToMove);
                    applyChanges.run();
                });

        boolean adjustForKbd = prefs != null && prefs.Reseed.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_keyboard,
                tr(activity, "Resize Window When Keyboard Opens", "تحجيم النافذة عند ظهور الكيبورد", "گۆڕینی قەبارەی پەنجەرە لەگەڵ تەختەکلیل"),
                tr(activity, "Adjust desktop height above the on-screen keyboard", "تعديل ارتفاع الشاشة تلقائياً فوق لوحة المفاتيح عند الكتابة", "ڕێکخستنی بەرزی شاشە لە سەرووی تەختەکلیل لە کاتی نووسین"),
                adjustForKbd ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                adjustForKbd, false, () -> {
                    if (prefs != null) prefs.Reseed.put(!adjustForKbd);
                    applyChanges.run();
                });

        boolean clipSync = prefs == null || prefs.clipboardEnable.get();
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_documents,
                tr(activity, "Paste from Phone Clipboard", "اللصق من حافظة الهاتف", "لکاندنی دەق لە کلیپبۆردی مۆبایلەوە"),
                tr(activity, "Allow pasting text copied on your phone directly into ASYCUDA fields", "السماح بلصق النصوص المنسوخة من الهاتف مباشرة داخل حقول نظام أسيكودا", "ڕێگەدان بە لکاندنی دەقی کۆپیکراوی مۆبایل لەناو خانەکانی ئەسیکۆدا"),
                clipSync ? tr(activity, "ON", AR_ON, KU_ON) : tr(activity, "OFF", AR_OFF, KU_OFF),
                clipSync, false, () -> {
                    if (prefs != null) prefs.clipboardEnable.put(!clipSync);
                    applyChanges.run();
                });

        body.addView(grid);
        scroll.addView(body);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();
    }

    private interface LiveSliderCallback {
        void onStartPreview();
        void onLiveProgress(int value);
        void onStopPreview(int finalValue);
    }

    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    private static LinearLayout createLivePreviewSliderCard(
            MainActivity activity,
            Dialog dialog,
            LinearLayout root,
            ScrollView scroll,
            LinearLayout body,
            String labelText,
            int minVal,
            int maxVal,
            int curVal,
            LiveSliderCallback callback) {

        LinearLayout sliderCard = new LinearLayout(activity);
        sliderCard.setOrientation(LinearLayout.VERTICAL);
        sliderCard.setPadding(dp(activity, 11), dp(activity, 8), dp(activity, 11), dp(activity, 8));
        sliderCard.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 11, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(activity, 8);
        sliderCard.setLayoutParams(cardLp);

        LinearLayout topRow = new LinearLayout(activity);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView labelTv = new TextView(activity);
        labelTv.setText(labelText);
        labelTv.setTextColor(TEXT_SECONDARY);
        labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        labelTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topRow.addView(labelTv);

        int clampedCur = Math.max(minVal, Math.min(maxVal, curVal));
        TextView valueBadge = new TextView(activity);
        valueBadge.setText(clampedCur + "%");
        valueBadge.setTextColor(SYSTEM_BLUE);
        valueBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        valueBadge.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        valueBadge.setPadding(dp(activity, 8), dp(activity, 2), dp(activity, 8), dp(activity, 2));
        valueBadge.setBackground(createCardDrawable(activity, SYSTEM_BLUE_TINT, 7, 0));
        topRow.addView(valueBadge);
        sliderCard.addView(topRow);

        LinearLayout barRow = new LinearLayout(activity);
        barRow.setOrientation(LinearLayout.HORIZONTAL);
        barRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams brLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brLp.topMargin = dp(activity, 8);
        barRow.setLayoutParams(brLp);

        TextView minTv = new TextView(activity);
        minTv.setText(minVal + "%");
        minTv.setTextColor(TEXT_TERTIARY);
        minTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        barRow.addView(minTv);

        SeekBar seekBar = new SeekBar(activity);
        seekBar.setMax(maxVal - minVal);
        seekBar.setProgress(clampedCur - minVal);
        seekBar.setProgressTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        seekBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(SYSTEM_GRAY_FILL));
        LinearLayout.LayoutParams sbLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        sbLp.setMarginStart(dp(activity, 6));
        sbLp.setMarginEnd(dp(activity, 6));
        barRow.addView(seekBar, sbLp);

        TextView maxTv = new TextView(activity);
        maxTv.setText(maxVal + "%");
        maxTv.setTextColor(TEXT_TERTIARY);
        maxTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        barRow.addView(maxTv);
        sliderCard.addView(barRow);

        final boolean[] inPeek = { false };
        final Runnable enterPeekMode = () -> {
            if (inPeek[0]) return;
            inPeek[0] = true;
            callback.onStartPreview();

            // Hide sheet container & all sibling views while preserving exact Y coordinate of sliderCard
            root.setBackgroundColor(Color.TRANSPARENT);
            root.setElevation(0f);
            for (int i = 0; i < root.getChildCount(); i++) {
                View ch = root.getChildAt(i);
                if (ch != scroll) {
                    ch.setVisibility(View.INVISIBLE);
                }
            }
            for (int i = 0; i < body.getChildCount(); i++) {
                View ch = body.getChildAt(i);
                if (ch != sliderCard) {
                    ch.setVisibility(View.INVISIBLE);
                }
            }
            sliderCard.setBackground(createCardDrawable(activity, Color.parseColor("#E61C1C1E"), 14, SYSTEM_BLUE));
            sliderCard.setElevation(dp(activity, 24));

            Window w = dialog.getWindow();
            if (w != null) {
                WindowManager.LayoutParams wlp = w.getAttributes();
                wlp.dimAmount = 0.0f;
                if (Build.VERSION.SDK_INT >= 31) {
                    try {
                        wlp.setBlurBehindRadius(0);
                        w.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    } catch (Exception ignored) { /* Best-effort UI operation */ }
                }
                w.setAttributes(wlp);
            }
        };

        final Runnable exitPeekMode = () -> {
            if (!inPeek[0]) return;
            inPeek[0] = false;

            root.setBackground(createSheetContainerDrawable(activity, 22));
            root.setElevation(dp(activity, 20));
            for (int i = 0; i < root.getChildCount(); i++) {
                root.getChildAt(i).setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < body.getChildCount(); i++) {
                body.getChildAt(i).setVisibility(View.VISIBLE);
            }
            sliderCard.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
            sliderCard.setElevation(0f);

            Window w = dialog.getWindow();
            if (w != null) {
                WindowManager.LayoutParams wlp = w.getAttributes();
                wlp.dimAmount = 0.50f;
                if (Build.VERSION.SDK_INT >= 31) {
                    try {
                        w.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                        wlp.setBlurBehindRadius(dp(activity, 28));
                    } catch (Exception ignored) { /* Best-effort UI operation */ }
                }
                w.setAttributes(wlp);
            }

            int finalVal = minVal + seekBar.getProgress();
            callback.onStopPreview(finalVal);
        };

        seekBar.setOnTouchListener((v, event) -> {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                if (v.getParent() != null) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                enterPeekMode.run();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                exitPeekMode.run();
            }
            return false;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int val = minVal + progress;
                valueBadge.setText(val + "%");
                if (fromUser) {
                    callback.onLiveProgress(val);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
                enterPeekMode.run();
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                exitPeekMode.run();
            }
        });

        return sliderCard;
    }

    private static void addSegmentedChoice(Context ctx, LinearLayout parent, String label, boolean selected, Runnable onTap) {
        TextView btn = new TextView(ctx);
        btn.setText(label);
        btn.setTextColor(selected ? TEXT_PRIMARY : TEXT_SECONDARY);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        btn.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, selected ? Typeface.BOLD : Typeface.NORMAL));
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dp(ctx, 6), dp(ctx, 6), dp(ctx, 6), dp(ctx, 6));
        btn.setBackground(createRippleCardDrawable(ctx,
                selected ? SYSTEM_BLUE : SURFACE_GROUPED_CARD,
                8,
                selected ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
        btn.setClickable(true);
        btn.setFocusable(true);
        btn.setOnClickListener(v -> onTap.run());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMarginEnd(dp(ctx, 5));
        btn.setLayoutParams(lp);
        parent.addView(btn);
    }

    public static UpdateCenterController showUpdateCenterModal(MainActivity activity, Runnable onCloseWhenUpToDate) {
        Dialog dialog = createModalDialog(activity, 0.76f, 490, false);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_cloud_sync);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Check for Updates",
                "فحص التحديثات",
                "پشکنینی نوێکردنەوەکان"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView sub = new TextView(activity);
        sub.setText(tr(activity,
                "Checking app version and customs packages...",
                "جاري التحقق من إصدار التطبيق وملفات النظام...",
                "پشکنینی وەشانی بەرنامە و پەڕگەکانی سیستەم..."));
        sub.setTextColor(TEXT_SECONDARY);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        titleCol.addView(sub);
        header.addView(titleCol);

        // Top-bar icon action (replaces old bottom footer text button): disabled while checking
        final FrameLayout actionBtn = createIconButton(activity, R.drawable.ic_menu_cloud_sync,
                tr(activity, "Checking...", "جاري الفحص...", "پشکنین..."),
                false, false, null);
        actionBtn.setEnabled(false);
        actionBtn.setAlpha(0.4f);
        header.addView(actionBtn);

        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scroll.setLayoutParams(scrollLp);

        LinearLayout scrollBody = new LinearLayout(activity);
        scrollBody.setOrientation(LinearLayout.VERTICAL);

        // 1. App Release Update Card
        LinearLayout appCard = new LinearLayout(activity);
        appCard.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 14);
        appCard.setPadding(pad, dp(activity, 12), pad, dp(activity, 12));
        appCard.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.topMargin = dp(activity, 12);
        appCard.setLayoutParams(cardLp);

        TextView appHeader = createSectionHeader(activity,
                tr(activity, "APP VERSION", "إصدار التطبيق", "وەشانی بەرنامە"));
        appCard.addView(appHeader);

        TextView appStatus = new TextView(activity);
        appStatus.setText(tr(activity,
                "Checking GitHub releases...",
                "جاري البحث عن إصدار جديد...",
                "گەڕان بۆ وەشانی نوێ..."));
        appStatus.setTextColor(TEXT_PRIMARY);
        appStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        appStatus.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.NORMAL));
        appCard.addView(appStatus);

        TextView appNotes = new TextView(activity);
        appNotes.setTextColor(TEXT_SECONDARY);
        appNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        appNotes.setVisibility(View.GONE);
        LinearLayout.LayoutParams notesLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        notesLp.topMargin = dp(activity, 4);
        appCard.addView(appNotes, notesLp);

        ProgressBar appProgress = createHorizontalProgressBar(activity);
        appProgress.setIndeterminate(true);
        LinearLayout.LayoutParams progLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, 6));
        progLp.topMargin = dp(activity, 10);
        appCard.addView(appProgress, progLp);

        TextView installAppBtn = createCupertinoButton(activity,
                tr(activity, "Download & Install Update", "تحميل وتثبيت التحديث", "دابەزاندن و دامەزراندنی نوێکردنەوە"),
                true, false, null);
        LinearLayout.LayoutParams instLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instLp.topMargin = dp(activity, 10);
        installAppBtn.setGravity(Gravity.CENTER);
        installAppBtn.setLayoutParams(instLp);
        installAppBtn.setVisibility(View.GONE);
        appCard.addView(installAppBtn);

        scrollBody.addView(appCard);

        // 2. MOF Government JARs Card
        LinearLayout mofCard = new LinearLayout(activity);
        mofCard.setOrientation(LinearLayout.VERTICAL);
        mofCard.setPadding(pad, dp(activity, 12), pad, dp(activity, 12));
        mofCard.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams mofLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mofLp.topMargin = dp(activity, 10);
        mofCard.setLayoutParams(mofLp);

        TextView mofHeader = createSectionHeader(activity,
                tr(activity, "CUSTOMS SERVER PACKAGES", "ملفات خادم الكمارك (وزارة المالية)", "پەڕگەکانی سێرڤەری گومرگ"));
        mofCard.addView(mofHeader);

        TextView mofStatus = new TextView(activity);
        mofStatus.setText(tr(activity,
                "Connecting to asy.mof.gov.iq...",
                "جاري الاتصال بخادم وزارة المالية...",
                "پەیوەندیکردن بە سێرڤەری وەزارەتی دارایی..."));
        mofStatus.setTextColor(TEXT_PRIMARY);
        mofStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        mofStatus.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.NORMAL));
        mofCard.addView(mofStatus);

        TextView mofDetail = new TextView(activity);
        mofDetail.setText("0 / 131");
        mofDetail.setTextColor(TEXT_SECONDARY);
        mofDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        LinearLayout.LayoutParams mdLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mdLp.topMargin = dp(activity, 2);
        mofCard.addView(mofDetail, mdLp);

        ProgressBar mofProgress = createHorizontalProgressBar(activity);
        mofProgress.setIndeterminate(true);
        mofProgress.setMax(100);
        LinearLayout.LayoutParams mpLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, 6));
        mpLp.topMargin = dp(activity, 10);
        mofCard.addView(mofProgress, mpLp);

        scrollBody.addView(mofCard);
        scroll.addView(scrollBody);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();

        final boolean[] apkDownloadActive = { false };

        return new UpdateCenterController() {
            @Override
            public void setAppUpdateStatus(String statusText, int progressPercent, boolean indeterminate) {
                activity.runOnUiThread(() -> {
                    appStatus.setText(statusText);
                    if (progressPercent < 0 && !indeterminate) {
                        appProgress.setVisibility(View.GONE);
                    } else {
                        appProgress.setVisibility(View.VISIBLE);
                        appProgress.setIndeterminate(indeterminate);
                        if (!indeterminate) {
                            appProgress.setMax(100);
                            appProgress.setProgress(Math.max(0, Math.min(100, progressPercent)));
                        }
                    }
                });
            }

            @Override
            public void showAppUpdateAvailable(String versionTag, String notesSummary, Runnable onInstallClicked) {
                activity.runOnUiThread(() -> {
                    appStatus.setText(tr(activity,
                            "New update available: " + versionTag,
                            "يتوفر تحديث جديد: " + versionTag,
                            "نوێکردنەوەی نوێ بەردەستە: " + versionTag));
                    appStatus.setTextColor(SYSTEM_GREEN);
                    if (notesSummary != null && !notesSummary.isEmpty()) {
                        appNotes.setText(notesSummary);
                        appNotes.setVisibility(View.VISIBLE);
                    }
                    appProgress.setVisibility(View.GONE);
                    installAppBtn.setVisibility(View.VISIBLE);
                    installAppBtn.setOnClickListener(v -> {
                        apkDownloadActive[0] = true;
                        appNotes.setVisibility(View.GONE);
                        installAppBtn.setVisibility(View.GONE);
                        appProgress.setVisibility(View.VISIBLE);
                        appProgress.setIndeterminate(false);
                        appProgress.setProgress(0);
                        onInstallClicked.run();
                    });
                });
            }

            @Override
            public void setMofUpdateStatus(String statusText, String detailText, int current, int total, boolean indeterminate) {
                activity.runOnUiThread(() -> {
                    mofStatus.setText(statusText);
                    if (detailText != null) mofDetail.setText(detailText);
                    if (total <= 0 && !indeterminate) {
                        mofProgress.setVisibility(View.GONE);
                    } else {
                        mofProgress.setVisibility(View.VISIBLE);
                        mofProgress.setIndeterminate(indeterminate);
                        if (!indeterminate && total > 0) {
                            mofProgress.setMax(total);
                            mofProgress.setProgress(Math.max(0, Math.min(total, current)));
                        }
                    }
                });
            }

            @Override
            public void onCheckFinished(int updatedJarsCount, boolean appUpdatePending, Runnable onRestartNow) {
                activity.runOnUiThread(() -> {
                    // Never auto-dismiss or restart the session if an APK update is available or currently downloading
                    if (updatedJarsCount > 0 && !appUpdatePending && !apkDownloadActive[0]) {
                        sub.setText(tr(activity,
                                "Updates downloaded. Restarting session...",
                                "تم تحميل التحديثات. جاري إعادة تشغيل الجلسة...",
                                "نوێکردنەوەکان دابەزێنران. دووبارە پێکردنەوەی دانیشتن..."));
                        setIconButtonState(activity, actionBtn, R.drawable.ic_menu_restart,
                                tr(activity, "Restarting...", "جاري إعادة التشغيل...", "دووبارە پێکردنەوە..."), true);
                        actionBtn.setAlpha(1.0f);
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            if (apkDownloadActive[0]) return;
                            try { dialog.dismiss(); } catch (Exception ignored) { /* Best-effort UI operation */ }
                            if (onRestartNow != null) onRestartNow.run();
                        }, 1200);
                    } else {
                        dialog.setCancelable(true);
                        if (onCloseWhenUpToDate != null) {
                            dialog.setOnCancelListener(d -> onCloseWhenUpToDate.run());
                        }
                        if (appUpdatePending || apkDownloadActive[0]) {
                            sub.setText(tr(activity,
                                    "New app version available above.",
                                    "يتوفر إصدار جديد للتطبيق أعلاه.",
                                    "وەشانی نوێی بەرنامە لە سەرەوە بەردەستە."));
                        } else {
                            sub.setText(tr(activity,
                                    "Everything is up to date.",
                                    "جميع الملفات محدّثة.",
                                    "هەموو پەڕگەکان نوێکراونەتەوە."));
                        }
                        actionBtn.setEnabled(true);
                        actionBtn.setAlpha(1.0f);
                        setIconButtonState(activity, actionBtn,
                                onCloseWhenUpToDate != null ? R.drawable.ic_nav_back : R.drawable.ic_action_check,
                                onCloseWhenUpToDate != null
                                        ? tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER)
                                        : tr(activity, "Continue", "متابعة", "بەردەوامبوون"),
                                !appUpdatePending && onCloseWhenUpToDate == null);
                        actionBtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            if (updatedJarsCount > 0 && !apkDownloadActive[0] && onRestartNow != null) {
                                onRestartNow.run();
                            } else if (onCloseWhenUpToDate != null) {
                                onCloseWhenUpToDate.run();
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Hidden Smart Audit check for submitted issue reports.
     * Blocks empty/short notes, keyboard-mash spam, test/placeholder submissions,
     * and any profane, insulting, hostile, or harmful language in English, Arabic, or Kurdish.
     */
    private static String auditFeedbackReport(Context ctx, String rawText) {
        if (rawText == null) {
            return tr(ctx,
                    "Please describe the technical issue before submitting your report.",
                    "يرجى كتابة وصف واضح للمشكلة التقنية قبل إرسال البلاغ.",
                    "تکایە وەسفێکی ڕوونی کێشە تەکنیکییەکە بنووسە پێش ناردن.");
        }
        String trimmed = rawText.trim();
        if (trimmed.length() < 12) {
            return tr(ctx,
                    "Please provide a clear description of the issue (at least a full sentence).",
                    "يرجى كتابة تفاصيل أوضح عن المشكلة (جملة كاملة على الأقل) قبل الإرسال.",
                    "تکایە وردەکاری زیاتر و ڕستەیەکی تەواو لەسەر کێشەکە بنووسە.");
        }

        // Normalize for word, test-phrase & profanity/insult inspection
        String lower = trimmed.toLowerCase(Locale.ROOT);
        String cleaned = lower.replaceAll("[^\\p{L}\\p{N}\\s]", " ").replaceAll("\\s+", " ").trim();
        String[] words = cleaned.isEmpty() ? new String[0] : cleaned.split("\\s+");

        if (words.length < 3) {
            return tr(ctx,
                    "Please write a complete sentence explaining what happened and which window you were using.",
                    "يرجى كتابة جملة مفيدة توضح المشكلة التي ظهرت لك والنافذة التي كنت تعمل عليها.",
                    "تکایە ڕستەیەکی تەواو بنووسە بۆ ڕوونکردنەوەی کێشەکە و ئەو پەنجەرەیەی بەکارت دەهێنا.");
        }

        // Block repeated character spam like "aaaaaaaaaa" or "1111111111" or "ههههههه"
        String noSpaces = cleaned.replace(" ", "");
        if (noSpaces.length() >= 5) {
            char first = noSpaces.charAt(0);
            boolean allSame = true;
            for (int i = 1; i < noSpaces.length(); i++) {
                if (noSpaces.charAt(i) != first) {
                    allSame = false;
                    break;
                }
            }
            if (allSame || noSpaces.matches(".*(.)\\1{4,}.*")) {
                return tr(ctx,
                        "Please enter a genuine technical description of the problem.",
                        "يرجى إدخال وصف حقيقي ومفهوم للمشكلة التقنية.",
                        "تکایە وەسفێکی ڕاستەقینە و دروستی کێشەکە بنووسە.");
            }
        }

        String padded = " " + cleaned + " ";

        // Block offensive, profane, insulting, hostile, or harmful words/phrases (English, Arabic, Kurdish)
        // Checked BEFORE test words so mixed reports like "Test3 , Harm sentences , the developer is awful lol"
        // get flagged specifically for disrespectful/harmful content.
        String[] harmfulOrInsulting = {
                // Profanity
                "fuck", "fucking", "fucker", "shit", "shitty", "bitch", "asshole", "bastard", "dick", "pussy",
                "cunt", "whore", "slut", "motherfucker", "stfu", "wtf", "retard", "retarded",
                // Insults, hostility & harm toward developer or app
                "idiot", "moron", "stupid", "dumb", "awful", "terrible", "horrible", "useless", "garbage",
                "trash", "pathetic", "sucks", "worst", "hate you", "hate the developer", "developer is awful",
                "developer is bad", "bad developer", "very bad", "bad app", "bad application", "everything is bad",
                "fake", "fake app", "fake application", "harm", "harmful", "harm sentences", "kill", "die",
                "attack", "threat", "abuse", "insult", "loser", "clown", "scam", "fraud", "piece of crap",
                // Arabic profanity, insults & harm
                "كس", "كسمك", "طيز", "عير", "شرموط", "شرموطة", "قحبة", "قحبه", "كواد", "قواد",
                "منيوك", "نيك", "تافه", "زبالة", "زباله", "حيوان", "كلب", "حمار", "خرا", "خرة",
                "ساقط", "ولك", "انعل", "ملعون", "فاشل", "غبي", "سيء", "أسوأ", "مسخرة", "مسخره", "مزيف", "كذب",
                "حقير", "وقح", "قذر", "حثالة", "نصب", "احتيال", "تهديد", "أذى", "ضرر", "اقتل", "موت",
                "المطور فاشل", "المطور سيء", "برنامج فاشل", "تطبيق فاشل", "تطبيق زبالة", "تطبيق سيء",
                // Kurdish profanity, insults & harm
                "قۆندەرە", "گەوشت", "سەگ", "کەر", "گو", "قوز", "کێر", "حیز",
                "گەمژە", "خراپ", "بێ کەڵک", "زباڵە", "فاشیل", "درۆزن", "تێکدەر", "ساختە"
        };
        for (String bad : harmfulOrInsulting) {
            if (padded.contains(" " + bad + " ") || (bad.contains(" ") && cleaned.contains(bad))) {
                return tr(ctx,
                        "Submission blocked: Please keep your report respectful, constructive, and focused strictly on technical issues.",
                        "تم إيقاف الإرسال: يرجى الالتزام بأسلوب محترم وبنّاء والتركيز فقط على المشاكل التقنية.",
                        "ناردنەکە ڕاگیرا: تکایە تەنها باسی کێشە تەکنیکییەکە بکە بە شێوازێکی ڕێزدارانە و پیشەیی.");
            }
        }

        // Block test / dummy / placeholder reports or casual chatter
        String[] testTokens = {
                "test", "test1", "test2", "test3", "test4", "testing", "tester",
                "asdf", "asdfg", "qwer", "qwerty", "zxcv", "lol", "lmao", "rofl", "haha", "hahaha", "hehe",
                "dummy", "placeholder", "hello", "hi there",
                "تجربة", "تجربه", "تست", "تيست", "فحص تجريبي", "بلاغ تجريبي", "للتجربة",
                "تاقیکردنەوە", "تێست"
        };
        String[] testPhrases = {
                "test report", "this is a test", "this a test", "for testing", "for development purposes",
                "just testing", "sample report", "ignore this", "فقط للتجربة", "بلاغ للتجربة"
        };
        for (String phrase : testPhrases) {
            if (cleaned.contains(phrase)) {
                return tr(ctx,
                        "Test or placeholder reports are not sent. Please describe an actual issue in ASYCUDA World.",
                        "لا يتم إرسال البلاغات التجريبية. يرجى كتابة مشكلة فعلية واجهتك داخل نظام أسيكودا.",
                        "ڕاپۆرتی تاقیکردنەوە نانێردرێت. تکایە کێشەیەکی ڕاستەقینەی ناو سیستەمی ئەسیکۆدا بنووسە.");
            }
        }
        for (String tok : testTokens) {
            if (padded.contains(" " + tok + " ")) {
                return tr(ctx,
                        "Test or placeholder reports are not sent. Please describe an actual issue in ASYCUDA World.",
                        "لا يتم إرسال البلاغات التجريبية. يرجى كتابة مشكلة فعلية واجهتك داخل نظام أسيكودا.",
                        "ڕاپۆرتی تاقیکردنەوە نانێردرێت. تکایە کێشەیەکی ڕاستەقینەی ناو سیستەمی ئەسیکۆدا بنووسە.");
            }
        }

        return null;
    }

    public static FeedbackModalController showFeedbackModal(MainActivity activity, FeedbackSubmitHandler handler) {
        return showFeedbackModal(activity, handler, null);
    }

    public static FeedbackModalController showFeedbackModal(MainActivity activity, FeedbackSubmitHandler handler, Runnable onBackToMenu) {
        Dialog dialog = createModalDialog(activity, 0.80f, 560, true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_diagnostics);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Report a Technical Issue",
                "الإبلاغ عن مشكلة فنية",
                "ناردنی ڕاپۆرتی کێشەی تەکنیکی"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Submit a bug report with diagnostic logs and an optional screenshot",
                "إرسال بلاغ بالمشكلة مع سجلات الفحص وصورة للشاشة إلى مطور التطبيق",
                "ناردنی ڕاپۆرتی کێشە لەگەڵ تۆماری هەڵەکان و وێنەی شاشە بۆ گەشەپێدەر"));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        View closeBtn = createIconButton(activity,
                onBackToMenu != null ? R.drawable.ic_nav_back : R.drawable.ic_action_close,
                onBackToMenu != null
                        ? tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER)
                        : tr(activity, "Cancel", "إلغاء", "پاشگەزبوونەوە"),
                false, false, v -> {
                    dialog.dismiss();
                    if (onBackToMenu != null) onBackToMenu.run();
                });
        if (onBackToMenu != null) {
            dialog.setOnCancelListener(d -> onBackToMenu.run());
        }
        header.addView(closeBtn);
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 10);
        scroll.setLayoutParams(scrollLp);

        LinearLayout body = new LinearLayout(activity);
        body.setOrientation(LinearLayout.VERTICAL);

        body.addView(createSectionHeader(activity,
                tr(activity, "ISSUE DESCRIPTION", "وصف المشكلة بالتفصيل", "وەسفی کێشەکە بە وردی")));

        EditText descInput = new EditText(activity);
        descInput.setHint(tr(activity,
                "Describe the problem you encountered, which declaration window or menu you were using, and any error message shown...",
                "اشرح المشكلة التي واجهتك بالتفصيل، وفي أي نافذة أو قائمة ظهرت، وما هي رسالة الخطأ إن وجدت...",
                "باسی ئەو کێشەیە بکە کە تووشت بووە، لە کام پەنجەرەدا کاردەکرد، و چ پەیامێکی هەڵە دەرکەوتووە..."));
        descInput.setHintTextColor(TEXT_TERTIARY);
        descInput.setTextColor(TEXT_PRIMARY);
        descInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        descInput.setMinLines(3);
        descInput.setMaxLines(5);
        descInput.setGravity(Gravity.TOP | Gravity.START);
        descInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        int ep = dp(activity, 12);
        descInput.setPadding(ep, dp(activity, 10), ep, dp(activity, 10));
        descInput.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 12, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams edLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        edLp.bottomMargin = dp(activity, 12);
        body.addView(descInput, edLp);

        // Screenshot / Image Attachment Card
        LinearLayout imgCard = new LinearLayout(activity);
        imgCard.setOrientation(LinearLayout.HORIZONTAL);
        imgCard.setGravity(Gravity.CENTER_VERTICAL);
        imgCard.setPadding(dp(activity, 12), dp(activity, 10), dp(activity, 12), dp(activity, 10));
        imgCard.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams icardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        icardLp.bottomMargin = dp(activity, 10);
        imgCard.setLayoutParams(icardLp);

        ImageView thumbView = new ImageView(activity);
        thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        thumbView.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 8, SEPARATOR_HAIRLINE));
        thumbView.setVisibility(View.GONE);
        LinearLayout.LayoutParams thLp = new LinearLayout.LayoutParams(dp(activity, 44), dp(activity, 44));
        thLp.setMarginEnd(dp(activity, 10));
        imgCard.addView(thumbView, thLp);

        LinearLayout imgTextCol = new LinearLayout(activity);
        imgTextCol.setOrientation(LinearLayout.VERTICAL);
        imgTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView imgTitle = new TextView(activity);
        imgTitle.setText(tr(activity,
                "Screenshot or Image (Optional)",
                "صورة للشاشة أو الخطأ (اختياري)",
                "وێنەی شاشە یان هەڵەکە (ئارەزوومەندانە)"));
        imgTitle.setTextColor(TEXT_PRIMARY);
        imgTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        imgTitle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        imgTextCol.addView(imgTitle);

        TextView imgSub = new TextView(activity);
        imgSub.setText(tr(activity,
                "Capture the live ASYCUDA window or choose an image from your device",
                "التقط صورة مباشرة لنافذة النظام أو اختر صورة من الهاتف",
                "وێنەیەکی ڕاستەوخۆی پەنجەرەی ئەسیکۆدا بگرە یان لە مۆبایلەکەت هەڵبژێرە"));
        imgSub.setTextColor(TEXT_SECONDARY);
        imgSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        imgTextCol.addView(imgSub);

        TextView imgPrivacyNote = new TextView(activity);
        imgPrivacyNote.setText(tr(activity,
                "Privacy Note: Attached screenshots are sent as original images directly to the developer's private error tracker to help diagnose visual issues, and are never posted publicly. Please make sure your screenshot does not show passwords, tax numbers, or confidential declaration details before attaching it.",
                "ملاحظة الخصوصية: يتم إرسال لقطات الشاشة المرفقة كصور أصلية إلى لوحة الصيانة الخاصة بالمطور فقط للمساعدة في تشخيص المشكلة، ولا يتم نشرها للعامة أبداً. يرجى التأكد من عدم ظهور كلمات المرور أو الأرقام الضريبية أو تفاصيل التصاريح السرية في الصورة قبل إرفاقها.",
                "تێبینی تایبەتمەندی: وێنە هاوپێچکراوەکان تەنها بۆ بەشی چاککردنەوەی تایبەت بە گەشەپێدەر دەنێردرێن بۆ چارەسەرکردنی کێشەکە و هەرگیز بە شێوەی گشتی بڵاوناکرێنەوە. تکایە دڵنیابەرەوە کە وشەی نهێنی یان زانیاری تایبەتی گومرگی لەناو وێنەکەدا دیار نییە پێش ناردنی."));
        imgPrivacyNote.setTextColor(Color.parseColor("#FFFF9F0A"));
        imgPrivacyNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        LinearLayout.LayoutParams ipnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ipnLp.topMargin = dp(activity, 3);
        imgPrivacyNote.setLayoutParams(ipnLp);
        imgTextCol.addView(imgPrivacyNote);
        imgCard.addView(imgTextCol);

        final File[] currentImageHolder = { null };
        final FeedbackModalController[] controllerRef = new FeedbackModalController[1];

        View captureBtn = createIconButton(activity, R.drawable.ic_action_camera,
                tr(activity, "Capture Screen", "التقاط الشاشة", "وێنەگرتنی شاشە"),
                false, false, v -> {
                    if (handler != null && controllerRef[0] != null) {
                        handler.onCaptureScreenRequested(controllerRef[0], dialog);
                    }
                });
        LinearLayout.LayoutParams capLp = (LinearLayout.LayoutParams) captureBtn.getLayoutParams();
        capLp.setMarginEnd(dp(activity, 6));
        imgCard.addView(captureBtn);

        View pickBtn = createIconButton(activity, R.drawable.ic_action_image,
                tr(activity, "Choose Image", "اختيار صورة", "هەڵبژاردنی وێنە"),
                false, false, v -> {
                    if (handler != null && controllerRef[0] != null) {
                        handler.onPickImageRequested(controllerRef[0]);
                    }
                });
        imgCard.addView(pickBtn);

        View removeImgBtn = createIconButton(activity, R.drawable.ic_action_delete,
                tr(activity, "Remove", "حذف", "لابردن"),
                false, true, v -> {
                    if (currentImageHolder[0] != null && "screen_capture.jpg".equals(currentImageHolder[0].getName())) {
                        try { currentImageHolder[0].delete(); } catch (Exception ignored) { /* Best-effort delete */ }
                    }
                    currentImageHolder[0] = null;
                    thumbView.setImageBitmap(null);
                    thumbView.setVisibility(View.GONE);
                    imgSub.setText(tr(activity,
                            "Capture the live ASYCUDA window or choose an image from your device",
                            "التقط صورة مباشرة لنافذة النظام أو اختر صورة من الهاتف",
                            "وێنەیەکی ڕاستەوخۆی پەنجەرەی ئەسیکۆدا بگرە یان لە مۆبایلەکەت هەڵبژێرە"));
                    imgSub.setTextColor(TEXT_SECONDARY);
                    v.setVisibility(View.GONE);
                });
        removeImgBtn.setVisibility(View.GONE);
        LinearLayout.LayoutParams remLp = (LinearLayout.LayoutParams) removeImgBtn.getLayoutParams();
        remLp.setMarginStart(dp(activity, 6));
        imgCard.addView(removeImgBtn);

        body.addView(imgCard);

        // Diagnostics Bundle Info Row + Diagnostic Data Preview Button
        LinearLayout zipInfoRow = new LinearLayout(activity);
        zipInfoRow.setOrientation(LinearLayout.VERTICAL);
        zipInfoRow.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));
        zipInfoRow.setBackground(createCardDrawable(activity, SURFACE_INSET_CARD, 12, SEPARATOR_HAIRLINE));

        TextView zipInfoText = new TextView(activity);
        zipInfoText.setText(tr(activity,
                "What gets sent with your report: When you submit this form, the app includes your phone model, Android version, app version, the list of installed system files, and recent error logs so the issue can be fixed quickly. Your login details, usernames, passwords, and tax numbers are automatically removed before sending, and no log files are saved on your phone.",
                "ما الذي يتم إرساله مع البلاغ: عند إرسال هذا النموذج، يرفق التطبيق طراز الهاتف، وإصدار الأندرويد، وإصدار التطبيق، وقائمة ملفات النظام المثبتة، وسجلات الأخطاء الأخيرة للمساعدة في حل المشكلة بسرعة. يتم تلقائياً حذف وإخفاء بيانات الدخول وأسماء المستخدمين وكلمات المرور والأرقام الضريبية قبل الإرسال، ولا يتم ترك أي ملفات سجلات محفوظة على هاتفك.",
                "چی لەگەڵ ڕاپۆرتەکەت دەنێردرێت: لە کاتی ناردنی ئەم فۆڕمەدا، جۆری مۆبایل، وەشانی ئەندرۆید، وەشانی بەرنامەکە، لیستی پەڕگەکانی سیستەم و تۆماری هەڵەکانی کۆتایی هاوپێچ دەکرێن بۆ چارەسەرکردنی خێرای کێشەکە. زانیاری چوونەژوورەوە و ناوی بەکارهێنەر و وشەی نهێنی بە شێوەی خۆکار لادەبرێن پێش ناردن."));
        zipInfoText.setTextColor(TEXT_SECONDARY);
        zipInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.2f);
        zipInfoRow.addView(zipInfoText);

        TextView previewRamBtn = createCupertinoButton(activity,
                tr(activity,
                        "Review Diagnostic Logs Before Sending",
                        "مراجعة السجلات التشخيصية قبل الإرسال",
                        "پێداچوونەوەی تۆمارە تەکنیکییەکان پێش ناردن"),
                false, false, v -> {
                    if (handler != null) {
                        String curDesc = descInput.getText() != null ? descInput.getText().toString().trim() : "";
                        StringBuilder ramBuffer = handler.buildRedactedDiagnosticPreviewInRam(curDesc);
                        showDiagnosticDataPreviewModal(activity, ramBuffer);
                    }
                });
        LinearLayout.LayoutParams prLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prLp.topMargin = dp(activity, 8);
        previewRamBtn.setLayoutParams(prLp);
        zipInfoRow.addView(previewRamBtn);

        ProgressBar uploadProgress = createHorizontalProgressBar(activity);
        uploadProgress.setIndeterminate(true);
        uploadProgress.setVisibility(View.GONE);
        LinearLayout.LayoutParams upLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, 5));
        upLp.topMargin = dp(activity, 8);
        zipInfoRow.addView(uploadProgress, upLp);

        body.addView(zipInfoRow);
        scroll.addView(body);
        root.addView(scroll);

        dialog.setOnDismissListener(d -> {
            if (descInput.getText() != null) {
                descInput.getText().clear();
            }
            if (currentImageHolder[0] != null && "screen_capture.jpg".equals(currentImageHolder[0].getName())) {
                try { currentImageHolder[0].delete(); } catch (Exception ignored) { /* Best-effort delete */ }
                currentImageHolder[0] = null;
            }
        });

        // Top-bar Send icon (replaces old bottom footer "Submit Report" button)
        final FrameLayout submitBtn = createIconButton(activity, R.drawable.ic_action_send,
                tr(activity, "Submit Report", "إرسال البلاغ", "ناردنی ڕاپۆرت"),
                true, false, null);
        submitBtn.setOnClickListener(v -> {
            String desc = descInput.getText() != null ? descInput.getText().toString().trim() : "";
            String auditError = auditFeedbackReport(activity, desc);
            if (auditError != null) {
                descInput.setError(auditError);
                descInput.requestFocus();
                zipInfoText.setText(auditError);
                zipInfoText.setTextColor(SYSTEM_RED);
                return;
            }
            if (handler != null && controllerRef[0] != null) {
                handler.onSubmitToSentry(desc, currentImageHolder[0], controllerRef[0], dialog);
            }
        });
        LinearLayout.LayoutParams sbLp = (LinearLayout.LayoutParams) submitBtn.getLayoutParams();
        sbLp.setMarginEnd(dp(activity, 8));
        header.addView(submitBtn, Math.max(0, header.indexOfChild(closeBtn)));

        controllerRef[0] = new FeedbackModalController() {
            @Override
            public void onImageAttached(File imageFile, String displayName) {
                activity.runOnUiThread(() -> {
                    currentImageHolder[0] = imageFile;
                    if (imageFile != null && imageFile.exists()) {
                        try {
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inSampleSize = 4;
                            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), opts);
                            if (bmp != null) {
                                thumbView.setImageBitmap(bmp);
                                thumbView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception ignored) { /* Best-effort UI operation */ }
                        long kb = Math.max(1, imageFile.length() / 1024);
                        imgSub.setText(displayName + " (" + kb + " KB)");
                        imgSub.setTextColor(SYSTEM_GREEN);
                        removeImgBtn.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void setUploadState(boolean uploading, String statusMessage, boolean success) {
                activity.runOnUiThread(() -> {
                    uploadProgress.setVisibility(uploading ? View.VISIBLE : View.GONE);
                    submitBtn.setEnabled(!uploading);
                    submitBtn.setAlpha(uploading ? 0.5f : 1.0f);
                    if (statusMessage != null) {
                        zipInfoText.setText(statusMessage);
                        int statusColor = uploading ? SYSTEM_BLUE : TEXT_PRIMARY;
                        zipInfoText.setTextColor(success ? SYSTEM_GREEN : statusColor);
                    }
                });
            }
        };

        dialog.setContentView(root);
        dialog.show();
        return controllerRef[0];
    }

    /**
     * Displays the redacted diagnostic payload strictly in RAM inside a scrollable modal.
     * Zero bytes are written to disk, and the mutable {@link StringBuilder} + {@link TextView}
     * buffers are zeroed/cleared immediately when the window is dismissed.
     */
    public static void showDiagnosticDataPreviewModal(MainActivity activity, final StringBuilder ramBuffer) {
        if (activity == null || activity.isFinishing()) return;
        Dialog dialog = createModalDialog(activity, 0.80f, 550, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_diagnostics);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_GREEN));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Diagnostic Logs Preview",
                "معاينة السجلات التشخيصية",
                "پێداچوونەوەی تۆمارە تەکنیکییەکان"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView sub = new TextView(activity);
        sub.setText(tr(activity,
                "Shown temporarily so you can verify what will be included in your report. Nothing is saved to your device storage.",
                "تُعرض هذه السجلات مؤقتاً لتتمكن من مراجعة البيانات التي ستُرفق مع البلاغ، ولا يتم حفظ أي نسخة منها على ذاكرة هاتفك.",
                "ئەم تۆمارانە بە شێوەی کاتی پیشان دەدرێن بۆ ئەوەی بزانیت چ زانیارییەک لەگەڵ ڕاپۆرتەکەت دەنێردرێت، و هیچ پەڕگەیەک لەسەر مۆبایلەکەت پاشەکەوت ناکرێت."));
        sub.setTextColor(SYSTEM_GREEN);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        titleCol.addView(sub);
        header.addView(titleCol);

        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Close", "إغلاق", "داخستن"),
                false, false, v -> dialog.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scLp.topMargin = dp(activity, 7);
        scroll.setLayoutParams(scLp);

        final TextView previewTv = new TextView(activity);
        previewTv.setText(ramBuffer != null ? ramBuffer : "");
        previewTv.setTextColor(TEXT_PRIMARY);
        previewTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        previewTv.setTypeface(Typeface.MONOSPACE);
        int p = dp(activity, 10);
        previewTv.setPadding(p, p, p, p);
        previewTv.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 10, SEPARATOR_HAIRLINE));
        scroll.addView(previewTv);
        root.addView(scroll);

        dialog.setOnDismissListener(d -> {
            previewTv.setText("");
            if (ramBuffer != null) {
                for (int i = 0; i < ramBuffer.length(); i++) {
                    ramBuffer.setCharAt(i, '\0');
                }
                ramBuffer.setLength(0);
                ramBuffer.trimToSize();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    /**
     * Displayed if the fail-closed JVM SecurityManager in {@code AsycudaBoot} fails to initialize,
     * explaining clearly to the user why the session was halted for their security.
     */
    public static void showSecuritySandboxErrorModal(MainActivity activity, String reason,
                                                     Runnable onRestartSession, Runnable onExitApp) {
        if (activity == null || activity.isFinishing()) return;
        Dialog dialog = createModalDialog(activity, 0.74f, 460, false);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Session Stopped for Your Security",
                "تم إيقاف الجلسة لحماية أمان بياناتك",
                "دانیشتنەکە ڕاگیرا بۆ پاراستنی ئاسایشی زانیارییەکانت"));
        title.setTextColor(SYSTEM_RED);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scLp.topMargin = dp(activity, 7);
        scLp.bottomMargin = dp(activity, 8);
        scroll.setLayoutParams(scLp);

        TextView body = new TextView(activity);
        body.setText(tr(activity,
                "The built-in security protection could not be started properly during launch. To keep your ASYCUDA account safe and ensure the app only communicates with the official Iraqi Customs server (asy.mof.gov.iq), the session was stopped automatically. Please restart the session to try again.\n\nTechnical details: " + (reason != null ? reason : "Unknown error"),
                "تعذر تشغيل نظام الحماية الداخلي بشكل صحيح عند بدء التشغيل. وللحفاظ على أمان حسابك في نظام أسيكودا وضمان اتصال التطبيق بخادم الكمارك العراقية الرسمي (asy.mof.gov.iq) فقط، تم إيقاف الجلسة تلقائياً. يرجى إعادة تشغيل الجلسة للمحاولة مرة أخرى.\n\nالتفاصيل الفنية: " + (reason != null ? reason : ""),
                "سیستەمی پاراستنی ناوخۆیی لە کاتی دەستپێکردندا بە دروستی کارینەکرد. بۆ پاراستنی هەژماری ئەسیکۆداکەت و دڵنیابوون لەوەی بەرنامەکە تەنها پەیوەندی بە ڕاژەی فەرمی گومرگی عێراق (asy.mof.gov.iq) دەکات، دانیشتنەکە بە شێوەی خۆکار ڕاگیرا. تکایە دانیشتنەکە دووبارە پێبکەرەوە.\n\nوردەکاری تەکنیکی: " + (reason != null ? reason : "")));
        body.setTextColor(TEXT_PRIMARY);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        int pad = dp(activity, 10);
        body.setPadding(pad, pad, pad, pad);
        body.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 10, Color.parseColor("#4DFF453A")));
        scroll.addView(body);
        root.addView(scroll);

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);

        TextView restartBtn = createCupertinoButton(activity,
                tr(activity, "Restart Session", "إعادة تشغيل الجلسة", "دووبارە پێکردنەوە"),
                true, false, v -> {
                    dialog.dismiss();
                    if (onRestartSession != null) onRestartSession.run();
                });
        LinearLayout.LayoutParams rLp = (LinearLayout.LayoutParams) restartBtn.getLayoutParams();
        rLp.setMarginEnd(dp(activity, 8));
        actions.addView(restartBtn);

        TextView exitBtn = createCupertinoButton(activity,
                tr(activity, "Exit App", "خروج من التطبيق", "چوونەدەرەوە"),
                false, true, v -> {
                    dialog.dismiss();
                    if (onExitApp != null) onExitApp.run();
                });
        actions.addView(exitBtn);
        root.addView(actions);

        dialog.setContentView(root);
        dialog.show();
    }

    private static Dialog sActiveNetworkDiagDialog;

    /**
     * Universal Error & Network Diagnostic Modal (Proposal B + Sentry Known-Issues Catalog).
     * Displays a natural, human-written explanation of any connection, system, or runtime error in EN/AR/KU,
     * highlights brand-new uncatalogued errors with a prompt to share to Sentry, provides a user-consented
     * Verified Fix button for known issues, and includes a compact developer button
     * ({@code </> View Error Code & Technical Log}) for full stack/socket telemetry.
     */
    public static void showUniversalNetworkDiagnosticModal(
            MainActivity activity,
            String errorCode,
            String causeBadgeText,
            String humanExplanation,
            final StringBuilder technicalLogRamBuffer,
            boolean isNewUncataloguedError,
            String verifiedFixButtonText,
            Runnable onApplyVerifiedFix,
            Runnable onPreviewFullRamReport,
            Runnable onRetryConnection,
            Runnable onOpenReportCenter) {
        if (activity == null || activity.isFinishing()) return;
        try {
            if (sActiveNetworkDiagDialog != null && sActiveNetworkDiagDialog.isShowing()) {
                sActiveNetworkDiagDialog.dismiss();
            }
        } catch (Exception ignored) {
            // Ignore dismiss of previous dialog
        }

        final Dialog dialog = createModalDialog(activity, 0.80f, 550, true);
        sActiveNetworkDiagDialog = dialog;
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 7));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_diagnostics);
        int accentColor = Color.parseColor(isNewUncataloguedError ? "#FFBF5AF2" : "#FFFF9F0A");
        icon.setImageTintList(ColorStateList.valueOf(accentColor));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Universal Error & Network Diagnostics",
                "التشخيص الشامل للأعطال والشبكة",
                "پشکنینی گشتگیری هەڵە و تۆڕ"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView sub = new TextView(activity);
        String codeLabel = (errorCode != null && !errorCode.isEmpty()) ? errorCode : "DIAGNOSTIC";
        String badgeLabel = (causeBadgeText != null && !causeBadgeText.isEmpty())
                ? (codeLabel + "  •  " + causeBadgeText) : codeLabel;
        sub.setText(badgeLabel);
        sub.setTextColor(accentColor);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        sub.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(sub);

        header.addView(titleCol);
        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Close", "إغلاق", "داخستن"),
                false, false, v -> dialog.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scLp.topMargin = dp(activity, 7);
        scLp.bottomMargin = dp(activity, 6);
        scroll.setLayoutParams(scLp);

        LinearLayout contentCol = new LinearLayout(activity);
        contentCol.setOrientation(LinearLayout.VERTICAL);

        // 0. If this is a brand-new / uncatalogued error, show a prominent highlight banner suggesting Sentry share
        if (isNewUncataloguedError) {
            TextView newErrBanner = new TextView(activity);
            newErrBanner.setText(tr(activity,
                    "NEW UNCATALOGUED ERROR DETECTED\nThis error is not yet recorded in the Known Issues Catalog. Please tap 'Share to Sentry' below so it can be identified and given a direct fix.",
                    "تم رصد خطأ جديد غير مسجل في قاعدة البيانات\nهذا الخطأ غير مدرج بعد في سجل الأعطال المعروفة. يرجى الضغط على زر (مشاركة إلى Sentry) بالأسفل ليتم تشخيصه وإضافة حل مباشر له.",
                    "هەڵەیەکی نوێی تۆمارنەکراو دۆزرایەوە\nئەم هەڵەیە هێشتا لە بنکەی زانیاری تۆمار نەکراوە. تکایە دوگمەی (هاوبەشکردن بۆ Sentry) لە خوارەوە دابگرە بۆ زیادکردنی چارەسەر."));
            newErrBanner.setTextColor(Color.WHITE);
            newErrBanner.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.2f);
            newErrBanner.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            newErrBanner.setLineSpacing(dp(activity, 2), 1.04f);
            int bPad = dp(activity, 9);
            newErrBanner.setPadding(bPad, bPad, bPad, bPad);
            newErrBanner.setBackground(createCardDrawable(activity, Color.parseColor("#38BF5AF2"), 9, Color.parseColor("#FFBF5AF2")));
            LinearLayout.LayoutParams bannerLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bannerLp.bottomMargin = dp(activity, 6);
            newErrBanner.setLayoutParams(bannerLp);
            contentCol.addView(newErrBanner);
        }

        // 1. Human-understandable explanation card
        TextView explanationTv = new TextView(activity);
        explanationTv.setText(humanExplanation != null ? humanExplanation : "");
        explanationTv.setTextColor(TEXT_PRIMARY);
        explanationTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        explanationTv.setLineSpacing(dp(activity, 2), 1.06f);
        int pad = dp(activity, 10);
        explanationTv.setPadding(pad, pad, pad, pad);
        explanationTv.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 10,
                Color.parseColor(isNewUncataloguedError ? "#66BF5AF2" : "#4DFF9F0A")));
        contentCol.addView(explanationTv);

        // 1b. User-Consented Verified Self-Healing Fix Card & Button (Only executed when user chooses to click)
        if (verifiedFixButtonText != null && !verifiedFixButtonText.trim().isEmpty() && onApplyVerifiedFix != null) {
            LinearLayout fixRow = new LinearLayout(activity);
            fixRow.setOrientation(LinearLayout.VERTICAL);
            fixRow.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
            fixRow.setBackground(createCardDrawable(activity, Color.parseColor("#1F30D158"), 10, SYSTEM_GREEN));
            LinearLayout.LayoutParams fixRowLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fixRowLp.topMargin = dp(activity, 6);
            fixRow.setLayoutParams(fixRowLp);

            TextView fixHint = new TextView(activity);
            fixHint.setText(tr(activity,
                    "Verified Security-Checked Solution Available (Requires Your Approval):",
                    "يتوفر حل آمن ومعتمد لهذه المشكلة (يتم تطبيقه باختيارك فقط):",
                    "چارەسەری پارێزراو و باوەڕپێکراو بەردەستە (تەنها بە ڕەزامەندی تۆ جێبەجێ دەکرێت):"));
            fixHint.setTextColor(SYSTEM_GREEN);
            fixHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
            fixHint.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            fixRow.addView(fixHint);

            TextView applyFixBtn = createCupertinoButton(activity,
                    verifiedFixButtonText,
                    true, false, v -> {
                        dialog.dismiss();
                        onApplyVerifiedFix.run();
                    });
            applyFixBtn.setBackground(createCardDrawable(activity, SYSTEM_GREEN, 8, Color.parseColor("#FF248A3D")));
            applyFixBtn.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams applyLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            applyLp.topMargin = dp(activity, 5);
            applyFixBtn.setLayoutParams(applyLp);
            fixRow.addView(applyFixBtn);
            contentCol.addView(fixRow);
        }

        // 2. Collapsible Developer / Technical Log Section (100% Redacted & In-RAM Only, Zeroed on Dismiss)
        final LinearLayout devCard = new LinearLayout(activity);
        devCard.setOrientation(LinearLayout.VERTICAL);
        devCard.setVisibility(View.GONE);
        devCard.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        devCard.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 10, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams devCardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        devCardLp.topMargin = dp(activity, 6);
        devCard.setLayoutParams(devCardLp);

        LinearLayout devHeaderRow = new LinearLayout(activity);
        devHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        devHeaderRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout devTitleCol = new LinearLayout(activity);
        devTitleCol.setOrientation(LinearLayout.VERTICAL);
        devTitleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView devTitle = new TextView(activity);
        devTitle.setText(tr(activity,
                "Redacted Diagnostic Trace (RAM-Only • Zero Disk Writes)",
                "السجل التشخيصي المنقّح (في الذاكرة المؤقتة RAM فقط)",
                "تۆماری تەکنیکی پاککراوە (تەنها لە بیرگەی کاتی RAM)"));
        devTitle.setTextColor(SYSTEM_GREEN);
        devTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.2f);
        devTitle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        devTitleCol.addView(devTitle);

        TextView devPrivacySub = new TextView(activity);
        devPrivacySub.setText(tr(activity,
                "All credentials, usernames, passwords, TINs, tokens, and server IPs are automatically scrubbed. Buffer is zeroed when closed.",
                "يتم تلقائياً إخفاء بيانات الدخول وأسماء المستخدمين وكلمات المرور والأرقام الضريبية وعناوين الخادم، ويتم مسح الذاكرة فور الإغلاق.",
                "زانیاری چوونەژوورەوە و ناوی بەکارهێنەر و وشەی نهێنی بە شێوەی خۆکار لادەبرێن و لە کاتی داخستندا دەسڕێنەوە."));
        devPrivacySub.setTextColor(TEXT_SECONDARY);
        devPrivacySub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9.8f);
        devTitleCol.addView(devPrivacySub);
        devHeaderRow.addView(devTitleCol);

        if (onPreviewFullRamReport != null) {
            TextView fullRamPreviewBtn = createCupertinoButton(activity,
                    tr(activity, "Full RAM Preview", "معاينة السجل الكامل", "پێداچوونەوەی تەواو"),
                    false, false, v -> onPreviewFullRamReport.run());
            devHeaderRow.addView(fullRamPreviewBtn);
        }
        devCard.addView(devHeaderRow);

        final TextView devLogTv = new TextView(activity);
        devLogTv.setText(technicalLogRamBuffer != null ? technicalLogRamBuffer : "No technical log available.");
        devLogTv.setTextColor(TEXT_PRIMARY);
        devLogTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        devLogTv.setTypeface(Typeface.MONOSPACE);
        devLogTv.setTextIsSelectable(false);
        LinearLayout.LayoutParams logTvLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logTvLp.topMargin = dp(activity, 6);
        devLogTv.setLayoutParams(logTvLp);
        devCard.addView(devLogTv);

        dialog.setOnDismissListener(d -> {
            devLogTv.setText("");
            if (technicalLogRamBuffer != null) {
                for (int i = 0; i < technicalLogRamBuffer.length(); i++) {
                    technicalLogRamBuffer.setCharAt(i, '\0');
                }
                technicalLogRamBuffer.setLength(0);
                technicalLogRamBuffer.trimToSize();
            }
        });

        // Small Developer / Nerd Button to toggle the technical log
        final TextView nerdBtn = createCupertinoButton(activity,
                tr(activity,
                        "</> View Error Code & Technical Log",
                        "</> عرض كود الخطأ والسجل التقني",
                        "</> پیشاندانی کۆدی هەڵە و تۆماری تەکنیکی"),
                false, false, null);
        nerdBtn.setOnClickListener(v -> {
            boolean showing = devCard.getVisibility() == View.VISIBLE;
            devCard.setVisibility(showing ? View.GONE : View.VISIBLE);
            nerdBtn.setText(showing
                    ? tr(activity,
                            "</> View Error Code & Technical Log",
                            "</> عرض كود الخطأ والسجل التقني",
                            "</> پیشاندانی کۆدی هەڵە و تۆماری تەکنیکی")
                    : tr(activity,
                            "</> Hide Error Code & Technical Log",
                            "</> إخفاء كود الخطأ والسجل التقني",
                            "</> شاردنەوەی کۆدی هەڵە و تۆماری تەکنیکی"));
        });
        LinearLayout.LayoutParams nerdLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nerdLp.topMargin = dp(activity, 10);
        nerdBtn.setLayoutParams(nerdLp);
        contentCol.addView(nerdBtn);
        contentCol.addView(devCard);

        scroll.addView(contentCol);
        root.addView(scroll);

        // Footer Buttons: Share to Sentry + Retry Connection
        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        if (onOpenReportCenter != null) {
            TextView reportBtn = createCupertinoButton(activity,
                    isNewUncataloguedError
                            ? tr(activity, "Share New Error to Sentry", "مشاركة الخطأ الجديد إلى Sentry", "هاوبەشکردنی هەڵەی نوێ بۆ Sentry")
                            : tr(activity, "Share to Sentry / Report", "مشاركة إلى Sentry / إبلاغ", "هاوبەشکردن بۆ Sentry"),
                    isNewUncataloguedError, false, v -> {
                        dialog.dismiss();
                        onOpenReportCenter.run();
                    });
            LinearLayout.LayoutParams repLp = (LinearLayout.LayoutParams) reportBtn.getLayoutParams();
            repLp.setMarginEnd(dp(activity, 8));
            actions.addView(reportBtn);
        }

        TextView retryBtn = createCupertinoButton(activity,
                tr(activity, "Retry Session", "إعادة المحاولة", "دووبارە هەوڵدانەوە"),
                !isNewUncataloguedError, false, v -> {
                    dialog.dismiss();
                    if (onRetryConnection != null) {
                        onRetryConnection.run();
                    }
                });
        actions.addView(retryBtn);
        root.addView(actions);

        dialog.setContentView(root);
        dialog.show();
    }

    public static void showLanguageModal(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.72f, 440, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_language);
        headerIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        hiLp.setMarginEnd(dp(activity, 8));
        header.addView(headerIcon, hiLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity, "Interface Language", "لغة واجهة التطبيق", "زمانی ڕووکاری بەرنامە"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createIconButton(activity, R.drawable.ic_nav_back,
                tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER),
                false, false, v -> {
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                }));
        dialog.setOnCancelListener(d -> showControlCenter(activity, cb));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        String current = getAppLanguage(activity);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scLp.topMargin = dp(activity, 7);
        scroll.setLayoutParams(scLp);

        LinearLayout list = new LinearLayout(activity);
        list.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        list.setLayoutParams(listLp);

        addLanguageOptionCard(activity, list, "en",
                "English",
                "Display all menus, settings, and alerts in English",
                "en".equals(current), () -> {
                    setAppLanguage(activity, "en");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        addLanguageOptionCard(activity, list, "ar",
                "العربية (النسخة الرسمية)",
                "عرض جميع القوائم والإعدادات والتنبيهات باللغة العربية",
                "ar".equals(current), () -> {
                    setAppLanguage(activity, "ar");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        addLanguageOptionCard(activity, list, "ku",
                "کوردی (سۆرانی)",
                "پیشاندانی سەرجەم لیست و ڕێکخستنەکان بە زمانی کوردی",
                "ku".equals(current), () -> {
                    setAppLanguage(activity, "ku");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        scroll.addView(list);
        root.addView(scroll);
        dialog.setContentView(root);
        dialog.show();
    }

    private static void addLanguageOptionCard(Context ctx, LinearLayout parent,
                                              String code, String titleText, String subText,
                                              boolean isSelected, Runnable onSelect) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dp(ctx, 11);
        row.setPadding(pad, dp(ctx, 9), pad, dp(ctx, 9));
        row.setBackground(createRippleCardDrawable(ctx,
                SURFACE_GROUPED_CARD,
                11,
                isSelected ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> onSelect.run());
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.bottomMargin = dp(ctx, 6);
        row.setLayoutParams(rlp);

        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(ctx);
        t.setText(titleText);
        t.setTextColor(TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        t.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        textCol.addView(t);

        TextView s = new TextView(ctx);
        s.setText(subText);
        s.setTextColor(TEXT_SECONDARY);
        s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        textCol.addView(s);
        row.addView(textCol);

        TextView badge = new TextView(ctx);
        badge.setText(isSelected
                ? tr(ctx, "Active", AR_ON, "هەڵبژێردراو")
                : code.toUpperCase(Locale.US));
        badge.setTextColor(isSelected ? SYSTEM_BLUE : TEXT_SECONDARY);
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        badge.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        row.addView(badge);

        parent.addView(row);
    }

    private static void addMenuTileWithBadge(Context ctx, GridLayout grid,
                                             int iconResId, String titleText, String descText,
                                             String statusBadgeText,
                                             boolean isAccentIcon, boolean isDestructive,
                                             Runnable onClick) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dp(ctx, 10);
        card.setPadding(pad, dp(ctx, 8), pad, dp(ctx, 8));
        card.setBackground(createRippleCardDrawable(ctx,
                SURFACE_GROUPED_CARD, 11,
                isDestructive ? Color.parseColor("#4DFF453A") : SEPARATOR_HAIRLINE));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> onClick.run());

        int defaultBg = isAccentIcon ? SYSTEM_BLUE_TINT : SYSTEM_GRAY_FILL;
        int badgeBgColor = isDestructive ? SYSTEM_RED_TINT : defaultBg;
        int defaultTint = isAccentIcon ? SYSTEM_BLUE : TEXT_PRIMARY;
        int iconTint = isDestructive ? SYSTEM_RED : defaultTint;

        FrameLayout badgeBox = new FrameLayout(ctx);
        badgeBox.setBackground(createCardDrawable(ctx, badgeBgColor, 8, 0));
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(dp(ctx, 32), dp(ctx, 32));
        badgeLp.setMarginEnd(dp(ctx, 9));

        ImageView iconView = new ImageView(ctx);
        iconView.setImageResource(iconResId);
        iconView.setImageTintList(ColorStateList.valueOf(iconTint));
        FrameLayout.LayoutParams icLp = new FrameLayout.LayoutParams(dp(ctx, 17), dp(ctx, 17), Gravity.CENTER);
        badgeBox.addView(iconView, icLp);
        card.addView(badgeBox, badgeLp);

        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(ctx);
        t.setText(titleText);
        t.setTextColor(isDestructive ? SYSTEM_RED : TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.2f);
        t.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        textCol.addView(t);

        TextView d = new TextView(ctx);
        d.setText(descText);
        d.setTextColor(TEXT_SECONDARY);
        d.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.2f);
        textCol.addView(d);

        card.addView(textCol);

        if (statusBadgeText != null && !statusBadgeText.isEmpty()) {
            TextView pill = new TextView(ctx);
            pill.setText(statusBadgeText);
            boolean isOn = isAccentIcon;
            pill.setTextColor(isOn ? SYSTEM_GREEN : TEXT_SECONDARY);
            pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9.8f);
            pill.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            pill.setPadding(dp(ctx, 6), dp(ctx, 2), dp(ctx, 6), dp(ctx, 2));
            pill.setBackground(createCardDrawable(ctx,
                    isOn ? SYSTEM_GREEN_TINT : SURFACE_INSET_CARD,
                    6,
                    isOn ? Color.parseColor("#4D30D158") : SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            plp.setMarginStart(dp(ctx, 5));
            card.addView(pill, plp);
        }

        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.width = 0;
        glp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        int m = dp(ctx, 3);
        glp.setMargins(m, m, m, m);
        grid.addView(card, glp);
    }

    /**
     * Vault — File Manager Modal with Full Folder Navigation, Folder Creation,
     * File Import, Individual Open/Share/Delete Icons, and Multi-Select Batch Actions.
     */
    public static void showFilesModal(MainActivity activity, MenuActionCallback cb) {
        File vaultRoot = new File(activity.getFilesDir(), "prints");
        if (!vaultRoot.exists() && !vaultRoot.mkdirs()) {
            // Directory exists or created concurrently
        }
        showVaultFileManagerModal(activity, vaultRoot, vaultRoot, cb);
    }

    public static void showVaultFileManagerModal(MainActivity activity, File vaultRoot, File requestedDir, MenuActionCallback cb) {
        if (activity == null || activity.isFinishing()) return;
        if (!vaultRoot.exists() && !vaultRoot.mkdirs()) {
            // Root exists
        }
        File currentDir = (requestedDir != null && requestedDir.exists() && requestedDir.isDirectory()
                && isSubPathOrEqual(vaultRoot, requestedDir)) ? requestedDir : vaultRoot;
        final boolean isAtRoot = currentDir.equals(vaultRoot);

        File[] rawChildren = currentDir.listFiles();
        final List<File> items = new ArrayList<>();
        if (rawChildren != null) {
            for (File f : rawChildren) {
                String n = f.getName();
                if (n.startsWith(".") || n.endsWith(".apk")) continue;
                if (f.isDirectory() || (f.isFile() && f.length() > 0)) {
                    items.add(f);
                }
            }
        }
        items.sort((a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            if (a.isDirectory()) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
            return Long.compare(b.lastModified(), a.lastModified());
        });

        Dialog dialog = createModalDialog(activity, 0.84f, 580, true);
        LinearLayout root = createDialogContainer(activity);

        final Set<File> selectedItems = new LinkedHashSet<>();

        // Top Bar with Title + Clean Apple Icon Buttons
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_folder);
        headerIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        hiLp.setMarginEnd(dp(activity, 8));
        header.addView(headerIcon, hiLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Vault — File Manager",
                "الخزنة — مدير الملفات",
                "قاسە — بەڕێوەبەری پەڕگەکان"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subPath = new TextView(activity);
        String relPath = getRelativeVaultPath(vaultRoot, currentDir);
        subPath.setText(isAtRoot
                ? tr(activity, "Private Document Storage  •  /Vault", "خزنة المستندات الخاصة  •  /الخزنة", "بیرگەی بەڵگەنامە تایبەتەکان  •  /قاسە")
                : "📁 /Vault/" + relPath);
        subPath.setTextColor(isAtRoot ? TEXT_SECONDARY : SYSTEM_BLUE);
        subPath.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        titleCol.addView(subPath);
        header.addView(titleCol);

        // 1. Encrypted Vault Info (!) Button
        TextView vaultInfoBadge = new TextView(activity);
        vaultInfoBadge.setText("!");
        vaultInfoBadge.setGravity(Gravity.CENTER);
        vaultInfoBadge.setTextColor(Color.WHITE);
        vaultInfoBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        vaultInfoBadge.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        vaultInfoBadge.setBackground(createRippleCardDrawable(activity, SYSTEM_BLUE_TINT, 9, SYSTEM_BLUE));
        LinearLayout.LayoutParams vibLp = new LinearLayout.LayoutParams(dp(activity, 32), dp(activity, 32));
        vibLp.setMarginEnd(dp(activity, 6));
        vaultInfoBadge.setLayoutParams(vibLp);
        vaultInfoBadge.setOnClickListener(v -> showEncryptedVaultInfoModal(activity));
        header.addView(vaultInfoBadge);

        // 2. Import File from Phone Icon Button
        View importFileBtn = createIconButton(activity, R.drawable.ic_file_import,
                tr(activity, "Import File from Phone", "استيراد ملف من الهاتف", "هاوردەکردنی پەڕگە لە مۆبایل"),
                true, false, v -> {
                    if (cb != null) {
                        cb.onImportFileToVault(currentDir, () -> {
                            try {
                                if (dialog.isShowing()) dialog.dismiss();
                            } catch (Exception ignored) { /* Best-effort UI operation */ }
                            showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                        });
                    }
                });
        LinearLayout.LayoutParams impLp = (LinearLayout.LayoutParams) importFileBtn.getLayoutParams();
        impLp.setMarginEnd(dp(activity, 8));
        header.addView(importFileBtn);

        // 3. New Folder Icon Button
        View newFolderBtn = createIconButton(activity, R.drawable.ic_folder_add,
                tr(activity, "New Folder", "مجلد جديد", "بوخچەی نوێ"),
                false, false, v -> showCreateFolderDialog(activity, currentDir, () -> {
                    dialog.dismiss();
                    showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                }));
        LinearLayout.LayoutParams nfLp = (LinearLayout.LayoutParams) newFolderBtn.getLayoutParams();
        nfLp.setMarginEnd(dp(activity, 8));
        header.addView(newFolderBtn);

        // 4. Select All Icon Button (when non-empty)
        final View selectAllBtn;
        if (!items.isEmpty()) {
            selectAllBtn = createIconButton(activity, R.drawable.ic_select_all,
                    tr(activity, "Select All", "تحديد الكل", "دیاریکردنی هەموو"),
                    false, false, null);
            LinearLayout.LayoutParams saLp = (LinearLayout.LayoutParams) selectAllBtn.getLayoutParams();
            saLp.setMarginEnd(dp(activity, 8));
            header.addView(selectAllBtn);
        } else {
            selectAllBtn = null;
        }

        // 5. Back Icon Button (Navigates up to Parent Folder if in subfolder, or Control Center if at root)
        Runnable navigateBackAction = () -> {
            dialog.dismiss();
            if (!isAtRoot) {
                File parent = currentDir.getParentFile();
                showVaultFileManagerModal(activity, vaultRoot, parent != null ? parent : vaultRoot, cb);
            } else {
                showControlCenter(activity, cb);
            }
        };
        View backBtn = createIconButton(activity, R.drawable.ic_nav_back,
                tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER),
                false, false, v -> navigateBackAction.run());
        header.addView(backBtn);
        dialog.setOnCancelListener(d -> navigateBackAction.run());

        root.addView(header);
        root.addView(createHairlineDivider(activity));

        // Subfolder Navigation Bar (shown when inside a subfolder for one-tap return to parent folder)
        if (!isAtRoot) {
            LinearLayout pathBar = new LinearLayout(activity);
            pathBar.setOrientation(LinearLayout.HORIZONTAL);
            pathBar.setGravity(Gravity.CENTER_VERTICAL);
            pathBar.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));
            pathBar.setBackground(createRippleCardDrawable(activity, SURFACE_INSET_CARD, 12, SEPARATOR_HAIRLINE));
            pathBar.setClickable(true);
            pathBar.setFocusable(true);
            pathBar.setOnClickListener(v -> navigateBackAction.run());
            LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pbLp.topMargin = dp(activity, 10);
            pathBar.setLayoutParams(pbLp);

            ImageView upIcon = new ImageView(activity);
            upIcon.setImageResource(R.drawable.ic_nav_back);
            upIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
            LinearLayout.LayoutParams upLp = new LinearLayout.LayoutParams(dp(activity, 18), dp(activity, 18));
            upLp.setMarginEnd(dp(activity, 8));
            pathBar.addView(upIcon, upLp);

            TextView upLabel = new TextView(activity);
            upLabel.setText(tr(activity,
                    "Up to parent folder  •  /Vault/" + relPath,
                    "العودة للمجلد السابق  •  /Vault/" + relPath,
                    "گەڕانەوە بۆ بوخچەی پێشوو  •  /Vault/" + relPath));
            upLabel.setTextColor(SYSTEM_BLUE);
            upLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
            upLabel.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            pathBar.addView(upLabel);
            root.addView(pathBar);
        }

        if (!items.isEmpty()) {
            // Multi-select batch action bar (shown when 1+ items are selected)
            LinearLayout batchBar = new LinearLayout(activity);
            batchBar.setOrientation(LinearLayout.HORIZONTAL);
            batchBar.setGravity(Gravity.CENTER_VERTICAL);
            batchBar.setPadding(dp(activity, 14), dp(activity, 10), dp(activity, 14), dp(activity, 10));
            batchBar.setBackground(createCardDrawable(activity, SURFACE_INSET_CARD, 12, SYSTEM_BLUE));
            LinearLayout.LayoutParams bbLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bbLp.topMargin = dp(activity, 10);
            batchBar.setLayoutParams(bbLp);
            batchBar.setVisibility(View.GONE);

            TextView selectedCountView = new TextView(activity);
            selectedCountView.setTextColor(TEXT_PRIMARY);
            selectedCountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            selectedCountView.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            selectedCountView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            batchBar.addView(selectedCountView);

            View renameSelectedBtn = createIconButton(activity, R.drawable.ic_action_edit,
                    tr(activity, "Rename", "إعادة تسمية", "گۆڕینی ناو"),
                    false, false, v -> {
                        if (selectedItems.size() != 1) return;
                        File singleTarget = selectedItems.iterator().next();
                        showRenameItemDialog(activity, singleTarget, () -> {
                            dialog.dismiss();
                            showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                        });
                    });
            LinearLayout.LayoutParams rsLp = (LinearLayout.LayoutParams) renameSelectedBtn.getLayoutParams();
            rsLp.setMarginEnd(dp(activity, 8));
            batchBar.addView(renameSelectedBtn);

            View copySelectedBtn = createIconButton(activity, R.drawable.ic_action_copy,
                    tr(activity, "Copy Selected", "نسخ المحدد", "کۆپیکردنی دیاریکراو"),
                    false, false, v -> {
                        if (selectedItems.isEmpty()) return;
                        showCopyMoveDestinationDialog(activity, dialog, vaultRoot, currentDir,
                                new ArrayList<>(selectedItems), true, cb);
                    });
            LinearLayout.LayoutParams csLp = (LinearLayout.LayoutParams) copySelectedBtn.getLayoutParams();
            csLp.setMarginEnd(dp(activity, 8));
            batchBar.addView(copySelectedBtn);

            View moveSelectedBtn = createIconButton(activity, R.drawable.ic_action_move,
                    tr(activity, "Move Selected", "نقل المحدد", "گواستنەوەی دیاریکراو"),
                    false, false, v -> {
                        if (selectedItems.isEmpty()) return;
                        showCopyMoveDestinationDialog(activity, dialog, vaultRoot, currentDir,
                                new ArrayList<>(selectedItems), false, cb);
                    });
            LinearLayout.LayoutParams msLp = (LinearLayout.LayoutParams) moveSelectedBtn.getLayoutParams();
            msLp.setMarginEnd(dp(activity, 8));
            batchBar.addView(moveSelectedBtn);

            View shareSelectedBtn = createIconButton(activity, R.drawable.ic_action_share,
                    tr(activity, "Share Selected", "مشاركة المحدد", "هاوبەشکردنی دیاریکراو"),
                    true, false, v -> {
                        List<File> toShare = new ArrayList<>();
                        for (File sel : selectedItems) {
                            if (sel.isFile()) toShare.add(sel);
                        }
                        if (toShare.isEmpty()) {
                            Toast.makeText(activity, tr(activity,
                                    "Folders cannot be shared — select files only",
                                    "لا يمكن مشاركة المجلدات — حدد ملفات فقط",
                                    "بوخچەکان هاوبەش ناکرێن — تەنها پەڕگە دیاری بکە"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        if (toShare.size() == 1) {
                            cb.onShareFile(toShare.get(0));
                        } else {
                            cb.onShareMultipleFiles(toShare);
                        }
                    });
            LinearLayout.LayoutParams ssLp = (LinearLayout.LayoutParams) shareSelectedBtn.getLayoutParams();
            ssLp.setMarginEnd(dp(activity, 8));
            batchBar.addView(shareSelectedBtn);

            View deleteSelectedBtn = createIconButton(activity, R.drawable.ic_action_delete,
                    tr(activity, "Delete Selected", "حذف المحدد", "سڕینەوەی دیاریکراو"),
                    false, true, v -> {
                        if (selectedItems.isEmpty()) return;
                        for (File sel : selectedItems) {
                            deleteItemRecursive(sel);
                        }
                        Toast.makeText(activity, tr(activity,
                                "Deleted " + selectedItems.size() + " item(s)",
                                "تم حذف " + selectedItems.size() + " عنصر",
                                selectedItems.size() + " دانە سڕایەوە"), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                    });
            batchBar.addView(deleteSelectedBtn);
            root.addView(batchBar);

            ScrollView scroll = new ScrollView(activity);
            scroll.setFillViewport(true);
            LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            scrollLp.topMargin = dp(activity, 10);
            scroll.setLayoutParams(scrollLp);

            LinearLayout listBox = new LinearLayout(activity);
            listBox.setOrientation(LinearLayout.VERTICAL);

            final List<TextView> checkIndicators = new ArrayList<>();
            final List<LinearLayout> rowCards = new ArrayList<>();

            Runnable updateBatchUi = () -> {
                int count = selectedItems.size();
                if (count > 0) {
                    batchBar.setVisibility(View.VISIBLE);
                    selectedCountView.setText(tr(activity,
                            count + " selected",
                            "تم تحديد " + count,
                            count + " دیاریکراوە"));
                    renameSelectedBtn.setVisibility(count == 1 ? View.VISIBLE : View.GONE);
                    boolean hasSelectedFolder = false;
                    for (File sel : selectedItems) {
                        if (sel.isDirectory()) {
                            hasSelectedFolder = true;
                            break;
                        }
                    }
                    // Folders cannot be shared: hide Share button whenever any folder is selected
                    shareSelectedBtn.setVisibility(hasSelectedFolder ? View.GONE : View.VISIBLE);
                } else {
                    batchBar.setVisibility(View.GONE);
                }
                for (int i = 0; i < items.size(); i++) {
                    File itemFile = items.get(i);
                    boolean sel = selectedItems.contains(itemFile);
                    TextView chk = checkIndicators.get(i);
                    LinearLayout rowCard = rowCards.get(i);
                    chk.setText(sel ? "✓" : "");
                    chk.setBackground(createCardDrawable(activity,
                            sel ? SYSTEM_BLUE : SURFACE_INPUT_FIELD,
                            8,
                            sel ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
                    rowCard.setBackground(createCardDrawable(activity,
                            SURFACE_GROUPED_CARD, 14,
                            sel ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
                }
            };

            if (selectAllBtn != null) {
                selectAllBtn.setOnClickListener(v -> {
                    if (selectedItems.size() == items.size()) {
                        selectedItems.clear();
                    } else {
                        selectedItems.addAll(items);
                    }
                    updateBatchUi.run();
                });
            }

            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            for (File f : items) {
                final boolean isDir = f.isDirectory();
                LinearLayout row = new LinearLayout(activity);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                int padH = dp(activity, 11);
                int padV = dp(activity, 9);
                row.setPadding(padH, padV, padH, padV);
                row.setBackground(createCardDrawable(activity,
                        SURFACE_GROUPED_CARD, 12, SEPARATOR_HAIRLINE));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.bottomMargin = dp(activity, 7);
                row.setLayoutParams(rlp);

                // 1. Multi-select checkbox pill on the start side
                TextView checkBtn = new TextView(activity);
                checkBtn.setGravity(Gravity.CENTER);
                checkBtn.setTextColor(TEXT_PRIMARY);
                checkBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                checkBtn.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
                checkBtn.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 7, SEPARATOR_HAIRLINE));
                LinearLayout.LayoutParams chkLp = new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24));
                chkLp.setMarginEnd(dp(activity, 8));
                checkBtn.setLayoutParams(chkLp);

                View.OnClickListener toggleRowSelection = v -> {
                    if (selectedItems.contains(f)) {
                        selectedItems.remove(f);
                    } else {
                        selectedItems.add(f);
                    }
                    updateBatchUi.run();
                };
                checkBtn.setOnClickListener(toggleRowSelection);
                row.addView(checkBtn);

                // 2. Folder / File Icon Badge (32dp x 32dp)
                FrameLayout typeBadge = new FrameLayout(activity);
                typeBadge.setBackground(createCardDrawable(activity,
                        isDir ? SYSTEM_BLUE_TINT : SYSTEM_GRAY_FILL, 9, 0));
                LinearLayout.LayoutParams tbLp = new LinearLayout.LayoutParams(dp(activity, 32), dp(activity, 32));
                tbLp.setMarginEnd(dp(activity, 10));
                ImageView typeIcon = new ImageView(activity);
                typeIcon.setImageResource(isDir ? R.drawable.ic_folder : R.drawable.ic_menu_documents);
                typeIcon.setImageTintList(ColorStateList.valueOf(isDir ? SYSTEM_BLUE : TEXT_PRIMARY));
                typeBadge.addView(typeIcon, new FrameLayout.LayoutParams(dp(activity, 17), dp(activity, 17), Gravity.CENTER));
                row.addView(typeBadge, tbLp);

                // 3. Name + Metadata Column (Spacious, single-line middle ellipsize so buttons never crowd)
                LinearLayout infoCol = new LinearLayout(activity);
                infoCol.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                infoLp.setMarginEnd(dp(activity, 8));
                infoCol.setLayoutParams(infoLp);

                View.OnClickListener primaryRowTap = isDir
                        ? (v -> {
                            dialog.dismiss();
                            showVaultFileManagerModal(activity, vaultRoot, f, cb);
                        })
                        : toggleRowSelection;
                typeBadge.setOnClickListener(primaryRowTap);
                infoCol.setOnClickListener(primaryRowTap);

                TextView nameView = new TextView(activity);
                nameView.setText(f.getName());
                nameView.setTextColor(TEXT_PRIMARY);
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.8f);
                nameView.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
                nameView.setSingleLine(true);
                nameView.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
                infoCol.addView(nameView);

                TextView metaView = new TextView(activity);
                if (isDir) {
                    int childCount = countVisibleChildren(f);
                    metaView.setText(tr(activity,
                            "Folder  •  " + childCount + " item(s)  •  " + dateFmt.format(new Date(f.lastModified())),
                            "مجلد  •  " + childCount + " عنصر  •  " + dateFmt.format(new Date(f.lastModified())),
                            "بوخچە  •  " + childCount + " دانە  •  " + dateFmt.format(new Date(f.lastModified()))));
                    metaView.setTextColor(SYSTEM_BLUE);
                } else {
                    long kb = Math.max(1, f.length() / 1024);
                    metaView.setText(kb + " KB  •  " + dateFmt.format(new Date(f.lastModified())));
                    metaView.setTextColor(TEXT_SECONDARY);
                }
                metaView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
                LinearLayout.LayoutParams mvLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mvLp.topMargin = dp(activity, 2);
                metaView.setLayoutParams(mvLp);
                infoCol.addView(metaView);
                row.addView(infoCol);

                // 4. Clean Apple-style Action Icon Buttons (Zero crowded text)
                if (isDir) {
                    View openFolderBtn = createIconButton(activity, R.drawable.ic_action_open,
                            tr(activity, "Open Folder", "فتح المجلد", "کردنەوەی بوخچە"),
                            true, false, v -> {
                                dialog.dismiss();
                                showVaultFileManagerModal(activity, vaultRoot, f, cb);
                            });
                    LinearLayout.LayoutParams ofLp = (LinearLayout.LayoutParams) openFolderBtn.getLayoutParams();
                    ofLp.setMarginEnd(dp(activity, 8));
                    row.addView(openFolderBtn);

                    View renameFolderBtn = createIconButton(activity, R.drawable.ic_action_edit,
                            tr(activity, "Rename Folder", "إعادة تسمية المجلد", "گۆڕینی ناوی بوخچە"),
                            false, false, v -> showRenameItemDialog(activity, f, () -> {
                                dialog.dismiss();
                                showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                            }));
                    LinearLayout.LayoutParams rnfLp = (LinearLayout.LayoutParams) renameFolderBtn.getLayoutParams();
                    rnfLp.setMarginEnd(dp(activity, 8));
                    row.addView(renameFolderBtn);

                    View moveCopyFolderBtn = createIconButton(activity, R.drawable.ic_action_move,
                            tr(activity, "Move or Copy Folder", "نقل أو نسخ المجلد", "گواستنەوە یان کۆپیکردنی بوخچە"),
                            false, false, v -> showCopyMoveDestinationDialog(activity, dialog, vaultRoot, currentDir,
                                    Collections.singletonList(f), null, cb));
                    LinearLayout.LayoutParams mcfLp = (LinearLayout.LayoutParams) moveCopyFolderBtn.getLayoutParams();
                    mcfLp.setMarginEnd(dp(activity, 8));
                    row.addView(moveCopyFolderBtn);
                } else {
                    View openBtn = createIconButton(activity, R.drawable.ic_action_open,
                            tr(activity, "Open", "فتح", "کردنەوە"),
                            true, false, v -> {
                                dialog.dismiss();
                                cb.onOpenFile(f);
                            });
                    LinearLayout.LayoutParams olp = (LinearLayout.LayoutParams) openBtn.getLayoutParams();
                    olp.setMarginEnd(dp(activity, 8));
                    row.addView(openBtn);

                    View renameFileBtn = createIconButton(activity, R.drawable.ic_action_edit,
                            tr(activity, "Rename File", "إعادة تسمية الملف", "گۆڕینی ناوی پەڕگە"),
                            false, false, v -> showRenameItemDialog(activity, f, () -> {
                                dialog.dismiss();
                                showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                            }));
                    LinearLayout.LayoutParams rnfLp = (LinearLayout.LayoutParams) renameFileBtn.getLayoutParams();
                    rnfLp.setMarginEnd(dp(activity, 8));
                    row.addView(renameFileBtn);

                    View moveCopyBtn = createIconButton(activity, R.drawable.ic_action_move,
                            tr(activity, "Move or Copy", "نقل أو نسخ", "گواستنەوە یان کۆپیکردن"),
                            false, false, v -> showCopyMoveDestinationDialog(activity, dialog, vaultRoot, currentDir,
                                    Collections.singletonList(f), null, cb));
                    LinearLayout.LayoutParams mcLp = (LinearLayout.LayoutParams) moveCopyBtn.getLayoutParams();
                    mcLp.setMarginEnd(dp(activity, 8));
                    row.addView(moveCopyBtn);

                    View shareBtn = createIconButton(activity, R.drawable.ic_action_share,
                            tr(activity, "Share", "مشاركة", "هاوبەشکردن"),
                            false, false, v -> {
                                dialog.dismiss();
                                cb.onShareFile(f);
                            });
                    LinearLayout.LayoutParams shLp = (LinearLayout.LayoutParams) shareBtn.getLayoutParams();
                    shLp.setMarginEnd(dp(activity, 8));
                    row.addView(shareBtn);
                }

                View delBtn = createIconButton(activity, R.drawable.ic_action_delete,
                        tr(activity, "Delete", "حذف", "سڕینەوە"),
                        false, true, v -> {
                            deleteItemRecursive(f);
                            Toast.makeText(activity, tr(activity,
                                    "Deleted: " + f.getName(),
                                    "تم الحذف: " + f.getName(),
                                    "سڕایەوە: " + f.getName()), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                        });
                row.addView(delBtn);

                checkIndicators.add(checkBtn);
                rowCards.add(row);
                listBox.addView(row);
            }

            scroll.addView(listBox);
            root.addView(scroll);
        } else {
            LinearLayout emptyCard = new LinearLayout(activity);
            emptyCard.setOrientation(LinearLayout.VERTICAL);
            emptyCard.setGravity(Gravity.CENTER);
            int p = dp(activity, 28);
            emptyCard.setPadding(p, p, p, p);
            emptyCard.setBackground(createCardDrawable(activity,
                    SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams ecLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ecLp.topMargin = dp(activity, 12);
            emptyCard.setLayoutParams(ecLp);

            ImageView emptyIcon = new ImageView(activity);
            emptyIcon.setImageResource(R.drawable.ic_folder);
            emptyIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
            LinearLayout.LayoutParams eiLp = new LinearLayout.LayoutParams(dp(activity, 40), dp(activity, 40));
            eiLp.bottomMargin = dp(activity, 10);
            emptyCard.addView(emptyIcon, eiLp);

            TextView emptyText = new TextView(activity);
            emptyText.setText(isAtRoot
                    ? tr(activity,
                            "This Vault is currently empty.\n\nPrinted PDFs and files imported from your phone appear here automatically. Use the top bar icons to import a file or create a new folder.",
                            "الخزنة فارغة حالياً.\n\nملفات PDF المطبوعة والملفات المستوردة من الهاتف تظهر هنا تلقائياً. يمكنك استخدام الأيقونات في الأعلى لاستيراد ملف أو إنشاء مجلد جديد.",
                            "قاسەکە لە ئێستادا بەتاڵە.\n\nپەڕگەکانی PDF و پەڕگە هاوردەکراوەکان لێرە دەردەکەون. دەتوانیت لە شریتی سەرەوە پەڕگە هاوردە بکەیت یان بوخچەی نوێ دروست بکەیت.")
                    : tr(activity,
                            "This folder is empty.\n\nUse the top bar icons to import a file into this folder or create a subfolder.",
                            "هذا المجلد فارغ.\n\nاستخدم الأيقونات في الأعلى لاستيراد ملف داخل هذا المجلد أو إنشاء مجلد فرعي.",
                            "ئەم بوخچەیە بەتاڵە.\n\nلە شریتی سەرەوە پەڕگە هاوردە بکە یان بوخچەی لاوەکی دروست بکە."));
            emptyText.setTextColor(TEXT_SECONDARY);
            emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            emptyText.setGravity(Gravity.CENTER);
            emptyCard.addView(emptyText);
            root.addView(emptyCard);
        }

        dialog.setContentView(root);
        dialog.show();
    }

    private static void showCreateFolderDialog(MainActivity activity, File parentDir, Runnable onCreated) {
        Dialog dlg = createModalDialog(activity, isCompactWidth(activity) ? 0.80f : 0.60f, 360, true);
        if (dlg.getWindow() != null) {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_folder_add);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 19), dp(activity, 19));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity, "Create New Folder", "إنشاء مجلد جديد", "دروستکردنی بوخچەی نوێ"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Cancel", "إلغاء", "پاشگەزبوونەوە"),
                false, false, v -> dlg.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        EditText input = new EditText(activity);
        input.setHint(tr(activity, "Folder name...", "اسم المجلد...", "ناوی بوخچە..."));
        input.setHintTextColor(TEXT_TERTIARY);
        input.setTextColor(TEXT_PRIMARY);
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        input.setSingleLine(true);
        input.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        input.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 9, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams inLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inLp.topMargin = dp(activity, 10);
        inLp.bottomMargin = dp(activity, 10);
        root.addView(input, inLp);

        // Top-bar Create (✓) icon placed before the Cancel icon (replaces old bottom footer button)
        View createBtn = createIconButton(activity, R.drawable.ic_action_check,
                tr(activity, "Create Folder", "إنشاء المجلد", "دروستکردن"),
                true, false, v -> {
                    String raw = input.getText() != null ? input.getText().toString().trim() : "";
                    String safe = raw.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}\\u200E\\u200F\\u202A-\\u202E\\u2066-\\u2069]", "_").trim();
                    if (safe.length() > 120) {
                        safe = safe.substring(0, 120).trim();
                    }
                    if (safe.isEmpty() || ".".equals(safe) || "..".equals(safe) || safe.startsWith(".")) {
                        input.setError(tr(activity, "Enter a valid folder name", "أدخل اسماً صحيحاً للمجلد", "ناوێکی دروست بنووسە"));
                        return;
                    }
                    File vaultRoot = new File(activity.getFilesDir(), "prints");
                    File newDir = new File(parentDir, safe);
                    if (!isSubPathOrEqual(vaultRoot, newDir)) {
                        input.setError(tr(activity, "Enter a valid folder name", "أدخل اسماً صحيحاً للمجلد", "ناوێکی دروست بنووسە"));
                        return;
                    }
                    if (newDir.exists() || newDir.mkdirs()) {
                        newDir.setReadable(true, true);
                        newDir.setWritable(true, true);
                        newDir.setExecutable(true, true);
                        dlg.dismiss();
                        if (onCreated != null) onCreated.run();
                    } else {
                        Toast.makeText(activity, tr(activity, "Could not create folder", "تعذر إنشاء المجلد", "نەتوانرا بوخچەکە دروست بکرێت"), Toast.LENGTH_SHORT).show();
                    }
                });
        LinearLayout.LayoutParams cbLp = (LinearLayout.LayoutParams) createBtn.getLayoutParams();
        cbLp.setMarginEnd(dp(activity, 6));
        header.addView(createBtn, header.getChildCount() - 1);

        dlg.setContentView(root);
        dlg.show();
    }

    private static void showRenameItemDialog(MainActivity activity, File targetItem, Runnable onRenamed) {
        if (targetItem == null || !targetItem.exists()) return;
        final boolean isDir = targetItem.isDirectory();
        final String currentName = targetItem.getName();
        final int dotIdx = (!isDir && currentName.lastIndexOf('.') > 0) ? currentName.lastIndexOf('.') : -1;
        final String origExt = dotIdx > 0 ? currentName.substring(dotIdx) : "";

        Dialog dlg = createModalDialog(activity, isCompactWidth(activity) ? 0.80f : 0.60f, 380, true);
        if (dlg.getWindow() != null) {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_action_edit);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 19), dp(activity, 19));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(icon, icLp);

        TextView title = new TextView(activity);
        title.setText(isDir
                ? tr(activity, "Rename Folder", "إعادة تسمية المجلد", "گۆڕینی ناوی بوخچە")
                : tr(activity, "Rename File", "إعادة تسمية الملف", "گۆڕینی ناوی پەڕگە"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Cancel", "إلغاء", "پاشگەزبوونەوە"),
                false, false, v -> dlg.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        EditText input = new EditText(activity);
        input.setText(currentName);
        input.setHint(isDir
                ? tr(activity, "New folder name...", "اسم المجلد الجديد...", "ناوی نوێی بوخچە...")
                : tr(activity, "New file name...", "اسم الملف الجديد...", "ناوی نوێی پەڕگە..."));
        input.setHintTextColor(TEXT_TERTIARY);
        input.setTextColor(TEXT_PRIMARY);
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        input.setSingleLine(true);
        input.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        input.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 9, SEPARATOR_HAIRLINE));
        if (dotIdx > 0) {
            input.setSelection(0, dotIdx);
        } else {
            input.selectAll();
        }
        LinearLayout.LayoutParams inLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inLp.topMargin = dp(activity, 10);
        inLp.bottomMargin = dp(activity, 10);
        root.addView(input, inLp);

        View saveBtn = createIconButton(activity, R.drawable.ic_action_check,
                tr(activity, "Save Name", "حفظ الاسم", "پاشەکەوتکردنی ناو"),
                true, false, v -> {
                    String raw = input.getText() != null ? input.getText().toString().trim() : "";
                    String safe = raw.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}\\u200E\\u200F\\u202A-\\u202E\\u2066-\\u2069]", "_").trim();
                    if (safe.length() > 120) {
                        safe = safe.substring(0, 120).trim();
                    }
                    if (safe.isEmpty() || ".".equals(safe) || "..".equals(safe) || safe.startsWith(".") || safe.endsWith(".apk")) {
                        input.setError(tr(activity, "Please enter a valid name", "يرجى إدخال اسم صحيح", "تکایە ناوێکی دروست بنووسە"));
                        return;
                    }
                    if (!isDir && !origExt.isEmpty() && !safe.contains(".")) {
                        safe = safe + origExt;
                    }
                    if (safe.equals(currentName)) {
                        dlg.dismiss();
                        return;
                    }
                    File vaultRoot = new File(activity.getFilesDir(), "prints");
                    File parentDir = targetItem.getParentFile();
                    File destFile = new File(parentDir, safe);
                    if (!isSubPathOrEqual(vaultRoot, destFile)) {
                        input.setError(tr(activity, "Please enter a valid name", "يرجى إدخال اسم صحيح", "تکایە ناوێکی دروست بنووسە"));
                        return;
                    }
                    if (destFile.exists()) {
                        input.setError(tr(activity,
                                "An item with this name already exists in this folder",
                                "يوجد ملف أو مجلد بنفس الاسم في هذا المجلد",
                                "پەڕگە یان بوخچەیەک بەم ناوە هەیە"));
                        return;
                    }
                    if (targetItem.renameTo(destFile)) {
                        destFile.setReadable(true, true);
                        destFile.setWritable(true, true);
                        Toast.makeText(activity, tr(activity,
                                "Renamed to: " + safe,
                                "تم تغيير الاسم إلى: " + safe,
                                "ناوەکە گۆڕدرا بۆ: " + safe), Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                        if (onRenamed != null) onRenamed.run();
                    } else {
                        Toast.makeText(activity, tr(activity,
                                "Could not rename item",
                                "تعذر إعادة التسمية",
                                "نەتوانرا ناوەکە بگۆڕدرێت"), Toast.LENGTH_SHORT).show();
                    }
                });
        LinearLayout.LayoutParams sbLp = (LinearLayout.LayoutParams) saveBtn.getLayoutParams();
        sbLp.setMarginEnd(dp(activity, 6));
        header.addView(saveBtn, header.getChildCount() - 1);

        dlg.setContentView(root);
        dlg.show();
    }

    private static void showCopyMoveDestinationDialog(
            MainActivity activity,
            Dialog parentVaultDialog,
            File vaultRoot,
            File currentDir,
            List<File> sourceItems,
            Boolean forceCopyMode,
            MenuActionCallback cb) {
        if (sourceItems == null || sourceItems.isEmpty()) return;

        Dialog destDlg = createModalDialog(activity, isCompactWidth(activity) ? 0.84f : 0.68f, 450, true);
        LinearLayout root = createDialogContainer(activity);

        // Header
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 10));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(forceCopyMode != null && forceCopyMode
                ? R.drawable.ic_action_copy : R.drawable.ic_action_move);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        icLp.setMarginEnd(dp(activity, 10));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        if (forceCopyMode == null) {
            title.setText(tr(activity,
                    "Move or Copy to Folder",
                    "نقل أو نسخ إلى مجلد",
                    "گواستنەوە یان کۆپیکردن بۆ بوخچە"));
        } else if (forceCopyMode) {
            title.setText(tr(activity,
                    "Copy " + sourceItems.size() + " Item(s) to Folder",
                    "نسخ " + sourceItems.size() + " عنصر إلى مجلد",
                    "کۆپیکردنی " + sourceItems.size() + " دانە بۆ بوخچە"));
        } else {
            title.setText(tr(activity,
                    "Move " + sourceItems.size() + " Item(s) to Folder",
                    "نقل " + sourceItems.size() + " عنصر إلى مجلد",
                    "گواستنەوەی " + sourceItems.size() + " دانە بۆ بوخچە"));
        }
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subTitle = new TextView(activity);
        if (sourceItems.size() == 1) {
            subTitle.setText(sourceItems.get(0).getName());
        } else {
            subTitle.setText(tr(activity,
                    "Choose a destination folder inside the Vault",
                    "اختر المجلد الوجهة داخل الخزنة",
                    "بوخچەی مەبەست لە ناو قاسەکە هەڵبژێرە"));
        }
        subTitle.setTextColor(TEXT_SECONDARY);
        subTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        subTitle.setSingleLine(true);
        subTitle.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
        titleCol.addView(subTitle);
        header.addView(titleCol);

        // Quick New Folder button inside destination dialog
        View newFolderBtn = createIconButton(activity, R.drawable.ic_folder_add,
                tr(activity, "New Folder", "مجلد جديد", "بوخچەی نوێ"),
                false, false, v -> showCreateFolderDialog(activity, currentDir, () -> {
                    destDlg.dismiss();
                    showCopyMoveDestinationDialog(activity, parentVaultDialog, vaultRoot, currentDir, sourceItems, forceCopyMode, cb);
                }));
        LinearLayout.LayoutParams nfLp = (LinearLayout.LayoutParams) newFolderBtn.getLayoutParams();
        nfLp.setMarginEnd(dp(activity, 8));
        header.addView(newFolderBtn);

        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Cancel", "إلغاء", "پاشگەزبوونەوە"),
                false, false, v -> destDlg.dismiss()));

        root.addView(header);
        root.addView(createHairlineDivider(activity));

        // Collect all valid destination folders in the Vault
        List<File> allDirs = new ArrayList<>();
        allDirs.add(vaultRoot);
        collectVaultDirectoriesRecursive(vaultRoot, allDirs);

        List<File> validTargets = new ArrayList<>();
        for (File candidateDir : allDirs) {
            // Skip currentDir itself (moving/copying to the same folder is redundant)
            if (isSameDirectory(candidateDir, currentDir)) continue;
            // Skip if candidateDir is inside any source directory being moved/copied
            boolean insideSource = false;
            for (File src : sourceItems) {
                if (src.isDirectory() && isSubPathOrEqual(src, candidateDir)) {
                    insideSource = true;
                    break;
                }
            }
            if (!insideSource) {
                validTargets.add(candidateDir);
            }
        }

        if (validTargets.isEmpty()) {
            LinearLayout emptyBox = new LinearLayout(activity);
            emptyBox.setOrientation(LinearLayout.VERTICAL);
            emptyBox.setGravity(Gravity.CENTER);
            int p = dp(activity, 24);
            emptyBox.setPadding(p, p, p, p);
            emptyBox.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams ebLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ebLp.topMargin = dp(activity, 12);
            emptyBox.setLayoutParams(ebLp);

            TextView emptyMsg = new TextView(activity);
            emptyMsg.setText(tr(activity,
                    "No other folders exist in the Vault yet.\nTap the New Folder (+) icon above to create a destination folder.",
                    "لا توجد مجلدات أخرى في الخزنة حالياً.\nاضغط على أيقونة إنشاء مجلد (+) في الأعلى لإنشاء مجلد وجهة.",
                    "هیچ بوخچەیەکی تر لە قاسەکەدا نییە.\nرتە لە ئایکۆنی بوخچەی نوێ (+) لە سەرەوە بکە بۆ دروستکردنی بوخچە."));
            emptyMsg.setTextColor(TEXT_SECONDARY);
            emptyMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            emptyMsg.setGravity(Gravity.CENTER);
            emptyBox.addView(emptyMsg);
            root.addView(emptyBox);
        } else {
            ScrollView scroll = new ScrollView(activity);
            scroll.setFillViewport(true);
            LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            scrollLp.topMargin = dp(activity, 10);
            scroll.setLayoutParams(scrollLp);

            LinearLayout listBox = new LinearLayout(activity);
            listBox.setOrientation(LinearLayout.VERTICAL);

            for (File targetDir : validTargets) {
                boolean isRootTarget = isSameDirectory(targetDir, vaultRoot);
                String displayPath = isRootTarget
                        ? tr(activity, "/Vault (Main Folder)", "/Vault (المجلد الرئيسي)", "/Vault (بوخچەی سەرەکی)")
                        : "/Vault/" + getRelativeVaultPath(vaultRoot, targetDir);

                LinearLayout row = new LinearLayout(activity);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(dp(activity, 14), dp(activity, 11), dp(activity, 14), dp(activity, 11));
                row.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 12, SEPARATOR_HAIRLINE));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.bottomMargin = dp(activity, 8);
                row.setLayoutParams(rlp);

                ImageView fIcon = new ImageView(activity);
                fIcon.setImageResource(R.drawable.ic_folder);
                fIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
                LinearLayout.LayoutParams fiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
                fiLp.setMarginEnd(dp(activity, 12));
                row.addView(fIcon, fiLp);

                LinearLayout textCol = new LinearLayout(activity);
                textCol.setOrientation(LinearLayout.VERTICAL);
                textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                TextView fName = new TextView(activity);
                fName.setText(isRootTarget
                        ? tr(activity, "Vault (Main Folder)", "الخزنة (المجلد الرئيسي)", "قاسە (بوخچەی سەرەکی)")
                        : targetDir.getName());
                fName.setTextColor(TEXT_PRIMARY);
                fName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
                fName.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
                fName.setSingleLine(true);
                fName.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
                textCol.addView(fName);

                TextView fPath = new TextView(activity);
                fPath.setText(displayPath);
                fPath.setTextColor(TEXT_SECONDARY);
                fPath.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
                fPath.setSingleLine(true);
                fPath.setEllipsize(android.text.TextUtils.TruncateAt.START);
                textCol.addView(fPath);
                row.addView(textCol);

                if (forceCopyMode == null || forceCopyMode) {
                    View copyHereBtn = createIconButton(activity, R.drawable.ic_action_copy,
                            tr(activity, "Copy Here", "نسخ هنا", "کۆپیکردن بۆ ئێرە"),
                            forceCopyMode != null, false, v -> {
                                int ok = 0;
                                for (File src : sourceItems) {
                                    if (copyItemToDirectory(src, targetDir)) ok++;
                                }
                                Toast.makeText(activity, tr(activity,
                                        "Copied " + ok + " item(s) to " + (isRootTarget ? "Vault" : targetDir.getName()),
                                        "تم نسخ " + ok + " عنصر إلى " + (isRootTarget ? "الخزنة" : targetDir.getName()),
                                        ok + " دانە کۆپی کرا بۆ " + (isRootTarget ? "قاسە" : targetDir.getName())),
                                        Toast.LENGTH_SHORT).show();
                                destDlg.dismiss();
                                if (parentVaultDialog != null && parentVaultDialog.isShowing()) {
                                    parentVaultDialog.dismiss();
                                }
                                showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                            });
                    LinearLayout.LayoutParams chLp = (LinearLayout.LayoutParams) copyHereBtn.getLayoutParams();
                    if (forceCopyMode == null) {
                        chLp.setMarginEnd(dp(activity, 8));
                    }
                    row.addView(copyHereBtn);
                }

                if (forceCopyMode == null || !forceCopyMode) {
                    View moveHereBtn = createIconButton(activity, R.drawable.ic_action_move,
                            tr(activity, "Move Here", "نقل هنا", "گواستنەوە بۆ ئێرە"),
                            true, false, v -> {
                                int ok = 0;
                                for (File src : sourceItems) {
                                    if (moveItemToDirectory(src, targetDir)) ok++;
                                }
                                Toast.makeText(activity, tr(activity,
                                        "Moved " + ok + " item(s) to " + (isRootTarget ? "Vault" : targetDir.getName()),
                                        "تم نقل " + ok + " عنصر إلى " + (isRootTarget ? "الخزنة" : targetDir.getName()),
                                        ok + " دانە گواسترایەوە بۆ " + (isRootTarget ? "قاسە" : targetDir.getName())),
                                        Toast.LENGTH_SHORT).show();
                                destDlg.dismiss();
                                if (parentVaultDialog != null && parentVaultDialog.isShowing()) {
                                    parentVaultDialog.dismiss();
                                }
                                showVaultFileManagerModal(activity, vaultRoot, currentDir, cb);
                            });
                    row.addView(moveHereBtn);
                }

                listBox.addView(row);
            }

            scroll.addView(listBox);
            root.addView(scroll);
        }

        destDlg.setContentView(root);
        destDlg.show();
    }

    private static void collectVaultDirectoriesRecursive(File dir, List<File> outDirs) {
        if (dir == null || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        List<File> subDirs = java.util.Arrays.stream(children)
                .filter(c -> c.isDirectory() && !c.getName().startsWith("."))
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(java.util.stream.Collectors.toList());
        for (File sub : subDirs) {
            outDirs.add(sub);
            collectVaultDirectoriesRecursive(sub, outDirs);
        }
    }

    private static boolean isSameDirectory(File a, File b) {
        if (a == null || b == null) return false;
        try {
            return a.getCanonicalPath().equals(b.getCanonicalPath());
        } catch (Exception e) {
            return a.getAbsolutePath().equals(b.getAbsolutePath());
        }
    }

    private static File resolveNonCollidingTarget(File destDir, String originalName) {
        File target = new File(destDir, originalName);
        if (!target.exists()) return target;
        String base = originalName;
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            base = originalName.substring(0, dot);
            ext = originalName.substring(dot);
        }
        for (int i = 1; i < 1000; i++) {
            File cand = new File(destDir, base + "_" + i + ext);
            if (!cand.exists()) return cand;
        }
        return new File(destDir, base + "_" + System.currentTimeMillis() + ext);
    }

    private static boolean copyItemToDirectory(File src, File destDir) {
        if (src == null || !src.exists() || destDir == null) return false;
        if (!destDir.exists() && !destDir.mkdirs()) return false;
        File target = resolveNonCollidingTarget(destDir, src.getName());
        return copyRecursiveInternal(src, target);
    }

    private static boolean copyRecursiveInternal(File src, File dst) {
        try {
            if (src.isDirectory()) {
                if (!dst.exists() && !dst.mkdirs()) return false;
                File[] children = src.listFiles();
                if (children != null) {
                    for (File c : children) {
                        if (!copyRecursiveInternal(c, new File(dst, c.getName()))) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                try (FileInputStream in = new FileInputStream(src);
                     FileOutputStream out = new FileOutputStream(dst)) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean moveItemToDirectory(File src, File destDir) {
        if (src == null || !src.exists() || destDir == null) return false;
        if (!destDir.exists() && !destDir.mkdirs()) return false;
        File target = resolveNonCollidingTarget(destDir, src.getName());
        if (src.renameTo(target)) {
            return true;
        }
        if (copyRecursiveInternal(src, target)) {
            deleteItemRecursive(src);
            return true;
        }
        return false;
    }

    private static boolean isSubPathOrEqual(File rootDir, File target) {
        try {
            String r = rootDir.getCanonicalPath();
            String t = target.getCanonicalPath();
            return t.equals(r) || t.startsWith(r + File.separator);
        } catch (Exception e) {
            String r = rootDir.getAbsolutePath();
            String t = target.getAbsolutePath();
            return t.equals(r) || t.startsWith(r + File.separator);
        }
    }

    private static String getRelativeVaultPath(File rootDir, File target) {
        try {
            String r = rootDir.getCanonicalPath();
            String t = target.getCanonicalPath();
            if (t.startsWith(r + File.separator)) {
                return t.substring(r.length() + 1);
            }
        } catch (Exception ignored) { /* Best-effort UI operation */ }
        return target.getName();
    }

    private static int countVisibleChildren(File dir) {
        if (dir == null || !dir.isDirectory()) return 0;
        File[] list = dir.listFiles();
        if (list == null) return 0;
        int c = 0;
        for (File f : list) {
            if (!f.getName().startsWith(".") && !f.getName().endsWith(".apk")) {
                c++;
            }
        }
        return c;
    }

    private static void deleteItemRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    deleteItemRecursive(c);
                }
            }
        }
        if (!f.delete()) {
            f.deleteOnExit();
        }
    }

    public static void showEncryptedVaultInfoModal(MainActivity activity) {
        Dialog infoDialog = createModalDialog(activity, 0.76f, 470, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        TextView iconCircle = new TextView(activity);
        iconCircle.setText("!");
        iconCircle.setGravity(Gravity.CENTER);
        iconCircle.setTextColor(Color.WHITE);
        iconCircle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        iconCircle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        iconCircle.setBackground(createCardDrawable(activity, SYSTEM_BLUE, 12, 0));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24));
        icLp.setMarginEnd(dp(activity, 8));
        header.addView(iconCircle, icLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "About the Private Document Vault",
                "حول خزنة المستندات الخاصة",
                "دەربارەی قاسەی تایبەتی بەڵگەنامەکان"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);
        header.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Got It", "حسناً، فهمت", KU_DONE),
                false, false, v -> infoDialog.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scLp.topMargin = dp(activity, 8);
        scroll.setLayoutParams(scLp);

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 12);
        card.setPadding(pad, pad, pad, pad);
        card.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 12, Color.parseColor("#550A84FF")));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardLp);

        TextView msg = new TextView(activity);
        msg.setText(tr(activity,
                "To keep your customs declarations and business records private, every PDF you print and every document you import is stored inside a dedicated Vault folder built into this app rather than in your phone's public Downloads folder.\n\n"
                        + "• Protected from Other Apps: Files saved in the Vault are kept inside this app's private storage space, so other apps on your phone cannot view or scan your customs paperwork.\n\n"
                        + "• Ready for ASYCUDA Uploads & Printing: Documents placed in the Vault appear directly inside ASYCUDA World's file window whenever you need to attach supporting paperwork to a declaration or save a printed form.\n\n"
                        + "• Your Personal Files Stay Separate: The app does not ask for full access to your phone's personal photos or storage. Only the specific files you choose to import are copied into the Vault, and you can open, rename, organize, or share them anytime from this screen.",
                "للحفاظ على خصوصية تصاريحك الكمركية ومستندات عملك، يتم حفظ جميع ملفات PDF التي تطبعها والمستندات التي تستوردها داخل خزنة خاصة مدمجة في هذا التطبيق بدلاً من تركها في مجلد التنزيلات العام على هاتفك.\n\n"
                        + "• محمية من التطبيقات الأخرى: تُحفظ ملفات الخزنة داخل المساحة الخاصة بالتطبيق، مما يمنع أي تطبيقات أخرى على هاتفك من الاطلاع على معاملاتك أو فحص مستنداتك الكمركية.\n\n"
                        + "• جاهزة للرفع والطباعة داخل أسيكودا: تظهر الملفات الموجودة في الخزنة مباشرة داخل نافذة اختيار الملفات في نظام أسيكودا العالمي كلما احتجت إلى إرفاق مستندات مع التصريح أو حفظ استمارة مطبوعة.\n\n"
                        + "• خصوصية ملفات هاتفك الشخصية: لا يطلب التطبيق صلاحية الوصول الشامل إلى صورك أو ملفات هاتفك الشخصية، بل يتم فقط نسخ الملفات التي تختار استيرادها بنفسك إلى الخزنة، ويمكنك فتحها أو إعادة تسميتها أو تنظيمها في مجلدات أو مشاركتها في أي وقت من هذه النافذة.",
                "بۆ پاراستنی تایبەتمەندی مامەڵە گومرگییەکانت و بەڵگەنامەکانی کارەکەت، هەموو ئەو پەڕگە PDFانەی چاپیان دەکەیت و ئەو بەڵگەنامانەی هاوردەیان دەکەیت لەناو قاسەیەکی تایبەتی ناو ئەم بەرنامەیەدا هەڵدەگیرێن نەک لەناو بوخچەی گشتی مۆبایلەکەت.\n\n"
                        + "• پارێزراو لە بەرنامەکانی تر: پەڕگەکانی ناو قاسەکە لە شوێنی تایبەتی بەرنامەکەدا هەڵدەگیرێن، بۆیە هیچ بەرنامەیەکی تر لەسەر مۆبایلەکەت ناتوانێت بەڵگەنامە گومرگییەکانت ببینێت.\n\n"
                        + "• ئامادەیە بۆ هاوپێچکردن و چاپکردن: هەر بەڵگەنامەیەک لەناو قاسەکەدا بێت ڕاستەوخۆ لە پەنجەرەی هەڵبژاردنی پەڕگەی ئەسیکۆدا دەردەکەوێت کاتێک دەتەوێت بەڵگەنامە هاوپێچ بکەیت.\n\n"
                        + "• جیاکردنەوەی پەڕگە کەسییەکانت: بەرنامەکە داوای مۆڵەتی گەیشتن بە وێنە و پەڕگە تایبەتییەکانی مۆبایلەکەت ناکات، تەنها ئەو پەڕگانەی خۆت هەڵیاندەبژێریت دەخرێنە ناو قاسەکە و دەتوانیت لە هەر کاتێکدا بیانکەیتەوە، ناویان بگۆڕیت، یان هاوبەشیان پێبکەیت."));
        msg.setTextColor(TEXT_PRIMARY);
        msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.8f);
        card.addView(msg);
        scroll.addView(card);
        root.addView(scroll);

        infoDialog.setContentView(root);
        infoDialog.show();
    }

    public static void showAboutAndCreditsModal(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.85f, 590, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout topBar = new LinearLayout(activity);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView infoIcon = new ImageView(activity);
        infoIcon.setImageResource(R.drawable.ic_menu_info);
        infoIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams iiLp = new LinearLayout.LayoutParams(dp(activity, 19), dp(activity, 19));
        iiLp.setMarginEnd(dp(activity, 8));
        topBar.addView(infoIcon, iiLp);

        TextView topTitle = new TextView(activity);
        topTitle.setText(tr(activity,
                "About & Open-Source Licences",
                "حول التطبيق والتراخيص مفتوحة المصدر",
                "دەربارەی بەرنامە و مۆڵەتە سەرچاوە کراوەکان"));
        topTitle.setTextColor(TEXT_PRIMARY);
        topTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        topTitle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        topTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(topTitle);

        topBar.addView(createIconButton(activity, R.drawable.ic_nav_back,
                tr(activity, EN_BACK_CONTROL_CENTER, AR_BACK_CONTROL_CENTER, KU_BACK_CONTROL_CENTER),
                false, false, v -> {
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                }));
        dialog.setOnCancelListener(d -> showControlCenter(activity, cb));
        root.addView(topBar);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 8);
        scroll.setLayoutParams(scrollLp);

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        // 1. App Card
        LinearLayout heroCard = new LinearLayout(activity);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 12);
        heroCard.setPadding(pad, pad, pad, pad);
        heroCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 12, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(activity, 8);
        heroCard.setLayoutParams(cardLp);

        LinearLayout heroTop = new LinearLayout(activity);
        heroTop.setOrientation(LinearLayout.HORIZONTAL);
        heroTop.setGravity(Gravity.CENTER_VERTICAL);

        ImageView appIcon = new ImageView(activity);
        appIcon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 40), dp(activity, 40));
        icLp.setMarginEnd(dp(activity, 12));
        heroTop.addView(appIcon, icLp);

        LinearLayout heroTextCol = new LinearLayout(activity);
        heroTextCol.setOrientation(LinearLayout.VERTICAL);

        TextView appTitle = new TextView(activity);
        appTitle.setText(tr(activity,
                "ASYCUDA World — Republic of Iraq",
                "نظام أسيكودا العالمي — جمهورية العراق",
                "سیستەمی ئەسیکۆدای جیهانی — کۆماری عێراق"));
        appTitle.setTextColor(TEXT_PRIMARY);
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        appTitle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        heroTextCol.addView(appTitle);

        TextView verPill = new TextView(activity);
        verPill.setText("Version " + getAppVersionLabel(activity) + " (Major Release)  •  Android Desktop Environment");
        verPill.setTextColor(SYSTEM_BLUE);
        verPill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        verPill.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.NORMAL));
        heroTextCol.addView(verPill);
        heroTop.addView(heroTextCol);
        heroCard.addView(heroTop);

        TextView appDesc = new TextView(activity);
        appDesc.setText(tr(activity,
                "Runs the full Iraqi Customs ASYCUDA World desktop system on Android phones and tablets, complete with built-in PDF printing, a private document vault, and automatic updates from the official customs server.",
                "يتيح تشغيل النظام الكامل لأسيكودا العالمي التابع للهيئة العامة للكمارك العراقية على الهواتف والأجهزة اللوحية بنظام أندرويد، مع دعم مدمج لطباعة ملفات PDF وخزنة خاصة للمستندات وتحديث تلقائي من خادم الكمارك الرسمي.",
                "سیستەمی تەواوی ئەسیکۆدای جیهانی سەر بە گومرگی عێراق لەسەر مۆبایل و تابلێتی ئەندرۆید کارپێدەکات، لەگەڵ چاپکردنی ڕاستەوخۆی PDF، قاسەی تایبەتی بەڵگەنامەکان و نوێکردنەوەی خۆکار لە ڕاژەی فەرمی گومرگەوە."));
        appDesc.setTextColor(TEXT_SECONDARY);
        appDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descLp.topMargin = dp(activity, 10);
        heroCard.addView(appDesc, descLp);

        TextView whatsNewBtn = createCupertinoButton(activity,
                tr(activity, "What's New in Major Release " + getAppVersionLabel(activity) + " (Changelog)",
                        "ما الجديد في الإصدار الرئيسي " + getAppVersionLabel(activity) + " (سجل التحديثات)",
                        "چی نوێ هەیە لە وەشانی سەرەکی " + getAppVersionLabel(activity)),
                false, false, v -> {
                    dialog.dismiss();
                    showWhatsNewModal(activity, () -> showAboutAndCreditsModal(activity, cb));
                });
        LinearLayout.LayoutParams wnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wnLp.topMargin = dp(activity, 10);
        whatsNewBtn.setLayoutParams(wnLp);
        heroCard.addView(whatsNewBtn);
        content.addView(heroCard);

        // 1.5 Security & Privacy Card with Live Visual Badges (SonarQube Cloud & Snyk)
        LinearLayout secCard = new LinearLayout(activity);
        secCard.setOrientation(LinearLayout.VERTICAL);
        secCard.setPadding(pad, pad, pad, pad);
        secCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, Color.parseColor("#4430D158")));
        secCard.setLayoutParams(cardLp);

        secCard.addView(createSectionHeader(activity,
                tr(activity, "SECURITY, PRIVACY & CODE QUALITY",
                        "الأمان والخصوصية وجودة البرمجة",
                        "ئاسایش و تایبەتمەندی و کوالێتی کۆد")));

        TextView secBody = new TextView(activity);
        secBody.setText(tr(activity,
                "This app is 100% open-source so anyone can inspect how it works. Your ASYCUDA username, password, and customs declarations connect directly to the Ministry of Finance server (asy.mof.gov.iq) — this app never sees, saves, or collects them. Release " + getAppVersionLabel(activity) + " is a Major Security & Architecture Release; all earlier versions (< v4.1.0) are now unsafe, superseded, and not recommended.",
                "هذا التطبيق مفتوح المصدر بالكامل ليتمكن أي شخص أو مبرمج من فحصه والتأكد من أمانه. اسم المستخدم وكلمة المرور والتصاريح الكمركية الخاصة بك تتصل مباشرة بخادم وزارة المالية (asy.mof.gov.iq)، ولا يقوم التطبيق بقراءتها أو حفظها أو جمعها نهائياً. الإصدار " + getAppVersionLabel(activity) + " هو إصدار رئيسي للأمان والبنية البرمجية، وجميع الإصدارات السابقة (< v4.1.0) أصبحت غير آمنة وغير موصى بها.",
                "ئەم بەرنامەیە ١٠٠٪ سەرچاوە کراوەیە بۆ ئەوەی هەر کەسێک بتوانێت کۆدەکەی بپشکنێت. ناوی بەکارهێنەر و وشەی تێپەڕ و مامەڵە گومرگییەکانت ڕاستەوخۆ دەچنە سێرڤەری وەزارەتی دارایی (asy.mof.gov.iq) و ئەم بەرنامەیە هەرگیز نایانبینێت و پاشەکەوتیان ناکات. وەشانی " + getAppVersionLabel(activity) + " وەشانێکی سەرەکی ئاسایشە و گشت وەشانەکانی پێشتر (< v4.1.0) چیتر سەلامەت نین و پێشنیار ناکرێن."));
        secBody.setTextColor(TEXT_PRIMARY);
        secBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams sbLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sbLp.bottomMargin = dp(activity, 10);
        secCard.addView(secBody, sbLp);

        // Live Shields.io-style Visual Badges Grid (2 or 3 columns)
        GridLayout badgesGrid = new GridLayout(activity);
        badgesGrid.setColumnCount(isCompactWidth(activity) ? 2 : 3);
        LinearLayout.LayoutParams bgLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bgLp.bottomMargin = dp(activity, 8);
        badgesGrid.setLayoutParams(bgLp);

        Runnable openSonar = () -> cb.onOpenUrl(SONAR_DASHBOARD_URL);
        Runnable openSecurityMd = () -> cb.onOpenUrl("https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android/blob/main/SECURITY.md");

        TextView gateValView = addLiveShieldBadge(activity, badgesGrid, "Quality Gate", "PASSED", SYSTEM_GREEN, openSonar);
        TextView secValView = addLiveShieldBadge(activity, badgesGrid, "Sonar Security", "A • 0 Issues", SYSTEM_GREEN, openSonar);
        TextView relValView = addLiveShieldBadge(activity, badgesGrid, "Reliability", "A • 0 Bugs", SYSTEM_GREEN, openSonar);
        TextView maintValView = addLiveShieldBadge(activity, badgesGrid, "Maintainability", "A • 0 Smells", SYSTEM_GREEN, openSonar);
        addLiveShieldBadge(activity, badgesGrid, "Snyk Security", "Verified • 0 High", SYSTEM_BLUE, openSecurityMd);
        addLiveShieldBadge(activity, badgesGrid, "Open Source", "100% Public", SYSTEM_GREEN, openSecurityMd);

        secCard.addView(badgesGrid);

        TextView liveStatusView = new TextView(activity);
        liveStatusView.setText(tr(activity,
                "Checking current code quality status on SonarQube Cloud...",
                "جاري التحقق من حالة فحص الكود على SonarQube Cloud...",
                "پشکنینی دۆخی کوالێتی کۆد لە SonarQube Cloud..."));
        liveStatusView.setTextColor(TEXT_SECONDARY);
        liveStatusView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        LinearLayout.LayoutParams lsLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lsLp.bottomMargin = dp(activity, 10);
        secCard.addView(liveStatusView, lsLp);

        fetchLiveSonarBadges(activity, gateValView, secValView, relValView, maintValView, liveStatusView);

        LinearLayout secBtnsRow = new LinearLayout(activity);
        secBtnsRow.setOrientation(LinearLayout.HORIZONTAL);
        TextView secBtn = createCupertinoButton(activity,
                tr(activity, "Security Policy (SECURITY.md)",
                        "سياسة الأمان (SECURITY.md)",
                        "سیاسەتی ئاسایش (SECURITY.md)"),
                true, false, v -> openSecurityMd.run());
        LinearLayout.LayoutParams sb1Lp = (LinearLayout.LayoutParams) secBtn.getLayoutParams();
        sb1Lp.setMarginEnd(dp(activity, 8));
        secBtnsRow.addView(secBtn);

        TextView sonarBtn = createCupertinoButton(activity,
                tr(activity, "SonarQube Report",
                        "تقرير SonarQube",
                        "ڕاپۆرتی SonarQube"),
                false, false, v -> openSonar.run());
        secBtnsRow.addView(sonarBtn);

        secCard.addView(secBtnsRow);
        content.addView(secCard);

        // 2. Developer Card
        LinearLayout devCard = new LinearLayout(activity);
        devCard.setOrientation(LinearLayout.VERTICAL);
        devCard.setPadding(pad, pad, pad, pad);
        devCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        devCard.setLayoutParams(cardLp);

        TextView devSectionLabel = createSectionHeader(activity,
                tr(activity, "DEVELOPER", "مطور التطبيق", "گەشەپێدەری بەرنامە"));
        devCard.addView(devSectionLabel);

        TextView devName = new TextView(activity);
        devName.setText("Mohammed Ammar Hussein (Mohammed AL-Shimmary)");
        devName.setTextColor(TEXT_PRIMARY);
        devName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        devName.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        devCard.addView(devName);

        TextView devRole = new TextView(activity);
        devRole.setText(tr(activity,
                "Senior Logistics Coordinator & Software Developer — Baghdad, Iraq",
                "منسق لوجستي أقدم ومطور برمجيات — بغداد، العراق",
                "ڕێکخەری باڵای لۆجستی و گەشەپێدەری نەرمەکاڵا — بەغدا، عێراق"));
        devRole.setTextColor(SYSTEM_BLUE);
        devRole.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams roleLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        roleLp.bottomMargin = dp(activity, 12);
        devCard.addView(devRole, roleLp);

        GridLayout socialGrid = new GridLayout(activity);
        int socialCols = isCompactWidth(activity) ? 2 : 4;
        socialGrid.setColumnCount(socialCols);
        socialGrid.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addSocialButton(activity, socialGrid, "GitHub", true,
                () -> cb.onOpenUrl("https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android"));
        addSocialButton(activity, socialGrid, "LinkedIn", false,
                () -> cb.onOpenUrl("https://www.linkedin.com/in/muhammed-ammar-al-shimmary-693a161b1"));
        addSocialButton(activity, socialGrid, "Instagram", false,
                () -> cb.onOpenUrl("https://www.instagram.com/_m.o.d.y?rpxt=djFldnZjbTlya2Yx"));
        addSocialButton(activity, socialGrid, "Email", false,
                () -> cb.onOpenUrl("mailto:muhammed.ammar.1997@gmail.com"));
        devCard.addView(socialGrid);
        content.addView(devCard);

        // 2.5 Original Java Bridge Attribution Card (GNU GPL-3.0 Section 7 Attribution Notice)
        LinearLayout bridgeAttrCard = new LinearLayout(activity);
        bridgeAttrCard.setOrientation(LinearLayout.VERTICAL);
        bridgeAttrCard.setPadding(pad, pad, pad, pad);
        bridgeAttrCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        bridgeAttrCard.setLayoutParams(cardLp);

        bridgeAttrCard.addView(createSectionHeader(activity,
                tr(activity, "ORIGINAL JAVA BRIDGE ATTRIBUTION (GPL-3.0)",
                        "حقوق ملكية ونسبة جسر الجافا الأصلي (GPL-3.0)",
                        "خاوەندارێتی پردی جاڤای سەرەکی (GPL-3.0)")));

        TextView bridgeAttrBody = new TextView(activity);
        bridgeAttrBody.setText(tr(activity,
                "Original Java Bridge Author: Mohammed Ammar Hussein (@MohammedALShimmary)\n"
                        + "Original Project: ASYCUDA Iraq Android (00_bootpatch.jar, 00_boot.jar, 00_jnlp.jar)\n"
                        + "Source: https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android\n\n"
                        + "Licensed under GNU GPL-3.0 with Section 7 attribution preservation terms (see java-bridge/ATTRIBUTION-TERMS.md).",
                "مؤلف جسر الجافا الأصلي: Mohammed Ammar Hussein (@MohammedALShimmary)\n"
                        + "المشروع الأصلي: ASYCUDA Iraq Android (حزم 00_bootpatch.jar و 00_boot.jar و 00_jnlp.jar)\n"
                        + "المصدر الرسمي: https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android\n\n"
                        + "مرخّص بموجب رخصة GNU GPL-3.0 مع شروط حفظ حقوق النسبة للمؤلف الأصلي وفق القسم 7.",
                "دانەری سەرەکی پردی جاڤا: Mohammed Ammar Hussein (@MohammedALShimmary)\n"
                        + "پڕۆژەی سەرەکی: ASYCUDA Iraq Android (00_bootpatch.jar, 00_boot.jar, 00_jnlp.jar)\n"
                        + "سەرچاوە: https://github.com/MohammedALShimmary/ASYCUDA-Iraq-Android"));
        bridgeAttrBody.setTextColor(TEXT_PRIMARY);
        bridgeAttrBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        bridgeAttrCard.addView(bridgeAttrBody);
        content.addView(bridgeAttrCard);

        // 3. Open-Source Components & Licences
        LinearLayout ossCard = new LinearLayout(activity);
        ossCard.setOrientation(LinearLayout.VERTICAL);
        ossCard.setPadding(pad, pad, pad, pad);
        ossCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        ossCard.setLayoutParams(cardLp);

        ossCard.addView(createSectionHeader(activity,
                tr(activity, "OPEN-SOURCE ACKNOWLEDGEMENTS & LICENCES", "المكتبات والمشاريع مفتوحة المصدر", "پڕۆژە و کتێبخانە سەرچاوە کراوەکان")));

        addCreditEntry(activity, ossCard,
                "ASYCUDA Iraq Java Compatibility Bridge (00_bootpatch.jar, 00_boot.jar, 00_jnlp.jar)",
                "GNU GPL-3.0 (Sec. 7 Attribution)",
                "Mohammed Ammar Hussein (@MohammedALShimmary)",
                "Original Android X11/Swing desktop bridge, JFileChooser native document picker router, DirectPdfPrintService, and hardened JVM SecurityManager.");

        addCreditEntry(activity, ossCard,
                "Termux:X11 (libXlorie)",
                "GPLv3 / MIT",
                "Twaik Yont (@twaik), Fredrik Fornwall (@fornwall), @agnostic-apollo, @sylirre",
                "Embedded X11 display server, SurfaceView renderer, and touch input translation layer.");

        addCreditEntry(activity, ossCard,
                "Eclipse Temurin OpenJDK 8u504-b01 LTS",
                "GPLv2 + Classpath",
                "Eclipse Adoptium, Red Hat AArch64 Port Team & OpenJDK Contributors",
                "ARM64 Java Standard Edition 8u504-b01 LTS runtime environment and Swing/AWT desktop graphical toolkit.");

        addCreditEntry(activity, ossCard,
                "GNU C Library (glibc) for Android",
                "LGPLv2.1+",
                "Free Software Foundation & @Maxython (glibc-packages)",
                "POSIX standard C library and dynamic ELF linker for running native Linux ARM64 binaries.");

        addCreditEntry(activity, ossCard,
                "X.Org Foundation & FreeType",
                "MIT / FreeType",
                "Keith Packard, David Turner, Werner Lemberg & freedesktop.org",
                "Core X11 client protocol libraries (libX11, libXext, libXrender, libXtst) and font rendering engine.");

        addCreditEntry(activity, ossCard,
                "XZ for Java & Apache Commons Compress",
                "0BSD / Apache 2.0",
                "Lasse Collin, Igor Pavlov & The Apache Software Foundation",
                "High-ratio LZMA2/XZ archive decompression and POSIX TAR extraction with symlink preservation.");

        content.addView(ossCard);

        // 4. Legal & Liability Disclaimer Card
        LinearLayout legalCard = new LinearLayout(activity);
        legalCard.setOrientation(LinearLayout.VERTICAL);
        legalCard.setPadding(pad, dp(activity, 14), pad, dp(activity, 14));
        legalCard.setBackground(createCardDrawable(activity,
                SURFACE_INSET_CARD, 14, SEPARATOR_HAIRLINE));

        TextView legalHeader = createSectionHeader(activity,
                tr(activity, "LEGAL NOTICE & LIMITATION OF LIABILITY", "إخلاء المسؤولية القانونية والفنية", "ئاگاداری و بەرپرسیارێتی یاسایی"));
        legalCard.addView(legalHeader);

        // Green Acceptance Indicator Badge inside the Disclaimer section
        LinearLayout consentBadge = new LinearLayout(activity);
        consentBadge.setOrientation(LinearLayout.VERTICAL);
        consentBadge.setPadding(dp(activity, 12), dp(activity, 10), dp(activity, 12), dp(activity, 10));
        boolean accepted = hasAcceptedTerms(activity);
        consentBadge.setBackground(createCardDrawable(activity,
                accepted ? SYSTEM_GREEN_TINT : SYSTEM_BLUE_TINT,
                10,
                accepted ? Color.parseColor("#6630D158") : SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cbLp.bottomMargin = dp(activity, 10);
        consentBadge.setLayoutParams(cbLp);

        LinearLayout badgeTopRow = new LinearLayout(activity);
        badgeTopRow.setOrientation(LinearLayout.HORIZONTAL);
        badgeTopRow.setGravity(Gravity.CENTER_VERTICAL);

        View greenDot = new View(activity);
        greenDot.setBackground(createCardDrawable(activity, accepted ? SYSTEM_GREEN : SYSTEM_BLUE, 5, 0));
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(activity, 10), dp(activity, 10));
        dotLp.setMarginEnd(dp(activity, 8));
        badgeTopRow.addView(greenDot, dotLp);

        TextView badgeTitle = new TextView(activity);
        badgeTitle.setText(getTermsAcceptanceBadgeTitle(activity));
        badgeTitle.setTextColor(accepted ? SYSTEM_GREEN : SYSTEM_BLUE);
        badgeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        badgeTitle.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        badgeTopRow.addView(badgeTitle);
        consentBadge.addView(badgeTopRow);

        TextView badgeSub = new TextView(activity);
        badgeSub.setText(getTermsAcceptanceBadgeSubtitle(activity));
        badgeSub.setTextColor(TEXT_PRIMARY);
        badgeSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        LinearLayout.LayoutParams bsLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bsLp.topMargin = dp(activity, 3);
        consentBadge.addView(badgeSub, bsLp);
        legalCard.addView(consentBadge);

        TextView legalText = new TextView(activity);
        legalText.setText(tr(activity,
                "This application was developed solely as a helpful tool to assist customs brokers, logistics coordinators, and trade operators in accessing ASYCUDA World on Android devices. It is an independent technical utility and is not affiliated with, sponsored by, or officially endorsed by the Iraqi Ministry of Finance, the General Commission for Customs, or UNCTAD.\n\nThis software is provided \"as is\" without warranties or guarantees of any kind. The developer assumes no responsibility or liability whatsoever for any direct, indirect, incidental, or consequential harm, financial loss, data discrepancy, account suspension, administrative penalty, or any action taken by government authorities or any other entity against individual users or companies using this application. By using this app, you acknowledge that you do so entirely at your own risk and responsibility.",
                "تم تطوير هذا التطبيق لغرض المساعدة فقط وتسهيل عمل المخلصين الكمركيين، المنسقين اللوجستيين، والشركات التجارية في تشغيل نظام أسيكودا العالمي على أجهزة الأندرويد. هذا التطبيق مبادرة تقنية مستقلة ولا يتبع رسمياً لوزارة المالية العراقية أو الهيئة العامة للكمارك أو مؤتمر الأمم المتحدة للتجارة والتنمية (UNCTAD).\n\nيُقدّم هذا البرنامج \"كما هو\" دون أي ضمانات صريحة أو ضمنية. يخلي مطور التطبيق مسؤوليته القانونية والمالية والفنية الكاملة عن أي ضرر مباشر أو غير مباشر، أو خسائر مالية، أو إيقاف للحسابات، أو غرامات، أو أي إجراءات إدارية أو قانونية قد تُتخذ من قِبل الجهات الحكومية أو أي جهة أخرى بحق المستخدمين أو الشركات التي تستخدم هذا التطبيق. إن استخدامك لهذا التطبيق يعني موافقتك التامة على تحمّل كامل المسؤولية المترتبة على ذلك.",
                "ئەم بەرنامەیە تەنها بە مەبەستی هاوکاری و کارئاسانی بۆ بریکارانی گومرگی و کۆمپانیا بازرگانییەکان دروستکراوە بۆ بەکارهێنانی سیستەمی ئەسیکۆدای جیهانی لەسەر ئامێرەکانی ئەندرۆید. ئەم پڕۆژەیە دەستپێشخەرییەکی تەکنیکی سەربەخۆیە و بە فەرمی سەر بە وەزارەتی دارایی عێراق، دەستەی گشتیی گومرگ، یان ڕێکخراوی UNCTAD نییە.\n\nئەم نەرمەکاڵایە \"وەک خۆی\" پێشکەش دەکرێت بەبێ هیچ گەرەنتییەک. گەشەپێدەری بەرنامەکە هیچ بەرپرسیارێتییەکی یاسایی، دارایی، یان تەکنیکی هەڵناگرێت بەرامبەر هەر زیانێکی ڕاستەوخۆ یان ناڕاستەوخۆ، ڕاگرتنی هەژمار، سزا، یان هەر ڕێکارێک کە لەلایەن لایەنە حکومییەکان یان هەر دامەزراوەیەکی ترەوە بگیرێتەبەر بەرامبەر بەکارهێنەران یان کۆمپانیاکان. بەکارهێنانی ئەم بەرنامەیە بە تەواوی لەسەر بەرپرسیارێتی تایبەتی خۆتانە."));
        legalText.setTextColor(TEXT_SECONDARY);
        legalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        legalCard.addView(legalText);
        content.addView(legalCard);

        scroll.addView(content);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();
    }

    private static TextView addLiveShieldBadge(Context ctx, GridLayout grid,
                                               String leftLabel, String initialRightValue,
                                               int rightAccentColor, Runnable onTap) {
        LinearLayout pill = new LinearLayout(ctx);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER_VERTICAL);
        pill.setBackground(createRippleCardDrawable(ctx, SURFACE_INSET_CARD, 8, SEPARATOR_HAIRLINE));
        pill.setClickable(true);
        pill.setFocusable(true);
        if (onTap != null) {
            pill.setOnClickListener(v -> onTap.run());
        }

        TextView leftTv = new TextView(ctx);
        leftTv.setText(leftLabel);
        leftTv.setTextColor(TEXT_PRIMARY);
        leftTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        leftTv.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.NORMAL));
        leftTv.setPadding(dp(ctx, 8), dp(ctx, 5), dp(ctx, 7), dp(ctx, 5));
        leftTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        pill.addView(leftTv);

        TextView rightTv = new TextView(ctx);
        rightTv.setText(initialRightValue);
        rightTv.setTextColor(Color.WHITE);
        rightTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        rightTv.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        rightTv.setPadding(dp(ctx, 8), dp(ctx, 5), dp(ctx, 8), dp(ctx, 5));
        rightTv.setBackground(createCardDrawable(ctx, rightAccentColor, 7, 0));
        pill.addView(rightTv);

        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.width = 0;
        glp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        int m = dp(ctx, 3);
        glp.setMargins(m, m, m, m);
        grid.addView(pill, glp);
        return rightTv;
    }

    private static String ratingToLetter(String raw) {
        if ("1.0".equals(raw) || "1".equals(raw)) return "A";
        if ("2.0".equals(raw) || "2".equals(raw)) return "B";
        if ("3.0".equals(raw) || "3".equals(raw)) return "C";
        if ("4.0".equals(raw) || "4".equals(raw)) return "D";
        if ("5.0".equals(raw) || "5".equals(raw)) return "E";
        return "A";
    }

    private static void fetchLiveSonarBadges(MainActivity activity,
                                             TextView gateView,
                                             TextView secView,
                                             TextView relView,
                                             TextView maintView,
                                             TextView statusView) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(SONAR_MEASURES_API_URL).openConnection();
                conn.setConnectTimeout(4500);
                conn.setReadTimeout(6000);
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() == 200) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try (InputStream in = conn.getInputStream()) {
                        byte[] buf = new byte[4096];
                        int n;
                        while ((n = in.read(buf)) != -1) bos.write(buf, 0, n);
                    }
                    JSONObject root = new JSONObject(new String(bos.toByteArray(), StandardCharsets.UTF_8));
                    JSONObject comp = root.optJSONObject("component");
                    JSONArray measures = comp != null ? comp.optJSONArray("measures") : null;
                    if (measures != null) {
                        String gate = "OK";
                        String vulns = "0";
                        String bugs = "0";
                        String smells = "0";
                        String secRating = "A";
                        String relRating = "A";
                        String sqaleRating = "A";
                        for (int i = 0; i < measures.length(); i++) {
                            JSONObject m = measures.getJSONObject(i);
                            String metric = m.optString("metric", "");
                            String val = m.optString("value", "");
                            switch (metric) {
                                case "alert_status":
                                    gate = val;
                                    break;
                                case "vulnerabilities":
                                    vulns = val;
                                    break;
                                case "bugs":
                                    bugs = val;
                                    break;
                                case "code_smells":
                                    smells = val;
                                    break;
                                case "security_rating":
                                    secRating = ratingToLetter(val);
                                    break;
                                case "reliability_rating":
                                    relRating = ratingToLetter(val);
                                    break;
                                case "sqale_rating":
                                    sqaleRating = ratingToLetter(val);
                                    break;
                                default:
                                    break;
                            }
                        }
                        final boolean passed = "OK".equalsIgnoreCase(gate);
                        final String fGate = passed ? "PASSED" : gate;
                        final String fSec = secRating + " • " + vulns + " Issues";
                        final String fRel = relRating + " • " + bugs + " Bugs";
                        final String fMaint = sqaleRating + " • " + smells + " Smells";
                        final int warnOrRedColor = "WARN".equalsIgnoreCase(gate)
                                ? Color.parseColor("#FFFF9F0A") : SYSTEM_RED;
                        final int gateColor = passed ? SYSTEM_GREEN : warnOrRedColor;
                        final int secColor = getBadgeColorForRating(secRating, vulns);
                        final int relColor = getBadgeColorForRating(relRating, bugs);
                        final int maintColor = getBadgeColorForRating(sqaleRating, smells);
                        final boolean allClean = passed && secColor == SYSTEM_GREEN && relColor == SYSTEM_GREEN;
                        activity.runOnUiThread(() -> {
                            gateView.setText(fGate);
                            gateView.setBackground(createCardDrawable(activity, gateColor, 7, 0));
                            secView.setText(fSec);
                            secView.setBackground(createCardDrawable(activity, secColor, 7, 0));
                            relView.setText(fRel);
                            relView.setBackground(createCardDrawable(activity, relColor, 7, 0));
                            maintView.setText(fMaint);
                            maintView.setBackground(createCardDrawable(activity, maintColor, 7, 0));
                            statusView.setText(tr(activity,
                                    "Updated from SonarQube Cloud — tap any badge to view the full report",
                                    "تم التحديث من SonarQube Cloud — اضغط على أي شارة لعرض التقرير الكامل",
                                    "نوێکرایەوە لە SonarQube Cloud — گرتە لە هەر نیشانەیەک بکە بۆ بینینی ڕاپۆرتی تەواو"));
                            statusView.setTextColor(allClean ? SYSTEM_GREEN : Color.parseColor("#FFFF9F0A"));
                        });
                        return;
                    }
                }
            } catch (Exception ignored) {
                // Offline fallback keeps default verified badge labels
            } finally {
                if (conn != null) conn.disconnect();
            }
            activity.runOnUiThread(() ->
                statusView.setText(tr(activity,
                        "Tap any badge to open the SonarQube Cloud or Snyk security reports",
                        "اضغط على أي شارة لفتح تقارير SonarQube Cloud و Snyk",
                        "گرتە لە هەر نیشانەیەک بکە بۆ کردنەوەی ڕاپۆرتەکان"))
            );
        }, "Asycuda-SonarBadgeFetcher").start();
    }

    private static int getBadgeColorForRating(String rating, String countStr) {
        int count = 0;
        try {
            count = Integer.parseInt(countStr != null ? countStr.trim() : "0");
        } catch (NumberFormatException ignored) {
            // Default to 0
        }
        if ("A".equalsIgnoreCase(rating) && count == 0) {
            return SYSTEM_GREEN;
        }
        if ("D".equalsIgnoreCase(rating) || "E".equalsIgnoreCase(rating)) {
            return SYSTEM_RED;
        }
        return Color.parseColor("#FFFF9F0A");
    }

    /**
     * Post-install / post-update In-App Changelog Modal ("What's New").
     */
    public static void showWhatsNewModal(MainActivity activity, Runnable onContinue) {
        Dialog dialog = createModalDialog(activity, 0.84f, 550, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams lgLp = new LinearLayout.LayoutParams(dp(activity, 28), dp(activity, 28));
        lgLp.setMarginEnd(dp(activity, 8));
        header.addView(logo, lgLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "What's New in Major Release " + getAppVersionLabel(activity),
                "ما الجديد في الإصدار الرئيسي " + getAppVersionLabel(activity),
                "چی نوێ هەیە لە وەشانی سەرەکی " + getAppVersionLabel(activity)));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Major Security & Architecture Upgrade — Previous Versions (< v4.1.0) Are Unsafe & Not Recommended",
                "تحديث رئيسي شامل للأمان والبنية — جميع الإصدارات السابقة (< v4.1.0) غير آمنة وغير موصى بها",
                "نوێکردنەوەی سەرەکی ئاسایش — گشت وەشانەکانی پێشتر (< v4.1.0) سەلامەت نین و پێشنیار ناکرێن"));
        subtitle.setTextColor(SYSTEM_BLUE);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        // Top-bar Continue icon (replaces old full-width bottom "Continue to ASYCUDA" button)
        header.addView(createIconButton(activity, R.drawable.ic_action_check,
                tr(activity, "Continue to ASYCUDA", "متابعة إلى النظام", "بەردەوامبوون بۆ سیستەم"),
                true, false, v -> {
                    markWhatsNewSeen(activity);
                    dialog.dismiss();
                    if (onContinue != null) onContinue.run();
                }));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scLp.topMargin = dp(activity, 8);
        scroll.setLayoutParams(scLp);

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = dp(activity, 12);
        card.setPadding(p, dp(activity, 10), p, dp(activity, 6));
        card.setBackground(createCardDrawable(activity, SURFACE_GROUPED_CARD, 12, SEPARATOR_HAIRLINE));

        addPolicyClause(activity, card,
                tr(activity,
                        "1. Automatic Connection Setup from the Official Customs Server",
                        "1. الضبط التلقائي للاتصال من خادم الكمارك الرسمي",
                        "١. ڕێکخستنی خۆکاری پەیوەندی لە ڕاژەی فەرمی گومرگەوە"),
                tr(activity,
                        "The app now reads server connection settings and communication ports directly from the official Ministry of Finance configuration file (AWIQWorld.jnlp) and automatically reconnects when switching between Wi-Fi and mobile data.",
                        "يقرأ التطبيق الآن إعدادات الاتصال وأرقام المنافذ مباشرة من ملف الإعدادات الرسمي لوزارة المالية (AWIQWorld.jnlp)، ويعيد ربط الاتصال تلقائياً عند التنقل بين شبكة الواي فاي وبيانات الهاتف.",
                        "بەرنامەکە ئێستا ڕێکخستنەکانی پەیوەندی و دەرچەکان ڕاستەوخۆ لە پەڕگەی فەرمی وەزارەتی دارایی (AWIQWorld.jnlp) وەردەگرێت و لە کاتی گۆڕینی نێوان وایفای و ئینتەرنێتی مۆبایلدا پەیوەندییەکە نوێ دەکاتەوە."));

        addPolicyClause(activity, card,
                tr(activity,
                        "2. File & Folder Renaming in the Document Vault",
                        "2. ميزة تعديل أسماء الملفات والمجلدات داخل الخزنة",
                        "٢. تایبەتمەندی گۆڕینی ناوی پەڕگە و بوخچەکان لەناو قاسەدا"),
                tr(activity,
                        "You can now rename any saved PDF, imported document, or folder directly inside the Vault File Manager so your customs declarations and attachments stay neatly organized.",
                        "يمكنك الآن تعديل وإعادة تسمية أي ملف PDF مطبوع أو مستند مستورد أو مجلد مباشرة من داخل مدير ملفات الخزنة لترتيب معاملاتك ومرفقاتك الكمركية بسهولة.",
                        "ئێستا دەتوانیت ناوی هەر پەڕگەیەکی PDF، بەڵگەنامەی هاوردەکراو، یان بوخچەیەک ڕاستەوخۆ لەناو بەڕێوەبەری پەڕگەکانی قاسەدا بگۆڕیت بۆ ڕێکخستنی باشتری مامەڵەکانت."));

        addPolicyClause(activity, card,
                tr(activity,
                        "3. Safe One-Way Clipboard Paste into ASYCUDA",
                        "3. لصق النصوص بأمان من الهاتف إلى داخل أسيكودا",
                        "٣. لکاندنی پارێزراوی دەق لە مۆبایلەوە بۆ ناو ئەسیکۆدا"),
                tr(activity,
                        "Text copied on your phone can be pasted directly into ASYCUDA World fields using either the keyboard clipboard or the Paste menu, while keeping data inside your ASYCUDA session private and never recording what you type or paste.",
                        "يمكنك لصق النصوص المنسوخة من هاتفك مباشرة داخل حقول نظام أسيكودا سواء عبر حافظة الكيبورد أو قائمة اللصق، مع الحفاظ على خصوصية البيانات داخل الجلسة وعدم تسجيل أي نصوص مكتوبة أو منسوخة.",
                        "دەتوانیت ئەو دەقانەی لەسەر مۆبایلەکەت کۆپیت کردوون ڕاستەوخۆ لەناو خانەکانی ئەسیکۆدا دابنێیت، لەگەڵ پاراستنی تەواوی نهێنی زانیارییەکانی ناو دانیشتنەکەت."));

        addPolicyClause(activity, card,
                tr(activity,
                        "4. Stronger System File Protection & Package Verification",
                        "4. تعزيز حماية ملفات النظام والتحقق من سلامة الحزم",
                        "٤. پاراستنی باشتری پەڕگەکانی سیستەم و پشتڕاستکردنەوەی پاکێجەکان"),
                tr(activity,
                        "All official customs client packages are verified against their digital signature on startup and locked against unauthorized modification while the session is running.",
                        "يتم التحقق من التوقيع الرقمي لجميع حزم نظام الكمارك الرسمية عند كل تشغيل، مع قفل ملفات النظام لمنع أي تعديل غير مصرح به أثناء عمل الجلسة.",
                        "هەموو پەڕگە فەرمییەکانی سیستەمی گومرگ لە کاتی دەستپێکردندا واژۆی دیجیتاڵییان دەپشکنرێت و دەپارێزرێن لە هەر دەستکارییەکی ڕێگەپێنەدراو."));

        addPolicyClause(activity, card,
                tr(activity,
                        "5. Universal Network Diagnostics, Known-Issues Catalog & 4G Bypass",
                        "5. نافذة التشخيص الذكي للأعطال وقاعدة البيانات وتمرير اتصال 4G",
                        "٥. پەنجەرەی دەستنیشانکردنی زیرەکی کێشەکان و تێپەڕاندنی پەیوەندی 4G"),
                tr(activity,
                        "Automatically diagnoses network, firewall, and runtime issues against a verified Known-Issues Catalog, lets you route your ASYCUDA session over 4G Mobile Data with one tap when Wi-Fi is blocked, and provides a 100% in-RAM redacted log preview.",
                        "تشخيص تلقائي لأعطال الشبكة والجدار الناري ومطابقتها مع قاعدة بيانات الأعطال المعروفة، مع إمكانية تمرير جلسة أسيكودا عبر بيانات الهاتف (4G) بضغطة واحدة عند حجب شبكة Wi-Fi، ومعاينة آمنة للسجلات المنقاة داخل الذاكرة العشوائية (RAM).",
                        "دەستنیشانکردنی خۆکاری کێشەکانی تۆڕ و بەراوردکردنی لەگەڵ بنکەی زانیاری هەڵە ناسراوەکان، لەگەڵ توانای تێپەڕاندنی دانیشتنی ئەسیکۆدا لە ڕێگەی داتای مۆبایل (4G) کاتێک وایفای بلۆک دەکرێت و پێداچوونەوەی پارێزراوی تۆمارەکان لەناو RAM."));

        scroll.addView(card);
        root.addView(scroll);

        dialog.setOnCancelListener(d -> {
            markWhatsNewSeen(activity);
            if (onContinue != null) onContinue.run();
        });

        dialog.setContentView(root);
        dialog.show();
    }

    private static void purgeLegacyConsentPii(Context ctx) {
        if (ctx == null) return;
        try {
            File legacyReceiptFile = new File(ctx.getFilesDir(), "legal_consent_receipt.json");
            if (legacyReceiptFile.exists() && !legacyReceiptFile.delete()) {
                // Best-effort cleanup of legacy file
            }
            SharedPreferences sp = ctx.getSharedPreferences(CONSENT_PREFS_NAME, Context.MODE_PRIVATE);
            if (sp.contains(KEY_SIGNATORY_NAME) || sp.contains(KEY_COMPANY_NAME) || sp.contains(KEY_DEVICE_HASH)) {
                sp.edit()
                        .remove(KEY_SIGNATORY_NAME)
                        .remove(KEY_COMPANY_NAME)
                        .remove(KEY_DEVICE_HASH)
                        .remove(KEY_CONSENT_ID)
                        .remove(KEY_SHA256_SIG)
                        .remove(KEY_CLOUD_SYNCED)
                        .apply();
            }
        } catch (Exception ignored) { /* Best-effort cleanup */ }
    }

    public static boolean hasAcceptedTerms(Context ctx) {
        if (ctx == null) return false;
        purgeLegacyConsentPii(ctx);
        SharedPreferences sp = ctx.getSharedPreferences(CONSENT_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_ACCEPTED, false);
    }

    public static void recordTermsAccepted(Context ctx) {
        if (ctx == null) return;
        purgeLegacyConsentPii(ctx);
        String localStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
        String utcStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(new Date());
        SharedPreferences sp = ctx.getSharedPreferences(CONSENT_PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putBoolean(KEY_ACCEPTED, true)
                .putString(KEY_POLICY_VERSION, CURRENT_POLICY_VERSION)
                .putString(KEY_APP_VERSION, getAppVersionLabel(ctx))
                .putString(KEY_ACCEPTED_UTC, utcStr)
                .putString(KEY_ACCEPTED_LOCAL, localStr)
                .apply();
        // On a fresh install where the user just accepted Terms & Conditions, mark the current
        // version's What's New dialog as seen so two full-screen modals do not stack back-to-back.
        markWhatsNewSeen(ctx);
    }

    public static String getTermsAcceptanceBadgeTitle(Context ctx) {
        if (hasAcceptedTerms(ctx)) {
            return tr(ctx,
                    "✓ Terms & Conditions Accepted (Iraqi Civil Code Art. 259/2 & Law 78/2012 Art. 18)",
                    "✓ تمت الموافقة على الشروط وإخلاء المسؤولية (م 259/2 مدني عراقي وم 18 قانون 78 لسنة 2012)",
                    "✓ مەرج و بەرپرسیارێتی یاسایی پەسەندکراون (یاسای شارستانی عێراقی م ٢٥٩/٢ و یاسای ٧٨/٢٠١٢)");
        }
        return tr(ctx,
                "Terms & Conditions Pending Acceptance",
                "الشروط والأحكام بانتظار الموافقة",
                "مەرج و ڕێساکان چاوەڕوانی پەسەندکردنن");
    }

    public static String getTermsAcceptanceBadgeSubtitle(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(CONSENT_PREFS_NAME, Context.MODE_PRIVATE);
        if (!sp.getBoolean(KEY_ACCEPTED, false)) {
            return tr(ctx,
                    "Requires one-time acceptance on first launch. No personal information is collected.",
                    "يتطلب الموافقة لمرة واحدة عند التشغيل الأول، ولا يتم جمع أي معلومات شخصية.",
                    "پێویستی بە پەسەندکردن هەیە لە یەکەم کارپێکردندا، و هیچ زانیارییەکی کەسی کۆناکرێتەوە.");
        }
        String localDate = sp.getString(KEY_ACCEPTED_LOCAL, "");
        return tr(ctx,
                "Accepted on " + localDate + "  •  Saved locally on this device (Policy v" + CURRENT_POLICY_VERSION + ")",
                "تمت الموافقة بتاريخ " + localDate + "  •  محفوظة محلياً على هذا الجهاز (الإصدار " + CURRENT_POLICY_VERSION + ")",
                "پەسەندکراوە لە " + localDate + "  •  لەسەر ئەم ئامێرە پاشەکەوتکراوە (وەشانی " + CURRENT_POLICY_VERSION + ")");
    }

    public static String getTermsConsentSummaryForDiagnostics(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(CONSENT_PREFS_NAME, Context.MODE_PRIVATE);
        if (!sp.getBoolean(KEY_ACCEPTED, false)) {
            return "TermsAccepted: false";
        }
        return "TermsAccepted: true (Iraqi Civil Code Art. 259/2 & Law No. 78 of 2012 Art. 18/1)\n"
                + "PolicyVersion: " + sp.getString(KEY_POLICY_VERSION, CURRENT_POLICY_VERSION) + "\n"
                + "AcceptedAppVersion: " + sp.getString(KEY_APP_VERSION, "");
    }

    public interface TermsAcceptedHandler {
        void onAccepted();
    }

    public static void showTermsAndConditionsModal(
            MainActivity activity,
            TermsAcceptedHandler onAccepted,
            Runnable onDeclined) {

        Dialog dialog = createModalDialog(activity, 0.85f, 580, false);
        LinearLayout root = createDialogContainer(activity);

        // Header Row with Logo + Title + Language Switcher Pills
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 6));

        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams lgLp = new LinearLayout.LayoutParams(dp(activity, 28), dp(activity, 28));
        lgLp.setMarginEnd(dp(activity, 8));
        header.addView(logo, lgLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Terms & Conditions and Legal Liability Waiver",
                "الشروط والأحكام وإخلاء المسؤولية القانونية",
                "مەرج و ڕێساکان و بەرپرسیارێتی یاسایی"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Governed by Iraqi Civil Code No. 40 of 1951 (Art. 259/2) & Electronic Transactions Law No. 78 of 2012 (Art. 18/1)",
                "خاضع للمادة (259/2) من القانون المدني العراقي رقم 40 لسنة 1951 والمادة (18/أولاً) من قانون المعاملات الإلكترونية رقم 78 لسنة 2012",
                "بەپێی ماددەی (٢٥٩/٢) لە یاسای شارستانی عێراقی ژمارە ٤٠ی ١٩٥١ و ماددەی (١٨/١) لە یاسای مامەڵە ئەلیکترۆنییەکان ژمارە ٧٨ی ٢٠١٢"));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        // Quick Language Switcher so user can read Terms in AR / EN / KU on first launch
        LinearLayout langBar = new LinearLayout(activity);
        langBar.setOrientation(LinearLayout.HORIZONTAL);
        String curLang = getAppLanguage(activity);
        String[][] langs = {
                { "ar", "عربي" },
                { "en", "EN" },
                { "ku", "کوردی" }
        };
        for (String[] l : langs) {
            boolean sel = l[0].equals(curLang);
            TextView lBtn = new TextView(activity);
            lBtn.setText(l[1]);
            lBtn.setTextColor(sel ? TEXT_PRIMARY : TEXT_SECONDARY);
            lBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
            lBtn.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, sel ? Typeface.BOLD : Typeface.NORMAL));
            lBtn.setPadding(dp(activity, 8), dp(activity, 4), dp(activity, 8), dp(activity, 4));
            lBtn.setBackground(createRippleCardDrawable(activity,
                    sel ? SYSTEM_BLUE : SURFACE_GROUPED_CARD, 7, sel ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams lLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lLp.setMarginStart(dp(activity, 4));
            lBtn.setLayoutParams(lLp);
            lBtn.setOnClickListener(v -> {
                setAppLanguage(activity, l[0]);
                dialog.dismiss();
                showTermsAndConditionsModal(activity, onAccepted, onDeclined);
            });
            langBar.addView(lBtn);
        }
        header.addView(langBar);
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        // Scrollable Policy Body
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scLp.topMargin = dp(activity, 6);
        scLp.bottomMargin = dp(activity, 6);
        scroll.setLayoutParams(scLp);

        LinearLayout scrollContent = new LinearLayout(activity);
        scrollContent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout bodyCard = new LinearLayout(activity);
        bodyCard.setOrientation(LinearLayout.VERTICAL);
        int p = dp(activity, 11);
        bodyCard.setPadding(p, dp(activity, 9), p, dp(activity, 6));
        bodyCard.setBackground(createCardDrawable(activity, SURFACE_INSET_CARD, 12, SEPARATOR_HAIRLINE));

        addPolicyClause(activity, bodyCard,
                tr(activity,
                        "1. Purpose of Application & Non-Affiliation",
                        "1. الغرض من التطبيق وعدم التبعية الرسمية",
                        "١. مەبەستی بەرنامەکە و سەربەخۆیی"),
                tr(activity,
                        "This free, open-source application (GPL v3 License) was created solely as a technical utility to assist customs brokers, logistics coordinators, and trade companies in running the Iraqi Customs ASYCUDA World desktop environment on Android devices. It is an independent project and is not affiliated with, sponsored by, or officially endorsed by the Iraqi Ministry of Finance, the General Commission for Customs, or UNCTAD.",
                        "تم تطوير هذا التطبيق المجاني مفتوح المصدر (بموجب رخصة GPL v3) لغرض المساعدة التقنية فقط وتسهيل عمل المخلصين الكمركيين والشركات التجارية في تشغيل بيئة نظام أسيكودا العالمي على أجهزة الأندرويد. هذا التطبيق مبادرة تقنية مستقلة ولا يتبع رسمياً لوزارة المالية العراقية أو الهيئة العامة للكمارك أو مؤتمر الأمم المتحدة للتجارة والتنمية (UNCTAD).",
                        "ئەم بەرنامە خۆڕایی و سەرچاوە کراوەیە (مۆڵەتی GPL v3) تەنها بە مەبەستی هاوکاری تەکنیکی بۆ بریکارانی گومرگی و کۆمپانیا بازرگانییەکان دروستکراوە بۆ بەکارهێنانی سیستەمی ئەسیکۆدای جیهانی لەسەر ئامێرەکانی ئەندرۆید، و بە فەرمی سەر بە وەزارەتی دارایی عێراق، دەستەی گشتیی گومرگ، یان ڕێکخراوی UNCTAD نییە."));

        addPolicyClause(activity, bodyCard,
                tr(activity,
                        "2. Official Client Packages & Enforced Network Isolation",
                        "2. سلامة الملفات الرسمية وحصر الاتصال بالخادم الحكومي",
                        "٢. سەلامەتی پەڕگە فەرمییەکان و پەیوەندی ڕاستەوخۆ"),
                tr(activity,
                        "The application connects directly to the official Iraqi Customs server (https://asy.mof.gov.iq), downloads the 131 official UNCTAD/SOClass-signed JAR packages on first launch after verifying their X.509 cryptographic signature, and enforces a strict JVM SecurityManager network allowlist confining all ASYCUDA traffic exclusively to asy.mof.gov.iq.",
                        "يتصل التطبيق مباشرة بالخادم الرسمي للهيئة العامة للكمارك (https://asy.mof.gov.iq) ويقوم بتحميل الحزم الرسمية الموقّعة رقمياً (131 ملف JAR) والتحقق من شهادتها التشفيرية (X.509)، مع فرض جدار حماية برمجي (SecurityManager) يحصر اتصالات بيئة الجافا بالخادم الحكومي حصرياً.",
                        "ئەم بەرنامەیە ڕاستەوخۆ پەیوەندی دەکات بە سێرڤەری فەرمی گومرگی عێراق (https://asy.mof.gov.iq) و ١٣١ پەڕگە فەرمییەکانی ئەسیکۆدا دوای پشتڕاستکردنەوەی بڕوانامەی X.509 کارپێدەکات."));

        addPolicyClause(activity, bodyCard,
                tr(activity,
                        "3. Binding Contractual Liability Waiver (Iraqi Civil Code Art. 259/2 & Law No. 78 of 2012 Art. 18/1)",
                        "3. شرط الإعفاء من المسؤولية وحجية القبول الإلكتروني (المادة 259/2 مدني عراقي والمادة 18/أولاً قانون 78 لسنة 2012)",
                        "٣. مەرجی بەخشین لە بەرپرسیارێتی یاسایی (ماددەی ٢٥٩/٢ یاسای شارستانی عێراقی و ماددەی ١٨/١ یاسای ٧٨ی ٢٠١٢)"),
                tr(activity,
                        "Pursuant to Article 259(2) of the Iraqi Civil Code No. 40 of 1951 (permitting contractual exemption from liability), the gratuitous nature of this free software, and Article 18(1) of the Iraqi Electronic Signature and Electronic Transactions Law No. 78 of 2012 (recognizing the full legal validity of electronic offer and click-wrap acceptance), this software is provided \"AS IS\" without warranties of any kind. The developer assumes no responsibility or liability whatsoever for any direct, indirect, incidental, or consequential harm, financial loss, data discrepancy, customs clearance delay, account suspension, administrative penalty, or any action taken by government authorities or any other entity. Because the application is technologically locked until you accept below, proceeding to use the application constitutes conclusive electronic proof of your acceptance of this waiver.",
                        "عملاً بأحكام المادة (259/2) من القانون المدني العراقي رقم (40) لسنة 1951 (التي تُجيز الاتفاق على الإعفاء من المسؤولية العقدية)، وبما أن هذا البرنامج يُقدّم مجاناً بدون عوض مالي (على سبيل التبرع بالمنافع التقنية)، واستناداً إلى المادة (18/أولاً) والمادة (13) من قانون التوقيع الإلكتروني والمعاملات الإلكترونية العراقي رقم (78) لسنة 2012 (التي تُقر الحجية القانونية الكاملة للإيجاب والقبول الإلكتروني)، يُقدّم هذا البرنامج «كما هو» (AS IS) دون أي ضمانات صريحة أو ضمنية. يُعفى مطور التطبيق إعفاءً تاماً وشاملاً من أي مسؤولية قانونية أو مدنية أو مالية أو فنية عن أي ضرر مباشر أو غير مباشر، أو خسائر مالية، أو تأخير في المعاملات الكمركية، أو إيقاف للحسابات، أو غرامات، أو أي إجراءات إدارية أو قانونية. وبما أن التطبيق مقفل برمجياً ولا يعمل إلا بعد الضغط على زر الموافقة أدناه، فإن تشغيلك أو استخدامك للتطبيق يُعد قرينة إلكترونية قاطعة وحجة قانونية ملزمة على قبولك التام لهذا الإعفاء.",
                        "بەپێی حوکمەکانی ماددەی (٢٥٩/٢) لە یاسای شارستانی عێراقی ژمارە (٤٠)ی ساڵی ١٩٥١ و ماددەی (١٨/یەکەم) لە یاسای واژۆی ئەلیکترۆنی و مامەڵە ئەلیکترۆنییەکانی عێراق ژمارە (٧٨)ی ساڵی ٢٠١٢، ئەم نەرمەکاڵایە بە خۆڕایی و «وەک خۆی» (AS IS) پێشکەش دەکرێت بەبێ هیچ گەرەنتییەک. گەشەپێدەری بەرنامەکە بە تەواوی دەبەخشرێت لە هەر بەرپرسیارێتییەکی یاسایی، شارستانی، دارایی، یان تەکنیکی بەرامبەر هەر زیانێکی ڕاستەوخۆ یان ناڕاستەوخۆ، دواکەوتنی مامەڵەی گومرگی، یان ڕاگرتنی هەژمار. بەهۆی ئەوەی بەرنامەکە کارناکات تاوەکو دوگمەی پەسەندکردن لە خوارەوە دانەگریت، بەکارهێنانی بەرنامەکە بەڵگەی ئەلیکترۆنی یەکلاکەرەوەیە لەسەر پەسەندکردنی تەواوی ئەم مەرجانە."));

        addPolicyClause(activity, bodyCard,
                tr(activity,
                        "4. Privacy & Personal Data Protection",
                        "4. حماية الخصوصية وعدم جمع البيانات الشخصية",
                        "٤. پاراستنی تایبەتمەندی و کۆنەکردنەوەی زانیاری کەسی"),
                tr(activity,
                        "This application does not collect personal information, names, company details, hardware identifiers, or ASYCUDA login credentials. Your acceptance of these terms is saved only as a simple setting on your phone, and nothing is sent to any external server when you accept. Technical logs are only shared if you choose to submit a report through the 'Report a Technical Issue' screen.",
                        "لا يقوم هذا التطبيق بجمع أي معلومات شخصية أو أسماء للأفراد أو الشركات أو معرفات للأجهزة أو بيانات دخول لنظام أسيكودا. يتم حفظ موافقتك على هذه الشروط كإعداد بسيط داخل هاتفك فقط، ولا يتم إرسال أي بيانات إلى أي جهة خارجية عند الموافقة. ولا تُرسل السجلات الفنية إلا إذا قمت بنفسك بإرسال بلاغ عبر نافذة «الإبلاغ عن مشكلة فنية».",
                        "ئەم بەرنامەیە هیچ زانیارییەکی کەسی، ناوی کەسەکان، زانیاری کۆمپانیا، ناسنامەی ئامێر، یان زانیاری چوونەژوورەوەی ئەسیکۆدا کۆناکاتەوە. پەسەندکردنی ئەم مەرجانە تەنها وەک ڕێکخستنێکی سادە لەناو مۆبایلەکەتدا هەڵدەگیرێت و هیچ زانیارییەک لە کاتی پەسەندکردندا نانێردرێت. تۆمارە تەکنیکییەکان تەنها کاتێک دەنێردرێن کە خۆت ڕاپۆرتێک لە ڕێگەی بەشی «ناردنی ڕاپۆرتی کێشەی تەکنیکی» بنێریت."));

        scrollContent.addView(bodyCard);

        // Mandatory Click-Wrap Agreement Checkbox
        final boolean[] isChecked = { false };

        LinearLayout checkCard = new LinearLayout(activity);
        checkCard.setOrientation(LinearLayout.HORIZONTAL);
        checkCard.setGravity(Gravity.CENTER_VERTICAL);
        checkCard.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        checkCard.setBackground(createRippleCardDrawable(activity, SURFACE_GROUPED_CARD, 10, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams ckLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ckLp.topMargin = dp(activity, 8);
        checkCard.setLayoutParams(ckLp);

        TextView checkBox = new TextView(activity);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setText("");
        checkBox.setTextColor(Color.WHITE);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        checkBox.setTypeface(Typeface.create("sans-serif-bold", Typeface.BOLD));
        checkBox.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 6, GRABBER_COLOR));
        LinearLayout.LayoutParams boxLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        boxLp.setMarginEnd(dp(activity, 10));
        checkCard.addView(checkBox, boxLp);

        TextView checkLabel = new TextView(activity);
        checkLabel.setText(tr(activity,
                "I have read and agree to the Terms & Conditions and the Contractual Liability Waiver pursuant to Iraqi Civil Code No. 40 of 1951 (Article 259/2) and Iraqi Electronic Transactions Law No. 78 of 2012 (Article 18/1).",
                "لقد قرأت الشروط والأحكام أعلاه وأوافق موافقة إلكترونية ملزمة على شرط الإعفاء الكامل من المسؤولية وفقاً للمادة (259/2) من القانون المدني العراقي رقم 40 لسنة 1951 والمادة (18/أولاً) من قانون التوقيع الإلكتروني والمعاملات الإلكترونية رقم 78 لسنة 2012.",
                "مەرج و ڕێساکانی سەرەوەم خوێندووەتەوە و بە شێوەیەکی ئەلیکترۆنی پابەندکەر ڕازیم بە مەرجی بەخشین لە بەرپرسیارێتی یاسایی بەپێی ماددەی (٢٥٩/٢) لە یاسای شارستانی عێراقی ژمارە ٤٠ی ١٩٥١ و ماددەی (١٨/یەکەم) لە یاسای مامەڵە ئەلیکترۆنییەکان ژمارە ٧٨ی ٢٠١٢."));
        checkLabel.setTextColor(TEXT_PRIMARY);
        checkLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.8f);
        checkLabel.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.NORMAL));
        checkLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        checkCard.addView(checkLabel);
        scrollContent.addView(checkCard);

        scroll.addView(scrollContent);
        root.addView(scroll);

        // Action Buttons Row (Decline & Exit | Accept & Continue)
        LinearLayout btnRow = new LinearLayout(activity);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams brLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brLp.topMargin = dp(activity, 4);
        btnRow.setLayoutParams(brLp);

        TextView declineBtn = createCupertinoButton(activity,
                tr(activity, "Decline & Exit", "رفض وخروج", "ڕەتکردنەوە و دەرچوون"),
                false, true, v -> {
                    dialog.dismiss();
                    if (onDeclined != null) onDeclined.run();
                });
        declineBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams decLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        decLp.setMarginEnd(dp(activity, 8));
        declineBtn.setLayoutParams(decLp);
        btnRow.addView(declineBtn);

        TextView acceptBtn = createCupertinoButton(activity,
                tr(activity, "Accept & Continue", "موافقة ومتابعة", "پەسەندکردن و بەردەوامبوون"),
                true, false, null);
        acceptBtn.setGravity(Gravity.CENTER);
        acceptBtn.setAlpha(0.42f);
        LinearLayout.LayoutParams accLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f);
        acceptBtn.setLayoutParams(accLp);
        btnRow.addView(acceptBtn);
        root.addView(btnRow);

        checkCard.setOnClickListener(v -> {
            isChecked[0] = !isChecked[0];
            if (isChecked[0]) {
                checkBox.setText("✓");
                checkBox.setBackground(createCardDrawable(activity, SYSTEM_GREEN, 6, SYSTEM_GREEN));
                checkCard.setBackground(createRippleCardDrawable(activity, SYSTEM_GREEN_TINT, 10, Color.parseColor("#6630D158")));
                acceptBtn.setAlpha(1.0f);
            } else {
                checkBox.setText("");
                checkBox.setBackground(createCardDrawable(activity, SURFACE_INPUT_FIELD, 6, GRABBER_COLOR));
                checkCard.setBackground(createRippleCardDrawable(activity, SURFACE_GROUPED_CARD, 10, SEPARATOR_HAIRLINE));
                acceptBtn.setAlpha(0.42f);
            }
        });

        acceptBtn.setOnClickListener(v -> {
            if (!isChecked[0]) {
                scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
                Toast.makeText(activity, tr(activity,
                        "Please tick the agreement checkbox to continue.",
                        "يرجى تحديد مربع الموافقة على الشروط وإخلاء المسؤولية للمتابعة.",
                        "تکایە نیشانەی ڕەزامەندی دابگرە بۆ بەردەوامبوون."), Toast.LENGTH_SHORT).show();
                return;
            }
            recordTermsAccepted(activity);
            dialog.dismiss();
            if (onAccepted != null) {
                onAccepted.onAccepted();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

    private static void addPolicyClause(Context ctx, LinearLayout parent, String heading, String bodyText) {
        TextView h = new TextView(ctx);
        h.setText(heading);
        h.setTextColor(TEXT_PRIMARY);
        h.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.8f);
        h.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        parent.addView(h);

        TextView b = new TextView(ctx);
        b.setText(bodyText);
        b.setTextColor(TEXT_SECONDARY);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.topMargin = dp(ctx, 2);
        blp.bottomMargin = dp(ctx, 8);
        parent.addView(b, blp);
    }

    private static void addCreditEntry(Context ctx, LinearLayout parent,
                                       String project, String license,
                                       String authors, String description) {
        LinearLayout item = new LinearLayout(ctx);
        item.setOrientation(LinearLayout.VERTICAL);
        int p = dp(ctx, 10);
        item.setPadding(p, dp(ctx, 8), p, dp(ctx, 8));
        item.setBackground(createCardDrawable(ctx,
                SURFACE_INSET_CARD, 10, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 6);
        item.setLayoutParams(lp);

        LinearLayout titleRow = new LinearLayout(ctx);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView pName = new TextView(ctx);
        pName.setText(project);
        pName.setTextColor(TEXT_PRIMARY);
        pName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        pName.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        pName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleRow.addView(pName);

        TextView licBadge = new TextView(ctx);
        licBadge.setText(license);
        licBadge.setTextColor(SYSTEM_BLUE);
        licBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9.5f);
        licBadge.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        licBadge.setPadding(dp(ctx, 7), dp(ctx, 2), dp(ctx, 7), dp(ctx, 2));
        licBadge.setBackground(createCardDrawable(ctx,
                SYSTEM_BLUE_TINT, 7, 0));
        titleRow.addView(licBadge);
        item.addView(titleRow);

        TextView authView = new TextView(ctx);
        authView.setText(authors);
        authView.setTextColor(TEXT_PRIMARY);
        authView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alp.topMargin = dp(ctx, 2);
        item.addView(authView, alp);

        TextView descView = new TextView(ctx);
        descView.setText(description);
        descView.setTextColor(TEXT_SECONDARY);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dlp.topMargin = dp(ctx, 2);
        item.addView(descView, dlp);

        parent.addView(item);
    }

    private static void addSocialButton(Context ctx, GridLayout grid, String label,
                                        boolean isPrimary, Runnable onClick) {
        TextView btn = createCupertinoButton(ctx, label, isPrimary, false, v -> onClick.run());
        btn.setGravity(Gravity.CENTER);
        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.width = 0;
        glp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        int m = dp(ctx, 3);
        glp.setMargins(m, m, m, m);
        grid.addView(btn, glp);
    }

    private static TextView createSectionHeader(Context ctx, String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(TEXT_SECONDARY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.2f);
        tv.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        tv.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 4);
        tv.setLayoutParams(lp);
        return tv;
    }

    private static View createHairlineDivider(Context ctx) {
        View v = new View(ctx);
        v.setBackgroundColor(SEPARATOR_HAIRLINE);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(ctx, 1))));
        return v;
    }

    public static ProgressBar createHorizontalProgressBar(Context ctx) {
        ProgressBar pb = new ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal);
        GradientDrawable bg = createCardDrawable(ctx, SURFACE_INPUT_FIELD, 4, 0);
        GradientDrawable fgShape = createCardDrawable(ctx, SYSTEM_BLUE, 4, 0);
        ClipDrawable fg = new ClipDrawable(fgShape, Gravity.START, ClipDrawable.HORIZONTAL);
        LayerDrawable ld = new LayerDrawable(new Drawable[] { bg, fg });
        ld.setId(0, android.R.id.background);
        ld.setId(1, android.R.id.progress);
        pb.setProgressDrawable(ld);
        pb.setIndeterminateTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        return pb;
    }

    @SuppressLint("NewApi")
    private static Dialog createModalDialog(MainActivity activity, float widthFraction, int maxWidthDp, boolean cancelable) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        final int targetW = Math.min((int) (dm.widthPixels * widthFraction), dp(activity, maxWidthDp));
        Dialog dialog = new Dialog(activity) {
            @Override
            public void setContentView(View view) {
                view.setMinimumWidth(targetW);
                super.setContentView(view, new ViewGroup.LayoutParams(targetW, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            @Override
            public void show() {
                super.show();
                Window win = getWindow();
                if (win != null) {
                    win.setLayout(targetW, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            w.setLayout(targetW, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = w.getAttributes();
            wlp.width = targetW;
            wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            wlp.dimAmount = 0.50f;
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            if (Build.VERSION.SDK_INT >= 31) {
                try {
                    w.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    wlp.setBlurBehindRadius(dp(activity, 24));
                } catch (Exception ignored) { /* Best-effort UI operation */ }
            }
            w.setAttributes(wlp);
        }
        return dialog;
    }

    /**
     * Space-friendly dialog root container that:
     * 1. Caps maximum height to 84% of screen height when content overflows, while wrapping tightly when content fits.
     * 2. Automatically inserts a dedicated, non-overlapping Scroll-Down Arrow Indicator Row directly below
     *    any primary ScrollView (and above any footer row). Because the indicator is its own layout row
     *    outside the ScrollView, it NEVER covers or overlaps any displayed information.
     */
    private static final class DialogRootContainer extends LinearLayout {
        private ScrollView mPrimaryScrollView;
        private final LinearLayout mScrollArrowRow;
        private boolean mHasOverflow = false;
        private boolean mAddingInternalArrow = false;

        DialogRootContainer(Context ctx) {
            super(ctx);
            setOrientation(LinearLayout.VERTICAL);
            setLayoutDirection(isRtl(ctx) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
            int p = dp(ctx, 13);
            setPadding(p, dp(ctx, 8), p, dp(ctx, 11));
            setBackground(createSheetContainerDrawable(ctx, 18));
            setElevation(dp(ctx, 18));

            FrameLayout grabberRow = new FrameLayout(ctx);
            LinearLayout.LayoutParams grLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            grLp.bottomMargin = dp(ctx, 6);
            View grabber = new View(ctx);
            grabber.setBackground(createCardDrawable(ctx, GRABBER_COLOR, 2, 0));
            FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(dp(ctx, 30), dp(ctx, 3), Gravity.CENTER_HORIZONTAL);
            grabberRow.addView(grabber, glp);
            super.addView(grabberRow, -1, grLp);

            mScrollArrowRow = buildScrollArrowRow(ctx);
        }

        private LinearLayout buildScrollArrowRow(Context ctx) {
            LinearLayout bar = new LinearLayout(ctx);
            bar.setOrientation(LinearLayout.HORIZONTAL);
            bar.setGravity(Gravity.CENTER);
            bar.setPadding(dp(ctx, 12), dp(ctx, 4), dp(ctx, 12), dp(ctx, 4));
            bar.setBackground(createRippleCardDrawable(ctx,
                    SYSTEM_BLUE_TINT, 9, Color.parseColor("#550A84FF")));
            bar.setVisibility(View.GONE);
            bar.setClickable(true);
            bar.setFocusable(true);

            ImageView leftArrow = new ImageView(ctx);
            leftArrow.setImageResource(R.drawable.ic_extra_key_arrow_down);
            leftArrow.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
            LinearLayout.LayoutParams laLp = new LinearLayout.LayoutParams(dp(ctx, 13), dp(ctx, 13));
            laLp.setMarginEnd(dp(ctx, 6));
            bar.addView(leftArrow, laLp);

            TextView label = new TextView(ctx);
            label.setText(tr(ctx,
                    "Scroll down for more",
                    "مرر للأسفل لعرض المزيد",
                    "بۆ بینینی زیاتر بکشێنە خوارەوە"));
            label.setTextColor(SYSTEM_BLUE);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
            label.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
            bar.addView(label);

            ImageView rightArrow = new ImageView(ctx);
            rightArrow.setImageResource(R.drawable.ic_extra_key_arrow_down);
            rightArrow.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
            LinearLayout.LayoutParams raLp = new LinearLayout.LayoutParams(dp(ctx, 13), dp(ctx, 13));
            raLp.setMarginStart(dp(ctx, 6));
            bar.addView(rightArrow, raLp);

            bar.setOnClickListener(v -> {
                if (mPrimaryScrollView != null) {
                    int step = Math.max(dp(ctx, 110), (int) (mPrimaryScrollView.getHeight() * 0.72f));
                    mPrimaryScrollView.smoothScrollBy(0, step);
                }
            });
            return bar;
        }

        @Override
        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            if (!mAddingInternalArrow && mPrimaryScrollView == null && child instanceof ScrollView) {
                mPrimaryScrollView = (ScrollView) child;
                LinearLayout.LayoutParams scLp;
                if (params instanceof LinearLayout.LayoutParams) {
                    scLp = (LinearLayout.LayoutParams) params;
                } else {
                    scLp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                scLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                scLp.weight = 1f;
                super.addView(child, index, scLp);

                mAddingInternalArrow = true;
                try {
                    LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    arrowLp.topMargin = dp(getContext(), 5);
                    arrowLp.bottomMargin = dp(getContext(), 2);
                    super.addView(mScrollArrowRow, -1, arrowLp);
                } finally {
                    mAddingInternalArrow = false;
                }
                attachScrollMonitor(mPrimaryScrollView);
                return;
            }
            super.addView(child, index, params);
        }

        private void attachScrollMonitor(ScrollView sv) {
            sv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> updateScrollArrowState());
            sv.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> postUpdateScrollArrow());
            sv.getViewTreeObserver().addOnGlobalLayoutListener(this::updateScrollArrowState);
            sv.post(this::updateScrollArrowState);
        }

        private void postUpdateScrollArrow() {
            post(this::updateScrollArrowState);
        }

        private void updateScrollArrowState() {
            if (mPrimaryScrollView == null || mScrollArrowRow == null) return;
            View inner = mPrimaryScrollView.getChildCount() > 0 ? mPrimaryScrollView.getChildAt(0) : null;
            int svH = mPrimaryScrollView.getHeight();
            if (inner == null || svH <= 0) return;

            int totalContentH = inner.getHeight()
                    + mPrimaryScrollView.getPaddingTop()
                    + mPrimaryScrollView.getPaddingBottom();
            if (totalContentH > svH + dp(getContext(), 4)) {
                mHasOverflow = true;
            }

            if (!mHasOverflow) {
                if (mScrollArrowRow.getVisibility() != View.GONE) {
                    mScrollArrowRow.setVisibility(View.GONE);
                }
                return;
            }

            boolean canScrollDownMore = mPrimaryScrollView.canScrollVertically(1);
            if (canScrollDownMore) {
                if (mScrollArrowRow.getVisibility() != View.VISIBLE) {
                    mScrollArrowRow.setVisibility(View.VISIBLE);
                }
                mScrollArrowRow.setAlpha(1.0f);
                mScrollArrowRow.setClickable(true);
            } else {
                // Keep layout slot reserved (INVISIBLE) so ScrollView height never jitters when reaching the bottom
                if (mScrollArrowRow.getVisibility() == View.GONE) {
                    mScrollArrowRow.setVisibility(View.INVISIBLE);
                } else {
                    mScrollArrowRow.setVisibility(View.INVISIBLE);
                }
                mScrollArrowRow.setClickable(false);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxH = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.84f);
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            int size = MeasureSpec.getSize(heightMeasureSpec);
            int cappedSpec;
            if (mode == MeasureSpec.UNSPECIFIED) {
                cappedSpec = MeasureSpec.makeMeasureSpec(maxH, MeasureSpec.AT_MOST);
            } else {
                cappedSpec = MeasureSpec.makeMeasureSpec(Math.min(size, maxH), mode);
            }
            super.onMeasure(widthMeasureSpec, cappedSpec);
            postUpdateScrollArrow();
        }
    }

    private static LinearLayout createDialogContainer(Context ctx) {
        return new DialogRootContainer(ctx);
    }

    public static Drawable createSheetContainerDrawable(Context ctx, int radiusDp) {
        GradientDrawable base = new GradientDrawable();
        base.setColor(SURFACE_SHEET);
        base.setCornerRadius(dp(ctx, radiusDp));
        base.setStroke(Math.max(1, dp(ctx, 1)), SEPARATOR_HAIRLINE);
        return base;
    }

    public static Drawable createGlassContainerDrawable(Context ctx, int radiusDp) {
        return createSheetContainerDrawable(ctx, radiusDp);
    }

    public static GradientDrawable createGlassGradientDrawable(Context ctx, int topColor, int bottomColor, int radiusDp, int strokeColor) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { topColor, bottomColor });
        gd.setCornerRadius(dp(ctx, radiusDp));
        if (strokeColor != 0) {
            gd.setStroke(Math.max(1, dp(ctx, 1)), strokeColor);
        }
        return gd;
    }

    public static RippleDrawable createGlassRippleDrawable(Context ctx, int topColor, int bottomColor, int radiusDp, int strokeColor) {
        GradientDrawable content = createGlassGradientDrawable(ctx, topColor, bottomColor, radiusDp, strokeColor);
        GradientDrawable mask = createCardDrawable(ctx, Color.WHITE, radiusDp, 0);
        return new RippleDrawable(ColorStateList.valueOf(RIPPLE_HIGHLIGHT), content, mask);
    }

    public static Drawable createCupertinoButtonDrawable(Context ctx, boolean isPrimary, boolean isDestructive) {
        int baseFill = isPrimary ? SYSTEM_BLUE : SYSTEM_GRAY_FILL;
        int fill = isDestructive ? SYSTEM_RED_TINT : baseFill;
        int stroke = isDestructive ? Color.parseColor("#4DFF453A") : 0;
        return createRippleCardDrawable(ctx, fill, 9, stroke);
    }

    public static TextView createCupertinoButton(Context ctx, String label, boolean isPrimary, boolean isDestructive, View.OnClickListener listener) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(isDestructive ? SYSTEM_RED : TEXT_PRIMARY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.8f);
        tv.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        tv.setPadding(dp(ctx, 12), dp(ctx, 7), dp(ctx, 12), dp(ctx, 7));
        tv.setBackground(createCupertinoButtonDrawable(ctx, isPrimary, isDestructive));
        tv.setClickable(true);
        tv.setFocusable(true);
        if (listener != null) tv.setOnClickListener(listener);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    public static FrameLayout createIconButton(Context ctx, int iconResId, String tooltipText,
                                               boolean isPrimary, boolean isDestructive,
                                               View.OnClickListener listener) {
        FrameLayout box = new FrameLayout(ctx);
        box.setBackground(createCupertinoButtonDrawable(ctx, isPrimary, isDestructive));
        box.setClickable(true);
        box.setFocusable(true);
        if (tooltipText != null && !tooltipText.isEmpty()) {
            box.setContentDescription(tooltipText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                box.setTooltipText(tooltipText);
            }
        }
        if (listener != null) box.setOnClickListener(listener);
        int size = dp(ctx, 32);
        box.setLayoutParams(new LinearLayout.LayoutParams(size, size));

        ImageView icon = new ImageView(ctx);
        icon.setImageResource(iconResId);
        icon.setImageTintList(ColorStateList.valueOf(isDestructive ? SYSTEM_RED : TEXT_PRIMARY));
        int icSize = dp(ctx, 17);
        box.addView(icon, new FrameLayout.LayoutParams(icSize, icSize, Gravity.CENTER));
        return box;
    }

    /** Updates an icon button (created by createIconButton) with a new icon, tooltip and accent style. */
    public static void setIconButtonState(Context ctx, FrameLayout box, int iconResId, String tooltipText, boolean isPrimary) {
        if (box == null) return;
        box.setBackground(createCupertinoButtonDrawable(ctx, isPrimary, false));
        if (tooltipText != null) {
            box.setContentDescription(tooltipText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                box.setTooltipText(tooltipText);
            }
        }
        if (box.getChildCount() > 0 && box.getChildAt(0) instanceof ImageView) {
            ((ImageView) box.getChildAt(0)).setImageResource(iconResId);
        }
    }

    public static TextView createGlassPillButton(Context ctx, String label, boolean isPrimaryAccent, View.OnClickListener listener) {
        return createCupertinoButton(ctx, label, isPrimaryAccent, false, listener);
    }

    public static GradientDrawable createCardDrawable(Context ctx, int fillColor, int radiusDp, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(ctx, radiusDp));
        if (strokeColor != 0) {
            gd.setStroke(Math.max(1, dp(ctx, 1)), strokeColor);
        }
        return gd;
    }

    public static RippleDrawable createRippleCardDrawable(Context ctx, int fillColor, int radiusDp, int strokeColor) {
        GradientDrawable content = createCardDrawable(ctx, fillColor, radiusDp, strokeColor);
        GradientDrawable mask = createCardDrawable(ctx, Color.WHITE, radiusDp, 0);
        return new RippleDrawable(ColorStateList.valueOf(RIPPLE_HIGHLIGHT), content, mask);
    }

    public static void showFilePickerBridgePromptModal(MainActivity activity, int vaultFileCount,
                                                       Runnable onImportFromPhone, Runnable onUseExistingVault) {
        if (activity == null || activity.isFinishing()) return;
        boolean compact = isCompactWidth(activity);
        Dialog dlg = createModalDialog(activity, compact ? 0.82f : 0.64f, 350, true);
        LinearLayout outer = createDialogContainer(activity);
        int p = dp(activity, 12);
        outer.setPadding(p, dp(activity, 8), p, p);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout sheet = new LinearLayout(activity);
        sheet.setOrientation(LinearLayout.VERTICAL);

        LinearLayout topRow = new LinearLayout(activity);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        trLp.bottomMargin = dp(activity, 6);
        topRow.setLayoutParams(trLp);

        ImageView folderIcon = new ImageView(activity);
        folderIcon.setImageResource(R.drawable.ic_folder);
        folderIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams fiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        fiLp.setMarginEnd(dp(activity, 8));
        topRow.addView(folderIcon, fiLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Choose a File for ASYCUDA",
                "اختيار ملف لنظام أسيكودا",
                "هەڵبژاردنی پەڕگە بۆ ئەسیکۆدا"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topRow.addView(title);

        topRow.addView(createIconButton(activity, R.drawable.ic_action_close,
                tr(activity, "Use Vault", "ملفات الخزنة", "ناو قاسە"),
                false, false, v -> {
                    dlg.dismiss();
                    if (onUseExistingVault != null) onUseExistingVault.run();
                }));
        sheet.addView(topRow);

        TextView body = new TextView(activity);
        body.setText(tr(activity,
                "Import a new file from your phone into the Vault, or select from " + vaultFileCount + " file(s) already in the Vault.",
                "استورد ملفاً جديداً من هاتفك إلى الخزنة، أو اختر من بين (" + vaultFileCount + ") ملفات موجودة بالخزنة.",
                "پەڕگەیەکی نوێ لە مۆبایلەوە هاوردە بکە، یان لە (" + vaultFileCount + ") پەڕگەی ناو قاسەکە هەڵبژێرە."));
        body.setTextColor(TEXT_SECONDARY);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        body.setLineSpacing(dp(activity, 2), 1.0f);
        LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyLp.bottomMargin = dp(activity, 12);
        sheet.addView(body, bodyLp);

        LinearLayout btnRow = new LinearLayout(activity);
        btnRow.setOrientation(compact ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView useVaultBtn = createCupertinoButton(activity, tr(activity,
                "Use Vault (" + vaultFileCount + ")",
                "ملفات الخزنة (" + vaultFileCount + ")",
                "ناو قاسە (" + vaultFileCount + ")"), false, false, v -> {
            dlg.dismiss();
            if (onUseExistingVault != null) onUseExistingVault.run();
        });
        useVaultBtn.setGravity(Gravity.CENTER);
        useVaultBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        useVaultBtn.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));

        TextView importBtn = createCupertinoButton(activity, tr(activity,
                "Import from Phone",
                "استيراد من الهاتف",
                "هاوردەکردن لە مۆبایل"), true, false, v -> {
            dlg.dismiss();
            if (onImportFromPhone != null) onImportFromPhone.run();
        });
        importBtn.setGravity(Gravity.CENTER);
        importBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        importBtn.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));

        if (compact) {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.bottomMargin = dp(activity, 8);
            btnRow.addView(importBtn, lp1);
            btnRow.addView(useVaultBtn, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp1.setMarginEnd(dp(activity, 8));
            btnRow.addView(useVaultBtn, lp1);
            btnRow.addView(importBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.15f));
        }

        sheet.addView(btnRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scroll.addView(sheet);
        outer.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        dlg.setContentView(outer);
        dlg.show();
    }

    private static boolean isCompactWidth(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        return cfg.screenWidthDp < 480;
    }

    public static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
