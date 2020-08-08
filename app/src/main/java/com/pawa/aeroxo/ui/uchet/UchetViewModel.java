package com.pawa.aeroxo.ui.uchet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UchetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UchetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Скоро тут что-то будет...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}