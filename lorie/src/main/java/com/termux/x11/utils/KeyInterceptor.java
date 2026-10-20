package com.termux.x11.utils;

import androidx.annotation.NonNull;
import com.termux.x11.MainActivity;

/**
 * Security Hardening: Android AccessibilityService key interception (inherited from upstream
 * Termux:X11 for Samsung DeX hardware Win+Tab shortcuts) has been permanently disabled and
 * removed from AndroidManifest.xml so the application never registers an AccessibilityService
 * or intercepts system-wide key events.
 */
public final class KeyInterceptor {

    private KeyInterceptor() {
        // No-op stub: AccessibilityService removed for least-privilege security compliance
    }

    public static void launch(@NonNull MainActivity activity) {
        // Intentionally disabled: no AccessibilityService is used by ASYCUDA Iraq
    }

    public static void shutdown(boolean onlyIfEnabledAutomatically) {
        // Intentionally disabled
    }

    public static boolean isLaunched() {
        return false;
    }

    public static void recheck() {
        // Intentionally disabled
    }
}
