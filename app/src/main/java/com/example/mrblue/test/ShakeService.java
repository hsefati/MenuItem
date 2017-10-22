package com.example.mrblue.test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Mr.Blue on 10/22/2017.
 */

public class ShakeService extends Service {

    private float accelerationValue, accelerationLast, shake;

    public ShakeService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelerationValue = SensorManager.GRAVITY_EARTH;
        accelerationLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        SensorManager sManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL); // or other delay

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast toast = Toast.makeText(getApplicationContext(), "Shaker Service is started", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");

        return null;

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            handleShake(x, y, z);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void handleShake(float x, float y, float z) {
        accelerationLast = accelerationValue;
        accelerationValue = (float) Math.sqrt((double) (x * x + y * y + z + z));
        float delta = accelerationValue - accelerationLast;
        shake = shake * 0.9f + delta;


        //TODO: 12 is a threshhold value of shake detection. Define config class and put all of similar parameters inside
        if (shake > 15) {
//            Toast toast = Toast.makeText(getApplicationContext(), "Do not shake me", Toast.LENGTH_LONG);
//            toast.show();
            Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(activityIntent);
        }
    }
}
