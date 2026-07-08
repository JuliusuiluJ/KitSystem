package fr.tayaress21.kitsystem.gui;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

/**
 * Écouteur d'événements dédié à l'interface graphique.
 * Intercepte les clics pour empêcher le vol d'items et déclencher l'attribution.
 */
public class GUIListener implements Listener {

    private final KitSystemPlugin plugin;

    public GUIListener(KitSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Sécurité 1 : S'assurer que le menu cliqué est bien le nôtre
        if (!(event.getInventory().getHolder() instanceof KitSystemHolder)) return;
        
        // Sécurité 2 : Annuler l'action pour empêcher le déplacement de l'item
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;
        ItemMeta meta = clickedItem.getItemMeta();

        // Lecture de l'ID du kit via le PersistentDataContainer
        if (meta.getPersistentDataContainer().has(plugin.kitKey, PersistentDataType.STRING)) {
            String kitId = meta.getPersistentDataContainer().get(plugin.kitKey, PersistentDataType.STRING);
            
            fr.tayaress21.kitsystem.model.Kit kit = plugin.getKitManager().getKits().get(kitId);
            if (kit == null) return;

            // --- VÉRIFICATIONS ---
            
            if (!player.hasPermission(kit.getPermission())) {
                player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
                player.closeInventory();
                return;
            }

            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), kitId)) {
                long remaining = plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId(), kitId);
                player.sendMessage(plugin.getMessageManager().getMessage("cooldown-active", "<time>", String.valueOf(remaining)));
                player.closeInventory();
                return;
            }

            // --- ATTRIBUTION ---

            // Tente d'ajouter les items. Les items en surplus sont retournés dans la HashMap.
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kit.getItems());
            boolean hasDroppedItems = false;
            
            for (ItemStack itemToDrop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
                hasDroppedItems = true;
            }

            // --- SUCCÈS & COOLDOWN ---

            player.sendMessage(plugin.getMessageManager().getMessage("kit-received", "<kit_name>", kit.getDisplayName()));
            if (hasDroppedItems) {
                player.sendMessage(plugin.getMessageManager().getMessage("inventory-full"));
            }
            
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), kitId, kit.getCooldown());
            player.closeInventory();
        }
    }
}