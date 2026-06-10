---
title: Entitlements for Unjustified Capability Exposure
platform: ios
id: MASTG-TEST-0362
type: [static, package, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0051]
knowledge: [MASTG-KNOW-0077]
---

## Overview

Entitlements are signed rights or privileges that enable an iOS app or app extension to use specific platform services, capabilities, or system integrations. Unlike purpose strings, entitlements are not limited to protected resources or user-facing privacy prompts. Some entitlements are mainly functional or security-related, while others may affect privacy by enabling access to personal data, shared containers, cloud data, home data, network capabilities, or system entry points.

If an app enables entitlements or capabilities that it does not need, it may gain unnecessary capability exposure outside the default sandbox model.

Capabilities configured in Xcode are signed into the app as entitlements, but the entitlement itself is not a runtime API call. The associated runtime surface depends on the service. Some examples of entitlements and their related APIs or entry points include:

| Entitlement or capability | Associated APIs or entry points |
| --- | --- |
| `com.apple.developer.healthkit` | `HKHealthStore`, HealthKit data types |
| `com.apple.security.application-groups` | `FileManager.containerURL(forSecurityApplicationGroupIdentifier:)`, `UserDefaults(suiteName:)` |
| `com.apple.developer.icloud-container-identifiers` | `CKContainer`, iCloud container APIs |
| `com.apple.developer.homekit` | `HMHomeManager` |
| `com.apple.developer.associated-domains` | `NSUserActivityTypeBrowsingWeb`, universal link continuation handlers |
| `com.apple.developer.networking.multicast` | `NWConnectionGroup` |
| `com.apple.developer.siri` | Intents, App Intents, Siri related entry points |

Other security-relevant or platform-service entitlements include Keychain Sharing, Data Protection, Push Notifications, Apple Pay, Network Extensions, NFC Tag Reading, and App Sandbox related entitlements.

An unused entitlement is not usually a direct privacy violation by itself. The risk is latent capability exposure. The app is signed with a right or privilege that can enable access to a service or capability if the app, an extension, bundled framework, feature flag, compromised code path, or later update starts using the related APIs. The clearest framing is least privilege: unneeded entitlements expand what the signed executable is allowed to do, even when the current test does not observe active use.

See @MASTG-KNOW-0077 for the relationship between Xcode capabilities, signed entitlements, and the framework APIs or entry points that use the corresponding service.

This test verifies whether the entitlements signed into the app are justified by the app's user-visible functionality and whether they create unnecessary privacy-relevant capability exposure.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0111 to extract entitlements from the app binaries, including the main app and app extensions.
3. Use @MASTG-TECH-0066 to look for framework APIs, shared container APIs, identifiers, or system entry points related to the identified entitlements.

> The entitlements signed into the app binary are the reliable source, because they are present regardless of how the app was built or signed. The `embedded.mobileprovision` file can carry the same entitlements, but it only exists when the packaged app includes a provisioning profile, such as development, ad-hoc, enterprise, or App Store submission builds before App Store processing. It is absent in simulator builds, App Store-distributed apps, and pseudo-signed builds (for example, when the binary is signed with `ldid`).

## Observation

The output should contain:

- The entitlements embedded in the app's code signatures.
- The entitlement-backed APIs, shared container APIs, identifiers, or system entry points referenced by the app binaries.

## Evaluation

The test case fails if the collected evidence shows that the app is signed with an entitlement without a reasonable connection to a user-visible feature, or if the entitlement creates a broader privacy-relevant capability, data access, or data sharing surface than the app's feature set justifies.

**Further Validation Required:**

Use the signed entitlements, referenced APIs, app metadata, visible app features, and relevant identifiers to determine whether each entitlement is justified, prioritizing entitlements that affect personal data, shared storage, cross-app communication, cloud data, home data, network behavior, or system entry points.

Consider the following when evaluating:

- Is the entitlement and its related API surface reasonably connected to the app's stated purpose or visible functionality?
- Does the entitlement create a personal data access, shared storage, cross-app communication, or system integration surface that is broader or more sensitive than the feature requires?
- Could the app use a narrower alternative instead, such as local app storage instead of an App Group container when cross-app or app-extension sharing is not required?

Static analysis can find unused code, SDK code, dead code, weak-linked frameworks, or APIs that only check availability and never use the entitlement-backed service. Treat an entitlement without matching API references as a failure only when the tester can reasonably connect the entitlement to an unnecessary capability or privacy-relevant data access path. Otherwise, treat it as requiring further validation.

Static analysis may also miss APIs or entry points that are resolved dynamically, hidden through obfuscation, placed in native libraries, loaded from frameworks, invoked by extensions, or only present in server-controlled or feature-flagged code paths. Treat missing API references as absence of evidence, not proof that the app never uses the entitlement-backed service.
