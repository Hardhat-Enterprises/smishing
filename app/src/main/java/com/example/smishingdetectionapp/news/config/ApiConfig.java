package com.example.smishingdetectionapp.news.config;

/**
 * Configuration class for API settings
 * Contains base URLs, timeouts, and other API-related constants
 */
public class ApiConfig {
    
    // Base URL for the backend API
    // For Android emulator, use 10.0.2.2 to access localhost
    // For real device on same network, replace with your computer's IP address
    public static final String BASE_URL = "http://10.0.2.2:3000/api/";
    
    // Alternative base URL for real devices (update with your IP)
    // public static final String BASE_URL = "http://192.168.1.XXX:3000/api/";
    
    // HTTP timeouts in seconds
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
    
    // API endpoints
    public static final String NEWS_ENDPOINT = "news";
    public static final String CATEGORIES_ENDPOINT = "news/categories";
    public static final String STATS_ENDPOINT = "news/stats";
    
    // Default query parameters
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_BY = "publishedAt";
    public static final String DEFAULT_SORT_ORDER = "desc";
}
