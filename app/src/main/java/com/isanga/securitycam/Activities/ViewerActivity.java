package com.isanga.securitycam.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.isanga.securitycam.R;


public class ViewerActivity extends AppCompatActivity
{

    public final static String TAG = "ViewerActivity";

    private VideoView videoView;
    private EditText ipText;
    private Uri uri;


    /**
     * initialize the state of the app by creating correct text box interactions and initializing the video
     * The video will take some time to open
     * VLC stream capture-card format H264 + AAC with container TS
     * Add RTSP destination
     * Set the path to /test.ts and port to 8554
     * Configure profile to be MPEG-TS, H-264, and MPEG 4 Audio AAC
     * Click stream
     * @param savedInstanceState bundle containing init info
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewer);

        videoView = (VideoView) findViewById(R.id.videoView);
        uri = Uri.parse(getString(R.string.enter_ip));
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

    /**
     * cleanup the video stream
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.suspend();
    }

    /**
     * queue up a new uri to stream
     * @param uri
     */
    private void updateVideo(Uri uri){
        this.uri = uri;
        videoView.stopPlayback();
        videoView.suspend();
        videoView.setVideoURI(uri);
        videoView.start();
        resizeVideo();
    }

    /**
     * make the drawn surface aspect look correct
     */
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
