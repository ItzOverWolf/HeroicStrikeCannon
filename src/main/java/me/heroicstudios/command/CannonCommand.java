package me.heroicstudios.command;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.item.CannonItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CannonCommand implements CommandExecutor {
    
    private final HeroicStrikeCannon plugin;
    
    public CannonCommand(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                return handleGive(sender, args);
            case "reload":
                return handleReload(sender);
            default:
                sendUsage(sender);
                return true;
        }
    }
    
    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("heroicstrike.give")) {
            sender.sendMessage("§cYou don't have permission to give Strike Cannons!");
            return true;
        }
        
        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer '" + args[1] + "' not found!");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cYou must specify a player when using this command from console!");
            return true;
        }
        
        // Give the cannon
        var cannon = CannonItem.createDefault();
        target.getInventory().addItem(cannon);
        
        target.sendMessage("§aYou received a Heroic Strike Cannon!");
        if (sender != target) {
            sender.sendMessage("§aGave Heroic Strike Cannon to " + target.getName());
        }
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("heroicstrike.reload")) {
            sender.sendMessage("§cYou don't have permission to reload the configuration!");
            return true;
        }
        
        plugin.getConfigManager().loadConfig();
        sender.sendMessage("§aHeroic Strike Cannon configuration reloaded!");
        
        return true;
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6=== Heroic Strike Cannon ===");
        sender.sendMessage("§e/hsc give [player] §7- Give a strike cannon");
        sender.sendMessage("§e/hsc reload §7- Reload configuration");
    }
}