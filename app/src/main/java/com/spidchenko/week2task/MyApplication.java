package com.spidchenko.week2task;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    // TODO Delete this ExService
    public ExecutorService executorService = Executors.newFixedThreadPool(4);
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public CurrentUser getCurrentUser() {
        return CurrentUser.getInstance();
    }

    public FavouriteRepository getFavouriteRepository() {
        return FavouriteRepository.getInstance(getDatabase(), getCurrentUser(), mAppExecutors);
    }

// TODO
//    public FileRepository getFileRepository() {
//        return FileRepository.getInstance(this);
//    }
// TODO
//    public ImageRepository getImageRepository() {
//        return ImageRepository.getInstance(getDatabase(), mAppExecutors);
//    }

}