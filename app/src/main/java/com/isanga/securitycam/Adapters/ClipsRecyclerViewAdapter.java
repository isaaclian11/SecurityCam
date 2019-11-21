package com.isanga.securitycam.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.isanga.securitycam.Models.ClipsModel;
import com.isanga.securitycam.R;

import java.io.File;
import java.util.ArrayList;

public class ClipsRecyclerViewAdapter extends RecyclerView.Adapter<ClipsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ClipsModel> models;
    private LayoutInflater layoutInflater;
    private ClipsRecyclerViewListener listener;
    private Context context;

    public ClipsRecyclerViewAdapter(Context context, ArrayList<ClipsModel> list, ClipsRecyclerViewListener listener){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.models = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= layoutInflater.inflate(R.layout.clip_item_row, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.clipTitle.setText(models.get(position).getTitle());
        File file = models.get(position).getThumbnail();
        Glide.with(context)
                .asBitmap().load(Uri.fromFile(file))
                .override(500, 500)
                .into(holder.clipThumbnail);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView clipTitle;
        ImageView clipThumbnail;
        ClipsRecyclerViewListener listener;

        public ViewHolder(@NonNull View itemView, ClipsRecyclerViewListener listener) {
            super(itemView);
            clipTitle = itemView.findViewById(R.id.clip_title);
            clipThumbnail = itemView.findViewById(R.id.clip_thumbnail);
            itemView.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface ClipsRecyclerViewListener{
        void onItemClick(int position);
    }

}
