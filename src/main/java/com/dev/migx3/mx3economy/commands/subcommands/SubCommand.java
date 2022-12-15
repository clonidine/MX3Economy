package com.dev.migx3.mx3economy.commands.subcommands;

import me.kbrewster.exceptions.APIException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public interface SubCommand {

    List<String> getAliases();
    boolean execute(Player player, String[] args) throws APIException, IOException;
}
