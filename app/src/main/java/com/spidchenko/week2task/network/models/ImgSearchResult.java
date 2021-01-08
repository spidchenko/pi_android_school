package com.spidchenko.week2task.network.models;

import com.google.gson.annotations.SerializedName;

public class ImgSearchResult {

    @SerializedName("photos")
    private ImageContainer mImageContainer;

    public ImageContainer getImageContainer() {
        return mImageContainer;
    }
}
