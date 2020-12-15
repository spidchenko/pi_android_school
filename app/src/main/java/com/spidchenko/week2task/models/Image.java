package com.spidchenko.week2task.models;

import com.google.gson.annotations.SerializedName;

public class Image {
    public static final String PIC_SIZE_MEDIUM = "z"; //z = medium 640 Longest edge (px)
    private static final String PIC_URL_TEMPLATE = "https://live.staticflickr.com/%s/%s_%s_%s.jpg"; ///{server-id}/{id}_{secret}_{size-suffix}.jpg

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

    public String getUrl(String imageSize) {
        return String.format(PIC_URL_TEMPLATE,
                getServer(),
                getId(),
                getSecret(),
                imageSize);
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setSecret(String mSecret) {
        this.mSecret = mSecret;
    }

    public void setServer(String mServer) {
        this.mServer = mServer;
    }

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
