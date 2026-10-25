/*
 * Copyright (C) 2026 Mohammed Ammar Hussein (@MohammedALShimmary)
 * Original Java bridge code authored for ASYCUDA Iraq Android.
 * Licensed under GNU GPL-3.0; see the repository LICENSE file and java-bridge/ATTRIBUTION-TERMS.md.
 */
package java.awt;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Headless/Android X11 Desktop bridge for OpenJDK 8 (`00_bootpatch.jar`).
 *
 * Loaded via `-Xbootclasspath/p:00_bootpatch.jar` so that calls from the
 * unmodified UNCTAD ASYCUDA World client (`Desktop.getDesktop().open(file)` and
 * `Desktop.getDesktop().browse(uri)`) do not fail looking for desktop GNOME/GTK
 * libraries on Android, and instead forward the document path or URI to
 * `asycuda.AsycudaBoot.handleDesktopAction(...)`.
 */
public class Desktop {
    public enum Action {
        OPEN, EDIT, PRINT, MAIL, BROWSE
    }

    private static final Desktop INSTANCE = new Desktop();

    private Desktop() {
        // Singleton bridge instance
    }

    public static synchronized Desktop getDesktop() {
        return INSTANCE;
    }

    public static boolean isDesktopSupported() {
        return true;
    }

    public boolean isSupported(Action action) {
        return action != null;
    }

    public void open(File file) throws IOException {
        if (file == null) throw new NullPointerException("File must not be null");
        notifyBridge("OPEN", file.getAbsolutePath());
    }

    public void edit(File file) throws IOException {
        open(file);
    }

    public void print(File file) throws IOException {
        open(file);
    }

    public void browse(URI uri) throws IOException {
        if (uri == null) throw new NullPointerException("URI must not be null");
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            notifyBridge("OPEN", new File(uri).getAbsolutePath());
        } else {
            notifyBridge("BROWSE", uri.toString());
        }
    }

    public void mail() throws IOException {
        // Mail client invocation is not used by ASYCUDA World
    }

    public void mail(URI mailtoURI) throws IOException {
        // Mail client invocation is not used by ASYCUDA World
    }

    private void notifyBridge(String action, String target) {
        try {
            Class<?> cls = Class.forName("asycuda.AsycudaBoot", true, Thread.currentThread().getContextClassLoader());
            cls.getMethod("handleDesktopAction", String.class, String.class).invoke(null, action, target);
        } catch (Throwable t) {
            try {
                Class<?> cls2 = Class.forName("asycuda.AsycudaBoot", true, ClassLoader.getSystemClassLoader());
                cls2.getMethod("handleDesktopAction", String.class, String.class).invoke(null, action, target);
            } catch (Throwable ignored) {
                // Ignore if AsycudaBoot is not on the active classloader
            }
        }
    }
}
