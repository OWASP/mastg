---
platform: ios
title: Missing Input Validation in Universal Link Handlers
id: MASTG-TEST-0395
type: [static, code, manual]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0072]
knowledge: [MASTG-KNOW-0080]
apis: [continueUserActivity, webpageURL, NSUserActivityTypeBrowsingWeb, URLComponents, URLQueryItem]
---

## Overview

Apps that support universal links must validate and sanitize the path and query parameters of the incoming URL before using them in security-sensitive operations (@MASTG-KNOW-0080). iOS verifies the link's **domain** against the website's Apple App Site Association file at install time, but it does not validate the rest of the URL. The path and query parameters remain caller-controlled: anyone can craft a link to the verified domain with arbitrary values and get the user to open it (for example, by sending it in a message or embedding it in a web page).

Without input validation, the universal link handler acts on attacker-influenced values, bypassing the expected business logic constraints. For example, for a verified domain `example.com`:

- `https://example.com/transfer?amount=-1` or `amount=9999999` to bypass business logic bounds.
- `https://example.com/open?path=../../private/secrets.txt` for path traversal if the value is used in file operations.
- `https://example.com/search?q=<script>alert(1)</script>` for script injection if the value is rendered in a WebView.
- `https://example.com/showPage?page=https://attacker.com` for loading attacker-controlled content if the value is used in a WebView or network request.

The handler receives the URL through an `NSUserActivity` with an `activityType` of `NSUserActivityTypeBrowsingWeb`, read from its `webpageURL` property. Depending on the app's architecture, this is delivered via `application(_:continue:restorationHandler:)`, `scene(_:continue:)`, or SwiftUI's `onContinueUserActivity(NSUserActivityTypeBrowsingWeb, perform:)`. Unlike custom URL schemes (@MASTG-TEST-0370), universal links carry no `sourceApplication` value, so the handler cannot identify the origin of the request.

This test checks whether the app's universal link handler validates the URL path and parameters before acting on them.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain the disassembly of the universal link handler, showing whether it performs type conversion, bounds checking, or sanitization on the path and query parameters read from `webpageURL`.

## Evaluation

The test case fails if the universal link handler uses path or query parameter values directly without performing adequate validation before acting on them.

**Further Validation Required:**

Inspect each reported handler using @MASTG-TECH-0076, looking for cases such as:

- **Missing type conversion:** a numeric parameter is used as a raw string without converting it (e.g., not calling `Int.init` or `Double.init`).
- **Missing bounds or range checks:** the value is used without verifying it falls within an expected range.
- **Missing sanitization:** special characters are not sanitized before the value is used in a sink such as a file path, SQL query, or WebView.
- **Missing allowlist checks:** a path or parameter that selects a resource or action is not validated against an allowlist.

!!! note
    If the app intentionally accepts arbitrary parameter values (for example, a search path that passes user-typed text to a search UI), input validation may not be required and this test may not apply.
