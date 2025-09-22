package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Retrofitinterface {

    // ------------------ AUTH ------------------
    @POST("/login")
    Call<DBresult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);

    @POST("/checkemail")
    Call<SignupResponse> checkEmail(@Body HashMap<String, String> map);

    // ------------------ BACKUP CODES (no JWT) ------------------
    // Body: { "email": "<user@example.com>" }
    @POST("/api/auth/generate-backup-codes")
    Call<BackupCodesResponse> generateBackupCodes(@Body HashMap<String, String> map);

    // Body: { "email": "<user@example.com>" }
    @POST("/api/auth/regenerate-backup-codes")
    Call<BackupCodesResponse> regenerateBackupCodes(@Body HashMap<String, String> map);

    // Body: { "email": "<user@example.com>", "code": "<8-char>" }
    @POST("/api/auth/verify-backup-code")
    Call<VerifyBackupCodeResponse> verifyBackupCode(@Body HashMap<String, String> map);
}
