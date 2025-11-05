package dev.tserato.coupsquad.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tserato.coupsquad.RTPGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPGUICommand {
    private final RTPGUI plugin;

    public RTPGUICommand(RTPGUI plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("rtpgui")
            .then(
                Commands.literal("reload")
                    .requires(source -> source.getSender().hasPermission("rtpgui.reload"))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        CommandSender sender = source.getSender();

                        plugin.reloadConfiguration();
                        sender.sendMessage(ChatColor.GREEN + "RTPGUI configuration reloaded successfully!");

                        return Command.SINGLE_SUCCESS;
                    })
            )
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                CommandSender sender = source.getSender();

                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    return Command.SINGLE_SUCCESS;
                }

                plugin.openRTPGUI((Player) sender);
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }
}
