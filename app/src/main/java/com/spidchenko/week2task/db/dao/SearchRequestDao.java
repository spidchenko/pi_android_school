package com.spidchenko.week2task.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.List;

@Dao
public interface SearchRequestDao {

    @Insert
    void addSearchRequest(SearchRequest searchRequest);

    @Query()
    SearchRequest getLastSearchRequest(int userId);

    @Query()
    LiveData<List<SearchRequest>> getAllSearchRequests(int userId);

    @Delete
    void deleteSearchRequest(SearchRequest searchRequest);

}
