package com.example.smishingdetectionapp.data;

import com.example.smishingdetectionapp.DataBase.BackupCodesResponse;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.VerifyBackupCodeResponse;
import com.example.smishingdetectionapp.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles authentication and backup code management.
 */
public class LoginDataSource {

    private final Retrofitinterface retrofitService;

    public LoginDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Emulator localhost
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.retrofitService = retrofit.create(Retrofitinterface.class);
    }

    // ------------------ LOGIN ------------------
    public Result<LoggedInUser> login(String username, String password) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", username);
            map.put("password", password);

            Call<DBresult> call = retrofitService.executeLogin(map);
            Response<DBresult> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                LoggedInUser user = new LoggedInUser(
                        UUID.randomUUID().toString(),
                        username
                );
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Login failed"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication, clear stored tokens
    }

    // ------------------ PIN ------------------
    private String storedPin = "123456"; // replace with secure storage

    public boolean verifyPin(String pin) {
        return storedPin.equals(pin);
    }

    public void savePin(String pin) {
        storedPin = pin;
    }

    // ------------------ BACKUP CODES ------------------

    public Result<BackupCodesResponse> generateBackupCodes(String email) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);

            Call<BackupCodesResponse> call = retrofitService.generateBackupCodes(map);
            Response<BackupCodesResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return new Result.Success<>(response.body());
            } else {
                return new Result.Error(new IOException("Failed to generate backup codes"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error generating backup codes", e));
        }
    }

    public Result<VerifyBackupCodeResponse> verifyBackupCode(String email, String code) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("code", code);

            Call<VerifyBackupCodeResponse> call = retrofitService.verifyBackupCode(map);
            Response<VerifyBackupCodeResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return new Result.Success<>(response.body());
            } else {
                return new Result.Error(new IOException("Invalid backup code"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error verifying backup code", e));
        }
    }
}
