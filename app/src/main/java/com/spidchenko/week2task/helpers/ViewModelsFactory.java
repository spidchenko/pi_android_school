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

    private final FavouriteRepository favouriteRepository;
    private final FileRepository fileRepository;
    private final ImageRepository imageRepository;
    private final SyncImageRepository syncImageRepository;
    private final SharedPrefRepository sharedPrefRepository;
    private final SearchRequestRepository searchRequestRepository;
    private final LogInHelper logInHelper;

    public ViewModelsFactory(@NonNull Application application) {
        favouriteRepository = ((MyApplication) application).getFavouriteRepository();
        fileRepository = ((MyApplication) application).getFileRepository();
        imageRepository = ((MyApplication) application).getImageRepository();
        syncImageRepository = ((MyApplication) application).getSyncImageRepository();
        sharedPrefRepository = ((MyApplication) application).getSharedPrefRepository();
        searchRequestRepository = ((MyApplication) application).getSearchRequestRepository();
        logInHelper = ((MyApplication) application).getLogInHelper();
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass == FavouritesViewModel.class) {
            return (T) new FavouritesViewModel(favouriteRepository);
        }

        if (modelClass == GalleryViewModel.class) {
            return (T) new GalleryViewModel(fileRepository);
        }

        if (modelClass == ImageViewerViewModel.class) {
            return (T) new ImageViewerViewModel(favouriteRepository, sharedPrefRepository, fileRepository);
        }

        if (modelClass == SearchViewModel.class) {
            return (T) new SearchViewModel(imageRepository, sharedPrefRepository, searchRequestRepository);
        }

        if (modelClass == SearchHistoryViewModel.class) {
            return (T) new SearchHistoryViewModel(searchRequestRepository);
        }

        if (modelClass == SyncImagesViewModel.class) {
            return (T) new SyncImagesViewModel(syncImageRepository);
        }

        return (T) new LoginViewModel(logInHelper);
    }
}