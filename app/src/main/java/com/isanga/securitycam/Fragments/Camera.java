package com.isanga.securitycam.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static int MY_PERMISSIONS_REQUEST_CAMERA;
    private static int MY_PERMISSIONS_REQUEST_AUDIO;
    private static int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE;

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
     * Image reader to access image data rendered to surface.
     */
    private ImageReader imageReader;

    /**
     * Record button.
     */
    private Button record;

    /**
     * Stop recording button.
     */
    private Button stop;

    /**
     * Does not work on emulator. Used to record video and audio.
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
            if (mCameraId == null) {
                try {
                    for (String cameraId : mCameraManager.getCameraIdList()) {
                        if (mCameraManager.getCameraCharacteristics(cameraId).get(mCameraManager.getCameraCharacteristics(cameraId).LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                            mCameraId = cameraId;
                            DisplayMetrics metrics = new DisplayMetrics();
                            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            mSurfaceView.getHolder().setFixedSize(metrics.heightPixels, metrics.widthPixels);
                            imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, ImageFormat.JPEG, 1);
                            Log.d(TAG, "Found back facing camera");
                            return;
                        }
                    }
                } catch (CameraAccessException e) {
                    Log.d(TAG, "Could not find or access the camera");
                }
            } else if (secondCall == false) {
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

            setMediaRecorder();
            File folder = new File(getContext().getExternalFilesDir(null), "media");
            if (!folder.exists()) {
                folder.mkdir();
            } else if (!folder.isDirectory() && folder.canWrite()) {
                folder.delete();
                folder.mkdir();
            }
            Log.d(TAG, "" + folder);
            try {
                File name = File.createTempFile("Recording_", ".mp4", folder);
                mediaRecorder.setOutputFile(name.getAbsolutePath());
                Log.d(TAG, "" + name);
            } catch (IOException e) {
                Log.e(TAG, "Failed to create video file name: " + e);
            }
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "Media recorder prepare failed with " + e);
            }

            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(mSurfaceView.getHolder().getSurface());
            surfaceList.add(mediaRecorder.getSurface());
            surfaceList.add(imageReader.getSurface());
            try {
                cameraDevice.createCaptureSession(surfaceList, mCaptureSession, mHandler);
            } catch (CameraAccessException e) {
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
            if (mSurfaceHolder != null) {
                try {
                    CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(mCameraDevice.TEMPLATE_RECORD);
                    captureRequest.addTarget(mSurfaceHolder.getSurface());
                    captureRequest.addTarget(mediaRecorder.getSurface());
                    captureRequest.build();
                    cameraCaptureSession.setRepeatingRequest(captureRequest.build(), null, null);
                } catch (CameraAccessException e) {

                }
            }

            mCameraCaptureSession = cameraCaptureSession;
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
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        checkPermissions();
        mHandler = new Handler();
        mediaRecorder = new MediaRecorder();
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_camera, container, false);
        setSurface(layout);
        //start recording
        record = layout.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Starting media recorder");
                mediaRecorder.start();
            }
        });
        //stop recording
        stop = layout.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Stopping media recorder");

                try {
                    mCameraCaptureSession.stopRepeating();
                    mCameraCaptureSession.abortCaptures();
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Can't access camera to stop camera capture session: " + e);
                }

                mediaRecorder.stop();

                mCaptureSession = new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        mCameraCaptureSession = cameraCaptureSession;
                        if (mSurfaceHolder != null) {
                            try {
                                CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(mCameraDevice.TEMPLATE_RECORD);
                                captureRequest.addTarget(mSurfaceHolder.getSurface());
                                captureRequest.addTarget(mediaRecorder.getSurface());
                                captureRequest.build();
                                cameraCaptureSession.setRepeatingRequest(captureRequest.build(), null, null);
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "Unable to access camera for camera capture session after stop: " + e);
                            }
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Log.d(TAG, "Configure for camera capture session failed after stop");
                    }
                };
                //reset fragment
                Camera cameraFragment = (Camera) getFragmentManager().findFragmentById(R.id.fragment_container);
                getFragmentManager().beginTransaction().detach(cameraFragment).attach(cameraFragment).commit();
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
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    /**
     * Setup the surface for preview and recording.
     * @param layout
     */
    private void setSurface(View layout) {
        mSurfaceView = layout.findViewById(R.id.camera_surface_view);
        mSurfaceView.getHolder().addCallback(mCallback);
        mSurfaceHolder = mSurfaceView.getHolder();
    }

    /**
     * Setup media recorder in the order from Camera documentation.
     */
    private void setMediaRecorder() {
        //they suggest to use this for API level 8 or higher but we will set them manually
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoFrameRate(30);
    }

    /**
     * Used to make sure the app has the required permissions.
     */
    private void checkPermissions() {
        //Camera permissions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        //Audio permissions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_AUDIO);
            }
        }

        //External storage permissions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        }
    }
}
