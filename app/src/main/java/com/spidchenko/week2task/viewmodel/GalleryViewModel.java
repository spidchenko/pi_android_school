package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.spidchenko.week2task.MyApplication;
import com.spidchenko.week2task.repositories.FileRepository;

import java.io.File;
import java.util.List;

public class GalleryViewModel extends ViewModel {

    FileRepository mFileRepository;

    public GalleryViewModel(FileRepository repository) {
        mFileRepository = repository;
    }

    public LiveData<List<File>> getImageFiles() {
        return mFileRepository.getImageFiles();
    }

    public void deleteFile(ContentResolver contentResolver, File file) {
        mFileRepository.deleteFile(contentResolver, file);
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final FileRepository fileRepository;

        public Factory(@NonNull Application application) {
            fileRepository = ((MyApplication) application).getFileRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new GalleryViewModel(fileRepository);
        }
    }

}
