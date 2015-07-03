package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends Activity implements OnClickListener {
    private Button btnStart, btnStop, btnGraph;
    private TextView txtAvg, xAxis, yAxis, zAxis;
    public static boolean vibrateFwdOn = true;
    public static boolean vibrateBwdOn = true;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preflistener;
    public static char oriented = 0;
    private AccelHandler lAccelHandler;
    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        lAccelHandler = new AccelHandler(this,
                Integer.parseInt(sharedPrefs.getString("updates_interval", "1000")));

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnGraph = (Button) findViewById(R.id.btnGraph);
        txtAvg = (TextView) findViewById(R.id.textView);
        xAxis = (TextView) findViewById(R.id.xAxistextView);
        yAxis = (TextView) findViewById(R.id.yAxistextView);
        zAxis = (TextView) findViewById(R.id.zAxistextView);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        mHandler = new Handler();

        preflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                vibrateFwdOn = sharedPrefs.getBoolean("checkBoxFwd", true);
                vibrateBwdOn = sharedPrefs.getBoolean("checkBoxBwd", true);
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(preflistener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 0, 0, "Change settings");
        menu.add(Menu.NONE, 1, 0, "Display settings");
        menu.add(Menu.NONE, 2, 0, "Calibrate");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivity(intent);
                return true;
            case 1:
                startActivity(new Intent(this, ShowSettingsActivity.class));
                return true;
            case 2:
                startActivity(new Intent(this, CalibrateActivity.class));
                return true;
        }
        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lAccelHandler.stopAccel();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long[] vpatternf = {200, 200, 200, 200, 200};
            long[] vpatternb = {500,500 };

            txtAvg.setText(String.format("%.2f", lAccelHandler.getLongTermAverage()));
            xAxis.setText("X-Axis = " + String.format("%.2f", lAccelHandler.getTotalX()));
            yAxis.setText("Y-Axis = " + String.format("%.2f", lAccelHandler.getTotalY()));
            zAxis.setText("Z-Axis = " + String.format("%.2f", lAccelHandler.getTotalZ()));
            lAccelHandler.setTotalX(0);
            lAccelHandler.setTotalY(0);
            lAccelHandler.setTotalZ(0);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if ((lAccelHandler.getLongTermAverage() > 3.5) && vibrateFwdOn) {
                v.vibrate(vpatternf, -1);
            } else if ((lAccelHandler.getLongTermAverage() < -1.5) && vibrateBwdOn) {
                v.vibrate(vpatternb, -1);
            }
            mHandler.postDelayed(this,1000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnGraph.setEnabled(false);
                lAccelHandler.startAccel();
                mHandler.postDelayed(runnable, 1000);
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnGraph.setEnabled(true);
                lAccelHandler.stopAccel();
                mHandler.removeCallbacks(runnable);
//                layout.removeAllViews();

                // show data in chart
                break;
            case R.id.btnGraph:
                Intent i = new Intent(this, GraphActivity.class);
                i.putExtra("data", lAccelHandler.sensorData);
                startActivity(i);
                break;
            default:
                break;
        }

    }

}
