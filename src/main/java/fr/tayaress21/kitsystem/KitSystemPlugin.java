package fr.tayaress21.kitsystem;

import fr.tayaress21.kitsystem.command.KitCommand;
import fr.tayaress21.kitsystem.manager.CooldownManager;
import fr.tayaress21.kitsystem.manager.KitManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class KitSystemPlugin extends JavaPlugin {

    private KitManager kitManager;
    private CooldownManager cooldownManager; // <-- NOUVEAU

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.kitManager = new KitManager(getLogger());
        this.kitManager.loadKits(getConfig());
        
        this.cooldownManager = new CooldownManager(); // <-- NOUVEAU

        MiniMessage mm = MiniMessage.miniMessage();
        Component successMessage = mm.deserialize("<gradient:#00ff00:#00aa00><bold>KitSystem activé avec succès !</bold></gradient>");
        getServer().getConsoleSender().sendMessage(successMessage);
        
        getServer().getPluginManager().registerEvents(new fr.tayaress21.kitsystem.gui.GUIListener(this), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("kit", "Commande principale du KitSystem", new KitCommand(this));
        });
    }

    @Override
    public void onDisable() {
        MiniMessage mm = MiniMessage.miniMessage();
        Component disableMessage = mm.deserialize("<red>KitSystem a été désactivé.</red>");
        getServer().getConsoleSender().sendMessage(disableMessage);
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}