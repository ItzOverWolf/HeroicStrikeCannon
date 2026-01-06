package me.heroicstudios.item;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.cannon.StrikeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemDataUtil {
    
    private static final NamespacedKey STRIKE_HEIGHT = new NamespacedKey(HeroicStrikeCannon.getInstance(), "strike_height");
    private static final NamespacedKey STRIKE_TYPE = new NamespacedKey(HeroicStrikeCannon.getInstance(), "strike_type");
    private static final NamespacedKey RING_COUNT = new NamespacedKey(HeroicStrikeCannon.getInstance(), "ring_count");
    
    public static int getStrikeHeight(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 100;
        
        return meta.getPersistentDataContainer().getOrDefault(STRIKE_HEIGHT, PersistentDataType.INTEGER, 100);
    }
    
    public static void setStrikeHeight(ItemStack item, int height) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        // Validate height
        var config = HeroicStrikeCannon.getInstance().getConfigManager();
        height = Math.max(config.getStrikeMinHeight(), Math.min(height, config.getStrikeMaxHeight()));
        
        meta.getPersistentDataContainer().set(STRIKE_HEIGHT, PersistentDataType.INTEGER, height);
        item.setItemMeta(meta);
    }
    
    public static StrikeType getStrikeType(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return StrikeType.ORBITAL_RING;
        
        String typeString = meta.getPersistentDataContainer().getOrDefault(STRIKE_TYPE, PersistentDataType.STRING, "ORBITAL_RING");
        return StrikeType.fromString(typeString);
    }
    
    public static void setStrikeType(ItemStack item, StrikeType strikeType) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        meta.getPersistentDataContainer().set(STRIKE_TYPE, PersistentDataType.STRING, strikeType.name());
        item.setItemMeta(meta);
    }
    
    public static int getRingCount(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 3;
        
        return meta.getPersistentDataContainer().getOrDefault(RING_COUNT, PersistentDataType.INTEGER, 4);
    }
    
    public static void setRingCount(ItemStack item, int ringCount) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        // Validate ring count
        var config = HeroicStrikeCannon.getInstance().getConfigManager();
        ringCount = Math.max(1, Math.min(ringCount, config.getStrikeMaxRings()));
        
        meta.getPersistentDataContainer().set(RING_COUNT, PersistentDataType.INTEGER, ringCount);
        item.setItemMeta(meta);
    }
}