# Configuration

AntiPopup creates `plugins/AntiPopup/config.yml` with Paper's built-in YAML
configuration API. Missing defaults are copied into an existing file when the
plugin starts or `/antipopup reload` is run.

## Settings

| Setting | Default | Takes effect | Behavior |
| --- | ---: | --- | --- |
| `bstats` | `false` | Restart | Starts anonymous bStats metrics ID 16308 when explicitly enabled. |
| `filter-not-secure` | `true` | Restart | Installs a Log4j filter for the matching MinecraftServer INFO message. |
| `auto-setup` | `false` | Next startup | Checks `server.properties` shortly after enable and may request a restart. |
| `block-chat-reports` | `true` | Reload | Controls the status marker, incoming chat-session cancellation, and safe outgoing chat conversion. |
| `show-popup` | `false` | Reload | When false, adjusts applicable secure-chat flags so clients do not show the unsafe-server popup. |
| `properties-location` | `server.properties` | Reload | Relative path inside the Paper server directory used by setup and first-run checks. Absolute paths and `..` escapes are rejected. |
| `first-run` | `true` | Internal | Shows the secure-profile setup reminder once, then saves false. |
| `ask-bstats` | `true` | Internal | Shows the metrics reminder once, then saves false. |

Historical options such as `send-header`, `clickable-urls`, `setup-mode`, and
`experimental-mode` are not read by this branch. They can remain harmlessly in
an upgraded file or be removed after making a backup.

## Report Blocking and Filtering

With `block-chat-reports: true`, AntiPopup cancels chat-session updates and
converts pass-through player-chat packets to disguised chat. If Paper has
partially or fully filtered a message for a recipient, AntiPopup leaves Paper's
original packet unchanged so filtered content is never exposed.
Packets already cancelled by another listener are also left cancelled; AntiPopup
never sends a replacement for them.

With `block-chat-reports: false`, AntiPopup leaves chat sessions, player chat,
and the existing server-status marker unchanged. This avoids erasing a true
marker owned by Paper or another plugin. `show-popup` remains independent.

## Reload and Restart Scope

`/antipopup reload` publishes one immutable settings snapshot. Packet callbacks
then use the new `block-chat-reports` and `show-popup` values immediately, and
the next setup command uses the new properties path.

Restart after changing `bstats` or `filter-not-secure`, because their runtime
objects are created only during enable. `auto-setup` naturally runs only during
startup. Reload does not duplicate listeners, metrics, or logger filters.

## Persistence

There is no database or per-player data. Back up `plugins/AntiPopup/config.yml`
and `server.properties` before an update. AntiPopup's properties writer changes
only `enforce-secure-profile` and preserves the rest of that file.
