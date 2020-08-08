package com.pawa.aeroxo.ui.cheks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CheksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Скоро тут что-то будет...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}