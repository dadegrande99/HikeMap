package com.usi.hikemap.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {

    private int id, idRoute, subRoute;
    private String timestamp;
    private double altitude, longitude, latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
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

    public Route(int id, int idRoute, int subRoute, String timestamp, double altitude, double longitude, double latitude) {
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
}

