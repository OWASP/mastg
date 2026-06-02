---
platform: ios
title: Extracting the Universal Link Receiver Selector from the Binary
id: MASTG-DEMO-0x70-4
code: [swift]
test: MASTG-TEST-0x70-4
tools: [MASTG-TOOL-0129]
---

## Sample

The code snippet below demonstrates the extraction of the Universal Link receiver method from the binary.

{{ ../MASTG-DEMO-0x70-1/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to extract the strings and Objective-C selectors from the compiled binary using @MASTG-TOOL-0129.
3. Check the output for the `scene:continueUserActivity:` selector.

{{ run.sh }}

## Observation

The output contains the `scene:continueUserActivity:` selector, confirming that the compiled binary implements the Universal Link receiver method.

{{ output.txt }}

## Evaluation

The test fails because the `scene:continueUserActivity:` selector is found inside the binary.
