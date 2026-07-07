package fr.tayaress21.kitsystem.gui;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    private final KitSystemPlugin plugin;

    public GUIListener(KitSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 1. Si ce n'est pas notre GUI, on ne fait rien
        if (!(event.getInventory().getHolder() instanceof KitSystemHolder)) {
            return;
        }

        // 2. On annule l'événement : impossible de déplacer ou voler l'item !
        event.setCancelled(true);

        // 3. On récupère le joueur et l'item cliqué
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;
        ItemMeta meta = clickedItem.getItemMeta();

        // 4. On lit l'ID du kit caché dans l'item
        NamespacedKey key = new NamespacedKey(plugin, "kit_id");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String kitId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            
            // Pour l'instant, on ferme le menu et on envoie un message de confirmation
            player.closeInventory();
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Vous avez sélectionné le kit : <white>" + kitId + "</white> !</green>"));
            
            // TODO: Ajouter la logique d'attribution des items et du cooldown
        }
    }
}