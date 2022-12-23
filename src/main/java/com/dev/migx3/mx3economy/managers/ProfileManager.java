package com.dev.migx3.mx3economy.managers;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.database.Database;
import com.dev.migx3.mx3economy.entities.Profile;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.bson.conversions.Bson;

public class ProfileManager {

    private final ConcurrentMap<String, Document> profilesInCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final String COLLECTION_NAME = "profiles";
    private final Database database;

    public ProfileManager(MX3Economy plugin) {
        this.database = plugin.getDatabase();
    }

    public void profileCacheCleanup() {
        Objects.requireNonNull(profilesInCache);
        executor.scheduleAtFixedRate(profilesInCache::clear, 1L, 1L, TimeUnit.MINUTES);
    }

    public void addInCache(UUID uuid, Document document) {
        profilesInCache.put(uuid.toString(), document);
        profileCacheCleanup();
    }

    public void createProfile(Profile profile) {
        Document profileDocument = new Document()
                .append("name", profile.getName())
                .append("uuid", profile.getUuid().toString())
                .append("ip", profile.getIp())
                .append("coins", 0.0);

        database.insert(profileDocument, COLLECTION_NAME);
    }

    public void updateProfile(Bson filter, Bson update) {
        database.updateOne(COLLECTION_NAME, filter, update);
    }

    public void deleteProfile(Document document) {
        database.deleteOne(document, COLLECTION_NAME);
    }

    public boolean hasAccount(UUID uuid) {
        return getProfile(uuid) != null;
    }

    public Document getProfile(UUID uuid) {
        if (!profilesInCache.containsKey(uuid.toString()))
            return database.find(COLLECTION_NAME, "uuid", uuid.toString());
        return profilesInCache.getOrDefault(uuid.toString(), null);
    }

    public Map<String, Document> getProfilesInCache() {
        return profilesInCache;
    }
}
