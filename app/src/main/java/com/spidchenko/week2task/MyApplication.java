package com.spidchenko.week2task;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

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

    public FileRepository getFileRepository() {
        return FileRepository.getInstance(this);
    }

    public ImageRepository getImageRepository() {
        return ImageRepository.getInstance(getDatabase(), ServiceGenerator.getFlickrApi(), getCurrentUser());
    }

    public SharedPrefRepository getSharedPreferencesRepository() {
        return SharedPrefRepository.getInstance(this);
    }

}