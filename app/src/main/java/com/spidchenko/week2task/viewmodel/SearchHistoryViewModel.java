package com.spidchenko.week2task.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.repositories.SearchRequestRepository;

import java.util.List;

public class SearchHistoryViewModel extends ViewModel {

    private final SearchRequestRepository mSearchRequestRepository;


    public SearchHistoryViewModel(SearchRequestRepository searchRequestRepository) {
        mSearchRequestRepository = searchRequestRepository;
    }

    public LiveData<List<SearchRequest>> getSearchRequests(){
        return mSearchRequestRepository.getSearchRequests();
    }
}
