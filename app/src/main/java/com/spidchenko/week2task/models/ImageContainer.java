package com.spidchenko.week2task.models;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
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
    private final LinkedList<Image> mImage = null;

    public LinkedList<Image> getImage() {
        return mImage;
    }
}
