package me.heroicstudios.cannon;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.heroicstudios.HeroicStrikeCannon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DrillStrike {

    private final HeroicStrikeCannon plugin;

    public DrillStrike(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }

    public boolean execute(Player player, Location spawnLocation, Location targetLocation) {
        // WorldGuard check if available
        if (isWorldGuardAvailable() && !canStrikeLocation(player, targetLocation)) {
            player.sendMessage("§cYou cannot use the strike cannon in this protected area!");
            return false;
        }

        var config = plugin.getConfigManager();
        int startHeight = config.getDrillStrikeStartHeight();
        int minY = config.getDrillStrikeMinY();
        int step = config.getDrillStrikeStep();
        int delayTicks = config.getDrillStrikeDelayTicks();
        float explosionPower = config.getDrillStrikeExplosionPower();
        int maxDepth = config.getDrillStrikeMaxDepth();

        // Calculate drill parameters
        double drillX = targetLocation.getX();
        double drillZ = targetLocation.getZ();
        double startY = player.getLocation().getY() + startHeight;

        // Validate depth
        double totalDepth = startY - minY;
        if (totalDepth > maxDepth) {
            player.sendMessage("§cDrill depth exceeds maximum limit! §7(" + (int)totalDepth + "/" + maxDepth + " blocks)");
            return false;
        }

        // Start drilling
        player.sendMessage("§c☢ BUNKER BUSTER DEPLOYED ☢");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.8f, 0.5f);

        // Create drilling task
        BukkitTask drillTask = new BukkitRunnable() {
            double currentY = startY;
            int explosionCount = 0;
            final int maxExplosions = (int) Math.ceil(totalDepth / step);

            @Override
            public void run() {
                // Check if we should stop
                if (currentY <= minY || explosionCount >= maxExplosions) {
                    cancel();
                    player.sendMessage("§6Bunker buster complete! §7Depth: " + (int)(startY - currentY) + " blocks");
                    return;
                }

                // Check if player is still online and in same world
                if (!player.isOnline() || !player.getWorld().equals(targetLocation.getWorld())) {
                    cancel();
                    return;
                }

                // Create explosion at current position
                Location explosionLocation = new Location(targetLocation.getWorld(), drillX, currentY, drillZ);

                // Check if chunk is loaded
                if (!explosionLocation.getChunk().isLoaded()) {
                    cancel();
                    return;
                }

                // Create explosion
                targetLocation.getWorld().createExplosion(
                        explosionLocation,
                        explosionPower,
                        false, // No fire
                        true   // Break blocks
                );

                // Visual and audio effects
                targetLocation.getWorld().spawnParticle(
                        Particle.EXPLOSION_LARGE,
                        explosionLocation,
                        3,
                        0.5, 0.5, 0.5,
                        0.1
                );

                targetLocation.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        explosionLocation,
                        10,
                        1.0, 1.0, 1.0,
                        0.05
                );

                // Sound with increasing pitch as we go deeper
                float pitch = 0.8f + (explosionCount * 0.1f);
                pitch = Math.min(pitch, 2.0f);
                targetLocation.getWorld().playSound(
                        explosionLocation,
                        Sound.ENTITY_GENERIC_EXPLODE,
                        0.7f,
                        pitch
                );

                // Update player with depth info (action bar)
                int currentDepth = (int)(startY - currentY);
                player.sendActionBar("§7Drilling... §c" + currentDepth + "§7/§c" + (int)totalDepth + " §7blocks deep");

                // Move down for next explosion
                currentY -= step;
                explosionCount++;
            }
        }.runTaskTimer(plugin, 0L, delayTicks);

        return true;
    }

    private boolean isWorldGuardAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("WorldGuard") &&
                plugin.getConfigManager().isWorldGuardEnabled();
    }

    private boolean canStrikeLocation(Player player, Location location) {
        if (!isWorldGuardAvailable()) {
            return true;
        }

        try {
            var config = plugin.getConfigManager();
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            var worldEditLocation = BukkitAdapter.adapt(location);
            var worldEditPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            // Check build permission
            if (config.isWorldGuardCheckBuildPermission()) {
                if (!query.testBuild(worldEditLocation, worldEditPlayer)) {
                    return false;
                }
            }

            // Check explosion flags
            if (config.isWorldGuardRespectExplosionFlags()) {
                if (!query.testState(worldEditLocation, worldEditPlayer, Flags.TNT)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("WorldGuard check failed: " + e.getMessage());
            return true; // Default to allowing if check fails
        }
    }
}