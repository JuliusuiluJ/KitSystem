package fr.tayaress21.kitsystem.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // Structure : L'UUID du joueur -> (L'ID du kit -> Timestamp d'expiration en millisecondes)
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    // Vérifie si le joueur doit encore attendre
    public boolean isOnCooldown(UUID playerId, String kitId) {
        if (!cooldowns.containsKey(playerId)) return false;
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (!playerCooldowns.containsKey(kitId)) return false;

        // Le joueur est en cooldown si le temps actuel est inférieur au temps d'expiration
        return System.currentTimeMillis() < playerCooldowns.get(kitId);
    }

    // Récupère le temps restant en secondes
    public long getRemainingSeconds(UUID playerId, String kitId) {
        if (!isOnCooldown(playerId, kitId)) return 0;
        long expirationTime = cooldowns.get(playerId).get(kitId);
        return (expirationTime - System.currentTimeMillis()) / 1000L;
    }

    // Applique un nouveau cooldown
    public void setCooldown(UUID playerId, String kitId, long durationInSeconds) {
        if (durationInSeconds <= 0) return; // Pas de cooldown configuré
        
        long expirationTime = System.currentTimeMillis() + (durationInSeconds * 1000L);
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>()).put(kitId, expirationTime);
    }
}