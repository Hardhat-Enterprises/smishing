package com.example.smishingdetectionapp.news.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// NewsHeadlines class to model the structure of a news headline in a news API response
public class NewsHeadlines {
    Source source = null; // Variable to hold the source of the news article
    String author = ""; // Variable to store the author of the news article
    String title = ""; // Variable to store the title of the news article
    String url = ""; // Variable to store the url of the news article
    String publishedAt = ""; // Variable to store the published date of the news article

    // Similar to NewsAPIResponse, Getter method to return the source of the news article
    public Source getSource() {
        return source;
    }

    // Setter method to set the source of the news article
    public void setSource(Source source) {
        this.source = source;
    }

    // Getter method to return the author of the news article
    public String getAuthor() {
        return author;
    }

    // Setter method to set the author of the news article
    public void setAuthor(String author) {
        this.author = author;
    }

    // Getter method to return the title of the news article
    public String getTitle() {
        return title;
    }

    // Setter method to set the title of the news article
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter method to return the URL of the news article
    public String getUrl() {
        return url;
    }

    // Setter method to set the URL of the news article
    public void setUrl(String url) {
        this.url = url;
    }

    // Method to format the published date from ISO format to a more readable format
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

    // Setter method to set the published date and time of the news article
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

}
