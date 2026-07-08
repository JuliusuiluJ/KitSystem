package fr.tayaress21.kitsystem.manager;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final KitSystemPlugin plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessageManager(KitSystemPlugin plugin) {
        this.plugin = plugin;
        reloadMessages();
    }

    // Charge ou crée le fichier messages.yml
    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    // Récupère un message simple (avec le préfixe inclus)
    public Component getMessage(String path) {
        String prefix = messagesConfig.getString("prefix", "<gray>[Kit]</gray> ");
        String message = messagesConfig.getString(path, "<red>Message introuvable : " + path + "</red>");
        return MiniMessage.miniMessage().deserialize(prefix + message);
    }

    // Récupère un message et remplace une variable (ex: le temps ou le nom du kit)
    public Component getMessage(String path, String placeholder, String replacement) {
        String prefix = messagesConfig.getString("prefix", "<gray>[Kit]</gray> ");
        String message = messagesConfig.getString(path, "<red>Message introuvable : " + path + "</red>");
        
        message = message.replace(placeholder, replacement);
        
        return MiniMessage.miniMessage().deserialize(prefix + message);
    }

    // Récupère un composant formaté sans le préfixe (pour les noms d'items)
    public Component getRawMessage(String path, String placeholder, String replacement) {
        String message = messagesConfig.getString(path, "<red>Erreur: " + path + "</red>");
        message = message.replace(placeholder, replacement);
        return MiniMessage.miniMessage().deserialize(message);
    }

    // Récupère la chaîne de caractères brute (pour remplacer dans le lore)
    public String getRawString(String path) {
        return messagesConfig.getString(path, "<red>Erreur: " + path + "</red>");
    }
}