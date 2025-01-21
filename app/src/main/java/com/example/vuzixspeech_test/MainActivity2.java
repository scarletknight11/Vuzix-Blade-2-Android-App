package com.example.vuzixspeech_test;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import android.Manifest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity2 extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    FirebaseDatabase db;
    MediaController mediaController;
    DatabaseReference reference;
    StorageReference storageReference;
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();


        // Set audio file path
        audioFilePath = getRecordingFilePath();

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }
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

            // Upload the audio file to Firebase Storage
            //uploadAudioToFirebase();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile" + ".mp3");
        return file.getPath();
    }

//    private void uploadAudioToFirebase() {
//        // Get reference to Firebase Storage location
//        StorageReference audioRef = storageReference.child("audio/" + System.currentTimeMillis() + ".mp3");
//
//        // Get the audio file
//        File audioFile = new File(audioFilePath);
//
//        // Upload the file
//        audioRef.putFile(Uri.fromFile(audioFile))
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Get the download URL and save it to Realtime Database
//                    audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
////                        db = FirebaseDatabase.getInstance();
////                        reference = db.getReference("Records");
//
//                        // Store the download URL in the database
////                        reference.push().setValue(uri)
////                                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity2.this, "Audio uploaded successfully", Toast.LENGTH_SHORT).show())
////                                .addOnFailureListener(e -> Toast.makeText(MainActivity2.this, "Failed to upload audio to database", Toast.LENGTH_SHORT).show());
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to upload audio", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                });
//        }
}
