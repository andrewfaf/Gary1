package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class MainActivity extends Activity implements OnClickListener {
    private Button btnStart, btnStop, btnUpload;
    private TextView txtAvg, xAxis, yAxis, zAxis;
//    private boolean started = false;
//    private SensorManager sensorManager;
//    private long lastSaved = System.currentTimeMillis();
//    private double LongTermAverage = 0;
//    private ArrayList<AccelData> sensorData;
    private LinearLayout layout;
    private View mChart;
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
        layout = (LinearLayout) findViewById(R.id.chart_container);
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensorData = new ArrayList<AccelData>();
        lAccelHandler = new AccelHandler(this,
                Integer.parseInt(sharedPrefs.getString("updates_interval", "1000")) );

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        txtAvg = (TextView) findViewById(R.id.textView);
        xAxis = (TextView) findViewById(R.id.xAxistextView);
        yAxis = (TextView) findViewById(R.id.yAxistextView);
        zAxis = (TextView) findViewById(R.id.zAxistextView);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        mHandler = new Handler();
/*        if (sensorData == null || sensorData.size() == 0) {
            btnUpload.setEnabled(false);
        }
*/        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
//		if (started == true) {
//			sensorManager.unregisterListener(this);
//		}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lAccelHandler.stopAccel();
/*        if (started == true) {
            sensorManager.unregisterListener(this);
        }
*/    }

// to do
/* Add some kind of listener method that fires periodically to:
        1. Check the Thresholds and vibrate accordingly
*/

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long[] vpatternf = {0, 63, 37, 63, 137, 100, 137, 63, 137, 100};
            long[] vpatternb = {0, 125, 28, 125, 28, 113, 28, 43, 10, 113, 28};
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if ((lAccelHandler.getLongTermAverage() > 3.5) && vibrateFwdOn) {
                v.vibrate(vpatternf, -1);
            } else if ((lAccelHandler.getLongTermAverage() < -1.5) && vibrateBwdOn) {
                v.vibrate(vpatternb, -1);
            }
            mHandler.postDelayed(this,1000);
        }
    };

/*    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) > Integer.parseInt(sharedPrefs.getString("updates_interval", "1000"))) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                long[] vpatternf = {0, 63, 37, 63, 137, 100, 137, 63, 137, 100};
                long[] vpatternb = {0, 125, 28, 125, 28, 113, 28, 43, 10, 113, 28};
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Simple converging average for proof of concept
                LongTermAverage += z;
                LongTermAverage /= 2;
                long timestamp = System.currentTimeMillis();
                //			AccelData data = new AccelData(timestamp, x, y, z);
                AccelData data = new AccelData(timestamp, LongTermAverage, y, z);
                sensorData.add(data);
                txtAvg.setText(String.format("%.2f", LongTermAverage));
                xAxis.setText("X-Axis = " + String.format("%.2f", x*1000));
                yAxis.setText("Y-Axis = " + String.format("%.2f", y*1000));
                zAxis.setText("Z-Axis = " + String.format("%.2f", z*1000));

                vibrateFwdOn = sharedPrefs.getBoolean("checkBoxFwd", true);
                vibrateBwdOn = sharedPrefs.getBoolean("checkBoxBwd", true);

                if ((LongTermAverage > 3.5) && vibrateFwdOn) {
                    v.vibrate(vpatternf, -1);
                } else if ((LongTermAverage < -1.5) && vibrateBwdOn) {
                    v.vibrate(vpatternb, -1);
                }
            }
        }

    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnUpload.setEnabled(false);
                lAccelHandler.startAccel();
                mHandler.postDelayed(runnable, 1000);
/*
                sensorData = new ArrayList<AccelData>();
                // save prev data if available
                started = true;
                Sensor accel = sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel,
                        SensorManager.SENSOR_DELAY_NORMAL);
*/
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnUpload.setEnabled(true);
                lAccelHandler.stopAccel();
                mHandler.removeCallbacks(runnable);
/*
                started = false;
                sensorManager.unregisterListener(this);
*/
                layout.removeAllViews();
                openChart();

                // show data in chart
                break;
            case R.id.btnUpload:

                break;
            default:
                break;
        }

    }

    private void openChart() {
        if (lAccelHandler.sensorData != null || lAccelHandler.sensorData.size() > 0) {
            long t = lAccelHandler.sensorData.get(0).getTimestamp();
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

            XYSeries xSeries = new XYSeries("X");
//			XYSeries ySeries = new XYSeries("Y");
            XYSeries zSeries = new XYSeries("Z");

            for (AccelData data : lAccelHandler.sensorData) {
                xSeries.add(data.getTimestamp() - t, data.getX());
//				ySeries.add(data.getTimestamp() - t, data.getY());
                zSeries.add(data.getTimestamp() - t, data.getZ());
            }

            dataset.addSeries(xSeries);
//			dataset.addSeries(ySeries);
            dataset.addSeries(zSeries);

            XYSeriesRenderer xRenderer = new XYSeriesRenderer();
            xRenderer.setColor(Color.RED);
            xRenderer.setPointStyle(PointStyle.CIRCLE);
            xRenderer.setFillPoints(true);
            xRenderer.setLineWidth(1);
            xRenderer.setDisplayChartValues(false);

/*			XYSeriesRenderer yRenderer = new XYSeriesRenderer();
            yRenderer.setColor(Color.GREEN);
			yRenderer.setPointStyle(PointStyle.CIRCLE);
			yRenderer.setFillPoints(true);
			yRenderer.setLineWidth(1);
			yRenderer.setDisplayChartValues(false);
*/
            XYSeriesRenderer zRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.BLUE);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(3);
            zRenderer.setDisplayChartValues(false);

            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
/*			multiRenderer.setXLabels(0);
			multiRenderer.setLabelsColor(Color.RED);
			multiRenderer.setChartTitle("t vs (x,y,z)");
			multiRenderer.setXTitle("Sensor Data");
			multiRenderer.setYTitle("Values of Acceleration");
			multiRenderer.setZoomButtonsVisible(true);
			for (int i = 0; i < sensorData.size(); i++) {
				
				multiRenderer.addXTextLabel(i + 1, ""
						+ (sensorData.get(i).getTimestamp() - t));
			}
			for (int i = 0; i < 12; i++) {
				multiRenderer.addYTextLabel(i + 1, ""+i);
			}

*/
            multiRenderer.addSeriesRenderer(xRenderer);
//			multiRenderer.addSeriesRenderer(yRenderer);
            multiRenderer.addSeriesRenderer(zRenderer);

            // Getting a reference to LinearLayout of the MainActivity Layout


            // Creating a Line Chart
            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);

            // Adding the Line Chart to the LinearLayout
            layout.addView(mChart);

        }
    }

}
