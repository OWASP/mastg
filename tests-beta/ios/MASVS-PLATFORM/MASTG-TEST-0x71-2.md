---
platform: ios
title: Runtime Use of UIActivityViewController
id: MASTG-TEST-0x71-2
type: [dynamic]
weakness: MASWE-0053
best-practices: [MASTG-BEST-0x71]
profiles: [L1, L2]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0081]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x71.

If the app uses [`UIActivityViewController`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller) to share data via the system share sheet, any items passed to [`init(activityItems:applicationActivities:)`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/init(activityitems:applicationactivities:)) become available to all active system activities unless the app explicitly restricts them. This test verifies at runtime what data the app actually shares and whether `excludedActivityTypes` is configured appropriately.

## Steps

1. Deploy the app to a device or simulator as described in @MASTG-TECH-0056.
2. Launch the app with a runtime instrumentation tool such as @MASTG-TOOL-0039.
3. Hook the relevant `UIActivityViewController` APIs to observe what items are shared and which activity types are excluded.
4. Trigger the code paths that create and present the `UIActivityViewController`.
5. Inspect the captured runtime arguments.

Typical APIs to monitor include:

- `UIActivityViewController initWithActivityItems:applicationActivities:` to capture the items being shared and optional custom `UIActivity`.
- `UIActivityViewController setExcludedActivityTypes:` to capture the excluded activity types.

## Observation

The output should show the activity items passed to `UIActivityViewController` and the list of excluded activity types, if any.

## Evaluation

The test case fails if the app passes sensitive data as activity items to `UIActivityViewController` without setting `excludedActivityTypes` to restrict the sharing to appropriate activity types.

- Determine whether the `activityItems` array contains or derives from sensitive data (for example, credentials, personal information, or health data).
- Determine whether `excludedActivityTypes` is set for the corresponding `UIActivityViewController` instance and whether the excluded types are appropriate for the sensitivity of the shared data.
- If `excludedActivityTypes` is not set or is `nil`, all system activity types are available to the user, potentially allowing sensitive data to be sent via AirDrop, posted to social networks, or otherwise shared in unintended ways.
- If the parameter `applicationActivities` is not `nil` the data is shared with one or multiple custom `UIActivity`. Use static or dynamic techniques to verify if the data is handle securely.
