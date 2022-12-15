package com.dev.migx3.mx3economy.entities;

import java.util.UUID;

public class Profile {

    private String name;
    private final UUID uuid;
    private final String ip;
    private double coins;

    public Profile(String name, UUID uuid, String ip, double coins) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.coins = coins;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }
}
