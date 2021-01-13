package com.spidchenko.week2task.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.spidchenko.week2task.FavouriteRepository;
import com.spidchenko.week2task.MyApplication;
import com.spidchenko.week2task.helpers.SingleLiveEvent;
import com.spidchenko.week2task.repositories.FileRepository;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.repositories.UserRepository;

public class LoginViewModel extends ViewModel {

    private final UserRepository mUserRepository;
    private final SingleLiveEvent<Boolean> isLoggedIn;

    public LoginViewModel(final UserRepository userRepository) {
        mUserRepository = userRepository;
        isLoggedIn = userRepository.getIsLoggedIn();
    }

    public void logIn(String userName){
        mUserRepository.logIn(userName);
    }

    public LiveData<Boolean> getIsLoggedIn(){
        return isLoggedIn;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final UserRepository userRepository;

        public Factory(@NonNull Application application) {
            userRepository = ((MyApplication) application).getUserRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginViewModel(userRepository);
        }
    }
}
