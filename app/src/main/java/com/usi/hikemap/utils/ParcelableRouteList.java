package com.usi.hikemap.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.usi.hikemap.model.Route;

import java.util.ArrayList;
import java.util.List;

public class ParcelableRouteList implements Parcelable {

    private List<Route> routeList;

    public ParcelableRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    protected ParcelableRouteList(Parcel in) {
        routeList = new ArrayList<>();
        in.readList(routeList, Route.class.getClassLoader());
    }

    public static final Creator<ParcelableRouteList> CREATOR = new Creator<ParcelableRouteList>() {
        @Override
        public ParcelableRouteList createFromParcel(Parcel in) {
            return new ParcelableRouteList(in);
        }

        @Override
        public ParcelableRouteList[] newArray(int size) {
            return new ParcelableRouteList[size];
        }
    };

    public List<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(routeList);
    }
}
