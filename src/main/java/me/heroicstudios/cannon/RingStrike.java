package me.heroicstudios.cannon;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.heroicstudios.HeroicStrikeCannon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RingStrike {
    
    private final HeroicStrikeCannon plugin;
    
    public RingStrike(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }
    
    public boolean execute(Player player, Location spawnLocation, Location targetLocation, int ringCount) {
        // WorldGuard check if available
        if (isWorldGuardAvailable() && !canStrikeLocation(player, targetLocation)) {
            player.sendMessage("§cYou cannot use the strike cannon in this protected area!");
            return false;
        }
        
        var config = plugin.getConfigManager();
        double baseRadius = config.getRingStrikeBaseRadius();
        double ringSpacing = config.getRingStrikeSpacing();
        int basePoints = config.getRingStrikeBasePoints();
        int pointsPerRing = config.getRingStrikePointsPerRing();
        int baseFuse = config.getRingStrikeBaseFuse();
        int fuseOffset = config.getRingStrikeFuseOffset();
        
        // Use target location for ring center, spawn location for Y coordinate
        double centerX = targetLocation.getX();
        double centerZ = targetLocation.getZ();
        double spawnY = spawnLocation.getY();
        
        // Spawn rings with delay
        new BukkitRunnable() {
            int currentRing = 0;
            
            @Override
            public void run() {
                if (currentRing >= ringCount) {
                    cancel();
                    return;
                }
                
                spawnRing(player, centerX, centerZ, spawnY, currentRing, 
                         baseRadius, ringSpacing, basePoints, pointsPerRing, 
                         baseFuse, fuseOffset);
                
                currentRing++;
            }
        }.runTaskTimer(plugin, 0L, 0L); // 5 tick delay between rings
        
        return true;
    }
    
    private void spawnRing(Player player, double centerX, double centerZ, double spawnY, 
                          int ringIndex, double baseRadius, double ringSpacing,
                          int basePoints, int pointsPerRing, int baseFuse, int fuseOffset) {
        
        double radius = baseRadius + (ringIndex * ringSpacing);
        int points = basePoints + (ringIndex * pointsPerRing);
        int fuse = baseFuse + (ringIndex * fuseOffset);
        
        double angleStep = 2 * Math.PI / points;
        
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            
            Location tntLocation = new Location(player.getWorld(), x, spawnY, z);
            
            // Spawn TNT
            TNTPrimed tnt = player.getWorld().spawn(tntLocation, TNTPrimed.class);
            tnt.setFuseTicks(fuse);
            tnt.setYield(plugin.getConfigManager().getRingStrikeTntYield());
            tnt.setIsIncendiary(false);
            
            // Set downward velocity
            tnt.setVelocity(new Vector(0, -0.1, 0));
            
            // Set source for damage tracking
            tnt.setSource(player);
        }
        
        // Visual/audio effects
        player.sendMessage("§6Ring " + (ringIndex + 1) + " deployed! §7(" + points + " TNT)");
        player.getWorld().playSound(player.getLocation(), "entity.tnt.primed", 0.5f, 1.2f);
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