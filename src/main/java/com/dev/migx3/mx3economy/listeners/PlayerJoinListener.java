package com.dev.migx3.mx3economy.listeners;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.entities.Profile;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinListener implements Listener {


    private final ProfileManager profileManager;

    public PlayerJoinListener(MX3Economy plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.profileManager = plugin.getProfileManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (profileManager.hasAccount(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "Profile has been found");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Profile has been created!");
        profileManager.createProfile(new Profile(player.getName(), player.getUniqueId(), Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress(), 0.0));
    }
}
