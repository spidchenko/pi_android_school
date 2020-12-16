package com.spidchenko.week2task;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.models.ImgSearchResult;
import com.spidchenko.week2task.network.FlickrApi;
import com.spidchenko.week2task.network.Result;
import com.spidchenko.week2task.network.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private static final String TAG = "ImageRepository.LOG_TAG";
    public static final String RESULT_EMPTY_RESPONSE = "com.spidchenko.week2task.extras.RESULT_EMPTY_RESPONSE";
    public static final String RESULT_TIMEOUT = "com.spidchenko.week2task.extras.RESULT_TIMEOUT";
    private DatabaseHelper mDb;
    private int mUserId;

    public ImageRepository(@NonNull Application application, int userId) {
        mDb = DatabaseHelper.getInstance(application);
        mUserId = userId;
    }

    public void updateImages(String searchRequest, RepositoryCallback<List<Image>> callback) {
        saveCurrentSearchInDb(searchRequest);
        FlickrApi mDataService = ServiceGenerator.getFlickrApi();
        Call<ImgSearchResult> call = mDataService.searchImages(searchRequest);
        getResultsFromNetwork(call, callback);
    }

    //TODO update this method too
    public void updateImagesByCoordinates(MutableLiveData<List<Image>> mImages, String lat, String lon,
                                          String geoSearchString) {

        saveCurrentSearchInDb(geoSearchString);
        FlickrApi mDataService = ServiceGenerator.getFlickrApi();

        Call<ImgSearchResult> call = mDataService.searchImagesByCoordinates(lat, lon);

        call.enqueue(new Callback<ImgSearchResult>() {
            @Override
            public void onResponse(Call<ImgSearchResult> call, Response<ImgSearchResult> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "Error: code = " + response.code());
                    return;
                }

                if (response.body() != null) {
                    Log.d(TAG, "Received flikr response" + response.body().getImageContainer().getImage());
                    List<Image> images = response.body().getImageContainer().getImage();
                    //TODO stop spinning wheel here
                    if (!images.isEmpty()) {
                        mImages.postValue(images);
                    } else {
                        //TODO return something here (R.string.error_nothing_found)
                    }
                }
            }

            @Override
            public void onFailure(Call<ImgSearchResult> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }


    private void saveCurrentSearchInDb(String searchString) {
        new Thread(() -> {
            mDb.addSearchRequest(new SearchRequest(mUserId, searchString));
            mDb.close();
            Log.d(TAG, "saveCurrentSearch: Worker thread finished saving. String: " + searchString);
        }).start();
    }


    private void getResultsFromNetwork(Call<ImgSearchResult> call, RepositoryCallback<List<Image>> callback) {
        call.enqueue(new Callback<ImgSearchResult>() {
            @Override
            public void onResponse(Call<ImgSearchResult> call, Response<ImgSearchResult> response) {

                if (!response.isSuccessful()) {
                    String errorCode = String.valueOf(response.code());
                    Log.d(TAG, "onResponse: ErrorCode " + errorCode);
                    callback.onComplete(new Result.Error<>(new Exception(errorCode)));
                    return;
                }

                if (response.body() != null) {
                    Log.d(TAG, "Received flikr response" + response.body().getImageContainer().getImage());
                    List<Image> images = response.body().getImageContainer().getImage();
                    if (!images.isEmpty()) {
                        callback.onComplete(new Result.Success<>(images));
                    } else {
                        callback.onComplete(new Result.Error<>(new Exception(RESULT_EMPTY_RESPONSE)));
                    }
                }
            }

            @Override
            public void onFailure(Call<ImgSearchResult> call, Throwable t) {
                if (t.getMessage().equals("timeout")) {
                    callback.onComplete(new Result.Error<>(new Exception(RESULT_TIMEOUT)));
                } else {
                    callback.onComplete(new Result.Error<>(t));
                }
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onComplete(Result<T> result);
    }

}

