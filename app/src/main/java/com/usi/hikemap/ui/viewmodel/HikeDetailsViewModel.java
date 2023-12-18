package com.usi.hikemap.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.usi.hikemap.model.Route;
import com.usi.hikemap.repository.IManagerRepository;
import com.usi.hikemap.repository.ManagerRepository;

import java.util.List;

public class HikeDetailsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Route>> readeRoute;
    private final IManagerRepository mManageRepository;
    public HikeDetailsViewModel(@NonNull Application application) {
        super(application);
        //this.readeRoute = readeRoute;
        this.mManageRepository = new ManagerRepository(application);
    }

    public MutableLiveData<List<Route>> readRoute(String routeId) {
        readeRoute = mManageRepository.readRoute(routeId);
        return readeRoute;
    }

    public MutableLiveData<List<Route>> readRoutes(String userId) {
        readeRoute = mManageRepository.readRoutes(userId);
        return readeRoute;
    }
}
