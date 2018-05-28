package com.trinity.trinity.client.soundsystem;

import android.media.MediaPlayer;

import com.trinity.trinity.client.Trinity;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;
import com.voicerss.tts.AudioCodec;
import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.Languages;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;

import java.util.LinkedList;
import java.util.Queue;

public class TTS {

    private static final TTS INSTANCE = new TTS();
    public static final float RATE = 44100;
    public static final int CHANNELS = 2;

    public static TTS getInstance() {
        return INSTANCE;
    }

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean mute = false;

    private VoiceProvider tts;
    private VoiceParameters params;

    private String lastMessage = "";
    private String lastCode = "";

    private Queue<String> messageQueue = new LinkedList<>();

    private TTS() {
        tts = new VoiceProvider("d3ef6ede1a7940c8a16454a00fcfc4f8");

        params = new VoiceParameters("", Languages.German);
        params.setCodec(AudioCodec.MP3);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                if (MusicPlayer.hasInstance())
                    MusicPlayer.getInstance().onTrinityFinished();
            }
        });
    }

    public void repeatLastMessage() {
        enqueueMessage(lastMessage);
        if (!lastCode.equalsIgnoreCase(""))
            enqueueMessage(lastCode);
    }

    public void enqueueMessage (String message) {
        messageQueue.add(AnglicismAppender.getPhoneticTranscription(message));
    }

    public void overrideQueue (String message) {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        isPlaying = false;
        messageQueue.clear();
        if (message != "")
            enqueueMessage(message);
    }

    public void clearQueue () {
        messageQueue.clear();
    }

    public void stopAndDequeueAll() {
        overrideQueue("");
    }

    public void stopTalking() {
        if (isPlaying)
            mediaPlayer.reset();
        isPlaying = false;
        mute = true;
    }

    public void continueTalking() {
        mute = false;
    }

    protected void update() {
        if (messageQueue.size() == 0)
            return;
        if (isPlaying || mute)
            return;
        playNextInQueue();
    }

    private void playNextInQueue() {
        while (messageQueue.peek() == "")
            messageQueue.remove();
        while (isCode(messageQueue.peek())) {
            if (messageQueue.size() > 1) {
                Object[] mqa = messageQueue.toArray();
                boolean allCodes = true;
                for (int i=0; i<mqa.length; i++) {
                    if (!isCode((String) mqa[i])) {
                        allCodes = false;
                        for (int a = 0; a<i; a++) {
                            String s = messageQueue.poll();
                            messageQueue.add(s);
                        }
                        i = mqa.length;
                    }
                }
                if (allCodes) {
                    for (String c: messageQueue) {
                        c = messageQueue.poll();
                        executeCode(c);
                    }
                    return;
                }
            } else {
                executeCode(messageQueue.poll());
                return;
            }
        }

        isPlaying = true;
        String message = messageQueue.poll();
        params.setText(message);
        lastMessage = message;

        if (messageQueue.size() != 0) {
            String code = messageQueue.peek();
            System.out.println("Size != 0");
            if (isCode(code)) {
                System.out.println("code: " + code);
                lastCode = code;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] voice = null;

                try {
                    voice = tts.speech(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (MusicPlayer.hasInstance())
                    MusicPlayer.getInstance().onTrinitySpeaking();
                if (voice != null)
                    SoundSystem.playMp3(mediaPlayer, voice);
                else
                    throw new NullPointerException("Voice returned null");
            }
        }).start();
    }

    private boolean executeCodeIfIsCode(String code, boolean execute) {
        switch (code) {
            case Codes.WELCOME:
                if (execute) {
                    Trinity.log("Hallo. Wie kann ich dir behilflich sein?");
                    Trinity.queueCode(Codes.CONVERSATION);
                }
                break;

            case Codes.CONVERSATION:
                if (execute)
                    STT.getInstance().startListening();
                break;

            default:
                return false;
        }
        return true;
    }

    private boolean isCode(String string) {
        return executeCodeIfIsCode(string, false);
    }

    private void executeCode(String code) {
        executeCodeIfIsCode(code, true);
    }

    public static class Codes {
        public static final String WELCOME = "#\0000";
        public static final String CONVERSATION = "#\0001";
    }

}