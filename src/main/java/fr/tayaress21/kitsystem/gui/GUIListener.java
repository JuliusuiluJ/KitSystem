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
        if (!(event.getInventory().getHolder() instanceof KitSystemHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;
        ItemMeta meta = clickedItem.getItemMeta();

        NamespacedKey key = new NamespacedKey(plugin, "kit_id");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String kitId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            
            // 1. Récupérer l'objet Kit en mémoire
            fr.tayaress21.kitsystem.model.Kit kit = plugin.getKitManager().getKits().get(kitId);
            if (kit == null) return;

            // Vérification de la permission spécifique au kit
            if (!player.hasPermission(kit.getPermission())) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Vous n'avez pas la permission de récupérer ce kit.</red>"));
                player.closeInventory();
                return;
            }

            // 2. Vérification du Cooldown
            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), kitId)) {
                long remaining = plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId(), kitId);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Ce kit est en rechargement. Veuillez patienter <bold>" + remaining + "s</bold>.</red>"));
                player.closeInventory();
                return;
            }

            // 3. Attribution des items
            // La méthode addItem retourne les items qui n'ont pas pu rentrer dans l'inventaire
            java.util.HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kit.getItems());
            boolean hasDroppedItems = false;
            
            // 4. Gestion de l'inventaire plein (Drop au sol)
            for (ItemStack itemToDrop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
                hasDroppedItems = true;
            }

            // 5. Envoi des messages de succès
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Vous avez reçu le kit : </green>" + kit.getDisplayName()));
            if (hasDroppedItems) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow><bold>Attention:</bold> Votre inventaire était plein, certains objets sont tombés au sol.</yellow>"));
            }

            // 6. Application du Cooldown
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), kitId, kit.getCooldown());
            
            player.closeInventory();
        }
    }
}