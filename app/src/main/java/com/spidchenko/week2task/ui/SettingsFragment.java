package com.spidchenko.week2task.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SyncWorker;

import java.util.concurrent.TimeUnit;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String PREF_NIGHT_MODE = "night_mode";
    public static final String PREF_BACKGROUND_UPDATE = "background_update";
    public static final String PREF_UPDATE_TEXT = "request";
    public static final String PREF_UPDATE_INTERVAL = "interval hours";
    private static final String TAG = "SettingsFrag.LOG_TAG";

    private final SharedPreferences.OnSharedPreferenceChangeListener mListener =
            (sharedPreferences, key) -> {

                if (key.equals(PREF_NIGHT_MODE)) {

                    if (sharedPreferences.getBoolean(key, false)) {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    }

                } else {

                    boolean isBackgroundUpdatesEnabled =
                            sharedPreferences.getBoolean(PREF_BACKGROUND_UPDATE, false);
                    String updateText = sharedPreferences.getString(PREF_UPDATE_TEXT, "");
                    int updateInterval = Integer.parseInt(
                            sharedPreferences.getString(PREF_UPDATE_INTERVAL, "0"));

                    if (isBackgroundUpdatesEnabled &&
                            !updateText.isEmpty() &&
                            updateInterval > 0) {
                        Log.d(TAG, "onSharedPreferenceChanged: Starting background task... Text: " + updateText + ". Interval: " + updateInterval);

                        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                                SyncWorker.class,
                                updateInterval,
                                TimeUnit.MINUTES).build();

                        WorkManager.getInstance(getContext())
                                .enqueueUniquePeriodicWork("sync",
                                        ExistingPeriodicWorkPolicy.REPLACE,
                                        workRequest);
                    } else {
                        WorkManager.getInstance(getContext()).cancelUniqueWork("sync");
                        Log.d(TAG, "onSharedPreferenceChanged: Background task canceled");
                    }

                }

            };


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

}