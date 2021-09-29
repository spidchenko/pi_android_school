package com.spidchenko.week2task.db.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.spidchenko.week2task.network.models.Image;

@Entity(tableName = "syncImages", indices = {@Index(value = "url", unique = true)})
public class SyncImage {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "text")
    private String searchText;


    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "created_at")
    private int dateTime;

    public SyncImage() {
    }

    @Ignore
    public SyncImage(Image image, String searchText) {
        this.searchText = searchText;
        this.url = image.getUrl(Image.PIC_SIZE_MEDIUM);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDateTime() {
        return dateTime;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = dateTime;
    }
}
