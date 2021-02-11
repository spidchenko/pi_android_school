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
    private FavouriteDao mFavouriteDaoMock;
    @Mock
    private SharedPrefRepository mSharedPrefRepoMock;
    @Mock
    FavouriteRepository.RepositoryCallback<Boolean> mRepositoryCallbackMock;

    private final Executor mExecutor = new CurrentThreadExecutor();

    private FavouriteRepository mFavouriteRepository;

    static class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(mSharedPrefRepoMock.getUserId()).thenReturn(MOCK_USER_ID);

        mFavouriteRepository = FavouriteRepository.getInstance(mFavouriteDaoMock,
                mSharedPrefRepoMock,
                mExecutor);
    }

    @Test
    public void getFavouritesWithCategories_callDaoWithValidInput() {
        mFavouriteRepository.getFavouritesWithCategories();
        Mockito.verify(mFavouriteDaoMock).getFavouritesWithCategories(MOCK_USER_ID);
    }

    @Test
    public void addFavorite_completes() {
        mFavouriteRepository.addFavorite(new Favourite(), mRepositoryCallbackMock);
        Mockito.verify(mRepositoryCallbackMock).onComplete(Mockito.any());
    }

    @Test
    public void deleteFavourite_completes() {
        mFavouriteRepository.deleteFavourite(new Favourite(), mRepositoryCallbackMock);
        Mockito.verify(mRepositoryCallbackMock).onComplete(Mockito.any());
    }

}
