package com.example.smishingdetectionapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder{
    TextView text_title, text_source, text_time;
    CardView cardView;
    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.news_headline);
        text_source = itemView.findViewById(R.id.news_source);
        cardView = itemView.findViewById(R.id.card_view);
        text_time = itemView.findViewById(R.id.news_time);
    }
}
