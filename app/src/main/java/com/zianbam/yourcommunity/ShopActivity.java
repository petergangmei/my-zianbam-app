package com.zianbam.yourcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShopActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private TextView Zcoins, rzcoins;
    private Button op1, op2, op3;
    private DatabaseReference ref;
    ImageView play_rewardvideo;
    BillingProcessor bp;
    int time = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        rzcoins = findViewById(R.id.r_zcoins);
        Zcoins = findViewById(R.id.yourZcoins);
        op1 = findViewById(R.id.op1);
        op2 = findViewById(R.id.op2);
        op3 = findViewById(R.id.op3);
        play_rewardvideo = findViewById(R.id.play_rewardvideo);




        refreshcoins();
        onclicks();
    }

    @Override
    protected void onRestart() {
        refreshcoins();



        super.onRestart();
    }

    private void onclicks() {
        play_rewardvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShopActivity.this, RewardedVideoActivity.class);
                startActivity(intent);
            }
        });

        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(ShopActivity.this, "android.test.purchased");
            }
        });
    }
    private void refreshcoins(){
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

        bp = new BillingProcessor(this, null, this);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, "Purchase successfull!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void playrewardvideo(View view) {

    }
}
