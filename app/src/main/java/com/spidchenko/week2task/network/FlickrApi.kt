package com.spidchenko.week2task.network;

import com.spidchenko.week2task.network.models.ImgSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {
    @GET("services/rest/?method=flickr.photos.search")
    Call<ImgSearchResult> searchImages(@Query("text") String text);

    @GET("services/rest/?method=flickr.photos.search")
    Call<ImgSearchResult> searchImagesByCoordinates(@Query("lat") String latitude,
                                                    @Query("lon") String longitude);

}