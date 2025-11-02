package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.news.NewsRequestManager;
import com.example.smishingdetectionapp.news.OnFetchDataListener;
import com.example.smishingdetectionapp.news.SavedNewsActivity;
import com.example.smishingdetectionapp.news.SelectListener;
import com.example.smishingdetectionapp.notifications.NotificationType;
import com.example.smishingdetectionapp.ui.BaseOfflineActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NewsActivity extends BaseOfflineActivity implements SelectListener {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsRequestManager manager;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private Button refreshButton, savedNewsButton;

    // Guard to avoid spamming fetches while rapidly toggling connectivity
    private boolean isFetching = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // UI refs
        errorMessage     = findViewById(R.id.errorTextView);
        recyclerView     = findViewById(R.id.news_recycler_view);
        refreshButton    = findViewById(R.id.refreshButton);
        savedNewsButton  = findViewById(R.id.btn_saved_news);
        progressBar      = findViewById(R.id.progressBar);

        // Saved News
        savedNewsButton.setOnClickListener(v ->
                startActivity(new Intent(NewsActivity.this, SavedNewsActivity.class)));

        // Bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(this, CommunityReportActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;

        });

        // RecyclerView + Adapter (init once)
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new NewsAdapter(this, this);
        recyclerView.setAdapter(adapter);

        // Try initial load
        maybeFetchIfNeeded();

        // Refresh button
        refreshButton.setOnClickListener(v -> fetchArticles());

        // Also observe connectivity directly (extra safety)
        ConnectivityMonitor.getIsConnected().observe(this, connected -> {
            if (Boolean.TRUE.equals(connected)) {
                // If we come back online and list is empty, try again
                maybeFetchIfNeeded();
            }
        });
        handleDeepLinkIfAny();
    }

    /**
     * Called by BaseOfflineActivity when we transition from offline -> online.
     * We retry only if nothing is shown yet.
     */
    @Override
    protected void onBackOnline() {
        super.onBackOnline();
        // Auto-refresh when network is back
        fetchArticles();
    }


    /**
     * If user navigates back to News screen and nothing is loaded yet,
     * try fetching again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        maybeFetchIfNeeded();
    }

    /** Retry only when there is nothing in the adapter and we're not fetching already. */
    private void maybeFetchIfNeeded() {
        if (!isFetching && adapter.getItemCount() == 0) {
            fetchArticles();
        }
    }

    /** Fetch RSS feed (idempotent/guarded by isFetching). */
    private void fetchArticles() {
        if (isFetching) return;
        isFetching = true;

        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);

        if (manager == null) {
            manager = new NewsRequestManager(this);
        }

        manager.fetchRSSFeed(new OnFetchDataListener<RSSFeedModel.Feed>() {
            @Override
            public void onFetchData(List<RSSFeedModel.Article> list, String msg) {
                isFetching = false;
                progressBar.setVisibility(View.GONE);

                if (list != null && !list.isEmpty()) {
                    adapter.submitList(list);
                    errorMessage.setVisibility(View.GONE);
                    // Check latest for notification
                    checkAndNotifyLatestNews(list.get(0));
                } else {
                    // No items returned -> show message and keep Refresh visible
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Failed to load news. Please try again.");
                }
            }

            @Override
            public void onError(String message) {
                isFetching = false;
                progressBar.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Failed to load news. Please try again.");
            }
        });
    }

    @Override
    public void OnNewsClicked(RSSFeedModel.Article article) {
        if (article != null && article.link != null && !article.link.isEmpty()) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(browserIntent);
            } catch (Exception e) {
                Log.e("NewsActivity", "Error opening URL", e);
                Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
        }
    }

    /** Hardware back – bounce to Home tab */
    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        super.onBackPressed();
    }

    // ===== Notifications for the newest news item =====
    private void checkAndNotifyLatestNews(RSSFeedModel.Article latestArticle) {
        SharedPreferences prefs = getSharedPreferences("NewsPrefs", MODE_PRIVATE);
        String lastTitle = prefs.getString("last_notified_title", "");

        boolean isNewsNotificationEnabled =
                NotificationType.createNewsAlert(getApplicationContext()).getEnabled();

        if (isNewsNotificationEnabled && latestArticle != null
                && latestArticle.title != null
                && !latestArticle.title.equals(lastTitle)) {

            showNotification("Cyber News Update", latestArticle.title);
            prefs.edit().putString("last_notified_title", latestArticle.title).apply();
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "news_channel_id";
        String channelName = "News Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch =
                    new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.new_logo)
                .setAutoCancel(true)
                .build();

        nm.notify(1, notification);
    }
}
