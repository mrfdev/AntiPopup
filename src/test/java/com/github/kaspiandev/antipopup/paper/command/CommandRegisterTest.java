package com.github.kaspiandev.antipopup.paper.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRegisterTest {

    @Test
    void noArgumentsShowsPaperCandidateInfoAndClickableDocs() {
        RecordingActions actions = new RecordingActions();
        RecordingSender sender = RecordingSender.commandSender();
        CommandRegister command = new CommandRegister("13.2-paper-only.1", actions);

        assertTrue(command.onCommand(sender.proxy(), null, "antipopup", new String[0]));

        String output = sender.plainOutput();
        assertTrue(output.contains("AntiPopup v13.2-paper-only.1"));
        assertTrue(output.contains("/antipopup info"));
        assertTrue(output.contains(CommandRegister.DOCS_URL));
        ClickEvent<?> clickEvent = sender.components().stream()
                .map(CommandRegisterTest::findClickEvent)
                .filter(event -> event != null)
                .findFirst()
                .orElse(null);
        assertNotNull(clickEvent);
        ClickEvent.Payload.Text payload = assertInstanceOf(
                ClickEvent.Payload.Text.class, clickEvent.payload());
        assertEquals(CommandRegister.DOCS_URL, payload.value());
    }

    @Test
    void infoIsCaseInsensitive() {
        RecordingSender sender = RecordingSender.commandSender();
        CommandRegister command = new CommandRegister("test", new RecordingActions());

        assertTrue(command.onCommand(sender.proxy(), null, "antipopup", new String[]{"InFo"}));
        assertTrue(sender.plainOutput().contains("Documentation:"));
    }

    @Test
    void playerCannotRunAdministrativeActions() {
        RecordingActions actions = new RecordingActions();
        RecordingSender sender = RecordingSender.commandSender();
        CommandRegister command = new CommandRegister("test", actions);

        command.onCommand(sender.proxy(), null, "antipopup", new String[]{"setup"});
        command.onCommand(sender.proxy(), null, "antipopup", new String[]{"reload"});

        assertEquals(0, actions.setupCalls);
        assertEquals(0, actions.reloadCalls);
        assertTrue(sender.plainOutput().contains("local server console"));
    }

    @Test
    void localConsoleCanRunBothAdministrativeActions() {
        RecordingActions actions = new RecordingActions();
        RecordingSender sender = RecordingSender.consoleSender();
        CommandRegister command = new CommandRegister("test", actions);

        command.onCommand(sender.proxy(), null, "antipopup", new String[]{"setup"});
        command.onCommand(sender.proxy(), null, "antipopup", new String[]{"reload"});

        assertEquals(1, actions.setupCalls);
        assertEquals(1, actions.reloadCalls);
    }

    private static ClickEvent<?> findClickEvent(Component component) {
        if (component.clickEvent() != null) {
            return component.clickEvent();
        }
        for (Component child : component.children()) {
            ClickEvent<?> result = findClickEvent(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static final class RecordingActions implements CommandRegister.Actions {
        private int setupCalls;
        private int reloadCalls;

        @Override
        public void setup() {
            setupCalls++;
        }

        @Override
        public void reload() {
            reloadCalls++;
        }
    }

    private record RecordingSender(CommandSender proxy, List<Component> components) {

        static RecordingSender commandSender() {
            return create(CommandSender.class);
        }

        static RecordingSender consoleSender() {
            return create(ConsoleCommandSender.class);
        }

        private static RecordingSender create(Class<? extends CommandSender> type) {
            List<Component> messages = new ArrayList<>();
            CommandSender proxy = (CommandSender) Proxy.newProxyInstance(
                    type.getClassLoader(), new Class<?>[]{type}, (ignored, method, args) -> {
                        if ("sendMessage".equals(method.getName()) && args != null) {
                            for (Object argument : args) {
                                if (argument instanceof Component component) {
                                    messages.add(component);
                                }
                            }
                        }
                        if ("getName".equals(method.getName())) {
                            return "test-sender";
                        }
                        Class<?> returnType = method.getReturnType();
                        if (returnType == boolean.class) {
                            return false;
                        }
                        if (returnType == int.class) {
                            return 0;
                        }
                        return null;
                    });
            return new RecordingSender(proxy, messages);
        }

        String plainOutput() {
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            return components.stream()
                    .map(serializer::serialize)
                    .reduce("", (left, right) -> left + "\n" + right);
        }
    }
}
