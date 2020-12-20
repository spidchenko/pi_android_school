package com.spidchenko.week2task.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.db.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Favourite.class, SearchRequest.class}, version = 4, exportSchema = false)
public abstract class FlickrRoomDatabase extends RoomDatabase {

    private static final String DB_FILE_NAME = "com.spidchenko.week2task.db";
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile FlickrRoomDatabase INSTANCE;

    public static FlickrRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FlickrRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FlickrRoomDatabase.class, DB_FILE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract FavouriteDao favouriteDao();

    public abstract SearchRequestDao searchRequestDao();

}
