package fr.tayaress21.kitsystem;

import fr.tayaress21.kitsystem.command.KitCommand;
import fr.tayaress21.kitsystem.gui.GUIListener;
import fr.tayaress21.kitsystem.manager.KitManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class KitSystemPlugin extends JavaPlugin {

    // On déclare notre manager pour y avoir accès partout
    private KitManager kitManager;

    @Override
    public void onEnable() {
        // 1. Sauvegarde et chargement de la configuration
        saveDefaultConfig(); // Crée config.yml dans le dossier du plugin s'il n'existe pas
        
        // 2. Initialisation du KitManager
        this.kitManager = new KitManager(getLogger());
        this.kitManager.loadKits(getConfig()); // On lui passe la configuration chargée

        // 3. Messages de démarrage
        MiniMessage mm = MiniMessage.miniMessage();
        Component successMessage = mm.deserialize("<gradient:#00ff00:#00aa00><bold>KitSystem activé avec succès !</bold></gradient>");
        getServer().getConsoleSender().sendMessage(successMessage);
        
        // 4. Enregistrement de la commande
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("kit", "Commande principale du KitSystem", new KitCommand(this));
        });

        // 5. Enregistrement des événements
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    @Override
    public void onDisable() {
        MiniMessage mm = MiniMessage.miniMessage();
        Component disableMessage = mm.deserialize("<red>KitSystem a été désactivé.</red>");
        getServer().getConsoleSender().sendMessage(disableMessage);
    }
    
    // Permet de récupérer le manager depuis d'autres classes
    public KitManager getKitManager() {
        return kitManager;
    }
}