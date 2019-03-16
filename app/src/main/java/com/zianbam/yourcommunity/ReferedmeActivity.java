package com.zianbam.yourcommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Model.ReferralCode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ReferedmeActivity extends AppCompatActivity {
    private Button yesBtn, nobtn, continuebtn;
    private TextView textview1, lostcode;
    private EditText referalCode;
    private LinearLayout buttonLayOut, continuebtnlayout;
    private DatabaseReference ref, reference, db;
    private ProgressBar progressBar;
    private String referredby, from;
    String myintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referedme);

        Intent intent = getIntent();
         myintent = intent.getStringExtra("from");

        progressBar = findViewById(R.id.progressBar);
        referalCode = findViewById(R.id.referralCode);
        yesBtn = findViewById(R.id.yesBtn);
        nobtn = findViewById(R.id.noBtn);
        textview1 = findViewById(R.id.textview1);
        buttonLayOut = findViewById(R.id.buttonLayOut);
        continuebtnlayout = findViewById(R.id.continueBtnLayout);
        continuebtn = findViewById(R.id.ContinueBtn);
        lostcode = findViewById(R.id.lostcode);
        
        checkReferralCode();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesClick();
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noBtnClick();
            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continnueBtnClick();
            }
        });

        lostcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextActivty();
            }
        });




    }

    private void gotoNextActivty() {
        Intent intent = new Intent(getApplicationContext(), SetupPrefereceActivity.class);
        intent.putExtra("referred", "no");
        intent.putExtra("code", "null");
        intent.putExtra("from", myintent);
        startActivity(intent);
    }

    private void continnueBtnClick() {
        continuebtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);


        ref = FirebaseDatabase.getInstance().getReference("ReferralcodesActive").child(referalCode.getText().toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ReferralCode r = dataSnapshot.getValue(ReferralCode.class);
                    if (r.getEndon()<System.currentTimeMillis()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ReferedmeActivity.this, "Code expired ", Toast.LENGTH_LONG).show();
                        referredby = r.getUserid();

                    }else {
                        progressBar.setVisibility(View.GONE);
                        continuebtn.setEnabled(true);
                        Calendar calForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                        final String currentTime =currentTimeFormat.format(calForTime.getTime());
                        Calendar calForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                        String currentDate = currentDateFormat.format(calForDate.getTime());




                        reference = FirebaseDatabase.getInstance().getReference("Referralprogram").child(r.getUserid())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("timestamp", System.currentTimeMillis());
                        hashMap.put("time", currentTime);
                        hashMap.put("date", currentDate);
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (continuebtn.getTag().toString().equals("yes")){
                                    Intent intent = new Intent(getApplicationContext(), SetupPrefereceActivity.class);
                                    intent.putExtra("referred", "yes");
                                    intent.putExtra("code", referalCode.getText().toString() );
                                    intent.putExtra("from", myintent);
                                    startActivity(intent);

                                }else {
                                    Intent intent = new Intent(getApplicationContext(), SetupPrefereceActivity.class);
                                    intent.putExtra("referred", "yes");
                                    intent.putExtra("code", "null");
                                    intent.putExtra("from", myintent);
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void noBtnClick() {
        continuebtn.setTag("no");
        Intent intent = new Intent(getApplicationContext(), SetupPrefereceActivity.class);
        intent.putExtra("referred", "no");
        intent.putExtra("code", "null");
        intent.putExtra("from", myintent);
        startActivity(intent);


    }



    private void checkReferralCode() {
        referalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (referalCode.length()>=7){
                    progressBar.setVisibility(View.VISIBLE);
                    seaerchCodeExistance(s);
                }else if (referalCode.length()==0){
                    referalCode.setText("Z-");
                }else {
                    continuebtn.setEnabled(false);
                    progressBar.setVisibility(View.GONE);
                }

            }

            private void seaerchCodeExistance(Editable s) {
                ref = FirebaseDatabase.getInstance().getReference("ReferralcodesActive").child(s.toString());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            ReferralCode r = dataSnapshot.getValue(ReferralCode.class);
                            if (r.getEndon()<System.currentTimeMillis()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ReferedmeActivity.this, "Expired code!", Toast.LENGTH_SHORT).show();
                                textview1.setText("Expired code!");
                                referredby = r.getUserid();

                            }else {
                                progressBar.setVisibility(View.GONE);
                                continuebtn.setEnabled(true);


                                Toast.makeText(ReferedmeActivity.this, "Your code is valid!", Toast.LENGTH_SHORT).show();
                                textview1.setText("Your code is valid!");
                            }
                            
                        }else {
                            progressBar.setVisibility(View.GONE);
                            textview1.setText("Invalid code!");
                            Toast.makeText(ReferedmeActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                
            }
        });
    }

    private void yesClick() {
        textview1.setText("Enter your referral code here");
        buttonLayOut.setVisibility(View.GONE);
        referalCode.setVisibility(View.VISIBLE);
        continuebtnlayout.setVisibility(View.VISIBLE);
        continuebtn.setTag("yes");

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ReferedmeActivity.this);
        builder.setMessage("Do you really want to do this?");
        builder.setTitle("Exit Zianbam");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes, exit!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                moveTaskToBack(true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
