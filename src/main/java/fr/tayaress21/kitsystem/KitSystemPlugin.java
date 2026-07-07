package fr.tayaress21.kitsystem;

import fr.tayaress21.kitsystem.command.KitCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class KitSystemPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        MiniMessage mm = MiniMessage.miniMessage();
        Component successMessage = mm.deserialize("<gradient:#00ff00:#00aa00><bold>KitSystem activé avec succès !</bold></gradient>");
        getServer().getConsoleSender().sendMessage(successMessage);
        
        // Enregistrement ultra-propre de la commande avec l'API BasicCommand
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("kit", "Commande principale du KitSystem", new KitCommand());
        });
    }

    @Override
    public void onDisable() {
        MiniMessage mm = MiniMessage.miniMessage();
        Component disableMessage = mm.deserialize("<red>KitSystem a été désactivé.</red>");
        getServer().getConsoleSender().sendMessage(disableMessage);
    }
}