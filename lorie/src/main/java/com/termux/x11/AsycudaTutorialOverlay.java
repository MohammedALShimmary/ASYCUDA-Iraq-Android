package com.termux.x11;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Two-Stage Interactive Startup UI Animation Tutorial:
 * - Stage 1: Lets the user pick their preferred Input Method (1 = Trackpad, 2 = Simulated Touchscreen,
 *   3 = Direct Touch) and applies it immediately.
 * - Stage 2: Runs an animated multi-step walkthrough tailored to the default gestures of their chosen
 *   Input Method, highlights the floating Control Center pill + attached Baby Calculator icon
 *   and top Shortcut Bar (ESC, TAB, Keyboard), and shows where to customize any click/gesture in
 *   Display Settings -> Mouse, Touch & Finger Gestures.
 *
 * Uses a pinned Header + scrollable middle body + pinned Footer layout matching AsycudaMaterialUi
 * so navigation buttons ("Back" / "Next" / "Finish") are never clipped in Landscape mode.
 */
@SuppressLint("SetTextI18n")
@SuppressWarnings({"java:S3776", "java:S107", "java:S1141", "java:S1192", "java:S1172", "java:S2696", "java:S9439"})
public final class AsycudaTutorialOverlay {

    private static final String PREF_NAME = "asycuda_prefs";
    private static final String KEY_TUTORIAL_DONE = "tutorial_completed_v1";
    private static final String SANS_SERIF_MEDIUM = "sans-serif-medium";

    private static FrameLayout sOverlayRoot = null;
    private static ValueAnimator sDemoAnimator = null;
    private static Runnable sOnCompleteCallback = null;

    private AsycudaTutorialOverlay() {}

