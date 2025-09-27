package com.example.smishingdetectionapp.news;

import android.content.Context;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.smishingdetectionapp.news.api.ApiClient;
import com.example.smishingdetectionapp.news.api.NewsApiService;
import com.example.smishingdetectionapp.news.models.NewsApiResponse;
import com.example.smishingdetectionapp.news.models.NewsArticle;
import com.example.smishingdetectionapp.news.models.CategoriesResponse;
import com.example.smishingdetectionapp.news.config.ApiConfig;

import java.util.List;

/**
 * News Request Manager for REST API integration
 * Handles all news-related API calls to the backend server
 * Replaces the original RSS-based implementation
 */
public class NewsRequestManager {
    private static final String TAG = "NewsRequestManager";
    private Context context;
    private NewsApiService apiService;
    
    public NewsRequestManager(Context context) {
        this.context = context;
        this.apiService = ApiClient.createService(NewsApiService.class);
    }
    
    /**
     * Fetches news with default parameters (20 articles, page 1)
     * @param listener Callback for handling response
     */
    public void fetchNews(OnFetchDataListener listener) {
        fetchNews(null, ApiConfig.DEFAULT_PAGE_SIZE, 1, null, listener);
    }
    
    /**
     * Fetches news with pagination
     * @param limit Number of articles per page
     * @param page Page number
     * @param listener Callback for handling response
     */
    public void fetchNews(Integer limit, Integer page, OnFetchDataListener listener) {
        fetchNews(null, limit, page, null, listener);
    }
    
