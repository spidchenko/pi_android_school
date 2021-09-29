package com.spidchenko.week2task.repositories

import android.util.Log
import com.spidchenko.week2task.network.FlickrApi
import com.spidchenko.week2task.network.Result
import com.spidchenko.week2task.network.ServiceGenerator.flickrApi
import com.spidchenko.week2task.network.models.Image
import com.spidchenko.week2task.network.models.ImgSearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ImageRepository private constructor(private val mDataService: FlickrApi) {
    fun updateImages(searchString: String?, callback: RepositoryCallback<List<Image>?>) {
        val call = mDataService.searchImages(searchString)
        getResultsFromNetwork(call, callback)
    }

    fun updateImagesByCoordinates(
        lat: String?, lon: String?,
        callback: RepositoryCallback<List<Image>?>
    ) {
        val mDataService = flickrApi
        val call = mDataService.searchImagesByCoordinates(lat, lon)
        getResultsFromNetwork(call, callback)
    }

    private fun getResultsFromNetwork(
        call: Call<ImgSearchResult?>?,
        callback: RepositoryCallback<List<Image>?>
    ) {
        call!!.enqueue(object : Callback<ImgSearchResult?> {
            override fun onResponse(
                call: Call<ImgSearchResult?>,
                response: Response<ImgSearchResult?>
            ) {
                if (!response.isSuccessful) {
                    val errorCode = response.code().toString()
                    Log.d(TAG, "onResponse: ErrorCode $errorCode")
                    callback.onComplete(Result.Error(Exception(errorCode)))
                    return
                }
                if (response.body() != null) {
                    Log.d(TAG, "Received flikr response" + response.body()!!.imageContainer!!.image)
                    val images: List<Image>? = response.body()!!.imageContainer!!.image
                    if (images!!.isNotEmpty()) {
                        callback.onComplete(Result.Success(images))
                    } else {
                        callback.onComplete(Result.Error(Exception(RESULT_EMPTY_RESPONSE)))
                    }
                }
            }

            override fun onFailure(call: Call<ImgSearchResult?>, t: Throwable) {
                if (Objects.requireNonNull(t.message) == "timeout") {
                    callback.onComplete(Result.Error(Exception(RESULT_TIMEOUT)))
                } else {
                    callback.onComplete(Result.Error(t))
                }
            }
        })
    }

    interface RepositoryCallback<T> {
        fun onComplete(result: Result<T>?)
    }

    companion object {
        private const val TAG = "ImageRepository.LOG_TAG"
        const val RESULT_EMPTY_RESPONSE = "com.spidchenko.week2task.extras.RESULT_EMPTY_RESPONSE"
        const val RESULT_TIMEOUT = "com.spidchenko.week2task.extras.RESULT_TIMEOUT"

        @Volatile
        private var sInstance: ImageRepository? = null

        @JvmStatic
        fun getInstance(dataService: FlickrApi): ImageRepository? {
            if (sInstance == null) {
                synchronized(FavouriteRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = ImageRepository(dataService)
                    }
                }
            }
            return sInstance
        }
    }
}