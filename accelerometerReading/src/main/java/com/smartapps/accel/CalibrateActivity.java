package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.StrictMath.abs;


public class CalibrateActivity extends Activity {

    private AccelHandler cAccelHandler;
    private Handler cHandler;
    private boolean delayFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);


        cAccelHandler = new AccelHandler(this, 100);
        cHandler = new Handler();

    }


    public void doCalibrate(View v) {
        Log.d("Gary:", "Calibrate Activity doCalibrate");
        cAccelHandler.startAccel();
        delayFlag = false;
        cHandler.postDelayed(crunnable, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Gary:", "Calibrate Activity onResume");
        cAccelHandler.restartAccel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Gary:", "Calibrate Activity onPause");
		cAccelHandler.pauseAccel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Gary:", "Calibrate Activity onDestroy");
        cAccelHandler.stopAccel();
    }


    private Runnable crunnable = new Runnable() {
        @Override
        public void run() {
            long[] vpattern = {0, 200, 100, 400, 100, 200, 0};
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Log.d("Gary:", "delayFlag is " + delayFlag);
            if(delayFlag == false){
                delayFlag = true;
                v.vibrate(vpattern, -1);
                cHandler.postDelayed(crunnable, 5000);
                return;
            }
            delayFlag = false;
            cAccelHandler.stopAccel();
            cHandler.removeCallbacks(crunnable);
            v.vibrate(vpattern, -1);
/*
            if (abs(cAccelHandler.getTotalX()) > abs(cAccelHandler.getTotalY())) {
                if (cAccelHandler.getTotalX() > 0) {
                    MainActivity.oriented = 1;
                    Log.d("Gary:", "Left Landscape");
                } else if (cAccelHandler.getTotalX() < 0) {
                    MainActivity.oriented = 2;
                    Log.d("Gary:", "Right Landscape");
                }
            }
            else
            {
                MainActivity.oriented = 3;
                Log.d("Gary:", "Portrait");
            }
*/

            MainActivity.calibratedZ = cAccelHandler.getAverageZ();
            Log.d("Gary:", "CalibratedZ " + MainActivity.calibratedZ);

            SharedPreferences sharedPrefs = getSharedPreferences("CalibratedZ",0);
            sharedPrefs.edit().putFloat("Calibrat4edZ",(float)MainActivity.calibratedZ).apply();

            finish();
        }
    };

}
