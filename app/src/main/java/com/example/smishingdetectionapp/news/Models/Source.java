package com.example.smishingdetectionapp.news.Models;

// Define the Source class which models the source of a news article
public class Source {
    String id = "";
    String name = "";

    // Like the last two models, Getter method to return the identifier of the news source
    public String getId() {
        return id;
    }

    // Setter method to set the identifier of the news source
    public void setId(String id) {
        this.id = id;
    }

    // Getter method to return the name of the news source
    public String getName() {
        return name;
    }

    // Setter method to set the name of the news source
    public void setName(String name) {
        this.name = name;
    }
}
