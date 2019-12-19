package com.isanga.securitycam.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.isanga.securitycam.R;

import java.io.IOException;


public class ViewerActivity extends AppCompatActivity
{

    public final static String TAG = "ViewerActivity";

    private VideoView videoView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewer);

        videoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("rtsp://192.168.15.140:8554/test.ts");
        try {
            videoView.setVideoURI(uri);
            videoView.start();
        }catch (Exception ex ){

        }

    }



}
