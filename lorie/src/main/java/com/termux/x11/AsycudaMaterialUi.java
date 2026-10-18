package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class AsycudaMaterialUi {

    private static final String PREFS_NAME = "asycuda_ui_prefs";
    private static final String KEY_APP_LANG = "app_lang";
    private static final String KEY_AUTO_KEYBOARD = "auto_show_keyboard";

    // Apple Human Interface Guidelines (iOS / macOS Dark Mode System Palette)
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
        void onToggleAssistKeys();
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
        } catch (Throwable ignored) {}
        return "v3.7.0";
    }

    public static void showControlCenter(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.88f, 700, true);
        LinearLayout root = createDialogContainer(activity);

        // Header Row
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(activity, 40), dp(activity, 40));
        logoLp.setMarginEnd(dp(activity, 12));
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
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.5f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Control Center • " + getAppVersionLabel(activity),
                "لوحة التحكم • " + getAppVersionLabel(activity),
                "ناوەندی کۆنترۆڵ • " + getAppVersionLabel(activity)));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        TextView langPill = createCupertinoButton(activity, getCurrentLanguageDisplayName(activity), false, false, v -> {
            dialog.dismiss();
            showLanguageModal(activity, cb);
        });
        LinearLayout.LayoutParams langLp = (LinearLayout.LayoutParams) langPill.getLayoutParams();
        langLp.setMarginEnd(dp(activity, 8));
        header.addView(langPill);

        TextView closeBtn = createCupertinoButton(activity,
                tr(activity, "Done", "تم", "تەواو"),
                true, false, v -> dialog.dismiss());
        header.addView(closeBtn);
        root.addView(header);

        // Hairline Divider
        root.addView(createHairlineDivider(activity));

        // Scrollable 2-Column Grid
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 8);
        scroll.setLayoutParams(scrollLp);

        GridLayout grid = new GridLayout(activity);
        int cols = isCompactWidth(activity) ? 1 : 2;
        grid.setColumnCount(cols);
        grid.setUseDefaultMargins(false);

        boolean autoKbd = isAutoKeyboardEnabled(activity);
        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_keyboard,
                tr(activity, "Auto-Show Keyboard", "إظهار الكيبورد تلقائياً", "پیشاندانی خۆکاری تەختەکلیل"),
                autoKbd
                        ? tr(activity, "Opens automatically on text fields", "يظهر تلقائياً عند النقر على الحقول", "خۆکارانە لەسەر خانەکان دەکرێتەوە")
                        : tr(activity, "Disabled • Press Back to open manually", "معطّل • اضغط رجوع لإظهاره يدوياً", "ناچالاکە • دوگمەی گەڕانەوە بەکاربهێنە"),
                autoKbd ? tr(activity, "ON", "مفعّل", "چالاک") : tr(activity, "OFF", "معطّل", "ناچالاک"),
                autoKbd, false, () -> {
                    boolean next = !isAutoKeyboardEnabled(activity);
                    setAutoKeyboardEnabled(activity, next);
                    cb.onToggleAutoKeyboard(next);
                    dialog.dismiss();
                    showControlCenter(activity, cb);
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_assist_keys,
                tr(activity, "Function & Arrow Keys", "أزرار الاختصار والأسهم", "دوگمەکانی فرمانی و تیرەکان"),
                tr(activity, "Show or hide TAB, CTRL, ESC toolbar", "إظهار أو إخفاء شريط TAB و CTRL والأسهم", "پیشاندانی شریتی TAB و CTRL و تیرەکان"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onToggleAssistKeys();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_cloud_sync,
                tr(activity, "Check for Updates", "فحص التحديثات", "پشکنینی نوێکردنەوەکان"),
                tr(activity, "Verify app releases and MOF packages", "فحص إصدارات التطبيق وملفات وزارة المالية", "پشکنینی وەشانی بەرنامە و پەڕگەکانی وەزارەت"),
                null, true, false, () -> {
                    dialog.dismiss();
                    cb.onCheckUpdates();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_documents,
                tr(activity, "Printed PDFs & Files", "ملفات PDF والمستندات", "پەڕگەکانی PDF و چاپکراوەکان"),
                tr(activity, "Open or share declarations and receipts", "فتح أو مشاركة التصاريح والوصلات الكمركية", "کردنەوە یان هاوبەشکردنی بەڵگەنامە گومرگییەکان"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onOpenFiles();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_display,
                tr(activity, "Display & Input Settings", "إعدادات العرض والتحكم", "ڕێکخستنی شاشە و کۆنترۆڵ"),
                tr(activity, "Resolution, screen scaling and pointer", "ضبط أبعاد الشاشة والدقة وحركة المؤشر", "ڕێکخستنی قەبارەی شاشە و وردی و نیشاندەر"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onOpenDisplaySettings();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_language,
                tr(activity, "App Language", "لغة التطبيق", "زمانی بەرنامە"),
                tr(activity, "English • العربية • کوردی", "العربية • English • کوردی", "کوردی • العربية • English"),
                getCurrentLanguageDisplayName(activity), false, false, () -> {
                    dialog.dismiss();
                    showLanguageModal(activity, cb);
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_diagnostics,
                tr(activity, "Report Issue & Feedback", "إرسال ملاحظات أو بلاغ", "ناردنی تێبینی و ڕاپۆرتی کێشە"),
                tr(activity, "Send description, screenshot & logs", "إرسال وصف المشكلة مع صورة وسجل التشخيص", "ناردنی وەسفی کێشە لەگەڵ وێنە و تۆمارەکان"),
                null, true, false, () -> {
                    dialog.dismiss();
                    cb.onOpenFeedbackModal();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_restart,
                tr(activity, "Restart Session", "إعادة تشغيل الجلسة", "دووبارە پێکردنەوەی دانیشتن"),
                tr(activity, "Reconnect and reload ASYCUDA client", "إعادة الاتصال وتشغيل نظام أسيكودا من جديد", "پەیوەندیکردنەوە و بارکردنەوەی سیستەمی ئەسیکۆدا"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onRestartSession();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_info,
                tr(activity, "About & Licences", "حول التطبيق والتراخيص", "دەربارەی بەرنامە و مۆڵەتەکان"),
                tr(activity, "Version info and open-source credits", "معلومات الإصدار والمكتبات مفتوحة المصدر", "زانیاری وەشان و سەرچاوە کراوەکان"),
                null, false, false, () -> {
                    dialog.dismiss();
                    cb.onOpenAbout();
                });

        addMenuTileWithBadge(activity, grid, R.drawable.ic_menu_exit,
                tr(activity, "Exit Application", "خروج من التطبيق", "دەرچوون لە بەرنامە"),
                tr(activity, "Close session and stop background service", "إغلاق الجلسة وإيقاف الخدمة في الخلفية نهائياً", "داخستنی دانیشتن و ڕاگرتنی بەرنامە بەتەواوی"),
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
     * Unified Blocking "Check for Updates" Modal (Requirement #5):
     * Pauses user work with a clean Apple-style modal checking both GitHub App Releases
     * and MOF Government JARs with smooth progress bars.
     */
    public static UpdateCenterController showUpdateCenterModal(MainActivity activity, Runnable onCloseWhenUpToDate) {
        Dialog dialog = createModalDialog(activity, 0.82f, 580, false);
        LinearLayout root = createDialogContainer(activity);

        // Header
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_cloud_sync);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24));
        icLp.setMarginEnd(dp(activity, 10));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Software & System Update",
                "تحديث التطبيق وملفات النظام",
                "نوێکردنەوەی بەرنامە و پەڕگەکانی سیستەم"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        titleCol.addView(title);

        TextView sub = new TextView(activity);
        sub.setText(tr(activity,
                "Please wait while updates are verified...",
                "يرجى الانتظار ريثما يتم التحقق من التحديثات...",
                "تکایە چاوەڕوان بن تا پشکنینی نوێکردنەوەکان تەواو دەبێت..."));
        sub.setTextColor(TEXT_SECONDARY);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        titleCol.addView(sub);
        header.addView(titleCol);
        root.addView(header);
        root.addView(createHairlineDivider(activity));

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
                tr(activity, "APPLICATION RELEASE (GITHUB)", "تحديث التطبيق (GITHUB)", "وەشانی بەرنامە (GITHUB)"));
        appCard.addView(appHeader);

        TextView appStatus = new TextView(activity);
        appStatus.setText(tr(activity,
                "Checking for new app version...",
                "جاري التحقق من وجود إصدار جديد للتطبيق...",
                "پشکنین بۆ وەشانی نوێی بەرنامە..."));
        appStatus.setTextColor(TEXT_PRIMARY);
        appStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        appStatus.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
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

        root.addView(appCard);

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
                tr(activity, "MINISTRY OF FINANCE PACKAGES (ASY.MOF.GOV.IQ)", "حزم نظام وزارة المالية (ASY.MOF.GOV.IQ)", "پەڕگەکانی سێرڤەری وەزارەتی دارایی"));
        mofCard.addView(mofHeader);

        TextView mofStatus = new TextView(activity);
        mofStatus.setText(tr(activity,
                "Connecting to Ministry of Finance server...",
                "جاري الاتصال بخادم وزارة المالية...",
                "پەیوەندیکردن بە سێرڤەری وەزارەتی دارایی..."));
        mofStatus.setTextColor(TEXT_PRIMARY);
        mofStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        mofStatus.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
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

        root.addView(mofCard);

        // Footer Action Row
        LinearLayout footer = new LinearLayout(activity);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams footLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footLp.topMargin = dp(activity, 14);
        footer.setLayoutParams(footLp);

        TextView actionBtn = createCupertinoButton(activity,
                tr(activity, "Checking Updates...", "جاري الفحص...", "پشکنین..."),
                false, false, null);
        actionBtn.setEnabled(false);
        actionBtn.setAlpha(0.5f);
        footer.addView(actionBtn);
        root.addView(footer);

        dialog.setContentView(root);
        dialog.show();

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
                            "New version available: " + versionTag,
                            "يتوفر إصدار جديد للتطبيق: " + versionTag,
                            "وەشانی نوێ بەردەستە: " + versionTag));
                    appStatus.setTextColor(SYSTEM_GREEN);
                    if (notesSummary != null && !notesSummary.isEmpty()) {
                        appNotes.setText(notesSummary);
                        appNotes.setVisibility(View.VISIBLE);
                    }
                    appProgress.setVisibility(View.GONE);
                    installAppBtn.setVisibility(View.VISIBLE);
                    installAppBtn.setOnClickListener(v -> {
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
                    if (updatedJarsCount > 0) {
                        sub.setText(tr(activity,
                                "Updates downloaded • Restarting session...",
                                "تم تحميل التحديثات • جاري إعادة تشغيل الجلسة...",
                                "نوێکردنەوەکان دابەزێنران • دووبارە پێکردنەوەی دانیشتن..."));
                        actionBtn.setText(tr(activity, "Restarting Session...", "جاري إعادة التشغيل...", "دووبارە پێکردنەوە..."));
                        actionBtn.setBackground(createCupertinoButtonDrawable(activity, true, false));
                        actionBtn.setAlpha(1.0f);
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            try { dialog.dismiss(); } catch (Throwable ignored) {}
                            if (onRestartNow != null) onRestartNow.run();
                        }, 1200);
                    } else {
                        dialog.setCancelable(true);
                        sub.setText(tr(activity,
                                "Verification complete • Ready to continue",
                                "اكتمل الفحص • يمكنك متابعة العمل",
                                "پشکنین تەواو بوو • ئامادەیە بۆ کارکردن"));
                        actionBtn.setEnabled(true);
                        actionBtn.setAlpha(1.0f);
                        actionBtn.setText(tr(activity, "Continue Working", "متابعة العمل", "بەردەوامبوون لە کار"));
                        actionBtn.setBackground(createCupertinoButtonDrawable(activity, true, false));
                        actionBtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            if (onCloseWhenUpToDate != null) onCloseWhenUpToDate.run();
                        });
                    }
                });
            }
        };
    }

    /**
     * Full-Fledged Feedback & Bug Report Modal (Requirement #6):
     * Allows user to write a description, capture current screen or pick an image (image/* only),
     * and auto-uploads the description + image + Diagnostics ZIP directly to Sentry in the background.
     */
    public interface FeedbackSubmitHandler {
        void onCaptureScreenRequested(FeedbackModalController controller, Dialog feedbackDialog);
        void onPickImageRequested(FeedbackModalController controller);
        void onSubmitToSentry(String description, File attachedImage, FeedbackModalController controller, Dialog feedbackDialog);
        void onExportZipLocally(String description, File attachedImage, Dialog feedbackDialog);
    }

    public static FeedbackModalController showFeedbackModal(MainActivity activity, FeedbackSubmitHandler handler) {
        Dialog dialog = createModalDialog(activity, 0.86f, 640, true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        LinearLayout root = createDialogContainer(activity);

        // Header
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 10));

        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.ic_menu_diagnostics);
        icon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24));
        icLp.setMarginEnd(dp(activity, 10));
        header.addView(icon, icLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Report Issue & Send Feedback",
                "إرسال بلاغ عن مشكلة أو ملاحظات",
                "ناردنی ڕاپۆرتی کێشە و تێبینی"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.5f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText(tr(activity,
                "Sent directly to the developer with diagnostic logs • No sign-in required",
                "يُرسل مباشرة للمطور مع سجلات التشخيص • بدون الحاجة لتسجيل دخول",
                "ڕاستەوخۆ بۆ گەشەپێدەر دەنێردرێت لەگەڵ تۆمارەکان • بەبێ چوونەژوورەوە"));
        subtitle.setTextColor(TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        TextView closeBtn = createCupertinoButton(activity,
                tr(activity, "Cancel", "إلغاء", "پاشگەزبوونەوە"),
                false, false, v -> dialog.dismiss());
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

        // 1. Description Input
        body.addView(createSectionHeader(activity,
                tr(activity, "ISSUE DESCRIPTION OR FEEDBACK", "وصف المشكلة أو الملاحظة", "وەسفی کێشە یان تێبینی")));

        EditText descInput = new EditText(activity);
        descInput.setHint(tr(activity,
                "Describe what happened, which window or button you used, or your suggestion...",
                "اكتب تفاصيل المشكلة أو النافذة التي ظهرت فيها أو مقترحك لتحسين التطبيق...",
                "وردەکاری کێشەکە یان پێشنیارەکەت لێرە بنووسە..."));
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

        // 2. Screenshot / Image Attachment Card
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
                "Screenshot / Image (Optional)",
                "صورة الشاشة (اختياري)",
                "وێنەی شاشە (ئارەزوومەندانە)"));
        imgTitle.setTextColor(TEXT_PRIMARY);
        imgTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        imgTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        imgTextCol.addView(imgTitle);

        TextView imgSub = new TextView(activity);
        imgSub.setText(tr(activity,
                "No image attached • PNG or JPG only",
                "لم يتم إرفاق صورة • يقبل الصور فقط",
                "هیچ وێنەیەک هاوپێچ نەکراوە • تەنها وێنە"));
        imgSub.setTextColor(TEXT_SECONDARY);
        imgSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        imgTextCol.addView(imgSub);
        imgCard.addView(imgTextCol);

        final File[] currentImageHolder = new File[] { null };
        final FeedbackModalController[] controllerRef = new FeedbackModalController[1];

        TextView captureBtn = createCupertinoButton(activity,
                tr(activity, "Capture Screen", "التقاط الشاشة", "وێنەگرتنی شاشە"),
                false, false, v -> {
                    if (handler != null && controllerRef[0] != null) {
                        handler.onCaptureScreenRequested(controllerRef[0], dialog);
                    }
                });
        LinearLayout.LayoutParams capLp = (LinearLayout.LayoutParams) captureBtn.getLayoutParams();
        capLp.setMarginEnd(dp(activity, 6));
        imgCard.addView(captureBtn);

        TextView pickBtn = createCupertinoButton(activity,
                tr(activity, "Choose Image", "اختيار صورة", "هەڵبژاردنی وێنە"),
                false, false, v -> {
                    if (handler != null && controllerRef[0] != null) {
                        handler.onPickImageRequested(controllerRef[0]);
                    }
                });
        imgCard.addView(pickBtn);

        TextView removeImgBtn = createCupertinoButton(activity,
                tr(activity, "Remove", "حذف", "لابردن"),
                false, true, v -> {
                    currentImageHolder[0] = null;
                    thumbView.setVisibility(View.GONE);
                    imgSub.setText(tr(activity,
                            "No image attached • PNG or JPG only",
                            "لم يتم إرفاق صورة • يقبل الصور فقط",
                            "هیچ وێنەیەک هاوپێچ نەکراوە • تەنها وێنە"));
                    imgSub.setTextColor(TEXT_SECONDARY);
                    v.setVisibility(View.GONE);
                });
        removeImgBtn.setVisibility(View.GONE);
        LinearLayout.LayoutParams remLp = (LinearLayout.LayoutParams) removeImgBtn.getLayoutParams();
        remLp.setMarginStart(dp(activity, 6));
        imgCard.addView(removeImgBtn);

        body.addView(imgCard);

        // 3. Automatic Diagnostics Bundle Info Row
        LinearLayout zipInfoRow = new LinearLayout(activity);
        zipInfoRow.setOrientation(LinearLayout.VERTICAL);
        zipInfoRow.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));
        zipInfoRow.setBackground(createCardDrawable(activity, SURFACE_INSET_CARD, 12, SEPARATOR_HAIRLINE));

        TextView zipInfoText = new TextView(activity);
        zipInfoText.setText(tr(activity,
                "✓ Diagnostic archive (asycuda.log, device specs, JAR inventory & system logcat) is attached automatically.",
                "✓ يتم إرفاق ملف التشخيص المضغوط (سجل أسيكودا، مواصفات الجهاز، وقائمة الملفات) تلقائياً مع البلاغ.",
                "✓ پەڕگەی پشکنینی سیستەم (تۆماری ئەسیکۆدا و زانیاری ئامێرەکە) بە شێوەی خۆکار هاوپێچ دەکرێت."));
        zipInfoText.setTextColor(TEXT_SECONDARY);
        zipInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        zipInfoRow.addView(zipInfoText);

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

        // Footer Buttons
        LinearLayout footer = new LinearLayout(activity);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams footLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footLp.topMargin = dp(activity, 12);
        footer.setLayoutParams(footLp);

        TextView exportZipBtn = createCupertinoButton(activity,
                tr(activity, "Share ZIP...", "مشاركة ملف ZIP...", "هاوبەشکردنی ZIP..."),
                false, false, v -> {
                    String desc = descInput.getText() != null ? descInput.getText().toString().trim() : "";
                    if (handler != null) {
                        handler.onExportZipLocally(desc, currentImageHolder[0], dialog);
                    }
                });
        LinearLayout.LayoutParams exLp = (LinearLayout.LayoutParams) exportZipBtn.getLayoutParams();
        exLp.setMarginEnd(dp(activity, 8));
        footer.addView(exportZipBtn);

        TextView submitBtn = createCupertinoButton(activity,
                tr(activity, "Submit Report", "إرسال البلاغ للمطور", "ناردنی ڕاپۆرت"),
                true, false, null);
        submitBtn.setOnClickListener(v -> {
            String desc = descInput.getText() != null ? descInput.getText().toString().trim() : "";
            if (desc.isEmpty()) {
                descInput.setError(tr(activity,
                        "Please enter a brief description",
                        "يرجى كتابة وصف مختصر للمشكلة",
                        "تکایە کورتەیەک لەسەر کێشەکە بنووسە"));
                descInput.requestFocus();
                return;
            }
            if (handler != null && controllerRef[0] != null) {
                handler.onSubmitToSentry(desc, currentImageHolder[0], controllerRef[0], dialog);
            }
        });
        footer.addView(submitBtn);
        root.addView(footer);

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
                        } catch (Throwable ignored) {}
                        long kb = Math.max(1, imageFile.length() / 1024);
                        imgSub.setText("✓ " + displayName + " (" + kb + " KB)");
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
                    exportZipBtn.setEnabled(!uploading);
                    submitBtn.setAlpha(uploading ? 0.5f : 1.0f);
                    if (statusMessage != null) {
                        zipInfoText.setText(statusMessage);
                        zipInfoText.setTextColor(success ? SYSTEM_GREEN : (uploading ? SYSTEM_BLUE : TEXT_PRIMARY));
                    }
                });
            }
        };

        dialog.setContentView(root);
        dialog.show();
        return controllerRef[0];
    }

    public static void showLanguageModal(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.78f, 500, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_language);
        headerIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        hiLp.setMarginEnd(dp(activity, 10));
        header.addView(headerIcon, hiLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity, "App Language", "لغة التطبيق", "زمانی بەرنامە"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createCupertinoButton(activity,
                tr(activity, "Done", "تم", "تەواو"),
                false, false, v -> dialog.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        String current = getAppLanguage(activity);

        LinearLayout list = new LinearLayout(activity);
        list.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listLp.topMargin = dp(activity, 10);
        list.setLayoutParams(listLp);

        addLanguageOptionCard(activity, list, "en",
                "English",
                "Default interface language",
                "en".equals(current), () -> {
                    setAppLanguage(activity, "en");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        addLanguageOptionCard(activity, list, "ar",
                "العربية",
                "واجهة التطبيق باللغة العربية — جمهورية العراق",
                "ar".equals(current), () -> {
                    setAppLanguage(activity, "ar");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        addLanguageOptionCard(activity, list, "ku",
                "کوردی (سۆرانی)",
                "ڕووکاری بەرنامە بە زمانی کوردی — هەرێمی کوردستان و عێراق",
                "ku".equals(current), () -> {
                    setAppLanguage(activity, "ku");
                    dialog.dismiss();
                    if (cb != null) cb.onLanguageChanged();
                    showControlCenter(activity, cb);
                });

        root.addView(list);
        dialog.setContentView(root);
        dialog.show();
    }

    private static void addLanguageOptionCard(Context ctx, LinearLayout parent,
                                              String code, String titleText, String subText,
                                              boolean isSelected, Runnable onSelect) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dp(ctx, 14);
        row.setPadding(pad, dp(ctx, 12), pad, dp(ctx, 12));
        row.setBackground(createRippleCardDrawable(ctx,
                SURFACE_GROUPED_CARD,
                14,
                isSelected ? SYSTEM_BLUE : SEPARATOR_HAIRLINE));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> onSelect.run());
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.bottomMargin = dp(ctx, 8);
        row.setLayoutParams(rlp);

        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(ctx);
        t.setText(titleText);
        t.setTextColor(TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        textCol.addView(t);

        TextView s = new TextView(ctx);
        s.setText(subText);
        s.setTextColor(TEXT_SECONDARY);
        s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        textCol.addView(s);
        row.addView(textCol);

        TextView badge = new TextView(ctx);
        badge.setText(isSelected
                ? tr(ctx, "✓ Selected", "✓ مفعّل", "✓ هەڵبژێردراو")
                : code.toUpperCase(Locale.US));
        badge.setTextColor(isSelected ? SYSTEM_BLUE : TEXT_SECONDARY);
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        badge.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
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
        int pad = dp(ctx, 13);
        card.setPadding(pad, dp(ctx, 11), pad, dp(ctx, 11));
        card.setBackground(createRippleCardDrawable(ctx,
                SURFACE_GROUPED_CARD, 14,
                isDestructive ? Color.parseColor("#4DFF453A") : SEPARATOR_HAIRLINE));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> onClick.run());

        int badgeBgColor = isDestructive ? SYSTEM_RED_TINT
                : (isAccentIcon ? SYSTEM_BLUE_TINT : SYSTEM_GRAY_FILL);
        int iconTint = isDestructive ? SYSTEM_RED
                : (isAccentIcon ? SYSTEM_BLUE : TEXT_PRIMARY);

        FrameLayout badgeBox = new FrameLayout(ctx);
        badgeBox.setBackground(createCardDrawable(ctx, badgeBgColor, 10, 0));
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(dp(ctx, 38), dp(ctx, 38));
        badgeLp.setMarginEnd(dp(ctx, 11));

        ImageView iconView = new ImageView(ctx);
        iconView.setImageResource(iconResId);
        iconView.setImageTintList(ColorStateList.valueOf(iconTint));
        FrameLayout.LayoutParams icLp = new FrameLayout.LayoutParams(dp(ctx, 20), dp(ctx, 20), Gravity.CENTER);
        badgeBox.addView(iconView, icLp);
        card.addView(badgeBox, badgeLp);

        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(ctx);
        t.setText(titleText);
        t.setTextColor(isDestructive ? SYSTEM_RED : TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        textCol.addView(t);

        TextView d = new TextView(ctx);
        d.setText(descText);
        d.setTextColor(TEXT_SECONDARY);
        d.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        textCol.addView(d);

        card.addView(textCol);

        if (statusBadgeText != null && !statusBadgeText.isEmpty()) {
            TextView pill = new TextView(ctx);
            pill.setText(statusBadgeText);
            boolean isOn = isAccentIcon;
            pill.setTextColor(isOn ? SYSTEM_GREEN : TEXT_SECONDARY);
            pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
            pill.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            pill.setPadding(dp(ctx, 8), dp(ctx, 3), dp(ctx, 8), dp(ctx, 3));
            pill.setBackground(createCardDrawable(ctx,
                    isOn ? SYSTEM_GREEN_TINT : SURFACE_INSET_CARD,
                    8,
                    isOn ? Color.parseColor("#4D30D158") : SEPARATOR_HAIRLINE));
            LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            plp.setMarginStart(dp(ctx, 6));
            card.addView(pill, plp);
        }

        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.width = 0;
        glp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        int m = dp(ctx, 4);
        glp.setMargins(m, m, m, m);
        grid.addView(card, glp);
    }

    public static void showFilesModal(MainActivity activity, List<File> files, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.86f, 650, true);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_documents);
        headerIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        hiLp.setMarginEnd(dp(activity, 10));
        header.addView(headerIcon, hiLp);

        TextView title = new TextView(activity);
        title.setText(tr(activity,
                "Printed PDFs & Exported Files",
                "ملفات PDF والمستندات المصدرة",
                "پەڕگەکانی PDF و چاپکراوەکان"));
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createCupertinoButton(activity,
                tr(activity, "Done", "تم", "تەواو"),
                false, false, v -> dialog.dismiss()));
        root.addView(header);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 10);
        scroll.setLayoutParams(scrollLp);

        LinearLayout listBox = new LinearLayout(activity);
        listBox.setOrientation(LinearLayout.VERTICAL);

        if (files.isEmpty()) {
            LinearLayout emptyCard = new LinearLayout(activity);
            emptyCard.setOrientation(LinearLayout.VERTICAL);
            emptyCard.setGravity(Gravity.CENTER);
            int p = dp(activity, 28);
            emptyCard.setPadding(p, p, p, p);
            emptyCard.setBackground(createCardDrawable(activity,
                    SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));

            TextView emptyText = new TextView(activity);
            emptyText.setText(tr(activity,
                    "No documents generated yet.\n\nWhen you print or export declarations inside ASYCUDA World, your PDFs and files will appear here and in Download/Asycuda_Prints.",
                    "لا توجد مستندات مطبوعة بعد.\n\nعند طباعة أو تصدير التصاريح والوصلات من داخل نظام أسيكودا، ستظهر ملفات PDF هنا وفي مجلد Download/Asycuda_Prints.",
                    "هێشتا هیچ بەڵگەنامەیەک چاپ نەکراوە.\n\nکاتێک بەڵگەنامەکان لەناو سیستەمی ئەسیکۆدا چاپ دەکەیت، پەڕگەکانی PDF لێرە و لە فۆڵدەری Download/Asycuda_Prints دەردەکەون."));
            emptyText.setTextColor(TEXT_SECONDARY);
            emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            emptyText.setGravity(Gravity.CENTER);
            emptyCard.addView(emptyText);
            listBox.addView(emptyCard);
        } else {
            for (File f : files) {
                LinearLayout row = new LinearLayout(activity);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                int pad = dp(activity, 12);
                row.setPadding(pad, dp(activity, 10), pad, dp(activity, 10));
                row.setBackground(createCardDrawable(activity,
                        SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.bottomMargin = dp(activity, 8);
                row.setLayoutParams(rlp);

                LinearLayout infoCol = new LinearLayout(activity);
                infoCol.setOrientation(LinearLayout.VERTICAL);
                infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                TextView nameView = new TextView(activity);
                nameView.setText(f.getName());
                nameView.setTextColor(TEXT_PRIMARY);
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
                nameView.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
                infoCol.addView(nameView);

                long kb = Math.max(1, f.length() / 1024);
                TextView metaView = new TextView(activity);
                metaView.setText(kb + " KB • " + tr(activity,
                        "Ready to open or share",
                        "جاهز للفتح أو المشاركة",
                        "ئامادەیە بۆ کردنەوە یان هاوبەشکردن"));
                metaView.setTextColor(TEXT_SECONDARY);
                metaView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
                infoCol.addView(metaView);
                row.addView(infoCol);

                TextView openBtn = createCupertinoButton(activity,
                        tr(activity, "Open", "فتح", "کردنەوە"),
                        true, false, v -> {
                            dialog.dismiss();
                            cb.onOpenFile(f);
                        });
                LinearLayout.LayoutParams olp = (LinearLayout.LayoutParams) openBtn.getLayoutParams();
                olp.setMarginEnd(dp(activity, 6));
                row.addView(openBtn);

                TextView shareBtn = createCupertinoButton(activity,
                        tr(activity, "Share", "مشاركة", "هاوبەشکردن"),
                        false, false, v -> {
                            dialog.dismiss();
                            cb.onShareFile(f);
                        });
                row.addView(shareBtn);

                listBox.addView(row);
            }
        }

        scroll.addView(listBox);
        root.addView(scroll);
        dialog.setContentView(root);
        dialog.show();
    }

    public static void showAboutAndCreditsModal(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.90f, 720, true);
        LinearLayout root = createDialogContainer(activity);

        // Top Bar
        LinearLayout topBar = new LinearLayout(activity);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView infoIcon = new ImageView(activity);
        infoIcon.setImageResource(R.drawable.ic_menu_info);
        infoIcon.setImageTintList(ColorStateList.valueOf(SYSTEM_BLUE));
        LinearLayout.LayoutParams iiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        iiLp.setMarginEnd(dp(activity, 10));
        topBar.addView(infoIcon, iiLp);

        TextView topTitle = new TextView(activity);
        topTitle.setText(tr(activity,
                "About & Open-Source Licences",
                "حول التطبيق والتراخيص مفتوحة المصدر",
                "دەربارەی بەرنامە و مۆڵەتە کراوەکان"));
        topTitle.setTextColor(TEXT_PRIMARY);
        topTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.5f);
        topTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        topTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(topTitle);

        topBar.addView(createCupertinoButton(activity,
                tr(activity, "Done", "تم", "تەواو"),
                false, false, v -> dialog.dismiss()));
        root.addView(topBar);
        root.addView(createHairlineDivider(activity));

        ScrollView scroll = new ScrollView(activity);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        scrollLp.topMargin = dp(activity, 10);
        scroll.setLayoutParams(scrollLp);

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        // 1. Hero App Identity Card
        LinearLayout heroCard = new LinearLayout(activity);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 16);
        heroCard.setPadding(pad, pad, pad, pad);
        heroCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(activity, 10);
        heroCard.setLayoutParams(cardLp);

        LinearLayout heroTop = new LinearLayout(activity);
        heroTop.setOrientation(LinearLayout.HORIZONTAL);
        heroTop.setGravity(Gravity.CENTER_VERTICAL);

        ImageView appIcon = new ImageView(activity);
        appIcon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 50), dp(activity, 50));
        icLp.setMarginEnd(dp(activity, 14));
        heroTop.addView(appIcon, icLp);

        LinearLayout heroTextCol = new LinearLayout(activity);
        heroTextCol.setOrientation(LinearLayout.VERTICAL);

        TextView appTitle = new TextView(activity);
        appTitle.setText(tr(activity,
                "ASYCUDA World — Republic of Iraq",
                "نظام أسيكودا العالمي — جمهورية العراق",
                "سیستەمی ئەسیکۆدای جیهانی — کۆماری عێراق"));
        appTitle.setTextColor(TEXT_PRIMARY);
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.5f);
        appTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        heroTextCol.addView(appTitle);

        TextView verPill = new TextView(activity);
        verPill.setText("Release " + getAppVersionLabel(activity) + " • ARM64");
        verPill.setTextColor(SYSTEM_BLUE);
        verPill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        verPill.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heroTextCol.addView(verPill);
        heroTop.addView(heroTextCol);
        heroCard.addView(heroTop);

        TextView appDesc = new TextView(activity);
        appDesc.setText(tr(activity,
                "Android client for the Iraqi General Commission for Customs ASYCUDA World portal, built for customs brokers, clearing agents, and trade operators.",
                "تطبيق أندرويد لتشغيل نظام أسيكودا العالمي التابع للهيئة العامة للكمارك العراقية، مصمم للمخلصين الكمركيين وشركات الشحن والتجارة.",
                "بەرنامەی ئەندرۆید بۆ کارپێکردنی سیستەمی ئەسیکۆدای جیهانی سەر بە دەستەی گشتیی گومرگی عێراق، تایبەت بە بریکارانی گومرگی و کۆمپانیا بازرگانییەکان."));
        appDesc.setTextColor(TEXT_SECONDARY);
        appDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descLp.topMargin = dp(activity, 10);
        heroCard.addView(appDesc, descLp);
        content.addView(heroCard);

        // 2. Developer Card
        LinearLayout devCard = new LinearLayout(activity);
        devCard.setOrientation(LinearLayout.VERTICAL);
        devCard.setPadding(pad, pad, pad, pad);
        devCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        devCard.setLayoutParams(cardLp);

        TextView devSectionLabel = createSectionHeader(activity,
                tr(activity, "DEVELOPER & MAINTAINER", "المطور والمشرف على المشروع", "گەشەپێدەر و سەرپەرشتیاری پرۆژە"));
        devCard.addView(devSectionLabel);

        TextView devName = new TextView(activity);
        devName.setText("Mohammed Ammar Hussein (Mohammed AL-Shimmary)");
        devName.setTextColor(TEXT_PRIMARY);
        devName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        devName.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        devCard.addView(devName);

        TextView devRole = new TextView(activity);
        devRole.setText(tr(activity,
                "Senior Logistics Coordinator & Software Developer • Baghdad, Iraq",
                "منسق لوجستي أقدم ومطور برمجيات • بغداد، العراق",
                "ڕێکخەری باڵای لۆجستی و گەشەپێدەری نەرمەکاڵا • بەغدا، عێراق"));
        devRole.setTextColor(SYSTEM_BLUE);
        devRole.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        devCard.addView(devRole);

        TextView devBio = new TextView(activity);
        devBio.setText(tr(activity,
                "Built and maintained as an open-source project for the Iraqi customs and logistics community.",
                "تم تطويره وإتاحته كمشروع مفتوح المصدر لخدمة مجتمع التخليص الكمركي واللوجستيات في العراق.",
                "پەرەی پێدراوە وەک پرۆژەیەکی سەرچاوە کراوە بۆ خزمەتکردنی کەرتی گومرگ و لۆجستی لە عێراق."));
        devBio.setTextColor(TEXT_SECONDARY);
        devBio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams bioLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bioLp.topMargin = dp(activity, 6);
        bioLp.bottomMargin = dp(activity, 12);
        devCard.addView(devBio, bioLp);

        GridLayout socialGrid = new GridLayout(activity);
        int socialCols = isCompactWidth(activity) ? 2 : 4;
        socialGrid.setColumnCount(socialCols);
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

        // 3. Open-Source Components & Licences
        LinearLayout ossCard = new LinearLayout(activity);
        ossCard.setOrientation(LinearLayout.VERTICAL);
        ossCard.setPadding(pad, pad, pad, pad);
        ossCard.setBackground(createCardDrawable(activity,
                SURFACE_GROUPED_CARD, 14, SEPARATOR_HAIRLINE));
        ossCard.setLayoutParams(cardLp);

        ossCard.addView(createSectionHeader(activity,
                tr(activity, "OPEN-SOURCE SOFTWARE & CREDITS", "البرمجيات مفتوحة المصدر", "نەرمەکاڵا سەرچاوە کراوەکان")));

        addCreditEntry(activity, ossCard,
                "Termux:X11 (libXlorie)",
                "GPLv3 / MIT",
                "Twaik Yont (@twaik), Fredrik Fornwall (@fornwall), @agnostic-apollo, @sylirre",
                "Embedded X11 display server, SurfaceView rendering, touch input handling, and extra keys bar.");

        addCreditEntry(activity, ossCard,
                "Eclipse Temurin / OpenJDK",
                "GPLv2 + Classpath",
                "Oracle Corporation, Red Hat AArch64 Port Team & OpenJDK Contributors",
                "64-bit ARMv8 Java runtime environment and Swing/AWT graphical toolkit.");

        addCreditEntry(activity, ossCard,
                "GNU C Library (glibc) for Android",
                "LGPLv2.1+",
                "Free Software Foundation & @Maxython (glibc-packages)",
                "POSIX C library and dynamic ELF linker for ARM64 Linux binaries.");

        addCreditEntry(activity, ossCard,
                "X.Org Foundation & FreeType",
                "MIT / FreeType",
                "Keith Packard, David Turner, Werner Lemberg & freedesktop.org",
                "X11 protocol libraries (libX11, libxcb, libXrender, libXtst) and TrueType font rendering.");

        addCreditEntry(activity, ossCard,
                "XZ for Java & Apache Commons Compress",
                "0BSD / Apache 2.0",
                "Lasse Collin, Igor Pavlov & The Apache Software Foundation",
                "XZ decompression and POSIX TAR archive handling.");

        addCreditEntry(activity, ossCard,
                "ASYCUDA World Client Libraries",
                "UNCTAD / MOF Iraq",
                "UNCTAD & Republic of Iraq Ministry of Finance / General Commission for Customs",
                "ASYCUDA World client modules and customs declaration components.");

        content.addView(ossCard);

        // 4. Legal & Trademark Notice Card
        LinearLayout legalCard = new LinearLayout(activity);
        legalCard.setOrientation(LinearLayout.VERTICAL);
        legalCard.setPadding(pad, dp(activity, 12), pad, dp(activity, 12));
        legalCard.setBackground(createCardDrawable(activity,
                SURFACE_INSET_CARD, 14, SEPARATOR_HAIRLINE));

        TextView legalHeader = createSectionHeader(activity,
                tr(activity, "DISCLAIMER", "إخلاء مسؤولية", "بەرپرسیارێتی"));
        legalCard.addView(legalHeader);

        TextView legalText = new TextView(activity);
        legalText.setText(tr(activity,
                "Independent open-source client developed to support ASYCUDA World access on Android devices. Not affiliated with or endorsed by UNCTAD or the Ministry of Finance of the Republic of Iraq.",
                "تطبيق مستقل مفتوح المصدر تم تطويره لتسهيل الوصول إلى نظام أسيكودا العالمي على أجهزة الأندرويد، ولا يتبع رسمياً لوزارة المالية العراقية أو منظمة الأونكتاد (UNCTAD).",
                "بەرنامەیەکی سەربەخۆی سەرچاوە کراوەیە بۆ کارئاسانی بەکارهێنانی سیستەمی ئەسیکۆدا لەسەر ئامێرەکانی ئەندرۆید، و بە فەرمی سەر بە وەزارەتی دارایی عێراق یان ڕێکخراوی UNCTAD نییە."));
        legalText.setTextColor(TEXT_SECONDARY);
        legalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        legalCard.addView(legalText);
        content.addView(legalCard);

        scroll.addView(content);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();
    }

    private static void addCreditEntry(Context ctx, LinearLayout parent,
                                       String project, String license,
                                       String authors, String description) {
        LinearLayout item = new LinearLayout(ctx);
        item.setOrientation(LinearLayout.VERTICAL);
        int p = dp(ctx, 12);
        item.setPadding(p, dp(ctx, 10), p, dp(ctx, 10));
        item.setBackground(createCardDrawable(ctx,
                SURFACE_INSET_CARD, 12, SEPARATOR_HAIRLINE));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 8);
        item.setLayoutParams(lp);

        LinearLayout titleRow = new LinearLayout(ctx);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView pName = new TextView(ctx);
        pName.setText(project);
        pName.setTextColor(TEXT_PRIMARY);
        pName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        pName.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        pName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleRow.addView(pName);

        TextView licBadge = new TextView(ctx);
        licBadge.setText(license);
        licBadge.setTextColor(SYSTEM_BLUE);
        licBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        licBadge.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        licBadge.setPadding(dp(ctx, 8), dp(ctx, 2), dp(ctx, 8), dp(ctx, 2));
        licBadge.setBackground(createCardDrawable(ctx,
                SYSTEM_BLUE_TINT, 8, 0));
        titleRow.addView(licBadge);
        item.addView(titleRow);

        TextView authView = new TextView(ctx);
        authView.setText(authors);
        authView.setTextColor(TEXT_PRIMARY);
        authView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alp.topMargin = dp(ctx, 3);
        item.addView(authView, alp);

        TextView descView = new TextView(ctx);
        descView.setText(description);
        descView.setTextColor(TEXT_SECONDARY);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
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
        int m = dp(ctx, 4);
        glp.setMargins(m, m, m, m);
        grid.addView(btn, glp);
    }

    private static TextView createSectionHeader(Context ctx, String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(TEXT_SECONDARY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        tv.setLetterSpacing(0.06f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 6);
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
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            int targetW = Math.min((int) (dm.widthPixels * widthFraction), dp(activity, maxWidthDp));
            w.setLayout(targetW, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = w.getAttributes();
            wlp.dimAmount = 0.50f;
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            if (Build.VERSION.SDK_INT >= 31) {
                try {
                    w.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    wlp.setBlurBehindRadius(dp(activity, 28));
                } catch (Throwable ignored) {}
            }
            w.setAttributes(wlp);
        }
        return dialog;
    }

    private static LinearLayout createDialogContainer(Context ctx) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(isRtl(ctx) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        int p = dp(ctx, 18);
        root.setPadding(p, dp(ctx, 10), p, dp(ctx, 16));
        root.setBackground(createSheetContainerDrawable(ctx, 22));
        root.setElevation(dp(ctx, 20));

        // Apple iOS/iPadOS Modal Top Grabber Handle
        View grabber = new View(ctx);
        grabber.setBackground(createCardDrawable(ctx, GRABBER_COLOR, 3, 0));
        LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(dp(ctx, 36), dp(ctx, 4));
        glp.gravity = Gravity.CENTER_HORIZONTAL;
        glp.bottomMargin = dp(ctx, 10);
        root.addView(grabber, glp);

        return root;
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
        int fill = isDestructive ? SYSTEM_RED_TINT
                : (isPrimary ? SYSTEM_BLUE : SYSTEM_GRAY_FILL);
        int stroke = isDestructive ? Color.parseColor("#4DFF453A") : 0;
        return createRippleCardDrawable(ctx, fill, 10, stroke);
    }

    public static TextView createCupertinoButton(Context ctx, String label, boolean isPrimary, boolean isDestructive, View.OnClickListener listener) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(isDestructive ? SYSTEM_RED : TEXT_PRIMARY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        tv.setPadding(dp(ctx, 14), dp(ctx, 8), dp(ctx, 14), dp(ctx, 8));
        tv.setBackground(createCupertinoButtonDrawable(ctx, isPrimary, isDestructive));
        tv.setClickable(true);
        tv.setFocusable(true);
        if (listener != null) tv.setOnClickListener(listener);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    public static TextView createGlassPillButton(Context ctx, String label, boolean isPrimaryAccent, View.OnClickListener listener) {
        return createCupertinoButton(ctx, label, isPrimaryAccent, false, listener);
    }

    public static TextView createPillButton(Context ctx, String label, int bgColor, int textColor, View.OnClickListener listener) {
        return createCupertinoButton(ctx, label, true, false, listener);
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

    private static boolean isCompactWidth(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        return cfg.screenWidthDp < 480;
    }

    public static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
