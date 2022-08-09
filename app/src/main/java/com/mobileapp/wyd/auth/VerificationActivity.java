package com.mobileapp.wyd.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mobileapp.wyd.BaseActivity;
import com.mobileapp.wyd.ProfileActivity;
import com.mobileapp.wyd.R;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends BaseActivity
{
    private String verificationid;
    private FirebaseAuth mAuth;
    String code="";
    TextView tvTimer;
    CountDownTimer cTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mAuth = FirebaseAuth.getInstance();
        String number=getIntent().getStringExtra("number");
        PinView etPinView=findViewById(R.id.et_pin);
        tvTimer=findViewById(R.id.tv_timer);
        ImageView imgBack=findViewById(R.id.img_back);
        startTimer();
        ImageView imgProceed=findViewById(R.id.img_proceed);
        imgProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=etPinView.getText().toString().trim();
                if(code!=null)
                {
                    verifyCode(code);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                sendVerificationCode(number);
            }
        });

    }

    private void verifyCode(String code)
    {
        showLoading();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void sendVerificationCode(String number)
    {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            profileSetup();
                        }
                        else
                        {
                            hideLoading();
                            Log.i("errorMessage",task.getException().getMessage());
                            Toast.makeText(VerificationActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void profileSetup()
    {
        String number=getIntent().getStringExtra("number");
        startActivity(new Intent(getBaseContext(), ProfileActivity.class).putExtra("number",number));
        finish();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
        {
            code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            Log.i("errorMessage",e.getMessage());
            Toast.makeText(VerificationActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

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