package com.github.kaspiandev.antipopup.paper.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public final class LogFilter extends AbstractFilter {

    private static final String MINECRAFT_LOGGER = "net.minecraft.server.MinecraftServer";
    private static final String NOT_SECURE = "[Not Secure]";

    @Override
    public Filter.Result filter(LogEvent event) {
        if (event.getLevel() != Level.INFO
                || !MINECRAFT_LOGGER.equals(event.getLoggerName())) {
            return Filter.Result.NEUTRAL;
        }

        String message = event.getMessage().getFormattedMessage();
        String cleaned = removeNotSecureMarker(message);
        if (cleaned.equals(message)) {
            return Filter.Result.NEUTRAL;
        }

        LogManager.getLogger(event.getLoggerName())
                .log(event.getLevel(), cleaned);
        return Filter.Result.DENY;
    }

    static String removeNotSecureMarker(String message) {
        return message.replace(NOT_SECURE + " ", "").replace(NOT_SECURE, "");
    }
}
