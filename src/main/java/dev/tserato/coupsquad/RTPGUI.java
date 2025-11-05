package dev.tserato.coupsquad;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import dev.tserato.coupsquad.commands.RTPGUICommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RTPGUI extends JavaPlugin implements Listener, CommandExecutor {
    private final Map<String, String> itemCommandMap = new HashMap<>();
    private FileConfiguration config;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand rtpCommand = getCommand("rtp");
        if (rtpCommand != null) {
            rtpCommand.setExecutor(this);
        }

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(new RTPGUICommand(this).createCommand(), "RTPGUI reload command");
        });

        loadCommands();
        getLogger().info("RTPGUI has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RTPGUI has been disabled!");
    }

    private void loadCommands() {
        itemCommandMap.clear();
        itemCommandMap.put("OVERWORLD_RTP", config.getString("commands.overworld", "betterrtp player %player% world"));
        itemCommandMap.put("NETHER_RTP", config.getString("commands.nether", "betterrtp player %player% world_nether"));
        itemCommandMap.put("END_RTP", config.getString("commands.end", "betterrtp player %player% world_the_end"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rtp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(translateColors("&cOnly players can use this command."));
                return true;
            }

            openRTPGUI((Player) sender);
            return true;
        }

        return false;
    }

    public void openRTPGUI(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder) null,
            config.getInt("gui.size", 27),
            translateColors(config.getString("gui.title", "&bRTP Menu")));

        gui.setItem(config.getInt("items.overworld.slot", 11),
            createItem(Material.valueOf(config.getString("items.overworld.material", "GRASS_BLOCK")),
                config.getString("items.overworld.display-name", "&aOverworld RTP"),
                "OVERWORLD_RTP",
                config.getStringList("items.overworld.lore").toArray(new String[0])));

        gui.setItem(config.getInt("items.nether.slot", 13),
            createItem(Material.valueOf(config.getString("items.nether.material", "NETHERRACK")),
                config.getString("items.nether.display-name", "&cNether RTP"),
                "NETHER_RTP",
                config.getStringList("items.nether.lore").toArray(new String[0])));

        gui.setItem(config.getInt("items.end.slot", 15),
            createItem(Material.valueOf(config.getString("items.end.material", "END_STONE")),
                config.getString("items.end.display-name", "&eEnd RTP"),
                "END_RTP",
                config.getStringList("items.end.lore").toArray(new String[0])));

        player.openInventory(gui);
    }

    public void reloadConfiguration() {
        reloadConfig();
        config = getConfig();
        loadCommands();
    }

    private ItemStack createItem(Material material, String displayName, String key, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(translateColors(displayName));
            meta.setLore(Arrays.stream(loreLines)
                .map(this::translateColors)
                .toList());
            NamespacedKey namespacedKey = new NamespacedKey(this, "rtp_key");
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, key);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player)) {
            return;
        }

        Player player = (Player) whoClicked;
        String guiTitle = translateColors(config.getString("gui.title", "&bRTP Menu"));

        if (!event.getView().getTitle().equals(guiTitle)) {
            return;
        }

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }

        NamespacedKey namespacedKey = new NamespacedKey(this, "rtp_key");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(namespacedKey, PersistentDataType.STRING)) {
            return;
        }

        String key = container.get(namespacedKey, PersistentDataType.STRING);
        if (key == null) {
            return;
        }

        String command = itemCommandMap.get(key);
        if (command == null) {
            return;
        }

        String finalCommand = command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);

        try {
            player.playSound(player.getLocation(),
                Sound.valueOf(config.getString("sound.click", "ENTITY_EXPERIENCE_ORB_PICKUP")),
                (float) config.getDouble("sound.volume", 1.0),
                (float) config.getDouble("sound.pitch", 1.0));
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid sound specified in config: " + config.getString("sound.click"));
        }

        player.closeInventory();
    }

    private String translateColors(String text) {
        if (text == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
