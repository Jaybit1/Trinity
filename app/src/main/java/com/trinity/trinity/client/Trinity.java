package com.trinity.trinity.client;

import android.support.annotation.NonNull;
import android.util.Log;
import com.jaybit.client.Client;
import com.trinity.trinity.client.notification.NotificationSystem;
import com.trinity.trinity.client.soundsystem.SoundSystem;
import com.trinity.trinity.client.soundsystem.TTS;

public class Trinity {

    public static void log(@NonNull Object log) {
        log(log.toString());
    }

    public static void log(String log) {
        TTS.getInstance().enqueueMessage(log);
        Log.e("Trinity:", log);
    }

    public static void queueCode(String code) {
        log(code);
    }

    public static void overrideLog(@NonNull Object log) {
        overrideLog(log.toString());
    }

    public static void overrideLog (String log) {
        TTS.getInstance().overrideQueue(log);
    }

    private static Client client;

    public static Client getClient() {
        return client;
    }

    public static void setup() {
        String ip = "192.168.178.39";
        int port = 25565;
        try {
            client = Client.createClient(ip, port, 2000);
            client.getEventManager().registerListener(new EventListener());
        } catch (Error | Exception e) {
        }
    }

    public static void onDestroy() {
        NotificationSystem.getInstance().deactivate();
    }


    public static void onTick() {
        SoundSystem.onTick();
        client.callQueuedEvents();
    }

    public static void establishConnection() {
        try {
            if (client.isConnected()) {
                log("Verbindung besteht bereits.");
            }
            else {
                log("Verbindung wird hergestellt.");
                try {
                    client.connect();
                } catch (Exception | Error e) {
                    overrideLog("Verbindung fehlgeschlagen!");
                }
                queueCode(TTS.Codes.CONVERSATION);
            }
        } catch (Error | Exception e) {
            overrideLog("Verbindung fehlgeschlagen!");
        }
    }

    public static void disconnect() {
        if (!client.isConnected()) {
            log("Die Verbindung besteht noch nicht.");
        }
        else {
            client.disconnect();
            log("Verbindung getrennt.");
        }
    }

    public static String getString(int id) {
        return MainActivity.getInstance().getResources().getString(id);
    }

}
