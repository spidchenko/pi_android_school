package com.spidchenko.week2task.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import java.util.concurrent.Executor;

public class LogInHelper {
    private final UserDao mUserDao;
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private static volatile LogInHelper sInstance;
    private final Executor mExecutor;
    private final SharedPrefRepository mSharedPrefRepository;

    private LogInHelper(final UserDao userDao,
                        final SharedPrefRepository sharedPrefRepository,
                        final Executor executor) {
        mUserDao = userDao;
        isLoggedIn.setValue(false);
        mSharedPrefRepository = sharedPrefRepository;
        mExecutor = executor;
    }

    public LiveData<Boolean> isLoggedIn() {
        return isLoggedIn;
    }


    public static LogInHelper getInstance(final UserDao userDao,
                                          final SharedPrefRepository sharedPrefRepository,
                                          final Executor executor) {
        if (sInstance == null) {
            synchronized (LogInHelper.class) {
                if (sInstance == null) {
                    sInstance = new LogInHelper(userDao, sharedPrefRepository, executor);
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

            mSharedPrefRepository.saveUserId(user.getId());
            isLoggedIn.postValue(true);
        });
    }

}
