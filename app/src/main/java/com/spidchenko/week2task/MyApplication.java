package com.spidchenko.week2task;

import android.app.Application;
import android.os.Build;

import com.facebook.stetho.Stetho;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.dao.SyncImageDao;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.helpers.LogInHelper;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.repositories.SyncImageRepository;

public class MyApplication extends Application {

    private AppExecutors mAppExecutors;

    public static boolean isRobolectricUnitTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!isRobolectricUnitTest()) {
            Stetho.initializeWithDefaults(this);
        }

        mAppExecutors = new AppExecutors();
    }

    public FavouriteDao getFavouriteDao() {
        return getDatabase().favouriteDao();
    }

    public UserDao getUserDao() {
        return getDatabase().userDao();
    }

    public SyncImageDao getSyncImageDao() {
        return getDatabase().syncImageDao();
    }

    public SearchRequestDao getSearchRequestDao() {
        return getDatabase().searchRequestDao();
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

    public SyncImageRepository getSyncImageRepository() {
        return SyncImageRepository.getInstance(getSyncImageDao(), mAppExecutors.diskIO());
    }

    public SharedPrefRepository getSharedPrefRepository() {
        return SharedPrefRepository.getInstance(this);
    }

    public LogInHelper getLogInHelper() {
        return LogInHelper.getInstance(getUserDao(), getSharedPrefRepository(), mAppExecutors.diskIO());
    }

    public SearchRequestRepository getSearchRequestRepository() {
        return SearchRequestRepository.getInstance(getSearchRequestDao(), getSharedPrefRepository(), mAppExecutors.diskIO());
    }

    private AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }


}