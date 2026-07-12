# Permissions

| Permission | Default | Description |
| --- | --- | --- |
| `antipopup.commands` | Everyone | Allows Bukkit to dispatch `/antipopup`, including the public `info` response. |

There are no additional player, administrative, wildcard, or bypass permission
nodes.

`/antipopup setup` and `/antipopup reload` are restricted by sender type in
the Java implementation. Granting `antipopup.commands` does not allow a player,
command block, or remote-console sender to run those local-console operations.

A permissions plugin can explicitly deny `antipopup.commands`, in which case
the player will not reach `/antipopup info` even though the declared default is
public.
