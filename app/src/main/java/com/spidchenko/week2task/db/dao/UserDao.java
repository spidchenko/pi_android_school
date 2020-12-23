package com.spidchenko.week2task.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.spidchenko.week2task.db.models.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addUser(User user);

    @Query("SELECT * FROM users WHERE login LIKE :login")
    User getUser(String login);

}
