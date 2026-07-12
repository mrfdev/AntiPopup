# Integrations

## Bundled Runtime Libraries

The standalone jar shades its required runtime libraries, including:

- PacketEvents Spigot `2.13.1-SNAPSHOT`
- BoostedYAML `1.3`
- bStats Bukkit `3.0.0`
- FoliaLib `0.2.3`
- Adventure platform and serializer components

Do not install separate copies of these as AntiPopup dependencies.

## ViaVersion

ViaVersion is the only optional plugin with direct hook code. If ViaVersion is
installed, AntiPopup loads its hook after confirming the plugin is enabled.
ViaVersion 4 is explicitly rejected by that hook. The compile-time integration
target is ViaVersion 5.4.0; test the exact installed build before production use.

The two bundled protocol modifiers target historical server-version comparisons.
On this fork's Paper 26.2 target neither modifier is expected to activate.
ViaVersion can still translate clients independently, but no client-version
compatibility matrix has been certified for this fork.

## Other Soft Dependencies

`plugin.yml` also orders AntiPopup after ProtocolLib, ProtocolSupport,
ViaBackwards, ViaRewind, and Geyser-Spigot when those plugins are present. No
direct integration implementation for those names exists in the active source,
and none is required to run AntiPopup.

## Paper, Folia, and Logging

Paper 26.2 supplies Bukkit, CraftBukkit/NMS, Netty, and Log4j runtime classes.
`folia-supported: true` is declared and FoliaLib schedules delayed work, but a
dedicated Folia runtime test has not been completed.

The optional `filter-not-secure` feature installs a Log4j root filter at
startup. Test it with any plugin that also changes server logging.

## Metrics and Placeholders

bStats is disabled by default. When enabled it uses plugin metrics ID `16308`
and a custom chart that reports whether ViaVersion is enabled.

AntiPopup has no PlaceholderAPI expansion or public placeholders.
