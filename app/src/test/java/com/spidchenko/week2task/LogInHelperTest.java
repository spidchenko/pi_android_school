package com.spidchenko.week2task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.helpers.LogInHelper;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

import static com.spidchenko.week2task.LiveDataTestUtil.getOrAwaitValue;

public class LogInHelperTest {

    private final Executor executor = new LogInHelperTest.CurrentThreadExecutor();
    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();
    @Mock
    private UserDao userDaoMock;
    @Mock
    private SharedPrefRepository sharedPrefRepositoryMock;
    private LogInHelper logInHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logInHelper = LogInHelper.getInstance(userDaoMock, sharedPrefRepositoryMock, executor);
    }

    @Test
    public void logIn_saveUserIdInSharedPref() {
        int userId = 55;
        String userName = "TestUserName";
        User user = new User(userName);
        user.setId(userId);

        Mockito.when(userDaoMock.getUser(Mockito.anyString())).thenReturn(user);
        logInHelper.logIn(userName);

        Mockito.verify(sharedPrefRepositoryMock).saveUserId(userId);
    }

    @Test
    public void getIsLoggedIn() throws InterruptedException {
        SingleLiveEvent<Boolean> isLogged = logInHelper.getIsLoggedIn();
        getOrAwaitValue(isLogged);
        Assert.assertNotNull(isLogged);
    }

    static private class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

}
