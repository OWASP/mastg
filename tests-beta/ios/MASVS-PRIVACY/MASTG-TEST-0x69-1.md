---
title: Entitlements in Embedded Provisioning Profile
platform: ios
id: MASTG-TEST-0313
type: [static]
weakness: MASWE-0117
profiles: [P]
---

## Overview

iOS apps declare entitlements in the provisioning profile (`embedded.mobileprovision`), which grant special capabilities like HomeKit, Wallet, or application groups. These entitlements are embedded in a cryptographically signed property list and represent permissions beyond standard sandbox restrictions.

This test checks whether declared entitlements are appropriate and justified by the app's core functionality. Excessive entitlements indicate over-privileged apps and potential security risks.

## Steps

1. Sign the `.ipa` using @MASTG-TECH-0092 (if not already signed).
2. Extract the app package content (@MASTG-TECH-0058) and locate the embedded provisioning profile at `Payload/<appname>.app/embedded.mobileprovision`.
3. Decode the provisioning profile from binary CMS format to XML using the `security` tool (see @MASTG-TECH-0069).
4. Extract and review the Entitlements dictionary to identify all declared entitlements.

## Observation

The output should contain the entitlements from the `embedded.mobileprovision` file. This shows all special capabilities and permissions granted to the app. Common entitlements include:

- `com.apple.developer.homekit` – HomeKit access
- `com.apple.developer.healthkit` – HealthKit access
- `com.apple.application-identifier` – Unique app identifier
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