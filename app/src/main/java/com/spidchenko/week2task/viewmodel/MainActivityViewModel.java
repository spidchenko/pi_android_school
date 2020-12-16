package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.ImageRepository;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SharedPreferencesRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.network.Result;

import java.util.List;

import static com.spidchenko.week2task.ImageRepository.RESULT_EMPTY_RESPONSE;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainAcViewModel.LOG_TAG";

    private ImageRepository mImageRepository;
    private MutableLiveData<List<Image>> mImages = new MutableLiveData<>();
    private MutableLiveData<String> mLastSearchString = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();
    private CurrentUser mCurrentUser;
    private SharedPreferencesRepository mSharedPrefRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mCurrentUser = CurrentUser.getInstance();
        mImageRepository = new ImageRepository(application, mCurrentUser.getUser().getId());

        mSharedPrefRepository = SharedPreferencesRepository.init(application);
        mLastSearchString.setValue(mSharedPrefRepository.getLastSearch());

        Log.d(TAG, "MainActivityViewModel: Created");
        Log.d(TAG, "MainActivityViewModel: images=" + (mImages == null));
    }

    public LiveData<List<Image>> getAllImages() {
        return mImages;
    }

    public void searchImages(String searchRequest) {
        downloadStarted();
        mSharedPrefRepository.saveLastSearch(searchRequest);
        setSearchString(searchRequest);
        mImageRepository.updateImages(searchRequest, result -> {
            if (result instanceof Result.Error) {
                Log.d(TAG, "searchImages: Error Returned From Repo: " + ((Result.Error<List<Image>>) result).throwable.getMessage());
                String errorMessage = ((Result.Error<List<Image>>) result).throwable.getMessage();
                switch (errorMessage) {
                    case RESULT_EMPTY_RESPONSE:
                        mSnackBarMessage.postValue(R.string.error_nothing_found);
                        break;
                    default:
                        mSnackBarMessage.postValue(R.string.error_default_message);
                }
            } else {
                mImages.postValue(((Result.Success<List<Image>>) result).data);
            }
            downloadFinished();
        });
    }

    public void searchImagesByCoordinates(String lat, String lon) {
        String geoSearchString = "GeoSearch. Lat:" + lat.substring(0, 5) +
                " Lon:" + lon.substring(0, 5);
        setSearchString(geoSearchString);
        mImageRepository.updateImagesByCoordinates(mImages, lat, lon, geoSearchString);
    }

    public void deleteImageAtPosition(int position) {
        //Remove and update LiveData
        List<Image> temp = mImages.getValue();
        if ((temp.size() > position) && (temp.get(position) != null)) {
            temp.remove(position);
            mImages.setValue(temp);
        }
    }

    public LiveData<String> getSearchString() {
        return mLastSearchString;
    }

    public LiveData<Boolean> getLoadingState() {
        return mIsLoading;
    }

    public SingleLiveEvent<Integer> getSnackBarMessage() {
        return mSnackBarMessage;
    }

    private void setSearchString(String string) {
        mLastSearchString.setValue(string);
    }

    private void downloadFinished() {
        mIsLoading.postValue(false);
    }

    private void downloadStarted() {
        mIsLoading.postValue(true);
    }
}



