package com.isanga.securitycam.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.isanga.securitycam.R;

import java.io.IOException;
import java.net.URI;


public class ViewerActivity extends AppCompatActivity
{

    public final static String TAG = "ViewerActivity";

    private VideoView videoView;
    private EditText ipText;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewer);

        videoView = (VideoView) findViewById(R.id.videoView);
        uri = Uri.parse("rtsp://10.26.40.54:8554/test.ts");
        updateVideo(uri);


        ipText = (EditText) findViewById(R.id.editTextIP);

        ipText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Uri fresh = Uri.parse(textView.getText().toString());
                    if (fresh != null) {
                        updateVideo(fresh);
                        Log.d(TAG, "onEditorAction: " + fresh.toString());
                    } else {
                        Toast.makeText(ViewerActivity.this, "Invalid URI", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.suspend();
    }

    private void updateVideo(Uri uri){
        this.uri = uri;
        videoView.stopPlayback();
        videoView.suspend();
        videoView.setVideoURI(uri);
        videoView.start();
        resizeVideo();
    }

    private void resizeVideo(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);
    }



}
