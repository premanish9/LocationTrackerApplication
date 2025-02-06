package com.example.locationtrackerapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variable {
    @SerializedName("variable_name")
    @Expose
    private String name;
    private String value;
    @SerializedName("variable_id")
    @Expose
    private int id;



    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", id=" + id +
                '}';
    }
}
