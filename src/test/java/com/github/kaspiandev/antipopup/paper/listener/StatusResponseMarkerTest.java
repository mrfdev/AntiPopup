package com.github.kaspiandev.antipopup.paper.listener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusResponseMarkerTest {

    @Test
    void addsMarkerWithoutDroppingPaperStatusFields() {
        String marked = StatusResponseMarker.markPreventsChatReports(
                "{\"description\":\"1MoreBlock\",\"players\":{\"max\":100}} ");
        JsonObject response = JsonParser.parseString(marked).getAsJsonObject();

        assertTrue(response.get("preventsChatReports").getAsBoolean());
        assertEquals("1MoreBlock", response.get("description").getAsString());
        assertEquals(100, response.getAsJsonObject("players").get("max").getAsInt());
    }

    @Test
    void replacesAFalseMarkerWithTrue() {
        JsonObject response = JsonParser.parseString(
                StatusResponseMarker.markPreventsChatReports(
                        "{\"preventsChatReports\":false}"))
                .getAsJsonObject();

        assertTrue(response.get("preventsChatReports").getAsBoolean());
    }
}
