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

public class ShowSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_display);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        CheckBox forwardVibrateCheckBox = (CheckBox) findViewById(R.id.ForwardVibratecheckBox);
        forwardVibrateCheckBox.setText(settingsStringBuilder("\n Forward Vibrate is " + sharedPrefs.getBoolean("checkBoxFwd", false)));
        forwardVibrateCheckBox.setChecked(MainActivity.vibrateFwdOn);

        CheckBox backwardVibrateCheckBox = (CheckBox) findViewById(R.id.BackwardVibratecheckBox);
        backwardVibrateCheckBox.setText(settingsStringBuilder("\n Backward Vibrate is " + sharedPrefs.getBoolean("checkBoxBwd", false)));
        backwardVibrateCheckBox.setChecked(MainActivity.vibrateBwdOn);

        TextView updateIntervalTextView = (TextView) findViewById(R.id.UpdateIntervaltextView);
        updateIntervalTextView.setText(settingsStringBuilder("\n Update Interval " + Integer.parseInt(sharedPrefs.getString("updates_interval", "1000"))));

    }
    private String settingsStringBuilder(String sbinputText) {
        StringBuilder builder = new StringBuilder();
        builder.append(sbinputText);
        return builder.toString();
    }

}
