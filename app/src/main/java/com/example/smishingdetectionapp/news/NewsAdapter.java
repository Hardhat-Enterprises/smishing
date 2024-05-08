package com.example.smishingdetectionapp.news;

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

    public NewsAdapter(List<RSSFeedModel.Article> articles, SelectListener listener) {
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        RSSFeedModel.Article article = articles.get(position);
        holder.text_title.setText(article.title);
        holder.text_description.setText(article.description);
        holder.text_pubDate.setText(article.getFormattedDate());

        holder.cardView.setOnClickListener(v -> listener.OnNewsClicked(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}

