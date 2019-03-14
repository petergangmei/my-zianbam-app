package com.zianbam.yourcommunity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.InboxAdapter;
import com.zianbam.yourcommunity.Model.Cbox;

import java.util.ArrayList;
import java.util.List;

public class MessageContainerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Cbox> mUsers;
    private InboxAdapter inboxAdapter;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Conversation");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
//        linearLayoutManager1.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager1);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();
        getUsers();

    }

    private void getUsers() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Cbox");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        final Cbox cbox = snapshot.getValue(Cbox.class);
                        if (cbox.getDeleted().equals("false")){
                            userList.add(cbox.getReceiver());
                        }else if (cbox.getDeleted().equals(cbox.getReceiver())){
                            userList.add(cbox.getReceiver());
                        }

                    }
                    readChat();

                }else {
                    Toast.makeText(MessageContainerActivity.this, "No message...", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readChat() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(
                firebaseUser.getUid()).child("Cbox");
        Query query = reference.orderByChild("time").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUsers.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                        User user = snapshot.getValue(User.class);
                        Cbox cbox = snapshot.getValue(Cbox.class);
                        mUsers.add(cbox);
                    }
                }else {
                    Toast.makeText(MessageContainerActivity.this, "data does not exist!", Toast.LENGTH_SHORT).show();
                }
            inboxAdapter = new InboxAdapter(getApplication(), mUsers);
            recyclerView.setAdapter(inboxAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//
//    private void readChat() {
//        mUsers = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
//
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    User user = snapshot.getValue(User.class);
//
//                    //display 1 user from chats
//                    for (String id:userList){
////                        Toast.makeText(MessageContainerActivity.this, ""+userList.size(), Toast.LENGTH_SHORT).show();
//                        if (user.getId().equals(id)){
//
//                            if (mUsers.size() != 0){
//                                Toast.makeText(MessageContainerActivity.this, "not eual 0", Toast.LENGTH_SHORT).show();
//                                for (User user1:mUsers){
////
//                                    if (!user.getId().equals(user1.getId())){
//                                        mUsers.add(user);
//                                    }
//
//                                }
//                            }else {
//                                Toast.makeText(MessageContainerActivity.this, "equal to 0", Toast.LENGTH_SHORT).show();
//                                mUsers.add(user);
//                            }
//
//                        }
//                    }
//                }
//                inboxAdapter = new InboxAdapter(getApplicationContext(), mUsers);
//                recyclerView.setAdapter(inboxAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
