# Installation and Updates

## Requirements

- Paper 26.2.
- Java 25. Java 26.0.1 has also been verified as a runtime.
- The standalone `AntiPopup-13.2-j25-mc26.2.jar`.

The plugin compiles against Paper development bundle
`26.2.build.34-alpha`. A full runtime smoke test has passed on Paper
`26.2-56-main@8cd4f47` with Java `26.0.1`.

No companion dependency jar is required. Do not deploy this build to Velocity,
BungeeCord, Spigot, Paper 26.1.2, or historical Minecraft versions.

## Fresh Installation

1. Stop the server.
2. Put `AntiPopup-13.2-j25-mc26.2.jar` in the server's top-level `plugins/`
   directory.
3. Confirm no other AntiPopup jar is present there.
4. Start Paper with Java 25, or with the separately verified Java 26.0.1 runtime.
5. Confirm the log includes:
   - `[AntiPopup] Loading server plugin AntiPopup v13.2`
   - `[AntiPopup] Initiated PacketEvents.`
   - `[AntiPopup] Hooked on 26.2`
   - `[AntiPopup] Commands registered.`
6. Join once and run `/antipopup info`.
7. Test chat with the exact client/ViaVersion combinations allowed by the server.

## Optional Server-Properties Setup

Review and back up `server.properties` first. From the local server console,
run:

```text
antipopup setup
```

If `enforce-secure-profile` is true, the command writes it as false and asks
Paper to restart after five seconds. It rewrites the Java properties file and
does not create a backup. If the property is already false, no restart is
requested.

Alternatively, `auto-setup: true` performs the same check shortly after each
startup. Manual setup is easier to supervise.

## Updating

1. Stop the server cleanly.
2. Back up `plugins/AntiPopup/config.yml` and `server.properties`.
3. Remove the previous AntiPopup jar.
4. Copy in the new standalone jar.
5. Start the server and review BoostedYAML's updated configuration.
6. Confirm the readiness log lines above.
7. Re-test `/antipopup info`, player chat, the popup state, and any ViaVersion
   client combinations.

Do not leave multiple AntiPopup builds in `plugins/`.

## Building Locally

```bash
./gradlew clean build
```

JDK 25 is required for compilation. The deployable file is:

```text
build/libs/AntiPopup-13.2-j25-mc26.2.jar
```
