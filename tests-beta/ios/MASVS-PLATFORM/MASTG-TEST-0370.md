---
platform: ios
title: Missing Input Validation in Custom URL Scheme Handlers
id: MASTG-TEST-0370
type: [static, code]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0045, MASTG-BEST-0054]
knowledge: [MASTG-KNOW-0079]
apis: [onOpenURL, URLComponents, URLQueryItem]
---

## Overview

Apps that register custom URL schemes must validate and sanitize all URL parameters before using them in security-sensitive operations (@MASTG-KNOW-0079). Without input validation, any caller that opens a registered URL scheme can supply arbitrary parameter values, bypassing expected business logic constraints.

Since any app on the device can open a custom URL scheme, an attacker could craft URLs with malicious parameters and send them to the app by following @MASTG-TECH-0169. For example:

- `mastgtest://transfer?amount=-1` or `amount=9999999` to bypass business logic bounds.
- `mastgtest://open?path=../../private/secrets.txt` for path traversal if the value is used in file operations.
- `mastgtest://search?q=<script>alert(1)</script>` for script injection if the value is rendered in a WebView.

This test checks whether the app's URL scheme handler validates URL parameters before acting on them.

!!! note
    If the app intentionally accepts arbitrary parameter values (for example, a search scheme that passes user-typed text to a search UI), input validation may not be required and this test may not apply.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain the disassembly of the URL handler, showing whether it performs type conversion or bounds checking on URL parameters.

## Evaluation

The test case fails if the URL handler uses parameter values directly without performing adequate validation. This includes missing type conversion (e.g. not converting a numeric parameter to `Int`), missing bounds or range checks, missing sanitization of special characters, or missing allowlist checks when the parameter selects a resource or action.
