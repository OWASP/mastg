---
title: Entitlements in Embedded Provisioning Profile
platform: ios
id: MASTG-TEST-0x69-1
type: [static]
weakness: MASWE-0117
profiles: [L1, L2]
---

## Overview

If an app declares entitlements that are not justified by its core functionality, it gains access to privileged capabilities beyond standard sandbox restrictions — such as HomeKit, HealthKit, VPN configuration, or iCloud containers. This can lead to unauthorized access to sensitive user data, expanded attack surface, or privacy violations through excessive privilege.

On iOS, entitlements are declared in the provisioning profile (`embedded.mobileprovision`) as a cryptographically signed property list. They represent permissions granted by Apple's provisioning system for capabilities that go beyond the default app sandbox.

## Steps

1. Extract the app package contents using @MASTG-TECH-0058 and locate the embedded provisioning profile at `Payload/<appname>.app/embedded.mobileprovision`.
2. Decode the provisioning profile from binary CMS format to XML using @MASTG-TOOL-0063 and extract the `Entitlements` dictionary.

## Observation

The output should contain the `Entitlements` dictionary extracted from the `embedded.mobileprovision` file, listing all capabilities and permissions granted to the app by the provisioning profile.

## Evaluation

The test case fails if the app declares entitlements in the provisioning profile that are not justified by its core functionality, granting excessive access to privileged capabilities or sensitive user data.
