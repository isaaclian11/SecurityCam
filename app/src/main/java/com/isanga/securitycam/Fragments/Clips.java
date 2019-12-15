package com.isanga.securitycam.Fragments;



import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isanga.securitycam.Adapters.ClipsRecyclerViewAdapter;
import com.isanga.securitycam.Models.ClipsModel;
import com.isanga.securitycam.R;

import java.io.File;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Clips extends Fragment implements ClipsRecyclerViewAdapter.ClipsRecyclerViewListener {

    private RecyclerView recyclerView;
    //Holds a list of clips
    private ArrayList<ClipsModel> models;
    private RecyclerView.LayoutManager manager;
    private ClipsRecyclerViewAdapter adapter;

    private File folder;

    public Clips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clips, container, false);
        folder = getContext().getExternalFilesDir("media");
        if(savedInstanceState==null) {
            setUpRecyclerView(view);
            loadThumbnails();
        }
        registerForContextMenu(recyclerView);
        return view;

    }

    /**
     * Sets up the recycler view
     * @param view
     */
    private void setUpRecyclerView(View view){
        recyclerView = view.findViewById(R.id.clips_recyclerview);
        models = new ArrayList<>();
        manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);
        adapter = new ClipsRecyclerViewAdapter(getContext(), models, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles click event on items from the list
     * @param position position of the item being clicked
     */
    @Override
    public void onItemClick(int position) {
        //If video does not have a default player, prompt apps that support video playing
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = models.get(position).getThumbnail();
        //FileProvider is need on sdk targets bigger than 23
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clip_delete:
                deleteClip(item.getGroupId());
            case R.id.clip_share:
                shareClip(item.getGroupId());
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void deleteClip(int id){
        String path = folder.getAbsolutePath() + "/" + models.get(id).getTitle();
        File video = new File(path);
        video.delete();
        models.remove(id);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "deleteClip: " + path);
    }

    private void shareClip(int id){
        String path = folder.getAbsolutePath() + "/" + models.get(id).getTitle();
        File video = new File(path);
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", video);
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "send"));
    }

    /**
     * Loads thumbnails from media folder
     */
    private void loadThumbnails(){
        String path = folder.getAbsolutePath();
        Log.d(TAG, "loadThumbnails: " + path);
        if(folder.exists()){
            File[] videos = folder.listFiles();
            if(videos!=null) {
                for (File video : videos) {
                    Log.d(TAG, "currentThumbnail: " + video.getAbsolutePath());
                    models.add(new ClipsModel(video.getName(), video));
                }
            }
        }
        else{
            folder.mkdirs();
        }

    }
}
