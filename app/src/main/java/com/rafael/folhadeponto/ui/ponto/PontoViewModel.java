package com.rafael.folhadeponto.ui.ponto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PontoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PontoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}