package com.github.kaspiandev.antipopup.paper.message;

import java.util.Arrays;
import java.util.function.Consumer;

public final class ConsoleMessages {

    public static final String[] ASK_SETUP = {
            "----------------------[ READ ME ]----------------------",
            "This is your first startup with AntiPopup.",
            "Run command 'antipopup setup' to disable",
            "enforce-secure-profile for the intended experience.",
            "This will not force players to sign their messages.",
            "Thanks for using AntiPopup!",
            "-------------------------------------------------------"
    };

    public static final String[] ASK_BSTATS = {
            "------------------------[ READ ME ]------------------------",
            "This is your first startup with AntiPopup.",
            "You can opt in to anonymous bStats metrics in config.yml.",
            "Metrics remain disabled unless you explicitly enable them.",
            "Thanks for using AntiPopup! (you will not see this again)",
            "-----------------------------------------------------------"
    };

    public static final String[] SETUP_SUCCESS = {
            "-----------------[ READ ME ]-----------------",
            "Set enforce-secure-profile=false while preserving",
            "the rest of server.properties. Paper will restart shortly.",
            "---------------------------------------------"
    };

    private ConsoleMessages() {
    }

    public static void log(String[] lines, Consumer<String> consumer) {
        Arrays.stream(lines).forEach(consumer);
    }
}
