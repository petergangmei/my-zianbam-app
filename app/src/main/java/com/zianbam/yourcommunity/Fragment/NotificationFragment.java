package com.zianbam.yourcommunity.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.NotificationAdapter;
import com.zianbam.yourcommunity.Model.Notification;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class NotificationFragment extends Fragment {
    private  RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationsList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private  ArrayList<String> userList;
    APIService apiService;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);


        recyclerView= v.findViewById(R.id.recycle_view_notifications);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationsList = new ArrayList<>();
        notificationsList.clear();
        notificationAdapter = new NotificationAdapter(getContext(), notificationsList);
        recyclerView.setAdapter(notificationAdapter);


            notificationsList.clear();
            readNotification();
        return v;
    }

    @Override
    public void onResume() {

        notificationsList.clear();
        readNotification();
        super.onResume();
    }

    private void readNotification() {

        notificationsList.clear();final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.keepSynced(true);
        Query query = reference.limitToLast(25);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();
               if (dataSnapshot.exists()){
                   for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                       Notification notification = snapshot.getValue(Notification.class);
                       notificationsList.add(notification);
                     reference =  FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).child(notification.getNotificationid());
                       HashMap<String, Object> hashMap = new HashMap<>();
                       hashMap.put("isseen", "true");
                       reference.updateChildren(hashMap);

                   }
                   Collections.reverse(notificationsList);
                   notificationAdapter.notifyDataSetChanged();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
