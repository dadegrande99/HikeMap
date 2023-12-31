package com.usi.hikemap.ui.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.model.User;
import com.usi.hikemap.repository.IManagerRepository;
import com.usi.hikemap.repository.ManagerRepository;

import java.util.List;
import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {
    private MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private MutableLiveData<User> mUserLiveData;
    private MutableLiveData<List<Route>> mRouteLiveDataL;
    private final IManagerRepository mManageRepository;

    public ProfileViewModel (@NonNull Application application){
        super(application);
        this.mManageRepository = new ManagerRepository(application);
    }

    public MutableLiveData<User> readUser(String userId) {
        mUserLiveData = mManageRepository.readUser(userId);
        return mUserLiveData;
    }

    public MutableLiveData<AuthenticationResponse> readImage(String userId) {
        mAuthenticationResponse = mManageRepository.readImage(userId);
        return mAuthenticationResponse;
    }


    //PUT MAP AL POSTO DI USER
    public MutableLiveData<AuthenticationResponse> updateProfile(Map<String, Object> data) {
        mAuthenticationResponse = mManageRepository.updateData(data);
        return mAuthenticationResponse;
    }
    //GO BACK TO UPDATE

    public MutableLiveData<AuthenticationResponse> writeImage(Uri profileUri) {
        mAuthenticationResponse = mManageRepository.writeImage(profileUri);
        return mAuthenticationResponse;
    }

    public MutableLiveData<List<Route>> readRoutes(String userId) {
        mRouteLiveDataL = mManageRepository.readRoutes(userId);
        return mRouteLiveDataL;
    }

    public MutableLiveData<AuthenticationResponse> deleteAccount(String userId) {
        mAuthenticationResponse = mManageRepository.deleteAccount(userId);
        return mAuthenticationResponse;
    }
}
