package asycuda;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.PrinterName;
import javax.print.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

@SuppressWarnings({"java:S3776", "java:S1141", "java:S1181", "java:S135", "java:S1075", "java:S1168", "java:S3011"})
public class AsycudaBoot {

    private static final String PRINTER_NAME = "ASYCUDA_PDF_Printer";
    private static final String DEFAULT_TMP_DIR = "/data/data/iq.asycuda/files/tmp";
    private static final String DEFAULT_PRINTS_DIR = "/data/data/iq.asycuda/files/prints";
    private static final String DEFAULT_WORKSPACE_DIR = DEFAULT_PRINTS_DIR;
    private static final String PDF_MIME = "application/pdf";
    private static final String JPG_MIME = "image/jpeg";
    private static final String PNG_MIME = "image/png";

    private static final Object FILE_PROCESS_LOCK = new Object();
    private static final ThreadLocal<Boolean> IN_CHECK = new ThreadLocal<Boolean>();
    private static final Map<String, Long> RECENT_HASHES = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Long> SEEN_PATHS = new ConcurrentHashMap<String, Long>();
    private static final AtomicBoolean SOCLASS_ALERT_SENT = new AtomicBoolean(false);
    private static volatile Object wrappedRejectHandler = null;
    private static volatile Object hookedMultiLinguisticElements = null;
    private static volatile JFileChooser activeFileChooser = null;
    private static volatile Dialog activeFileChooserDialog = null;
    private static volatile long lastAutoPickTime = 0L;
    private static volatile long lastMousePressTime = 0L;

    private AsycudaBoot() {
        // Utility entrypoint class
    }

    private static File getWorkspaceDir() {
        return new File(System.getProperty("asycuda.workspace.dir", DEFAULT_WORKSPACE_DIR));
    }

