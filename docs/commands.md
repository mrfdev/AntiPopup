# Commands

AntiPopup registers one command, `/antipopup`. Subcommands are matched
case-insensitively.

| Command | Sender | Description |
| --- | --- | --- |
| `/antipopup` | Player/console | Show the same overview as `/antipopup info`. |
| `/antipopup info` | Player/console | Show AntiPopup's purpose, installed version, starting command, and clickable canonical docs URL. |
| `/antipopup setup` | Local server console only | Set `enforce-secure-profile=false` in the configured properties file. If a change is made, terminate PacketEvents and request a server restart after five seconds. |
| `/antipopup reload` | Local server console only | Reload the YAML document and log confirmation. This does not reconstruct startup-created hooks, listeners, metrics, filters, or injectors. |

Unknown subcommands return a hint to use `/antipopup info`.

## Setup Safety

`/antipopup setup` is accepted only from Bukkit's local
`ConsoleCommandSender`. Players, command blocks, and remote-console senders do
not pass that check.

The command reads the path selected by `properties-location`. When
`enforce-secure-profile` is `true`, Java's properties writer stores the file
again with the value set to `false`; the plugin does not create its own backup.
Back up `server.properties` before running it. No restart is requested when the
property is already false.

## Reload Scope

`/antipopup reload` refreshes values from `config.yml`. The packet listener
reads `show-popup` and `send-header` for every applicable packet, so those
settings can take effect after reload. Settings that create or remove runtime
state at startup require a full server restart; see
[Configuration](configuration.md).
