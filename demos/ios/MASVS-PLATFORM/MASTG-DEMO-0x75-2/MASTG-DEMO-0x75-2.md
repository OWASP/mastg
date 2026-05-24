---
platform: ios
title: Custom URL Scheme Handler Without Input Validation
code: [swift]
id: MASTG-DEMO-0x75-2
test: MASTG-TEST-0x75-2
kind: fail
status: draft
---

## Sample

The following sample demonstrates an app that handles custom URL scheme requests using the modern `application:openURL:options:` delegate method, but without validating the URL scheme, host, or parameters. Any app can call the registered scheme and trigger sensitive actions, because the handler processes all incoming URLs unconditionally.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ url_scheme_handler.r2 # run.sh }}

## Observation

The output should contain the location where `application:openURL:options:` is implemented in the binary.

{{ output.txt }}

## Evaluation

The test case fails because the `application:openURL:options:` handler processes URL parameters without validating the scheme, host, or query parameters against an allowlist. Any caller can trigger the `transfer` action with an arbitrary `amount` value. The handler also does not check the source application (`UIApplicationOpenURLOptionsSourceApplicationKey`) before performing a sensitive operation.

**Further Validation Required:**

Inspect the code location reported above using @MASTG-TECH-0076 to determine whether the handler validates incoming URL data:

- The `url.scheme` is compared against a hardcoded constant but is not validated against an allowlist of all expected schemes and hosts.
- The `amount` parameter is used directly without sanitization or bounds checking.
- The `UIApplicationOpenURLOptionsSourceApplicationKey` entry in the `options` dictionary is not read or checked before performing the `transfer` action.
