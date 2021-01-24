package com.spidchenko.week2task.viewmodel;

import android.content.ContentResolver;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.RequestManager;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.network.Result;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

public class ImageViewerViewModel extends ViewModel {
    private static final String TAG = "ImageViewerVM.LOG_TAG";

    private final FavouriteRepository mFavouriteRepository;
    private final FileRepository mFileRepository;
    private final SharedPrefRepository mSharedPrefRepository;
    private LiveData<Favourite> mInFavourites;
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    public ImageViewerViewModel(FavouriteRepository favouriteRepository,
                                SharedPrefRepository sharedPrefRepository,
                                FileRepository fileRepository) {

        mFavouriteRepository = favouriteRepository;
        mSharedPrefRepository = sharedPrefRepository;
        mFileRepository = fileRepository;
    }


    public SingleLiveEvent<Integer> getSnackBarMessage() {
        return mSnackBarMessage;
    }


    public LiveData<Favourite> getInFavourites(String searchString, String url) {

        int userId = mSharedPrefRepository.getUserId();
        Favourite favourite = new Favourite(userId, searchString, url);

        if (mInFavourites == null) {
            mInFavourites = mFavouriteRepository.getFavourite(favourite);
        }
        return mInFavourites;
    }


    public void toggleFavourite(String searchString, String url) {
        int userId = mSharedPrefRepository.getUserId();
        Favourite favourite = new Favourite(userId, searchString, url);

        if ((mInFavourites != null) && (mInFavourites.getValue() != null)) {
            mFavouriteRepository.deleteFavourite(favourite, result -> {
                if (result instanceof Result.Success) {
                    setMessage(R.string.removed_from_favourites);
                } else {
                    handleError((Result.Error<Boolean>) result);
                }

            });
        } else {
            mFavouriteRepository.addFavorite(favourite, result -> {
                if (result instanceof Result.Success) {
                    setMessage(R.string.added_to_favourites);
                } else {
                    handleError((Result.Error<Boolean>) result);
                }

            });
        }
    }

    public void saveImage(RequestManager glide, ContentResolver contentResolver,
                          String searchString, String url) {

        int userId = mSharedPrefRepository.getUserId();
        Favourite favourite = new Favourite(userId, searchString, url);
        mFileRepository.saveImage(glide, contentResolver, favourite);
    }

    private void handleError(Result.Error<Boolean> error) {
        Log.d(TAG, "handleError: Error Returned From Repo: " + error.throwable.getMessage());
        setMessage(R.string.error_default_message);
    }

    private void setMessage(@StringRes int resId) {
        mSnackBarMessage.postValue(resId);
    }
}
