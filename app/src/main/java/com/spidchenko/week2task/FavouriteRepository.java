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
    private final DatabaseHelper mDb;
    private final MutableLiveData<List<Favourite>> mFavourites;
    private final int mUserId;

    public FavouriteRepository(@NonNull Application application, int userId) {
        mDb = DatabaseHelper.getInstance(application);
        mUserId = userId;
        mFavourites = new MutableLiveData<>();
        updateFavouritesLiveData();
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return mFavourites;
    }

    public void deleteFavourite(Favourite favourite) {
        new Thread(() -> {
            Log.d(TAG, "deleteFavourite: Inside thread. Favourite toRemove " + favourite);
            mDb.deleteFavourite(favourite);
            mDb.close();
            Log.d(TAG, "deleteFavourite: Delete thread ended");
            updateFavouritesLiveData();
        }).start();

    }

    public void checkInFavourites(String url, RepositoryCallback<Boolean> callback) {

        new Thread(() -> {
            Favourite favourite = mDb.getFavourite(mUserId, url);
            mDb.close();
            //TODO send callback here
            if (favourite != null) {
                callback.onComplete(new Result.Success<Boolean>() {
                });
            }
//            mUiHandler.post(() -> {
//                cbToggleFavourite.setClickable(true);
//                if (mFavourite != null) {
//                    cbToggleFavourite.setChecked(true);
//                    Log.d(TAG, "checkInFavourites: Already in Favourites!");
//                } else {
//                    cbToggleFavourite.setChecked(false);
//                    Log.d(TAG, "checkInFavourites: Not in Favourites!");
//                }
//            });

        }).start();
    }

    //Room will take care of this feature
    private void updateFavouritesLiveData() {
        new Thread(() -> {
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