    /**
     * Fetches news with full parameter control
     * @param category Category filter (cybersecurity, data-breach, malware, phishing)
     * @param limit Number of articles per page
     * @param page Page number
     * @param search Search query
     * @param listener Callback for handling response
     */
    public void fetchNews(String category, Integer limit, Integer page, String search, OnFetchDataListener listener) {
        Log.d(TAG, String.format("Fetching news - Category: %s, Limit: %d, Page: %d, Search: %s", 
                category, limit, page, search));
        
        Call<NewsApiResponse> call = apiService.getNews(
                category, 
                limit, 
                page, 
                search, 
                null, // tags
                ApiConfig.DEFAULT_SORT_BY, 
                ApiConfig.DEFAULT_SORT_ORDER
        );
        
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                handleNewsResponse(response, listener);
            }
            
            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                Log.e(TAG, "Network request failed", t);
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Searches news articles
     * @param query Search query
     * @param limit Number of results
     * @param page Page number
     * @param listener Callback for handling response
     */
    public void searchNews(String query, Integer limit, Integer page, OnFetchDataListener listener) {
        Log.d(TAG, String.format("Searching news - Query: %s, Limit: %d, Page: %d", query, limit, page));
        
        Call<NewsApiResponse> call = apiService.searchNews(query, limit, page);
        
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                handleNewsResponse(response, listener);
            }
            
            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                Log.e(TAG, "Search request failed", t);
                listener.onError("Search failed: " + t.getMessage());
            }
        });
    }
    
    /**
     * Fetches news by category
     * @param category Category to filter by
     * @param limit Number of articles
     * @param page Page number
     * @param listener Callback for handling response
     */
    public void fetchNewsByCategory(String category, Integer limit, Integer page, OnFetchDataListener listener) {
        Log.d(TAG, String.format("Fetching news by category - Category: %s, Limit: %d, Page: %d", category, limit, page));
        
        Call<NewsApiResponse> call = apiService.getNewsByCategory(category, limit, page);
        
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                handleNewsResponse(response, listener);
            }
            
            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                Log.e(TAG, "Category request failed", t);
                listener.onError("Failed to fetch category news: " + t.getMessage());
            }
        });
    }
    
    /**
     * Fetches available categories (updated version)
     * @param listener Callback for handling categories response
     */
    public void fetchCategories(OnFetchCategoriesListener listener) {
        Log.d(TAG, "Fetching categories");
        
        Call<CategoriesResponse> call = apiService.getCategories();
        
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriesResponse categoriesResponse = response.body();
                    if (categoriesResponse.success) {
                        Log.d(TAG, "Successfully fetched " + categoriesResponse.categories.size() + " categories");
                        listener.onFetchCategories(categoriesResponse.categories, categoriesResponse.message);
                    } else {
                        Log.w(TAG, "Categories request unsuccessful: " + categoriesResponse.message);
                        listener.onError(categoriesResponse.message != null ? categoriesResponse.message : "Failed to fetch categories");
                    }
                } else {
                    Log.e(TAG, "Categories response error: " + response.code());
                    listener.onError("Server error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.e(TAG, "Categories request failed", t);
                listener.onError("Failed to fetch categories: " + t.getMessage());
            }
        });
    }
    
    /**
     * Handles news API response
     * @param response API response
     * @param listener Callback listener
     */
    private void handleNewsResponse(Response<NewsApiResponse> response, OnFetchDataListener listener) {
        if (response.isSuccessful() && response.body() != null) {
            NewsApiResponse apiResponse = response.body();
            
            if (apiResponse.success && apiResponse.hasArticles()) {
                Log.d(TAG, String.format("Successfully fetched %d articles", apiResponse.getArticleCount()));
                listener.onFetchData(apiResponse.articles, apiResponse.message);
            } else {
                Log.w(TAG, "API response success but no articles found");
                listener.onError(apiResponse.message != null ? apiResponse.message : "No articles found");
            }
        } else {
            String errorMessage = String.format("API request failed with code: %d", response.code());
            Log.e(TAG, errorMessage);
            
            // Provide more specific error messages based on HTTP status codes
            switch (response.code()) {
                case 404:
                    listener.onError("News service not found. Please check your connection.");
                    break;
                case 500:
                    listener.onError("Server error. Please try again later.");
                    break;
                case 400:
                    listener.onError("Invalid request. Please check your search parameters.");
                    break;
                default:
                    listener.onError(errorMessage);
            }
        }
    }
    
    /**
     * Fetches news with advanced filtering options
     * @param category Category filter
     * @param search Search query
     * @param tags Comma-separated tags
     * @param limit Number of articles
     * @param page Page number
     * @param listener Callback for handling response
     */
    public void fetchNewsAdvanced(String category, String search, String tags, Integer limit, Integer page, OnFetchDataListener listener) {
        Log.d(TAG, String.format("Advanced fetch - Category: %s, Search: %s, Tags: %s, Limit: %d, Page: %d", 
                category, search, tags, limit, page));
        
        Call<NewsApiResponse> call = apiService.getNews(category, limit, page, search, tags, "publishedAt", "desc");
        
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                handleNewsResponse(response, listener);
            }
            
            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                Log.e(TAG, "Advanced fetch failed", t);
                listener.onError("Failed to fetch news: " + t.getMessage());
            }
        });
    }
    
    /**
     * Manually trigger news refresh from external sources
     * @param listener Callback for handling response
     */
    public void refreshNews(OnFetchDataListener listener) {
        Log.d(TAG, "Triggering manual news refresh");
        
        Call<NewsApiResponse> call = apiService.fetchNews();
        
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsApiResponse refreshResponse = response.body();
                    if (refreshResponse.success) {
                        Log.d(TAG, "News refresh successful: " + refreshResponse.message);
                        // After refresh, fetch the latest news
                        fetchNews(listener);
                    } else {
                        Log.w(TAG, "News refresh unsuccessful: " + refreshResponse.message);
                        listener.onError(refreshResponse.message != null ? refreshResponse.message : "Failed to refresh news");
                    }
                } else {
                    Log.e(TAG, "Refresh response error: " + response.code());
                    listener.onError("Server error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                Log.e(TAG, "News refresh failed", t);
                listener.onError("Failed to refresh news: " + t.getMessage());
            }
        });
    }
    
    /**
     * Interface for categories fetch callback
     */
    public interface OnFetchCategoriesListener {
        void onFetchCategories(List<String> categories, String message);
        void onError(String message);
    }
}
