# Troubleshooting

## The Plugin Will Not Load

Confirm that:

1. The server is Paper 26.2.
2. The runtime is Java 25 or newer.
3. Only one AntiPopup JAR is present in `plugins/`.
4. The file name is `AntiPopup-13.2-paper-only.1-j25-mc26.2.jar`.

`UnsupportedClassVersionError` means the runtime is older than Java 25; this
candidate uses class-file version 69. This branch no longer contains the old
`No valid injector found` NMS version switch.

## The Popup or Report Protection Returned

- Confirm `show-popup: false` and `block-chat-reports: true`.
- Run `antipopup reload` from the local console.
- Confirm startup logged `Initiated embedded PacketEvents for Paper 26.2.`
- Record the exact Paper build, client version, and any protocol translator.

If the log says an unknown chat packet was left unchanged, AntiPopup failed
safe rather than guessing a packet format. Update PacketEvents on a disposable
branch and repeat the certification checklist before production use.

Filtered messages intentionally remain on Paper's original per-recipient packet
path. That is a privacy safeguard, not a failure to process chat.

## Info Is Not Available to a Player

`antipopup.commands` defaults to everyone. Check whether a permissions plugin
explicitly denies it. `/antipopup info` should include a clickable link to:

```text
https://docs.1moreblock.com/custom-server-plugins/antipopup/
```

## Setup or Reload Does Nothing

Both operations require the local server console. Players, command blocks, and
RCON are intentionally rejected.

`setup` requests a restart only when `enforce-secure-profile` was missing or not
already false. Check that `properties-location` is relative to the server root
and points to the intended file.

## A Reloaded Setting Did Not Take Effect

`block-chat-reports`, `show-popup`, and the properties path use the new snapshot
after reload. Restart after changing `bstats` or `filter-not-secure`.
`auto-setup` is evaluated on the next startup.

Old keys including `send-header`, `clickable-urls`, `setup-mode`, and
`experimental-mode` are ignored by this branch.

## Java 26 Final-Field Warning

PacketEvents 2.13.0 reflectively attaches to Paper's network channel list. Java
26.0.1 permits this but prints a warning that a future Java release may block
it. The candidate completed startup, status handling, commands, reload, and clean
shutdown despite the warning.

To explicitly authorize this access on Java 26, add the following JVM argument
before `-jar`:

```text
--enable-final-field-mutation=ALL-UNNAMED
```

Treat a later JDK that actually blocks the operation as a failed certification:
update PacketEvents or the server launch policy on a disposable branch before
production use.

## Reporting a Reproducible Problem

Use the [mrfdev/AntiPopup issue tracker](https://github.com/mrfdev/AntiPopup/issues)
and include the Paper build, Java version, candidate version/JAR name, plugin
list, client and protocol-translation versions, relevant sanitized
configuration, and a sanitized complete log. Remove credentials, tokens,
private addresses, and other private values first.
