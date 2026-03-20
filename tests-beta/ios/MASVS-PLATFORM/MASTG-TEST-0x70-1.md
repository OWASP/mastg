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

Universal Links require the application to explicitly declare which domains it is authorized to open. This is configured in the app's entitlements using the [`com.apple.developer.associated-domains`](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_associated-domains) key. If a developer uses a wildcard (e.g., `*.example.com`) or includes an untrusted third-party domain, it significantly expands the attack surface. An attacker who compromises a forgotten or unsecured subdomain can intercept Universal Links intended for this application, leading to unauthorized actions or data leakage.

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058).
2. Extract the entitlements from the binary's code signature to view the configured capabilities.
3. Search the extracted plist for the `com.apple.developer.associated-domains` key.

## Observation

The output should contain the `associated-domains` array extracted from the entitlements file, listing all the domains the OS will trust for Universal Links.

## Evaluation

The test case fails if the `associated-domains` array contains wildcards (`*`) for domains that the organization does not strictly control, or if it contains unrecognized or untrusted third-party domains.
