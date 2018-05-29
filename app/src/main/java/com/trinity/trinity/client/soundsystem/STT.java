package com.trinity.trinity.client.soundsystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trinity.trinity.R;
import com.trinity.trinity.client.MainActivity;
import com.trinity.trinity.client.commands.CommandHandler;
import com.trinity.trinity.client.commands.RequestCodes;
import com.trinity.trinity.client.soundsystem.music.MusicPlayer;

import java.util.ArrayList;

public class STT implements RecognitionListener {

    private static final String LOG_TAG = "Recording: ";

    private static final STT INSTANCE = new STT();

    public static STT getInstance() {
        return INSTANCE;
    }


    TextView returnResult;
    ImageButton listenButton;

    private SpeechRecognizer speech;
    private Intent intent;

    private boolean isListening;

    private STT() {
        returnResult = MainActivity.getInstance().findViewById(R.id.result);
        listenButton = MainActivity.getInstance().findViewById(R.id.listen);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isListening)
                    ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.RECORD_AUDIO}, RequestCodes.RECORD_PERMISSION);
                else
                    speech.stopListening();
            }
        });

        speech = SpeechRecognizer.createSpeechRecognizer(MainActivity.getInstance().getApplicationContext());
        speech.setRecognitionListener(this);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "de-DE");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    public void startListening() {
        if (!isListening) {
            isListening = true;
            speech.startListening(intent);
            listenButton.setColorFilter(Color.rgb(45, 188, 255));
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        onStopListening();
    }

    private void onStopListening() {
        isListening = false;
        TTS.getInstance().continueTalking();

        final Handler handler = new Handler();
        if (MusicPlayer.hasInstance()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.getInstance().onUserFinish();
                }
            }, 500);
        }
        listenButton.setColorFilter(Color.rgb(100, 100, 100));

    }

    @Override
    public void onError(int errorCode) {
        onStopListening();
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults: ");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        TTS.getInstance().stopTalking();
        if (MusicPlayer.hasInstance())
            MusicPlayer.getInstance().onUserSpeaking();
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        returnResult.setText(matches.get(0));
        CommandHandler.onCommand(matches.get(0));
        System.out.println(matches.get(0));

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
