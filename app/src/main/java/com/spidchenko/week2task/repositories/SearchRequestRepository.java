package com.spidchenko.week2task.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.List;
import java.util.concurrent.Executor;

public class SearchRequestRepository {

    private final SharedPrefRepository mSharedPrefRepository;
    private final Executor mExecutor;
    private final AppDatabase mAppDatabase;
    private MutableLiveData<List<SearchRequest>> mSearchRequests;
    private static volatile SearchRequestRepository sInstance;

    private SearchRequestRepository(final AppDatabase appDatabase,
                                    final SharedPrefRepository sharedPrefRepository,
                                    final Executor executor) {
        mSharedPrefRepository = sharedPrefRepository;
        mAppDatabase = appDatabase;
        mExecutor = executor;
    }

    public static SearchRequestRepository getInstance(final AppDatabase appDatabase,
                                                      final SharedPrefRepository sharedPrefRepository,
                                                      final Executor executor) {
        if (sInstance == null) {
            synchronized (SearchRequestRepository.class) {
                if (sInstance == null) {
                    sInstance = new SearchRequestRepository(appDatabase, sharedPrefRepository, executor);
                }
            }
        }
        return sInstance;
    }

    public void saveCurrentSearchInDb(final SearchRequest request) {
        mExecutor.execute(() -> mAppDatabase.searchRequestDao().addSearchRequest(request));
    }

    public LiveData<List<SearchRequest>> getSearchRequests() {
        if (mSearchRequests == null) {
            mSearchRequests = new MutableLiveData<>();
        }
        int userId = mSharedPrefRepository.getUserId();
        return mAppDatabase.searchRequestDao().getAllSearchRequests(userId);
    }

}
