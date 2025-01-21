package com.example.vuzixspeech_test;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private DatabaseReference reference;
    private VoiceCmdReceiver voiceCmdReceiver;
    private TextView transcribedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextView
        transcribedText = findViewById(R.id.transcribed_text);

        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reference = db.getReference("Records");


        // Enable Vuzix Speech Client
        try {
            VuzixSpeechClient.TriggerVoiceAudio(getApplicationContext(), true);
            Toast.makeText(this, "Vuzix Speech SDK Enabled", Toast.LENGTH_SHORT).show();
        } catch (NoClassDefFoundError e) {
            Toast.makeText(this, "Cannot find Vuzix Speech SDK", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Vuzix Speech SDK: " + e.getMessage());
        }

        // Initialize VoiceCmdReceiver
        try {
            voiceCmdReceiver = new VoiceCmdReceiver(this, reference, transcribedText);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing VoiceCmdReceiver: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        if (voiceCmdReceiver != null) {
            unregisterReceiver(voiceCmdReceiver);
        }
    }
}
