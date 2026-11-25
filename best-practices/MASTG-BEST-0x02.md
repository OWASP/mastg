---
title: Managing Third-Party Dependencies with Known Vulnerabilities
alias: managing-third-party-dependencies-with-known-vulnerabilities-ios
id: MASTG-BEST-0x02
platform: ios
knowledge: [MASTG-KNOW-0059]
---

Know exactly which third-party components go into every release, check them against vulnerability databases automatically on every build, and update or replace the ones with known vulnerabilities before you ship. Third-party code runs inside your app's sandbox with the same entitlements and data access as your own code, so a publicly known flaw in it is often the easiest way in for an attacker (see @MASTG-KNOW-0059).

iOS projects often mix several dependency managers and prebuilt binaries, and version information is largely gone once everything is compiled into the IPA. The lockfiles and the SBOM you keep at build time are what you'll rely on, so they need to be complete and match what you ship.

## Commit and Enforce Lockfiles

Commit the resolved-versions file of every dependency manager you use and build releases from it:

- **Swift Package Manager:** commit `Package.resolved`, which for Xcode projects lives under `<Project>.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/`. In CI, build with `xcodebuild -onlyUsePackageVersionsFromResolvedFile` so the build fails instead of silently resolving different versions. See [Adding package dependencies to your app](https://developer.apple.com/documentation/xcode/adding-package-dependencies-to-your-app).
- **CocoaPods:** commit `Podfile.lock` and use `pod install` in CI. Only run `pod update` when you mean to change versions, as explained in [pod install vs. pod update](https://guides.cocoapods.org/using/pod-install-vs-update.html).
- **Carthage:** commit `Cartfile.resolved` and use `carthage bootstrap`, which builds the versions it pins.

Lockfile changes then show up in code review like any other change.

## Generate an SBOM per Release

Produce a CycloneDX Software Bill of Materials (SBOM) for every release and keep it with the release artifacts. @MASTG-TOOL-0134 can generate one for SwiftPM projects (see @MASTG-TECH-0132), but check its coverage: per that technique, it doesn't resolve transitive SwiftPM dependencies or support CocoaPods and Carthage. Fill those gaps by scanning the lockfiles directly (see below) and by adding missing components to the SBOM yourself.

Upload each SBOM to a platform such as @MASTG-TOOL-0132. It re-analyzes stored components regularly, so you also get notified when a new CVE is published for a version that's already in users' hands, not just when you build.

## Scan in CI and Fail the Build

Run a Software Composition Analysis (SCA) tool on every build against all of your lockfiles, for example @MASTG-TOOL-0131 (see @MASTG-TECH-0133), and fail the build when a finding exceeds your threshold, for example with `--failOnCVSS 7`. If you use more than one dependency manager, scan each lockfile, since a single scan only covers one of them.

@MASTG-TOOL-0131's [SwiftPM](https://jeremylong.github.io/DependencyCheck/analyzers/swift.html), [CocoaPods](https://jeremylong.github.io/DependencyCheck/analyzers/cocoapods.html) and [Carthage](https://jeremylong.github.io/DependencyCheck/analyzers/carthage.html) analyzers are experimental, so don't take a clean report as proof that there's nothing to fix. Cross-check with the SBOM results and review high-risk components, such as networking, crypto, parsing and media libraries, by hand.

Keep transitive dependencies in scope. Many pods and packages pull in their own dependencies, and those are just as much part of your app.

## Cover What the Lockfiles Don't See

Some components never show up with a usable name and version in a lockfile or SBOM:

- Prebuilt XCFrameworks and static libraries, whether they're vendored into the repo, delivered as a SwiftPM [binary target](https://developer.apple.com/documentation/xcode/distributing-binary-frameworks-as-swift-packages), or a Carthage [binary-only framework](https://github.com/Carthage/Carthage/blob/master/Documentation/Artifacts.md).
- Closed-source SDKs that bundle their own copies of open-source libraries, such as OpenSSL, SQLite or protobuf, often under renamed symbols.
- C/C++ sources copied into the project instead of being pulled through a dependency manager.

Track these components and their versions manually, add them to the SBOM, and ask SDK vendors for their own SBOM and security advisories. [Verifying the signature of XCFrameworks](https://developer.apple.com/documentation/xcode/verifying-the-origin-of-your-xcframeworks) tells you who shipped the binary, not whether it contains vulnerable code.

!!! note
    [Privacy manifests](https://developer.apple.com/documentation/bundleresources/privacy-manifest-files) and Apple's [third-party SDK requirements](https://developer.apple.com/support/third-party-SDK-requirements/) describe data collection and required reason API usage, plus who signed the SDK. They don't contain any vulnerability information, so having them doesn't replace an SCA scan.

## Triage Findings, Don't Hide Them

Not every finding is exploitable in your app. The vulnerable function might never be called, or the affected platform might not be iOS. When that's the case, record the decision instead of silently suppressing it:

- Document the reason, for example as a [CycloneDX VEX](https://cyclonedx.org/capabilities/vex/) statement or as an analysis decision in @MASTG-TOOL-0132, which supports VEX.
- If you use a @MASTG-TOOL-0131 suppression file, write the justification in the `notes` element, scope it to the specific CVE and package, and set an [`until` date](https://jeremylong.github.io/DependencyCheck/general/suppression.html) so it gets reviewed again.
- Re-check the decision when the app or the dependency changes, because code that wasn't reachable can become reachable.

!!! warning
    "Not reachable" is a claim about your current code. Upgrading is almost always cheaper and safer than proving that a vulnerability can't be triggered, so treat suppression as the exception.

## Keep Dependencies Updated

Update dependencies regularly, not only when a scanner complains. [Renovate](https://docs.renovatebot.com/) supports [CocoaPods](https://docs.renovatebot.com/modules/manager/cocoapods/) and [SwiftPM](https://docs.renovatebot.com/modules/manager/swift/), and [Dependabot](https://docs.github.com/en/code-security/dependabot) supports SwiftPM, so you can get update pull requests automatically. Small, frequent updates are easier to test than large jumps, and they make it much faster to ship a fix when a critical CVE lands. Remove dependencies you no longer use, and replace libraries that are no longer maintained, since they won't get security fixes.
