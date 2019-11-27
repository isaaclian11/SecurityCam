package com.isanga.securitycam.Fragments;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private static final int REQ_READ_STORAGE = 0;

    public Clips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clips, container, false);
        setUpRecyclerView(view);
        loadThumbnails();
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = models.get(position).getThumbnail();
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void loadThumbnails(){
        File folder = getContext().getExternalFilesDir("media");
        String path = folder.getAbsolutePath();
        Log.d(TAG, "loadThumbnails: " + path);
        if(folder.exists()){
            File[] videos = folder.listFiles();
            for(File video: videos){
                models.add(new ClipsModel(video.getName(), video));
            }
        }
        else{
            folder.mkdirs();
        }

    }
}
