package com.zianbam.yourcommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Model.User;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText name, username, bio;
    ImageView image_profile,close,taken, available;
    TextView post;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        mAuth = FirebaseAuth.getInstance();
        close = findViewById(R.id.close);
        post = findViewById(R.id.post);
        name = findViewById(R.id.ename);
        username = findViewById(R.id.eusername);
        bio = findViewById(R.id.ebio);
        image_profile = findViewById(R.id.image_profile);
        taken = findViewById(R.id.taken);
        available = findViewById(R.id.available);

        username.setText(username.getText().toString().replaceAll(" ",""));

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", name.getText().toString());
                hashMap.put("bio", bio.getText().toString());
                hashMap.put("username", username.getText().toString());
                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdateProfileActivity.this, "User info updated.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User u = dataSnapshot.getValue(User.class);
                    String _name = u.getName();
                    String _username = u.getUsername();
                    String _bio = u.getBio();
                    String _imageURL = u.getImageURL();

                    name.setText(_name);
                    username.setText(_username);
                    bio.setText(_bio);
//                    Picasso.get().load(_imageURL).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        check user name availability
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searhUsers(charSequence.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                searhUsers(editable.toString().toLowerCase());
                String s = editable.toString();
            }
        });
    }

    private void searhUsers(String s) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = reference.orderByChild("id")
                .equalTo(s);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (username.getText().toString() == ""){
                    taken.setVisibility(View.GONE);
                }
                User u = dataSnapshot.getValue(User.class);
                if (dataSnapshot.hasChildren()){
                    String username2 = u.getUsername();

                    Toast.makeText(UpdateProfileActivity.this, ""+username2, Toast.LENGTH_SHORT).show();
                    if(username.getText().toString() != username2){
                        taken.setVisibility(View.VISIBLE);
                        available.setVisibility(View.GONE);
                        post.setEnabled(false);
                        post.setText("");
                        Toast.makeText(UpdateProfileActivity.this, "username already taken!", Toast.LENGTH_SHORT).show();
                    }else {
//                        taken.setVisibility(View.GONE);
//                        available.setVisibility(View.VISIBLE);
//                        Toast.makeText(UpdateProfileActivity.this, "username available", Toast.LENGTH_SHORT).show();
                    }

                }else{


                    String uname = username.getText().toString();
                    if(TextUtils.isEmpty(uname)){
                        Toast.makeText(UpdateProfileActivity.this, "username required.", Toast.LENGTH_SHORT).show();
                        taken.setVisibility(View.VISIBLE);
                        available.setVisibility(View.GONE);
                        post.setEnabled(false);
                        post.setText("");
                    }else {
                        taken.setVisibility(View.GONE);
                        available.setVisibility(View.VISIBLE);
                        post.setEnabled(true);
                        post.setText("UPDATE");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
