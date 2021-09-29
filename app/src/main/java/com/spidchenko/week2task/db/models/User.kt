package com.spidchenko.week2task.db.models

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "users")
class User() {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "login")
    var login: String? = null

    @Ignore
    constructor(login: String) : this() {
        this.login = login
    }

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}'
    }

    companion object {
        private const val TAG = "User.LOG_TAG"
    }
}