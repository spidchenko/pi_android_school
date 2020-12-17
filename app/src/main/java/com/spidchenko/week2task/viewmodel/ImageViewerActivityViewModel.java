package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.DatabaseHelper;

public class ImageViewerActivityViewModel extends AndroidViewModel {
    private static final String TAG = "ImageViewerActivityView.LOG_TAG";

    private CurrentUser mCurrentUser;
    private FavouriteRepository mFavouriteRepository;
    private MutableLiveData<Boolean> isInFavourites = new MutableLiveData<>();

    public ImageViewerActivityViewModel(@NonNull Application application) {
        super(application);
        mCurrentUser = CurrentUser.getInstance();
        mFavouriteRepository = new FavouriteRepository(application, mCurrentUser.getUser().getId());


    }

    private void checkInFavourites(String url) {
        mFavouriteRepository.checkInFavourites(url, inFavourites -> {
            //TODO handle this callback
        });
//        cbToggleFavourite.setClickable(false);


    }

}
