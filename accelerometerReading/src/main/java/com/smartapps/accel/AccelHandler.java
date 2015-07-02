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
    private boolean calFlag = false;
    private double totalX, totalY, totalZ = 0;

    public AccelHandler(Context mContext,int sampleTime){
        this.mContext = mContext;
        this.sampleTime = sampleTime;
        sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
    }
    public AccelHandler(Context mContext,int sampleTime, boolean calFlag){
        this.mContext = mContext;
        this.sampleTime = sampleTime;
        this.calFlag = calFlag;
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
    public double getLongTermAverage(){
        return LongTermAverage;
    }

    public double getTotalX(){
        return totalX;
    }

    public double getTotalY(){
        return totalY;
    }

    public double getTotalZ(){
        return totalZ;
    }

    public void setTotalX(double x){
        totalX = x;
    }

    public void setTotalY(double y){
        totalY = y;
    }

    public void setTotalZ(double z){
        totalZ = z;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > sampleTime) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                if (calFlag == true){
                    totalX += x;
                    totalY += y;
                    totalZ += z;
                }
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
