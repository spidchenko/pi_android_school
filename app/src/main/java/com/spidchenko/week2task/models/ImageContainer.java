package com.spidchenko.week2task.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageContainer {
    @SerializedName("page")
    private int mPage;

    @SerializedName("pages")
    private int mPages;

    @SerializedName("perpage")
    private int mPerPage;

    @SerializedName("total")
    private String mTotal;

    @SerializedName("photo")
    private final List<Image> mImage = null;

    public List<Image> getImage() {
        return mImage;
    }
}
