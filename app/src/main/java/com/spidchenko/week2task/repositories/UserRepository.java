package com.spidchenko.week2task.repositories;

import android.util.Log;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;
import com.spidchenko.week2task.helpers.SingleLiveEvent;

public class UserRepository {
    private static final String TAG = "UserRepository.LOG_TAG";
    private final UserDao mUserDao;
    private int mUserId;
    private final SingleLiveEvent<Boolean> isLoggedIn = new SingleLiveEvent<>();
    private static volatile UserRepository sInstance;

    private UserRepository(final AppDatabase appDatabase) {
        mUserDao = appDatabase.userDao();
        isLoggedIn.setValue(false);
    }

    public SingleLiveEvent<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }


    public static UserRepository getInstance(final AppDatabase appDatabase) {
        if (sInstance == null) {
            synchronized (UserRepository.class) {
                if (sInstance == null) {
                    sInstance = new UserRepository(appDatabase);
                }
            }
        }
        return sInstance;
    }

    public void logIn(final String userName) {

        new Thread(() -> {
            Log.d(TAG, "actionSignIn: on Worker Thread." + Thread.currentThread().getName());
            User user = mUserDao.getUser(userName);
            if (user == null) {
                mUserDao.addUser(new User(userName));
                user = mUserDao.getUser(userName);
            }
            CurrentUser currentUser = CurrentUser.getInstance();
            currentUser.setUser(user);
            isLoggedIn.postValue(true);
        }).start();
    }

    public int getUserId() {
        return mUserId;
    }

//    public void setUser(User user) {
//        this.mUser = user;
//    }


}
