package com.isanga.securitycam.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isanga.securitycam.Adapters.ClipsRecyclerViewAdapter;
import com.isanga.securitycam.Models.ClipsModel;
import com.isanga.securitycam.R;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Clips extends Fragment {

    private RecyclerView recyclerView;
    //Holds a list of clips
    private ArrayList<ClipsModel> models;
    private RecyclerView.LayoutManager manager;
    private ClipsRecyclerViewAdapter adapter;

    public Clips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clips, container, false);
        setUpRecyclerView(view);
        populateList();
        return view;

    }

    /**
     * Sets up the recycler view
     * @param view
     */
    public void setUpRecyclerView(View view){
        recyclerView = view.findViewById(R.id.clips_recyclerview);
        models = new ArrayList<>();
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ClipsRecyclerViewAdapter(getContext(), models);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Scans folder and shows list of clips
     *
     */
    public void populateList(){
        File folder = getContext().getExternalFilesDir("SecurityCam");
        File[] files = folder.listFiles();
        for(File file: files){
            String filename = file.getName();
            String strippedExtension = filename.substring(0, filename.lastIndexOf('.'));
            models.add(new ClipsModel(strippedExtension));
        }
        adapter.notifyDataSetChanged();
    }

}
