package com.example.locationtrackerapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationUpdateRequest {
    public LocationUpdateRequest(Double lat, Double lng, Float direction) {
        this.lat = lat;
        this.lng = lng;
        this.direction=direction;
    }

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;


    @SerializedName("direction")
    @Expose
    private Float direction;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "LocationUpdateRequest{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", direction=" + direction +
                '}';
    }
}
