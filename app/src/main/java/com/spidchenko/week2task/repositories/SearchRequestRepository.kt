package com.spidchenko.week2task.repositories

import androidx.lifecycle.LiveData
import com.spidchenko.week2task.db.dao.SearchRequestDao
import com.spidchenko.week2task.db.models.SearchRequest
import java.util.concurrent.Executor

class SearchRequestRepository private constructor(
    private val mSearchRequestDao: SearchRequestDao,
    private val mSharedPrefRepository: SharedPrefRepository,
    private val mExecutor: Executor
) {
    fun saveCurrentSearchInDb(request: SearchRequest?) {
        mExecutor.execute { mSearchRequestDao.addSearchRequest(request) }
    }

    val searchRequests: LiveData<List<SearchRequest?>?>?
        get() {
            val userId = mSharedPrefRepository.userId
            return mSearchRequestDao.getAllSearchRequests(userId)
        }

    companion object {
        @Volatile
        private var sInstance: SearchRequestRepository? = null

        @JvmStatic
        fun getInstance(
            searchRequestDao: SearchRequestDao,
            sharedPrefRepository: SharedPrefRepository,
            executor: Executor
        ): SearchRequestRepository? {
            if (sInstance == null) {
                synchronized(SearchRequestRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = SearchRequestRepository(
                            searchRequestDao,
                            sharedPrefRepository,
                            executor
                        )
                    }
                }
            }
            return sInstance
        }
    }
}