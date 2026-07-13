package com.github.kaspiandev.antipopup.paper.service;

import com.github.kaspiandev.antipopup.paper.config.APConfig;
import com.github.kaspiandev.antipopup.paper.message.ConsoleMessages;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerPropertiesService {

    private static final Pattern SECURE_PROFILE_PROPERTY = Pattern.compile(
            "(?m)^([\\t\\f ]*enforce-secure-profile[\\t\\f ]*=[\\t\\f ]*)([^\\r\\n]*)(\\r?)$");

    private final JavaPlugin plugin;
    private final APConfig config;
    private final Path serverRoot;

    public ServerPropertiesService(JavaPlugin plugin, APConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.serverRoot = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
    }

    public void setup(long restartDelayTicks, boolean silent) {
        Path propertiesFile = propertiesFile();
        try {
            UpdateResult result = disableSecureProfiles(propertiesFile);
            if (result == UpdateResult.CHANGED) {
                ConsoleMessages.log(ConsoleMessages.SETUP_SUCCESS, plugin.getLogger()::warning);
                plugin.getServer().getScheduler().runTaskLater(plugin,
                        () -> plugin.getServer().restart(), restartDelayTicks);
            } else if (!silent) {
                plugin.getLogger().info("AntiPopup is already set up; enforce-secure-profile is false.");
            }
        } catch (IOException | RuntimeException exception) {
            plugin.getLogger().severe(
                    "Could not update " + propertiesFile + ": " + exception.getMessage());
        }
    }

    public boolean isSecureProfilesEnforced() throws IOException {
        return isSecureProfilesEnforced(propertiesFile());
    }

    Path propertiesFile() {
        Path resolved = serverRoot.resolve(config.settings().propertiesLocation()).normalize();
        if (!resolved.startsWith(serverRoot)) {
            throw new IllegalStateException("properties-location escaped the Paper server directory");
        }
        return resolved;
    }

    public static boolean isSecureProfilesEnforced(Path file) throws IOException {
        String content = Files.readString(file, StandardCharsets.ISO_8859_1);
        Matcher matcher = SECURE_PROFILE_PROPERTY.matcher(content);
        boolean found = false;
        boolean enforced = false;
        while (matcher.find()) {
            found = true;
            enforced = Boolean.parseBoolean(matcher.group(2).trim());
        }
        // Paper's secure default applies when a trimmed/custom file omits the key.
        return !found || enforced;
    }

    public static UpdateResult disableSecureProfiles(Path file) throws IOException {
        String original = Files.readString(file, StandardCharsets.ISO_8859_1);
        Matcher matcher = SECURE_PROFILE_PROPERTY.matcher(original);
        StringBuffer updated = new StringBuffer(original.length() + 32);
        boolean found = false;
        boolean changed = false;

        while (matcher.find()) {
            found = true;
            if ("false".equalsIgnoreCase(matcher.group(2).trim())) {
                matcher.appendReplacement(updated, Matcher.quoteReplacement(matcher.group()));
                continue;
            }

            changed = true;
            String replacement = matcher.group(1) + "false" + matcher.group(3);
            matcher.appendReplacement(updated, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(updated);

        if (!found) {
            changed = true;
            String lineSeparator = original.contains("\r\n") ? "\r\n" : "\n";
            if (!original.isEmpty() && !original.endsWith("\n") && !original.endsWith("\r")) {
                updated.append(lineSeparator);
            }
            updated.append("enforce-secure-profile=false").append(lineSeparator);
        }

        if (!changed) {
            return UpdateResult.UNCHANGED;
        }

        writeAtomically(file, updated.toString());
        return UpdateResult.CHANGED;
    }

    private static void writeAtomically(Path file, String content) throws IOException {
        Path absolute = file.toAbsolutePath();
        Path parent = absolute.getParent();
        if (parent == null) {
            throw new IOException("server.properties has no parent directory");
        }

        Set<PosixFilePermission> permissions = null;
        try {
            permissions = Files.getPosixFilePermissions(absolute);
        } catch (UnsupportedOperationException ignored) {
            // The filesystem does not expose POSIX permissions.
        }

        Path temporary = Files.createTempFile(parent, ".antipopup-", ".properties");
        try {
            Files.writeString(temporary, content, StandardCharsets.ISO_8859_1,
                    StandardOpenOption.TRUNCATE_EXISTING);
            if (permissions != null) {
                Files.setPosixFilePermissions(temporary, permissions);
            }
            try {
                Files.move(temporary, absolute, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    public enum UpdateResult {
        CHANGED,
        UNCHANGED
    }
}
