package com.zianbam.yourcommunity.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zianbam.yourcommunity.Adapter.PostAdapter;
import com.zianbam.yourcommunity.Adapter.StoryAdapter;
import com.zianbam.yourcommunity.AddStoryActivity;
import com.zianbam.yourcommunity.CreatePostActivity;
import com.zianbam.yourcommunity.MessageContainerActivity;
import com.zianbam.yourcommunity.Model.Cbox;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.Story;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    DatabaseReference reference;
    ProgressBar progressBar ,prograssing;
    private LinearLayout create_post;
    private TextView loadingtext;
    private ImageView inbox;
    private int inb = 1;
    Spinner spinner;
    ImageView storyAdd;
    LinearLayout notpostLayout,toolfeedlayout;

    //story
//    private RecyclerView recyclerView_story;
//    private StoryAdapter storyAdapter;
//    private List<Story> storyList;


    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private ArrayList<String> followingList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);


        create_post = v.findViewById(R.id.create_post);
        loadingtext = v.findViewById(R.id.loadingtext);
        ImageView sms = v.findViewById(R.id.inbox);
        inbox = v.findViewById(R.id.inbox);
        recyclerView = v.findViewById(R.id.recycle_view_post);
        progressBar = v.findViewById(R.id.progress_circular);
        prograssing = v.findViewById(R.id.prograssing);
        storyAdd = v.findViewById(R.id.storyAdd);
        spinner = v.findViewById(R.id.spinner1);
        notpostLayout = v.findViewById(R.id.notpostLayout);
        toolfeedlayout = v.findViewById(R.id.toolfeedlayout);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
         recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter= new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = v.findViewById(R.id.recycle_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);


        init();
        checkFollowing();
        postList.clear();
        readPostG();
        readStory();
//        addItemSpinner(spinner);
        checkMail(inbox);

        updateToaken(FirebaseInstanceId.getInstance().getToken());


        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MessageContainerActivity.class));
            }
        });
        storyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddStoryActivity.class);
                startActivity(intent);
            }
        });

        create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                intent.putExtra("post_type", "new");
                intent.putExtra("post_id", "null");
                startActivity(intent);
            }
        });


        return v;
    }

    private void init() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                  notpostLayout.setVisibility(View.VISIBLE);
//                  toolfeedlayout.setVisibility(View.VISIBLE);
//            }
//        },2500);
    }

    private void addItemSpinner(final Spinner spinner) {

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {

                        if (spinner.getSelectedItem().equals("Your feed")){
//                            readPost(spinner.getSelectedItem());

                        }else if (spinner.getSelectedItem().equals("Global feed")){
//                            readPostG();
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkMail(final ImageView image) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Cbox").child(firebaseUser.getUid());
        final String n;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   
                   for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                       reference = FirebaseDatabase.getInstance().getReference("Cbox").child(firebaseUser.getUid()).child(snapshot.getKey());
                       reference.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.exists()){
                                Cbox cbox = dataSnapshot.getValue(Cbox.class);

                                if (cbox.getIsseen().equals("true")){
                                    inb ++;
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
        if (inb > 1){
            image.setImageResource(R.drawable.ic_mail);
            inb = 1;
        }else {
            image.setImageResource(R.drawable.ic_mail_white);
        }
    }


    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPostG() {
        postList.clear();
        notpostLayout.setVisibility(View.GONE);
        toolfeedlayout.setVisibility(View.GONE);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.keepSynced(true);
        Query query = reference.limitToLast(35);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    postList.clear();
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Post p =  snapshot.getValue(Post.class);
                                postList.add(p);
                        }

                    progressBar.setVisibility(View.GONE);
                    loadingtext.setVisibility(View.GONE);
                    postAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(), "No post available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ops! Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  readPost(){
        postList.clear();
        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.keepSynced(true);
        Query query = reference.limitToLast(25);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    postList.clear();
                    for (String id: followingList){
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Post p =  snapshot.getValue(Post.class);
                            if (id.equals(p.getPublisher())){
                                postList.add(p);
                                notpostLayout.setVisibility(View.GONE);
                                toolfeedlayout.setVisibility(View.GONE);
                            }else {
                            }
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    loadingtext.setVisibility(View.GONE);
                    postAdapter.notifyDataSetChanged();
                }else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ops! Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void  readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id: followingList){
                    int countStory = 0 ;
                    Story story  = null;
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot: dataSnapshot.child(id).getChildren()){
                            story = snapshot.getValue(Story.class);
                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                                countStory++;
                            }
                        }
                        if (countStory > 0){
                            storyList.add(story);
                        }
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToaken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }

}
