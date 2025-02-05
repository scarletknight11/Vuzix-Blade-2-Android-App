package com.example.vuzixspeech_test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.PixelCopy;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class URLTest extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        TextView textView = findViewById(R.id.response);
        findViewById(R.id.send).setOnClickListener(click-> {
            Request.Builder builder = new Request.Builder();
            //RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "");
            Request request = builder
                    .url("https://publicobject.com/helloworld.txt")
                    .get()
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            try {
                Response response = client.newCall(request).execute();
                textView.setText(request.body().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
