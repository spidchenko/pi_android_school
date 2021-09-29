package com.spidchenko.week2task.network

import retrofit2.http.GET
import com.spidchenko.week2task.network.models.ImgSearchResult
import retrofit2.Call
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.photos.search")
    fun searchImages(@Query("text") text: String?): Call<ImgSearchResult?>?

    @GET("services/rest/?method=flickr.photos.search")
    fun searchImagesByCoordinates(
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?
    ): Call<ImgSearchResult?>?
}