package com.example.locationtrackerapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocationUpdate {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("duty_status")
    @Expose
    private Boolean duty_status;





    @SerializedName("variables")
    @Expose
    private ArrayList<Variable> variables;
    @Override
    public String toString() {
        return "LocationUpdate{" +
                "message='" + message + '\'' +
                ", duty_status=" + duty_status +
                ", variables=" + variables +
                '}';
    }

    public Boolean getDuty_status() {
        return duty_status;
    }

    public void setDuty_status(Boolean duty_status) {
        this.duty_status = duty_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }


}


