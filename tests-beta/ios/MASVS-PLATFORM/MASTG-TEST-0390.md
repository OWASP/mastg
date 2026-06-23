---
platform: ios
title: Full Access Requested by a Custom Keyboard Extension
id: MASTG-TEST-0390
type: [static, code, manual]
weakness: MASWE-0061
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0069]
profiles: [L2]
knowledge: [MASTG-KNOW-0082, MASTG-KNOW-0141]
---

## Overview

A [custom keyboard](https://developer.apple.com/documentation/uikit/creating-a-custom-keyboard) is an app extension that replaces the system keyboard across all apps on the device (see @MASTG-KNOW-0141). By default it runs without "Full Access", which keeps it from making network requests or reaching shared containers. A keyboard requests "Full Access" with the [`RequestsOpenAccess`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsextension/nsextensionattributes/requestsopenaccess) key in its `Info.plist`, and once the user grants it the keyboard can check the [`hasFullAccess`](https://developer.apple.com/documentation/uikit/uiinputviewcontroller/hasfullaccess) property and then send what the user types off the device.

Because a custom keyboard receives the characters the user types in other apps, a keyboard that requests Full Access and transmits input is in a position to exfiltrate sensitive data such as passwords, messages, or payment details. Apple requires Full Access to be justified and limited to features that genuinely need it (for example, network-based predictions the user opted into).

This test checks whether a custom keyboard extension bundled with the app requests Full Access, and whether it uses that access to transmit typed data off the device or to reach shared storage. The analysis targets the keyboard extension binary (the `.appex` under `PlugIns/`) and its `Info.plist`.

## Steps

1. Use @MASTG-TECH-0058 to extract the app package, including the keyboard extension in the `PlugIns/*.appex` bundle.
2. Inspect the keyboard extension's `Info.plist` for the `RequestsOpenAccess` key under `NSExtension > NSExtensionAttributes`.
3. Use @MASTG-TECH-0066 to look for the relevant APIs in the keyboard extension binary, in particular `hasFullAccess` and networking APIs such as `URLSession`.

## Observation

The output should indicate:

- Whether the keyboard extension's `Info.plist` sets `RequestsOpenAccess` to `true`.
- Whether the keyboard binary references `hasFullAccess`.
- Whether the keyboard binary references networking or shared-container APIs.

## Evaluation

The test case fails if the keyboard extension requests Full Access (`RequestsOpenAccess` is `true`) and uses it to send typed data off the device or to write it to a shared container, without a feature that justifies the access.

**Further Validation Required:**

Inspect the keyboard extension implementation using @MASTG-TECH-0076 to determine:

- Whether the keyboard transmits the characters it receives (for example, in `insertText(_:)` or `textDidChange(_:)` handlers) when `hasFullAccess` is `true`.
- Whether the data it transmits or stores is sensitive.
- Whether Full Access is required by a user-facing feature, or requested without a justifying use.
