package com.dev.migx3.mx3economy.commands;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.commands.subcommands.SubCommand;
import com.dev.migx3.mx3economy.commands.subcommands.impl.profile.ProfileDeleteSubCommand;
import com.dev.migx3.mx3economy.commands.subcommands.impl.profile.ProfileSearchSubCommand;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProfileCommand implements CommandExecutor, TabCompleter {
    private final List<SubCommand> subCommands = new ArrayList<>();

    private final MX3Economy plugin;

    public ProfileCommand(MX3Economy plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("profile")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("profile")).setTabCompleter(this);
        subCommands.add(new ProfileSearchSubCommand(plugin));
        subCommands.add(new ProfileDeleteSubCommand(plugin));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
            return false;
        }

        if (!sender.isOp() || !sender.hasPermission("mx3economy.profile.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have sufficient permissions to run this command.");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("help.profile-usage")));
            return false;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0].toLowerCase()))
                try {
                    return subCommand.execute(player, args);
                } catch (InvalidPlayerException | APIException | IOException e) {
                    throw new RuntimeException(e);
                }
        }

        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> possibleOptions = Arrays.asList("search", "delete");
        if (args.length == 1) {
            String argument = args[0];
            for (String option : possibleOptions) {
                if (option.startsWith(argument))
                    completions.add(option);
            }
        }
        return completions;
    }
}
