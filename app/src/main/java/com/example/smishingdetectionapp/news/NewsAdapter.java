package com.example.smishingdetectionapp.news;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.models.NewsArticle;

import java.util.List;

/**
 * RecyclerView adapter for displaying news articles from REST API
 * Updated to work with NewsArticle model instead of RSS model
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{
    private final List<NewsArticle> articles;
    private final SelectListener listener;

    // Constructor to initialize the adapter with articles and a click listener.
    public NewsAdapter(List<NewsArticle> articles, SelectListener listener) {
        this.articles = articles;
        this.listener = listener;
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false));
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Get the article for the current position
        NewsArticle article = articles.get(position);

        // Bind the article data to the ViewHolder's views
        holder.text_title.setText(article.title != null ? article.title : "No Title");
        
        // Use the cleaned description from our model
        String cleanDescription = article.getCleanDescription();
        holder.text_description.setText(cleanDescription);
        
        // Set the formatted date
        holder.text_pubDate.setText(article.getFormattedDate());

        // Set a click listener on the card view to handle item clicks
        holder.cardView.setOnClickListener(v -> listener.OnNewsClicked(article));
        
        // Log for debugging
        Log.d("NewsAdapter", String.format("Binding article: %s", article.title));
    }

    // Returns the total number of items in the data set - NO MORE 9 ARTICLE LIMIT!
    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }
    
    /**
     * Updates the articles list and refreshes the adapter
     * @param newArticles New list of articles
     */
    public void updateArticles(List<NewsArticle> newArticles) {
        this.articles.clear();
        this.articles.addAll(newArticles);
        notifyDataSetChanged();
    }
}

