---
platform: ios
title: Runtime Injection of Universal Link Payloads
id: MASTG-TEST-0070-6
type: [dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If an application processes [Universal Link](https://developer.apple.com/documentation/xcode/allowing-apps-and-websites-to-link-to-your-content) payloads at runtime without proper validation, an attacker can craft malicious inbound URLs to manipulate application state or exfiltrate sensitive data. This test verifies whether the app's Universal Link processing flow can be triggered with arbitrary payloads that bypass OS level AASA domain checks, and whether the app validates those payloads before acting on them.

## Steps

1. Install the target application on a jailbroken device (@MASTG-TECH-0056).
2. Launch the app and attach a Frida script to hook the `application:continue:restorationHandler:` delegate method.
3. Invoke `UIApplication.sharedApplication().openURL_()` with a crafted URL to trigger the Universal Link processing flow, bypassing OS-level AASA validation.

## Observation

The output should contain the result of the URL routing API call, including the target URL and a boolean value indicating whether the OS recognized the scheme and accepted the routing request.

## Evaluation

The test case fails if it returns `true` and the app processes the injected URL without validating its components, resulting in unintended application behavior or state change.
