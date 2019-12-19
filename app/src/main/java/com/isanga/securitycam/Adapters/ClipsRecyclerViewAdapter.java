package com.isanga.securitycam.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        File file = models.get(position).getThumbnail();
        Glide.with(context)
               .load(Uri.fromFile(file))
                .thumbnail(0.1f)
                .centerCrop()
                .into(holder.clipThumbnail);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {
        ImageView clipThumbnail;
        ClipsRecyclerViewListener listener;

        public ViewHolder(@NonNull View itemView, ClipsRecyclerViewListener listener) {
            super(itemView);
            clipThumbnail = itemView.findViewById(R.id.clip_thumbnail);
            itemView.setOnClickListener(this);
            this.listener = listener;
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }



        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), R.id.clip_delete, 0, R.string.clip_delete);
            contextMenu.add(this.getAdapterPosition(), R.id.clip_share, 0, R.string.clip_share);
            contextMenu.add(this.getAdapterPosition(), R.id.clip_edit, 0, "Edit");
        }


    }

    public interface ClipsRecyclerViewListener{
        void onItemClick(int position);
    }
}
