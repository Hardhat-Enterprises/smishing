package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.smishingdetectionapp.riskmeter.PulseInjectorKt.injectPulsing;

public class RiskScannerActivity extends AppCompatActivity {

    private ComposeView pulseView;
    private TextView scanningText;
    private View semiCircle;
    private View smishingLogo;
    private ScrollView habitContainer;

    private LinearLayout layoutError;
    private LinearLayout layoutUnsupported;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riskscanner);

        // Main loading state views
        pulseView = findViewById(R.id.pulseComposeView);
        scanningText = findViewById(R.id.scanningText);
        semiCircle = findViewById(R.id.SemiCircle);
        smishingLogo = findViewById(R.id.SmishingLogo);
        habitContainer = findViewById(R.id.habitContainer);

        // Extra UI state layouts
        layoutError = findViewById(R.id.layoutError);
        layoutUnsupported = findViewById(R.id.layoutUnsupported);
        btnRetry = findViewById(R.id.btnRetry);

        injectPulsing(pulseView);
        showLoadingState();

        boolean disableSmsRisk = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        boolean disableAgeRisk = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        boolean disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);

        boolean hasRequiredExtras =
                 getIntent().hasExtra("DISABLE_SMS_RISK") &&
                         getIntent().hasExtra("DISABLE_AGE_RISK") &&
                         getIntent().hasExtra("DISABLE_SECURITY_RISK");

        // Retry button action
        btnRetry.setOnClickListener(v -> {
            showLoadingState();

            new Handler().postDelayed(() -> {
                if (!hasRequiredExtras) {
                    showErrorState();
                } else {
                    Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
                    intent.putExtra("DISABLE_SMS_RISK", disableSmsRisk);
                    intent.putExtra("DISABLE_AGE_RISK", disableAgeRisk);
                    intent.putExtra("DISABLE_SECURITY_RISK", disableSecurityRisk);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        });

        // Main scan flow
        new Handler().postDelayed(() -> {
            if (!hasRequiredExtras) {
                showErrorState();
            } else {
                Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
                intent.putExtra("DISABLE_SMS_RISK", disableSmsRisk);
                intent.putExtra("DISABLE_AGE_RISK", disableAgeRisk);
                intent.putExtra("DISABLE_SECURITY_RISK", disableSecurityRisk);
                startActivity(intent);
                finish();
            }
        }, 3000);

        BottomNavCoordinator.setup(this, R.id.nav_home);


        // back button
        ImageButton report_back = findViewById(R.id.RiskScanner_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });
    }

    private void showLoadingState() {
        semiCircle.setVisibility(View.VISIBLE);
        smishingLogo.setVisibility(View.VISIBLE);
        pulseView.setVisibility(View.VISIBLE);
        scanningText.setVisibility(View.VISIBLE);
        habitContainer.setVisibility(View.VISIBLE);

        layoutError.setVisibility(View.GONE);
        layoutUnsupported.setVisibility(View.GONE);
    }

    private void showErrorState() {
        semiCircle.setVisibility(View.GONE);
        smishingLogo.setVisibility(View.GONE);
        pulseView.setVisibility(View.GONE);
        scanningText.setVisibility(View.GONE);
        habitContainer.setVisibility(View.GONE);

        layoutError.setAlpha(0f);
        layoutError.setVisibility(View.VISIBLE);
        layoutError.animate().alpha(1f).setDuration(300);

        layoutUnsupported.setVisibility(View.GONE);
    }

    private void showUnsupportedState() {
        semiCircle.setVisibility(View.GONE);
        smishingLogo.setVisibility(View.GONE);
        pulseView.setVisibility(View.GONE);
        scanningText.setVisibility(View.GONE);
        habitContainer.setVisibility(View.GONE);

        layoutError.setVisibility(View.GONE);

        layoutUnsupported.setAlpha(0f);
        layoutUnsupported.setVisibility(View.VISIBLE);
        layoutUnsupported.animate().alpha(1f).setDuration(300);
    }
}