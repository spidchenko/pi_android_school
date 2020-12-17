package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

public class ImageViewerActivityViewModel extends AndroidViewModel {
    private static final String TAG = "ImageViewerVM.LOG_TAG";

    private final CurrentUser mCurrentUser;
    private final FavouriteRepository mFavouriteRepository;
    private final MutableLiveData<Boolean> mInFavourites = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    public ImageViewerActivityViewModel(@NonNull Application application) {
        super(application);
        mCurrentUser = CurrentUser.getInstance();
        mFavouriteRepository = new FavouriteRepository(application, mCurrentUser.getUser().getId());
    }

    public SingleLiveEvent<Integer> getSnackBarMessage() {
        return mSnackBarMessage;
    }

    public LiveData<Boolean> getInFavourites(Favourite favourite) {
        checkInFavourites(favourite);
        return mInFavourites;
    }

    private void checkInFavourites(Favourite favourite) {
        mFavouriteRepository.checkInFavourites(favourite, result -> {
            if (result instanceof Result.Success) {
                mInFavourites.postValue(((Result.Success<Boolean>) result).data);
            } else {
                mSnackBarMessage.postValue(R.string.error_default_message);
                String message = ((Result.Error<Boolean>) result).throwable.getMessage();
                Log.d(TAG, "checkInFavourites: ERROR " + message);
            }
        });
    }


    public void toggleFavourite(Favourite favourite) {
        if (mInFavourites.getValue()) {
            mFavouriteRepository.deleteFavourite(favourite, result -> {
                if (result instanceof Result.Success) {
                    Log.d(TAG, "toggleFavourite: Deleted from DB");
                } else {
                    String message = ((Result.Error<Boolean>) result).throwable.getMessage();
                    Log.d(TAG, "toggleFavourite: ERROR" + message);
                }
                //Room will take care of auto updating from DB
                checkInFavourites(favourite);
            });
        } else {
            mFavouriteRepository.addFavorite(favourite, result -> {
                if (result instanceof Result.Success) {
                    Log.d(TAG, "toggleFavourite: Added to DB");
                } else {
                    String message = ((Result.Error<Boolean>) result).throwable.getMessage();
                    Log.d(TAG, "toggleFavourite: ERROR" + message);
                }
                //Room will take care of auto updating from DB
                checkInFavourites(favourite);
            });
        }
    }
}
