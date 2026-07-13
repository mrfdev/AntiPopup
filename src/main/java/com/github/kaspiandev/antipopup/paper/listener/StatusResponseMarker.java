package com.github.kaspiandev.antipopup.paper.listener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final class StatusResponseMarker {

    private StatusResponseMarker() {
    }

    static String markPreventsChatReports(String json) {
        JsonObject response = JsonParser.parseString(json).getAsJsonObject();
        response.addProperty("preventsChatReports", true);
        return response.toString();
    }
}
