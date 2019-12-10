package com.isanga.securitycam.Fragments;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.isanga.securitycam.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Camera extends Fragment {
    private static final String TAG = "CameraFragment";

    /**
     * The camera manager. Used to find the camera.
     */
    private CameraManager mCameraManager;

    /**
     * Handler to use for camera processes. Required by openCamera and createCaptureSession.
     * TODO We may need another handler for both processes.
     */
    private Handler mHandler;

    /**
     * The camera device.
     */
    private CameraDevice mCameraDevice;

    /**
     * The id of the camera.
     */
    private String mCameraId;

    /**
     * View used to display the camera preview.
     */
    private SurfaceView mSurfaceView;

    /**
     * The surface view holder.
     */
    private SurfaceHolder mSurfaceHolder;

    /**
     * Record button.
     */
    private Button record;

    /**
     * Stop recording button.
     */
    private Button stop;

    /**
     *
     */
    MediaRecorder mediaRecorder;

    /**
     * Implement the logic for surface holder callbacks.
     */
    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        //to avoid constantly opening the camera
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

            //Find the back facing camera.
            if(mCameraId == null) {
                try {
                    for (String cameraId : mCameraManager.getCameraIdList()) {
                        if (mCameraManager.getCameraCharacteristics(cameraId).get(mCameraManager.getCameraCharacteristics(cameraId).LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                            mCameraId = cameraId;
                            SurfaceHolder holder = mSurfaceView.getHolder();
                            DisplayMetrics metrics = new DisplayMetrics();
                            ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            holder.setFixedSize(metrics.heightPixels, metrics.widthPixels);
                            Log.d(TAG, "Found back facing camera");
                            return;
                        }
                    }
                } catch (CameraAccessException e) {
                    Log.d(TAG, "Could not find or access the camera");
                }
            } else if(secondCall == false) {
                try {
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
            Log.d(TAG, "Surface destroyed");
            surfaceHolder.removeCallback(this);
        }
    };

    /**
     * Implementation of callbacks for the camera device.
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "Camera onOpened");
            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(mSurfaceView.getHolder().getSurface());
            try {
                cameraDevice.createCaptureSession(surfaceList, mCaptureSession, mHandler);
            } catch(CameraAccessException e) {

            }
            mCameraDevice = cameraDevice;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "Camera onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Log.d(TAG, "Camera onError");
        }
    };

    /**
     * Capture session for our camera device to display the preview on our surface.
     */
    CameraCaptureSession mCameraCaptureSession;

    /**
     * Listener for the session ready/not ready cause the documentation mentions that creating a capture session is an expensive operation.
     */
    private CameraCaptureSession.StateCallback mCaptureSession = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            Log.d(TAG, "Camera configured");
            mCameraCaptureSession = cameraCaptureSession;
            if(mSurfaceHolder != null) {
                try {
                    CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(mCameraDevice.TEMPLATE_PREVIEW);
                    captureRequest.addTarget(mSurfaceHolder.getSurface());
                    captureRequest.build();
                    cameraCaptureSession.setRepeatingRequest(captureRequest.build(), null, null);
                } catch(CameraAccessException e) {

                }
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Log.d(TAG, "Camera failed configuration");
        }
    };

    /**
     * Required empty public constructor.
     */
    public Camera() {
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mHandler = new Handler();
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_camera, container, false);
        mSurfaceView = layout.findViewById(R.id.camera_surface_view);
        mSurfaceView.getHolder().addCallback(mCallback);
        mSurfaceHolder = mSurfaceView.getHolder();
        mediaRecorder = new MediaRecorder();
        record = layout.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                File folder = getContext().getExternalFilesDir("media");
                mediaRecorder.setOutputFile(folder);
                try {
                    mediaRecorder.prepare();
                } catch(IOException e) {
                    Log.d(TAG, "Media recorder prepare failed with IOException");
                }
                mediaRecorder.start();
            }
        });
        stop = layout.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            }
        });

        return layout;
    }

    /**
     * {@inheritDoc}
     */
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
