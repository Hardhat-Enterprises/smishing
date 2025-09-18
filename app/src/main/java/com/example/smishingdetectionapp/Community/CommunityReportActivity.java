package com.example.smishingdetectionapp.Community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.model.ReportRequest;
import com.example.smishingdetectionapp.model.ReportResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class CommunityReportActivity extends AppCompatActivity {

    // Inputs
    private EditText etPhone, etMessage;
    private Button btnReport;

    // Result UI
    private CardView resultCard;
    private TextView tvBadge, tvConfidence, tvDetails;
    private ProgressBar progress;

    // API
    private SmishingService service;

    // Retrofit API (inner interface to keep things simple)
    private interface SmishingService {
        @POST("api/reports")
        Call<ReportResponse> submitReport(@Body ReportRequest body);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_report);

        // ----- find views -----
        etPhone = findViewById(R.id.etPhoneNumber);
        etMessage = findViewById(R.id.etMessageContent);
        btnReport = findViewById(R.id.btnReportProtect);

        resultCard = findViewById(R.id.resultCard);
        tvBadge = findViewById(R.id.tvBadge);
        tvConfidence = findViewById(R.id.tvConfidence);
        tvDetails = findViewById(R.id.tvDetails);
        progress = findViewById(R.id.progress);

        // Hide result on first load
        if (resultCard != null) resultCard.setVisibility(View.GONE);
        if (progress != null) progress.setVisibility(View.GONE);

        // ----- tab layout -----
        final String source = (getIntent().getStringExtra("source") == null)
                ? "home"
                : getIntent().getStringExtra("source");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Trending"));
            tabLayout.addTab(tabLayout.newTab().setText("Posts"));
            tabLayout.addTab(tabLayout.newTab().setText("Report"));
            if (tabLayout.getTabCount() >= 3) tabLayout.getTabAt(2).select();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override public void onTabSelected(TabLayout.Tab tab) {
                    int pos = tab.getPosition();
                    if (pos == 0) {
                        Intent i = new Intent(CommunityReportActivity.this, CommunityHomeActivity.class);
                        i.putExtra("source", source);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        finish();
                    } else if (pos == 1) {
                        Intent i = new Intent(CommunityReportActivity.this, CommunityPostActivity.class);
                        i.putExtra("source", source);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        finish();
                    }
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) { }
                @Override public void onTabReselected(TabLayout.Tab tab) { }
            });
        }

        // ----- back button -----
        ImageButton community_back = findViewById(R.id.community_back);
        if (community_back != null) {
            community_back.setOnClickListener(view ->  {
                if ("settings".equals(source)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                overridePendingTransition(0,0);
                finish();
            });
        } else {
            Log.e("CommunityReport", "Back button is null");
        }

        // ----- bottom nav -----
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_report);
            nav.setOnItemSelectedListener(item -> {
                Intent intent;
                int id = item.getItemId();
                if      (id == R.id.nav_home)     intent = new Intent(this, MainActivity.class);
                else if (id == R.id.nav_news)     intent = new Intent(this, NewsActivity.class);
                else if (id == R.id.nav_settings) intent = new Intent(this, SettingsActivity.class);
                else return false;
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return true;
            });
        }

        // ----- Retrofit (with timeouts) -----
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVERIP + "/") // http://10.0.2.2:3000/
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(SmishingService.class);

        // ----- Button: call backend and render card -----
        btnReport.setOnClickListener(v -> {
            String phone = safeText(etPhone);
            String msg   = safeText(etMessage);

            // Only message is required; phone is optional
            if (msg.isEmpty()) {
                etMessage.setError("Message is required");
                etMessage.requestFocus();
                return;
            }

            // Hide keyboard & clear focus for a cleaner UX
            try {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (Exception ignored) {}
            etPhone.clearFocus();
            etMessage.clearFocus();

            toggleLoading(true);

            // If phone is blank, send null so Gson omits the field
            ReportRequest body = new ReportRequest(phone.isEmpty() ? null : phone, msg);

            service.submitReport(body).enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                    toggleLoading(false);
                    if (!response.isSuccessful() || response.body() == null) {
                        String err = "Unexpected server response";
                        try {
                            ResponseBody eb = response.errorBody();
                            if (eb != null) {
                                err = "HTTP " + response.code() + ": " + eb.string();
                                eb.close();
                            }
                        } catch (Exception ignored) {}
                        showError(err);
                        return;
                    }
                    bindResult(response.body());
                    etPhone.setText("");
                    etMessage.setText("");
                }

                @Override
                public void onFailure(Call<ReportResponse> call, Throwable t) {
                    toggleLoading(false);
                    showError("Network error: " + t.getMessage());
                }
            });
        });
    }

    // ===== helpers =====
    private String safeText(EditText et) {
        return (et != null && et.getText() != null) ? et.getText().toString().trim() : "";
    }

    private void toggleLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (btnReport != null) btnReport.setEnabled(!loading);
        if (loading && resultCard != null) resultCard.setVisibility(View.GONE);
    }

    private void bindResult(ReportResponse res) {
        // Prefer server-sent badge; fall back to normalized label
        String label = (res.getBadge() != null && !res.getBadge().trim().isEmpty())
                ? res.getBadge()
                : normalizeLabel(res.getLabel() == null ? "" : res.getLabel());

        // Percent comes from your updated model (0..100 already)
        Integer percent = res.getPercent();
        int pctRounded = (percent == null) ? 0 : Math.max(0, Math.min(100, percent));

        String details = res.getDetails() != null ? res.getDetails() : "";

        if (tvBadge != null)      tvBadge.setText(label.toUpperCase());
        if (tvConfidence != null) tvConfidence.setText("Confidence: " + pctRounded + "%");

        if (tvDetails != null) {
            if (details.isEmpty()) {
                tvDetails.setVisibility(View.GONE);
            } else {
                tvDetails.setVisibility(View.VISIBLE);
                tvDetails.setText(details);
            }
        }

        int color;
        switch (label.toUpperCase()) {
            case "SAFE":     color = getColorCompat(R.color.badge_green);  break;
            case "SPAM":     color = getColorCompat(R.color.badge_orange); break;
            case "SMISHING": color = getColorCompat(R.color.badge_red);    break;
            default:         color = getColorCompat(R.color.badge_orange);
        }

        if (tvBadge != null && tvBadge.getBackground() != null) {
            tvBadge.getBackground().setTint(color);
        }

        if (resultCard != null) resultCard.setVisibility(View.VISIBLE);
    }

    // Map common synonyms to the three display buckets
    private String normalizeLabel(String raw) {
        String s = raw.trim().toUpperCase();
        if (s.equals("HAM") || s.equals("LEGIT") || s.equals("SAFE")) return "SAFE";
        if (s.equals("SPAM") || s.equals("AD") || s.equals("PROMO"))  return "SPAM";
        if (s.equals("PHISHING") || s.equals("SMISHING") || s.equals("SCAM") || s.equals("FRAUD"))
            return "SMISHING";
        // Generic statuses — show neutral (orange)
        if (s.equals("CLASSIFIED") || s.equals("OK") || s.equals("SUCCESS")) return "SPAM";
        return s.isEmpty() ? "SPAM" : s;
    }

    private void showError(String msg) {
        if (resultCard != null) resultCard.setVisibility(View.VISIBLE);
        if (tvBadge != null) {
            tvBadge.setText("ERROR");
            if (tvBadge.getBackground() != null) {
                tvBadge.getBackground().setTint(getColorCompat(R.color.badge_red));
            }
        }
        if (tvConfidence != null) tvConfidence.setText("");
        if (tvDetails != null) {
            tvDetails.setVisibility(View.VISIBLE);
            tvDetails.setText(msg);
        }
    }

    private int getColorCompat(int id) {
        return ContextCompat.getColor(this, id);
    }
}
