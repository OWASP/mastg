---
platform: ios
title: References to Wildcard in the Associated Domains Entitlement
id: MASTG-TEST-0070-1
type: [static]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If the app's [`com.apple.developer.associated-domains`](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_associated-domains) entitlement includes a wildcard (e.g., `*.example.com`) or an untrusted third-party domain, an attacker who compromises a forgotten or unsecured subdomain can serve a fraudulent Apple App Site Association (AASA) file and intercept Universal Links intended for this app. This can lead to unauthorized actions or sensitive data leakage. This test checks whether the associated domains entitlement is scoped to only explicitly trusted domains fully under the organization's control.

## Steps

1. Extract the app package contents using @MASTG-TECH-0058 and locate the `entitlements.plist` file at `Payload/<appname>.app/entitlements.plist`.
2. Convert the `entitlements.plist` to a readable format using @MASTG-TECH-0x70-1.
3. Search the extracted plist for the `com.apple.developer.associated-domains` key.

## Observation

The output should contain the `associated-domains` array extracted from the entitlements file, listing all the domains the OS will trust for Universal Links.

## Evaluation

The test case fails if the `associated-domains` array contains wildcards (`*`) for domains that the organization does not strictly control, or if it contains unrecognized or untrusted third-party domains.
