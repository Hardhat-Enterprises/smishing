package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SmishingReportActivity extends AppCompatActivity {


    private SmishingReportService smishingReportService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smishing_report);


        smishingReportService = new SmishingReportService();

        //#UI

        //EditText messageInput = findViewById(R.id.message_input);
        //Button reportButton = findViewById(R.id.report_button);

        //reportButton.setOnClickListener(v -> {
        //    String message = messageInput.getText().toString();
        //    boolean isFalsePositive = false;
        //    boolean isFalseNegative = false;

            // message report
            //smishingReportService.reportSmishing(message, isFalsePositive, isFalseNegative);

            // Output to log to check results
            //System.out.println("Reported Messages: " + smishingReportService.getAllReportedMessages());
        //});
    }
}
