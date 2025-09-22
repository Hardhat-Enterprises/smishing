package com.example.smishingdetectionapp.data;

import com.example.smishingdetectionapp.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.BackupCodesResponse;
import com.example.smishingdetectionapp.DataBase.VerifyBackupCodeResponse;



/**
 * Repository that manages authentication and backup codes
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;
    private LoggedInUser user = null;

    // Retrofit
    private final Retrofitinterface retrofitInterface;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // emulator -> localhost:3000
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(Retrofitinterface.class);
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    // ---------------- NORMAL LOGIN ----------------
    public Result<LoggedInUser> login(String username, String password) {
        // Still uses DataSource (fake user)
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    // ---------------- BACKUP CODES ----------------
    public void generateBackupCodes(String email, BackupCallback callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<BackupCodesResponse> call = retrofitInterface.generateBackupCodes(map);
        call.enqueue(new Callback<BackupCodesResponse>() {
            @Override
            public void onResponse(Call<BackupCodesResponse> call, Response<BackupCodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    callback.onComplete(new Result.Success<>(response.body()));
                } else {
                    callback.onComplete(new Result.Error(new IOException("Failed: " + response.message())));
                }
            }

            @Override
            public void onFailure(Call<BackupCodesResponse> call, Throwable t) {
                callback.onComplete(new Result.Error(new IOException("Network error", t)));
            }
        });
    }

    public void verifyBackupCode(String email, String code, BackupCallback callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("code", code);

        Call<VerifyBackupCodeResponse> call = retrofitInterface.verifyBackupCode(map);
        call.enqueue(new Callback<VerifyBackupCodeResponse>() {
            @Override
            public void onResponse(Call<VerifyBackupCodeResponse> call, Response<VerifyBackupCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(new Result.Success<>(response.body()));
                } else {
                    callback.onComplete(new Result.Error(new IOException("Failed: " + response.message())));
                }
            }

            @Override
            public void onFailure(Call<VerifyBackupCodeResponse> call, Throwable t) {
                callback.onComplete(new Result.Error(new IOException("Network error", t)));
            }
        });
    }

    // Simple callback interface for async results
    public interface BackupCallback {
        void onComplete(Result<?> result);
    }
}
