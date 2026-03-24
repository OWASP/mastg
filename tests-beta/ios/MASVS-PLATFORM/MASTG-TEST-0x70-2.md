---
platform: ios
title: References to Misconfigured Apple App Site Association File
id: MASTG-TEST-0070-2
type: [static, dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If the [apple-app-site-association](https://developer.apple.com/documentation/xcode/supporting-associated-domains) (AASA) file is misconfigured through overly permissive `appIDs`, wildcard path entries, or being served over HTTP, an attacker can register an unauthorized app to claim Universal Links intended for the legitimate application. This can lead to link hijacking, unintended data exposure, or the interception of sensitive payloads passed via Universal Links. This test checks whether the AASA file is served securely and scoped to only the authorized app identifiers and paths.

## Steps

1. Identify the allowed domains from the application's `com.apple.developer.associated-domains` entitlement.
2. Intercept or manually fetch the AASA file from the server's well-known directory from: `https://<domain>/.well-known/apple-app-site-association`.
3. Analyze the JSON response for the target `appID` and allowed paths.

## Observation

The output should contain a valid JSON payload with the `applinks` dictionary, specifying the `appIDs` and `components` (paths) allowed by the server.

## Evaluation

The test case fails if:

- The server returns a 404 or the file is served over HTTP instead of HTTPS.
- The file contains invalid JSON syntax.
- The `appIDs` array contains overly permissive wildcards or unauthorized team identifiers.
- The paths array exposes sensitive directories that the application is not designed to securely handle.
