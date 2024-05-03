package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.news.Models.NewsAPIResponse;
import com.example.smishingdetectionapp.news.Models.NewsHeadlines;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.news.NewsRequestManager;
import com.example.smishingdetectionapp.news.OnFetchDataListener;
import com.example.smishingdetectionapp.news.SelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

// NewsActivity class definition, extends AppCompatActivity for fundamental Android UI components and implements the SelectListener interface
public class NewsActivity extends AppCompatActivity implements SelectListener {
    RecyclerView recyclerView; // Holds the recyclerview which displays the news list
    NewsAdapter adapter; // adapter to handle the recyclerview

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news); // Set the content view to our activity_news layout

        BottomNavigationView nav = findViewById(R.id.bottom_navigation); // Initialize the bottom navigation view

        nav.setSelectedItemId(R.id.nav_settings); // Set the selected item in the bottom navigation to "settings"

        // Set a listener for item selection events on the navigation bar
        nav.setOnItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId(); // Get the ID of the selected menu item
            // Handle navigation based on the selected item ID
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else return id == R.id.nav_settings;
        });

        NewsRequestManager manager = new NewsRequestManager(this); // Create an instance of NewsRequestManager
        manager.getNewsHeadlines(listener, "technology", null); // Request news headlines with the "technology" category

    }

    // Listener for fetching data, implemented using the OnFetchDataListener interface
    private final OnFetchDataListener<NewsAPIResponse> listener = new OnFetchDataListener<NewsAPIResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            showNews(list); // Call showNews to update UI with fetched news
        }

        @Override
        public void onError(String message) {
            // Error handling logic could be implemented here
        }
    };

    // Method to initialize and update the RecyclerView with news headlines
    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.news_recycler_view); // Find the RecyclerView by ID
        recyclerView.setHasFixedSize(true); // Set to improve performance since changes in content do not change layout size
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Set layout manager for RecyclerView
        adapter = new NewsAdapter(list, this); // Initialize the adapter with data and context
        recyclerView.setAdapter(adapter); // Set adapter to the RecyclerView
    }

    // Method defined by the SelectListener interface, called when a news item is clicked
    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        Log.d("NewsActivity", "Article clicked"); // Debug log for a clicked article
        if (headlines != null) { // Check if the headlines data is not null
            String url = headlines.getUrl(); // Get the URL from the headlines
            Log.d("NewsActivity", "URL: " + url); // Debug log for the URL
            if (url != null && !url.isEmpty()) { // Check if the URL is not null and not empty
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Log.e("NewsActivity", "Error opening URL", e);
                }
            } else {
                Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("NewsActivity", "Headlines data is null");
        }
    }


}
