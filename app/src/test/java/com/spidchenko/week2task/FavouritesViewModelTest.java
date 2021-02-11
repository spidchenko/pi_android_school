package com.spidchenko.week2task;

import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;

public class FavouritesViewModelTest {

    @Mock
    private FavouriteRepository mRepositoryMock;

    private FavouritesViewModel mFavouritesViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(mRepositoryMock.getFavouritesWithCategories()).thenReturn(new MutableLiveData<>());
        mFavouritesViewModel = new FavouritesViewModel(mRepositoryMock);
    }

    @Test
    public void getFavouritesWithCategories_callRepo() {
        Mockito.verify(mRepositoryMock, Mockito.times(1)).getFavouritesWithCategories();
    }

    @Test
    public void deleteFavourites_callRepo() {
        Favourite favourite = new Favourite();
        mFavouritesViewModel.deleteFavourite(favourite);
        Mockito.verify(mRepositoryMock).deleteFavourite(eq(favourite), Mockito.any());
    }

    @Test
    public void getFavouritesWithCategories_returnNotNull() {
        Assert.assertNotNull(mFavouritesViewModel.getFavouritesWithCategories());
    }

    @Test
    public void getSnackBarMessage_returnNotNull() {
        Assert.assertNotNull(mFavouritesViewModel.getSnackBarMessage());
    }

}
