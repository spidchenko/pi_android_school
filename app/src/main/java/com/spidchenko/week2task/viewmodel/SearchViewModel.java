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
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.network.Result;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.network.models.Image;

import java.util.List;

import static com.spidchenko.week2task.ImageRepository.RESULT_EMPTY_RESPONSE;
import static com.spidchenko.week2task.ImageRepository.RESULT_TIMEOUT;

public class SearchViewModel extends AndroidViewModel {
    private static final String TAG = "MainAcViewModel.LOG_TAG";

    private final ImageRepository mImageRepository;
    private final MutableLiveData<List<Image>> mImages = new MutableLiveData<>();
    private final MutableLiveData<String> mLastSearchString = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    private final SharedPreferencesRepository mSharedPrefRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        CurrentUser user = CurrentUser.getInstance();
        FlickrRoomDatabase database = FlickrRoomDatabase.getDatabase(application);

        mImageRepository = new ImageRepository(database.searchRequestDao(),
                ServiceGenerator.getFlickrApi(),
                user.getUser().getId());

        mSharedPrefRepository = SharedPreferencesRepository.init(application);
        mLastSearchString.setValue(mSharedPrefRepository.getLastSearch());
        Log.d(TAG, "SearchViewModel: Created");
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
                handleError((Result.Error<List<Image>>) result);
            } else {
                mImages.postValue(((Result.Success<List<Image>>) result).data);
            }
            downloadFinished();
        });
    }

    public void searchImagesByCoordinates(String lat, String lon) {
        downloadStarted();
        String geoSearchString = "GeoSearch. Lat:" + lat.substring(0, 5) +
                " Lon:" + lon.substring(0, 5);
        setSearchString(geoSearchString);
        mImageRepository.updateImagesByCoordinates(lat, lon, geoSearchString, result -> {
            if (result instanceof Result.Error) {
                handleError((Result.Error<List<Image>>) result);
            } else {
                mImages.postValue(((Result.Success<List<Image>>) result).data);
            }
            downloadFinished();
        });
    }

    public void deleteImageAtPosition(int position) {
        //Remove and update LiveData
        List<Image> temp = mImages.getValue();
        if ((temp != null) && (temp.get(position) != null)) {
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

    private void handleError(Result.Error<List<Image>> error) {
        Log.d(TAG, "handleError: Error Returned From Repo: " + error.throwable);
        String errorMessage = error.throwable.getMessage();
        if (errorMessage != null) {
            switch (errorMessage) {
                case RESULT_EMPTY_RESPONSE:
                    mSnackBarMessage.postValue(R.string.error_nothing_found);
                    break;
                case RESULT_TIMEOUT:
                    mSnackBarMessage.postValue(R.string.error_network_timeout);
                    break;
                default:
                    mSnackBarMessage.postValue(R.string.error_default_message);
            }
        }
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



