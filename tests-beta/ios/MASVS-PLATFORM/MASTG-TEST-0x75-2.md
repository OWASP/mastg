---
platform: ios
title: References to Custom URL Scheme Handler Methods
id: MASTG-TEST-0x75-2
type: [static, code, manual]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0045]
knowledge: [MASTG-KNOW-0079]
apis: [application:openURL:options:, UIApplicationOpenURLOptionsSourceApplicationKey]
---

## Overview

If the app implements `application:openURL:options:` without validating the URL and its parameters, any app can trigger potentially sensitive actions via the registered custom URL scheme (@MASTG-KNOW-0079). The `options` dictionary passed to this method contains the source application identifier (`UIApplicationOpenURLOptionsSourceApplicationKey`), which the app can use to restrict which callers are allowed. Without checking the source and validating URL parameters, a malicious app could trigger unauthorized operations in the target app.

This test checks whether the app implements URL scheme handler methods and whether those handlers validate the incoming URL and its parameters before processing them.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations in the binary where URL scheme handler methods (such as `application:openURL:options:`) are implemented.

## Evaluation

The test case fails if any URL scheme handler method is found that processes URL parameters without proper validation.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether the handler validates incoming URL data:

- Determine whether the URL scheme and host are validated against an allowlist of expected values before processing.
- Determine whether URL parameters are validated and sanitized before being used in security-sensitive operations.
- Determine whether the source application (`UIApplicationOpenURLOptionsSourceApplicationKey`) is verified when the handler performs security-sensitive or irreversible operations.
