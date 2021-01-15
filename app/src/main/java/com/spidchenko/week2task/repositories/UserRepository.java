package com.spidchenko.week2task.repositories;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.helpers.SingleLiveEvent;

import java.util.concurrent.Executor;

public class UserRepository {
    private final UserDao mUserDao;
    private final SingleLiveEvent<Boolean> isLoggedIn = new SingleLiveEvent<>();
    private static volatile UserRepository sInstance;
    private final Executor mExecutor;

    private UserRepository(final AppDatabase appDatabase, Executor executor) {
        mUserDao = appDatabase.userDao();
        isLoggedIn.setValue(false);
        mExecutor = executor;
    }

    public SingleLiveEvent<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }


    public static UserRepository getInstance(final AppDatabase appDatabase, final Executor executor) {
        if (sInstance == null) {
            synchronized (UserRepository.class) {
                if (sInstance == null) {
                    sInstance = new UserRepository(appDatabase, executor);
                }
            }
        }
        return sInstance;
    }

    public void logIn(final String userName) {

        mExecutor.execute(() -> {
            User user = mUserDao.getUser(userName);
            if (user == null) {
                mUserDao.addUser(new User(userName));
                user = mUserDao.getUser(userName);
            }
            CurrentUser currentUser = CurrentUser.getInstance();
            currentUser.setUser(user);
            isLoggedIn.postValue(true);
        });
    }

}
