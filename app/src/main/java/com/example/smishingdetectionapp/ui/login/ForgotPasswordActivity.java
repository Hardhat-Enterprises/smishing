package com.example.smishingdetectionapp.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button sendResetBtn = findViewById(R.id.sendResetLink);
        sendResetBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Reset link sent to your email!", Toast.LENGTH_SHORT).show();
        });
    }
}