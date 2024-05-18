package com.example.smishingdetectionapp.news;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

// NewsViewHolder class extends RecyclerView.ViewHolder to provide view caching mechanism
public class NewsViewHolder extends RecyclerView.ViewHolder{
    TextView text_title, text_description, text_pubDate; // TextViews to display the title, description, and publish date
    CardView cardView; // CardView to wrap each news item for a better UI experience

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        // Finding and assigning each item
        text_title = itemView.findViewById(R.id.text_title);
        text_description = itemView.findViewById(R.id.text_description);
        text_pubDate = itemView.findViewById(R.id.text_pubDate);
        cardView = itemView.findViewById(R.id.card_view); // Assuming the CardView is the root element in your layout
    }
}

