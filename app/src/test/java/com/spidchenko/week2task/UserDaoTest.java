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
    public InstantTaskExecutorRule mExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;
    private UserDao mUserDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        mUserDao = mDatabase.userDao();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void addUser_savesData() {
        String login = "UserTestLogin";
        mUserDao.addUser(new User(login));
        Assert.assertNotNull(mUserDao.getUser(login));
    }

    @Test
    public void getUser_returnValidData() {
        String login = "AnotherUserTestLogin";
        mUserDao.addUser(new User(login));
        Assert.assertEquals(mUserDao.getUser(login).getLogin(), login);
    }

    @Test
    public void toString_returnValidData() {
        User user = new User("LogIn");
        Assert.assertEquals(user.toString(), "User{id=0, login='LogIn'}");
    }
}
