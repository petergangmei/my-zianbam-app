package com.zianbam.yourcommunity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.UpdateAdapter;
import com.zianbam.yourcommunity.Model.Update;

import java.util.List;

public class UpdateAvailableActivity extends AppCompatActivity {
    private Button updatenow;
    private TextView updatetext, updateverson;
    private RecyclerView recyclerView;
    private List<Update> updateList;
    private UpdateAdapter updateAdapter;

    private static String versonCode = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_available);


        updatetext = findViewById(R.id.message);
        updatenow = findViewById(R.id.updatenow);
        updateverson = findViewById(R.id.verson);




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Update");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String verson = dataSnapshot.child("verson").getValue().toString();
                    String message = dataSnapshot.child("message").getValue().toString();
                    updateverson.setText("verson: v."+verson );
                    updatetext.setText(message);

                }else {
                    Log.d("tag", "data doesn't exist");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updatenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updatenow.setEnabled(false);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zianbam.yourcommunity"));
                startActivity(intent);
                finish();
                moveTaskToBack(true);



            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
