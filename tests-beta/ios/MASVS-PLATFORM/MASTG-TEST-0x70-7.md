---
platform: ios
title: Runtime Validation of the Apple App Site Association File
id: MASTG-TEST-0070-7
type: [dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If the live [`apple-app-site-association`](https://developer.apple.com/documentation/xcode/supporting-associated-domains) (AASA) file served by the domain is misconfigured or overly permissive, an unauthorized application may intercept Universal Links intended for the legitimate app. This test verifies whether the live server configuration correctly restricts Universal Link handling to the authorized application, which may differ from what is declared in the app bundle.

## Steps

1. Identify the domains listed in the app's `entitlements.plist` file.
2. Set up network monitoring to intercept the AASA file request from the live server (@MASTG-TECH-0062).
3. Request the file from `https://<domain>/.well-known/apple-app-site-association` and capture the response.

## Observation

The output should contain the JSON payload hosted by the server, indicating which App IDs and paths are permitted by the current live infrastructure.

## Evaluation

The test case fails if:

- The server returns a misconfigured AASA file or serves it over HTTP instead of HTTPS.
- The `appIDs` array contains incorrect Team IDs or Bundle IDs.
- The paths array contains overly permissive wildcards (`"*"`) for sensitive directories that the application is not designed to securely handle.
