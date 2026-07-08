package fr.tayaress21.kitsystem.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Modèle de données représentant un Kit.
 * Les instances de cette classe sont stockées en mémoire vive (RAM)
 * pour des performances optimales lors de la consultation.
 */
public class Kit {

    private final String id;
    private final String displayName;
    private final String permission;
    private final long cooldown;
    
    private final Material iconMaterial;
    private final String iconName;
    private final List<String> iconLore;
    
    private final ItemStack[] items;

    public Kit(String id, String displayName, String permission, long cooldown, Material iconMaterial, String iconName, List<String> iconLore, ItemStack[] items) {
        this.id = id;
        this.displayName = displayName;
        this.permission = permission;
        this.cooldown = cooldown;
        this.iconMaterial = iconMaterial;
        this.iconName = iconName;
        this.iconLore = iconLore;
        this.items = items;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getPermission() { return permission; }
    public long getCooldown() { return cooldown; }
    public Material getIconMaterial() { return iconMaterial; }
    public String getIconName() { return iconName; }
    public List<String> getIconLore() { return iconLore; }
    public ItemStack[] getItems() { return items; }
}