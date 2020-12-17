package com.spidchenko.week2task;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRepository {
    private static final String TAG = "FavRepository.LOG_TAG";
    private final Application mApplication;
    private final MutableLiveData<List<Favourite>> mFavourites;
    private final int mUserId;

    public FavouriteRepository(@NonNull Application application, int userId) {
        mApplication = application;
        mUserId = userId;
        mFavourites = new MutableLiveData<>();
        updateFavouritesLiveData();
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return mFavourites;
    }

    public void addFavorite(Favourite favourite, RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                DatabaseHelper mDb = DatabaseHelper.getInstance(mApplication);
                mDb.addFavorite(favourite);
                mDb.close();
                callback.onComplete(new Result.Success<>(true));
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        }).start();
    }

    public void deleteFavourite(Favourite favourite, RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "deleteFavourite: Inside thread. Favourite toRemove " + favourite);
                DatabaseHelper mDb = DatabaseHelper.getInstance(mApplication);
                mDb.deleteFavourite(favourite);
                mDb.close();
                Log.d(TAG, "deleteFavourite: Delete thread ended");
                callback.onComplete(new Result.Success<>(true));
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        }).start();

    }

    public void checkInFavourites(Favourite favourite, RepositoryCallback<Boolean> callback) {

        new Thread(() -> {
            try {
                DatabaseHelper mDb = DatabaseHelper.getInstance(mApplication);
                Favourite result = mDb.getFavourite(mUserId, favourite.getUrl());
                mDb.close();
                if (result != null) {
                    callback.onComplete(new Result.Success<>(true));
                } else {
                    callback.onComplete(new Result.Success<>(false));
                }
            } catch (Exception e) {
                callback.onComplete(new Result.Error<>(e));
            }
        }).start();
    }

    //Room will take care of this feature
    public void updateFavouritesLiveData() {
        new Thread(() -> {
            DatabaseHelper mDb = DatabaseHelper.getInstance(mApplication);
            ArrayList<Favourite> favourites =
                    new ArrayList<>(mDb.getAllFavourites(mUserId, null));
            mDb.close();
            mFavourites.postValue(favourites);
            Log.d(TAG, "FavouritesRepository: favourites from DB: " + favourites.toString());
        }).start();
    }


    public interface RepositoryCallback<T> {
        void onComplete(Result<T> result);
    }

}










