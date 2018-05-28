package com.trinity.trinity.client.soundsystem;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.trinity.trinity.Util;
import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;

import java.io.File;


public class SoundSystem {

    public static void setup() {
        STT.getInstance();
        TTS.getInstance();
    }

    public static void onTick() {
        TTS.getInstance().update();
    }

    public static void stopSounds() {
        TTS.getInstance().stopAndDequeueAll();
        if (MusicPlayer.hasInstance())
            MusicPlayer.getInstance().stopPlaying();
    }

    public static void playMp3(MediaPlayer mediaPlayer, byte[] data)
    {
        try {
            String filename = "audio.mp3";
            Context context = MainActivity.getInstance().getApplicationContext();
            File path = Util.tempsave(data, "", filename, context);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.fromFile(path));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception | Error e) { e.printStackTrace(); }
    }

}
