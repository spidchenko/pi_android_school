package com.spidchenko.week2task.db.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "searches")
public class SearchRequest {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "searches")
    int mId;

    @NonNull
    @ColumnInfo(name = "user_id")
    int mUser;

    @NonNull
    @ColumnInfo(name = "search_string")
    String mSearchRequest;

    @ColumnInfo(name = "date")
    long mDateTime;


    public SearchRequest() {

    }

    public SearchRequest(int id, int user, String searchRequest, long dateTime) {
        this.mId = id;
        this.mUser = user;
        this.mSearchRequest = searchRequest;
        this.mDateTime = dateTime;
    }

    public SearchRequest(int user, String searchRequest) {
        this.mUser = user;
        this.mSearchRequest = searchRequest;
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

    public long getDateTime() {
        return this.mDateTime;
    }

    public void setDate(long sDateTime) {
        this.mDateTime = sDateTime;
    }
}
