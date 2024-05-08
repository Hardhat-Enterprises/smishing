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

    @Root(name = "rss", strict = false)
    public static class Feed {
        @Element(name = "channel")
        public Channel channel;
    }

    @Root(name = "channel", strict = false)
    public static class Channel {
        @ElementList(entry = "item", inline = true)
        public List<Article> articles;
    }

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

        // Changing date format to better suit the app
        public String getFormattedDate() {
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                Date date = originalFormat.parse(this.pubDate);
                SimpleDateFormat newFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
                return newFormat.format(date);
            } catch (Exception e) {
                return this.pubDate; // Return original date string if parsing fails
            }
        }
    }

    public interface RSSApi {
        @GET("feed/")
        Call<Feed> getArticles();
    }
}
