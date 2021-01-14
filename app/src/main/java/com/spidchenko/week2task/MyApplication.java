package com.spidchenko.week2task;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.repositories.UserRepository;

public class MyApplication extends Application {

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
        return FavouriteRepository.getInstance(getDatabase(), getCurrentUser(), mAppExecutors.diskIO());
    }

    public FileRepository getFileRepository() {
        return FileRepository.getInstance(this);
    }

    public ImageRepository getImageRepository() {
        return ImageRepository.getInstance(getDatabase(), mAppExecutors.diskIO(), ServiceGenerator.getFlickrApi(), getCurrentUser());
    }

    public SharedPrefRepository getSharedPreferencesRepository() {
        return SharedPrefRepository.getInstance(this);
    }

    public UserRepository getUserRepository(){
        return UserRepository.getInstance(getDatabase(), mAppExecutors.diskIO());
    }


}