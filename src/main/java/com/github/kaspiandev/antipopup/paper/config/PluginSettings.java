package com.github.kaspiandev.antipopup.paper.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.Objects;

public record PluginSettings(
        boolean bstats,
        boolean filterNotSecure,
        boolean autoSetup,
        boolean blockChatReports,
        boolean showPopup,
        String propertiesLocation
) {

    static PluginSettings from(FileConfiguration config) {
        Objects.requireNonNull(config, "config");

        String propertiesLocation = Objects.requireNonNullElse(
                config.getString("properties-location"), "server.properties").trim();
        Path relativePath = Path.of(propertiesLocation).normalize();
        if (propertiesLocation.isEmpty()
                || relativePath.isAbsolute()
                || relativePath.startsWith("..")) {
            throw new IllegalArgumentException(
                    "properties-location must stay inside the Paper server directory");
        }

        return new PluginSettings(
                config.getBoolean("bstats", false),
                config.getBoolean("filter-not-secure", true),
                config.getBoolean("auto-setup", false),
                config.getBoolean("block-chat-reports", true),
                config.getBoolean("show-popup", false),
                relativePath.toString()
        );
    }
}
