package com.example.vuzix_blade_elitecad;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.IOException;

public class face_recongition extends AppCompatActivity {
    // First add implementation of tensorflow
    // import interpretor
    private Interpreter intepreter;
    // define input size of model
    private int INPUT_SIZE;
    // define height and width of frame
    private int height=0;
    private int width=0;
    // now define Gpudelegate
    private GpuDelegate gpuDelegate=null;
    // This is used to run model using GPU
    // Now define CascadeClassifier
    private CascadeClassifier cascadeClassifier;
    // now create
    face_recongition(AssetManager assetManager, Context context, String modelPath, int input_size) throws IOException {
        // now call this class in CameraActivity
    }

}
