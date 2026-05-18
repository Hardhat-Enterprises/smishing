package com.example.smishingdetectionapp.ui.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupPN extends BottomSheetDialogFragment {

    private EditText etPhone;
    private Button btnUpdate;
    private Retrofitinterface retrofitinterface;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.popup_phone_number, container, false);

        // Bind UI
        etPhone = v.findViewById(R.id.change_accPN);
        btnUpdate = v.findViewById(R.id.pn_changeBtn);

        // Initialize Retrofit (same as email)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVERIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        btnUpdate.setEnabled(false);

        // TextWatcher
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        };

        etPhone.addTextChangedListener(watcher);

        // Button click → API call
        btnUpdate.setOnClickListener(v1 -> {
            String phone = etPhone.getText().toString().trim();
            callUpdateAPI(phone);
        });

        return v;
    }

    // Validation
    private void validate() {
        String phone = etPhone.getText().toString().trim();

        boolean isValid = phone.matches("^\\d+$"); // digits only

        if (!isValid && !phone.isEmpty()) {
            etPhone.setError("Digits only (no spaces or symbols)");
        }

        btnUpdate.setEnabled(isValid && !phone.isEmpty());
    }

    // API Call
    private void callUpdateAPI(String phoneNumber) {


        //String token = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2OWRiOTQyODE1MTMxYmRjYzE2MTEwYzYiLCJpYXQiOjE3NzYwNzAyNzYsImV4cCI6MTc3NjE1NjY3Nn0.cuTyBb7yBZ5W8Q9fJjm7tCZ8hhBQZmKFDbsjNpS4L2s";
        SharedPreferences prefs = getActivity().getSharedPreferences("APP_PREFS", 0);
        String savedToken = prefs.getString("JWT_TOKEN", null);

        if (savedToken == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + savedToken;
        HashMap<String, String> map = new HashMap<>();
        map.put("phoneNumber", phoneNumber);

        retrofitinterface.updateProfile(token, map).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    try {
                        String errorBody = response.errorBody().string();

                        Toast.makeText(getContext(), "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();

                        // Debug
                        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                .setTitle("Update Error " + response.code())
                                .setMessage(errorBody)
                                .setPositiveButton("OK", null)
                                .show();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}