package com.spidchenko.week2task.db.dao;

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
    Favourite getFavourite(int user, String url);

    @Query("SELECT * FROM favourites WHERE user_id = :user ORDER BY search_string ASC")
    List<Favourite> getAllFavourites(int user);

    @Query("DELETE FROM favourites WHERE user_id = :user AND url LIKE :url")
    void deleteFavourite(int user, String url);

}
