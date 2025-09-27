package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.NetworkCapabilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.SearchView;

import com.example.smishingdetectionapp.news.models.NewsArticle;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.news.NewsRequestManager;
import com.example.smishingdetectionapp.news.OnFetchDataListener;
import com.example.smishingdetectionapp.news.SelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class NewsActivity extends SharedActivity implements SelectListener{
    RecyclerView recyclerView;
    NewsAdapter adapter;
    NewsRequestManager manager;
    ProgressBar progressBar;
    TextView errorMessage;
    Button refreshButton;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;
    ChipGroup categoryChipGroup;
    
    // State variables for filtering
    private String currentCategory = null;
    private String currentSearchQuery = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize UI components
        errorMessage = findViewById(R.id.errorTextView);
        recyclerView = findViewById(R.id.news_recycler_view);
        refreshButton = findViewById(R.id.refreshButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.newsSearchView);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);

        // Setup search functionality
        setupSearchView();
        
        // Setup category filtering
        setupCategoryFiltering();
        
        // Setup swipe-to-refresh
        setupSwipeRefresh();

        // Navigation at the bottom of the page designed by Damian
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);
        nav.setOnItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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


        // Initialize ProgressBar and set it visible before fetching data
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Initialize NewsRequestManager and fetch news data using REST API
        manager = new NewsRequestManager(this);
        loadFilteredData();

        // Set up the refresh button click listener
        refreshButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                // Toast.makeText(this, "Connected to Wi-Fi or Mobile Data", Toast.LENGTH_SHORT).show();
                loadFilteredData();
            } else {
                Toast.makeText(this, "You Have Lost Network Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Sets up search functionality
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.trim().isEmpty() ? null : query.trim();
                loadFilteredData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: implement real-time search if desired
                return false;
            }
        });
        
        // Clear search when close button is pressed
        searchView.setOnCloseListener(() -> {
            currentSearchQuery = null;
            loadFilteredData();
            return false;
        });
    }

    /**
     * Sets up category filtering with chips
     */
    private void setupCategoryFiltering() {
        categoryChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = null;
            } else {
                int checkedId = checkedIds.get(0);
                Chip selectedChip = findViewById(checkedId);
                if (selectedChip != null) {
                    String chipText = selectedChip.getText().toString();
                    currentCategory = chipText.equals("All") ? null : mapChipTextToCategory(chipText);
                }
            }
            loadFilteredData();
        });
    }

    /**
     * Maps UI chip text to backend category values
     */
    private String mapChipTextToCategory(String chipText) {
        switch (chipText) {
            case "Cybersecurity": return "cybersecurity";
            case "Data Breach": return "data-breach";
            case "Malware": return "malware";
            case "Phishing": return "phishing";
            default: return null;
        }
    }

    /**
     * Sets up pull-to-refresh functionality
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkConnected()) {
                loadFilteredData();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set refresh colors
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    /**
     * Loads news data with current filters applied
     */
    private void loadFilteredData() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        
        manager = new NewsRequestManager(this);
        
        Log.d("NewsActivity", String.format("Loading with filters - Category: %s, Search: %s", 
                currentCategory, currentSearchQuery));
        
        // Use the advanced filtering method
        manager.fetchNewsAdvanced(
                currentCategory,
                currentSearchQuery,
                null, // tags
                20,   // limit
                1,    // page
                new OnFetchDataListener() {
                    @Override
                    public void onFetchData(List<NewsArticle> articles, String message) {
                        showNews(articles);
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("NewsActivity", "Error fetching filtered news: " + message);
                        errorMessage.setText("Failed to load news: " + message);
                        errorMessage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    private void showNews(List<NewsArticle> articles) {
                        adapter = new NewsAdapter(articles, NewsActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
                        recyclerView.setAdapter(adapter);
                        
                        Log.d("NewsActivity", String.format("Successfully loaded %d articles with filters", articles.size()));
                    }
                }
        );
    }

    // This is for the refresh button
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities != null) {
                // Check for both Wi-Fi and Mobile Data transport capabilities
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }
        return false; // No network connection
    }




    private void loadData() {
        loadFilteredData();
    }

    // Handle news article click events. Opens the article in NewsDetailActivity.
    @Override
    public void OnNewsClicked(NewsArticle article) {
        if (article != null) {
            try {
                Log.d("NewsActivity", "Article clicked: " + article.title);
                Log.d("NewsActivity", "Content available: " + (article.content != null ? "Yes (" + article.content.length() + " chars)" : "No"));
                
                Intent detailIntent = new Intent(this, com.example.smishingdetectionapp.news.NewsDetailActivity.class);
                
                // Pass article data to detail activity
                detailIntent.putExtra("title", article.title);
                detailIntent.putExtra("content", article.content);
                detailIntent.putExtra("author", article.author);
                detailIntent.putExtra("date", article.getFormattedDate());
                detailIntent.putExtra("imageUrl", article.urlToImage);
                
                // Pass source information
                if (article.source != null && article.source.name != null) {
                    detailIntent.putExtra("source", article.source.name);
                }
                
                Log.d("NewsActivity", "Starting NewsDetailActivity with intent extras");
                startActivity(detailIntent);
            } catch (Exception e) {
                Log.e("NewsActivity", "Error opening article detail", e);
                Toast.makeText(this, "Unable to open article", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w("NewsActivity", "Article is null, cannot open detail");
            Toast.makeText(this, "Article not available", Toast.LENGTH_SHORT).show();
        }
    }

}