package com.spidchenko.week2task;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class FavouriteRepository {
    private static final String TAG = "FavRepository.LOG_TAG";
    private final MutableLiveData<List<Favourite>> mFavourites = new MutableLiveData<>();
    private final FavouriteDao mFavouriteDao;
    private static volatile FavouriteRepository sInstance;
    private final int mUserId;
    private final Executor mExecutor;


    private FavouriteRepository(final AppDatabase database,
                                final CurrentUser user,
                                final Executor executor) {
//    FavouriteDao dao, Executor executor, int userId) {
        mUserId = user.getUser().getId();
        mExecutor = executor;
//        mExecutor = Executors.newFixedThreadPool(4);
        mFavouriteDao = database.favouriteDao();
        updateFavouritesLiveData();
        Log.d(TAG, "FavouriteRepository: userId=" + mUserId + ". dao=" + mFavouriteDao);
    }

    public static FavouriteRepository getInstance(final AppDatabase database,
                                                  final CurrentUser user,
                                                  final Executor executor) {
        if (sInstance == null) {
            synchronized (FavouriteRepository.class) {
                if (sInstance == null) {
                    sInstance = new FavouriteRepository(database, user, executor);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return mFavourites;
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

    public void checkInFavourites(final Favourite favourite, RepositoryCallback<Boolean> callback) {
        mExecutor.execute(() -> {
            try {
                Favourite result = mFavouriteDao.getFavourite(favourite.getUser(), favourite.getUrl());
                if (result != null) {
                    callback.onComplete(new Result.Success<>(true));
                } else {
                    callback.onComplete(new Result.Success<>(false));
                }
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        });
    }

    public void updateFavouritesLiveData() {
        mExecutor.execute(() -> {
            ArrayList<Favourite> favourites =
                    new ArrayList<>(prepareList(mFavouriteDao.getAllFavourites(mUserId)));
            mFavourites.postValue(favourites);
            Log.d(TAG, "FavouritesRepository: favourites from DB: " + favourites.toString());
        });
    }

    private List<Favourite> prepareList(List<Favourite> inList) {
        Log.d(TAG, "prepareList: in: " + inList.toString());
        List<Favourite> outList = new LinkedList<>();
        String tempRequest = "";
        for (Favourite fav : inList) {
            String searchRequest = fav.getSearchRequest();
            if (!searchRequest.equals(tempRequest)) {
                outList.add(new Favourite(mUserId, searchRequest, ""));
            }
            outList.add(fav);
            tempRequest = searchRequest;
        }
        return outList;
    }

    public interface RepositoryCallback<T> {
        void onComplete(Result<T> result);
    }

}










