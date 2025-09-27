package com.example.smishingdetectionapp.news.models;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Model class representing a news article from the backend API
 * Maps to the JSON structure returned by the /api/news endpoint
 */
public class NewsArticle {
    
    @SerializedName("_id")
    public String id;
    
    @SerializedName("title")
    public String title;
    
    @SerializedName("description") 
    public String description;
    
    @SerializedName("content")
    public String content;
    
    @SerializedName("url")
    public String url;
    
    @SerializedName("urlToImage")
    public String urlToImage;
    
    @SerializedName("publishedAt")
    public Date publishedAt;
    
    @SerializedName("source")
    public Source source;
    
    @SerializedName("author")
    public String author;
    
    @SerializedName("category")
    public String category;
    
    @SerializedName("tags")
    public List<String> tags;
    
    @SerializedName("isActive")
    public boolean isActive;
    
    @SerializedName("fetchedAt")
    public Date fetchedAt;
    
    /**
     * Formats the published date for display
     * @return Formatted date string
     */
    public String getFormattedDate() {
        if (publishedAt == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(publishedAt);
    }
    
    /**
     * Gets a clean description without HTML tags
     * @return Clean description text
     */
    public String getCleanDescription() {
        if (description == null) {
            return "";
        }
        
        // Remove HTML tags
        String cleaned = description.replaceAll("<.*?>", "");
        
        // Remove extra whitespace
        cleaned = cleaned.trim().replaceAll("\\s+", " ");
        
        // Limit length for display
        if (cleaned.length() > 150) {
            cleaned = cleaned.substring(0, 147) + "...";
        }
        
        return cleaned;
    }
    
    /**
     * Checks if the article has an image
     * @return true if urlToImage is not null and not empty
     */
    public boolean hasImage() {
        return urlToImage != null && !urlToImage.trim().isEmpty();
    }
    
    /**
     * Gets the source name, with fallback
     * @return Source name or "Unknown" if not available
     */
    public String getSourceName() {
        if (source != null && source.name != null) {
            return source.name;
        }
        return "Unknown Source";
    }
    
    /**
     * Nested class representing the article source
     */
    public static class Source {
        @SerializedName("name")
        public String name;
        
        @SerializedName("id") 
        public String id;
    }
    
    // Default constructor
    public NewsArticle() {}
    
    // Constructor for testing
    public NewsArticle(String title, String description, String url, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }
}
