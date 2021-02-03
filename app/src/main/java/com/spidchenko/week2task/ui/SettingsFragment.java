package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.spidchenko.week2task.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_NIGHT_MODE = "night_mode";
    private static final String TAG = "SettingsFrag.LOG_TAG";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreferenceCompat nightMode = findPreference(PREF_NIGHT_MODE);
        if (nightMode != null) {
            nightMode.setOnPreferenceChangeListener((preference, newValue) -> {
                Log.d(TAG, "onChanged: pref: " + preference + ". val: " + newValue);
                if ((Boolean) newValue) {
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            });
        }

    }


}