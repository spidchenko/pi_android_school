package com.spidchenko.week2task;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRepository {
    private static final String TAG = "FavRepository.LOG_TAG";
    private DatabaseHelper mDb;
    private MutableLiveData<List<Favourite>> mFavourites;
    private int mUserId;

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

    //TODO Room will take care of this feature
    private void updateFavouritesLiveData() {
        new Thread(() -> {
            ArrayList<Favourite> favourites =
                    new ArrayList<>(mDb.getAllFavourites(mUserId, null));
            mDb.close();
            mFavourites.postValue(favourites);
            Log.d(TAG, "FavouritesRepository: favourites from DB: " + favourites.toString());
        }).start();
    }
}










