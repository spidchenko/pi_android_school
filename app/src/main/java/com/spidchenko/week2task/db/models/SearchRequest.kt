package com.spidchenko.week2task.db.models

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "searches")
class SearchRequest() {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "user_id")
    var userId = 0

    @ColumnInfo(name = "search_string")
    var searchRequest: String? = null

    @Ignore
    constructor(userId: Int, searchRequest: String?) : this() {
        this.userId = userId
        this.searchRequest = searchRequest
    }
}