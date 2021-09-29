package com.spidchenko.week2task.db.models

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "favourites")
class Favourite() {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "user_id")
    var user = 0

    @ColumnInfo(name = "search_string")
    var searchRequest: String? = null

    @ColumnInfo(name = "url")
    var url: String? = null

    @Ignore
    constructor(user: Int, searchRequest: String?, url: String?) : this() {
        this.user = user
        this.searchRequest = searchRequest
        this.url = url
    }

    override fun toString(): String {
        return "Favourite{" +
                "id=" + id +
                ", user=" + user +
                ", searchRequest='" + searchRequest + '\'' +
                ", url='" + url + '\'' +
                '}'
    }
}