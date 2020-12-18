package com.spidchenko.week2task.db.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    private static final String TAG = "User.LOG_TAG";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    int mId;

    @NonNull
    @ColumnInfo(name = "login")
    String mLogin;

    public User(int id, String login) {
        this.mId = id;
        this.mLogin = login;
    }

    public User(String login) {
        this.mLogin = login;
        Log.d(TAG, "User: Created " + login);
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getLogin() {
        return this.mLogin;
    }

    public void setLogin(String login) {
        this.mLogin = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + mId +
                ", login='" + mLogin + '\'' +
                '}';
    }
}
