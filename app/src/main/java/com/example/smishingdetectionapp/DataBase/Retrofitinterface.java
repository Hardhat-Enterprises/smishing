package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Header;

public interface Retrofitinterface {

    @POST("/login")
    Call<DBresult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);


    @POST("/checkemail")
    Call<SignupResponse> checkEmail(@Body HashMap<String, String> map);

    @POST("/checkUserID")
    Call<SignupResponse> checkUserID(@Query("userID") int userID);

    @GET("/user")
    Call<UserResponse> getUserDetails(@Header("Authorization") String token);
}
