package com.dev.migx3.mx3economy.db.impl;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.db.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@SuppressWarnings("unchecked")
public class MongoDB implements Database {
    private final MX3Economy plugin;

    private MongoDatabase mongoDatabase;

    private MongoClient mongoClient;

    public MongoDB(MX3Economy plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            mongoClient = new MongoClient(
                    plugin.getConfig().getString("Mongo.Host"),
                    plugin.getConfig().getInt("Mongo.Port"));

            mongoDatabase = mongoClient.getDatabase(plugin.getConfig().getString("Mongo.Database"));
        } catch (MongoSocketOpenException exception) {
            Bukkit.getConsoleSender().sendMessage("" + ChatColor.RED + "Error connecting to Database");
        }
    }

    public void insert(Document document, String collectionName) {
        getCollection(collectionName).insertOne(document);
    }

    public void query() {}

    public void close() {
        mongoClient.close();
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }

    public void deleteOne(Document document, String collectionName) {
        getCollection(collectionName).findOneAndDelete(document);
    }

    public void updateOne(String collectionName, Bson filter, Bson update) {
        getCollection(collectionName).updateOne(filter, update);
    }

    public Document find(String collectionName, String key, String target) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(key, target);
        return getCollection(collectionName).find(searchQuery).first();
    }
}
