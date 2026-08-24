package com.manoel.audiobooster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build;
import android.os.IBinder;

public class BoosterService extends Service {
    public static final String ACTION_ENABLE = "com.manoel.audiobooster.ENABLE";
    public static final String ACTION_DISABLE = "com.manoel.audiobooster.DISABLE";
    public static final String ACTION_SET_GAIN = "com.manoel.audiobooster.SET_GAIN";
    public static final String EXTRA_GAIN = "gain_mb";

    private static final String CHANNEL_ID = "audio_booster";
    private static final int NOTIFICATION_ID = 1001;
    private LoudnessEnhancer enhancer;
    private int gainMb = 600;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) return START_NOT_STICKY;

        String action = intent.getAction();
        if (intent.hasExtra(EXTRA_GAIN)) gainMb = Math.max(0, Math.min(1200, intent.getIntExtra(EXTRA_GAIN, 600)));

        if (ACTION_ENABLE.equals(action)) {
            startForeground(NOTIFICATION_ID, buildNotification("Ativando booster..."));
            enableBooster();
        } else if (ACTION_SET_GAIN.equals(action)) {
            if (enhancer != null) {
                try {
                    enhancer.setTargetGain(gainMb);
                    updateNotification("Booster ativo: +" + String.format("%.1f", gainMb / 100.0) + " dB");
                } catch (RuntimeException e) {
                    updateNotification("Não foi possível alterar o ganho neste aparelho.");
                }
            } else {
                stopSelf(startId);
            }
        } else if (ACTION_DISABLE.equals(action)) {
            disableBooster();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void enableBooster() {
        disableBooster();
        try {
            // Session 0 requests processing on the global output mix. Some Android/OEM builds reject it.
            enhancer = new LoudnessEnhancer(0);
            enhancer.setTargetGain(gainMb);
            enhancer.setEnabled(true);
            updateNotification("Booster ativo: +" + String.format("%.1f", gainMb / 100.0) + " dB");
        } catch (Throwable e) {
            enhancer = null;
            updateNotification("Este aparelho bloqueou o booster global.");
        }
    }

    private void disableBooster() {
        if (enhancer != null) {
            try { enhancer.setEnabled(false); } catch (Throwable ignored) {}
            try { enhancer.release(); } catch (Throwable ignored) {}
            enhancer = null;
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Audio Booster", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Mantém o booster de áudio ativo");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String text) {
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);
        return b.setContentTitle("Audio Booster")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String text) {
        getSystemService(NotificationManager.class).notify(NOTIFICATION_ID, buildNotification(text));
    }

    @Override
    public void onDestroy() {
        disableBooster();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
