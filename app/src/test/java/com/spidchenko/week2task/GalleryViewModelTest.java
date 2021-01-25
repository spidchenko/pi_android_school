package com.spidchenko.week2task;

import android.content.ContentResolver;

import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.viewmodel.GalleryViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class GalleryViewModelTest {

    FileRepository fileRepositoryMock = Mockito.mock(FileRepository.class);
    ContentResolver contentResolverMock = Mockito.mock(ContentResolver.class);
    GalleryViewModel galleryViewModel;

    @Before
    public void setUp() {
        galleryViewModel = new GalleryViewModel(fileRepositoryMock);
    }

    @Test
    public void check_getImageFiles() {
        galleryViewModel.getImageFiles();
        Mockito.verify(fileRepositoryMock).getImageFiles();
    }

    @Test
    public void check_deleteFile() {
        File file = new File("TestFile");
        fileRepositoryMock.deleteFile(contentResolverMock, file);
        Mockito.verify(fileRepositoryMock).deleteFile(contentResolverMock, file);
    }

}
