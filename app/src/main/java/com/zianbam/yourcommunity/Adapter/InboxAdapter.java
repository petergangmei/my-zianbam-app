package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.DeleteActivity;
import com.zianbam.yourcommunity.MessageActivity;
import com.zianbam.yourcommunity.Model.Cbox;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;

import java.util.HashMap;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context mContext;
    private List<Cbox> mUser;

    public InboxAdapter(Context mContext, List<Cbox> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inbox_item,viewGroup,false);
        return new InboxAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Cbox cbox = mUser.get(i);
        if (cbox.getDeleted().equals("false")){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(cbox.getReceiver());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final User user = dataSnapshot.getValue(User.class);

                        viewHolder.username.setText(user.getUsername());

                        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder)
                                .into(viewHolder.image_profile);

                        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Intent intent = new Intent(mContext, DeleteActivity.class);
                                intent.putExtra("userid", user.getId());
                                mContext.startActivity(intent);
                                return true;
                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", user.getId());
                                mContext.startActivity(intent);
                                addisSeen(user.getId());
                            }
                        });


                        isSeen(viewHolder.username, viewHolder.last_message, user.getId(), viewHolder.time);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if (cbox.getDeleted().equals(cbox.getReceiver())){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(cbox.getReceiver());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final User user = dataSnapshot.getValue(User.class);

                        viewHolder.username.setText(user.getUsername());

                        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder)
                                .into(viewHolder.image_profile);

                        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Intent intent = new Intent(mContext, DeleteActivity.class);
                                intent.putExtra("userid", user.getId());
                                mContext.startActivity(intent);
                                return true;
                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", user.getId());
                                mContext.startActivity(intent);
                                addisSeen(user.getId());
                            }
                        });


                        isSeen(viewHolder.username, viewHolder.last_message, user.getId(), viewHolder.time);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            viewHolder.image_profile.setVisibility(View.GONE);
            viewHolder.username.setVisibility(View.GONE);
            viewHolder.last_message.setVisibility(View.GONE);
            viewHolder.time.setVisibility(View.GONE);
        }


    }



    private void addisSeen(String userid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cbox").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isseen", "false");
        ref.updateChildren(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image_profile;
        TextView username, last_message, time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            last_message = itemView.findViewById(R.id.last_message);
            time = itemView.findViewById(R.id.time);
        }
    }

    private void isSeen( final TextView username, final TextView last_message, String userid, final TextView time) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cbox").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        Cbox cbox = dataSnapshot.getValue(Cbox.class);
                        if (cbox.getIsseen().equals("true")){
                            username.setTypeface(username.getTypeface(), Typeface.BOLD);
                            last_message.setTypeface(last_message.getTypeface(), Typeface.BOLD);
                        }
                        last_message.setText(cbox.getLastmsg() +"..");
                        time.setText(cbox.getTime());


                }else {
                    Toast.makeText(mContext, "data doesn't exists", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
