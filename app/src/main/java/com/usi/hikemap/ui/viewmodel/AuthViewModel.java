package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.PhoneAuthCredential;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;
import com.usi.hikemap.repository.AuthenticationRepository;
import com.usi.hikemap.repository.IAuthenticationRepository;



public class AuthViewModel extends AndroidViewModel {

    private MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private final IAuthenticationRepository mAuthenticationRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.mAuthenticationRepository = new AuthenticationRepository(application);
    }

    public MutableLiveData<AuthenticationResponse> createUserWithEmail(User user) {
        mAuthenticationResponse = mAuthenticationRepository.createUserWithEmail(user);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> loginWithEmail(String email, String password) {
        mAuthenticationResponse = mAuthenticationRepository.loginWithEmail(email, password);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> createUserWithPhone(User user, PhoneAuthCredential credential) {
        mAuthenticationResponse = mAuthenticationRepository.createUserWithPhone(user, credential);
        return mAuthenticationResponse;
    }

    public MutableLiveData<AuthenticationResponse> createUserWithGoogle(GoogleSignInAccount account) {
        mAuthenticationResponse = mAuthenticationRepository.createUserWithGoogle(account);
        return mAuthenticationResponse;
    }

}
