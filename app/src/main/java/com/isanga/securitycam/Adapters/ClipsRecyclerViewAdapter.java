package com.isanga.securitycam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isanga.securitycam.Models.ClipsModel;
import com.isanga.securitycam.R;

import java.util.ArrayList;

public class ClipsRecyclerViewAdapter extends RecyclerView.Adapter<ClipsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ClipsModel> models;
    private LayoutInflater layoutInflater;
    public ClipsRecyclerViewAdapter(Context context, ArrayList<ClipsModel> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.models = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= layoutInflater.inflate(R.layout.clip_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.clipTitle.setText(models.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView clipTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clipTitle = itemView.findViewById(R.id.clip_title);
        }
    }
}
