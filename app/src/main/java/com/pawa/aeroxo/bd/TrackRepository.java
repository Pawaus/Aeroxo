package com.pawa.aeroxo.bd;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;

public class TrackRepository {
    private TrackDao trackDao;
    private int count;
    private LiveData<List<Track>> tracks;
    public TrackRepository(Application application){
        Database db = Database.getDatabase(application);
        trackDao = db.trackDao();
        tracks = trackDao.liveDataTrack();
        //count = trackDao.count();
    }
    int getCount(){
        count();
        return count;
    }
    public LiveData<List<Track>> getTracks(){
        return tracks;
    }
    void insert(final Track track){
        Database.databaseWriteExecutor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        trackDao.insert(track);
                    }
                }
        );
    }
    void count(){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                count = trackDao.count();
                return;
            }
        });
    }
    void deleteAll(){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.deleteAll();
            }
        });
    }
    void update(final Track track){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.update(track);
            }
        });
    }
}
