# Installation and Updates

## Experimental Candidate Scope

Build `009` passed its vanilla-client popup and join/rejoin check and targets:

- Paper 26.3 ALPHA build 40.
- A native Minecraft 26.3 client.
- Java 25 or newer. The plugin contains Java 25 bytecode; Oracle JDK 25.0.4.1 and
  26.0.2.1 are server-smoke-tested.

<!-- release-metadata:start -->
| Release fact | Value |
| --- | --- |
| Plugin | `14.0.3-009` |
| Artifact | `1MB-AntiPopup-v14.0.3-009-j25-26.3.jar` |
| Paper runtime | `26.3` build `40` (`ALPHA`) |
| Compiled Paper API | `26.3.build.40-alpha` |
| Java bytecode | `25` |
| Verified runtimes | Oracle JDK `25.0.4.1` and `26.0.2.1` |
<!-- release-metadata:end -->

The plugin is not certified
for proxies, protocol translators, old clients, other server implementations,
Paper 26.1.2, or later Paper releases. Do not install a separate PacketEvents
JAR; the required packet transport is embedded.

The isolated runtime tests use Paper 26.3 ALPHA build 40. The 26.2 build `008`
snapshot remains the working rollback. Follow the
[26.3 testing instructions](paper-26.3-testing.md) for the cloned test server;
popup suppression is operator-confirmed; ordinary-player chat remains unverified.

## Fresh Installation

1. Stop the Paper server.
2. Put the build `009` JAR in the server's top-level `plugins/` directory.
3. Confirm no other AntiPopup JAR is present there.
4. Start Paper with Java 25 or newer.
5. Confirm the AntiPopup startup section includes:
   - `Loaded embedded PacketEvents.`
   - `Initiated embedded PacketEvents for Paper 26.3.`
6. Join directly with a native 26.3 client.
7. Confirm the blue unsafe-server popup is absent and ordinary player chat still
   works.

There are no commands, permissions, settings, configuration files, or setup
steps. Popup suppression is always active while the plugin is loaded.

## Choosing Modern or Legacy

Build `009` is the experimental 26.3 compatibility candidate. Its only behavior
is hiding the native login popup. Keep the working 26.2 build `008` and its
original server while this candidate completes staging.

Build `003` is the public archived legacy fallback for servers that still need
functionality intentionally removed from the modern line. It includes commands,
configuration/reload, popup toggling, server-properties setup/restart handling,
chat-report blocking, optional bStats, a Log4j console filter, legacy-client
packet handling, and protocol-translator load-order metadata. It is unsupported,
will not receive future Paper updates, and its complete chat-report behavior was
not re-certified with real clients during the modern cleanup.

Never install multiple AntiPopup builds at once.

## Updating and Rolling Back

Build `008` and the untouched 26.2 server are the immediate rollback for this
upgrade. Builds `006` and `005` remain earlier internal rollbacks. Public users
can use archived build `003` when
they specifically need the removed legacy features. Retain rollback JARs and
checksums outside the active `plugins/` directory.

1. Stop the server cleanly.
2. Remove the previous AntiPopup JAR.
3. Copy in `1MB-AntiPopup-v14.0.3-009-j25-26.3.jar` and start the server.
4. Confirm the two startup lines above.
5. Repeat the native 26.3 client join and chat test.
6. If build `009` fails, stop the 26.3 clone and use the original 26.2 server
   with build `008`. Do not downgrade an upgraded world in place.

Never leave multiple AntiPopup builds in `plugins/`.

## Building Locally

```bash
export JAVA_HOME=/path/to/jdk-25.0.4.1
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean build --warning-mode all
```

The build requires the Oracle JDK 25.0.4.1 toolchain and produces:

```text
build/libs/1MB-AntiPopup-v14.0.3-009-j25-26.3.jar
```

The build includes final-JAR validation. See
[Maintaining Paper compatibility](maintenance.md) before changing the target.
