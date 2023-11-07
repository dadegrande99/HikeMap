package com.usi.hikemap.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.ActionCodeSettings;
import com.usi.hikemap.model.AuthenticationResponse;

public interface IUserRepository {

    MutableLiveData<AuthenticationResponse> sendSignInLinkToEmail(String auth, ActionCodeSettings actionCodeSettings);
    MutableLiveData<AuthenticationResponse> sendPasswordResetEmail(String auth);
    MutableLiveData<AuthenticationResponse> updatePassword(String password);

}

