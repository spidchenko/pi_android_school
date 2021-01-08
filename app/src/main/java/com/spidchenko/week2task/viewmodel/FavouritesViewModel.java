package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.MyApplication;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.util.List;

public class FavouritesViewModel extends AndroidViewModel {
    private static final String TAG = "FavouritesActivityViewM";
    private final FavouriteRepository mFavouriteRepository;
    private final LiveData<List<Favourite>> mFavourites;
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();


    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        CurrentUser currentUser = CurrentUser.getInstance();
        // TODO: 12/22/20 inject repository in viewModel (instead of application)
        FlickrRoomDatabase database = FlickrRoomDatabase.getDatabase(application);
        mFavouriteRepository = new FavouriteRepository(database.favouriteDao(),
                ((MyApplication) getApplication()).executorService,
                currentUser.getUser().getId());
        mFavourites = mFavouriteRepository.getAllFavourites();
        Log.d(TAG, "FavouritesViewModel: Created");
    }

    public LiveData<List<Favourite>> getAllFavourites() {
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