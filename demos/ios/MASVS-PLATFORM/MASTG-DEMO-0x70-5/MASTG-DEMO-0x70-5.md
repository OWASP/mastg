---
platform: ios
title: Extracting the openURL Selector from the Binary
id: MASTG-DEMO-0070-5
code: [swift]
test: MASTG-TEST-0070-5
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

The test fails because the `openURL:options:completionHandler:` selector is present in the binary, indicating the application uses the iOS API for opening external links and must be further assessed to confirm whether untrusted input is passed to it without validation.
