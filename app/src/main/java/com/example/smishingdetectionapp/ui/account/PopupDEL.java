package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupDEL extends AppCompatActivity {

    private EditText passwordEditText;
    private Button confirmDelYesBtn, confirmDelNoBtn;
    private Retrofitinterface retrofitinterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_delete_account);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVERIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        passwordEditText = findViewById(R.id.del_accPW);
        confirmDelYesBtn = findViewById(R.id.confirmDelYesBtn);
        confirmDelNoBtn = findViewById(R.id.confirmDelNoBtn);

        confirmDelNoBtn.setOnClickListener(view -> {
            finish();
        });

        confirmDelYesBtn.setOnClickListener(view -> handleAccountDeletion());

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                confirmDelYesBtn.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    private void handleAccountDeletion() {
        String enteredPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", 0);
        String savedToken = prefs.getString("JWT_TOKEN", null);

        if (savedToken == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + savedToken;
        HashMap<String, String> map = new HashMap<>();
        map.put("password", enteredPassword);

        retrofitinterface.deleteAccount(token, map).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PopupDEL.this, "Account successfully deleted", Toast.LENGTH_LONG).show();
                    
                    // Clear token and logout
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("JWT_TOKEN");
                    editor.apply();

                    Intent intent = new Intent(PopupDEL.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Deletion failed";
                    try (ResponseBody responseBody = response.errorBody()) {
                        if (responseBody != null) {
                            errorMsg = responseBody.string();
                        }
                    } catch (Exception e) {}
                    
                    new AlertDialog.Builder(PopupDEL.this)
                            .setTitle("Error")
                            .setMessage(errorMsg)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(PopupDEL.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
