package me.heroicstudios.command;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.item.CannonItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CannonCommand implements CommandExecutor {

    private final HeroicStrikeCannon plugin;

    public CannonCommand(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

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
        String cannonType = "ring"; // default

        // /hsc give [player] [type]
        if (args.length >= 2) {
            Player possiblePlayer = Bukkit.getPlayer(args[1]);

            if (possiblePlayer != null) {
                target = possiblePlayer;
                if (args.length >= 3) {
                    cannonType = args[2].toLowerCase();
                }
            } else {
                // args[1] is cannon type
                if (sender instanceof Player) {
                    target = (Player) sender;
                    cannonType = args[1].toLowerCase();
                } else {
                    sender.sendMessage("§cPlayer '" + args[1] + "' not found!");
                    return true;
                }
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage("§cYou must specify a player when using this command from console!");
                return true;
            }
        }

        ItemStack cannon;
        String cannonName;

        switch (cannonType) {
            case "drill", "bunker", "buster" -> {
                cannon = CannonItem.createDrillCannon();
                cannonName = "§cBunker Buster Cannon";
            }
            case "ring", "orbital" -> {
                cannon = CannonItem.createRingCannon();
                cannonName = "§6Orbital Ring Cannon";
            }
            default -> {
                sender.sendMessage("§cInvalid cannon type! Use 'ring' or 'drill'");
                return true;
            }
        }

        target.getInventory().addItem(cannon);
        target.sendMessage("§aYou received a " + cannonName + "§a!");

        if (sender != target) {
            sender.sendMessage("§aGave " + cannonName + " §ato " + target.getName());
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
        sender.sendMessage("§e/hsc give [player] [type]");
        sender.sendMessage("§7Types: §fring §7(orbital ring), §fdrill §7(bunker buster)");
        sender.sendMessage("§e/hsc reload");
    }
}
