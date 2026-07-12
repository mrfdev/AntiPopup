# Configuration

AntiPopup creates `plugins/AntiPopup/config.yml` from its bundled defaults.
BoostedYAML automatically updates the file using `config-version`.

## Settings

| Setting | Default | Applied | Verified behavior |
| --- | ---: | --- | --- |
| `config-version` | `33` | Internal | Schema marker used by BoostedYAML. Do not edit it manually. |
| `bstats` | `false` | Restart | Starts bStats metrics ID 16308 when enabled. |
| `filter-not-secure` | `true` | Restart | Installs a Log4j filter that removes `[Not Secure]` from matching MinecraftServer INFO log messages. |
| `send-header` | `false` | Reload | When false, cancels outgoing `PLAYER_CHAT_HEADER` packets. |
| `auto-setup` | `false` | Next startup | Runs the server-properties setup shortly after enable and may request a restart. |
| `block-chat-reports` | `true` | Restart | Installs the Paper 26.2 player-chat packet injector. |
| `show-popup` | `false` | Reload | When false, changes applicable secure-chat flags so clients do not show the popup. |
| `clickable-urls` | `false` | Restart | Registers the Bukkit chat listener that resends messages with sender identity. It can conflict with other chat plugins. |
| `properties-location` | `server.properties` | Restart | Path resolved from the parent of Bukkit's world container and used by setup and first-run checks. |
| `first-run` | `true` | Internal | Shows the setup reminder once, then is saved as false. |
| `ask-bstats` | `true` | Internal | Shows the metrics reminder once, then is saved as false. |
| `setup-mode` | `true` | None | Retained legacy field; active Paper code does not read it. |

The code also recognizes an unshipped `experimental-mode` field. When manually
set to true it skips the Paper 26.2 chat injector. It is not part of this fork's
supported configuration and should be absent or false.

## Important Interactions

`block-chat-reports: false` prevents installation of the Paper 26.2 player-chat
injector only. It does not disable the status-response marker, popup flag changes,
header cancellation, or incoming chat-session cancellation performed by the
always-registered PacketEvents listener.

`clickable-urls: true` cancels normal Bukkit chat delivery at monitor priority,
manually sends the formatted message, and fires a follow-up event with no
recipients. Keep it false when another chat plugin owns message delivery unless
the combination has been tested.

## Reload and Restart

`/antipopup reload` reloads the YAML document. `show-popup` and `send-header`
are read for every relevant packet and can change immediately.

Use a full restart after changing `bstats`, `filter-not-secure`,
`auto-setup`, `block-chat-reports`, `clickable-urls`,
`properties-location`, or `experimental-mode`. Reload does not add or remove
listeners, player injectors, metrics, or the logger filter.

## Persistence and Migration

There is no database and no per-player data. Back up the complete
`plugins/AntiPopup/` folder before updates. BoostedYAML provides schema-based
automatic configuration updates, but the project contains no additional custom
migration process.
