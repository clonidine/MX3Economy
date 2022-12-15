package com.dev.migx3.mx3economy.db;

import org.bson.Document;
import org.bson.conversions.Bson;

public interface Database {
    void connect();

    void insert(Document paramDocument, String paramString);

    void query();

    void close();

    <T> T getCollection(String paramString);

    void deleteOne(Document paramDocument, String paramString);

    void updateOne(String paramString, Bson paramBson1, Bson paramBson2);

    <T> T find(String paramString1, String paramString2, String paramString3);
}