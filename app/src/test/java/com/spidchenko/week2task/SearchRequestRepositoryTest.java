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

    private final Executor mExecutor = new CurrentThreadExecutor();
    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule mExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private SearchRequestDao mSearchRequestDao;
    @Mock
    private SharedPrefRepository mSharedPrefRepository;
    private SearchRequestRepository mSearchRequestRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mSearchRequestRepository = SearchRequestRepository.getInstance(mSearchRequestDao, mSharedPrefRepository, mExecutor);
    }

    @Test
    public void saveCurrentSearchInDb_callDao() {
        SearchRequest request = new SearchRequest(42, "Test");
        mSearchRequestRepository.saveCurrentSearchInDb(request);
        Mockito.verify(mSearchRequestDao).addSearchRequest(request);
    }

    static private class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }
}
