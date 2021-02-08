package com.spidchenko.week2task;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.FavouriteDao;
import com.spidchenko.week2task.db.models.Favourite;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.spidchenko.week2task.LiveDataTestUtil.getOrAwaitValue;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class FavouriteDaoTest {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private FavouriteDao favouriteDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        favouriteDao = database.favouriteDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void addFavourite_savesData() {
        Favourite favourite = new Favourite(1, "Test", "http://te.st");
        favouriteDao.addFavourite(favourite);
        Assert.assertNotNull(favouriteDao.getFavourite(1, "http://te.st"));
    }

    @Test
    public void getFavourite_returnValidData() throws InterruptedException {
        int userId = 115;
        String searchRequest = "Test";
        String url = "http://te.st";

        Favourite favourite = new Favourite(userId, searchRequest, url);
        favouriteDao.addFavourite(favourite);
        LiveData<Favourite> dbLiveData = favouriteDao.getFavourite(userId, url);
        getOrAwaitValue(dbLiveData);
        Assert.assertEquals(Objects.requireNonNull(dbLiveData.getValue()).getUrl(), url);
    }

    @Test
    public void getFavouritesWithCategories_returnValidData() throws InterruptedException {
        List<Favourite> favourites = Arrays.asList(
                new Favourite(1, "AAAA", "http://url.1"),
                new Favourite(1, "AAAA", "http://url.2"),
                new Favourite(2, "AAAA", "http://url.3"),
                new Favourite(1, "BBBB", "http://url.4"),
                new Favourite(1, "BBBB", "http://url.5"),
                new Favourite(1, "CCCC", "http://url.6")
        );

        List<Favourite> favouritesWithCategories = Arrays.asList(
                new Favourite(0, "AAAA", ""),
                new Favourite(1, "AAAA", "http://url.1"),
                new Favourite(1, "AAAA", "http://url.2"),

                new Favourite(0, "BBBB", ""),
                new Favourite(1, "BBBB", "http://url.4"),
                new Favourite(1, "BBBB", "http://url.5"),

                new Favourite(0, "CCCC", ""),
                new Favourite(1, "CCCC", "http://url.6")
        );

        for (Favourite f : favourites) {
            favouriteDao.addFavourite(f);
        }

        LiveData<List<Favourite>> favouritesFromDb = favouriteDao.getFavouritesWithCategories(1);
        getOrAwaitValue(favouritesFromDb);
        Assert.assertEquals(Objects.requireNonNull(favouritesFromDb.getValue()).size(), favouritesWithCategories.size());
    }

    @Test
    public void deleteFavourite_deletesData() throws InterruptedException {
        int userId = 115;
        String searchRequest = "Test";
        String url = "http://te.st";

        favouriteDao.addFavourite(new Favourite(userId, searchRequest, url));
        favouriteDao.deleteFavourite(userId, url);

        LiveData<Favourite> favouriteFromDb = favouriteDao.getFavourite(userId, url);
        getOrAwaitValue(favouriteFromDb);

        Assert.assertNull(favouriteFromDb.getValue());
    }

    @Test
    public void toString_returnValidData() {
        int userId = 333;
        String searchRequest = "TesT";
        String url = "http://te.st";
        Favourite favourite = new Favourite(userId, searchRequest, url);

        Assert.assertEquals(favourite.toString(),
                "Favourite{id=0, user=333, searchRequest='TesT', url='http://te.st'}");
    }
}
