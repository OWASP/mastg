---
platform: ios
title: References to UIActivityViewController Initialization
id: MASTG-TEST-0x71
type: [static]
weakness: MASWE-0053
best-practices: [MASTG-BEST-0x71]
profiles: [L1, L2]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0081]
---

## Overview

If the app uses [`UIActivityViewController`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller) to share data via the system share sheet, any items passed to [`init(activityItems:applicationActivities:)`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/init(activityitems:applicationactivities:)) become available to all active system activities (AirDrop, Mail, Messages, social networks, etc.) unless the app explicitly excludes them via the [`excludedActivityTypes`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/excludedactivitytypes) property. Failing to restrict available activity types may allow sensitive data to be shared through channels the developer did not intend.

This test checks whether the app uses `UIActivityViewController` and whether `excludedActivityTypes` is set to prevent sensitive data from reaching inappropriate activity types.

## Steps

1. Extract the app as described in @MASTG-TECH-0058.
2. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary and search for references to `initWithActivityItems:applicationActivities:` and `excludedActivityTypes`.

## Observation

The output should contain a list of locations where `UIActivityViewController` is initialized and, if present, where `excludedActivityTypes` is set.

## Evaluation

The test case fails if the app passes sensitive data as activity items to `UIActivityViewController` without setting `excludedActivityTypes` to restrict the sharing to appropriate activity types.

Inspect each reported call site using @MASTG-TECH-0076.

- Determine what items are passed to `initWithActivityItems:applicationActivities:` and whether any of them contain or derive from sensitive data (for example, credentials, personal information, or health data).
- Determine whether `excludedActivityTypes` is set for the corresponding `UIActivityViewController` instance and whether the excluded types are appropriate for the sensitivity of the shared data.

Note that new system activity types are added by Apple in each iOS release and are not automatically excluded. Even if `excludedActivityTypes` is set, verify that it covers all activity types that are not appropriate for the shared content.
