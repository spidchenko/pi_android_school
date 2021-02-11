package com.spidchenko.week2task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.spidchenko.week2task.helpers.LogInHelper;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.viewmodel.LoginViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LoginViewModelTest {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    private LogInHelper mLogInHelperMock;
    private LoginViewModel mLoginViewModel;

    private final SingleLiveEvent<Boolean> mEvent = new SingleLiveEvent<>();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mEvent.setValue(true);
        Mockito.when(mLogInHelperMock.isLoggedIn()).thenReturn(mEvent);
        mLoginViewModel = new LoginViewModel(mLogInHelperMock);
    }

    @Test
    public void logIn_callHelperWithValidInput() {
        mLoginViewModel.logIn("TestUserName");
        Mockito.verify(mLogInHelperMock).logIn("TestUserName");
    }

    @Test
    public void getIsLoggedIn_hasValidReturnValue() {
        Assert.assertEquals(mLoginViewModel.getIsLoggedIn(), mEvent);
    }

}
