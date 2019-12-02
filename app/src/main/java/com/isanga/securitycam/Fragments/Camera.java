package com.isanga.securitycam.Fragments;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.isanga.securitycam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Camera extends Fragment {
    private static final String TAG = "CameraFragment";

    /**
     *
     */
    private CameraManager mCameraManager;

    /**
     *
     */
    private Handler mHandler;

    /**
     *
     */
    private CameraDevice mCameraDevice;

    /**
     *
     */
    private String mCameraId;

    /**
     *
     */
    private SurfaceView mSurfaceView;

    /**
     *
     */
    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        boolean secondCall;

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "Surface created");
            mCameraId = null;
            secondCall = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "surfaceChanged");

            if(mCameraId == null) {
                try {
                    for (String cameraId : mCameraManager.getCameraIdList()) {
                        if (mCameraManager.getCameraCharacteristics(cameraId).get(mCameraManager.getCameraCharacteristics(cameraId).LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                            mCameraId = cameraId;
                            surfaceHolder.setFixedSize(400, 400);
                            Log.d(TAG, "Found back facing camera");
                            return;
                        }
                    }
                } catch (CameraAccessException e) {
                    Log.d(TAG, "Could not find or access the camera");
                }
            } else if(secondCall == false) {
                try {
                    Log.d(TAG, "Trying to open camera");
                    mCameraManager.openCamera(mCameraId, mStateCallback, mHandler);
                    Log.d(TAG, "Camera was opened");
                } catch (CameraAccessException e) {
                    Log.d(TAG, "Could not find or access camera");
                } catch (SecurityException e) {
                    Log.d(TAG, "Found some security exception while opening camera");
                }
                secondCall = true;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            surfaceHolder.removeCallback(this);
            Log.d(TAG, "Surface holder callback removed");
        }
    };

    /**
     *
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "Camera onOpened");
            mCameraDevice = cameraDevice;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    public Camera() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mHandler = new Handler(Looper.getMainLooper());
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        View layout = getLayoutInflater().inflate(R.layout.fragment_camera, null);
        mSurfaceView = layout.findViewById(R.id.camera_surface_view);
        mSurfaceView.getHolder().addCallback(mCallback);

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_camera, container, false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "Camera closed");
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

}
