package com.usi.hikemap.repository;

import android.net.Uri;


import androidx.lifecycle.MutableLiveData;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;

import java.util.List;
import java.util.Map;

public interface IManagerRepository {

    MutableLiveData<AuthenticationResponse> updateData (Map<String, Object> data);
    MutableLiveData<User> readUser(String userId);
    MutableLiveData<AuthenticationResponse> deleteAccount(String userId);
}
