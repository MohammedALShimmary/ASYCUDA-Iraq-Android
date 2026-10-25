/*
 * Copyright (C) 2026 Mohammed Ammar Hussein (@MohammedALShimmary)
 * Original Java bridge code authored for ASYCUDA Iraq Android.
 * Licensed under GNU GPL-3.0; see the repository LICENSE file and java-bridge/ATTRIBUTION-TERMS.md.
 */
package javax.jnlp;

import java.net.URL;

/**
 * Minimal JNLP BasicService interface (`00_jnlp.jar`) required by the
 * UNCTAD ASYCUDA World client when running outside Oracle Java Web Start (`javaws`).
 */
public interface BasicService {
    URL getCodeBase();
    boolean isOffline();
    boolean isWebBrowserSupported();
    boolean showDocument(URL url);
}
