package com.github.kaspiandev.antipopup.paper.compat.packetevents;

import java.util.concurrent.Callable;

/**
 * Linkage-only replacement for PacketEvents' hard-coded version chart.
 */
public final class NoOpSimplePie extends NoOpChart {

    public NoOpSimplePie(String chartId, Callable<String> valueSupplier) {
        // Intentionally empty: the chart is never collected or submitted.
    }
}
