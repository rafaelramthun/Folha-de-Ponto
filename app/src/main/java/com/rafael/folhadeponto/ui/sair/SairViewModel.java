package com.rafael.folhadeponto.ui.sair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SairViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SairViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}