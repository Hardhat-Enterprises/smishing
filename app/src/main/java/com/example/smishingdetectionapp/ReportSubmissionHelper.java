package com.example.smishingdetectionapp;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportSubmissionHelper {

    private final Context context;

    public ReportSubmissionHelper(Context context) {
        this.context = context;
    }

    public void detectAndSubmitReport() {
        String detectedMessage = detectSuspiciousMessage();
        if (detectedMessage != null) {
            submitReport(detectedMessage, true);
        }
    }

    private String detectSuspiciousMessage() {
        return "Suspicious message content";
    }

    private void submitReport(String message, boolean isSmishing) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Report report = new Report(1, message, isSmishing, null);

        apiService.submitReport(report).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("ReportSubmissionHelper", "Report submitted: " + response.body().getId());
                } else {
                    Log.d("ReportSubmissionHelper", "Report submission failed");
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                Log.d("ReportSubmissionHelper", "Error: " + t.getMessage());
            }
        });
    }

    public void submitFeedback(String message, boolean isSmishing, String feedback) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Report report = new Report(1, message, isSmishing, feedback);

        apiService.submitFeedback(report).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("ReportSubmissionHelper", "Feedback submitted: " + response.body().getId());
                } else {
                    Log.d("ReportSubmissionHelper", "Feedback submission failed");
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                Log.d("ReportSubmissionHelper", "Error: " + t.getMessage());
            }
        });
    }
}
