package com.spidchenko.week2task.network.models;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

public class ImageContainer {

    @SerializedName("photo")
    private final LinkedList<Image> mImage = null;

    public LinkedList<Image> getImage() {
        return mImage;
    }
}
