package com.spidchenko.week2task.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spidchenko.week2task.db.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: User?)

    @Query("SELECT * FROM users WHERE login LIKE :login")
    fun getUser(login: String?): User?
}