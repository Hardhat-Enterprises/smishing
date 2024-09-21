package com.example.smishingdetectionapp;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.sms.SMSExtractor;
import com.example.smishingdetectionapp.sms.OnStatsUpdateListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity implements OnStatsUpdateListener {

    private TextView totalMessagesTextView;
    private TextView smishingMessagesTextView;
    private TextView cpuUsageTextView;
    private TextView memoryUsageTextView;
    private TextView timeSpentTextView;
    private Button refreshButton;
    private Button exportButton;
    private ProgressBar messagesProgressBar;
    private SMSExtractor smsExtractor;
    private long sessionStartTime;
    private long totalTimeSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_statistics);

        smsExtractor = new SMSExtractor(getApplicationContext());
        smsExtractor.setOnStatsUpdateListener(this);

        totalMessagesTextView = findViewById(R.id.totalMessagesTextView);
        smishingMessagesTextView = findViewById(R.id.smishingMessagesTextView);
        cpuUsageTextView = findViewById(R.id.cpuUsageTextView);
        memoryUsageTextView = findViewById(R.id.memoryUsageTextView);
        timeSpentTextView = findViewById(R.id.timeSpentTextView);
        refreshButton = findViewById(R.id.refreshButton);
        exportButton = findViewById(R.id.exportButton);
        messagesProgressBar = findViewById(R.id.messagesProgressBar);
        // Update CPU and Memory Usage when activity is created
        updateCpuAndMemoryUsage();
        refreshButton.setOnClickListener(v -> {
            updateStatistics();
            updateCpuAndMemoryUsage();
        });

        exportButton.setOnClickListener(v -> exportDataToPDF());

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> finish());

        // Track time spent
        sessionStartTime = System.currentTimeMillis();
        updateStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        long sessionEndTime = System.currentTimeMillis();
        totalTimeSpent += (sessionEndTime - sessionStartTime);
        timeSpentTextView.setText("Total Time Spent: " + formatTimeSpent(totalTimeSpent));
    }

    private String formatTimeSpent(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (1000 * 60)) % 60);
        int hours = (int) ((timeInMillis / (1000 * 60 * 60)) % 24);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Retrieve CPU usage
    private long getCpuUsage() {
        String[] stats = null;
        String statFilePath = "/proc/self/stat";
        try (BufferedReader reader = new BufferedReader(new FileReader(statFilePath))) {
            String line = reader.readLine();
            stats = line.split("\\s+");

            long utime = Long.parseLong(stats[13]);
            long stime = Long.parseLong(stats[14]);
            return utime + stime;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    // Retrieve memory usage
    private long getMemoryUsage() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int[] pids = new int[]{android.os.Process.myPid()};
        android.os.Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pids);
        return memoryInfo[0].getTotalPss(); // Returns memory usage in KB
    }
    private void updateCpuAndMemoryUsage() {
        long cpuUsage = getCpuUsage();
        long memoryUsage = getMemoryUsage();  // Fetch memory usage in KB

        TextView cpuUsageTextView = findViewById(R.id.cpuUsageTextView);
        TextView memoryUsageTextView = findViewById(R.id.memoryUsageTextView);

        // Convert CPU usage to a percentage, and memory usage to MB
        cpuUsageTextView.setText(String.format("CPU Usage: %d%%", calculateCpuPercentage(cpuUsage)));
        memoryUsageTextView.setText(String.format("Memory Usage: %d MB", memoryUsage / 1024));
    }

    private int calculateCpuPercentage(long cpuUsage) {
        return (int) (cpuUsage % 100);
    }


    private void updateStatistics() {
        smsExtractor.extractAllMessages();
        smsExtractor.extractSuspiciousMessages();

        float cpuUsage = getCpuUsage();
        cpuUsageTextView.setText(String.format(Locale.getDefault(), "CPU Usage: %.2f%%", cpuUsage));

        long memoryUsage = getMemoryUsage();
        memoryUsageTextView.setText(String.format(Locale.getDefault(), "Memory Usage: %d MB", memoryUsage / (1024 * 1024)));
    }

    // Export data to PDF
    private void exportDataToPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        // Gather data for PDF
        String totalMessages = totalMessagesTextView.getText().toString();
        String smishingMessages = smishingMessagesTextView.getText().toString();
        String cpuUsage = cpuUsageTextView.getText().toString();
        String memoryUsage = memoryUsageTextView.getText().toString();
        String timeSpent = timeSpentTextView.getText().toString();

        // Draw text on the PDF page
        page.getCanvas().drawText("Statistics Report", 10, 25, paint);
        page.getCanvas().drawText(totalMessages, 10, 50, paint);
        page.getCanvas().drawText(smishingMessages, 10, 75, paint);
        page.getCanvas().drawText(cpuUsage, 10, 100, paint);
        page.getCanvas().drawText(memoryUsage, 10, 125, paint);
        page.getCanvas().drawText(timeSpent, 10, 150, paint);

        pdfDocument.finishPage(page);

        // Save the document to storage
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "StatisticsReport.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Log.d("StatisticsActivity", "PDF Exported: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("StatisticsActivity", "Error exporting PDF: ", e);
        } finally {
            pdfDocument.close();
        }
    }

    @Override
    public void onStatsUpdated(int totalMessagesAnalyzed, int smishingMessagesCount) {
        totalMessagesTextView.setText("Total Messages Analyzed: " + totalMessagesAnalyzed);
        smishingMessagesTextView.setText("Smishing Messages Count: " + smishingMessagesCount);
    }
}
