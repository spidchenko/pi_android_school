package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.network.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_PICTURES;

public class ImageViewerActivityViewModel extends AndroidViewModel {
    private static final String TAG = "ImageViewerVM.LOG_TAG";

    private final FavouriteRepository mFavouriteRepository;
    private final MutableLiveData<Boolean> mInFavourites = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    Context mContext;

    public ImageViewerActivityViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        CurrentUser currentUser = CurrentUser.getInstance();
        mFavouriteRepository = new FavouriteRepository(application, currentUser.getUser().getId());
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
        if ((mInFavourites.getValue() != null) && (mInFavourites.getValue())) {
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

    //TODO FileRepository. Check if file already downloaded, show message on success
    public void saveImage(Favourite mFavourite) {

        String fileName = "_";
        Pattern r = Pattern.compile("(?<=_)\\w+(?=_)");//Substring between "_" and "_"
        Matcher m = r.matcher(mFavourite.getUrl());
        if (m.find()) {
            Log.d(TAG, "saveImage: imageName = " + m.group(0));
            fileName = m.group(0);
        }

        String finalFileName = fileName;
        Glide.with(mContext)
                .asBitmap()
                .load(mFavourite.getUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        FileOutputStream outputStream;
                        try {
                            File file = new File(getPublicDirectory(), finalFileName + ".jpg");
                            Log.d(TAG, "onResourceReady: saving to " + file);
                            outputStream = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();
                        } catch (Exception error) {
                            Log.d(TAG, "onResourceReady: ERROR" + error.getMessage());
                            error.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


    }

    //TODO FileRepository
    private File getPublicDirectory() {
        File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES), "Simple flickr client");
        if (!pictureFolder.exists()) {
            if (!pictureFolder.mkdir()) {
                Log.e(TAG, "Failed to create public directory: " + pictureFolder);
            }
        }
        return pictureFolder;
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
