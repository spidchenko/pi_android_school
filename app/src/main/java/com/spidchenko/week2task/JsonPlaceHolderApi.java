package com.spidchenko.week2task;

import com.spidchenko.week2task.models.ImgSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<ImgSearchResult> searchImages(@Query("api_key") String api_key, @Query("text") String text);
}
