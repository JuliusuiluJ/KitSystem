package fr.tayaress21.kitsystem.command;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import fr.tayaress21.kitsystem.gui.KitGUI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class KitCommand implements BasicCommand {

    // On stocke l'instance de notre plugin
    private final KitSystemPlugin plugin;

    // Constructeur exigé pour passer l'instance
    public KitCommand(KitSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Seul un joueur en jeu peut executer cette commande.</red>"));
            return;
        }

        // Si aucune sous-commande n'est tapée (juste /kit)
        if (args.length == 0) {
            // Magie : on ouvre le GUI !
            KitGUI.open(player, plugin);
        }
    }
}