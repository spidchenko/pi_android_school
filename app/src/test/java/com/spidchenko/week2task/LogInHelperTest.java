package com.spidchenko.week2task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.helpers.LogInHelper;
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

    private final Executor mExecutor = new LogInHelperTest.CurrentThreadExecutor();
    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule mExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private UserDao mUserDaoMock;
    @Mock
    private SharedPrefRepository mSharedPrefRepositoryMock;
    private LogInHelper mLogInHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mLogInHelper = LogInHelper.getInstance(mUserDaoMock, mSharedPrefRepositoryMock, mExecutor);
    }

    @Test
    public void logIn_saveUserIdInSharedPref() {
        int userId = 55;
        String userName = "TestUserName";
        User user = new User(userName);
        user.setId(userId);

        Mockito.when(mUserDaoMock.getUser(Mockito.anyString())).thenReturn(user);
        mLogInHelper.logIn(userName);

        Mockito.verify(mSharedPrefRepositoryMock).saveUserId(userId);
    }

    @Test
    public void logIn_setsIsLoggedInLiveData() throws InterruptedException {
        LiveData<Boolean> isLogged = mLogInHelper.isLoggedIn();
        getOrAwaitValue(isLogged);
        Assert.assertNotNull(isLogged);
    }

    static private class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

}
