package com.spidchenko.week2task;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Objects;

import static com.spidchenko.week2task.LiveDataTestUtil.getOrAwaitValue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class SearchRequestDaoTest {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private SearchRequestDao searchRequestDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        searchRequestDao = database.searchRequestDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void addSearchRequest_savesData() throws InterruptedException {
        int userId = 111;
        String request = "test";
        SearchRequest searchRequest = new SearchRequest(userId, request);

        searchRequestDao.addSearchRequest(searchRequest);

        LiveData<List<SearchRequest>> searchRequestLiveData = searchRequestDao.getAllSearchRequests(userId);
        getOrAwaitValue(searchRequestLiveData);
        Assert.assertNotNull(searchRequestLiveData.getValue());
    }

    @Test
    public void getAllSearchRequests_returnValidData() throws InterruptedException {
        int userId = 222;
        String request = "TEST";
        SearchRequest searchRequest = new SearchRequest(userId, request);

        searchRequestDao.addSearchRequest(searchRequest);

        LiveData<List<SearchRequest>> searchRequestLiveData = searchRequestDao.getAllSearchRequests(userId);
        getOrAwaitValue(searchRequestLiveData);
        Assert.assertEquals(Objects.requireNonNull(searchRequestLiveData.getValue()).get(0).getSearchRequest(), request);
    }
}
