package com.example.smishingdetectionapp.news.api;

import com.example.smishingdetectionapp.news.models.NewsApiResponse;
import com.example.smishingdetectionapp.news.models.CategoriesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit service interface defining API endpoints for news-related operations
 * Contains methods for fetching news, categories, and other news-related data
 */
public interface NewsApiService {
    
    /**
     * Gets news articles with optional filtering parameters
     * @param category Filter by category (cybersecurity, data-breach, malware, phishing)
     * @param limit Number of articles per page (default: 20, max: 100)
     * @param page Page number for pagination (default: 1)
     * @param search Search query for title and description
     * @param tags Comma-separated list of tags to filter by
     * @param sortBy Field to sort by (default: publishedAt)
     * @param sortOrder Sort order: asc or desc (default: desc)
     * @return Call object for the API request
     */
    @GET("news")
    Call<NewsApiResponse> getNews(
            @Query("category") String category,
            @Query("limit") Integer limit,
            @Query("page") Integer page,
            @Query("search") String search,
            @Query("tags") String tags,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );
    
    /**
     * Gets news articles with minimal parameters (uses defaults for others)
     * @param limit Number of articles to fetch
     * @param page Page number
     * @return Call object for the API request
     */
    @GET("news")
    Call<NewsApiResponse> getNews(
            @Query("limit") Integer limit,
            @Query("page") Integer page
    );
    
    /**
     * Gets news articles with default parameters (20 articles, page 1)
     * @return Call object for the API request
     */
    @GET("news")
    Call<NewsApiResponse> getNews();
    
    /**
     * Gets available news categories
     * @return Call object for the categories request
     */
    @GET("news/categories")
    Call<CategoriesResponse> getCategories();
    
    /**
     * Searches news articles by query
     * @param query Search query
     * @param limit Number of results
     * @param page Page number
     * @return Call object for the search request
     */
    @GET("news")
    Call<NewsApiResponse> searchNews(
            @Query("search") String query,
            @Query("limit") Integer limit,
            @Query("page") Integer page
    );
    
    /**
     * Gets news articles by category
     * @param category Category to filter by
     * @param limit Number of articles
     * @param page Page number
     * @return Call object for the category request
     */
    @GET("news")
    Call<NewsApiResponse> getNewsByCategory(
            @Query("category") String category,
            @Query("limit") Integer limit,
            @Query("page") Integer page
    );

    /**
     * Manually trigger news fetch from external sources
     * @return Call object for refresh request
     */
    @POST("news/fetch")
    Call<NewsApiResponse> fetchNews();

}
