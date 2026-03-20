---
platform: ios
title: References to Universal Link Receiver Selector in Binary
id: MASTG-TEST-0070-3
type: [static]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

When a Universal Link is clicked, iOS passes the URL payload to the app via the [`application(_:continue:restorationHandler:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623072-application) delegate method. If the app implements this receiver, it accepts external payloads from Universal Links. Without proper validation of the incoming URL, an attacker can inject malicious payloads to trigger unauthorized actions or alter application state.

## Steps

1. Unzip the app package and locate the main compiled app binary (@MASTG-TECH-0058).
2. Disassemble or extract the strings and Objective-C method signatures from the binary (@MASTG-TECH-0047).
3. Search the extracted symbols for the `application:continue:restorationHandler:` (or equivalent `NSUserActivity`) delegate method.

## Observation

The output should contain the Objective-C selector confirming the compiled binary implements the Universal Link receiver method.

## Evaluation

The test case fails if the `application:continue:restorationHandler:` selector is found inside the binary.
