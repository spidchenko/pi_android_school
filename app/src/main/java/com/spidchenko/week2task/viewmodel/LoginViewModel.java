package com.spidchenko.week2task.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spidchenko.week2task.helpers.LogInHelper;
import com.spidchenko.week2task.helpers.SingleLiveEvent;

public class LoginViewModel extends ViewModel {

    private final LogInHelper mLogInHelper;
    private final SingleLiveEvent<Boolean> isLoggedIn;

    public LoginViewModel(final LogInHelper logInHelper) {
        mLogInHelper = logInHelper;
        isLoggedIn = logInHelper.getIsLoggedIn();
    }

    public void logIn(String userName) {
        mLogInHelper.logIn(userName);
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

}
