package com.example.smishingdetectionapp.news.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class representing the API response from the /api/news/categories endpoint
 * Contains the list of available news categories
 */
public class CategoriesResponse {
    
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("data")
    public List<String> categories;
    
    @SerializedName("message")
    public String message;
    
    /**
     * Checks if the response contains categories
     * @return true if categories list is not null and not empty
     */
    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }
    
    /**
     * Gets the number of categories
     * @return Number of categories, or 0 if categories is null
     */
    public int getCategoryCount() {
        return categories != null ? categories.size() : 0;
    }
    
    /**
     * Checks if a specific category exists
     * @param category The category to check
     * @return true if the category exists in the list
     */
    public boolean hasCategory(String category) {
        return categories != null && categories.contains(category);
    }
    
    // Default constructor
    public CategoriesResponse() {}
    
    // Constructor for testing
    public CategoriesResponse(boolean success, List<String> categories, String message) {
        this.success = success;
        this.categories = categories;
        this.message = message;
    }
}
