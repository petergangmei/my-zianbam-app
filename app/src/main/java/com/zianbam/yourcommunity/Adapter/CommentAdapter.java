package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zianbam.yourcommunity.GetTimeAgo;
import com.zianbam.yourcommunity.MainActivity;
import com.zianbam.yourcommunity.Model.Comment;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postid;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup ,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(i);
        viewHolder.comment.setText(comment.getComment());

//        getUserInfo(viewHolder.image_profile, viewHolder.comment, comment.getPublisherid());

        //get time ago
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("Comments").child(comment.getPostid()).child(comment.getCommentid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String time = dataSnapshot.child("timestamp").getValue().toString();
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long realtime = Long.parseLong(time);
                    String timeAgo = getTimeAgo.getTimeAgo(realtime, mContext);
                    viewHolder.timego.setText(timeAgo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

// get user name and image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(comment.getPublisherid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                Picasso.get().load(user.getImageURL()).into(viewHolder.image_profile);
                viewHolder.username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisherid", comment.getPublisherid());
//                mContext.startActivity(intent);
//            }
//        });
        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisherid());
                mContext.startActivity(intent);
            }
        });

        viewHolder.comment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               if (comment.getPublisherid().equals(firebaseUser.getUid())){
                   AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                   alertDialog.setTitle("Do you want to delete?");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                               }
                           });
                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   Log.d("tag", "postid:->"+postid + " commentid:-> "+comment.getCommentid() +" userid:- "+comment.getUserid());

                                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid());
                                   ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()){
                                          
                                               FirebaseDatabase.getInstance().getReference("Comments").child(postid).child(comment.getCommentid()).removeValue();
                                                if (!comment.getUserid().equals(firebaseUser.getUid())){
                                                    FirebaseDatabase.getInstance().getReference("Notifications").child(comment.getUserid()).child(comment.getCommentid()).removeValue();
                                                }
                                               
                                           }else {
                                           }
                                       }
                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });

                                   dialogInterface.dismiss();;
                               }
                           });
                   alertDialog.show();
               }else{
                   AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                   alertDialog.setTitle("Report this comment?");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                               }
                           });
                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "Reported!", Toast.LENGTH_SHORT).show();
                                   dialogInterface.dismiss();;
                               }
                           });
                   alertDialog.show();
               }
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_profile,deleteImage;
        TextView comment, username, timego;
        LinearLayout re1;
        RelativeLayout bottom;
        ScrollView scrollView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bottom = itemView.findViewById(R.id.bottom);
            scrollView = itemView.findViewById(R.id.scrollview);
            re1 = itemView.findViewById(R.id.re1);
            deleteImage = itemView.findViewById(R.id.optionImage);
            image_profile = itemView.findViewById(R.id.image_profile);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username_);
            timego = itemView.findViewById(R.id.timeago);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView comment, String publisherid){

    }
}
