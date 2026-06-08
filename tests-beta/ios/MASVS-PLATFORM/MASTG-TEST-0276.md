---
platform: ios
title: Use of the iOS General Pasteboard
id: MASTG-TEST-0276
type: [static, code, manual]
weakness: MASWE-0053
threat: [app]
prerequisites:
- identify-sensitive-data
profiles: [L2]
knowledge: [MASTG-KNOW-0083]
---

## Overview

This test checks whether the app uses the systemwide general @MASTG-KNOW-0083, which is persistent across device restarts and app uninstalls and is accessible by all foreground apps and, in some cases, other devices. Placing sensitive data here may pose a privacy risk.

The test statically analyzes the code for use of the general pasteboard ([`UIPasteboard.general`](https://developer.apple.com/documentation/uikit/uipasteboard/general)) and checks whether sensitive data is written using any of the following methods:

- [`addItems`](https://developer.apple.com/documentation/uikit/uipasteboard/additems(_:))
- [`setItems`](https://developer.apple.com/documentation/uikit/uipasteboard/setitems(_:options:))
- [`setData`](https://developer.apple.com/documentation/uikit/uipasteboard/setdata(_:forpasteboardtype:))
- [`setValue`](https://developer.apple.com/documentation/uikit/uipasteboard/setvalue(_:forpasteboardtype:))

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if calls are made to `UIPasteboard.generalPasteboard` and sensitive data is written to it.

**Further Validation Required:**

Since determining what constitutes sensitive data is context-dependent, inspect each reported code location using @MASTG-TECH-0076:

- Determine whether the data written to the pasteboard is sensitive (e.g., passwords, tokens, or personal data).
