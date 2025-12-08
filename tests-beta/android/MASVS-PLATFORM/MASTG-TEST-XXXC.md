---
title: Use of Unverified Custom URL Schemes
platform: android
id: MASTG-TEST-0295
type: [static]
profiles: [L1, L2]
weakness: MASWE-0058
---

## Overview

Custom URL schemes (e.g., `myapp://...`) are not exclusive on Android—any app may register the same scheme/host. If the app relies on custom schemes for high-risk flows (e.g., authentication callbacks, password reset), another app can hijack or spoof those links.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0110 on the `AndroidManifest.xml` to list the `VIEW/BROWSABLE` intent-filters declaring non-HTTP(S) schemes.

## Observation

The output should include the manifest declarations and locations where the app constructs or consumes custom-scheme URIs workflows.

## Evaluation

The test fails if the app relies on custom URL schemes for high-risk operations that should use verified App Links (HTTPS) with Digital Asset Links. Mitigations include migrating to App Links for exclusivity or implementing robust in-app validation and user confirmation.
