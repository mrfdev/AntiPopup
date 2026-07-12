package com.github.kaspiandev.antipopup.spigot;

import com.github.kaspiandev.antipopup.config.APConfig;
import com.github.kaspiandev.antipopup.spigot.api.Api;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getLogger;

public class CommandRegister implements CommandExecutor {

    static final String DOCS_URL = "https://docs.1moreblock.com/custom-server-plugins/antipopup/";

    private final APConfig config;
    private final String version;

    public CommandRegister(APConfig config, String version) {
        this.config = config;
        this.version = version;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (args.length == 0
                || (args.length == 1 && "info".equalsIgnoreCase(args[0]))) {
            sendInfo(sender);
            return true;
        }

        if (args.length == 1
                && ("setup".equalsIgnoreCase(args[0])
                    || "reload".equalsIgnoreCase(args[0]))) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This subcommand can only be run from the local server console.");
                return true;
            }

            if ("setup".equalsIgnoreCase(args[0])) {
                Api.setupAntiPopup(100, false);
            } else {
                config.reload();
                getLogger().info("Config has been reloaded.");
            }
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Use /antipopup info for AntiPopup help and documentation.");
        return true;
    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "AntiPopup" + ChatColor.GRAY + " v" + version);
        sender.sendMessage(ChatColor.WHITE + "Helps remove the unsafe-server popup and limit vanilla chat reporting.");
        sender.sendMessage(ChatColor.YELLOW + "Start here: " + ChatColor.WHITE + "/antipopup info");

        TextComponent label = new TextComponent("Documentation: ");
        label.setColor(ChatColor.YELLOW);

        TextComponent link = new TextComponent(DOCS_URL);
        link.setColor(ChatColor.AQUA);
        link.setUnderlined(true);
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DOCS_URL));
        sender.spigot().sendMessage(label, link);
    }

}
