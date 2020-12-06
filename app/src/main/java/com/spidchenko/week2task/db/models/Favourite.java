package com.spidchenko.week2task.db.models;

public class Favourite {
    int id;
    int user;
    String searchRequest;
    String title;
    String url;


    public Favourite(){}

    public Favourite(int id, int user, String searchRequest, String title, String url){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.title = title;
        this.url = url;
    }

    public Favourite(int user, String searchRequest, String title, String url){
        this.user = user;
        this.searchRequest = searchRequest;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "id=" + id +
                ", user=" + user +
                ", searchRequest='" + searchRequest + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
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

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
