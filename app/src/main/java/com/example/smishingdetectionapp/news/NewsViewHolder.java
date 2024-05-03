package com.example.smishingdetectionapp.news;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

// NewsViewHolder class extends RecyclerView.ViewHolder to provide view caching mechanism
public class NewsViewHolder extends RecyclerView.ViewHolder{
    TextView text_title, text_source, text_time; // TextViews to display the title, source, and time of news articles
    CardView cardView; // CardView to wrap each news item for a better UI experience
    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        // Finding and assigning each item
        text_title = itemView.findViewById(R.id.news_headline);
        text_source = itemView.findViewById(R.id.news_source);
        cardView = itemView.findViewById(R.id.card_view);
        text_time = itemView.findViewById(R.id.news_time);
    }
}
