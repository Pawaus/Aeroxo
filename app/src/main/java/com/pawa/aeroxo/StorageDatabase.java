package com.pawa.aeroxo;

import android.app.Application;

import com.pawa.aeroxo.bd.Database;

import androidx.room.Room;

public class StorageDatabase extends Application {

    public static StorageDatabase instance;

    private Database database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, Database.class, "database")
                .build();
    }

    public static StorageDatabase getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}