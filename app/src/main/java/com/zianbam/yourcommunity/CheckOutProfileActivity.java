package com.zianbam.yourcommunity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Model.User;

import java.util.ArrayList;

public class CheckOutProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference reference, ref, ref2, ref3, ref4;
    private String profileid, pusername;
    private FirebaseUser firebaseUser;
    private ImageView image_profile1, image_profile2;
    String myP;
    String ids;
    ProgressBar progress_circular;
    private TextView percent, matchOut_toolbar;
    private String intro, question1, question2, question3, question4, question5, question6, question7, question8, question9, question10;
    private TextView q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10;

    RecyclerView recycle_view;

    ArrayList<String> questions = new ArrayList<String>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        progress_circular = findViewById(R.id.progress_circular);
        recycle_view = findViewById(R.id.recycle_view);
        image_profile1 = findViewById(R.id.image_profile1);
        image_profile2 = findViewById(R.id.image_profile2);
        percent = findViewById(R.id.percent);



        questions.add("question1");
        questions.add("question2");
        questions.add("question3");
        questions.add("question4");
        questions.add("question5");
        questions.add("question6");
        questions.add("question7");
        questions.add("question8");
        questions.add("question9");
        questions.add("question10");

        a1 = findViewById(R.id.a1);
        a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        a4 = findViewById(R.id.a4);
        a5 = findViewById(R.id.a5);
        a6 = findViewById(R.id.a6);
        a7 = findViewById(R.id.a7);
        a8 = findViewById(R.id.a8);
        a9 = findViewById(R.id.a9);
        a10 = findViewById(R.id.a10);

        q1 = findViewById(R.id.q1);
        q2 = findViewById(R.id.q2);
        q3 = findViewById(R.id.q3);
        q4 = findViewById(R.id.q4);
        q5 = findViewById(R.id.q5);
        q6 = findViewById(R.id.q6);
        q7 = findViewById(R.id.q7);
        q8 = findViewById(R.id.q8);
        q9 = findViewById(R.id.q9);
        q10 = findViewById(R.id.q10);
        matchOut_toolbar = findViewById(R.id.matchOut_toolbar);

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        Intent intent = getIntent();
         profileid = intent.getStringExtra("profileid");
         pusername = intent.getStringExtra("username");

        getProfile();



         for (final String id: questions){
             ref = FirebaseDatabase.getInstance().getReference("Prefs").child(firebaseUser.getUid()).child(id);
             ref.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()){
                         final String myP =  dataSnapshot.child("pref").getValue().toString();
                         final String pQ = dataSnapshot.child("questionNO").getValue().toString();
//
                         reference = FirebaseDatabase.getInstance().getReference("Prefs").child(profileid).child(id);
                         reference.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 String myP2 =  dataSnapshot.child("pref").getValue().toString();
                                 String pQ2 = dataSnapshot.child("questionNO").getValue().toString();
                                 Log.d("tag", "tag :- "+ myP2);
                                 if (myP.equals(myP2)){
                                 switch (pQ2){
                                     case "question1":
                                         q1.setText(myP);
                                         q1.setVisibility(View.VISIBLE);
                                         a1.setVisibility(View.VISIBLE);
                                         break;
                                     case "question2":
                                             q2.setText(myP);
                                         q2.setVisibility(View.VISIBLE);
                                         a2.setVisibility(View.VISIBLE);
                                         break;
                                     case "question3":
                                             q3.setText(myP);
                                         q3.setVisibility(View.VISIBLE);
                                         a3.setVisibility(View.VISIBLE);
                                         break;

                                     case "question4":
                                             q4.setText(myP);
                                         q4.setVisibility(View.VISIBLE);
                                         a4.setVisibility(View.VISIBLE);
                                         break;

                                     case "question5":
                                             q5.setText(myP);
                                         q5.setVisibility(View.VISIBLE);
                                         a5.setVisibility(View.VISIBLE);
                                         break;
                                     case "question6":
                                             q6.setText(myP);
                                         q6.setVisibility(View.VISIBLE);
                                         a6.setVisibility(View.VISIBLE);
                                         break;
                                     case "question7":
                                             q7.setText(myP);
                                         q7.setVisibility(View.VISIBLE);
                                         a7.setVisibility(View.VISIBLE);
                                         break;
                                     case "question8":
                                             q8.setText(myP);
                                         q8.setVisibility(View.VISIBLE);
                                         a8.setVisibility(View.VISIBLE);
                                         break;
                                     case "question9":
                                             q9.setText(myP);
                                         q9.setVisibility(View.VISIBLE);
                                         a9.setVisibility(View.VISIBLE);
                                         break;
                                     case "question10":
                                             q10.setText(myP);
                                         q10.setVisibility(View.VISIBLE);
                                         a10.setVisibility(View.VISIBLE);
                                         break;
                                 }
                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {
                             }
                         });
                     }
                 }
                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {
                 }
             });
             
         }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                if (!q1.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q2.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q3.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q4.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q5.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q6.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q7.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q8.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q9.getText().toString().equals("Question 1")){
                    count++;
                }
                if (!q10.getText().toString().equals("Question 1")){
                    count++;
                }
                percent.setText(count +"0% match");
                progress_circular.setVisibility(View.GONE);
                percent.setVisibility(View.VISIBLE);

            }
        }, 1800);


    }

    private void getProfile() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.get().load(user.getImageURL()).into(image_profile2);
                    matchOut_toolbar.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.get().load(user.getImageURL()).into(image_profile1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
