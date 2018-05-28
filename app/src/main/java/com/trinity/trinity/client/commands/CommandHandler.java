package com.trinity.trinity.client.commands;

import android.content.Context;
import android.media.AudioManager;

import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.Trinity;
import com.trinity.trinity.client.information.InformationFetcher;
import com.trinity.trinity.client.soundsystem.SoundSystem;
import com.trinity.trinity.client.soundsystem.TTS;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;

public class CommandHandler {

    private static Runnable QNA_Action;

    public static void onCommand (final String cmd) {
        if (cmdEqualTo(cmd, Commands.generic.STOP))
            SoundSystem.stopSounds();
        else if (cmdEqualTo(cmd, Commands.generic.NEXT))
            Generics.next();
        else if (cmdEqualTo(cmd, Commands.generic.REPEAT))
            TTS.getInstance().repeatLastMessage();

        else if (cmdStartsWith(cmd, Commands.generic.FETCH_INFORMATION))
            InformationFetcher.getInstance().fetchInformation(cmd);
        else if (QNA_Action != null) {
            if (cmdEqualTo(cmd, Commands.information.YES)) {
                QNA_Action.run();
                QNA_Action = null;
            } else if (cmdEqualTo(cmd, Commands.information.NO))
                QNA_Action = null;
            else
                Trinity.log("Ich konnte dich nicht verstehen, bitte antworte mit Ja oder Nein.");
        }


        else if (cmdStartsWith(cmd, Commands.generic.MAKE_HIGHER))
            Generics.change(cmd, 1);
        else if (cmdStartsWith(cmd, Commands.generic.MAKE_LOWER))
            Generics.change(cmd, -1);
        else if (cmdStartsWith(cmd, Commands.generic.SET))
            Generics.set(cmd);

        else if (cmdEqualTo(cmd, Commands.server.CONNECT))
            Trinity.establishConnection();
        else if (cmdEqualTo(cmd, Commands.server.DISCONNECT))
            Trinity.disconnect();

        else if (cmdEqualTo(cmd, Commands.media.music.PLAY) && !MusicPlayer.getInstance().isPlaying())
            MusicPlayer.getInstance().startPlaying();
        else if (cmdEqualTo(cmd, Commands.media.music.PAUSE))
            MusicPlayer.getInstance().pause();

        else if (isEasterEgg(cmd))
            return;

        else {
            executeQNA("Ich bin mir nicht sicher, was ich tun soll. Soll ich nach " + cmd + " auf Google suchen?", new Runnable() {
                @Override
                public void run() {
                    InformationFetcher.getInstance().executeGoogleSearch(cmd);
                }
            });
        }
    }

    public static void executeQNA (String question, Runnable executionOnPositiveAnswer) {
        Trinity.log(question);
        Trinity.queueCode(TTS.Codes.CONVERSATION);
        QNA_Action = executionOnPositiveAnswer;
    }

    public static boolean cmdStartsWith (String cmd, String[] versions) {
        cmd = cmd.toLowerCase();
        for (String version: versions) {
            if (cmd.startsWith(version.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean cmdEqualTo (String cmd, String other) {
        return cmd.equalsIgnoreCase(other);
    }

    public static boolean cmdEqualTo (String cmd, String[] versions) {
        cmd = cmd.toLowerCase();
        for (String version: versions) {
            if (cmd.equalsIgnoreCase(version.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean cmdContains (String cmd, String[] versions) {
        cmd = cmd.toLowerCase();
        for (String version: versions) {
            if (cmd.contains(version.toLowerCase()))
                return true;
        }
        return false;
    }

    private static int extractNumber (String cmd) {
        cmd = cmd.toLowerCase();
        if (cmd.contains("zehn"))
            return 10;
        else if(cmd.contains("null"))
            return 0;

        String numberOnly = cmd.replaceAll("[^0-9]", "");
        if (numberOnly == "")
            return 0;
        else {
            try {
                return Integer.valueOf(numberOnly);
            } catch (Exception | Error e) {
                return 0;
            }
        }
    }

    private static boolean isEasterEgg (String cmd) {
        if (cmdEqualTo(cmd, Commands.eastereggs.THANKS))
            Trinity.log("Gern geschehen.");
        else if (cmdEqualTo(cmd, Commands.eastereggs.HYDRA))
            Trinity.log("Heil Hydra.");
        else
            return false;
        return true;
    }

    private static class Generics {

        static void next() {
            if (MusicPlayer.hasInstance() && (MusicPlayer.getInstance().isPlaying() || MusicPlayer.getInstance().isPaused()))
                MusicPlayer.getInstance().startPlayingPermissionGranted();
        }

        static void change (String cmd, int signum){
            if (cmdContains(cmd, Commands.media.VOLUME)) {
                AudioManager audioManager = (AudioManager) MainActivity.getInstance().getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, signum > 0 ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        }

        static void set (String cmd) {
            if (cmdContains(cmd, Commands.media.VOLUME)) {
                AudioManager audioManager = (AudioManager) MainActivity.getInstance().getSystemService(Context.AUDIO_SERVICE);
                int value = extractNumber(cmd);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_SHOW_UI);
            }
        }

    }

}
