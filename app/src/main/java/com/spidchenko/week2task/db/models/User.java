package com.spidchenko.week2task.db.models;

import android.util.Log;

public class User {
    private static final String TAG = "User";

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }

    int id;
    String login;

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public User(String login) {
        this.login = login;
        Log.d(TAG, "User: Created " + login);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
