package fr.tayaress21.kitsystem.gui;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import fr.tayaress21.kitsystem.model.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class KitGUI {

    public static void open(Player player, KitSystemPlugin plugin) {
        MiniMessage mm = MiniMessage.miniMessage();
        String titleString = plugin.getConfig().getString("gui.title", "<dark_gray>Choix de Kit</dark_gray>");
        Component title = mm.deserialize(titleString);
        int size = plugin.getConfig().getInt("gui.size", 27);

        // MODIFICATION 1 : On assigne le KitSystemHolder à la création
        Inventory inventory = Bukkit.createInventory(new KitSystemHolder(), size, title);

        int slot = 0;
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            ItemStack icon = new ItemStack(kit.getIconMaterial());
            ItemMeta meta = icon.getItemMeta();
            
            if (meta != null) {
                meta.displayName(mm.deserialize(kit.getIconName()));
                List<Component> lore = new ArrayList<>();
                for (String line : kit.getIconLore()) {
                    String formattedLine = line.replace("<cooldown_format>", kit.getCooldown() + "s");
                    lore.add(mm.deserialize(formattedLine));
                }
                meta.lore(lore);

                // MODIFICATION 2 : On cache l'ID du kit dans l'item (invisible pour le joueur)
                NamespacedKey key = new NamespacedKey(plugin, "kit_id");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, kit.getId());

                icon.setItemMeta(meta);
            }

            inventory.setItem(slot, icon);
            slot++; 
            if (slot >= size) break; 
        }

        player.openInventory(inventory);
    }
}