package me.heroicstudios.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CannonTabCompleter implements TabCompleter {
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
        
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            if (sender.hasPermission("heroicstrike.give")) {
                subcommands.add("give");
            }
            if (sender.hasPermission("heroicstrike.reload")) {
                subcommands.add("reload");
            }
            return filterStartingWith(subcommands, args[0]);
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            if (sender.hasPermission("heroicstrike.give")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return filterStartingWith(playerNames, args[1]);
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<String> filterStartingWith(List<String> options, String input) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }
}