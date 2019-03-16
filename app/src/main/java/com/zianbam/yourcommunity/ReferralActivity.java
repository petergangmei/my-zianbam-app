package com.zianbam.yourcommunity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Model.ReferralCode;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReferralActivity extends AppCompatActivity {
    private Button generateCodeBtn;
    private TextView yourReferralCode, errorTextArea, endtime, codeStatus,msgfromBackend , joincount;
    private EditText createOwnCode;
    private LinearLayout yourReferralCodeLayout,createYourCodeLayout;
    private ProgressBar progressBar;
    private ImageView responsimage, closeBtn;
    private DatabaseReference ref, reference, dbref;
    private FirebaseUser firebaseUser;
    private ClipboardManager clipboardManager;
    private Timestamp enddate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        init();
        checkActiveReferralCode();
        checkCodeExistance();
        showjoincount();

        generateCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCodeClick();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        joincount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReferd();
            }
        });
        

        
        //onCreate ends here
    }

    private void gotoReferd() {
        startActivity(new Intent(getApplicationContext(), ReferraldetailActivity.class));
        finish();
    }

    private void showjoincount() {
        ref = FirebaseDatabase.getInstance().getReference("Referralprogram").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    long count = dataSnapshot.getChildrenCount();
                if (count == 1){
                    joincount.setText(count+ " user have joint through your referral code!");
                }else if (count > 1){
                    joincount.setText(count+ " users have joint through your referral code!");

                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View view = findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return super.onTouchEvent(event);
    }



    private void checkActiveReferralCode() {
        ref = FirebaseDatabase.getInstance().getReference("Referralprogram").child("value");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String  msg = dataSnapshot.child("msg").getValue().toString();
                    msgfromBackend.setVisibility(View.VISIBLE);
                    msgfromBackend.setText(msg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("ReferralcodesActive");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                generateCodeBtn.setVisibility(View.VISIBLE);
                createYourCodeLayout.setVisibility(View.VISIBLE);
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    final String key = snapshot.getKey();
                    ref = FirebaseDatabase.getInstance().getReference("ReferralcodesActive").child(key);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String userid = dataSnapshot.child("userid").getValue().toString();
                                if (userid.equals(firebaseUser.getUid())){
                                    ReferralCode f = dataSnapshot.getValue(ReferralCode.class);

                                    if (f.getEndon() >= System.currentTimeMillis()){

                                        createYourCodeLayout.setVisibility(View.GONE);
                                        yourReferralCodeLayout.setVisibility(View.VISIBLE);
                                        yourReferralCode.setText(key);
                                        generateCodeBtn.setEnabled(true);
                                        generateCodeBtn.setText("Copy Code");
                                        generateCodeBtn.setTag("copycode");
                                        Timestamp ts=new Timestamp(f.getEndon());
                                        Date date=ts;
                                        endtime.setText("This code will expired on: "+date);

                                    }else {
                                        createYourCodeLayout.setVisibility(View.GONE);
                                        yourReferralCodeLayout.setVisibility(View.VISIBLE);
                                        yourReferralCode.setText(key);
                                        generateCodeBtn.setEnabled(true);
                                        generateCodeBtn.setText("Regenerate code");
                                        generateCodeBtn.setTag("resetcode");
                                        codeStatus.setText("This code has expired: ");

                                         enddate=new Timestamp(f.getEndon());
                                        Date date=enddate;
                                        endtime.setText("This code expired on: "+date);

                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void generateCodeClick() {
        if (generateCodeBtn.getTag().toString().equals("resetcode")){
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("ReferralcodesActive").child(yourReferralCode.getText().toString())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    generateCodeBtn.setText("Generate");
                    generateCodeBtn.setTag("generatecode");
                    createYourCodeLayout.setVisibility(View.VISIBLE);
                    yourReferralCodeLayout.setVisibility(View.GONE);
                    endtime.setVisibility(View.GONE);
                }
            });

            
        } else if(generateCodeBtn.getTag().toString().equals("copycode")){
            ref = FirebaseDatabase.getInstance().getReference("ReferralcodesActive").child(yourReferralCode.getText().toString());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ReferralCode referralCode = dataSnapshot.getValue(ReferralCode.class);
                        Timestamp ts=new Timestamp(referralCode.getEndon());

                        Date date=ts;

                        ClipData clipData = ClipData.newPlainText("Source Text", "Hey! Download Zianbam and " +
                                "Use my referral code: "+yourReferralCode.getText().toString() +" for amazing rewards. Hurry up! code expires on: "+ date +" https://play.google.com/store/apps/details?id=com.zianbam.yourcommunity");
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(ReferralActivity.this);
                        dialog.setContentView(R.layout.dialog_messages);
                        TextView title = dialog.findViewById(R.id.title);
                        TextView body = dialog.findViewById(R.id.body);


                        title.setText("Copied");
                        body.setText("Hey! Download Zianbam and " +
                                "Use my referral code: "+yourReferralCode.getText().toString() +" for amazing rewards. Hurry up! code expires on: "+ date +" https://play.google.com/store/apps/details?id=com.zianbam.yourcommunity" );
                        dialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else {
            progressBar.setVisibility(View.VISIBLE);
            generateCodeBtn.setEnabled(false);
            createYourCodeLayout.setVisibility(View.GONE);
            generateCodeBtn.setVisibility(View.GONE);


            ref = FirebaseDatabase.getInstance().getReference("Referralprogram").child("value");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Calendar calForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                        final String currentTime =currentTimeFormat.format(calForTime.getTime());
                        Calendar calForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                        String currentDate = currentDateFormat.format(calForDate.getTime());

                        String fix = "Z-";
                        String referralC = createOwnCode.getText().toString();
                        String finalreferralCode = fix + referralC;
                        Long currentime = System.currentTimeMillis();
                        Long endTime = currentime + 864000000;


                        String join = dataSnapshot.child("join").getValue().toString();
                        String refer = dataSnapshot.child("refer").getValue().toString();


                        //save in ReferralCodes
                        ref = FirebaseDatabase.getInstance().getReference("Referralcodes");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("endon", endTime);
                        hashMap.put("time", currentTime);
                        hashMap.put("date", currentDate);
                        hashMap.put("creeatedat", currentime);
                        hashMap.put("join", join);
                        hashMap.put("refer", refer);
                        hashMap.put("userid", firebaseUser.getUid());
                        ref.child(finalreferralCode).setValue(hashMap);

                        //save in RefferralActive
                        reference = FirebaseDatabase.getInstance().getReference("ReferralcodesActive");
                        HashMap<String, Object> hashMapActive = new HashMap<>();
                        hashMapActive.put("createdat", currentime);
                        hashMapActive.put("endon", endTime);
                        hashMapActive.put("time", currentTime);
                        hashMapActive.put("date", currentDate);
                        hashMapActive.put("join", join);
                        hashMapActive.put("refer", refer);
                        hashMapActive.put("userid", firebaseUser.getUid());
                        reference.child(finalreferralCode)
                                .setValue(hashMapActive).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                yourReferralCodeLayout.setVisibility(View.VISIBLE);
                                yourReferralCode.setText("Z-"+createOwnCode.getText().toString());
                                progressBar.setVisibility(View.GONE);

                                generateCodeBtn.setVisibility(View.VISIBLE);
                                generateCodeBtn.setEnabled(true);
                                generateCodeBtn.setTag("copycode");
                                generateCodeBtn.setText("Copy Code");
                            }
                        });

                    }else {
                        Toast.makeText(ReferralActivity.this, "not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
      
    }


    private void checkCodeExistance() {
        createOwnCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (createOwnCode.length() >= 5){
                    searchReferralCodeFromDatabase();
                }else {
                    errorTextArea.setVisibility(View.INVISIBLE);
                    responsimage.setVisibility(View.INVISIBLE);
                    generateCodeBtn.setEnabled(false);
                }

            }

            private void searchReferralCodeFromDatabase() {
                ref = FirebaseDatabase.getInstance().getReference("Referralcodes").child("Z-"+createOwnCode.getText().toString());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            errorTextArea.setVisibility(View.VISIBLE);
                            errorTextArea.setText("This code exist, try another!");
                            errorTextArea.setTextColor(Color.RED);
                            responsimage.setVisibility(View.INVISIBLE);
                            generateCodeBtn.setEnabled(false);

                        }else {
                            responsimage.setVisibility(View.VISIBLE);
                            generateCodeBtn.setEnabled(true);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }



    private void init() {
        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        createYourCodeLayout = findViewById(R.id.createYourCodeLayout);
        responsimage = findViewById(R.id.responsimage);
        generateCodeBtn = findViewById(R.id.generateCodeBtn);
        yourReferralCode = findViewById(R.id.yourReferralCode);
        yourReferralCodeLayout = findViewById(R.id.yourReferralCodeLayout);
        progressBar = findViewById(R.id.progressBar);
        createOwnCode = findViewById(R.id.createdCodeText);
        errorTextArea = findViewById(R.id.errorTextArea);
        endtime = findViewById(R.id.endtime);
        closeBtn = findViewById(R.id.closeBtn);
        codeStatus = findViewById(R.id.codeStatus);
        msgfromBackend = findViewById(R.id.msgfromBackend);
        joincount = findViewById(R.id.joincount);
    }
}
