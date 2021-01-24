package com.spidchenko.week2task.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefRepository {
    private static final String PREF_FILE_KEY = "com.spidchenko.week2task.PREF_FILE_KEY";
    private static final String PREF_USER_ID_KEY = "com.spidchenko.week2task.PREF_LOGIN_KEY";
    private static final String PREF_LAST_SEARCH_KEY = "com.spidchenko.week2task.PREF_LAST_SEARCH_KEY";
    private static final String TAG = "SharedPrefRepo.LOG_TAG";

    private static volatile SharedPrefRepository sInstance;
    private static SharedPreferences sSharedPreferences;


    private SharedPrefRepository(final Application application) {
        sSharedPreferences = application.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
    }

    public static SharedPrefRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (SharedPrefRepository.class) {
                if (sInstance == null) {
                    sInstance = new SharedPrefRepository(application);
                }
            }
        }
        return sInstance;
    }

    public int getUserId() {
        Log.d(TAG, "getLogin: " + sSharedPreferences.getInt(PREF_USER_ID_KEY, -1));
        return sSharedPreferences.getInt(PREF_USER_ID_KEY, -1);
    }

    public String getLastSearch() {
        Log.d(TAG, "getLastSearch: " + sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, ""));
        return sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, "");
    }

    public void saveUserId(int userId) {
        Log.d(TAG, "saveUserId: " + userId);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putInt(PREF_USER_ID_KEY, userId);
        editor.apply();
    }

    public void saveLastSearch(String lastSearch) {
        Log.d(TAG, "saveLastSearch: " + lastSearch);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(PREF_LAST_SEARCH_KEY, lastSearch);
        editor.apply();
    }
}
