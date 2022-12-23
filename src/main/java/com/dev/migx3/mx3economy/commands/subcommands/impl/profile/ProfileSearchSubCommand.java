package com.dev.migx3.mx3economy.commands.subcommands.impl.profile;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.commands.subcommands.SubCommand;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProfileSearchSubCommand implements SubCommand {
    private final ProfileManager profileManager;

    private final MX3Economy plugin;

    public ProfileSearchSubCommand(MX3Economy plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
    }

    public List<String> getAliases() {
        return Collections.singletonList("search");
    }

    public boolean execute(Player player, String[] args) {
        UUID uuid;

        if (args.length != 2) {
            this.plugin.getConfig().getString("help.profile-usage");
            return false;
        }

        try {
            uuid = MojangAPI.getUUID(args[1]);
        } catch (InvalidPlayerException|java.io.IOException|me.kbrewster.exceptions.APIException|NullPointerException e) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return false;
        }

        if (!this.profileManager.hasAccount(uuid)) {
            player.sendMessage( ChatColor.RED + "This player has never joined the server");
            System.out.println(this.profileManager.getProfilesInCache().size());
            return false;
        }

        Document profileDocument = this.profileManager.getProfile(uuid);

        if (this.profileManager.getProfilesInCache().containsKey(uuid.toString())) {
            player.sendMessage(ChatColor.GREEN + "[CACHE]");
        } else {
            player.sendMessage(ChatColor.GREEN + "[DATABASE]");
            System.out.println(this.profileManager.getProfilesInCache().size());
            this.profileManager.addInCache(uuid, profileDocument);
        }

        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100.0F, 100.0F);

        player.sendMessage(ChatColor.GREEN + "Name: " + profileDocument.getString("name"));
        player.sendMessage(ChatColor.GREEN + "UUID: " + profileDocument.getString("uuid"));
        player.sendMessage(ChatColor.GREEN + "IP: " + profileDocument.getString("ip"));
        player.sendMessage(ChatColor.GREEN + "Coins: " + profileDocument.getDouble("coins"));

        return false;
    }
}
