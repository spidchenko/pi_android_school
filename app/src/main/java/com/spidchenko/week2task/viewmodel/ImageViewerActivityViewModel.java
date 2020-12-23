package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_PICTURES;
import static java.io.File.separator;

public class ImageViewerActivityViewModel extends AndroidViewModel {
    private static final String TAG = "ImageViewerVM.LOG_TAG";

    private final FavouriteRepository mFavouriteRepository;
    private final MutableLiveData<Boolean> mInFavourites = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    // TODO: 12/22/20
    //  1 - leaking context object
    //  2 - context should not be stored in viewModel
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
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            saveImage(resource, mContext, mContext.getString(R.string.app_name), finalFileName);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "onResourceReady: ERROR" + e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    //TODO FileRepository
    // TODO: 12/22/20 Move image saving into separate class
    private void saveImage(Bitmap bitmap, Context context, String folderName, String fileName) throws FileNotFoundException {

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString() + separator + folderName);
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);
            saveImageToStream(bitmap, new FileOutputStream(file));
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }
    //TODO FileRepository
    private ContentValues contentValues(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        //values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }
    //TODO FileRepository
    private void saveImageToStream(Bitmap bitmap,OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.close();
            } catch (Exception e ) {
                e.printStackTrace();
            }
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
