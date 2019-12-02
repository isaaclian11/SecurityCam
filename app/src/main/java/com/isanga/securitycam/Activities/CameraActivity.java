package com.isanga.securitycam.Activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.isanga.securitycam.R;

public class CameraActivity extends Activity {
    private static final String TAG = "CameraActivity";

    private CameraManager mCameraManager;

    private SurfaceView mSurfaceView;

    private SurfaceHolder mSurfaceHolder;

    private Handler mHandler;

    private CameraDevice mCameraDevice;

    private String mCameraId;

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mCameraId = null;
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            try {
                for (String cameraId : mCameraManager.getCameraIdList()) {
                    if (mCameraManager.getCameraCharacteristics(cameraId).get(mCameraManager.getCameraCharacteristics(cameraId).LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraId = cameraId;
                        Log.d(TAG, "Got back facing camera");
                        return;
                    }
                }
            } catch(CameraAccessException e) {
                Log.d(TAG, "Could not find or access the camera");
            }

            try {
                mCameraManager.openCamera(mCameraId, mStateCallback, mHandler);
                Log.d(TAG, "Camera was opened");
            } catch(CameraAccessException e) {
                Log.d(TAG, "Could not find or access camera");
            } catch(SecurityException e) {
                Log.d(TAG, "Found some security exception while opening camera");
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            surfaceHolder.removeCallback(this);
            Log.d(TAG, "Surface holder callback removed");
        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mHandler = new Handler(getMainLooper());
        mCameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        View layout = getLayoutInflater().inflate(R.layout.fragment_camera, null);
        mSurfaceView = (SurfaceView) layout.findViewById(R.id.camera_surface_view);
        mSurfaceView.getHolder().addCallback(mCallback);

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

        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

}
