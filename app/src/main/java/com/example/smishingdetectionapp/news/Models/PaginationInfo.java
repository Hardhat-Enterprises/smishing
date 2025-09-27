package com.example.smishingdetectionapp.news.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing pagination information in API responses
 * Contains page, limit, total, and pages information
 */
public class PaginationInfo {
    
    @SerializedName("page")
    public int page;
    
    @SerializedName("limit") 
    public int limit;
    
    @SerializedName("total")
    public int total;
    
    @SerializedName("pages")
    public int pages;
    
    /**
     * Checks if this is the first page
     * @return true if page is 1
     */
    public boolean isFirstPage() {
        return page == 1;
    }
    
    /**
     * Checks if this is the last page
     * @return true if page equals total pages
     */
    public boolean isLastPage() {
        return page >= pages;
    }
    
    /**
     * Gets the next page number
     * @return next page number, or current page if already at last page
     */
    public int getNextPage() {
        return isLastPage() ? page : page + 1;
    }
    
    /**
     * Gets the previous page number
     * @return previous page number, or 1 if already at first page
     */
    public int getPreviousPage() {
        return isFirstPage() ? 1 : page - 1;
    }
    
    /**
     * Creates a formatted pagination info string
     * @return String like "Page 1 of 5 (100 total items)"
     */
    public String getDisplayInfo() {
        return String.format("Page %d of %d (%d total items)", page, pages, total);
    }
    
    // Default constructor
    public PaginationInfo() {}
    
    // Constructor for testing
    public PaginationInfo(int page, int limit, int total, int pages) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.pages = pages;
    }
}
