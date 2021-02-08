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
    LogInHelper logInHelperMock;
    LoginViewModel loginViewModel;

    SingleLiveEvent<Boolean> event = new SingleLiveEvent<>();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        event.setValue(true);
        Mockito.when(logInHelperMock.getIsLoggedIn()).thenReturn(event);
        loginViewModel = new LoginViewModel(logInHelperMock);
    }

    @Test
    public void logIn_callHelperWithValidInput() {
        loginViewModel.logIn("TestUserName");
        Mockito.verify(logInHelperMock).logIn("TestUserName");
    }

    @Test
    public void getIsLoggedIn_hasValidReturnValue() {
        Assert.assertEquals(loginViewModel.getIsLoggedIn(), event);
    }

}
