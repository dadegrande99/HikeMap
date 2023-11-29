package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.usi.hikemap.model.User;
import com.usi.hikemap.repository.IManagerRepository;
import com.usi.hikemap.repository.ManagerRepository;


public class GoViewModel extends AndroidViewModel {

    private MutableLiveData<User> mUserLiveData;
    private final IManagerRepository mManageRepository;
    private SavedStateHandle state;

    private boolean isFirstTime = true;

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public GoViewModel (SavedStateHandle savedStateHandle, @NonNull Application application){
        super(application);
        state = savedStateHandle;
        this.mManageRepository = new ManagerRepository(application);
    }

    public MutableLiveData<User> readUser(String userId) {
        mUserLiveData = mManageRepository.readUser(userId);
        return mUserLiveData;
    }


}