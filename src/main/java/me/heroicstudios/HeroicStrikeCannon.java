package me.heroicstudios;

import me.heroicstudios.command.CannonCommand;
import me.heroicstudios.command.CannonTabCompleter;
import me.heroicstudios.config.ConfigManager;
import me.heroicstudios.cooldown.CooldownManager;
import me.heroicstudios.listener.RodUseListener;
import me.heroicstudios.listener.SignInputListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeroicStrikeCannon extends JavaPlugin {

    private static HeroicStrikeCannon instance;
    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private SignInputListener signInputListener;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.cooldownManager = new CooldownManager();

        // Load configuration
        configManager.loadConfig();

        // Register listeners
        getServer().getPluginManager().registerEvents(new RodUseListener(this), this);
        this.signInputListener = new SignInputListener(this);
        getServer().getPluginManager().registerEvents(signInputListener, this);

        // Register commands
        var command = getCommand("hsc");
        if (command != null) {
            command.setExecutor(new CannonCommand(this));
            command.setTabCompleter(new CannonTabCompleter());
        }

        getLogger().info("Heroic Strike Cannon has been enabled!");
    }

    @Override
    public void onDisable() {
        cooldownManager.clearAll();
        getLogger().info("Heroic Strike Cannon has been disabled!");
    }

    public static HeroicStrikeCannon getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public SignInputListener getSignInputListener() {
        return signInputListener;
    }
}