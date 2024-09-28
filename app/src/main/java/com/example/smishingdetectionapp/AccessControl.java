package com.example.smishingdetectionapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class AccessControl {
    private MongoCollection<Document> rolesCollection;

    public AccessControl() {
        // MongoDB connection setting
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:3000");
        MongoDatabase database = mongoClient.getDatabase("detectlist.db");
        rolesCollection = database.getCollection("roles");
    }

    // checking user has minimum permission
    public boolean hasPermission(String userId, String requiredPermission) {
        Document userRole = rolesCollection.find(new Document("userId", userId)).first();
        if (userRole != null) {
            List<String> permissions = (List<String>) userRole.get("permissions");
            return permissions.contains(requiredPermission);
        }

        return false;
    }

}
