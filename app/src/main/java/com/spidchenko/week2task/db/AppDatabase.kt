package com.spidchenko.week2task.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spidchenko.week2task.db.AppDatabase
import com.spidchenko.week2task.db.dao.FavouriteDao
import com.spidchenko.week2task.db.dao.SearchRequestDao
import com.spidchenko.week2task.db.dao.SyncImageDao
import com.spidchenko.week2task.db.dao.UserDao
import com.spidchenko.week2task.db.models.Favourite
import com.spidchenko.week2task.db.models.SearchRequest
import com.spidchenko.week2task.db.models.SyncImage
import com.spidchenko.week2task.db.models.User

@Database(
    entities = [User::class, Favourite::class, SearchRequest::class, SyncImage::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?
    abstract fun favouriteDao(): FavouriteDao?
    abstract fun searchRequestDao(): SearchRequestDao?
    abstract fun syncImageDao(): SyncImageDao?

    companion object {
        private const val DB_FILE_NAME = "com.spidchenko.week2task.db"

        @Volatile
        private var sInstance: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, DB_FILE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return sInstance
        }
    }
}