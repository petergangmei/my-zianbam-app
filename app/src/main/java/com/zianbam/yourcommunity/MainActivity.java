package com.zianbam.yourcommunity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Fragment.APIService;
import com.zianbam.yourcommunity.Fragment.AccountFragment;
import com.zianbam.yourcommunity.Fragment.HomeFragment;
import com.zianbam.yourcommunity.Fragment.NotificationFragment;
import com.zianbam.yourcommunity.Fragment.SearchFragment;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Notifications.Client;
import com.zianbam.yourcommunity.Notifications.Data;
import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;
import com.zianbam.yourcommunity.Notifications.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference reference, ref;
    List<String> notificationlist;
    BottomNavigationView bottomNavigationView;
    private  String versonCode = "1.9";
    private Menu menu;
    private static int SPLAST_TIME_OUT = 2000;
    APIService apiService;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        checkPref();
        checkUpdates();
        rechargeEnergy();
        checkSubcription();



        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //    Fragment managment here
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        final HomeFragment homeFragment = new HomeFragment();
        final NotificationFragment notificationFragment = new NotificationFragment();
        final AccountFragment accountFragment = new AccountFragment();
        final SearchFragment searchFragment = new SearchFragment();



        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    startActivity(new Intent(MainActivity.this, AccountSetupActivity.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.home){
                    setFragment(homeFragment);
                    return true;
                }else if (id == R.id.search){
                    setFragment(searchFragment);
                    return true;
                }else if (id == R.id.account){
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid",mAuth.getCurrentUser().getUid());
                    editor.apply();
                    setFragment(accountFragment);
                    return true;
                }
                else if (id == R.id.notification){
                    setFragment(notificationFragment);
                    return true;
                }else if (id == R.id.post){
                    Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                intent.putExtra("post_type", "new");
                intent.putExtra("post_id", "null");
                    startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
                }

                return false;

            }
        });


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationlist = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    notificationlist.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Notification notification = snapshot.getValue(Notification.class);
                        notificationlist.add(snapshot.getKey());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(snapshot.getKey());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String isseen = dataSnapshot.child("isseen").getValue().toString();
                                    if (isseen.equals("false")){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Menu menu = bottomNavigationView.getMenu();
                                                menu.findItem(R.id.notification).setIcon(R.drawable.red_notification);

                                            }
                                        }, SPLAST_TIME_OUT);
                                    }else {
                                        Menu menu = bottomNavigationView.getMenu();
                                        menu.findItem(R.id.notification).setIcon(R.drawable.ic_notification);
                                    }


                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bottomNavigationView.setItemIconSize(80);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void checkSubcription() {
        MobileAds.initialize(this, "ca-app-pub-2148730556114390~4320451757");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2148730556114390/2411408050");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
                super.onAdLoaded();
            }
        });
        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//                    String subscription = dataSnapshot.child("subscribeBtn").getValue().toString();
//                    Integer result = Integer.valueOf(subscription);
//                    Long currentime = System.currentTimeMillis();
//                    if (currentime > result){
//
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void rechargeEnergy() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
//                    String recharge = dataSnapshot.child("energycharge").getValue().toString();
                     long timestamp = System.currentTimeMillis()+ 3600000;//1hours
                     long currentime = System.currentTimeMillis();

                     Log.d("tag", "time end : "+user.getEnergycharge() + "current time "+currentime);
                    if (user.getEnergycharge() <= currentime){

                        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("energy", "10");
                        hashMap.put("energycharge", timestamp);
                        ref.updateChildren(hashMap);
                        sendNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getName(), "Message");
                    }
                }else {
                    Toast.makeText(MainActivity.this, "data does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkPref() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getPref().equals("none")){
                        startActivity(new Intent(getApplicationContext(), SetupPrefereceActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "base-root");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
//
       Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);

//        Toast.makeText(this, "backpressed"+fragment.getTag().toLowerCase(), Toast.LENGTH_SHORT).show();

        switch (fragment.getTag()){
            case "base-root":
                exitAppPermission();
                break;
            case "home-profile":
                go2HomeFragment();
                break;

            case "search-profile":
                go2SearchFragment();
                break;


            case "account-menu":
                go2AccountFragment();
                break;

            case "home->likes":
                go2HomeFragment();
                break;

            case "profile-followers":
                go2AccountFragment();
                break;

            case "profile-following":
                go2AccountFragment();
                break;
            case  "notification-profile":
                go2NotificationFragment();
                break;

        }

    }

    private void go2NotificationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new NotificationFragment(), "base-root");
        fragmentTransaction.commit();
    }

    private void go2AccountFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new AccountFragment(), "base-root");
        fragmentTransaction.commit();
    }

    private void go2SearchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new SearchFragment(), "base-root");
        fragmentTransaction.commit();
    }

    private void go2HomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new  HomeFragment(), "base-root");
        fragmentTransaction.commit();
    }

    private void exitAppPermission(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you really want to do this?");
        builder.setTitle("Exit Zianbam");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes, exit!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void  checkUpdates(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Update");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final String verson = dataSnapshot.child("verson").getValue().toString();
                    String level = dataSnapshot.child("level").getValue().toString();
                    if (!verson.equals(versonCode)){
                        if (level.equals("easy")){
                            Log.d("tag", "easy");
                             Dialog dialog = new Dialog(MainActivity.this);
//                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.update_dialog);
                            Button updatenow_Btn = dialog.findViewById(R.id.updatenowBtn);
                            final TextView updateverson= dialog.findViewById(R.id.verson);
                            final TextView updatemessage= dialog.findViewById(R.id.message);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Update");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        String newverson = dataSnapshot.child("verson").getValue().toString();
                                        String message = dataSnapshot.child("message").getValue().toString();
                                        updateverson.setText("verson: v."+newverson);
                                        updatemessage.setText(message);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            updatenow_Btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zianbam.yourcommunity"));
                                    startActivity(intent);
                                }
                            });
                            dialog.show();

                        } else if (level.equals("required")) {
                            Log.d("tag", "required");
                            startActivity(new Intent(getApplicationContext(), UpdateAvailableActivity.class));
                            finish();


                        }
                        Log.d("tag", "Required update");
                    }else {
                        Log.d("tag", "Does not update");

                    }


                }else {
                    Log.e("tag", "does not extis");
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
        final String msgs = "10 energy bar has been restored to your account! ";
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(publisher);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher, ""+"   "+msgs+" ", "Energy refilled",
                                FirebaseAuth.getInstance().getCurrentUser().getUid());
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
