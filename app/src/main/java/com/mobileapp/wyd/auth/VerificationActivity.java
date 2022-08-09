package com.mobileapp.wyd.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileapp.wyd.ProfileActivity;
import com.mobileapp.wyd.R;

import java.text.DecimalFormat;

public class VerificationActivity extends AppCompatActivity
{
    TextView tvTimer;
    CountDownTimer cTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        tvTimer=findViewById(R.id.tv_timer);
        startTimer();
        ImageView imgProceed=findViewById(R.id.img_proceed);
        imgProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ProfileActivity.class));
            }
        });
    }

    void startTimer() {
        DecimalFormat df = new DecimalFormat("00");
        cTimer = new CountDownTimer(40000, 1000) {
            public void onTick(long millisUntilFinished)
            {
                String seconds= df.format(millisUntilFinished/1000);
                tvTimer.setText("Resend SMS: 00:" + seconds);
            }
            public void onFinish()
            {
                tvTimer.setText("Resend SMS NOW");
            }
        };
        cTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTimer();
    }

    void cancelTimer()
    {
        if(cTimer!=null)
            cTimer.cancel();
    }
}