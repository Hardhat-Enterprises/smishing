package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
public interface Retrofitinterface {

    @POST("api/auth/login")
    Call<DBresult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);


    @POST("/checkemail")
    Call<SignupResponse> checkEmail(@Body HashMap<String, String> map);

    @PUT("api/userUpdate/update")
    Call<Void> updateProfile(
            @Header("Authorization") String token,
            @Body HashMap<String, String> map
    );
}
