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
    private FavouriteRepository mFavouriteRepositoryMock;
    @Mock
    private SharedPrefRepository mSharedPrefRepositoryMock;
    @Mock
    private FileRepository mFileRepositoryMock;
    @Mock

    private ImageViewerViewModel mImageViewerViewModel;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mImageViewerViewModel = new ImageViewerViewModel(mFavouriteRepositoryMock,
                mSharedPrefRepositoryMock,
                mFileRepositoryMock);

        Mockito.when(mSharedPrefRepositoryMock.getUserId()).thenReturn(MOCK_USER_ID);
    }

    @Test
    public void getInFavourites_callRepoWithValidInput() {
        mImageViewerViewModel.getInFavourites("TestSearchString", "http://test.url");
        Mockito.verify(mFavouriteRepositoryMock).getFavourite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("TestSearchString") &&
                        f.getUrl().equals("http://test.url")));
    }

    @Test
    public void toggleFavourite_whenNotFavourite() {
        mImageViewerViewModel.toggleFavourite("NotInFavourites", "http://not.in");
        Mockito.verify(mFavouriteRepositoryMock).addFavorite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("NotInFavourites") &&
                        f.getUrl().equals("http://not.in")
        ), Mockito.any());
    }

    @Test
    public void toggleFavourite_whenFavourite() {
        Mockito.when(mFavouriteRepositoryMock.getFavourite(Mockito.any())).thenReturn(new MutableLiveData<>(new Favourite()));
        //Set in Favourites:
        mImageViewerViewModel.getInFavourites("", "");
        mImageViewerViewModel.toggleFavourite("InFavourites", "http://in.fav");
        Mockito.verify(mFavouriteRepositoryMock).deleteFavourite(Mockito.argThat(
                (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                        f.getSearchRequest().equals("InFavourites") &&
                        f.getUrl().equals("http://in.fav")), Mockito.any());
    }

    @Test
    public void saveImage_callRepoWithValidInput() {
        RequestManager requestManagerMock = Mockito.mock(RequestManager.class);
        ContentResolver contentResolverMock = Mockito.mock(ContentResolver.class);
        mImageViewerViewModel.saveImage(requestManagerMock, contentResolverMock,
                "ToSave", "http://to.save");
        Mockito.verify(mFileRepositoryMock).saveImage(Mockito.eq(requestManagerMock),
                Mockito.eq(contentResolverMock),
                Mockito.argThat(
                        (Favourite f) -> f.getUser() == MOCK_USER_ID &&
                                f.getSearchRequest().equals("ToSave") &&
                                f.getUrl().equals("http://to.save")));
    }

}
