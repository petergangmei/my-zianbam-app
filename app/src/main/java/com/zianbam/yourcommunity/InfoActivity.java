package com.zianbam.yourcommunity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.InfoAdapter;
import com.zianbam.yourcommunity.Model.Info;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
  RecyclerView recyclerView;
  InfoAdapter infoAdapter;
  List<Info> infoList;
  DatabaseReference reference;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);

    Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    toolbar.setTitle("Informations");
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });


    recyclerView = findViewById(R.id.recycle_view_info);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    linearLayoutManager.setReverseLayout(true);
    linearLayoutManager.setStackFromEnd(true);
    infoList = new ArrayList<>();
    recyclerView.setLayoutManager(linearLayoutManager);
    infoAdapter = new InfoAdapter(getApplicationContext(), infoList);
    recyclerView.setAdapter(infoAdapter);

    reference = FirebaseDatabase.getInstance().getReference().child("Informations");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()){
          infoList.clear();
          for (DataSnapshot snapshot:dataSnapshot.getChildren()){
            Info info = snapshot.getValue(Info.class);
            infoList.add(info);
          }
          infoAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }
}
