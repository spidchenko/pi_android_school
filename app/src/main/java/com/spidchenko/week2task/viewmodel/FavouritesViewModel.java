package com.spidchenko.week2task.viewmodel;

import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.network.Result;

import java.util.List;

public class FavouritesViewModel extends ViewModel {

    private static final String TAG = "FavViewModel.LOG_TAG";

    private final FavouriteRepository mFavouriteRepository;
    private final LiveData<List<Favourite>> mFavourites;
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();


    public FavouritesViewModel(FavouriteRepository repository) {

        mFavouriteRepository = repository;
        mFavourites = mFavouriteRepository.getAllFavourites();
        Log.d(TAG, "FavouritesViewModel: Created " + this + "( repo=" + repository + ")");
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        mFavouriteRepository.updateFavouritesLiveData();
        return mFavourites;
    }

    public SingleLiveEvent<Integer> getSnackBarMessage() {
        return mSnackBarMessage;
    }

    public void deleteFavourite(Favourite favourite) {
        mFavouriteRepository.deleteFavourite(favourite, result -> {
            if (result instanceof Result.Success) {
                Log.d(TAG, "toggleFavourite: Deleted from DB");
                setMessage(R.string.removed_from_favourites);
                mFavouriteRepository.updateFavouritesLiveData();
            } else {
                handleError((Result.Error<Boolean>) result);
            }
        });
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