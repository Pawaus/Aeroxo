package com.pawa.aeroxo.bd;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {Track.class,Contact.class},version = 1)
public abstract class Database extends RoomDatabase {
    public abstract TrackDao trackDao();
    public abstract ContactDao contactDao();
    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREAD = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);

    static Database getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (Database.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),Database.class,
                            "database").build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            TrackDao dao = INSTANCE.trackDao();
            //dao.deleteAll();
        }
    };
}