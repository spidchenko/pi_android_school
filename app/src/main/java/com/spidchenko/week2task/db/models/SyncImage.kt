package com.spidchenko.week2task.db.models

import androidx.room.*
import com.spidchenko.week2task.network.models.Image

@Entity(tableName = "syncImages", indices = [Index(value = arrayOf("url"), unique = true)])
class SyncImage() {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "text")
    var searchText: String? = null

    @ColumnInfo(name = "url")
    var url: String? = null

    @ColumnInfo(name = "created_at")
    var dateTime = 0

    @Ignore
    constructor(image: Image, searchText: String?) : this() {
        this.searchText = searchText
        url = image.getUrl(Image.PIC_SIZE_MEDIUM)
    }
}