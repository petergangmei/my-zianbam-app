package com.zianbam.yourcommunity.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.FeaturephotoAdapter;
import com.zianbam.yourcommunity.Adapter.StoryAdapter;
import com.zianbam.yourcommunity.Adapter.UserAdapter;
import com.zianbam.yourcommunity.Model.FeaturePhoto;
import com.zianbam.yourcommunity.Model.Story;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView_search_result;
    private RecyclerView.LayoutManager layoutManager;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    DatabaseReference databaseReference;
    EditText search_bar;
    ImageView trendingfire;
    ImageButton search, back;

    private RecyclerView recyclerView_recent_join;
    private FeaturephotoAdapter recentuserAdapter;
    private List<FeaturePhoto>  userList;

    //story
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private ArrayList<String> followingList;

    RelativeLayout layout;
    LinearLayout notrendinglayout;

    TextView joinText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_search, container, false);

        search_bar = v.findViewById(R.id.search_bar)  ;
        search = v.findViewById(R.id.search);
        back = v.findViewById(R.id.back);
        joinText = v.findViewById(R.id.joinText);
        notrendinglayout = v.findViewById(R.id.notrendinglayout);
        trendingfire = v.findViewById(R.id.trendingfire);



        recyclerView_story = v.findViewById(R.id.recycle_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager3);
        storyList = new ArrayList<>();
        storyList.clear();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);



        recyclerView_recent_join = v.findViewById(R.id.recyclerView_recent_join);
//        recyclerView_recent_join.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
//        linearLayoutManager.setReverseLayout(true);
        recyclerView_recent_join.setLayoutManager(linearLayoutManager);
        userList = new ArrayList<>();
        recentuserAdapter = new FeaturephotoAdapter(getContext(), userList);
        recyclerView_recent_join.setAdapter(recentuserAdapter);



        recyclerView_search_result = v.findViewById(R.id.recycle_view_search_result);
        recyclerView_search_result.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        recyclerView_search_result.setLayoutManager(linearLayoutManager1);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView_search_result.setAdapter(userAdapter);

        checkFollowing();
//        readStory();
        Recentusers();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notrendinglayout.setVisibility(View.VISIBLE);
                trendingfire.setVisibility(View.VISIBLE);

                search_bar.setText("");
                search_bar.clearFocus();
                back.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                recyclerView_search_result.setVisibility(View.GONE);
                recyclerView_recent_join.setVisibility(View.VISIBLE);
                joinText.setVisibility(View.VISIBLE);
                recyclerView_story.setVisibility(View.VISIBLE);

            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notrendinglayout.setVisibility(View.GONE);
                trendingfire.setVisibility(View.GONE);

            }
            @Override
            public void afterTextChanged(Editable editable) {
                searhUsers(editable.toString().toLowerCase());
            }
        });
        return v;
    }

    private void Recentusers() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(25);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        FeaturePhoto p =  snapshot.getValue(FeaturePhoto.class);
                        if (!p.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
                            if (p.getType().equals("feature_photo")){
                                userList.add(p);
                            }
                        }
                    }
                    recentuserAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(), "No post available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searhUsers(final String s) {
        if (!s.equals("")){
            recyclerView_story.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            joinText.setVisibility(View.GONE);
            recyclerView_recent_join.setVisibility(View.GONE);

            recyclerView_search_result.setVisibility(View.VISIBLE);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
//            databaseReference.keepSynced(true);
            Query query = databaseReference.orderByChild("username")
                    .startAt(s)
                    .endAt(s + "\uf8ff").limitToLast(15);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    if (dataSnapshot.hasChildren()){
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            User p = dataSnapshot1.getValue(User.class);
                            mUsers.add(p);
                        }
                    }else {
                        Toast.makeText(getContext(), "No matching result", Toast.LENGTH_SHORT).show();
                    }
                    userAdapter.notifyDataSetChanged();


                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "error!", Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            notrendinglayout.setVisibility(View.VISIBLE);
            trendingfire.setVisibility(View.VISIBLE);

            recyclerView_story.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            joinText.setVisibility(View.VISIBLE);
            recyclerView_recent_join.setVisibility(View.VISIBLE);

            recyclerView_search_result.setVisibility(View.GONE);

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

}
