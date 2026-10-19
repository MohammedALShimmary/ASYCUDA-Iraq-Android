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
