package com.example.locationtrackerapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateRequest {

    @SerializedName("version")
    @Expose
    private String version;


    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("app_version")
    @Expose
    private String app_version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "version='" + version + '\'' +
                ", mobile='" + mobile + '\'' +
                ", app_version='" + app_version + '\'' +
                '}';
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

}
