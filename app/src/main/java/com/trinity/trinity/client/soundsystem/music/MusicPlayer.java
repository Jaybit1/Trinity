package com.trinity.trinity.client.soundsystem.music;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.Trinity;
import com.trinity.trinity.client.commands.RequestCodes;
import com.trinity.trinity.client.notification.MusicPlayerNotification;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayer {

    private static MusicPlayer INSTANCE;

    public static MusicPlayer getInstance() {
        if (!hasInstance())
            INSTANCE = new MusicPlayer();
        return INSTANCE;
    }

    public static boolean hasInstance() {
        return INSTANCE != null;
    }


    private MediaPlayer mediaPlayer;
    private boolean isPaused;

    public MusicPlayer() {
        INSTANCE = this;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startPlayingPermissionGranted();
            }
        });
    }

    private ArrayList<Song> getAllSongs() {
        ArrayList<Song> list = new ArrayList<>();

        ContentResolver contentResolver = MainActivity.getInstance().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            if (songCursor != null && songCursor.moveToFirst()) {
                do {
                    list.add(getSong(songCursor));
                } while (songCursor.moveToNext());
            }
        }
        return list;
    }

    private Song getRandomSong() {
        ContentResolver contentResolver = MainActivity.getInstance().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        int index = (int) Math.round(Math.random() * songCursor.getCount());

        while (songCursor != null && songCursor.moveToPosition(index)) {
            Song s = getSong(songCursor);
            if (s.getDuration() < 10000)
                index = (int) Math.round(Math.random() * songCursor.getCount());
            else
                return s;
        }
        return null;
    }

    private Song getSong(Cursor songCursor) {
        int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

        do {
            String currentTitle = songCursor.getString(songTitle);
            String currentArtist = songCursor.getString(songArtist);
            String currentLocation = songCursor.getString(songLocation);
            long currentDuration = songCursor.getLong(songDuration);

            return new Song(currentTitle, currentArtist, currentLocation, currentDuration);
        } while (songCursor.moveToNext());
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    private int interval = 100;
    private int progress = 0;
    private Runnable progressTask = new Runnable() {
        @Override
        public void run() {
            while (isPlaying()) {
                progress++;
                MusicPlayerNotification.updateNotification(progress);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) { e.printStackTrace(); break; }
            }
        }
    };

    public void stopPlaying() {
        mediaPlayer.stop();
    }

    public void pause() {
        mediaPlayer.pause();
        isPaused = true;
    }

    public void continuePlaying() {
        mediaPlayer.start();
        isPaused = false;
        new Thread(progressTask).start();
    }

    public void startPlaying() {
        ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCodes.STORAGE_PERMISSION);
    }

    public void startPlayingPermissionGranted() {
        if (isPaused) {
            continuePlaying();
            return;
        }
        progress = 0;
        mediaPlayer.reset();
        Song song = getRandomSong();
        isPaused = false;
        try {
            MusicPlayerNotification.setNotification(song.getTitle(), song.getArtist());
            String message = "Spiele den Song " + song.getTitle();
            if (!song.getArtist().toLowerCase().equalsIgnoreCase("<unknown>"))
                message += " von " + song.getArtist();
            Trinity.log(message);

            mediaPlayer.setDataSource(MainActivity.getInstance().getApplicationContext(), Uri.parse(song.getLocation()));
            mediaPlayer.prepare();
            mediaPlayer.start();

            interval = Math.round((float) song.getDuration() / 100f);
            new Thread(progressTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isTrinitySpeaking = false;
    private boolean isUserSpeaking = false;

    public void onTrinitySpeaking() {
        isTrinitySpeaking = true;
        if (!isUserSpeaking)
            setVolume(0.25f);
        else
            setVolume(0.1f);
    }

    public void onTrinityFinished() {
        isTrinitySpeaking = false;
        if (!isUserSpeaking)
            setVolume(1f);
        else
            setVolume(0.1f);
    }

    public void onUserSpeaking() {
        setVolume(0.1f);
    }

    public void onUserFinish() {
        if (!isTrinitySpeaking)
            setVolume(1f);
        else
            setVolume(0.25f);
    }

    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

}
