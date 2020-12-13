package com.spidchenko.week2task.db;

import android.util.Log;

import com.spidchenko.week2task.db.models.User;

public class CurrentUser {
    private static final String TAG = "CurrentUser.LOG_TAG";
    private static CurrentUser sInstance;
    private User mUser;

    private CurrentUser() {
    }

    public static CurrentUser getInstance() {
        if (sInstance == null) {
            sInstance = new CurrentUser();
            Log.d(TAG, "getInstance: Created");
        }
        return sInstance;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + mUser +
                '}';
    }
}