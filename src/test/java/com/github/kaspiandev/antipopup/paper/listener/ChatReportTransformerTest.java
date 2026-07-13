package com.github.kaspiandev.antipopup.paper.listener;

import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.ChatTypeDecoration;
import com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
import com.github.retrooper.packetevents.protocol.chat.StaticChatType;
import com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_21_5;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatReportTransformerTest {

    @Test
    void preservesUnsignedPaperFormattingAndChatType() {
        Component unsigned = Component.text("formatted message");
        ChatMessage_v1_21_5 message = message(unsigned, FilterMask.PASS_THROUGH);

        ChatReportTransformer.Replacement transformed =
                ChatReportTransformer.transform(message).orElseThrow();

        assertEquals(unsigned, transformed.content());
        assertSame(message.getChatFormatting(), transformed.formatting());
    }

    @Test
    void fallsBackToPlainSignedContentWhenUnsignedContentIsAbsent() {
        ChatMessage_v1_21_5 message = message(null, FilterMask.PASS_THROUGH);

        ChatReportTransformer.Replacement transformed =
                ChatReportTransformer.transform(message).orElseThrow();

        assertEquals(Component.text("plain message"), transformed.content());
    }

    @Test
    void neverExposesFullyOrPartiallyFilteredContent() {
        ChatMessage_v1_21_5 fullyFiltered = message(null, FilterMask.FULLY_FILTERED);
        ChatMessage_v1_21_5 partiallyFiltered = message(null, new FilterMask(new BitSet()));

        assertTrue(ChatReportTransformer.transform(fullyFiltered).isEmpty());
        assertTrue(ChatReportTransformer.transform(partiallyFiltered).isEmpty());
    }

    private static ChatMessage_v1_21_5 message(Component unsigned, FilterMask filterMask) {
        ChatTypeDecoration decoration = ChatTypeDecoration.withSender("chat.type.text");
        ChatType type = new StaticChatType(decoration, null);
        ChatType.Bound bound = new ChatType.Bound(type, Component.text("Alice"), null);
        return new ChatMessage_v1_21_5(
                0,
                UUID.randomUUID(),
                0,
                new byte[0],
                "plain message",
                Instant.EPOCH,
                0L,
                new LastSeenMessages.Packed(List.of()),
                unsigned,
                filterMask,
                bound
        );
    }
}
