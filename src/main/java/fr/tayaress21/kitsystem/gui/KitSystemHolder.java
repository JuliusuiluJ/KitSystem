package fr.tayaress21.kitsystem.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class KitSystemHolder implements InventoryHolder {
    // Cette classe sert uniquement d'étiquette pour identifier notre GUI
    @Override
    public @NotNull Inventory getInventory() {
        return null; 
    }
}