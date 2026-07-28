package com.github.kaspiandev.antipopup.paper;

import com.github.kaspiandev.antipopup.paper.listener.PacketEventsListener;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiPopup extends JavaPlugin {

    private boolean packetEventsLoaded;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().debug(false).checkForUpdates(false);
        PacketEvents.getAPI().load();
        packetEventsLoaded = true;
        getLogger().info("Loaded embedded PacketEvents.");
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketEventsListener(),
                PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().init();
        getLogger().info("Initiated embedded PacketEvents for Paper "
                + PacketEvents.getAPI().getServerManager().getVersion().getReleaseName() + ".");
        logRuntimeMetadata();
    }

    @Override
    public void onDisable() {
        if (packetEventsLoaded) {
            PacketEvents.getAPI().terminate();
            packetEventsLoaded = false;
            getLogger().info("Disabled embedded PacketEvents.");
        }
    }

    private void logRuntimeMetadata() {
        Properties metadata = new Properties();
        try (InputStream input = getResource("META-INF/antipopup-release.properties")) {
            if (input == null) {
                throw new IllegalStateException("Embedded release metadata is missing.");
            }
            metadata.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read embedded release metadata.", exception);
        }

        getLogger().info("AntiPopup " + getPluginMeta().getVersion()
                + " [artifact=" + metadata.getProperty("artifactName")
                + ", compiledPaperApi=" + metadata.getProperty("paperApiVersion")
                + ", javaTarget=" + metadata.getProperty("javaTarget")
                + "] running on " + getServer().getVersion()
                + " with Java " + Runtime.version() + ".");
    }

}
