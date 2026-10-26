package com.termux.x11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Keep;

import dalvik.annotation.optimization.CriticalNative;

@Keep @SuppressLint("StaticFieldLeak")
public class CmdEntryPoint extends ICmdEntryInterface.Stub {
    public static final String ACTION_START = "com.termux.x11.CmdEntryPoint.ACTION_START";
    public static final String EXTRA_DOCUMENT_TAG = "document_tag";
    static final Handler handler;
    public static Context ctx;
    private final Intent intent = createIntent();
    private boolean broadcastPending;

    CmdEntryPoint(String[] args) {
        if (!start(args))
            System.exit(1);
    }

    // Called from native once OsVendorInit() resolves the "-tag" argument, if any.
    void setDocumentTag(String tag) {
        if (tag != null)
            intent.putExtra(EXTRA_DOCUMENT_TAG, tag);
    }

    private Intent createIntent() {
        Bundle bundle = new Bundle();
        bundle.putBinder(null, this);

        Intent intent = new Intent(ACTION_START);
        intent.putExtra(null, bundle);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        return intent;
    }

    private void sendBroadcast() {
        // Called from native (the X server thread) on every knock on the port; coalesce
        // bursts into a single broadcast fired 250ms later.
        synchronized (this) {
            if (broadcastPending)
                return;
            broadcastPending = true;
        }
        handler.postDelayed(() -> {
            synchronized (this) { broadcastPending = false; }
            sendBroadcast(intent);
        }, 250);
    }

    static void sendBroadcast(Intent intent) {
        try {
            if (ctx != null) {
                ctx.sendBroadcast(intent, "iq.asycuda.PERMISSION_INTERNAL_BROADCAST");
            }
        } catch (Exception e) {
            Log.e("Broadcast", "Failed to broadcast intent through Context:", e);
        }
    }

    // In some cases Android Activity part can not connect opened port.
    // In this case opened port works like a lock file.
    private void sendBroadcastDelayed() {
        if (!connected())
            sendBroadcast(intent);

        handler.postDelayed(this::sendBroadcastDelayed, 1000);
    }

    public native boolean start(String[] args);
    public native ParcelFileDescriptor getXConnection();
    public native ParcelFileDescriptor getLogcatOutput();
    public native void reportFatalError(String message);
    @CriticalNative private static native boolean connected();

    static {
        try {
            if (Looper.getMainLooper() == null)
                Looper.prepareMainLooper();
        } catch (Exception e) {
            Log.e("CmdEntryPoint", "Something went wrong when preparing MainLooper", e);
        }
        handler = new Handler();
    }
}
