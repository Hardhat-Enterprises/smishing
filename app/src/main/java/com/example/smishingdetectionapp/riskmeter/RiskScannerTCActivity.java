package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RiskScannerTCActivity extends AppCompatActivity {

    private Switch smsSwitch, ageSwitch, securitySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_scanner_tc);

        BottomNavCoordinator.setup(this, R.id.nav_home);
        // Back button
        ImageButton report_back = findViewById(R.id.riskscannertc_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // ✅ Toggle bindings
        smsSwitch = findViewById(R.id.switch_sms);
        ageSwitch = findViewById(R.id.switch_age);
        securitySwitch = findViewById(R.id.switch_security);

        // ✅ Scan button
        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RiskScannerActivity.class);
            intent.putExtra("DISABLE_SMS_RISK", smsSwitch.isChecked());       // true = disable SMS risk
            intent.putExtra("DISABLE_AGE_RISK", ageSwitch.isChecked());       // true = disable age risk
            intent.putExtra("DISABLE_SECURITY_RISK", securitySwitch.isChecked()); // true = disable security checks
            startActivity(intent);
        });
    }
}
