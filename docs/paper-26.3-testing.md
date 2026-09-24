# Paper 26.3 Build 40 Candidate

Build `009` (`14.0.3-009`) targets Paper 26.3 ALPHA build 40 with PacketEvents
2.14.0. It keeps Java 25 bytecode and the existing popup-only listener.
The operator confirmed a vanilla 26.3 join with no popup on 2026-09-24.
Console review confirmed successful join/rejoin without errors. Ordinary-player
chat verification has not yet been reported.

## Preserved 26.2 Baseline

The operator confirmed the existing 26.2 build working before this upgrade.
Commit `d96f613` and tag `snapshot-paper-26.2-20260924` were pushed to GitHub
before creating branch `codex/paper-26.3-build-40`.

The exact build `008` JAR remains under `run/retained/14.0.2-008/`, with SHA-256
`e555d34bee12edfff3a09cbc25c34944a8140c48b73b9511885ff61daf683f9a`.
The existing shared 26.2 test server is unchanged.

## Local Test Instance

The ignored `servers/Paper-26.3/` directory is a complete copy of the most recent
shared AntiPopup Java 26 instance, whose exact source path is recorded in
`clone-source.txt`. The copied world, operator list, and server settings remain
local. Other plugins, their data and old runtime logs are retained under
`retained-26.2/`; the prior Paper JAR is in `paperscript/backups/`. Only the
new AntiPopup JAR is active.

- Address: `127.0.0.1:26340`.
- Runtime: Oracle Java 26.0.2.1, with Java 25.0.4.1 also smoke-tested.
- Server JAR: `Paper-26.3.jar`, build 40, channel ALPHA.
- Server SHA-256: `49399919246cbf443efc8507447dc948eb7477c41be560b0e87e2a455aff824a`.
- `1MB-minecraft.sh` selects 26.3 and the installed Java 26.0.2.1 runtime.
- `paperscript/config.json` selects ALPHA and disables automatic same-version
  and cross-version upgrades. Use the exact command below to retain build 40.
- The original `online-mode=true` and `enforce-secure-profile=false` are kept
  for the in-game popup test. Exploit-protection settings stay enabled.

Start from the project root:

```bash
./servers/Paper-26.3/minecraft.sh
```

The wrapper checks the pinned Paper checksum before launching. The prepared
instance runs in tmux session `antipopup-26-3`. Attach to its console with
`tmux attach -t antipopup-26-3`; detach with Ctrl-B then D. Stop cleanly by
entering `stop` in its console. Do not launch a second copy while it is running.
With the instance stopped, fetch exactly the same Paper build again with:

```bash
cd servers/Paper-26.3
./paperscript.sh download --version 26.3 --build 40 --channel ALPHA
```

Do not use `experimental --download` to reproduce this test: it selects the
latest experimental build, which may have advanced beyond build 40.

The clean build, artifact checks, both JDK smoke tests, and cloned-world
startup/shutdown passed. See the dated
[verification record](agents/local-verification.md) for checksums and evidence.

## In-Game Checks

The 2026-09-24 console records two successful logins at 23:40:26 and 23:40:39
(Europe/Amsterdam), followed by normal disconnects. There were no errors or
exceptions. The operator confirmed the client was vanilla 26.3 and no popup
appeared. The installed JAR still matches the recorded build `009` checksum.
The retained log contains only join/leave system messages, so ordinary-player
chat is not marked as tested. Local evidence is under
`run/verification/14.0.3-009/` in `native-client-server.log`,
`native-client-console.txt`, and `native-client-result.json`.

For repeat testing and the remaining chat check:

1. Join `127.0.0.1:26340` directly using an unmodified native Minecraft 26.3
   client, without a proxy or protocol translator. Ensure the client has not
   previously dismissed the unsafe-server popup, so suppression is observable.
2. Confirm the blue unsafe-server popup does not appear.
3. Send ordinary chat and confirm it is delivered normally.
4. Disconnect and rejoin, then repeat the popup and chat checks.
5. Record the client version, Paper build, plugin version, and outcome before
   promoting the candidate. Automated startup tests cannot certify the popup.

If the candidate fails, stop the clone and return to the unchanged 26.2 server
and build `008`. Never downgrade the clone's upgraded world in place.

## Official Compatibility Evidence

- [Paper build 40 metadata](https://fill.papermc.io/v3/projects/paper/versions/26.3/builds/40)
  supplies the ALPHA channel, download and checksum.
- [Paper documentation index](https://docs.papermc.io/llms.txt) was the primary
  discovery source for the downloads service, project setup, plugin lifecycle,
  descriptor, configuration and roadmap guidance.
- [JavaPlugin Javadocs](https://jd.papermc.io/paper/26.3/org/bukkit/plugin/java/JavaPlugin.html)
  identified API `26.3.build.40-alpha` when checked on 2026-09-24. The lifecycle
  methods used by AntiPopup remain supported.
- [Paper roadmap](https://docs.papermc.io/paper/dev/roadmap/) concerns ItemStack
  construction and internal ServerPlayer reuse; AntiPopup uses neither.
- [PacketEvents 2.14.0](https://github.com/retrooper/packetevents/releases/tag/v2.14.0)
  adds 26.3 support. Its tagged JOIN_GAME wrapper includes the changed 26.3
  dimension encoding while preserving the secure-chat flag setter.
- Paper's exact API keeps Adventure 5.2.0, so the bundled NBT module stays at
  5.2.0. Lockfile review also identified Paper's Brigadier 1.3.11 and JOML 1.10.9,
  plus the transitive Checker Framework annotations update to 4.2.3.
