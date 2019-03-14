package com.zianbam.yourcommunity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Model.User;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class RewardedVideoActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private RewardedVideoAd mAd;
    ProgressBar pb, pc;
    TextView mlog;
    private TextView Zcoins, rzcoins, rewardedVideoReady, subscribeduntill;
    DatabaseReference reference, ref;
    ImageView playBtn ,close;
    AlertDialog alertDialog;
    private ImageView checked, video;
    ScrollView scrollView;
    LinearLayout clickVIP;
    Button subscribeBtn;
    ListView listView;


    String adsid = "ca-app-pub-2148730556114390/8231120063";
    String id = "ca-app-pub-2148730556114390~4320451757";

//    ca-app-pub-2148730556114390/8231120063
    //test id ca-app-pub-3940256099942544/5224354917
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_video);
        playBtn = findViewById(R.id.play_rewardvideo);
        pb = findViewById(R.id.progress_circular);
        pc = findViewById(R.id.pc);
        mlog = findViewById(R.id.mlog);
        Zcoins = findViewById(R.id.yourZcoins);
        rzcoins = findViewById(R.id.r_zcoins);
        close = findViewById(R.id.close);
        rewardedVideoReady = findViewById(R.id.rewardedVideoReady);
        checked = findViewById(R.id.checked);
        scrollView = findViewById(R.id.scoll);
        clickVIP = findViewById(R.id.clickVIP);
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        subscribeBtn = findViewById(R.id.subscribeBtn);
        subscribeduntill = findViewById(R.id.subscribeduntill);



        subscribeBtn.setEnabled(false);


        checksubscription();
       refreshcoins();


       
       subscribeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final  ProgressDialog pd;
               pd = new ProgressDialog(RewardedVideoActivity.this);
               pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               pd.setMessage("Please wait...");
               pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
               pd.setIndeterminate(false);
               pd.show();



               reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
               Long t1 = System.currentTimeMillis() + 864000000;
               Long t2 = t1 + 864000000;
               Long t3 = t2 + 864000000;

                                   Calendar c=new GregorianCalendar();
                    c.add(Calendar.DATE, 30);
                    Date d=c.getTime();


               Timestamp ts=new Timestamp(d.getTime());




               HashMap<String, Object> hashMap = new HashMap<>();
               hashMap.put("subscription", t1);
               reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       checksubscription();
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               pd.dismiss();
                               final Dialog dialog = new Dialog(RewardedVideoActivity.this);
                               dialog.setContentView(R.layout.dialog_subcribed);
                               dialog.show();
                           }
                       },1500);

                   }
               });


           }
       });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


       clickVIP.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               scrollView.postDelayed(new Runnable() {
                   public void run() {
                       scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                   }
               },100L);
           }
       });


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAd.isLoaded()){
                    mAd.show();
                    final  ProgressDialog pd;
                    pd = new ProgressDialog(RewardedVideoActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Please wait...");
                    pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                    pd.setIndeterminate(false);
                    pd.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    },2500);
                }else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RewardedVideoActivity.this);
                    builder.setMessage("Preparing ads please wait..");
                    builder.setTitle("Warrning!");
                    builder.setCancelable(true);
                     alertDialog = builder.create();
                    alertDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                        }
                    },1500);

                }



            }
        });

    }

    private void checksubscription() {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
//                    Timestamp ts =new Timestamp(user.getSubscription());

//                    Date date = new Date(ts.getTime());
                    long d = user.getSubscription();

//                    Long d = user.getSubscription();
//                    Calendar c=new GregorianCalendar();
//                    c.add(Calendar.DATE, 30);
//                    Date d=c.getTime();


                    Timestamp ts=new Timestamp(d);

                    Date date = new Date();
                    Timestamp ts2 = new Timestamp(date.getTime());

                    if (ts.compareTo(ts2)<0){
                    }else {
                        subscribeBtn.setText("Subcribed");
                        subscribeBtn.setEnabled(false);
                        subscribeduntill.setText("Subscribed until "+ts);
                    }

//                    if (user.getSubscription() < System.currentTimeMillis()){
//                        subscribeBtn.setEnabled(true);
//                    }else {
//                        subscribeBtn.setText("Subcribed");
//                        subscribeBtn.setEnabled(false);
//                        subscribeduntill.setText("Subscribed until "+ts);
//                    }
                    


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareAds() {
        MobileAds.initialize(this, id);
        mAd.loadAd(adsid,new AdRequest.Builder().build());
    }

    private void refreshcoins(){
        prepareAds();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String coins = dataSnapshot.child("coins").getValue().toString();
                    Zcoins.setText(coins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        checked.setVisibility(View.VISIBLE);
        pc.setVisibility(View.GONE);
        rewardedVideoReady.setText("[ Rewarded Video Ready ]");
        playBtn.setImageResource(R.drawable.videoplayer);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {
        rewardedVideoReady.setText("[ Searching Ads Video ]");
        checked.setVisibility(View.GONE);
        playBtn.setImageResource(R.drawable.playvideo);
        pc.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRewardedVideoAdClosed() {
        refreshcoins();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String number = dataSnapshot.child("coins").getValue().toString();
                    int ncoins = Integer.parseInt(number);
                    int newcoins = ncoins + 1;
                    ref = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("coins", newcoins);
                    ref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final Dialog dialog = new Dialog(RewardedVideoActivity.this);
                                    dialog.setContentView(R.layout.rewarded_dialog);
                                    TextView rzcoins = dialog.findViewById(R.id.r_zcoins);
//                Button btn = dialog.findViewById(R.id.dontshow);
                                    rzcoins.setText("1 Zcoins");
                                    final int option = 1;
                                    refreshcoins();

                                    dialog.show();
                                }
                            },1500);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "Faild to load ads! Please try again after sometime", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoCompleted() {


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final Dialog dialog = new Dialog(RewardedVideoActivity.this);
//                dialog.setContentView(R.layout.rewarded_dialog);
//                TextView rzcoins = dialog.findViewById(R.id.r_zcoins);
////                Button btn = dialog.findViewById(R.id.dontshow);
//                rzcoins.setText("1 Zcoins");
//                final int option = 1;
//                refreshcoins();
//
//                dialog.show();
//            }
//        },1500);

    }


    public void playrewardvideo(View view) {

    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }
}
