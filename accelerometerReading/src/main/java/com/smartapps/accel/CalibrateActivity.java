package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.StrictMath.abs;


public class CalibrateActivity extends Activity implements View.OnClickListener {

    private Button btnCalibrate;
    private TextView txtCalibrationMessage;
    private AccelHandler cAccelHandler;

//    private SensorManager sensorManager;
//    private ArrayList<AccelData> sensorData;
//    private boolean started = false;
//    private Sensor accel;
//    private long lastSaved = System.currentTimeMillis();
//    private long startSaved = System.currentTimeMillis();
//    private double totalX, totalY, totalZ = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        btnCalibrate = (Button) findViewById(R.id.calibrateButton);
        btnCalibrate.setOnClickListener(this);

        txtCalibrationMessage = (TextView) findViewById(R.id.calibrationMessagetextView);
        cAccelHandler = new AccelHandler(this, 1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calibrate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calibrateButton:
                btnCalibrate.setEnabled(false);
                cAccelHandler.startAccel();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cAccelHandler.restartAccel();
    }

    @Override
    protected void onPause() {
        super.onPause();
		cAccelHandler.pauseAccel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cAccelHandler.stopAccel();
    }


// to do
/* Add some kind of listener method that fires after a set time to:
        1. Figure out what orientation the phone will be used in
        2. Figure out the Threshold values
        3. Stop the accelHandler and clean up

*/
/*  @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > 100) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                totalX += x;
                totalY += y;
                totalZ += z;
            }
        }
        if ((System.currentTimeMillis() - startSaved) > 3000) {
            started = false;
            sensorManager.unregisterListener((SensorEventListener) this);
            if (abs(totalX) > abs(totalY)) {
                if (totalX > 0) {
                    MainActivity.oriented = 1;
                    txtCalibrationMessage.setText("Left Landscape");
                } else if (totalX < 0) {
                    MainActivity.oriented = 2;
                    txtCalibrationMessage.setText("Right Landscape");
                }
            }
            else
            {
                MainActivity.oriented = 3;
            }
        }

    }
*/
}
