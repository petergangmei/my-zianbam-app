package com.zianbam.yourcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.Adapter.MessageAdapter;
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Model.Chat;
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

public class MessageActivity extends AppCompatActivity {

    String userid;
    DatabaseReference reference, r1, r2, r3;
    FirebaseAuth mAuth;
    EditText message;
    TextView username;
    ImageView user_av, image_profile, back;
    ImageButton send;
    private ScrollView mScrollView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    APIService apiService;
    boolean notify = false;

    private String chatid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        image_profile = findViewById(R.id.image_profile);
        mAuth = FirebaseAuth.getInstance();
        final String myid = mAuth.getCurrentUser().getUid();
        username = findViewById(R.id.username);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message_text);
        mScrollView = findViewById(R.id.mScrollView);
        back = findViewById(R.id.ic_back);


        mScrollView.postDelayed(new Runnable() {
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                message.setText("");
                message.requestFocus();
            }
        },300L);

//        InputMethodManager inputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        InputMethodManager inputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        recyclerView = findViewById(R.id.recycle_view_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getUserinfo();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    OnSendMessage(message.getText().toString());
                    message.setText("");
                }
            }
        });
    }

    private void readMessage(final String myid, final String userid, final String imageURL) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("Chats").child(userid);
        Query query = reference.limitToLast(22);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Chat chat = snapshot.getValue(Chat.class);

                        if (chat.getDeleted().equals("false")){
                            mChat.add(chat);
                        } else if (chat.getDeleted().equals("foreveryone")){
                            mChat.add(chat);
                        }else if(chat.getDeleted().equals("forme")){

                        }

                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL, userid);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void OnSendMessage( final String msg) {
                notify = true;

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());


                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
              final  String currentDate = currentDateFormat.format(calForDate.getTime());

         final long number = System.currentTimeMillis();
        String timestamp = Long.toString(number);

        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(myid);

       final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("Cbox").child(userid);
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("receiver", userid);
        hashMap1.put("isseen", "false");
        hashMap1.put("deleted", "false");
        hashMap1.put("time", currentTime);
        hashMap1.put("date", currentDate);
        hashMap1.put("timestamp", timestamp );
        hashMap1.put("lastmsg", msg);
        ref1.setValue(hashMap1);

//        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                     FirebaseDatabase.getInstance().getReference().child("Cbox").child(mAuth.getUid()).child(userid).removeValue()
//                             .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                 @Override
//                                 public void onComplete(@NonNull Task<Void> task) {
//                                     HashMap<String, Object> hashMap1 = new HashMap<>();
//                                     hashMap1.put("receiver", userid);
//                                     hashMap1.put("isseen", "false");
//                                     hashMap1.put("deleted", "false");
//                                     hashMap1.put("time", currentTime);
//                                     hashMap1.put("date", currentDate);
//                                     hashMap1.put("timestamp", System.currentTimeMillis());
//                                     hashMap1.put("lastmsg", msg);
//                                     ref1.setValue(hashMap1);
//                                 }
//                             });
//                }else {
//                    HashMap<String, Object> hashMap1 = new HashMap<>();
//                    hashMap1.put("receiver", userid);
//                    hashMap1.put("isseen", "false");
//                    hashMap1.put("deleted", "false");
//                    hashMap1.put("time", currentTime);
//                    hashMap1.put("date", currentDate);
//                    hashMap1.put("timestamp", System.currentTimeMillis());
//                    hashMap1.put("lastmsg", msg);
//                    ref1.setValue(hashMap1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



       final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Cbox").child(mAuth.getUid());
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("receiver", mAuth.getUid() );
        hashMap2.put("isseen", "true");
        hashMap2.put("deleted", "false");
        hashMap2.put("time", currentTime);
        hashMap2.put("date", currentDate);
        hashMap2.put("timestamp", timestamp);
        hashMap2.put("lastmsg", msg);
        ref2.setValue(hashMap2);
//        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    FirebaseDatabase.getInstance().getReference().child("Cbox").child(userid).child(mAuth.getUid()).removeValue()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    HashMap<String, Object> hashMap2 = new HashMap<>();
//                                    hashMap2.put("receiver", mAuth.getUid() );
//                                    hashMap2.put("isseen", "true");
//                                    hashMap2.put("deleted", "false");
//                                    hashMap2.put("time", currentTime);
//                                    hashMap2.put("date", currentDate);
//                                    hashMap2.put("timestamp", System.currentTimeMillis());
//                                    hashMap2.put("lastmsg", msg);
//                                    ref2.setValue(hashMap2);
//                                }
//                            });
//                }else {
//                    HashMap<String, Object> hashMap2 = new HashMap<>();
//                    hashMap2.put("receiver", mAuth.getUid() );
//                    hashMap2.put("isseen", "true");
//                    hashMap2.put("deleted", "false");
//                    hashMap2.put("time", currentTime);
//                    hashMap2.put("date", currentDate);
//                    hashMap2.put("timestamp", System.currentTimeMillis());
//                    hashMap2.put("lastmsg", msg);
//                    ref2.setValue(hashMap2);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("Chats").child(userid);
        String id = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", msg);
        hashMap.put("id", id);
        hashMap.put("conversationid", chatid);
        hashMap.put("deleted", "false");
        hashMap.put("deleted2", "false");
        hashMap.put("time", currentTime);
//        hashMap.put("receiver", userid);
        hashMap.put("sender", mAuth.getUid());
        reference.child(id).setValue(hashMap);

        r1 = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Chats").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap_ = new HashMap<>();
        hashMap_.put("message", msg);
        hashMap_.put("id", id);
        hashMap_.put("conversationid", chatid);
        hashMap_.put("deleted", "false");
        hashMap_.put("deleted2", "false");
        hashMap_.put("time", currentTime);
//        hashMap_.put("receiver", userid);
        hashMap_.put("sender", mAuth.getUid());
        r1.child(id).setValue(hashMap_);


      //send notifications
        DatabaseReference reference1 =  FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    if (notify){
                        sendNotification(userid, user.getUsername(), msg );
                    }
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mScrollView.postDelayed(new Runnable() {
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                message.setText("");
                message.requestFocus();
            }
        },300L);

    }

    private void addChat(String chatid, String msg, String currentTime) {
    }

    private void getUserinfo() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    readMessage(mAuth.getUid(), userid, user.getImageURL());
                    Picasso.get().load(user.getImageURL()).placeholder(R.drawable.image_placeholder).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String publisher, final String username, final String msg) {
//        Toast.makeText(MessageActivity.this, "myid:-> "+FirebaseAuth.getInstance().getCurrentUser().getUid() + "publisher:-> "+userid + username, Toast.LENGTH_LONG).show();

//      final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                      Token token = snapshot.getValue(Token.class);
                      Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher, "["+username+"]  ' "+msg+" '", "New Message",
                              publisher);
                      Sender sender = new Sender(data, token.getToken());

                      apiService.sendNotification(sender)
                              .enqueue(new Callback<MyResponse>() {
                                  @Override
                                  public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                      if (response.code() == 200){
                                          if (response.body().success !=1 ){
                                              Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  }
                                  @Override
                                  public void onFailure(Call<MyResponse> call, Throwable t) {
                                  }
                              });
                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
