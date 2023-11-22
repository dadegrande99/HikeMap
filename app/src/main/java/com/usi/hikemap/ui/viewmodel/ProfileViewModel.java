package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;
import com.usi.hikemap.repository.IManagerRepository;
import com.usi.hikemap.repository.ManagerRepository;

public class ProfileViewModel extends AndroidViewModel {
    private MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private MutableLiveData<User>mUserLiveData;
    private final IManagerRepository mManageRepository;

    public ProfileViewModel (@NonNull Application application){
        super(application);
        this.mManageRepository = new ManagerRepository(application);
    }

    public MutableLiveData<AuthenticationResponse> deleteAccount(String userId) {
        mAuthenticationResponse = mManageRepository.deleteAccount(userId);
        return mAuthenticationResponse;
    }

    public MutableLiveData<User> readUser(String userId) {
        mUserLiveData = mManageRepository.readUser(userId);
        return mUserLiveData;
    }
}
