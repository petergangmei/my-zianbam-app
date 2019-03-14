package com.zianbam.yourcommunity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SetupPrefereceActivity extends AppCompatActivity {

    DatabaseReference reference, ref;
    Button startBtn, ans1, ans2, ans3, ans4, ans5, ans6;
    ImageView image;
    TextView text;
    FirebaseUser firebaseUser;
    ProgressBar progress_circular;
    private String intro, question1, question2, question3, question4, question5, question6, question7, question8, question9, question10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_preferece);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        text = findViewById(R.id.text);
        image = findViewById(R.id.image);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);
        ans5 = findViewById(R.id.ans5);
        ans6 = findViewById(R.id.ans6);
        progress_circular = findViewById(R.id.pb);




        intro = "Pick up your preferece to easly find people who shared common interest";
        question1 = "On your first date, if you were to choose between coffee and tea what would you prefer?";
        question2 = "Would you rather stay in or go out for a date?";
        question3 = "Your favorite choice?";
        question4 = "If you were to travel from a point of place to another (approx: 5 km) do you prefer?";
        question5 = "Would you  rather date with a full confident or a little shy person?";
        question6 = "Do you like vacation on summer or winter?";
        question7 = "Which pet would you to keep?";
        question8 = "Are you looking for..";
        question9 = "Which music genre you like the most?";
        question10 = "It's your first movie night with your bf/gf, which genre you would choose to watch?";

        ans1.setVisibility(View.VISIBLE);
        ans1.setText("start");
        text.setText(intro);
        ans1.setTag("start");

    }

    public void prefClick(View view){
        String tag = view.getTag().toString();
        switch (tag){
            case "start":
                toQuestion1();
                break;
                //drinks
            case "Coffee":
                  addToDatabase("ans1");
                break;
            case "Tea":
                addToDatabase("ans2");
                break;

                //food
            case "Veg":
                addToDatabase("ans1");
                break;

            case "Non-veg":
                addToDatabase("ans2");
                break;

                //transport
            case "Bike":
                addToDatabase("ans1");
                break;

            case "Car":
                addToDatabase("ans2");
                break;

                //vacations
            case "Summer":
                addToDatabase("ans1");
                break;

            case "Winter":
                addToDatabase("ans2");
                break;
                //pets
            case "Cat":
                addToDatabase("ans1");
                break;
            case "Dog":
                addToDatabase("ans2");
                break;
            case "Fish":
                addToDatabase("ans3");
                break;
            case "No pets":
                addToDatabase("ans4");
                break;

                //date in or out
            case "Stay in":
                addToDatabase("ans1");
                break;
            case "Go out":
                addToDatabase("ans2");
                break;

                //date a confident/shy
            case "Confident":
                addToDatabase("ans1");
                break;
            case "Little shy":
                addToDatabase("ans2");
                break;

                //relation ship section
            case "Friends":
                addToDatabase("ans1");
                break;
            case "Relationship":
                addToDatabase("ans2");
                break;
            case "Serious relationship":
                addToDatabase("ans3");
                break;
            case "Not sure":
                addToDatabase("ans4");
                break;

                //music section

            case "Jazz":
                addToDatabase("ans1");
                break;
            case "Rock music":
                addToDatabase("ans2");
                break;
            case "Pop music":
                addToDatabase("ans3");
                break;

            case "Electronic":
                addToDatabase("ans4");
                break;
            case "Soft-rock":
                addToDatabase("ans5");
                break;
            case "Country music":
                addToDatabase("ans6");
                break;

                //movie section
            case "Comedy":
                addToDatabase("ans1");
                break;
            case "Action":
                addToDatabase("ans2");
                break;
            case "Horror":
                addToDatabase("ans3");
                break;
            case "Fantasy":
                addToDatabase("ans4");
                break;
            case "Adventure":
                addToDatabase("ans5");
                break;
            case "Romance":
                addToDatabase("ans6");
                break;
            case "Continue":
                updatedp();
                break;


        }
    }
    private void updatedp(){
        progress_circular.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pref", "updated");
        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progress_circular.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }
    private void toExit(){
        image.setImageResource(R.drawable.checklist);
        text.setText("Coungratulation! on succesfully setting up preferences.");
        text.setTextColor(Color.BLACK);
        text.setTextSize(18);
        text.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        ans1.setText("Continue");
        ans1.setTag("Continue");
        ans2.setVisibility(View.GONE);
        ans3.setVisibility(View.GONE);
        ans4.setVisibility(View.GONE);
        ans5.setVisibility(View.GONE);
        ans6.setVisibility(View.GONE);
    }
    private void toQuestion10(){
        image.setImageResource(R.drawable.popcorn);
        text.setText(question10);
        text.setTag("question10");
        ans1.setText("Comedy");
        ans1.setTag("Comedy");
        ans2.setText("Action");
        ans2.setTag("Action");
        ans3.setText("Horror");
        ans3.setTag("Horror");
        ans4.setText("Fantasy");
        ans4.setTag("Fantasy");
        ans5.setText("Adventure");
        ans5.setTag("Adventure");
        ans6.setText("Romance");
        ans6.setTag("Romance");
    }

    private void toQuestion9() {
        image.setImageResource(R.drawable.listening);
        text.setText(question9);
        text.setTag("question9");
        ans1.setTag("Jazz");
        ans1.setText("Jazz");
        ans2.setText("Rock music");
        ans2.setTag("Rock music");
        ans3.setText("Pop music");
        ans3.setTag("Pop music");
        ans3.setVisibility(View.VISIBLE);
        ans4.setText("Electronic");
        ans4.setTag("Electronic");
        ans4.setVisibility(View.VISIBLE);
        ans5.setText("Soft-rock");
        ans5.setTag("Soft-rock");
        ans5.setVisibility(View.VISIBLE);
        ans6.setText("Country music");
        ans6.setTag("Country music");
        ans6.setVisibility(View.VISIBLE);
    }

    private void toQuestion8() {
        image.setImageResource(R.drawable.friendship);
        text.setText(question8);
        text.setTag("question8");
        ans1.setTag("Friends");
        ans1.setText("Friends");
        ans2.setText("Relationship");
        ans2.setTag("Relationship");
        ans3.setText("Serious relationship");
        ans3.setTag("Serious relationship");
        ans4.setText("Not sure");
        ans4.setTag("Not sure");
    }

    private void toQuestion7() {
        image.setImageResource(R.drawable.fishbowl);
        text.setText(question7);
        text.setTag("question7");
        ans1.setTag("Cat");
        ans1.setText("Cat");
        ans2.setText("Dog");
        ans2.setTag("Dog");
        ans3.setText("Fish");
        ans3.setTag("Fish");
        ans3.setVisibility(View.VISIBLE);
        ans4.setText("No pets");
        ans4.setTag("No pets");
        ans4.setVisibility(View.VISIBLE);
    }

    private void toQuestion6() {
        image.setImageResource(R.drawable.sunumbrella);
        text.setText(question6);
        text.setTag("question6");
        ans1.setTag("Summer");
        ans1.setText("Summer");
        ans2.setText("Winter");
        ans2.setTag("Winter");

    }

    private void toQuestion5(){
        image.setImageResource(R.drawable.protection);
        text.setText(question5);
        text.setTag("question5");
        ans1.setTag("Confident");
        ans1.setText("Confident");
        ans2.setText("Little shy");
        ans2.setTag("Little shy");
    }

    private void toQuestion4() {
        image.setImageResource(R.drawable.car);
        text.setText(question4);
        text.setTag("question4");
        ans1.setTag("Bike");
        ans1.setText("Bike");
        ans2.setText("Car");
        ans2.setTag("Car");
    }

    private void toQuestion3() {
        image.setImageResource(R.drawable.food);
        text.setText(question3);
        text.setTag("question3");
        ans1.setTag("Veg");
        ans1.setText("Veg");
        ans2.setText("Non-veg");
        ans2.setTag("Non-veg");
    }

    private void toQuestion2() {
        image.setImageResource(R.drawable.map);
        text.setText(question2);
        text.setTag("question2");
        ans1.setTag("Stay in");
        ans1.setText("Stay in");
        ans2.setText("Go out");
        ans2.setTag("Go out");
    }


    private void toQuestion1() {
        image.setImageResource(R.drawable.coffee);
        text.setText(question1);
        text.setTag("question1");
        ans1.setTag("Coffee");
        ans1.setText("Coffee");
        ans2.setText("Tea");
        ans2.setTag("Tea");
        ans2.setVisibility(View.VISIBLE);

    }

    private void addToDatabase(final String ans) {
        ans1.setEnabled(false);
        ans2.setEnabled(false);
        ans3.setEnabled(false);
        ans4.setEnabled(false);
        ans5.setEnabled(false);
        ans6.setEnabled(false);
        progress_circular.setVisibility(View.VISIBLE);

            DatabaseReference r = FirebaseDatabase.getInstance().getReference("PrefsNO").child(firebaseUser.getUid()).child(text.getTag().toString());
            r.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    switch (ans){
                        case "ans1":
                            String a1= ans1.getText().toString();
                            updatePrefValue(a1);
                            break;
                        case "ans2":
                            String a2 = ans2.getText().toString();
                            updatePrefValue(a2);
                            break;
                        case "ans3":
                            String a3 = ans3.getText().toString();
                            updatePrefValue(a3);
                            break;
                        case "ans4":
                            String a4= ans4.getText().toString();
                            updatePrefValue(a4);
                            break;
                        case "ans5":
                            String a5 = ans5.getText().toString();
                            updatePrefValue(a5);
                            break;
                        case "ans6":
                            String a6 = ans6.getText().toString();
                            updatePrefValue(a6);
                            break;
                    }

                }else {
                    switch (ans){
                        case "ans1":
                            String a1= ans1.getText().toString();
                            setPrefValue(a1);
                            break;
                        case "ans2":
                            String a2 = ans2.getText().toString();
                            setPrefValue(a2);
                            break;
                        case "ans3":
                            String a3 = ans3.getText().toString();
                            setPrefValue(a3);
                            break;
                        case "ans4":
                            String a4= ans4.getText().toString();
                            setPrefValue(a4);
                            break;
                        case "ans5":
                            String a5 = ans5.getText().toString();
                            setPrefValue(a5);
                            break;
                        case "ans6":
                            String a6 = ans6.getText().toString();
                            setPrefValue(a6);
                            break;
                    }
                }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


    }



    private void setPrefValue(String ans) {
        reference = FirebaseDatabase.getInstance().getReference("Prefs").child(firebaseUser.getUid());
        String id = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("pref", ans);
        hashMap.put("question", text.getText().toString());
        hashMap.put("questionNO", text.getTag().toString());
        reference.child(text.getTag().toString()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ans1.setEnabled(true);
                ans2.setEnabled(true);
                ans3.setEnabled(true);
                ans4.setEnabled(true);
                ans5.setEnabled(true);
                ans6.setEnabled(true);
                progress_circular.setVisibility(View.GONE);
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("PrefsNO").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap1  = new HashMap<>();
        hashMap1.put("question", text.getText().toString());
        hashMap1.put("tid", id);
        hashMap1.put("myid", firebaseUser.getUid());
        ref.child(text.getTag().toString()).setValue(hashMap1);

        //send to next question
        switch (text.getTag().toString()){
            case "question1":
                toQuestion2();
                break;
            case "question2":
                toQuestion3();
                break;
            case "question3":
                toQuestion4();
                break;
            case "question4":
                toQuestion5();
                break;
            case "question5":
                toQuestion6();
                break;
            case "question6":
                toQuestion7();
                break;
            case "question7":
                toQuestion8();
                break;
            case "question8":
                toQuestion9();
                break;
            case "question9":
                toQuestion10();
                break;
            case "question10":
                toExit();
                break;
        }

    }

    private void updatePrefValue(final String ans) {

        reference = FirebaseDatabase.getInstance().getReference("PrefsNO").child(firebaseUser.getUid()).child(text.getTag().toString());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String id = dataSnapshot.child("tid").getValue().toString();

                    setPrefValue2(id, ans);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setPrefValue2(String id, String ans) {
        reference = FirebaseDatabase.getInstance().getReference("Prefs").child(firebaseUser.getUid()).child(text.getTag().toString());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("pref", ans);
        hashMap.put("question", text.getText().toString());
        hashMap.put("questionNO", text.getTag().toString());
        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ans1.setEnabled(true);
                ans2.setEnabled(true);
                ans3.setEnabled(true);
                ans4.setEnabled(true);
                ans5.setEnabled(true);
                ans6.setEnabled(true);
                progress_circular.setVisibility(View.GONE);
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("PrefsNO").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap1  = new HashMap<>();
        hashMap1.put("question", text.getText().toString());
        hashMap1.put("tid", id);
        hashMap1.put("myid", firebaseUser.getUid());
        ref.child(text.getTag().toString()).setValue(hashMap1);


        //send to next question
        switch (text.getTag().toString()){
            case "question1":
                toQuestion2();
                break;
            case "question2":
                toQuestion3();
                break;
            case "question3":
                toQuestion4();
                break;
            case "question4":
                toQuestion5();
                break;
            case "question5":
                toQuestion6();
                break;
            case "question6":
                toQuestion7();
                break;
            case "question7":
                toQuestion8();
                break;
            case "question8":
                toQuestion9();
                break;
            case "question9":
                toQuestion10();
                break;
            case "question10":
                toExit();
                break;
        }

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "base-root");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
