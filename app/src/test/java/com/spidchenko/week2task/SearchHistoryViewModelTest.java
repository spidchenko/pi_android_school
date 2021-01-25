package com.spidchenko.week2task;

import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.viewmodel.SearchHistoryViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class SearchHistoryViewModelTest {

    private SearchHistoryViewModel searchHistoryViewModel;

    @Mock
    private SearchRequestRepository searchRequestRepositoryMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        searchHistoryViewModel = new SearchHistoryViewModel(searchRequestRepositoryMock);
    }

    @Test
    public void getSearchRequests_callRepo() {
        searchHistoryViewModel.getSearchRequests();
        Mockito.verify(searchRequestRepositoryMock).getSearchRequests();
    }

    @Test
    public void getSearchRequests_returnValidResult() {
        MutableLiveData<List<SearchRequest>> requests = new MutableLiveData<>();
        Mockito.when(searchRequestRepositoryMock.getSearchRequests()).thenReturn(requests);
        Assert.assertEquals(searchHistoryViewModel.getSearchRequests(), requests);
    }

}
