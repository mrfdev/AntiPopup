package com.github.kaspiandev.antipopup.paper.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogFilterTest {

    @Test
    void removesMarkerAndFollowingSpace() {
        assertEquals("<Alex> hello",
                LogFilter.removeNotSecureMarker("[Not Secure] <Alex> hello"));
    }

    @Test
    void removesMarkerWithoutFollowingSpace() {
        assertEquals("message ",
                LogFilter.removeNotSecureMarker("message [Not Secure]"));
    }

    @Test
    void leavesUnrelatedMessagesUntouched() {
        assertEquals("ordinary message",
                LogFilter.removeNotSecureMarker("ordinary message"));
    }
}
