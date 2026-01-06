package me.heroicstudios.item;

import me.heroicstudios.cannon.StrikeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CannonItem {
    
    public static ItemStack createDefault() {
        return createCannon(100, StrikeType.ORBITAL_RING, 3);
    }
    
    public static ItemStack createCannon(int height, StrikeType strikeType, int ringCount) {
        ItemStack cannon = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = cannon.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§cHeroic Strike Cannon");
            
            // Set persistent data
            ItemDataUtil.setStrikeHeight(cannon, height);
            ItemDataUtil.setStrikeType(cannon, strikeType);
            ItemDataUtil.setRingCount(cannon, ringCount);
            
            // Update lore
            updateLore(cannon);
            
            // Set unbreakable
            meta.setUnbreakable(true);
            cannon.setItemMeta(meta);
        }
        
        return cannon;
    }
    
    public static void updateLore(ItemStack cannon) {
        ItemMeta meta = cannon.getItemMeta();
        if (meta == null) return;
        
        int height = ItemDataUtil.getStrikeHeight(cannon);
        StrikeType strikeType = ItemDataUtil.getStrikeType(cannon);
        int ringCount = ItemDataUtil.getRingCount(cannon);
        
        List<String> lore = List.of(
            "",
            "§7Strike Height: §f" + height + " blocks",
            "§7Strike Type: §f" + strikeType.getDisplayName(),
            "§7Ring Count: §f" + ringCount,
            "",
            strikeType.getDescription(),
            "",
            "§eRight Click §7to fire",
            "§eShift + Right Click §7to adjust height"
        );
        
        meta.setLore(lore);
        cannon.setItemMeta(meta);
    }
    
    public static boolean isStrikeCannon(ItemStack item) {
        if (item == null || item.getType() != Material.FISHING_ROD) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        
        return meta.getDisplayName().equals("§cHeroic Strike Cannon");
    }
}