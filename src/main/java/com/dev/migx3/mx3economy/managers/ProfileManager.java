package com.dev.migx3.mx3economy.managers;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.db.Database;
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

public class ProfileManager {
    private final ConcurrentMap<String, Document> profilesInCache = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Database database;

    public ProfileManager(MX3Economy plugin) {
        this.database = plugin.getDatabase();
    }

    public void profileCacheCleanup() {
        Objects.requireNonNull(this.profilesInCache);
        this.executor.scheduleAtFixedRate(this.profilesInCache::clear, 1L, 1L, TimeUnit.MINUTES);
    }

    public void addInCache(UUID uuid, Document document) {
        this.profilesInCache.put(uuid.toString(), document);
        profileCacheCleanup();
    }

    public void createProfile(Profile profile) {
        Document profileDocument = (new Document()).append("name", profile.getName()).append("uuid", profile.getUuid().toString()).append("ip", profile.getIp()).append("coins", 0.0D);
        this.database.insert(profileDocument, "profiles");
    }

    public void deleteProfile(Document document) {
        this.database.deleteOne(document, "profiles");
    }

    public boolean hasAccount(UUID uuid) {
        return getProfile(uuid) != null;
    }

    public Document getProfile(UUID uuid) {
        if (!this.profilesInCache.containsKey(uuid.toString()))
            return this.database.find("profiles", "uuid", uuid.toString());
        return this.profilesInCache.getOrDefault(uuid.toString(), null);
    }

    public Map<String, Document> getProfilesInCache() {
        return this.profilesInCache;
    }
}
