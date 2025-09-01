package com.example.smishingdetectionapp.riskmeter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.text.Layout;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class RiskResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percentageText;
    TextView riskLevelText;

    //these are the circle indicators next to each digital habit that will change colour based on risk level
    View lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter, lightDeviceLock, lightUnknownSources, lightSmsBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_result);

        progressBar = findViewById(R.id.circularProgressBar);
        percentageText = findViewById(R.id.percentageText);
        riskLevelText = findViewById(R.id.riskLevelText);

        lightAgeGroup = findViewById(R.id.light_age_group);
        lightSmsApp = findViewById(R.id.light_sms_app);
        lightSecurityApp = findViewById(R.id.light_security_app);
        lightSpamFilter = findViewById(R.id.light_spam_filter);
        lightDeviceLock = findViewById(R.id.light_device_lock);
        lightUnknownSources = findViewById(R.id.light_unknown_sources);
        lightSmsBehaviour = findViewById(R.id.light_sms_behaviour);

        boolean disableSmsRisk = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        boolean disableAgeRisk = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        boolean disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);


        //our logic scan and updates to progress bar, texts and lights
        RiskScannerLogic.scanHabits(this, progressBar, riskLevelText,
                lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter,
                lightDeviceLock, lightUnknownSources, lightSmsBehaviour, disableSmsRisk,
                disableAgeRisk, disableSecurityRisk);

        // this is for view anlysis text to make it a clickable text view
        TextView viewAnalysisText = findViewById(R.id.textViewRiskDetails);
        viewAnalysisText.setOnClickListener(v -> {
            int score = RiskScannerLogic.getCalculatedScore();
            showRiskAnalysisDialog(score);
        });

        // navigation bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0,0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // this is the back button to go to home page
        ImageButton report_back = findViewById(R.id.RiskScannerResult_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        Button shareRiskButton = findViewById(R.id.shareRiskButton);
        shareRiskButton.setOnClickListener(v -> {
            // Prefer the logic’s canonical score, fall back to parsing the TextView.
            int score = RiskScannerLogic.getCalculatedScore();
            if (score <= 0) {
                // Fallback: parse "71%" from percentageText
                try {
                    String pct = percentageText.getText().toString().replace("%", "").trim();
                    score = Integer.parseInt(pct);
                } catch (Exception ignored) {
                    score = -1;
                }
            }

            String appName = getString(R.string.app_name);
            String msg;
            if (score >= 0) {
                msg = "My smishing risk score is " + score + "%. Try it yourself!";
            } else {
                // Extra-safe fallback if score unavailable
                msg = "Check your smishing risk score with " + appName + "! Try it yourself!";
            }

            // include a link (Play Store style link using your package name)
            String link = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String payload = msg + "\n" + link;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName + " – Smishing Risk");
            shareIntent.putExtra(Intent.EXTRA_TEXT, payload);

            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
        ImageButton btnShareStory = findViewById(R.id.btnShareStory);
        btnShareStory.setOnClickListener(v -> shareStoryFlow());


    }

    // this is the animation for the progress bar to make the percentage and fill move
    public void animateProgress(ProgressBar progressBar, TextView percentageText, int score) {
        progressBar.setMax(100);

        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, score);
        animator.setDuration(1500);
        animator.start();

        ValueAnimator textAnimator = ValueAnimator.ofInt(0, score);
        textAnimator.setDuration(1500);
        textAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            percentageText.setText(animatedValue + "%");
        });
        textAnimator.start();
    }

    // this is most of the java for when you click the view risk analysis and then get the bottom sheet dialog
    public void showRiskAnalysisDialog(int score) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_risk_analysis, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView riskTitle = bottomSheetView.findViewById(R.id.textRiskTitle);
        TextView riskMessage = bottomSheetView.findViewById(R.id.textRiskMessage);
        Button closeBtn = bottomSheetView.findViewById(R.id.btnCloseDialog);

        List<String> risks = RiskScannerLogic.getTriggeredRisks();
        SpannableStringBuilder message = new SpannableStringBuilder();

        // the blue message saying what the risk level is
        String header;
        if (score <= 30) {
            header = "Your digital habits suggest a Low Risk of being susceptible to smishing attacks.\n\n";
        } else if (score <= 60) {
            header = "Your digital habits suggest a Moderate Risk of being susceptible to smishing attacks.\n\n";
        } else {
            header = "Your digital habits suggest a High Risk of being susceptible to smishing attacks.\n\n";
        }

        // adding styling to detected issues and the text for it
        SpannableString riskHeader = new SpannableString(header);
        riskHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, riskHeader.length(), 0);
        riskHeader.setSpan(new ForegroundColorSpan(android.graphics.Color.parseColor("#4B74F0")), 0, riskHeader.length(), 0);
        riskHeader.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, riskHeader.length(), 0);
        message.append(riskHeader);

        if (!risks.isEmpty()) {
            SpannableString issuesHeader = new SpannableString("\n Detected Issues\n\n");
            issuesHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, issuesHeader.length(), 0);
            message.append(issuesHeader);

            for (String risk : risks) {
                message.append("• ").append(risk).append("\n");
            }
        } else {
            message.append("\n Woohoo, no major risks detected!\n");
        }

        // adding styling to recommendations and recommendations
        SpannableString recHeader = new SpannableString("\n Recommendations\n\n");
        recHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, recHeader.length(), 0);
        message.append(recHeader);
        if (score > 30) {
            message.append("• Set up a strong password or PIN to protect your data and reduce the risk of unauthorided access.\n");
            message.append("• Install antivirus or security apps to detect and block suspicious activity or attacks.\n");
            message.append("• Disable installations from unknown sources to prevent the risk of malicious apps being installed.\n");
            message.append("• Enable a spam filter to help automatically detect and block smishing messages.\n");
            message.append("• Avoid clicking on links in SMS messages from unknown senders to reduce exposure to malicious websites.\n");
            message.append("• Report suspicious SMS messages to your mobile provider to help block further threats.\n");
            message.append("• Take a short cybersecurity awareness course to learn how to identify and avoid common smishing techniques.\n");
        } else {
            message.append("• Keep up your excellent security habits! You're doing great.\n");
        }

        riskMessage.setText(message);
        closeBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }
    private void shareStoryFlow() {
        Bitmap bmp = captureViewBitmap(findViewById(android.R.id.content));
        if (bmp == null) return;

        File img = saveTempPng(bmp, "risk_result_story.png");
        if (img == null) return;

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                img
        );

        if (!shareToInstagramStories(uri)) {
            if (!shareToFacebookStories(uri)) {
                shareGenericImage(uri, "Here’s my smishing risk score from Smishing Detection!");
            }
        }
    }

    private Bitmap captureViewBitmap(View view) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(
                    view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File saveTempPng(Bitmap bmp, String name) {
        try {
            File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, name);
            try (FileOutputStream out = new FileOutputStream(f)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean shareToInstagramStories(Uri imageUri) {
        try {
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(imageUri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("content_url",
                    "https://play.google.com/store/apps/details?id=" + getPackageName());

            if (getPackageManager().resolveActivity(intent, 0) != null) {
                grantUriPermission("com.instagram.android", imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean shareToFacebookStories(Uri imageUri) {
        try {
            Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
            intent.setType("image/*");
            intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", getPackageName());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("background_asset_uri", imageUri);

            if (getPackageManager().resolveActivity(intent, 0) != null) {
                grantUriPermission("com.facebook.katana", imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void shareGenericImage(Uri imageUri, String message) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("image/*");
        send.putExtra(Intent.EXTRA_STREAM, imageUri);
        send.putExtra(Intent.EXTRA_TEXT, message + "\n" +
                "https://play.google.com/store/apps/details?id=" + getPackageName());
        send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(send, "Share your risk score"));
    }

}
