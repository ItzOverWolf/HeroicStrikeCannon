package me.heroicstudios.config;

import me.heroicstudios.HeroicStrikeCannon;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final HeroicStrikeCannon plugin;
    private FileConfiguration config;

    public ConfigManager(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // Cooldown settings
    public int getCooldownSeconds() {
        return config.getInt("cooldown-seconds", 30);
    }

    // Strike settings
    public int getStrikeMaxHeight() {
        return config.getInt("strike.max-height", 300);
    }

    public int getStrikeMinHeight() {
        return config.getInt("strike.min-height", 20);
    }

    public int getStrikeDefaultHeight() {
        return config.getInt("strike.default-height", 100);
    }

    public int getStrikeMaxRings() {
        return config.getInt("strike.max-rings", 10);
    }

    public int getStrikeMaxRange() {
        return config.getInt("strike.max-range", 100);
    }

    // Ring strike settings
    public double getRingStrikeBaseRadius() {
        return config.getDouble("ring-strike.base-radius", 2.0);
    }

    public double getRingStrikeSpacing() {
        return config.getDouble("ring-strike.ring-spacing", 3.0);
    }

    public int getRingStrikeBasePoints() {
        return config.getInt("ring-strike.base-points", 6);
    }

    public int getRingStrikePointsPerRing() {
        return config.getInt("ring-strike.points-per-ring", 4);
    }

    public int getRingStrikeBaseFuse() {
        return config.getInt("ring-strike.base-fuse", 60);
    }

    public int getRingStrikeFuseOffset() {
        return config.getInt("ring-strike.fuse-offset", 4);
    }

    public float getRingStrikeTntYield() {
        return (float) config.getDouble("ring-strike.tnt-yield", 6.0);
    }

    public boolean getRingStrikeBreakBlocks() {
        return config.getBoolean("ring-strike.break-blocks", true);
    }

    // WorldGuard settings
    public boolean isWorldGuardEnabled() {
        return config.getBoolean("worldguard.enabled", true);
    }

    public boolean isWorldGuardCheckBuildPermission() {
        return config.getBoolean("worldguard.check-build-permission", true);
    }

    public boolean isWorldGuardRespectExplosionFlags() {
        return config.getBoolean("worldguard.respect-explosion-flags", true);
    }

    // Drill strike settings
    public int getDrillStrikeStartHeight() {
        return config.getInt("drill-strike.start-height", 120);
    }

    public int getDrillStrikeMinY() {
        return config.getInt("drill-strike.min-y", -54);
    }

    public int getDrillStrikeStep() {
        return config.getInt("drill-strike.step", 2);
    }

    public int getDrillStrikeDelayTicks() {
        return config.getInt("drill-strike.delay-ticks", 3);
    }

    public float getDrillStrikeExplosionPower() {
        return (float) config.getDouble("drill-strike.explosion-power", 2.0);
    }

    public int getDrillStrikeMaxDepth() {
        return config.getInt("drill-strike.max-depth", 120);
    }
}