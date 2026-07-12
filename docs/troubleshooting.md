# Troubleshooting

## The Plugin Will Not Load

Check all three items:

1. The server is Paper 26.2.
2. The runtime is Java 25 or the separately verified Java 26.0.1.
3. Only one AntiPopup jar is present in `plugins/`.

`UnsupportedClassVersionError` indicates an older Java runtime; this build uses
Java 25 class-file version 69.

`No valid injector found for the server version!` means PacketEvents did not
detect exact server version `26.2` while `block-chat-reports` was enabled.
Other Paper/Minecraft versions are outside this fork's scope.

## The Popup or Reportable Chat Returned

- Confirm `show-popup: false`.
- Confirm `block-chat-reports: true`.
- Restart the server after changing `block-chat-reports`.
- Look for `[AntiPopup] Hooked on 26.2` during startup.
- Record the client version and whether ViaVersion, ViaBackwards, ViaRewind, or
  Geyser is translating its protocol.

`show-popup` is read dynamically and can be refreshed with
`/antipopup reload`; injector changes require a restart.

## Info Is Not Available to a Player

`antipopup.commands` defaults to everyone. Check whether a permissions plugin
explicitly denies it. The command should return the installed version and a
clickable link to:

```text
https://docs.1moreblock.com/custom-server-plugins/antipopup/
```

## Setup or Reload Does Nothing

Both operations require Bukkit's local server console. They intentionally reject
players, command blocks, and remote-console senders.

`setup` only requests a restart when `enforce-secure-profile` was true and
needed to be changed. Verify `properties-location` if it cannot find the
expected file.

## A Reloaded Setting Did Not Take Effect

Only `show-popup` and `send-header` are read dynamically by the packet
listener. Restart after changing startup-bound settings such as
`block-chat-reports`, `clickable-urls`, `filter-not-secure`, metrics, or
the properties path.

## Chat Formatting or Another Chat Plugin Breaks

Keep `clickable-urls: false` unless the full chat stack has been tested. When
enabled, AntiPopup takes over Bukkit chat delivery and can conflict with another
formatter.

## Java 26 Prints a Reflection Warning

The captured terminal output from the verified Java 26.0.1 smoke test logged a
final-field reflection warning from the bundled packet stack, but AntiPopup
still enabled, selected the Paper 26.2
injector, reached Paper's ready state, and shut down cleanly. Treat a warning
separately from an actual enable failure and retain the complete startup log when
reporting it.

## Configuration Problems After an Update

1. Stop the server.
2. Back up `plugins/AntiPopup/config.yml`.
3. Compare it with the defaults documented in
   [Configuration](configuration.md).
4. Keep `config-version` under BoostedYAML's control.
5. Start again and inspect the complete AntiPopup startup section.

For reproducible Paper 26.2 issues, use the
[mrfdev/AntiPopup issue tracker](https://github.com/mrfdev/AntiPopup/issues) and
include the Paper build, Java version, AntiPopup version, plugin list,
configuration with credentials, tokens, private addresses, and other private
values removed, plus a similarly sanitized complete log link.
