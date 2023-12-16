package com.usi.hikemap.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route implements Parcelable {

    private int id, subRoute;
    private String timestamp, idRoute;
    private double altitude, longitude, latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(String idRoute) {
        this.idRoute = idRoute;
    }

    public int getSubRoute() {
        return subRoute;
    }

    public void setSubRoute(int subRoute) {
        this.subRoute = subRoute;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Route(int id, String idRoute, int subRoute, String timestamp, double altitude, double longitude, double latitude) {
        this.id = id;
        this.idRoute = idRoute;
        this.subRoute = subRoute;
        this.timestamp = timestamp;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", idRoute=" + idRoute +
                ", subRoute=" + subRoute +
                ", timestamp='" + timestamp + '\'' +
                ", altitude=" + altitude +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    protected Route(Parcel in) {
        id = in.readInt();
        idRoute = in.readString();
        subRoute = in.readInt();
        timestamp = in.readString();
        altitude = in.readDouble();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.idRoute);
        parcel.writeInt(this.subRoute);
        parcel.writeString(this.timestamp);
        parcel.writeDouble(this.altitude);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitude);

    }
}

