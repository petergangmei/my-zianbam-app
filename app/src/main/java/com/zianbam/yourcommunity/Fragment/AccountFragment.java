package com.zianbam.yourcommunity.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.zianbam.yourcommunity.Adapter.FeaturephotoAdapter;
import com.zianbam.yourcommunity.Adapter.PostAdapter;
import com.zianbam.yourcommunity.CheckOutProfileActivity;
import com.zianbam.yourcommunity.MessageActivity;
import com.zianbam.yourcommunity.Model.FeaturePhoto;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;
import com.zianbam.yourcommunity.R;
import com.zianbam.yourcommunity.RewardedVideoActivity;
import com.zianbam.yourcommunity.UpdateProfileActivity;
import com.zianbam.yourcommunity.UpdateProfilePictureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {
    private TextView addcoins, name, username, location,bio,email,gender,follower,following,posts_,notice_text,notice_text2, notice_text3, energy_value, coins_value;
    private ImageView image_profile, menu, addBtn, inbox;
    private Button edit_profileBtn,connect;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String profileid;
    private String pusername;
    private FirebaseUser firebaseUser;
    private String energy;
    int newenergy;
    private Dialog dialog;

    private RecyclerView recyclerView_post;
    private PostAdapter postAdapter;
    private List<Post> postList;
    ProgressBar progress_circular, pc;

    private RecyclerView recycle_view_featurephoto;
    private FeaturephotoAdapter featurephotoAdapter;
    private  List<FeaturePhoto> featurePhotoList;

    private RecyclerView recyclerView_saves;
    private PostAdapter myPhotoAdapter_saves;
    private List<Post> postList_saves;
    private List<String> mySaves;
    ImageButton arrowdownBtn;
    APIService apiService;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        pusername = prefs.getString("username", "none");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        addcoins = v.findViewById(R.id.addcoins);
        recyclerView_saves = v.findViewById(R.id.recycle_view_saves);
        recyclerView_post = v.findViewById(R.id.recycle_view_post);
        recycle_view_featurephoto = v.findViewById(R.id.recycle_view_featurephoto);
         arrowdownBtn = v.findViewById(R.id.arrowdownBtn);
        inbox = v.findViewById(R.id.inbox);
        name = v.findViewById(R.id.name);
        username = v.findViewById(R.id.username);
        bio = v.findViewById(R.id.bio);
        location = v.findViewById(R.id.location);
        gender = v.findViewById(R.id.gender);
        follower = v.findViewById(R.id.follower);
        following = v.findViewById(R.id.following);
        posts_ = v.findViewById(R.id.posts_);
        image_profile = v.findViewById(R.id.image_profile);
        edit_profileBtn = v.findViewById(R.id.edit_profileBtn);
        menu = v.findViewById(R.id.setting_menu);
        connect = v.findViewById(R.id.connect);
        notice_text = v.findViewById(R.id.notice_text);
        addBtn = v.findViewById(R.id.addBtn);
        energy_value = v.findViewById(R.id.enery_value);
        coins_value = v.findViewById(R.id.coins_value);
        pc = v.findViewById(R.id.pc);
//        notice_text2 = v.findViewById(R.id.notice_text2);
//        notice_text3  = v.findViewById(R.id.notice_text3);
        LinearLayout l1 = v.findViewById(R.id.energyLayout);
        LinearLayout l2 = v.findViewById(R.id.coinsLayout);

        ImageButton imageBtn_grid = v.findViewById(R.id.imageBtn_grid);
        ImageButton imageBtn_solo = v.findViewById(R.id.imageBtn_solo);
        ImageButton imageBtn_saved = v.findViewById(R.id.imageBtn_saved);

        recyclerView_featurephoto_acitivties();
        recyclerView_post_activities();
        recyclerView_saves_activities();

        getUserInfo();
        OnClickClass();

        getFollowers();
        getNrPost();
        getUserInfo();
        mysaves();


        if (profileid.equals(firebaseUser.getUid())){
            edit_profileBtn.setText("edit profile");
            edit_profileBtn.setTag("edit profile");
            imageBtn_saved.setVisibility(View.VISIBLE);
            connect.setText("Verify");
            connect.setTag("add_photo");
            addBtn.setVisibility(View.VISIBLE);
            arrowdownBtn.setVisibility(View.GONE);
            edit_profileBtn.setMinimumWidth(60);
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            addcoins.setVisibility(View.VISIBLE);
        }else {

            addBtn.setVisibility(View.GONE);
            imageBtn_saved.setVisibility(View.GONE);
            connect.setText("Matchout");
            connect.setTag("connect");
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);
            //            edit_profileBtn.setTypeface();
//            edit_profileBtn.setTag("message");
//            edit_profileBtn.setText("message");
            checkFollow();

        }
        arrowdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.unfollow:
                                RemoveNotification(profileid);
                                getFollowers();
                                checkFollow();

                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.unfollow_menu);

                popupMenu.show();
            }
        });


        addcoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pc.setVisibility(View.VISIBLE);
                addcoins.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(), RewardedVideoActivity.class);
                intent.putExtra("log", "start");
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pc.setVisibility(View.GONE);
                        addcoins.setVisibility(View.VISIBLE);
                    }
                },4000);
            }
        });


        imageBtn_solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_post.setVisibility(View.INVISIBLE);
                recyclerView_saves.setVisibility(View.INVISIBLE);
                recycle_view_featurephoto.setVisibility(View.VISIBLE);
                recyclerView_featurephoto_acitivties();



            }
        });
        imageBtn_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_post.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.INVISIBLE);
                recycle_view_featurephoto.setVisibility(View.INVISIBLE);
                notice_text.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        imageBtn_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_saves.setVisibility(View.VISIBLE);
                recyclerView_post.setVisibility(View.INVISIBLE);
                notice_text.setVisibility(View.GONE);
                recycle_view_featurephoto.setVisibility(View.INVISIBLE);
            }
        });

        return v;
    }



    private void OnClickClass() {
        edit_profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profileBtn.getTag().toString();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (btn.equals("edit profile")){
                    startActivity(new Intent(getContext(), UpdateProfileActivity.class));
                }else if(btn.equals("follow")){
                    addNofication(profileid);
                    checkFollow();
                    getFollowers();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                User u = dataSnapshot.getValue(User.class);
                                sendNotification(profileid, u.getUsername());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else if (btn.equals("message")){

                    Intent intent = new Intent(getContext(), MessageActivity.class);
                    intent.putExtra("userid", profileid);
                    intent.putExtra("username", pusername);
                    startActivity(intent);
                }
            }
        });
        

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), UpdateProfilePictureActivity.class));
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String btn  = connect.getTag().toString();
               final FirebaseUser firebaseUser = mAuth.getCurrentUser();
               if (btn.equals("add_photo")){
//                   reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//                   reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                       @Override
//                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                           if (dataSnapshot.exists()){
//                               User user = dataSnapshot.getValue(User.class);
//                               if (user.getFeaturephoto() > System.currentTimeMillis()){
//                                   Toast.makeText(getContext(), "YOu can only upload another feature after 24 hours", Toast.LENGTH_SHORT).show();
//                               }else {
//                                   startActivity(new Intent(getContext(),FeaturePictureActivity.class));
//                               }
//                           }
//                       }
//
//                       @Override
//                       public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                       }
//                   });
//                   final Dialog dialog = new Dialog(mContext);
//                   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                   dialog.setContentView(R.layout.delete_dialog);
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_verify_profile);
                dialog.show();


               }else if (btn.equals("connect")){

                    dialog = new Dialog(getContext());
                   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                   dialog.setContentView(R.layout.checkout_prepare_dilog);
                   TextView cusername = dialog.findViewById(R.id.cusername);
                   final TextView energybar = dialog.findViewById(R.id.energybar);
                   final Button button = dialog.findViewById(R.id.continue_btn);
                   final TextView comment = dialog.findViewById(R.id.comment);
                   final TextView username2 = dialog.findViewById(R.id.username2);
                   final ImageView image_energy = dialog.findViewById(R.id.image_energy);
                   final Button closebtn = dialog.findViewById(R.id.closebtn);
                   progress_circular = dialog.findViewById(R.id.progress_circular);

                   cusername.setText(pusername);
                   reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                   reference.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){

                               String en = dataSnapshot.child("energy").getValue().toString();
                               energybar.setText(en);
                               username2.setText(pusername);
                               image_energy.setVisibility(View.VISIBLE);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

                   closebtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                            dialog.dismiss();
                       }
                   });

              button.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           if (button.getText().toString().equals("Continue")){
                               FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                               reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                               reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       String energy = dataSnapshot.child("energy").getValue().toString();
                                       if (energy.equals("0")){
                                           button.setText("Yes");
                                           button.setTag("get energy");
                                           comment.setText("Refill 10 energy bars with 5 coins?");
                                           image_energy.setVisibility(View.GONE);
                                           username2.setVisibility(View.GONE);
                                           closebtn.setVisibility(View.VISIBLE);

                                       }else {
                                           button.setVisibility(View.GONE);
                                           progress_circular.setVisibility(View.VISIBLE);
                                           final int myNum;
                                           switch (energy){
                                               case "1":
                                                   myNum = 1;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "2":
                                                   myNum = 2;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "3":
                                                   myNum = 3;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "4":
                                                   myNum = 4;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "5":
                                                   myNum = 5;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "6":
                                                   myNum = 6;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "7":
                                                   myNum = 7;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "8":
                                                   myNum = 8;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "9":
                                                   myNum = 9;
                                                   newenergy = myNum - 1;
                                                   toDatabase(newenergy);
                                                   break;
                                               case "10":
                                                   myNum = 10;
                                                   newenergy = myNum - 1;

                                                   toDatabase(newenergy);
                                                   break;
                                           }
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {
                                   }
                               });
                           }else if (button.getText().toString().equals("Yes")){
                               reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                               reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.exists()){
                                           String co = dataSnapshot.child("coins").getValue().toString();
                                           int coins = Integer.parseInt(co);
                                           if (coins >= 5){
                                               int newcoins = coins - 5;
                                               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                               HashMap<String, Object> hashMap = new HashMap<>();
                                               hashMap.put("coins", newcoins);
                                               hashMap.put("energy", "10");
                                               ref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(getContext(), "Energy Refilled!", Toast.LENGTH_SHORT).show();
                                                       comment.setText("Energy bar refill success!");

                                                       new Handler().postDelayed(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               dialog.dismiss();
                                                           }
                                                       }, 1000);
                                                   }
                                               });
                                           }else {
                                               comment.setText("You do not have enough coins!");
                                               Toast.makeText(getContext(), "You do not have enough coins!", Toast.LENGTH_LONG).show();
                                               new Handler().postDelayed(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       dialog.dismiss();
//                                                       new Handler().postDelayed(new Runnable() {
//                                                           @Override
//                                                           public void run() {
//                                                               dialog.show();
//                                                           }
//                                                       },500);
                                                   }
                                               }, 1000);
                                           }
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });



                           }

                       }
                   });
                   dialog.show();

               }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new MenuFragment(), "account-menu").commit();
            }
        });

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileid);
//                intent.putExtra("title", "followers");
//                startActivity(intent);
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", profileid);
                editor.putString("storyid", "none");
                editor.putString("title", "followers");
                editor.apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new FollowersFragment(), "profile-followers").commit();

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileid);
//                intent.putExtra("title", "following");
//                startActivity(intent);
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", profileid);
                editor.putString("storyid", "none");
                editor.putString("title", "following");
                editor.apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new FollowersFragment(), "profile-following").commit();
            }
        });



    }
    private void toDatabase(int newenergy) {
        String myenergy ;
        switch (newenergy){
            case 0:
                myenergy = "0";
                addEvalue(myenergy);
                break;

            case 1:
                myenergy = "1";
                addEvalue(myenergy);
                break;
            case 2:
                myenergy = "2";
                addEvalue(myenergy);
                break;
            case 3:
                myenergy = "3";
                addEvalue(myenergy);
                break;
            case 4:
                myenergy = "4";
                addEvalue(myenergy);
                break;
            case 5:
                myenergy = "5";
                addEvalue(myenergy);
                break;
            case 6:
                myenergy = "6";
                addEvalue(myenergy);
                break;
            case 7:
                myenergy = "7";
                addEvalue(myenergy);
                break;
            case 8:
                myenergy = "8";
                addEvalue(myenergy);
                break;
            case 9:
                myenergy = "9";
                addEvalue(myenergy);
                break;
            case 10:
                myenergy = "10";
                addEvalue(myenergy);
                break;
        }
    }

    private void addEvalue(String energy){

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("energy", energy);
        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(getContext(), CheckOutProfileActivity.class);
                intent.putExtra("profileid", profileid);
                intent.putExtra("username", pusername );
                startActivity(intent);
                dialog.dismiss();
                progress_circular.setVisibility(View.GONE);

            }
        });
    }

        private void recyclerView_featurephoto_acitivties() {
        recycle_view_featurephoto.setHasFixedSize(true);
        GridLayoutManager layoutManager_featurephotos = new GridLayoutManager(getContext(), 2);
        recycle_view_featurephoto.setLayoutManager(layoutManager_featurephotos);
        featurePhotoList = new ArrayList<>();
        featurephotoAdapter = new FeaturephotoAdapter(getContext(), featurePhotoList);
        recycle_view_featurephoto.setAdapter(featurephotoAdapter);

        reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    featurePhotoList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        FeaturePhoto f =  snapshot.getValue(FeaturePhoto.class);
                        if (f.getPublisher().equals(profileid)){
                            if (f.getType().equals("feature_photo")){
                                featurePhotoList.add(f);
                            }
                        }
                    }
                    featurephotoAdapter.notifyDataSetChanged();
                }else {
                    notice_text.setVisibility(View.VISIBLE);
                    notice_text.setText("No feature photo available!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recyclerView_saves_activities() {
        recyclerView_saves.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),1);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        recyclerView_saves.setNestedScrollingEnabled(true);
        postList_saves = new ArrayList<>();
        myPhotoAdapter_saves = new PostAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myPhotoAdapter_saves);
    }


    private void recyclerView_post_activities() {
        recyclerView_post.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView_post.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter= new PostAdapter(getContext(), postList);
        recyclerView_post.setAdapter(postAdapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    postList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Post p =  snapshot.getValue(Post.class);
                        if (p.getPublisher().equals(profileid)){
                               postList.add(p);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                    connect.setEnabled(true);
                    edit_profileBtn.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void RemoveNotification(final String id) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

                    Toast.makeText(getContext(), "Unfollowed!", Toast.LENGTH_SHORT).show();

                    checkFollow();
                    getFollowers();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addNofication(String userid){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


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

        Toast.makeText(getContext(), "Followed! ", Toast.LENGTH_SHORT).show();


    }


    private  void getUserInfo(){

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(profileid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                String energyV = dataSnapshot.child("energy").getValue().toString();
                String  coins = dataSnapshot.child("coins").getValue().toString();
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.user_avatar).into(image_profile);
                username.setText(user.getUsername());
                name.setText(user.getName());
                bio.setText(user.getBio());
                location.setText(user.getLocation());
                energy_value.setText(energyV);
                coins_value.setText(coins);

                String number = coins;
                int intcoins = Integer.parseInt(number);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profileBtn.setText("Message");
                    edit_profileBtn.setTag("message");
                    arrowdownBtn.setVisibility(View.VISIBLE);
                    edit_profileBtn.setWidth(40);
                }else {
                    edit_profileBtn.setText("follow");
                    edit_profileBtn.setTag("follow");
                    arrowdownBtn.setVisibility(View.GONE);
                    edit_profileBtn.setWidth(60);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                follower.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("following");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                       if (post.getType().equals("text_post")){
                           i++;
                       }
                    }
                }
                posts_.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void mysaves(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                    readSave();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
    private void readSave() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id: mySaves){
                        if (post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                myPhotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
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
