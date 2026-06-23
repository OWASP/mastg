---
title: Missing Input Validation in Custom URL Scheme Handlers
platform: android
id: MASTG-TEST-0394
type: [static, code, manual]
profiles: [L1, L2]
weakness: MASWE-0058
knowledge: [MASTG-KNOW-0019]
best-practices: [MASTG-BEST-0071]
apis: [getData, getQueryParameter]
---

## Overview

Apps register custom URL schemes by declaring an `<intent-filter>` in the `AndroidManifest.xml` with an `<action android:name="android.intent.action.VIEW">`, the `android.intent.category.BROWSABLE` category, and a `<data>` element whose `android:scheme` is a custom (non-`http`/`https`) value. The handling activity then reads the incoming URI — typically via `getIntent()` and `Intent.getData()` (or `onNewIntent()`) — and extracts parameters with methods such as `Uri.getQueryParameter()`, `Uri.getPathSegments()`, or `Uri.getLastPathSegment()`.

Apps must validate and sanitize these URL parameters before using them in security-sensitive operations (see @MASTG-KNOW-0019). Since any app on the device can send an Intent targeting a custom URL scheme, an attacker can supply arbitrary parameter values, bypassing the expected business logic constraints. For example:

- `myapp://transfer?amount=-1` or `amount=9999999` to bypass business logic bounds.
- `myapp://open?path=../../data/sensitive.txt` for path traversal if the value is used in file operations.
- `myapp://search?q=<script>alert(1)</script>` for script injection if the value is rendered in a WebView.

Unlike iOS, Android provides no mechanism to identify which app sent the Intent. There is no equivalent to iOS's `sourceApplication` property, so the handler cannot verify the caller's identity, and every custom URL scheme handler is effectively reachable by any app on the device.

This test checks whether the app's custom URL scheme handler validates URL parameters before acting on them.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain the custom URL scheme declarations in the manifest and the handler code locations where the incoming URI is read and its parameters are extracted.

## Evaluation

The test case fails if a custom URL scheme handler uses URL parameter values without performing adequate validation before acting on them.

To complement this static analysis, you can use @MASTG-TECH-0173 to observe at runtime which handler method receives the deep link and which parameters it reads.

**Further Validation Required:**

Inspect each reported handler using @MASTG-TECH-0023, looking for cases such as:

- **Missing type conversion:** a numeric parameter is used as a raw string without converting it (e.g., not calling `toLong()` or `toInt()`).
- **Missing bounds or range checks:** the value is used without verifying it falls within an expected range.
- **Missing sanitization:** special characters are not sanitized before the value is used in a sink such as a file path, SQL query, or WebView.
- **Missing allowlist checks:** a parameter that selects a resource or action is not validated against an allowlist.

!!! note
    If the app intentionally accepts arbitrary parameter values (for example, a search scheme that passes user-typed text to a search UI), input validation may not be required and this test may not apply.
