# Integrations and Bundled Libraries

## Standalone Runtime

The deployable JAR relocates the runtime libraries it needs:

- PacketEvents `2.13.0`
- Adventure NBT `5.2.0`
- bStats Bukkit `3.2.1`

PacketEvents and bStats are relocated. Adventure NBT is kept in its standard
package because it implements Paper's Adventure 5 API; bundling or relocating a
second Adventure API would create incompatible component types.

Paper supplies the Paper API, Adventure API 5.2.0, Netty, and Log4j.
AntiPopup does not bundle CraftBukkit/NMS, BoostedYAML, FoliaLib, ViaVersion API,
or a proxy implementation.

PacketEvents calls its Paper/Bukkit bootstrap artifact and builder `spigot`.
Those upstream names remain visible in source imports, but this fork compiles,
describes, verifies, and tests only a Paper plugin. No Spigot server artifact is
produced or supported.

## Protocol Translation Plugins

AntiPopup contains no direct ViaVersion, ViaBackwards, ViaRewind, ProtocolLib,
ProtocolSupport, or Geyser hook. Their names remain only as `softdepend` load
ordering because the embedded PacketEvents injector needs to initialize safely
when one of those packet translators is present.

Protocol translation is optional and independently managed. Test the exact
client and translator versions used by the production server; this fork does
not claim a compatibility matrix for them.

## Paper and Logging

The production source imports the Paper/Bukkit API and Adventure components. It
contains no versioned NMS class and no Folia support declaration. The optional
`filter-not-secure` setting installs one Log4j root filter at startup and removes
that same filter during plugin disable.

## Metrics and Placeholders

bStats is disabled by default and uses metrics ID `16308` when enabled. There is
no custom integration chart. AntiPopup has no PlaceholderAPI expansion or public
placeholders.
