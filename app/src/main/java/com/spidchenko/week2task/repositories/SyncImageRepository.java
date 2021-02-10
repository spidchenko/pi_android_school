package com.spidchenko.week2task.repositories;

import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.db.dao.SyncImageDao;
import com.spidchenko.week2task.db.models.SyncImage;

import java.util.List;
import java.util.concurrent.Executor;

public class SyncImageRepository {
    private final SyncImageDao mSyncImageDao;
    private static volatile SyncImageRepository sInstance;
    private final Executor mExecutor;

    private SyncImageRepository(final SyncImageDao syncImageDao,
                                final Executor executor) {
        mExecutor = executor;
        mSyncImageDao = syncImageDao;
    }

    public static SyncImageRepository getInstance(final SyncImageDao syncImageDao,
                                                  final Executor executor) {
        if (sInstance == null) {
            synchronized (FavouriteRepository.class) {
                if (sInstance == null) {
                    sInstance = new SyncImageRepository(syncImageDao, executor);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<SyncImage>> getAllImages() {
        return mSyncImageDao.getAllImages();
    }

    public void deleteImage(SyncImage image) {
        mExecutor.execute(() -> mSyncImageDao.deleteSyncImage(image.getUrl())
        );
    }
}
