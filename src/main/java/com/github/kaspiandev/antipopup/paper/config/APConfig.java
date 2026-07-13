package com.github.kaspiandev.antipopup.paper.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class APConfig {

    private final JavaPlugin plugin;
    private volatile PluginSettings settings;

    public APConfig(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        refreshSnapshot();
    }

    public PluginSettings settings() {
        return settings;
    }

    public void reload() {
        plugin.reloadConfig();
        refreshSnapshot();
    }

    public boolean isFirstRun() {
        return plugin.getConfig().getBoolean("first-run", true);
    }

    public void setFirstRun(boolean value) {
        setAndSave("first-run", value);
    }

    public boolean isAskBstats() {
        return plugin.getConfig().getBoolean("ask-bstats", true);
    }

    public void setAskBstats(boolean value) {
        setAndSave("ask-bstats", value);
    }

    private void refreshSnapshot() {
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
        settings = PluginSettings.from(config);
    }

    private void setAndSave(String path, boolean value) {
        plugin.getConfig().set(path, value);
        plugin.saveConfig();
    }
}
