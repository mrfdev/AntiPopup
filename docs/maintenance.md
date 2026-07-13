# Maintaining Paper Compatibility

This project cannot promise compatibility with an API version that has not been
released. It does provide a low-risk update path: the production code has no
CraftBukkit/NMS mapping, no exact server-version implementation switch, pinned
dependencies, strict deprecation checks, unit tests, and final-JAR validation.

## Candidate Workflow for 26.2.1 or 26.3

1. Keep the last certified branch and JAR unchanged.
2. Create a new disposable branch from that commit.
3. Set the exact released `paperApiVersion` and its `paperTarget` in
   `gradle.properties`.
   Increment the three-digit `pluginBuild` for each candidate and change
   `pluginVersion` only for an intentional release-version change.
4. Refresh the dependency lock after reviewing the resolved change:

   ```bash
   ./gradlew dependencies --write-locks
   ```

5. Run the strict build:

   ```bash
   ./gradlew clean build --warning-mode all
   ```

6. Review every source/build deprecation. Java compilation uses
   `-Xlint:deprecation`, `-Xlint:removal`, and `-Werror`, so deprecated API use
   cannot silently enter a successful build.
7. Inspect the artifact name, manifest, `plugin.yml`, Java class major, and JAR
   contents. `verifyArtifact` automates the invariants this fork depends on.
8. Start a clean isolated Paper server using the real candidate JAR. Verify
   enable, `/antipopup info`, local-console reload, clean disable, and logs.
9. Test with real clients: status ping, join popup, ordinary signed chat,
   decorated/unsigned formatting, fully and partially filtered chat, and every
   protocol translator used in production.
10. Promote or merge only after all checks pass. Otherwise delete the candidate
    branch and continue deploying the last certified JAR.

Changing only `paperTarget` is not certification. PacketEvents must also
recognize the released protocol and runtime chat behavior must be exercised.

## Dependency Maintenance

`gradle.lockfile` makes local and CI resolution reproducible. Dependency and
GitHub Actions update proposals are automated with Dependabot, but each proposed
PacketEvents or Paper change still requires the full build and runtime checklist.
Do not reintroduce snapshot dependencies into the certified branch.

## Release Boundary

The `paper-only-26.2` branch is a candidate lane. `master` is the rollback lane
until the candidate has passed server and player testing and is deliberately
promoted. Keep the tested JAR checksum with the deployment record so the exact
binary can be restored.
