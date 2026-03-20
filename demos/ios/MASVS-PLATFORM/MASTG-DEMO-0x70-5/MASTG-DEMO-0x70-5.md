---
platform: ios
title: Extracting the openURL Selector from the Binary
id: MASTG-DEMO-0070-5
code: [swift]
test: MASTG-TEST-0070-5
tools: [MASTG-TOOL-0129]
---

## Sample

The source code snippet below passes an unvalidated URL to `UIApplication.shared.open`. During a black-box assessment, testers verify this behavior by extracting the imported `openURL:` Objective-C selectors from the compiled binary.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to parse the binary strings and method signatures using `rabin2` (@MASTG-TOOL-0129).
3. Check the output for the `openURL:options:completionHandler:` selector.

{{ run.sh }}

## Observation

The output confirms that the compiled application imports and utilizes the iOS APIs responsible for opening external links.

{{ output.txt }}

## Evaluation

The test confirms the application calls the `openURL:options:completionHandler:` selector.

Finding this selector acts as a trigger for further investigation. The tester must dynamically trace the data flow to ensure that untrusted inbound Universal Links are not blindly passed directly into this outgoing `openURL` function, which would result in URI Scheme Hijacking.
