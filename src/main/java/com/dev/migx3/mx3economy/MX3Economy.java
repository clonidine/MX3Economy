package com.dev.migx3.mx3economy;

import com.dev.migx3.mx3economy.commands.CoinsCommand;
import com.dev.migx3.mx3economy.commands.ProfileCommand;
import com.dev.migx3.mx3economy.db.Database;
import com.dev.migx3.mx3economy.db.impl.MongoDB;
import com.dev.migx3.mx3economy.listeners.PlayerJoinListener;
import com.dev.migx3.mx3economy.managers.PlayerManager;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class MX3Economy extends JavaPlugin {
    private Database database;

    private ProfileManager profileManager;

    private PlayerManager playerManager;

    public void onEnable() {
        if (getConfig().getBoolean("Mongo.Enabled")) {
            this.database = new MongoDB(this);
            this.database.connect();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MongoDB connected");
        }

        registerManagers();
        registerListeners();
        registerCommands();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MX3Economy enabled");
    }

    public void onDisable() {
        this.database.close();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MongoDB disconnected");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MX3Economy disabled");
    }

    public Database getDatabase() {
        return this.database;
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public void registerManagers() {
        this.profileManager = new ProfileManager(this);
        this.playerManager = new PlayerManager();
    }

    public void registerListeners() {
        new PlayerJoinListener(this);
    }

    public void registerCommands() {
        new ProfileCommand(this);
        new CoinsCommand(this);
    }
}
