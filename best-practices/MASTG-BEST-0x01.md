---
title: Managing Third-Party Dependencies with Known Vulnerabilities
alias: managing-third-party-dependencies-with-known-vulnerabilities-android
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0004]
---

Know exactly which third-party components go into every release, check them against vulnerability databases automatically on every build, and update or replace the ones with known vulnerabilities before you ship. A vulnerable library runs with the same permissions and data access as the rest of your app, so a publicly known flaw in it is often the easiest way in for an attacker (see @MASTG-KNOW-0004).

This only works if the dependency list is complete and reproducible. A scan of a dependency tree that changes between builds, or that misses components which don't come from Gradle, gives you a false sense of safety.

## Pin What You Build

Use [Gradle dependency locking](https://docs.gradle.org/current/userguide/dependency_locking.html) so that the versions you scanned are the versions you ship. Locks apply to direct and transitive dependencies, and the resulting `gradle.lockfile` should be committed so version changes show up in code review:

```kotlin
dependencyLocking {
    lockAllConfigurations()
}
```

Generate or refresh the lock state with `./gradlew :app:dependencies --write-locks` (repeat for each module), and only update it on purpose.

[Version catalogs](https://developer.android.com/build/migrate-to-catalogs) (`gradle/libs.versions.toml`) are useful on top of that because they keep declared versions in one place, which makes upgrades across modules easier. They don't pin transitive versions, though, so they aren't a replacement for locking.

## Generate an SBOM per Release

Produce a CycloneDX Software Bill of Materials (SBOM) for every release build and keep it with the release artifacts. You can use the [CycloneDX Gradle plugin](https://github.com/CycloneDX/cyclonedx-gradle-plugin) (`org.cyclonedx.bom`) or @MASTG-TOOL-0134 (see @MASTG-TECH-0130). Restrict the SBOM to the configurations that end up in the app, such as the release runtime classpath, so build-only and test dependencies don't drown out real findings.

Upload each SBOM to a platform such as @MASTG-TOOL-0132. It re-analyzes stored components regularly, so you also get notified when a new CVE is published for a version that's already in users' hands, not just when you build.

## Scan in CI and Fail the Build

Run a Software Composition Analysis (SCA) tool on every build, for example the @MASTG-TOOL-0131 Gradle plugin (see @MASTG-TECH-0131), and fail the build when a finding exceeds your threshold. The plugin never fails the build by default; set [`failBuildOnCVSS`](https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/configuration.html) to a value that matches your risk tolerance, for example `7.0` for high and critical findings.

Keep transitive dependencies in scope. Most vulnerable code in Android apps arrives as a dependency of a dependency, and Gradle may resolve it to a different version than the one the direct dependency declared.

Also check the [Google Play SDK Index](https://developer.android.com/distribute/sdk-index) for the SDKs you use. SDK providers can send you messages about problematic versions through Android Studio and Play Console, and Google Play can warn you before you publish a release that includes one.

## Cover What Gradle Doesn't See

Some components never appear in Gradle metadata, so SCA tools that read the build graph or the SBOM won't report them:

- Native libraries (`.so` files) compiled from vendored C/C++ sources, such as OpenSSL, libcurl or image codecs, or prebuilt ones copied into `jniLibs`.
- JAR or AAR files added from a local `libs/` directory without Maven coordinates.
- Closed-source SDKs that bundle their own copies of third-party libraries, sometimes repackaged under a different package name.

Track these components and their versions manually, add them to the SBOM, and ask SDK vendors for their own SBOM and security advisories. Cross-checking the final APK with @MASTG-TOOL-0130, which can generate an SBOM from the binary, helps catch components that the build-time view missed.

## Triage Findings, Don't Hide Them

Not every finding is exploitable in your app. The vulnerable function might be unused, or the dependency might only be on a build-time classpath. When that's the case, record the decision instead of silently suppressing it:

- Document the reason, for example as a [CycloneDX VEX](https://cyclonedx.org/capabilities/vex/) statement or as an analysis decision in @MASTG-TOOL-0132, which supports VEX.
- If you use a @MASTG-TOOL-0131 suppression file, write the justification in the `notes` element, scope it to the specific CVE and package instead of using broad regular expressions, and set an [`until` date](https://jeremylong.github.io/DependencyCheck/general/suppression.html) so it gets reviewed again.
- Re-check the decision when the app or the dependency changes, because code that wasn't reachable can become reachable.

!!! warning
    "Not reachable" is a claim about your current code. Upgrading is almost always cheaper and safer than proving that a vulnerability can't be triggered, so treat suppression as the exception.

## Keep Dependencies Updated

Update dependencies regularly, not only when a scanner complains. Tools such as [Dependabot](https://docs.github.com/en/code-security/dependabot) or [Renovate](https://docs.renovatebot.com/) can open update pull requests automatically. Small, frequent updates are easier to test than large jumps, and they make it much faster to ship a fix when a critical CVE lands. Remove dependencies you no longer use, and replace libraries that are no longer maintained, since they won't get security fixes.
