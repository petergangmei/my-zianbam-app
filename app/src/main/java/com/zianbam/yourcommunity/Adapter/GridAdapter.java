package com.zianbam.yourcommunity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zianbam.yourcommunity.Model.GridModel;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<GridModel> gridModels;

    public GridAdapter(Context context, ArrayList<GridModel> gridModels) {
        this.context = context;
        this.gridModels = gridModels;
    }

    @Override
    public int getCount() {
        return gridModels.size();
    }

    @Override
    public Object getItem(int position) {
        return gridModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
        }
        convertView  = LayoutInflater.from(context).inflate(R.layout.layout_grid_image_view, parent, false);

        final GridModel g = (GridModel) this.getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gridimageview);
        ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.gridview_progressbar);
//        Picasso.get().load(g.getUri()).placeholder(R.drawable.image_placeholder)
//                .into(imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clickeD!", Toast.LENGTH_SHORT).show();
            }
        });
    return convertView;
    }

}
