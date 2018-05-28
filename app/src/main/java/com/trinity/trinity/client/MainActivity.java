package com.trinity.trinity.client;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.trinity.trinity.R;
import com.trinity.trinity.client.commands.RequestCodes;
import com.trinity.trinity.client.soundsystem.STT;
import com.trinity.trinity.client.soundsystem.SoundSystem;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static MainActivity INSTANCE;

    private static final int tickerRate = 30; // in tps

    public static MainActivity getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ON CREATE a a");
        System.out.println("luul");
        INSTANCE = this;

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Trinity.setup();
        SoundSystem.setup();

        // Setup Ticker
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onTick();
                    }
                });
            }
        };
        int period = Math.round(1000 / (float) tickerRate);
        timer.scheduleAtFixedRate(timerTask, period, period);
    }

    private void onTick() {
        Trinity.onTick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestCodes.RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    STT.getInstance().startListening();
                } else {
                    permissionDenied("Zugriff auf Aufnahmefunktion wurde verweigert.");
                }
                break;
            case RequestCodes.STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MusicPlayer.getInstance().startPlayingPermissionGranted();
                } else {
                    permissionDenied("Zugriff auf externe Dateien wurde verweigert.");
                }
                break;
        }
    }

    private void permissionDenied(String text) {
        Toast.makeText(this, "Permission Denied!", Toast
                .LENGTH_SHORT).show();
        Trinity.log(text);
    }
}

