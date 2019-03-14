package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Fragment.AccountFragment;
import com.zianbam.yourcommunity.Model.RecentUser;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;

import java.util.List;

public class RecentuserAdapter extends RecyclerView.Adapter<RecentuserAdapter.ViewHolder>{

    private Context mContext;
    private List<RecentUser> userList;

    public RecentuserAdapter(Context mContext, List<RecentUser> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recentuser_item,viewGroup, false);
        return  new RecentuserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final RecentUser user = userList.get(i);
        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder)
                .into(viewHolder.image_profile);
        viewHolder.username.setText(user.getUsername());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User u = dataSnapshot.getValue(User.class);
                    viewHolder.usergender.setText(u.getGender());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.putString("username", user.getUsername());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new AccountFragment(), "search-profile").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image_profile;
        TextView username, usergender;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            usergender = itemView.findViewById(R.id.usergender);

        }
    }
}
