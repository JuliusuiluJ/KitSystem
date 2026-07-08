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

        // 1. Sous-commande : /kit reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("kitsystem.admin")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Vous n'avez pas la permission d'utiliser cette commande.</red>"));
                return;
            }
            // On recharge le fichier sur le disque puis on met à jour la RAM
            plugin.reloadConfig();
            plugin.getKitManager().loadKits(plugin.getConfig());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Configuration des kits rechargée avec succès !</green>"));
            return;
        }

        // 2. Commande principale : /kit (Ouverture du menu)
        if (args.length == 0) {
            if (!player.hasPermission("kitsystem.use")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Vous n'avez pas accès aux kits.</red>"));
                return;
            }
            KitGUI.open(player, plugin);
        }
    }
}