package com.spidchenko.week2task;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// TODO: 12/22/20 rework threading approach.
public class FavouriteRepository {
    private static final String TAG = "FavRepository.LOG_TAG";
    private final MutableLiveData<List<Favourite>> mFavourites = new MutableLiveData<>();
    private final FavouriteDao mFavouriteDao;
    private final int mUserId;

    public FavouriteRepository(@NonNull Application application, int userId) {
        mUserId = userId;
        FlickrRoomDatabase db = FlickrRoomDatabase.getDatabase(application);
        mFavouriteDao = db.favouriteDao();
        updateFavouritesLiveData();
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return mFavourites;
    }

    public void addFavorite(Favourite favourite, RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                mFavouriteDao.addFavourite(favourite);
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
                mFavouriteDao.deleteFavourite(favourite.getUser(), favourite.getUrl());
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
                Favourite result = mFavouriteDao.getFavourite(mUserId, favourite.getUrl());
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

    //Can use LiveData here
    public void updateFavouritesLiveData() {
        new Thread(() -> {
            ArrayList<Favourite> favourites =
                    new ArrayList<>(prepareList(mFavouriteDao.getAllFavourites(mUserId)));
            mFavourites.postValue(favourites);
            Log.d(TAG, "FavouritesRepository: favourites from DB: " + favourites.toString());
        }).start();
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










