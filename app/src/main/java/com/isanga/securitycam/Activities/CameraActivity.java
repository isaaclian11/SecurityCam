package com.isanga.securitycam.Activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.isanga.securitycam.R;

public class CameraActivity extends Activity {
    static final String TAG = "CameraActivity";

    CameraManager mCameraManager;

    SurfaceView mSurfaceView;

    SurfaceHolder mSurfaceHolder;

    @Override
    protected void onResume() {
        super.onResume();

        mCameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        View layout = getLayoutInflater().inflate(R.layout.fragment_camera, null);
        mSurfaceView = (SurfaceView) layout.findViewById(R.id.camera_surface_view);

        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        setContentView(layout.findViewById(R.id.camera_relative_layout));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
