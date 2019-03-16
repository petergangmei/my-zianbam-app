package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.ViewHolder>{
    private Context mContext;
    private List<User> mUser;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    APIService apiService;

    public UserAdapter2(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup,false);
        return new UserAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUser.get(i);
        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        viewHolder.dName.setText(user.getUsername());
        viewHolder.gender.setText(user.getGender());
        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(viewHolder.image_profile);

        isFollowing(user.getId(), viewHolder.btn_follow);

        if (user.getId().equals(firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("tag", user.getUsername());
                Log.i("tag", user.getId());
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                editor.putString("profileid", user.getId());
//                editor.putString("username", user.getUsername());
//                editor.apply();
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
//                        new AccountFragment(), "search-profile").commit();
            }
        });

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.btn_follow.getText().equals("follow")){
                    addNofication(user.getId());
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                User u = dataSnapshot.getValue(User.class);
                                sendNotification(user.getId(), u.getUsername());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    Toast.makeText(mContext, "Followed!", Toast.LENGTH_SHORT).show();
                }else {
                    RemoveNotification(user.getId());

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dName, dLocation, gender;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dName = itemView.findViewById(R.id.userName);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            gender = itemView.findViewById(R.id.usergender);
        }
    }

    private void RemoveNotification(final String id) {
        reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers").child(firebaseUser.getUid()).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String notificationid = dataSnapshot.child("id").getValue().toString();

                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).child(notificationid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                            .child(id).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers")
                            .child(firebaseUser.getUid()).child(id).removeValue();

                    Toast.makeText(mContext, "Unfollowed!", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(mContext, "Data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addNofication(String userid){
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());
        String notificationId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);
        hashMap.put("notificationid", notificationId);
        hashMap.put("time", currentTime);
        hashMap.put("date", currentDate);
        hashMap.put("isseen", "false");
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        reference.child(notificationId).setValue(hashMap);

        DatabaseReference  ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                .child(userid);
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("id", notificationId);
        ref.child(firebaseUser.getUid()).setValue(hashMap1);

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userid).child("followers")
                .child(firebaseUser.getUid());
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("id", notificationId);
        ref1.child(userid).setValue(hashMap2);

    }

    private void sendNotification(final String publisher, final String username) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "["+username+"] started following. ", "Notification",
                            publisher);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200){
                                        if (response.body().success !=1 ){
                                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                                        }else {
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
