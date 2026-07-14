# Troubleshooting

## The Plugin Will Not Load

Confirm that:

1. The server is Paper 26.2.
2. The runtime is Java 25 or newer.
3. Only one AntiPopup JAR is present in `plugins/`.
4. The file name is `1MB-AntiPopup-v14.0.0-006-j25-26.2.jar`.

`UnsupportedClassVersionError` means the runtime is older than Java 25; this
build uses class-file version 69. This branch no longer contains the old
`No valid injector found` NMS version switch.

## The Popup Returned

- Confirm the server is the certified Paper 26.2 build line.
- Confirm the joining client is native 26.2 rather than an older or translated
  client.
- Confirm the connection does not pass through a proxy or protocol translator.
- Confirm startup logged `Initiated embedded PacketEvents for Paper 26.2.`
- Record the exact Paper build, Java version, client version, and JAR checksum.

If the popup still returns inside that exact boundary, stop the server and
keep the build `006` logs for diagnosis. 1MoreBlock can restore its retained
build `005` JAR. Public users may try archived build `003`, but it restores the
full commands/config/setup/chat-report/bStats/Log4j/legacy-client surface and is
unsupported. There is no configuration or reload path in build `006` itself.

## A Feature Is Missing After Updating to Build 006

That is expected if the missing feature is a command, configuration toggle,
reload/setup action, chat-report modification, metric, console filter,
legacy-client path, or translator integration. Build `006` intentionally
contains none of them. If the server still requires that behavior, use archived
build `003` after reviewing its release notes, and never load both JARs together.

## A New Paper or Client Version Was Released

Do not assume that a successful build alone certifies a new protocol. Paper
26.2.1, 26.3, and later versions need a disposable candidate branch, a strict
build, and a direct matching-client login test. Continue using the last
certified JAR until that work passes.

## Java 26 Final-Field Warning

PacketEvents 2.13.0 reflectively attaches to Paper's network channel list. Java
26.0.1 permits this but prints a warning that a future Java release may block
it. Build `006` completed startup, plugin listing, and clean shutdown despite
the warning.

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
and include the Paper build, Java version, AntiPopup version/JAR name, plugin
list, native client version, JAR checksum, and a sanitized complete log. Remove
credentials, tokens, private addresses, and other private values first.
