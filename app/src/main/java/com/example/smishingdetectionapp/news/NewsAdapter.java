package com.example.smishingdetectionapp.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.news.Models.NewsAPIResponse;
import com.example.smishingdetectionapp.news.Models.NewsHeadlines;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

// Define the NewsAdapter class that extends the RecyclerView.Adapter with a parameterized NewsViewHolder
public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{
    private final List<NewsHeadlines> headlines;

    private final SelectListener listener;

    // Constructor to initialize the NewsAdapter with context, list of headlines, and a listener
    public NewsAdapter(List<NewsHeadlines> headlines, SelectListener listener) {
        // Context to access application-specific resources and classes
        this.headlines = headlines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the individual news item layout and create a new ViewHolder
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false));

    }

    // Method to replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Set text from the news headlines to the TextViews in the holder
        holder.text_title.setText(headlines.get(position).getTitle());
        holder.text_source.setText(headlines.get(position).getSource().getName());
        holder.text_time.setText(headlines.get(position).getFormattedPublishedAt());

        // Set an onClickListener for the card view of each news item
        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(v -> {
                // Notify the listener when a news item is clicked
                if (listener != null) {
                    listener.OnNewsClicked(headlines.get(position));
                } else {
                    Log.e("NewsAdapter", "Listener is null");
                }
            });
        } else {
            Log.e("NewsAdapter", "CardView is null");
        }
    }

    // Method to return the size of the dataset (this is invoked by the layout manager)
    @Override
    public int getItemCount() {
        return headlines.size();
    }

    //NAV STUFF CREATED BY DAMIAN
    public static class NewsActivity extends AppCompatActivity implements SelectListener {
        RecyclerView recyclerView;
        NewsAdapter adapter;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news);

            BottomNavigationView nav = findViewById(R.id.bottom_navigation);

            nav.setSelectedItemId(R.id.nav_settings);

            nav.setOnItemSelectedListener(menuItem -> {

                int id = menuItem.getItemId();
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

            NewsRequestManager manager = new NewsRequestManager(this);
            manager.getNewsHeadlines(listener, "technology", null);

        }

        // Anonymous implementation of the OnFetchDataListener interface that's specifically designed to handle responses for NewsAPIResponse types
        private final OnFetchDataListener<NewsAPIResponse> listener = new OnFetchDataListener<NewsAPIResponse>() {
            @Override
            public void onFetchData(List<NewsHeadlines> list, String message) {
                showNews(list); // Update UI with the list of news headlines
            }

            @Override
            public void onError(String message) {
                // Method called when there is an error in fetching data, empty for now
            }
        };

        // Private method to display news headlines in a RecyclerView this is just like the one in NewsActivity,
        // I cant delete this as it is being called from showsNews
        private void showNews(List<NewsHeadlines> list) {
            recyclerView = findViewById(R.id.news_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new NewsAdapter(list, this);
            recyclerView.setAdapter(adapter);
        }

        // Overridden method from the SelectListener interface to handle news item clicks, Just like NewsActivity
        @Override
        public void OnNewsClicked(NewsHeadlines headlines) {
            Log.d("NewsActivity", "Article clicked");
            if (headlines != null) {
                String url = headlines.getUrl();
                Log.d("NewsActivity", "URL: " + url);
                if (url != null && !url.isEmpty()) {
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
}
