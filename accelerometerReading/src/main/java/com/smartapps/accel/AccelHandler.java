package com.smartapps.accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by fraw on 2/07/2015.
 */
public class AccelHandler implements SensorEventListener{
    Context mContext;
    private SensorManager sensorManager;
    private boolean started = false;
    private long lastSaved = System.currentTimeMillis();
    public ArrayList<AccelData> sensorData;
    private double LongTermAverage = 0;
    private int sampleTime = 1000;
    private Sensor accel;

    public AccelHandler(Context mContext,int sampleTime){
        this.mContext = mContext;
        this.sampleTime = sampleTime;
        sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
    }


    public void startAccel(){
        sensorData = new ArrayList<AccelData>();
        // save prev data if available
        started = true;
        accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_NORMAL);

}

    public void stopAccel(){
        started = false;
        sensorManager.unregisterListener(this);

    }

    public void pauseAccel(){
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
    }

    public void restartAccel(){
        if (started == true) {
            sensorManager.registerListener(this, accel,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > sampleTime) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                // Simple converging average for proof of concept
                LongTermAverage += z;
                LongTermAverage /= 2;
                long timestamp = System.currentTimeMillis();
                //			AccelData data = new AccelData(timestamp, x, y, z);
                AccelData data = new AccelData(timestamp, LongTermAverage, y, z);
                sensorData.add(data);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
