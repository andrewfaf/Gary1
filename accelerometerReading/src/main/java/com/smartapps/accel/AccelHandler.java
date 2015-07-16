package com.smartapps.accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by fraw on 2/07/2015.
 * Impements that accelerometer handler
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
    private double totalZ = 0;
    private double z = 0;
    private double zcount = 0;

    public AccelHandler(Context mContext,int sampleTime){
        this.mContext = mContext;
        this.sampleTime = sampleTime;
        sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
    }



    public void startAccel(){
//        sensorData = new ArrayList<AccelData>();
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
        if (started) {
            sensorManager.unregisterListener(this);
        }
    }

    public void restartAccel(){
        if (started) {
            sensorManager.registerListener(this, accel,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    public double getLongTermAverage(){
        return LongTermAverage;
    }
    public double getZ(){
        return z;
    }

    public double getAverageZ (){
        Log.d("Gary:" , "Number of samples " + zcount);
        double avgZ;
        avgZ = totalZ;
        avgZ /= zcount;
        Log.d("Gary:", "avgZ " + avgZ);
        return avgZ;
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > sampleTime) {
                lastSaved = System.currentTimeMillis();
                z = event.values[2];
                totalZ += z;
                zcount += 1;
                z -= MainActivity.calibratedZ;

                // Moving window average
                LongTermAverage -= LongTermAverage/3;
                LongTermAverage += z/4;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
