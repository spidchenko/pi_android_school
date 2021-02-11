package com.spidchenko.week2task.db.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "searches")
public class SearchRequest {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "user_id")
    private int mUserId;

    @ColumnInfo(name = "search_string")
    private String mSearchRequest;

    public SearchRequest() {
    }

    @Ignore
    public SearchRequest(int userId, String searchRequest) {
        this.mUserId = userId;
        this.mSearchRequest = searchRequest;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getSearchRequest() {
        return this.mSearchRequest;
    }

    public void setSearchRequest(String searchRequest) {
        this.mSearchRequest = searchRequest;
    }

}
