package com.spidchenko.week2task.repositories

import androidx.lifecycle.LiveData
import com.spidchenko.week2task.db.dao.SyncImageDao
import com.spidchenko.week2task.db.models.SyncImage
import java.util.concurrent.Executor

class SyncImageRepository private constructor(
    private val mSyncImageDao: SyncImageDao,
    private val mExecutor: Executor
) {
    val allImages: LiveData<List<SyncImage?>?>?
        get() = mSyncImageDao.allImages

    fun deleteImage(image: SyncImage) {
        mExecutor.execute { mSyncImageDao.deleteSyncImage(image.url) }
    }

    companion object {
        @Volatile
        private var sInstance: SyncImageRepository? = null

        @JvmStatic
        fun getInstance(
            syncImageDao: SyncImageDao,
            executor: Executor
        ): SyncImageRepository? {
            if (sInstance == null) {
                synchronized(FavouriteRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = SyncImageRepository(syncImageDao, executor)
                    }
                }
            }
            return sInstance
        }
    }
}