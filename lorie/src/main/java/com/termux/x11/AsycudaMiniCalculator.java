package com.termux.x11;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Freeform, draggable, edge-resizable Desktop-style Mini Calculator window attached to the
 * floating Control Center pill's baby calculator button (🧮).
 *
 * - Hide (—): Collapses with a smooth animation straight into the attached baby calculator button
 *   on the floating Control Center pill while preserving all calculator state in memory.
 * - Close (✕): Destroys the window and wipes calculator state so the next launch starts fresh.
 */
@SuppressLint("SetTextI18n")
@SuppressWarnings({"java:S3776", "java:S107", "java:S1141", "java:S1192", "java:S1172", "java:S2696", "java:S9439"})
public final class AsycudaMiniCalculator {

    private static final String PREF_NAME = "asycuda_prefs";
    private static final String KEY_CALC_FEATURE_ENABLED = "mini_calc_feature_enabled";
    private static final String KEY_CALC_WIN_X = "mini_calc_win_x";
    private static final String KEY_CALC_WIN_Y = "mini_calc_win_y";
    private static final String KEY_CALC_WIN_W = "mini_calc_win_w";
    private static final String KEY_CALC_WIN_H = "mini_calc_win_h";

    // Preserved session state across Hide (—) / Expand cycles
    private static String sCurrentInput = "0";
    private static String sExpressionHistory = "";
    private static BigDecimal sAccumulator = null;
    private static String sPendingOperator = null;
    private static boolean sResetInputOnNextDigit = false;
    private static boolean sIsHiddenStatePreserved = false;

    // Active floating window reference
    private static FrameLayout sWindowRoot = null;
    private static TextView sHistoryView = null;
    private static TextView sDisplayView = null;
    private static boolean sIsWindowVisible = false;
    private static boolean sIsAnimating = false;

    private AsycudaMiniCalculator() {}

