package com.spidchenko.week2task.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesHelper {
    private static final String PREF_FILE_KEY = "com.spidchenko.week2task.PREF_FILE_KEY";
    private static final String PREF_LOGIN_KEY = "com.spidchenko.week2task.PREF_LOGIN_KEY";
    private static final String PREF_LAST_SEARCH_KEY = "com.spidchenko.week2task.PREF_LAST_SEARCH_KEY";
    private static final String TAG = "SharedPreferencesHelper";

    private static SharedPreferences sSharedPreferences;

    private SharedPreferencesHelper() {
    }

    public static SharedPreferencesHelper init(Context context) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
        }
        Log.d(TAG, "init: Initialized");
        return new SharedPreferencesHelper();
    }

    public String getLogin() {
        Log.d(TAG, "getLogin: " + sSharedPreferences.getString(PREF_LOGIN_KEY, ""));
        return sSharedPreferences.getString(PREF_LOGIN_KEY, "");
    }

    public String getLastSearch() {
        Log.d(TAG, "getLastSearch: " + sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, ""));
        return sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, "");
    }

    public void saveLogin(String login) {
        Log.d(TAG, "saveLogin: " + login);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(PREF_LOGIN_KEY, login);
        editor.apply();
    }

    public void saveLastSearch(String lastSearch) {
        Log.d(TAG, "saveLastSearch: " + lastSearch);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(PREF_LAST_SEARCH_KEY, lastSearch);
        editor.apply();
    }
}
