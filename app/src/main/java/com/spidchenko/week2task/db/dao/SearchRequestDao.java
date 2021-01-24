package com.spidchenko.week2task.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.List;

@Dao
public interface SearchRequestDao {

    @Insert
    long addSearchRequest(SearchRequest searchRequest);

    @Query("SELECT * FROM searches WHERE user_id = :userId ORDER BY id DESC")
    LiveData<List<SearchRequest>> getAllSearchRequests(int userId);

}
