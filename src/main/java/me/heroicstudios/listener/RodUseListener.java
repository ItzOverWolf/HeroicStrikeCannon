package me.heroicstudios.listener;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.cannon.StrikeExecutor;
import me.heroicstudios.item.CannonItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class RodUseListener implements Listener {

    private final HeroicStrikeCannon plugin;
    private final StrikeExecutor strikeExecutor;

    public RodUseListener(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
        this.strikeExecutor = new StrikeExecutor(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!CannonItem.isStrikeCannon(item)) {
            return;
        }

        event.setCancelled(true); // Prevent normal fishing rod behavior

        player.playSound(
                player.getLocation(),      // Location to play the sound
                Sound.ENTITY_ITEM_BREAK, // The sound effect
                1.0f,                      // Volume
                1.0f                       // Pitch
        );

        if (!player.hasPermission("heroicstrike.use")) {
            player.sendMessage("§cYou don't have permission to use the Strike Cannon!");
            return;
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (player.isSneaking()) {
                    openHeightAdjustment(player);
                } else {
                    performStrike(player, item);
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // Cancel fishing bobber launches from strike cannons
        if (event.getEntity().getShooter() instanceof Player player) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            if (CannonItem.isStrikeCannon(mainHand) || CannonItem.isStrikeCannon(offHand)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (CannonItem.isStrikeCannon(item)) {
            event.setCancelled(true); // Prevent fishing mechanics
        }
    }

    private void performStrike(Player player, ItemStack cannon) {
        // Check cooldown
        var cooldownManager = plugin.getCooldownManager();
        if (cooldownManager.isOnCooldown(player.getUniqueId())) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId());
            player.sendMessage("§cStrike Cannon on cooldown! §7" + (remaining / 1000) + "s remaining");
            return;
        }

        // Perform ray trace
        var config = plugin.getConfigManager();
        RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                config.getStrikeMaxRange()
        );

        if (rayTrace == null || rayTrace.getHitBlock() == null) {
            player.sendMessage("§cNo target found! Aim at a block within range.");
            return;
        }

        // Execute strike
        if (strikeExecutor.executeStrike(player, cannon, rayTrace.getHitPosition().toLocation(player.getWorld()))) {
            // Apply cooldown
            cooldownManager.setCooldown(player.getUniqueId(), config.getCooldownSeconds() * 1000L);

            player.sendMessage("§6Orbital Strike initiated! §7Target: " +
                    rayTrace.getHitBlock().getType().name().toLowerCase().replace('_', ' '));
        }
    }

    private void openHeightAdjustment(Player player) {
        plugin.getSignInputListener().openHeightEditor(player);
    }
}