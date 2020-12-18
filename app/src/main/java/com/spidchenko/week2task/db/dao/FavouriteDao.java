package com.spidchenko.week2task.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.spidchenko.week2task.db.models.Favourite;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert
    void addFavourite(Favourite favourite);

    @Query("SELECT * FROM favourites WHERE id = :id")
    Favourite getFavourite(int id);

    @Query()
    Favourite getFavourite(int user, String url);

    @Query()
    LiveData<List<Favourite>> getAllFavourites(int user, String searchRequest);

    @Update()
    int updateFavourite(Favourite favourite);

    @Delete
    void deleteFavourite(Favourite favourite);

}
