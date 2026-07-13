package com.github.kaspiandev.antipopup.paper;

import com.github.kaspiandev.antipopup.paper.command.CommandRegister;
import com.github.kaspiandev.antipopup.paper.config.APConfig;
import com.github.kaspiandev.antipopup.paper.config.PluginSettings;
import com.github.kaspiandev.antipopup.paper.listener.PacketEventsListener;
import com.github.kaspiandev.antipopup.paper.log.LogFilter;
import com.github.kaspiandev.antipopup.paper.message.ConsoleMessages;
import com.github.kaspiandev.antipopup.paper.service.ServerPropertiesService;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AntiPopup extends JavaPlugin {

    private APConfig config;
    private ServerPropertiesService serverProperties;
    private LoggerContext loggerContext;
    private LoggerConfig rootLoggerConfig;
    private LogFilter logFilter;
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
        config = new APConfig(this);
        serverProperties = new ServerPropertiesService(this, config);
        PluginSettings settings = config.settings();

        if (settings.bstats()) {
            new Metrics(this, 16308);
            getLogger().info("Loaded optional anonymous metrics.");
        }

        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketEventsListener(config::settings, getLogger()),
                PacketListenerPriority.HIGHEST);
        PacketEvents.getAPI().init();
        getLogger().info("Initiated embedded PacketEvents for Paper "
                + PacketEvents.getAPI().getServerManager().getVersion().getReleaseName() + ".");

        PluginCommand command = getCommand("antipopup");
        if (command == null) {
            throw new IllegalStateException("The antipopup command is missing from plugin.yml");
        }
        command.setExecutor(new CommandRegister(getPluginMeta().getVersion(), new CommandActions()));
        getLogger().info("Commands registered.");

        if (settings.filterNotSecure()) {
            installLogFilter();
        }

        getServer().getScheduler().runTaskLater(this, this::runStartupChecks, 5L);
    }

    @Override
    public void onDisable() {
        if (rootLoggerConfig != null && logFilter != null) {
            rootLoggerConfig.removeFilter(logFilter);
            loggerContext.updateLoggers();
            logFilter.stop();
            loggerContext = null;
            rootLoggerConfig = null;
            logFilter = null;
            getLogger().info("Logger filter removed.");
        }

        if (packetEventsLoaded) {
            PacketEvents.getAPI().terminate();
            packetEventsLoaded = false;
            getLogger().info("Disabled embedded PacketEvents.");
        }
    }

    private void installLogFilter() {
        loggerContext = (LoggerContext) LogManager.getContext(false);
        rootLoggerConfig = loggerContext.getConfiguration().getRootLogger();
        logFilter = new LogFilter();
        logFilter.start();
        rootLoggerConfig.addFilter(logFilter);
        loggerContext.updateLoggers();
        getLogger().info("Logger filter enabled.");
    }

    private void runStartupChecks() {
        if (config.settings().autoSetup()) {
            serverProperties.setup(80L, true);
        }

        if (config.isFirstRun()) {
            try {
                if (serverProperties.isSecureProfilesEnforced()) {
                    ConsoleMessages.log(ConsoleMessages.ASK_SETUP, getLogger()::warning);
                }
            } catch (IOException exception) {
                getLogger().warning("Could not inspect server.properties: " + exception.getMessage());
            }
            config.setFirstRun(false);
        }

        if (config.isAskBstats()) {
            ConsoleMessages.log(ConsoleMessages.ASK_BSTATS, getLogger()::warning);
            config.setAskBstats(false);
        }
    }

    private final class CommandActions implements CommandRegister.Actions {
        @Override
        public void setup() {
            serverProperties.setup(100L, false);
        }

        @Override
        public void reload() {
            config.reload();
            getLogger().info("Config has been reloaded; packet settings now use the new snapshot.");
        }
    }
}
