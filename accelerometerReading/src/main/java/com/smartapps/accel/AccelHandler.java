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
    private double totalX, totalY, totalZ = 0;
//    private double calibratedZ = 0;


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
    public double getLongTermAverage(){
        return LongTermAverage;
    }

    public void resetLongTermAverage(){
        LongTermAverage = 0 ;
    }

/*    public double getCalibratedZ() {
        return calibratedZ;
    }
*/
    public double getTotalX(){
        return totalX;
    }

    public double getTotalY(){
        return totalY;
    }

    public double getTotalZ(){
        return totalZ;
    }
/*
    public void setCalibratedZ(double calibratedZ){
        this.calibratedZ = calibratedZ;
    }
*/
    public void setTotalX(double x){
        totalX = x;
    }

    public void setTotalY(double y){
        totalY = y;
    }

    public void setTotalZ(double z){
        totalZ = z;
    }

    public double getAverageZ (){
        Log.d("Gary:" , "Size of Sensor Data " + sensorData.size());
        double avgZ = 0;
        for (int i = 0; i <sensorData.size() ; i++) {
            avgZ += sensorData.get(i).getZ();
        }
        avgZ /= sensorData.size();
        Log.d("Gary:", "avgZ " + avgZ);
        return avgZ;
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > sampleTime) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                totalX += x;
                totalY += y;
                totalZ += z;
                // Simple converging average for proof of concept
                LongTermAverage += (z - MainActivity.calibratedZ);
                LongTermAverage /= 2;
//                LongTermAverage -= MainActivity.calibratedZ;
                long timestamp = System.currentTimeMillis();
                //			AccelData data = new AccelData(timestamp, x, y, z);
                AccelData data = new AccelData(timestamp, LongTermAverage, y, z, LongTermAverage);
                sensorData.add(data);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
