package com.example.snargemobile.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.snargemobile.entity.User;


@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
