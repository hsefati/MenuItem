package com.example.mrblue.test;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Mr.Blue on 7/13/2017.
 *
 * Camera preview class
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Camera myCamera;
    private SurfaceHolder holder;
    private boolean isConfigured = false;

    private static final String TAG = "SimpleCameraApp";

    public CameraSurfaceView(Context context, Camera myCamera) {
        super(context);
        this.myCamera = myCamera;


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        this.holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.

        try {
            isConfigured = false;
            myCamera.setPreviewDisplay(holder);
            myCamera.startPreview();
        }catch (IOException e){
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.


        // The section supposed to reduce the resolution of the picture, which is shown in surfaceView
        Camera.Parameters parametersCamera =myCamera.getParameters();
        Camera.Size size=getBestPreviewSize(width, height, parametersCamera);
        Camera.Size pictureSize=getSmallestPictureSize(parametersCamera);

        if(!isConfigured) {
            if (size != null && pictureSize != null) {
                parametersCamera.setPreviewSize(size.width, size.height);
                parametersCamera.setPictureSize(pictureSize.width,
                        pictureSize.height);
                parametersCamera.setPictureFormat(ImageFormat.JPEG);
                myCamera.setParameters(parametersCamera);
                isConfigured = true;
            }
        }

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
//            myCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
//            myCamera.setPreviewDisplay(holder);
//            myCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.

        myCamera.stopPreview();
//        myCamera.release();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result=size;
            }
            else {
                int resultArea=result.width * result.height;
                int newArea=size.width * size.height;

                if (newArea < resultArea) {
                    result=size;
                }
            }
        }

        return(result);
    }
}
