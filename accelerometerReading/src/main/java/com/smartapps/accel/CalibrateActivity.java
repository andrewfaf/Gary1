package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.StrictMath.abs;


public class CalibrateActivity extends Activity implements View.OnClickListener {

    private Button btnCalibrate,btnEnd;
    private TextView txtCalibrationMessage;
    private AccelHandler cAccelHandler;
    private Handler cHandler;

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
        btnEnd = (Button) findViewById(R.id.endcalibratebutton);
        btnCalibrate.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

        txtCalibrationMessage = (TextView) findViewById(R.id.calibrationMessagetextView);
        cAccelHandler = new AccelHandler(this, 100);
        cHandler = new Handler();

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
                cHandler.postDelayed(crunnable, 2000);
                break;
            case R.id.endcalibratebutton:
                btnEnd.setEnabled(false);
                this.finish();
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

    private Runnable crunnable = new Runnable() {
        @Override
        public void run() {
            long[] vpatternf = {200, 200, 200, 200, 200};
            long[] vpatternb = {500,500 };
            cAccelHandler.stopAccel();
            cHandler.removeCallbacks(crunnable);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(vpatternf, -1);
            if (abs(cAccelHandler.getTotalX()) > abs(cAccelHandler.getTotalY())) {
                if (cAccelHandler.getTotalX() > 0) {
                    MainActivity.oriented = 1;
                    txtCalibrationMessage.setText("Left Landscape - Calibration value ");
                } else if (cAccelHandler.getTotalX() < 0) {
                    MainActivity.oriented = 2;
                    txtCalibrationMessage.setText("Right Landscape - Calibration value ");
                }
            }
            else
            {
                MainActivity.oriented = 3;
                txtCalibrationMessage.setText("Portrait - Calibration value ");
            }

//            cAccelHandler.setCalibratedZ(cAccelHandler.getLongTermAverage());
            MainActivity.calibratedZ = cAccelHandler.getAverageZ();
            txtCalibrationMessage.append(String.format(" %2f", MainActivity.calibratedZ));
            Log.d("Gary:", "CalibratedZ " + MainActivity.calibratedZ);
//            cHandler.postDelayed(this,1000);
        }
    };

}
