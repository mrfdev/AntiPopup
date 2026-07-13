package com.github.kaspiandev.antipopup.paper.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Objects;

public final class CommandRegister implements CommandExecutor {

    static final String DOCS_URL = "https://docs.1moreblock.com/custom-server-plugins/antipopup/";

    private final String version;
    private final Actions actions;

    public CommandRegister(String version, Actions actions) {
        this.version = Objects.requireNonNull(version, "version");
        this.actions = Objects.requireNonNull(actions, "actions");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0
                || (args.length == 1 && "info".equalsIgnoreCase(args[0]))) {
            sendInfo(sender);
            return true;
        }

        if (args.length == 1
                && ("setup".equalsIgnoreCase(args[0])
                || "reload".equalsIgnoreCase(args[0]))) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Component.text(
                        "This subcommand can only be run from the local server console.",
                        NamedTextColor.RED));
                return true;
            }

            if ("setup".equalsIgnoreCase(args[0])) {
                actions.setup();
            } else {
                actions.reload();
            }
            return true;
        }

        sender.sendMessage(Component.text(
                "Use /antipopup info for AntiPopup help and documentation.",
                NamedTextColor.YELLOW));
        return true;
    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage(Component.text("AntiPopup", NamedTextColor.GOLD)
                .append(Component.text(" v" + version, NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(
                "Helps remove the unsafe-server popup and limit vanilla chat reporting.",
                NamedTextColor.WHITE));
        sender.sendMessage(Component.text("Start here: ", NamedTextColor.YELLOW)
                .append(Component.text("/antipopup info", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Documentation: ", NamedTextColor.YELLOW)
                .append(Component.text(DOCS_URL, NamedTextColor.AQUA)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(DOCS_URL))));
    }

    public interface Actions {
        void setup();

        void reload();
    }
}
