package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class AsycudaMaterialUi {

    private static final int SURFACE_MODAL_TOP       = Color.parseColor("#CC141B2D");
    private static final int SURFACE_MODAL_BOTTOM    = Color.parseColor("#B80B101E");
    private static final int BORDER_BRIGHT           = Color.parseColor("#52FFFFFF");
    private static final int CARD_TOP                = Color.parseColor("#33FFFFFF");
    private static final int CARD_BOTTOM             = Color.parseColor("#14FFFFFF");
    private static final int CARD_ALT_TOP            = Color.parseColor("#24FFFFFF");
    private static final int CARD_ALT_BOTTOM         = Color.parseColor("#0DFFFFFF");
    private static final int CARD_BORDER             = Color.parseColor("#38FFFFFF");
    private static final int ACCENT_TOP              = Color.parseColor("#730284C7");
    private static final int ACCENT_BOTTOM           = Color.parseColor("#4D0369A1");
    private static final int ACCENT_BORDER           = Color.parseColor("#807DD3FC");
    private static final int PILL_SEC_TOP            = Color.parseColor("#38FFFFFF");
    private static final int PILL_SEC_BOTTOM         = Color.parseColor("#18FFFFFF");
    private static final int TEXT_PRIMARY            = Color.parseColor("#F8FAFC");
    private static final int TEXT_SECONDARY          = Color.parseColor("#CBD5E1");
    private static final int ACCENT_LIGHT            = Color.parseColor("#7DD3FC");
    private static final int RIPPLE_COLOR            = Color.parseColor("#407DD3FC");

    public interface MenuActionCallback {
        void onToggleKeyboard();
        void onToggleAssistKeys();
        void onOpenFiles();
        void onOpenDisplaySettings();
        void onSyncGovJars();
        void onRestartSession();
        void onShareDiagnosticsLog();
        void onOpenAbout();
        void onOpenUrl(String url);
        void onOpenFile(File f);
        void onShareFile(File f);
    }

    public static String getAppVersionLabel(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            if (pi != null && pi.versionName != null && !pi.versionName.isEmpty()) {
                return "v" + pi.versionName;
            }
        } catch (Throwable ignored) {}
        return "v3.5.0";
    }

    public static void showControlCenter(MainActivity activity, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.88f, 700);
        LinearLayout root = createDialogContainer(activity);

        // Header Row
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 14));

        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(activity, 42), dp(activity, 42));
        logoLp.rightMargin = dp(activity, 12);
        header.addView(logo, logoLp);

        LinearLayout titleCol = new LinearLayout(activity);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(activity);
        title.setText("ASYCUDA World — Republic of Iraq");
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.5f);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        titleCol.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText("Control Center • " + getAppVersionLabel(activity));
        subtitle.setTextColor(ACCENT_LIGHT);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        titleCol.addView(subtitle);
        header.addView(titleCol);

        TextView closeBtn = createGlassPillButton(activity, "Done", false, v -> dialog.dismiss());
        header.addView(closeBtn);
        root.addView(header);

        // Scrollable 2-Column Grid
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        GridLayout grid = new GridLayout(activity);
        int cols = isCompactWidth(activity) ? 1 : 2;
        grid.setColumnCount(cols);
        grid.setUseDefaultMargins(false);

        addMenuTile(activity, grid, R.drawable.ic_menu_keyboard, "On-Screen Keyboard",
                "Show or hide Android text input", true, () -> {
                    dialog.dismiss();
                    cb.onToggleKeyboard();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_assist_keys, "Function & Arrow Keys",
                "Toggle TAB, CTRL, ESC and navigation bar", false, () -> {
                    dialog.dismiss();
                    cb.onToggleAssistKeys();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_cloud_sync, "Check for Updates",
                "Synchronize client packages with MOF server", true, () -> {
                    dialog.dismiss();
                    cb.onSyncGovJars();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_documents, "Printed PDFs & Files",
                "Open or share customs declarations and reports", false, () -> {
                    dialog.dismiss();
                    cb.onOpenFiles();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_display, "Display & Input Settings",
                "Adjust screen scaling, resolution and pointer", false, () -> {
                    dialog.dismiss();
                    cb.onOpenDisplaySettings();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_restart, "Restart Session",
                "Reconnect and reload the ASYCUDA client", false, () -> {
                    dialog.dismiss();
                    cb.onRestartSession();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_diagnostics, "Share Diagnostics ZIP",
                "Export logs & device info for troubleshooting", false, () -> {
                    dialog.dismiss();
                    cb.onShareDiagnosticsLog();
                });

        addMenuTile(activity, grid, R.drawable.ic_menu_info, "About & Licences",
                "Developer details and open-source notices", false, () -> {
                    dialog.dismiss();
                    cb.onOpenAbout();
                });

        scroll.addView(grid);
        root.addView(scroll);

        dialog.setContentView(root);
        dialog.show();
    }

    private static void addMenuTile(Context ctx, GridLayout grid,
                                    int iconResId, String titleText, String descText,
                                    boolean isAccentBadge, Runnable onClick) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dp(ctx, 14);
        card.setPadding(pad, dp(ctx, 12), pad, dp(ctx, 12));
        card.setBackground(createGlassRippleDrawable(ctx,
                CARD_TOP, CARD_BOTTOM, 20, CARD_BORDER));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> onClick.run());

        FrameLayout badgeBox = new FrameLayout(ctx);
        badgeBox.setBackground(createGlassGradientDrawable(ctx,
                isAccentBadge ? ACCENT_TOP : PILL_SEC_TOP,
                isAccentBadge ? ACCENT_BOTTOM : PILL_SEC_BOTTOM,
                14,
                isAccentBadge ? ACCENT_BORDER : CARD_BORDER));
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(dp(ctx, 44), dp(ctx, 44));
        badgeLp.rightMargin = dp(ctx, 12);

        ImageView iconView = new ImageView(ctx);
        iconView.setImageResource(iconResId);
        iconView.setImageTintList(ColorStateList.valueOf(isAccentBadge ? ACCENT_LIGHT : TEXT_PRIMARY));
        FrameLayout.LayoutParams icLp = new FrameLayout.LayoutParams(dp(ctx, 22), dp(ctx, 22), Gravity.CENTER);
        badgeBox.addView(iconView, icLp);
        card.addView(badgeBox, badgeLp);

        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t = new TextView(ctx);
        t.setText(titleText);
        t.setTextColor(TEXT_PRIMARY);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        textCol.addView(t);

        TextView d = new TextView(ctx);
        d.setText(descText);
        d.setTextColor(TEXT_SECONDARY);
        d.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        textCol.addView(d);

        card.addView(textCol);

        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.width = 0;
        glp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        int m = dp(ctx, 5);
        glp.setMargins(m, m, m, m);
        grid.addView(card, glp);
    }

    public static void showFilesModal(MainActivity activity, List<File> files, MenuActionCallback cb) {
        Dialog dialog = createModalDialog(activity, 0.86f, 650);
        LinearLayout root = createDialogContainer(activity);

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView headerIcon = new ImageView(activity);
        headerIcon.setImageResource(R.drawable.ic_menu_documents);
        headerIcon.setImageTintList(ColorStateList.valueOf(ACCENT_LIGHT));
        LinearLayout.LayoutParams hiLp = new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24));
        hiLp.rightMargin = dp(activity, 10);
        header.addView(headerIcon, hiLp);

        TextView title = new TextView(activity);
        title.setText("Printed PDFs & Exported Files");
        title.setTextColor(TEXT_PRIMARY);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        header.addView(createGlassPillButton(activity, "Close", false, v -> dialog.dismiss()));
        root.addView(header);

        ScrollView scroll = new ScrollView(activity);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout listBox = new LinearLayout(activity);
        listBox.setOrientation(LinearLayout.VERTICAL);

        if (files.isEmpty()) {
            LinearLayout emptyCard = new LinearLayout(activity);
            emptyCard.setOrientation(LinearLayout.VERTICAL);
            emptyCard.setGravity(Gravity.CENTER);
            int p = dp(activity, 28);
            emptyCard.setPadding(p, p, p, p);
            emptyCard.setBackground(createGlassGradientDrawable(activity,
                    CARD_ALT_TOP, CARD_ALT_BOTTOM, 20, CARD_BORDER));

            TextView emptyText = new TextView(activity);
            emptyText.setText("No documents generated yet.\n\nWhen you print or export declarations inside ASYCUDA World, your PDFs and files will appear here and in Download/Asycuda_Prints.");
            emptyText.setTextColor(TEXT_SECONDARY);
            emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
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
                row.setBackground(createGlassGradientDrawable(activity,
                        CARD_TOP, CARD_BOTTOM, 18, CARD_BORDER));
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
                metaView.setText(kb + " KB • Ready to open or share");
                metaView.setTextColor(TEXT_SECONDARY);
                metaView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
                infoCol.addView(metaView);
                row.addView(infoCol);

                TextView openBtn = createGlassPillButton(activity, "Open", true, v -> {
                    dialog.dismiss();
                    cb.onOpenFile(f);
                });
                LinearLayout.LayoutParams olp = (LinearLayout.LayoutParams) openBtn.getLayoutParams();
                olp.rightMargin = dp(activity, 6);
                row.addView(openBtn);

                TextView shareBtn = createGlassPillButton(activity, "Share", false, v -> {
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
        Dialog dialog = createModalDialog(activity, 0.90f, 740);
        LinearLayout root = createDialogContainer(activity);

        // Top Bar
        LinearLayout topBar = new LinearLayout(activity);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(activity, 4), 0, dp(activity, 4), dp(activity, 12));

        ImageView infoIcon = new ImageView(activity);
        infoIcon.setImageResource(R.drawable.ic_menu_info);
        infoIcon.setImageTintList(ColorStateList.valueOf(ACCENT_LIGHT));
        LinearLayout.LayoutParams iiLp = new LinearLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        iiLp.rightMargin = dp(activity, 10);
        topBar.addView(infoIcon, iiLp);

        TextView topTitle = new TextView(activity);
        topTitle.setText("About & Open-Source Licences");
        topTitle.setTextColor(TEXT_PRIMARY);
        topTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        topTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        topTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(topTitle);

        topBar.addView(createGlassPillButton(activity, "Close", false, v -> dialog.dismiss()));
        root.addView(topBar);

        ScrollView scroll = new ScrollView(activity);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        // 1. Hero App Identity Card
        LinearLayout heroCard = new LinearLayout(activity);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 18);
        heroCard.setPadding(pad, pad, pad, pad);
        heroCard.setBackground(createGlassGradientDrawable(activity,
                CARD_TOP, CARD_BOTTOM, 22, CARD_BORDER));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(activity, 12);
        heroCard.setLayoutParams(cardLp);

        LinearLayout heroTop = new LinearLayout(activity);
        heroTop.setOrientation(LinearLayout.HORIZONTAL);
        heroTop.setGravity(Gravity.CENTER_VERTICAL);

        ImageView appIcon = new ImageView(activity);
        appIcon.setImageResource(R.drawable.ic_asycuda_logo);
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(activity, 54), dp(activity, 54));
        icLp.rightMargin = dp(activity, 14);
        heroTop.addView(appIcon, icLp);

        LinearLayout heroTextCol = new LinearLayout(activity);
        heroTextCol.setOrientation(LinearLayout.VERTICAL);

        TextView appTitle = new TextView(activity);
        appTitle.setText("ASYCUDA World — Republic of Iraq");
        appTitle.setTextColor(TEXT_PRIMARY);
        appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        appTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        heroTextCol.addView(appTitle);

        TextView verPill = new TextView(activity);
        verPill.setText("Release " + getAppVersionLabel(activity) + " (ARM64)");
        verPill.setTextColor(ACCENT_LIGHT);
        verPill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        verPill.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heroTextCol.addView(verPill);
        heroTop.addView(heroTextCol);
        heroCard.addView(heroTop);

        TextView appDesc = new TextView(activity);
        appDesc.setText("Android client for the Iraqi General Commission for Customs ASYCUDA World portal, built for customs brokers, clearing agents, and trade operators.");
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
        devCard.setBackground(createGlassGradientDrawable(activity,
                CARD_TOP, CARD_BOTTOM, 22, CARD_BORDER));
        devCard.setLayoutParams(cardLp);

        TextView devSectionLabel = createSectionHeader(activity, "DEVELOPER & MAINTAINER");
        devCard.addView(devSectionLabel);

        TextView devName = new TextView(activity);
        devName.setText("Mohammed Ammar Hussein (Mohammed AL-Shimmary)");
        devName.setTextColor(TEXT_PRIMARY);
        devName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        devName.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        devCard.addView(devName);

        TextView devRole = new TextView(activity);
        devRole.setText("Senior Logistics Coordinator & Software Developer • Baghdad, Iraq");
        devRole.setTextColor(ACCENT_LIGHT);
        devRole.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        devCard.addView(devRole);

        TextView devBio = new TextView(activity);
        devBio.setText("Built and maintained as an open-source project for the Iraqi customs and logistics community.");
        devBio.setTextColor(TEXT_SECONDARY);
        devBio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        LinearLayout.LayoutParams bioLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bioLp.topMargin = dp(activity, 6);
        bioLp.bottomMargin = dp(activity, 12);
        devCard.addView(devBio, bioLp);

        // Social / Contact Links Row
        GridLayout socialGrid = new GridLayout(activity);
        int socialCols = isCompactWidth(activity) ? 2 : 4;
        socialGrid.setColumnCount(socialCols);
        addSocialButton(activity, socialGrid, "GitHub", true,
                () -> cb.onOpenUrl("https://github.com/MohammedALShimmary"));
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
        ossCard.setBackground(createGlassGradientDrawable(activity,
                CARD_TOP, CARD_BOTTOM, 22, CARD_BORDER));
        ossCard.setLayoutParams(cardLp);

        ossCard.addView(createSectionHeader(activity, "OPEN-SOURCE SOFTWARE & CREDITS"));

        addCreditEntry(activity, ossCard,
                "Termux & Termux:X11 (libXlorie)",
                "GPLv3 / MIT",
                "Twaik Yont (@twaik), Fredrik Fornwall (@fornwall), @agnostic-apollo, @sylirre",
                "Embedded X11 display server, SurfaceView rendering, touch input handling, and extra keys bar.");

        addCreditEntry(activity, ossCard,
                "Eclipse Temurin / OpenJDK 8",
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
        legalCard.setPadding(pad, dp(activity, 14), pad, dp(activity, 14));
        legalCard.setBackground(createGlassGradientDrawable(activity,
                CARD_ALT_TOP, CARD_ALT_BOTTOM, 18, CARD_BORDER));

        TextView legalHeader = createSectionHeader(activity, "DISCLAIMER");
        legalCard.addView(legalHeader);

        TextView legalText = new TextView(activity);
        legalText.setText("Independent open-source client developed to support ASYCUDA World access on Android devices. Not affiliated with or endorsed by UNCTAD or the Ministry of Finance of the Republic of Iraq.");
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
        item.setBackground(createGlassGradientDrawable(ctx,
                CARD_ALT_TOP, CARD_ALT_BOTTOM, 16, CARD_BORDER));
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
        licBadge.setTextColor(TEXT_PRIMARY);
        licBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        licBadge.setPadding(dp(ctx, 8), dp(ctx, 2), dp(ctx, 8), dp(ctx, 2));
        licBadge.setBackground(createGlassGradientDrawable(ctx,
                ACCENT_TOP, ACCENT_BOTTOM, 10, ACCENT_BORDER));
        titleRow.addView(licBadge);
        item.addView(titleRow);

        TextView authView = new TextView(ctx);
        authView.setText(authors);
        authView.setTextColor(ACCENT_LIGHT);
        authView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        authView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
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
        dlp.topMargin = dp(ctx, 3);
        item.addView(descView, dlp);

        parent.addView(item);
    }

    private static void addSocialButton(Context ctx, GridLayout grid, String label,
                                        boolean isPrimary, Runnable onClick) {
        TextView btn = createGlassPillButton(ctx, label, isPrimary, v -> onClick.run());
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
        tv.setTextColor(ACCENT_LIGHT);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
        tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        tv.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 6);
        tv.setLayoutParams(lp);
        return tv;
    }

    @SuppressLint("NewApi")
    private static Dialog createModalDialog(MainActivity activity, float widthFraction, int maxWidthDp) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            int targetW = Math.min((int) (dm.widthPixels * widthFraction), dp(activity, maxWidthDp));
            w.setLayout(targetW, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = w.getAttributes();
            wlp.dimAmount = 0.45f;
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            if (Build.VERSION.SDK_INT >= 31) {
                try {
                    w.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    wlp.setBlurBehindRadius(dp(activity, 24));
                } catch (Throwable ignored) {}
            }
            w.setAttributes(wlp);
        }
        return dialog;
    }

    private static LinearLayout createDialogContainer(Context ctx) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        int p = dp(ctx, 18);
        root.setPadding(p, dp(ctx, 16), p, dp(ctx, 16));
        root.setBackground(createGlassContainerDrawable(ctx, 28));
        root.setElevation(dp(ctx, 16));
        return root;
    }

    public static Drawable createGlassContainerDrawable(Context ctx, int radiusDp) {
        GradientDrawable base = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { SURFACE_MODAL_TOP, SURFACE_MODAL_BOTTOM });
        base.setCornerRadius(dp(ctx, radiusDp));
        base.setStroke(dp(ctx, 1), BORDER_BRIGHT);

        GradientDrawable topSheen = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.parseColor("#24FFFFFF"), Color.TRANSPARENT });
        topSheen.setCornerRadius(dp(ctx, radiusDp));

        return new LayerDrawable(new Drawable[] { base, topSheen });
    }

    public static GradientDrawable createGlassGradientDrawable(Context ctx, int topColor, int bottomColor, int radiusDp, int strokeColor) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { topColor, bottomColor });
        gd.setCornerRadius(dp(ctx, radiusDp));
        if (strokeColor != 0) {
            gd.setStroke(dp(ctx, 1), strokeColor);
        }
        return gd;
    }

    public static RippleDrawable createGlassRippleDrawable(Context ctx, int topColor, int bottomColor, int radiusDp, int strokeColor) {
        GradientDrawable content = createGlassGradientDrawable(ctx, topColor, bottomColor, radiusDp, strokeColor);
        GradientDrawable mask = createCardDrawable(ctx, Color.WHITE, radiusDp, 0);
        return new RippleDrawable(ColorStateList.valueOf(RIPPLE_COLOR), content, mask);
    }

    public static TextView createGlassPillButton(Context ctx, String label, boolean isPrimaryAccent, View.OnClickListener listener) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(TEXT_PRIMARY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        tv.setPadding(dp(ctx, 16), dp(ctx, 9), dp(ctx, 16), dp(ctx, 9));
        tv.setBackground(createGlassRippleDrawable(
                ctx,
                isPrimaryAccent ? ACCENT_TOP : PILL_SEC_TOP,
                isPrimaryAccent ? ACCENT_BOTTOM : PILL_SEC_BOTTOM,
                20,
                isPrimaryAccent ? ACCENT_BORDER : CARD_BORDER));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setOnClickListener(listener);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    public static TextView createPillButton(Context ctx, String label, int bgColor, int textColor, View.OnClickListener listener) {
        return createGlassPillButton(ctx, label, true, listener);
    }

    public static GradientDrawable createCardDrawable(Context ctx, int fillColor, int radiusDp, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(ctx, radiusDp));
        if (strokeColor != 0) {
            gd.setStroke(dp(ctx, 1), strokeColor);
        }
        return gd;
    }

    public static RippleDrawable createRippleCardDrawable(Context ctx, int fillColor, int radiusDp, int strokeColor) {
        GradientDrawable content = createCardDrawable(ctx, fillColor, radiusDp, strokeColor);
        GradientDrawable mask = createCardDrawable(ctx, Color.WHITE, radiusDp, 0);
        return new RippleDrawable(ColorStateList.valueOf(RIPPLE_COLOR), content, mask);
    }

    private static boolean isCompactWidth(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        return cfg.screenWidthDp < 480;
    }

    public static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
