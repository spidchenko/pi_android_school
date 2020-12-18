package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
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
                Log.d(TAG, "checkInFavourites: Already in favourites!");
            } else {
                handleError((Result.Error<Boolean>) result);
            }
        });
    }


    public void toggleFavourite(Favourite favourite) {
        if (mInFavourites.getValue()) {
            mFavouriteRepository.deleteFavourite(favourite, result -> {
                if (result instanceof Result.Success) {
                    setMessage(R.string.removed_from_favourites);
                } else {
                    handleError((Result.Error<Boolean>) result);
                }
                //Room will take care of auto updating from DB
                checkInFavourites(favourite);
            });
        } else {
            mFavouriteRepository.addFavorite(favourite, result -> {
                if (result instanceof Result.Success) {
                    setMessage(R.string.added_to_favourites);
                } else {
                    handleError((Result.Error<Boolean>) result);
                }
                //Room will take care of auto updating from DB
                checkInFavourites(favourite);
            });
        }
    }

    private void handleError(Result.Error<Boolean> error) {
        Log.d(TAG, "handleError: Error Returned From Repo: " + error.throwable.getMessage());
        //can use switch-case here
        setMessage(R.string.error_default_message);
    }

    private void setMessage(@StringRes int resId) {
        mSnackBarMessage.postValue(resId);
    }

}
