# Local Build and Runtime Verification

## Build JDK

Use the installed Oracle JDK 25.0.4.1 for the Gradle launcher and compiler:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.0.4.1.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
java -version
./gradlew --version
```

`gradle.properties` supplies the release version, build number, exact JDK
versions, Paper build and checksum. The Java toolchain reads `JAVA_HOME`;
automatic discovery and downloads are disabled. `verifyBuildJdk` checks the
selected compiler's full release version before compilation. Both `--release`
and the toolchain language version remain 25 (class-file version 69).

CI and the draft-release workflow share `.github/actions/setup-release-jdk`.
It installs the exact Oracle Linux x64 archive, verifies
`buildJdkLinuxX64Sha256`, and exports `JAVA_HOME` and `PATH`. This avoids
`setup-java@v5`'s SemVer parser, which rejects Oracle's four-part `25.0.4.1`
release number. Keep the archive checksum in sync when changing the build JDK.

For a compatibility release, increment the semantic patch and three-digit
build number once. Keep the version fixed while rerunning failed checks.
Preserve earlier JARs and verification records before cleaning `build/`.

```bash
./gradlew syncReleaseDocs
./gradlew clean build --warning-mode all
```

This is the canonical full rebuild. It runs the standard Gradle check lifecycle,
including `test`, `verifyBuildJdk`, `verifyArtifact` and `verifyReleaseDocs`.
There are currently no unit-test sources, so Gradle reports `test NO-SOURCE`.
The artifact and documentation checks still execute. The project produces one
shaded deployable JAR, reused unchanged for both runtime tests.

## Paper Smoke Tests

Use Python 3.11 or newer and an existing Paper template containing the canonical
`Paper-26.3.jar`, its downloaded libraries/cache, and an already accepted
`eula.txt`. The runner verifies the pinned checksum before creating fresh
loopback-only instances under ignored `run/verification/`; it does not modify
the template, old test records, or a live server.

```bash
python3 scripts/verify-paper-runtime.py \
  --paper-home /absolute/path/to/pinned-paper-template \
  --jdk25-home /Library/Java/JavaVirtualMachines/jdk-25.0.4.1.jdk/Contents/Home \
  --jdk26-home /Library/Java/JavaVirtualMachines/jdk-26.0.2.1.jdk/Contents/Home
