package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.Models.NewsHeadlines;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{
    private final Context context;
    private final List<NewsHeadlines> headlines;

    private final SelectListener listener;

    public NewsAdapter(Context context, List<NewsHeadlines> headlines, SelectListener listener) {
        this.context = context;
        this.headlines = headlines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.text_title.setText(headlines.get(position).getTitle());
        holder.text_source.setText(headlines.get(position).getSource().getName());
        holder.text_time.setText(headlines.get(position).getFormattedPublishedAt());

        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnNewsClicked(headlines.get(position));
                    } else {
                        Log.e("NewsAdapter", "Listener is null");
                    }
                }
            });
        } else {
            Log.e("NewsAdapter", "CardView is null");
        }
    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }
}
