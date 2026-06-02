---
platform: ios
title: Testing Arguments Passed to openURL
id: MASTG-DEMO-0x70-5
code: [swift]
test: MASTG-TEST-0x70-5
tools: [MASTG-TOOL-0129]
---

## Sample

The code snippet below demonstrates the extraction of the unvalidated URL to the `UIApplication.shared.open` method from the binary.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to parse the binary strings and method signatures using @MASTG-TOOL-0129.

{{ run.sh }}

## Observation

The output contains the `openURL:options:completionHandler:` selector, confirming that the compiled binary imports and uses the iOS API responsible for opening external links.

{{ output.txt }}

## Evaluation

The test fails because:

- The `openURL:options:completionHandler:` selector is found at `0x10000b37c`, confirming the binary uses the iOS API for opening external links.
- The sample code shows that `openOtherAppLinkInsecurelyWithUrl` passes the `url` argument directly to `UIApplication.shared.open` without verifying the scheme, host, or path, confirming that an attacker-controlled URL can reach this API without validation.
