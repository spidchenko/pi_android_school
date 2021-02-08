package com.spidchenko.week2task;

import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.repositories.FavouriteRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

public class FavouriteRepositoryTest {
    private static final int MOCK_USER_ID = 242;

    @Mock
    private FavouriteDao favouriteDaoMock;
    @Mock
    private SharedPrefRepository sharedPrefRepoMock;
    @Mock
    FavouriteRepository.RepositoryCallback<Boolean> repositoryCallbackMock;

    private final Executor executor = new CurrentThreadExecutor();

    private FavouriteRepository favouriteRepository;

    static class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(sharedPrefRepoMock.getUserId()).thenReturn(MOCK_USER_ID);

        favouriteRepository = FavouriteRepository.getInstance(favouriteDaoMock,
                sharedPrefRepoMock,
                executor);
    }

    @Test
    public void getFavouritesWithCategories_callDaoWithValidInput() {
        favouriteRepository.getFavouritesWithCategories();
        Mockito.verify(favouriteDaoMock).getFavouritesWithCategories(MOCK_USER_ID);
    }

    @Test
    public void addFavorite_completes() {
        favouriteRepository.addFavorite(new Favourite(), repositoryCallbackMock);
        Mockito.verify(repositoryCallbackMock).onComplete(Mockito.any());
    }

    @Test
    public void deleteFavourite_completes() {
        favouriteRepository.deleteFavourite(new Favourite(), repositoryCallbackMock);
        Mockito.verify(repositoryCallbackMock).onComplete(Mockito.any());
    }

}
