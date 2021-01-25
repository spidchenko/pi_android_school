package com.spidchenko.week2task;

import android.content.ContentResolver;

import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.RequestManager;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.viewmodel.ImageViewerViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ImageViewerViewModelTest {
    private static final int MOCK_USER_ID = 42;

    @Mock
    FavouriteRepository favouriteRepositoryMock;
    @Mock
    SharedPrefRepository sharedPrefRepositoryMock;
    @Mock
    FileRepository fileRepositoryMock;
    @Mock

    ImageViewerViewModel imageViewerViewModel;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        imageViewerViewModel = new ImageViewerViewModel(favouriteRepositoryMock,
                sharedPrefRepositoryMock,
                fileRepositoryMock);

        Mockito.when(sharedPrefRepositoryMock.getUserId()).thenReturn(MOCK_USER_ID);
    }

    @Test
    public void getInFavourites_callRepoWithValidInput() {
        imageViewerViewModel.getInFavourites("TestSearchString", "http://test.url");
        Mockito.verify(favouriteRepositoryMock).getFavourite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("TestSearchString") &&
                        f.getUrl().equals("http://test.url")));
    }

    @Test
    public void toggleFavourite_whenNotFavourite() {
        imageViewerViewModel.toggleFavourite("NotInFavourites", "http://not.in");
        Mockito.verify(favouriteRepositoryMock).addFavorite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("NotInFavourites") &&
                        f.getUrl().equals("http://not.in")
        ), Mockito.any());
    }

    @Test
    public void toggleFavourite_whenFavourite() {
        Mockito.when(favouriteRepositoryMock.getFavourite(Mockito.any())).thenReturn(new MutableLiveData<>(new Favourite()));
        //Set in Favourites:
        imageViewerViewModel.getInFavourites("", "");
        imageViewerViewModel.toggleFavourite("InFavourites", "http://in.fav");
        Mockito.verify(favouriteRepositoryMock).deleteFavourite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("InFavourites") &&
                        f.getUrl().equals("http://in.fav")), Mockito.any());
    }

    @Test
    public void saveImage_callRepoWithValidInput() {
        RequestManager requestManagerMock = Mockito.mock(RequestManager.class);
        ContentResolver contentResolverMock = Mockito.mock(ContentResolver.class);
        imageViewerViewModel.saveImage(requestManagerMock, contentResolverMock,
                "ToSave", "http://to.save");
        Mockito.verify(fileRepositoryMock).saveImage(Mockito.eq(requestManagerMock),
                Mockito.eq(contentResolverMock),
                Mockito.argThat(
                        (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                                f.getSearchRequest().equals("ToSave") &&
                                f.getUrl().equals("http://to.save")));
    }

}
