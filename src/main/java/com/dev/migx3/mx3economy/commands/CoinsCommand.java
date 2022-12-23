package com.dev.migx3.mx3economy.commands;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.commands.subcommands.SubCommand;
import com.dev.migx3.mx3economy.commands.subcommands.impl.coins.CoinsGiveSubCommand;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class CoinsCommand implements CommandExecutor, TabCompleter {
    private final List<SubCommand> subCommands = new ArrayList<>();

    private final ProfileManager profileManager;

    public CoinsCommand(MX3Economy plugin) {
        profileManager = plugin.getProfileManager();
        Objects.requireNonNull(plugin.getCommand("coins")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("coins")).setTabCompleter(this);
        subCommands.add(new CoinsGiveSubCommand(plugin));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Locale.setDefault(Locale.US);

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return false;
        }

        if (args.length == 0) {
            Document profileDocument = profileManager.getProfile(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Your coins: $" + String.format("%.2f", profileDocument.getDouble("coins")));
        }

        if (args.length == 1) {
            UUID uuid;
            String name;

            try {
                uuid = MojangAPI.getUUID(args[0]);
            } catch (IOException | NullPointerException | InvalidPlayerException | APIException e) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }

            if (!profileManager.hasAccount(uuid)) {
                player.sendMessage(ChatColor.RED + "Player has never joined the server");
                return true;
            }

            try {
                name = MojangAPI.getProfile(uuid).getName();
            } catch (IOException | APIException e) {
                throw new RuntimeException(e);
            }

            Document targetDocument = profileManager.getProfile(uuid);
            player.sendMessage(ChatColor.YELLOW + name + "'s " + "coins: $" + new BigDecimal(String.valueOf(targetDocument.getDouble("coins"))).toPlainString());
            profileManager.addInCache(uuid, targetDocument);
        }

        if (args.length > 0)
            for (SubCommand subCommand : this.subCommands) {
                if (subCommand.getAliases().contains(args[0].toLowerCase()))
                    try {
                        return subCommand.execute(player, args);
                    } catch (InvalidPlayerException|me.kbrewster.exceptions.APIException|IOException e) {
                        throw new RuntimeException(e);
                    }
            }

        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> possibleOptions = List.of("give");
        if (args.length == 1)
            for (String option : possibleOptions) {
                if (option.startsWith(args[0].toLowerCase()))
                    completions.add(option);
            }
        return completions;
    }
}
