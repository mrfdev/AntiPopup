package com.github.kaspiandev.antipopup.paper.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginSettingsTest {

    @Test
    void loadsDefaultsIntoAnImmutableSnapshot() {
        YamlConfiguration config = new YamlConfiguration();
        PluginSettings settings = PluginSettings.from(config);

        assertFalse(settings.bstats());
        assertTrue(settings.filterNotSecure());
        assertFalse(settings.autoSetup());
        assertTrue(settings.blockChatReports());
        assertFalse(settings.showPopup());
        assertEquals("server.properties", settings.propertiesLocation());
    }

    @Test
    void normalizesSafeRelativePropertiesPath() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("properties-location", "config/../server.properties");

        assertEquals("server.properties", PluginSettings.from(config).propertiesLocation());
    }

    @Test
    void rejectsPathsOutsideThePaperServerDirectory() {
        YamlConfiguration absolute = new YamlConfiguration();
        absolute.set("properties-location", "/tmp/server.properties");
        YamlConfiguration traversal = new YamlConfiguration();
        traversal.set("properties-location", "../server.properties");

        assertThrows(IllegalArgumentException.class, () -> PluginSettings.from(absolute));
        assertThrows(IllegalArgumentException.class, () -> PluginSettings.from(traversal));
    }
}
