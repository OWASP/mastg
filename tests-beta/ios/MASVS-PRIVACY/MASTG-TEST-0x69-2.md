---
title: Entitlements Embedded in the App Binary
platform: ios
id: MASTG-TEST-0x69-2
type: [static]
weakness: MASWE-0117
profiles: [L1, L2]
---

## Overview

If an app embeds entitlements in its binary that are not justified by its core functionality, it gains access to privileged capabilities such as HomeKit, HealthKit, VPN configuration, or shared app groups. This can lead to unauthorized access to sensitive user data, an expanded attack surface, or privacy violations through excessive privilege.

On iOS, every signed app binary includes entitlements embedded during the code signing process. These can be extracted directly from the binary without access to the provisioning profile, providing an independent verification of the app's declared capabilities.

## Steps

1. Extract the app package contents using @MASTG-TECH-0058 and locate the main app binary at `Payload/<appname>.app/<appname>`.
2. Extract the entitlements from the signed binary using @MASTG-TECH-0111.

## Observation

The output should contain the entitlements plist extracted from the app binary's code signature, listing all capabilities and permissions granted to the app.

## Evaluation

The test case fails if the app binary embeds entitlements that are not justified by its core functionality, granting excessive access to privileged capabilities or sensitive user data.
