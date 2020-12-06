package com.spidchenko.week2task.db.models;

public class SearchRequest {
    int id;
    int user;
    String searchRequest;
    long sDateTime;


    public SearchRequest(){

    }

    public SearchRequest(int id, int user, String searchRequest, long dateTime){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.sDateTime = dateTime;
    }

    public SearchRequest(int user, String searchRequest){
        this.user = user;
        this.searchRequest = searchRequest;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getUser(){
        return this.user;
    }

    public void setUser(int user){
        this.user = user;
    }

    public String getSearchRequest(){
        return this.searchRequest;
    }

    public void setSearchRequest(String searchRequest){
        this.searchRequest = searchRequest;
    }

    public long getDateTime(){
        return this.sDateTime;
    }

    public void setDate(long sDateTime){
        this.sDateTime = sDateTime;
    }
}
