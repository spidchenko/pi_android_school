package com.spidchenko.week2task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

public class SearchRequestRepositoryTest {

    private final Executor executor = new CurrentThreadExecutor();
    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();
    @Mock
    private SearchRequestDao searchRequestDao;
    @Mock
    private SharedPrefRepository sharedPrefRepository;
    private SearchRequestRepository searchRequestRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        searchRequestRepository = SearchRequestRepository.getInstance(searchRequestDao, sharedPrefRepository, executor);
    }

    @Test
    public void saveCurrentSearchInDb_callDao() {
        SearchRequest request = new SearchRequest(42, "Test");
        searchRequestRepository.saveCurrentSearchInDb(request);
        Mockito.verify(searchRequestDao).addSearchRequest(request);
    }

    static private class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }
}
