package javax.jnlp;

import java.io.File;
import java.net.URL;

/**
 * Minimal JNLP ServiceManager implementation (`00_jnlp.jar`) for Android.
 *
 * Routes `BasicService.showDocument(URL)` requests to `asycuda.AsycudaBoot.handleDesktopAction(...)`
 * so generated reports and external URLs open natively on Android.
 */
public final class ServiceManager {

    private ServiceManager() {
        // Static lookup utility
    }

    public static String[] getServiceNames() {
        return new String[] { "javax.jnlp.BasicService" };
    }

    public static Object lookup(String name) throws UnavailableServiceException {
        if ("javax.jnlp.BasicService".equals(name)) {
            return new BasicService() {
                @Override
                public URL getCodeBase() {
                    try {
                        return new URL("https://asy.mof.gov.iq/aw/");
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                public boolean isOffline() {
                    return false;
                }

                @Override
                public boolean isWebBrowserSupported() {
                    return true;
                }

                @Override
                public boolean showDocument(URL url) {
                    if (url == null) return false;
                    try {
                        String protocol = url.getProtocol();
                        String action = "file".equalsIgnoreCase(protocol) ? "OPEN" : "BROWSE";
                        String target = "file".equalsIgnoreCase(protocol)
                                ? new File(url.toURI()).getAbsolutePath()
                                : url.toExternalForm();
                        Class<?> cls = Class.forName("asycuda.AsycudaBoot", true, Thread.currentThread().getContextClassLoader());
                        cls.getMethod("handleDesktopAction", String.class, String.class).invoke(null, action, target);
                        return true;
                    } catch (Throwable t) {
                        try {
                            String protocol = url.getProtocol();
                            String action = "file".equalsIgnoreCase(protocol) ? "OPEN" : "BROWSE";
                            String target = "file".equalsIgnoreCase(protocol) ? url.getPath() : url.toExternalForm();
                            Class<?> cls2 = Class.forName("asycuda.AsycudaBoot", true, ClassLoader.getSystemClassLoader());
                            cls2.getMethod("handleDesktopAction", String.class, String.class).invoke(null, action, target);
                            return true;
                        } catch (Throwable ignored) {
                            return false;
                        }
                    }
                }
            };
        }
        throw new UnavailableServiceException(name);
    }
}
