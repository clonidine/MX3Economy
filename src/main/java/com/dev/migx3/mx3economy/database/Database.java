package com.dev.migx3.mx3economy.database;

import org.bson.Document;
import org.bson.conversions.Bson;

public interface Database {

    void connect();

    void insert(Document document, String collectionName);

    void close();

    <T> T getCollection(String collectionName);

    void deleteOne(Document document, String collectionName);

    void updateOne(String collectionName, Bson filter, Bson update);

    <T> T find(String collectionName, String key, String value);
}