package com.example.mrblue.test;

/**
 * Created by Mr.Blue on 10/3/2017.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.text.DecimalFormat;

class SensorMotion implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static final int SENSOR_DELAY = 700 * 1000; // 700ms
    private static final int FROM_RADS_TO_DEGS = -57;

    private float lastPitch;

    private Context appContext;
    private ListView listView;

    SensorMotion(Context context, ListView listView){

        this.appContext = context;
        this.listView = listView;

        mSensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }

    public void registerSensorMotion(){
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
    }

    public void unRegisterSensorMotion(){
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float scrollValue = 0;
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                scrollValue = update(truncatedRotationVector);
            } else {
                scrollValue = update(event.values);
            }
        }
        
        listView.smoothScrollByOffset((int)scrollValue);
    }

    private float update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
//        float roll = orientation[2] * FROM_RADS_TO_DEGS;

        float  diffPitch = lastPitch - pitch;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float pitchDiffFloat = Float.valueOf(decimalFormat.format(diffPitch));

        lastPitch = pitch;

        return pitchDiffFloat;
    }

}