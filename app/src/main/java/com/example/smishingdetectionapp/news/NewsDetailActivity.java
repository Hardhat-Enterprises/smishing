package com.example.smishingdetectionapp.news;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.models.NewsArticle;
import com.squareup.picasso.Picasso;

/**
 * Activity for displaying full news article content within the app
 * Replaces the browser redirect with an in-app reading experience
 */
public class NewsDetailActivity extends AppCompatActivity {

    private static final String TAG = "NewsDetailActivity";
    public static final String EXTRA_ARTICLE = "extra_article";
    
    private NewsArticle article;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView sourceTextView;
    private TextView contentTextView;
    private ImageView articleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Get the article data from intent extras
        if (getIntent().hasExtra("title")) {
            Log.d(TAG, "Received intent with article data");
            
            // Get all the data from intent extras
            String title = getIntent().getStringExtra("title");
            String content = getIntent().getStringExtra("content");
            String author = getIntent().getStringExtra("author");
            String date = getIntent().getStringExtra("date");
            String source = getIntent().getStringExtra("source");
            String imageUrl = getIntent().getStringExtra("imageUrl");
            
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Content length: " + (content != null ? content.length() : "null"));
            Log.d(TAG, "Author: " + author);
            
            // Create a basic article object
            article = new NewsArticle();
            article.title = title;
            article.content = content;
            article.author = author;
            article.urlToImage = imageUrl;
            
            initializeViews();
            setupToolbar();
            displayArticle();
        } else {
            Log.e(TAG, "No article data found in intent");
            Toast.makeText(this, "Article not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.article_title);
        authorTextView = findViewById(R.id.article_author);
        dateTextView = findViewById(R.id.article_date);
        sourceTextView = findViewById(R.id.article_source);
        contentTextView = findViewById(R.id.article_content);
        articleImageView = findViewById(R.id.article_image);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("News Article");
        }
    }

    private void displayArticle() {
        if (article == null) return;

        // Set title
        titleTextView.setText(article.title != null ? article.title : "No Title");

        // Set author
        if (article.author != null && !article.author.isEmpty()) {
            authorTextView.setText("By " + article.author);
            authorTextView.setVisibility(android.view.View.VISIBLE);
        } else {
            authorTextView.setVisibility(android.view.View.GONE);
        }

        // Set date
        String dateStr = getIntent().getStringExtra("date");
        if (dateStr != null && !dateStr.isEmpty()) {
            dateTextView.setText(dateStr);
            dateTextView.setVisibility(android.view.View.VISIBLE);
        } else {
            dateTextView.setVisibility(android.view.View.GONE);
        }

        // Set source
        String sourceStr = getIntent().getStringExtra("source");
        if (sourceStr != null && !sourceStr.isEmpty()) {
            sourceTextView.setText("Source: " + sourceStr);
            sourceTextView.setVisibility(android.view.View.VISIBLE);
        } else {
            sourceTextView.setVisibility(android.view.View.GONE);
        }

        // Set content with HTML formatting
        if (article.content != null && !article.content.isEmpty()) {
            // Convert HTML to formatted text
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                contentTextView.setText(Html.fromHtml(article.content, Html.FROM_HTML_MODE_COMPACT));
            } else {
                contentTextView.setText(Html.fromHtml(article.content));
            }
        } else {
            contentTextView.setText("Content not available");
        }

        // Load article image if available
        if (article.urlToImage != null && !article.urlToImage.isEmpty()) {
            Picasso.get()
                    .load(article.urlToImage)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(articleImageView);
            articleImageView.setVisibility(android.view.View.VISIBLE);
        } else {
            articleImageView.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
