package com.spidchenko.week2task.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.dao.SyncImageDao;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.db.models.SyncImage;
import com.spidchenko.week2task.db.models.User;

@Database(entities = {
        User.class,
        Favourite.class,
        SearchRequest.class,
        SyncImage.class},
        version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_FILE_NAME = "com.spidchenko.week2task.db";
    private static volatile AppDatabase sInstance;

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_FILE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }

    public abstract UserDao userDao();

    public abstract FavouriteDao favouriteDao();

    public abstract SearchRequestDao searchRequestDao();

    public abstract SyncImageDao syncImageDao();

}
