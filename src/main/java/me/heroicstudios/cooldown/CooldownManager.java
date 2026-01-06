package me.heroicstudios.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    
    public void setCooldown(UUID playerId, long durationMs) {
        cooldowns.put(playerId, System.currentTimeMillis() + durationMs);
    }
    
    public boolean isOnCooldown(UUID playerId) {
        Long expiry = cooldowns.get(playerId);
        if (expiry == null) {
            return false;
        }
        
        if (System.currentTimeMillis() >= expiry) {
            cooldowns.remove(playerId);
            return false;
        }
        
        return true;
    }
    
    public long getRemainingCooldown(UUID playerId) {
        Long expiry = cooldowns.get(playerId);
        if (expiry == null) {
            return 0;
        }
        
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public void removeCooldown(UUID playerId) {
        cooldowns.remove(playerId);
    }
    
    public void clearAll() {
        cooldowns.clear();
    }
}