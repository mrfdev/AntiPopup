package com.github.kaspiandev.antipopup.spigot;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRegisterTest {

    @Test
    void infoShowsVersionCommandAndClickableDocumentation() {
        RecordingSender recording = new RecordingSender();
        CommandRegister command = new CommandRegister(null, "13.2-test");

        boolean handled = command.onCommand(
                recording.sender(), null, "antipopup", new String[] { "InFo" });

        assertTrue(handled);
        assertTrue(recording.plainMessages().stream().anyMatch(message -> message.contains("AntiPopup")));
        assertTrue(recording.plainMessages().stream().anyMatch(message -> message.contains("13.2-test")));
        assertTrue(recording.plainMessages().stream().anyMatch(message -> message.contains("/antipopup info")));

        assertEquals(1, recording.componentMessages().size());
        BaseComponent[] components = recording.componentMessages().getFirst();
        assertEquals(2, components.length);
        assertEquals(CommandRegister.DOCS_URL, components[1].toPlainText());
        assertNotNull(components[1].getClickEvent());
        assertEquals(ClickEvent.Action.OPEN_URL, components[1].getClickEvent().getAction());
        assertEquals(CommandRegister.DOCS_URL, components[1].getClickEvent().getValue());
    }

    @Test
    void noArgumentsShowInfo() {
        RecordingSender recording = new RecordingSender();
        CommandRegister command = new CommandRegister(null, "13.2-test");

        assertTrue(command.onCommand(recording.sender(), null, "antipopup", new String[0]));
        assertTrue(recording.plainMessages().stream().anyMatch(message -> message.contains("AntiPopup")));
    }

    @Test
    void playerCannotRunConsoleSubcommands() {
        for (String subcommand : List.of("setup", "reload")) {
            RecordingSender recording = new RecordingSender();
            CommandRegister command = new CommandRegister(null, "13.2-test");

            assertTrue(command.onCommand(
                    recording.sender(), null, "antipopup", new String[] { subcommand }));
            assertTrue(recording.plainMessages().stream()
                                .anyMatch(message -> message.contains("local server console")));
        }
    }

    @Test
    void pluginMetadataKeepsInfoPublicAndLinksCanonicalDocs() throws Exception {
        InputStream resource = CommandRegisterTest.class.getClassLoader().getResourceAsStream("plugin.yml");
        assertNotNull(resource);

        PluginDescriptionFile description = new PluginDescriptionFile(
                new InputStreamReader(resource, StandardCharsets.UTF_8));
        Permission commandPermission = description.getPermissions().stream()
                                                  .filter(permission -> permission.getName()
                                                                                  .equals("antipopup.commands"))
                                                  .findFirst()
                                                  .orElseThrow();

        assertEquals(PermissionDefault.TRUE, commandPermission.getDefault());
        assertEquals(CommandRegister.DOCS_URL, description.getWebsite());
    }

    private static final class RecordingSender {

        private final List<String> plainMessages = new ArrayList<>();
        private final List<BaseComponent[]> componentMessages = new ArrayList<>();
        private final CommandSender sender;

        private RecordingSender() {
            CommandSender.Spigot spigot = new CommandSender.Spigot() {
                @Override
                public void sendMessage(BaseComponent... components) {
                    componentMessages.add(components);
                }
            };
            sender = (CommandSender) Proxy.newProxyInstance(
                    CommandSender.class.getClassLoader(),
                    new Class<?>[] { CommandSender.class },
                    (proxy, method, args) -> switch (method.getName()) {
                        case "sendMessage" -> {
                            if (args != null) {
                                for (Object argument : args) {
                                    if (argument instanceof String message) {
                                        plainMessages.add(message);
                                    } else if (argument instanceof String[] messages) {
                                        plainMessages.addAll(List.of(messages));
                                    }
                                }
                            }
                            yield null;
                        }
                        case "spigot" -> spigot;
                        case "getName" -> "TestPlayer";
                        case "hasPermission", "isPermissionSet", "isOp" -> true;
                        case "toString" -> "RecordingCommandSender";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> defaultValue(method.getReturnType());
                    });
        }

        private CommandSender sender() {
            return sender;
        }

        private List<String> plainMessages() {
            return plainMessages;
        }

        private List<BaseComponent[]> componentMessages() {
            return componentMessages;
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == char.class) return '\0';
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0F;
            if (type == double.class) return 0D;
            return null;
        }
    }
}
