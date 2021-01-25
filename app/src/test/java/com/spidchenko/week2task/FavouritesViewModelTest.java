package com.spidchenko.week2task;

import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;

public class FavouritesViewModelTest {

    FavouriteRepository repositoryMock = Mockito.mock(FavouriteRepository.class);

    FavouritesViewModel favouritesViewModel;

    @Before
    public void setUp() {
        favouritesViewModel = new FavouritesViewModel(repositoryMock);
    }

    @Test
    public void check_getFavouritesWithCategories() {
        Mockito.verify(repositoryMock).getFavouritesWithCategories();
    }

    @Test
    public void check_deleteFavourites() {
        Favourite favourite = new Favourite();
        favouritesViewModel.deleteFavourite(favourite);
        Mockito.verify(repositoryMock).deleteFavourite(eq(favourite), Mockito.any());
    }
}
