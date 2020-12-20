package com.spidchenko.week2task.db.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "searches")
public class SearchRequest {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int mId;

    @ColumnInfo(name = "user_id")
    int mUser;

    @ColumnInfo(name = "search_string")
    String mSearchRequest;

    public SearchRequest() {
    }

    @Ignore
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

}
