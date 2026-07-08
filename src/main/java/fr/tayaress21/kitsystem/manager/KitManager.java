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

            // Lecture dynamique des items depuis la configuration
            java.util.List<ItemStack> kitItems = new java.util.ArrayList<>();
            java.util.List<Map<?, ?>> itemMaps = section.getMapList("items");
            
            for (Map<?, ?> itemMap : itemMaps) {
                // Lecture du matériel (On s'assure qu'il existe bien)
                Object matObj = itemMap.get("material");
                if (matObj == null) continue; 
                String matName = matObj.toString();
                
                // Lecture sécurisée de la quantité avec une valeur par défaut de 1
                int amount = 1;
                Object amountObj = itemMap.get("amount");
                if (amountObj instanceof Integer) {
                    amount = (Integer) amountObj;
                }
                
                Material mat = Material.getMaterial(matName.toUpperCase());
                if (mat != null) {
                    kitItems.add(new ItemStack(mat, amount));
                } else {
                    logger.warning("Materiel inconnu dans le kit " + kitId + " : " + matName);
                }
            }
            
            ItemStack[] items = kitItems.toArray(new ItemStack[0]);

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