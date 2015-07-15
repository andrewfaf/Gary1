package com.smartapps.accel;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;


public class GraphActivity extends Activity {
    ArrayList<AccelData> sensorData;
    private View mChart;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        sensorData = (ArrayList<AccelData>)getIntent().getSerializableExtra("data");

        setContentView(R.layout.activity_graph);
        layout = (LinearLayout) findViewById(R.id.graphchart_container);

        layout.removeAllViews();
        openChart();


    }

    private void openChart() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, metrics);

        if (sensorData != null || sensorData.size() > 0) {
            long t = sensorData.get(0).getTimestamp();
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

            XYSeries zSeries = new XYSeries("Z");
            XYSeries longtermzSeries = new XYSeries("LongTermZ");

            for (AccelData data : sensorData) {
                zSeries.add(data.getTimestamp() - t, data.getZ());
                longtermzSeries.add(data.getTimestamp() - t, data.getLongtermZ());
            }

            dataset.addSeries(zSeries);
            dataset.addSeries(longtermzSeries);

            XYSeriesRenderer zRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.BLUE);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(3);
            zRenderer.setDisplayChartValues(false);

            XYSeriesRenderer longtermzRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.RED);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(3);
            zRenderer.setDisplayChartValues(false);

            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setLabelsTextSize(val / 2);
            multiRenderer.setAxisTitleTextSize(val);
            multiRenderer.setYLabelsPadding(25f);
            multiRenderer.setMargins(new int[] {0,50,0,0}); //Top, Left, Bottom, Right
//            multiRenderer.setZoomButtonsVisible(true);
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
            multiRenderer.addSeriesRenderer(zRenderer);
            multiRenderer.addSeriesRenderer(longtermzRenderer);

            // Creating a Line Chart
            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);

            // Adding the Line Chart to the LinearLayout
            layout.addView(mChart);

        }
    }

}
