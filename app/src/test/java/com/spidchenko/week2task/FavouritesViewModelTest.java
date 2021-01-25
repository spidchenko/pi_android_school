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
    private FavouriteRepository repositoryMock;

    private FavouritesViewModel favouritesViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(repositoryMock.getFavouritesWithCategories()).thenReturn(new MutableLiveData<>());
        favouritesViewModel = new FavouritesViewModel(repositoryMock);
    }

    @Test
    public void getFavouritesWithCategories_callRepo() {
        Mockito.verify(repositoryMock, Mockito.times(1)).getFavouritesWithCategories();
    }

    @Test
    public void deleteFavourites_callRepo() {
        Favourite favourite = new Favourite();
        favouritesViewModel.deleteFavourite(favourite);
        Mockito.verify(repositoryMock).deleteFavourite(eq(favourite), Mockito.any());
    }

    @Test
    public void getFavouritesWithCategories_returnNotNull() {
        Assert.assertNotNull(favouritesViewModel.getFavouritesWithCategories());
    }

    @Test
    public void getSnackBarMessage_returnNotNull() {
        Assert.assertNotNull(favouritesViewModel.getSnackBarMessage());
    }

}
