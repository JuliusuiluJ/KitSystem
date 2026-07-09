# KitSystem

Plugin de gestion de kits conçu pour Paper 1.21+. Il permet de créer et distribuer des kits personnalisés via des fichiers de configuration, avec une interface graphique qui s'adapte dynamiquement à l'état du joueur.

## Fonctionnalités

* **GUI Dynamique** : Le menu génère dynamiquement les items en filtrant les kits selon les permissions du joueur. Les kits en cooldown sont identifiés visuellement (icône grisée, texte barré) et affichent le temps restant calculé à l'ouverture de l'interface.
* **Sécurité** : L'identification des items cliqués utilise le `PersistentDataContainer` (PDC), évitant les failles de duplication basées sur la lecture du nom de l'item.
* **Personnalisation** : Support natif de l'API MiniMessage pour la coloration textuelle. L'intégralité des messages et paramètres est externalisée (`config.yml` et `messages.yml`).
* **Optimisation** : Le stockage des données de configuration et des cooldowns s'effectue en mémoire vive (RAM) pour garantir une exécution instantanée sans requêtes de lecture répétées.

## Installation

1. Placez `KitSystem-1.0.0.jar` dans le dossier `plugins/` de votre serveur.
2. Redémarrez le serveur pour générer la configuration par défaut.
3. Configurez vos permissions via un plugin tiers (ex: LuckPerms).

## Commandes et Permissions

| Commande | Action | Permission requise |
| :--- | :--- | :--- |
| `/kit` | Ouvre l'interface de sélection | `kitsystem.use` |
| `/kit <id>` | Attribue un kit sans passer par le menu | `kitsystem.kit.<id>` |
| `/kit reload` | Recharge la configuration en mémoire | `kitsystem.admin` |

*Note : Pour qu'un joueur puisse voir et utiliser un kit, il doit posséder la permission `kitsystem.kit.<id_du_kit>` correspondante.*

## Configuration (Exemple)

La structure d'un kit dans le fichier `config.yml` permet de définir ses attributs, son apparence dans le menu et son contenu.

```yaml
kits:
  guerrier:
    display-name: "<red><bold>Kit Guerrier</bold></red>"
    permission: "kitsystem.kit.guerrier"
    cooldown: 3600 # Valeur en secondes
    gui-icon:
      material: DIAMOND_SWORD
      name: "<red>Guerrier</red>"
      lore:
        - "<gray>Équipement de mêlée.</gray>"
        - ""
        - "<yellow>Cooldown : <cooldown_format></yellow>"
        - "<action_format>"
    items:
      - material: DIAMOND_SWORD
        amount: 1
        enchantments:
          sharpness: 3
          unbreaking: 2
      - material: COOKED_BEEF
        amount: 16