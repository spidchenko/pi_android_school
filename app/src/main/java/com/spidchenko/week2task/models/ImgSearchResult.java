package com.spidchenko.week2task.models;

import com.google.gson.annotations.SerializedName;

public class ImgSearchResult {

    @SerializedName("photos")
    private ImageContainer mImageContainer;

    @SerializedName("stat")
    private String mStat;

    public ImageContainer getImageContainer() {
        return mImageContainer;
    }
}
