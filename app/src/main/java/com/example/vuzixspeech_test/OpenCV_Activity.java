package com.example.vuzixspeech_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OpenCV_Activity extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    CascadeClassifier cascadeClassifier;
    Button back;
    Mat gray, rgb, transpose_gray, transpose_rgb;
    MatOfRect rects;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_test);

        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        back = findViewById(R.id.backButton);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                rgb = new Mat();
                gray = new Mat();
                rects = new MatOfRect();
            }

            @Override
            public void onCameraViewStopped() {
                rgb.release();
                gray.release();
                rects.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

                //todo: process input frame and return processed one ...
                gray = inputFrame.gray();
                rgb = inputFrame.rgba();


                transpose_gray = gray.t();
                transpose_rgb = rgb.t();

                MatOfRect rects = new MatOfRect();

                cascadeClassifier.detectMultiScale(transpose_gray, rects, 1.1, 2);

                for(Rect rect: rects.toList()) {

                    Mat submat = transpose_rgb.submat(rect);

                    Imgproc.blur(submat, submat, new Size(10, 10));
                    Imgproc.rectangle(transpose_rgb, rect, new Scalar(0, 255, 0), 10);

                    submat.release();
                }
                return transpose_rgb.t();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenCV_Activity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        if(OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();

            try {
                InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                File file = new File(getDir("cascade", MODE_PRIVATE), "lbpcascade_frontalface.xml");
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] data = new byte[4096];
                int read_bytes;

                while ((read_bytes = inputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, read_bytes);
                }

                cascadeClassifier = new CascadeClassifier(file.getAbsolutePath());
                if(cascadeClassifier.empty()) cascadeClassifier = null;

                inputStream.close();
                fileOutputStream.close();
                file.delete();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }
}
