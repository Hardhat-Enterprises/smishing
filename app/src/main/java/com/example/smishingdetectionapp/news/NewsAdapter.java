package com.example.smishingdetectionapp.news;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{
    private final List<RSSFeedModel.Article> articles;
    private final SelectListener listener;
    private String formattedDescription;

    // Constructor to initialize the adapter with articles and a click listener.
    public NewsAdapter(List<RSSFeedModel.Article> articles, SelectListener listener) {
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
        RSSFeedModel.Article article = articles.get(position);

        // Bind the article data to the ViewHolder's views
        holder.text_title.setText(article.title);
        holder.text_description.setText(article.description);
        // LOG BELOW GIVES THE DESC STRING AS PLAIN
        Log.d("DebugTag1", "Value " + article.description);


        formattedDescription = (article.description);
        // Formats description data to remove HTML tags if they are present. This is specifically setup to format SCAMWATCHs' RSS feed.
        formattedDescription = formattedDescription.replaceAll("\\<.*?\\>", "");
        // Removes whitespace and leftover tags
        formattedDescription = formattedDescription.substring(84, formattedDescription.length() - 14);
        holder.text_description.setText(formattedDescription);

        Log.d("DebugTag2", "Value " + holder.text_description);


        holder.text_pubDate.setText(article.getFormattedDate());

        // Set a click listener on the card view to handle item clicks
        holder.cardView.setOnClickListener(v -> listener.OnNewsClicked(article));
    }


    // Returns the total number of items in the data set held by the adapter, MAX 9.
    public int getItemCount() {
        return Math.min(articles.size(), 9);
    }
}

