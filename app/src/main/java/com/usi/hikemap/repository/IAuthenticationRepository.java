package com.usi.hikemap.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.PhoneAuthCredential;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;


public interface IAuthenticationRepository {

    MutableLiveData<AuthenticationResponse> createUserWithEmail (User user);
    MutableLiveData<AuthenticationResponse> loginWithEmail(String email, String password);
    MutableLiveData<AuthenticationResponse> createUserWithPhone(User user, PhoneAuthCredential credential);
    MutableLiveData<AuthenticationResponse> createUserWithGoogle (GoogleSignInAccount account);

}

