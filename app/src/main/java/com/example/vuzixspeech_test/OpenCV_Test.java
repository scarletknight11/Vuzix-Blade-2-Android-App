package com.example.vuzixspeech_test;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.Manifest;


public class OpenCV_Test extends AppCompatActivity {

    Button select, camera;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE = 100, CAMERA_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);

        if (OpenCVLoader.initDebug())
            Log.d("LOADED", "success");
        else
            Log.d("LOADED", "error");

        getPermissions();
        
        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageview);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_CODE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SELECT_CODE && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);

                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);

                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

                Utils.matToBitmap(mat, bitmap);
                imageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode==CAMERA_CODE && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);


        }
    }

    void getPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length>0) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getPermissions();
            }
        }
    }
}
