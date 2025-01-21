package com.example.vuzixspeech_test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

public class VoiceCmdReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "VoiceCmdReceiver";
    private final DatabaseReference reference;
    private final TextView transcribedText;

    public VoiceCmdReceiver(Context context, DatabaseReference reference, TextView transcribedText) throws RemoteException {
        this.reference = reference;
        this.transcribedText = transcribedText;

        // Register the receiver for voice commands
        IntentFilter filter = new IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND);
        context.registerReceiver(this, filter);

        // Initialize Vuzix Speech Client
        VuzixSpeechClient sc = new VuzixSpeechClient((Activity) context);

        // Register phrases for testing
        sc.insertPhrase("hello");
        sc.insertPhrase("start recording");
        sc.insertPhrase("stop recording");

        Log.i(LOG_TAG, "Phrases registered: hello, start recording, stop recording.");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(VuzixSpeechClient.ACTION_VOICE_COMMAND)) {
            String phrase = intent.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA);
            Log.d(LOG_TAG, "Recognized phrase: " + phrase);

            if (phrase != null) {
                // Update TextView with the recognized phrase
                transcribedText.post(() -> {
                    String existingText = transcribedText.getText().toString();
                    transcribedText.setText(existingText + "\n" + phrase);
                });
                // Save the recognized phrase to Firebase with success and failure listeners
                reference.push().setValue(phrase);

            } else {
                Log.d(LOG_TAG, "No phrase recognized.");
            }
        }
    }
}
