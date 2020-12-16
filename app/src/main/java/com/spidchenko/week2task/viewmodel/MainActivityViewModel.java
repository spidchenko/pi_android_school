package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.ImageRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.SharedPreferencesRepository;
import com.spidchenko.week2task.network.Result;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainAcViewModel.LOG_TAG";

    private ImageRepository mImageRepository;
    private MutableLiveData<List<Image>> mImages = new MutableLiveData<>();
    private MutableLiveData<String> mLastSearchString = new MutableLiveData<>();
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
        mSharedPrefRepository.saveLastSearch(searchRequest);
        setSearchString(searchRequest);
        mImageRepository.updateImages(searchRequest, result -> {
            if (result instanceof Result.Error) {
                Log.d(TAG, "searchImages: Error Returned From Repo: " + ((Result.Error<List<Image>>) result).throwable.getMessage());
            } else {
                mImages.postValue(((Result.Success<List<Image>>) result).data);
            }
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

    private void setSearchString(String string) {
        mLastSearchString.setValue(string);
    }

}



