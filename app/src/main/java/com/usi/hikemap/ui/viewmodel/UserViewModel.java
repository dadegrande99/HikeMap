package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.google.firebase.auth.ActionCodeSettings;
import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.repository.IUserRepository;
import com.usi.hikemap.repository.UserRepository;


public class UserViewModel extends  AndroidViewModel {

    private MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private final IUserRepository mUserRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.mUserRepository = new UserRepository(application);
    }

    public MutableLiveData<AuthenticationResponse> sendSignInLinkToEmail(String auth, ActionCodeSettings actionCodeSettings) {
        mAuthenticationResponse = mUserRepository.sendSignInLinkToEmail(auth, actionCodeSettings);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> sendPasswordResetEmail(String auth) {
        mAuthenticationResponse = mUserRepository.sendPasswordResetEmail(auth);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> updatePassword(String password) {
        mAuthenticationResponse = mUserRepository.updatePassword(password);
        return mAuthenticationResponse;
    }

}
