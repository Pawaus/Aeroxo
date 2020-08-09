package com.pawa.aeroxo.bd;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TrackViewModel extends AndroidViewModel {

    private TrackRepository repository;
    private int count;
    private LiveData<List<Track>> tracks;


    public TrackViewModel(@NonNull Application application) {
        super(application);
        repository = new TrackRepository(application);
        tracks = repository.getTracks();
        //repository.count();
        //count = repository.getCount();
    }

    public LiveData<List<Track>> getTracks(){return tracks;}

    public void insert(Track track){
        repository.insert(track);
    }

    public void deleteAll(){
        repository.deleteAll();
    }
//TODO:Решить вопрос с обновлением записей в таблицах - удалять все или обновлять по одной
    public void update(Track track){
        repository.update(track);
    }

    public int getCount(){
        repository.count();
        count = repository.getCount();
        return count;}
}
