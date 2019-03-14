package com.zianbam.yourcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Model.Cbox;
import com.zianbam.yourcommunity.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    private Button btnNo, btnYes;
    private String userid;
    ImageView conversation;
    TextView username;
    private List<String> chatidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        chatidList = new ArrayList<>();

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        username = findViewById(R.id.username);
        conversation = findViewById(R.id.conversation);
        btnNo = findViewById(R.id.btnNo);
        btnYes = findViewById(R.id.btnYes);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.get().load(user.getImageURL()).into(conversation);
                    username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final  String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//                FirebaseDatabase.getInstance().getReference("Cbox").child(myId).child(userid).removeValue();
//                FirebaseDatabase.getInstance().getReference("Cbox").child(userid).child(myId).removeValue();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myId).child("Cbox").child(userid);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                          final  Cbox cbox = dataSnapshot.getValue(Cbox.class);
                                DatabaseReference ref  =  FirebaseDatabase.getInstance().getReference("Users").child(myId).child("Cbox").child(userid);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("deleted", "true");
                                ref.updateChildren(hashMap);
                                FirebaseDatabase.getInstance().getReference("Users").child(myId).child("Chats").child(userid).removeValue();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Chats").child(userid);


                finish();

            }
        });
    }
}
