package com.example.smishingdetectionapp.news.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.smishingdetectionapp.news.config.ApiConfig;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for creating and managing the HTTP client and Retrofit instance
 * Provides configured instances for making API calls
 */
public class  ApiClient {
    
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    
    /**
     * Gets the configured Retrofit instance
     * Creates a new instance if one doesn't exist
     * @return Configured Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Gets the configured OkHttpClient instance
     * Creates a new instance if one doesn't exist
     * @return Configured OkHttpClient instance
     */
    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            
            // Create logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Build OkHttpClient with timeouts and interceptors
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    // Add retry on connection failure
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return okHttpClient;
    }
    
    /**
     * Creates a service instance for the given interface
     * @param serviceClass The service interface class
     * @param <T> The service type
     * @return Service instance
     */
    public static <T> T createService(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
    
    /**
     * Clears the cached instances (useful for testing or configuration changes)
     */
    public static void clearInstances() {
        retrofit = null;
        okHttpClient = null;
    }
}
