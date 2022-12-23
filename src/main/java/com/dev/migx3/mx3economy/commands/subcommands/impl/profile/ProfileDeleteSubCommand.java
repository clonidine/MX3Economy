package com.dev.migx3.mx3economy.commands.subcommands.impl.profile;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.commands.subcommands.SubCommand;
import com.dev.migx3.mx3economy.managers.ProfileManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ProfileDeleteSubCommand implements SubCommand {
    private final ProfileManager profileManager;

    private final MX3Economy plugin;

    public ProfileDeleteSubCommand(MX3Economy plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
    }

    public List<String> getAliases() {
        return Collections.singletonList("delete");
    }

    public boolean execute(Player player, String[] args) {
        UUID uuid;

        if (args.length != 2) {
            this.plugin.getConfig().getString("help.profile-usage");
            return false;
        }

        try {
            uuid = MojangAPI.getUUID(args[1]);
        } catch (InvalidPlayerException | IOException | APIException | NullPointerException e) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return false;
        }

        if (!profileManager.hasAccount(uuid)) {
            player.sendMessage(ChatColor.RED + "This player has never joined the server");
            return false;
        }

        if (uuid.equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't delete your profile");
            return false;
        }

        Document profileDocument = profileManager.getProfile(uuid);

        profileManager.deleteProfile(profileDocument);

        player.sendMessage(ChatColor.GREEN + "Profile deleted successfully");
        return false;
    }
}
