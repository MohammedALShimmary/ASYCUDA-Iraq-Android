package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

/**
 * Keeps the ASYCUDA World JVM and its RMI/TCP sockets alive while the user switches
 * to other Android apps (PDF viewer, WhatsApp, Browser, etc.).
 */
public class AsycudaKeepAliveService extends Service {

    private static final int FG_NOTIFICATION_ID = 7895;
    private static final String CHANNEL_ID = "asycuda_session_channel";

    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock mWifiLock;

    @Override
    public void onCreate() {
        super.onCreate();
        acquireLocks();
    }

    public static final String ACTION_EXIT_APP = "iq.asycuda.ACTION_EXIT_APP";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_EXIT_APP.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            AsycudaStandaloneManager.shutdownApp(this);
            return START_NOT_STICKY;
        }
        startForeground(FG_NOTIFICATION_ID, buildForegroundNotification());
        acquireLocks();
        return START_STICKY;
    }

    @SuppressLint("WakelockTimeout")
    private void acquireLocks() {
        try {
            if (mWakeLock == null) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pm != null) {
                    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "iq.asycuda::SessionWakeLock");
                    mWakeLock.setReferenceCounted(false);
                }
            }
            if (mWakeLock != null && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
        } catch (Exception ignored) {
            // Ignore wake lock acquisition error if restricted by device OEM
        }

        try {
            if (mWifiLock == null) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wm != null) {
                    int lockMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            ? WifiManager.WIFI_MODE_FULL_LOW_LATENCY
                            : WifiManager.WIFI_MODE_FULL_HIGH_PERF;
                    mWifiLock = wm.createWifiLock(lockMode, "iq.asycuda::SessionWifiLock");
                    mWifiLock.setReferenceCounted(false);
                }
            }
            if (mWifiLock != null && !mWifiLock.isHeld()) {
                mWifiLock.acquire();
            }
        } catch (Exception ignored) {
            // Ignore Wi-Fi lock acquisition error if restricted by device OEM
        }
    }

    private Notification buildForegroundNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && nm != null) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "ASYCUDA Active Session",
                    NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Keeps the ASYCUDA World network connection active in the background");
            ch.setShowBadge(false);
            nm.createNotificationChannel(ch);
        }

        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int piFlags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent, piFlags);

        Intent exitIntent = new Intent(this, AsycudaKeepAliveService.class);
        exitIntent.setAction(ACTION_EXIT_APP);
        PendingIntent exitPi = PendingIntent.getService(this, 99, exitIntent, piFlags);

        String exitLabel = AsycudaMaterialUi.tr(this, "Exit", "خروج", "دەرچوون");

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ASYCUDA World Iraq")
                .setContentText(AsycudaMaterialUi.tr(this,
                        "Session active • Network protected in background",
                        "الجلسة نشطة • الاتصال محمي في الخلفية",
                        "دانیشتن چالاکە • هێڵی ئینتەرنێت پارێزراوە"))
                .setSmallIcon(R.drawable.ic_x11_icon)
                .setContentIntent(pi)
                .addAction(R.drawable.ic_menu_exit, exitLabel, exitPi)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        try {
            if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
        } catch (Exception ignored) {
            // Ignore wake lock release error
        }
        try {
            if (mWifiLock != null && mWifiLock.isHeld()) mWifiLock.release();
        } catch (Exception ignored) {
            // Ignore Wi-Fi lock release error
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
