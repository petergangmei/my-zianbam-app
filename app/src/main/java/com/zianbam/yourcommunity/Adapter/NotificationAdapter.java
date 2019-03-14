package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Fragment.AccountFragment;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.Model.Notification;
import com.zianbam.yourcommunity.Model.Post;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder>{

    private Context mContext;
    private List<Notification> mNotification;
    private DatabaseReference reference;
    APIService apiService;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,viewGroup,false);
        return new NotificationAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, int i) {
      final   Notification notification = mNotification.get(i);
      final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        isFollowing(notification.getUserid(), viewholder.followbackBtn);

        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(myid).child(notification.getNotificationid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Notification n = dataSnapshot.getValue(Notification.class);
                    //add isseen value true.

                    //show time ago
                    String timestamepraw = dataSnapshot.child("timestamp").getValue().toString();
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long time = Long.parseLong(timestamepraw);
                    String timeAgo = getTimeAgo.getTimeAgo(time, mContext);
                    viewholder.timeago.setText(timeAgo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
      viewholder.text.setText(notification.getText());

      getUserInfo(viewholder.image_profile, viewholder.username, notification.getUserid());



      if (notification.isIspost()){
       reference = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
       reference.keepSynced(true);
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   Post post = dataSnapshot.getValue(Post.class);
                   if (post.getType().equals("photo_post")){
                       viewholder.post_image.setVisibility(View.VISIBLE);
                       getPostImage(viewholder.post_image, notification.getPostid());
                   }else if (post.getType().equals("text_post")){
                       if(!notification.getText().equals("like your post")){
                           viewholder.post_text.setVisibility(View.GONE);
                       }else {
                           viewholder.post_text.setVisibility(View.VISIBLE);

                       }
//                       viewholder.post_text.setVisibility(View.VISIBLE);
//                       viewholder.post_text.setText(post.getPost_text());
                       viewholder.post_image.setVisibility(View.GONE);
                       reference = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
                       reference.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.exists()){
                                   Post p = dataSnapshot.getValue(Post.class);
                                   viewholder.post_text.setText(" [ "+ p.getPost_text()+" ] ");
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }else {
                       viewholder.post_image.setVisibility(View.GONE);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

      }else {
          viewholder.post_image.setVisibility(View.GONE);
          viewholder.followbackBtn.setVisibility(View.VISIBLE);
          final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

          viewholder.followbackBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (viewholder.followbackBtn.getText().equals("follow back")){
                      addNofication(notification.getUserid());
                      reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                      reference.keepSynced(true);
                      reference.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.exists()){
                                  User u = dataSnapshot.getValue(User.class);
                                  sendNotification(notification.getUserid(), u.getUsername());
                              }
                          }
                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {
                          }
                      });
                      Toast.makeText(mContext, "Followed!", Toast.LENGTH_SHORT).show();
                  }else {
                      RemoveNotification(notification.getUserid());

                  }
              }
          });
      }

      viewholder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (notification.isIspost()){
                  Intent intent = new Intent(mContext, CommentsActivity.class);
                  intent.putExtra("postid", notification.getPostid());
                  mContext.startActivity(intent);
              }else {
                  SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                  editor.putString("profileid", notification.getUserid());
                  editor.putString("username", "none");
                  editor.apply();
                  ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                          new AccountFragment(), "notification-profile").commit();
              }
          }
      });


    }


    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        ImageView image_profile, post_image;
        TextView username, text, post_text, timeago;
        Button followbackBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
            post_text = itemView.findViewById(R.id.post_text);
            timeago = itemView.findViewById(R.id.timeago);
            followbackBtn = itemView.findViewById(R.id.followbackBtn);
        }
    }

    private void isFollowing(final String userid, final Button button){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow back");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private  void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, final String postid){
        reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.image_placeholder).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RemoveNotification(final String id) {
       final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

}
