package com.rafael.folhadeponto.ui.pontoEletronico;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PontoEletronicoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PontoEletronicoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Ponto Eletrônico fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}