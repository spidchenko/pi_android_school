package com.spidchenko.week2task.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.network.Result;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import java.util.List;

import static com.spidchenko.week2task.repositories.ImageRepository.RESULT_EMPTY_RESPONSE;
import static com.spidchenko.week2task.repositories.ImageRepository.RESULT_TIMEOUT;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "MainAcViewModel.LOG_TAG";

    private final MutableLiveData<List<Image>> mImages = new MutableLiveData<>();
    private final MutableLiveData<String> mLastSearchString = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();

    private final ImageRepository mImageRepository;
    private final SharedPrefRepository mSharedPrefRepository;
    private final SearchRequestRepository mSearchRequestRepository;


    public SearchViewModel(ImageRepository imageRepository,
                           SharedPrefRepository sharedPrefRepository,
                           SearchRequestRepository searchRequestRepository) {

        mImageRepository = imageRepository;
        mSharedPrefRepository = sharedPrefRepository;
        mSearchRequestRepository = searchRequestRepository;

        mLastSearchString.setValue(mSharedPrefRepository.getLastSearch());
        Log.d(TAG, "SearchViewModel: Created");
    }

    public LiveData<List<Image>> getAllImages() {
        return mImages;
    }

    public void searchImages(String searchString) {
        downloadStarted();
        mSharedPrefRepository.saveLastSearch(searchString);
        setSearchString(searchString);

        int userId = mSharedPrefRepository.getUserId();
        mSearchRequestRepository.saveCurrentSearchInDb(new SearchRequest(userId, searchString));

        mImageRepository.updateImages(searchString, result -> {
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

        int userId = mSharedPrefRepository.getUserId();
        mSearchRequestRepository.saveCurrentSearchInDb(new SearchRequest(userId, geoSearchString));

        mImageRepository.updateImagesByCoordinates(lat, lon, result -> {
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



