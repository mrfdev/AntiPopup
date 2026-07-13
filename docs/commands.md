# Commands

AntiPopup registers `/antipopup`; subcommands are case-insensitive.

| Command | Sender | Description |
| --- | --- | --- |
| `/antipopup` | Player/console | Shows the same overview as `/antipopup info`. |
| `/antipopup info` | Player/console | Shows the purpose, installed candidate version, starting command, and clickable canonical documentation URL. |
| `/antipopup setup` | Local server console only | Sets `enforce-secure-profile=false` and requests a native Paper restart after five seconds only when a change is needed. |
| `/antipopup reload` | Local server console only | Reloads and atomically publishes the current configuration snapshot. |

Unknown subcommands return a hint to use `/antipopup info`.

## Setup Safety

`setup` accepts only Paper's local `ConsoleCommandSender`. Players, command
blocks, and RCON are rejected even if they hold `antipopup.commands`.

`properties-location` must resolve inside the Paper server directory. The writer
preserves unrelated lines, comments, line endings, and POSIX permissions when
available. It uses an atomic replacement where the filesystem supports it, but
does not create an explicit backup.

## Reload Scope

Reloaded `block-chat-reports` and `show-popup` values are used by subsequent
packets. The reloaded properties path is used by subsequent setup operations.
Metrics and the logger filter are enable-time features and require a restart to
be added or removed. See [Configuration](configuration.md).
