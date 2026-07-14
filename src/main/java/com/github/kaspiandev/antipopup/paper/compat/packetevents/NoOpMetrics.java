package com.github.kaspiandev.antipopup.paper.compat.packetevents;

import org.bukkit.plugin.Plugin;

/**
 * Inert replacement for PacketEvents' hard-coded metrics bootstrap.
 */
public final class NoOpMetrics {

    public NoOpMetrics(Plugin plugin, int serviceId) {
        // PacketEvents 2.13 does not honor its bStats(false) setting. Shadow
        // redirects that constructor here so no telemetry service is started.
    }

    public void addCustomChart(NoOpChart chart) {
        // Intentionally empty: there is no metrics service to receive charts.
    }
}
