package com.spidchenko.week2task.helpers;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.repositories.SharedPrefRepository;

import java.util.concurrent.Executor;

public class LogInHelper {
    private final UserDao mUserDao;
    private final SingleLiveEvent<Boolean> isLoggedIn = new SingleLiveEvent<>();
    private static volatile LogInHelper sInstance;
    private final Executor mExecutor;
    private final SharedPrefRepository mSharedPrefRepository;

    private LogInHelper(final AppDatabase appDatabase,
                        final SharedPrefRepository sharedPrefRepository,
                        final Executor executor) {
        mUserDao = appDatabase.userDao();
        isLoggedIn.setValue(false);
        mSharedPrefRepository = sharedPrefRepository;
        mExecutor = executor;
    }

    public SingleLiveEvent<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }


    public static LogInHelper getInstance(final AppDatabase appDatabase,
                                          final SharedPrefRepository sharedPrefRepository,
                                          final Executor executor) {
        if (sInstance == null) {
            synchronized (LogInHelper.class) {
                if (sInstance == null) {
                    sInstance = new LogInHelper(appDatabase, sharedPrefRepository, executor);
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
