package com.github.kaspiandev.antipopup.paper.listener;

import com.github.kaspiandev.antipopup.paper.config.PluginSettings;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerResponse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class PacketEventsListener implements PacketListener {

    private final Supplier<PluginSettings> settingsSupplier;
    private final Logger logger;
    private final AtomicBoolean unknownChatPacketLogged = new AtomicBoolean();
    private final AtomicBoolean statusMutationFailureLogged = new AtomicBoolean();

    public PacketEventsListener(Supplier<PluginSettings> settingsSupplier, Logger logger) {
        this.settingsSupplier = settingsSupplier;
        this.logger = logger;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Never resurrect a packet cancelled by an earlier moderation, vanish,
        // or privacy listener. HIGHEST priority lets those listeners decide first.
        if (event.isCancelled()) {
            return;
        }

        PacketTypeCommon packetType = event.getPacketType();
        PluginSettings settings = settingsSupplier.get();

        if (packetType == PacketType.Status.Server.RESPONSE) {
            if (!settings.blockChatReports()) {
                // Do not erase a marker owned by Paper or another plugin.
                return;
            }
            ClientVersion clientVersion = event.getUser().getClientVersion();
            if (clientVersion.isNewerThan(ClientVersion.V_1_18_2)) {
                WrapperStatusServerResponse wrapper = new WrapperStatusServerResponse(event);
                try {
                    // Avoid PacketEvents' component serializer here: this payload is
                    // a server-status document, not an Adventure Component document.
                    wrapper.setComponentJson(StatusResponseMarker.markPreventsChatReports(
                            wrapper.getComponentJson()));
                } catch (RuntimeException exception) {
                    if (statusMutationFailureLogged.compareAndSet(false, true)) {
                        logger.warning("Left an unreadable server-status response unchanged: "
                                + exception.getMessage());
                    }
                }
            }
            return;
        }

        if (packetType == PacketType.Play.Server.SERVER_DATA) {
            ClientVersion clientVersion = event.getUser().getClientVersion();
            if (clientVersion.isOlderThan(ClientVersion.V_1_20_5)
                    && !settings.showPopup()) {
                WrapperPlayServerServerData wrapper = new WrapperPlayServerServerData(event);
                wrapper.setEnforceSecureChat(true);
            }
            return;
        }

        if (packetType == PacketType.Play.Server.JOIN_GAME) {
            ClientVersion clientVersion = event.getUser().getClientVersion();
            if (clientVersion.isNewerThan(ClientVersion.V_1_20_3)
                    && !settings.showPopup()) {
                WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
                wrapper.setEnforcesSecureChat(true);
            }
            return;
        }

        if (packetType == PacketType.Play.Server.CHAT_MESSAGE
                && settings.blockChatReports()) {
            ChatMessage message = new WrapperPlayServerChatMessage(event).getMessage();
            if (!(message instanceof ChatMessage_v1_19_3 modernMessage)) {
                if (unknownChatPacketLogged.compareAndSet(false, true)) {
                    logger.warning("Left an unknown chat packet unchanged; update PacketEvents before certifying this Paper version.");
                }
                return;
            }

            ChatReportTransformer.transform(modernMessage).ifPresent(replacement -> {
                event.setCancelled(true);
                event.getUser().sendPacketSilently(new WrapperPlayServerDisguisedChat(
                        replacement.content(), replacement.formatting()));
            });
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_SESSION_UPDATE
                && settingsSupplier.get().blockChatReports()) {
            event.setCancelled(true);
        }
    }
}
