package com.example.smishingdetectionapp.navigation;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class BottomNavCoordinator {

    private static final String DEFAULT_REPORT_SOURCE = "home";

    private BottomNavCoordinator() {
        // Utility class.
    }

    public static void setup(Activity activity, @IdRes int selectedItemId) {
        setup(activity, selectedItemId, DEFAULT_REPORT_SOURCE);
    }

    public static void setup(Activity activity, @IdRes int selectedItemId, @Nullable String reportSource) {
        BottomNavigationView nav = activity.findViewById(R.id.bottom_navigation);
        if (nav == null) return;

        nav.setSelectedItemId(selectedItemId);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) return true;

            Intent intent = createDestinationIntent(activity, itemId, reportSource);
            if (intent == null) return false;

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
            activity.finish();
            return true;
        });
    }

    @Nullable
    private static Intent createDestinationIntent(Activity activity, int destinationId, @Nullable String reportSource) {
        if (destinationId == R.id.nav_home) return new Intent(activity, MainActivity.class);

        if (destinationId == R.id.nav_report) {
            Intent intent = new Intent(activity, CommunityReportActivity.class);
            intent.putExtra("source", reportSource == null ? DEFAULT_REPORT_SOURCE : reportSource);
            return intent;
        }

        if (destinationId == R.id.nav_news) return new Intent(activity, NewsActivity.class);

        if (destinationId == R.id.nav_settings) {
            Intent intent = new Intent(activity, SettingsActivity.class);
            intent.putExtra("from_navigation", true);
            return intent;
        }

        return null;
    }
}

