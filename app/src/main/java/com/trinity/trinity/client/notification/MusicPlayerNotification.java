package com.trinity.trinity.client.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.trinity.trinity.R;
import com.trinity.trinity.Util;
import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;

public class MusicPlayerNotification {

    static final int MAX_PROGRESS = 100;
    private static Notification notification;

    @SuppressWarnings("deprecation")
    public static void setNotification(String songName, String songArtist) {
        Notification notification = new Notification(R.drawable.media_music_triplet_icon, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(MainActivity.getInstance().getPackageName(), R.layout.media_music_notification);

        // Text setup

        notificationView.setTextViewText(R.id.media_trackname, songName);
        notificationView.setTextViewText(R.id.media_artist, songArtist);

        Intent notificationIntent = new Intent(MainActivity.getInstance(), RemoteControlReceiver.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(MainActivity.getInstance(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;

        // Buttons setup

        Intent switchIntent = new Intent(Notifications.actions.PLAY);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(MainActivity.getInstance(), 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.media_action_playpause, pendingSwitchIntent);

        switchIntent = new Intent(Notifications.actions.NEXT);
        pendingSwitchIntent = PendingIntent.getBroadcast(MainActivity.getInstance(), 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.media_action_next, pendingSwitchIntent);

        switchIntent = new Intent(Notifications.actions.STOP);
        pendingSwitchIntent = PendingIntent.getBroadcast(MainActivity.getInstance(), 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.media_action_stop, pendingSwitchIntent);

        // Progressbar setup

        notificationView.setProgressBar(R.id.media_status_progress, MAX_PROGRESS, 0, false);

        // Sending Notification

        NotificationSystem.getInstance().send(Notifications.MUSIC_PLAYER, notification);
        MusicPlayerNotification.notification = notification;
    }

    @SuppressWarnings("deprecation")
    public static void updateNotification(int progress) {
        if (notification == null)
            return;
        RemoteViews remoteViews = new RemoteViews(MainActivity.getInstance().getPackageName(), R.layout.media_music_notification);
        remoteViews.setProgressBar(R.id.media_status_progress, MAX_PROGRESS, progress, false);
        notification.contentView = remoteViews;
        NotificationSystem.getInstance().send(Notifications.MUSIC_PLAYER, notification);
    }

    public static void deactivate() {
        NotificationManager nm = (NotificationManager) MainActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Notifications.MUSIC_PLAYER);
    }

    @SuppressWarnings("deprecation")
    public static class RemoteControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) MainActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            MusicPlayer mp = MusicPlayer.getInstance();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.media_music_notification);
            String action = intent.getAction();

            if (action.equalsIgnoreCase(Notifications.actions.PLAY)) {
                if (mp.isPlaying()){
                    mp.pause();
                    remoteViews.setImageViewResource(R.id.media_action_playpause, R.drawable.media_music_play);
                    notification.contentView = remoteViews;
                    NotificationSystem.getInstance().send(Notifications.MUSIC_PLAYER, notification);
                }
                else {
                    mp.startPlaying();
                    remoteViews.setImageViewResource(R.id.media_action_playpause, R.drawable.media_music_pause);
                    notification.contentView = remoteViews;
                    NotificationSystem.getInstance().send(Notifications.MUSIC_PLAYER, notification);
                }
            }
            else if (action.equalsIgnoreCase(Notifications.actions.NEXT)) {
                mp.startPlaying();
            }
            else if (action.equalsIgnoreCase(Notifications.actions.STOP)) {
                mp.stopPlaying();
                notificationManager.cancel(Notifications.MUSIC_PLAYER);
            }
        }
    }

}
