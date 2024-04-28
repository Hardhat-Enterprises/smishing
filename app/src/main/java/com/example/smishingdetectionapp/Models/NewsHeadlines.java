package com.example.smishingdetectionapp.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsHeadlines {
    Source source = null;
    String author = "";
    String title = "";
    String url = "";
    String publishedAt = "";

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormattedPublishedAt() {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            Date date = originalFormat.parse(publishedAt);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.US);
            assert date != null;
            return newFormat.format(date);
        } catch (Exception e) {
            return publishedAt;
        }
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

}