    public static boolean isFeatureEnabled(Context ctx) {
        if (ctx == null) return true;
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_CALC_FEATURE_ENABLED, true);
    }

    public static void setFeatureEnabled(Activity activity, boolean enabled) {
        if (activity == null) return;
        activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_CALC_FEATURE_ENABLED, enabled)
                .apply();
        if (!enabled) {
            closeAndDestroy(activity, false);
        }
        AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
    }

    public static boolean hasPreservedHiddenState() {
        return sIsHiddenStatePreserved;
    }

    public static boolean isWindowOpen() {
        return sIsWindowVisible && sWindowRoot != null && sWindowRoot.getParent() != null;
    }

    /**
     * Toggles the Mini Calculator window from the baby button or a gesture.
     */
    public static void toggleWindow(Activity activity, View anchorBabyButton) {
        if (activity == null || activity.isFinishing()) return;
        if (sIsAnimating) return;

        if (isWindowOpen()) {
            hideToBabyButton(activity, anchorBabyButton);
        } else {
            expandFromBabyButton(activity, anchorBabyButton);
        }
    }

    /**
     * Opens / expands the floating calculator window with an expanding animation from the baby button.
     */
    public static void expandFromBabyButton(Activity activity, View anchorBabyButton) {
        if (activity == null || activity.isFinishing()) return;
        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        // If not preserving state from Hide (—), reset state fresh
        if (!sIsHiddenStatePreserved) {
            resetCalculatorMemory();
        }

        if (sWindowRoot != null && sWindowRoot.getParent() instanceof ViewGroup) {
            ((ViewGroup) sWindowRoot.getParent()).removeView(sWindowRoot);
        }

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int minW = dp(activity, 185);
        int minH = dp(activity, 210);
        int maxW = Math.min(dp(activity, 420), (int) (dm.widthPixels * 0.80f));
        int maxH = Math.min(dp(activity, 460), (int) (dm.heightPixels * 0.92f));
        int defaultW = dp(activity, 235);
        int defaultH = Math.min(maxH, dp(activity, 275));

        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int savedW = Math.max(minW, Math.min(maxW, prefs.getInt(KEY_CALC_WIN_W, defaultW)));
        int savedH = Math.max(minH, Math.min(maxH, prefs.getInt(KEY_CALC_WIN_H, defaultH)));
        int defaultX = Math.max(dp(activity, 16), (dm.widthPixels - savedW) / 2);
        int defaultY = Math.max(dp(activity, 42), (dm.heightPixels - savedH) / 2);
        int savedX = Math.max(dp(activity, 8), Math.min(dm.widthPixels - savedW - dp(activity, 8), prefs.getInt(KEY_CALC_WIN_X, defaultX)));
        int savedY = Math.max(dp(activity, 28), Math.min(dm.heightPixels - savedH - dp(activity, 8), prefs.getInt(KEY_CALC_WIN_Y, defaultY)));

        sWindowRoot = buildWindowView(activity, root, anchorBabyButton, savedW, savedH, minW, minH, maxW, maxH);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(savedW, savedH);
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.leftMargin = savedX;
        lp.topMargin = savedY;
        root.addView(sWindowRoot, lp);
        sIsWindowVisible = true;
        sIsHiddenStatePreserved = true;
        AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);

        // Compute animation start delta from the attached baby button on the Control Center pill
        float startTx = 0f;
        float startTy = 0f;
        if (anchorBabyButton != null && anchorBabyButton.isShown()) {
            int[] btnLoc = new int[2];
            int[] rootLoc = new int[2];
            anchorBabyButton.getLocationOnScreen(btnLoc);
            root.getLocationOnScreen(rootLoc);
            float btnCenterX = (btnLoc[0] - rootLoc[0]) + (anchorBabyButton.getWidth() / 2f);
            float btnCenterY = (btnLoc[1] - rootLoc[1]) + (anchorBabyButton.getHeight() / 2f);
            float winCenterX = savedX + (savedW / 2f);
            float winCenterY = savedY + (savedH / 2f);
            startTx = btnCenterX - winCenterX;
            startTy = btnCenterY - winCenterY;
        }

        sIsAnimating = true;
        sWindowRoot.setPivotX(savedW / 2f);
        sWindowRoot.setPivotY(savedH / 2f);
        sWindowRoot.setTranslationX(startTx);
        sWindowRoot.setTranslationY(startTy);
        sWindowRoot.setScaleX(0.12f);
        sWindowRoot.setScaleY(0.12f);
        sWindowRoot.setAlpha(0.15f);

        sWindowRoot.animate()
                .translationX(0f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(260)
                .setInterpolator(new OvershootInterpolator(0.85f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        sIsAnimating = false;
                    }
                })
                .start();
    }

    /**
     * Collapses the floating calculator window with a smooth animation back into the attached
     * baby button on the Control Center pill, preserving all calculator state in memory.
     */
    public static void hideToBabyButton(Activity activity, View anchorBabyButton) {
        if (activity == null || sWindowRoot == null || !sIsWindowVisible) return;
        FrameLayout root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        View targetBtn = anchorBabyButton != null ? anchorBabyButton : AsycudaStandaloneManager.getFloatingCalculatorBabyView();
        float targetTx = 0f;
        float targetTy = 0f;
        if (targetBtn != null && targetBtn.isShown()) {
            int[] btnLoc = new int[2];
            int[] rootLoc = new int[2];
            targetBtn.getLocationOnScreen(btnLoc);
            root.getLocationOnScreen(rootLoc);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) sWindowRoot.getLayoutParams();
            float btnCenterX = (btnLoc[0] - rootLoc[0]) + (targetBtn.getWidth() / 2f);
            float btnCenterY = (btnLoc[1] - rootLoc[1]) + (targetBtn.getHeight() / 2f);
            float winCenterX = lp.leftMargin + (lp.width / 2f);
            float winCenterY = lp.topMargin + (lp.height / 2f);
            targetTx = btnCenterX - winCenterX;
            targetTy = btnCenterY - winCenterY;
        }

        sIsHiddenStatePreserved = true;
        sIsAnimating = true;
        final FrameLayout viewToRemove = sWindowRoot;

        viewToRemove.animate()
                .translationX(targetTx)
                .translationY(targetTy)
                .scaleX(0.10f)
                .scaleY(0.10f)
                .alpha(0f)
                .setDuration(230)
                .setInterpolator(new DecelerateInterpolator(1.4f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        sIsAnimating = false;
                        sIsWindowVisible = false;
                        if (viewToRemove.getParent() instanceof ViewGroup) {
                            ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                        }
                        AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
                    }
                })
                .start();
    }

    /**
     * Destroys the calculator window completely and wipes calculator memory so the next launch
     * starts completely fresh.
     */
    public static void closeAndDestroy(Activity activity, boolean animate) {
        sIsHiddenStatePreserved = false;
        resetCalculatorMemory();

        if (sWindowRoot == null) {
            sIsWindowVisible = false;
            if (activity != null) {
                AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
            }
            return;
        }

        final FrameLayout viewToRemove = sWindowRoot;
        sWindowRoot = null;
        sDisplayView = null;
        sHistoryView = null;
        sIsWindowVisible = false;

        if (animate && activity != null && !activity.isFinishing()) {
            sIsAnimating = true;
            viewToRemove.animate()
                    .scaleX(0.75f)
                    .scaleY(0.75f)
                    .alpha(0f)
                    .setDuration(160)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            sIsAnimating = false;
                            if (viewToRemove.getParent() instanceof ViewGroup) {
                                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                            }
                            AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
                        }
                    })
                    .start();
        } else {
            if (viewToRemove.getParent() instanceof ViewGroup) {
                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
            }
            if (activity != null) {
                AsycudaStandaloneManager.refreshFloatingCalculatorBabyButton(activity);
            }
        }
    }

    private static void resetCalculatorMemory() {
        sCurrentInput = "0";
        sExpressionHistory = "";
        sAccumulator = null;
        sPendingOperator = null;
        sResetInputOnNextDigit = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private static FrameLayout buildWindowView(
            Activity activity,
            FrameLayout parentContainer,
            View anchorBabyButton,
            int initialW,
            int initialH,
            int minW,
            int minH,
            int maxW,
            int maxH) {

        boolean ar = AsycudaMaterialUi.isArabic(activity);
        boolean ku = AsycudaMaterialUi.isKurdish(activity);

        FrameLayout windowContainer = new FrameLayout(activity);
        GradientDrawable winBg = new GradientDrawable();
        winBg.setColor(AsycudaMaterialUi.SURFACE_SHEET);
        winBg.setCornerRadius(dp(activity, 14));
        winBg.setStroke(dp(activity, 1), AsycudaMaterialUi.SEPARATOR_HAIRLINE);
        windowContainer.setBackground(winBg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            windowContainer.setElevation(dp(activity, 18));
        }

        LinearLayout mainColumn = new LinearLayout(activity);
        mainColumn.setOrientation(LinearLayout.VERTICAL);
        mainColumn.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 1. Desktop-style Top Titlebar (Draggable + Hide "—" and Close "✕" buttons)
        LinearLayout titleBar = new LinearLayout(activity);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(activity, 10), dp(activity, 6), dp(activity, 6), dp(activity, 6));
        GradientDrawable titleBg = new GradientDrawable();
        titleBg.setColor(AsycudaMaterialUi.SURFACE_GROUPED_CARD);
        float r = dp(activity, 13);
        titleBg.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
        titleBar.setBackground(titleBg);

        android.widget.ImageView titleIcon = new android.widget.ImageView(activity);
        titleIcon.setImageResource(R.drawable.ic_menu_calculator);
        titleIcon.setImageTintList(android.content.res.ColorStateList.valueOf(AsycudaMaterialUi.SYSTEM_BLUE));
        LinearLayout.LayoutParams tiLp = new LinearLayout.LayoutParams(dp(activity, 15), dp(activity, 15));
        tiLp.rightMargin = dp(activity, 6);
        titleBar.addView(titleIcon, tiLp);

        TextView titleLabel = new TextView(activity);
        titleLabel.setText(ar ? "حاسبة الجمارك" : (ku ? "ژمێرەری گومرگ" : "Customs Calculator"));
        titleLabel.setTextColor(AsycudaMaterialUi.TEXT_PRIMARY);
        titleLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        titleLabel.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleBar.addView(titleLabel, titleLp);

        // Hide button (—) -> Collapses into baby button on floating pill, keeps state
        TextView hideBtn = createTitleBarButton(activity, "—", "#334155", "#38BDF8");
        hideBtn.setContentDescription("Hide Calculator (Keep State)");
        hideBtn.setOnClickListener(v -> {
            vibrateTick(activity);
            hideToBabyButton(activity, AsycudaStandaloneManager.getFloatingCalculatorBabyView());
        });
        LinearLayout.LayoutParams hideLp = new LinearLayout.LayoutParams(dp(activity, 28), dp(activity, 24));
        hideLp.rightMargin = dp(activity, 5);
        titleBar.addView(hideBtn, hideLp);

        // Close button (✕) -> Destroys window and clears state
        TextView closeBtn = createTitleBarButton(activity, "✕", "#451A1A", "#F87171");
        closeBtn.setContentDescription("Close Calculator (Clear State)");
        closeBtn.setOnClickListener(v -> {
            vibrateTick(activity);
            closeAndDestroy(activity, true);
        });
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(activity, 28), dp(activity, 24));
        titleBar.addView(closeBtn, closeLp);

        // Titlebar Drag Listener
        titleBar.setOnTouchListener(new View.OnTouchListener() {
            float downRawX, downRawY;
            int startLeft, startTop;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) windowContainer.getLayoutParams();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        startLeft = lp.leftMargin;
                        startTop = lp.topMargin;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dx = Math.round(event.getRawX() - downRawX);
                        int dy = Math.round(event.getRawY() - downRawY);
                        int maxLeft = Math.max(0, parentContainer.getWidth() - lp.width);
                        int maxTop = Math.max(0, parentContainer.getHeight() - lp.height);
                        lp.leftMargin = Math.max(0, Math.min(maxLeft, startLeft + dx));
                        lp.topMargin = Math.max(0, Math.min(maxTop, startTop + dy));
                        windowContainer.setLayoutParams(lp);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        saveWindowBounds(activity, lp.leftMargin, lp.topMargin, lp.width, lp.height);
                        return true;
                }
                return false;
            }
        });

        mainColumn.addView(titleBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 2. Display Screen (Tap to copy value to clipboard)
        LinearLayout displayBox = new LinearLayout(activity);
        displayBox.setOrientation(LinearLayout.VERTICAL);
        displayBox.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        displayBox.setPadding(dp(activity, 10), dp(activity, 6), dp(activity, 10), dp(activity, 6));
        GradientDrawable dispBg = new GradientDrawable();
        dispBg.setColor(Color.parseColor("#0B0F17"));
        dispBg.setCornerRadius(dp(activity, 8));
        dispBg.setStroke(dp(activity, 1), Color.parseColor("#1E293B"));
        displayBox.setBackground(dispBg);

        sHistoryView = new TextView(activity);
        sHistoryView.setTextColor(Color.parseColor("#94A3B8"));
        sHistoryView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        sHistoryView.setGravity(Gravity.END);
        sHistoryView.setSingleLine(true);
        sHistoryView.setText(sExpressionHistory);
        displayBox.addView(sHistoryView);

        sDisplayView = new TextView(activity);
        sDisplayView.setTextColor(Color.parseColor("#F8FAFC"));
        sDisplayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f);
        sDisplayView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        sDisplayView.setGravity(Gravity.END);
        sDisplayView.setSingleLine(true);
        sDisplayView.setText(formatNumberForDisplay(sCurrentInput));
        displayBox.addView(sDisplayView);

        displayBox.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("ASYCUDA Calculator", sCurrentInput));
                vibrateTick(activity);
                Toast.makeText(activity,
                        ar ? "تم نسخ النتيجة: " + sCurrentInput :
                                (ku ? "ئەنجام کۆپی کرا: " + sCurrentInput : "Copied: " + sCurrentInput),
                        Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout.LayoutParams dispLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dispLp.setMargins(dp(activity, 8), dp(activity, 6), dp(activity, 8), dp(activity, 4));
        mainColumn.addView(displayBox, dispLp);

        // 3. Calculator Keypad Grid (5 rows x 4 columns)
        GridLayout grid = new GridLayout(activity);
        grid.setColumnCount(4);
        grid.setRowCount(5);
        grid.setUseDefaultMargins(false);
        LinearLayout.LayoutParams gridLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        gridLp.setMargins(dp(activity, 6), dp(activity, 2), dp(activity, 6), dp(activity, 8));

        String[][] keys = new String[][]{
                {"C", "⌫", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"±", "0", ".", "="}
        };

        final java.util.List<TextView> keypadButtons = new java.util.ArrayList<>(20);

        for (int rIdx = 0; rIdx < keys.length; rIdx++) {
            for (int cIdx = 0; cIdx < keys[rIdx].length; cIdx++) {
                final String key = keys[rIdx][cIdx];
                TextView keyBtn = createCalcKeyButton(activity, key);
                keyBtn.setOnClickListener(v -> {
                    vibrateTick(activity);
                    onCalculatorKeyPressed(key);
                });
                keypadButtons.add(keyBtn);

                GridLayout.LayoutParams cellLp = new GridLayout.LayoutParams(
                        GridLayout.spec(rIdx, 1, 1f),
                        GridLayout.spec(cIdx, 1, 1f));
                cellLp.width = 0;
                cellLp.height = 0;
                int m = dp(activity, 2);
                cellLp.setMargins(m, m, m, m);
                grid.addView(keyBtn, cellLp);
            }
        }

        mainColumn.addView(grid, gridLp);
        windowContainer.addView(mainColumn);

        // Helper to scale display & keypad typography smoothly when resizing vertically/horizontally
        final Runnable applyResponsiveScale = () -> {
            FrameLayout.LayoutParams curLp = (FrameLayout.LayoutParams) windowContainer.getLayoutParams();
            int curH = curLp != null ? curLp.height : initialH;
            float ratio = Math.max(0f, Math.min(1f, (float) (curH - minH) / Math.max(1, (dp(activity, 340) - minH))));
            float keySp = 12.5f + (3.5f * ratio);
            float dispSp = 17.5f + (4.5f * ratio);
            if (sDisplayView != null) {
                sDisplayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dispSp);
            }
            for (TextView kb : keypadButtons) {
                kb.setTextSize(TypedValue.COMPLEX_UNIT_SP, keySp);
            }
        };

        // 4A. Bottom Edge Vertical Resize Strip
        View bottomEdgeHandle = new View(activity);
        FrameLayout.LayoutParams bottomEdgeLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, 12));
        bottomEdgeLp.gravity = Gravity.BOTTOM;
        bottomEdgeLp.rightMargin = dp(activity, 28);
        windowContainer.addView(bottomEdgeHandle, bottomEdgeLp);

        // 4B. Right Edge Horizontal Resize Strip
        View rightEdgeHandle = new View(activity);
        FrameLayout.LayoutParams rightEdgeLp = new FrameLayout.LayoutParams(
                dp(activity, 12), ViewGroup.LayoutParams.MATCH_PARENT);
        rightEdgeLp.gravity = Gravity.END;
        rightEdgeLp.topMargin = dp(activity, 34);
        rightEdgeLp.bottomMargin = dp(activity, 28);
        windowContainer.addView(rightEdgeHandle, rightEdgeLp);

        // 4C. Bottom-Right Corner Diagonal Resize Handle (Width + Height)
        TextView resizeHandle = new TextView(activity);
        resizeHandle.setText("◢");
        resizeHandle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        resizeHandle.setTextColor(Color.parseColor("#38BDF8"));
        resizeHandle.setGravity(Gravity.BOTTOM | Gravity.END);
        resizeHandle.setPadding(0, 0, dp(activity, 3), dp(activity, 1));
        FrameLayout.LayoutParams resizeLp = new FrameLayout.LayoutParams(dp(activity, 30), dp(activity, 30));
        resizeLp.gravity = Gravity.BOTTOM | Gravity.END;
        windowContainer.addView(resizeHandle, resizeLp);

        View.OnTouchListener createResizeTouchListener = null;
        bottomEdgeHandle.setOnTouchListener(new View.OnTouchListener() {
            float downRawY;
            int startH, startTop;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) windowContainer.getLayoutParams();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawY = event.getRawY();
                        startH = lp.height;
                        startTop = lp.topMargin;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dh = Math.round(event.getRawY() - downRawY);
                        int newH = Math.max(minH, Math.min(maxH, startH + dh));
                        lp.height = newH;
                        if (parentContainer.getHeight() > 0 && startTop + newH > parentContainer.getHeight()) {
                            lp.topMargin = Math.max(0, parentContainer.getHeight() - newH);
                        }
                        windowContainer.setLayoutParams(lp);
                        applyResponsiveScale.run();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        saveWindowBounds(activity, lp.leftMargin, lp.topMargin, lp.width, lp.height);
                        return true;
                }
                return false;
            }
        });

        rightEdgeHandle.setOnTouchListener(new View.OnTouchListener() {
            float downRawX;
            int startW, startLeft;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) windowContainer.getLayoutParams();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawX = event.getRawX();
                        startW = lp.width;
                        startLeft = lp.leftMargin;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dw = Math.round(event.getRawX() - downRawX);
                        int newW = Math.max(minW, Math.min(maxW, startW + dw));
                        lp.width = newW;
                        if (parentContainer.getWidth() > 0 && startLeft + newW > parentContainer.getWidth()) {
                            lp.leftMargin = Math.max(0, parentContainer.getWidth() - newW);
                        }
                        windowContainer.setLayoutParams(lp);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        saveWindowBounds(activity, lp.leftMargin, lp.topMargin, lp.width, lp.height);
                        return true;
                }
                return false;
            }
        });

        resizeHandle.setOnTouchListener(new View.OnTouchListener() {
            float downRawX, downRawY;
            int startW, startH, startLeft, startTop;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) windowContainer.getLayoutParams();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        startW = lp.width;
                        startH = lp.height;
                        startLeft = lp.leftMargin;
                        startTop = lp.topMargin;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dw = Math.round(event.getRawX() - downRawX);
                        int dh = Math.round(event.getRawY() - downRawY);
                        int newW = Math.max(minW, Math.min(maxW, startW + dw));
                        int newH = Math.max(minH, Math.min(maxH, startH + dh));
                        lp.width = newW;
                        lp.height = newH;
                        if (parentContainer.getWidth() > 0 && startLeft + newW > parentContainer.getWidth()) {
                            lp.leftMargin = Math.max(0, parentContainer.getWidth() - newW);
                        }
                        if (parentContainer.getHeight() > 0 && startTop + newH > parentContainer.getHeight()) {
                            lp.topMargin = Math.max(0, parentContainer.getHeight() - newH);
                        }
                        windowContainer.setLayoutParams(lp);
                        applyResponsiveScale.run();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        saveWindowBounds(activity, lp.leftMargin, lp.topMargin, lp.width, lp.height);
                        return true;
                }
                return false;
            }
        });

        windowContainer.post(applyResponsiveScale);

        return windowContainer;
    }

    private static TextView createTitleBarButton(Context ctx, String label, String bgHex, String textHex) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(Color.parseColor(textHex));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgHex));
        bg.setCornerRadius(dp(ctx, 6));
        tv.setBackground(bg);
        return tv;
    }

    private static TextView createCalcKeyButton(Context ctx, String key) {
        TextView tv = new TextView(ctx);
        tv.setText(key);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);

        String bgHex = "#1E293B";
        String textHex = "#F8FAFC";
        if ("=".equals(key)) {
            bgHex = "#0284C7";
            textHex = "#FFFFFF";
        } else if ("+".equals(key) || "−".equals(key) || "×".equals(key) || "÷".equals(key)) {
            bgHex = "#0F172A";
            textHex = "#38BDF8";
        } else if ("C".equals(key)) {
            bgHex = "#3B1821";
            textHex = "#F87171";
        } else if ("⌫".equals(key) || "%".equals(key) || "±".equals(key)) {
            bgHex = "#172033";
            textHex = "#CBD5E1";
        }

        tv.setTextColor(Color.parseColor(textHex));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgHex));
        bg.setCornerRadius(dp(ctx, 8));
        bg.setStroke(dp(ctx, 1), Color.parseColor("#334155"));
        tv.setBackground(bg);
        return tv;
    }

    private static void onCalculatorKeyPressed(String key) {
        switch (key) {
            case "C":
                resetCalculatorMemory();
                break;
            case "⌫":
                if (sResetInputOnNextDigit) {
                    sCurrentInput = "0";
                    sResetInputOnNextDigit = false;
                } else if (sCurrentInput.length() > 1) {
                    sCurrentInput = sCurrentInput.substring(0, sCurrentInput.length() - 1);
                    if ("-".equals(sCurrentInput) || "-0".equals(sCurrentInput)) {
                        sCurrentInput = "0";
                    }
                } else {
                    sCurrentInput = "0";
                }
                break;
            case "±":
                if (!"0".equals(sCurrentInput) && !"Error".equals(sCurrentInput)) {
                    if (sCurrentInput.startsWith("-")) {
                        sCurrentInput = sCurrentInput.substring(1);
                    } else {
                        sCurrentInput = "-" + sCurrentInput;
                    }
                }
                break;
            case "%":
                try {
                    BigDecimal val = new BigDecimal(sCurrentInput);
                    if (sAccumulator != null && ("+".equals(sPendingOperator) || "−".equals(sPendingOperator))) {
                        // Customs duty style percentage: e.g. 1000 + 15% -> 15% of 1000 = 150
                        val = sAccumulator.multiply(val, MathContext.DECIMAL64)
                                .divide(new BigDecimal("100"), MathContext.DECIMAL64);
                    } else {
                        val = val.divide(new BigDecimal("100"), MathContext.DECIMAL64);
                    }
                    sCurrentInput = stripTrailingZeros(val);
                    sResetInputOnNextDigit = true;
                } catch (Exception ignored) {
                    sCurrentInput = "0";
                }
                break;
            case "+":
            case "−":
            case "×":
            case "÷":
                handleOperator(key);
                break;
            case "=":
                handleEquals();
                break;
            case ".":
                if (sResetInputOnNextDigit || "Error".equals(sCurrentInput)) {
                    sCurrentInput = "0.";
                    sResetInputOnNextDigit = false;
                } else if (!sCurrentInput.contains(".")) {
                    sCurrentInput += ".";
                }
                break;
            default:
                // Digit 0-9
                if (sResetInputOnNextDigit || "0".equals(sCurrentInput) || "Error".equals(sCurrentInput)) {
                    sCurrentInput = key;
                    sResetInputOnNextDigit = false;
                } else if (sCurrentInput.replace("-", "").replace(".", "").length() < 15) {
                    sCurrentInput += key;
                }
                break;
        }
        refreshDisplayViews();
    }

    private static void handleOperator(String op) {
        try {
            BigDecimal current = new BigDecimal(sCurrentInput);
            if (sAccumulator == null) {
                sAccumulator = current;
            } else if (sPendingOperator != null && !sResetInputOnNextDigit) {
                sAccumulator = evaluate(sAccumulator, current, sPendingOperator);
                sCurrentInput = stripTrailingZeros(sAccumulator);
            }
            sPendingOperator = op;
            sExpressionHistory = formatNumberForDisplay(stripTrailingZeros(sAccumulator)) + " " + op;
            sResetInputOnNextDigit = true;
        } catch (Exception e) {
            sCurrentInput = "Error";
            sExpressionHistory = "";
            sAccumulator = null;
            sPendingOperator = null;
            sResetInputOnNextDigit = true;
        }
    }

    private static void handleEquals() {
        if (sAccumulator == null || sPendingOperator == null) return;
        try {
            BigDecimal right = new BigDecimal(sCurrentInput);
            String leftStr = formatNumberForDisplay(stripTrailingZeros(sAccumulator));
            String rightStr = formatNumberForDisplay(stripTrailingZeros(right));
            BigDecimal result = evaluate(sAccumulator, right, sPendingOperator);
            sExpressionHistory = leftStr + " " + sPendingOperator + " " + rightStr + " =";
            sCurrentInput = stripTrailingZeros(result);
            sAccumulator = null;
            sPendingOperator = null;
            sResetInputOnNextDigit = true;
        } catch (ArithmeticException ae) {
            sCurrentInput = "Error";
            sExpressionHistory = "";
            sAccumulator = null;
            sPendingOperator = null;
            sResetInputOnNextDigit = true;
        } catch (Exception ignored) {
            sCurrentInput = "Error";
            sResetInputOnNextDigit = true;
        }
    }

    private static BigDecimal evaluate(BigDecimal left, BigDecimal right, String op) {
        switch (op) {
            case "+":
                return left.add(right, MathContext.DECIMAL64);
            case "−":
                return left.subtract(right, MathContext.DECIMAL64);
            case "×":
                return left.multiply(right, MathContext.DECIMAL64);
            case "÷":
                if (right.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return left.divide(right, 8, RoundingMode.HALF_UP);
            default:
                return right;
        }
    }

    private static String stripTrailingZeros(BigDecimal val) {
        if (val == null) return "0";
        BigDecimal stripped = val.stripTrailingZeros();
        if (stripped.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return stripped.toPlainString();
    }

    private static String formatNumberForDisplay(String raw) {
        if (raw == null || raw.isEmpty() || "Error".equals(raw)) return raw == null ? "0" : raw;
        try {
            boolean endsWithDot = raw.endsWith(".");
            String[] parts = raw.split("\\.", -1);
            BigDecimal intPart = new BigDecimal(parts[0]);
            DecimalFormat df = new DecimalFormat("#,###");
            String formattedInt = df.format(intPart);
            if (parts[0].startsWith("-") && !formattedInt.startsWith("-")) {
                formattedInt = "-" + formattedInt;
            }
            if (parts.length > 1 && !parts[1].isEmpty()) {
                return formattedInt + "." + parts[1];
            } else if (endsWithDot) {
                return formattedInt + ".";
            }
            return formattedInt;
        } catch (Exception e) {
            return raw;
        }
    }

    private static void refreshDisplayViews() {
        if (sHistoryView != null) {
            sHistoryView.setText(sExpressionHistory);
        }
        if (sDisplayView != null) {
            sDisplayView.setText(formatNumberForDisplay(sCurrentInput));
        }
    }

    private static void saveWindowBounds(Context ctx, int x, int y, int w, int h) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_CALC_WIN_X, x)
                .putInt(KEY_CALC_WIN_Y, y)
                .putInt(KEY_CALC_WIN_W, w)
                .putInt(KEY_CALC_WIN_H, h)
                .apply();
    }

    private static void vibrateTick(Context ctx) {
        try {
            Vibrator vib = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null && vib.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vib.vibrate(12);
                }
            }
        } catch (Throwable ignored) {}
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
