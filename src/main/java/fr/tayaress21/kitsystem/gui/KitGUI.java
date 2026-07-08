package fr.tayaress21.kitsystem.gui;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import fr.tayaress21.kitsystem.model.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire gérant la construction et l'ouverture du menu des kits.
 * Génère l'inventaire dynamiquement en fonction de l'état du joueur (cooldowns).
 */
public class KitGUI {

    /**
     * Construit et ouvre l'inventaire pour un joueur spécifique.
     * @param player Le joueur ciblé.
     * @param plugin L'instance principale du plugin.
     */
    public static void open(Player player, KitSystemPlugin plugin) {
        MiniMessage mm = MiniMessage.miniMessage();
        
        // Configuration de base du menu
        String titleString = plugin.getConfig().getString("gui.title", "<dark_gray>Choix de Kit</dark_gray>");
        Component title = mm.deserialize(titleString);
        int size = plugin.getConfig().getInt("gui.size", 27);

        // Utilisation d'un Holder personnalisé pour identifier notre GUI de manière sécurisée
        Inventory inventory = Bukkit.createInventory(new KitSystemHolder(), size, title);

        int slot = 0;
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            
            // 1. Vérification de la disponibilité du kit pour ce joueur
            boolean isOnCooldown = plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), kit.getId());
            long remainingSeconds = isOnCooldown ? plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId(), kit.getId()) : 0;

            // 2. Construction de l'icône
            ItemStack icon = new ItemStack(kit.getIconMaterial());
            ItemMeta meta = icon.getItemMeta();
            
            if (meta != null) {
                // Modification visuelle du nom (Grisé et barré si en cooldown)
                if (isOnCooldown) {
                    String cleanName = mm.stripTags(kit.getIconName()); // Retire les couleurs natives
                    meta.displayName(plugin.getMessageManager().getRawMessage("gui-cooldown-name", "<kit_name>", cleanName));
                } else {
                    meta.displayName(mm.deserialize(kit.getIconName()));
                }
                
                // Modification dynamique de la description (Lore)
                List<Component> lore = new ArrayList<>();
                for (String line : kit.getIconLore()) {
                    
                    // Si le kit est en cooldown, on masque la ligne invitant à cliquer
                    if (isOnCooldown && line.contains("<action_format>")) {
                        continue; 
                    }

                    String formattedLine = line;
                    
                    if (isOnCooldown) {
                        String timeFormat = plugin.getMessageManager().getRawString("gui-cooldown-time").replace("<time>", String.valueOf(remainingSeconds));
                        formattedLine = formattedLine.replace("<cooldown_format>", timeFormat);
                    } else {
                        String readyFormat = plugin.getMessageManager().getRawString("gui-ready");
                        formattedLine = formattedLine.replace("<cooldown_format>", readyFormat);
                        
                        // Si prêt, on remplace la balise d'action par le texte configuré
                        String actionFormat = plugin.getMessageManager().getRawString("gui-action-ready");
                        formattedLine = formattedLine.replace("<action_format>", actionFormat);
                    }
                    
                    lore.add(mm.deserialize(formattedLine));
                }
                meta.lore(lore);

                // 3. Injection sécurisée de l'ID du kit dans les métadonnées de l'item (PDC)
                // Évite de baser la reconnaissance du clic sur le nom de l'item
                meta.getPersistentDataContainer().set(plugin.kitKey, PersistentDataType.STRING, kit.getId());
                icon.setItemMeta(meta);
            }

            inventory.setItem(slot, icon);
            slot++; 
            if (slot >= size) break; // Sécurité contre le dépassement de taille de l'inventaire
        }

        // 4. Ouverture du menu pour le joueur
        player.openInventory(inventory);
    }
}