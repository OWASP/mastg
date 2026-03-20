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

Applications frequently interact with other apps by passing URLs to the operating system via [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open). If the URL being opened is constructed from untrusted input (such as an inbound Universal Link) and is not validated, it can lead to URI Scheme Hijacking.

## Steps

1. Locate the main compiled app binary (@MASTG-TECH-0058).
2. Disassemble or parse the binary imports and strings (@MASTG-TECH-0047).
3. Search for instances of the application importing or executing the `openURL:options:completionHandler:` selector.

## Observation

The output should contain the Objective-C selectors found in the binary, indicating whether the application imports and uses the iOS APIs responsible for opening external links.

## Evaluation

The test case fails if the application contains the `openURL:options:completionHandler:` selector and the decompiled code or dynamic analysis shows that an unvalidated, attacker-controllable URL is passed directly into this function without verifying the destination scheme.
