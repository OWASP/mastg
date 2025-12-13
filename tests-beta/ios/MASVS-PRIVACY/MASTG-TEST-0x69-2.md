---
title: Entitlements Embedded in the App Binary
platform: ios
id: MASTG-TEST-0313
type: [static]
weakness: MASWE-0117
profiles: [P]
---

## Overview

Entitlements can also be extracted directly from the compiled app binary using code signing tools. When you have the app binary (either from an IPA or an installed app on a jailbroken device), you can inspect the entitlements without needing to access the provisioning profile separately.

Every signed iOS app includes entitlements embedded in the binary by the code signing process. These entitlements grant the app special capabilities such as HomeKit, HealthKit, or access to application groups.

This test checks whether the entitlements embedded in the app binary are appropriate and justified by the app's core functionality. Excessive entitlements indicate over-privileged apps with potential security and privacy risks.

## Steps

1. Unzip the app package (@MASTG-TECH-0058) and locate the main app binary at `Payload/<appname>.app/<appname>` (or the main executable in the bundle).
2. Extract the entitlements from the signed binary using the `codesign` tool (see @MASTG-TECH-0069).
3. Review the entitlements plist to identify all declared capabilities.

## Observation

The output should contain `entitlements.plist` extracted directly from the app binary. This shows all special capabilities and permissions granted to the app via code signing. Common entitlements include:

- `com.apple.developer.homekit` – HomeKit access
- `com.apple.developer.healthkit` – HealthKit access
- `com.apple.developer.push-notification-service` – Push notifications
- `com.apple.security.application-groups` – Shared app groups
- `com.apple.developer.icloud-container-identifiers` – iCloud container access
- `com.apple.developer.associated-domains` – Associated domains
- `com.apple.developer.networking.vpn` – VPN configuration
- `com.apple.developer.siri` – Siri integration
- `com.apple.developer.maps` – Maps framework entitlements
- `com.apple.security.files.bookmarks.app-scope` – Bookmark scoped file access

## Evaluation

The test fails if the app declares entitlements that are not justified by its core functionality.