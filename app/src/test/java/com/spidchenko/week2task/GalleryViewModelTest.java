package com.spidchenko.week2task;

import android.content.ContentResolver;

import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.viewmodel.GalleryViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

public class GalleryViewModelTest {

    @Mock
    private FileRepository mFileRepositoryMock;
    @Mock
    private ContentResolver mContentResolverMock;

    private GalleryViewModel mGalleryViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mGalleryViewModel = new GalleryViewModel(mFileRepositoryMock);
    }

    @Test
    public void getImageFiles_callRepo() {
        mGalleryViewModel.getImageFiles();
        Mockito.verify(mFileRepositoryMock).getImageFiles();
    }

    @Test
    public void deleteFile_callRepo() {
        File file = new File("TestFile");
        mFileRepositoryMock.deleteFile(mContentResolverMock, file);
        Mockito.verify(mFileRepositoryMock).deleteFile(mContentResolverMock, file);
    }

}
