package com.usi.hikemap.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is go fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}