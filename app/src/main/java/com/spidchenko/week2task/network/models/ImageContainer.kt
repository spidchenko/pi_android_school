package com.spidchenko.week2task.network.models

import com.google.gson.annotations.SerializedName
import java.util.*

class ImageContainer {
    @SerializedName("photo")
    val image: LinkedList<Image>? = null
}