package com.spidchenko.week2task.db;

import android.util.Log;

import com.spidchenko.week2task.db.models.User;

public class CurrentUser {
    private static final String TAG = "CurrentUser";
    private static CurrentUser instance;
    private User user;

    private CurrentUser(){
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                '}';
    }

    public static CurrentUser getInstance(){
        if (instance == null) {
            instance = new CurrentUser();
            Log.d(TAG, "getInstance: Created");
        }
        return instance;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user=user;
    }

}