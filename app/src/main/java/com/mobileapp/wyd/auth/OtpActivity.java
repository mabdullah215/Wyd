package com.mobileapp.wyd.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobileapp.wyd.R;

public class OtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        EditText etNumber=findViewById(R.id.et_num);
        ImageView tvProceed=findViewById(R.id.img_proceed);
        ImageView imgBack=findViewById(R.id.img_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String num=etNumber.getText().toString().trim();

                if(num.isEmpty())
                {
                    Toast.makeText(OtpActivity.this, "Please Enter the number", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(getBaseContext(),VerificationActivity.class).putExtra("number",num));

            }
        });

    }
}