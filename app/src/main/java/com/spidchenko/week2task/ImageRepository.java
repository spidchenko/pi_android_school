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
import com.spidchenko.week2task.network.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private static final String TAG = "ImageRepository.LOG_TAG";
    private DatabaseHelper mDb;
    private int mUserId;

    public ImageRepository(@NonNull Application application, int userId) {
        mDb = DatabaseHelper.getInstance(application);
        mUserId = userId;
    }

    public void updateImages(MutableLiveData<List<Image>> mImages, String searchRequest) {

        saveCurrentSearchInDb(searchRequest);
        FlickrApi mDataService = ServiceGenerator.getFlickrApi();

        Call<ImgSearchResult> call = mDataService.searchImages(searchRequest);

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

                    if (!images.isEmpty()) {
                        mImages.postValue(images);
//                        updateImages(images);
                        //TODO stop spinning wheel here
//                        mBtnSearch.setClickable(true);
                    } else {
                        //TODO return something here or throw Exception
//                        Toast.makeText(mContext, R.string.error_nothing_found, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ImgSearchResult> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

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

                    if (!images.isEmpty()) {
                        mImages.postValue(images);
//                        updateImages(images);
                        //TODO stop spinning wheel here
//                        mBtnSearch.setClickable(true);
                    } else {
                        //TODO return something here
//                        Toast.makeText(mContext, R.string.error_nothing_found, Toast.LENGTH_LONG).show();
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
}
