package com.spidchenko.week2task.repositories

import androidx.lifecycle.LiveData
import com.spidchenko.week2task.db.dao.FavouriteDao
import com.spidchenko.week2task.db.models.Favourite
import com.spidchenko.week2task.network.Result
import java.util.concurrent.Executor

class FavouriteRepository private constructor(
    private val mFavouriteDao: FavouriteDao,
    private val mSharedPrefRepository: SharedPrefRepository,
    private val mExecutor: Executor
) {
    val favouritesWithCategories: LiveData<List<Favourite?>?>?
        get() {
            val userId = mSharedPrefRepository.userId
            return mFavouriteDao.getFavouritesWithCategories(userId)
        }

    fun addFavorite(favourite: Favourite?, callback: RepositoryCallback<Boolean>) {
        mExecutor.execute {
            try {
                mFavouriteDao.addFavourite(favourite)
                callback.onComplete(Result.Success(true))
            } catch (e: Exception) {
                callback.onComplete(Result.Error(e))
            }
        }
    }

    fun deleteFavourite(favourite: Favourite, callback: RepositoryCallback<Boolean>) {
        mExecutor.execute {
            try {
                mFavouriteDao.deleteFavourite(favourite.user, favourite.url)
                callback.onComplete(Result.Success(true))
            } catch (e: Exception) {
                callback.onComplete(Result.Error(e))
            }
        }
    }

    fun getFavourite(favourite: Favourite): LiveData<Favourite?>? {
        return mFavouriteDao.getFavourite(favourite.user, favourite.url)
    }

    interface RepositoryCallback<T> {
        fun onComplete(result: Result<T>?)
    }

    companion object {
        @Volatile
        private var sInstance: FavouriteRepository? = null

        @JvmStatic
        fun getInstance(
            favouriteDao: FavouriteDao,
            sharedPrefRepository: SharedPrefRepository,
            executor: Executor
        ): FavouriteRepository? {
            if (sInstance == null) {
                synchronized(FavouriteRepository::class.java) {
                    if (sInstance == null) {
                        sInstance =
                            FavouriteRepository(favouriteDao, sharedPrefRepository, executor)
                    }
                }
            }
            return sInstance
        }
    }
}