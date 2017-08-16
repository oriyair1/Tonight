package com.example.or.tonight;

/**
 * Created by oriya on 16/08/2017.
 */

public class SimpleLocation {

    float longitude;
    float latitude;

    public SimpleLocation(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
