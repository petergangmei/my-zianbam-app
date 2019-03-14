package com.zianbam.yourcommunity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReferedmeActivity extends AppCompatActivity {
    private Button yesBtn, nobtn, continuebtn;
    private TextView textview1, lostcode;
    private EditText referalCode;
    private LinearLayout buttonLayOut, continuebtnlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referedme);
        referalCode = findViewById(R.id.referralCode);
        yesBtn = findViewById(R.id.yesBtn);
        nobtn = findViewById(R.id.noBtn);
        textview1 = findViewById(R.id.textview1);
        buttonLayOut = findViewById(R.id.buttonLayOut);
        continuebtnlayout = findViewById(R.id.continueBtnLayout);
        continuebtn = findViewById(R.id.continueBtn);
        lostcode = findViewById(R.id.lostcode);


        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesClick();
            }
        });

    }

    private void yesClick() {
        textview1.setText("Enter your referral code here");
        buttonLayOut.setVisibility(View.GONE);
        referalCode.setVisibility(View.VISIBLE);
        continuebtnlayout.setVisibility(View.VISIBLE);

    }
}
