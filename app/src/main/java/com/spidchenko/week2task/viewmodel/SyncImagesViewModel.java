package com.spidchenko.week2task.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.db.models.SyncImage;
import com.spidchenko.week2task.repositories.SyncImageRepository;

import java.util.List;

public class SyncImagesViewModel extends ViewModel {

    private final SyncImageRepository mSyncImageRepository;

    public SyncImagesViewModel(SyncImageRepository syncImageRepository){
        mSyncImageRepository = syncImageRepository;
    }

    public LiveData<List<SyncImage>> getAllImages(){
        return mSyncImageRepository.getAllImages();
    }

    public void deleteImage(SyncImage image) {
        mSyncImageRepository.deleteImage(image);
    }
}