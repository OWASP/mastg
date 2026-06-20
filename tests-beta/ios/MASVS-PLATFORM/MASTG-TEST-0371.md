---
platform: ios
title: Missing Source Validation in Custom URL Scheme Handlers
id: MASTG-TEST-0371
type: [static, code]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0045, MASTG-BEST-0055]
knowledge: [MASTG-KNOW-0079]
apis: ["scene:openURLContexts:", "UIOpenURLContext.options.sourceApplication"]
---

## Overview

Custom URL scheme handlers that perform security-sensitive operations should validate the source application before acting on incoming requests (@MASTG-KNOW-0079). The [`sourceApplication`](https://developer.apple.com/documentation/uikit/uiscene/connectionoptions/sourceapplication) property provides the bundle ID of the calling app, allowing the handler to check it against an allowlist before processing.

Apple only populates `sourceApplication` when the calling app belongs to the same Apple Developer Team. Apps from other teams or system apps (e.g. Notes, Safari) will have `sourceApplication` set to `nil`. This is an Apple platform limitation, but it still allows verifying that the URL was opened by one of your own apps, which is useful when a URL scheme triggers privileged actions that should only be accessible from within your app suite.

This test checks whether the app's URL scheme handler reads and validates `sourceApplication` before performing sensitive operations.

!!! note
    If the app intentionally allows any app on the device to trigger the URL scheme (for example, a publicly documented deep-link scheme with no privileged actions), source validation may not be required and this test may not apply.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain the disassembly of any `scene(_:openURLContexts:)` implementation found in the binary, including any references to `sourceApplication` and `allowedSources` or equivalent allowlist properties.

## Evaluation

The test case fails if any URL scheme handler is found that does not read `UIOpenURLContext.options.sourceApplication` before performing security-sensitive or irreversible operations.
