package fr.tayaress21.kitsystem;

import fr.tayaress21.kitsystem.command.KitCommand;
import fr.tayaress21.kitsystem.gui.GUIListener;
import fr.tayaress21.kitsystem.manager.CooldownManager;
import fr.tayaress21.kitsystem.manager.KitManager;
import fr.tayaress21.kitsystem.manager.MessageManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class KitSystemPlugin extends JavaPlugin {

    // Clé unique optimisée pour le PersistentDataContainer
    public NamespacedKey kitKey;

    // Gestionnaires (Managers)
    private KitManager kitManager;
    private CooldownManager cooldownManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Initialisation de la clé d'identification des items du GUI
        this.kitKey = new NamespacedKey(this, "kit_id");

        // Sauvegarde de la configuration par défaut (config.yml) si inexistante
        saveDefaultConfig();

        // 1. Initialisation des Managers
        this.messageManager = new MessageManager(this);
        this.kitManager = new KitManager(this);
        this.cooldownManager = new CooldownManager();

        // Chargement initial des kits en mémoire
        this.kitManager.loadKits(getConfig());

        // 2. Enregistrement des événements (Listeners)
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        // 3. Enregistrement de la commande via la nouvelle API Lifecycle de Paper 1.21
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("kit", "Commande principale du KitSystem", new KitCommand(this));
        });

        // Message de succès dans la console
        getServer().getConsoleSender().sendMessage(messageManager.getMessage("prefix").append(
                net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<green>Plugin activé avec succès !</green>")
        ));
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>KitSystem a été désactivé.</red>"));
    }

    // --- Getters pour l'injection de dépendances ---
    public KitManager getKitManager() { return kitManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public MessageManager getMessageManager() { return messageManager; }
}