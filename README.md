# AntiPopup

AntiPopup is a standalone Paper plugin that removes the unsafe-server chat
popup and limits Minecraft's chat-reporting path. This `mrfdev` fork is built
specifically for **Paper 26.2** with **Java 25 bytecode**; historical Minecraft,
Spigot, BungeeCord, and Velocity targets are not part of the active build.

Player guide: [AntiPopup on docs.1moreblock.com](https://docs.1moreblock.com/custom-server-plugins/antipopup/)

## Compatibility

| Component | Target |
| --- | --- |
| Server | Paper 26.2 |
| Paper development bundle | `26.2.build.34-alpha` |
| Java bytecode | Java 25 |
| Tested runtime | Paper 26.2 build 56 on Java 26.0.1 |
| Plugin version | `13.2` |
| Main command | `/antipopup` |
| Artifact | `AntiPopup-13.2-j25-mc26.2.jar` |

The active Gradle project includes only `spigot`, `shared`, `nms`, and
`v26.2`. Older source directories remain for upstream history but are not
compiled or packaged.

## What It Does

- Marks compatible server-list responses as preventing chat reports.
- Hides the unsafe-server popup through the applicable server-data and join-game
  packet flags when `show-popup` is disabled.
- Cancels chat-session updates and, by default, outgoing chat headers.
- When `block-chat-reports` is enabled, replaces Paper 26.2 player-chat
  packets with decorated system-chat packets before they reach each player.
- Optionally removes the `[Not Secure]` prefix from matching Minecraft server
  log messages.
- Can optionally restore clickable URL behavior through its Bukkit chat
  listener, with the compatibility trade-off documented in the configuration.

AntiPopup does not provide rewards, costs, cooldowns, progression, player data,
or PlaceholderAPI placeholders.

## Commands

| Command | Sender | Description |
| --- | --- | --- |
| `/antipopup` | Player/console | Shows the same friendly overview as `info`. |
| `/antipopup info` | Player/console | Shows the installed version, starting command, and clickable canonical documentation link. |
| `/antipopup setup` | Local server console only | Sets `enforce-secure-profile=false` in the configured properties file and restarts when a change is needed. |
| `/antipopup reload` | Local server console only | Reloads `config.yml`; startup-bound features still require a restart. |

The command permission is `antipopup.commands` and defaults to everyone.
The setup and reload restrictions are also enforced in code, so granting the
permission does not expose those operations to players, command blocks, or RCON.

## Documentation

- [Player guide](docs/player-guide.md)
- [Commands](docs/commands.md)
- [Permissions](docs/permissions.md)
- [Configuration](docs/configuration.md)
- [Installation and updates](docs/installation.md)
- [Integrations](docs/integrations.md)
- [Troubleshooting](docs/troubleshooting.md)

No placeholders page is included because this project does not implement any
placeholders.

## Installation Summary

1. Stop the Paper 26.2 server.
2. Back up `plugins/AntiPopup/config.yml` and `server.properties` when
   updating an existing installation.
3. Remove older AntiPopup jars from the server's top-level `plugins/` folder.
4. Copy `AntiPopup-13.2-j25-mc26.2.jar` into `plugins/`.
5. Start with Java 25, or the separately verified Java 26.0.1 runtime, and
   confirm the log contains
   `[AntiPopup] Hooked on 26.2`.

No companion dependency jar is required. Do not install this build on a proxy or
on another Paper/Minecraft version.

## Build and Test

JDK 25 is required as the Gradle toolchain:

```bash
./gradlew clean build
```

The normal build runs the focused command tests and writes the deployable shaded
plugin to:

```text
build/libs/AntiPopup-13.2-j25-mc26.2.jar
```

The artifact shades PacketEvents, BoostedYAML, bStats, Adventure support, and
FoliaLib. The root unshaded jar task is disabled to avoid producing an
undeployable artifact.

## Persistence, Metrics, and Updates

Persistent project state is limited to `plugins/AntiPopup/config.yml`.
`/antipopup setup` or `auto-setup` can also modify the configured
`server.properties` file. There is no database or per-player store.

bStats metrics are disabled by default. When enabled, AntiPopup uses metrics ID
`16308` and reports whether ViaVersion is enabled in addition to bStats'
standard plugin metrics. PacketEvents update checks are disabled.

## Verification and Limitations

This fork has completed a clean Gradle build and a startup/enable/shutdown smoke
test on Paper `26.2-56-main@8cd4f47` with Java `26.0.1`. The built JAR contained Java
25 class files and only the `v26_2` NMS implementation.

`folia-supported: true` is declared and FoliaLib is used for scheduled work,
but this fork has not completed a dedicated Folia runtime test. ViaVersion client
translation combinations have not been certified as a compatibility matrix.

## Source, Support, and License

- Fork: [mrfdev/AntiPopup](https://github.com/mrfdev/AntiPopup)
- Issues: [mrfdev/AntiPopup issues](https://github.com/mrfdev/AntiPopup/issues)
- Upstream project: [KaspianDev/AntiPopup](https://github.com/KaspianDev/AntiPopup)

AntiPopup is distributed under the GNU General Public License v3.0. See
[LICENSE](LICENSE).
