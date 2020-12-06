package com.spidchenko.week2task.db;

import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.db.models.User;

import java.util.ArrayList;

public interface DatabaseHandler {

    void addUser(User user);

    User getUser(String login);

    void addFavorite(Favourite favourite);

    Favourite getFavourite(int id);

    Favourite getFavourite(int user, String url);

    ArrayList<Favourite> getAllFavourites(int user, String searchRequest);

    int updateFavourite(Favourite favourite);

    void deleteFavourite(Favourite favourite);

    void addSearchRequest(SearchRequest searchRequest);

    SearchRequest getLastSearchRequest(int user);

    ArrayList<SearchRequest> getAllSearchRequests(int user);

    void deleteSearchRequest(SearchRequest searchRequest);


}
