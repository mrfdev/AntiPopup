package com.github.kaspiandev.antipopup.paper.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;

public final class PacketEventsListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Never resurrect a packet cancelled by an earlier moderation, vanish,
        // or privacy listener. HIGHEST priority lets those listeners decide first.
        if (event.isCancelled()
                || event.getPacketType() != PacketType.Play.Server.JOIN_GAME) {
            return;
        }

        WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
        wrapper.setEnforcesSecureChat(true);
    }
}
