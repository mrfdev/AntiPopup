package com.github.kaspiandev.antipopup.paper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerPropertiesServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void updatesOnlyThePropertyAndPreservesCommentsAndOrder() throws Exception {
        Path file = temporaryDirectory.resolve("server.properties");
        String original = "# custom heading\r\nmotd=1MoreBlock\r\n"
                + "enforce-secure-profile = true\r\nmax-players=100\r\n";
        Files.writeString(file, original, StandardCharsets.ISO_8859_1);

        assertEquals(ServerPropertiesService.UpdateResult.CHANGED,
                ServerPropertiesService.disableSecureProfiles(file));

        String updated = Files.readString(file, StandardCharsets.ISO_8859_1);
        assertEquals("# custom heading\r\nmotd=1MoreBlock\r\n"
                + "enforce-secure-profile = false\r\nmax-players=100\r\n", updated);
        assertFalse(ServerPropertiesService.isSecureProfilesEnforced(file));
    }

    @Test
    void leavesAnAlreadyDisabledFileByteForByteUnchanged() throws Exception {
        Path file = temporaryDirectory.resolve("server.properties");
        String original = "motd=1MoreBlock\nenforce-secure-profile=false\n";
        Files.writeString(file, original, StandardCharsets.ISO_8859_1);

        assertEquals(ServerPropertiesService.UpdateResult.UNCHANGED,
                ServerPropertiesService.disableSecureProfiles(file));
        assertEquals(original, Files.readString(file, StandardCharsets.ISO_8859_1));
    }

    @Test
    void addsThePropertyWhenItIsMissing() throws Exception {
        Path file = temporaryDirectory.resolve("server.properties");
        Files.writeString(file, "motd=1MoreBlock", StandardCharsets.ISO_8859_1);

        assertTrue(ServerPropertiesService.isSecureProfilesEnforced(file));
        assertEquals(ServerPropertiesService.UpdateResult.CHANGED,
                ServerPropertiesService.disableSecureProfiles(file));
        assertEquals("motd=1MoreBlock\nenforce-secure-profile=false\n",
                Files.readString(file, StandardCharsets.ISO_8859_1));
    }

    @Test
    void readsTheLastEffectivePropertyValue() throws Exception {
        Path file = temporaryDirectory.resolve("server.properties");
        Files.writeString(file,
                "enforce-secure-profile=false\nenforce-secure-profile=true\n",
                StandardCharsets.ISO_8859_1);

        assertTrue(ServerPropertiesService.isSecureProfilesEnforced(file));
    }
}
