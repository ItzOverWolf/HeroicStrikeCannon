package me.heroicstudios.listener;

import me.heroicstudios.HeroicStrikeCannon;
import me.heroicstudios.item.CannonItem;
import me.heroicstudios.item.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignInputListener implements Listener {
    
    private final HeroicStrikeCannon plugin;
    private final Map<UUID, Boolean> awaitingInput = new HashMap<>();
    
    public SignInputListener(HeroicStrikeCannon plugin) {
        this.plugin = plugin;
    }
    
    public void openHeightEditor(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!CannonItem.isStrikeCannon(mainHand)) {
            player.sendMessage("§cYou must be holding a Strike Cannon!");
            return;
        }
        
        int currentHeight = ItemDataUtil.getStrikeHeight(mainHand);
        var config = plugin.getConfigManager();
        
        // Create a book for input
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        
        if (bookMeta != null) {
            bookMeta.setTitle("Height Adjustment");
            bookMeta.setAuthor("HeroicStrike");
            bookMeta.addPage(String.format(
                "Enter strike height:\n\n" +
                "Current: %d blocks\n" +
                "Range: %d - %d\n\n" +
                "Type the new height on this page and sign the book.",
                currentHeight,
                config.getStrikeMinHeight(),
                config.getStrikeMaxHeight()
            ));
            book.setItemMeta(bookMeta);
        }
        
        // Give book to player and mark as awaiting input
        awaitingInput.put(player.getUniqueId(), true);
        player.openBook(book);
        
        player.sendMessage("§eEnter the new strike height in the book and sign it!");
    }
    
    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        
        if (!awaitingInput.remove(player.getUniqueId())) {
            return;
        }
        
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!CannonItem.isStrikeCannon(mainHand)) {
            player.sendMessage("§cYou must be holding a Strike Cannon!");
            return;
        }
        
        BookMeta bookMeta = event.getNewBookMeta();
        if (bookMeta.getPageCount() == 0) {
            player.sendMessage("§cNo height entered!");
            return;
        }
        
        String input = bookMeta.getPage(1).trim();
        
        try {
            int newHeight = Integer.parseInt(input);
            var config = plugin.getConfigManager();
            
            if (newHeight < config.getStrikeMinHeight() || newHeight > config.getStrikeMaxHeight()) {
                player.sendMessage(String.format(
                    "§cHeight must be between %d and %d blocks!",
                    config.getStrikeMinHeight(),
                    config.getStrikeMaxHeight()
                ));
                return;
            }
            
            // Update item data
            int oldHeight = ItemDataUtil.getStrikeHeight(mainHand);
            ItemDataUtil.setStrikeHeight(mainHand, newHeight);
            CannonItem.updateLore(mainHand);
            
            player.sendMessage(String.format(
                "§aStrike height updated! §7%d → §f%d blocks",
                oldHeight, newHeight
            ));
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number! Please enter a valid height.");
        }
        
        // Cancel the book signing
        event.setCancelled(true);
    }
}