package com.spidchenko.week2task.db.dao

import androidx.room.Dao
import com.spidchenko.week2task.db.models.Favourite
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouriteDao {
    @Insert
    fun addFavourite(favourite: Favourite?)

    @Query("SELECT * FROM favourites WHERE user_id = :user AND url LIKE :url")
    fun getFavourite(user: Int, url: String?): LiveData<Favourite?>?

    @Query(
        "SELECT search_string, id, user_id, url " +
                "FROM favourites " +
                "WHERE user_id = :user " +
                "UNION " +
                "SELECT DISTINCT(search_string), 0, 0, \"\" " +
                "FROM favourites  " +
                "WHERE user_id = :user " +
                "ORDER BY search_string, id"
    )
    fun getFavouritesWithCategories(user: Int): LiveData<List<Favourite?>?>?

    @Query("DELETE FROM favourites WHERE user_id = :user AND url LIKE :url")
    fun deleteFavourite(user: Int, url: String?)
}