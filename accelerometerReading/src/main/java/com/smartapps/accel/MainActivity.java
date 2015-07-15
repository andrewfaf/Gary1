package com.smartapps.accel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/* To Do

    Save Data - Done for Saving to SDCard
    Save Data in csv format - Done
    Change Calibrate to a button in the settings/Action Bar? - Done
    Check Box to keep screen on for Accelerometer to work on some Phones

    Check Boxes showing correct state in Preferences Setting Menu
    Need to be able to select a saved data set and graph it
    Need to be able to select a group of data sets (or one) and email it for review
    Need to be able to select a group of data sets (or one) and email csv formatted versions


*/

public class MainActivity extends Activity implements OnClickListener {
    private Button btnStart, btnStop, btnGraph;
    private TextView txtAvg, xAxis, yAxis, zAxis;
    public static boolean vibrateFwdOn = true;
    public static boolean vibrateBwdOn = true;
    public static int fwdThreshold = 5;
    public static int bwdThreshold = 5;
    public static double calibratedZ = 0;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preflistener;
    public static char oriented = 0;
    private static float brightness = 0.1f;
    private AccelHandler lAccelHandler;
    Handler mHandler, vibHandler;

    private CSVWriter filecsv;
    private File file;
    private static final String FILENAME = "acelData";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnGraph = (Button) findViewById(R.id.btnGraph);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnGraph.setOnClickListener(this);

        txtAvg = (TextView) findViewById(R.id.textView);
        xAxis = (TextView) findViewById(R.id.xAxistextView);
        yAxis = (TextView) findViewById(R.id.yAxistextView);
        zAxis = (TextView) findViewById(R.id.zAxistextView);

        mHandler = new Handler();
        vibHandler = new Handler();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        preflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                vibrateFwdOn = sharedPrefs.getBoolean("checkBoxFwd", true);
                vibrateBwdOn = sharedPrefs.getBoolean("checkBoxBwd", true);
                Log.d("Gary:", "EditTextFwdThresh = " + Integer.parseInt(sharedPrefs.getString("EditTextFwdThresh", "5")));
                Log.d("Gary:", "EditTextBwdThresh = " + Integer.parseInt(sharedPrefs.getString("EditTextBwdThresh", "5")));

                fwdThreshold = Integer.parseInt(sharedPrefs.getString("EditTextFwdThresh", "5"));
                bwdThreshold = Integer.parseInt(sharedPrefs.getString("EditTextBwdThresh", "5"));

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
        mHandler.removeCallbacks(mrunnable);
        mHandler.removeCallbacks(vibrunnable);
    }

    private Runnable mrunnable = new Runnable() {
        @Override
        public void run() {

            txtAvg.setText(String.format("%.2f", lAccelHandler.getLongTermAverage()));
            xAxis.setText("X-Axis = " + String.format("%.2f", lAccelHandler.getTotalX()));
            yAxis.setText("Y-Axis = " + String.format("%.2f", lAccelHandler.getTotalY()));
            zAxis.setText("Z-Axis = " + String.format("%.2f", lAccelHandler.getTotalZ()));
            lAccelHandler.setTotalX(0);
            lAccelHandler.setTotalY(0);
            lAccelHandler.setTotalZ(0);

            mHandler.postDelayed(this,500);
        }
    };


    private Runnable vibrunnable = new Runnable() {
        @Override

        public void run() {
            long[] vpatternf = {0, 200, 200, 200, 200, 200, 0};
            long[] vpatternb = {0, 400, 200, 400, 0};

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if ((lAccelHandler.getLongTermAverage() > fwdThreshold/2) && vibrateFwdOn) {
                v.vibrate(vpatternf, -1);
            } else if ((lAccelHandler.getLongTermAverage() < -bwdThreshold/2) && vibrateBwdOn) {
                v.vibrate(vpatternb, -1);
            }
            mHandler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnGraph.setEnabled(false);
                lAccelHandler = new AccelHandler(this,
                        Integer.parseInt(sharedPrefs.getString("updates_interval", "1000")));

                lAccelHandler.startAccel();
                mHandler.post(mrunnable);
                vibHandler.post(vibrunnable);

                getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                Window w = getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                brightness = lp.screenBrightness;
//                lp.screenBrightness = lp.BRIGHTNESS_OVERRIDE_OFF;
                lp.screenBrightness = 0.1f;
// 1 Seems to be full brightness, 0 is off which seems to be the same as turning off the screen
// and you can't easily turn it back on
                w.setAttributes(lp);

                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnGraph.setEnabled(true);
                lAccelHandler.stopAccel();
                mHandler.removeCallbacks(mrunnable);
                mHandler.removeCallbacks(vibrunnable);
                getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                try {
                    createFile(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                w = getWindow();
                lp = w.getAttributes();
//                lp.screenBrightness = brightness;
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                w.setAttributes(lp);

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
    public boolean checkExternalStorage(){
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        } else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            Toast.makeText(this, "External Storage is read-only", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "External Storage is unavailable", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void createFile(View v) throws IOException {
        if (!checkExternalStorage()) {
            ArrayList<AccelData> sensorData = lAccelHandler.sensorData;
            String text = sensorData.toString();
            Log.d("Gary:", "Write to Internal SDCard");

            FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(this, "File written to Internal Disk: ", Toast.LENGTH_LONG).show();

            return;
        }

        Log.d("Gary:", "Write to External SDCard");

        ArrayList<AccelData> sensorData = lAccelHandler.sensorData;
        String text = sensorData.toString();

        File extDir = getExternalFilesDir(null);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fnametime = new SimpleDateFormat("yyyy-mm-dd-kk");
        String dateString = fnametime.format(calendar.getTime());
        file = new File(extDir,FILENAME + dateString + ".csv");
        file.createNewFile();
        filecsv = new CSVWriter(new FileWriter(file));

        // Write Header
        String csvText = "Timestamp#X#Y#Z";
        String[] entries = csvText.split("#");
        filecsv.writeNext(entries);

        // Write Data
        for (int i = 0; i < sensorData.size(); i++){
            csvText = "" + sensorData.get(i).getTimestamp() + '#'+ sensorData.get(i).getX() + '#'+sensorData.get(i).getY() + '#'+sensorData.get(i).getZ();
            entries = csvText.split("#");
            filecsv.writeNext(entries);
        }
        filecsv.close();

        Toast.makeText(this, "File written to External Disk: " + extDir, Toast.LENGTH_LONG).show();

    }
}
