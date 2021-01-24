package com.spidchenko.week2task.repositories;

import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.util.List;
import java.util.concurrent.Executor;

public class FavouriteRepository {
    private final FavouriteDao mFavouriteDao;
    private static volatile FavouriteRepository sInstance;
    private final SharedPrefRepository mSharedPrefRepository;
    private final Executor mExecutor;


    private FavouriteRepository(final AppDatabase database,
                                final SharedPrefRepository sharedPrefRepository,
                                final Executor executor) {
        mSharedPrefRepository = sharedPrefRepository;
        mExecutor = executor;
        mFavouriteDao = database.favouriteDao();
    }

    public static FavouriteRepository getInstance(final AppDatabase database,
                                                  final SharedPrefRepository sharedPrefRepository,
                                                  final Executor executor) {
        if (sInstance == null) {
            synchronized (FavouriteRepository.class) {
                if (sInstance == null) {
                    sInstance = new FavouriteRepository(database, sharedPrefRepository, executor);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Favourite>> getFavouritesWithCategories() {
        int userId = mSharedPrefRepository.getUserId();
        return mFavouriteDao.getFavouritesWithCategories(userId);
    }

    public void addFavorite(final Favourite favourite, RepositoryCallback<Boolean> callback) {
        mExecutor.execute(() -> {
            try {
                mFavouriteDao.addFavourite(favourite);
                callback.onComplete(new Result.Success<>(true));
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
    }

    public void deleteFavourite(final Favourite favourite, final RepositoryCallback<Boolean> callback) {
        mExecutor.execute(() -> {
            try {
                mFavouriteDao.deleteFavourite(favourite.getUser(), favourite.getUrl());
                callback.onComplete(new Result.Success<>(true));
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
    }

    public LiveData<Favourite> getFavourite(final Favourite favourite) {
        return mFavouriteDao.getFavourite(favourite.getUser(), favourite.getUrl());
    }

    public interface RepositoryCallback<T> {
        void onComplete(Result<T> result);
    }

}