    public static void handleDesktopAction(final String action, final String target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ("BROWSE".equals(action)) {
                    sendToAndroidApk("BROWSE|" + target);
                } else {
                    processAndOpenFile(new File(target));
                }
            }
        }, "Asycuda-Opener").start();
    }

    public static void processAndOpenFile(File rawFile) {
        try {
            if (rawFile == null || !rawFile.exists() || !rawFile.isFile()) return;
            String origName = rawFile.getName();
            String lowOrig = origName.toLowerCase(Locale.US);
            if (origName.startsWith("Resource") && origName.endsWith(".so")) return;
            if (lowOrig.startsWith("soems") || lowOrig.endsWith("ems")) return;
            if (lowOrig.startsWith("prn") && lowOrig.endsWith("xc")) return;
            if (lowOrig.endsWith(".part") || lowOrig.endsWith(".lck") || lowOrig.endsWith(".tmp")) return;

            if (!waitForStableFileSize(rawFile)) return;

            synchronized (FILE_PROCESS_LOCK) {
                String pathKey = rawFile.getAbsolutePath() + "_" + rawFile.lastModified() + "_" + rawFile.length();
                SEEN_PATHS.put(pathKey, System.currentTimeMillis());

                String sha256 = computeSha256(rawFile);
                long now = System.currentTimeMillis();
                Long lastTime = RECENT_HASHES.get(sha256);
                if (lastTime != null && (now - lastTime) < 15000L) {
                    return;
                }
                RECENT_HASHES.put(sha256, now);

                String[] typeInfo = detectExtensionAndMime(rawFile);
                String realExt = typeInfo[0];
                String mime = typeInfo[1];

                String baseNoExt = origName;
                int dot = origName.lastIndexOf('.');
                if (dot > 0) baseNoExt = origName.substring(0, dot);
                String cleanBase = baseNoExt.replaceAll("[^a-zA-Z0-9._-]", "_");
                String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String finalName = stamp + "_" + cleanBase + "." + realExt;

                String internalPrints = System.getProperty("asycuda.prints.dir", DEFAULT_PRINTS_DIR);
                File intDir = new File(internalPrints);
                if (!intDir.exists() && !intDir.mkdirs()) {
                    // Directory may already exist
                }
                File intDest = new File(intDir, finalName);
                copyFile(rawFile, intDest);

                sendToAndroidApk("OPEN|" + intDest.getAbsolutePath() + "|" + mime);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
            // Ignore file opener errors to keep ASYCUDA Swing thread responsive
        }
    }

    private static boolean waitForStableFileSize(File rawFile) throws InterruptedException {
        long s1 = rawFile.length();
        if (s1 <= 0) return false;
        Thread.sleep(150);
        long s2 = rawFile.length();
        if (s1 != s2) {
            for (int i = 0; i < 10; i++) {
                s1 = s2;
                Thread.sleep(150);
                s2 = rawFile.length();
                if (s1 == s2 && s2 > 0) break;
            }
        }
        return rawFile.length() > 0;
    }

    public static boolean sendToAndroidApk(String msg) {
        java.net.InetSocketAddress loopbackAddr = new java.net.InetSocketAddress(
                java.net.InetAddress.getLoopbackAddress(), 47891);
        try (java.nio.channels.SocketChannel ch = java.nio.channels.SocketChannel.open(loopbackAddr)) {
            String token = System.getProperty("asycuda.bridge.token", "");
            String wirePayload = token.isEmpty() ? msg : ("AUTH|" + token + "|" + msg);
            byte[] payloadBytes = (wirePayload + "\n").getBytes(StandardCharsets.UTF_8);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(payloadBytes);
            while (buf.hasRemaining()) {
                ch.write(buf);
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void notifySoClassUpdateRequired(final String reason) {
        if (SOCLASS_ALERT_SENT.compareAndSet(false, true)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String cleanReason = (reason != null && !reason.trim().isEmpty())
                            ? reason.trim().replace('\n', ' ').replace('\r', ' ')
                            : "SOClass Client have to be updated.";
                    sendToAndroidApk("SOCLASS_UPDATE|" + cleanReason);
                }
            }, "Asycuda-SOClassAlert").start();
        }
    }

    public static String[] detectExtensionAndMime(File f) {
        String name = f.getName();
        String ext = "bin";
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            ext = name.substring(dot + 1).toLowerCase(Locale.US);
        }
        byte[] head = new byte[2048];
        int n = 0;
        try (FileInputStream fis = new FileInputStream(f)) {
            n = fis.read(head);
        } catch (Throwable ignored) {
            // Fallback to extension-based detection if header read fails
        }

        if (n >= 4 && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F') {
            return new String[] { "pdf", PDF_MIME };
        }
        if (n >= 3 && (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF) {
            return new String[] { "jpg", JPG_MIME };
        }
        if (n >= 4 && (head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G') {
            return new String[] { "png", PNG_MIME };
        }
        if (n >= 4 && ((head[0] == 'I' && head[1] == 'I' && head[2] == 42 && head[3] == 0) ||
                       (head[0] == 'M' && head[1] == 'M' && head[2] == 0 && head[3] == 42))) {
            return new String[] { "tif", "image/tiff" };
        }
        if (n >= 4 && head[0] == 'G' && head[1] == 'I' && head[2] == 'F' && head[3] == '8') {
            return new String[] { "gif", "image/gif" };
        }
        if (n >= 4 && (head[0] & 0xFF) == 0xD0 && (head[1] & 0xFF) == 0xCF && (head[2] & 0xFF) == 0x11 && (head[3] & 0xFF) == 0xE0) {
            if ("doc".equals(ext)) return new String[] { "doc", "application/msword" };
            return new String[] { "xls", "application/vnd.ms-excel" };
        }
        if (n >= 4 && head[0] == 'P' && head[1] == 'K' && head[2] == 3 && head[3] == 4) {
            String ascii = new String(head, 0, n, StandardCharsets.ISO_8859_1);
            if (ascii.contains("xl/")) {
                return new String[] { "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" };
            }
            if (ascii.contains("word/")) {
                return new String[] { "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" };
            }
            return new String[] { "zip", "application/zip" };
        }
        if (n >= 5) {
            String start = new String(head, 0, Math.min(n, 256), StandardCharsets.UTF_8).trim().toLowerCase(Locale.US);
            if (start.startsWith("<?xml")) return new String[] { "xml", "text/xml" };
            if (start.startsWith("<!doctype html") || start.startsWith("<html")) return new String[] { "html", "text/html" };
        }
        if ("pdf".equals(ext)) return new String[] { "pdf", PDF_MIME };
        if ("jpg".equals(ext) || "jpeg".equals(ext)) return new String[] { "jpg", JPG_MIME };
        if ("png".equals(ext)) return new String[] { "png", PNG_MIME };
        return new String[] { ext, "application/octet-stream" };
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[16384];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        }
    }

    private static String computeSha256(File f) {
        try (FileInputStream in = new FileInputStream(f)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buf = new byte[16384];
            int len;
            while ((len = in.read(buf)) > 0) md.update(buf, 0, len);
            byte[] dig = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b & 0xFF));
            return sb.toString();
        } catch (Throwable t) {
            return f.getName() + "_" + f.length();
        }
    }

    // Pure-Java Pageable/Printable -> Multi-Page PDF 1.4 Generator (Zero Ghostscript needed!)
    public static File renderPageableToPdf(Pageable pageable) throws Exception {
        File tmpDir = new File(System.getProperty("java.io.tmpdir", DEFAULT_TMP_DIR));
        if (!tmpDir.exists() && !tmpDir.mkdirs()) {
            // Directory already exists or will be handled by FileOutputStream
        }
        File pdfFile = new File(tmpDir, "Print_" + System.currentTimeMillis() + ".pdf");

        List<byte[]> pageJpegs = new ArrayList<byte[]>();
        List<int[]> pageDims = new ArrayList<int[]>();
        double scale = 2.0; // 144 DPI crisp rendering

        int numPages = pageable.getNumberOfPages();
        int maxPages = (numPages == Pageable.UNKNOWN_NUMBER_OF_PAGES) ? 100 : numPages;

        for (int p = 0; p < maxPages; p++) {
            PageFormat pf;
            Printable printable;
            try {
                pf = pageable.getPageFormat(p);
                printable = pageable.getPrintable(p);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            int wPt = Math.max(1, (int) Math.round(pf.getWidth()));
            int hPt = Math.max(1, (int) Math.round(pf.getHeight()));
            int wPx = (int) Math.round(wPt * scale);
            int hPx = (int) Math.round(hPt * scale);

            BufferedImage img = new BufferedImage(wPx, hPx, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, wPx, hPx);
            g2.scale(scale, scale);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int res = printable.print(g2, pf, p);
            g2.dispose();
            if (res == Printable.NO_SUCH_PAGE) break;

            ByteArrayOutputStream jpgOut = new ByteArrayOutputStream();
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.90f);
                ImageOutputStream ios = ImageIO.createImageOutputStream(jpgOut);
                writer.setOutput(ios);
                writer.write(null, new IIOImage(img, null, null), param);
                ios.close();
                writer.dispose();
            } else {
                ImageIO.write(img, "jpg", jpgOut);
            }
            pageJpegs.add(jpgOut.toByteArray());
            pageDims.add(new int[] { wPt, hPt, wPx, hPx });
        }

        if (pageJpegs.isEmpty()) return null;
        writePdf14(pdfFile, pageJpegs, pageDims);
        return pdfFile;
    }

    private static void writePdf14(File outFile, List<byte[]> jpegs, List<int[]> dims) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            List<Long> offsets = new ArrayList<Long>();
            offsets.add(0L); // Object 0

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            writeAscii(buf, "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");

            int pageCount = jpegs.size();
            // Object 1: Catalog
            offsets.add((long) buf.size());
            writeAscii(buf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            // Object 2: Pages
            offsets.add((long) buf.size());
            StringBuilder kids = new StringBuilder("[");
            for (int i = 0; i < pageCount; i++) {
                kids.append(3 + i * 3).append(" 0 R ");
            }
            kids.append("]");
            writeAscii(buf, "2 0 obj\n<< /Type /Pages /Kids " + kids + " /Count " + pageCount + " >>\nendobj\n");

            for (int i = 0; i < pageCount; i++) {
                int pageObj = 3 + i * 3;
                int imgObj = 4 + i * 3;
                int cntObj = 5 + i * 3;
                int[] d = dims.get(i);
                byte[] jpg = jpegs.get(i);
                int wPt = d[0];
                int hPt = d[1];
                int wPx = d[2];
                int hPx = d[3];

                // Page Object
                offsets.add((long) buf.size());
                writeAscii(buf, pageObj + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + wPt + " " + hPt +
                    "] /Resources << /XObject << /Im0 " + imgObj + " 0 R >> >> /Contents " + cntObj + " 0 R >>\nendobj\n");

                // Image XObject
                offsets.add((long) buf.size());
                writeAscii(buf, imgObj + " 0 obj\n<< /Type /XObject /Subtype /Image /Width " + wPx + " /Height " + hPx +
                    " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + jpg.length + " >>\nstream\n");
                buf.write(jpg);
                writeAscii(buf, "\nendstream\nendobj\n");

                // Content Stream
                String cmd = "q " + wPt + " 0 0 " + hPt + " 0 0 cm /Im0 Do Q\n";
                byte[] cmdBytes = cmd.getBytes(StandardCharsets.US_ASCII);
                offsets.add((long) buf.size());
                writeAscii(buf, cntObj + " 0 obj\n<< /Length " + cmdBytes.length + " >>\nstream\n");
                buf.write(cmdBytes);
                writeAscii(buf, "endstream\nendobj\n");
            }

            long xrefPos = buf.size();
            int totalObjs = offsets.size();
            writeAscii(buf, "xref\n0 " + totalObjs + "\n");
            writeAscii(buf, "0000000000 65535 f \r\n");
            for (int i = 1; i < totalObjs; i++) {
                writeAscii(buf, String.format(Locale.US, "%010d 00000 n \r\n", offsets.get(i)));
            }
            writeAscii(buf, "trailer\n<< /Size " + totalObjs + " /Root 1 0 R >>\nstartxref\n" + xrefPos + "\n%%EOF\n");
            buf.writeTo(fos);
        }
    }

    private static void writeAscii(OutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    static class DirectPdfPrintService implements PrintService {
        private static final DocFlavor[] FLAVORS = new DocFlavor[] {
            DocFlavor.SERVICE_FORMATTED.PAGEABLE,
            DocFlavor.SERVICE_FORMATTED.PRINTABLE,
            DocFlavor.INPUT_STREAM.PDF,
            DocFlavor.INPUT_STREAM.AUTOSENSE,
            DocFlavor.BYTE_ARRAY.PDF,
            DocFlavor.BYTE_ARRAY.AUTOSENSE
        };

        @Override
        public String getName() {
            return PRINTER_NAME;
        }

        @Override
        public DocPrintJob createPrintJob() {
            return new DocPrintJob() {
                @Override public PrintService getPrintService() { return DirectPdfPrintService.this; }
                @Override public PrintJobAttributeSet getAttributes() { return new HashPrintJobAttributeSet(); }
                @Override public void addPrintJobListener(PrintJobListener l) { /* No-op */ }
                @Override public void removePrintJobListener(PrintJobListener l) { /* No-op */ }
                @Override public void addPrintJobAttributeListener(PrintJobAttributeListener l, PrintJobAttributeSet a) { /* No-op */ }
                @Override public void removePrintJobAttributeListener(PrintJobAttributeListener l) { /* No-op */ }

                @Override
                public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {
                    try {
                        Object data = doc.getPrintData();
                        if (data instanceof Pageable) {
                            File pdf = renderPageableToPdf((Pageable) data);
                            if (pdf != null) processAndOpenFile(pdf);
                        } else if (data instanceof Printable) {
                            final Printable pr = (Printable) data;
                            final PageFormat pf = new PageFormat();
                            Pageable pg = new Pageable() {
                                @Override public int getNumberOfPages() { return Pageable.UNKNOWN_NUMBER_OF_PAGES; }
                                @Override public PageFormat getPageFormat(int pageIndex) { return pf; }
                                @Override public Printable getPrintable(int pageIndex) { return pr; }
                            };
                            File pdf = renderPageableToPdf(pg);
                            if (pdf != null) processAndOpenFile(pdf);
                        } else if (data instanceof InputStream) {
                            File tmpDir = new File(System.getProperty("java.io.tmpdir", DEFAULT_TMP_DIR));
                            if (!tmpDir.exists() && !tmpDir.mkdirs()) {
                                // Ignore mkdirs result if directory already exists
                            }
                            File f = new File(tmpDir, "PrintStream_" + System.currentTimeMillis() + ".pdf");
                            try (InputStream in = (InputStream) data; FileOutputStream out = new FileOutputStream(f)) {
                                byte[] b = new byte[16384];
                                int n;
                                while ((n = in.read(b)) > 0) out.write(b, 0, n);
                            }
                            processAndOpenFile(f);
                        }
                    } catch (Exception e) {
                        throw new PrintException(e);
                    }
                }
            };
        }

        @Override public void addPrintServiceAttributeListener(PrintServiceAttributeListener l) { /* No-op */ }
        @Override public void removePrintServiceAttributeListener(PrintServiceAttributeListener l) { /* No-op */ }
        @Override public PrintServiceAttributeSet getAttributes() {
            PrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
            set.add(new PrinterName(PRINTER_NAME, null));
            return set;
        }
        @Override public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
            if (category == PrinterName.class) return category.cast(new PrinterName(PRINTER_NAME, null));
            return null;
        }
        @Override public DocFlavor[] getSupportedDocFlavors() { return FLAVORS.clone(); }
        @Override public boolean isDocFlavorSupported(DocFlavor flavor) { return true; }
        @Override public Class<?>[] getSupportedAttributeCategories() {
            return new Class<?>[] {
                PrinterName.class,
                javax.print.attribute.standard.Media.class,
                javax.print.attribute.standard.MediaPrintableArea.class,
                javax.print.attribute.standard.OrientationRequested.class,
                javax.print.attribute.standard.Copies.class
            };
        }
        @Override public boolean isAttributeCategorySupported(Class<? extends Attribute> category) { return true; }
        @Override public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
            if (category == PrinterName.class) return new PrinterName(PRINTER_NAME, null);
            if (category == javax.print.attribute.standard.Media.class) return javax.print.attribute.standard.MediaSizeName.ISO_A4;
            if (category == javax.print.attribute.standard.MediaPrintableArea.class) {
                return new javax.print.attribute.standard.MediaPrintableArea(
                    0f, 0f, 210f, 297f, javax.print.attribute.standard.MediaPrintableArea.MM);
            }
            if (category == javax.print.attribute.standard.OrientationRequested.class) {
                return javax.print.attribute.standard.OrientationRequested.PORTRAIT;
            }
            if (category == javax.print.attribute.standard.Copies.class) {
                return new javax.print.attribute.standard.Copies(1);
            }
            return null;
        }
        @Override public Object getSupportedAttributeValues(Class<? extends Attribute> c, DocFlavor f, AttributeSet a) {
            if (c == javax.print.attribute.standard.MediaPrintableArea.class) {
                return new javax.print.attribute.standard.MediaPrintableArea[] {
                    new javax.print.attribute.standard.MediaPrintableArea(
                        0f, 0f, 210f, 297f, javax.print.attribute.standard.MediaPrintableArea.MM)
                };
            }
            if (c == javax.print.attribute.standard.Media.class) {
                return new javax.print.attribute.standard.Media[] {
                    javax.print.attribute.standard.MediaSizeName.ISO_A4
                };
            }
            if (c == javax.print.attribute.standard.OrientationRequested.class) {
                return new javax.print.attribute.standard.OrientationRequested[] {
                    javax.print.attribute.standard.OrientationRequested.PORTRAIT,
                    javax.print.attribute.standard.OrientationRequested.LANDSCAPE
                };
            }
            if (c == javax.print.attribute.standard.Copies.class) {
                return new javax.print.attribute.standard.CopiesSupported(1, 99);
            }
            return null;
        }
        @Override public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes) { return true; }
        @Override public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) { return null; }
        @Override public ServiceUIFactory getServiceUIFactory() { return null; }
    }

    private static boolean isEditableInput(Component c) {
        Component cur = c;
        for (int i = 0; i < 4 && cur != null; i++) {
            if (cur instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) cur;
                return tc.isEditable() && tc.isEnabled();
            }
            if (cur instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) cur;
                return cb.isEditable() && cb.isEnabled();
            }
            if (cur instanceof JTable) {
                JTable tbl = (JTable) cur;
                if (tbl.isEditing()) return true;
            }
            cur = cur.getParent();
        }
        return false;
    }

    private static void triggerAndroidKeyboard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendToAndroidApk("IME|SHOW");
            }
        }, "Asycuda-IME").start();
    }

    private static void checkForSoClassUpdateAlert(Container root) {
        if (root == null) return;
        try {
            if (root instanceof Dialog) {
                String title = ((Dialog) root).getTitle();
                if (isSoClassUpdateText(title)) {
                    notifySoClassUpdateRequired(title);
                    return;
                }
            }
            for (Component c : root.getComponents()) {
                if (c instanceof JLabel) {
                    String txt = ((JLabel) c).getText();
                    if (isSoClassUpdateText(txt)) {
                        notifySoClassUpdateRequired(txt);
                        return;
                    }
                } else if (c instanceof JOptionPane) {
                    Object msg = ((JOptionPane) c).getMessage();
                    if (msg != null && isSoClassUpdateText(String.valueOf(msg))) {
                        notifySoClassUpdateRequired(String.valueOf(msg));
                        return;
                    }
                } else if (c instanceof JTextComponent) {
                    String txt = ((JTextComponent) c).getText();
                    if (isSoClassUpdateText(txt)) {
                        notifySoClassUpdateRequired(txt);
                        return;
                    }
                    if (isBenignAsycudaPopupText(txt)) {
                        dismissBenignErrorInfoDialog(root);
                        return;
                    }
                }
                if (c instanceof Container) {
                    checkForSoClassUpdateAlert((Container) c);
                }
            }
        } catch (Throwable ignored) {
            // Ignore component inspection errors
        }
    }

    private static boolean isBenignAsycudaPopupText(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.contains("R_Load_SadItmAdm.execute(")
                || text.contains("R_Load_HisWriteOff.execute(")
                || text.contains("VisualFacetJTableSDS.updateComponent(");
    }

    private static void dismissBenignErrorInfoDialog(Container root) {
        try {
            Window win = (root instanceof Window) ? (Window) root : SwingUtilities.getWindowAncestor(root);
            if (win != null && win.getClass().getName().startsWith("so.kernel.core.ErrorPrintStream")) {
                try {
                    Class<?> epsClass = Class.forName("so.kernel.core.ErrorPrintStream", false, win.getClass().getClassLoader());
                    Method finishMethod = epsClass.getDeclaredMethod("finishDialog");
                    finishMethod.setAccessible(true);
                    finishMethod.invoke(null);
                    return;
                } catch (Throwable ignored) {
                    // Fallback to direct window disposal
                }
            }
            if (win != null) {
                win.setVisible(false);
                win.dispose();
            }
        } catch (Throwable ignored) {
            // Ignore dialog dismissal errors
        }
    }

    private static boolean isSoClassUpdateText(String text) {
        if (text == null || text.isEmpty()) return false;
        String lower = text.toLowerCase(Locale.US);
        return (lower.contains("soclass") && (lower.contains("update") || lower.contains("shutdown")))
                || lower.contains("soclass client have to be updated")
                || lower.contains("client have to be updated");
    }

    private static boolean isBenignAsycudaThrowable(Throwable t) {
        if (!(t instanceof NullPointerException)) return false;
        StackTraceElement[] stack = t.getStackTrace();
        if (stack == null || stack.length == 0) return false;
        int limit = Math.min(stack.length, 4);
        for (int i = 0; i < limit; i++) {
            String cls = stack[i].getClassName();
            String mth = stack[i].getMethodName();
            if (("un.broker.asysad.client.rules.R_Load_SadItmAdm".equals(cls) && "execute".equals(mth))
                    || ("un.broker.asysad.client.rules.R_Load_HisWriteOff".equals(cls) && "execute".equals(mth))
                    || ("so.kernel.core.client.facets.VisualFacetJTableSDS".equals(cls) && "updateComponent".equals(mth))) {
                return true;
            }
        }
        return false;
    }

    private static final class BenignNpeFilteringPrintStream extends PrintStream {
        private final PrintStream delegate;
        private final ThreadLocal<Boolean> suppressingStack = new ThreadLocal<Boolean>();

        BenignNpeFilteringPrintStream(PrintStream delegate) {
            super(delegate, true);
            this.delegate = delegate;
        }

        @Override
        public void println(Object x) {
            if (x instanceof Throwable) {
                Throwable t = (Throwable) x;
                if (isBenignAsycudaThrowable(t)) {
                    suppressingStack.set(Boolean.TRUE);
                    StackTraceElement top = t.getStackTrace()[0];
                    System.out.println("[ASYCUDA-Bridge] Suppressed benign UNCTAD client NPE popup at "
                            + top.getClassName() + "." + top.getMethodName() + ":" + top.getLineNumber());
                    return;
                }
                suppressingStack.remove();
            } else if (Boolean.TRUE.equals(suppressingStack.get())) {
                String s = String.valueOf(x);
                if (s.startsWith("\tat ") || s.startsWith("\t... ") || s.startsWith("Caused by:")) {
                    return;
                }
                suppressingStack.remove();
            }
            delegate.println(x);
        }

        @Override
        public void println(String x) {
            if (Boolean.TRUE.equals(suppressingStack.get())) {
                if (x != null && (x.startsWith("\tat ") || x.startsWith("\t... ") || x.startsWith("Caused by:"))) {
                    return;
                }
                suppressingStack.remove();
            }
            delegate.println(x);
        }
    }

    private static void installErrorPrintStreamFilterHook() {
        try {
            PrintStream currentErr = System.err;
            if (currentErr != null
                    && !(currentErr instanceof BenignNpeFilteringPrintStream)
                    && "so.kernel.core.ErrorPrintStream".equals(currentErr.getClass().getName())) {
                System.setErr(new BenignNpeFilteringPrintStream(currentErr));
            }
        } catch (Throwable ignored) {
            // Ignore if System.setErr is not yet initialized
        }
    }

    private static final class GuardedMultiLinguisticList extends ArrayList<java.lang.ref.WeakReference<Object>> {
        private final transient ClassLoader cl;
        private final transient Class<?> visualPageClass;
        private final transient Class<?> listenerInterface;
        private final transient Class<?> subDocClass;
        private final transient Class<?> abstractFacetClass;
        private final transient Method getDocumentMethod;
        private final transient Method getConnectedDocsIterMethod;
        private final transient java.lang.reflect.Field facetsField;
        private final transient java.lang.reflect.Field procSupportField;
        private final transient java.lang.reflect.Field guiHasBeenInit1Field;
        private final transient java.lang.reflect.Field subDocGuiInitField;
        private final transient Method isGuiInitMethod;
        private final transient Method dataGUIInitMethod;
        private final transient Map<Object, Object> strongProxyCache =
                Collections.synchronizedMap(new WeakHashMap<Object, Object>());

        GuardedMultiLinguisticList(Collection<java.lang.ref.WeakReference<Object>> initial, ClassLoader cl) throws Exception {
            super(initial);
            this.cl = cl;
            this.visualPageClass = Class.forName("so.kernel.client.VisualPage", false, cl);
            this.listenerInterface = Class.forName("so.i18n.MultiLinguisticListener", false, cl);
            Class<?> docClass = Class.forName("so.kernel.core.Document", false, cl);
            Class<?> procSupportClass = Class.forName("so.kernel.core.DocProcSupport", false, cl);
            this.subDocClass = Class.forName("so.kernel.core.SubDocument", false, cl);
            this.abstractFacetClass = Class.forName("so.kernel.core.client.facets.AbstractVisualFacet", false, cl);

            this.getDocumentMethod = visualPageClass.getMethod("getDocument");
            this.getConnectedDocsIterMethod = visualPageClass.getMethod("getConnectedDocumentsIter");
            this.facetsField = visualPageClass.getDeclaredField("facets");
            this.facetsField.setAccessible(true);

            this.procSupportField = docClass.getDeclaredField("procSupport");
            this.procSupportField.setAccessible(true);

            this.guiHasBeenInit1Field = procSupportClass.getDeclaredField("guiHasBeenInit1");
            this.guiHasBeenInit1Field.setAccessible(true);

            this.subDocGuiInitField = subDocClass.getDeclaredField("guiHasBeenInit");
            this.subDocGuiInitField.setAccessible(true);

            this.isGuiInitMethod = abstractFacetClass.getDeclaredMethod("isGuiInit");
            this.isGuiInitMethod.setAccessible(true);
            this.dataGUIInitMethod = abstractFacetClass.getMethod("dataGUIInit");
        }

        @Override
        public java.lang.ref.WeakReference<Object> get(int index) {
            java.lang.ref.WeakReference<Object> ref = super.get(index);
            if (ref == null) return null;
            final Object target = ref.get();
            if (target == null || !visualPageClass.isInstance(target) || Proxy.isProxyClass(target.getClass())) {
                return ref;
            }
            Object proxy = strongProxyCache.get(target);
            if (proxy == null) {
                proxy = Proxy.newProxyInstance(
                        cl,
                        new Class<?>[] { listenerInterface },
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
                                if (!"languageChanged".equals(method.getName())) {
                                    return method.invoke(target, args);
                                }
                                Object procSupport = null;
                                boolean origDocGuiInit = false;
                                List<Object> resetSubDocs = new ArrayList<Object>();
                                try {
                                    Object doc = getDocumentMethod.invoke(target);
                                    if (doc != null) {
                                        procSupport = procSupportField.get(doc);
                                        if (procSupport != null && guiHasBeenInit1Field.getBoolean(procSupport)) {
                                            origDocGuiInit = true;
                                            guiHasBeenInit1Field.setBoolean(procSupport, false);
                                        }
                                    }
                                    Iterator<?> iter = (Iterator<?>) getConnectedDocsIterMethod.invoke(target);
                                    if (iter != null) {
                                        while (iter.hasNext()) {
                                            Object item = iter.next();
                                            if (item != null && subDocClass.isInstance(item)
                                                    && subDocGuiInitField.getBoolean(item)) {
                                                resetSubDocs.add(item);
                                                subDocGuiInitField.setBoolean(item, false);
                                            }
                                        }
                                    }
                                    return method.invoke(target, args);
                                } catch (java.lang.reflect.InvocationTargetException ite) {
                                    throw ite.getTargetException();
                                } finally {
                                    try {
                                        if (procSupport != null && origDocGuiInit) {
                                            guiHasBeenInit1Field.setBoolean(procSupport, true);
                                        }
                                        for (Object sd : resetSubDocs) {
                                            subDocGuiInitField.setBoolean(sd, true);
                                        }
                                        if (origDocGuiInit || !resetSubDocs.isEmpty()) {
                                            Object rawFacets = facetsField.get(target);
                                            if (rawFacets instanceof List<?>) {
                                                List<?> facetsCopy = new ArrayList<Object>((List<?>) rawFacets);
                                                for (Object facet : facetsCopy) {
                                                    if (facet != null && abstractFacetClass.isInstance(facet)) {
                                                        try {
                                                            if (Boolean.FALSE.equals(isGuiInitMethod.invoke(facet))) {
                                                                dataGUIInitMethod.invoke(facet);
                                                            }
                                                        } catch (Throwable ignored) {
                                                            // Ignore individual facet post-init errors
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Throwable ignored) {
                                        // Ignore state restoration errors
                                    }
                                }
                            }
                        });
                strongProxyCache.put(target, proxy);
            }
            return new java.lang.ref.WeakReference<Object>(proxy);
        }
    }

    @SuppressWarnings("unchecked")
    private static void installLanguageChangeFacetGuardHook() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            Class<?> resClass = Class.forName("so.kernel.client.Resources", false, cl);
            java.lang.reflect.Field mlField = resClass.getDeclaredField("multiLinguisticListeners");
            mlField.setAccessible(true);
            Object weakColl = mlField.get(null);
            if (weakColl == null) return;

            Class<?> wcClass = weakColl.getClass();
            java.lang.reflect.Field elementsField = wcClass.getDeclaredField("elements");
            elementsField.setAccessible(true);
            Object currentElements = elementsField.get(weakColl);
            if (currentElements == null || currentElements == hookedMultiLinguisticElements
                    || currentElements instanceof GuardedMultiLinguisticList) {
                return;
            }

            java.lang.reflect.Field lockField = wcClass.getDeclaredField("lock");
            lockField.setAccessible(true);
            java.util.concurrent.locks.ReentrantReadWriteLock rwLock =
                    (java.util.concurrent.locks.ReentrantReadWriteLock) lockField.get(weakColl);
            rwLock.writeLock().lock();
            try {
                Collection<java.lang.ref.WeakReference<Object>> orig =
                        (Collection<java.lang.ref.WeakReference<Object>>) elementsField.get(weakColl);
                if (!(orig instanceof GuardedMultiLinguisticList)) {
                    GuardedMultiLinguisticList guarded = new GuardedMultiLinguisticList(orig, cl);
                    elementsField.set(weakColl, guarded);
                    hookedMultiLinguisticElements = guarded;
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        } catch (Throwable ignored) {
            // Resources class may not be initialized until desktop startup
        }
    }

    private static void installSoClassRejectHandlerHook() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            Class<?> ctlClass = Class.forName("so.rmi.transport.client.ClientTransportLayer", false, cl);
            Class<?> rhInterface = Class.forName("so.rmi.transport.RejectHandler", false, cl);
            Method getMethod = ctlClass.getMethod("getRejectHandler");
            final Object current = getMethod.invoke(null);
            if (current != null && current != wrappedRejectHandler && !Proxy.isProxyClass(current.getClass())) {
                Method setMethod = ctlClass.getMethod("setRejectHandler", rhInterface);
                Object proxy = Proxy.newProxyInstance(
                        rhInterface.getClassLoader(),
                        new Class<?>[] { rhInterface },
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
                                if ("reject".equals(method.getName())) {
                                    notifySoClassUpdateRequired("SOClass Client have to be updated. It will be shutdown now.");
                                }
                                return method.invoke(current, args);
                            }
                        });
                setMethod.invoke(null, proxy);
                wrappedRejectHandler = proxy;
            }
        } catch (Throwable ignored) {
            // Class may not be loaded until login/session initialization
        }
    }

    private static void startWindowManagerAndFileWatcher() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                try {
                    if (event instanceof MouseEvent) {
                        MouseEvent me = (MouseEvent) event;
                        if (me.getID() == MouseEvent.MOUSE_PRESSED || me.getID() == MouseEvent.MOUSE_RELEASED) {
                            lastMousePressTime = System.currentTimeMillis();
                            Component src = me.getComponent();
                            if (src != null) {
                                Window win = (src instanceof Window) ? (Window) src : SwingUtilities.getWindowAncestor(src);
                                if (win != null && !win.isFocused()) {
                                    win.toFront();
                                    win.requestFocus();
                                }
                                Component deep = SwingUtilities.getDeepestComponentAt(src, me.getX(), me.getY());
                                if (isEditableInput(deep != null ? deep : src)) {
                                    triggerAndroidKeyboard();
                                }
                            }
                        }
                    } else if (event instanceof FocusEvent) {
                        FocusEvent fe = (FocusEvent) event;
                        if (fe.getID() == FocusEvent.FOCUS_GAINED && fe.getComponent() != null
                                && (System.currentTimeMillis() - lastMousePressTime) < 650L
                                && isEditableInput(fe.getComponent())) {
                            triggerAndroidKeyboard();
                        }
                    }
                } catch (Throwable ignored) {
                    // Ignore UI event listener exceptions
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);

        // 1. Pure-Java Window Manager (Auto-maximize main ASYCUDA frame, focus dialogs & detect SOClass alerts)
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                int id = event.getID();
                if ((id == WindowEvent.WINDOW_OPENED || id == ComponentEvent.COMPONENT_SHOWN)
                        && event.getSource() instanceof Window) {
                    final Window w = (Window) event.getSource();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
                                if (w instanceof JFrame) {
                                    JFrame f = (JFrame) w;
                                    if (f.getWidth() > scr.width * 0.6 || f.getHeight() > scr.height * 0.6 ||
                                        f.getClass().getName().contains("Desktop")) {
                                        f.setExtendedState(Frame.MAXIMIZED_BOTH);
                                        f.setBounds(0, 0, scr.width, scr.height);
                                    }
                                } else if (w instanceof Dialog) {
                                    checkForSoClassUpdateAlert(w);
                                    redirectFileChooserToDownloads(w);
                                    if (w.getX() <= 0 && w.getY() <= 0) {
                                        w.setLocationRelativeTo(w.getOwner());
                                    }
                                }
                                w.toFront();
                                w.requestFocus();
                            } catch (Throwable ignored) {
                                // Ignore window layout adjustment errors
                            }
                        }
                    });
                }
            }
        }, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);

        // 2. Background Daemon Thread: Watch tmp/, auto-rename .so downloads, and hook SOClass RejectHandler
        Thread watcher = new Thread(new Runnable() {
            @Override
            public void run() {
                File tmpDir = new File(System.getProperty("java.io.tmpdir", DEFAULT_TMP_DIR));
                File dlDir = getWorkspaceDir();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        installSoClassRejectHandlerHook();
                        installErrorPrintStreamFilterHook();
                        installLanguageChangeFacetGuardHook();
                        scanTmpDir(tmpDir, 0);
                        scanDownloadsForSo(dlDir);
                        checkWorkspaceImportSignal(dlDir);
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Throwable ignored) {
                        // Continue watching on transient I/O errors
                    }
                }
            }
        }, "Asycuda-FileDaemon");
        watcher.setDaemon(true);
        watcher.start();
    }

    private static void triggerAutomaticAndroidFilePicker(final File workspaceDir) {
        long now = System.currentTimeMillis();
        if ((now - lastAutoPickTime) < 1200L) {
            return;
        }
        lastAutoPickTime = now;
        if (workspaceDir != null && workspaceDir.exists()) {
            File staleImport = new File(workspaceDir, ".last_imported");
            if (staleImport.exists() && !staleImport.delete()) {
                // Ignore best-effort cleanup
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendToAndroidApk("PICK_WORKSPACE_FILE");
            }
        }, "Asycuda-AutoPickBridge").start();
    }

    private static void populateFileChooserTextField(Container root, String fileName) {
        if (root == null || fileName == null) return;
        for (Component c : root.getComponents()) {
            if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                if (tf.isEditable() && tf.isEnabled()) {
                    tf.setText(fileName);
                }
            } else if (c instanceof Container) {
                populateFileChooserTextField((Container) c, fileName);
            }
        }
    }

    private static void checkWorkspaceImportSignal(final File workspaceDir) {
        if (workspaceDir == null || !workspaceDir.exists()) return;

        File marker = new File(workspaceDir, ".last_imported");
        if (!marker.exists()) return;
        String importedName = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(marker), StandardCharsets.UTF_8))) {
            importedName = br.readLine();
        } catch (Throwable ignored) {
            // Ignore marker read failure
        }
        if (!marker.delete()) {
            // Best-effort cleanup
        }
        if (importedName == null || importedName.trim().isEmpty()) return;
        final File importedFile = new File(workspaceDir, importedName.trim());
        if (!importedFile.exists()) return;
        final JFileChooser fc = activeFileChooser;
        final Dialog dlg = activeFileChooserDialog;
        if (fc != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (fc.getFileFilter() != null && !fc.getFileFilter().accept(importedFile)) {
                            fc.setFileFilter(fc.getAcceptAllFileFilter());
                        }
                        fc.setCurrentDirectory(workspaceDir);
                        fc.rescanCurrentDirectory();
                        fc.setSelectedFile(importedFile);
                        populateFileChooserTextField(fc, importedFile.getName());
                        fc.repaint();
                        if (dlg != null && dlg.isShowing()) {
                            dlg.toFront();
                            dlg.requestFocus();
                        }

                        // Re-assert selection after async BasicDirectoryModel LoadFilesThread completes
                        // without closing the Java JFileChooser so the user can confirm inside Java UI.
                        javax.swing.Timer refreshTimer = new javax.swing.Timer(260, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    fc.rescanCurrentDirectory();
                                    fc.setSelectedFile(importedFile);
                                    populateFileChooserTextField(fc, importedFile.getName());
                                    fc.repaint();
                                } catch (Throwable ignored) {
                                    // Ignore refresh error
                                }
                            }
                        });
                        refreshTimer.setRepeats(false);
                        refreshTimer.start();
                    } catch (Throwable ignored) {
                        // Ignore Swing update errors
                    }
                }
            });
        }
    }

    private static final class VaultFileSystemView extends javax.swing.filechooser.FileSystemView {
        private final File vaultRoot;
        private final String vaultCanon;

        VaultFileSystemView(File root) {
            this.vaultRoot = root;
            String canon;
            try {
                canon = root.getCanonicalPath();
            } catch (IOException e) {
                canon = root.getAbsolutePath();
            }
            this.vaultCanon = canon;
        }

        boolean isAllowed(File f) {
            if (f == null) return false;
            try {
                String c = f.getCanonicalPath();
                return c.equals(vaultCanon) || c.startsWith(vaultCanon + File.separator);
            } catch (IOException e) {
                String a = f.getAbsolutePath();
                return a.equals(vaultCanon) || a.startsWith(vaultCanon + File.separator);
            }
        }

        @Override
        public File createNewFolder(File containingDir) throws IOException {
            File base = isAllowed(containingDir) ? containingDir : vaultRoot;
            File folder = new File(base, "NewFolder");
            int i = 1;
            while (folder.exists() && i < 100) {
                folder = new File(base, "NewFolder_" + i++);
            }
            if (!folder.mkdirs() && !folder.exists()) {
                throw new IOException("Unable to create folder");
            }
            return folder;
        }

        @Override
        public File[] getRoots() {
            return new File[] { vaultRoot };
        }

        @Override
        public File getHomeDirectory() {
            return vaultRoot;
        }

        @Override
        public File getDefaultDirectory() {
            return vaultRoot;
        }

        @Override
        public boolean isRoot(File f) {
            return f != null && (f.equals(vaultRoot) || !isAllowed(f));
        }

        @Override
        public boolean isFileSystemRoot(File dir) {
            return dir != null && (dir.equals(vaultRoot) || !isAllowed(dir));
        }

        @Override
        public File getParentDirectory(File dir) {
            if (dir == null || dir.equals(vaultRoot) || !isAllowed(dir)) {
                return vaultRoot;
            }
            File parent = dir.getParentFile();
            return (parent != null && isAllowed(parent)) ? parent : vaultRoot;
        }

        @Override
        public File[] getFiles(File dir, boolean useFileHiding) {
            File target = isAllowed(dir) ? dir : vaultRoot;
            File[] list = target.listFiles();
            if (list == null) return new File[0];
            List<File> visible = new ArrayList<File>();
            for (File f : list) {
                if (!f.getName().startsWith(".")) {
                    visible.add(f);
                }
            }
            return visible.toArray(new File[0]);
        }

        @Override
        public String getSystemDisplayName(File f) {
            if (f != null && f.equals(vaultRoot)) {
                return "ASYCUDA Vault (prints)";
            }
            return f != null ? f.getName() : "";
        }
    }

    private static void redirectFileChooserToDownloads(Container root) {
        if (root == null) return;
        final File dlDir = getWorkspaceDir();
        if (!dlDir.exists() && !dlDir.mkdirs()) {
            // Directory already exists or will be created on demand
        }
        if (root instanceof FileDialog) {
            FileDialog fd = (FileDialog) root;
            fd.setDirectory(dlDir.getAbsolutePath());
            if (fd.getMode() == FileDialog.LOAD) {
                triggerAutomaticAndroidFilePicker(dlDir);
            }
            return;
        }
        for (Component c : root.getComponents()) {
            if (c instanceof JFileChooser) {
                final JFileChooser fc = (JFileChooser) c;
                activeFileChooser = fc;
                Window win = SwingUtilities.getWindowAncestor(fc);
                if (win instanceof Dialog) {
                    activeFileChooserDialog = (Dialog) win;
                } else if (root instanceof Dialog) {
                    activeFileChooserDialog = (Dialog) root;
                }
                if (!(fc.getFileSystemView() instanceof VaultFileSystemView)) {
                    final VaultFileSystemView vaultView = new VaultFileSystemView(dlDir);
                    fc.setFileSystemView(vaultView);
                    fc.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
                            new java.beans.PropertyChangeListener() {
                                @Override
                                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                                    File cur = fc.getCurrentDirectory();
                                    if (cur == null || !vaultView.isAllowed(cur)) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                fc.setCurrentDirectory(dlDir);
                                            }
                                        });
                                    }
                                }
                            });
                }
                fc.setCurrentDirectory(dlDir);
                fc.rescanCurrentDirectory();
                if (fc.getDialogType() != JFileChooser.SAVE_DIALOG) {
                    triggerAutomaticAndroidFilePicker(dlDir);
                }
            } else if (c instanceof Container) {
                redirectFileChooserToDownloads((Container) c);
            }
        }
    }

    private static boolean isFileAlreadyInVault(File vaultDir, File candidate, String candidateSha256, int depth) {
        if (vaultDir == null || !vaultDir.exists() || candidate == null || depth > 4) return false;
        File[] list = vaultDir.listFiles();
        if (list == null) return false;
        long candLen = candidate.length();
        for (File vf : list) {
            if (vf.getName().startsWith(".")) continue;
            if (vf.isDirectory()) {
                if (isFileAlreadyInVault(vf, candidate, candidateSha256, depth + 1)) {
                    return true;
                }
            } else if (vf.isFile() && vf.length() == candLen) {
                if (vf.getName().equalsIgnoreCase(candidate.getName())
                        || computeSha256(vf).equalsIgnoreCase(candidateSha256)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void scanTmpDir(File dir, int depth) {
        if (dir == null || !dir.exists() || depth > 3) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.getName().startsWith(".")) continue;
            if (f.isDirectory()) {
                String dlow = f.getName().toLowerCase(Locale.US);
                if ("soe.tmp".equals(dlow) || "son.tmp".equals(dlow)) continue;
                scanTmpDir(f, depth + 1);
            } else {
                String name = f.getName();
                String low = name.toLowerCase(Locale.US);
                if (name.startsWith("Resource") && name.endsWith(".so")) continue;
                if (low.startsWith("soems") || low.endsWith("ems")) continue;
                if (low.startsWith("prn") && low.endsWith("xc")) continue;
                if (low.endsWith(".part") || low.endsWith(".lck") || low.endsWith(".tmp")) continue;
                if (f.length() <= 0) continue;
                String key = f.getAbsolutePath() + "_" + f.lastModified() + "_" + f.length();
                if (!SEEN_PATHS.containsKey(key)) {
                    SEEN_PATHS.put(key, System.currentTimeMillis());
                    String candHash = computeSha256(f);
                    if (isFileAlreadyInVault(getWorkspaceDir(), f, candHash, 0)) {
                        continue;
                    }
                    processAndOpenFile(f);
                }
            }
        }
    }

    private static void scanDownloadsForSo(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".so") && f.length() > 0) {
                try {
                    long s1 = f.length();
                    Thread.sleep(150);
                    if (f.length() != s1) continue;
                    String[] info = detectExtensionAndMime(f);
                    String ext = info[0];
                    if (!"so".equals(ext) && !"bin".equals(ext)) {
                        String base = f.getName().substring(0, f.getName().length() - 3);
                        File renamed = new File(f.getParentFile(), base + "." + ext);
                        if (!f.renameTo(renamed)) {
                            // Keep original file if rename is not permitted
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Throwable ignored) {
                    // Ignore individual file inspection failures
                }
            }
        }
    }

    private static void installHighSpeedNetworkTuning() {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("networkaddress.cache.ttl", "-1");
            System.setProperty("sun.net.inetaddr.ttl", "-1");
            System.setProperty("networkaddress.cache.negative.ttl", "0");
            System.setProperty("http.keepAlive", "true");
            System.setProperty("http.maxConnections", "32");
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "120000");
            System.setProperty("sun.rmi.transport.connectionTimeout", "15000");

            final java.rmi.server.RMISocketFactory defaultFactory =
                    java.rmi.server.RMISocketFactory.getDefaultSocketFactory();
            java.rmi.server.RMISocketFactory.setSocketFactory(new java.rmi.server.RMISocketFactory() {
                @Override
                public Socket createSocket(String host, int port) throws IOException {
                    Socket s = defaultFactory.createSocket(host, port);
                    try {
                        s.setTcpNoDelay(true);
                        s.setKeepAlive(true);
                        s.setReceiveBufferSize(1024 * 1024);
                        s.setSendBufferSize(1024 * 1024);
                        s.setTrafficClass(0x10 | 0x08);
                    } catch (Throwable ignored) {
                        // Socket options are best-effort
                    }
                    return s;
                }

                @Override
                public java.net.ServerSocket createServerSocket(int port) throws IOException {
                    return defaultFactory.createServerSocket(port);
                }
            });
        } catch (Throwable ignored) {
            // RMI socket factory may already be set
        }
    }

    private static final Set<String> ALLOWED_CUSTOMS_IPS = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    static {
        ALLOWED_CUSTOMS_IPS.add("185.112.191.94");
        ALLOWED_CUSTOMS_IPS.add("118.97.60.75");
        // Internal virtualized RMI endpoint IP returned by asy.mof.gov.iq:2006 (ClientTransportLayer)
        ALLOWED_CUSTOMS_IPS.add("10.25.25.10");
    }

    public static void refreshAllowedCustomsIps() {
        try {
            java.net.InetAddress[] addrs = java.net.InetAddress.getAllByName("asy.mof.gov.iq");
            if (addrs != null) {
                for (java.net.InetAddress a : addrs) {
                    if (a != null && a.getHostAddress() != null) {
                        ALLOWED_CUSTOMS_IPS.add(a.getHostAddress().trim().toLowerCase(Locale.US));
                    }
                }
            }
        } catch (Throwable ignored) {
            // Keep pre-seeded official Ministry of Finance IPs if offline
        }
    }

    private static boolean isRegisteredMofVirtualEndpoint(String hostIp) {
        if (Boolean.TRUE.equals(IN_CHECK.get())) return false;
        IN_CHECK.set(Boolean.TRUE);
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            Class<?> ctlClass = Class.forName("so.rmi.transport.client.ClientTransportLayer", false, cl);
            java.lang.reflect.Field hooksField = ctlClass.getDeclaredField("hooks");
            hooksField.setAccessible(true);
            java.lang.reflect.Field remoteHostField = ctlClass.getDeclaredField("remoteHost");
            remoteHostField.setAccessible(true);
            java.lang.reflect.Field transportsField = ctlClass.getDeclaredField("transports");
            transportsField.setAccessible(true);
            Object hooksObj = hooksField.get(null);
            if (hooksObj instanceof java.util.Collection) {
                for (Object ctl : (java.util.Collection<?>) hooksObj) {
                    if (ctl == null) continue;
                    Object rhObj = remoteHostField.get(ctl);
                    String rh = rhObj != null ? rhObj.toString().trim().toLowerCase(Locale.US) : "";
                    if ("asy.mof.gov.iq".equals(rh) || rh.endsWith(".mof.gov.iq") || ALLOWED_CUSTOMS_IPS.contains(rh)) {
                        Object trMap = transportsField.get(ctl);
                        if (trMap instanceof java.util.Map) {
                            for (Object k : ((java.util.Map<?, ?>) trMap).keySet()) {
                                if (k != null) {
                                    ALLOWED_CUSTOMS_IPS.add(k.toString().trim().toLowerCase(Locale.US));
                                }
                            }
                        }
                    }
                }
            }
            return ALLOWED_CUSTOMS_IPS.contains(hostIp);
        } catch (Throwable ignored) {
            return false;
        } finally {
            IN_CHECK.remove();
        }
    }

    public static void checkAllowedOutboundNetwork(String host, int port) {
        if (host == null) return;
        String h = host.trim().toLowerCase(Locale.US);
        if (h.isEmpty()) return;
        if (h.startsWith("[") && h.endsWith("]")) {
            h = h.substring(1, h.length() - 1);
        }
        // 1. Allow loopback & local X11 / IPC bridge sockets
        if ("localhost".equals(h)
                || "localhost.localdomain".equals(h)
                || "127.0.0.1".equals(h)
                || h.startsWith("127.")
                || "::1".equals(h)
                || "0:0:0:0:0:0:0:1".equals(h)
                || "0.0.0.0".equals(h)) {
            return;
        }
        // Allow local single-label device hostname resolution during InetAddress.getLocalHost() (port == -1)
        if (port == -1 && !h.contains(".") && !h.contains(":")) {
            return;
        }
        // 2. Allow official Iraqi Ministry of Finance / General Commission of Customs domains & resolved/virtual IPs
        if ("asy.mof.gov.iq".equals(h)
                || h.endsWith(".asy.mof.gov.iq")
                || h.endsWith(".mof.gov.iq")
                || h.endsWith(".asycuda.gov.iq")
                || ALLOWED_CUSTOMS_IPS.contains(h)
                || isRegisteredMofVirtualEndpoint(h)) {
            return;
        }
        throw new SecurityException("Blocked unauthorized outbound JVM network connection to: " + host + ":" + port);
    }

    public static void checkBlockedHostPath(String file) {
        if (file == null || file.isEmpty()) return;
        String norm = file.replace('\\', '/').toLowerCase(Locale.US);
        if (norm.contains("/shared_prefs")
                || norm.contains("legal_consent_receipt.json")
                || norm.contains("/databases/")
                || norm.contains("/datastore/")) {
            throw new SecurityException("JVM access to Android host storage path is blocked: " + file);
        }
    }

    public static SecurityManager createHardenedSecurityManager() {
        return new SecurityManager() {
            @Override public void checkPermission(Permission perm) { /* Fine-grained checks handled below */ }
            @Override public void checkPermission(Permission perm, Object context) { /* Fine-grained checks handled below */ }
            @Override public void checkExec(String cmd) {
                throw new SecurityException("OS subprocess execution is blocked inside the ASYCUDA JVM sandbox: " + cmd);
            }
            @Override public void checkConnect(String host, int port) {
                checkAllowedOutboundNetwork(host, port);
            }
            @Override public void checkConnect(String host, int port, Object context) {
                checkAllowedOutboundNetwork(host, port);
            }
            @Override public void checkRead(String file) {
                checkBlockedHostPath(file);
            }
            @Override public void checkRead(String file, Object context) {
                checkBlockedHostPath(file);
            }
            @Override public void checkDelete(String file) {
                checkBlockedHostPath(file);
            }
            @Override public void checkWrite(String file) {
                checkBlockedHostPath(file);
                if (Boolean.TRUE.equals(IN_CHECK.get())) return;
                if (file != null && file.contains("/tmp/")) {
                    IN_CHECK.set(Boolean.TRUE);
                    try {
                        File f = new File(file);
                        File parent = f.getParentFile();
                        if (parent != null) {
                            if (parent.isFile() && !parent.delete()) {
                                // Ignore if unable to delete stale file
                            }
                            if (!parent.exists() && !parent.mkdirs()) {
                                // Ignore if directory already exists
                            }
                        }
                    } finally {
                        IN_CHECK.remove();
                    }
                }
            }
        };
    }

    public static void main(String[] args) throws Exception {
        installHighSpeedNetworkTuning();
        refreshAllowedCustomsIps();
        // Install a hardened SecurityManager that enforces:
        // 1. Strict outbound network allowlist (checkConnect: only localhost/127.0.0.1 and asy.mof.gov.iq)
        // 2. Complete block on OS subprocess execution (checkExec: Runtime.exec / ProcessBuilder)
        // 3. Complete isolation from Android host preferences/databases (checkRead / checkWrite / checkDelete)
        try {
            System.setSecurityManager(createHardenedSecurityManager());
        } catch (UnsupportedOperationException ignored) {
            // OpenJDK 18+ may disallow System.setSecurityManager unless -Djava.security.manager=allow is set
        }

        try {
            final PrintService pdfPrinter = new DirectPdfPrintService();
            PrintServiceLookup.registerServiceProvider(new PrintServiceLookup() {
                @Override public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes) {
                    return new PrintService[] { pdfPrinter };
                }
                @Override public PrintService[] getPrintServices() {
                    return new PrintService[] { pdfPrinter };
                }
                @Override public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes) {
                    return new MultiDocPrintService[0];
                }
                @Override public PrintService getDefaultPrintService() {
                    return pdfPrinter;
                }
            });
        } catch (Throwable ignored) {
            // Continue if default print service provider is already registered
        }

        startWindowManagerAndFileWatcher();
        Class<?> desktopMain = Class.forName("so.kernel.client.DesktopMain");
        desktopMain.getMethod("main", String[].class).invoke(null, (Object) args);
    }
}
