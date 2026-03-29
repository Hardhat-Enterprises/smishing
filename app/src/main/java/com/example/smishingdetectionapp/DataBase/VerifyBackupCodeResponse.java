package com.example.smishingdetectionapp.DataBase;

import com.google.gson.annotations.SerializedName;

public class VerifyBackupCodeResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // JWT returned on success
    @SerializedName("token")
    private String token;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
}
