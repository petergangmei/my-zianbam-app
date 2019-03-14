package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zianbam.yourcommunity.Model.Info;
import com.zianbam.yourcommunity.R;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
  private Context mContext;
  private List<Info> infoList;

  public InfoAdapter(Context mContext, List<Info> infoList) {
    this.mContext = mContext;
    this.infoList = infoList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.info_item,viewGroup, false);
    return new InfoAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    Info info = infoList.get(i);
    viewHolder.title.setText(info.getTitle());
    viewHolder.information.setText(info.getInfo());
  }

  @Override
  public int getItemCount() {
    return infoList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder{
    TextView title, information;
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.title);
      information = itemView.findViewById(R.id.info);

    }
  }
}
