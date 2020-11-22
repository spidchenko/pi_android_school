package com.spidchenko.week2task.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("id")
    private String mId;

    @SerializedName("owner")
    private String mOwner;

    @SerializedName("secret")
    private String mSecret;

    @SerializedName("server")
    private String mServer;

    @SerializedName("farm")
    private int mFarm;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("ispublic")
    private int mIsPublic;

    @SerializedName("isfriend")
    private int mIsFriend;

    @SerializedName("isfamily")
    private int mIsFamily;

    public String getId() {
        return mId;
    }

    public String getSecret() {
        return mSecret;
    }

    public String getServer() {
        return mServer;
    }
}
