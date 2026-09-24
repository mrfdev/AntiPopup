# Troubleshooting

<!-- release-metadata:start -->
Current release: `14.0.3-009` in `1MB-AntiPopup-v14.0.3-009-j25-26.3.jar`, compiled for
Paper API `26.3.build.40-alpha` and Java `25`. Target server:
Paper `26.3` build `40` (`ALPHA`).
<!-- release-metadata:end -->

## The Plugin Will Not Load

Confirm that:

1. The server is Paper 26.3.
2. The runtime is Java 25 or newer.
3. Only one AntiPopup JAR is present in `plugins/`.
4. The JAR matches the current release metadata above.

`UnsupportedClassVersionError` means the runtime is older than Java 25; this
build uses class-file version 69. This branch no longer contains the old
`No valid injector found` NMS version switch.

## The Popup Returned

- Confirm the server is the pinned Paper 26.3 ALPHA build 40.
- Confirm the joining client is native 26.3 rather than an older or translated
  client.
- Confirm the connection does not pass through a proxy or protocol translator.
- Confirm startup logged `Initiated embedded PacketEvents for Paper 26.3.`
- Record the exact Paper build, Java version, client version, and JAR checksum.

If the popup still returns inside that exact boundary, stop the server and
keep the build `009` logs for diagnosis. Return to the original 26.2 server and
its retained build `008` JAR; do not downgrade the clone's upgraded world.
There is no configuration or reload path in the modern line.

## A Feature Is Missing After Updating to the Modern Release

That is expected if the missing feature is a command, configuration toggle,
reload/setup action, chat-report modification, metric, console filter,
legacy-client path, or translator integration. Builds `006` through `009` intentionally
contain none of them. If the server still requires that behavior, use archived
build `003` after reviewing its release notes, and never load both JARs together.

## A New Paper or Client Version Was Released

Do not assume that a successful build alone certifies a new protocol. Every
Paper or protocol update needs a candidate branch, a strict build, and a direct
matching-client login test. Build `009` still awaits its native 26.3 client test.

## Java 26 Final-Field Warning

PacketEvents 2.14.0 reflectively attaches to Paper's network channel list. Java
26.0.2.1 permits this but prints a warning that a future Java release may block
it. The runtime smoke tests retain the default JVM policy so this warning
remains visible when checking startup and clean shutdown.

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
