package com.example.ap2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Main2Activity extends AppCompatActivity {
    private static int SPLASH_TIMEOUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Main2Activity.this, com.example.ap2.MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMEOUT);
    }
}
