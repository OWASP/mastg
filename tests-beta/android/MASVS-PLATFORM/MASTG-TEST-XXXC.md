---
title: Use of Custom URL Schemes
platform: android
id: MASTG-TEST-XXXC
type: [static]
profiles: [L1, L2]
weakness: MASWE-0058
knowledge: [MASTG-KNOW-0019]
---

## Overview

Custom URL schemes (e.g., `myapp://...`) are not exclusive on Android—any app may register the same scheme/host. If the app relies on custom schemes for high-risk flows (e.g., authentication callbacks, password reset), another app can hijack or spoof those links.

## Steps

1. Run @MASTG-TECH-XXXX on the manifest to look for Unverified Custom URL Schemes.

## Observation

The output should include the manifest declarations and locations where the app constructs or consumes custom-scheme URIs workflows.

## Evaluation

The test case fails if the app relies on custom URL schemes for high-risk operations that should use verified App Links (HTTPS) with Digital Asset Links.
