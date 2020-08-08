package com.pawa.aeroxo.bd;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TrackDao {

    @Query("SELECT * FROM track")
    List<Track> getAll();

    @Query("SELECT count(*) FROM track")
    int count();
    @Insert
    void insert(Track track);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Track track);

    @Delete
    void delete(Track track);

    @Query("DELETE FROM track")
    void deleteAll();

    @Query("Select * FROM track")
    LiveData<List<Track>> liveDataTrack();
}