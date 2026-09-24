# Runtime Boundary and Bundled Libraries

## Standalone Runtime

The deployable JAR relocates the runtime libraries it needs:

- PacketEvents `2.14.0`
- Adventure NBT `5.2.0`

PacketEvents is relocated. Adventure NBT is kept in its standard package because
it implements Paper's Adventure 5 API; bundling or relocating a second Adventure
API would create incompatible component types.
PacketEvents' bundled bStats implementation is excluded. Its hard-coded metrics
bootstrap is redirected to inert linkage shims, so the embedded packet engine
does not start a telemetry service.

AntiPopup itself owns only five classes. The standalone JAR is about 4.9
MiB because PacketEvents' complete reflection-driven injector and protocol
library is retained; its generic compatibility internals do not make old
clients or proxies supported deployment paths.

Paper supplies the Paper API, Adventure API 5.2.0, and Netty.
AntiPopup does not bundle CraftBukkit/NMS, BoostedYAML, FoliaLib, ViaVersion API,
or a proxy implementation.

PacketEvents calls its Paper/Bukkit bootstrap artifact and builder `spigot`.
Those upstream names remain visible in source imports, but this fork compiles,
describes, verifies, and tests only a Paper plugin. No Spigot server artifact is
produced or supported.

## No External Integrations

Build `009` declares no soft dependencies and supports no proxy or
protocol-translation integration. Its candidate test path is a native 26.3
client joining Paper 26.3 directly. A proxy or translated old-client connection
is not a supported deployment path. The vanilla 26.3 popup and join/rejoin check
passed; ordinary-player chat remains unverified.

The plugin does not expose commands, permissions, settings, placeholders, or a
public API.

## Paper Runtime

The production source uses the Paper/Bukkit API, while embedded PacketEvents
uses Paper's Adventure types. It contains no versioned NMS class and no Folia
support declaration. Build `009` handles only the modern `JOIN_GAME` packet;
the historical `SERVER_DATA` old-client path is not included.
