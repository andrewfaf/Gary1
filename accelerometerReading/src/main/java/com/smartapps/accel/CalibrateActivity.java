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
    private AccelHandler cAccelHandler;
    private Handler cHandler;
    private boolean delayFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        btnCalibrate = (Button) findViewById(R.id.calibrateButton);
        btnEnd = (Button) findViewById(R.id.endcalibratebutton);
        btnCalibrate.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

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
                delayFlag = false;
                cHandler.postDelayed(crunnable, 1000);
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


    private Runnable crunnable = new Runnable() {
        @Override
        public void run() {
            long[] vpatternf = {200, 200, 200, 200, 200};
            long[] vpatternb = {500,500 };
            Log.d("Gary:", "delayFlag" + delayFlag);
            if(delayFlag == false){
                delayFlag = true;
                cHandler.postDelayed(crunnable, 2000);
                return;
            }
            delayFlag = false;
            cAccelHandler.stopAccel();
            cHandler.removeCallbacks(crunnable);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(vpatternf, -1);
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

            MainActivity.calibratedZ = cAccelHandler.getAverageZ();
            Log.d("Gary:", "CalibratedZ " + MainActivity.calibratedZ);
            finish();
        }
    };

}
