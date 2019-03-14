package com.zianbam.yourcommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Adapter.UserAdapter;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.Story;
import com.zianbam.yourcommunity.Model.User;

import java.util.ArrayList;
import java.util.List;

public class StoryViewsActivity extends AppCompatActivity {
    String id;
    String title;
    String storyid;
    ImageButton close_btn;
    List<String> idList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private ImageView story_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_views);

        Intent intent = getIntent();
        storyid = intent.getStringExtra("storyid");
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        story_image = findViewById(R.id.story_image);
        close_btn = findViewById(R.id.close_btn);


        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userList.clear();
        userAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });


        idList = new ArrayList<>();

        switch (title) {
            case "likes":
                getPostImage();
                getLikes();
                break;
            case "following":
                break;
            case "followers":
                break;
            case "views":
                getselectedImage();
                getViews();
                break;
        }

    }

    private void getPostImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Post post = dataSnapshot.getValue(Post.class);
                    Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.image_placeholder).into(story_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getselectedImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(id).child(storyid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Story story = dataSnapshot.getValue(Story.class);
                    Picasso.get().load(story.getImageurl()).placeholder(R.drawable.image_placeholder).into(story_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getViews() {

        Log.d("tag", id+" "+storyid+" " + title);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(id).child(getIntent().getStringExtra("storyid")).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        idList.add(snapshot.getKey());
                    }
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id:idList){
                        if (user.getId().equals(id)){
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
