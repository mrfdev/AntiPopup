# Maintaining Paper Compatibility

This project cannot promise compatibility with an API version that has not been
released. It does provide a low-risk update path: the production code has no
CraftBukkit/NMS mapping, no exact server-version implementation switch, pinned
dependencies, strict warning-free compilation, and final-JAR validation.

## Candidate Workflow for Paper Updates

1. Keep the working 26.2 build `008` source snapshot and exact JAR with its
   checksum. Preserve earlier rollback artifacts and all verification records.
2. Create a new disposable branch from the last certified source commit.
3. Set the exact released `paperApiVersion` and its `paperTarget` in
   `gradle.properties`.
   Treat that file as the release source of truth: increment the semantic patch
   version and three-digit build number once for a compatibility release, then
   update the exact Paper build, channel, JAR checksum, and verified JDKs there.
4. Refresh the dependency lock after reviewing the resolved change:

   ```bash
   ./gradlew dependencies --write-locks
   ```

   Verify that the maintained server JAR still matches the canonical filename
   and Paper API checksum:

   ```bash
   ./gradlew verifyMaintainedPaperJar \
     -PpaperJarPath=/absolute/path/Paper-26.3.jar
   ```

5. Select Oracle JDK 25.0.4.1 with `JAVA_HOME` and prepend its `bin` directory
   to `PATH` (see [local verification in the source repository](https://github.com/mrfdev/AntiPopup/blob/master/docs/agents/local-verification.md)). Run the
   canonical full rebuild:

   ```bash
   ./gradlew clean build --warning-mode all
   ```

   Run `./gradlew syncReleaseDocs` whenever the canonical release facts change.
   CI and release automation read the same facts through
   `./gradlew -q printReleaseMetadata`; do not hardcode a candidate JAR name.
6. Review every source/build deprecation. Java compilation uses
   `-Xlint:deprecation`, `-Xlint:removal`, and `-Werror`, so deprecated API use
   cannot silently enter a successful build.
7. Inspect the artifact name, manifest, `plugin.yml`, Java class major, and JAR
   contents. `verifyArtifact` automates the invariants this fork depends on.
8. Start a clean isolated Paper server using the real candidate JAR. Verify
   enable, plugin listing, clean disable, and logs on both recorded JDKs.
   The repeatable local smoke runner is described in
   [local verification in the source repository](https://github.com/mrfdev/AntiPopup/blob/master/docs/agents/local-verification.md).
9. Join directly with the matching native client. Verify the popup is absent and
   ordinary player chat still works. Do not use proxies, protocol translators,
   or older clients for certification.
10. Promote or merge only after all checks pass. Otherwise delete the candidate
    branch and continue deploying the last certified JAR.

Changing only `paperTarget` is not certification. PacketEvents must recognize
the released protocol, its `JOIN_GAME` wrapper must remain compatible, and the
native-client join behavior must be exercised. The current 26.3 build 40 ALPHA
candidate remains experimental until the direct native-client check passes.
Later versions require the same complete checklist.

The build accepts pinned STABLE, BETA, and ALPHA Paper artifacts; the channel is
recorded in the JAR manifest and release metadata. Non-STABLE draft releases
are marked as GitHub prereleases. An exact ALPHA API is still an experimental
target, and a successful build does not make it production-certified.

## Dependency Maintenance

`gradle.lockfile` makes local and CI resolution reproducible. Dependency and
GitHub Actions update proposals are automated with Dependabot, but each proposed
PacketEvents or Paper change still requires the full build and runtime checklist.
Do not reintroduce snapshot dependencies into the certified branch. The normal
`check` task verifies the lockfile, generated JAR metadata, manifest,
`plugin.yml`, Java class version, and marker-bounded current-release
documentation against `gradle.properties`.

## Release Boundary

Build `003` is the public, archived, unsupported full-feature fallback. Build
`005` remains an earlier internal rollback artifact. Build `006` is the known-
live minimalist rollback certified by its native-client join and chat test.
Build `007` is the previous Paper 26.2 compatibility release. Build `008`
updates the build JDK to 25.0.4.1 and runtime checks to 25.0.4.1 and 26.0.2.1
without changing Paper or plugin behavior. The operator confirmed build `008`
working before the 26.3 upgrade; tag `snapshot-paper-26.2-20260924` preserves that
source. Build `009` is the 26.3 ALPHA candidate, awaiting in-game testing.
Restore the original 26.2 server to roll back; never open an upgraded world with
an older server JAR. Keep all retained
JAR checksums with the deployment record, never load multiple builds together,
and do not apply modern maintenance promises to legacy build `003`.
