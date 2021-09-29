package com.spidchenko.week2task.db.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import com.spidchenko.week2task.db.models.SyncImage
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncImageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addSyncImage(syncImage: SyncImage?): Long

    @Query("DELETE FROM syncImages WHERE url LIKE :url")
    fun deleteSyncImage(url: String?)

    @get:Query("SELECT * FROM syncImages")
    val allImages: LiveData<List<SyncImage?>?>?
}