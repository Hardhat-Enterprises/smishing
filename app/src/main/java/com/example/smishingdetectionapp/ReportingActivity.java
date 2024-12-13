package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class ReportingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);

        // Reference UI elements
        final EditText phoneNumber = findViewById(R.id.PhoneNumber);
        final EditText message = findViewById(R.id.reportmessage);
        final Button sendReportButton = findViewById(R.id.reportButton);

        // Set onClickListener for the report button
        sendReportButton.setOnClickListener(v -> {
            try {
                // Retrieve user input
                String phoneNumString = phoneNumber.getText().toString();
                String reportMessage = message.getText().toString();

                // Validate input
                if (phoneNumString.isEmpty() || reportMessage.isEmpty()) {
                    Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int phoneNum;
                try {
                    phoneNum = Integer.parseInt(phoneNumString); // Validate phone number format
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Open database and attempt to insert the report
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                boolean isInserted = databaseAccess.sendReport(phoneNum, reportMessage);

                if (isInserted) {
                    phoneNumber.setText(null); // Clear input fields
                    message.setText(null);
                    Toast.makeText(this, "Report sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Report could not be sent!", Toast.LENGTH_LONG).show();
                }

                databaseAccess.close();
            } catch (Exception e) {
                Log.e("ReportingActivity", "Error sending report: " + e.getMessage(), e);
                Toast.makeText(this, "An error occurred while sending the report!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
