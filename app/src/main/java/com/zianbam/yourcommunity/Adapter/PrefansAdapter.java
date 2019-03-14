package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zianbam.yourcommunity.R;

public class PrefansAdapter extends RecyclerView.Adapter<PrefansAdapter.ViewHolder> {

    private int myin = 0;
    public PrefansAdapter(Context mContext) {
        this.mContext = mContext;

    }

    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pref_items, viewGroup, false);
        return new PrefansAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Qprefs");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                        String p = snapshot.getKey();
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Qprefs").child(p).child("answers");
//                        ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()){
//                                Prefans prefans = dataSnapshot.getValue(Prefans.class);
//                                Toast.makeText(mContext, "data: "+prefans.getAnswer1() , Toast.LENGTH_SHORT).show();
//                                myin ++;
//                            }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public Button btnAnswer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAnswer = itemView.findViewById(R.id.answerBtn);
        }
    }
}
