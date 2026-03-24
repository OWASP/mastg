---
platform: ios
title: References to openURL Selector in Binary
id: MASTG-TEST-0070-5
type: [static]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If the app passes a URL constructed from untrusted input (such as an inbound Universal Link) into [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open) without validating the destination scheme, an attacker can trigger URI Scheme Hijacking. This test checks whether the binary contains this API and whether the URL passed to it can be attacker controlled.

## Steps

1. Obtain the main app binary (@MASTG-TECH-0058).
2. Extract the Objective-C method signatures and binary imports from the binary  using (@MASTG-TECH-0076).
3. Search the extracted symbols for the `openURL:options:completionHandler:` selector.

## Observation

The output should contain the Objective-C selectors found in the binary, indicating whether the application imports and uses the iOS APIs responsible for opening external links.

## Evaluation

The test case fails if the application contains the `openURL:options:completionHandler:` selector and the decompiled code or dynamic analysis shows that an unvalidated, attacker-controllable URL is passed directly into this function without verifying the destination scheme.
