# AntiPopup - Paper 26.2 / Java 25 fork

> [!IMPORTANT]
> This `mrfdev` fork is intentionally specialized for Paper 26.2. For the
> original multi-version and proxy-compatible project, see
> [KaspianDev/AntiPopup](https://github.com/KaspianDev/AntiPopup).

## What is special about this fork?

- Builds a standalone Paper server plugin only; Velocity and the historical
  Minecraft NMS modules are excluded from the active Gradle project and final
  artifact.
- Includes only the Paper 26.2 `v26.2` injector and declares
  `api-version: '26.2'`.
- Compiles every retained project module as Java 25 bytecode. The resulting
  plugin runs on Java 25 and newer JVMs, including Java 26.0.1.
- Shades its runtime libraries, including PacketEvents, BoostedYAML, bStats,
  Adventure, and FoliaLib, so no companion dependency jars are required.
- Has been compiled with Gradle 9.4.1 and smoke-tested through a complete
  enable, Paper 26.2 injector selection, and clean shutdown on Paper
  26.2-29-dev with Java 26.0.1.

## Building this fork

JDK 25 is required as the Gradle compilation toolchain.

```bash
./gradlew clean build
```

The deployable shaded plugin is written to:

```text
build/libs/AntiPopup-13.2-j25-mc26.2.jar
```

## Original project information

**For faster updates, priority support and a discord role purchase AntiPopup Pro on Polymart.**  
Click the button below for more details.

[<img src="https://images.polymart.org/resource/4921/default.jpg" width="480" alt="Download AntiPopup Pro on Polymart.org" title="Download AntiPopup Pro on Polymart.org">](https://polymart.org/resource/antipopup-pro.4921)

**What is this plugin?** \
AntiPopup is a plugin aiming to remove chat reporting system entirely using packets. It also has unique feature - blocks the new annoying popup (below) even if your server doesn't enforce chat reporting (please don't enforce it). \
My plugin is also the safest to use, chance of breaking a different plugin is unlikely, I'd even say impossible. Note that some plugins might break it, report that to me. \
![](https://cdn.discordapp.com/attachments/834878536816525344/1002561207603048468/unknown.png)

**How to install?**\
Plug and play, nothing else for you to do. \
Optionally you can run **antipopup setup** in console, recommended. \
If you use viaversion on bungeecord install AntiPopup ViaVersion addon.

**Commands** \
**antipopup setup** - Disables **enforce-secure-profile** in server.properties (console only). \
**antipopup reload** - Reloads configuration.

**Faq** \
Q: What if my server runs an older version? \
A: If you use viaversion and antipopup, 1.19.1+ players will not see popup. \
Q: Will it work with ViaFabric? \
A: Popup will be shown for 1.19.2 clients spoofing to 1.19, nothing I can fix on my side.

**Support Me!** \
Donations are not required but they help me assign more time to improve the software. After donating message me on discord to add you to supporters list on marketplaces.

Available options are: \
[![liberapay](https://liberapay.com/assets/widgets/donate.svg "")](https://liberapay.com/Kaspian/donate "")
[![ko-fi](https://i.imgur.com/TUJMO7O.png "")](https://ko-fi.com/kaspiandev "")

**Need Help?** \
Message me on [matrix](https://matrix.to/#/#future-project:matrix.org) or [discord](https://discord.gg/eak2zA4s6m).
