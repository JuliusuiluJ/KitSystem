package fr.tayaress21.kitsystem.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage") // Requis par l'API Paper 1.21
public class KitCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        
        // Récupérer l'entité qui a exécuté la commande
        CommandSender sender = stack.getSender();

        // 1. Vérifier si c'est un joueur
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Seul un joueur en jeu peut executer cette commande.</red>"));
            return;
        }

        // 2. Si aucune sous-commande n'est tapée (juste /kit)
        if (args.length == 0) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Pong ! La commande /kit fonctionne parfaitement avec la nouvelle API Paper !</green>"));
        }
    }
}