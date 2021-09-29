package com.spidchenko.week2task.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.spidchenko.week2task.network.FlickrApi;
import com.spidchenko.week2task.network.Result;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.network.models.ImgSearchResult;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private static final String TAG = "ImageRepository.LOG_TAG";
    public static final String RESULT_EMPTY_RESPONSE = "com.spidchenko.week2task.extras.RESULT_EMPTY_RESPONSE";
    public static final String RESULT_TIMEOUT = "com.spidchenko.week2task.extras.RESULT_TIMEOUT";
    private final FlickrApi mDataService;
    private static volatile ImageRepository sInstance;


    private ImageRepository(final FlickrApi dataService) {
        mDataService = dataService;
    }

    public static ImageRepository getInstance(final FlickrApi dataService) {
        if (sInstance == null) {
            synchronized (FavouriteRepository.class) {
                if (sInstance == null) {
                    sInstance = new ImageRepository (dataService);
                }
            }
        }
        return sInstance;
    }

    public void updateImages(String searchString, RepositoryCallback<List<Image>> callback) {
        Call<ImgSearchResult> call = mDataService.searchImages(searchString);
        getResultsFromNetwork(call, callback);
    }


    public void updateImagesByCoordinates(String lat, String lon,
                                          RepositoryCallback<List<Image>> callback) {
        FlickrApi mDataService = ServiceGenerator.getFlickrApi();
        Call<ImgSearchResult> call = mDataService.searchImagesByCoordinates(lat, lon);
        getResultsFromNetwork(call, callback);
    }

    private void getResultsFromNetwork(Call<ImgSearchResult> call, RepositoryCallback<List<Image>> callback) {
        call.enqueue(new Callback<ImgSearchResult>() {
            @Override
            public void onResponse(@NonNull Call<ImgSearchResult> call, @NonNull Response<ImgSearchResult> response) {

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
            public void onFailure(@NonNull Call<ImgSearchResult> call, @NonNull Throwable t) {
                if (Objects.requireNonNull(t.getMessage()).equals("timeout")) {
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

