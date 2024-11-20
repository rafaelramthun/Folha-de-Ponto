package com.rafael.folhadeponto.ui.mensagens;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MensagensViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MensagensViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mensagens fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}