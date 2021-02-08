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
    private FileRepository fileRepositoryMock;
    @Mock
    private ContentResolver contentResolverMock;

    private GalleryViewModel galleryViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        galleryViewModel = new GalleryViewModel(fileRepositoryMock);
    }

    @Test
    public void getImageFiles_callRepo() {
        galleryViewModel.getImageFiles();
        Mockito.verify(fileRepositoryMock).getImageFiles();
    }

    @Test
    public void deleteFile_callRepo() {
        File file = new File("TestFile");
        fileRepositoryMock.deleteFile(contentResolverMock, file);
        Mockito.verify(fileRepositoryMock).deleteFile(contentResolverMock, file);
    }

}
