package com.spidchenko.week2task;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class UserDaoTest {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private UserDao userDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        userDao = database.userDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void addUser_savesData() {
        String login = "UserTestLogin";
        userDao.addUser(new User(login));
        Assert.assertNotNull(userDao.getUser(login));
    }

    @Test
    public void getUser_returnValidData() {
        String login = "AnotherUserTestLogin";
        userDao.addUser(new User(login));
        Assert.assertEquals(userDao.getUser(login).getLogin(), login);
    }

    @Test
    public void toString_returnValidData() {
        User user = new User("LogIn");
        Assert.assertEquals(user.toString(), "User{id=0, login='LogIn'}");
    }
}
