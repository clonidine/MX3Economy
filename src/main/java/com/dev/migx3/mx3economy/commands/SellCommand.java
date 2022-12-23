package com.dev.migx3.mx3economy.commands;

import com.dev.migx3.mx3economy.MX3Economy;
import com.dev.migx3.mx3economy.managers.ProfileManager;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SellCommand implements CommandExecutor, TabCompleter {

    private final MX3Economy plugin;
    private final ProfileManager profileManager;

    public SellCommand(MX3Economy plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
        Objects.requireNonNull(plugin.getCommand("sell")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("sell")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Locale.setDefault(Locale.US);

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
            return false;
        }

        if (args.length == 0) {

            ItemStack playerItem = player.getInventory().getItemInMainHand();

            if (playerItem.getItemMeta() == null) {
                player.sendMessage(ChatColor.RED + "You're not with an item in your hand");
                return false;
            }

            String playerItemName = playerItem.getType().name();

            System.out.println(playerItemName);

            ConfigurationSection section = plugin.getConfig().getConfigurationSection("items");

            if (section == null) {
                player.sendMessage(ChatColor.RED + "Setup your available items in config.yml");
                return false;
            }

            AtomicReference<Double> itemPrice = new AtomicReference<>(0.0);

            int quantity = 0;

            itemPrice.set(plugin.getConfig().getDouble("items.item1.price"));

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType().name().equalsIgnoreCase(playerItem.getType().name())) {
                    quantity++;
                }
            }

            Document playerDocument = profileManager.getProfile(player.getUniqueId());

            double playerCoins = playerDocument.getDouble("coins");
            double sellPrice = itemPrice.get() * quantity;

            Bson playerFilter = Filters.eq("uuid", player.getUniqueId().toString());
            Bson playerUpdate = Updates.set("coins", playerCoins + sellPrice);

            profileManager.updateProfile(playerFilter, playerUpdate);
            player.sendMessage(ChatColor.GREEN + "You've sold your item for $" + String.format("%.2f", sellPrice));

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType().getKey().equals(playerItem.getType().getKey())) {
                    player.getInventory().remove(itemStack);
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> possibleOptions = List.of("sell");

        if (args.length == 0) {
            possibleOptions.forEach(option -> {
                if (option.startsWith(Arrays.toString(args))) {
                    completions.add(option);
                }
            });
        }

        return completions;
    }
}