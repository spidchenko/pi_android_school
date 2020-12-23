package com.spidchenko.week2task.db.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class Favourite {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int mId;

    @ColumnInfo(name = "user_id")
    int mUser;

    @ColumnInfo(name = "search_string")
    String mSearchRequest;

    @ColumnInfo(name = "url")
    String mUrl;


    public Favourite() {
    }


    @Ignore
    public Favourite(int user, String searchRequest, String url) {
        this.mUser = user;
        this.mSearchRequest = searchRequest;
        this.mUrl = url;
    }


    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getUser() {
        return this.mUser;
    }

    public void setUser(int user) {
        this.mUser = user;
    }

    public String getSearchRequest() {
        return this.mSearchRequest;
    }

    public void setSearchRequest(String searchRequest) {
        this.mSearchRequest = searchRequest;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @NonNull
    @Override
    public String toString() {
        return "Favourite{" +
                "id=" + mId +
                ", user=" + mUser +
                ", searchRequest='" + mSearchRequest + '\'' +
                ", url='" + mUrl + '\'' +
                '}';
    }
}
