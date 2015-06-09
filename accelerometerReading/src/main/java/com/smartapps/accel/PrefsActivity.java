package com.smartapps.accel;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by fraw on 9/06/2015.
 */
public class PrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