```

The runner sets `JAVA_HOME` and `PATH` separately for each runtime. It checks
exact Java versions, Paper startup, embedded PacketEvents load/init/terminate,
release metadata, plugin listing/version, the absence of removed commands,
a network server-list response, absence of plugin state, and clean shutdown.
It retains the console logs, status response and JSON result per runtime.
Java 26 runs with its default final-field policy to expose PacketEvents warnings.

The live runtime is Java 26. These server checks do not assert that a graphical
client displayed no popup or that a real player sent chat. Repeat the native
26.3 popup/chat check during staging as described in [maintenance](../maintenance.md).
Historical native-client results for builds `005` and `006` remain unchanged.

## Verification Record: 2026-09-24, Candidate 14.0.3-009

- Target: Paper 26.3 ALPHA build 40 and exact API `26.3.build.40-alpha`.
- Embedded PacketEvents: 2.14.0, with explicit 26.3 protocol support.
- Canonical `./gradlew clean build --warning-mode all`: PASS, all eight
  actionable tasks executed using Oracle JDK `25.0.4.1+1-LTS-5`.
  `test` and `compileTestJava` remain `NO-SOURCE`.
- `verifyMaintainedPaperJar`: PASS against the PaperMC build 40 SHA-256
  `49399919246cbf443efc8507447dc948eb7477c41be560b0e87e2a455aff824a`.
- Artifact: 5,157,521 bytes and 1,896 classes. All five AntiPopup classes have
  major version 69; no bundled class exceeds major version 69.
- Plugin SHA-256:
  `24d2b36b094a69216ae3acf182bca5baab5a39bbf658ad318b162906a23ab722`.

| Runtime | Paper | Smoke checks | Shutdown |
| --- | --- | --- | --- |
| Oracle `25.0.4.1+1-LTS-5` | 26.3 ALPHA build 40 | All nine passed | Exit 0 |
| Oracle `26.0.2.1+1-7` | 26.3 ALPHA build 40 | All nine passed | Exit 0 |

Both disposable tests used the same candidate JAR, loopback binding,
`online-mode=true`, and `enforce-secure-profile=true`. Neither logged an error
or exception, and neither created AntiPopup state. Logs, status responses and
JSON results are under `run/verification/14.0.3-009/20260924-232224/`.
The full build and artifact inspection records are in its parent directory.

The actual cloned world also started via the updated launch script on Java
26.0.2.1, listed only AntiPopup `14.0.3-009`, and shut down cleanly with exit 0.
Its first-run log is `run/verification/14.0.3-009/staging-first-start.log`.
The clone retains `enforce-secure-profile=false` from its source for the
native-client popup test, as described in [26.3 testing](../paper-26.3-testing.md).

Observed upstream warnings remain Gradle native access, JOML Unsafe access,
OSHI's unrecognized macOS 27 name, and PacketEvents' Java 26 final-field mutation.
The runtime smoke tests use the default JVM access policy.

The operator subsequently confirmed a vanilla Minecraft 26.3 join with no popup.
Console review recorded successful join/rejoin at 23:40:26 and 23:40:39
(Europe/Amsterdam) on 2026-09-24, normal disconnects, and no errors or exceptions.
The installed JAR checksum remains the one recorded above. Evidence is retained
in `run/verification/14.0.3-009/native-client-server.log`,
`native-client-console.txt`, and `native-client-result.json`.
Popup suppression passes this native-client check. Ordinary-player chat was not
reported or present in the log, so it remains unverified. Paper build 40 remains
an ALPHA target.

## Verification Record: 2026-09-15, Release 14.0.2-008

Retain this dated record when updating the active instructions above.

- Host: macOS 27.0, Apple silicon.
- Gradle 9.4.1 launcher, daemon and compiler: Oracle
  `25.0.4.1+1-LTS-5`, selected through the JDK 25.0.4.1 `JAVA_HOME` and `PATH`.
- Canonical `./gradlew clean build --warning-mode all`: PASS, all eight
  actionable tasks executed. `test` and `compileTestJava`: `NO-SOURCE`.
- `verifyMaintainedPaperJar`: PASS for the existing Paper 26.2 stable build 84
  template, SHA-256
  `defe82c1c89067186895de34cf32983e9f5a2ea387cfe7597c020faebb98ca16`.
- Compiler guard: a deliberate `-PbuildJdkVersion=25.0.4` mismatch was rejected;
  the recorded 25.0.4.1 version passed.
- Artifact: 4,757,610 bytes, 1,736 classes. All five AntiPopup classes have
  major version 69 and are byte-for-byte identical to retained build `007`.
  No bundled class exceeds major version 69.
- Plugin JAR SHA-256:
  `e555d34bee12edfff3a09cbc25c34944a8140c48b73b9511885ff61daf683f9a`.

| Runtime | Paper | Smoke checks | Shutdown |
| --- | --- | --- | --- |
| Oracle `25.0.4.1+1-LTS-5` | 26.2 build 84 | All nine passed | Exit 0 |
| Oracle `26.0.2.1+1-7` | 26.2 build 84 | All nine passed | Exit 0 |

Both tests used the same JAR checksum. The checks covered startup, plugin load,
runtime metadata, plugin listing, plugin version, removed-command absence,
network server status, absence of AntiPopup state and clean shutdown. Follow-up
inspection confirmed `online-mode=true`, `enforce-secure-profile=true` and
loopback binding remained intact. Neither run logged an error or exception.

The local evidence is under `run/verification/14.0.2-008/`: `full-build.log`,
`gradle-version.txt`, `jdk-mismatch-check.log`, `artifact-verification.json`, and
the successful `20260915-002432/` runtime logs/results. Earlier attempts remain
there too; the runner's assertions were corrected to match Paper's version
banner and unknown-command text, then the full runtime matrix was rerun.

Observed upstream warnings were Gradle native access, JOML's deprecated Unsafe
access, OSHI's unrecognized macOS 27 name, and PacketEvents' existing Java 26
final-field mutation. The Java 26 test used the default JVM policy. No new
native-client popup/chat certification is claimed by this record.
