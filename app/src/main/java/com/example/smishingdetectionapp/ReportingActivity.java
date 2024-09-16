package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class ReportingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        final EditText phonenumber = findViewById(R.id.PhoneNumber);
        final EditText message = findViewById(R.id.reportmessage);
        final Button sendReportButton = findViewById(R.id.reportButton);

        //DATABASE REPORT FUNCTION
        sendReportButton.setOnClickListener(v -> {
            boolean isInserted = DatabaseAccess.sendReport(Integer.parseInt(phonenumber.getText().toString()),
                    message.getText().toString());
            if (isInserted){
                phonenumber.setText(null);
                message.setText(null);
                Toast.makeText(getApplicationContext(), "Report sent!", Toast.LENGTH_LONG).show();}
            else
                Toast.makeText(getApplicationContext(), "Report could not be sent!", Toast.LENGTH_LONG).show();
        });

        //For enabling the report button when both text fields are filled in.
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String userPhone = phonenumber.getText().toString();
                String userMessage = message.getText().toString();
                sendReportButton.setEnabled(!userPhone.isEmpty() && !userMessage.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        phonenumber.addTextChangedListener(afterTextChangedListener);
        message.addTextChangedListener(afterTextChangedListener);
    }
}