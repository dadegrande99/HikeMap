package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.model.User;
import com.usi.hikemap.repository.IManagerRepository;
import com.usi.hikemap.repository.ManagerRepository;

import java.util.List;
import java.util.Map;


public class GoViewModel extends AndroidViewModel {

    private MutableLiveData<User> mUserLiveData;
    private final IManagerRepository mManageRepository;
    private MutableLiveData<AuthenticationResponse> mAuthenticationResponse;

    private boolean isFirstTime = true;

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public GoViewModel (Application application){
        super(application);
        this.mManageRepository = new ManagerRepository(application);
    }

    public MutableLiveData<User> readUser(String userId) {
        mUserLiveData = mManageRepository.readUser(userId);
        return mUserLiveData;
    }

    public MutableLiveData<AuthenticationResponse> updateData(Map<String, Object> data) {
        mAuthenticationResponse = mManageRepository.updateData(data);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> updateRoute(String userId, List<Route> route) {
        mAuthenticationResponse = mManageRepository.updateRoute(userId, route);
        return mAuthenticationResponse;
    }



}