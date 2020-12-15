package com.spidchenko.week2task.db.models;

public class Favourite {
    int mId;
    int mUser;
    String mSearchRequest;
    String mTitle;
    String mUrl;


    public Favourite() {
    }

    public Favourite(int id, int user, String searchRequest, String title, String url) {
        this.mId = id;
        this.mUser = user;
        this.mSearchRequest = searchRequest;
        this.mTitle = title;
        this.mUrl = url;
    }

    public Favourite(int user, String searchRequest, String title, String url) {
        this.mUser = user;
        this.mSearchRequest = searchRequest;
        this.mTitle = title;
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

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "id=" + mId +
                ", user=" + mUser +
                ", searchRequest='" + mSearchRequest + '\'' +
                ", title='" + mTitle + '\'' +
                ", url='" + mUrl + '\'' +
                '}';
    }
}
