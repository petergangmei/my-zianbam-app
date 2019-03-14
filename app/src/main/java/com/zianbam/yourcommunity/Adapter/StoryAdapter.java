package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.AddStoryActivity;
import com.zianbam.yourcommunity.Model.Story;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;
import com.zianbam.yourcommunity.StoryActivity;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
private DatabaseReference reference, ref;
private FirebaseUser firebaseUser;


    private Context mContext;
    private List<Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (i == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, viewGroup, false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, viewGroup, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Story story = mStory.get(i);

        userInfo(viewHolder, story.getUserid(), i);

        if (viewHolder.getAdapterPosition() != 0) {

            seenSTory(viewHolder, story.getUserid());
        }

        if (viewHolder.getAdapterPosition() == 0){
            myStory(viewHolder.addstory_text, viewHolder.story_plus, false);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.getAdapterPosition() == 0) {
                    myStory(viewHolder.addstory_text, viewHolder.story_plus, true);
                } else {
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userid", story.getUserid());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

       public  ImageView story_photo, story_plus, story_photo_seen;
       public TextView story_username, addstory_text;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           story_photo = itemView.findViewById(R.id.story_photo);
           story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
           story_username = itemView.findViewById(R.id.story_username);
           story_plus = itemView.findViewById(R.id.story_plus);
           addstory_text = itemView.findViewById(R.id.addstory_text);
       }
   }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
           return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, final String userid, final int pos){
        ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(viewHolder.story_photo);
                if (pos != 0){
                    Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        ref = FirebaseDatabase.getInstance().getReference("Story")
                .child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    final Story story = snapshot.getValue(Story.class);
                    if (timecurrent> story.getTimestart() && timecurrent <story.getTimeend()){
                        count ++;
                    }
                }
                if (click){
                    if (count > 0) {
                        Intent intent = new Intent(mContext, StoryActivity.class);
                        intent.putExtra("userid", firebaseUser.getUid() );
                        mContext.startActivity(intent);

                    }else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                }else {
                    if (count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void seenSTory(final ViewHolder viewHolder, String userid){
        ref= FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (!snapshot.child("views")
                    .child(firebaseUser.getUid()).exists()
                    && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeend()){
                        i++;
                    }
                }
                if (i >0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
