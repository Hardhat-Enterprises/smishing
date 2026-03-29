package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;

import com.example.smishingdetectionapp.data.model.ContactUsMessage;
import com.example.smishingdetectionapp.data.model.ContactUsResponse;

public interface Retrofitinterface {

    @POST("/login")
    Call<DBresult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);

    @POST("/checkemail")
    Call<SignupResponse> checkEmail(@Body HashMap<String, String> map);

    // 🔹 Contact Us APIs
    @POST("/api/contactus")
    Call<ContactUsResponse> sendContactMessage(@Body HashMap<String, String> map);

    @GET("/api/contactus")
    Call<List<ContactUsMessage>> getAllMessages();

    @GET("/api/contactus/{id}")
    Call<ContactUsMessage> getMessageById(@Path("id") String id);

    @PATCH("/api/contactus/{id}")
    Call<ContactUsMessage> updateMessageStatus(@Path("id") String id, @Body HashMap<String, String> map);

    @DELETE("/api/contactus/{id}")
    Call<HashMap<String, String>> deleteMessage(@Path("id") String id);
}
