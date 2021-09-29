package com.spidchenko.week2task.db.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    private static final String TAG = "User.LOG_TAG";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "login")
    private String mLogin;

    public User() {
    }

    @Ignore
    public User(@NonNull String login) {
        this.mLogin = login;
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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + mId +
                ", login='" + mLogin + '\'' +
                '}';
    }
}
