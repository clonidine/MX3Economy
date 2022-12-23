package com.dev.migx3.mx3economy.commands.subcommands.impl.coins;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.commands.subcommands.SubCommand;
import com.dev.migx3.mx3economy.database.Database;
import com.dev.migx3.mx3economy.managers.PlayerManager;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CoinsGiveSubCommand implements SubCommand {
    private final ProfileManager profileManager;

    private final Database database;

    private final MX3Economy plugin;

    private final PlayerManager playerManager;

    public CoinsGiveSubCommand(MX3Economy plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
        this.playerManager = plugin.getPlayerManager();
        this.database = plugin.getDatabase();
    }

    public List<String> getAliases() {
        return List.of("give", "donate");
    }

    public boolean execute(Player player, String[] args) {
        try {

            UUID targetUUID;

            if (args.length != 3) {
                player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("help.coins-usage")));
                return false;
            }

            double value = Double.parseDouble(args[2]);
            String name;

            try {
                targetUUID = MojangAPI.getUUID(args[1]);
            } catch (InvalidPlayerException | IOException | APIException | NullPointerException e) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return false;
            }

            if (!this.profileManager.hasAccount(targetUUID)) {
                player.sendMessage(ChatColor.RED + "This player has never joined the server");
                return false;
            }

            if (targetUUID.equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You cannot give money to yourself");
                return false;
            }

            if (this.playerManager.getPlayersInCache().contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must wait 3 minutes to give money again");
                return false;
            }

            try {
                name = MojangAPI.getProfile(targetUUID).getName();
            } catch (APIException | IOException e) {
                throw new RuntimeException(e);
            }

            String collectionName = "profiles";

            Document targetDocument = profileManager.getProfile(targetUUID);
            Document playerDocument = profileManager.getProfile(player.getUniqueId());

            double playerTotalCoins = playerDocument.getDouble("coins");
            double targetTotalCoins = targetDocument.getDouble("coins");

            if (playerTotalCoins < value) {
                player.sendMessage(ChatColor.RED + "You don't have enough money");
                return false;
            }

            Bson targetFilter = Filters.eq("uuid", targetUUID.toString());
            Bson playerFilter = Filters.eq("uuid", player.getUniqueId().toString());

            Bson targetUpdate = Updates.set("coins", targetTotalCoins + value);
            Bson playerUpdate = Updates.set("coins", playerTotalCoins - value);

            database.updateOne(collectionName, targetFilter, targetUpdate);
            database.updateOne(collectionName, playerFilter, playerUpdate);

            player.sendMessage(ChatColor.GREEN + "You successfully gave $" + value + " to player " + name);
            Player target = Bukkit.getPlayer(targetUUID);

            if (target != null)
                target.sendMessage(ChatColor.GREEN + "You've received $" + value + " from " + player.getName());

            playerManager.getPlayersInCache().add(player.getUniqueId());
            playerManager.playersInCacheCleanup();
        } catch (NumberFormatException exception) {
            player.sendMessage(ChatColor.RED + "You must specific a number");
            return false;
        }
        return false;
    }
}
