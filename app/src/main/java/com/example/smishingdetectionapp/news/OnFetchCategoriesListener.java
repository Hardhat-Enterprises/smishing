package com.example.smishingdetectionapp.news;

import java.util.List;

/**
 * Interface for handling category fetch callbacks
 */
public interface OnFetchCategoriesListener {
    /**
     * Called when categories are successfully fetched
     * @param categories List of available categories
     * @param message Success message
     */
    void onFetchCategories(List<String> categories, String message);
    
    /**
     * Called when category fetch fails
     * @param message Error message
     */
    void onError(String message);
}
