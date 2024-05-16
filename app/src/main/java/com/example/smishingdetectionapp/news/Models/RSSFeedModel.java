package com.example.smishingdetectionapp.news.Models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.http.GET;

public class RSSFeedModel {

    // Root class representing the RSS feed.
    @Root(name = "rss", strict = false)
    public static class Feed {
        @Element(name = "channel")
        public Channel channel;
    }

    // Class representing the channel element in the RSS feed.
    @Root(name = "channel", strict = false)
    public static class Channel {
        @ElementList(entry = "item", inline = true)
        public List<Article> articles;
    }

    // Class representing an article item in the RSS feed.
    @Root(name = "item", strict = false)
    public static class Article {
        @Element(name = "title")
        public String title;

        @Element(name = "link")
        public String link;

        @Element(name = "description", required = false)
        public String description;

        @Element(name = "pubDate", required = false)
        public String pubDate;

        // Method to format the publication date to a more user-friendly format.
        public String getFormattedDate() {
            try {
                // Original date format in the RSS feed
                SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                Date date = originalFormat.parse(this.pubDate);
                // New date format for displaying in the app
                SimpleDateFormat newFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
                return newFormat.format(date);
            } catch (Exception e) {
                // Return original date string if parsing fails
                return this.pubDate;
            }
        }
    }

    // Interface representing the API for fetching RSS feed articles.
    public interface RSSApi {
        @GET("feed/")
        Call<Feed> getArticles();
    }
}
