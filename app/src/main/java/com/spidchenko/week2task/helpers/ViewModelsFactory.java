package com.spidchenko.week2task.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.spidchenko.week2task.MyApplication;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.repositories.SyncImageRepository;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;
import com.spidchenko.week2task.viewmodel.GalleryViewModel;
import com.spidchenko.week2task.viewmodel.ImageViewerViewModel;
import com.spidchenko.week2task.viewmodel.LoginViewModel;
import com.spidchenko.week2task.viewmodel.SearchHistoryViewModel;
import com.spidchenko.week2task.viewmodel.SearchViewModel;
import com.spidchenko.week2task.viewmodel.SyncImagesViewModel;

public class ViewModelsFactory extends ViewModelProvider.NewInstanceFactory {

    private final FavouriteRepository mFavouriteRepository;
    private final FileRepository mFileRepository;
    private final ImageRepository mImageRepository;
    private final SyncImageRepository mSyncImageRepository;
    private final SharedPrefRepository mSharedPrefRepository;
    private final SearchRequestRepository mSearchRequestRepository;
    private final LogInHelper mLogInHelper;

    public ViewModelsFactory(@NonNull Application application) {
        mFavouriteRepository = ((MyApplication) application).getFavouriteRepository();
        mFileRepository = ((MyApplication) application).getFileRepository();
        mImageRepository = ((MyApplication) application).getImageRepository();
        mSyncImageRepository = ((MyApplication) application).getSyncImageRepository();
        mSharedPrefRepository = ((MyApplication) application).getSharedPrefRepository();
        mSearchRequestRepository = ((MyApplication) application).getSearchRequestRepository();
        mLogInHelper = ((MyApplication) application).getLogInHelper();
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass == FavouritesViewModel.class) {
            return (T) new FavouritesViewModel(mFavouriteRepository);
        }

        if (modelClass == GalleryViewModel.class) {
            return (T) new GalleryViewModel(mFileRepository);
        }

        if (modelClass == ImageViewerViewModel.class) {
            return (T) new ImageViewerViewModel(mFavouriteRepository, mSharedPrefRepository, mFileRepository);
        }

        if (modelClass == SearchViewModel.class) {
            return (T) new SearchViewModel(mImageRepository, mSharedPrefRepository, mSearchRequestRepository);
        }

        if (modelClass == SearchHistoryViewModel.class) {
            return (T) new SearchHistoryViewModel(mSearchRequestRepository);
        }

        if (modelClass == SyncImagesViewModel.class) {
            return (T) new SyncImagesViewModel(mSyncImageRepository);
        }

        return (T) new LoginViewModel(mLogInHelper);
    }
}