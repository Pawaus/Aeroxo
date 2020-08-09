package com.pawa.aeroxo;

import android.app.Application;
import android.util.Log;

import com.pawa.aeroxo.bd.Track;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ProfileModelView extends AndroidViewModel {
    private FireBase FB;

    public ProfileModelView(@NonNull Application application) {
        super(application);
        FB = new FireBase();
    }
    public LiveData<List<Track>>getTracksFirebase(){
        return FB.getTracksLive();
    }
    public LiveData<Map<String,Object>> getUserData(){
        return FB.getUserDataLive();
    }
}
