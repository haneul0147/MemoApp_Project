package com.blockent.memoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

public class LogoActivity extends AppCompatActivity {

    Thread thread;
    boolean interrupted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final Handler handler = new Handler();
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        thread = new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(2000);
                if(!interrupted){
                    handler.post(doNextActivity);
                }
            }
        };
        thread.start();
    }

    @Override
    public void onBackPressed() {
        interrupted = true;
        super.onBackPressed();
    }
}