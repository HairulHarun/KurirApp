package com.example.gorontalo.kurir_app.model;

/**
 * Created by Gorontalo on 11/28/2018.
 */

public class LocationsModel {
    public double latitude, longitude;

    public LocationsModel() {
    }

    public LocationsModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
