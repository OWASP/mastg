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

If an application processes [Universal Link](https://developer.apple.com/documentation/xcode/allowing-apps-and-websites-to-link-to-your-content) payloads at runtime without proper validation, an attacker can craft malicious inbound URLs to manipulate application state or exfiltrate sensitive data. Dynamically triggering the iOS URL routing mechanism allows testers to inject arbitrary payloads into the app's Universal Link processing flow, bypassing OS-level AASA domain checks that would normally filter out unauthorized links.

## Steps

1. Install the target application on a jailbroken device or Corellium instance (@MASTG-TECH-0056).
2. Use dynamic analysis techniques (@MASTG-TECH-0067) to inject a script into the application's memory space.
3. Programmatically invoke the non-public iOS URL routing APIs (e.g., `UIApplication.sharedApplication().openURL_()`) to force the operating system to evaluate and route a target URL scheme.

## Observation

The output should contain the result of the URL routing API call, including the target URL and a boolean value indicating whether the OS recognized the scheme and accepted the routing request.

## Evaluation

The test case fails if the script cannot trigger the API or the OS returns `false`, indicating it did not accept the routing payload.
