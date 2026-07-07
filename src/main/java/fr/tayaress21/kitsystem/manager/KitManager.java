package fr.tayaress21.kitsystem.manager;

import fr.tayaress21.kitsystem.model.Kit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class KitManager {
    
    // Stockage en mémoire vive des kits (Clé = ID du kit, Valeur = L'objet Kit)
    private final Map<String, Kit> kits = new HashMap<>();
    private final Logger logger;

    public KitManager(Logger logger) {
        this.logger = logger;
    }

    public void loadKits(FileConfiguration config) {
        kits.clear(); // On vide la liste (utile pour la commande /kit reload)
        
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");
        if (kitsSection == null) {
            logger.warning("Aucune section 'kits' trouvée dans le config.yml !");
            return;
        }

        // On parcourt chaque kit configuré (ex: "guerrier")
        for (String kitId : kitsSection.getKeys(false)) {
            ConfigurationSection section = kitsSection.getConfigurationSection(kitId);
            if (section == null) continue;

            String displayName = section.getString("display-name", "<white>" + kitId);
            String permission = section.getString("permission", "kitsystem.kit." + kitId);
            long cooldown = section.getLong("cooldown", 0);

            // Lecture de l'icône GUI
            String materialName = section.getString("gui-icon.material", "CHEST").toUpperCase();
            Material material = Material.getMaterial(materialName);
            if (material == null) {
                logger.warning("Materiau invalide pour le kit " + kitId + " : " + materialName);
                material = Material.CHEST; // Valeur de secours
            }
            
            String iconName = section.getString("gui-icon.name", displayName);
            List<String> iconLore = section.getStringList("gui-icon.lore");

            // TODO : On verra comment lire les vrais items (l'inventaire) un peu plus tard
            ItemStack[] items = new ItemStack[0];

            // Création et sauvegarde du kit en mémoire
            Kit kit = new Kit(kitId, displayName, permission, cooldown, material, iconName, iconLore, items);
            kits.put(kitId, kit);
        }
        
        logger.info(kits.size() + " kit(s) chargé(s) avec succès !");
    }

    public Map<String, Kit> getKits() {
        return kits;
    }
}