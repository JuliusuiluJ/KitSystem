package fr.tayaress21.kitsystem.manager;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import fr.tayaress21.kitsystem.model.Kit;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire responsable de la lecture du fichier de configuration (config.yml)
 * et du chargement dynamique des kits en mémoire vive (RAM).
 * Cette approche évite de lire le fichier à chaque fois qu'un joueur tape /kit.
 */
public class KitManager {
    
    private final KitSystemPlugin plugin;
    
    // Stockage en mémoire des kits (Clé = ID du kit, Valeur = L'objet Kit)
    private final Map<String, Kit> kits = new HashMap<>();

    /**
     * Constructeur du gestionnaire de kits.
     * @param plugin L'instance principale du plugin pour accéder au Logger et autres composants.
     */
    public KitManager(KitSystemPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Lit le fichier YAML et peuple la mémoire avec les kits configurés.
     * Cette méthode est appelée au démarrage du serveur et lors de la commande /kit reload.
     * * @param config La configuration chargée depuis le fichier config.yml.
     */
    public void loadKits(FileConfiguration config) {
        kits.clear(); // Réinitialisation essentielle pour éviter les doublons lors d'un reload
        
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");
        if (kitsSection == null) {
            plugin.getLogger().warning("Aucune section 'kits' trouvée dans le config.yml !");
            return;
        }

        // Parcours de chaque kit défini dans le YAML (ex: "guerrier", "mineur", "archer")
        for (String kitId : kitsSection.getKeys(false)) {
            ConfigurationSection section = kitsSection.getConfigurationSection(kitId);
            if (section == null) continue;

            // 1. Lecture des propriétés de base du kit
            String displayName = section.getString("display-name", "<white>" + kitId);
            String permission = section.getString("permission", "kitsystem.kit." + kitId);
            long cooldown = section.getLong("cooldown", 0);

            // 2. Lecture de l'icône destinée à l'interface graphique (GUI)
            String materialName = section.getString("gui-icon.material", "CHEST").toUpperCase();
            Material material = Material.getMaterial(materialName);
            if (material == null) {
                plugin.getLogger().warning("Materiau de l'icône invalide pour le kit " + kitId + " : " + materialName);
                material = Material.CHEST; // Valeur de secours pour éviter un crash de l'interface
            }
            
            String iconName = section.getString("gui-icon.name", displayName);
            List<String> iconLore = section.getStringList("gui-icon.lore");

            // 3. Lecture dynamique du contenu réel du kit (les items donnés au joueur)
            List<ItemStack> kitItems = new ArrayList<>();
            List<Map<?, ?>> itemMaps = section.getMapList("items");
            
            for (Map<?, ?> itemMap : itemMaps) {
                // Lecture sécurisée du nom du matériel
                Object matObj = itemMap.get("material");
                if (matObj == null) continue; 
                String matName = matObj.toString();
                
                // Lecture sécurisée de la quantité (fallback à 1 si l'admin a mal configuré)
                int amount = 1;
                Object amountObj = itemMap.get("amount");
                if (amountObj instanceof Integer) {
                    amount = (Integer) amountObj;
                }
                
                Material mat = Material.getMaterial(matName.toUpperCase());
                if (mat != null) {
                    ItemStack item = new ItemStack(mat, amount);

                    // --- Lecture et application des enchantements ---
                    if (itemMap.containsKey("enchantments") && itemMap.get("enchantments") instanceof Map) {
                        Map<?, ?> enchantsMap = (Map<?, ?>) itemMap.get("enchantments");
                        for (Map.Entry<?, ?> entry : enchantsMap.entrySet()) {
                            String enchantName = entry.getKey().toString().toLowerCase();
                            int level = entry.getValue() instanceof Integer ? (Integer) entry.getValue() : 1;
                            
                            //  Utilisation du Registry moderne pour récupérer l'enchantement
                            Enchantment enchant = RegistryAccess.registryAccess()
                                    .getRegistry(RegistryKey.ENCHANTMENT)
                                    .get(NamespacedKey.minecraft(enchantName));

                            if (enchant != null) {
                                item.addUnsafeEnchantment(enchant, level);
                            } else {
                                plugin.getLogger().warning("Enchantement inconnu dans le kit " + kitId + " : " + enchantName);
                            }
                        }
                    }
                    
                    kitItems.add(item);
                } else {
                    plugin.getLogger().warning("Materiau d'item inconnu dans le kit " + kitId + " : " + matName);
                }
            }
            
            ItemStack[] items = kitItems.toArray(new ItemStack[0]);

            // 4. Instanciation du modèle et sauvegarde en mémoire
            Kit kit = new Kit(kitId, displayName, permission, cooldown, material, iconName, iconLore, items);
            kits.put(kitId, kit);
        }
        
        // Confirmation dans la console du serveur
        plugin.getLogger().info(kits.size() + " kit(s) chargé(s) avec succès !");
    }

    /**
     * Récupère la liste des kits chargés en mémoire.
     * @return Une Map contenant les identifiants des kits et leurs objets associés.
     */
    public Map<String, Kit> getKits() {
        return kits;
    }
}