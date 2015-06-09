package com.smartapps.accel;

/**
 * Created by fraw on 9/06/2015.
 */
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class ShowSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_display);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();

        builder.append("\n ChkBoxF " + sharedPrefs.getBoolean("checkBoxFwd", false));
        builder.append("\n ChkBoxB " + sharedPrefs.getBoolean("checkBoxBwd", false));
        builder.append("\n VibF " + MainActivity.vibrateFwdOn);
        builder.append("\n VibB " + MainActivity.vibrateBwdOn);
        builder.append("\n UpdateInt " + Integer.parseInt(sharedPrefs.getString("updates_interval", "1000")));

        TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
        settingsTextView.setText(builder.toString());

    }

}
