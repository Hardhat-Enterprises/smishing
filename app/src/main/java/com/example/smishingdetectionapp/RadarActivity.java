package com.example.smishingdetectionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.ui.BaseOfflineActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/** Live Smishing Radar – offline-aware */
public class RadarActivity extends BaseOfflineActivity {

    private TextView radarStatus, tipBanner, lastUpdated;
    private final Handler handler = new Handler();

    // Periodic tasks
    private Runnable radarTick;     // refresh detections + charts
    private Runnable tipTick;       // rotate UX tips
    private boolean radarLoopRunning = false;

    // Data
    private final Map<String, Integer> categoryCounts = new LinkedHashMap<>();
    private final Map<String, List<String>> messageSamples = new HashMap<>();
    private int index = 0;
    private String[] categoryOrder;

    // UI
    private BarChart barChart;
    private PieChart pieChart;
    private Spinner categoryFilter;

    // Colors for categories
    private final Map<String, Integer> categoryColors = new HashMap<>();

    private final String[] tips = new String[]{
            "🚫 Never click on unknown links.",
            "🔒 Enable spam filters in your messaging app.",
            "📵 Ignore suspicious SMS from unknown senders.",
            "🧠 Be cautious of messages asking for personal info."
    };
    private int tipIndex = 0;

    // Timings
    private static final long RADAR_REFRESH_MS = 10_000L;
    private static final long TIP_ROTATE_MS   = 3_000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        radarStatus   = findViewById(R.id.radarStatus);
        tipBanner     = findViewById(R.id.tipBanner);
        lastUpdated   = findViewById(R.id.lastUpdated);
        barChart      = findViewById(R.id.barChart);
        pieChart      = findViewById(R.id.pieChart);
        categoryFilter = findViewById(R.id.region_filter);

