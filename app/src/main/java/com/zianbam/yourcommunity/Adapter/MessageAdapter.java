package com.zianbam.yourcommunity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Model.Chat;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final  int MSG_TYPE_LEFT = 0;
    public static final  int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageURL;
    FirebaseUser firebaseUser;
    private String userid;

    APIService apiService;
    boolean notify = false;


    public MessageAdapter(Context mContext, List<Chat> mChat, String imageURL, String userid) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageURL = imageURL;
        this.userid =  userid;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup ,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup ,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Chat chat = mChat.get(i);

        Picasso.get().load(imageURL).into(viewHolder.image_profile);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Chats").child(userid).child(chat.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Chat chat1 = dataSnapshot.getValue(Chat.class);
                    if (chat1.getDeleted().equals("foreveryone")){
                        if (!chat1.getSender().equals(firebaseUser.getUid())) {
                            DatabaseReference re = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            re.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String un = dataSnapshot.child("username").getValue().toString();
                                    viewHolder.show_message.setText(un+ " deleted a message");
                                    viewHolder.time.setText(chat.getTime());
                                    viewHolder.show_message.setTextSize(12);
                                    viewHolder.show_message.setTypeface(viewHolder.show_message.getTypeface(), Typeface.ITALIC);
                                    viewHolder.body.setBackgroundColor(Color.WHITE);
//                                    viewHolder.show_message.setTextColor(Color.BLACK);
                                    viewHolder.ic_icon.setVisibility(View.VISIBLE);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else {
                            viewHolder.show_message.setText("You deleted this message");
                            viewHolder.time.setText(chat.getTime());
                            viewHolder.show_message.setTextSize(12);
                            viewHolder.show_message.setTypeface(viewHolder.show_message.getTypeface(), Typeface.ITALIC);
                            viewHolder.body.setBackgroundColor(Color.WHITE);
                            viewHolder.ic_icon.setVisibility(View.VISIBLE);
//                            viewHolder.show_message.setTextColor(Color.BLACK);

                        }

                    }else {
                        viewHolder.show_message.setText(chat.getMessage());
                        viewHolder.time.setText(chat.getTime());
                    }
                }else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.delete_dialog);
                    TextView deleteForMe = dialog.findViewById(R.id.deleteForMe);
                    TextView deteteForE = dialog.findViewById(R.id.deleteForE);
                    TextView alreadydeleted = dialog.findViewById(R.id.alreadydeleted);
                    if (chat.getDeleted().equals("foreveryone")){
                        deteteForE.setVisibility(View.GONE);

                    }

                    deleteForMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Chats")
                                    .child(userid).child(chat.getId());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("deleted", "forme");
                            ref.updateChildren(hashMap);
                            dialog.dismiss();
                        }
                    });

                    deteteForE.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Chats")
                                    .child(userid).child(chat.getId());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("deleted", "foreveryone");
                            ref.updateChildren(hashMap);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Chats").child(firebaseUser.getUid())
                                    .child(chat.getId());
                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("deleted", "foreveryone");
                            ref2.updateChildren(hashMap2);
                            dialog.dismiss();

                            DatabaseReference r = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                    .child("Cbox").child(userid);
                            HashMap<String, Object> hashMapCbox = new HashMap<>();
                            hashMapCbox.put("lastmsg", "-- message deleted --");
                            r.updateChildren(hashMapCbox);
                            DatabaseReference r2 = FirebaseDatabase.getInstance().getReference("Users").child(userid)
                                    .child("Cbox").child(firebaseUser.getUid());
                            HashMap<String, Object> hashMapCbox2 = new HashMap<>();
                            hashMapCbox2.put("lastmsg", "-- message deleted -- ");
                            r2.updateChildren(hashMapCbox2);
                        }
                    });

                    dialog.show();
                }else {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.delete_dialog);
                    TextView deleteForMe = dialog.findViewById(R.id.deleteForMe);
                    TextView deteteForE = dialog.findViewById(R.id.deleteForE);
                    TextView alreadydeleted = dialog.findViewById(R.id.alreadydeleted);
                    deteteForE.setVisibility(View.GONE);


                    deleteForMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Chats").child(userid).child(chat.getId());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("deleted", "forme");
                            ref.updateChildren(hashMap);
                            dialog.dismiss();
                        }
                    });

                    deteteForE.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
//                                    .child(firebaseUser.getUid())
//                                    .child("Chats")
//                                    .child(userid)
//                                    .child(chat.getId());
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("deleted", "foreveryone");
//                            ref.updateChildren(hashMap);

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userid)
                                    .child("Chats")
                                    .child(firebaseUser.getUid())
                                    .child(chat.getId());
                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("deleted", "foreveryone");
                            ref2.updateChildren(hashMap2);

                            dialog.dismiss();
                        }
                    });



                    dialog.show();
                }



                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_profile, ic_icon;
        RelativeLayout body;
        TextView show_message, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            show_message = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.time);
            body = itemView.findViewById(R.id.body);
            ic_icon = itemView.findViewById(R.id.ic_icon);


        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    private void sendNotification(final String publisher, final String username, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": send you message ' "+msg+" '", "New Message",
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
