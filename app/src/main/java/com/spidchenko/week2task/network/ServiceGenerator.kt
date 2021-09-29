package com.spidchenko.week2task.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    private const val BASE_URL = "https://www.flickr.com/"
    private const val API_KEY = "02692fb0a64b6b1b77f7f689c7f050c7"
    private val myHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val url = chain.request().url()
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .build()
            val request = chain.request().newBuilder().url(url).build()
            chain.proceed(request)
        }
    private val client: OkHttpClient = myHttpClient.build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
    private val retrofit = retrofitBuilder.build()

    @JvmStatic
    val flickrApi: FlickrApi = retrofit.create(FlickrApi::class.java)
}