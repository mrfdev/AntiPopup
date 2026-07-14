package com.github.kaspiandev.antipopup.paper;

import com.github.kaspiandev.antipopup.paper.listener.PacketEventsListener;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
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
    }

    @Override
    public void onDisable() {
        if (packetEventsLoaded) {
            PacketEvents.getAPI().terminate();
            packetEventsLoaded = false;
            getLogger().info("Disabled embedded PacketEvents.");
        }
    }

}
