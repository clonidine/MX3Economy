package com.dev.migx3.mx3economy.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private final Set<UUID> playersInCache = new HashSet<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void playersInCacheCleanup() {
        executor.scheduleAtFixedRate(playersInCache::clear, 0L, 3L, TimeUnit.MINUTES);
    }

    public Set<UUID> getPlayersInCache() {
        return playersInCache;
    }
}
