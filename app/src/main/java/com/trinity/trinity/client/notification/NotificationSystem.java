package com.trinity.trinity.client.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.trinity.trinity.client.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationSystem {

    private static final NotificationSystem INSTANCE = new NotificationSystem();

    public static NotificationSystem getInstance() {
        return INSTANCE;
    }


    public void deactivate() {
        MusicPlayerNotification.deactivate();
    }

    public void send(final int id, final Notification notification) {
        ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                ((KillNotificationService.KillBinder) binder).service.startService(new Intent(
                        MainActivity.getInstance(), KillNotificationService.class));
                NotificationManager mNM = (NotificationManager) MainActivity.getInstance().getSystemService(NOTIFICATION_SERVICE);
                mNM.notify(id,
                        notification);
            }

            public void onServiceDisconnected(ComponentName className) {
            }

        };
        MainActivity.getInstance().bindService(new Intent(MainActivity.getInstance().getApplicationContext(),
                        KillNotificationService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }
}