        ImageButton backButton = findViewById(R.id.radar_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RadarActivity.this, MainActivity.class));
            finish();
        });

        setupCategoryFilter();
        setupCategoryColors();

        // Tip rotator always runs (offline safe)
        tipTick = this::rotateTipBanner;
        handler.post(tipTick);

        // Radar loop – runs only when online
        radarTick = () -> {
            if (!radarLoopRunning) return; // safety
            loadDetections();
            runCategoryCycle();
            generateCharts();
            handler.postDelayed(radarTick, RADAR_REFRESH_MS);
        };

        // Start immediately if we’re online; BaseOfflineActivity will stop/start appropriately
        startRadarLoopIfNeeded();
    }

    // ---------- BaseOfflineActivity hooks ----------
    @Override
    protected void onWentOffline() {
        stopRadarLoop();
        // Keep current charts visible but annotate state
        if (radarStatus != null) {
            radarStatus.setText("Offline — showing last known activity.");
        }
    }

    @Override
    protected void onBackOnline() {
        startRadarLoopIfNeeded();
    }

    // ---------- Loops control ----------
    private void startRadarLoopIfNeeded() {
        if (radarLoopRunning) return;
        radarLoopRunning = true;
        handler.removeCallbacks(radarTick);
        handler.post(radarTick); // run now
    }

    private void stopRadarLoop() {
        radarLoopRunning = false;
        handler.removeCallbacks(radarTick);
    }

    // ---------- UI setup ----------
    private void setupCategoryFilter() {
        List<String> filters = Arrays.asList("All Categories", "Banking", "Delivery", "Insurance", "Phishing", "Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        categoryFilter.setAdapter(adapter);
    }

    private void setupCategoryColors() {
        categoryColors.put("Phishing",  0xFF03A9F4); // Light Blue
        categoryColors.put("Banking",   0xFF9C27B0); // Purple
        categoryColors.put("Delivery",  0xFF4CAF50); // Green
        categoryColors.put("Other",     0xFFFFC107); // Yellow
        categoryColors.put("Insurance", 0xFFFF5722); // Orange
    }

    // ---------- Data ----------
    private void loadDetections() {
        categoryCounts.clear();
        messageSamples.clear();

        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        Cursor cursor = db.getAllDetections();
        if (cursor != null) {
            try {
                int msgIdx = cursor.getColumnIndexOrThrow("Message");
                while (cursor.moveToNext()) {
                    String message = cursor.getString(msgIdx);
                    String category = categorizeMessageContent(message);
                    categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                    messageSamples.computeIfAbsent(category, k -> new ArrayList<>()).add(message);
                }
            } finally {
                cursor.close();
            }
        }
        db.close();

        categoryOrder = categoryCounts.keySet().toArray(new String[0]);
    }

    private String categorizeMessageContent(String message) {
        if (message == null) return "Other";
        String m = message.toLowerCase(Locale.ROOT);

        if (m.contains("account") || m.contains("bank") || m.contains("login")) return "Banking";
        if (m.contains("parcel")  || m.contains("delivery") || m.contains("courier")) return "Delivery";
        if (m.contains("insurance") || m.contains("medicare") || m.contains("policy")) return "Insurance";
        if (m.contains("click") || m.contains("win") || m.contains("prize") || m.contains("verify")) return "Phishing";
        return "Other";
    }

    // ---------- Rendering ----------
    private void runCategoryCycle() {
        if (categoryCounts.isEmpty()) {
            radarStatus.setText("No smishing activity detected.");
            lastUpdated.setText("");
            return;
        }

        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String category = "All Categories".equals(selectedCategory)
                ? categoryOrder[index]
                : selectedCategory;

        int count = categoryCounts.getOrDefault(category, 0);
        String alertLevel = (count >= 4) ? "🔴 High" : (count >= 2) ? "⚠️ Alert" : "🟢 Low";
        radarStatus.setText(alertLevel + " activity in: " + category + " (" + count + " detections)");

        if (count >= 2) animatePulse(radarStatus);

        String ts = new java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        lastUpdated.setText("Last updated: " + ts);

        if (categoryOrder != null && categoryOrder.length > 0) {
            index = (index + 1) % categoryOrder.length;
        }
    }

    private void generateCharts() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> barColors = new ArrayList<>();
        List<Integer> pieColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int i = 0;
        String selected = (String) categoryFilter.getSelectedItem();

        for (Map.Entry<String, Integer> e : categoryCounts.entrySet()) {
            if (!"All Categories".equals(selected) && !e.getKey().equals(selected)) continue;

            barEntries.add(new BarEntry(i, e.getValue()));
            pieEntries.add(new PieEntry(e.getValue(), e.getKey()));
            labels.add(e.getKey());

            int color = categoryColors.getOrDefault(
                    e.getKey(),
                    ColorTemplate.MATERIAL_COLORS[i % ColorTemplate.MATERIAL_COLORS.length]
            );
            barColors.add(color);
            pieColors.add(color);
            i++;
        }

        // Bar
        BarDataSet barDataSet = new BarDataSet(barEntries, "Scam Detections by Category");
        barDataSet.setColors(barColors);
        barChart.setData(new BarData(barDataSet));
        barChart.setDescription(new Description());
        barChart.invalidate();
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override public void onValueSelected(Entry e, Highlight h) {
                int pos = (int) e.getX();
                if (pos < labels.size()) showMessageDialog(labels.get(pos));
            }
            @Override public void onNothingSelected() {}
        });

        // Pie
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(pieColors);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(48f);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));
        pieChart.setDescription(new Description());
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) showMessageDialog(((PieEntry) e).getLabel());
            }
            @Override public void onNothingSelected() {}
        });
    }

    private void showMessageDialog(String category) {
        List<String> samples = messageSamples.getOrDefault(category, Collections.emptyList());

        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < Math.min(samples.size(), 5); i++) {
            msg.append("• ").append(samples.get(i)).append("\n\n");
        }
        if (msg.length() == 0) msg.append("No samples available.");

        new AlertDialog.Builder(this)
                .setTitle("Sample Messages - " + category)
                .setMessage(msg.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private void animatePulse(View v) {
        AlphaAnimation pulse = new AlphaAnimation(0.5f, 1.0f);
        pulse.setDuration(600);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(5);
        v.startAnimation(pulse);
    }

    // ---------- Tips ----------
    private void rotateTipBanner() {
        if (tipBanner != null) {
            tipBanner.setText(tips[tipIndex]);
            tipIndex = (tipIndex + 1) % tips.length;

            AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
            fade.setDuration(500);
            tipBanner.startAnimation(fade);
        }
        handler.postDelayed(tipTick, TIP_ROTATE_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarLoopRunning = false;
        handler.removeCallbacksAndMessages(null);
    }
}
