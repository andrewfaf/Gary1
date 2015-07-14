package com.smartapps.accel;

/**
 * Created by fraw on 9/06/2015.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;

public class ShowSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_display);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        TextView fwdThresholdTextView = (TextView) findViewById(R.id.ForwardThresholdtextView);
        fwdThresholdTextView.setText(settingsStringBuilder("\n Forward Threshold: " +
                Integer.parseInt(sharedPrefs.getString("FWDeditText","2"))+ "MA " +
                MainActivity.fwdThreshold));

        TextView bwdThresholdTextView = (TextView) findViewById(R.id.BackwardThresholdtextView);
        bwdThresholdTextView.setText(settingsStringBuilder("\n Backward Threshold: " +
                Integer.parseInt(sharedPrefs.getString("BWDeditText", "2"))+
                "MA " + MainActivity.bwdThreshold));

        TextView forwardValuesTextView = (TextView) findViewById(R.id.forwardValuestextView);
        forwardValuesTextView.setText(settingsStringBuilder("\n Main.Activity.forwardVibrate = " +
                MainActivity.vibrateFwdOn) + " Check Box is " +
                sharedPrefs.getBoolean("checkBoxFwd", false));

        TextView backwardValuesTextView = (TextView) findViewById(R.id.backwardValuestextView);
        backwardValuesTextView.setText(settingsStringBuilder("\n Main.Activity.backwardVibrate = " +
                MainActivity.vibrateBwdOn) + " Check Box is " +
                sharedPrefs.getBoolean("checkBoxBwd", false));

        TextView updateIntervalTextView = (TextView) findViewById(R.id.UpdateIntervaltextView);
        updateIntervalTextView.setText(settingsStringBuilder("\n Update Interval " +
                Integer.parseInt(sharedPrefs.getString("updates_interval", "1000"))));

        File extDir = getExternalFilesDir(null);
        TextView pathTextView = (TextView) findViewById(R.id.pathtextView);
        pathTextView.setText(settingsStringBuilder("\n Path to Files " + extDir));

    }

    private String settingsStringBuilder(String sbinputText) {
        StringBuilder builder = new StringBuilder();
        builder.append(sbinputText);
        return builder.toString();
    }

}
