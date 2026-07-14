# Installation and Updates

## Certified Scope

Build `006` has a deliberately narrow deployment boundary:

- Paper 26.2.
- A native Minecraft 26.2 client.
- Java 25 or newer. The plugin contains Java 25 bytecode; Java 26.0.1 is
  verified.
- The verified `1MB-AntiPopup-v14.0.0-006-j25-26.2.jar` artifact.

The plugin compiles against Paper API `26.2.build.56-alpha`. It is not certified
for proxies, protocol translators, old clients, other server implementations,
Paper 26.1.2, or later Paper releases. Do not install a separate PacketEvents
JAR; the required packet transport is embedded.

The isolated runtime test uses Paper `26.2-60-main@1cb58fb` on Java 26.0.1.

## Fresh Installation

1. Stop the Paper server.
2. Put the build `006` JAR in the server's top-level `plugins/` directory.
3. Confirm no other AntiPopup JAR is present there.
4. Start Paper with Java 25 or newer.
5. Confirm the AntiPopup startup section includes:
   - `Loaded embedded PacketEvents.`
   - `Initiated embedded PacketEvents for Paper 26.2.`
6. Join directly with a native 26.2 client.
7. Confirm the blue unsafe-server popup is absent and ordinary player chat still
   works.

There are no commands, permissions, settings, configuration files, or setup
steps. Popup suppression is always active while the plugin is loaded.

## Choosing Modern or Legacy

Build `006` is the current recommended release. Use it when the only required
behavior is hiding the native 26.2 login popup.

Build `003` is the public archived legacy fallback for servers that still need
functionality intentionally removed from build `006`. It includes commands,
configuration/reload, popup toggling, server-properties setup/restart handling,
chat-report blocking, optional bStats, a Log4j console filter, legacy-client
packet handling, and protocol-translator load-order metadata. It is unsupported,
will not receive future Paper updates, and its complete chat-report behavior was
not re-certified with real clients during the build `006` cleanup.

Never install both builds at once.

## Updating and Rolling Back

Build `005` remains the 1MoreBlock internal rollback boundary. Public users can
use the archived build `003` when they specifically need the removed legacy
features. Retain rollback JARs and checksums outside the active `plugins/`
directory.

1. Stop the server cleanly.
2. Remove the previous AntiPopup JAR.
3. Copy in `1MB-AntiPopup-v14.0.0-006-j25-26.2.jar` and start the server.
4. Confirm the two startup lines above.
5. Repeat the native 26.2 client join and chat test.
6. If build `006` fails, stop the server. 1MoreBlock can restore retained build
   `005`; public users can try archived build `003` after reviewing its expanded
   behavior and unsupported status.

Never leave multiple AntiPopup builds in `plugins/`.

## Building Locally

```bash
./gradlew clean build --warning-mode all
```

The build requires a JDK 25 toolchain and produces:

```text
build/libs/1MB-AntiPopup-v14.0.0-006-j25-26.2.jar
```

The build includes final-JAR validation. See
[Maintaining Paper compatibility](maintenance.md) before changing the target.
