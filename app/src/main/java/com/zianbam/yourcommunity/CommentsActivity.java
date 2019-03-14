package com.zianbam.yourcommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.zianbam.yourcommunity.Adapter.CommentAdapter;
import com.zianbam.yourcommunity.Adapter.PostdetailAdapter;
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Model.Comment;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList;

    private RecyclerView recyclerView_post;
    private PostdetailAdapter postAdapter;
    private List<Post> list;
    DatabaseReference reference;

    EditText add_comment;
    TextView post_comment;
    ImageView image_profile;
    private  String postid, publisherid, commentid;
    FirebaseUser firebaseUser;
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        recyclerView_post= findViewById(R.id.recycle_view_post);
        recyclerView_post.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(this);
        linearLayoutManagerr.setReverseLayout(true);
        linearLayoutManagerr.setStackFromEnd(true);
        recyclerView_post.setLayoutManager(linearLayoutManagerr);
        list = new ArrayList<Post>();

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query = reference.orderByChild("postid").equalTo(postid).limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
               if(dataSnapshot.exists()){
                   for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                       Post p =  dataSnapshot1.getValue(Post.class);
                       list.add(p);
                   }
                   postAdapter = new PostdetailAdapter(getApplicationContext(), list);
                   recyclerView_post.setAdapter(postAdapter);
               }else {
                   Toast.makeText(CommentsActivity.this, "Post has been deleted!", Toast.LENGTH_SHORT).show();

                   AppBarLayout appBarLayout = findViewById(R.id.bar);
                   appBarLayout.setVisibility(View.GONE);
                   ScrollView scrollView = findViewById(R.id.scrollview);
                   scrollView.setVisibility(View.GONE);
                   RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.deletedpost);
                   relativeLayout.setVisibility(View.VISIBLE);
                   RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(R.id.bottom);
                   relativeLayout1.setVisibility(View.GONE);

               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Ops! Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recycle_view_comments);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);

        add_comment = findViewById(R.id.add_comment);
        post_comment = findViewById(R.id.post_comment);
        image_profile = findViewById(R.id.image_profile);


        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(add_comment.getText().toString())){
                    Toast.makeText(CommentsActivity.this, "Enter comment..", Toast.LENGTH_SHORT).show();
                }else {
//                    addComment();
                    addNotificationComment(add_comment.getText().toString());
                }
            }
        });
        getImage();
        readComments();
    }

    private void addNotificationComment(String msg){
        notify = true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        String commentid = reference.push().getKey();

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("comment", msg);
        hashMap1.put("postid", postid);
        hashMap1.put("commentid", commentid);
        hashMap1.put("publisherid", firebaseUser.getUid());
        hashMap1.put("userid", publisherid);
        hashMap1.put("time", currentTime);
        hashMap1.put("date", currentDate);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        reference.child(commentid).setValue(hashMap1);

       if (!publisherid.equals(firebaseUser.getUid())){
           reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
           HashMap<String, Object> hashMap = new HashMap<>();
           hashMap.put("userid", firebaseUser.getUid());
           hashMap.put("text", "Commented: "+msg);
           hashMap.put("postid", postid);
           hashMap.put("notificationid",commentid );
           hashMap.put("ispost", true);
           hashMap.put("time", currentTime);
           hashMap.put("date", currentDate);
           hashMap.put("isseen", "false");
           hashMap.put("timestamp", ServerValue.TIMESTAMP);
           reference.child(commentid).setValue(hashMap);
       }

//        addComment(viewHolder.comment_text.getText().toString(),  post.getPostid());

       final  String msg1 = add_comment.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                if (notify){
                    if (!publisherid.equals(firebaseUser.getUid())){
                        sendCommentNotification(publisherid, user1.getUsername(), msg1);
                    }
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        add_comment.setText("");
    }

//    private void addComment() {
//        notify = true;
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("comment", comment_text);
//        hashMap.put("publisherid", firebaseUser.getUid());
//        reference.push().setValue(hashMap);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
//        String commentid = reference.push().getKey();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("comment", add_comment.getText().toString());
//        hashMap.put("postid", postid);
//        hashMap.put("commentid", commentid);
//        hashMap.put("publisherid", firebaseUser.getUid());
//
//        reference.child(commentid).setValue(hashMap);
//
//        addNofication();
//
//        final  String msg = add_comment.getText().toString();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user1 = dataSnapshot.getValue(User.class);
//                if (notify){
//                    sendNotification(publisherid, user1.getUsername(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        add_comment.setText("");
//
//
//    }




//    private void sendNotification(final String publisher, final String username, final String msg) {
//
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(publisher);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(firebaseUser.getUid(), R.drawable.user_avatar, username+": Commented in your post ' "+msg+" '", "Notification",
//                            publisher);
//                    Sender sender = new Sender(data, token.getToken());
//
//
//
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                                    if (response.code() == 200){
//                                        if (response.body().success !=1 ){
//                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
//                                        }else {
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

//    private void addNofication(){
//        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("userid", firebaseUser.getUid());
//        hashMap.put("text", "commented: "+add_comment.getText().toString());
//        hashMap.put("postid", postid);
//        hashMap.put("ispost", true);
//        reference.push().setValue(hashMap);
//    }

    private void sendCommentNotification(final String publisher, final String username, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "["+username+"] Commented: ' "+msg+" '", "Notification",
                            publisher);
                    Sender sender = new Sender(data, token.getToken());



                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200){
                                        if (response.body().success !=1 ){
//                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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
    private  void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Picasso.get().load(user.getImageURL()).into(image_profile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        commentList.add(comment);

                    }
                    commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
