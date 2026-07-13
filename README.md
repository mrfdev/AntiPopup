# AntiPopup

AntiPopup is a standalone Paper plugin that hides Minecraft's unsafe-server
popup and limits the vanilla reportable-chat path. This `mrfdev` fork is a
deliberately narrow build for **Paper 26.2** with **Java 25 bytecode**.

Player guide: [AntiPopup on docs.1moreblock.com](https://docs.1moreblock.com/custom-server-plugins/antipopup/)

## What Is Special About This Fork

- One Paper plugin and one deployable JAR; Velocity, BungeeCord, Spigot server,
  Folia, and historical Minecraft build targets are gone.
- Compiles against the Paper API and uses Adventure components instead of
  deprecated Bungee chat components or legacy string messaging.
- Contains no CraftBukkit/NMS implementation and no exact-version NMS switch.
  PacketEvents performs the protocol work, which removes the most brittle part
  of a Paper patch or minor-version update.
- Uses stable, pinned dependencies plus a Gradle lockfile. Snapshot dependencies
  and unused BoostedYAML, FoliaLib, ViaVersion hook, and compatibility modules
  are gone.
- Treats deprecation and removal warnings as build errors and verifies the final
  JAR's target bytecode, descriptor, main class, and legacy exclusions.
- Preserves Paper's original per-recipient packet for filtered chat. It never
  replaces filtered text with an unfiltered copy.
- Runs after ordinary packet listeners and never recreates a packet that another
  moderation or privacy listener already cancelled.

The known-good build remains on `master`. Development and runtime certification
for this rewrite happen on `paper-only-26.2`, so the candidate can be discarded
without disturbing the working version.

## Compatibility

| Component | Candidate target |
| --- | --- |
| Server | Paper 26.2 |
| Paper API | `26.2.build.56-alpha` |
| Java bytecode | Java 25 (class version 69) |
| Runtime | Paper `26.2-56-main@8cd4f47` on Java 26.0.1 verified |
| Plugin version | `14.0.0-003` |
| Artifact | `1MB-AntiPopup-v14.0.0-003-j25-26.2.jar` |

No proxy or companion plugin is required. The embedded PacketEvents Paper/Bukkit
adapter retains `spigot` in its upstream artifact and class names; that is an
implementation detail, not a supported Spigot server target.

## What It Does

- Marks compatible server-list responses as preventing chat reports when
  `block-chat-reports` is enabled.
- Adjusts the applicable server-data and join-game secure-chat flags when
  `show-popup` is disabled.
- Cancels chat-session updates and converts unfiltered outgoing player chat to
  non-reportable disguised chat when report blocking is enabled.
- Leaves partially or fully filtered messages on Paper's original safe path.
- Can remove `[Not Secure]` from the matching Minecraft server log message.
- Can safely set `enforce-secure-profile=false` in `server.properties`, while
  preserving the rest of the file, and request Paper's native restart.

AntiPopup has no rewards, costs, cooldowns, progression, player data, database,
or PlaceholderAPI placeholders.

## Commands

| Command | Sender | Description |
| --- | --- | --- |
| `/antipopup` | Player/console | Shows the same friendly overview as `info`. |
| `/antipopup info` | Player/console | Shows the installed version, starting command, and clickable canonical documentation link. |
| `/antipopup setup` | Local server console only | Sets `enforce-secure-profile=false` and requests a restart only when a change is made. |
| `/antipopup reload` | Local server console only | Reloads the configuration snapshot. |

The permission `antipopup.commands` defaults to everyone. Sender checks in the
implementation still reject players, command blocks, and RCON for `setup` and
`reload`.

## Build and Test

JDK 25 is the configured Gradle toolchain:

```bash
./gradlew clean build --warning-mode all
```

The build runs unit tests and `verifyArtifact`, then writes only the shaded,
deployable plugin to:

```text
build/libs/1MB-AntiPopup-v14.0.0-003-j25-26.2.jar
```

`pluginVersion` and the required three-digit `pluginBuild` live in
`gradle.properties`. Future artifacts keep the pattern
`1MB-AntiPopup-v<version>-<build>-j<java>-<paper>.jar`.

PacketEvents 2.13.0 and bStats 3.2.1 are relocated inside the JAR. The matching
Adventure NBT 5.2.0 implementation is bundled while Paper supplies Adventure's
API, keeping Paper and PacketEvents on one compatible component type system.

The strict build currently runs 19 tests. An isolated runtime test also verified
startup, `/antipopup info`, reload, setup's already-configured path, plugin
listing, a real status-protocol response with `preventsChatReports: true`, and
clean disable on Paper 26.2 build 56 with Java 26.0.1.

## Future Paper Updates

No project can guarantee compatibility with an unreleased Paper API. This fork
instead removes the known exact-version/NMS failure point and keeps a repeatable
certification path. For 26.2.1 or 26.3, make another disposable branch, update
`paperApiVersion` and `paperTarget` in `gradle.properties`, refresh the lockfile,
run the strict build, and complete an isolated server/chat test before merging.

The complete checklist is in [Maintaining Paper compatibility](docs/maintenance.md).

The embedded PacketEvents injector emits Java 26's final-field-mutation warning
while it attaches to Paper's network channels. It works on Java 26.0.1; a server
can explicitly authorize that access with
`--enable-final-field-mutation=ALL-UNNAMED`. Re-test this boundary when moving to
a later JDK or PacketEvents release.

## Documentation

- [Player guide](docs/player-guide.md)
- [Commands](docs/commands.md)
- [Permissions](docs/permissions.md)
- [Configuration](docs/configuration.md)
- [Installation and updates](docs/installation.md)
- [Integrations](docs/integrations.md)
- [Maintaining Paper compatibility](docs/maintenance.md)
- [Troubleshooting](docs/troubleshooting.md)

## Persistence, Metrics, and Privacy

Persistent project state is limited to `plugins/AntiPopup/config.yml`.
`/antipopup setup` or `auto-setup` can also update the configured
`server.properties` file. bStats metrics ID `16308` is disabled by default and
starts only when explicitly enabled. PacketEvents update checks are disabled.

The isolated test did not simulate two real signed-chat clients or a moderation
plugin. Player chat, per-recipient filtering, and production protocol translators
still require the real-client certification steps in `docs/maintenance.md`.

## Source, Support, and License

- Fork: [mrfdev/AntiPopup](https://github.com/mrfdev/AntiPopup)
- Issues: [mrfdev/AntiPopup issues](https://github.com/mrfdev/AntiPopup/issues)
- Upstream: [KaspianDev/AntiPopup](https://github.com/KaspianDev/AntiPopup)

AntiPopup is distributed under the GNU General Public License v3.0. See
[LICENSE](LICENSE).
