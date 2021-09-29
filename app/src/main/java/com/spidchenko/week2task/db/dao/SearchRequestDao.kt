package com.spidchenko.week2task.db.dao

import androidx.room.Dao
import com.spidchenko.week2task.db.models.SearchRequest
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchRequestDao {
    @Insert
    fun addSearchRequest(searchRequest: SearchRequest?): Long

    @Query("SELECT * FROM searches WHERE user_id = :userId ORDER BY id DESC")
    fun getAllSearchRequests(userId: Int): LiveData<List<SearchRequest?>?>?
}