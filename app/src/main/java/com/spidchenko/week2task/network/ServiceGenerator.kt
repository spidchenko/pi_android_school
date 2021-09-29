package com.spidchenko.week2task.network;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String BASE_URL = "https://www.flickr.com/";
    private static final String API_KEY = "02692fb0a64b6b1b77f7f689c7f050c7";

    static OkHttpClient.Builder myHttpClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                HttpUrl url = chain.request().url()
                        .newBuilder()
                        .addQueryParameter("api_key", API_KEY)
                        .addQueryParameter("format", "json")
                        .addQueryParameter("nojsoncallback", "1")
                        .build();
                Request request = chain.request().newBuilder().url(url).build();
                return chain.proceed(request);
            });

    static OkHttpClient client = myHttpClient.build();

    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create());
    private static final Retrofit retrofit = retrofitBuilder.build();
    private static final FlickrApi flickrApi = retrofit.create(FlickrApi.class);

    public static FlickrApi getFlickrApi() {
        return flickrApi;
    }
}