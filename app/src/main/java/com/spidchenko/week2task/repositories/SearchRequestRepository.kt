package com.spidchenko.week2task.repositories;

import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.List;
import java.util.concurrent.Executor;

public class SearchRequestRepository {

    private final SharedPrefRepository mSharedPrefRepository;
    private final Executor mExecutor;
    private final SearchRequestDao mSearchRequestDao;
    private static volatile SearchRequestRepository sInstance;

    private SearchRequestRepository(final SearchRequestDao searchRequestDao,
                                    final SharedPrefRepository sharedPrefRepository,
                                    final Executor executor) {
        mSharedPrefRepository = sharedPrefRepository;
        mSearchRequestDao = searchRequestDao;
        mExecutor = executor;
    }

    public static SearchRequestRepository getInstance(final SearchRequestDao searchRequestDao,
                                                      final SharedPrefRepository sharedPrefRepository,
                                                      final Executor executor) {
        if (sInstance == null) {
            synchronized (SearchRequestRepository.class) {
                if (sInstance == null) {
                    sInstance = new SearchRequestRepository(searchRequestDao, sharedPrefRepository, executor);
                }
            }
        }
        return sInstance;
    }

    public void saveCurrentSearchInDb(final SearchRequest request) {
        mExecutor.execute(() -> mSearchRequestDao.addSearchRequest(request));
    }

    public LiveData<List<SearchRequest>> getSearchRequests() {
        int userId = mSharedPrefRepository.getUserId();
        return mSearchRequestDao.getAllSearchRequests(userId);
    }

}
