package com.spidchenko.week2task.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.spidchenko.week2task.db.dao.UserDao
import com.spidchenko.week2task.db.models.User
import com.spidchenko.week2task.helpers.LogInHelper
import com.spidchenko.week2task.repositories.SharedPrefRepository
import java.util.concurrent.Executor

class LogInHelper private constructor(
    private val mUserDao: UserDao,
    sharedPrefRepository: SharedPrefRepository,
    executor: Executor
) {
    private val isLoggedIn = MutableLiveData<Boolean>()
    private val mExecutor: Executor
    private val mSharedPrefRepository: SharedPrefRepository
    fun isLoggedIn(): LiveData<Boolean> {
        return isLoggedIn
    }

    fun logIn(userName: String?) {
        mExecutor.execute {
            var user = mUserDao.getUser(userName)
            if (user == null) {
                mUserDao.addUser(User(userName!!))
                user = mUserDao.getUser(userName)
            }
            mSharedPrefRepository.saveUserId(user!!.id)
            isLoggedIn.postValue(true)
        }
    }

    companion object {
        @Volatile
        private var sInstance: LogInHelper? = null
        @JvmStatic
        fun getInstance(
            userDao: UserDao,
            sharedPrefRepository: SharedPrefRepository,
            executor: Executor
        ): LogInHelper? {
            if (sInstance == null) {
                synchronized(LogInHelper::class.java) {
                    if (sInstance == null) {
                        sInstance = LogInHelper(userDao, sharedPrefRepository, executor)
                    }
                }
            }
            return sInstance
        }
    }

    init {
        isLoggedIn.value = false
        mSharedPrefRepository = sharedPrefRepository
        mExecutor = executor
    }
}