package com.spidchenko.week2task.viewmodel;

import android.content.ContentResolver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.repositories.FileRepository;

import java.io.File;
import java.util.List;

public class GalleryViewModel extends ViewModel {
    private final SingleLiveEvent<Integer> mSnackBarMessage = new SingleLiveEvent<>();
    FileRepository mFileRepository;

    public GalleryViewModel(FileRepository repository) {
        mFileRepository = repository;
    }

    public LiveData<List<File>> getImageFiles() {
        return mFileRepository.getImageFiles();
    }

    public void deleteFile(ContentResolver contentResolver, File file) {
        mFileRepository.deleteFile(contentResolver, file);
        mSnackBarMessage.setValue(R.string.removed_from_list);
    }

    public SingleLiveEvent<Integer> getSnackBarMessage() {
        return mSnackBarMessage;
    }
}
