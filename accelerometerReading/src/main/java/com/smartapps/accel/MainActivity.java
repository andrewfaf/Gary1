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
import android.os.Vibrator;
import android.preference.PreferenceActivity;
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

public class MainActivity extends Activity implements SensorEventListener,
		OnClickListener {
	private SensorManager sensorManager;
	private Button btnStart, btnStop, btnUpload;
    private TextView txtAvg;
	private boolean started = false;
	private ArrayList<AccelData> sensorData;
	private double LongTermAverage = 0;
	private LinearLayout layout;
	private View mChart;
	private boolean vibrateFwdOn = true;
	private boolean vibrateBwdOn = true;
	private SharedPreferences sharedPrefs;
	static int ACCE_FILTER_DATA_MIN_TIME = 2000; // 1000ms
	private long lastSaved = System.currentTimeMillis();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (LinearLayout) findViewById(R.id.chart_container);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorData = new ArrayList<AccelData>();

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnUpload = (Button) findViewById(R.id.btnUpload);
        txtAvg = (TextView) findViewById(R.id.textView);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		if (sensorData == null || sensorData.size() == 0) {
			btnUpload.setEnabled(false);
		}
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		vibrateFwdOn = sharedPrefs.getBoolean("checkBoxFwd", true);
		vibrateBwdOn = sharedPrefs.getBoolean("checkBoxBwd", true);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu(menu);
/*		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
*/
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(this,PrefsActivity.class);
		startActivity(intent);
		return true;
/*
		if (item.getItemId() == R.id.menu_red)
		{
			vibrateFwdOn = true;
		}
		else
		{
			vibrateFwdOn = false;
		}
		if (item.getItemId() == R.id.menu_green)
		{
			vibrateBwdOn = true;
		}
		else
		{
			vibrateBwdOn = false;
		}

*/
/*		switch (item.getItemId()) {
			case R.id.menu_red:
				if (item.isChecked()) item.setChecked(false);
				else item.setChecked(true);
				layout.setBackgroundColor(android.graphics.Color.RED);
				return true;
			case R.id.menu_green:
				if (item.isChecked()) item.setChecked(false);
				else item.setChecked(true);
				layout.setBackgroundColor(android.graphics.Color.GREEN);
				return true;
			case R.id.menu_yellow:
				if (item.isChecked()) item.setChecked(false);
				else item.setChecked(true);
				layout.setBackgroundColor(android.graphics.Color.YELLOW);
				return true;
			case R.id.menu_blue:
				if (item.isChecked()) item.setChecked(false);
				else item.setChecked(true);
				layout.setBackgroundColor(android.graphics.Color.BLUE);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

		return super.onOptionsItemSelected(item);
*/
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (started) {
            if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
                lastSaved = System.currentTimeMillis();
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                long[] vpatternf = {0, 200, 200, 200, 200};
                long[] vpatternb = {0, 400, 100, 400, 100};
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Simple converging average for proof of concept
                LongTermAverage += z;
                LongTermAverage /= 2;
                long timestamp = System.currentTimeMillis();
                //			AccelData data = new AccelData(timestamp, x, y, z);
                AccelData data = new AccelData(timestamp, LongTermAverage, y, z);
                sensorData.add(data);
                txtAvg.setText(String.format("%.2f", LongTermAverage));
				sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
				vibrateFwdOn = sharedPrefs.getBoolean("checkBoxFwd", true);
				vibrateBwdOn = sharedPrefs.getBoolean("checkBoxBwd", true);
                if ((LongTermAverage > 3.5) && vibrateFwdOn) {
                    v.vibrate(vpatternf, -1);
                } else if ((LongTermAverage < -1.5) &&vibrateBwdOn) {
                    v.vibrate(vpatternb, -1);
                }
            }
        }

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			btnUpload.setEnabled(false);
			sensorData = new ArrayList<AccelData>();
			// save prev data if available
			started = true;
			Sensor accel = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accel,
					SensorManager.SENSOR_DELAY_NORMAL);
//			sensorManager.registerListener(this, accel,20000000); // Sample every second
			break;
		case R.id.btnStop:
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			btnUpload.setEnabled(true);
			started = false;
			sensorManager.unregisterListener(this);
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
		if (sensorData != null || sensorData.size() > 0) {
			long t = sensorData.get(0).getTimestamp();
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

			XYSeries xSeries = new XYSeries("X");
//			XYSeries ySeries = new XYSeries("Y");
			XYSeries zSeries = new XYSeries("Z");

			for (AccelData data : sensorData) {
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

*/			multiRenderer.addSeriesRenderer(xRenderer);
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