    public static boolean isTutorialCompleted(Context ctx) {
        if (ctx == null) return true;
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_TUTORIAL_DONE, false);
    }

    public static void markTutorialCompleted(Context ctx) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_TUTORIAL_DONE, true)
                .apply();
    }

    public static void showBeforeSessionStartup(Activity activity, Runnable onComplete) {
        if (activity == null || activity.isFinishing()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        if (isTutorialCompleted(activity)) {
            if (onComplete != null) onComplete.run();
            return;
        }
        sOnCompleteCallback = onComplete;
        showInteractiveTutorial(activity, false);
    }

    public static void showIfNeededOnFirstSession(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        if (isTutorialCompleted(activity)) return;
        showInteractiveTutorial(activity, false);
    }

    public static void showInteractiveTutorial(Activity activity, boolean isManualReplay) {
        if (activity == null || activity.isFinishing()) return;
        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        if (isManualReplay) {
            sOnCompleteCallback = null;
        }
        dismissViewOnly();

        sOverlayRoot = new FrameLayout(activity);
        sOverlayRoot.setBackgroundColor(Color.parseColor("#E6080D1A"));
        sOverlayRoot.setClickable(true);
        sOverlayRoot.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sOverlayRoot.setElevation(dp(activity, 28));
        }

        root.addView(sOverlayRoot, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        renderStage1InputMethodPicker(activity);
    }

    private static void dismissViewOnly() {
        if (sDemoAnimator != null) {
            sDemoAnimator.cancel();
            sDemoAnimator = null;
        }
        if (sOverlayRoot != null) {
            if (sOverlayRoot.getParent() instanceof ViewGroup) {
                ((ViewGroup) sOverlayRoot.getParent()).removeView(sOverlayRoot);
            }
            sOverlayRoot = null;
        }
    }

    public static void dismiss(Activity activity) {
        dismissViewOnly();
        Runnable cb = sOnCompleteCallback;
        sOnCompleteCallback = null;
        if (cb != null) {
            cb.run();
        }
    }

    private static void renderStage1InputMethodPicker(Activity activity) {
        if (sOverlayRoot == null) return;
        if (sDemoAnimator != null) {
            sDemoAnimator.cancel();
            sDemoAnimator = null;
        }
        sOverlayRoot.removeAllViews();

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();

        int cardW = Math.min(dp(activity, 530), (int) (dm.widthPixels * 0.90f));
        int maxCardH = (int) (dm.heightPixels * 0.88f);

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(activity, 16), dp(activity, 12), dp(activity, 16), dp(activity, 12));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(AsycudaMaterialUi.SURFACE_SHEET);
        cardBg.setCornerRadius(dp(activity, 16));
        cardBg.setStroke(dp(activity, 1), AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        card.setBackground(cardBg);

        // 1. Pinned Header Row (Icon + Title/Badge + Close Button)
        LinearLayout headerRow = new LinearLayout(activity);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_tutorial);
        headerIcon.setImageTintList(ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        hiLp.rightMargin = dp(activity, 10);
        headerRow.addView(headerIcon, hiLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView badge = new TextView(activity);
        badge.setText(AsycudaMaterialUi.tr(activity,
                "INTERACTIVE SETUP — STAGE 1 OF 2",
                "دليل البدء التفاعلي — الخطوة ١ من ٢",
                "ڕێبەری دەستپێکردن — هەنگاوی ١ لە ٢"));
        badge.setTextColor(AsycudaMaterialUi.SYSTEM_BLUE);
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
        badge.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(badge);

        TextView title = new TextView(activity);
        title.setText(AsycudaMaterialUi.tr(activity,
                "Choose Your Preferred Input Method",
                "اختر طريقة التحكم المفضلة لديك",
                "شێوازی کۆنترۆڵکردنی دڵخوازت هەڵبژێرە"));
        title.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        title.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleCol.addView(title);

        headerRow.addView(titleCol);
        headerRow.addView(createCloseIconButton(activity, () -> {
            markTutorialCompleted(activity);
            dismiss(activity);
        }));
        card.addView(headerRow);
        card.addView(createDivider(activity));

        // 2. Scrollable Middle Body
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollLp.topMargin = dp(activity, 8);
        scrollLp.bottomMargin = dp(activity, 8);
        scroll.setLayoutParams(scrollLp);

        LinearLayout contentCol = new LinearLayout(activity);
        contentCol.setOrientation(LinearLayout.VERTICAL);

        TextView subtitle = new TextView(activity);
        subtitle.setText(AsycudaMaterialUi.tr(activity,
                "Because ASYCUDA World uses dense desktop forms, choose how you want your fingers to control the cursor and clicks:",
                "بما أن نظام أسيكودا صُمم للحواسيب المكتبية، يوفر لك التطبيق طريقتي تحكم ذكيتين. اختر الطريقة التي تناسبك لنبدأ جولة سريعة:",
                "چونکە سیستەمی ئەسیکۆدا بۆ کۆمپیوتەر دروستکراوە، ئەم بەرنامەیە ٢ شێوازی کۆنترۆڵ دابین دەکات. یەکێکیان هەڵبژێرە بۆ فێربوون:"));
        subtitle.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.bottomMargin = dp(activity, 10);
        contentCol.addView(subtitle, subLp);

        int currentMode = getCurrentTouchMode(activity) >= 2 ? 2 : 1;

        // Option 1: Trackpad (Virtual Mouse)
        contentCol.addView(buildModeOptionCard(
                activity,
                1,
                currentMode == 1,
                R.drawable.ic_menu_gestures,
                AsycudaMaterialUi.tr(activity,
                        "1. Trackpad (Virtual Mouse — Recommended for Phones)",
                        "١. لوحة اللمس (ماوس افتراضي — موصى به للهواتف)",
                        "١. تراكپاد (ماوسی خەیاڵی — پێشنیارکراو)"),
                AsycudaMaterialUi.tr(activity,
                        "Slide 1 finger anywhere like a laptop touchpad to glide the desktop cursor smoothly without your finger covering tiny fields. 1-finger tap = Left Click, 2-finger tap = Right Click.",
                        "حرك إصبعك في أي مكان لتحريك المؤشر بدقة عالية دون أن تحجب إصبعك الحقول الصغيرة. لمسة واحدة للنقر الأيسر، ولمستان للنقر الأيمن.",
                        "پەنجەت لە هەر شوێنێک بجوڵێنە بۆ جوڵاندنی ماوسەکە بە وردی. یەک لێدان کرتەی چەپ، دوو لێدان کرتەی ڕاست.")
        ));

        // Option 2: Direct Touchscreen
        contentCol.addView(buildModeOptionCard(
                activity,
                2,
                currentMode == 2,
                R.drawable.ic_menu_display,
                AsycudaMaterialUi.tr(activity,
                        "2. Direct Touchscreen (Tap & Drag Directly)",
                        "٢. شاشة لمس مباشر (موصى به للأجهزة اللوحية واللمس الفوري)",
                        "٢. شاشەی لەمسی ڕاستەوخۆ (کرتە و ڕاکێشانی ڕاستەوخۆ)"),
                AsycudaMaterialUi.tr(activity,
                        "Tap directly on any button or field to click it right under your finger, drag 1 finger to move windows or select text, and Long-Press (or 2-Finger Tap) for Right Click.",
                        "المس أي زر أو حقل مباشرة للنقر الفوري عليه، واسحب إصبعك لتحريك النوافذ أو تحديد النصوص، واضغط مطولاً أو بإصبعين للنقرة اليمنى.",
                        "ڕاستەوخۆ پەنجە بنێ بە هەر دوگمەیەکدا بۆ کرتەکردن، یان ڕایبکێشە بۆ جوڵاندنی پەنجەرەکان. پەنجە ڕاگرتن یان دوو پەنجە کرتەی ڕاست دەکات.")
        ));

        scroll.addView(contentCol);
        card.addView(scroll);
        card.addView(createDivider(activity));

        // 3. Pinned Footer Row
        LinearLayout footerRow = new LinearLayout(activity);
        footerRow.setOrientation(LinearLayout.HORIZONTAL);
        footerRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fLp.topMargin = dp(activity, 8);
        footerRow.setLayoutParams(fLp);

        TextView skipBtn = createActionPill(activity,
                AsycudaMaterialUi.tr(activity, "Skip Tutorial", "تخطي الدليل", "تێپەڕاندن"),
                AsycudaMaterialUi.SURFACE_GROUPED_CARD, AsycudaMaterialUi.TEXT_SECONDARY, AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        skipBtn.setOnClickListener(v -> {
            markTutorialCompleted(activity);
            dismiss(activity);
        });
        footerRow.addView(skipBtn);

        View spacer = new View(activity);
        footerRow.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));

        TextView continueBtn = createActionPill(activity,
                AsycudaMaterialUi.tr(activity, "Continue with Selected →", "متابعة بالوضع الحالي ←", "بەردەوامبوون ←"),
                "#FF0A84FF", "#FFFFFFFF", "#FF0A84FF");
        continueBtn.setOnClickListener(v -> renderStage2Walkthrough(activity, currentMode, 0));
        footerRow.addView(continueBtn);

        card.addView(footerRow);

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(cardW, maxCardH, Gravity.CENTER);
        sOverlayRoot.addView(card, cardLp);
    }

    private static View buildModeOptionCard(Activity activity, int mode, boolean isCurrent, int iconRes, String titleText, String descText) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(activity, 12), dp(activity, 10), dp(activity, 12), dp(activity, 10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isCurrent ? AsycudaMaterialUi.SYSTEM_BLUE_TINT : AsycudaMaterialUi.SURFACE_GROUPED_CARD);
        bg.setCornerRadius(dp(activity, 12));
        bg.setStroke(dp(activity, isCurrent ? 2 : 1), isCurrent ? AsycudaMaterialUi.SYSTEM_BLUE : AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        box.setBackground(bg);

        FrameLayout iconBadge = new FrameLayout(activity);
        GradientDrawable ibBg = new GradientDrawable();
        ibBg.setColor(isCurrent ? AsycudaMaterialUi.SYSTEM_BLUE : AsycudaMaterialUi.SYSTEM_GRAY_FILL);
        ibBg.setCornerRadius(dp(activity, 9));
        iconBadge.setBackground(ibBg);
        ImageView iv = new ImageView(activity);
        iv.setImageResource(iconRes);
        iv.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        iconBadge.addView(iv, new FrameLayout.LayoutParams(dp(activity, 18), dp(activity, 18), Gravity.CENTER));
        LinearLayout.LayoutParams ibLp = new LinearLayout.LayoutParams(dp(activity, 34), dp(activity, 34));
        ibLp.rightMargin = dp(activity, 11);
        box.addView(iconBadge, ibLp);

        LinearLayout textCol = new LinearLayout(activity);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(activity);
        t.setText(titleText);
        t.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        t.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        textCol.addView(t);

        TextView d = new TextView(activity);
        d.setText(descText);
        d.setTextColor(AsycudaMaterialUi.TEXT_SECONDARY);
        d.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.2f);
        LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dLp.topMargin = dp(activity, 2);
        textCol.addView(d, dLp);

        box.addView(textCol);

        box.setOnClickListener(v -> {
            applyTouchMode(activity, mode);
            renderStage2Walkthrough(activity, mode, 0);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(activity, 7);
        box.setLayoutParams(lp);
        return box;
    }

    private static void renderStage2Walkthrough(Activity activity, int chosenMode, int stepIndex) {
        if (sOverlayRoot == null) return;
        if (sDemoAnimator != null) {
            sDemoAnimator.cancel();
            sDemoAnimator = null;
        }
        sOverlayRoot.removeAllViews();

        final int totalSteps = 5;

        String stepTitle;
        String stepBody;
        int demoType;
        int stepIconRes;

        String trackpadLabel = AsycudaMaterialUi.tr(activity,
                "Trackpad Mode",
                "لوحة اللمس (الماوس الافتراضي)",
                "تراكپاد (ماوسی خەیاڵی)");
        String touchLabel = AsycudaMaterialUi.tr(activity,
                "Direct Touchscreen",
                "شاشة اللمس المباشر",
                "شاشەی لەمسی ڕاستەوخۆ");
        String modeName = chosenMode == 1 ? trackpadLabel : touchLabel;

        switch (stepIndex) {
            case 0:
                demoType = 0;
                stepIconRes = R.drawable.ic_menu_gestures;
                stepTitle = AsycudaMaterialUi.tr(activity,
                        "1/5 — Cursor & Left Click (" + modeName + ")",
                        "١/٥ — تحريك المؤشر والنقر الأساسي (" + modeName + ")",
                        "١/٥ — جوڵاندنی نیشاندەر و کرتەی سەرەکی (" + modeName + ")");
                if (chosenMode == 1) {
                    stepBody = AsycudaMaterialUi.tr(activity,
                            "• Slide 1 finger anywhere to move the mouse cursor.\n• Tap with 1 finger for Left Click.\n• Hold 1 finger & drag to select text or move windows.",
                            "• اسحب إصبعاً واحداً في أي مكان لتحريك مؤشر الماوس.\n• المس الشاشة بإصبع واحد لتنفيذ نقرة يسرى (Left Click).\n• اضغط مطولاً بإصبع واحد ثم اسحب لتحديد النصوص أو تحريك النوافذ.",
                            "• یەک پەنجە لە هەر شوێنێک ڕابکێشە بۆ جوڵاندنی ماوس.\n• یەک پەنجە لێبدە بۆ کرتەی چەپ (Left Click).\n• پەنجە ڕابگرە و ڕابکێشە بۆ دیاریکردنی دەق.");
                } else {
                    stepBody = AsycudaMaterialUi.tr(activity,
                            "• Tap anywhere with 1 finger for an instant Left Click (or your customized 1-Finger Tap action) right under your finger.\n• Drag 1 finger immediately to move windows, scrollbars, or select text.",
                            "• المس أي عنصر مباشرة بإصبع واحد للنقر الفوري في موقع إصبعك (Left Click أو حسب تخصيصك).\n• اسحب إصبعك مباشرة لتحريك النوافذ أو أشرطة التمرير أو تحديد النصوص.",
                            "• ڕاستەوخۆ پەنجە بنێ بە هەر دوگمەیەکدا بۆ کرتەی چەپ.\n• پەنجەت ڕابکێشە بۆ دیاریکردنی دەق یان جوڵاندنی پەنجەرەکان.");
                }
                break;

            case 1:
                demoType = 1;
                stepIconRes = R.drawable.ic_menu_gestures;
                stepTitle = AsycudaMaterialUi.tr(activity,
                        "2/5 — Right Click for ASYCUDA Context Menus",
                        "٢/٥ — النقرة اليمنى لفتح قوائم أسيكودا (Right Click)",
                        "٢/٥ — کرتەی ڕاست بۆ لیستەکانی ئەسیکۆدا (Right Click)");
                if (chosenMode >= 2) {
                    stepBody = AsycudaMaterialUi.tr(activity,
                            "• In Direct Touchscreen mode: Hold 1 finger still (Long-Press) OR Tap with 2 fingers together to trigger a Right Click directly under your finger.",
                            "• في وضع شاشة اللمس المباشر: اضغط مطولاً بإصبع واحد في مكانه (Long Press) أو المس الشاشة بإصبعين معاً لفتح القائمة الفرعية (Right Click) مباشرة تحت إصبعك.",
                            "• لە شێوازی لەمسی ڕاستەوخۆ: پەنجە ڕابگرە (Long Press) یان بە دوو پەنجە لێبدە بۆ کرتەی ڕاست.");
                } else {
                    stepBody = AsycudaMaterialUi.tr(activity,
                            "• Tap with 2 fingers simultaneously (2-Finger Tap) to perform a Right Click and open ASYCUDA document context menus.",
                            "• المس الشاشة بإصبعين معاً (2-Finger Tap) في نفس الوقت لتنفيذ نقرة يمنى (Right Click) وفتح قوائم المستندات في أسيكودا.",
                            "• بە دوو پەنجە پێکەوە لێبدە (2-Finger Tap) بۆ کردنەوەی لیستی کرتەی ڕاست (Right Click) لە ئەسیکۆدا.");
                }
                break;

            case 2:
                demoType = 2;
                stepIconRes = R.drawable.ic_menu_assist_keys;
                stepTitle = AsycudaMaterialUi.tr(activity,
                        "3/5 — Two-Finger Scroll & Three-Finger Swipes",
                        "٣/٥ — التمرير بإصبعين وإيماءات ٣ أصابع",
                        "٣/٥ — جوڵاندن بە دوو پەنجە و جوڵەی ٣ پەنجە");
                stepBody = AsycudaMaterialUi.tr(activity,
                        "• Glide 2 fingers up/down to scroll tables (Mouse Wheel).\n• Swipe 3 fingers UP to toggle the Soft Keyboard.\n• Swipe 3 fingers DOWN to show/hide the top Shortcut Bar.",
                        "• اسحب بإصبعين للأعلى أو الأسفل للتمرير داخل الجداول الطويلة (Scroll Wheel).\n• اسحب بـ ٣ أصابع للأعلى لإظهار/إخفاء لوحة المفاتيح.\n• اسحب بـ ٣ أصابع للأسفل لإظهار/إخفاء شريط الاختصارات العلوي.",
                        "• بە دوو پەنجە بەرەو سەرەوە یان خوارەوە ڕابکێشە بۆ گەڕان لە خشتەکاندا.\n• بە ٣ پەنجە بەرەو سەرەوە ڕابکێشە بۆ کردنەوەی تەختەکلیل.\n• بە ٣ پەنجە بەرەو خوارەوە ڕابکێشە بۆ شاردنەوەی شریتی سەرەوە.");
                break;

            case 3:
                demoType = 3;
                stepIconRes = R.drawable.ic_menu_calculator;
                stepTitle = AsycudaMaterialUi.tr(activity,
                        "4/5 — Control Center Pill, Calculator & Shortcut Bar",
                        "٤/٥ — الزر العائم والحاسبة المدمجة وشريط الاختصارات",
                        "٤/٥ — دوگمەی سەرئاوکەوتوو و ژمێرەری بچووک و شریتی کورتکراوە");
                stepBody = AsycudaMaterialUi.tr(activity,
                        "• Floating Pill (AsyLauncher): Opens Control Center and hosts the attached Mini Calculator icon.\n• Calculator Hide (—) collapses it back into the pill keeping your math (green indicator dot); Close (✕) resets it.\n• Top Shortcut Bar Keyboard button toggles the Android keyboard both open and closed.",
                        "• الزر العائم (AsyLauncher): يفتح مركز التحكم، ويلتصق به زر الحاسبة المصغرة الذي يفتح نافذة حاسبة حرة الحركة وتغيير الحجم.\n• زر الإخفاء (—) في الحاسبة يطويها داخل الزر العائم مع حفظ حساباتك (تظهر نقطة خضراء)، بينما زر الإغلاق (✕) يصفرها.\n• زر الكيبورد في الشريط العلوي يفتح ويغلق لوحة المفاتيح بضغطة واحدة.",
                        "• دوگمەی سەرئاوکەوتوو (AsyLauncher): ناوەندی کۆنترۆڵ دەکاتەوە، و دوگمەی ژمێرەری پێوە نووساوە.\n• دوگمەی (—) ژمێرەرەکە دەشارێتەوە بە پاراستنی ژمارەکان، و (✕) دایئەخات.\n• دوگمەی تەختەکلیل لە شریتی سەرەوە تەختەکلیل دەکاتەوە و دایدەخات.");
                break;

            default:
                demoType = 4;
                stepIconRes = R.drawable.ic_menu_display;
                stepTitle = AsycudaMaterialUi.tr(activity,
                        "5/5 — Customize Every Click & Gesture Anytime",
                        "٥/٥ — خصّص جميع النقرات والإيماءات كما تحب",
                        "٥/٥ — هەموو کرتە و جوڵەکان بە دڵی خۆت ڕێکبخە");
                stepBody = AsycudaMaterialUi.tr(activity,
                        "• Nothing is hard-fixed! You can customize every 1-finger, 2-finger, and 3-finger tap, hold, or swipe for each Input Mode.\n• Open Control Center → Display Settings → \"Mouse, Touch & Finger Gestures\" anytime.",
                        "• لا شيء مقيد! يمكنك تغيير وظيفة أي لمسة أو ضغطة مطولة أو إيماءة لكل طريقة تحكم على حدة.\n• افتح مركز التحكم ← إعدادات العرض ← «إعدادات الماوس واللمس وإيماءات الأصابع» لتخصيص كل إصبع وسرعة المؤشر.",
                        "• هیچ شتێک جێگیر نەکراوە! دەتوانیت کاری هەر لێدانێک یان جوڵەیەکی پەنجە بگۆڕیت.\n• بڕۆ بۆ ناوەندی کۆنترۆڵ ← ڕێکخستنەکانی پیشاندان ← «ڕێکخستنی ماوس و جوڵەی پەنجەکان».");
                break;
        }

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int cardW = Math.min(dp(activity, 530), (int) (dm.widthPixels * 0.90f));
        int maxCardH = Math.min(dp(activity, 370), (int) (dm.heightPixels * 0.88f));

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(activity, 16), dp(activity, 12), dp(activity, 16), dp(activity, 12));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(AsycudaMaterialUi.SURFACE_SHEET);
        cardBg.setCornerRadius(dp(activity, 16));
        cardBg.setStroke(dp(activity, 1), AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        card.setBackground(cardBg);

        // 1. Pinned Header Row (Step Icon + Title + Close Button)
        LinearLayout headerRow = new LinearLayout(activity);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(dp(activity, 2), 0, dp(activity, 2), dp(activity, 8));

        ImageView stepIcon = new ImageView(activity);
        stepIcon.setImageResource(stepIconRes);
        stepIcon.setImageTintList(ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        LinearLayout.LayoutParams siLp = new LinearLayout.LayoutParams(dp(activity, 20), dp(activity, 20));
        siLp.rightMargin = dp(activity, 8);
        headerRow.addView(stepIcon, siLp);

        TextView titleView = new TextView(activity);
        titleView.setText(stepTitle);
        titleView.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        titleView.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        titleView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(titleView);

        headerRow.addView(createCloseIconButton(activity, () -> {
            markTutorialCompleted(activity);
            dismiss(activity);
        }));
        card.addView(headerRow);
        card.addView(createDivider(activity));

        // 2. Scrollable Middle Section (Animated Canvas + Step Explanation)
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scLp.topMargin = dp(activity, 8);
        scLp.bottomMargin = dp(activity, 8);
        scroll.setLayoutParams(scLp);

        LinearLayout middleCol = new LinearLayout(activity);
        middleCol.setOrientation(LinearLayout.VERTICAL);

        GestureDemoCanvasView demoCanvas = new GestureDemoCanvasView(activity, demoType, chosenMode);
        LinearLayout.LayoutParams canvasLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, 94));
        canvasLp.bottomMargin = dp(activity, 8);
        middleCol.addView(demoCanvas, canvasLp);

        sDemoAnimator = ValueAnimator.ofFloat(0f, 1f);
        sDemoAnimator.setDuration(2200);
        sDemoAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sDemoAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        sDemoAnimator.addUpdateListener(anim -> demoCanvas.setProgress((float) anim.getAnimatedValue()));
        sDemoAnimator.start();

        TextView bodyView = new TextView(activity);
        bodyView.setText(stepBody);
        bodyView.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        bodyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        bodyView.setLineSpacing(dp(activity, 2), 1.05f);
        middleCol.addView(bodyView);

        scroll.addView(middleCol);
        card.addView(scroll);
        card.addView(createDivider(activity));

        // 3. Pinned Always-Visible Bottom Navigation Bar (Never clipped!)
        LinearLayout navRow = new LinearLayout(activity);
        navRow.setOrientation(LinearLayout.HORIZONTAL);
        navRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams navLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        navLp.topMargin = dp(activity, 8);

        TextView changeModeBtn = createActionPill(activity,
                AsycudaMaterialUi.tr(activity, "⇄ Input Mode", "⇄ تغيير الطريقة", "⇄ گۆڕینی شێواز"),
                AsycudaMaterialUi.SURFACE_GROUPED_CARD, AsycudaMaterialUi.TEXT_SECONDARY, AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        changeModeBtn.setOnClickListener(v -> renderStage1InputMethodPicker(activity));
        navRow.addView(changeModeBtn);

        View spacer = new View(activity);
        navRow.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));

        if (stepIndex > 0) {
            TextView prevBtn = createActionPill(activity,
                    AsycudaMaterialUi.tr(activity, "← Back", "السابق", "پێشوو"),
                    AsycudaMaterialUi.SURFACE_GROUPED_CARD, AsycudaMaterialUi.TEXT_PRIMARY, AsycudaMaterialUi.SEPARATOR_HAIRLINE);
            prevBtn.setOnClickListener(v -> renderStage2Walkthrough(activity, chosenMode, stepIndex - 1));
            LinearLayout.LayoutParams prevLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            prevLp.rightMargin = dp(activity, 8);
            navRow.addView(prevBtn, prevLp);
        }

        if (stepIndex == totalSteps - 1) {
            TextView openGesturesBtn = createActionPill(activity,
                    AsycudaMaterialUi.tr(activity, "Customize Gestures", "تخصيص الإيماءات", "ڕێکخستنی جوڵەکان"),
                    "#240A84FF", "#FF0A84FF", "#FF0A84FF");
            openGesturesBtn.setOnClickListener(v -> {
                markTutorialCompleted(activity);
                dismiss(activity);
                AsycudaMaterialUi.showMouseAndGesturesModal(activity);
            });
            LinearLayout.LayoutParams ogLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ogLp.rightMargin = dp(activity, 8);
            navRow.addView(openGesturesBtn, ogLp);
        }

        boolean isLast = (stepIndex == totalSteps - 1);
        String finishLabel = AsycudaMaterialUi.tr(activity, "✓ Finish & Start", "✓ ابدأ العمل", "✓ دەستپێکردن");
        String nextLabel = AsycudaMaterialUi.tr(activity, "Next →", "التالي ←", "دواتر ←");
        TextView nextBtn = createActionPill(activity,
                isLast ? finishLabel : nextLabel,
                isLast ? "#FF30D158" : "#FF0A84FF", "#FFFFFFFF",
                isLast ? "#FF248A3D" : "#FF0A84FF");
        nextBtn.setOnClickListener(v -> {
            if (isLast) {
                markTutorialCompleted(activity);
                dismiss(activity);
            } else {
                renderStage2Walkthrough(activity, chosenMode, stepIndex + 1);
            }
        });
        navRow.addView(nextBtn);

        card.addView(navRow, navLp);

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(cardW, maxCardH, Gravity.CENTER);
        sOverlayRoot.addView(card, cardLp);
    }

    private static View createDivider(Context ctx) {
        View v = new View(ctx);
        v.setBackgroundColor(AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(ctx, 1))));
        return v;
    }

    private static FrameLayout createCloseIconButton(Context ctx, Runnable onClick) {
        FrameLayout box = new FrameLayout(ctx);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(AsycudaMaterialUi.SURFACE_GROUPED_CARD);
        bg.setCornerRadius(dp(ctx, 8));
        bg.setStroke(Math.max(1, dp(ctx, 1)), AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        box.setBackground(bg);

        ImageView iv = new ImageView(ctx);
        iv.setImageResource(R.drawable.ic_action_close);
        iv.setImageTintList(ColorStateList.valueOf(AsycudaMaterialUi.TEXT_SECONDARY));
        box.addView(iv, new FrameLayout.LayoutParams(dp(ctx, 15), dp(ctx, 15), Gravity.CENTER));

        box.setClickable(true);
        box.setFocusable(true);
        box.setOnClickListener(v -> onClick.run());
        box.setLayoutParams(new LinearLayout.LayoutParams(dp(ctx, 28), dp(ctx, 28)));
        return box;
    }

    private static TextView createActionPill(Context ctx, String text, String bgHex, String fgHex, String strokeHex) {
        return createActionPill(ctx, text, Color.parseColor(bgHex), Color.parseColor(fgHex), Color.parseColor(strokeHex));
    }

    private static TextView createActionPill(Context ctx, String text, int bgColor, int fgColor, int strokeColor) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(fgColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tv.setTypeface(Typeface.create(SANS_SERIF_MEDIUM, Typeface.BOLD));
        tv.setPadding(dp(ctx, 13), dp(ctx, 7), dp(ctx, 13), dp(ctx, 7));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dp(ctx, 9));
        bg.setStroke(Math.max(1, dp(ctx, 1)), strokeColor);
        tv.setBackground(bg);
        return tv;
    }

    private static int getCurrentTouchMode(Context ctx) {
        try {
            if (ctx instanceof MainActivity && ((MainActivity) ctx).prefs != null) {
                return Integer.parseInt(((MainActivity) ctx).prefs.touchMode.get());
            }
        } catch (Exception ignored) {
            // Default to trackpad mode when preference is unset or unparsable
        }
        return 1;
    }

    private static void applyTouchMode(Activity activity, int mode) {
        try {
            if (activity instanceof MainActivity) {
                MainActivity ma = (MainActivity) activity;
                if (ma.prefs != null) {
                    ma.prefs.touchMode.put(String.valueOf(mode));
                }
                ma.onPreferencesChanged("");
            }
            SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            sp.edit().putInt("selected_input_mode", mode).apply();
        } catch (Exception ignored) {
            // Ignore preference persistence errors
        }
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }

    /**
     * Custom animated canvas illustrating finger taps, multi-finger swipes, cursor motion,
     * and the floating Control Center pill + attached Baby Calculator icon capsule.
     */
    private static final class GestureDemoCanvasView extends View {
        private final int mDemoType;
        private final int mInputMode;
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF mRect = new RectF();
        private float mProgress = 0f;

        GestureDemoCanvasView(Context ctx, int demoType, int inputMode) {
            super(ctx);
            mDemoType = demoType;
            mInputMode = inputMode;
        }

        void setProgress(float progress) {
            mProgress = progress;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            if (w <= 0 || h <= 0) return;

            // Background stage box matching SURFACE_INPUT_FIELD
            mRect.set(0, 0, w, h);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(AsycudaMaterialUi.SURFACE_INPUT_FIELD);
            canvas.drawRoundRect(mRect, dp(getContext(), 10), dp(getContext(), 10), mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dp(getContext(), 1));
            mPaint.setColor(AsycudaMaterialUi.SEPARATOR_HAIRLINE);
            canvas.drawRoundRect(mRect, dp(getContext(), 10), dp(getContext(), 10), mPaint);

            float cx = w / 2f;
            float cy = h / 2f;

            if (mDemoType == 0) {
                // Step 1: 1-finger glide + Left Click
                float startX = w * 0.28f;
                float endX = w * 0.72f;
                float curX = startX + (endX - startX) * (float) Math.sin(mProgress * Math.PI);
                float curY = cy + dp(getContext(), 8) * (float) Math.cos(mProgress * Math.PI * 2);

                drawFingerTouch(canvas, curX, curY, "#FF0A84FF", mProgress > 0.75f);

                float ptrX = (mInputMode == 1) ? curX + dp(getContext(), 26) : curX;
                float ptrY = (mInputMode == 1) ? curY - dp(getContext(), 20) : curY;
                drawCursorPointer(canvas, ptrX, ptrY);

            } else if (mDemoType == 1) {
                // Step 2: 2-finger tap (or 1-finger hold in Direct Touchscreen) -> Right-Click Menu
                float pulse = (float) Math.abs(Math.sin(mProgress * Math.PI * 2));
                if (mInputMode >= 2) {
                    drawFingerTouch(canvas, cx - dp(getContext(), 30), cy, "#FFFF9F0A", true);
                } else {
                    drawFingerTouch(canvas, cx - dp(getContext(), 46), cy + dp(getContext(), 5), "#FF0A84FF", pulse > 0.5f);
                    drawFingerTouch(canvas, cx - dp(getContext(), 16), cy - dp(getContext(), 5), "#FF0A84FF", pulse > 0.5f);
                }
                // Context menu box preview
                mRect.set(cx + dp(getContext(), 12), cy - dp(getContext(), 28), cx + dp(getContext(), 110), cy + dp(getContext(), 28));
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(AsycudaMaterialUi.SURFACE_GROUPED_CARD);
                canvas.drawRoundRect(mRect, dp(getContext(), 6), dp(getContext(), 6), mPaint);
                mPaint.setColor(AsycudaMaterialUi.SYSTEM_BLUE);
                canvas.drawRect(mRect.left + dp(getContext(), 8), mRect.top + dp(getContext(), 8), mRect.right - dp(getContext(), 14), mRect.top + dp(getContext(), 15), mPaint);
                mPaint.setColor(AsycudaMaterialUi.TEXT_SECONDARY);
                canvas.drawRect(mRect.left + dp(getContext(), 8), mRect.top + dp(getContext(), 22), mRect.right - dp(getContext(), 24), mRect.top + dp(getContext(), 29), mPaint);
                canvas.drawRect(mRect.left + dp(getContext(), 8), mRect.top + dp(getContext(), 36), mRect.right - dp(getContext(), 18), mRect.top + dp(getContext(), 43), mPaint);

            } else if (mDemoType == 2) {
                // Step 3: 2-finger scroll & 3-finger swipe up/down
                float dy = (0.5f - mProgress) * dp(getContext(), 38);
                drawFingerTouch(canvas, w * 0.26f - dp(getContext(), 14), cy + dy, "#FF0A84FF", false);
                drawFingerTouch(canvas, w * 0.26f + dp(getContext(), 14), cy + dy, "#FF0A84FF", false);
                drawFingerTouch(canvas, w * 0.72f - dp(getContext(), 22), cy - dy, "#FF30D158", false);
                drawFingerTouch(canvas, w * 0.72f, cy - dy, "#FF30D158", false);
                drawFingerTouch(canvas, w * 0.72f + dp(getContext(), 22), cy - dy, "#FF30D158", false);

            } else if (mDemoType == 3) {
                // Step 4: Sleek ASYCUDA Floating Pill + Attached Calculator Icon Capsule expanding into Calculator Window
                float pillLeft = w * 0.14f;
                float pillTop = cy - dp(getContext(), 15);
                float pillRight = pillLeft + dp(getContext(), 110);
                float pillBottom = cy + dp(getContext(), 15);

                // Draw outer pill
                mRect.set(pillLeft, pillTop, pillRight, pillBottom);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.parseColor("#E61C1C1E"));
                canvas.drawRoundRect(mRect, dp(getContext(), 15), dp(getContext(), 15), mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(dp(getContext(), 1));
                mPaint.setColor(AsycudaMaterialUi.SEPARATOR_HAIRLINE);
                canvas.drawRoundRect(mRect, dp(getContext(), 15), dp(getContext(), 15), mPaint);

                // Draw ASYCUDA dot + text bar inside left of pill
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(AsycudaMaterialUi.SYSTEM_BLUE);
                canvas.drawCircle(pillLeft + dp(getContext(), 14), cy, dp(getContext(), 5), mPaint);
                mPaint.setColor(Color.WHITE);
                mRect.set(pillLeft + dp(getContext(), 24), cy - dp(getContext(), 3), pillLeft + dp(getContext(), 68), cy + dp(getContext(), 3));
                canvas.drawRoundRect(mRect, dp(getContext(), 2), dp(getContext(), 2), mPaint);

                // Draw attached Baby Calculator capsule on right side of pill
                float calcBtnLeft = pillLeft + dp(getContext(), 76);
                float calcBtnRight = pillRight - dp(getContext(), 5);
                mRect.set(calcBtnLeft, pillTop + dp(getContext(), 4), calcBtnRight, pillBottom - dp(getContext(), 4));
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(AsycudaMaterialUi.SURFACE_GROUPED_CARD);
                canvas.drawRoundRect(mRect, dp(getContext(), 9), dp(getContext(), 9), mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(AsycudaMaterialUi.SYSTEM_BLUE);
                canvas.drawRoundRect(mRect, dp(getContext(), 9), dp(getContext(), 9), mPaint);

                // Green saved-state indicator dot on baby calculator capsule
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(AsycudaMaterialUi.SYSTEM_GREEN);
                canvas.drawCircle(calcBtnRight - dp(getContext(), 3), pillTop + dp(getContext(), 6), dp(getContext(), 3), mPaint);

                // Animated expanding calculator window preview
                float animPhase = (float) Math.sin(mProgress * Math.PI);
                float scale = 0.25f + 0.75f * animPhase;
                float winW = dp(getContext(), 84) * scale;
                float winH = dp(getContext(), 64) * scale;
                float startCx = (calcBtnLeft + calcBtnRight) / 2f;
                float targetCx = w * 0.72f;
                float winCx = startCx + (targetCx - startCx) * animPhase;

                mRect.set(winCx - winW / 2f, cy - winH / 2f, winCx + winW / 2f, cy + winH / 2f);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(AsycudaMaterialUi.SURFACE_GROUPED_CARD);
                canvas.drawRoundRect(mRect, dp(getContext(), 8), dp(getContext(), 8), mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(AsycudaMaterialUi.SYSTEM_BLUE);
                canvas.drawRoundRect(mRect, dp(getContext(), 8), dp(getContext(), 8), mPaint);

                // Calculator screen bar inside expanding preview
                if (scale > 0.55f) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(AsycudaMaterialUi.SURFACE_INPUT_FIELD);
                    canvas.drawRect(mRect.left + dp(getContext(), 6), mRect.top + dp(getContext(), 8),
                            mRect.right - dp(getContext(), 6), mRect.top + dp(getContext(), 20), mPaint);
                }

            } else {
                // Step 5: Custom Gesture Mapping visual
                for (int i = 0; i < 3; i++) {
                    float bx = w * (0.22f + i * 0.28f);
                    boolean active = ((int) (mProgress * 3) % 3) == i;
                    drawFingerTouch(canvas, bx, cy, active ? "#FF30D158" : "#FF0A84FF", active);
                }
            }
        }

        private void drawFingerTouch(Canvas canvas, float x, float y, String hexColor, boolean ripple) {
            int color = Color.parseColor(hexColor);
            if (ripple) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(dp(getContext(), 2));
                mPaint.setColor(color);
                mPaint.setAlpha(120);
                canvas.drawCircle(x, y, dp(getContext(), 18), mPaint);
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(color);
            mPaint.setAlpha(215);
            canvas.drawCircle(x, y, dp(getContext(), 10), mPaint);
            mPaint.setAlpha(255);
        }

        private void drawCursorPointer(Canvas canvas, float x, float y) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, dp(getContext(), 4), mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dp(getContext(), 1));
            mPaint.setColor(AsycudaMaterialUi.SYSTEM_BLUE);
            canvas.drawCircle(x, y, dp(getContext(), 6), mPaint);
        }
    }
}
