package com.spidchenko.week2task.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.spidchenko.week2task.db.models.User;

public class CurrentUser {
    private static final String TAG = "CurrentUser.LOG_TAG";
    private static CurrentUser INSTANCE;
    private User mUser;

    private CurrentUser() {
    }

    public static CurrentUser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CurrentUser();
            Log.d(TAG, "getInstance: Created");
        }
        return INSTANCE;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + mUser +
                '}';
    }
}