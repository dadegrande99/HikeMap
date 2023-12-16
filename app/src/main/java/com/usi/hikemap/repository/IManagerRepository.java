package com.usi.hikemap.repository;

import android.net.Uri;


import androidx.lifecycle.MutableLiveData;

import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.model.User;

import java.util.List;
import java.util.Map;

public interface IManagerRepository {


    MutableLiveData<User> readUser(String userId);
    MutableLiveData<AuthenticationResponse> readImage(String userId);
    MutableLiveData<AuthenticationResponse> deleteAccount(String userId);

    MutableLiveData<AuthenticationResponse> writeImage(Uri profileUri);

    MutableLiveData<AuthenticationResponse> updateData (Map<String, Object> data);

    MutableLiveData<AuthenticationResponse> updateRoute(String userId, List<Route> route);

    MutableLiveData<List<Route>> readRoutes(String userId);

}
