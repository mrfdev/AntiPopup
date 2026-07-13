package com.github.kaspiandev.antipopup.paper.listener;

import com.github.retrooper.packetevents.protocol.chat.filter.FilterMaskType;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import net.kyori.adventure.text.Component;

import java.util.Optional;

final class ChatReportTransformer {

    private ChatReportTransformer() {
    }

    static Optional<Replacement> transform(ChatMessage_v1_19_3 message) {
        if (message.getFilterMask() == null
                || message.getFilterMask().getType() != FilterMaskType.PASS_THROUGH) {
            // Preserve Paper's original per-recipient filtering packet. Replacing it
            // with unfiltered system chat would expose content hidden by the server.
            return Optional.empty();
        }

        Component content = message.getUnsignedChatContent()
                .orElseGet(message::getChatContent);
        return Optional.of(new Replacement(content, message.getChatFormatting()));
    }

    record Replacement(Component content, ChatType.Bound formatting) {
    }
}
