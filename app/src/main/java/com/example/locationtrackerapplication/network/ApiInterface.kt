package com.example.locationtrackerapplication;

import com.google.gson.JsonObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {


    @PATCH
    Call<LocationUpdate> updateLocation(
            @Url String url,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authToken,
            @Body LocationUpdateRequest locationUpdate
    );



}
