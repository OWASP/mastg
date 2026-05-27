---
platform: ios
title: Missing Validation in Custom URL Scheme Handlers
id: MASTG-TEST-0x02
type: [static, code]
weakness: MASWE-0058
profiles: [L1, L2]
best-practices: [MASTG-BEST-0045]
knowledge: [MASTG-KNOW-0079]
apis: [application:openURL:options:, UIApplicationOpenURLOptionsSourceApplicationKey, CFBundleURLSchemes]
---

## Overview

Apps that register custom URL schemes must validate the incoming URL and its parameters before acting on them (@MASTG-KNOW-0079). The modern handler `application:openURL:options:` receives an `options` dictionary that includes the source application identifier (`UIApplicationOpenURLOptionsSourceApplicationKey`). Without checking the source and validating URL parameters, any app or web page can trigger potentially sensitive operations in the target app.

This test checks whether the app registers a custom URL scheme and, if so, whether the handler validates the incoming URL, its parameters, and the source application.

## Steps

1. Use @MASTG-TECH-0058 to extract the app bundle.
2. Use @MASTG-TECH-0x01 to inspect the app's `Info.plist` for registered URL schemes (`CFBundleURLSchemes`) and identify any custom URL scheme handlers.
3. Use @MASTG-TECH-0x01 to locate the `application:openURL:options:` implementation in the binary.
4. Use @MASTG-TECH-0066 to disassemble the handler and check whether `UIApplicationOpenURLOptionsSourceApplicationKey` is read from the `options` dictionary and whether the URL scheme, host, path, and parameters are validated before use.

## Observation

The output should identify:

- Any custom URL schemes declared in `Info.plist` under `CFBundleURLSchemes`.
- The address and disassembly of any `application:openURL:options:` implementation.
- Whether the handler reads `UIApplicationOpenURLOptionsSourceApplicationKey` from the `options` dictionary.
- Whether the URL components (scheme, host, path, query parameters) are validated before being used.

## Evaluation

The test fails if any `application:openURL:options:` handler is found that:

- does not validate the URL scheme or host against an allowlist of expected values, or
- does not validate or sanitize URL parameters before using them in security-sensitive operations, or
- does not check the source application identifier (`UIApplicationOpenURLOptionsSourceApplicationKey`) before performing security-sensitive or irreversible operations.
