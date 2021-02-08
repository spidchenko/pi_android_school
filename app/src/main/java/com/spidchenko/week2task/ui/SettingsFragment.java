package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.spidchenko.week2task.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_NIGHT_MODE = "night_mode";
    private static final String TAG = "SettingsFrag.LOG_TAG";
    private static final int APPLY_SETTINGS_DELAY = 500;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreferenceCompat nightMode = findPreference(PREF_NIGHT_MODE);
        if (nightMode != null) {
            nightMode.setOnPreferenceChangeListener((preference, newValue) -> {
                setNightModeAsynchronously((boolean) newValue);
                return true;
            });
        }
    }

    private void setNightModeAsynchronously(boolean isNightMode) {
        if (isNightMode) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_YES);
            }, APPLY_SETTINGS_DELAY);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_NO);
            }, APPLY_SETTINGS_DELAY);
        }
    }

}