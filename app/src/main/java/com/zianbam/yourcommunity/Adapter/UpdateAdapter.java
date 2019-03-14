package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.zianbam.yourcommunity.Model.Update;
import com.zianbam.yourcommunity.R;

import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ViewHolder> {
    public UpdateAdapter(Context mContext, List<Update> mUpdate) {
        this.mContext = mContext;
        this.mUpdate = mUpdate;
    }

    private Context mContext;
    private List<Update> mUpdate;

    private String postid;
    private FirebaseUser firebaseUser;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_item, viewGroup ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Update update = mUpdate.get(i);
        viewHolder.verson.setText(update.getVerson());
        viewHolder.message.setText(update.getMessage());

    }

    @Override
    public int getItemCount() {
        return mUpdate.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        private TextView verson, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            verson = itemView.findViewById(R.id.verson);
            message = itemView.findViewById(R.id.message);

        }
    }
}
