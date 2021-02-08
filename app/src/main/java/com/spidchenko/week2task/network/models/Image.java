package com.spidchenko.week2task.network.models;

import com.google.gson.annotations.SerializedName;

public class Image {
    public static final String PIC_SIZE_MEDIUM = "z"; //z = medium 640 Longest edge (px)
    private static final String PIC_URL_TEMPLATE = "https://live.staticflickr.com/%s/%s_%s_%s.jpg"; ///{server-id}/{id}_{secret}_{size-suffix}.jpg

    @SerializedName("id")
    private String mId;

    @SerializedName("secret")
    private String mSecret;

    @SerializedName("server")
    private String mServer;

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
