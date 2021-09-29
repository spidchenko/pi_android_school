package com.spidchenko.week2task.network.models

import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("secret")
    val secret: String? = null

    @SerializedName("server")
    val server: String? = null
    fun getUrl(imageSize: String?): String {
        return String.format(
            PIC_URL_TEMPLATE,
            server,
            id,
            secret,
            imageSize
        )
    }

    companion object {
        const val PIC_SIZE_MEDIUM = "z" //z = medium 640 Longest edge (px)
        private const val PIC_URL_TEMPLATE =
            "https://live.staticflickr.com/%s/%s_%s_%s.jpg" ///{server-id}/{id}_{secret}_{size-suffix}.jpg
    }
}