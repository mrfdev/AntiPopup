# AntiPopup

AntiPopup is a standalone Paper plugin that hides Minecraft's unsafe-server
login popup. This `mrfdev` fork is a deliberately narrow build for **Paper
26.2 and its future Paper update line** with **Java 25 bytecode**.

Player guide: [AntiPopup on docs.1moreblock.com](https://docs.1moreblock.com/custom-server-plugins/antipopup/)

## What Is Special About This Fork

- One Paper plugin and one deployable JAR; Velocity, BungeeCord, Spigot server,
  Folia, and historical Minecraft build targets are gone.
- Compiles against the Paper API and contains no legacy command or chat-
  messaging surface.
- Contains no CraftBukkit/NMS implementation and no exact-version NMS switch.
  PacketEvents performs the protocol work, which removes the most brittle part
  of a Paper patch or minor-version update.
- Uses stable, pinned dependencies plus a Gradle lockfile. Snapshot dependencies
  and unused BoostedYAML, FoliaLib, ViaVersion hook, and compatibility modules
  are gone.
- Treats deprecation and removal warnings as build errors and verifies the final
  JAR's target bytecode, descriptor, main class, and legacy exclusions.
- Runs after ordinary packet listeners and leaves packets already cancelled by
  another listener untouched.
- Beginning with build `006`, contains no commands, permissions, configuration,
  reload path, setup behavior, or persistent plugin state.

## Server-Specific Direction After Build 005

Build `005` was confirmed on the 1MoreBlock server: the blue popup stayed hidden
on join and ordinary player chat continued to work. Its exact JAR is retained
internally as the known-good rollback artifact.

Starting with build `006`, this fork is intentionally a server-specific tool
that performs one operation and nothing else. It sets the modern Paper join
packet's secure-chat flag so the native 26.2 client does not show the popup.
The following historical upstream scope is deliberately removed moving forward:

- Proxy and protocol-translation integrations, load-order metadata, and
  networking-platform targets such as Velocity and BungeeCord.
- Spigot, Folia, legacy NMS modules, old Minecraft releases, and old-client
  packet formats.
- Old Java targets; this fork emits Java 25 bytecode and build `006` has passed
  its server smoke test on the installed Java 26.0.1 runtime.
- Commands, help, info, permissions, configuration, reload, diagnostics,
  chat-report features, metrics, console filtering, and server-properties setup.

PacketEvents remains embedded solely as the Paper packet transport needed for
the one join-packet mutation. Its upstream `spigot` adapter name is an internal
Bukkit/Paper implementation detail, not a supported Spigot server target.
AntiPopup itself now owns only five classes. The standalone JAR remains about
4.5 MiB because PacketEvents' full reflection-driven injector and protocol
library stays embedded; its generic internals are not supported old-client or
proxy paths, and aggressively pruning them would risk breaking login injection.

## Release Lines

- **Build `006` — current 1MB modern:** the certified popup-only Paper 26.2
  build documented by this README. It has no commands, configuration, reload,
  setup, chat-report modification, metrics, Log4j filter, legacy-client path,
  proxy integration, or persistent state.
- **Build `003` — archived full legacy:** the feature-complete Paper-only 26.2
  fallback. It retains `/antipopup` commands, configuration and reload, the
  popup toggle, `server.properties` setup/restart handling, chat-report
  blocking, the status marker, chat-session cancellation, disguised-chat
  conversion, optional bStats, the Log4j `[Not Secure]` filter, legacy
  `SERVER_DATA` handling, and translator soft-dependency metadata.

Build `003` is published for users who discover that build `006` deliberately
removed behavior they still require. It is archived and unsupported, includes
the features later rejected for the 1MB server, and will not receive future
Paper updates. Never install builds `003` and `006` at the same time. Downloads
and checksums are available from the
[GitHub releases page](https://github.com/mrfdev/AntiPopup/releases).

## Compatibility

| Component | Certified build |
| --- | --- |
| Server | Paper 26.2 |
| Paper API | `26.2.build.56-alpha` |
| Java bytecode | Java 25 (class version 69) |
| Runtime | Paper `26.2-60-main@1cb58fb` on Java 26.0.1 smoke-tested |
| Plugin version | `14.0.0-006` |
| Artifact | `1MB-AntiPopup-v14.0.0-006-j25-26.2.jar` |

No proxy or companion plugin is required. The embedded PacketEvents Paper/Bukkit
adapter retains `spigot` in its upstream artifact and class names; that is an
implementation detail, not a supported Spigot server target.

## What It Does

- For an uncancelled modern `JOIN_GAME` packet, sets
  `enforcesSecureChat=true` so the native 26.2 client does not show the popup.
- Nothing else. Suppression is always enabled while the plugin is installed.

AntiPopup has no rewards, costs, cooldowns, progression, player data, database,
PlaceholderAPI placeholders, commands, permissions, or configuration.

## Build and Test

JDK 25 is the configured Gradle toolchain:

```bash
./gradlew clean build --warning-mode all
```

The build runs strict compilation and `verifyArtifact`, then writes only the
shaded, deployable plugin to:

```text
build/libs/1MB-AntiPopup-v14.0.0-006-j25-26.2.jar
```

`pluginVersion` and the required three-digit `pluginBuild` live in
`gradle.properties`. Future artifacts keep the pattern
`1MB-AntiPopup-v<version>-<build>-j<java>-<paper>.jar`.

PacketEvents 2.13.0 is relocated inside the JAR. The matching Adventure NBT
5.2.0 implementation is bundled while Paper supplies Adventure's API, keeping
Paper and PacketEvents on one compatible component type system.
PacketEvents' bundled bStats implementation is excluded, and its hard-coded
metrics bootstrap is linked to inert local compatibility shims.

The strict build runs its artifact checks. An isolated runtime test verifies
startup, plugin listing, and clean disable on Paper 26.2 build 60 with Java
26.0.1. Build `006` also passed its native 26.2 client join and ordinary-chat
test; every later candidate must repeat that test.

## Future Paper Updates

No project can guarantee compatibility with an unreleased Paper API. This fork
instead removes the known exact-version/NMS failure point and keeps a repeatable
certification path. For 26.2.1 or 26.3, make another disposable branch, update
`paperApiVersion` and `paperTarget` in `gradle.properties`, refresh the lockfile,
run the strict build, and complete an isolated server and client-login test
before merging.

The complete checklist is in [Maintaining Paper compatibility](docs/maintenance.md).

The embedded PacketEvents injector emits Java 26's final-field-mutation warning
while it attaches to Paper's network channels. It works on Java 26.0.1; a server
can explicitly authorize that access with
`--enable-final-field-mutation=ALL-UNNAMED`. Re-test this boundary when moving to
a later JDK or PacketEvents release.

## Documentation

- [Player guide](docs/player-guide.md)
- [Installation and updates](docs/installation.md)
- [Integrations](docs/integrations.md)
- [Maintaining Paper compatibility](docs/maintenance.md)
- [Troubleshooting](docs/troubleshooting.md)

## Persistence and Privacy

AntiPopup has no configuration or persistent plugin state. PacketEvents update
checks are disabled and no metrics service is started.

The isolated test does not replace a real-client login test. The production
native 26.2 client completed the build `006` certification steps in
`docs/maintenance.md`; protocol translators and older clients remain out of
scope for the modern release.

## Source, Support, and License

- Fork: [mrfdev/AntiPopup](https://github.com/mrfdev/AntiPopup)
- Issues: [mrfdev/AntiPopup issues](https://github.com/mrfdev/AntiPopup/issues)
- Upstream: [KaspianDev/AntiPopup](https://github.com/KaspianDev/AntiPopup)

AntiPopup is distributed under the GNU General Public License v3.0. See
[LICENSE](LICENSE).
