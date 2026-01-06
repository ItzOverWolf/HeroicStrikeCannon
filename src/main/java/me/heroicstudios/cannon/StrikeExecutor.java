package me.heroicstudios.cannon;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.item.ItemDataUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StrikeExecutor {
    
    private final HeroicStrikeCannon plugin;
    private final RingStrike ringStrike;
    
    public StrikeExecutor(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
        this.ringStrike = new RingStrike(plugin);
    }
    
    public boolean executeStrike(Player player, ItemStack cannon, Location targetLocation) {
        StrikeType strikeType = ItemDataUtil.getStrikeType(cannon);
        int strikeHeight = ItemDataUtil.getStrikeHeight(cannon);
        int ringCount = ItemDataUtil.getRingCount(cannon);
        
        // Calculate spawn location (above player, not target)
        Location spawnLocation = player.getLocation().clone();
        spawnLocation.setY(spawnLocation.getY() + strikeHeight);
        
        switch (strikeType) {
            case ORBITAL_RING:
                return ringStrike.execute(player, spawnLocation, targetLocation, ringCount);
            case ORBITAL_SINGLE:
                // TODO: Implement single strike when needed
                player.sendMessage("§cSingle strike not implemented yet!");
                return false;
            default:
                return false;
        }
    }
}