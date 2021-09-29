package com.spidchenko.week2task.repositories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlin.jvm.Volatile

class SharedPrefRepository private constructor(application: Application) {
    val userId: Int
        get() {
            Log.d(TAG, "getLogin: " + sSharedPreferences.getInt(PREF_USER_ID_KEY, -1))
            return sSharedPreferences.getInt(PREF_USER_ID_KEY, -1)
        }
    val lastSearch: String?
        get() {
            Log.d(TAG, "getLastSearch: " + sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, ""))
            return sSharedPreferences.getString(PREF_LAST_SEARCH_KEY, "")
        }

    fun saveUserId(userId: Int) {
        Log.d(TAG, "saveUserId: $userId")
        val editor = sSharedPreferences.edit()
        editor.putInt(PREF_USER_ID_KEY, userId)
        editor.apply()
    }

    fun saveLastSearch(lastSearch: String) {
        Log.d(TAG, "saveLastSearch: $lastSearch")
        val editor = sSharedPreferences.edit()
        editor.putString(PREF_LAST_SEARCH_KEY, lastSearch)
        editor.apply()
    }

    companion object {
        private const val PREF_FILE_KEY = "com.spidchenko.week2task.PREF_FILE_KEY"
        private const val PREF_USER_ID_KEY = "com.spidchenko.week2task.PREF_LOGIN_KEY"
        private const val PREF_LAST_SEARCH_KEY = "com.spidchenko.week2task.PREF_LAST_SEARCH_KEY"
        private const val TAG = "SharedPrefRepo.LOG_TAG"

        @Volatile
        private var sInstance: SharedPrefRepository? = null
        private lateinit var sSharedPreferences: SharedPreferences

        @JvmStatic
        fun getInstance(application: Application): SharedPrefRepository? {
            if (sInstance == null) {
                synchronized(SharedPrefRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = SharedPrefRepository(application)
                    }
                }
            }
            return sInstance
        }
    }

    init {
        sSharedPreferences = application.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE)
    }
}