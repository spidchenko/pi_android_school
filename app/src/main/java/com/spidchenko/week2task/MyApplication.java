package com.spidchenko.week2task;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.helpers.LogInHelper;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

public class MyApplication extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        mAppExecutors = new AppExecutors();
    }

    // TODO This method may be private
    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public FavouriteDao getFavouriteDao(){
        return getDatabase().favouriteDao();
    }

    public FavouriteRepository getFavouriteRepository() {
        return FavouriteRepository.getInstance(getFavouriteDao(), getSharedPrefRepository(), mAppExecutors.diskIO());
    }

    public FileRepository getFileRepository() {
        return FileRepository.getInstance(this);
    }

    public ImageRepository getImageRepository() {
        return ImageRepository.getInstance(ServiceGenerator.getFlickrApi());
    }

    public SharedPrefRepository getSharedPrefRepository() {
        return SharedPrefRepository.getInstance(this);
    }

    public LogInHelper getLogInHelper() {
        return LogInHelper.getInstance(getDatabase(), getSharedPrefRepository(), mAppExecutors.diskIO());
    }

    public SearchRequestRepository getSearchRequestRepository() {
        return SearchRequestRepository.getInstance(getDatabase(), getSharedPrefRepository(), mAppExecutors.diskIO());
    }

}