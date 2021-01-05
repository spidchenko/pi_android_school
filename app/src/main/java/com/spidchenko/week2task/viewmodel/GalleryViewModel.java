package com.spidchenko.week2task.viewmodel;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.FileRepository;

import java.io.File;
import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    FileRepository mFileRepository;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        mFileRepository = new FileRepository(application);
    }

    public LiveData<List<File>> getImageFiles() {
        return mFileRepository.getImageFiles();
    }

    public void deleteFile(ContentResolver contentResolver, File file) {
        mFileRepository.deleteFile(contentResolver, file);
    }

}
