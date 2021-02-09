package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.spidchenko.week2task.R;

import java.util.Objects;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_NIGHT_MODE = "night_mode";
    public static final String PREF_BACKGROUND_UPDATE = "background_update";
    public static final String PREF_UPDATE_TEXT = "request";
    public static final String PREF_UPDATE_INTERVAL = "interval hours";
    public static final String PREF_CATEGORY_BACKGROUND_WORK = "background_work_category";
    private static final String TAG = "SettingsFrag.LOG_TAG";
    private static final int APPLY_SETTINGS_DELAY = 200;

    private SwitchPreferenceCompat nightMode;
    private SwitchPreferenceCompat backgroundUpdate;
    private EditTextPreference keyword;
    private ListPreference updateInterval;

    // TODO SharedPreferences.OnSharedPreferenceChangeListener
    // https://developer.android.com/guide/topics/ui/settings/use-saved-values

    private Preference.OnPreferenceChangeListener mListener = (preference, newValue) -> {

        if (preference.getKey().equals(PREF_NIGHT_MODE)) {
            setNightModeAsynchronously((boolean) newValue);
            return true;
        } else {

            // if one of background work settings
            if (Objects.requireNonNull(preference.getParent()).getKey()
                    .equals(PREF_CATEGORY_BACKGROUND_WORK)) {
                updateBackgroundTask();
            }

            Log.d(TAG, "backgroundUpdate.isChecked(): " + backgroundUpdate.isChecked());
            Log.d(TAG, "keyword.getText(): " + keyword.getText());
            Log.d(TAG, "updateInterval.getValue(): " + updateInterval.getValue());

//            if (backgroundUpdate.isChecked() && !keyword.getText().trim().isEmpty() && updateInterval.getValue())

        }

        if (preference.getKey().equals(PREF_BACKGROUND_UPDATE)) {

        }

        // TODO
        Log.d(TAG, "pref parent: " + preference.getParent());
        return true;
    };

    private void updateBackgroundTask() {

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        nightMode = findPreference(PREF_NIGHT_MODE);
        if (nightMode != null) {
            nightMode.setOnPreferenceChangeListener(mListener);
//            nightMode.setOnPreferenceChangeListener((preference, isEnabled) -> {
//                setNightModeAsynchronously((boolean) isEnabled);
//                return true;
//            });
        }

        backgroundUpdate = findPreference(PREF_BACKGROUND_UPDATE);
        if (backgroundUpdate != null) {
            backgroundUpdate.setOnPreferenceChangeListener(mListener);
        }

        keyword = findPreference(PREF_UPDATE_TEXT);
        if (keyword != null) {
            keyword.setOnPreferenceChangeListener(mListener);
        }

        updateInterval = findPreference(PREF_UPDATE_INTERVAL);
        if (updateInterval != null) {
            updateInterval.setOnPreferenceChangeListener(mListener);
        }

//            backgroundUpdate.setOnPreferenceChangeListener((preference, isEnabled) -> {
//                if ((boolean) isEnabled) {
////                    PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 1, TimeUnit.HOURS).build();
//                } else {
//
//                }
//            });
    }


    private void setNightModeAsynchronously(boolean isNightMode) {
        if (isNightMode) {
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES), APPLY_SETTINGS_DELAY);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO), APPLY_SETTINGS_DELAY);
        }
    }

}