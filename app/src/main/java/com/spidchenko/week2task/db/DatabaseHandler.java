package com.spidchenko.week2task.db;

import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.db.models.User;

import java.util.ArrayList;

public interface DatabaseHandler {

    public void addUser(User user);
    public User getUser(String login);

    public void addFavorite(Favourite favourite);
    public Favourite getFavourite(int id);
    public Favourite getFavourite(int user, String url);
    public ArrayList<Favourite> getAllFavourites(int user, String searchRequest);
    public int updateFavourite(Favourite favourite);
    public void deleteFavourite(Favourite favourite);

    public void addSearchRequest(SearchRequest searchRequest);
    public SearchRequest getLastSearchRequest(int user);
    public ArrayList<SearchRequest> getAllSearchRequests(int user);
    public void deleteSearchRequest(SearchRequest searchRequest);


}
