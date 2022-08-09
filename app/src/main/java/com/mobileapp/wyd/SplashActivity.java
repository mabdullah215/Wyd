package com.mobileapp.wyd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.mobileapp.wyd.auth.OnboardActivity;
import com.mobileapp.wyd.auth.OtpActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashActivity.this, OnboardActivity.class);
                startActivity(i);
                finish();
            }
        };

        new Timer().schedule(task, 3000);

    }
}