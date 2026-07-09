package fr.tayaress21.kitsystem.command;

import fr.tayaress21.kitsystem.KitSystemPlugin;
import fr.tayaress21.kitsystem.gui.KitGUI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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
            sender.sendMessage(plugin.getMessageManager().getMessage("player-only"));
            return;
        }

        // 1. Sous-commande : /kit reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("kitsystem.admin")) {
                player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
                return;
            }
            
            plugin.reloadConfig();
            plugin.getKitManager().loadKits(plugin.getConfig());
            plugin.getMessageManager().reloadMessages();
            
            player.sendMessage(plugin.getMessageManager().getMessage("reload-success"));
            return;
        }

        // 2. Sous-commande : /kit <nom_du_kit> (Récupération directe)
        if (args.length == 1 && !args[0].equalsIgnoreCase("reload")) {
            String kitId = args[0].toLowerCase();
            fr.tayaress21.kitsystem.model.Kit kit = plugin.getKitManager().getKits().get(kitId);

            // Vérification de l'existence du kit
            if (kit == null) {
                player.sendMessage(plugin.getMessageManager().getMessage("kit-not-found"));
                return;
            }

            // Vérification de la permission
            if (!player.hasPermission(kit.getPermission())) {
                player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
                return;
            }

            // Vérification du cooldown
            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), kitId)) {
                long remaining = plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId(), kitId);
                player.sendMessage(plugin.getMessageManager().getMessage("cooldown-active", "<time>", String.valueOf(remaining)));
                return;
            }

            // Attribution des items (même logique que pour le GUI)
            java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> leftover = player.getInventory().addItem(kit.getItems());
            boolean hasDroppedItems = false;
            
            for (org.bukkit.inventory.ItemStack itemToDrop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
                hasDroppedItems = true;
            }

            // Messages de succès et application du Cooldown
            player.sendMessage(plugin.getMessageManager().getMessage("kit-received", "<kit_name>", kit.getDisplayName()));
            if (hasDroppedItems) {
                player.sendMessage(plugin.getMessageManager().getMessage("inventory-full"));
            }
            
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), kitId, kit.getCooldown());
            return;
        }

        // 3. Commande principale : /kit (Ouverture du menu)
        if (args.length == 0) {
            if (!player.hasPermission("kitsystem.use")) {
                player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
                return;
            }
            KitGUI.open(player, plugin);
        }
    }
}