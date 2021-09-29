package com.spidchenko.week2task.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.spidchenko.week2task.db.models.Favourite;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert
    void addFavourite(Favourite favourite);

    @Query("SELECT * FROM favourites WHERE user_id = :user AND url LIKE :url")
    LiveData<Favourite> getFavourite(int user, String url);

    @Query("SELECT search_string, id, user_id, url " +
            "FROM favourites " +
            "WHERE user_id = :user " +
            "UNION " +
            "SELECT DISTINCT(search_string), 0, 0, \"\" " +
            "FROM favourites  " +
            "WHERE user_id = :user " +
            "ORDER BY search_string, id")
    LiveData<List<Favourite>> getFavouritesWithCategories(int user);

    @Query("DELETE FROM favourites WHERE user_id = :user AND url LIKE :url")
    void deleteFavourite(int user, String url);

}
