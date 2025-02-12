package com.example.vuzixspeech_test;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    private static final int MICROPHONE_PERMISSION_CODE = 200;
    private static final String TRANSCRIPTION_API_URL = "https://vuzixaudio-default-rtdb.firebaseio.com/"; // Replace with your transcription API URL
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private FirebaseDatabase db;
    private MediaController mediaController;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private String audioFilePath;
    private TextView textView;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = findViewById(R.id.textView);
        scanButton = findViewById(R.id.button5);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Set audio file path
        audioFilePath = getRecordingFilePath();

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ScannerActivity when the scan button is clicked
                Intent intent = new Intent(MainActivity2.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void btnRecordPressed(View v) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnStopPressed(View v) {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_LONG).show();

            // Transcribe the audio
            transcribeAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnPlayPressed(View v) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnStoredPressed(View v) {
        try {
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Records");
            reference.push().setValue(audioFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMicrophonePresent() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile" + ".mp3");
        return file.getPath();
    }

    private void transcribeAudio() {
        OkHttpClient client = new OkHttpClient();
        File audioFile = new File(audioFilePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mp3"), audioFile);

        Request request = new Request.Builder()
                .url(TRANSCRIPTION_API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity2.this, "Failed to transcribe audio", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String transcription = response.body().string();
                    runOnUiThread(() -> {
                        textView.setText(transcription);

                        // Store the transcription in Firebase
                        reference.push().setValue(transcription)
                                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity2.this, "Transcription saved to Firebase", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(MainActivity2.this, "Failed to save transcription", Toast.LENGTH_SHORT).show());
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity2.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
