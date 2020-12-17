package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;

import java.util.List;

public class FavouritesActivityViewModel extends AndroidViewModel {
    private static final String TAG = "FavouritesActivityViewM";
    private final FavouriteRepository mFavouriteRepository;
    private final LiveData<List<Favourite>> mFavourites;
    private final CurrentUser mCurrentUser;


    public FavouritesActivityViewModel(@NonNull Application application) {
        super(application);
        mCurrentUser = CurrentUser.getInstance();
        mFavouriteRepository = new FavouriteRepository(application, mCurrentUser.getUser().getId());
        mFavourites = mFavouriteRepository.getAllFavourites();
        Log.d(TAG, "FavouritesActivityViewModel: ");
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return mFavourites;
    }

    public void deleteFavourite(Favourite favourite) {
        mFavouriteRepository.deleteFavourite(favourite, result -> {
            //TODO handle error here
            mFavouriteRepository.updateFavouritesLiveData();
        });
    }
}
