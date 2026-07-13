# Installation and Updates

## Requirements

- Paper 26.2.
- Java 25 or newer. The plugin contains Java 25 bytecode; Java 26.0.1 is verified.
- `AntiPopup-13.2-paper-only.1-j25-mc26.2.jar` from this candidate branch.

The plugin compiles against Paper API `26.2.build.56-alpha`. It is standalone:
do not install a separate PacketEvents jar and do not deploy this build to
Velocity, BungeeCord, Spigot, Paper 26.1.2, or historical Minecraft versions.

The isolated runtime test used Paper `26.2-56-main@8cd4f47` on Java 26.0.1.

## Fresh Installation

1. Stop the Paper server.
2. Put the candidate JAR in the server's top-level `plugins/` directory.
3. Confirm no other AntiPopup JAR is present there.
4. Start Paper with Java 25 or newer.
5. Confirm the AntiPopup startup section includes:
   - `Loaded embedded PacketEvents.`
   - `Initiated embedded PacketEvents for Paper 26.2.`
   - `Commands registered.`
6. Join once and run `/antipopup info`.
7. Test normal signed chat, the popup state, and any protocol-translation setup
   used by the real server before promoting the candidate.

## Optional Server-Properties Setup

Back up `server.properties` first. From the local server console, run:

```text
antipopup setup
```

If `enforce-secure-profile` is true, AntiPopup changes only that property,
preserves the file's other lines and permissions, writes through a temporary
file, and requests Paper's native restart after five seconds. If the property
is absent, it is appended. If it is already false, the file is not rewritten
and no restart is requested.

`auto-setup: true` performs the same check shortly after each startup. Manual
setup is easier to supervise. AntiPopup does not create a separate backup file.

## Updating an Existing Installation

1. Stop the server cleanly.
2. Back up `plugins/AntiPopup/config.yml` and `server.properties`.
3. Remove the previous AntiPopup JAR.
4. Copy in the candidate JAR and start the server.
5. Review the updated `config.yml`. Paper's YAML loader adds missing defaults;
   obsolete settings from old builds are ignored and may be removed manually.
6. Confirm the startup lines above.
7. Re-test `/antipopup info`, signed chat, any moderation/filtering path, the
   popup state, and the server's actual client-version mix.

Never leave multiple AntiPopup builds in `plugins/`.

## Building Locally

```bash
./gradlew clean build --warning-mode all
```

The build requires a JDK 25 toolchain and produces:

```text
build/libs/AntiPopup-13.2-paper-only.1-j25-mc26.2.jar
```

The build includes unit tests and final-JAR validation. See
[Maintaining Paper compatibility](maintenance.md) before changing the target.
