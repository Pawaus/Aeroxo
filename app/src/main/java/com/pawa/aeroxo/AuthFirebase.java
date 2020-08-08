package com.pawa.aeroxo;

import com.google.firebase.auth.FirebaseAuth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthFirebase extends ViewModel {

    MutableLiveData<FirebaseAuth> liveAuth = new MutableLiveData<>();

    public AuthFirebase(){
        liveAuth.setValue(FirebaseAuth.getInstance());
    }

    LiveData<FirebaseAuth>getAuth(){return liveAuth;}

}
