package com.pawa.aeroxo;

import android.app.Application;

import com.pawa.aeroxo.bd.Track;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FireBaseViewModel extends AndroidViewModel {
    FireBase FB;

    LiveData<List<Track>> tracks;

    public FireBaseViewModel(@NonNull Application application) {
        super(application);
        FB = new FireBase();
    }
}
