package com.tooloom.accel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.smartapps.accel.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
        // Get Medium Text (18dp) size value in pixels, achartengine uses pixels, not dp
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, metrics);

        if (sensorData != null || sensorData.size() > 0) {
            long t = sensorData.get(0).getTimestamp();
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

            XYSeries zSeries = new XYSeries("Z");
            XYSeries longtermzSeries = new XYSeries("LongTermZ");

            XYSeries fwdThreshSeries = new XYSeries("fwdThresh");
            XYSeries bwdThreshSeries = new XYSeries("bwdThresh");

            for (AccelData data : sensorData) {
                zSeries.add((data.getTimestamp() - t)/1000, data.getZ());
                longtermzSeries.add((data.getTimestamp() - t)/1000, data.getLongtermZ());
                fwdThreshSeries.add((data.getTimestamp() - t)/1000,MainActivity.fwdThreshold/2 );
                bwdThreshSeries.add((data.getTimestamp() - t)/1000,-MainActivity.bwdThreshold/2 );
            }

            dataset.addSeries(zSeries);
            dataset.addSeries(longtermzSeries);
            dataset.addSeries(fwdThreshSeries);
            dataset.addSeries(bwdThreshSeries);

            XYSeriesRenderer zRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.BLUE);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(3);
            zRenderer.setDisplayChartValues(false);

            XYSeriesRenderer longtermzRenderer = new XYSeriesRenderer();
            longtermzRenderer.setColor(Color.GREEN);
            longtermzRenderer.setPointStyle(PointStyle.DIAMOND);
            longtermzRenderer.setFillPoints(true);
            longtermzRenderer.setLineWidth(5);
            longtermzRenderer.setDisplayChartValues(false);

            XYSeriesRenderer fwdThresholdRenderer = new XYSeriesRenderer();
            fwdThresholdRenderer.setColor(Color.RED);
            fwdThresholdRenderer.setPointStyle(PointStyle.POINT);
            fwdThresholdRenderer.setFillPoints(true);
            fwdThresholdRenderer.setLineWidth(3);
            fwdThresholdRenderer.setDisplayChartValues(false);

            XYSeriesRenderer bwdThresholdRenderer = new XYSeriesRenderer();
            bwdThresholdRenderer.setColor(Color.RED);
            bwdThresholdRenderer.setPointStyle(PointStyle.POINT);
            bwdThresholdRenderer.setFillPoints(true);
            bwdThresholdRenderer.setLineWidth(3);
            bwdThresholdRenderer.setDisplayChartValues(false);

            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setLabelsTextSize(val / 2);
            multiRenderer.setAxisTitleTextSize(val);
            multiRenderer.setYLabelsPadding(25f);
            multiRenderer.setMargins(new int[]{0, 50, 25, 0}); //Top, Left, Bottom, Right
            multiRenderer.setLegendTextSize(val);
            multiRenderer.setFitLegend(true);
            multiRenderer.setZoomEnabled(true, false);
            multiRenderer.addSeriesRenderer(zRenderer);
            multiRenderer.addSeriesRenderer(longtermzRenderer);
            multiRenderer.addSeriesRenderer(fwdThresholdRenderer);
            multiRenderer.addSeriesRenderer(bwdThresholdRenderer);

            // Creating a Line Chart
            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);

            // Adding the Line Chart to the LinearLayout
            layout.addView(mChart);

        }
    }

}
